<template>
  <b-tabs
    id="billing-detail-table-tabs"
    v-model="selectedTabIndex"
    active-nav-item-class="medium"
    class="custom-tabs border-color-gray-2"
  >
    <b-row
      class="base-absolute top-10 tab-panel-wrapper"
      no-gutters>
      <div
        class="billing-detail-download"
        @click="toggleEmailPopoverTooltip">
        <b-button
          :id="'sendInvoice'"
          class="btn-download"
          variant="transparent">
          <base-material
            :size="24"
            :name="'email'"
            width="12"
            height="14"
            color="gray-2"/>
        </b-button>
      </div>
      <b-tooltip
        :target="'sendInvoice'"
      >
        {{ $t('billing.sendInvoiceModal.title') }}
      </b-tooltip>
      <BasePopoverDropdown
        :target="'sendInvoice'"
        :placement="detailTableDownload.placement"
        :options="sendInvoice.options"
        :show-popover="detailTableDownload.showEmailPopover"
        :enabled-localization="detailTableDownload.enabledLocalization"
        @selectOption="onSelectSendInvoiceType"
      />

      <b-modal
        v-model="showSendInvoiceModal"
        :title="$t('billing.sendInvoiceModal.title')"
        modal-class="right-wing"
        hide-footer
        hide-backdrop>
        <!--
        <SendInvoiceModal
          :selected-vendor="selectedVendor"
          :send-type="invoiceSendType"
          :charge-list-item="selectedChargeListItem"
          @hideModal="hideSendInvoiceModal"/>
          -->
      </b-modal>

      <div
        class="billing-detail-download"
        @click="togglePopoverTooltip">
        <b-button
          :id="detailTableDownload.billingDetailTableDownloadBtnId"
          class="btn-download"
          variant="transparent">
          <base-icon
            v-if="Profile.env !== 'HCMP'"
            :size="24"
            width="12"
            height="14"
            name="icon_download"
            color="#7b8088"/>
          <span
            v-if="Profile.env === 'HCMP'"
            class="material-icons -color-gray-1 -font-size-20">get_app</span>
        </b-button>
      </div>
      <b-tooltip
        :target="detailTableDownload.billingDetailTableDownloadBtnId"
      >
        {{ $t('download') }}
      </b-tooltip>
      <BasePopoverDropdown
        :target="detailTableDownload.billingDetailTableDownloadBtnId"
        :placement="detailTableDownload.placement"
        :options="detailTableDownloadOptions"
        :show-popover="detailTableDownload.showPopover"
        :enabled-localization="detailTableDownload.enabledLocalization"
        @selectOption="onSelectDetailTableDownloadOption"
      />
    </b-row>
    <base-warning-banner
      :opened="warningBanner.opened"
      :message="warningBanner.message"
      @onCloseBanner="onCloseBanner"/>
    <base-notice-banner
      :opened="!isInvoiceInsightListLoading && invoiceInsightList.length < 1 && showInvoiceInsightBanner && !isClosedNoticeBanner"
      :message="noticeBanner.message"
      @onCloseBanner="onCloseNoticeBanner"/>
    <b-tab
      active
      @click="changeTab(TAB_INDEX[selectedVendor].CHARGE_LIST)">
      <template slot="title">
        <p
          :class="selectedTabIndex === TAB_INDEX[selectedVendor].CHARGE_LIST ? 'font-family-notosanscjkkr-medium' : ''"
          class="font-14 py-10"
        >{{ $t(`billing.billingDetailTableTabs.${selectedVendor.toLowerCase()}.chargeList`) }}</p>
      </template>
      <!-- Back_up -->
      <!--
      <ChargeListTable
        v-show="selectedTabIndex === TAB_INDEX[selectedVendor].CHARGE_LIST"
        ref="chargeListTable"
        :show-flag="selectedTabIndex === TAB_INDEX[selectedVendor].CHARGE_LIST"
        :charge-list="chargeList"
        :bill-list="billList"
        :selected-vendor="selectedVendor"
        :active-month-idx="activeMonthIdx"
        :apply-exchange-rate="applyExchangeRate"
        :warning-banner="warningBanner"
        @clickCell="onClickChargeListCell"
      />
      -->
      <PrimeChargeListTable
        ref="chargeListTable"
        :charge-list="chargeList"
        :bill-list="billList"
        :selected-vendor="selectedVendor"
        :active-month-idx ="activeMonthIdx"
        :apply-exchange-rate="applyExchangeRate"
        @clickCell = "onClickChargeListCell"
      />
    </b-tab>
    <b-tab @click="changeTab(TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL)">
      <template slot="title">
        <p
          :class="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL ? 'font-family-notosanscjkkr-medium' : ''"
          class="font-14 py-10 text-paging-btn"
        >{{ $t(`billing.billingDetailTableTabs.cloudBillDetails`) }}</p>
      </template>
      <BillingDetailTableViewBy
        v-show="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL"
        :selected-vendor="selectedVendor"
        :selected-month-year="selectedMonthYear"
        :selected-view-by-option="selectedViewByOption"
      />
      <!--  cloud bill detail should be render after tab render for calculate columns width-->
      <!--
      <CloudBillDetail
        v-show="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL "
        ref="cloudBillDetails"
        :show-flag="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL"
        :row-data="groupDataInput"
        :is-viewed-by-tag="isViewedByTag"
        :number-of-layers="numberOfLayers"
        :table-width="tableWidth"
        :is-loading="isCloudBillDetailLoading"
        :warning-banner="warningBanner"
        :display-currency="displayCurrency"
        @onSearchList="onSearchList"
      />
      -->
      <PrimeCloudBillDetail
        v-show="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL "
        ref="cloudBillDetails"
        :show-flag="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_BILL_DETAIL"
        :row-data="groupDataInput"
        :is-viewed-by-tag="isViewedByTag"
        :number-of-layers="numberOfLayers"
        :table-width="tableWidth"
        :is-loading="isCloudBillDetailLoading"
        :warning-banner="warningBanner"
        :display-currency="displayCurrency"
        @onSearchList="onSearchList"
      />
    </b-tab>
    <!--<b-tab
      v-if="$store.state.loginUser.siteCd !== 'SCMP' && selectedVendor === 'AWS' && (Profile.env === 'PROD' || Profile.env === 'DEV')"
      @click="changeTab(TAB_INDEX[selectedVendor].CLOUD_INVOICE_INSIGHT)">
      <template slot="title">
        <div
          v-show="currentStep === 4"
          class="base-hotspot-container in-invoice-insight"
          @click="showHotspot(`#${INVOICE_INSIGHT_HOTSPOT}`)">
          <BaseHotspot
            :id="INVOICE_INSIGHT_HOTSPOT"
          />
        </div>
        <p
          :class="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_INVOICE_INSIGHT ? 'font-family-notosanscjkkr-medium' : ''"
          class="font-14 py-10 text-paging-btn"
        >{{ $t(`billing.billingDetailTableTabs.${selectedVendor.toLowerCase()}.cloudInvoiceInsight`) }}
          <base-material
            :id="invoiceInsightInfoToolTip"
            :size="12"
            color="gray-1"
            name="info"/>
        </p>
        <b-tooltip
          :target="invoiceInsightInfoToolTip"
          :custom-class="invoiceInsightInfoArea"
        >
          <div v-html="invoiceInsightInfoTooltip"/>
        </b-tooltip>
      </template>
      <InvoiceInsightLayout
        v-if="selectedTabIndex === TAB_INDEX[selectedVendor].CLOUD_INVOICE_INSIGHT"
        :invoice-insight-list="invoiceInsightList"
        :is-invoice-insight-list-loading="isInvoiceInsightListLoading"
        :is-invoice-insight-grid-loading="isInvoiceInsightGridLoading"
        :selected-month-year="selectedMonthYear"
        :active-month-idx="activeMonthIdx"
        :selected-vendor="selectedVendor"
        :apply-exchange-rate="applyExchangeRate"
        :warning-banner="warningBanner"
      />
    </b-tab>
    <b-tab
      v-if="TAB_INDEX[selectedVendor].SERVICE_GROUPS"
      @click="changeTab(TAB_INDEX[selectedVendor].SERVICE_GROUPS)">
      <template slot="title">
        <p
          :class="selectedTabIndex === TAB_INDEX[selectedVendor].SERVICE_GROUPS ? 'font-family-notosanscjkkr-medium' : ''"
          class="font-14 py-10 text-paging-btn"
        >{{ $t(`billing.billingDetailTableTabs.${selectedVendor.toLowerCase()}.serviceGroups`) }}</p>
      </template>
      <ServiceGroupsTable
        ref="serviceGroupsTable"
        :service-groups="serviceGroups"
        :selected-month-year="selectedMonthYear"
        :bill-list="billList"
        :active-month-idx="activeMonthIdx"
        :selected-vendor="selectedVendor"
        :apply-exchange-rate="applyExchangeRate"
      />
    </b-tab>
    <b-tab
      v-if="TAB_INDEX[selectedVendor].AUTOSPOT && $store.state.loginUser.siteCd !== 'SCMP' && hasAutoSpotData"
      @click="changeTab(TAB_INDEX[selectedVendor].AUTOSPOT)">
      <template slot="title">
        <p
          :class="selectedTabIndex === TAB_INDEX[selectedVendor].AUTOSPOT ? 'font-family-notosanscjkkr-medium' : ''"
          class="font-14 py-10 text-paging-btn"
        >{{ $t(`billing.billingDetailTableTabs.${selectedVendor.toLowerCase()}.autoSpot`) }}</p>
      </template>
      <AutoSpotListTable
        ref="autoSpotListTable"
        :auto-spot="autoSpot"
        :bill-list="billList"
        :selected-month-year="selectedMonthYear"
        :active-month-idx="activeMonthIdx"
        :selected-vendor="selectedVendor"
        :apply-exchange-rate="applyExchangeRate"
      />
    </b-tab>
    <b-tab
      v-if="TAB_INDEX[selectedVendor].COMPUTING_RESOURCES && (this.$store.state.loginUser.siteCd === 'DOOSAN' || this.$store.state.loginUser.curCmpnId === '1')"
      @click="changeTab(TAB_INDEX[selectedVendor].COMPUTING_RESOURCES)">
      <template slot="title">
        <p
          :class="selectedTabIndex === TAB_INDEX[selectedVendor].COMPUTING_RESOURCES ? 'font-family-notosanscjkkr-medium' : ''"
          class="font-14 py-10 text-paging-btn"
        >{{ $t(`billing.billingDetailTableTabs.${selectedVendor.toLowerCase()}.computingResources`) }}</p>
      </template>
      <ComputingResourcesTable
        ref="computingResourcesTable"
        :computing-resources="computingResources"
        :selected-month-year="selectedMonthYear"
        :bill-list="billList"
        :active-month-idx="activeMonthIdx"
        :selected-vendor="selectedVendor"
        :apply-exchange-rate="applyExchangeRate"
      />
    </b-tab>-->
  </b-tabs>
