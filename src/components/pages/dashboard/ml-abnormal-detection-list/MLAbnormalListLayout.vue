<template>
  <!-- fluid of b-container property full width -->
  <b-container
    id="wrapper"
    class="wrapper-anomaly-list"
    fluid>
    <fragment>
      <mcmp-base-header/>
      <base-title class="base-title">
        <b-row
          no-gutters
          align-v="center">
          <span class="font-16 -mr-1 bold">{{ $t('anomaly.anomalyListTitle') }}</span>
          <base-material
            id="alarmListTooltip"
            :size="16"
            color="gray-1"
            name="info"
          />
          <b-tooltip
            target="alarmListTooltip"
            placement="right"
          >
            <span
              class="tooltip-info">
              <div
                style="text-align:left">
                {{ $t('anomaly.anomalyListTooltip') }}
              </div>
            </span>
          </b-tooltip>
        </b-row>
      </base-title>
    </fragment>
    <b-row
      class="mt-20"
      no-gutters/>
    <BaseLeftMenu >
      <template v-slot:mainRight>
        <div>
          <b-row
            no-gutters>
            <div class="mb-2 div-search-filter">
              <b-button
                variant="transparent"
                class="custom-date-picker-toggle-btn"
                @click.stop="toggleDatePicker"
              >
                <div class="mr-4">
                  <span class="bold pr-1">{{ $t('dashboard.filterOption.custom') }}</span>
                  {{ startDateText }}~{{ endDateText }}
                </div>
                <base-material
                  :size="15"
                  name="event"
                  color="gray-1"
                />
              </b-button>
            </div>
          </b-row>
          <MLAbnormalDatePicker
            v-click-outside="onClickOutsideOfDatePicker"
            v-if="canShowDatePicker"
            :filter-settings="dateRangeSettings"
            :is-sidebar-active="isSidebarActive"
            @closeDatePicker="onCloseDatePicker"
            @submit="onSubmitDatePicker"/>
          <!--
          <b-row
            no-gutters
            align-v="center"
            class="w-100">
            <TotalAlerts
              :anomaly-list-settings="anomalyListSettings"
              :user-abnormal-raw-data="userAbnormalRawData"
              :data-loaded-flag="dataLoadedFlag"
              @click="onClickAlertLevel"
            />
            <UserAnomalyAlertTop5
              :anomaly-list-settings="anomalyListSettings"
              :user-abnormal-raw-data="userAbnormalRawData"
              :data-loaded-flag="dataLoadedFlag"/>
            <AiAnomalyChangeTop5
              :anomaly-list-settings="anomalyListSettings"/>
          </b-row>
          -->
          <b-row
            no-gutters
            align-v="center"
            class="w-100 -mt-5"
            style="margin-bottom: 48px">
            <m-l-abnormal-table
              :anomaly-list-settings="anomalyListSettings"
              :selected-alert-level="selectedAlertLevel"
              :user-abnormal-raw-data="userAbnormalRawData"
              :data-loaded-flag="dataLoadedFlag"
              :is-loading="isLoading"
              @onCellClicked="onCellClicked"
            />
          </b-row>
        </div>
      </template>
    </BaseLeftMenu>
  </b-container>
</template>

<script>
import _isEqual from 'lodash/isEqual'
import _cloneDeep from 'lodash/cloneDeep';
import _isEmpty from 'lodash/isEmpty'
import _get from 'lodash/get';
import _size from 'lodash/size';
import _isNil from 'lodash/isNil';
import {mapGetters} from 'vuex';
import ClickOutside from 'vue-click-outside';
import {
  DEFAULT_DASHBOARD,
  VIEW_MODE,
} from '@/constants/dashboardConstants';
import {
  getStandardizedWidgetsForRender,
} from '@/util/dashboardUtils';
import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
const BaseNoAccount = () => import('@/components/pages/dashboard/no-account/BaseNoAccount');
import {CLASSIC_VERSION_PAGE} from "@/constants/constants";
import BaseLeftMenu from '@/components/common/base-left-menu/BaseLeftMenu';
const DashboardHeader = () => import('@/components/pages/dashboard/dashboard-header/DashboardHeader');
const DashboardWidgets = () => import('@/components/pages/dashboard/DashboardWidgets');
const LastUpdated = () => import('@/components/pages/dashboard/last-updated/LastUpdated');
// import BaseHeader from '@/components/common/base-header/BaseHeader';
import McmpBaseHeader from '@/components/common/base-header/McmpBaseHeader';
import MLAbnormalDatePicker from './MLAbnormalDatePicker';
import MLAbnormalTable from "./MLAbnormalTable";
import dayjs from "dayjs";
import {ABNORMAL_TIME_FRAME} from "../../../../constants/dashboardConstants";
import {
  COST_ANALYTICS_ACTION_TYPE,
  DEFAULT_CURRENCY,
  DEFAULT_EXCHANGE_RATE,
  ROUTE_NAME
} from "../../../../constants/constants";
import {fetchAbnormalDetectedList} from "../../../../api/anomalyDetail";

