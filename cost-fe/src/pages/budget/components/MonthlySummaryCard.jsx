import Card from "@/components/common/card/Card";
import { analyzeBudgetPerformance } from "@/utils/budgetUtils";

export default function MonthlySummaryCard({ data, currency = "USD" }) {
  if (!data) return null;

  const currencySymbol = currency === "USD" ? "$" : "₩";
  const convertValue = (value) => {
    if (currency === "KRW") {
      return Math.round(value * 1300); // 1 USD = 1300 KRW 임시 환율
    }
    return value;
  };

  return (
    <Card title="Monthly Summary">
      <div className="table-responsive">
        <table className="table table-bordered">
          <thead>
            <tr>
              <th>Month</th>
              <th className="text-end">Budget</th>
              <th className="text-end">Actual</th>
              <th className="text-end">Difference</th>
              <th className="text-end">Achievement</th>
            </tr>
          </thead>
          <tbody>
            {data.categories.map((month, idx) => {
              const budget = convertValue(data.budget[idx]);
              const actual = convertValue(data.actual[idx]);
              const analysis = analyzeBudgetPerformance(
                data.budget[idx],
                data.actual[idx]
              );

              return (
                <tr key={month}>
                  <td>{month}</td>
                  <td className="text-end">
                    {currencySymbol}
                    {budget.toLocaleString()}
                  </td>
                  <td className="text-end">
                    {currencySymbol}
                    {actual.toLocaleString()}
                  </td>
                  <td
                    className={`text-end ${
                      analysis.isOverBudget ? "text-danger" : "text-success"
                    }`}
                  >
                    {analysis.difference >= 0 ? "+" : ""}
                    {currencySymbol}
                    {convertValue(
                      Math.abs(analysis.difference)
                    ).toLocaleString()}
                  </td>
                  <td
                    className={`text-end ${
                      analysis.achievement > 100
                        ? "text-danger"
                        : "text-success"
                    }`}
                  >
                    {analysis.achievement.toFixed(1)}%
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </Card>
  );
}
