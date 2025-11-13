import { useState, useEffect, useCallback } from "react";
import { getBudgetComparison } from "@/api/budget/budget";
import { useProjectStore } from "@/stores/useProjectStore";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useBudgetComparison
 * @description Custom Hook for fetching budget vs actual comparison data
 * @param {number} year - Year to query
 * @returns {Object} Comparison data and state
 * @returns {Object} returns.comparisonData - Budget vs actual comparison data
 * @returns {boolean} returns.loading - Data loading state
 * @returns {Function} returns.refetch - Function to manually refetch data
 */
export const useBudgetComparison = (year) => {
  const { projectId } = useProjectStore();
  const { addAlert } = useAlertStore();
  const [comparisonData, setComparisonData] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchComparison = useCallback(async () => {
    // Don't make API request if projectId is not available
    if (!projectId) {
      console.log("⏳ [Budget Comparison API] Waiting for projectId...");
      return;
    }

    setLoading(true);

    console.log("=== [Budget Comparison API] Fetch Request ===");
    console.log("year:", year);
    console.log("projectId:", projectId);
    console.log("====================================");

    try {
      const response = await getBudgetComparison(year, projectId);
      console.log("=== [Budget Comparison API] Response ===");
      console.log("response.data:", response.data);
      console.log("====================================");
      setComparisonData(response.data);
    } catch (error) {
      logger.error("Failed to fetch budget comparison:", error);
      addAlert({
        variant: "danger",
        title: "Error",
        message:
          error.userMessage ||
          "Failed to load comparison data. Please try again.",
      });
      setComparisonData(null);
    } finally {
      setLoading(false);
    }
  }, [year, projectId, addAlert]);

  useEffect(() => {
    if (year) {
      fetchComparison();
    }
  }, [year, fetchComparison]);

  return { comparisonData, loading, refetch: fetchComparison };
};