</template>

<script>
  import PrimeCloudBillDetail from "../table-result/PrimeCloudBillDetail";
  import BillingDetailTableViewBy from '../table-view-by/BillingDetailTableViewBy';
  // import ChargeListTable from './../table-result/ChargeListTable';
  import PrimeChargeListTable from "./../table-result/PrimeChargeListTable";
  // const CloudBillDetail = () => import('../table-result/CloudBillDetail');
  import BaseWarningBanner from "@/components/common/BaseWarningBanner";
  import BaseNoticeBanner from "@/components/common/BaseNoticeBanner";
  import { initWorkBookViaExcelJs, saveAndReturnDownloadFile } from '@/util/excelJS';
  import { VENDOR,CURRENCY } from '@/constants/constants';
  import Profile from '@/components/common/profile';
  import {
    VIEW_BY_OPTION_VALUE,
    DETAIL_TABLE_DOWNLOAD_OPTION_VALUE,
    DETAIL_TABLE_DOWNLOAD_OPTIONS,
    TAB_INDEX
  } from '@/constants/billingConstants';
  import { EXCEL_HEADER_TITLE } from '@/constants/excelConstants';
  import { mapGetters } from "vuex";
  import {
    groupDataByAccount,
    groupDataByInvoice,
    groupDataByRegion,
    groupDataByTag,
    groupDataByServiceGroup,
    prepareBillSummaryData,
    groupingByAccount,
    groupingByInvoice,
    groupingByRegion,
    customFormatCostValue,
    formatCostValue,
    getAllCodeToServiceFromChargeList
  } from '@/util/billingUtils';
  import { groupDataForExportCloudBillDetailsByView, prepareBillDetailExportData } from '@/util/exportUtils';
  import { fetchBillingDetailWithTag, fetchBillingDetailWithServiceGroup } from "@/api/billing";
  import { formatCost, exportCostsForNumber, exportUsagesForNumber } from '@/util/costUtils';
  import { SEND_INVOICE_OPTIONS, SEND_INVOICE_OPTION_VALUE, SEND_INVOICE_AVAILABLE_VENDORS } from "@/constants/billingConstants";
  import _cloneDeep from "lodash/cloneDeep";
  import _isNil from "lodash/isNil";
  import _ from "lodash";
  import {sortCrdtDetailNmAsc} from "../../../../../util/billingUtils";

  const CHARGE_LIST_HOTSPOT = 'charge-list-hotspot';
  const INVOICE_LIST_HOTSPOT = 'invoice-list-hotspot';
  const CLOUD_BILL_DETAIL_HOTSPOT = 'cloud-bill-detail-hotspot';
  const INVOICE_INSIGHT_HOTSPOT = 'invoice-insight-hotspot';
  const COMPUTING_RESOURCES_HOTSPOT = 'compute-resources-hotspot';
  const ADDITIONAL_SERVICES_HOTSPOT = 'additional-services-hotspot';

  export default {
    name: 'BillingDetailTableTabs',
    components: {
      PrimeCloudBillDetail,
      // CloudBillDetail,
      BaseWarningBanner,
      BaseNoticeBanner,
      BillingDetailTableViewBy,
      // ChargeListTable,
      PrimeChargeListTable,
    },
    props: {
      billList: {
        type: Array,
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
      chargeList: {
        type: Array,
        required: true
      },
      selectedVendor: {
        type: String,
        default: VENDOR.AWS,
      },
      selectedMonthYear: {
        type: Object,
        required: true
      },
      selectedTabIndex: {
        type: Number,
        default: 0
      },
      currentStep: {
        type: Number,
        required: true
      },
      hasAutoSpotData: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        cloudBillDetailText: '',
        cloudInvoiceListText: '',
        chargeListText: '',
        TAB_INDEX,
        detailTableDownload: {
          options: DETAIL_TABLE_DOWNLOAD_OPTIONS,
          showPopover: false,
          showEmailPopover: false,
          billingDetailTableDownloadBtnId: 'billing-detail-table-download-btn',
          placement: 'bottomleft',
          enabledLocalization: true
        },
        sendInvoice: {
          options: SEND_INVOICE_OPTIONS,
          showPopover: false,
          btnId: 'send-invoice-btn',
          iconName: 'email',
          placement: 'bottomleft',
          enabledLocalization: true
        },
        VENDOR : VENDOR,
        showTooltip: false,
        showEmailTooltip: false,
        tableWidth: 0,
        groupDataInput: [],
        groupDataExport: [],
        isCloudBillDetailLoading: false,
        CHARGE_LIST_HOTSPOT: CHARGE_LIST_HOTSPOT,
        INVOICE_LIST_HOTSPOT: INVOICE_LIST_HOTSPOT,
        CLOUD_BILL_DETAIL_HOTSPOT: CLOUD_BILL_DETAIL_HOTSPOT,
        INVOICE_INSIGHT_HOTSPOT: INVOICE_INSIGHT_HOTSPOT,
        COMPUTING_RESOURCES_HOTSPOT: COMPUTING_RESOURCES_HOTSPOT,
        ADDITIONAL_SERVICES_HOTSPOT: ADDITIONAL_SERVICES_HOTSPOT,
        invoiceInsightInfoToolTip: `invoice-insight-info-tooltip`,
        invoiceInsightInfoArea: `invoice-insight-info-area`,
        isClosedNoticeBanner: false,
        displayCurrency: '',
        Profile,
        showInvoiceInsightBanner: false,
        searchText:'',
        showSendInvoiceModal: false,
        invoiceSendType: null
      };
    },
    computed: {
      ...mapGetters({
        chargeTableState: 'billing/chargeTableState',
        invoiceList : 'billing/invoiceList',
        billingDetail: 'billing/billingDetail',
        selectedViewByOption: 'billing/selectedViewByOption',
        selectedTag: 'billing/cloudBilDetailsSelectedTagKey',
        tagKeys: 'billing/cloudBilDetailsTagKeys',
        selectedServiceGroup: 'billing/cloudBilDetailsSelectedSvgSet',
        serviceGroupSets: 'billing/cloudBilDetailsSvgSets',
        invoiceInsightList: 'billing/invoiceInsightList',
        isInvoiceInsightListLoading: 'billing/isInvoiceInsightListLoading',
        isInvoiceInsightGridLoading: 'billing/isInvoiceInsightGridLoading',
        computingResources: 'billing/computingResources',
        additionalServices: 'billing/additionalServices',
        serviceGroups: 'billing/serviceGroups',
        invoiceCurrency: 'billing/invoiceCurrency',
        companyCurrency: 'billing/companyCurrency',
        autoSpot: 'billing/autoSpot',
      }),
      selectedChargeListItem() {
        // console.log('this.chargeList 11',this.chargeList);
        let result;
        result = _cloneDeep(mergeChargeListItems(this.chargeList));
        // if (this.chargeList.length <= 1) {
        //   result = _cloneDeep(this.chargeList[0]);
        // } else {
        //   result = _cloneDeep(mergeChargeListItems(this.chargeList));
        // }
        // console.log('result 123', result)
        let cost;
        if(result){
          if(result.companyCurrency === CURRENCY.KRW){
            if(this.selectedVendor==='GCP'){
              if(result.totalCharge >= 0) cost = Math.floor(result.totalCharge);
              else{
                cost = Math.floor(Math.abs(result.totalCharge));
                cost = cost * -1;
              }

              if(!_.isEmpty(result.creditDetails)) {
                Object.assign(result.creditDetails,sortCrdtDetailNmAsc(result.creditDetails, this));
              }

            }else{
              cost = Math.round(result.totalCharge);
            }
          }else{
            let addtionalCost = 0;
            if(result.additionalServices !== undefined){
              result.additionalServices.forEach( service => {
                addtionalCost += service.additionalServiceCharge
              })
            }
            cost = result.cloudServiceCharge + addtionalCost;
          }
          result.totalCharge = cost;
        }
        return result;

      },
      numberOfLayers: function () {
        switch (this.selectedViewByOption) {
          case VIEW_BY_OPTION_VALUE.REGION:
            return 4;
          /*case VIEW_BY_OPTION_VALUE.TAG:
            return 6;*/
          default:
            return 5
        }
      },
      isViewedByTag: function() {
        return VIEW_BY_OPTION_VALUE.TAG === this.selectedViewByOption;
      },
      exportData: function(){
        let groupBill = [];
        switch(this.selectedViewByOption) {
          case VIEW_BY_OPTION_VALUE.ACCOUNT:
            groupBill = this.exportDataFilter(prepareBillDetailExportData(this.billingDetail, this.selectedViewByOption));
            break;
          case VIEW_BY_OPTION_VALUE.INVOICE:
            groupBill = this.exportDataFilter(prepareBillDetailExportData(this.billingDetail, this.selectedViewByOption));
            break;
          case VIEW_BY_OPTION_VALUE.REGION:
            groupBill = this.exportDataFilter(prepareBillDetailExportData(this.billingDetail, this.selectedViewByOption));
            break;
          case VIEW_BY_OPTION_VALUE.TAG:
            groupBill = this.exportDataFilter(this.groupDataInput);
            break;
          case VIEW_BY_OPTION_VALUE.SERVICEGROUP:
            groupBill = this.exportDataFilter(this.groupDataInput);
            break;
          default:
            groupBill = this.exportDataFilter(prepareBillDetailExportData(this.billingDetail, this.selectedViewByOption));
            break;
        }

        return groupDataForExportCloudBillDetailsByView(groupBill, this.selectedViewByOption)
      },
      warningBanner: function () {
        let message = '';
        let opened = false;
        if (this.chargeTableState.cloudBillDetails.displayedWarning && this.selectedTabIndex === this.TAB_INDEX[this.selectedVendor].CLOUD_BILL_DETAIL) {
          message = this.$t("billing.billingSummary.cloudBillDetailBanner");
          opened = true;
        } else if (this.chargeTableState.cloudInvoiceList.displayedWarning && this.selectedTabIndex === this.TAB_INDEX[this.selectedVendor].CLOUD_INVOICE_LIST) {
          message = this.$t("billing.billingSummary.cloudInvoiceBanner");
          opened = true;
        } else if (this.chargeTableState.cloudInvoiceInsight.displayedWarning && this.showInvoiceInsightBanner) {
          message = this.$t("billing.billingSummary.cloudInvoiceInsightBanner");
          opened = true;
        }

        return {
          opened: opened,
          message: message,
        }
      },
      noticeBanner: function() {
        let message = this.$t("billing.billingSummary.invoiceInsightNoData");
        let opened = true;
        return {
          opened: opened,
          message: message,
        }
      },
      invoiceInsightInfoTooltip: function () {
        return `<div class="text-left">
            <p>${ this.$t("billing.invoiceInsight.tooltip.currentMonth") }</p>
            <p>${ this.$t("billing.invoiceInsight.tooltip.lastMonth") }</p>
            <p>${ this.$t("billing.invoiceInsight.tooltip.nonDataInLastMonth") }</p>
            <p>${ this.$t("billing.invoiceInsight.tooltip.DataInLastMonth") }</p>
        </div>`
      },
      detailTableDownloadOptions: function() {
        return DETAIL_TABLE_DOWNLOAD_OPTIONS.filter(option => {
          if(option.availableVendors.includes(this.selectedVendor)){
            if(option.value == 'COMPUTING_RESOURCES'){
              if(this.$store.state.loginUser.siteCd === 'DOOSAN' || this.$store.state.loginUser.curCmpnId === '1') return option;
              else return;
            }
            if(option.value == 'AUTO_SPOT' && !this.hasAutoSpotData) return;
            return option;
          }
        })
      }
    },
    watch: {
      // tagKeys: {
      //   handler() {
      //     console.log('handler tagKeys ')
      //     if (this.selectedViewByOption === VIEW_BY_OPTION_VALUE.TAG) {
      //       //this.prepareCloudBillingDetailDataWithTag();
      //       return
      //     }
      //     //this.isCloudBillDetailLoading = true
      //   }
      // },
      // serviceGroupSets: {
      //   handler() {
      //     if (this.selectedViewByOption === VIEW_BY_OPTION_VALUE.SERVICEGROUP) {
      //       this.prepareCloudBillingDetailDataWithServiceGroup();
      //     }
      //     //this.isCloudBillDetailLoading = true
      //   }
      // },
      selectedMonthYear: {
        handler() {
          if(this.selectedMonthYear.chargeYear){
            this.getTabData(this.selectedTabIndex);
            this.initTabData();
          }
        }
      },
      selectedVendor: {
        handler() { // 벤더 변경할 때 상세 그리드 로딩 처리
          this.$store.dispatch('billing/setSelectedTabIndex', TAB_INDEX[this.selectedVendor].CHARGE_LIST);
        }
      },
      billingDetail: {
        handler() { // BillingLayout 의 'billing/getBillingDetail' 응답이 끝난 후 이벤트 시작
          this.prepareTreeTableInputData(false);
        }
      },
      selectedViewByOption: {
        handler() { // 벤더 변경 후 selectedOption 의 디폴트 처리된 값일때, 상세 그리드 로딩 처리
          if(this.selectedViewByOption === ""){
            this.isCloudBillDetailLoading = true;
            return
          }

          this.prepareTreeTableInputData(false);
        }
      },
      selectedTag: function () {
        if (this.selectedViewByOption === VIEW_BY_OPTION_VALUE.TAG) {
          this.prepareCloudBillingDetailDataWithTag();
        }
      },
      selectedServiceGroup: function () {
        if (this.selectedViewByOption === VIEW_BY_OPTION_VALUE.SERVICEGROUP  ) {
          this.prepareCloudBillingDetailDataWithServiceGroup();
        }
      },
      invoiceCurrency:{
        handler() {
          if(this.displayCurrency === this.invoiceCurrency) {
            this.displayCurrency = ''
          }
          this.displayCurrency = this.invoiceCurrency
        },
        immediate: true
      },
      companyCurrency:{
        handler() {

        },
        immediate: true
      }
    },
    mounted() {
      this.$root.$on('bv::tooltip::show', bvEvent => {
        if (this.detailTableDownload.showPopover === true) {
          this.$root.$emit('bv::hide::tooltip')
          bvEvent.preventDefault()
        }
      })
      this.$root.$on('bv::popover::hide', bvEvent => {
        this.detailTableDownload.showPopover = false
      })
      this.$root.$on('bv::popover::show', bvEventObj => {
        this.showTooltip = false
      })
      //get width of *billing-detail-table-tabs* and apply for tree table, because tree table width ~~ table-tabs width
      this.tableWidth = document.getElementById("billing-detail-table-tabs").offsetWidth - 9;
    },
    methods: {
      initTabData() {
        /***
         * 화면 렌더링 시 탭 전환없이 선택되지 않은 탭의 파일 다운로드가 정상 보이기 위해 실행
         * @Value gridData
         */
        let payload = {
          selectedVendor : this.selectedVendor,
          chargeYear : this.selectedMonthYear.chargeYear,
          chargeMonth : this.selectedMonthYear.chargeMonth,
          yearMonth : this.selectedMonthYear.chargeYear + '-' + this.selectedMonthYear.chargeMonth,
        };

        if(this.selectedViewByOption === 'TAG'){
          this.prepareCloudBillingDetailDataWithTag();
        }else{
          this.$store.dispatch('billing/getBillingDetail', payload);
        }


        if(this.selectedVendor == VENDOR.AWS){
          if(this.$store.state.loginUser.siteCd === 'DOOSAN' || this.$store.state.loginUser.curCmpnId === '1'){
            // this.$store.dispatch('billing/getComputingResources', payload);
          }
          // this.$store.dispatch('billing/getServiceGroups', payload);
          // this.$store.dispatch('billing/getAutoSpot', payload);
        }
      },
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
      prepareTreeTableInputData(canGroupByData) {
        let $vm = this;
        switch (this.selectedViewByOption) {
          case VIEW_BY_OPTION_VALUE.ACCOUNT:
            this.isCloudBillDetailLoading = true;
            setTimeout(function () {
              //$vm.treeTableInput = groupDataByAccount($vm.billingDetail, $vm);
              $vm.groupDataInput = groupingByAccount($vm.billingDetail, $vm);
              $vm.isCloudBillDetailLoading = false;
            }, 500);
            break;
          case VIEW_BY_OPTION_VALUE.INVOICE:
            this.isCloudBillDetailLoading = true;
            setTimeout(function () {
              //$vm.treeTableInput = groupDataByInvoice($vm.billingDetail, $vm);
              $vm.groupDataInput = groupingByInvoice($vm.billingDetail)
              $vm.isCloudBillDetailLoading = false;
            }, 500);
            break;
          case VIEW_BY_OPTION_VALUE.REGION:
            this.isCloudBillDetailLoading = true;
            setTimeout(function () {
              //$vm.treeTableInput = groupDataByRegion($vm.billingDetail, $vm);
              $vm.groupDataInput = groupingByRegion($vm.billingDetail);
              $vm.isCloudBillDetailLoading = false;
            }, 500);
            break;
          case VIEW_BY_OPTION_VALUE.TAG:
            this.isCloudBillDetailLoading = true;
            if (canGroupByData) {
              this.prepareCloudBillingDetailDataWithTag();
            }
            break;
          case VIEW_BY_OPTION_VALUE.SERVICEGROUP:
            this.isCloudBillDetailLoading = true;
            if (canGroupByData) {
              this.prepareCloudBillingDetailDataWithServiceGroup();
            }
            break;
          default:
            this.isCloudBillDetailLoading = true;
            setTimeout(function () {
              // $vm.treeTableInput = groupDataByAccount($vm.billingDetail, $vm);
              $vm.groupDataInput = groupingByAccount($vm.billingDetail, $vm);
              $vm.isCloudBillDetailLoading = false;
            }, 500);
            break;
        }
      },
      onCloseBanner() {
        this.$store.dispatch('billing/setDisplayedWarningBanner', this.tabIndex);
      },
      onCloseNoticeBanner() {
        this.isClosedNoticeBanner = true;
      },
      changeTab(selectedTab) {
        this.isClosedNoticeBanner = false;
        this.$store.dispatch('billing/setSelectedTabIndex', selectedTab);
        this.getTabData(selectedTab);
      },
      getTabData(selectedTab) {
        let payload = {
          selectedVendor : this.selectedVendor,
          chargeYear : this.selectedMonthYear.chargeYear,
          chargeMonth : this.selectedMonthYear.chargeMonth,
          yearMonth : this.selectedMonthYear.chargeYear + '-' + this.selectedMonthYear.chargeMonth,
        };
        this.showInvoiceInsightBanner = false;

        switch (selectedTab) {
          case TAB_INDEX[this.selectedVendor].CHARGE_LIST:
            this.$store.dispatch('billing/getChargeList', payload);
            break;
          case TAB_INDEX[this.selectedVendor].CLOUD_BILL_DETAIL:
            this.$store.dispatch('billing/getBillingDetail', payload);
            break;
          // case TAB_INDEX[this.selectedVendor].CLOUD_INVOICE_INSIGHT:
          //   this.$store.dispatch('billing/getInvoiceInsightList', payload);
          //   this.showInvoiceInsightBanner = true;
          //   break;
          // case TAB_INDEX[this.selectedVendor].COMPUTING_RESOURCES:
          //   this.$store.dispatch('billing/getComputingResources', payload);
          //   break;
          case TAB_INDEX[this.selectedVendor].ADDITIONAL_SERVICES:
            this.$store.dispatch('billing/getAdditionalServices', payload);
            break;
          // case TAB_INDEX[this.selectedVendor].SERVICE_GROUPS:
          //   this.$store.dispatch('billing/getServiceGroups', payload);
          //   break;
          // case TAB_INDEX[this.selectedVendor].AUTOSPOT:
          //   this.$store.dispatch('billing/getAutoSpot', payload);
          //   break;
          default :
            break;
        }
      },
      onSelectDetailTableDownloadOption(selectedOption) {
        switch (selectedOption) {
          case DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CHARGE_LIST_ON_CURRENT_VIEW:
            this.$refs.chargeListTable.exportChargeList()
            break;
          case DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CLOUD_INVOICE_LIST_ON_CURRENT_VIEW:
            this.$refs.invoiceListTable.exportCloudInvoiceListCsv();
            break;
          case DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CLOUD_BILL_DETAILS:
            this.exportCloudBillDetail()
            break;
          case DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.COMPUTING_RESOURCES:
            this.$refs.computingResourcesTable.exportComputingResources()
            break;
          case DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.SERVICE_GROUP:
            this.$refs.serviceGroupsTable.exportServiceGroup();
            break;
          case DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.AUTO_SPOT:
            this.$refs.autoSpotListTable.exportAutoSpot();
            break;
          default:
          // invalid selectedOption
        }
      },
      onSelectSendInvoiceType(selectedOption) {
        this.showSendInvoiceModal = !this.showSendInvoiceModal;

        if(selectedOption === SEND_INVOICE_OPTION_VALUE.SEND_ONCE){
          this.invoiceSendType = 'N';
        }else if(selectedOption === SEND_INVOICE_OPTION_VALUE.SEND_REPEATEDLY){
          this.invoiceSendType = 'Y';
        }
      },
      prepareCloudBillingDetailDataWithTag: function () {
        let payload = {
          chargeMonth: this.selectedMonthYear.chargeMonth,
          chargeYear: this.selectedMonthYear.chargeYear,
          tagKey: this.selectedTag,
          selectedVendor: this.selectedVendor, // payload : selectedVendor > fetch request : vendor
          viewBy: VIEW_BY_OPTION_VALUE.TAG
        };
        this.isCloudBillDetailLoading = true;
        fetchBillingDetailWithTag(payload).then(response => {
          this.isCloudBillDetailLoading = false;
          //this.treeTableInput = groupDataByTag(response, this.selectedTag, this);
          this.groupDataInput = groupDataByTag(response, this.selectedTag, this);
        }).catch(() => {
          this.isCloudBillDetailLoading = false;
        });
      },
      prepareCloudBillingDetailDataWithServiceGroup: function () {
        let payload = {
          chargeMonth: this.selectedMonthYear.chargeMonth,
          chargeYear: this.selectedMonthYear.chargeYear,
          serviceGroupSetNm : this.selectedServiceGroup,
          selectedVendor: this.selectedVendor, // payload : selectedVendor > fetch request : vendor
          viewBy: VIEW_BY_OPTION_VALUE.SERVICEGROUP
        };
        this.isCloudBillDetailLoading = true;
        fetchBillingDetailWithServiceGroup(payload).then(response => {
          this.isCloudBillDetailLoading = false;
          //this.treeTableInput = groupDataByServiceGroup(response, this.selectedServiceGroup, this);
          this.groupDataInput = groupDataByServiceGroup(response, this.selectedServiceGroup, this);
        }).catch(() => {
          this.isCloudBillDetailLoading = false;
        });
      },
      togglePopoverTooltip() {
        this.detailTableDownload.showPopover = !this.detailTableDownload.showPopover
        this.showTooltip = false
      },
      toggleEmailPopoverTooltip() {
        this.detailTableDownload.showEmailPopover = !this.detailTableDownload.showEmailPopover
        this.showEmailTooltip = false
      },
      hideSendInvoiceModal() {
        this.showSendInvoiceModal = !this.showSendInvoiceModal;
      },
      exportCloudBillDetail() {
        /**Prepare dummy data*/
        let totalAmount = ''
        let currencyType = "("+`${this.invoiceCurrency}`+")"
        if (this.displayCurrency) { // OCI 케이스
          totalAmount = customFormatCostValue(this.exportData.totalAmount, null , this.invoiceCurrency) + currencyType;
        } else {
          totalAmount = formatCost(this.exportData.totalAmount, {mantissa:8}) + currencyType;
        }
        const accountList = this.getAccountListForCloudBillDetails(); // row data
        //const totalAmount = formatCost(this.exportData.totalAmount) + "(USD)"; // 기존로직

        /**Init workbook via excel js library*/
        let workbook = new this.$excel.Workbook();
        initWorkBookViaExcelJs(workbook);
        // Get & Write export file name with billed date
        const billingDetail =  prepareBillSummaryData(this.activeMonthIdx, this.billList);
        //const excelFileName = `CloudBillDetails_${billingDetail.chargeYear}${billingDetail.chargeMonth}`;
        const excelFileName = `${this.$t('billing.cloudBillDetails.cloudBillDetails')}_${billingDetail.chargeYear}${billingDetail.chargeMonth}`;
        let worksheet = workbook.addWorksheet(excelFileName, {views:[{showGridLines:false}]});

        /**Set width for columns*/
        worksheet.columns = [
          { key: 'A', width: 20 },
          { key: 'B', width: 24 },
          { key: 'C', width: 24 },
          { key: 'D', width: 24 },
          { key: 'E', width: 24 },
          { key: 'F', width: 0 },
          { key: 'G', width: 0 },
          { key: 'H', width: 0 },
          { key: 'I', width: 15 },
          { key: 'J', width: 20 }
        ];

        // 최초 디폴트 상태에서 보기 or Account 보기에서만 컬럼 너비 넓히기
        if(this.selectedViewByOption === VIEW_BY_OPTION_VALUE.ACCOUNT || this.selectedViewByOption === ''){
          worksheet.columns[0].width =24
        }

        const headerTitle = EXCEL_HEADER_TITLE[this.selectedVendor];
        let row = worksheet.addRow([headerTitle]);
        row.height = 35;

        /**Set font & border for header cell*/
        const headerCell = worksheet.getCell('A1');
        headerCell.font = {
          name: 'Arial',
          size: 14,
          bold: true
        };
        headerCell.alignment = { vertical: 'middle' };

        /**Add empty row*/
        worksheet.addRow([]);

        /**Add invoice summary*/
        const invoiceSummaryRowTitle =`${this.$t('billing.cloudBillDetails.invoiceSummary')}` ;
        worksheet.addRow([invoiceSummaryRowTitle]);

        let cloudAccountHeaderName = '';
        if(this.selectedVendor == VENDOR.AZURE){
          cloudAccountHeaderName = `${this.$t('billing.cloudBillDetails.cloudSubscription')}`;
        } else if (this.selectedVendor == VENDOR.GCP) {
          cloudAccountHeaderName = `${this.$t('billing.cloudBillDetails.accountIdGcp')}`;
        } else if (this.selectedVendor == VENDOR.OCI) {
          cloudAccountHeaderName = `${this.$t('billing.cloudBillDetails.compartmentId')}`;
        } else {
          cloudAccountHeaderName = `${this.$t('billing.cloudBillDetails.accountId')}`;
        }

        /**Populate accounts to invoice summary*/
        accountList.forEach(item => {
          let headerRowByView = []
          //let subTotal = formatCost(item.sub_total,{mantissa:8}) // 기존코드
          let subTotal = ''
          if (this.displayCurrency) { // OCI 케이스
            subTotal = formatCostValue(item.sub_total, null, this.invoiceCurrency,this.selectedVendor);
          } else {
            subTotal = formatCost(item.sub_total,{mantissa:8})
          }

          switch(this.selectedViewByOption){
            case VIEW_BY_OPTION_VALUE.ACCOUNT:
              headerRowByView = [`${cloudAccountHeaderName}: ${item.account_id}`, '', '', '', '', '', '', '', '', subTotal]
              break;
            case VIEW_BY_OPTION_VALUE.INVOICE:
              headerRowByView = [`${this.$t('billing.cloudBillDetails.invoiceId')}: ${item.invoice_id}`, '', '', '', '', '', '', '', '', subTotal]
              break
            case VIEW_BY_OPTION_VALUE.REGION:
              headerRowByView = [`${this.$t('billing.cloudBillDetails.regionName')}: ${item.region_name}`, '', '', '', '', '', '', '', '', subTotal]
              break;
            case VIEW_BY_OPTION_VALUE.TAG:
              headerRowByView = [`${this.$t('billing.cloudBillDetails.tagValue')}: ${item.tag_value}`, '', '', '', '', '', '', '', '', subTotal]
              break;
            case VIEW_BY_OPTION_VALUE.SERVICEGROUP:
              headerRowByView = [`${this.$t('billing.cloudBillDetails.serviceGroup')}: ${item.service_group}`, '', '', '', '', '', '', '', '', subTotal]
              break;
            default:
              headerRowByView = [`${cloudAccountHeaderName}: ${item.account_id}`, '', '', '', '', '', '', '', '', subTotal]
              break;
          }

          worksheet.addRow(headerRowByView);
        });

        worksheet.addRow([this.$t('billing.cloudBillDetails.totalAmount'), '', '', '', '', '', '', '', '', totalAmount ]);

        /**Add empty row*/
        worksheet.addRow([]);
        worksheet.addRow([this.$t('billing.cloudBillDetails.detailedUsageCharges')]);

        const columns = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];
        const invoiceSummaryRowIndex = 3;
        let invoiceSummaryStartIndex = invoiceSummaryRowIndex + 1;

        const totalMountRowIndex = accountList.length + invoiceSummaryStartIndex;
        const detailedUsageChargesTitleIndex = totalMountRowIndex + 2;

        columns.forEach(column => {
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}1`).border = {
            bottom: {style: 'medium', color: {argb:'16365C'}},
          };
        });

        //Set style for accounts of invoice summary
        for (let i = invoiceSummaryStartIndex; i < accountList.length + invoiceSummaryStartIndex; i ++) {
          this.setStyleToDisplayAccount(worksheet, columns, i);
          worksheet.getCell(`A${i}`).font = {
            name: 'Arial',
            bold: true,
            size: 10
          };
          worksheet.getCell(`J${i}`).alignment = {
            horizontal: 'right'
          };
        }
        this.setStyleOfCloudBillDetailsTitleForCell(worksheet, columns, invoiceSummaryRowIndex);
        this.setStyleOfCloudBillDetailsTitleForCell(worksheet, columns, detailedUsageChargesTitleIndex);
        this.setStyleForTotalMountRow(worksheet, columns, totalMountRowIndex);

        let accountStartIndex = detailedUsageChargesTitleIndex + 1;
        accountList.forEach(account => {
          this.populateDetailUsageChargesForOneAccount(worksheet, account, accountList, columns, accountStartIndex);
          const bonusRowIndex = 4;//account id row, header row, sub total row and blank row
          accountStartIndex = accountStartIndex + account.detail_usage_charges.length + bonusRowIndex;
        });

        /**Write data to file and then save & download the file*/
        saveAndReturnDownloadFile(workbook, excelFileName);
      },
      getAccountListForCloudBillDetails() {
        return this.exportData.row;
      },
      populateDetailUsageChargesForOneAccount(worksheetAccount, account, accountList, columns, accountStartIndex) {
        let worksheet = worksheetAccount;
        let viewColumn = [];
        let rowColumn = [];
        let accountLabelView = this.$t('billing.cloudBillDetails.accountId');
        let accountLabel = this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.accountId');

        if(this.selectedVendor == VENDOR.AZURE){
          accountLabelView = `${this.$t('billing.cloudBillDetails.cloudSubscription')}`;
          accountLabel = accountLabelView;
        } else if(this.selectedVendor == VENDOR.GCP){
          accountLabelView = `${this.$t('billing.cloudBillDetails.accountIdGcp')}`;
          accountLabel = `${this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.accountIdGcp')}`;
        } else if(this.selectedVendor == VENDOR.OCI){
          accountLabelView = `${this.$t('billing.cloudBillDetails.compartmentId')}`;
          accountLabel = `${this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.compartmentId')}`;
        }
        switch(this.selectedViewByOption) {
          case VIEW_BY_OPTION_VALUE.ACCOUNT:

            viewColumn = [`${accountLabelView}: ${account.account_id}`];
            rowColumn = [
              accountLabel,
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.productName'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.region'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useType'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.description'),
              '','','',
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useQnt'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.totalCost')
            ]

            break;
          case VIEW_BY_OPTION_VALUE.INVOICE:

            viewColumn = [`${account.invoice_id}`];
            rowColumn = [
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.invoiceId'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.productName'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.region'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useType'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.description'),
              '','','',
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useQnt'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.totalCost')
            ]

            break;
          case VIEW_BY_OPTION_VALUE.REGION:

            viewColumn = [`${account.region_name}`];
            rowColumn = [
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.region'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.productName'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useType'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.description'),
              '','','','',
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useQnt'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.totalCost')
            ]

            break;
          case VIEW_BY_OPTION_VALUE.TAG:

            viewColumn = [`${account.tag_value}`];
            rowColumn = [
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.tagValue'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.productName'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.region'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useType'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.description'),
              '','','',
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useQnt'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.totalCost')
            ]

            break;
          case VIEW_BY_OPTION_VALUE.SERVICEGROUP:

            viewColumn = [`${account.service_group}`];
            rowColumn = [
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.serviceGroup'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.productName'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.region'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useType'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.description'),
              '','','',
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useQnt'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.totalCost')
            ]

            break;
          default:

            viewColumn = [`${accountLabelView}: ${account.account_id}`];
            rowColumn = [
              accountLabel,
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.productName'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.region'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useType'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.description'),
              '','','',
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.useQnt'),
              this.$t('billing.cloudBillDetails.download.detailedUsageChargesTitle.totalCost')
            ]

            break;
        }

        worksheet.addRow(viewColumn);
        worksheet.addRow(rowColumn);

        for (let i = accountStartIndex; i < accountStartIndex + 2; i ++) {
          this.setStyleToDisplayAccount(worksheet, columns, i);
        }
        //Set height for column header of detailed usage charges
        let row = worksheet.getRow(accountStartIndex + 1);
        row.height = 18;

        columns.forEach(column => {
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${accountStartIndex}`).border = {
            top: {style: 'medium', color: {argb:'16365C'}},
            bottom: {style: 'dotted'},
          };
        });

        let subTotalForDetail = ''
        if (this.displayCurrency) {
          subTotalForDetail = customFormatCostValue(account.sub_total, null, this.invoiceCurrency)
        } else {
          subTotalForDetail = formatCost(account.sub_total,{mantissa:8})
        }

        //GCP인 경우 소수점 버림으로 처리
        if(this.selectedVendor == VENDOR.GCP){
          subTotalForDetail = formatCostValue(account.sub_total, '', this.invoiceCurrency, 'GCP');
        }

        //let subTotalForDetail = formatCost(account.sub_total,{mantissa:8}) // 기존 코드
        account && account.detail_usage_charges && account.detail_usage_charges.forEach(item => {
          let detailList = [];
          let useQnt = exportUsagesForNumber(item.use_qnt)
          //let totalCost = exportCostsForNumber(item.total_cost) // 기존 코드
          let totalCost = ''
          if (this.displayCurrency) {
            if(this.selectedVendor == VENDOR.GCP){
              totalCost = formatCostValue(item.total_cost, '', this.invoiceCurrency, 'GCP');
            }else{
              totalCost = customFormatCostValue(item.total_cost, null, this.invoiceCurrency)
            }
          } else {
            if(this.selectedVendor == VENDOR.GCP){
              totalCost = formatCostValue(item.total_cost, '', this.invoiceCurrency, 'GCP');
            }else {
              totalCost = exportCostsForNumber(item.total_cost)
            }
          }

          switch(this.selectedViewByOption) {
            case VIEW_BY_OPTION_VALUE.ACCOUNT: // formatCost(item.use_qnt) // formatCost(item.total_cost)
              detailList = [item.account_id, item.product_name, item.region, item.use_type, item.description, '', '', '', useQnt, totalCost];
              break;
            case VIEW_BY_OPTION_VALUE.INVOICE:
              detailList = [item.invoice_id, item.product_name, item.region, item.use_type, item.description, '', '', '', useQnt, totalCost];
              break;
            case VIEW_BY_OPTION_VALUE.REGION:
              detailList = [item.region_name, item.product_name, item.use_type, item.description, '', '', '', '', useQnt, totalCost];
              break;
            case VIEW_BY_OPTION_VALUE.TAG:
              detailList = [item.tag_value, item.product_name, item.region, item.use_type, item.description, '', '', '', useQnt, totalCost];
              break;
            case VIEW_BY_OPTION_VALUE.SERVICEGROUP:
              detailList = [item.service_group, item.product_name, item.region, item.use_type, item.description, '', '', '', useQnt, totalCost];
              break;
            default:
              detailList = [item.account_id, item.product_name, item.region, item.use_type, item.description, '', '', '', useQnt, totalCost];
              break;
          }
          worksheet.addRow(detailList);
        });
        worksheet.addRow(['', '', '', '', '', '', '', this.$t('billing.cloudBillDetails.subTotal'), '', subTotalForDetail]);
        worksheet.addRow([]);

        for (let i = accountStartIndex; i < accountStartIndex + account.detail_usage_charges.length + 3; i ++) {
          columns.forEach(column => {
            // eslint-disable-next-line no-param-reassign
            worksheet.getCell(`${column}${i}`).font = {
              name: 'Arial',
              size: 10,
            };
          });
          //Set height for row without account id
          if (i > accountStartIndex) {
            let row = worksheet.getRow(i);
            row.height = 18;
            ['I', 'J'].map(column => {
              // eslint-disable-next-line no-param-reassign
              return worksheet.getCell(`${column}${i}`).alignment = {
                horizontal: 'right'
              };
            });
          }
        }

        worksheet.getCell(`A${accountStartIndex}`).font = {
          name: 'Arial',
          bold: true,
          size: 10
        };

        const subTotalRowIndex = accountStartIndex + account.detail_usage_charges.length + 2;
        this.setStyleForSubTotalRow(worksheet, columns, subTotalRowIndex);
      },
      setStyleForTotalMountRow(worksheet, columns, totalMountRowIndex) {
        // eslint-disable-next-line no-param-reassign
        worksheet.getCell(`J${totalMountRowIndex}`).font = {
          name: 'Arial',
          bold: true,
          size: 10
        };
        // eslint-disable-next-line no-param-reassign
        worksheet.getCell(`J${totalMountRowIndex}`).alignment = {
          horizontal: 'right'
        };
        // eslint-disable-next-line no-param-reassign
        worksheet.getCell(`A${totalMountRowIndex}`).font = {
          name: 'Arial',
          size: 10
        };
        //Set bottom border for total sub_total
        columns.map(column => {
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${totalMountRowIndex}`).border = {
            bottom: {style: 'medium', color: {argb:'16365C'}},
          };
        });
      },
      setStyleForSubTotalRow(worksheet, columns, subTotalRowIndex) {
        columns.forEach(column => {
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${subTotalRowIndex}`).fill = {
            type: 'pattern',
            pattern:'solid',
            fgColor:{argb:'F2F3F2'}
          };
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${subTotalRowIndex}`).border = {
            top: {style:'dotted'},
            bottom: {style: 'medium', color: {argb:'16365C'}},
          };
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${subTotalRowIndex}`).font = {
            name: 'Arial',
            size: 10,
          };
        });
        ['H', 'J'].map(column => {
          // eslint-disable-next-line no-param-reassign
          return worksheet.getCell(`${column}${subTotalRowIndex}`).font = {
            name: 'Arial',
            bold: true,
            size: 10
          };
        });
        // eslint-disable-next-line no-param-reassign
        worksheet.getCell(`J${subTotalRowIndex}`).alignment = {
          horizontal: 'right'
        };
      },
      setStyleToDisplayAccount(worksheet, columns, rowIndex) {
        columns.forEach(column => {
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${rowIndex}`).fill = {
            type: 'pattern',
            pattern:'solid',
            fgColor:{argb:'F2F3F2'}
          };
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${rowIndex}`).border = {
            bottom: {style:'dotted'},
          };
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${rowIndex}`).font = {
            name: 'Arial',
            size: 10,
          };
        });
        //Set height for title
        let row = worksheet.getRow(rowIndex);
        row.height = 26;
      },
      setStyleOfCloudBillDetailsTitleForCell(worksheet, columns, rowIndex) {
        columns.map(column => {
          // eslint-disable-next-line no-param-reassign
          worksheet.getCell(`${column}${rowIndex}`).fill = {
            type: 'pattern',
            pattern:'solid',
            fgColor:{argb:'16365C'}
          };
        });
        // eslint-disable-next-line no-param-reassign
        worksheet.getCell(`A${rowIndex}`).font = {
          name: 'Arial',
          size: 10,
          color: { argb: 'FFFFFF' },
          bold: true
        };
        //Set height for title
        let row = worksheet.getRow(rowIndex);
        row.height = 26;
      },
      onClickChargeListCell(params) {
        this.$emit('clickChargeListCell', params);
      },
      onSearchList(params) {
        this.searchText = params;
      },
      exportDataFilter: function(exportDataList){

        return _.filter(exportDataList, _.flow(
          _.partial(
            _.some, _,
            _.flow(_.toLower, _.partial(_.includes, _, _.toLower(this.searchText), 0))
          )
        ));
      }
    },
  };
  function mergeChargeListItems(chargeListItems) {
    const allCodeToService = getAllCodeToServiceFromChargeList(chargeListItems);
    let codeToArrIdx = {};
    Object.keys(allCodeToService).forEach((code, idx) => {
      codeToArrIdx[code] = idx;
    });
    // init model
    let mergedItem;
    if(chargeListItems && chargeListItems.length > 0){
      mergedItem = {
        linkedAccountId: null,
        linkedAccountAlias: null,
        chargeYear: chargeListItems[0].chargeYear,
        chargeMonth: chargeListItems[0].chargeMonth,
        totalCharge: 0,
        invoiceCurrency: chargeListItems[0].invoiceCurrency,
        companyCurrency: chargeListItems[0].companyCurrency,
        cloudCost: 0,
        cloudOriginalCost: 0,
        onDemandDiscount: 0,
        cloudFrontDiscount: 0,
        cloudFrontDtoDiscount: 0,
        cloudFrontReqDiscount: 0,
        cloudServiceCharge: 0,
        exchangedCloudServiceCharge: 0,
        salesDiscount: 0,
        ncpDiscount: 0,
        salesDiscountApplyValue: null,
        supportFee: 0,
        supportFeeApplyValue: null,
        credit: 0,
        vatCost: 0,
        additionalServices: _cloneDeep(Object.values(allCodeToService)),
        additionalServicesObject: _cloneDeep(allCodeToService),
      };
      // merge
      chargeListItems.forEach(item => {
        mergedItem.totalCharge += item.totalCharge;
        mergedItem.cloudCost += item.cloudCost;
        mergedItem.cloudOriginalCost += item.cloudOriginalCost;
        mergedItem.onDemandDiscount += item.onDemandDiscount;
        mergedItem.cloudFrontDiscount += item.cloudFrontDiscount;
        mergedItem.cloudFrontDtoDiscount += item.cloudFrontDtoDiscount;
        mergedItem.cloudFrontReqDiscount += item.cloudFrontReqDiscount;
        mergedItem.cloudServiceCharge += item.cloudServiceCharge;
        mergedItem.exchangedCloudServiceCharge += item.exchangedCloudServiceCharge;
        mergedItem.ncpDiscount += _isNil(item.ncpDiscount) ? 0 : item.ncpDiscount;
        mergedItem.salesDiscount += _isNil(item.salesDiscount) ? 0 : item.salesDiscount;
        mergedItem.supportFee += _isNil(item.supportFee) ? 0 : item.supportFee;
        mergedItem.credit += _isNil(item.credit) ? 0 : item.credit;
        mergedItem.vatCost += _isNil(item.vatCost) ? 0 : item.vatCost;
        if (item.additionalServices) {
          item.additionalServices.forEach(service => {
            mergedItem.additionalServices[codeToArrIdx[service.additionalServiceCode]].additionalServiceCharge += service.additionalServiceCharge;
            mergedItem.additionalServicesObject[service.additionalServiceCode].additionalServiceCharge += service.additionalServiceCharge;
          });
        }
      });
    }

    return mergedItem;
  }
</script>
<style lang="scss" scoped>
  .base-hotspot-container {
    &.in-charge-list {
      left: 48px;
      top: -11px;
    }
    &.in-invoice-list {
      left: 155px;
      top: -11px;
    }
    &.in-cloud-bill-detail {
      left: 288px;
      top: -11px;
    }
    &.in-invoice-insight {
      left: 400px;
      top: -11px;
    }
  }
  #billing-detail-table-tabs {
    .tab-panel-wrapper {
      right: 0px !important;
    }
    .nav-link {
      &.active {
        .base-hotspot-container {
          &.in-charge-list {
            left: 28px;
          }
          &.in-invoice-list {
            left: 40px;
          }
          &.in-cloud-bill-detail {
            left: 39px;
          }
          &.in-invoice-insight {
            left: 40px;
          }
        }
      }
    }
  }
  .billing-detail-download {
    margin-right: 10px;
    .btn-download{
      margin: 0 !important;
    }
    width: 25px;
    height: 25px;
    display: flex;
    align-content: center;
    justify-content: center;
    &:hover {
      background-color: #f0f0f0;
      border-radius: 5px;
      cursor: pointer;
    }
    &:active{
      background-color:#e0e0e0!important
    }
  }
</style>
