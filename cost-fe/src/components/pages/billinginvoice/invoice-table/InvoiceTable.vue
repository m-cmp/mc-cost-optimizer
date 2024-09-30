<template>
<div class="col-12">
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Invoices</h3>
        </div>
        <div ref="tableRef" id="example-table-tabulator"></div>

        <div class="card-footer d-flex align-items-center">
            <ul class="pagination m-0 ms-auto">
                <li class="page-item disabled">
                    <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
                        <!-- Download SVG icon from http://tabler-icons.io/i/chevron-left -->
                        <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none" />
                            <path d="M15 6l-6 6l6 6" /></svg>
                        prev
                    </a>
                </li>
                <li class="page-item active"><a class="page-link" href="#">1</a></li>
                <li class="page-item"><a class="page-link" href="#">2</a></li>
                <li class="page-item"><a class="page-link" href="#">3</a></li>
                <li class="page-item"><a class="page-link" href="#">4</a></li>
                <li class="page-item"><a class="page-link" href="#">5</a></li>
                <li class="page-item">
                    <a class="page-link" href="#">
                        next
                        <!-- Download SVG icon from http://tabler-icons.io/i/chevron-right -->
                        <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none" />
                            <path d="M9 6l6 6l-6 6" /></svg>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</div>
<link href="https://unpkg.com/tabulator-tables@5.0.10/dist/css/tabulator.min.css" rel="stylesheet">
</template>

<script>
import axios from 'axios';
import ENDPOINT from '@/api/Endpoints'
import {
    ref,
    watch,
    onMounted
} from 'vue';
import {
    TabulatorFull as Tabulator
} from 'tabulator-tables';
import {
    useSelectedOptionsStore
} from '@/stores/selectedOptions';


export default {
    name: 'InvoiceTable',
    setup() {
        const store = useSelectedOptionsStore();
        const entriesCount = ref(8);
        const searchQuery = ref('');
        const tableRef = ref(null);
        const tableOption = ref({
            height: "311px",
            layout: "fitColumns",
            movableRows: true,
            groupBy: ["csp", "accountID", "productID"],
            groupHeader: (value, count, data) => {
                // 그룹의 bill 합산
                const totalBill = data.reduce((sum, row) => sum + (row.bill || 0), 0);
                return `${value} (${count} items) - Total: ${totalBill.toLocaleString()} USD`;
            },
            columns: [{
                    title: "CSP",
                    field: "csp",
                    headerHozAlign: "center"
                },
                {
                    title: "Account ID",
                    field: "accountID",
                    width: 200,
                    headerHozAlign: "center"
                },
                {
                    title: "Product ID",
                    field: "productID",
                    sorter: "number",
                    headerHozAlign: "center"
                },
                {
                    title: "Resource ID",
                    field: "resourceID",
                    headerHozAlign: "center"
                },
                {
                    title: "Billing (USD)",
                    field: "bill",
                    hozAlign: "right",
                    headerHozAlign: "center",
                    formatter: cell => {
                        const value = cell.getValue();
                        return value !== null && value !== undefined ? value.toLocaleString() + " USD" : "-";
                    }
                }
            ],
            data: [],
        })

        const getTableData = async () => {
            try {
                const response = await axios.post(ENDPOINT.be + '/api/v2/invoice/getInvoice', store.selectedOptions)
                const data = response.data.Data.invoice;
                const additionalData = [{
                        csp: 'NCP',
                        accountID: null,
                        productID: null,
                        resourceID: null,
                        bill: null
                    },
                    {
                        csp: 'GCP',
                        accountID: null,
                        productID: null,
                        resourceID: null,
                        bill: null
                    },
                    {
                        csp: 'AZURE',
                        accountID: null,
                        productID: null,
                        resourceID: null,
                        bill: null
                    }
                ];
                const mergedData = data.concat(additionalData);
                tableOption.value.data = mergedData;

                if (tableRef.value) {
                    new Tabulator(tableRef.value, tableOption.value)
                }

            } catch (error) {
                console.error(error);
            }
        }

        watch(() => store.selectedOptions, () => {
            getTableData();
        }, {
            deep: true
        });

        onMounted(() => {
            getTableData();
        })
        
        return {
            entriesCount,
            searchQuery,
            tableRef
        };
    }
};
</script>

<style scoped>
</style>
