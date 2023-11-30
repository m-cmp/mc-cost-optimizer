<template>
  <div
    v-if="shouldRenderComponent"
    class="prime-charge-list-table">
    <b-col
      id="screen-component-search"
      class="bg-white pb-1 px-0"
    >
      <b-input-group>
        <b-input-group-prepend>
          <b-button
            class="icon-search custom-color text-gray-1"
            variant="transparent"
            @click="onSearchChargeList"
          >
            <base-material
              :class="{ 'change-color': chargeListSearchText }"
              name="search"
              @click.native="resetSearch"
            />
          </b-button>
        </b-input-group-prepend>
        <b-form-input
          :placeholder="`${$t('billing.chargeList.searchAnything')}`"
          v-model="chargeListSearchText"
          class="search-input"
          @input="onSearchChargeList"
          @keyup.enter="onSearchChargeList"/>
        <base-material
          v-if="chargeListSearchText"
          class="material-icons close"
          name="close"
          @click.native="resetSearch"
        />
      </b-input-group>
    </b-col>
    <div>
      <b-col
        v-show="isChargeListLoading">
        <BaseLoadingIndicator
          :loading-height="188"
          :loading-item-right-px="0"
        />
      </b-col>
      <div
        v-show="!isChargeListLoading">
        <b-col
          v-show="!hasChargeListData"
          class="no-data-message">
          <BaseNotificationNoData/>
        </b-col>
        <b-col
          v-show = "hasChargeListData"
          style="padding: 0">
          <div
            v-if="filteredChargeList.length === 0"
            class="search-empty-message">
            <div class="empty-message-content">
              {{ noSearchData }}
            </div>
          </div>
          <DataTable
            v-else
            ref="dataTable"
            :value ="filteredChargeList"
            :rows ="10"
            :paginator="true"
            :rows-per-page-options="[10,20,30,50,100,150,200]"
            :show-gridlines="true"
            responsive-layout="scroll">
            <ColumnGroup
              type="header">
              <Row>
                <Column
                  :header = "columnDefs[0].headerName"
                  :header-style="{ left:0, position: 'sticky','min-width':'250px','max-width': '275px', 'background-color':'#F6F8FA','z-index':1 }"
                  :key = "columnDefs[0].columnKey"
                  :column-key ="columnDefs[0].columnKey"
                  :rowspan="3"
                  :sortable="true"
                  :field = "columnDefs[0].field"
                />
                <Column
                  :header = "columnDefs[1].headerName"
                  :header-style = "{'background-color' : '#eaebf4'}"
                  :colspan="columnDefs[1].children.length"
                  :key = "columnDefs[1].columnKey"
                  :column-key ="columnDefs[1].columnKey"
                  :field = "columnDefs[1].field"
                />
                <Column
                  v-if="columnDefs[2].children.length !== 0"
                  :header = "columnDefs[2].headerName"
                  :header-style = "{'background-color' : '#eaebf4'}"
                  :colspan="columnDefs[2].children.length"
                  :key = "columnDefs[2].columnKey"
                  :column-key ="columnDefs[2].columnKey"
                  :field = "columnDefs[2].field"
                />
                <Column
                  :header = "columnDefs[3].headerName"
                  :header-style="{ right:0, position: 'sticky', 'min-width':'120px','background-color':'#F6F8FA' }"
                  :key = "columnDefs[3].columnKey"
                  :column-key ="columnDefs[3].columnKey"
                  :rowspan="3"
                  :sortable="true"
                  :field = "columnDefs[3].field"
                />
              </Row> <!-- 상위헤더 -->
              <Row>
                <Column
                  v-for = "cloudService of columnDefs[1].children"
                  :header ="cloudService.headerName"
                  :header-style="{ 'min-width':'185px','background-color':'#F6F8FA' }"
                  :key = "cloudService.columnKey"
                  :column-key ="cloudService.columnKey"
                  :field = "cloudService.field"
                  :sortable="true"/>
                <Column
                  v-for = "additionService of columnDefs[2].children"
                  :key = "additionService.columnKey"
                  :column-key ="additionService.columnKey"
                  :header = "additionService.headerName"
                  :header-style="{ 'min-width':'195px','background-color':'#F6F8FA' }"
                  :field = "additionService.field"
                  :sortable="true"/>
              </Row> <!-- 하위헤더 -->
            </ColumnGroup>
            <!--  body 컬럼   -->
            <Column
              :field = "columnDefs[0].field"
              :key = "columnDefs[0].columnKey"
              :column-key = "columnDefs[0].columnKey"
              :body-style="{ left:0, position:'sticky','background-color':'#fff','text-align': 'left','z-index':1}">
              <template
                #body = "slotProps">
                <span
                  style ="cursor:pointer"
                  @click="onClickCell(slotProps)"
                  v-html="columnDefs[0].cellRenderer(slotProps)"/>
              </template>
            </Column>
            <Column
              v-for = "cloudService of columnDefs[1].children"
              :key = "cloudService.columnKey"
              :column-key = "cloudService.columnKey"
              :field ="cloudService.field">
              <template
                #body = "slotProps">
                <div
                  :class="{ 'mouse-highlight': isTooltipVisible[slotProps.index] && isTooltipVisible[slotProps.index][cloudService.columnKey] }"
                  @mouseover="showTooltip(slotProps.index,cloudService.columnKey,$event)"
                  @mouseleave="hideTooltip(slotProps.index,cloudService.columnKey)">
                  <span
                    v-html ="cloudService.cellRenderer(slotProps)"/>
                  <div
                    v-if="isTooltipVisible[slotProps.index] && isTooltipVisible[slotProps.index][cloudService.columnKey]"
                    @mouseleave="hideTooltip(slotProps.index,cloudService.columnKey)">
                    <!-- 툴팁 컴포넌트가 보여질 영역 -->
                    <SumOfCostTooltip
                      :slot-props="slotProps"
                      :apply-exchange-rate="applyExchangeRate"
                      :style="toolTipStyle"/>
                  </div>
                </div>
              </template>
            </Column>
            <Column
              v-for = "additionService of columnDefs[2].children"
              :key = "additionService.columnKey"
              :column-key = "additionService.columnKey"
              :field ="additionService.field">
              <template
                #body = "slotProps">
                <div
                  :class="{ 'mouse-highlight': isTooltipVisible[slotProps.index] && isTooltipVisible[slotProps.index][additionService.columnKey] && !isZeroValueTooltip(additionService.cellRenderer(slotProps)) }"
                  @mouseover="showTooltip(slotProps.index,additionService.columnKey,$event)"
                  @mouseleave="hideTooltip(slotProps.index,additionService.columnKey)">
                  <span
                    v-html ="additionService.cellRenderer(slotProps)"/>
                  <div
                    v-if="isTooltipVisible[slotProps.index] && isTooltipVisible[slotProps.index][additionService.columnKey] && !isZeroValueTooltip(additionService.cellRenderer(slotProps))"
                    @mouseleave="hideTooltip(slotProps.index,additionService.columnKey)">
                    <!-- 툴팁 컴포넌트가 보여질 영역 -->
                    <SumOfCostTooltip
                      :slot-props="slotProps"
                      :apply-exchange-rate="applyExchangeRate"
                      :style="toolTipStyle"/>
                  </div>
                </div>
              </template>
            </Column>
            <Column
              :field = "columnDefs[3].field"
              :key = "columnDefs[3].columnKey"
              :column-key = "columnDefs[3].columnKey"
              :body-style = "{ right:0, position: 'sticky','background-color':'#fff'}">
              <template
                #body = "slotProps">
                {{ columnDefs[3].cellRenderer(slotProps) }}
              </template>
            </Column>
          </DataTable>
        </b-col>
      </div>
    </div>
  </div>