// don't know why it finishes scrolling in less than the duration provided
const SCROLL_TO_NEW_WIDGET_DURATION = 1000;
const FIRST_TIME_RENDER_TIMEOUT_OF_HEAVY_WIDGET = SCROLL_TO_NEW_WIDGET_DURATION * 3 / 4;
const FIRST_TIME_RENDER_TIMEOUT_OF_LIGHT_WIDGET = 200;
const DATE_FORMAT_SUBMIT = 'YYYY-MM-DD';

export default {
  name: 'AbnormalList',
  components: {
    MLAbnormalTable,
    BaseNoAccount,
    LastUpdated,
    DashboardHeader,
    DashboardWidgets,
    BaseLeftMenu,
    // BaseHeader,
    McmpBaseHeader,
    MLAbnormalDatePicker,
  },
  directives: {
    ClickOutside
  },
  inheritAttrs: false,
  props: {
  },
  data() {
    return {
      // this is current dashboard local state. Changes while in edit mode will be written directly to this.
      startDate: this.$dayjs.utc().startOf('month'),
      endDate: this.$dayjs.utc(),
      startDateText: '',
      endDateText: '',
      month: this.$dayjs.utc().month(),
      isSidebarActive: false,
      sharingClicked: false,
      toShareDashboardIndex: -1,
      currentDashboard: DEFAULT_DASHBOARD,
      VIEW_MODE: VIEW_MODE,
      backToClassicalVersionContent: '',
      compareCostTrendGuideIndex: -1,
      CLASSIC_VERSION_PAGE:  CLASSIC_VERSION_PAGE,
      widgetLoadingState: {},
      canShowWelcomePopup: false,
      anomalyListSettings: null,
      dateRangeSettings: {
        startDate: this.$dayjs.utc().startOf('month'),
        endDate: this.$dayjs.utc()
      },
      canShowDatePicker: false,
      alertListSetting: {},
      selectedAlertLevel: '',
      userAbnormalRawData:[],
      dataLoadedFlag: false,
      isLoading: true
    }
  },
  computed: {
    ...mapGetters({
      costMonthToDate: 'dashboard/costMonthToDate',
      estimatedCost: 'dashboard/estimatedCost',
      budgetData:'dashboard/budgetData',
      totalSaving:'dashboard/totalSaving',
      dashboardData: 'dashboard/dashboardData',
      dashboardViewMode: 'dashboard/dashboardViewMode',
      batchTime: 'dashboard/batchTime',
      commonUserInfo: 'common/info',
      exchangeRate: 'common/exchangeRate',
      submittedSurvey: 'common/submittedSurvey',
      browser: "common/browser"
    }),
    hasAccount: function () {
      // out of scope
      return true;
    },
    allVendors : function (){
      // return ['GCP' , 'AZURE'];
      let curCmpnId = this.$store.state.loginUser.curCmpnId;
      let vendorInfo = this.$store.state.vendorInfo;
      if(curCmpnId && vendorInfo &&vendorInfo.length > 0){
        return vendorInfo;
      }else{
        return [];
      }
    }
  },
  watch: {
    dashboardData: {
      handler() {
        const currentDashboard = this.dashboardData.find(dashboard => dashboard.isDashboardSelected);
        this.currentDashboard = {
          ..._cloneDeep(currentDashboard),
          widgets: getStandardizedWidgetsForRender(currentDashboard.widgets)
        };
      }
    },
    'commonUserInfo.selectedVendors': {
      handler() {
        let requestPayload = {
          vendors: this.commonUserInfo.selectedVendors
        }
      },
      immediate: false
    },
    'currentDashboard.widgets': {
      handler() {
        // this.getCompareCostTrendGuideIndex()
        // update data.widgetLoadingState if widgets.length changes (remove widget/add widget/change dashboard...)
        if (_size(this.currentDashboard.widgets) !== _size(this.widgetLoadingState)) {
          let newWidgetLoadingState = {
          };
          this.currentDashboard.widgets.forEach(widget => {
            newWidgetLoadingState[widget.index] = _isNil(this.widgetLoadingState[widget.index])
              ? true
              : this.widgetLoadingState[widget.index];
          });
          this.widgetLoadingState = newWidgetLoadingState;
        }
      },
      immediate: true
    },
    allVendors:{
      handler() {
        this.$store.dispatch('dashboard/getDashboardData')
      },
      immediate: false
    },
    '$i18n.locale': {
      handler() {
        this.formatDateText();
      }
    },
    anomalyListSettings:{
      handler() {
        this.getDetectedListData();
      },
    },
  },
  mounted() {
    this.formatDateText();
  },
  created() {
    this.anomalyListSettings = {
      startDate: (this.$route.params.endDate != undefined && dayjs(this.$route.params.endDate) < this.startDate)?
        dayjs(this.$route.params.endDate).startOf('month').format(DATE_FORMAT_SUBMIT):
        this.startDate.format(DATE_FORMAT_SUBMIT),
      endDate: this.$route.params.endDate != undefined ?
        dayjs(this.$route.params.endDate).format(DATE_FORMAT_SUBMIT) : this.endDate.format(DATE_FORMAT_SUBMIT),
      widgetIndex: this.$route.params.widgetIndex,
      dashboardIndex: this.$route.params.dashboardIndex,
      widgetType: this.$route.params.widgetType,
      sensitivity: this.$route.params.sensitivity,
    };
    this.dateRangeSettings = {
      startDate: (this.$route.params.endDate != undefined && dayjs(this.$route.params.endDate) < this.startDate)?
        dayjs(this.$route.params.endDate).startOf('month') : this.startDate,
      endDate: this.$route.params.endDate != undefined ? dayjs(this.$route.params.endDate) : this.endDate
    }
  },
  methods: {
    toggleDatePicker() {
      this.canShowDatePicker = !this.canShowDatePicker;
      // if (this.canShowDatePicker) {
      //   this.setIsCloseQuickFilterDropdown();
      // }
    },
    onClickOutsideOfDatePicker() {
      this.onCloseDatePicker();
    },
    onSubmitDatePicker(param) {
      this.updateDateRange(param);
      this.$store.dispatch('anomaly/getTotalAlertsData', this.anomalyListSettings);
      this.onCloseDatePicker();
    },
    updateDateRange(param){
      this.dateRangeSettings.startDate = param.startDate;
      this.dateRangeSettings.endDate = param.endDate;
      this.anomalyListSettings = {
        ...this.anomalyListSettings,
        startDate : param.startDate.format(DATE_FORMAT_SUBMIT),
        endDate : param.endDate.format(DATE_FORMAT_SUBMIT)
      };
      this.formatDateText();
    },
    onCloseDatePicker() {
      this.canShowDatePicker = false
    },
    routerToMLAbnormalDetail(rowData){
      let widgetConfig = {
        sensitivity:rowData.message.sensitivity,
        minAlert:rowData.minAlert,
        maxAlert:rowData.maxAlert,
        timeFrame:rowData.message.timeFrame,
        viewBy:rowData.message.viewBy,
        title:rowData.widgetTitle,
        dashboardIndex:rowData.dashboardIndex,
        index:rowData.widgetIndex,
      }
      let params = {
        widgetConfig: widgetConfig,
        getSelectedVendorsByWidget: rowData.vendor,
        fluctuationCost: rowData.message.cost,
        fluctuationRate: rowData.message.rate,
        currency: DEFAULT_CURRENCY,
        exchangeRate: DEFAULT_EXCHANGE_RATE,
        forecastDate: rowData.analDt,
        widgetTitle: rowData.widgetTitle,
        dashboardTitle: rowData.dashboardTitle,
      }
      this.$router.push({
        name: ROUTE_NAME.ANOMALY_DETAIL,
        params: params
      });
    },
    routerToCostAnalytics(rowData){
      let startDate = ''
      let endDate = ''
      let compareStartDate =''
      let compareEndDate =''
      let isCompare = true;
      let isCompareDateCustom = false;
      const latestSummarizedBillDate = rowData.analDt.substring(0,10)
      switch (rowData.message.timeFrame) {
        case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE: {
          endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT_SUBMIT);
          startDate = dayjs(latestSummarizedBillDate).subtract(2, 'day').format(DATE_FORMAT_SUBMIT);
          compareStartDate = dayjs(latestSummarizedBillDate).subtract(5, 'day').format(DATE_FORMAT_SUBMIT);
          compareEndDate = dayjs(latestSummarizedBillDate).subtract(3, 'day').format(DATE_FORMAT_SUBMIT);
          break;
        }
        case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE: {
          endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT_SUBMIT);
          startDate = dayjs(latestSummarizedBillDate).subtract(6, 'day').format(DATE_FORMAT_SUBMIT);
          compareStartDate = dayjs(latestSummarizedBillDate).subtract(13, 'day').format(DATE_FORMAT_SUBMIT);
          compareEndDate = dayjs(latestSummarizedBillDate).subtract(7, 'day').format(DATE_FORMAT_SUBMIT);
          break;
        }
        case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS: {
          isCompare = false;
          if (latestSummarizedBillDate) {
            endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT_SUBMIT)
            startDate = dayjs(latestSummarizedBillDate).subtract(7, 'day').format(DATE_FORMAT_SUBMIT)
          } else {
            endDate = dayjs.utc().subtract(2, 'day').format(DATE_FORMAT_SUBMIT)
            startDate = dayjs.utc().subtract(9, 'day').format(DATE_FORMAT_SUBMIT)
          }
          break;
        }
        case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH: {
          startDate = dayjs(latestSummarizedBillDate).startOf('month').format(DATE_FORMAT_SUBMIT)
          endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT_SUBMIT)
          compareStartDate =  dayjs(latestSummarizedBillDate).subtract(1,'month').startOf('month').format(DATE_FORMAT_SUBMIT)
          compareEndDate = dayjs(latestSummarizedBillDate).subtract(1,'month').format(DATE_FORMAT_SUBMIT)
          isCompareDateCustom = true;
          break;
        }
      }

      let itemArray = rowData.message.item;
      let vendorInfo = rowData.message.vendor;
      let itemData = [];
      if(itemArray != undefined){
        itemArray.forEach( item => {
          let objectItems = {}
          objectItems['dataKey'] = item;
          objectItems['vendor'] = vendorInfo;
          itemData.push(objectItems);
        })
      }

      let payload = {
        selectedVendor: vendorInfo,
        actionType: COST_ANALYTICS_ACTION_TYPE.CLICK_TO_ANALYZE,
        viewBy: rowData.message.viewBy,
        startDate: startDate,
        endDate: endDate,
        compareStartDate: compareStartDate,
        compareEndDate: compareEndDate,
        isCompare: isCompare,
        isCompareDateCustom: isCompareDateCustom,
        data: itemData
      };

      this.$store.dispatch('common/setIsLoading', true);
      // this.$store.dispatch('common/setSearchFrom', this.$route.fullPath);
      this.$router.push({
        name: ROUTE_NAME.COST_ANALYTICS,
        params: payload
      });

    },
    onCellClicked(rowData) {
      if(rowData.detcBy === "AI"){
        this.routerToMLAbnormalDetail(rowData)
      }else{
        this.routerToCostAnalytics(rowData)
      }
    },
    onClickAlertLevel(param) {
      this.selectedAlertLevel = param;
    },
    formatDateText() {
      this.startDateText = this.dateRangeSettings.startDate.format(getFullDateFormatByLocalization());
      this.endDateText = this.dateRangeSettings.endDate.format(getFullDateFormatByLocalization());
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
  }
};

