import { llmClient, USE_MOCK } from "../Client";
import { mockRecommendation, mockRecommendHistory, mockInstances } from "../../config/mockData";

// Request a recommendation for ONE instance.
// Returns the axios-shaped { data: { Data: <Recommendation> } } to match ResultModel.
export const recommend = async ({ instanceId, provider, model, userQuestion, nsId }) => {
  if (USE_MOCK) {
    await new Promise((r) => setTimeout(r, 600)); // simulate LLM latency
    return Promise.resolve({ data: { Data: mockRecommendation(instanceId, userQuestion) } });
  }
  return llmClient.post("/recommend", { instanceId, provider, model, userQuestion, nsId });
};

// Fetch the selectable model catalog (provider -> [modelId]) from the backend.
// In mock mode there is no backend, so return Data=null to signal "keep the fallback".
export const getModels = () => {
  if (USE_MOCK) return Promise.resolve({ data: { Data: null } });
  return llmClient.get("/models");
};

// Fetch the real resource/instance list for a namespace, sourced from servicegroup_meta.
// spec/usd are not wired up yet on the backend, so they may come back null.
export const getInstances = (nsId) => {
  if (USE_MOCK) return Promise.resolve({ data: { Data: mockInstances } });
  return llmClient.get("/instances", { params: { nsId } });
};

// Fetch recommendation history for a namespace (most recent first).
export const getHistory = (nsId) => {
  if (USE_MOCK) return Promise.resolve({ data: { Data: mockRecommendHistory } });
  return llmClient.get("/history", { params: { nsId } });
};
