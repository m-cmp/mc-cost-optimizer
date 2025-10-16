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
 * @description Custom Hook for fetching homepage billing data
 * @returns {Object} Billing data and loading state
 * @returns {Object|null} returns.summary - Monthly summary data
 * @returns {Array} returns.top5 - Top 5 service data
 * @returns {Array} returns.services - Service cost list
 * @returns {boolean} returns.loading - Loading state
 */
export const useBillingData = () => {
  const { projectId, workspaceId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [summary, setSummary] = useState(null);
  const [top5, setTop5] = useState([]);
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Don't make API request if projectId and workspaceId are not available
    if (!projectId || !workspaceId) {
      console.log("⏳ [Billing API] Waiting for Store data...");
      return;
    }

    const fetchBillingData = async () => {
      const req = {
        today: new Date().toISOString().slice(0, 10).replace(/-/g, ""),
        selectedProjects: [projectId],
        selectedCsps: ["AWS", "AZURE", "NCP"],
        selectedWorkspace: workspaceId,
      };

      console.log("=== [Billing API] Request Payload ===");
      console.log("workspaceId:", workspaceId);
      console.log("projectId:", projectId);
      console.log("Full payload:", req);
      console.log("====================================");

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
          title: "API Error",
          message: err.userMessage || "An error occurred while loading home data.",
        });
      } finally {
        setLoading(false);
      }
    };

    fetchBillingData();
  }, [projectId, workspaceId, addAlert]);

  return { summary, top5, services, loading };
};
