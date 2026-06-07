import { llmClient, USE_MOCK } from "../Client";
import { mockRecommendation } from "../../config/mockData";

// Request a recommendation for ONE instance.
// Returns the axios-shaped { data: { Data: <Recommendation> } } to match ResultModel.
export const recommend = async ({ instanceId, provider, model, userQuestion }) => {
  if (USE_MOCK) {
    await new Promise((r) => setTimeout(r, 600)); // simulate LLM latency
    return Promise.resolve({ data: { Data: mockRecommendation(instanceId, userQuestion) } });
  }
  return llmClient.post("/recommend", { instanceId, provider, model, userQuestion });
};
