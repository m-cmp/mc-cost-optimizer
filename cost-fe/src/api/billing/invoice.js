import { invoiceClient, USE_MOCK } from "../Client";
import {
  baseInfoData,
  summaryBillData,
  InvoiceData,
} from "../../config/mockData";

// 베이스 정보 조회
export const getBillingBaseInfo = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: baseInfoData });
  }
  return invoiceClient.post("/getBillingBaseInfo", payload);
};

// 월별 요약 조회
export const getInvoiceSummary = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: summaryBillData });
  }
  return invoiceClient.post("/getSummary", payload);
};

// 이번달 인보이스 상세 조회
export const getInvoice = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: InvoiceData });
  }
  return invoiceClient.post("/getInvoice", payload);
};
