import Card from "@/components/common/card/Card";
import BarChart from "@/components/common/chart/BarChart";

export default function BudgetComparisonCard({ data, currency = 'USD' }) {
  if (!data) return null;

  const chartSeries = [
    { name: "Budget", data: data.budget },
    { name: "Actual", data: data.actual }
  ];

  const currencySymbol = currency === 'USD' ? '$' : '₩';
  const yAxisTitle = currency === 'USD' ? 'USD' : 'KRW';

  return (
    <Card title="Monthly Budget vs Actual">
      <BarChart
        series={chartSeries}
        categories={data.categories}
        height={300}
        colors={["#206bc4", "#d63384"]}
        yAxisTitle={yAxisTitle}
      />
    </Card>
  );
}