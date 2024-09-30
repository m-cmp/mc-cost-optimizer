<template>
    <div class="col-lg-6">
        <div class="card">
            <div class="card-body">
                <div class="d-flex">
                    <h3 class="card-title">Monthly Overview</h3>
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
    watch,
    onMounted
} from 'vue';
import ApexCharts from 'apexcharts';
import axios from 'axios';
import ENDPOINT from '@/api/Endpoints'
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';

export default {
    name: 'MonthlyOverview',
    setup() {
        const store = useSelectedOptionsStore();

        let chart = null;

        // 차트 설정 & 스타일
        const chartOptions = {
            chart: {
                type: 'bar',
                height: 240,
                parentHeightOffset: 0,
                toolbar: {
                    show: false,
                },
                animations: {
                    enabled: false,
                },
                stacked: true,
                zoom: {
                    enabled: false,
                },
            },
            plotOptions: {
                bar: {
                    columnWidth: '50%',
                },
            },
            dataLabels: {
                enabled: false,
            },
            fill: {
                opacity: 1,
            },
            tooltip: {
                theme: 'dark',
                y: {
                    formatter: function (val) {
                        return val.toLocaleString() + " USD"; 
                    },
                }
            },
            grid: {
                padding: {
                    top: -20,
                    right: 0,
                    left: -4,
                    bottom: -4,
                },
                strokeDashArray: 4,
                xaxis: {
                    lines: {
                        show: true,
                    },
                },
            },
            xaxis: {
                labels: {
                    padding: 0,
                },
                tooltip: {
                    enabled: false,
                },
                axisBorder: {
                    show: false,
                },
                type: 'datetime',
            },
            yaxis: {
                labels: {
                    padding: 4,
                    formatter: function (val) {
                        return val.toFixed(0) + " USD"; 
                    }
                },
            },
            labels: [],
            colors: ['#008FFB', '#00E396', '#FEB019'],
            legend: {
                show: false,
            },
        };

        const createChart = (options) => {
            
            if (chart !== null) {
                chart.destroy();
            }

            chart = new ApexCharts(document.getElementById('chart-active-users-2'), options);
            chart.render();
        };

        const getChartData = async () => {
            try {
                const response = await axios.post(ENDPOINT.be + '/api/v2/invoice/getSummary', store.selectedOptions);
                const data = response.data.Data;

                // yearMonths를 기준으로 X축 labels 설정
                const categories = data.yearMonths.map(month => `${month.slice(0, 4)}-${month.slice(4, 6)}`);

                const seriesData = data.summaryBill.map(cspData => {
                    const cspSeriesData = categories.map(category => {
                        const matchingMonth = cspData.monthlyBill.find(monthlyData => {
                            return `${monthlyData.yearMonth.slice(0, 4)}-${monthlyData.yearMonth.slice(4, 6)}` === category;
                        });
                        return matchingMonth ? matchingMonth.bill : 0;
                    });
                    
                    return {
                        name: cspData.csp, 
                        data: cspSeriesData,
                    };
                });

                // chartOptions 업데이트
                chartOptions.xaxis.categories = categories; 
                chartOptions.series = seriesData; 

                chartOptions.plotOptions = {
                    bar: {
                        columnWidth: '70%', 
                        endingShape: 'rounded', 
                    },
                };
                chartOptions.chart.stacked = false;

                createChart(chartOptions);
            } catch (error) {
                console.error('Error occurred while fetching data:', error);
            }
        };

        watch(() => store.selectedOptions, () => {
            getChartData();
        }, {
            deep: true
        });

        onMounted(() => {
            getChartData();
        });

        return {
            selectedOptions: store.selectedOptions
        }
    }
};
</script>

<style></style>
