import Card from "@/components/common/card/Card";
import PieChart from "@/components/common/chart/PieChart";

export default function TopServicesCard({ data }) {
  const series = data.map((item) => item.bill);
  const labels = data.map((item) => `${item.resourceNm} (${item.csp})`);

  return (
    <Card title="Top 5 Services by Cost" titleSize={2}>
      <PieChart
        series={series}
        labels={labels}
        height={270}
        donutSize="60%"
        centerValueFontSize="14px"
        centerTotalFontSize="12px"
        dataLabelFontSize="10px"
      />
    </Card>
  );
}
