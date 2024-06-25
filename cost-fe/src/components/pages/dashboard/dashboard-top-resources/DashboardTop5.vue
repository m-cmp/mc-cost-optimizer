<template>
<div class="card">
    <div class="card-body">
        <h3 class="card-title">요금 상위 5개 서비스</h3>
        <div id="chart-demo-pie" style="min-height: 201.9px;">
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
    name: 'DashboardTop',
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
                labels: ["Ec2", "ALB", "CDB", "Other"],
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
                    position: 'bottom',
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
                },
            };

            const chart = new ApexCharts(document.querySelector("#chart-demo-pie"), chartOptions);
            chart.render();
        });
    }
}
</script>

<style>
</style>
