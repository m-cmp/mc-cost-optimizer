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
                                {{ selectedPeriodOptions }}
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
    watch,
    onMounted
} from 'vue';
import ApexCharts from 'apexcharts';
import axios from 'axios';
import ENDPOINT from '@/api/Endpoints'
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';
import {
    useCalCurrencyStore
} from '@/stores/calCurrency';
import ps from '@/utils/common.js'

export default {
    name: 'BillingSummary',
    setup() {
        const calCurrencyStore = useCalCurrencyStore();
        const store = useSelectedOptionsStore();

        const selectedPeriodOptions = ref('Last 7 days');

        let chart = null;

        const selectOption = (option) => {
            selectedPeriodOptions.value = option;
        }

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
            series: "",
            labels: "",
            tooltip: {
                theme: 'dark',
                y: {
                    formatter: function (value) {
                        return Math.round(calCurrencyStore.usdToKrw(value)).toLocaleString() + ' KRW';
                    }
                }
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
                    padding: 4,
                    formatter: function (value) {
                        return Math.round(calCurrencyStore.usdToKrw(value)).toLocaleString();
                    }
                }
            },
            legend: {
                show: false,
            },
        };

        const createChart = (options) => {
            // 기존 차트가 있으면 파괴
            if (chart !== null) {
                chart.destroy();
            }

            // 새로운 차트 생성
            chart = new ApexCharts(document.getElementById('chart-active-users-2'), options);
            chart.render();
        };

        const getChartData = async () => {
            try {
                var selectedPeriod = ps.str.removeDelimiter(ps.str.removeDelimiter(selectedPeriodOptions.value, ' '), 'Last');
                store.selectedOptions.selectedPeriod = selectedPeriod;
                const response = await axios.post(ENDPOINT.be + '/api/v2/invoice/getSummary', store.selectedOptions)
                const data = response.data.Data;

                chartOptions.labels = data.dates;
                chartOptions.series = data.summaryBill.map(item => ({
                    name: item.csp,
                    data: item.bill
                }))
                createChart(chartOptions);

            } catch (error) {
                console.error(error);
            }
        };

        watch(() => store.selectedOptions, () => {
            getChartData();
        }, {
            deep: true
        });

        watch(() => selectedPeriodOptions, () => {
            getChartData();
        }, {
            deep: true
        });

        onMounted(() => {
            getChartData();
        })

        return {
            selectedPeriodOptions,
            selectOption,
            selectedOptions: store.selectedOptions
        }
    }
};
</script>

<style scoped>
</style>
