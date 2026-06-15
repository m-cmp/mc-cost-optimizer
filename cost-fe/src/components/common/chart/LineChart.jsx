import Chart from "react-apexcharts";
import { chartColors } from "../../../utils/styles/colors";

/**
 * @component LineChart
 *
 * @description
 * 순수 라인 차트 렌더링 컴포넌트.
 *
 * @prop {Array<string>} categories X축 라벨
 * @prop {Array<{name: string, data: number[]}>} series 시리즈 데이터
 * @prop {Array<string>} [colors] 라인 색상 배열
 * @prop {string} [curve="straight"] 라인 곡선 타입 ("straight" | "smooth" | "stepline")
 */
export default function LineChart({
  categories = [],
  series = [],
  width = "100%",
  height = 300,
  colors = chartColors,
  curve = "straight",
}) {
  const options = {
    chart: {
      type: "line",
      toolbar: { show: false },
    },
    stroke: {
      width: 2,
      curve,
    },
    xaxis: {
      categories,
      labels: { style: { fontSize: "11px" } },
    },
    yaxis: {
      labels: {
        style: { fontSize: "11px" },
        formatter: (val) => `${val} USD`,
      },
    },
    tooltip: {
      theme: "dark",
      y: {
        formatter: (val) => `${val.toFixed(2)} USD`,
      },
    },
    legend: {
      show: true,
      position: "bottom",
    },
    colors,
  };

  return (
    <Chart
      options={options}
      series={series}
      type="line"
      width={width}
      height={height}
    />
  );
}
