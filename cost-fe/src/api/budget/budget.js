import { budgetClient, USE_MOCK } from "../Client";
import { mockBudgetData } from "@/config/mockData";

/**
 * 존재하는 연도 목록 조회
 * @returns {Promise<Array<number>>} [2023, 2024, 2025, ...]
 */
export const getAvailableYears = () => {
  if (USE_MOCK) {
    return Promise.resolve({ data: [2023, 2024, 2025] });
  }
  return budgetClient.get("/years");
};

/**
 * 연도별 예산 조회
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
 * 예산 일괄 저장/업데이트
 * @param {Object} payload - {budgets: [{csp, year, month, budget}, ...]}
 * @returns {Promise<Array>}
 */
export const upsertBudgets = (payload) => {
  if (USE_MOCK) {
    return Promise.resolve({ data: mockBudgetData });
  }
  return budgetClient.post("/save", payload);
};
