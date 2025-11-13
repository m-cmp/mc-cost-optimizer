import { budgetClient, USE_MOCK } from "../Client";
import { mockBudgetData, mockBudgetComparisonData } from "@/config/mockData";

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
 * @param {string} projectId
 * @returns {Promise<Array>} [{csp, year, month, budget, currency}, ...]
 */
export const getBudgetsByYear = (year, projectId) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: mockBudgetData });
  }
  return budgetClient.get(`/${year}?projectId=${projectId}`);
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

/**
 * Fetch budget vs actual comparison by year
 * @param {number} year
 * @param {string} projectId
 * @returns {Promise<Object>} { year, months: [{month, yearMonth, budget: {total, AWS, NCP, AZURE}, actual: {...}}, ...] }
 */
export const getBudgetComparison = (year, projectId) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: mockBudgetComparisonData });
  }
  return budgetClient.get(`/comparison/${year}?projectId=${projectId}`);
};
