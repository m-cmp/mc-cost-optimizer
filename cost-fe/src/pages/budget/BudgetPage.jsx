import { useState } from "react";
import { budgetData } from "@/config/mockData";
import Grid from "@/components/layout/Grid";
import Dropdown from "@/components/common/dropdown/Dropdown";
import BudgetComparisonCard from "./components/BudgetComparisonCard";
import MonthlySummaryCard from "./components/MonthlySummaryCard";
import CSPBudgetSettingCard from "./components/CSPBudgetSettingCard";
import Loading from "@/components/common/loading/Loading";
import { useBudgetData } from "@/hooks/useBudgetData";

export default function BudgetPage() {
  const [selectedYear, setSelectedYear] = useState(budgetData.Data.year);
  const {
    availableYears,
    cspBudgets,
    setCspBudgets,
    loading,
    saving,
    saveBudgets,
    resetBudgets,
  } = useBudgetData(selectedYear);

  const monthlyData = budgetData.Data.monthly;

  // Convert the year list received from API to Dropdown format
  const yearOptions = availableYears.map((year) => ({
    value: year,
    label: year.toString(),
  }));

  if (loading) {
    return <Loading fullscreen withLabel label="Loading data..." />;
  }

  return (
    <>
      <div>
        <div className="d-flex gap-2 mb-4">
          <div>
            <label className="form-label">Year</label>
            <Dropdown
              trigger={selectedYear}
              items={yearOptions}
              selectedValue={selectedYear}
              onSelect={(value) => setSelectedYear(value)}
              className="btn-outline-secondary"
            />
          </div>
        </div>

        <Grid>
          <BudgetComparisonCard
            data={monthlyData}
            currency="USD"
            colSpan={12}
          />
          <CSPBudgetSettingCard
            cspBudgets={cspBudgets}
            onBudgetChange={setCspBudgets}
            onSave={saveBudgets}
            onReset={resetBudgets}
            isSaving={saving}
            colSpan={12}
          />
          <MonthlySummaryCard data={monthlyData} currency="USD" colSpan={12} />
        </Grid>
      </div>
    </>
  );
}
