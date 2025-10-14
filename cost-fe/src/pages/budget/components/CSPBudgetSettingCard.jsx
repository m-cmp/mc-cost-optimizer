import { useState, useRef, useEffect, useMemo } from "react";
import Card from "@/components/common/card/Card";
import Button from "@/components/common/button/Button";
import Tooltip from "@/components/common/tooltip/Tooltip";
import {
  calculateCSPTotal,
  calculateCurrencyTotal,
  getCSPColorClass,
  validateBudgetInput,
  MONTH_NAMES,
  getCurrencySymbol,
  getCspCurrency,
} from "@/utils/budgetUtils";
import { formatCompactNumber, formatFullNumber } from "@/utils/format";

const HOVER_BG_COLOR = "rgba(32, 107, 196, 0.1)";
const ALLOWED_KEYS = ["Backspace", "Delete", "Tab", "ArrowLeft", "ArrowRight"];
const CELL_WIDTH = "80px";

/** EditableCell 스타일 상수 */
const EDITABLE_CELL_STYLE = {
  cursor: "pointer",
  minHeight: "31px",
  display: "flex",
  alignItems: "center",
  justifyContent: "flex-end",
  transition: "background-color 0.2s ease",
};

/** EditableCell의 key 생성 헬퍼 함수 */
const getCellKey = (csp, monthIdx, budget, isEditing) => {
  return isEditing ? `editing-${csp}-${monthIdx}` : `${csp}-${monthIdx}-${budget}`;
};

/** 통화별 설정 */
const CURRENCIES = [
  { currency: "USD", symbol: "$" },
  { currency: "KRW", symbol: "₩" },
];

/** 툴팁과 함께 축약된 숫자를 표시 */
const NumberWithTooltip = ({ value, prefix = "", suffix = "" }) => (
  <Tooltip
    title={`${prefix}${formatFullNumber(value)}${suffix}`}
    placement="top"
  >
    {prefix}
    {formatCompactNumber(value)}
    {suffix}
  </Tooltip>
);

/** 편집 가능한 셀 (클릭 시 input으로 전환) */
const EditableCell = ({
  budget,
  csp,
  monthIdx,
  isEditing,
  tempValue,
  onCellClick,
  onTempValueChange,
  onSave,
  onKeyDown,
  inputRef,
}) => {
  if (isEditing) {
    return (
      <Tooltip title={formatFullNumber(tempValue || 0)} placement="top">
        <input
          ref={inputRef}
          type="text"
          className="form-control form-control-sm text-end"
          value={tempValue}
          onChange={(e) => onTempValueChange(e.target.value)}
          onBlur={onSave}
          onKeyDown={onKeyDown}
        />
      </Tooltip>
    );
  }

  return (
    <Tooltip title={formatFullNumber(budget)} placement="top">
      <div
        onClick={() => onCellClick(csp, monthIdx)}
        className="text-end px-2 py-1"
        style={EDITABLE_CELL_STYLE}
        onMouseEnter={(e) => {
          e.currentTarget.style.backgroundColor = HOVER_BG_COLOR;
        }}
        onMouseLeave={(e) => {
          e.currentTarget.style.backgroundColor = "transparent";
        }}
      >
        {formatCompactNumber(budget)}
      </div>
    </Tooltip>
  );
};

/** 통화별 합계 행 (USD, KRW) */
const CurrencySummaryRow = ({ cspBudgets, currency, symbol }) => (
  <tr className="table-info fw-bold">
    <td>{currency}</td>
    <td className="text-center">
      <NumberWithTooltip
        value={calculateCurrencyTotal(cspBudgets, currency)}
        prefix={symbol}
      />
    </td>
    {Array.from({ length: 12 }, (_, monthIdx) => (
      <td key={monthIdx} className="text-center">
        <NumberWithTooltip
          value={calculateCurrencyTotal(cspBudgets, currency, monthIdx)}
          prefix={symbol}
        />
      </td>
    ))}
  </tr>
);

/**
 * CSP별 예산 설정 카드
 * - 각 CSP별로 월별 예산을 입력할 수 있는 테이블
 * - 셀 클릭으로 편집 모드 진입
 * - 통화별(USD, KRW) 합계 자동 계산
 */
