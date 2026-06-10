import { llmClient, USE_MOCK } from "../Client";
import { mockRecommendation, mockRecommendHistory, mockUnifiedHistory } from "../../config/mockData";

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

// Fetch recommendation history for a namespace (most recent first).
export const getHistory = (nsId) => {
  if (USE_MOCK) return Promise.resolve({ data: { Data: mockRecommendHistory } });
  return llmClient.get("/history", { params: { nsId } });
};

// Fetch the unified history (ML alarm recs + LLM recs) for a namespace.
export const getUnifiedHistory = (nsId) => {
  if (USE_MOCK) return Promise.resolve({ data: { Data: mockUnifiedHistory } });
  return llmClient.get("/history/unified", { params: { nsId } });
};
