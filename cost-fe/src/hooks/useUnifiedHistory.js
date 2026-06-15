import { useState, useEffect } from "react";
import { getUnifiedHistory } from "@/api/llm_recommender/llmRecommender";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useUnifiedHistory
 * @description Fetches the unified recommendation history (ML + LLM) for the
 *   current namespace (projectId). Waits for projectId (null guard).
 */
export function useUnifiedHistory() {
  const projectId = useProjectStore((s) => s.projectId);
  const { addAlert } = useAlertStore();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!projectId) return; // nsId 준비 전엔 호출 보류
    const fetchHistory = async () => {
      setLoading(true);
      try {
        const res = await getUnifiedHistory(projectId);
        setHistory(res.data.Data || []);
      } catch (err) {
        logger.error("Unified history error:", err);
        addAlert({
          variant: "danger",
          title: "API Error",
          message: err.userMessage || "Failed to load recommendation history.",
        });
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [projectId, addAlert]);

  return { history, loading };
}
