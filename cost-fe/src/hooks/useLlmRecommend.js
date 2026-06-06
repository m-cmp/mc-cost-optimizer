import { useState, useCallback } from "react";
import { recommend } from "@/api/llm_recommender/llmRecommender";
import { logger } from "@/utils/logger";

/**
 * @hook useLlmRecommend
 * @description Runs LLM recommendation sequentially for the selected instances,
 *   one request at a time, tracking progress. One instance's failure is isolated.
 */
export function useLlmRecommend() {
  const [results, setResults] = useState([]); // [{ instanceId, data?, error? }]
  const [progress, setProgress] = useState({ done: 0, total: 0 });
  const [running, setRunning] = useState(false);

  const run = useCallback(async (instanceIds, model) => {
    if (running || !instanceIds?.length) return;
    setRunning(true);
    setResults([]);
    setProgress({ done: 0, total: instanceIds.length });

    const acc = [];
    for (let i = 0; i < instanceIds.length; i++) {
      const instanceId = instanceIds[i];
      try {
        const res = await recommend({ instanceId, model });
        acc.push({ instanceId, data: res.data.Data });
      } catch (err) {
        logger.error("LLM recommend error:", err);
        acc.push({
          instanceId,
          data: { instance: instanceId, status: "error", error: err.userMessage || "Request failed" },
        });
      }
      setResults([...acc]);
      setProgress({ done: i + 1, total: instanceIds.length });
    }
    setRunning(false);
  }, [running]);

  return { results, progress, running, run };
}