</template>

<script>
import 'primevue/resources/themes/fluent-light/theme.css';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import ColumnGroup from 'primevue/columngroup';
import Row from 'primevue/row';
import {formatCostValue, getAllCodeToServiceFromChargeList} from '@/util/billingUtils';
import {CURRENCY, CURRENCY_SYMBOL, DEFAULT_PAGE_SIZE, VENDOR} from '@/constants/constants';
import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
import BaseNotificationNoData from '@/components/common/BaseNotificationNoData'
import _isEmpty from "lodash/isEmpty";
import _get from "lodash/get";
import {mapGetters} from "vuex";
import Pagination from '@/components/common/Pagination';
import _ from "lodash";
import {
  initWorkBookViaExcelJs,
  saveAndReturnSupportedUTF18CSVFile
} from "@/util/excelJS";
import SumOfCostTooltip from './sum-of-cost-tooltip/SumOfCostTooltip';
import Vue from "vue";

const DASHES = '-';


export default {
  name: "NewChargeListTable",
  components:{
    DataTable,
    Column,
    ColumnGroup,
    Row,
    BaseLoadingIndicator,
    BaseNotificationNoData,
    Pagination,
    SumOfCostTooltip
  },
  props:{
    chargeList:{
      type: Array,
      default: null
    },
    billList: {
      type: Array,
      required: true
    },
    selectedVendor: {
      type: String,
      required: true
    },
    activeMonthIdx: {
      type: Number,
      required: true
    },
    applyExchangeRate: {
      type: Number,
      required: true
    },
  },
  data() {
    return {
      columnDefs:[],
      currentPage: 0,
      totalPage: 1,
      paginationPageSize: null,
      chargeListSearchText: '',
      isTooltipVisible: {},
      shouldRenderComponent: true,
      frameworkComponents:null,
      noSearchData : this.$t('billing.chargeList.noResultsFound'),
      toolTipStyle:{}
    }
  },
  computed:{
    ...mapGetters({
      isChargeListLoading: 'billing/isChargeListLoading',
    }),
    hasChargeListData() {
      return this.chargeList && !_isEmpty(this.chargeList)
    },
    filteredChargeList:{
      cache: false,
      get(){
        if(!this.chargeListSearchText){
          return this.chargeList
        }
        const searchText = this.chargeListSearchText.toLowerCase();

        return this.chargeList.filter((row) => {

          const linkedAccountId = row.linkedAccountId ? row.linkedAccountId.toLowerCase() : '';
          const linkedAccountAlias = row.linkedAccountAlias ? row.linkedAccountAlias.toLowerCase() : '';

          return (linkedAccountId && linkedAccountId.includes(searchText) )
            || (linkedAccountAlias && linkedAccountAlias.includes(searchText))
        })
      }
    }
  },
  watch :{
    chargeList:{
      handler(){
        this.columnDefs = this.getColumnDefs()
        this.componentReload();
      },
    },
    '$i18n.locale': {
      handler() {
        this.columnDefs = this.getColumnDefs()
        this.noSearchData = this.$t('header.searchBar.nothingFound');
      },
    }
  },

  mounted() {
    this.columnDefs = this.getColumnDefs()
  },
  methods:{
    // vendor별 컬럼 정의
    getColumnDefs() {
      let columnDefs = [];
      switch (this.selectedVendor) {
        case VENDOR.AWS:
          columnDefs = this.getAwsColumnDefs();
          break;
        case VENDOR.AZURE:
          columnDefs = this.getAzureColumnDefs();
          break;
        case VENDOR.GCP:
          columnDefs = this.getGcpColumnDefs();
          break;
        case VENDOR.OCI:
          columnDefs = this.getOciColumnDefs();
          break;
        case VENDOR.NCP:
          columnDefs = this.getNcpColumnDefs();
          break;
        case VENDOR.TENCENT:
          columnDefs = this.getTencentColumnDefs();
          break;
        case VENDOR.OPENSTACK:
          columnDefs = this.getOpenStackColumnDefs();
          break;
        default:
          break;
      }
      return columnDefs;
    },
    getAwsColumnDefs(){
      let columnDefs = [
        {
          headerName:this.$t('billing.chargeList.cloudAccount'),
          field:'linkedAccountId',
          columnKey:'linkedAccountId',
          width: '250px',
          rowspan:3,
          cellRenderer :cloudAccountRender
        },
        {
          headerName: this.$t('billing.chargeList.cloudService'),
          field:'cloudService',
          columnKey:'cloudService',
          children:[
            {
              headerName: this.$t('billing.chargeList.cloudCost'),
              field: 'cloudCost',
              columnKey: 'cloudCost',
              width: '185px',
              cellRenderer: cloudCostRender
            },
            {
              headerName: this.$t('billing.chargeList.supportFee'),
              field: 'supportFee',
              columnKey: 'supportFee',
              width:'185px',
              cellRenderer: supportFeeRender
            },
            {
              headerName: this.$t('billing.chargeList.salesDiscount'),
              field: 'salesDiscount',
              columnKey: 'salesDiscount',
              width: '185px',
              cellRenderer: salesDiscountRender
            },
            {
              headerName: this.$t('billing.chargeList.credit'),
              field: 'credit',
              columnKey: 'credit',
              width: '185px',
              cellRenderer: creditRender
            }
          ],
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          rowspan:3,
          cellRenderer: totalRender
        }
      ]
      return columnDefs
    },
    getAzureColumnDefs(){
      let columnDefs = [
        {
          headerName: this.$t('billing.chargeList.cloudSubscription'),
          field: 'linkedAccountId',
          columnKey: 'linkedAccountId',
          width: '250px',
          tooltipField: 'linkedAccountId',
          cellRenderer: cloudAccountRender
        },
        {
          headerName: this.$t('billing.chargeList.cloudService'),
          children: [
            {
              headerName: this.$t('billing.chargeList.cloudCost'),
              field: 'cloudCost',
              columnKey: 'cloudCost',
              width: '185px',
              cellRenderer: cloudCostRender
            },
            {
              headerName: this.$t('billing.chargeList.supportFee'),
              field: 'supportFee',
              columnKey: 'supportFee',
              width: '185px',
              cellRenderer: supportFeeRender
            },
            {
              headerName: this.$t('billing.chargeList.salesDiscount'),
              field: 'salesDiscount',
              columnKey: 'salesDiscount',
              width: '185px',
              cellRenderer: salesDiscountRender
            },
            {
              headerName: this.$t('billing.chargeList.credit'),
              field: 'credit',
              columnKey: 'credit',
              width: '185px',
              cellRenderer: creditRender
            }
          ]
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          cellRenderer: totalRender
        }
      ];
      return columnDefs
    },
    getGcpColumnDefs(){
      let columnDefs = [
        {
          headerName: this.$t('billing.chargeList.cloudProject'),
          field: 'linkedAccountId',
          columnKey: 'linkedAccountId',
          width: '250px',
          cellRenderer: cloudAccountRender,
        },
        {
          headerName: this.$t('billing.chargeList.cloudService'),
          field: 'cloudService',
          columnKey: 'cloudService',
          children: [
            {
              headerName: this.$t('billing.chargeList.cloudCost'),
              field: 'cloudCost',
              columnKey: 'cloudCost',
              width: '185px',
              cellRenderer: cloudCostRender,
            },
            {
              headerName: this.$t('billing.chargeList.salesDiscount'),
              field: 'salesDiscount',
              columnKey: 'salesDiscount',
              width: '185px',
              cellRenderer: salesDiscountRender
            },
            {
              headerName: this.$t('billing.chargeList.credit'),
              field: 'credit',
              columnKey: 'credit',
              width: '185px',
              cellRenderer: creditRender
            }
          ]
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          cellRenderer: totalRenderGcp
        }
      ]

      return columnDefs
    },
    getOciColumnDefs() {
      let columnDefs = [
        {
          headerName: this.$t('billing.chargeList.cloudCompartment'),
          field: 'linkedAccountId',
          columnKey: 'linkedAccountId',
          width: '250px',
          cellRenderer: cloudAccountRender
          },
        {
          headerName: this.$t('billing.chargeList.cloudService'),
          children: [
            {
              headerName: this.$t('billing.chargeList.cloudCost'),
              field: 'cloudCost',
              columnKey: 'cloudCost',
              width: '185px',
              cellRenderer: cloudCostRender,
            },
            {
              headerName: this.$t('billing.chargeList.salesDiscount'),
              field: 'salesDiscount',
              columnKey: 'salesDiscount',
              width: '195px',
              cellRenderer: salesDiscountRender
            }
          ]
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          cellRenderer: totalRender
        }
      ];
      return columnDefs
    },
    getNcpColumnDefs() {
      let columnDefs = [
        {
          headerName: this.$t('billing.chargeList.cloudAccount'),
          field: 'linkedAccountId',
          columnKey: 'linkedAccountId',
          width: '250px',
          cellRenderer: cloudAccountRender,
        },
        {
          headerName: this.$t('billing.chargeList.cloudService'),
          children: [
            {
              headerName: this.$t('billing.chargeList.cloudCost'),
              field: 'cloudCost',
              columnKey: 'cloudCost',
              width: '185px',
              cellRenderer: cloudCostRender
            },
            {
              headerName: this.$t('billing.chargeList.ncpDiscount'),
              field: 'ncpDiscount',
              columnKey: 'ncpDiscount',
              width: '185px',
              cellRenderer: ncpDiscountRender
            },
            {
              headerName: this.$t('billing.chargeList.salesDiscount'),
              field: 'salesDiscount',
              columnKey: 'salesDiscount',
              width: '185px',
              cellRenderer: salesDiscountRender
            },
            {
              headerName: this.$t('billing.chargeList.credit'),
              field: 'credit',
              columnKey: 'credit',
              width: '185px',
              cellRenderer: creditRender
            }
          ]
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          cellRenderer: totalRender
        }
      ];
      return columnDefs
    },
    getTencentColumnDefs() {
      let columnDefs = [
        {
          headerName: this.$t('billing.chargeList.cloudAccount'),
          field: 'linkedAccountId',
          columnKey: 'linkedAccountId',
          width: '250px',
          cellRenderer: cloudAccountRender
        },
        {
          headerName: this.$t('billing.chargeList.cloudService'),
          children: [
            {
              headerName: this.$t('billing.chargeList.cloudCost'),
              field: 'cloudCost',
              columnKey: 'cloudCost',
              width: '185px',
              cellRenderer: cloudCostRender
            },
          ]
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          cellRenderer: totalRender
        }
      ];
      return columnDefs
    },
    getOpenStackColumnDefs(){
      let columnDefs = [
        {
          headerName: '서비스',     //OpenStack 데모용 코드, 다국어 처리 안함.
          field: 'linkedAccountId',
          columnKey: 'linkedAccountId',
          width: '250px',
          cellRenderer: cloudAccountRender,
        },
        {
          headerName: this.$t('billing.chargeList.cloudCost'),
          field: 'cloudCost',
          columnKey: 'cloudCost',
          width: '185px',
          cellRenderer: cloudCostRender
        },
        this.getAdditionalServiceColumnDef(),
        {
          headerName: this.$t('billing.chargeList.total'),
          field: 'totalCharge',
          columnKey: 'totalCharge',
          width: '120px',
          cellRenderer: totalRender
        }
      ];
      return columnDefs
    },
    getAdditionalServiceColumnDef() {
      let result = {
        headerName: this.$t('billing.chargeList.additionalServiceFee'),
        children: []
      }
      const allCodeToService = getAllCodeToServiceFromChargeList(this.chargeList);
      const allServiceCodes = Object.keys(allCodeToService);
      for (let i = 0; i < allServiceCodes.length; i++) {
        let field = `additionalServicesObject.${allServiceCodes[i]}.additionalServiceCharge`;
        let headerName = allCodeToService[allServiceCodes[i]].additionalServiceName;

        //Border class to support set style border when user highlight additional service

        let newAdditionalService = {
          headerName: headerName,
          field: field,
          columnKey : field,
          width:'195px',
          cellRenderer: (params) => additionalServiceChargeRender(params, allServiceCodes[i])
        };

        result.children.push(newAdditionalService);
      }
      return result;
    },
    onClickCell(params) {
      // this.$emit('clickCell', params);
      alert("미구현 기능입니다.")
    },
    onSearchChargeList(searchText) {
      this.chargeListSearchText = searchText;
    },
    resetSearch() {
      this.chargeListSearchText = '';
      this.onSearchChargeList();
    },
    componentReload(){
      this.shouldRenderComponent = false;
      this.$nextTick(() => {
        this.shouldRenderComponent = true;
      })
    },
    exportChargeList() {
      const activeMonthBill = this.billList[this.activeMonthIdx];
      const companyCurrency = {
        currency: activeMonthBill.companyCurrency
      }
      const invoiceCurrency = {
        currency: activeMonthBill.invoiceCurrency
      }

      let i18nCnyParam = {
        currency: CURRENCY.CNY
      }

      //new exportCsv
      let workbook = new this.$excel.Workbook();
      initWorkBookViaExcelJs(workbook);
      let worksheet = workbook.addWorksheet('data');



      // 데이터 설정

      const excelData = _.cloneDeep(this.chargeList);
      const allCodeToService = getAllCodeToServiceFromChargeList(excelData);
      const allServiceCodes = Object.keys(allCodeToService);
      this.excelRowData = this.getExportRowData(excelData,allServiceCodes);


      // 컬럼 설정
      worksheet.columns = this.getWorksheetColumns(companyCurrency,invoiceCurrency,allCodeToService,allServiceCodes);

      worksheet.addRows(this.excelRowData);
      // // 파일명 설정
      let excelFileName = `ChargeList_${activeMonthBill.chargeYear}${activeMonthBill.chargeMonth}`
      // // 파일 다운로드 처리
      saveAndReturnSupportedUTF18CSVFile(workbook, excelFileName);

    },
    getExportRowData(list,allServiceCodes) {
      let result = [];
      list.forEach(item => {
        let rowData = [];
        rowData.push(item.linkedAccountAlias);
        rowData.push(item.linkedAccountId);
        rowData.push(item.cloudCost ? Number(item.cloudCost.toFixed(2)).toLocaleString('ko') :0);
        rowData.push(item.supportFee ? Number(item.supportFee.toFixed(2)).toLocaleString('ko') :0);
        rowData.push(item.salesDiscount && item.salesDiscount !=0 ? Number(item.salesDiscount.toFixed(2)).toLocaleString('ko') :0);
        rowData.push(item.credit ? Number(item.credit.toFixed(2)).toLocaleString('ko') :0);
        allServiceCodes.forEach(serviceCode =>{
          if(!item.additionalServicesObject){
            rowData.push(0);
          }else if(item.additionalServicesObject && item.additionalServicesObject[`${serviceCode}`]){
            // rowData.push(rowData.push(Number(item.additionalServicesObject[`${serviceCode}`].additionalServiceCharge.toFixed(2)).toLocaleString('ko')));
            rowData.push(Number(item.additionalServicesObject[`${serviceCode}`].additionalServiceCharge.toFixed(2)).toLocaleString('ko'));
          }else{
            rowData.push(0);
          }
        })
        rowData.push(Number(item.totalCharge.toFixed(2)).toLocaleString('ko'));
        result.push(rowData)
      });

      return this.exportDataFilter(result);
    },
    exportDataFilter: function(exportDataList){
      return _.filter(exportDataList, _.flow(
        _.partial(
          _.some, _,
          _.flow(_.toLower, _.partial(_.includes, _, _.toLower(this.chargeListSearchText), 0))
        )
      ));
    },
    getWorksheetColumns(companyCurrency,invoiceCurrency,allCodeToService,allServiceCodes) {

      let accountId = this.$t('billing.chargeList.download.columnHeader.cloudAccountId');
      let accountName = this.$t('billing.chargeList.download.columnHeader.cloudAccountName');

      if(this.selectedVendor === 'GCP') {
        accountId = this.$t('billing.chargeList.download.columnHeader.projectId');
        accountName = this.$t('billing.chargeList.download.columnHeader.projectName');
      } else if(this.selectedVendor === 'AZURE'){
        accountId = this.$t('billing.chargeList.download.columnHeader.subscriptionId');
        accountName = this.$t('billing.chargeList.download.columnHeader.subscriptionName');
      }else if(this.selectedVendor === 'OCI'){
        accountId = this.$t('billing.chargeList.download.columnHeader.compartmentId');
        accountName = this.$t('billing.chargeList.download.columnHeader.compartmentName');
      }

      let defaultColumns = [
        {width: 150, header: accountName},
        {width: 145, header: accountId},
        {width: 100, header: this.$t('billing.chargeList.download.columnHeader.cloudCost', invoiceCurrency)},
        {width: 100, header: this.$t('billing.chargeList.download.columnHeader.supportFee', invoiceCurrency)},
        {width: 100, header: this.$t('billing.chargeList.download.columnHeader.salesDiscount', invoiceCurrency)},
        {width: 60, header: this.$t('billing.chargeList.download.columnHeader.credit', invoiceCurrency)}
      ];

      for (let i = 0; i < allServiceCodes.length; i++) {
        let headerName = allCodeToService[allServiceCodes[i]].additionalServiceName;
        let additionalServiceName = {width: 145, header: headerName + '(' + companyCurrency.currency + ')'}
        defaultColumns.push(additionalServiceName)
      }
      defaultColumns.push({width: 60, header: this.$t('billing.chargeList.download.columnHeader.total', companyCurrency)})
      return defaultColumns;
    },
    isZeroValueTooltip(value){
      let zeroText = value.slice(1)
      if(zeroText === '0'){
        return true
      }
      return false
    },
    showTooltip(rowIndex, columnKey,event) {
      this.toolTipLocate(event,rowIndex)
      if (!this.isTooltipVisible[rowIndex]) {
        this.$set(this.isTooltipVisible, rowIndex, {});
      }
      this.$set(this.isTooltipVisible[rowIndex], columnKey, true);
    },
    hideTooltip(rowIndex,columnKey) {
      // 해당 인덱스의 컬럼에 대한 툴팁을 숨깁니다.
      if (this.isTooltipVisible[rowIndex]) {
        this.$set(this.isTooltipVisible[rowIndex], columnKey, false);
      }
    },
    toolTipLocate(event,index){
      const upsideDown = 'translateY(-110%)'

      const cellClientXValue = event.clientX
      const datatableWrapperLeft = document.querySelector(".p-datatable-wrapper").getBoundingClientRect().left;
      this.toolTipStyle.left = cellClientXValue - datatableWrapperLeft+'px'

      let pageLength = this.getCurrentPageDataCount()
      let median = pageLength/2
      if(index>=median){
        this.toolTipStyle.transform = upsideDown
      } else {
        this.toolTipStyle.transform = ''
      }
    },
    getCurrentPageDataCount() {
      const dataTable = this.$refs['dataTable'];
      if (dataTable) {
        // DataTable 컴포넌트의 특정 DOM 요소인 p-datatable-tbody를 참조
        const tbodyElement = dataTable.$el.querySelector('.p-datatable-tbody');
        return tbodyElement.childElementCount;
      }
      return null;
    },
  },
}

