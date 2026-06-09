import { apikeyClient } from "../Client";

// TODO
// 임시 userId — 추후 실제 인증 연동 시 대체
const USER_ID = "mcmpcostopti";

export const saveApiKey = (provider, plainKey) =>
  apikeyClient.post(`/${provider}`, { userId: USER_ID, plainKey });

export const getApiKeyStatus = (provider) =>
  apikeyClient.get(`/${provider}/status`, { params: { userId: USER_ID } });

export const deleteApiKey = (provider) =>
  apikeyClient.delete(`/${provider}`, { params: { userId: USER_ID } });
