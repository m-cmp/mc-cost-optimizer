import Chart from "react-apexcharts";
import { chartColors } from "../../../utils/styles/colors";

/**
 * @component BarChart
 *
 * @description
 * 막대형(Bar) 차트를 그려주는 공용 컴포넌트.
 *
 * @prop {Array<{ name: string, data: number[] }>} series
 *   차트 데이터 시리즈 배열 (예: [{ name: "Bill", data: [10, 20, 30] }])
 *
 * @prop {Array<string>} categories
 *   X축 라벨 배열 (예: ["Jan", "Feb", "Mar"])
 *
 * @prop {string|number} [width="100%"]
 *   차트 너비 (픽셀 또는 퍼센트)
 *
 * @prop {string|number} [height=300]
 *   차트 높이 (픽셀)
 *
 * @prop {Array<string>} [colors=chartColors.default]
 *   막대 색상 배열 (기본값은 공통 팔레트)
 *
 * @prop {string} [yAxisTitle=""]
 *   Y축 제목 (예: "USD")
 */
export default function BarChart({
  series,
  categories,
  width = "100%",
  height = 300,
  colors = chartColors.default,
  yAxisTitle = "",
}) {
  const options = {
    chart: {
      type: "bar",
      toolbar: { show: false },
    },
    xaxis: {
      categories,
      labels: { style: { fontSize: "12px" } },
    },
    yaxis: {
      title: { text: yAxisTitle },
      labels: {
        style: { fontSize: "12px" },
        formatter: (val) => `${val.toFixed(2)} USD`,
      },
    },
    plotOptions: {
      bar: {
        borderRadius: 6,
        columnWidth: "50%",
        dataLabels: {
          position: "top",
        },
      },
    },
    dataLabels: {
      enabled: true,
      formatter: (val) => (val === 0 ? "" : `${val.toFixed(2)}`),
      style: {
        fontSize: "12px",
        fontWeight: "bold",
        colors: ["#4B5563"],
      },
      offsetY: -20,
    },
    colors,
    tooltip: {
      theme: "dark",
      y: {
        formatter: (val) => `${val.toFixed(2)} USD`,
      },
    },
    legend: { show: false },
  };

  return (
    <Chart
      options={options}
      series={series}
      type="bar"
      width={width}
      height={height}
    />
  );
}
