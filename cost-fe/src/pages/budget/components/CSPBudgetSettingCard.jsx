import { useState, useRef, useEffect, useMemo } from "react";
import Card from "@/components/common/card/Card";
import Button from "@/components/common/button/Button";
import Tooltip from "@/components/common/tooltip/Tooltip";
import {
  calculateCSPTotal,
  validateBudgetInput,
  MONTH_NAMES,
} from "@/utils/budgetUtils";
import { formatCompactNumber, formatFullNumber } from "@/utils/format";
import { CSP_CONFIG, getCSPColorClass } from "@/constants/cspConstants";
import { convertKrwToUsd } from "@/constants/currencyConstants";

const HOVER_BG_COLOR = "rgba(32, 107, 196, 0.1)";
const ALLOWED_KEYS = ["Backspace", "Delete", "Tab", "ArrowLeft", "ArrowRight"];
const CELL_WIDTH = "80px";

/** Inline cell styling */
const EDITABLE_CELL_STYLE = {
  cursor: "pointer",
  minHeight: "31px",
  display: "flex",
  alignItems: "center",
  justifyContent: "flex-end",
  transition: "background-color 0.2s ease",
};

/** Generate a stable key for each cell */
const getCellKey = (csp, monthIdx, budget, isEditing) =>
  isEditing ? `editing-${csp}-${monthIdx}` : `${csp}-${monthIdx}-${budget}`;

/** Displays a number in compact form with full value on hover tooltip */
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

/** A single table cell that switches between display and input mode */
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
  const isNCP = csp === "NCP";
  const currencySymbol = isNCP ? "₩" : "$";

  if (isEditing) {
    return (
      <Tooltip
        title={`${currencySymbol}${formatFullNumber(tempValue || 0)}`}
        placement="top"
      >
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
    <Tooltip
      title={`${currencySymbol}${formatFullNumber(budget)}`}
      placement="top"
    >
      <div
        onClick={() => onCellClick(csp, monthIdx)}
        className="text-end px-2 py-1"
        style={EDITABLE_CELL_STYLE}
        onMouseEnter={(e) =>
          (e.currentTarget.style.backgroundColor = HOVER_BG_COLOR)
        }
        onMouseLeave={(e) =>
          (e.currentTarget.style.backgroundColor = "transparent")
        }
      >
        {formatCompactNumber(budget)}
      </div>
    </Tooltip>
  );
};

/** Displays the total budget summary in USD */
const TotalSummaryRow = ({ cspBudgets }) => {
  const getTotalInUSD = (monthIdx = null) => {
    let total = 0;
    Object.entries(cspBudgets).forEach(([csp, budgets]) => {
      const isNCP = csp === "NCP";
      const values = monthIdx !== null ? [budgets[monthIdx] || 0] : budgets;
      const subtotal = values.reduce((sum, val) => sum + val, 0);
      total += isNCP ? convertKrwToUsd(subtotal) : subtotal;
    });
    return Math.round(total * 100) / 100;
  };

  return (
    <tr className="table-info fw-bold">
      <td>Total (USD)</td>
      <td className="text-center">
        <NumberWithTooltip value={getTotalInUSD()} prefix="$" />
      </td>
      {Array.from({ length: 12 }).map((_, monthIdx) => (
        <td key={monthIdx} className="text-center">
          <NumberWithTooltip value={getTotalInUSD(monthIdx)} prefix="$" />
        </td>
      ))}
    </tr>
  );
};

/**
 * CSPBudgetSettingCard
 * Displays a table for setting monthly budgets per CSP.
 * - Cells are editable
 * - Shows compact numbers with hover tooltips
 * - Calculates totals (converted to USD)
 */
export default function CSPBudgetSettingCard({
  cspBudgets,
  onBudgetChange,
  onSave,
  onReset,
  isSaving = false,
}) {
  const [editingCell, setEditingCell] = useState(null);
  const [tempValue, setTempValue] = useState("");
  const inputRef = useRef(null);

  if (!cspBudgets) return null;

  // Focus and select the text when entering edit mode
  useEffect(() => {
    if (editingCell && inputRef.current) {
      inputRef.current.focus();
      inputRef.current.select();
    }
  }, [editingCell]);

  /** Handle clicking a cell to edit */
  const handleCellClick = (csp, monthIdx) => {
    setEditingCell({ csp, monthIdx });
    setTempValue(cspBudgets[csp][monthIdx].toString());
  };

  /** Update parent component with new data */
  const syncBudgetToParent = (csp, monthIdx, newValue) => {
    const updated = {
      ...cspBudgets,
      [csp]: cspBudgets[csp].map((v, i) => (i === monthIdx ? newValue : v)),
    };
    onBudgetChange(updated);
  };

  /** Validate and save edited value */
  const saveCellValue = (csp, monthIdx, value) => {
    const { isValid, value: parsedValue } = validateBudgetInput(value);
    syncBudgetToParent(csp, monthIdx, isValid ? parsedValue : 0);
  };

  const handleSaveCell = () => {
    if (editingCell)
      saveCellValue(editingCell.csp, editingCell.monthIdx, tempValue);
    setEditingCell(null);
    setTempValue("");
  };

  const handleCancelEdit = () => {
    setEditingCell(null);
    setTempValue("");
  };

  /** Handle keyboard events */
  const handleKeyDown = (e) => {
    if (e.key === "Enter") return handleSaveCell();
    if (e.key === "Escape") return handleCancelEdit();
    if (e.ctrlKey || e.metaKey) return;

    const isNumber = /[0-9]/.test(e.key);
    if (!isNumber && !ALLOWED_KEYS.includes(e.key)) e.preventDefault();
  };

  /** Adjust active editing value in total calculation */
  const effectiveBudgets = useMemo(() => {
    if (!editingCell) return cspBudgets;
    const { csp, monthIdx } = editingCell;
    const { isValid, value } = validateBudgetInput(tempValue);
    const effectiveValue = isValid
      ? value
      : tempValue === ""
      ? 0
      : cspBudgets[csp][monthIdx];
    return {
      ...cspBudgets,
      [csp]: cspBudgets[csp].map((v, i) =>
        i === monthIdx ? effectiveValue : v
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
              {MONTH_NAMES.map((month, i) => (
                <th key={i} className="text-center">
                  {month}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {Object.entries(cspBudgets).map(([csp, budgets]) => {
              const isNCP = csp === "NCP";
              const total = calculateCSPTotal(effectiveBudgets, csp);
              const displayTotal = isNCP
                ? Math.round(convertKrwToUsd(total) * 100) / 100
                : total;

              return (
                <tr key={csp}>
                  <td>
                    <span
                      className={`badge fs-10 px-2 py-2 ${getCSPColorClass(
                        csp
                      )}`}
                    >
                      {CSP_CONFIG[csp]?.name || csp}
                    </span>
                  </td>
                  <td className="text-center fw-bold">
                    <NumberWithTooltip value={displayTotal} prefix="$" />
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
            <TotalSummaryRow cspBudgets={effectiveBudgets} />
          </tbody>
        </table>
      </div>

      <div className="mt-3 d-flex gap-2">
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
    </Card>
  );
}
