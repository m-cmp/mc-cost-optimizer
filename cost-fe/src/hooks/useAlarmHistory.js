import { useState, useEffect } from "react";
import { getAlarmHistory } from "@/api/alarm/alarm";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { transformAlarmData } from "@/utils/historyUtils";
import { logger } from "@/utils/logger";

/**
 * @hook useAlarmHistory
 * @description 알람 히스토리 데이터를 가져오는 Custom Hook
 * @returns {Object} 알람 데이터 및 로딩 상태
 * @returns {Array} returns.alarmData - 변환된 알람 히스토리 데이터
 * @returns {boolean} returns.loading - 로딩 상태
 */
export const useAlarmHistory = () => {
  const { projectId, workspaceId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [alarmData, setAlarmData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAlarmHistory = async () => {
      const req = {
        selectedCsps: ["AWS", "AZURE", "NCP"],
        selectedWorkspace: workspaceId ?? "ws01",
        selectedProjects: [projectId ?? "ns01"],
      };

      console.log("=== [Alarm API] 요청 Payload ===");
      console.log("workspaceId:", workspaceId);
      console.log("projectId:", projectId);
      console.log("전체 payload:", req);
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
          title: "API 에러",
          message:
            err.userMessage ||
            "알람 히스토리를 불러오는 중 오류가 발생했습니다.",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchAlarmHistory();
  }, [projectId, workspaceId, addAlert]);

  return { alarmData, loading };
};
