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

/** EditableCell style constants */
const EDITABLE_CELL_STYLE = {
  cursor: "pointer",
  minHeight: "31px",
  display: "flex",
  alignItems: "center",
  justifyContent: "flex-end",
  transition: "background-color 0.2s ease",
};

/** Helper function to generate key for EditableCell */
const getCellKey = (csp, monthIdx, budget, isEditing) => {
  return isEditing ? `editing-${csp}-${monthIdx}` : `${csp}-${monthIdx}-${budget}`;
};

/** Currency configurations */
const CURRENCIES = [
  { currency: "USD", symbol: "$" },
  { currency: "KRW", symbol: "₩" },
];

/** Displays abbreviated number with tooltip */
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

/** Editable cell (switches to input on click) */
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

/** Currency summary row (USD, KRW) */
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
 * CSP Budget Setting Card
 * - Table for entering monthly budgets per CSP
 * - Enter edit mode by clicking cells
 * - Automatically calculates totals by currency (USD, KRW)
 */
export default function CSPBudgetSettingCard({
  cspBudgets,
  onBudgetChange,
  onSave,
  onReset,
  isSaving = false,
}) {
  const [editingCell, setEditingCell] = useState(null); // Currently editing cell { csp, monthIdx }
  const [tempValue, setTempValue] = useState(""); // Temporary value being edited
  const inputRef = useRef(null); // Input element reference

  if (!cspBudgets) return null;

  // Focus input and select text when entering edit mode
  useEffect(() => {
    if (editingCell && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editingCell]);

  /** Enter edit mode on cell click */
  const handleCellClick = (csp, monthIdx) => {
    const currentValue = cspBudgets[csp][monthIdx].toString();
    setEditingCell({ csp, monthIdx });
    setTempValue(currentValue);
  };

  /** Synchronize budget data to parent component */
  const syncBudgetToParent = (csp, monthIndex, newValue) => {
    const newBudgets = {
      ...cspBudgets,
      [csp]: cspBudgets[csp].map((val, idx) =>
        idx === monthIndex ? newValue : val
      ),
    };
    onBudgetChange(newBudgets);
  };

  /** Save cell value after input validation */
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

  /** Save edit and exit edit mode */
  const handleSaveCell = () => {
    if (editingCell) {
      saveCellValue(editingCell.csp, editingCell.monthIdx, tempValue);
    }
    setEditingCell(null);
    setTempValue("");
  };

  /** Cancel edit (discard changes and exit edit mode) */
  const handleCancelEdit = () => {
    setEditingCell(null);
    setTempValue("");
  };

  /** Keyboard input handling (Enter: save, Escape: cancel, numbers only) */
  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSaveCell();
      return;
    }

    if (e.key === "Escape") {
      handleCancelEdit();
      return;
    }

    // Only numbers and allowed keys are permitted
    const isNumber = /[0-9]/.test(e.key);
    const isAllowedKey = ALLOWED_KEYS.includes(e.key);

    if (!isNumber && !isAllowedKey) {
      e.preventDefault();
    }
  };

  /** Budget data considering editing value (for total calculation) */
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
            {/* Currency totals */}
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
