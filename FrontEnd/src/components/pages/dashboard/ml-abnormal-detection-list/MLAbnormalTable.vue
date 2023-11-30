<template>
  <section
    id="ml-abnormal-list-table"
    class="-wrapper-column -wrapper-tile w-100 ">
    <div class="-header -wrapper-between -pr-3">

      <p class="-h2">{{ $t('anomaly.anomalyList') }}</p>
      <div>
        <!--컬럼 필터링 버튼 General 적용 : 탐지목록 추가 개발 때 적용하기-->
        <!--<button
          variant="transparent"
          class="-btn secondary- small- text-gray-1 -m-0"
        >
          <base-material
            :size="20"
            color="gray-1"
            name="filter_list" />
          <span>view option</span>
        </button>-->
        <!-- BackUp -->
        <!--
        <button
          class="-btn borderless- -minus-mr4"
          variant="transparent"
          @click="downloadGridData"
        >
        -->
        <button
          class="-btn borderless- -minus-mr4"
          variant="transparent"
          @click="exportPrimeAbnormalList"
        >
          <base-material
            :size="20"
            color="gray-1"
            name="get_app"
          />
        </button>
      </div>
    </div>
    <div>
      <b-col
        id="screen-component-search"
        class="bg-white pb-1 px-0 col-12"
      >
        <b-input-group>
          <b-input-group-prepend>
            <b-button
              class="icon-search custom-color text-gray-1"
              variant="transparent"
              @click="primeOnSearchCostAnalytics"
            >
              <base-material
                :class="{ 'change-color': searchText }"
                name="search"
                @click.native="resetSearch"
              />
            </b-button>
          </b-input-group-prepend>
          <b-form-input
            :placeholder="$t('costAnalytics.costAnalyticsTable.enterSearchTexts')"
            v-model="searchText"
            class="search-input"
            @input="primeOnSearchCostAnalytics"
            @keyup.enter="primeOnSearchCostAnalytics"/>
          <base-material
            v-if="searchText"
            id="ml-abnormal-list-table-closeSearchBtn"
            class="material-icons close"
            name="close"
            @click.native="resetSearch"
          />
        </b-input-group>
      </b-col>
    </div>
    <BaseLoadingIndicator
      v-show="isLoading"
      :loading-item-right-px="0"
      class="loading-indicator"
    />
    <div
      id="abnormal-detect-list-grid"
      :class="hasAbnormalData ? 'have-data' : 'no-data'"
      class="-wrapper-column w-100 -pb-9"
    >
      <!-- BackUp -->
      <!--
      <ag-grid-vue
        v-show="hasAbnormalData && dataLoadedFlag && !isLoading"
        :animate-rows="true"
        :column-defs="columnDefs"
        :enable-range-selection="false"
        :grid-options="gridOptions"
        :header-height="40"
        :row-data="userAbnormalRawData"
        :row-height="50"
        :sorting-order="sortingOrder"
        :framework-components="frameworkComponents"
        :loading-overlay-component="loadingOverlayComponent"
        :loading-overlay-component-params="loadingOverlayComponentParams"
        :no-rows-overlay-component="noRowsOverlayComponent"
        :no-rows-overlay-component-params="noRowsOverlayComponentParams"
        :class="hasAbnormalData ? 'have-data' : 'no-data'"
        class="ag-theme-bootstrap w-100"
        @grid-ready="onGridReady"
        @cellClicked="onCellClicked"
      />
      -->
      <PrimeMLAbnormalTable
        v-show="!isLoading"
        :columns="columnDefs"
        :row-data="primeOnSearchCostAnalytics"
        :value-prefix="currencySymbol"
        @cellClicked="onCellClicked"
      />
      <div v-if="hasAbnormalData">
        <p
          v-if="primeOnSearchCostAnalytics.length === 0 && searchText.length > 0"
          class="empty-message"
        >
          {{ noSearchData }}
        </p>
      </div>
      <!--      :suppress-column-virtualisation="true"-->

      <!--      <div class="-py-1">-->
      <!--        <p class="-tag red-">Critical</p>-->
      <!--        <p class="-tag green-">Minor</p>-->
      <!--        <p class="-tag yellow-">Normal</p>-->
      <!--      </div>-->

      <!--      <div class="-py-1">-->
      <!--        <p>-->
      <!--          <span class="ci ci-vendor-aws"/>-->
      <!--          <span class="-font-size-12">Platform dev(1234234234)</span>-->
      <!--        </p>-->
      <!--        <p>-->
      <!--          <span class="ci ci-vendor-azure"/>-->
      <!--          <span class="-font-size-12">Platform dev(1234234234)</span>-->
      <!--        </p>-->
      <!--        <p>-->
      <!--          <span class="ci ci-vendor-gcp"/>-->
      <!--          <span class="-font-size-12">Platform dev(1234234234)</span>-->
      <!--        </p>-->
      <!--      </div>-->


      <!--      <div class="-py-1">-->
      <!--        <span class="-tag gray-"><span class="material-icons">person</span> <p>User</p></span>-->
      <!--        <span class="-tag gray-"><base-icon-->
      <!--          :original="true"-->
      <!--          name="icon_ai" /><p>AI</p></span>-->
      <!--      </div>-->

      <!--      <div class="-py-1">-->
      <!--        <base-material-->
      <!--          :size="16"-->
      <!--          name="visibility"-->
      <!--          class="-color-blue-1" />-->
      <!--        <base-material-->
      <!--          :size="16"-->
      <!--          name="visibility_off"-->
      <!--          class="-color-lightgray-1" />-->

      <!--        <base-material-->
      <!--          :size="16"-->
      <!--          name="star"-->
      <!--          class="-color-blue-1" />-->
      <!--        <base-material-->
      <!--          :size="16"-->
      <!--          name="star_border"-->
      <!--          class="-color-lightgray-1" />-->

      <!--        <base-material-->
      <!--          :size="16"-->
      <!--          name="launch"-->
      <!--          class="-color-gray-1" />-->
      <!--      </div>-->


      <BaseNotificationNoData
        v-show="!hasAbnormalData && dataLoadedFlag && !isLoading"
        id="abnormal-detect-list-grid-no-data"
        content = ""
      />
    </div>

  </section>
