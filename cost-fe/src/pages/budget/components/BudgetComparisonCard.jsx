import { useState } from "react";
import Card from "@/components/common/card/Card";
import Chart from "react-apexcharts";
import { CSP_CONFIG } from "@/constants/cspConstants";
import { MONTH_NAMES } from "@/constants/dateConstants";

export default function BudgetComparisonCard({ data, loading }) {
  const [showDetailedView, setShowDetailedView] = useState(true);

  if (loading) {
    return (
      <Card title="Budget vs Usage Comparison" titleSize={2}>
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </Card>
    );
  }

  if (!data) {
    return (
      <Card title="Budget vs Usage Comparison" titleSize={2}>
        <div className="text-center py-5 text-muted">
          No data available
        </div>
      </Card>
    );
  }
  const categories = data.months.flatMap(() => ["Budget", "Usage"]);
  let series, colors, legendItems;

  if (showDetailedView) {
    const awsBudgetData = data.months.flatMap((m) => [m.budget.AWS || 0, 0]);
    const awsUsageData = data.months.flatMap((m) => [0, m.actual.AWS || 0]);
    const ncpBudgetData = data.months.flatMap((m) => [m.budget.NCP || 0, 0]);
    const ncpUsageData = data.months.flatMap((m) => [0, m.actual.NCP || 0]);
    const azureBudgetData = data.months.flatMap((m) => [
      m.budget.Azure || 0,
      0,
    ]);
    const azureUsageData = data.months.flatMap((m) => [0, m.actual.Azure || 0]);

    series = [
      { name: "AWS-Budget", data: awsBudgetData },
      { name: "AWS-Usage", data: awsUsageData },
      { name: "NCP-Budget", data: ncpBudgetData },
      { name: "NCP-Usage", data: ncpUsageData },
      { name: "Azure-Budget", data: azureBudgetData },
      { name: "Azure-Usage", data: azureUsageData },
    ];

    colors = [
      "#9fb8d9",
      "#1a5a9e", // AWS
      "#8fc9a8",
      "#0d8f63", // NCP
      "#e6b98a",
      "#c77d08", // Azure
    ];

    legendItems = (
      <div className="d-flex justify-content-center align-items-center gap-4 mt-3">
        <div className="d-flex align-items-center">
          <span
            style={{
              width: "12px",
              height: "12px",
              backgroundColor: "#1a5a9e",
              borderRadius: "2px",
              display: "inline-block",
              marginRight: "6px",
            }}
          ></span>
          <span style={{ fontSize: "12px" }}>
            {CSP_CONFIG.AWS?.name || "AWS"}
          </span>
        </div>
        <div className="d-flex align-items-center">
          <span
            style={{
              width: "12px",
              height: "12px",
              backgroundColor: "#0d8f63",
              borderRadius: "2px",
              display: "inline-block",
              marginRight: "6px",
            }}
          ></span>
          <span style={{ fontSize: "12px" }}>
            {CSP_CONFIG.NCP?.name || "NCP"}
          </span>
        </div>
        <div className="d-flex align-items-center">
          <span
            style={{
              width: "12px",
              height: "12px",
              backgroundColor: "#c77d08",
              borderRadius: "2px",
              display: "inline-block",
              marginRight: "6px",
            }}
          ></span>
          <span style={{ fontSize: "12px" }}>
            {CSP_CONFIG.AZURE?.name || "Azure"}
          </span>
        </div>
      </div>
    );
  } else {
    const totalBudgetData = data.months.flatMap((m) => [
      m.budget.total || 0,
      0,
    ]);
    const totalUsageData = data.months.flatMap((m) => [0, m.actual.total || 0]);

    series = [
      { name: "Budget", data: totalBudgetData },
      { name: "Usage", data: totalUsageData },
    ];

    colors = [
      "#f4be62ff", // Budget
      "#2563eb", // Usage
    ];

    legendItems = (
      <div className="d-flex justify-content-center align-items-center gap-4 mt-3">
        <div className="d-flex align-items-center">
          <span
            style={{
              width: "12px",
              height: "12px",
              backgroundColor: "#f4be62ff",
              borderRadius: "2px",
              display: "inline-block",
              marginRight: "6px",
            }}
          ></span>
          <span style={{ fontSize: "12px" }}>Budget</span>
        </div>
        <div className="d-flex align-items-center">
          <span
            style={{
              width: "12px",
              height: "12px",
              backgroundColor: "#2563eb",
              borderRadius: "2px",
              display: "inline-block",
              marginRight: "6px",
            }}
          ></span>
          <span style={{ fontSize: "12px" }}>Usage</span>
        </div>
      </div>
    );
  }

  const options = {
    chart: {
      type: "bar",
      stacked: true,
      toolbar: { show: false },
    },
    xaxis: {
      categories,
      labels: {
        style: {
          fontSize: "11px",
        },
        rotate: 0,
      },
      group: {
        groups: data.months.map((m) => ({
          title: MONTH_NAMES[m.month - 1],
          cols: 2,
        })),
        style: {
          fontSize: "12px",
          fontWeight: 600,
          colors: ["#333"],
        },
      },
    },
    yaxis: {
      title: { text: "USD" },
      labels: {
        style: { fontSize: "12px" },
        formatter: (val) => `$${val.toFixed(0)}`,
      },
    },
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: "65%",
        borderRadius: 4,
      },
    },
    colors: colors,
    legend: {
      show: false,
    },
    dataLabels: {
      enabled: false,
    },
    tooltip: {
      theme: "light",
      shared: true,
      intersect: false,
      custom: function ({ series, seriesIndex, dataPointIndex, w }) {
        const monthIndex = Math.floor(dataPointIndex / 2);
        const isBudget = dataPointIndex % 2 === 0;
        const monthName = MONTH_NAMES[monthIndex];
        const type = isBudget ? "Budget" : "Usage";

        // data.months에서 total 값 가져오기
        const monthData = data.months[monthIndex];
        const total = isBudget
          ? monthData.budget.total
          : monthData.actual.total;

        let tooltipContent = `
          <div class="apexcharts-tooltip-title" style="font-size: 12px; padding: 6px 10px; border-bottom: 1px solid #e3e3e3;">
            ${monthName} ${type}
          </div>
        `;

        tooltipContent += `
          <div style="display: flex; justify-content: space-between; padding: 6px 10px; background-color: #f8f9fa; font-weight: 700; border-bottom: 1px solid #e3e3e3;">
            <span>Total:</span>
            <span>$${total.toFixed(2)}</span>
          </div>
        `;

        if (showDetailedView) {
          const cspNames = ["AWS", "NCP", "Azure"];
          const cspColors = isBudget
            ? ["#9fb8d9", "#8fc9a8", "#e6b98a"]
            : ["#1a5a9e", "#0d8f63", "#c77d08"];

          cspNames.forEach((csp, idx) => {
            const seriesIdx = idx * 2 + (isBudget ? 0 : 1);
            const value = series[seriesIdx][dataPointIndex];

            if (value && value > 0) {
              const cspDisplayName = CSP_CONFIG[csp.toUpperCase()]?.name || csp;
              tooltipContent += `
                <div class="apexcharts-tooltip-series-group" style="display: flex; align-items: center; padding: 3px 10px;">
                  <span class="apexcharts-tooltip-marker" style="background-color: ${
                    cspColors[idx]
                  }; width: 12px; height: 12px; border-radius: 2px; margin-right: 6px;"></span>
                  <div style="display: flex; justify-content: space-between; width: 100%;">
                    <span style="margin-right: 10px;">${cspDisplayName}:</span>
                    <span style="font-weight: 600;">$${value.toFixed(2)}</span>
                  </div>
                </div>
              `;
            }
          });
        }

        return tooltipContent;
      },
    },
  };

  return (
    <Card title={`${data.year} Budget vs Usage Comparison`} titleSize={2}>
      {/* Checkbox for toggling view */}
      <div className="mb-3 d-flex justify-content-end">
        <label className="form-check">
          <input
            className="form-check-input"
            type="checkbox"
            checked={showDetailedView}
            onChange={(e) => setShowDetailedView(e.target.checked)}
          />
          <span className="form-check-label">Show CSP Breakdown</span>
        </label>
      </div>

      <Chart options={options} series={series} type="bar" height={400} />

      {/* Custom Legend */}
      {legendItems}
    </Card>
  );
}
