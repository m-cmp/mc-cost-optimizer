<template>
<div class="card">
    <div class="card-body flex-container">
        <div class="chart-container-left-donutchart">
            <h3 class="card-title">약정커버리지 현황</h3>
            <div id="chart-demo-pie2" style="min-height: 236.9px;"></div>
        </div>
        <div class="text-container-right-text">
            <p>절감액<br>
                <span style="color: rgb(0, 128, 0);">▼ ₩300,000</span>
            </p>
            <p>약정활용
                <span class="green-box">적절</span>
            </p>
        </div>
    </div>
</div>
</template>

<script>
import {
    onMounted
} from 'vue';
import ApexCharts from 'apexcharts';

export default {
    name: 'DashboardCommitment',
    setup() {
        onMounted(() => {
            const chartOptions = {
                chart: {
                    type: "donut",
                    fontFamily: 'inherit',
                    height: 240,
                    sparkline: {
                        enabled: true
                    },
                    animations: {
                        enabled: false
                    },
                },
                fill: {
                    opacity: 1,
                },
                series: [44, 55, 12, 2],
                labels: ["Spot Instance", "Reserved Instance", "Anomaly Detection", "Others"],
                tooltip: {
                    theme: 'dark',
                    fillSeriesColor: false
                },
                grid: {
                    strokeDashArray: 4,
                },
                colors: ['#008FFB', '#00E396', '#FEB019', '#CED4DA'],
                legend: {
                    show: true,
                    position: 'right',
                    offsetY: 12,
                    markers: {
                        width: 10,
                        height: 10,
                        radius: 100,
                    },
                    itemMargin: {
                        horizontal: 8,
                        vertical: 8
                    },
                    formatter: function (val, opts) {
                        return val + " (" + opts.w.globals.series[opts.seriesIndex] + ")";
                    }
                },
            };
            const chart = new ApexCharts(document.querySelector("#chart-demo-pie2"), chartOptions);
            chart.render();
        });
    }
}
</script>

<style>
</style>