</script>

<style lang="scss">
.wrapper-anomaly-list {
  .tooltip div {
    font-size: 10px;
    text-align: left;
  }
  .wrapper-th{
    .-wrapper-tile{
      height:289px;
    }
  }
  .table-wrapper{
    width:100%;
    padding:0 20px;
    padding-top:20px;
    max-height:250px;
    overflow:hidden;
    overflow-y:auto;
  }
  .decoration{
    text-decoration: underline;
  }
  .alarm-ino-detail{
    width: 70%;
    padding-top: 6px;
  }
  .div-search-filter {
    display: flex;
    width: 300px;
    height: 28px;
    button {
      &:focus {
        color: #222222 !important;
      }
    }
    .custom-date-picker-toggle-btn {
      padding-left: 1px;
      padding-right: 0px;
      color: #222222;
      background-color: #ffffff;
      position: relative;
      font-size: 14px;
      width: 290px;
      border-radius: 4px !important;
      border: 1px solid #D5DAE0 !important;
      div {
        margin-left: 12px;
        width: auto;
        max-width: 84%;
        overflow: hidden;
        text-align: left;
        white-space: nowrap;
        text-overflow: ellipsis;
      }
      .material-icons {
        position: absolute;
        right: 8px;
      }
    }
  }
  #alarmListTooltip {
    color: #b0b7bf!important;
  }
}
</style>
