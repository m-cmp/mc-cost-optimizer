# ml-rightsize (AWS VM Rightsizer, CPU-only) — stateless API
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY rightsizing_score/ ./rightsizing_score/

# 기본 포트 (SERVER_PORT env 로 변경 가능)
EXPOSE 8093

# env (docker-compose 에서 주입): COST_DB_URL, COST_DB_USERNM, COST_DB_PW, [SERVER_PORT], [WINDOW_DAYS]
CMD ["python", "-m", "rightsizing_score.api"]
