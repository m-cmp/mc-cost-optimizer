<template>
  <div>
    <b-col
      id="screen-component-search-cloud-bill-detail"
      class="bg-white pb-1 px-0"
    >
      <b-input-group>
        <b-input-group-prepend>
          <b-button
            variant="transparent"
            class="icon-search custom-color text-gray-1"
            @click="primeOnSearchCloudBillList"
          >
            <base-material
              :class="{ 'change-color': cloudBillSearchText }"
              name="search"
              @click.native="resetSearch"/>
          </b-button>
        </b-input-group-prepend>
        <b-form-input
          v-model="cloudBillSearchText"
          :placeholder="`${$t('billing.cloudBillDetails.searchAnything')}`"
          class="search-input"
          @keyup.enter="primeOnSearchCloudBillList"
          @input="primeOnSearchCloudBillList"/>
        <base-material
          v-if="cloudBillSearchText"
          class="material-icons close"
          name="close"
          @click.native="resetSearch"
        />
      </b-input-group>
    </b-col>
    <div>
      <b-col
        v-show="isCloudBillDetailLoading || isLoading"
        class="billing-detail-table-height"
      >
        <BaseLoadingIndicator :loading-height="188"/>
      </b-col>
      <div v-show="!isCloudBillDetailLoading && !isLoading">
        <b-col
          v-show="!hasCloudBillDetailData"
          class="billing-detail-table-height"
        >
          <BaseNotificationNoData :content ="$t('billing.cloudBillDetails.noData')"/>
        </b-col>
        <b-col
          v-show="hasCloudBillDetailData"
          class="cloud-bill-detail-table-wrapper">
          <!-- BackUp
          <ag-grid-vue
            id="myGrid"
            :grid-options="gridOptions"
            :column-defs="columnDefs"
            :row-data="chartData"
            :default-col-def="defaultColDef"
            :row-height="rowHeight"
            :header-height="headerHeight"
            :animate-rows="true"
            :group-default-expanded="groupDefaultExpanded"
            :get-data-path="getDataPath"
            :auto-group-column-def="autoGroupColumnDef"
            :group-include-footer="groupIncludeFooter"
            :group-include-total-footer="groupIncludeTotalFooter"
            :class="`tree-map-${numberOfLayers}-layer ${warningBanner.opened ? 'warning-banner-open' : 'warning-banner-close'}`"
            class="ag-theme-bootstrap cloud-bill-detail-table group"
            always-show-vertical-scroll="true"
            @grid-ready="onGridReady"/>
            -->
          <PrimeCloudBillDetailTreeTable
            :row-data="primeOnSearchCloudBillList"
            :column="columnDefs"
            :header-description="this.$t('billing.cloudBillDetails.header.description')"
            :selected-vendor="selectedVendor"/>
        </b-col>
      </div>
    </div>
  </div>
</template>

<script>
import PrimeCloudBillDetailTreeTable from "./PrimeCloudBillDetailTreeTable";
import _isEmpty from 'lodash/isEmpty';
import _cloneDeep from 'lodash/cloneDeep';
import { mapGetters } from 'vuex';
import {CURRENCY, DEFAULT_CURRENCY, KRW_CURRENCY_FIELDS} from '@/constants/constants';
import {formatCostValue, prepareBillSummaryData, customFormatCostValue} from '@/util/billingUtils';
import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
import {formatCost, customFormatterForDetailCosts, customFormatterForDetailUsages } from '@/util/costUtils';
import { USAGE_TYPE, VIEW_BY_OPTION_VALUE, STANDARD_COLUMN_DEF  } from "@/constants/billingConstants";

const COLUMNS_WIDTH_CONFIG = {
  DESCRIPTION: 60,
  USAGE_TYPE: 25,
  COST: 15
};

