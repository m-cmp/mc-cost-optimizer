import Card from "@/components/common/card/Card";
import { analyzeBudgetPerformance } from "@/utils/budgetUtils";
import { MONTH_NAMES } from "@/constants/dateConstants";

export default function MonthlySummaryCard({ data }) {
  if (!data || !data.months) {
    return (
      <Card title="Monthly Summary" titleSize={2}>
        <div className="text-center py-5 text-muted">
          No data available
        </div>
      </Card>
    );
  }

  return (
    <Card title="Monthly Summary" titleSize={2}>
      <div className="table-responsive">
        <table className="table table-bordered">
          <thead>
            <tr>
              <th>MONTH</th>
              <th className="text-end">BUDGET</th>
              <th className="text-end">ACTUAL</th>
              <th className="text-end">DIFFERENCE</th>
              <th className="text-end">ACHIEVEMENT</th>
            </tr>
          </thead>
          <tbody>
            {data.months.map((monthData) => {
              const budget = monthData.budget.total;
              const actual = monthData.actual.total;
              const analysis = analyzeBudgetPerformance(budget, actual);
              const monthName = MONTH_NAMES[monthData.month - 1];

              return (
                <tr key={monthData.yearMonth}>
                  <td>{monthName}</td>
                  <td className="text-end">${budget.toLocaleString()}</td>
                  <td className="text-end">${actual.toLocaleString()}</td>
                  <td
                    className={`text-end ${
                      analysis.isOverBudget ? "text-danger" : "text-success"
                    }`}
                  >
                    {analysis.difference >= 0 ? "+" : ""}$
                    {Math.abs(analysis.difference).toLocaleString()}
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
