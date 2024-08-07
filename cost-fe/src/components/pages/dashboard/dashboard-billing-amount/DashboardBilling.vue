<template>
    <div class="card">
        <div class="card-body flex-container">
            <div class="text-content">
                <h3 class="card-title">{{ curYear }}년 {{ curMonth }}월 청구금액</h3>
                <h1 class="card-text">{{ convertedCurMonthBill.toLocaleString() }} KRW</h1>
                <p class="card-text">
                    전월사용 금액대비<br>
                    <span :style="{ color: parseFloat(momPer) > 0 ? 'rgb(128, 0, 0)' : 'rgb(0, 128, 255)' }">{{ momPerSign
                        }} {{ formattedMomPer }}%</span>
                    <span :style="{ color: momBill > 0 ? 'rgb(128, 0, 0)' : 'rgb(0, 128, 255)' }">{{ momBillSign }} {{
                        formattedMomBill.toLocaleString() }} KRW</span>
                </p>
            </div>

            <div id="chart-mentions" class="chart-lg chart-container" style="min-height: 240px;">
                <apexchart type="bar" height="240" :options="chartOptions" :series="series"></apexchart>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import ENDPOINT from '@/api/Endpoints'
import {
    ref,
    watch,
    computed,
    onMounted
} from 'vue';
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';
import {
    useCalCurrencyStore
} from '@/stores/calCurrency';
import ApexCharts from 'apexcharts';

export default {
    name: 'DashboardBillingamount',
    setup() {

        const store = useSelectedOptionsStore();
        const currencyStore = useCalCurrencyStore();

        const curYear = ref('');
        const curMonth = ref('');
        const curMonthBill = ref(0);
        const momPer = ref('');
        const momBill = ref(0);
        const series = ref([]);
        const chart = ref(null);

        // 환율 변환된 금액
        const convertedCurMonthBill = computed(() => Math.round(currencyStore.usdToKrw(curMonthBill.value)));
        const convertedMomBill = computed(() => Math.round(currencyStore.usdToKrw(momBill.value)));

        // 기호 계산
        const momPerSign = computed(() => (parseFloat(momPer.value) > 0 ? '▲' : '▼'));
        const momBillSign = computed(() => (momBill.value > 0 ? '▲' : '▼'));

        // momPer와 momBill 값을 양수로 변환
        const formattedMomPer = computed(() => Math.abs(parseFloat(momPer.value)).toFixed(2));
        const formattedMomBill = computed(() => Math.abs(convertedMomBill.value));

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
                    enabled: false, // 드래그로 확대 비활성화
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
                        return val.toLocaleString() + " KRW"; // 툴팁에 'USD' 추가
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
                    fommater: function (val) {
                        return val.toFixed(2); // 소수점 2자리로 제한
                    }
                },

            },
            labels: [],
            colors: ['#008FFB', '#00E396', '#FEB019'],
            legend: {
                show: false,
            },
        };

        const fetchBillingData = () => {
            // console.log("today: ", store.selectedOptions)
            axios.post(ENDPOINT.be + '/api/v2/getCurMonthBill', store.selectedOptions)
                .then(response => {
                    // console.log('response: ', response.data)
                    const data = response.data.Data;
                    curYear.value = data.curYear;
                    curMonth.value = data.curMonth;
                    curMonthBill.value = data.curMonthBill;
                    momPer.value = data.momPer;
                    momBill.value = data.momBill;

                    // 월, 금액 분리
                    const months = data.monthlyBill.map(item => `${item.year}-${item.month}-01`).reverse();
                    const bills = data.monthlyBill.map(item => Math.round(currencyStore.usdToKrw(item.bill.toFixed(2)))).reverse();

                    // 시리즈 데이터 업데이트
                    series.value = [{
                        name: '월별 청구 금액',
                        data: bills,
                    }];
                    // x축 레이블 업데이트
                    chartOptions.xaxis.categories = months;

                    // console.log('series.value', series.value);
                    // console.log('chartOptions', chartOptions);

                    // chartOptions.labels = data.monthlyBill.map(item => `${item.year}-${item.month}-01`);
                    if (chart.value) {
                        chart.value.updateOptions({
                            series: series.value,
                            xaxis: {
                                categories: chartOptions.xaxis.categories,
                            },
                        });
                    } else {
                        chart.value = new ApexCharts(document.querySelector("#chart-mentions"), {
                            ...chartOptions,
                            series: series.value,
                        });
                        chart.value.render();
                    }
                })
                .catch(error => {
                    console.log('message: ', store.selectedOptions)
                    console.error("There was an error!", error);
                });
        };

        watch(() => store.selectedOptions, () => {
            fetchBillingData();
        }, {
            deep: true,
        });

        onMounted(() => {
            if (store.selectedOptions.selectedWorkspace) {
                fetchBillingData();
            }
        });

        return {
            curYear,
            curMonth,
            curMonthBill,
            momPer,
            momBill,
            chartOptions,
            series,
            momPerSign,
            momBillSign,
            formattedMomPer,
            formattedMomBill,
            convertedCurMonthBill,
            convertedMomBill,
        };
    },
};
</script>

<style scoped></style>
