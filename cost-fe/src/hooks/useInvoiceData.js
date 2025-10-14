import { useState, useEffect } from "react";
import {
  getBillingBaseInfo,
  getInvoiceSummary,
  getInvoice,
} from "@/api/billing/invoice";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useInvoiceData
 * @description 청구서 리포트 데이터를 가져오는 Custom Hook
 * @returns {Object} 청구서 데이터 및 로딩 상태
 * @returns {Object|null} returns.baseInfo - 기본 정보 데이터
 * @returns {Object|null} returns.summary - 요약 데이터
 * @returns {Object|null} returns.invoice - 인보이스 데이터
 * @returns {boolean} returns.loading - 로딩 상태
 */
export const useInvoiceData = () => {
  const { projectId, workspaceId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [baseInfo, setBaseInfo] = useState(null);
  const [summary, setSummary] = useState(null);
  const [invoice, setInvoice] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInvoiceData = async () => {
      const req = {
        today: new Date().toISOString().slice(0, 10).replace(/-/g, ""),
        selectedProjects: [projectId ?? "ns01"],
        selectedCsps: ["AWS", "AZURE", "NCP"],
        selectedWorkspace: workspaceId ?? "ws01",
      };

      setLoading(true);

      try {
        const [baseRes, summaryRes, invoiceRes] = await Promise.all([
          getBillingBaseInfo(req),
          getInvoiceSummary(req),
          getInvoice(req),
        ]);

        setBaseInfo(baseRes.data.Data);
        setSummary(summaryRes.data.Data);
        setInvoice(invoiceRes.data.Data);
      } catch (err) {
        logger.error("Invoice API Error:", err);
        addAlert({
          variant: "danger",
          title: "API 에러",
          message: err.userMessage || "청구서 데이터를 불러오는 중 오류가 발생했습니다.",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchInvoiceData();
  }, [projectId, workspaceId, addAlert]);

  return { baseInfo, summary, invoice, loading };
};
