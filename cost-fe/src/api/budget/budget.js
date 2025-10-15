import { budgetClient, USE_MOCK } from "../Client";
import { mockBudgetData } from "@/config/mockData";

/**
 * Fetch available years list
 * @returns {Promise<Array<number>>} [2023, 2024, 2025, ...]
 */
export const getAvailableYears = () => {
  if (USE_MOCK) {
    return Promise.resolve({ data: [2023, 2024, 2025] });
  }
  return budgetClient.get("/years");
};

/**
 * Fetch budget by year
 * @param {number} year
 * @returns {Promise<Array>} [{csp, year, month, budget, currency}, ...]
 */
export const getBudgetsByYear = (year) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: mockBudgetData });
  }
  return budgetClient.get(`/${year}`);
};

/**
 * Bulk save/update budget
 * @param {Object} payload - {budgets: [{csp, year, month, budget}, ...]}
 * @returns {Promise<Array>}
 */
export const upsertBudgets = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: mockBudgetData });
  }
  return budgetClient.post("/save", payload);
};
