import { useState, useEffect } from "react";
import {
  getAvailableYears,
  getBudgetsByYear,
  upsertBudgets,
} from "@/api/budget/budget";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import {
  transformApiToUiFormat,
  transformUiToApiFormat,
} from "@/utils/budgetUtils";
import { logger } from "@/utils/logger";

/**
 * @hook useBudgetData
 * @description Custom Hook for managing budget data
 * @param {number} year - Year to query
 * @returns {Object} Budget data and state
 * @returns {Array<number>} returns.availableYears - List of available years
 * @returns {Object} returns.cspBudgets - Monthly budget data by CSP
 * @returns {Function} returns.setCspBudgets - Function to set budget data
 * @returns {boolean} returns.loading - Data loading state
 * @returns {boolean} returns.saving - Saving state
 * @returns {Function} returns.saveBudgets - Function to save budgets
 * @returns {Function} returns.resetBudgets - Function to reset budgets
 */
export const useBudgetData = (year) => {
  const { projectId } = useProjectStore();
  const { addAlert } = useAlertStore();

  const [availableYears, setAvailableYears] = useState([]);
  const [cspBudgets, setCspBudgets] = useState({});
  const [originalBudgets, setOriginalBudgets] = useState({});
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Initial load: fetch available years list
  useEffect(() => {
    const fetchAvailableYears = async () => {
      try {
        const response = await getAvailableYears();
        setAvailableYears(response.data || []);
      } catch (error) {
        logger.error("Failed to fetch available years:", error);
        // Set default years on error
        setAvailableYears([2023, 2024, 2025]);
      }
    };

    fetchAvailableYears();
  }, []);

  // Load data when year changes
  useEffect(() => {
    // Don't make API request if projectId is not available
    if (!projectId) {
      console.log("⏳ [Budget API] Waiting for projectId...");
      return;
    }

    const fetchBudgets = async () => {
      setLoading(true);

      console.log("=== [Budget API] Fetch Request ===");
      console.log("year:", year);
      console.log("projectId:", projectId);
      console.log("====================================");

      try {
        const response = await getBudgetsByYear(year, projectId);
        const uiBudgets = transformApiToUiFormat(response.data);
        setCspBudgets(uiBudgets);
        setOriginalBudgets(uiBudgets);
      } catch (error) {
        logger.error("Failed to fetch budgets:", error);
        addAlert({
          variant: "danger",
          title: "Error",
          message:
            error.userMessage || "Failed to load budget data. Please try again.",
        });
        // Initialize to empty state on error
        setCspBudgets({});
        setOriginalBudgets({});
      } finally {
        setLoading(false);
      }
    };

    fetchBudgets();
  }, [year, projectId, addAlert]);

  // Budget save function
  const saveBudgets = async () => {
    try {
      setSaving(true);
      const payload = transformUiToApiFormat(cspBudgets, year, originalBudgets, projectId);

      console.log("=== [Budget API] Save Request Payload ===");
      console.log("year:", year);
      console.log("projectId:", projectId);
      console.log("payload:", payload);
      console.log("====================================");

      logger.info("Saving budget data:", payload);

      const res = await upsertBudgets(payload);

      if (res.data?.status === "fail") {
        addAlert({
          variant: "danger",
          title: "Error",
          message: res.data?.error?.Message || "Failed to save budget.",
        });
        return false;
      } else {
        logger.info("Budget saved successfully");

        // Fetch latest saved data from server again
        try {
          const response = await getBudgetsByYear(year, projectId);
          const uiBudgets = transformApiToUiFormat(response.data);
          setCspBudgets(uiBudgets);
          setOriginalBudgets(uiBudgets);
        } catch (fetchError) {
          logger.error("Failed to fetch updated budgets:", fetchError);
          // Keep current data as original even if fetch fails
          setOriginalBudgets(cspBudgets);
        }

        addAlert({
          variant: "success",
          title: "Success",
          message: "Budget has been saved successfully.",
        });
        return true;
      }
    } catch (error) {
      logger.error("Failed to save budget:", error);
      addAlert({
        variant: "danger",
        title: "Error",
        message: error.userMessage || "Failed to save budget. Please try again.",
      });
      return false;
    } finally {
      setSaving(false);
    }
  };

  // Budget reset function
  const resetBudgets = () => {
    setCspBudgets(originalBudgets);
  };

  return {
    availableYears,
    cspBudgets,
    setCspBudgets,
    loading,
    saving,
    saveBudgets,
    resetBudgets,
  };
};