export default function CSPBudgetSettingCard({
  cspBudgets,
  onBudgetChange,
  onSave,
  onReset,
  isSaving = false,
}) {
  const [editingCell, setEditingCell] = useState(null); // 현재 편집 중인 셀 { csp, monthIdx }
  const [tempValue, setTempValue] = useState(""); // 편집 중인 임시 값
  const inputRef = useRef(null); // input 요소 참조

  if (!cspBudgets) return null;

  // 편집 모드 진입 시 input 포커스 및 텍스트 선택
  useEffect(() => {
    if (editingCell && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editingCell]);

  /** 셀 클릭 시 편집 모드 진입 */
  const handleCellClick = (csp, monthIdx) => {
    const currentValue = cspBudgets[csp][monthIdx].toString();
    setEditingCell({ csp, monthIdx });
    setTempValue(currentValue);
  };

  /** 부모 컴포넌트로 예산 데이터 동기화 */
  const syncBudgetToParent = (csp, monthIndex, newValue) => {
    const newBudgets = {
      ...cspBudgets,
      [csp]: cspBudgets[csp].map((val, idx) =>
        idx === monthIndex ? newValue : val
      ),
    };
    onBudgetChange(newBudgets);
  };

  /** 입력값 검증 후 셀 값 저장 */
  const saveCellValue = (csp, monthIndex, value) => {
    if (!value) {
      syncBudgetToParent(csp, monthIndex, 0);
      return;
    }

    const validation = validateBudgetInput(value);
    if (validation.isValid) {
      syncBudgetToParent(csp, monthIndex, validation.value);
    }
  };

  /** 편집 저장 및 편집 모드 종료 */
  const handleSaveCell = () => {
    if (editingCell) {
      saveCellValue(editingCell.csp, editingCell.monthIdx, tempValue);
    }
    setEditingCell(null);
    setTempValue("");
  };

  /** 편집 취소 (변경 사항 버리고 편집 모드 종료) */
  const handleCancelEdit = () => {
    setEditingCell(null);
    setTempValue("");
  };

  /** 키보드 입력 처리 (Enter: 저장, Escape: 취소, 숫자만 허용) */
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSaveCell();
      return;
    }

    if (e.key === "Escape") {
      handleCancelEdit();
      return;
    }

    // 숫자와 허용된 키만 입력 가능
    const isNumber = /[0-9]/.test(e.key);
    const isAllowedKey = ALLOWED_KEYS.includes(e.key);

    if (!isNumber && !isAllowedKey) {
      e.preventDefault();
    }
  };

  /** 편집 중인 값을 고려한 예산 데이터 (합계 계산용) */
  const effectiveBudgets = useMemo(() => {
    if (!editingCell) return cspBudgets;

    const { csp, monthIdx } = editingCell;
    const validation = validateBudgetInput(tempValue);
    const effectiveValue = validation.isValid ? validation.value : (tempValue === "" ? 0 : cspBudgets[csp][monthIdx]);

    return {
      ...cspBudgets,
      [csp]: cspBudgets[csp].map((val, idx) =>
        idx === monthIdx ? effectiveValue : val
      ),
    };
  }, [cspBudgets, editingCell, tempValue]);

  return (
    <Card title="CSP Budget Setting" titleSize={2}>
      <div className="table-responsive">
        <table className="table table-bordered">
          <thead>
            <tr>
              <th>CSP</th>
              <th className="text-center">Total</th>
              {MONTH_NAMES.map((month, idx) => (
                <th key={idx} className="text-center">
                  {month}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {Object.entries(cspBudgets).map(([csp, budgets]) => {
              const cspCurrency = getCspCurrency(csp);
              const cspCurrencySymbol = getCurrencySymbol(cspCurrency);
              return (
                <tr key={csp}>
                  <td>
                    <span
                      className={`badge fs-10 px-2 py-2 ${getCSPColorClass(
                        csp
                      )}`}
                    >
                      {csp}
                    </span>
                  </td>
                  <td className="text-center fw-bold">
                    <NumberWithTooltip
                      value={calculateCSPTotal(effectiveBudgets, csp)}
                      prefix={cspCurrencySymbol}
                    />
                  </td>
                  {budgets.map((budget, monthIdx) => {
                    const isEditing =
                      editingCell?.csp === csp &&
                      editingCell?.monthIdx === monthIdx;

                    return (
                      <td key={monthIdx} style={{ width: CELL_WIDTH }}>
                        <EditableCell
                          key={getCellKey(csp, monthIdx, budget, isEditing)}
                          budget={budget}
                          csp={csp}
                          monthIdx={monthIdx}
                          isEditing={isEditing}
                          tempValue={tempValue}
                          onCellClick={handleCellClick}
                          onTempValueChange={setTempValue}
                          onSave={handleSaveCell}
                          onKeyDown={handleKeyDown}
                          inputRef={inputRef}
                        />
                      </td>
                    );
                  })}
                </tr>
              );
            })}
            {/* 통화별 합계 */}
            {CURRENCIES.map(({ currency, symbol }) => (
              <CurrencySummaryRow
                key={currency}
                cspBudgets={effectiveBudgets}
                currency={currency}
                symbol={symbol}
              />
            ))}
          </tbody>
        </table>
      </div>

      {/* Action Buttons */}
      <div className="mt-3">
        <div className="btn-list">
          <Button variant="primary" onClick={onSave} disabled={isSaving}>
            {isSaving ? "Saving..." : "Save Budget"}
          </Button>
          <Button
            variant="outline-secondary"
            onClick={onReset}
            disabled={isSaving}
          >
            Reset
          </Button>
        </div>
      </div>
    </Card>
  );
}
