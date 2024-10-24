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
                    총 금액
                </div>
                <div class="h2">{{ totalCost.toLocaleString() }} USD</div>
                <hr class="divider">
            </div>
            <div class="amount-grid">
                <div class="px-5" v-for="(item, index) in eachCost" :key="index">
                    <div class="text-muted">
                        <span class="status-dot" :class="item.colorClass"></span>
                        {{ item.csp }}
                    </div>
                    <div class="h2">{{ item.cost.toLocaleString() }} USD</div>
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
import axios from 'axios';
import ENDPOINT from '@/api/Endpoints'

export default {
    name: 'BaseInfo',
    setup() {
        const selectedOptionsStore = useSelectedOptionsStore();

        const eachCost = ref([]);
        const totalCost = ref(0);

        const getCostData = async () => {
            try {
                const response = await axios.post(ENDPOINT.be + '/api/costopti/be/invoice/getBillingBaseInfo', selectedOptionsStore.selectedOptions)
                eachCost.value = response.data.Data;
                CalculateTotalCost();
            } catch (error) {
                console.error(error);
              if(selectedOptionsStore.selectedOptions.selectedProjects.length < 1){
                alert('[ERROR] Project 코드에 맞는 값을 불러올 수 없습니다.')
              }
            }
        }

        const CalculateTotalCost = () => {
            totalCost.value = eachCost.value.reduce((sum, item) => sum + item.cost, 0);
        };

        watch(() => selectedOptionsStore.selectedOptions, () => {
            getCostData();
        }, {
            deep: true
        });

        onMounted(() => {
            if (selectedOptionsStore.selectedOptions.selectedProjects.length > 0) {
                getCostData();
            }
        })

        return {
            eachCost,
            totalCost,
            selectedOptions: selectedOptionsStore.selectedOptions
        };
    }
};
</script>

<style>
.total-amount {
    width: 100%;
    text-align: left;
    font-size: 1.5rem;
    margin-bottom: 10px;
}

.total-amount .h2 {
    font-size: 2.5rem;
}

.divider {
    width: 100%;
    border: none;
    border-top: 1.5px solid #666;
    margin: 10px 0;
}
</style>
