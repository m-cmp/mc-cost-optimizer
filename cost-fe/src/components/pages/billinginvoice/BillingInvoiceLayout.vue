<template>
<div class="page">
    <DashboardHeader />
    <div class="page-wrapper">
    </div>
    <div class="page-body">
        <div class="container-xl">
            <div class="row row-deck row-cards">
                <BaseInfo />
                <BillingSummary />
                <InvoiceTable />
            </div>
        </div>
    </div>
</div>
</template>

<script>
import DashboardHeader from '../dashboard/dashboard-header/DashboardHeader.vue'
import BaseInfo from './base-info/BaseInfo.vue'
import BillingSummary from './billing-summary/BillingSummary.vue'
import InvoiceTable from './invoice-table/InvoiceTable.vue'

export default {
    name: 'BillingInvoiceLayout',
    components: {
        DashboardHeader,
        BaseInfo,
        BillingSummary,
        InvoiceTable
    },
    data() {
        return {};
    },
    methods: {}
}
</script>

<style>
/* select box에 대한 스타일 */
.selectBoxList {
    display: flex;
    grid-gap: 30px;
}

.selectBox2 * {
    box-sizing: border-box;
}

.selectBox2 {
    position: relative;
    z-index: 1;
    width: 350px;
    height: 35px;
    border-radius: 4px;
    border: 1px solid rgb(34, 99, 183);
    cursor: pointer;
}

.selectBox2:after {
    content: '▼';
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    right: 5px;
    transition: transform 0.6s ease;
}

.selectBox2:hover:after {
    transform: translateY(-50%) rotate(180deg);
}

.selectBox2 .label {
    display: flex;
    align-items: center;
    width: inherit;
    height: inherit;
    border: 0 none;
    outline: 0 none;
    padding-left: 15px;
    background: transparent;
    cursor: pointer;
}

.selectBox2 .optionList {
    position: absolute;
    top: 28px;
    left: 0;
    width: 100%;
    background: rgb(34, 99, 183);
    color: #fff;
    list-style-type: none;
    padding: 0;
    border-radius: 6px;
    overflow: hidden;
    max-height: 0;
    transition: max-height 0.3s ease-in;
}

.selectBox2 .optionList::-webkit-scrollbar {
    width: 6px;
}

.selectBox2 .optionList::-webkit-scrollbar-track {
    background: transparent;
}

.selectBox2 .optionList::-webkit-scrollbar-thumb {
    background: #303030;
    border-radius: 45px;
}

.selectBox2 .optionList::-webkit-scrollbar-thumb:hover {
    background: #303030;
}

.selectBox2.active {
    z-index: 10;
}

.selectBox2.active .optionList {
    max-height: 500px;
}

.selectBox2 .optionItem {
    border-bottom: 1px dashed rgb(66, 114, 177);
    padding: 5px 15px 5px;
    transition: background 0.1s;
}

.selectBox2 .optionItem:hover {
    background: rgb(66, 114, 177);
}

.selectBox2 .optionItem:last-child {
    border-bottom: 0 none;
}

/* 그래프 카드에 대한 스타일 */
.flex-container {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.text-content {
    flex: 1;
    padding-right: 20px;
}

.chart-container {
    flex: 1;
    height: 200px;
}

.chart-container-left-donutchart {
    flex: 8;
    height: 240px;
}

.text-container-right-text {
    flex: 2;
    padding-left: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    /* This will align items to the bottom */
    height: 240px;
    /* Ensure the container takes full height for proper alignment */
}

.text-container-right-text p {
    margin: 0;
    font-size: 16px;
}

.green-box {
    display: inline-block;
    background-color: green;
    color: white;
    padding: 2px 8px;
    border-radius: 4px;
}

/* 청구금액 summary */
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

.amount-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    width: 100%;
}

/* 상태 점 스타일 */
.status-dot {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: currentColor;
    margin-right: 0.5rem;
}

/* 색상 클래스 */
.bg-primary {
    background-color: #467fcf;
}

.bg-google {
    background-color: #db4437;
}

.bg-red {
    background-color: #e74c3c;
}

.bg-facebook {
    background-color: #3b5998;
}

.bg-green {
    background-color: #2ecc71;
}


.card {
    margin-bottom: 1rem;
}

.dropdown-menu {
    min-width: 5rem;
}

.card {
    margin-bottom: 1rem;
}

.dropdown-menu {
    min-width: 5rem;
}

.pagination .page-link {
    display: flex;
    align-items: center;
}
</style>
