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
    onMounted
} from 'vue';
import ps from '@/utils/common.js';

export default {
    name: 'BaseInfo',
    setup() {
        const eachCost = ref([{
                'csp': 'AWS',
                'cost': 6458,
                'colorClass': 'bg-google'
            },
            {
                'csp': 'AZURE',
                'cost': 3985,
                'colorClass': 'bg-red'
            },
            {
                'csp': 'GCP',
                'cost': 3985,
                'colorClass': 'bg-facebook'
            },
            {
                'csp': 'NCP',
                'cost': 2412,
                'colorClass': 'bg-green'
            }
        ]);

        const totalCost = ref(0);

        const CalculateTotalCost = () => {
            totalCost.value = eachCost.value.reduce((sum, item) => sum + item.cost, 0);
            console.log(totalCost.value);
        };

        const toFixedLocaleString = (number) => {
            return ps.str.toFixedLocaleString(number, 3);
        };

        onMounted(() => {
            CalculateTotalCost();
        });

        return {
            eachCost,
            totalCost,
            toFixedLocaleString
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
