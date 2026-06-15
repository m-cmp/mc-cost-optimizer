import { apikeyClient } from "../Client";

export const saveApiKey = (provider, plainKey, nsId) =>
  apikeyClient.post(`/${provider}`, { nsId, plainKey });

export const getApiKeyStatus = (provider, nsId) =>
  apikeyClient.get(`/${provider}/status`, { params: { nsId } });

export const deleteApiKey = (provider, nsId) =>
  apikeyClient.delete(`/${provider}`, { params: { nsId } });
