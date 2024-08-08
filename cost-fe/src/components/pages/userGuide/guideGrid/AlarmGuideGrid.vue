<template>
  <div class="col-12">
    <div class="card">
      <div class="card-header">
        <h3 class="card-title">비용상태 및 알람</h3>
      </div>
      <div class="card-body border-bottom py-3">
        <div class="d-flex">
          <div class="text-muted">
            Show
            <div class="mx-2 d-inline-block">
              <input type="text" class="form-control form-control-sm" value="8" size="1" aria-label="Invoices count">
            </div>
            entries
          </div>
          <div class="ms-auto text-muted">
            Search:
            <div class="ms-2 d-inline-block">
              <input type="text" class="form-control form-control-sm" aria-label="Search invoice">
            </div>
          </div>
        </div>
      </div>
      <div class="table-responsive">
        <table class="table card-table table-vcenter text-nowrap datatable">
          <thead>
          <tr>
            <th class="w-1"><input class="form-check-input m-0 align-middle" type="checkbox" aria-label="Select all invoices"></th>
            <th class="w-1">No. <!-- Download SVG icon from http://tabler-icons.io/i/chevron-up -->
              <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-sm icon-thick" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M6 15l6 -6l6 6" /></svg>
            </th>
            <th>이벤트 유형</th>
            <th>계정명</th>
            <th>인스턴스 ID</th>
            <th>인스턴스 유형</th>
            <th>알림생성일자</th>
            <th>긴급도</th>
            <th>비고</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td><input class="form-check-input m-0 align-middle" type="checkbox" aria-label="Select invoice"></td>
            <td><span class="text-muted">001401</span></td>
            <td><a href="invoice.html" class="text-reset" tabindex="-1">Design Works</a></td>
            <td>
              <span class="flag flag-country-us"></span>
              Carlson Limited
            </td>
            <td>
              87956621
            </td>
            <td>
              15 Dec 2017
            </td>
            <td>
              <span class="badge bg-success me-1"></span> Paid
            </td>
            <td>$887</td>
            <td>비고 설명</td>
            <td class="text-end">
                            <span class="dropdown">
                              <button class="btn dropdown-toggle align-text-top" data-bs-boundary="viewport" data-bs-toggle="dropdown">Actions</button>
                              <div class="dropdown-menu dropdown-menu-end">
                                <a class="dropdown-item" href="#">
                                  Action
                                </a>
                                <a class="dropdown-item" href="#">
                                  Another action
                                </a>
                              </div>
                            </span>
            </td>
          </tr>
          <tr>
            <td><input class="form-check-input m-0 align-middle" type="checkbox" aria-label="Select invoice"></td>
            <td><span class="text-muted">001402</span></td>
            <td><a href="invoice.html" class="text-reset" tabindex="-1">UX Wireframes</a></td>
            <td>
              <span class="flag flag-country-us"></span>
              Adobe
            </td>
            <td>
              87956421
            </td>
            <td>
              12 Apr 2017
            </td>
            <td>
              <span class="badge bg-warning me-1"></span> Pending
            </td>
            <td>$1200</td>
            <td>비고 설명</td>
            <td class="text-end">
                            <span class="dropdown">
                              <button class="btn dropdown-toggle align-text-top" data-bs-boundary="viewport" data-bs-toggle="dropdown">Actions</button>
                              <div class="dropdown-menu dropdown-menu-end">
                                <a class="dropdown-item" href="#">
                                  Action
                                </a>
                                <a class="dropdown-item" href="#">
                                  Another action
                                </a>
                              </div>
                            </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="card-footer d-flex align-items-center">
        <p class="m-0 text-muted">Showing <span>1</span> to <span>8</span> of <span>16</span> entries</p>
        <ul class="pagination m-0 ms-auto">
          <li class="page-item disabled">
            <a class="page-link" href="#" tabindex="-1" aria-disabled="true">
              <!-- Download SVG icon from http://tabler-icons.io/i/chevron-left -->
              <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M15 6l-6 6l6 6" /></svg>
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
              next <!-- Download SVG icon from http://tabler-icons.io/i/chevron-right -->
              <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M9 6l6 6l-6 6" /></svg>
            </a>
          </li>
        </ul>
      </div>
    </div>
    <div class="test">
      <div class="card">
        <DataTable v-model:filters="filters" :value="products" paginator :rows="5" :rowsPerPageOptions="[5, 10, 20, 50]" tableStyle="min-width: 50rem">
          <template #header>
            <div class="c-card-header">
              <div class="flex flex-wrap align-items-center justify-content-between gap-2">
                <span class="text-xl text-900 font-bold">비용상태 및 알람</span>
              </div>
              <div class="flex justify-end">
                <IconField iconPosition="left">
                  <InputIcon>
                    <i class="pi pi-search" />
                  </InputIcon>
                  <InputText v-model="filters['global'].value" placeholder="Keyword Search" />
                </IconField>
              </div>
            </div>
          </template>
          <Column field="이벤트 유형" header="이벤트 유형"></Column>
          <Column field="name" header="계정명">
            <template #body="slotProps">
              {{ formatCurrency(slotProps.data.price) }}
            </template>
          </Column>
          <Column field="code" header="인스턴스 ID" sortable ></Column>
          <Column field="인스턴스 유형" header="인스턴스 유형">
            <template #body="slotProps">
              <Rating :modelValue="slotProps.data.rating" readonly :cancel="false" />
            </template>
          </Column>
          <Column field="price" header="알림생성일자" sortable >
          </Column>
          <Column field="inventoryStatus" header="긴급도" sortable >
            <template #body="slotProps">
              <Tag :value="slotProps.data.inventoryStatus" :severity="getSeverity(slotProps.data)" />
            </template>
          </Column>
          <Column header="비고">
            <template #body="slotProps">
              <Tag :value="slotProps.data.inventoryStatus" :severity="getSeverity(slotProps.data)" />
            </template>
          </Column>
          <Column>
            <template #body="slotProps">
              <Tag :value="slotProps.data.inventoryStatus" :severity="getSeverity(slotProps.data)" />
            </template>
          </Column>
          <template #footer> In total there are {{ products ? products.length : 0 }} products. </template>
        </DataTable>
      </div>
    </div>
  </div>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Tag from 'primevue/tag';
