import { alertClient, USE_MOCK, billingClient } from "../Client";
import { alarmHistoryData } from "../../config/mockData";

// Fetch alarm history
export const getAlarmHistory = async (req) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: alarmHistoryData });
  }
  return billingClient.post("/alarm/history", req);
};

// Save email account/password
export const insertMailInfo = (payload) => {
  return alertClient.post("/insertMailInfo", payload);
};

// Fetch saved email account
export const getMailInfo = () => {
  return alertClient.get("/getMailInfo");
};

// Send email (for testing)
export const sendAlertMail = (payload) => {
  return alertClient.post("/sendAlertMail", payload);
};

// Send Slack message
export const sendSlackMessage = ({ userId, message, linkUrl, linkText }) => {
  const params = new URLSearchParams({ userId, message });
  if (linkUrl) params.append("linkUrl", linkUrl);
  if (linkText) params.append("linkText", linkText);

  return alertClient.post(`/sendSlackAC?${params.toString()}`);
};

// Save Slack Token
export const insertSlackToken = (payload) => {
  return alertClient.post("/insertSlackToken", payload);
};

// Fetch Slack Token/Channel
export const getSlackInfo = (userId) => {
  return alertClient.get(`/getSlackIF`, { params: { userId } });
};
