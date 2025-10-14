import Card from "@/components/common/card/Card";
import Button from "@/components/common/button/Button";
import {
  calculateCSPTotal,
  calculateCurrencyTotal,
  getCSPColorClass,
  validateBudgetInput,
  MONTH_NAMES,
  getCurrencySymbol,
  getCspCurrency,
} from "@/utils/budgetUtils";

export default function CSPBudgetSettingCard({
  cspBudgets,
  onBudgetChange,
  onSave,
  onReset,
  isSaving = false,
}) {
  if (!cspBudgets) return null;

  const handleBudgetChange = (csp, monthIndex, value) => {
    // 빈 값이면 0으로 처리
    if (value === "" || value === null || value === undefined) {
      const newBudgets = {
        ...cspBudgets,
        [csp]: cspBudgets[csp].map((val, idx) =>
          idx === monthIndex ? 0 : val
        ),
      };
      onBudgetChange(newBudgets);
      return;
    }

    const validation = validateBudgetInput(value);
    if (validation.isValid) {
      const newBudgets = {
        ...cspBudgets,
        [csp]: cspBudgets[csp].map((val, idx) =>
          idx === monthIndex ? validation.value : val
        ),
      };
      onBudgetChange(newBudgets);
    }
  };

  return (
    <Card title="CSP Budget Setting">
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
                    {cspCurrencySymbol}
                    {calculateCSPTotal(cspBudgets, csp).toLocaleString()}
                  </td>
                  {budgets.map((budget, monthIdx) => (
                    <td key={monthIdx} style={{ width: "80px" }}>
                      <input
                        type="text"
                        className="form-control form-control-sm text-end"
                        value={budget}
                        onChange={(e) =>
                          handleBudgetChange(csp, monthIdx, e.target.value)
                        }
                        onKeyDown={(e) => {
                          // 숫자, Backspace, Delete, Tab, Arrow keys만 허용
                          if (
                            !/[0-9]/.test(e.key) &&
                            ![
                              "Backspace",
                              "Delete",
                              "Tab",
                              "ArrowLeft",
                              "ArrowRight",
                            ].includes(e.key)
                          ) {
                            e.preventDefault();
                          }
                        }}
                      />
                    </td>
                  ))}
                </tr>
              );
            })}
            {/* USD */}
            <tr className="table-info fw-bold">
              <td>USD</td>
              <td className="text-center">
                ${calculateCurrencyTotal(cspBudgets, "USD").toLocaleString()}
              </td>
              {Array.from({ length: 12 }, (_, monthIdx) => (
                <td key={monthIdx} className="text-center">
                  $
                  {calculateCurrencyTotal(
                    cspBudgets,
                    "USD",
                    monthIdx
                  ).toLocaleString()}
                </td>
              ))}
            </tr>
            {/* KRW */}
            <tr className="table-info fw-bold">
              <td>KRW</td>
              <td className="text-center">
                ₩{calculateCurrencyTotal(cspBudgets, "KRW").toLocaleString()}
              </td>
              {Array.from({ length: 12 }, (_, monthIdx) => (
                <td key={monthIdx} className="text-center">
                  ₩
                  {calculateCurrencyTotal(
                    cspBudgets,
                    "KRW",
                    monthIdx
                  ).toLocaleString()}
                </td>
              ))}
            </tr>
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