export default {
  name: 'PrimeCloudBillDetail',
  components: {
    PrimeCloudBillDetailTreeTable,
    BaseNotificationNoData,
    BaseLoadingIndicator,
  },
  props: {
    rowData: {
      type: Array,
      default() {
        return []
      },
      required: true
    },
    showFlag:{
      type: Boolean,
      required: true
    },
    isViewedByTag: {
      type: Boolean,
      required: true
    },
    numberOfLayers: {
      type: Number,
      default: 5
    },
    tableWidth: {
      type: Number,
      default: 1475
    },
    isLoading: {
      type: Boolean,
      default: false
    },
    warningBanner: {
      type: Object,
      default() {
        return {opened: false, message: ''}
      }
    },
    displayCurrency:{
      type:String,
      default:''
    }
  },
  data() {
    return {
      gridOptions: null,
      gridApi: null,
      columnApi: null,
      columnDefs: null,
      rowHeight: null,
      headerHeight: null,
      groupDefaultExpanded: null,
      getDataPath: null,
      autoGroupColumnDef: null,
      getRowNodeId: null,
      cloudBillSearchText: '',
      groupIncludeFooter: null,
      groupIncludeTotalFooter: null,
      treeMapClass: "ag-theme-bootstrap tree-map-5-layer",
      chartData: [],
      headerCurrency: '',
    }
  },
  computed: {
    ...mapGetters({
      activeMonthIdx: 'billing/activeMonthIdx',
      isCloudBillDetailLoading: 'billing/isCloudBillDetailLoading',
      selectedViewByOption: 'billing/selectedViewByOption',
      gridLayerOption: 'billing/gridLayerOption',
      invoiceCurrency: 'billing/invoiceCurrency',
      selectedVendor: 'billing/selectedVendor'
    }),
    hasCloudBillDetailData() {
      return this.chartData && !_isEmpty(this.chartData)
    },
    primeOnSearchCloudBillList() {
      if (!this.cloudBillSearchText) {
        return this.chartData;
      }
      let filteredData = this.chartData.filter(obj => {
        return Object.keys(obj).some(key => {
          let value = obj[key];
          if (typeof value === 'string') {
            return value.toLowerCase().replace(/\s+/g, '').includes(this.cloudBillSearchText.toLowerCase().replace(/\s+/g, ''));
          } else if (typeof value === 'number') {
            return value.toString().includes(this.cloudBillSearchText)
          }
          return false;
        });
      });
      return filteredData;
    },
  },
  watch: {
    // isViewedByTag: function () { // 태그 컬럼 정렬
    //   if (this.isViewedByTag) {
    //     this.sortCloudBillDetails('');
    //   } else {
    //     this.sortCloudBillDetails('asc');
    //   }
    // },
    rowData:{
      handler() {
        if(this.showFlag === true){
          this.chartData = _cloneDeep(this.rowData);
        }
      },
      immediate: false
    },
    showFlag:{
      handler() {
        if(this.showFlag === true){
          this.chartData = _cloneDeep(this.rowData);
        }
      },
      immediate: false
    },
    '$i18n.locale': {
      handler() {
        this.getHeaderSumCurrency(this.displayCurrency) // 다국어처리
        this.columnDefs = this.getColumnDefs(this.selectedViewByOption) // 다국어처리
        this.gridOptions.api.setColumnDefs(this.columnDefs);
        this.gridOptions.api.getColumnDef("ag-Grid-AutoColumn").headerName = this.$t('billing.cloudBillDetails.header.description')
        this.gridOptions.api.refreshHeader();
      },
    },
    selectedViewByOption: function () {
      this.columnDefs = this.getColumnDefs(this.selectedViewByOption)
    },
    // gridLayerOption: function () {
    //   if (this.gridLayerOption) {
    //     this.expandAll();
    //   }else{
    //     this.collapseAll();
    //   }
    // },
    displayCurrency: {
      handler() {
        if(this.headerCurrency !== ''){
          this.headerCurrency = ''
        }

        this.getHeaderSumCurrency(this.displayCurrency)
        this.columnDefs = this.getColumnDefs(this.selectedViewByOption)
      },
      immediate: true
    }
  },
  beforeMount() { // 브라우저 최초 로딩 액션
    this.gridOptions = {
      localeText: {
        noRowsToShow: this.$t('billing.cloudBillDetails.noResultsFound')
      },
      processCellForClipboard: params => {
        return params.value;
      },
      suppressAggFuncInHeader : true // 헤더의 aggregation 연산 표시 제거
    };

    this.headerCurrency = this.getHeaderSumCurrency(this.$store.state.billing.invoiceCurrency)
    this.columnDefs = this.getColumnDefs(this.selectedViewByOption)
    this.groupDefaultExpanded = 1; // 그리드의 최초 로딩시 펼쳐지는 레이어 단계를 결정
    this.getDataPath = data => {
      return data.description;
    };
    this.autoGroupColumnDef = { // 자동 집계 처리 대상
      headerName: this.$t('billing.cloudBillDetails.header.description'),
      field: 'itemDescription',
      width: this.getColumnsWidth(COLUMNS_WIDTH_CONFIG.DESCRIPTION),
      cellRenderer: 'agGroupCellRenderer',
      cellRendererParams: {suppressCount: false}, // 그룹화 된 로우들의 갯수 보여주기 설정
      //sort: "asc"  // 컬럼 정렬
    };
    this.getRowNodeId = data => {
      return data.id
    }
    this.rowHeight = 40;
    this.headerHeight = 30;
    this.defaultColDef = {
      sortable: true, suppressMenu: true, resizable: true, unSortIcon: true,
    };
    this.groupIncludeFooter = true;
    this.groupIncludeTotalFooter = false;
  },
  mounted() {
    this.gridApi = this.gridOptions.api;
    this.gridColumnApi = this.gridOptions.columnApi;
    this.$nextTick(() => {
      this.handleWindowResize();
    });
    //window.addEventListener('resize', this.handleWindowResize);
  },
  methods: {
    getHeaderSumCurrency(currency) {
      switch(currency) {
        case CURRENCY.KRW:
          return this.headerCurrency = this.$t('billing.cloudBillDetails.header.totalKRW')
        case CURRENCY.USD:
          return this.headerCurrency = this.$t('billing.cloudBillDetails.header.totalUSD')
        case CURRENCY.CNY:
          return this.headerCurrency = this.$t('billing.cloudBillDetails.header.totalCNY')
        case CURRENCY.MXN:
          return this.headerCurrency = this.$t('billing.cloudBillDetails.header.totalMXN')
        default:
          return this.headerCurrency = this.$t('billing.cloudBillDetails.header.totalUSD')
      }
    },
    handleWindowResize() { // 브라우저 리사이징 대응 이벤트
      if(this.gridOptions != undefined && this.gridOptions.api != undefined) {
        this.gridOptions.api.sizeColumnsToFit();
      }
    },
    getColumnDefs(viewByOptions){
      let commonColDefs =
        [
          {
            headerName: 'product',
            field: 'productName',
            rowGroupIndex: 1,
            hide: true
          },
          {
            headerName: 'region',
            field: 'regionName',
            rowGroupIndex: 2,
            hide: true
          },
          {
            headerName: 'usage',
            field: 'usageType',
            rowGroupIndex: 3,
            hide: true
          },
          {
            headerName: this.$t('billing.cloudBillDetails.header.usage'),
            field: 'usage',
            cellStyle: { textAlign: 'right' },
            width: this.getColumnsWidth(COLUMNS_WIDTH_CONFIG.USAGE_TYPE),
            aggFunc: 'sum',
            cellClass: 'cell-number',
            cellRenderer: 'agAnimateShowChangeCellRenderer', // 상위 > 하위 계층 이동할 때 상단 계층에 금액, 사용량 표시 없애기
            valueFormatter: (params) => usageRender(params)
          },
          {
            //this.$t('billing.cloudBillDetails.header.totalUSD'), 총계(USD) , Total (USD)
            headerName: this.headerCurrency,
            field: 'cost',
            cellStyle: { textAlign: 'right' },
            width: this.getColumnsWidth(COLUMNS_WIDTH_CONFIG.COST),
            aggFunc: 'sum',
            cellClass: 'cell-number',
            cellRenderer: 'agAnimateShowChangeCellRenderer', // 상위 > 하위 계층 이동할 때 상단 계층에 금액, 사용량 표시 없애기
            valueFormatter: this.displayCurrency !== 'KRW' ? (params) => totalRender(params) : (params) => totalRenderKrwOnly(params, this.displayCurrency,this.selectedVendor), // OCI 케이스
            getQuickFilterText: function (params) {
              return '';
            }
          },
        ];

      let result = this.setColumnDefsByView(commonColDefs, viewByOptions)
      return result;
    },
    setColumnDefsByView(columnList, viewByOptions){
      let result = []
      switch(viewByOptions){
        case VIEW_BY_OPTION_VALUE.INVOICE:
          STANDARD_COLUMN_DEF.headerName = 'invoiceId'
          STANDARD_COLUMN_DEF.field = 'invoiceId'
          columnList.unshift(STANDARD_COLUMN_DEF)
          result = columnList
          break;
        case VIEW_BY_OPTION_VALUE.ACCOUNT:
          STANDARD_COLUMN_DEF.headerName = 'linkedAccountId'
          STANDARD_COLUMN_DEF.field = 'linkedAccountId'
          columnList.unshift(STANDARD_COLUMN_DEF)
          result = columnList
          break;
        case VIEW_BY_OPTION_VALUE.REGION: // 리전 > 제품 > 사용유형 > 디테일
          let filteredList = columnList.filter(d => { return d.headerName !== "region" })
          STANDARD_COLUMN_DEF.headerName = 'regionName'
          STANDARD_COLUMN_DEF.field = 'regionName'
          filteredList.unshift(STANDARD_COLUMN_DEF)
          result = filteredList
          break;
        case VIEW_BY_OPTION_VALUE.TAG:
          STANDARD_COLUMN_DEF.headerName = 'tagValue'
          STANDARD_COLUMN_DEF.field = 'tagValue'
          columnList.unshift(STANDARD_COLUMN_DEF)
          result = columnList
          break;
        case VIEW_BY_OPTION_VALUE.SERVICEGROUP:
          STANDARD_COLUMN_DEF.headerName = 'serviceGroup'
          STANDARD_COLUMN_DEF.field = 'serviceGroup'
          columnList.unshift(STANDARD_COLUMN_DEF)
          result = columnList
          break;
        default:
          STANDARD_COLUMN_DEF.headerName = 'linkedAccountId'
          STANDARD_COLUMN_DEF.field = 'linkedAccountId'
          columnList.unshift(STANDARD_COLUMN_DEF)
          result = columnList
          break;
      }
      return result;
    },
    getColumnsWidth(percentage) {
      return (this.tableWidth * percentage) / 100
    },
    resetSearch() {
      this.cloudBillSearchText = '';
      this.onSearchCloudBillList();
    },
    // sortCloudBillDetails(sortType) {
    //   const sort = [
    //     {colId: 'ag-Grid-AutoColumn', sort: sortType}
    //   ];
    //   this.gridOptions.api.setSortModel(sort)
    // },
    onFilterTextBoxChanged() {
      this.gridApi.setQuickFilter(document.getElementById("filter-text-box").value);
    },
    onSearchCloudBillList() {

      this.gridOptions.api.setQuickFilter(this.cloudBillSearchText);
      this.$emit('onSearchList', this.cloudBillSearchText)
      if (this.gridApi) {
        if (_isEmpty(this.gridApi.rowModel.rowsToDisplay)) {
          this.gridApi.showNoRowsOverlay();
        } else {
          this.gridApi.hideOverlay();
        }
      }

      /*
      setInterval(() => {
        this.gridOptions.api.setQuickFilter(this.cloudBillSearchText);
        this.$emit('onSearchCloudBillList', this.cloudBillSearchText)
        if (this.gridApi) {
          if (_isEmpty(this.gridApi.rowModel.rowsToDisplay)) {
            this.gridApi.showNoRowsOverlay();
          } else {
            this.gridApi.hideOverlay();
          }
        }
      }, 1000)

       */
    },
    onGridReady(params) {
      this.gridOptions.api.sizeColumnsToFit();
    },
    onClickedExpandAll(){
      this.gridApi.expandAll();
    },
    onClickedCollapseAll(){
      this.gridApi.collapseAll();
    },

  }
};

