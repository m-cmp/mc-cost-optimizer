"""
ml-rightsize API — stateless. 요청 올 때만 계산하고 응답, 적재 없음.

요청 시점 기준 최근 WINDOW_DAYS 일의 CPU 메트릭을 DB에서 읽어 점수를 산출해 돌려준다.
(수집·LLM 호출은 타 팀. 우리는 DB 읽고 점수 JSON 돌려주면 끝.)

env (docker-compose config 에서 주입):
  COST_DB_URL     jdbc:mariadb://host:port/cost?...   (필수, 기존 .env 재사용)
  COST_DB_USERNM  (필수)
  COST_DB_PW      (필수)
  SERVER_PORT     우리 API 포트 (기본 8093)
  WINDOW_DAYS     분석 기간(일) (기본 30)

요청:  POST /ml-rightsize    {"csp_instanceid": "i-0233..."}
응답:  점수 JSON (LLM_DATA_GUIDE 참조)

실행:  python3 -m rightsizing_score.api
"""
from __future__ import annotations

import json
import os
from datetime import datetime, timedelta
from http.server import BaseHTTPRequestHandler, HTTPServer
from typing import Dict
from urllib.parse import urlparse

import pymysql

from .config import ScoringConfig
from .datasource import AssetComputeMetricSource, InMemorySource
from .explain import to_llm_context
from .models import TimeWindow
from .pipeline import RightsizingPipeline

ENDPOINT = "/ml-rightsize"


def _parse_jdbc(url: str):
    """jdbc:mariadb://host:port/db?params → (host, port, db)."""
    s = url[5:] if url.startswith("jdbc:") else url   # 'jdbc:' 제거
    u = urlparse(s)
    return u.hostname, (u.port or 3306), (u.path or "").lstrip("/")


def load_settings() -> Dict:
    host, port, db = _parse_jdbc(os.environ["COST_DB_URL"])
    return {
        "host": host, "port": port, "db": db,
        "user": os.environ["COST_DB_USERNM"],
        "password": os.environ["COST_DB_PW"],
        "server_port": int(os.environ.get("SERVER_PORT", "8093")),
        "window_days": float(os.environ.get("WINDOW_DAYS", "30")),
    }


def score_instance(csp_instanceid: str, st: Dict) -> Dict:
    """instance 1개 → 요청 시점 기준 최근 window_days 일 DB 조회 → 점수 JSON."""
    conn = pymysql.connect(
        host=st["host"], port=st["port"], database=st["db"],
        user=st["user"], password=st["password"],
        connect_timeout=10, read_timeout=30,
    )
    try:
        now = datetime.now()
        fetch_window = TimeWindow(start=now - timedelta(days=st["window_days"]), end=now)
        raw = AssetComputeMetricSource(conn).fetch(csp_instanceid, fetch_window)

        if not raw.samples:
            return {
                "instance": csp_instanceid,
                "action_signal": "insufficient_data",
                "confidence": 0.0,
                "binding_resource": "-",
                "reasons": [f"최근 {st['window_days']:.0f}일 내 CPU 데이터 없음"],
            }

        # 격자/분석은 '실제 데이터 범위'로 — fetch 는 30일이지만 데이터가 짧으면 그만큼만.
        # → coverage·confidence 가 정직하게(데이터 적으면 낮게) 나온다.
        ts = [s.ts for s in raw.samples]
        grid_window = TimeWindow(start=min(ts), end=max(ts))
        span_days = max((grid_window.end - grid_window.start).total_seconds() / 86400.0, 0.01)
        cfg = ScoringConfig(window_days=round(span_days, 2))

        pipe = RightsizingPipeline(source=InMemorySource({csp_instanceid: raw}), config=cfg)
        trace = pipe.run(csp_instanceid, grid_window)
        return to_llm_context(trace)
    finally:
        conn.close()


class _Handler(BaseHTTPRequestHandler):
    settings: Dict = {}

    def _send(self, code: int, obj: Dict) -> None:
        body = json.dumps(obj, ensure_ascii=False, indent=2).encode("utf-8")
        self.send_response(code)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        if self.path == "/health":
            self._send(200, {"status": "ok"})
        else:
            self._send(404, {"error": f"POST {ENDPOINT}"})

    def do_POST(self):
        if self.path != ENDPOINT:
            self._send(404, {"error": f"unknown path; use POST {ENDPOINT}"})
            return
        try:
            n = int(self.headers.get("Content-Length", 0))
            payload = json.loads(self.rfile.read(n) or b"{}")
            iid = payload.get("csp_instanceid") or payload.get("instance_id")
            if not iid:
                self._send(400, {"error": "csp_instanceid is required"})
                return
            self._send(200, score_instance(iid, self.settings))
        except Exception as e:  # noqa: BLE001 — API 경계 에러는 JSON 으로
            self._send(400, {"error": f"{type(e).__name__}: {e}"})

    def log_message(self, *args):
        pass


def serve() -> None:
    st = load_settings()
    _Handler.settings = st
    print(f"ml-rightsize API → http://0.0.0.0:{st['server_port']}{ENDPOINT}   "
          f"(db={st['host']}:{st['port']}/{st['db']}, window={st['window_days']:.0f}d)")
    HTTPServer(("0.0.0.0", st["server_port"]), _Handler).serve_forever()


if __name__ == "__main__":
    serve()
