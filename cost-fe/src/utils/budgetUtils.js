/**
 * Budget-related utility functions
 */

import { MONTH_NAMES } from "../constants/dateConstants";
import { getCSPColorClass as getCSPColorClassFromConstants } from "../constants/cspConstants";

// Re-export MONTH_NAMES to maintain existing import path
export { MONTH_NAMES };

/**
 * Return currency symbol
 * @param {string} currency - Currency code ("USD" | "KRW")
 * @returns {string} Currency symbol
 */
export const getCurrencySymbol = (currency) => {
  return currency === "USD" ? "$" : "₩";
};

/**
 * Currency conversion (USD <-> KRW)
 * TODO: Plan to change to fetch real-time exchange rate from API
 * @param {number} value - Amount to convert
 * @param {string} currency - Target currency ("USD" | "KRW")
 * @param {number} [exchangeRate=1400] - Exchange rate (1 USD = KRW)
 * @returns {number} Converted amount
 */
export const convertCurrency = (value, currency, exchangeRate = 1400) => {
  if (currency === "KRW") {
    return Math.round(value * exchangeRate);
  }
  return value;
};

/**
 * Calculate total monthly budget by CSP
 * @param {Object} cspBudgets - Monthly budget data by CSP
 * @param {string} csp - CSP name
 * @returns {number} Total budget by CSP
 */
export const calculateCSPTotal = (cspBudgets, csp) => {
  if (!cspBudgets[csp]) return 0;
  return cspBudgets[csp].reduce((sum, val) => sum + (val || 0), 0);
};

/**
 * Calculate total budget for all CSPs in a specific month
 * @param {Object} cspBudgets - Monthly budget data by CSP
 * @param {number} monthIndex - Month index (0-11)
 * @returns {number} Total budget for the month
 */
export const calculateMonthTotal = (cspBudgets, monthIndex) => {
  return Object.values(cspBudgets).reduce((sum, budgets) => {
    return sum + (budgets[monthIndex] || 0);
  }, 0);
};

/**
 * Calculate total annual budget
 * @param {Object} cspBudgets - Monthly budget data by CSP
 * @returns {number} Total annual budget
 */
export const calculateYearTotal = (cspBudgets) => {
  return Object.values(cspBudgets).reduce((sum, budgets) => {
    return sum + budgets.reduce((a, b) => a + (b || 0), 0);
  }, 0);
};

/**
 * Analyze actual usage against monthly budget
 * @param {number} budget - Budget
 * @param {number} actual - Actual usage
 * @returns {Object} Analysis result (difference, achievement rate, status)
 */
export const analyzeBudgetPerformance = (budget, actual) => {
  const difference = actual - budget;
  const achievement = budget > 0 ? (actual / budget) * 100 : 0;

  let status = "on_track";
  if (achievement > 110) status = "over_budget";
  else if (achievement < 80) status = "under_budget";

  return {
    difference,
    achievement,
    status,
    isOverBudget: difference > 0,
    isUnderBudget: difference < 0,
  };
};

/**
 * Return color class by CSP
 * @deprecated Recommend using getCSPColorClass from cspConstants.js instead
 * @param {string} csp - CSP name
 * @returns {string} Bootstrap color class
 */
export const getCSPColorClass = (csp) => {
  return getCSPColorClassFromConstants(csp);
};

/**
 * Validate budget input value
 * @param {number} value - Input value
 * @returns {Object} Validation result
 */
export const validateBudgetInput = (value) => {
  const numValue = parseFloat(value);

  if (isNaN(numValue) || numValue < 0) {
    return { isValid: false, error: "Budget must be a positive number" };
  }

  return { isValid: true, value: numValue };
};

/**
 * Return currency by CSP
 * @param {string} csp - CSP name
 * @returns {string} Currency code ("USD" | "KRW")
 */
export const getCspCurrency = (csp) => {
  const upperCsp = csp?.toUpperCase();
  if (upperCsp === "NCP") {
    return "KRW";
  }
  // AWS, Azure, GCP, etc. use USD
  return "USD";
};

/**
 * Calculate total budget for a specific currency
 * @param {Object} cspBudgets - Monthly budget data by CSP
 * @param {string} currency - Currency code ("USD" | "KRW")
 * @param {number|null} monthIdx - Month index (0-11), null for total sum
 * @returns {number} Total budget for the currency
 */
export const calculateCurrencyTotal = (cspBudgets, currency, monthIdx = null) => {
  let total = 0;
  Object.entries(cspBudgets).forEach(([csp, budgets]) => {
    if (getCspCurrency(csp) === currency) {
      if (monthIdx !== null) {
        total += budgets[monthIdx] || 0;
      } else {
        total += calculateCSPTotal(cspBudgets, csp);
      }
    }
  });
  return total;
};

/**
 * Transform API response to UI format
 * @param {Array} apiData - [{csp, year, month, budget, currency}, ...]
 * @returns {Object} {AWS: [budget1, budget2, ...], Azure: [...], NCP: [...]}
 */
export const transformApiToUiFormat = (apiData) => {
  // Initialize default CSP list to 0
  const result = {
    AWS: Array(12).fill(0),
    Azure: Array(12).fill(0),
    NCP: Array(12).fill(0),
  };

  if (!apiData || !Array.isArray(apiData) || apiData.length === 0) {
    return result;
  }

  // Fill with API data (month is 1-12, index is 0-11)
  apiData.forEach((item) => {
    const csp = item.csp;
    if (!result[csp]) {
      result[csp] = Array(12).fill(0);
    }
    const monthIndex = item.month - 1;
    if (monthIndex >= 0 && monthIndex < 12) {
      result[csp][monthIndex] = item.budget;
    }
  });

  return result;
};

/**
 * Transform UI format to API request format
 * @param {Object} cspBudgets - {AWS: [budget1, budget2, ...], Azure: [...], NCP: [...]}
 * @param {number} year - Year
 * @param {Object} originalBudgets - Original data before changes (optional, includes only changed items if provided)
 * @param {string} projectId - Project ID
 * @returns {Object} {budgets: [{csp, year, month, budget, projectId}, ...]}
 */
export const transformUiToApiFormat = (cspBudgets, year, originalBudgets = null, projectId) => {
  const budgets = [];

  Object.entries(cspBudgets).forEach(([csp, monthlyBudgets]) => {
    monthlyBudgets.forEach((budget, index) => {
      // Include only changed items if originalBudgets is provided
      if (originalBudgets) {
        const originalValue = originalBudgets[csp]?.[index];
        // Skip if value hasn't changed
        if (originalValue === budget) {
          return;
        }
      }

      // Include all valid numeric values including 0 (also reflect when explicitly set to 0)
      if (budget !== null && budget !== undefined) {
        budgets.push({
          csp,
          year,
          month: index + 1, // Index 0-11 -> Month 1-12
          budget: parseFloat(budget),
          projectId,
        });
      }
    });
  });

  return { budgets };
};
