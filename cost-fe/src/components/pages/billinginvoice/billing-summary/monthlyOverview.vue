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
import {ref} from 'vue';
import ENDPOINT from '@/api/Endpoints'
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';

export default {
    name: 'MonthlyOverview',
    setup() {
        const store = useSelectedOptionsStore();

        let chart = null;
        const init = ref(true);

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
                categories:[]
            },
            yaxis: {
                labels: {
                    padding: 4,
                    formatter: function (val) {
                        return val.toFixed(0) + " USD"; 
                    }
                },
            },
            series: [],
            labels: [],
            colors: ['#008FFB', '#00E396', '#FEB019'],
            legend: {
                show: false,
            },
        };

        const createChart = (options) => {
            const el = document.getElementById('chart-active-users-2');
            if (!el) return;
            if (!chart) {
                chart = new ApexCharts(el, options);
                chart.render();
            } else {
                chart.updateOptions(options);
            }
        };       

        const getChartData = async () => {
            try {
                const response = await axios.post(ENDPOINT.be + '/api/costopti/be/invoice/getSummary', store.selectedOptions);
                const data = response.data.Data;
                // 로컬빌드 관련 코드
                if (!data || !Array.isArray(data.yearMonths) || !Array.isArray(data.summaryBill)) {
                    if (process.env.NODE_ENV === 'development') {
                        chartOptions.xaxis.categories = [];
                        chartOptions.series = [];
                        createChart(chartOptions);
                    } else {
                        alert('데이터를 불러올 수 없습니다.');
                    }
                    return;
                }

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

                init.value = false;
                createChart(chartOptions);
            } catch (error) {
                if (err.code === 'ECONNREFUSED' || err.message.includes('Network Error') || !err.response) {
                    alert('서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.');
                    } else if (err.response) {
                    // 서버에서 에러 상태 코드를 반환한 경우 (4xx, 5xx)
                    const status = err.response.status;
                    if (status >= 500) {
                        alert('서버에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.');
                    } else if (status >= 400) {
                        alert('요청에 문제가 있습니다. 잠시 후 다시 시도해 주세요.');
                    }
                    } else {
                    alert('알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
                };
            }
        };

        watch(() => store.selectedOptions, () => {
            getChartData();
        }, {
            deep: true
        });

        onMounted(() => {
            createChart(chartOptions);

            if (store.selectedOptions.selectedProjects.length > 0) {
                getChartData();
            }
        });

        return {
            selectedOptions: store.selectedOptions
        }
    }
};
</script>

<style></style>