function cloudAccountRender(params) {
  /*${params.data.linkedAccountId} ${_isEmpty(params.data.linkedAccountAlias) ? '' : `(${params.data.linkedAccountAlias})`}*/
  return `
      <span @click ="onClickCell"  class="cloud-account">
        ${_isEmpty(params.data.linkedAccountAlias) ? params.data.linkedAccountId : params.data.linkedAccountAlias + ` (${params.data.linkedAccountId})`}
      </span>
    `;

}
function cloudCostRender(params) {
  return `<span class="td-triangle">
      <span class="cost">${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(params.data.cloudCost, 'cloudCost', params.data.invoiceCurrency, params.data.vendor)}</span>
      <span class="triangle triangle-0"></span>
    </span>`;
}

function supportFeeRender(params) {
  return `${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(params.data.supportFee, 'supportFee', params.data.invoiceCurrency, params.data.vendor)}`;
}

function ncpDiscountRender(params) {
  if (!params.data.ncpDiscount || params.data.ncpDiscount === '0') {
    return `${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(params.data.ncpDiscount, 'ncpDiscount', params.data.invoiceCurrency, params.data.vendor)}`;
  }

  let discountVal = params.data.ncpDiscount > 0 ? params.data.ncpDiscount : Math.abs(params.data.ncpDiscount);

  return `${DASHES} ${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(discountVal, 'ncpDiscount', params.data.invoiceCurrency, params.data.vendor)}`;
}

