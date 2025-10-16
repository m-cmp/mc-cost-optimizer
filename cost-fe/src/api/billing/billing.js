import { billingClient, USE_MOCK } from "../Client";
import { chartData, top5billData, billingAsset } from "../../config/mockData";

// Current month summary (compared to last month)
export const getCurMonthBill = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: chartData });
  }
  return billingClient.post("/getCurMonthBill", payload);
};

// Top 5 resources for current month
export const getTop5Bill = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: top5billData });
  }
  return billingClient.post("/getTop5Bill", payload);
};

// Current month cost by service
export const getBillAsset = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: billingAsset });
  }
  return billingClient.post("/getBillAsset", payload);
};
