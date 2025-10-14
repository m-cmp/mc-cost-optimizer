import { useState, useEffect } from "react";
import {
  getCurMonthBill,
  getTop5Bill,
  getBillAsset,
} from "@/api/billing/billing";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useBillingData
 * @description 홈페이지 청구 데이터를 가져오는 Custom Hook
 * @returns {Object} 청구 데이터 및 로딩 상태
 * @returns {Object|null} returns.summary - 월별 요약 데이터
 * @returns {Array} returns.top5 - Top 5 서비스 데이터
 * @returns {Array} returns.services - 서비스 비용 목록
 * @returns {boolean} returns.loading - 로딩 상태
 */
export const useBillingData = () => {
  const { projectId, workspaceId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [summary, setSummary] = useState(null);
  const [top5, setTop5] = useState([]);
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBillingData = async () => {
      const req = {
        today: new Date().toISOString().slice(0, 10).replace(/-/g, ""),
        selectedProjects: [projectId ?? "mock-proj"],
        selectedCsps: ["AWS", "AZURE", "NCP"],
        selectedWorkspace: workspaceId,
      };

      setLoading(true);

      try {
        const [summaryRes, top5Res, servicesRes] = await Promise.all([
          getCurMonthBill(req),
          getTop5Bill(req),
          getBillAsset(req),
        ]);

        setSummary(summaryRes.data.Data);
        setTop5(top5Res.data.Data.top5bill || []);
        setServices(servicesRes.data.Data.billingAsset || []);
      } catch (err) {
        logger.error("API Error:", err);
        addAlert({
          variant: "danger",
          title: "API 에러",
          message: err.userMessage || "홈 데이터를 불러오는 중 오류가 발생했습니다.",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchBillingData();
  }, [projectId, workspaceId, addAlert]);

  return { summary, top5, services, loading };
};