function salesDiscountRender(params) {
  if (!params.data.salesDiscount || params.data.salesDiscount === '0') {
    return `${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(params.data.salesDiscount, 'salesDiscount', params.data.invoiceCurrency, params.data.vendor)}`;
  }

  let discountVal = params.data.salesDiscount > 0 ? params.data.salesDiscount : Math.abs(params.data.salesDiscount);

  return `${DASHES} ${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(discountVal, 'salesDiscount', params.data.invoiceCurrency, params.data.vendor)}`;
}

function creditRender(params) {
  if (!params.data.credit || params.data.credit === '0' || formatCostValue(params.data.credit, 'credit', params.data.invoiceCurrency, params.data.vendor) === '0') {
    return `${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(params.data.credit, 'credit', params.data.invoiceCurrency, params.data.vendor)}`;
  }
  return `${DASHES} ${CURRENCY_SYMBOL[params.data.invoiceCurrency]}${formatCostValue(params.data.credit, 'credit', params.data.invoiceCurrency, params.data.vendor)}`;
}

function totalRender(params) {
  if(params.data.companyCurrency === CURRENCY.KRW) {
    return `${CURRENCY_SYMBOL[params.data.companyCurrency]}${formatCostValue(params.data.totalCharge, 'totalCharge', params.data.companyCurrency, params.data.vendor)}`;
  } else {
    let additionalCost = 0;
    if(params.data.additionalServices !== undefined){
      params.data.additionalServices.forEach( service => {
        additionalCost += service.additionalServiceCharge
      })
    }
    return `${CURRENCY_SYMBOL[params.data.companyCurrency]}${formatCostValue(params.data.cloudServiceCharge + additionalCost, 'totalCharge', params.data.companyCurrency, params.data.vendor)}`;
  }
}

