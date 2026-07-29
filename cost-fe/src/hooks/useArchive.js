import { useState, useEffect, useCallback } from "react";
import {
  getArchivableMonths,
  getArchiveStatus,
  archiveInvoice,
  purgeRaw,
  checkPurgeGate,
} from "@/api/billing/archive";

/**
 * 아카이브 상태/동작 훅.
 * ★ 과금/폭주 방어: archiving/purging 플래그로 in-flight 중 재요청 차단(버튼 연타 무시).
 */
export function useArchive() {
  const [months, setMonths] = useState([]);
  const [status, setStatus] = useState([]);
  const [archiving, setArchiving] = useState(false);
  const [purging, setPurging] = useState(null); // "ym:csp" 진행 키

  const refreshStatus = useCallback(async () => {
    try {
      const res = await getArchiveStatus();
      setStatus(Array.isArray(res?.data) ? res.data : []);
    } catch (e) {
      /* 조회 실패는 조용히 — 빈 상태 유지 */
    }
  }, []);

  const loadMonths = useCallback(async () => {
    try {
      const res = await getArchivableMonths();
      setMonths(Array.isArray(res?.data) ? res.data : []);
    } catch (e) {
      /* noop */
    }
  }, []);

  useEffect(() => {
    loadMonths();
    refreshStatus();
  }, [loadMonths, refreshStatus]);

  const runArchive = useCallback(
    async (payload) => {
      if (archiving) return null; // 진행 중이면 무시(연타 방어)
      setArchiving(true);
      try {
        const res = await archiveInvoice(payload);
        await refreshStatus();
        return res?.data || null;
      } finally {
        setArchiving(false);
      }
    },
    [archiving, refreshStatus],
  );

  const runPurge = useCallback(
    async (yearMonth, csp) => {
      if (purging) return null;
      setPurging(`${yearMonth}:${csp}`);
      try {
        const res = await purgeRaw({ yearMonth, csp });
        await refreshStatus();
        return res?.data || null;
      } finally {
        setPurging(null);
      }
    },
    [purging, refreshStatus],
  );

  return {
    months,
    status,
    archiving,
    purging,
    runArchive,
    runPurge,
    refreshStatus,
    checkGate: checkPurgeGate,
  };
}
