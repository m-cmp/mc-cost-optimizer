import { useState, useEffect } from "react";
import { getHistory } from "@/api/llm_recommender/llmRecommender";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useRecommendHistory
 * @description Fetches LLM recommendation history for the current namespace (projectId).
 *   Waits for projectId (null guard); mirrors useAlarmHistory.
 */
export function useRecommendHistory() {
  const projectId = useProjectStore((s) => s.projectId);
  const { addAlert } = useAlertStore();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!projectId) return; // nsId 준비 전엔 호출 보류
    const fetchHistory = async () => {
      setLoading(true);
      try {
        const res = await getHistory(projectId);
        setHistory(res.data.Data || []);
      } catch (err) {
        logger.error("Recommend history error:", err);
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
