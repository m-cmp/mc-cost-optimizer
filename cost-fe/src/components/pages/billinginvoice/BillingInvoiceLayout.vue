<template>
    <div class="page">
        <DashboardHeader />
        <div class="page-wrapper">
            <DashboardSelectbox />
            <div class="page-body">
                <div class="container-xl">
                    <div class="row row-deck row-cards">
                        <BaseInfo />
                        <MonthlyOverview />
                        <InvoiceTable />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import DashboardSelectbox from '../dashboard/dashboard-selectbox/DashboardSelectbox.vue'
import DashboardHeader from '../dashboard/dashboard-header/DashboardHeader.vue'
import BaseInfo from './base-info/BaseInfo.vue'
import MonthlyOverview from './billing-summary/monthlyOverview.vue'
import InvoiceTable from './invoice-table/InvoiceTable.vue'

export default {
    name: 'BillingInvoiceLayout',
    components: {
        DashboardHeader,
        DashboardSelectbox,
        BaseInfo,
        MonthlyOverview,
        InvoiceTable
    },
    data() {
        return {};
    },
    methods: {}
}
</script>

<style>
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
    height: 240px;
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

.total-amount {
    width: 100%;
    text-align: left;
    font-size: 1.25rem;
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

.amount-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    width: 100%;
}

.status-dot {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: currentColor;
    margin-right: 0.5rem;
}

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
