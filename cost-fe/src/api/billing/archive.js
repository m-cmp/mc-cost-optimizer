import { archiveClient, USE_MOCK } from "../Client";

// 아카이브 가능한 연·월
export const getArchivableMonths = () => {
  if (USE_MOCK) return Promise.resolve({ data: ["202606", "202605", "202604"] });
  return archiveClient.get("/months");
};

// 현황(manifest 전체)
export const getArchiveStatus = () => {
  if (USE_MOCK) return Promise.resolve({ data: [] });
  return archiveClient.get("/status");
};

// raw 삭제 게이트 판정
export const checkPurgeGate = (yearMonth, csp) => {
  if (USE_MOCK)
    return Promise.resolve({
      data: { csp, yearMonth, canPurge: false, reason: "mock" },
    });
  return archiveClient.get("/gate", { params: { yearMonth, csp } });
};

// 인보이스 아카이브(비파괴). payload: { yearMonth, csps[], projects[] }
export const archiveInvoice = (payload) => {
  if (USE_MOCK)
    return Promise.resolve({ data: { yearMonth: payload.yearMonth, results: [] } });
  return archiveClient.post("", payload);
};

// raw 삭제(게이트 통과 시만). payload: { yearMonth, csp }
export const purgeRaw = (payload) => {
  if (USE_MOCK)
    return Promise.resolve({
      data: { csp: payload.csp, yearMonth: payload.yearMonth, success: false, message: "mock" },
    });
  return archiveClient.post("/purge", payload);
};
