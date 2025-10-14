import { useState, useEffect } from "react";
import {
  getAvailableYears,
  getBudgetsByYear,
  upsertBudgets,
} from "@/api/budget/budget";
import { useAlertStore } from "@/stores/useAlertStore";
import {
  transformApiToUiFormat,
  transformUiToApiFormat,
} from "@/utils/budgetUtils";
import { logger } from "@/utils/logger";

/**
 * @hook useBudgetData
 * @description 예산 데이터를 관리하는 Custom Hook
 * @param {number} year - 조회할 연도
 * @returns {Object} 예산 데이터 및 상태
 * @returns {Array<number>} returns.availableYears - 존재하는 연도 목록
 * @returns {Object} returns.cspBudgets - CSP별 월별 예산 데이터
 * @returns {Function} returns.setCspBudgets - 예산 데이터 설정 함수
 * @returns {boolean} returns.loading - 데이터 로딩 상태
 * @returns {boolean} returns.saving - 저장 중 상태
 * @returns {Function} returns.saveBudgets - 예산 저장 함수
 * @returns {Function} returns.resetBudgets - 예산 초기화 함수
 */
export const useBudgetData = (year) => {
  const { addAlert } = useAlertStore();

  const [availableYears, setAvailableYears] = useState([]);
  const [cspBudgets, setCspBudgets] = useState({});
  const [originalBudgets, setOriginalBudgets] = useState({});
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // 초기 로드: 존재하는 연도 목록 조회
  useEffect(() => {
    const fetchAvailableYears = async () => {
      try {
        const response = await getAvailableYears();
        setAvailableYears(response.data || []);
      } catch (error) {
        logger.error("Failed to fetch available years:", error);
        // 에러 시 기본 연도 설정
        setAvailableYears([2023, 2024, 2025]);
      }
    };

    fetchAvailableYears();
  }, []);

  // Year 변경 시 데이터 로드
  useEffect(() => {
    const fetchBudgets = async () => {
      setLoading(true);
      try {
        const response = await getBudgetsByYear(year);
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
        // 에러 발생 시 빈 상태로 초기화
        setCspBudgets({});
        setOriginalBudgets({});
      } finally {
        setLoading(false);
      }
    };

    fetchBudgets();
  }, [year, addAlert]);

  // 예산 저장 함수
  const saveBudgets = async () => {
    try {
      setSaving(true);
      const payload = transformUiToApiFormat(cspBudgets, year);
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
        // 성공 시 원본 데이터 업데이트
        setOriginalBudgets(cspBudgets);
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

  // 예산 초기화 함수
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
