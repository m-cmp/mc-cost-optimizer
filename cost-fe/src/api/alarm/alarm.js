import { alertClient, USE_MOCK, billingClient } from "../Client";
import { alarmHistoryData } from "../../config/mockData";

// 알림 히스토리 조회
export const getAlarmHistory = async (req) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: alarmHistoryData });
  }
  return billingClient.post("/alarm/history", req);
};

// 메일 계정/비밀번호 저장
export const insertMailInfo = (payload) => {
  return alertClient.post("/insertMailInfo", payload);
};

// 저장된 메일 계정 조회
export const getMailInfo = () => {
  return alertClient.get("/getMailInfo");
};

// 메일 발송 (테스트용)
export const sendAlertMail = (payload) => {
  return alertClient.post("/sendAlertMail", payload);
};

// Slack 메시지 전송
export const sendSlackMessage = ({ userId, message, linkUrl, linkText }) => {
  const params = new URLSearchParams({ userId, message });
  if (linkUrl) params.append("linkUrl", linkUrl);
  if (linkText) params.append("linkText", linkText);

  return alertClient.post(`/sendSlackAC?${params.toString()}`);
};

// Slack Token 저장
export const insertSlackToken = (payload) => {
  return alertClient.post("/insertSlackToken", payload);
};

// Slack Token/Channel 조회
export const getSlackInfo = (userId) => {
  return alertClient.get(`/getSlackIF`, { params: { userId } });
};
