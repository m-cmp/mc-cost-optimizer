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
 * @description Custom Hook for fetching invoice report data
 * @returns {Object} Invoice data and loading state
 * @returns {Object|null} returns.baseInfo - Base information data
 * @returns {Object|null} returns.summary - Summary data
 * @returns {Object|null} returns.invoice - Invoice data
 * @returns {boolean} returns.loading - Loading state
 */
export const useInvoiceData = () => {
  const { projectId, workspaceId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [baseInfo, setBaseInfo] = useState(null);
  const [summary, setSummary] = useState(null);
  const [invoice, setInvoice] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Don't make API request if projectId and workspaceId are not available
    if (!projectId || !workspaceId) {
      console.log("⏳ [Invoice API] Waiting for Store data...");
      return;
    }

    const fetchInvoiceData = async () => {
      const req = {
        today: new Date().toISOString().slice(0, 10).replace(/-/g, ""),
        selectedProjects: [projectId],
        selectedCsps: ["AWS", "AZURE", "NCP"],
        selectedWorkspace: workspaceId,
      };

      console.log("=== [Invoice API] Request Payload ===");
      console.log("workspaceId:", workspaceId);
      console.log("projectId:", projectId);
      console.log("Full payload:", req);
      console.log("====================================");

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
          title: "API Error",
          message: err.userMessage || "An error occurred while loading invoice data.",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchInvoiceData();
  }, [projectId, workspaceId, addAlert]);

  return { baseInfo, summary, invoice, loading };
};