</template>

<script>
  import PrimeMLAbnormalTable from "@/components/common/prime-abnormal-table/PrimeMLAbnormalTable";
  import _isEmpty from 'lodash/isEmpty';
  import {calculateCostByCurrency, formatCost} from '@/util/costUtils';
  import { CURRENCY_SYMBOL, DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE } from '@/constants/constants';
  import dayjs from 'dayjs';
  import CustomLoadingOverlay from '@/components/common/custom-loading-overlay/CustomLoadingOverlay';
  import CustomNoRowsOverlay from '@/components/common/custom-no-rows-overlay/CustomNoRowsOverlay';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import {mapGetters} from 'vuex';
  import AutoSizeColumns from '@/mixins/AutoSizeColumns';
  import {fetchAbnormalDetectedList} from "../../../../api/anomalyDetail";
  import {ABNORMAL_TIME_FRAME, DASHBOARD_VIEW_BY} from "../../../../constants/dashboardConstants";
  import {formatPercentage} from "../../../../util/costUtils";
  import {getFullDateFormatByLocalization} from "../../../../util/dateTimeUtils";
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from "../../../../constants/costAnalyticsConstants";
  import _isEqual from "lodash/isEqual";
  import {
    initWorkBookViaExcelJs,
    saveAndReturnSupportedUTF18CSVFile
  } from "@/util/excelJS";
  export default {
    name: 'MLAbnormalTable',
    components: {
      PrimeMLAbnormalTable,
      BaseNotificationNoData,
      BaseLoadingIndicator
    },
    mixins: [AutoSizeColumns],
    props: {
      anomalyListSettings: {
        type: Object,
        required: true
      },
      selectedAlertLevel: {
        type: String,
        default: ''
      },
      userAbnormalRawData:{
        type: Array,
        required: true
      },
      dataLoadedFlag:{
        type: Boolean,
        required: false,
        default: false
      },
      isLoading:{
        type: Boolean,
        required: false,
        default: true
      }
    },
    data() {
      return {
        //userAbnormalRawData:[],
        searchText: '',
        //dataLoadedFlag: false,
        columnDefs: [],
        compareCostDate: null,
        frameworkComponents: null,
        loadingOverlayComponent: null,
        loadingOverlayComponentParams: null,
        noRowsOverlayComponent: null,
        noRowsOverlayComponentParams: null,
        //isLoading: true
        noSearchData: this.$t('header.searchBar.nothingFound'),
      }
    },
    computed: {
      primeOnSearchCostAnalytics() {
        if (!this.searchText) {
          return this.userAbnormalRawData;
        }
        let filteredData = this.userAbnormalRawData.filter(obj => {
          return Object.keys(obj).some(key => {
            let value = obj[key];
            if (typeof value === 'string') {
              return value.toLowerCase().replace(/\s+/g, '').includes(this.searchText.toLowerCase().replace(/\s+/g, ''));
            }
            return false;
          });
        });
        return filteredData;
      },
      currencySymbol: function() {
        return CURRENCY_SYMBOL[this.commonUserInfo ? this.commonUserInfo.selectedCurrency : CURRENCY.USD];
      },
      ...mapGetters({
        // isLoading: 'common/isLoading',
        commonUserInfo: 'common/info',
        exchangeRate: 'common/exchangeRate'
      }),
      tableData:{
        cache: true,
        get(){
          if(_isEmpty(this.userAbnormalRawData)){
            return []
          }
          return this.prepareDataForTable(this.userAbnormalRawData)
        }
      },
      hasAbnormalData: {
        get(){
          return !_isEmpty(this.userAbnormalRawData)
        }
      },
      enableCostAnalyticsDrillDown:{
        get(){
          return this.Common.checkCostAnalyticsMenuAuth(this);
        }
      }
    },
    watch: {
      tableData: function () {
        this.refreshTableValue();
      },
      '$i18n.locale': {
        handler() {
          this.columnDefs = this.getDefaultCols();
          this.noSearchData = this.$t('header.searchBar.nothingFound');
          refreshGridOnChanged(this)
          let self = this
          setTimeout(function(){
            self.primeOnSearchCostAnalytics();
          },10)
        },
      },
      selectedAlertLevel: {
        handler() {
          this.primeOnSearchCostAnalytics();
        },
        immediate: false
      },
      enableCostAnalyticsDrillDown: {
        handler() {
          this.columnDefs = this.getDefaultCols();
          refreshGridOnChanged(this)
        },
        immediate: false
      }
    },
    mounted() {
      // BackUp
      // this.gridApi = this.gridOptions.api;
      // this.gridColumnApi = this.gridOptions.columnApi;
      // this.gridOptions.api.showLoadingOverlay()
      // this.gridApi.sizeColumnsToFit();
      window.addEventListener('resize', this.handleWindowResize);
    },
    created() {
      this.gridOptions = {
        localeText: {
          noRowsToShow: this.$t('costAnalytics.costAnalyticsTable.noResultsFound')
        },
      };
      this.columnDefs = this.getDefaultCols();
      this.sortingOrder = ['desc', 'asc', null];
      this.frameworkComponents = {
        customLoadingOverlay: CustomLoadingOverlay,
        customNoRowsOverlay: CustomNoRowsOverlay
      };
      this.loadingOverlayComponent = "customLoadingOverlay";
      this.loadingOverlayComponentParams = {};

      //this.getDetectedListData();

    },
    methods: {
      getNoRowsToShow(){
        return this.$t('costAnalytics.costAnalyticsTable.noResultsFound')
      },
      getDetectedListData(){
        let payload = {
          startDate: this.anomalyListSettings.startDate,
          endDate: this.anomalyListSettings.endDate,
          widgetIndex: this.anomalyListSettings.widgetIndex,
          dashboardIndex: this.anomalyListSettings.dashboardIndex,
          sensitivity: this.anomalyListSettings.sensitivity,
        }
        this.isLoading = true
        fetchAbnormalDetectedList(payload).then(response => {
          this.userAbnormalRawData = response
          this.dataLoadedFlag = true
          this.isLoading = false
        })
      },
      prepareDataForTable(rawData){
        let rowNum = 1;
        rawData.forEach( raw => {
            Object.keys(raw).forEach(column => {
              if(column == 'analDt'){
                let param = {value: raw[column], data: raw}
                // eslint-disable-next-line no-param-reassign
                raw.analDt_txt = analysisDateRender(param,this,true)
              }

              if(column == 'message'){
                let param = {value: raw[column], data: raw}
                // eslint-disable-next-line no-param-reassign
                raw.message_txt = messageRender(param,this,true)
              }

              if(column == 'detcDt'){
                let param = {value: raw[column], data: raw}
                // eslint-disable-next-line no-param-reassign
                raw.detcDt_txt = detectedDateRender(param,this,true)
              }
            });

            // eslint-disable-next-line no-param-reassign
            raw.rowNum = rowNum++
          }
        )
        return rawData
      },
      refreshTableValue() {
        const $vm = this;
        setTimeout(function() {
          // Backup
          // $vm.gridApi.sizeColumnsToFit();
        }, 10);
      },
      handleWindowResize(){
        this.refreshTableValue();
      },
      onGridReady(params) {
        this.api = params.api;
        let sortDefault = [
          {
            colId: "totalCost",
            sort: "desc"
          }
        ];
        this.gridApi.setSortModel(sortDefault);
        params.api.setDomLayout('normal');
        this.autoSizeAll();
        try {
          // eslint-disable-next-line no-param-reassign
          params.api.context.beanWrappers.tooltipManager.beanInstance.MOUSEOVER_SHOW_TOOLTIP_TIMEOUT = 0;
        } catch (e) {
          console.error(e);
        }
      },
      // BackUp
      // onSearchCostAnalytics() {
      //   this.gridOptions.api.setQuickFilter(this.searchText + ' ' + this.selectedAlertLevel);
      //   if (this.gridApi) {
      //     if (_isEmpty(this.gridApi.rowModel.rowsToDisplay)) {
      //       this.gridApi.showNoRowsOverlay();
      //       document.getElementsByClassName("ag-floating-bottom")[0].style.display = 'none'
      //     } else {
      //       let dataItems = [];
      //       this.gridApi.rowModel.rowsToDisplay.forEach((rowToDisplay) => {
      //         dataItems.push(rowToDisplay.data.item);
      //       });
      //       this.gridApi.hideOverlay();
      //     }
      //   }
      // },
      resetSearch() {
        this.searchText = '';
        this.primeOnSearchCostAnalytics();
      },
      // BackUp
      // onCellClicked(params) {
      //   if (params.column.colId === "detail") {
      //     let enableVendor = this.Common.checkVendorAvailableFromAllVendors([params.data.vendor] ,COST_ANALYTICS_VIEW_BY_VENDORS )
      //     if(_isEqual(this.profile.env, "CHINA") && _isEqual(params.data.vendor, "GCP")) {
      //       enableVendor = false
      //     }
      //     if(params.data.detcBy === 'USER' && (!this.enableCostAnalyticsDrillDown || !enableVendor)){
      //       return
      //     }
      //     this.$emit('onCellClicked', params.data);
      //   }
      // },
      onCellClicked(params) {
        let enableVendor = this.Common.checkVendorAvailableFromAllVendors([params.vendor] ,COST_ANALYTICS_VIEW_BY_VENDORS )
        if(_isEqual(this.profile.env, "CHINA") && _isEqual(params.vendor, "GCP")) {
          enableVendor = false
        }
        if(params.detcBy === 'USER' && (!this.enableCostAnalyticsDrillDown || !enableVendor)){
          return
        }
        this.$emit('onCellClicked', params);
      },
      getViewByText(viewBy) {
        switch (viewBy) {
          case DASHBOARD_VIEW_BY.ACCOUNT:
            return this.$t('dashboard.abnormalChange.viewByPlurals.account');
          case DASHBOARD_VIEW_BY.PRODUCT:
            return this.$t('dashboard.abnormalChange.viewByPlurals.product');
          case DASHBOARD_VIEW_BY.REGION:
            return this.$t('dashboard.abnormalChange.viewByPlurals.region');
          default:
            return this.$t('dashboard.abnormalChange.viewByPlurals.account');
        }
      },
      getDefaultCols() {
        return [
          {
            headerName: this.$t(`anomaly.detectList.header.rowNum`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.rowNum`))
            },
            field: 'rowNum',
            colId: 'rowNum',
            width: 60,
            minWidth: 60,
            maxWidth: 80
          },
          {
            headerName: this.$t(`anomaly.detectList.header.level`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.level`))
            },
            field: 'level',
            colId: 'level',
            cellRenderer: params => levelRender(params, this),
            width: 85,
            minWidth: 85,
            maxWidth: 100
          },
          {
            headerName: this.$t(`anomaly.detectList.header.detectType`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.detectType`))
            },
            field: 'detcBy',
            colId: 'detcBy',
            cellRenderer: params => analysisTypeRender(params, this),
            width: 85,
            minWidth: 85,
            maxWidth: 100
          },
          {
            headerName: this.$t(`anomaly.detectList.header.cloudService`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.cloudService`))
            },
            field: 'vendor',
            colId: 'vendor',
            cellRenderer: params => vendorRender(params, this),
            width: 120,
            minWidth: 110,
            maxWidth: 130
          },
          {
            headerName: this.$t(`anomaly.detectList.header.analysisDate`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.analysisDate`))
            },
            field: 'analDt',
            colId: 'analDt',
            cellRenderer: params => analysisDateRender(params, this),
            width: 260,
            minWidth: 240,
            maxWidth: 300,
            resizable: true
          },
          {
            headerName: this.$t(`anomaly.detectList.header.analysisDate`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.analysisDate`))
            },
            field: 'analDt_txt',
            colId: 'analDt_txt',
            cellRenderer: params => analysisDateRender(params, this, true),
            width: 200,
            minWidth: 250,
            maxWidth: 250,
            hide: true,
            resizable: true
          },
          {
            headerName: this.$t(`anomaly.detectList.header.message`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.message`))
            },
            // BackUp
            // field: 'message',
            field: 'message_dt',
            colId: 'message',
            cellRenderer: params => messageRender(params, this),
            width: 540,
            minWidth: 530,
            resizable: true
          },
          {
            headerName: this.$t(`anomaly.detectList.header.message`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.message`))
            },
            field: 'message_txt',
            colId: 'message_txt',
            cellRenderer: params => messageRender(params, this,true),
            width: 400,
            minWidth: 380,
            hide: true,
            resizable: true
          },
          {
            headerName: this.$t(`anomaly.detectList.header.detectDate`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.detectDate`))
            },
            field: 'detcDt',
            colId: 'detcDt',
            cellRenderer: params => detectedDateRender(params, this),
            width: 130,
            minWidth: 120,
            maxWidth: 135
          },
          {
            headerName: this.$t(`anomaly.detectList.header.detectDate`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.detectDate`))
            },
            field: 'detcDt_txt',
            colId: 'detcDt_txt',
            cellRenderer: params => detectedDateRender(params, this, true),
            width: 145,
            minWidth: 140,
            maxWidth: 150,
            hide: true
          },
          {
            headerName: this.$t(`anomaly.detectList.header.detail`),
            headerComponentParams:{
              template: headerTemplate(this.$t(`anomaly.detectList.header.detail`))
            },
            field: 'message',
            colId: 'detail',
            cellRenderer: params => detailRender(params, this),
            width: 80,
            minWidth: 80,
            maxWidth: 95,
            cellClass:"-wrapper-h-center",
            cellStyle: params => getDetailLinkStyle(params, this)
          },
        ];
      },
      downloadGridData(){
        const params = {
          columnKeys:['rowNum','level','detcBy','vendor','analDt_txt','message_txt','detcDt_txt'],
          processCellCallback: getProcessCellCallBackForDownload(this)
        }
        this.gridApi.exportDataAsCsv(params);
      },
      exportPrimeAbnormalList(){
        const $vm = this;
        let workbook = new this.$excel.Workbook();
        initWorkBookViaExcelJs(workbook);
        let excelFileName = 'export';
        let worksheet = workbook.addWorksheet(excelFileName);
        let excludedFields = ["analDt", "message_dt", "detcDt", "message"]
        let columnDefs = this.columnDefs

        let filteredColumns = columnDefs.filter(columnDefs => !excludedFields.includes(columnDefs.field));

        worksheet.columns = filteredColumns.map(col => ({ header : col.headerName }));

        function alarmLevelRenderText(alarmLevel){
          switch (alarmLevel) {
            case 'minor':
              return $vm.$t('dashboard.abnormalChange.alarmLevels.minor');
            case 'critical':
              return $vm.$t('dashboard.abnormalChange.alarmLevels.critical');
            case 'major':
            default:
              return $vm.$t('dashboard.abnormalChange.alarmLevels.major');
          }
        }
        let rowData = [];

        rowData = this.primeOnSearchCostAnalytics.map(function (item) {
          let data = [
              item.rowNum,
              alarmLevelRenderText(item.level),
              item.detcBy,
              item.vendor,
              item.analDt_txt,
              item.message_txt,
              item.detcDt_txt
            ]
          ;
          return data;
        })

        worksheet.addRows(rowData);
        saveAndReturnSupportedUTF18CSVFile(workbook, excelFileName);
      },
    }
  };

  function headerTemplate(headerText){
    return `<span class="gray- font-weight-bold" style="white-space: normal">${headerText}</span>`
  }

  function vendorRender(params, $vm) {
    switch (params.value){
      case 'AWS':
        return `<span class="ci -wrapper-v-h-center ci-vendor-aws"/><span class="-font-size-12 -ml-2">AWS</span>`
      case 'GCP':
        return `<span class="ci -wrapper-v-h-center ci-vendor-gcp"/><span class="-font-size-12 -ml-2">GCP</span>`
      case 'AZURE':
        return `<span class="ci -wrapper-v-h-center ci-vendor-azure"/><span class="-font-size-12 -ml-2">Azure</span>`
      case 'OCI':
        return `<span class="ci -wrapper-v-h-center ci-vendor-oci"/><span class="-font-size-12 -ml-2">OCI</span>`
    }
  }

  function levelRender(params, $vm){
    switch (params.value) {
      case 'minor':
        return `<p class="-tag green-">` + $vm.$t('dashboard.abnormalChange.alarmLevels.minor') + `</p>`;
      case 'critical':
        return `<p class="-tag red-">` + $vm.$t('dashboard.abnormalChange.alarmLevels.critical') + `</p>`;
      case 'major':
      default:
        return `<p class="-tag yellow-">` + $vm.$t('dashboard.abnormalChange.alarmLevels.major') + `</p>`;
    }
  }

  function analysisTypeRender(params, $vm){
    switch (params.value) {
      case 'USER':
        return `<span class="-tag gray-"><span class="material-icons">person</span> <p>User</p></span>`
      case 'AI':
      default:
        return `<span class="-tag gray-">
                    <span class="ci ci-icon-ai"></span>
                    <p>AI</p>
                </span>`
    }
  }

  function getProcessCellCallBackForDownload($vm){
      return function(params) {
        switch (params.column.getColDef().field) {
          case 'level' :
           switch (params.value) {
             case 'minor':
               return `${$vm.$t('dashboard.abnormalChange.alarmLevels.minor')}`
             case 'critical':
               return `${$vm.$t('dashboard.abnormalChange.alarmLevels.critical')}`
             case 'major':
             default:
               return `${$vm.$t('dashboard.abnormalChange.alarmLevels.major')}`
           }
          default:
            return params.value
       }
      }
  }

  function analysisDateRender(params, $vm, dontRenderFlag){

    let paragraphStartHtml = `<p class="-paragraph">`
    let paragraphEndHtml = `</p>`
    let descriptionStartHtml = `<p class="-description">`
    let descriptionEndHtml = `</p>`
    let cellStartHtml = `<div class="-py-1 ">`
    let cellEndHtml = `</div>`
    if(dontRenderFlag === true){
      paragraphStartHtml = ``
      paragraphEndHtml = ``
      descriptionStartHtml = ``
      descriptionEndHtml = ``
      cellEndHtml = ``
      cellStartHtml = ``
    }

    let dateList = params.value.split('-');
    let year = dateList[0]
    let month = dateList[1]
    let day = dateList[2]
    let vHtml = cellStartHtml + `${paragraphStartHtml}${$vm.$t('anomaly.detectList.cell.asOfDate', {'year':year,'month':month,'day':day})}${paragraphEndHtml}`

    if(params.data.detcBy === 'AI'){
      vHtml += `${descriptionStartHtml}(${$vm.$t('anomaly.detectList.cell.aiAnomalyVsRealCostContent')})${descriptionEndHtml}`
    }else{
      switch (params.data.message.timeFrame) {
        case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
          vHtml += `${descriptionStartHtml}(${$vm.$t('anomaly.detectList.cell.anomalyDetectTimeFrameVSContent.latest3DaysTotalAnd3DaysBeforeThat')})${descriptionEndHtml}`
          break;
        case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
          vHtml += `${descriptionStartHtml}(${$vm.$t('anomaly.detectList.cell.anomalyDetectTimeFrameVSContent.latest7DaysTotalAnd7DaysBeforeThat')})${descriptionEndHtml}`
          break;
        case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
          vHtml += `${descriptionStartHtml}(${$vm.$t('anomaly.detectList.cell.anomalyDetectTimeFrameVSContent.latestTotalCostAndAverageCostOfLatest7Days')})${descriptionEndHtml}`
          break;
        case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
          vHtml += `${descriptionStartHtml}(${$vm.$t('anomaly.detectList.cell.anomalyDetectTimeFrameVSContent.thisMonthSoFarAndSamePeriodOfLastMonth')})${descriptionEndHtml}`
          break;
      }
    }

    vHtml += cellEndHtml

    return vHtml
  }
  function messageRender(params, $vm , dontRenderFlag){
    let bordStartHtml = `<strong class="-font-weight-bold font-family-notosanscjkkr-Bold">`
    let bordEndHtml = `</strong>`
    let cellStartHtml = `<span class="-paragraph" style="white-space: normal">`
    let cellEndHtml = `</span>`
    if(dontRenderFlag === true){
      bordStartHtml = ``
      bordEndHtml = ``
      cellStartHtml = ``
      cellEndHtml = ``
    }
    //let cost = `${CURRENCY_SYMBOL[DEFAULT_CURRENCY]}${formatCost(Math.abs(params.value.cost))}`;
    let getCurrency = params.value.currency === $vm.commonUserInfo.selectedCurrency ? params.value.currency : $vm.commonUserInfo.selectedCurrency
    let cost =`${CURRENCY_SYMBOL[getCurrency]}${formatCost(calculateCostByCurrency(Math.abs(params.value.cost), getCurrency, $vm.exchangeRate))}`
  let rate = formatPercentage(Math.abs(params.value.rate)) + '%';
  if(params.data.detcBy === 'AI'){
    if(params.value.rate >=0 ){
      return cellStartHtml + $vm.$t('anomaly.detectList.cell.aiAnomalyDetectionIncreased', {'vendor': params.data.vendor, 'cost': bordStartHtml + cost + ` (${rate})` + bordEndHtml}) + cellEndHtml
      } else {
        return cellStartHtml + $vm.$t('anomaly.detectList.cell.aiAnomalyDetectionDecreased', {'vendor': params.data.vendor, 'cost': bordStartHtml + cost + ` (${rate})` + bordEndHtml}) + cellEndHtml
      }
    }else{
      let increaseDecreaseTextOption = (params.value.cost >= 0) ? 'anomalyDetectIncreasedTextOptions' : 'anomalyDetectDecreasedTextOptions';
      switch (params.value.timeFrame) {
        case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE:
          return cellStartHtml + $vm.$t('anomaly.detectList.cell.'  + increaseDecreaseTextOption + '.latest3DayTotalVs3DaysBefore',
            {
              'count': `${params.value.count}`,
              'viewBy': $vm.getViewByText(params.value.viewBy),
              'cost': bordStartHtml + cost + ` (${rate})` + bordEndHtml,
            }) + cellEndHtml;
          break;
        case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE:
          return cellStartHtml + $vm.$t('anomaly.detectList.cell.'  + increaseDecreaseTextOption + '.latest7DayTotalVs7DaysBefore',
            {
              'count': `${params.value.count}`,
              'viewBy': $vm.getViewByText(params.value.viewBy),
              'cost': bordStartHtml + cost + ` (${rate})` + bordEndHtml,
            }) + cellEndHtml;
          break;
        case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS:
          return cellStartHtml + $vm.$t('anomaly.detectList.cell.'  + increaseDecreaseTextOption + '.latestTotalCostVsAverageCostOfLatest7Days',
            {
              'count': `${params.value.count}`,
              'viewBy': $vm.getViewByText(params.value.viewBy),
              'cost': bordStartHtml + cost + ` (${rate})` + bordEndHtml,
            }) + cellEndHtml;
          break;
        case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH:
          return cellStartHtml + $vm.$t('anomaly.detectList.cell.'  + increaseDecreaseTextOption + '.thisMonthSoFarVsSamePeriodOfLastMonth',
            {
              'count': `${params.value.count}`,
              'viewBy': $vm.getViewByText(params.value.viewBy),
              'cost': bordStartHtml + cost + ` (${rate})` + bordEndHtml,
            }) + cellEndHtml;
          break;
      }
    }
  }


  function detectedDateRender(params, $vm, dontRenderFlag){
    let date = params.value.split(' ')[0].replaceAll('-','/');
    // let time = params.value.split(' ')[1];
    let dateStr = $vm.$dayjs(date).format(getFullDateFormatByLocalization())
    return dateStr
  }


  function detailRender(params, $vm){
    let enableVendor = $vm.Common.checkVendorAvailableFromAllVendors([params.data.vendor] ,COST_ANALYTICS_VIEW_BY_VENDORS )
    if(_isEqual($vm.profile.env, "CHINA") && _isEqual(params.data.vendor, "GCP")) {
      enableVendor = false
    }
    if(params.data.detcBy === 'USER' && (!$vm.enableCostAnalyticsDrillDown || !enableVendor)){
      return ''
    }
    return `<span class="material-icons -font-size-14 -ml-1 -color-gray-1">launch</span>`
  }


  function renderCustomHeader(name) {
    return dayjs(name).format('MM/DD')
  }

  function cellRendererWithCurrency(value, currency, exchangeRate) {
    if (value === '-') {
      return value;
    }
    return `${CURRENCY_SYMBOL[currency]} ${formatCost(calculateCostByCurrency(value, currency, exchangeRate))}`;
  }

  function accountRender(params) {
    if (params.node.childIndex >= 0 && params.data.displayItem) {
      return `<span>${params.data.displayItem}</span>`
    }
  }


  function formatCostToNumber(cost) {
    return Number(formatCost(cost, {thousandSeparated: false}));
  }

  function refreshGridOnChanged($vm){
    // BackUp
    // $vm.gridOptions.api.refreshHeader();
    // $vm.gridOptions.api.sizeColumnsToFit();
    $vm.refreshTableValue();
    // eslint-disable-next-line no-param-reassign
    $vm.gridOptions.localeText.noRowsToShow = $vm.$t('costAnalytics.costAnalyticsTable.noResultsFound');
  }

  function getDetailLinkStyle(params, $vm){
    let enableVendor = $vm.Common.checkVendorAvailableFromAllVendors([params.data.vendor] ,COST_ANALYTICS_VIEW_BY_VENDORS )
    if(_isEqual($vm.profile.env, "CHINA") && _isEqual(params.data.vendor, "GCP")) {
      enableVendor = false
    }
    if(params.data.detcBy === 'USER' && (!$vm.enableCostAnalyticsDrillDown || !enableVendor)){
      return { cursor: 'default'};
    }
    return { cursor: 'pointer' };
  }

</script>

<style lang="scss">
#abnormal-detect-list-grid.no-data{
  height: 300px
}
#abnormal-detect-list-grid.have-data{
  height: 800px
}
#abnormal-detect-list-grid{
  .no-data{
    height: 300px
  }
  .have-data{
    height: 800px
  }
  .ag-row .ag-cell {
    display: flex;
    align-items: center;
  }
  .-header {
    padding-right: 28px!important;
  }
}
#abnormal-detect-list-grid-no-data{
  padding-top: 0px!important;
  position: relative !important;
}
#ml-abnormal-list-table {
  .loading-indicator {
    position: relative;
    top: 30%;
    width: 100%;
  }
  .-header {
    padding-right: 28px;
  }
}
#ml-abnormal-list-table-closeSearchBtn{
  right: -8px!important;
}
.empty-message {
  margin-top: 25%;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  font-family: NotoSansCJKkr-Regular !important;
  user-select: none;
}
</style>


