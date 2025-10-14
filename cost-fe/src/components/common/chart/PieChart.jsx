import Chart from "react-apexcharts";
import { chartColors } from "../../../utils/styles/colors";

export default function PieChart({
  series,
  labels,
  width = "100%",
  height = 350,
  colors,
  legendPosition = "bottom",
  legendFontSize = "12px",
  donutSize = "65%",
  centerValueFontSize = "15px",
  centerTotalFontSize = "12px",
  dataLabelFontSize = "10px",
  unit = "USD",
}) {
  const appliedColors = colors || chartColors.default;

  const options = {
    chart: {
      type: "donut",
      dropShadow: { enabled: false },
    },
    labels,
    colors: appliedColors,
    legend: { show: true, position: legendPosition, fontSize: legendFontSize },
    tooltip: {
      theme: "dark",
      y: { formatter: (val) => `${val.toFixed(2)} ${unit}` },
    },
    plotOptions: {
      pie: {
        donut: {
          size: donutSize,
          labels: {
            show: true,
            showAlways: true,
            value: {
              fontSize: centerValueFontSize,
              fontWeight: "bold",
              color: "#333",
              formatter: (val) => {
                return `${parseFloat(val).toFixed(2)} ${unit}`;
              },
            },
            total: {
              show: true,
              label: "Total",
              fontSize: centerTotalFontSize,
              color: "#666",
              formatter: (w) =>
                `${w.globals.seriesTotals
                  .reduce((a, b) => a + b, 0)
                  .toFixed(2)} ${unit}`,
            },
          },
        },
      },
    },
    dataLabels: {
      enabled: true,
      dropShadow: { enabled: false },
      style: {
        fontSize: dataLabelFontSize,
        fontWeight: "bold",
        textShadow: "none",
      },
      formatter: (val) => val.toFixed(1) + "%",
    },
  };

  return (
    <Chart
      options={options}
      series={series}
      type="donut"
      width={width}
      height={height}
    />
  );
}