import Rating from 'primevue/rating';
import { FilterMatchMode } from 'primevue/api';
import IconField from 'primevue/iconfield';
import InputIcon from 'primevue/inputicon';
import InputText from 'primevue/inputtext';
// import flexStyle from 'primeflex/primeflex.css';

export default {
  components:{
    DataTable,
    Column,
    Tag,
    Rating,
    IconField,
    InputIcon,
    InputText
  },
  props: {
  },
  data() {
    return {
      filters: {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      },
      products: [{
        id: '1000',
        code: 'f230fh0g3',
        name: 'Bamboo Watch',
        description: 'Product Description',
        image: 'bamboo-watch.jpg',
        price: 65,
        category: 'Accessories',
        quantity: 24,
        inventoryStatus: 'INSTOCK',
        rating: 5
      },{
        id: '2000',
        code: 'f230fh0g3',
        name: 'Bamboo Watch',
        description: 'Product Description',
        image: 'bamboo-watch.jpg',
        price: 50,
        category: 'Accessories',
        quantity: 24,
        inventoryStatus: 'TEST',
        rating: 5
      }]
    };
  },
  mounted() {

  },
  methods: {
    formatCurrency(value) {
      return value.toLocaleString('en-US', { style: 'currency', currency: 'USD' });
    },
    getSeverity(product) {
      switch (product.inventoryStatus) {
        case 'INSTOCK':
          return 'success';

        case 'LOWSTOCK':
          return 'warning';

        case 'OUTOFSTOCK':
          return 'danger';

        default:
          return null;
      }
    }
  }
}
</script>

<style lang="scss">
/*.p-datatable .p-datatable-tbody > tr > td {*/
/*  background-color: black;*/
/*}*/
.p-datatable-table {
  .p-datatable-tbody > tr, td, th {
    border-color: #e2e8f0 !important;
    border-style: revert-layer !important;
    border-width: revert-layer !important;
  }
}

.c-card-header{
  display: flex;
  justify-content: space-between;
  width: -webkit-fill-available;
  align-items: center;
  padding: 0 24px;
}

</style>
