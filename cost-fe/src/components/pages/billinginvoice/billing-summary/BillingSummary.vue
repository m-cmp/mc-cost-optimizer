<template>
<div class="col-lg-6">
    <div class="card">
        <div class="card-body">
            <div class="d-flex">
                <h3 class="card-title">Summary</h3>
                <div class="ms-auto">
                    <div class="dropdown">
                        <a class="dropdown-toggle text-muted" href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Last 7 days</a>
                        <div class="dropdown-menu dropdown-menu-end">
                            <a class="dropdown-item active" href="#">Last 7 days</a>
                            <a class="dropdown-item" href="#">Last 30 days</a>
                            <a class="dropdown-item" href="#">Last 3 months</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col">
                    <div id="chart-active-users-2"></div>
                </div>
            </div>
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
    name: 'BillingSummary',
    setup() {
        onMounted(() => {
            var options = {
                chart: {
                    type: "line",
                    fontFamily: 'inherit',
                    height: 288,
                    parentHeightOffset: 0,
                    toolbar: {
                        show: false,
                    },
                    animations: {
                        enabled: false
                    },
                },
                fill: {
                    opacity: 1,
                },
                stroke: {
                    width: 2,
                    lineCap: "round",
                    curve: "smooth",
                },
                series: [{
                    name: "Total",
                    data: [11000, 12000]
                }, {
                    name: "AWS",
                    data: [5000, 6000]
                }, {
                    name: "AZURE",
                    data: [2000, 2300]
                }, {
                    name: "GCP",
                    data: [1000, 1200]
                }, {
                    name: "NCP",
                    data: [500, 300]
                }],
                tooltip: {
                    theme: 'dark'
                },
                grid: {
                    padding: {
                        top: -20,
                        right: 0,
                        left: -4,
                        bottom: -4
                    },
                    strokeDashArray: 4,
                },
                xaxis: {
                    labels: {
                        padding: 0,
                    },
                    tooltip: {
                        enabled: false
                    },
                    type: 'datetime',
                },
                yaxis: {
                    labels: {
                        padding: 4
                    },
                },
                labels: [
                    '2020-05-01', '2020-06-01'
                ],
                colors: ['#00E396', '#FEB019', '#FF4560', '#775DD0', '#546E7A'],
                legend: {
                    show: false,
                },
            };

            var chart = new ApexCharts(document.getElementById('chart-active-users-2'), options);
            chart.render();
        });
    }
};
</script>

<style scoped>
</style>
