import { useState, useEffect } from "react";
import { getAlarmHistory } from "@/api/alarm/alarm";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { transformAlarmData } from "@/utils/historyUtils";
import { logger } from "@/utils/logger";

/**
 * @hook useAlarmHistory
 * @description Custom Hook for fetching alarm history data
 * @returns {Object} Alarm data and loading state
 * @returns {Array} returns.alarmData - Transformed alarm history data
 * @returns {boolean} returns.loading - Loading state
 */
export const useAlarmHistory = () => {
  const { projectId, workspaceId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [alarmData, setAlarmData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Don't make API request if projectId and workspaceId are not available
    if (!projectId || !workspaceId) {
      console.log("⏳ [Alarm API] Waiting for Store data...");
      return;
    }

    const fetchAlarmHistory = async () => {
      const req = {
        selectedCsps: ["AWS", "AZURE", "NCP"],
        selectedWorkspace: workspaceId,
        selectedProjects: [projectId],
      };

      console.log("=== [Alarm API] Request Payload ===");
      console.log("workspaceId:", workspaceId);
      console.log("projectId:", projectId);
      console.log("Full payload:", req);
      console.log("====================================");

      setLoading(true);

      try {
        const res = await getAlarmHistory(req);
        const rawData = res.data.Data.alarmHistory || [];
        const transformedData = transformAlarmData(rawData);
        setAlarmData(transformedData);
        logger.debug("Raw alarm data:", rawData);
        logger.debug("Transformed alarm data:", transformedData);
      } catch (err) {
        logger.error("Alarm API Error:", err);
        addAlert({
          variant: "danger",
          title: "API Error",
          message:
            err.userMessage ||
            "An error occurred while loading alarm history.",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchAlarmHistory();
  }, [projectId, workspaceId, addAlert]);

  return { alarmData, loading };
};
