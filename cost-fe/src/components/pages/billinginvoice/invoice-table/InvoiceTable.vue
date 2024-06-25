<template>
<div class="col-12">
    <div class="card">
        <div class="card-header">
            <h3 class="card-title">Invoices</h3>
        </div>
        <div class="card-body border-bottom py-3">
            <div class="d-flex">
                <div class="text-muted">
                    Show
                    <div class="mx-2 d-inline-block">
                        <input type="text" class="form-control form-control-sm" v-model="entriesCount" size="3" aria-label="Invoices count">
                    </div>
                    entries
                </div>
                <div class="ms-auto text-muted">
                    Search:
                    <div class="ms-2 d-inline-block">
                        <input type="text" class="form-control form-control-sm" v-model="searchQuery" aria-label="Search invoice">
                    </div>
                </div>
            </div>
        </div>

        <div ref="tableRef" id="example-table-tabulator"></div>

        <div class="card-footer d-flex align-items-center">
            <p class="m-0 text-muted">Showing <span>1</span> to <span>{{ entriesCount }}</span> of <span>16</span> entries</p>
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
                <li class="page-item"><a class="page-link" href="#">1</a></li>
                <li class="page-item active"><a class="page-link" href="#">2</a></li>
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
import {
    ref,
    onMounted
} from 'vue';
import {
    TabulatorFull as Tabulator
} from 'tabulator-tables';

export default {
    name: 'InvoiceTable',
    setup() {
        const entriesCount = ref(8);
        const searchQuery = ref('');
        const tableRef = ref(null);

        onMounted(() => {
            if (tableRef.value) {
                new Tabulator(tableRef.value, {
                    height: "311px",
                    layout: "fitColumns",
                    movableRows: true,
                    groupBy: "gender",
                    columns: [{
                      title: "CSP",
                      field: "csp",
                      formatter: () => 'AWS'
                    },
                      {
                            title: "계정 ID",
                            field: "accountID",
                            width: 200,
                        },
                        {
                            title: "Product ID",
                            field: "productID",
                            // formatter: "progress",
                            sorter: "number",
                        },
                        {
                            title: "리소스 ID",
                            field: "resourceID"
                        },
                        {
                            title: "비용",
                            field: "bill",
                            // formatter: "star",
                            // hozAlign: "center",
                          formatter: cell => "₩" + cell.getValue()
                        }
                    ],
                    data: [{
                            // id: 1,
                      accountID: "Oli Bob",
                      productID: 42,
                            gender: "male",
                      bill: 5,
                      resourceID: "red",
                            dob: "14/04/1984",
                            car: true
                        },
                        {
                            // id: 2,
                          accountID: "Mary May",
                          productID: 73,
                            gender: "female",
                          bill: 4,
                          resourceID: "blue",
                            dob: "14/05/1982",
                            car: true
                        },
                        {
                            // id: 3,
                          accountID: "Chris Brown",
                          productID: 100,
                            gender: "male",
                          bill: 3,
                          resourceID: "green",
                            dob: "22/08/1992",
                            car: false
                        },
                        {
                            // id: 4,
                          accountID: "Diana Ross",
                          productID: 78,
                            gender: "female",
                          bill: 2,
                          resourceID: "yellow",
                            dob: "12/09/1985",
                            car: true
                        },
                        {
                            // id: 5,
                          accountID: "Paul Smith",
                          productID: 52,
                            gender: "male",
                          bill: 1,
                          resourceID: "purple",
                            dob: "03/04/1979",
                            car: false
                        },
                    ],
                });
            }
        });

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