function totalRender(params) {
  // return `${customFormatCostValue(params.value, null, CURRENCY[DEFAULT_CURRENCY])}`; // 소수점 자리에 연속으로 0인 케이스에선 정수부분 처리가 정상적이지 않음
  return customFormatterForDetailCosts(params.value);
}

function totalRenderKrwOnly(params, currency,selectedVendor) {
  return `${formatCostValue(params.value, null, currency, selectedVendor)}`;
}


function usageRender(params) {
  // switch (params.data.usageType) {
  //   case USAGE_TYPE.ADDEND_VALUE:
  //     //return $vm.$t('billing.cloudBillDetails.usageDescription', {'usage': params.data.usage})
  //     return $vm.$t('billing.cloudBillDetails.usageDescription', {'usage': params.value})
  //   case USAGE_TYPE.TOTAL_VALUE:
  //     //return $vm.$t('billing.cloudBillDetails.totalUsageDescription', {'usage': params.data.usage});
  //     return $vm.$t('billing.cloudBillDetails.totalUsageDescription', {'usage': params.value});
  //   default:
  //     //return $vm.$t('billing.cloudBillDetails.usageDescription', {'usage': params.data.usage})
  //     return $vm.$t('billing.cloudBillDetails.usageDescription', {'usage': params.value})
  // }
  //return formatCost(params.value);
  return customFormatterForDetailUsages(params.value);
}
</script>

<style lang="scss">
#screen-component-search-cloud-bill-detail {
  .icon-search {
    padding-right: 0 !important;
    padding-left: 18px !important;
  }
}
.cloud-bill-detail-table-wrapper {
  padding: 0;
  max-height: 480px;
  .cloud-bill-detail-table {
    min-height: auto;
    max-height: 480px;
    width: 100%;
    &.warning-banner-open {
      height: calc(100vh - 538px);
    }
    &.warning-banner-close {
      height: calc(100vh - 490px);
    }
  }
}
.change-color {
  color: #007bff;
}

/*Grid 빈 공간 생기는 현상 대응 > 패키지 전체에서 사용하는 스타일이라 보류*/
/*.ag-theme-bootstrap .ag-row-group {*/
/*  margin-top: 10px;*/
/*}*/

/*.ag-theme-bootstrap  .ag-row:not(.ag-row-first) {*/
/*  margin-top: 10px;*/
/*}*/
</style>