function totalRenderGcp(params) {

  if(params.data.totalCharge < 0) {
    let minusTotalCharge = Math.abs(params.data.totalCharge);
    return `${DASHES} ${CURRENCY_SYMBOL[params.data.companyCurrency]}${formatCostValue(minusTotalCharge , 'totalCharge', params.data.companyCurrency, params.data.vendor)}`;
  }else {
    return `${CURRENCY_SYMBOL[params.data.companyCurrency]}${formatCostValue(params.data.totalCharge , 'totalCharge', params.data.companyCurrency, params.data.vendor)}`;
  }
}
function additionalServiceChargeRender(params, serviceCode) {
  let companyCurrency = params.data.companyCurrency;
  let value = _get(params, `data.additionalServicesObject.${serviceCode}.additionalServiceCharge`) || 0;

  //showTooltipEventListener();
  let minus = value < 0 ? '-':'';
  if (value) {
    return `${minus}${CURRENCY_SYMBOL[companyCurrency]}${formatCostValue(value, 'additionalServicesObject', companyCurrency, params.data.vendor)}`;
  }
  return `${CURRENCY_SYMBOL[companyCurrency]}0`;
}

</script>

<style lang="scss">
.prime-charge-list-table{
  $prime-range-selected-border: #1772FF;
  $prime-range-selected-color-1: #F2F8FF;
  .cloud-account {
    color: #0672ff;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: block;
    width: 250px;
  }
  .p-datatable-wrapper {
    overflow-x:auto;
    min-height: 250px;
    background: #f6f6f6;
  }
  .p-datatable-thead > tr > th {
    text-align: left;
    padding: 0.75rem 0.75rem;
    border: 1px solid #f3f2f1;
    font-weight: 600;
    color: #6c7994;
    background: #FFFFFF;
    user-select: none;
  }
  .p-datatable-tbody > tr > td {
    font-weight: 600;
    font-size: 14px;
    text-align: right;
    border: 1px solid #f3f2f1;
    border-width: 0 0 1px 0;
    padding: 0.75rem 0.75rem;
    user-select: none;
  }
  .no-data-message {
    position: static;
    top: 50%;
    left: 0;
    right: 0;
    height: 188px;
    background-color: #ffffff;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .search-empty-message {
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 250px; /* 원하는 높이를 설정하세요 */
    background-color: #f6f6f6;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .empty-message-content {
    font-size: 1rem;
    text-align: center;
  }
  .td-triangle {
    position: relative;
  }

  .triangle {
    position: absolute;
    bottom: -10px;
    display: inline-block;
  }

  .triangle-0 {
    border-right: solid 10px #0672FF;
    border-top: solid 10px transparent;
  }

  .mouse-highlight {
    background-color: $prime-range-selected-color-1 !important;
    //border-left: 2px solid $ag-range-selected-border !important;
    //border-right: 2px solid $ag-range-selected-border !important;
  }
}

</style>
