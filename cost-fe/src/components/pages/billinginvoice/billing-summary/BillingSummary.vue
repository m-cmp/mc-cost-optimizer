<template>
<div class="col-lg-6">
    <div class="card">
        <div class="card-body">
            <div class="d-flex">
                <h3 class="card-title">Summary</h3>
                <div class="ms-auto">
                    <div>
                        <div class="dropdown">
                          <a class="dropdown-toggle text-muted" href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            {{ selectedOption }}
                          </a>
                          <div class="dropdown-menu dropdown-menu-end">
                            <a class="dropdown-item" href="#" @click.prevent="selectOption('Last 7 days')">Last 7 days</a>
                            <a class="dropdown-item" href="#" @click.prevent="selectOption('Last 30 days')">Last 30 days</a>
                            <a class="dropdown-item" href="#" @click.prevent="selectOption('Last 3 months')">Last 3 months</a>
                          </div>
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
    ref,
    onMounted
} from 'vue';
import ApexCharts from 'apexcharts';

export default {
    name: 'BillingSummary',
    setup() {
        const selectedOption = ref('Last 7 days');

        const selectOption = (option) => {
            selectedOption.value = option;
        }

        const seriesData = ref([{
            name: "Total",
            data: [8500, 9800, 10000, 11650, 10150, 9120, 8781]
        }, {
            name: "AWS",
            data: [5000, 6000, 6200, 7300, 5800, 4220, 4500]
        }, {
            name: "AZURE",
            data: [2000, 2300, 2400, 2750, 2800, 3000, 2200]
        }, {
            name: "GCP",
            data: [1000, 1200, 1000, 1100, 1250, 1500, 1550]
        }, {
            name: "NCP",
            data: [500, 300, 400, 500, 300, 400, 531]
        }]);

        const labelsData = ref([
            '2024-06-01', '2024-06-02', '2024-06-03', '2024-06-04', '2024-06-05', '2024-06-06', '2024-06-07'
        ])

        onMounted(() => {
            var chartOptions = {
                chart: {
                    type: "line",
                    fontFamily: 'inherit',
                    height: 288,
                    parentHeightOffset: 0,
                    toolbar: {
                        show: true,
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
                series: seriesData.value,
                labels: labelsData.value,
                tooltip: {
                    theme: 'dark',
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
                legend: {
                    show: false,
                },
            };

            var chart = new ApexCharts(document.getElementById('chart-active-users-2'), chartOptions);
            chart.render();
        });

        return {
            selectedOption,
            selectOption
        }
    }
};
</script>

<style scoped>
</style>
