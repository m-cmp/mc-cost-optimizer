import { useState, useEffect } from "react";
import { getBudgetComparison } from "@/api/budget/budget";
import { useAlertStore } from "@/stores/useAlertStore";
import { logger } from "@/utils/logger";

/**
 * @hook useBudgetComparison
 * @description Custom Hook for fetching budget vs actual comparison data
 * @param {number} year - Year to query
 * @returns {Object} Comparison data and state
 * @returns {Object} returns.comparisonData - Budget vs actual comparison data
 * @returns {boolean} returns.loading - Data loading state
 */
export const useBudgetComparison = (year) => {
  const { addAlert } = useAlertStore();
  const [comparisonData, setComparisonData] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchComparison = async () => {
      setLoading(true);

      console.log("=== [Budget Comparison API] Fetch Request ===");
      console.log("year:", year);
      console.log("====================================");

      try {
        const response = await getBudgetComparison(year);
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
    };

    if (year) {
      fetchComparison();
    }
  }, [year, addAlert]);

  return { comparisonData, loading };
};
