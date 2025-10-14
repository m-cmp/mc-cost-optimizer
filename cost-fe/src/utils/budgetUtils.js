/**
 * Budget 관련 유틸리티 함수들
 */

import { MONTH_NAMES } from "../constants/dateConstants";
import { getCSPColorClass as getCSPColorClassFromConstants } from "../constants/cspConstants";

// MONTH_NAMES를 re-export하여 기존 import 경로 유지
export { MONTH_NAMES };

/**
 * 통화 기호 반환
 * @param {string} currency - 통화 코드 ("USD" | "KRW")
 * @returns {string} 통화 기호
 */
export const getCurrencySymbol = (currency) => {
  return currency === "USD" ? "$" : "₩";
};

/**
 * 통화 변환 (USD <-> KRW)
 * TODO: 향후 API로 실시간 환율을 받아오도록 변경 예정
 * @param {number} value - 변환할 금액
 * @param {string} currency - 대상 통화 ("USD" | "KRW")
 * @param {number} [exchangeRate=1400] - 환율 (1 USD = KRW)
 * @returns {number} 변환된 금액
 */
export const convertCurrency = (value, currency, exchangeRate = 1400) => {
  if (currency === "KRW") {
    return Math.round(value * exchangeRate);
  }
  return value;
};

/**
 * CSP별 월별 예산 총합 계산
 * @param {Object} cspBudgets - CSP별 월별 예산 데이터
 * @param {string} csp - CSP 이름
 * @returns {number} CSP별 총 예산
 */
export const calculateCSPTotal = (cspBudgets, csp) => {
  if (!cspBudgets[csp]) return 0;
  return cspBudgets[csp].reduce((sum, val) => sum + (val || 0), 0);
};

/**
 * 특정 월의 모든 CSP 예산 총합 계산
 * @param {Object} cspBudgets - CSP별 월별 예산 데이터
 * @param {number} monthIndex - 월 인덱스 (0-11)
 * @returns {number} 해당 월의 총 예산
 */
export const calculateMonthTotal = (cspBudgets, monthIndex) => {
  return Object.values(cspBudgets).reduce((sum, budgets) => {
    return sum + (budgets[monthIndex] || 0);
  }, 0);
};

/**
 * 전체 연간 예산 총합 계산
 * @param {Object} cspBudgets - CSP별 월별 예산 데이터
 * @returns {number} 연간 총 예산
 */
export const calculateYearTotal = (cspBudgets) => {
  return Object.values(cspBudgets).reduce((sum, budgets) => {
    return sum + budgets.reduce((a, b) => a + (b || 0), 0);
  }, 0);
};

/**
 * 월별 예산 대비 실사용량 분석
 * @param {number} budget - 예산
 * @param {number} actual - 실사용량
 * @returns {Object} 분석 결과 (차이, 달성률, 상태)
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
 * CSP별 색상 클래스 반환
 * @deprecated 대신 cspConstants.js의 getCSPColorClass 사용 권장
 * @param {string} csp - CSP 이름
 * @returns {string} Bootstrap 색상 클래스
 */
export const getCSPColorClass = (csp) => {
  return getCSPColorClassFromConstants(csp);
};

/**
 * 예산 입력값 유효성 검사
 * @param {number} value - 입력값
 * @returns {Object} 검사 결과
 */
export const validateBudgetInput = (value) => {
  const numValue = parseFloat(value);

  if (isNaN(numValue) || numValue < 0) {
    return { isValid: false, error: "Budget must be a positive number" };
  }

  return { isValid: true, value: numValue };
};

/**
 * CSP별 통화 반환
 * @param {string} csp - CSP 이름
 * @returns {string} 통화 코드 ("USD" | "KRW")
 */
export const getCspCurrency = (csp) => {
  const upperCsp = csp?.toUpperCase();
  if (upperCsp === "NCP") {
    return "KRW";
  }
  // AWS, Azure, GCP 등은 USD
  return "USD";
};

/**
 * 특정 통화의 총 예산 계산
 * @param {Object} cspBudgets - CSP별 월별 예산 데이터
 * @param {string} currency - 통화 코드 ("USD" | "KRW")
 * @param {number|null} monthIdx - 월 인덱스 (0-11), null이면 전체 합계
 * @returns {number} 해당 통화의 총 예산
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
 * API 응답을 UI 형식으로 변환
 * @param {Array} apiData - [{csp, year, month, budget, currency}, ...]
 * @returns {Object} {AWS: [budget1, budget2, ...], Azure: [...], NCP: [...]}
 */
export const transformApiToUiFormat = (apiData) => {
  // 기본 CSP 목록을 0으로 초기화
  const result = {
    AWS: Array(12).fill(0),
    Azure: Array(12).fill(0),
    NCP: Array(12).fill(0),
  };

  if (!apiData || !Array.isArray(apiData) || apiData.length === 0) {
    return result;
  }

  // API 데이터로 채우기 (month는 1-12, 인덱스는 0-11)
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
 * UI 형식을 API 요청 형식으로 변환
 * @param {Object} cspBudgets - {AWS: [budget1, budget2, ...], Azure: [...], NCP: [...]}
 * @param {number} year - 연도
 * @param {Object} originalBudgets - 변경 전 원본 데이터 (선택적, 제공 시 변경된 항목만 포함)
 * @returns {Object} {budgets: [{csp, year, month, budget}, ...]}
 */
export const transformUiToApiFormat = (cspBudgets, year, originalBudgets = null) => {
  const budgets = [];

  Object.entries(cspBudgets).forEach(([csp, monthlyBudgets]) => {
    monthlyBudgets.forEach((budget, index) => {
      // originalBudgets가 제공되면 변경된 항목만 포함
      if (originalBudgets) {
        const originalValue = originalBudgets[csp]?.[index];
        // 값이 변경되지 않았으면 스킵
        if (originalValue === budget) {
          return;
        }
      }

      // 0을 포함한 모든 유효한 숫자 값 포함 (명시적으로 0으로 설정한 경우도 반영)
      if (budget !== null && budget !== undefined) {
        budgets.push({
          csp,
          year,
          month: index + 1, // 인덱스 0-11 -> 월 1-12
          budget: parseFloat(budget),
        });
      }
    });
  });

  return { budgets };
};
