import { invoiceClient, USE_MOCK } from "../Client";
import {
  baseInfoData,
  summaryBillData,
  InvoiceData,
} from "../../config/mockData";

// Fetch base information
export const getBillingBaseInfo = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: baseInfoData });
  }
  return invoiceClient.post("/getBillingBaseInfo", payload);
};

// Fetch monthly summary
export const getInvoiceSummary = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: summaryBillData });
  }
  return invoiceClient.post("/getSummary", payload);
};

// Fetch current month invoice details
export const getInvoice = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: InvoiceData });
  }
  return invoiceClient.post("/getInvoice", payload);
};
