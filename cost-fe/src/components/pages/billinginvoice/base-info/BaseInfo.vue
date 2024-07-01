<template>
<div class="col-lg-6">
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Base info</h3>
        </div>
        <div class="card-body">
            <div class="px-5 total-amount">
                <div class="text-muted">
                    <span class="status-dot bg-primary"></span>
                    총금액
                </div>
                <div class="h2">{{ toFixedLocaleString(totalCost) }}</div>
                <hr class="divider">
            </div>
            <div class="amount-grid">
                <div class="px-5" v-for="(item, index) in eachCost" :key="index">
                    <div class="text-muted">
                        <span class="status-dot" :class="item.colorClass"></span>
                        {{ item.csp }}
                    </div>
                    <div class="h2">{{ toFixedLocaleString(item.cost) }}</div>
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
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';
import {
    useCalCurrencyStore
} from '@/stores/calCurrency';
import axios from 'axios';
// import ps from '@/utils/common.js';

export default {
    name: 'BaseInfo',
    setup() {
        const selectedOptionsStore = useSelectedOptionsStore();
        const calCurrencyStore = useCalCurrencyStore();

        const eachCost = ref([]);

        const totalCost = ref(0);

        const getCostData = async () => {
            try {
                const response = await axios.post('http://localhost:9090/api/v2/invoice/getBillingBaseInfo', selectedOptionsStore.selectedOptions)
                eachCost.value = response.data.Data;
                CalculateTotalCost(); // 데이터를 가져온 후에 총 금액 계산
            } catch (error) {
                console.error(error);
            }
        }


        const CalculateTotalCost = () => {
            totalCost.value = eachCost.value.reduce((sum, item) => sum + item.cost, 0);
        };

        const toFixedLocaleString = (number) => {
            return Math.round(calCurrencyStore.usdToKrw(number)).toLocaleString() + ' KRW';
        };

        watch(() => selectedOptionsStore.selectedOptions, () => {
            getCostData();
        }, {
            deep: true
        });

        onMounted(() => {
            getCostData();
        })

        return {
            eachCost,
            totalCost,
            toFixedLocaleString,
            selectedOptions: selectedOptionsStore.selectedOptions
        };
    }
};
</script>

<style>
.total-amount {
    width: 100%;
    text-align: left;
    font-size: 1.25rem;
    /* 2포인트 증가 */
    margin-bottom: 10px;
}

.total-amount .h2 {
    font-size: 2.5rem;
    /* 총금액 폰트 사이즈 키우기 */
}

.divider {
    width: 100%;
    border: none;
    border-top: 1.5px solid #666;
    /* 회색 줄 */
    margin: 10px 0;
    /* 줄 위아래 간격 */
}
</style>
