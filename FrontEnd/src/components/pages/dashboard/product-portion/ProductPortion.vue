<template>
  <b-card
    :id="productPortionByWrapperId"
    border-variant="transparent"
    class="card-custom portion-widget-wrapper"
    header-bg-variant="transparent"
    header-border-variant="lightgray-1">
    <b-row
      slot="header"
      align-h="between"
      align-v="center"
      class="px-20 py-16 portion-widget-header"
      no-gutters>
      <div class="font-16 medium font-family-notosanscjkkr-medium portion-widget-view-by">
        <span :id="productPortionHeader1">{{ selectedVendor }} {{ $t('dashboard.portionByAccount.productPortionOf.#1') }}</span>
        <base-dropdown
          ref="productPortionAccountDropdown"
          :id="accountsDropdownId"
          :options="accountOptions"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="font-family-notosanscjkkr-medium custom-btn-portion-date-type-dropdown custom-view-by-dropdown accounts-dropdown"
          @selectOption="onChangeAccount"
          @mounted="onProductPortionAccountDropdownMounted"
        />
        <span :id="productPortionHeader2">{{ $t('dashboard.portionByAccount.productPortionOf.#2') }}</span>
        <b-tooltip
          :disabled="!isAccountDropdownTooltipEnabled"
          :target="accountsDropdownId"
          custom-class="custom-accountOptions-tooltip"
          triggers="hover"
        >
          <div>{{ selectedAccountText }}</div>
        </b-tooltip>
        <BaseTimePeriod
          :time-period="timePeriod"
          class="portion-time-period-pos"/>
      </div>
      <b-row
        :id="rightHeaderProductPortionBy"
        class="portion-right-header-mr-top portion-right-header-mr-bottom">
        <base-dropdown
          ref="productPortionDateTypeDropdown"
          :options="dateTypeOptions"
          :enabled-localization="true"
          :disabled="widgetLoadingState[widgetConfig.index]"
          variant="default"
          class="font-family-notosanscjkkr-medium custom-btn-portion-date-type-dropdown"
          @selectOption="onChangeDateType"
          @mounted="onProductPortionDateTypeDropdownMounted"
        />
        <base-dropdown
          ref="productPortionTimeFrameDropdown"
          :options="timeFrameOptions"
          :disabled="widgetLoadingState[widgetConfig.index]"
          variant="default"
          class="font-family-notosanscjkkr-medium custom-btn-portion-time-frame-dropdown"
          @selectOption="onChangeTimeFrame"
        />
        <b-button
          :id="productAccountDropdownOptions.productAccountDropdownOptionsId"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="dropdown-button"
          variant="transparent">
          <base-material
            :size="24"
            name="more_vert"
            color="gray-1"/>
        </b-button>
        <div
          :id="containerCustomPopover"
          class="custom-popover-portion-widget"/>
        <BasePopoverDropdown
          ref="productCostPopover"
          :id="productCostDropdown"
          :target="productAccountDropdownOptions.productAccountDropdownOptionsId"
          :placement="productAccountDropdownOptions.placement"
          :options="productAccountDropdownOptions.options"
          :show-popover="productAccountDropdownOptions.showPopover"
          :enabled-localization="productAccountDropdownOptions.enabledLocalization"
          :container-custom-popover="containerCustomPopover"
          @selectOption="onSelectDropdownOption"
        />
      </b-row>
    </b-row>
    <b-col
      class="portion-widget-chart-wrapper">
      <BaseLoadingIndicator
        v-show="widgetLoadingState[widgetConfig.index]"
        :loading-height="366"
      />
      <div v-show="!widgetLoadingState[widgetConfig.index]">
        <PrimeBaseSimpleBarChart
          v-show="hasProductPortionData"
          :bar-chart-data="primeBarChartData"
          :scale="internalWidgetConfig.scale"
          :value-prefix="currencySymbol"
          :tooltip-data="productPortionData.portion"
          :selected-currency="internalCommonUserInfo.selectedCurrency"
          :exchange-rate="internalExchangeRate"
        />
        <BaseNotificationNotSupport
          v-if="typeof selectedVendor == 'undefined'"
          :support-vendors="supportVendors"
          class="no-data"
        />
        <BaseNotificationNoData v-if="typeof selectedVendor != 'undefined' && !hasProductPortionData"/>
      </div>
    </b-col>
    <b-modal
      ref="edit-product-cost-tree-map"
      v-model="widgetConfig.isEditFormVisible"
      :title="$t('dashboard.editWidget')"
      modal-class="right-wing"
      hide-footer
      hide-backdrop
    >
      <div v-if="widgetConfig.isEditFormVisible">
        <EditProductPortionForm
          :common-user-info="internalCommonUserInfo"
          :exchange-rate="internalExchangeRate"
          :widget-config="internalWidgetConfig"
          :dashboard-view-mode="dashboardViewMode"
          :all-vendors="allVendors"
          @save="saveWidget"
          @hideModal="hideModal"
        />
      </div>
    </b-modal>
  </b-card>
</template>

<script>
  import PrimeBaseSimpleBarChart from '@/components/common/base-charts/base-simple-bar-chart/PrimeBaseSimpleBarChart'
  import BaseDropdown from '@/components/common/BaseDropdown';
  import BaseNotificationNotSupport from '@/components/common/BaseNotificationNotSupport';
  const EditProductPortionForm = () => import('@/components/pages/dashboard/product-portion/EditProductPortionForm');
  import _cloneDeep from 'lodash/cloneDeep';
  import _isEqual from 'lodash/isEqual';
  import {
    DASHBOARD_DATE_TYPE,
    DASHBOARD_DATE_TYPE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DASHBOARD_DROPDOWN_OPTIONS,
    VIEW_MODE,
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_PORTION_REQUEST_MODEL,
    DEFAULT_PRODUCT_PORTION_BY_WIDGET_CONFIG,
    PORTION_DEFAULT_TIME_FRAME,
    DASHBOARD_WIDGET_TYPE,
    PORTION_DEFAULT_SELECTED_ACCOUNT,
    DASHBOARD_VIEW_BY,
    DEFAULT_WIDGET_DATA,
    SCALE,
    SELECTED_VENDOR
  } from "@/constants/dashboardConstants";
  import {
    CURRENCY,
    CURRENCY_SYMBOL, DEFAULT_CURRENCY,
    DEFAULT_EXCHANGE_RATE,
  } from '@/constants/constants';
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from '@/constants/costAnalyticsConstants';
  import {
    getTimeFrameListOption,
    getPortionChartData,
    getPrimePortionChartData,
    isProductPortionWidgetDataConfigChanged,
    getPortionMonthlyTimeFrame,
    getCsvExportData,
    getSelectedVendorsByWidget,
  } from '@/util/dashboardUtils';
  import {fetchDashboardProductPortion} from '@/api/dashboard';
  import _isEmpty from 'lodash/isEmpty';
  import _isNil from 'lodash/isNil';
  import _get from 'lodash/get';
  import ShowEditFormModalMixin from '@/mixins/ShowEditFormModalMixin';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import {initWorkBookViaExcelJs, saveAndReturnSupportedUTF18CSVFile} from "@/util/excelJS";
  import {prepareDataForExportProductPortionByAccount} from "@/util/exportUtils";
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import BaseTimePeriod from "@/components/common/BaseTimePeriod";
  import dayjs from "dayjs";
  import {
    formatMonthYearByLocalization,
    getFullDateFormatByLocalization,
    getMonthYearDateFormatByLocalization,
    getWeekNumberOfDate
  } from "@/util/dateTimeUtils";
  import {getDisplayItemWithVendorBaseOnViewBy} from "@/util/formatAccountUtils";
  import {
    COMPARE_COST_TREND_VENDORS,
    COST_MONTH_TO_DATE_VIEW_BY_VENDORS,
    PRODUCT_PORTION_VENDORS
  } from "../../../../constants/dashboardConstants";
  const REQUEST_PAYLOAD_DATE_FORMAT = 'YYYY-MM-DD';
  const TIME_PERIOD_DATE_FORMAT = "MM/DD/YYYY";
  const DAY_JS_INVALID_DATE = "Invalid Date";
  const COST_ANALYTIC_DATE_FORMAT = 'YYYY-MM-DD';
  const DISTANCE_BETWEEN_OUTSIDE = 32;
  const UNIT_POSTFIX_THRESHOLD = 1000;

  export default {
    name: 'ProductPortion',
    components: {
      PrimeBaseSimpleBarChart,
      BaseDropdown,
      EditProductPortionForm,
      BaseNotificationNoData,
      BaseLoadingIndicator,
      BaseTimePeriod,
      BaseNotificationNotSupport
    },
    mixins: [ShowEditFormModalMixin],
    props: {
      widgetConfig: {
        type: Object,
        required: true
      },
      allVendors: {
        type: Array,
        required: true,
        default() {
          return []
        }
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      dashboardViewMode: {
        type: String,
        default: VIEW_MODE.DEFAULT
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
      isSidebarActive: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        productPortionByWrapperId: `product-portion-by-wrapper-id-${this.widgetConfig.index}`,
        accountsDropdownId: `accounts-dropdown-id-${this.widgetConfig.index}`,
        rightHeaderProductPortionBy: `right-header-product-portion-by-${this.widgetConfig.index}`,
        productPortionHeader1: `product-portion-header-1-${this.widgetConfig.index}`,
        productPortionHeader2: `product-portion-header-2-${this.widgetConfig.index}`,
        productCostDropdown: `product-cost-dropdown-${this.widgetConfig.index}`,
        productPortionData: DEFAULT_WIDGET_DATA.PRODUCT_PORTION,
        latestSummarizedBillDate:null,
        internalWidgetConfig: DEFAULT_PRODUCT_PORTION_BY_WIDGET_CONFIG,
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendors: []
        },
        internalExchangeRate: DEFAULT_EXCHANGE_RATE,
        previewWidgetConfig: {
          dateType: DASHBOARD_DATE_TYPE.MONTHLY,
          timeFrame: null,
          selectedAccount: null,
          selectedVendors: [],
        },
        dateTypeOptions: DASHBOARD_DATE_TYPE_OPTIONS,
        timeFrameOptions: [],

        barChartFieldChildren: 'children',
        barChartFieldValue: SCALE.PERCENTAGE,
        barChartFieldName: 'name',
        barChartFieldColor: 'color',
        transformTranslateBarChart: {
          x: -10,
          y: 5
        },
        levelColumnStrokeColor: '#ff9c2e',
        bulletTextName: '{name}',
        bulletTextValue: '${value}',
        productAccountDropdownOptions: {
          productAccountDropdownOptionsId: `product-by-account-dropdown-options-${this.widgetConfig.index}`,
          options: DASHBOARD_DROPDOWN_OPTIONS,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        containerCustomPopover: `product-by-account-custom-popover-${this.widgetConfig.index}`,
        showEditFormModal: false,
        EXPORT_FILE_NAME: 'ProductPortionOf',
        EXPORT_COLUMNS_NAMES: {
          PRODUCT_FAMILY: this.$t('dashboard.portionByWidget.download.productFamily'),
          PRODUCT: this.$t('dashboard.portionByWidget.download.product'),
          VENDOR: this.$t('dashboard.portionByWidget.download.vendor'),
          COST: this.$t('dashboard.portionByWidget.download.cost'),
          PORTION: this.$t('dashboard.portionByWidget.download.percentage'),
          REGION: this.$t('dashboard.portionByWidget.download.region'),
          TAG_VALUE: this.$t('dashboard.portionByWidget.download.tagValue'),
          ACCOUNT: this.$t('dashboard.portionByWidget.download.account'),
          PROJECT: this.$t('dashboard.portionByWidget.download.project')
        },
        isAccountDropdownTooltipEnabled: false,
        supportVendors: PRODUCT_PORTION_VENDORS
      };
    },
    computed: {
      selectedAccountText: {
        cache: true,
        get() {
          let selectedAccount = this.accountOptions && this.accountOptions.find(accountOption => {
            return accountOption.value === this.previewWidgetConfig.selectedAccount;
          });
          // 선택된 계정 값이 없을 경우, 툴팁 표시용 데이터 세팅
          if (_isEmpty(selectedAccount)) {
            selectedAccount = this.accountOptions[0];
          }
          return selectedAccount && selectedAccount.text;
        }
      },
      simpleBarChartData: {
        cache: true,
        get() {
          if (!this.productPortionData || _isEmpty(this.productPortionData.portion)) {
            return [];
          } else {
            let accountInfo = this.productPortionData.accounts.find(account => account.item === this.widgetConfig.selectedAccount);
            let vendor = _isNil(accountInfo) ? "" : accountInfo.vendor;
            let widgetConfig = {
              viewBy: this.internalWidgetConfig.viewBy,
              widgetType: this.internalWidgetConfig.widgetType
            }
            return getPortionChartData(this.productPortionData.portion, vendor, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, widgetConfig, true, this.$t('dashboard.others'));
          }
        }
      },
      primeBarChartData: {
        cache: true,
        get() {
          if (!this.productPortionData || _isEmpty(this.productPortionData.portion)) {
            return [];
          } else {
            let accountInfo = this.productPortionData.accounts.find(account => account.item === this.widgetConfig.selectedAccount);
            let vendor = _isNil(accountInfo) ? "" : accountInfo.vendor;
            let widgetConfig = {
              viewBy: this.internalWidgetConfig.viewBy,
              widgetType: this.internalWidgetConfig.widgetType
            }
            return getPrimePortionChartData(this.productPortionData.portion, vendor, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, this.internalWidgetConfig.scale);
          }
        }
      },
      csvExportData: {
        cache: true,
        get() {
          if (!this.productPortionData || _isEmpty(this.productPortionData.portion)) {
            return [];
          } else {
            let accountInfo = this.productPortionData.accounts.find(account => account.item === this.widgetConfig.selectedAccount);
            let vendor = _isNil(accountInfo) ? "" : accountInfo.vendor;
            let widgetConfig = {
              viewBy: this.internalWidgetConfig.viewBy,
              widgetType: this.internalWidgetConfig.widgetType
            }
            return getCsvExportData(this.productPortionData.portion, vendor, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, widgetConfig, true, this.$t('dashboard.others')).reverse();
          }
        }
      },
      currencySymbol: function() {
        return CURRENCY_SYMBOL[this.internalCommonUserInfo ? this.internalCommonUserInfo.selectedCurrency : CURRENCY.USD];
      },
      hasProductPortionData() {
        if (!this.productPortionData || _isEmpty(this.productPortionData.portion)) {
          return false;
        }
        return Object.values(this.productPortionData.portion).some(costsByProduct => !_isEmpty(costsByProduct));
      },
      accountOptions() {
        return this.productPortionData.accounts.map(account => {
          return {
            text: getDisplayItemWithVendorBaseOnViewBy(account, DASHBOARD_VIEW_BY.ACCOUNT),
            value: account.item,
            isDefault: (account.item === this.previewWidgetConfig.selectedAccount)
          };
        });
      },
      valueNumberFormat() {
        return `${this.currencySymbol}#a`;
        // let totalCost = 0;
        // this.productPortionData.portion.forEach(item => {
        //   totalCost += item.cost
        // });
        // if (totalCost < UNIT_POSTFIX_THRESHOLD) {
        //   return `${this.currencySymbol}#`;
        // }
        // return `${this.currencySymbol}#a`;
      },
      timePeriod: function() {
        return this.getDisplayTimePeriod();
      },
      selectedVendor: {
        get() {
          return getSelectedVendorsByWidget(this.widgetConfig, this, PRODUCT_PORTION_VENDORS, true);
        }
      },
      enableDrillDown: {
        get() {
          if(_isEqual(this.profile.env, "CHINA") && _isEqual(this.selectedVendor, "GCP"))
            return false
          return this.Common.checkCostAnalyticsMenuAuth(this) && this.Common.checkVendorAvailableFromSelectedVendor(getSelectedVendorsByWidget(this.internalWidgetConfig, this, PRODUCT_PORTION_VENDORS),COST_ANALYTICS_VIEW_BY_VENDORS)
        }
      }
    },
    watch: {
      'commonUserInfo.selectedVendors': {
        handler(newSelectedVendors) {
          if (_isEqual(this.internalCommonUserInfo.selectedVendors, newSelectedVendors)) {
            return;
          }
          this.internalCommonUserInfo.selectedVendors = _cloneDeep(this.commonUserInfo.selectedVendors);
        },
        immediate: true
      },
      allVendors: {
        handler() {
          if(_isNil(this.widgetConfig)){
            return;
          }
          this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: false
      },
      widgetConfig: {
        handler(newValue, oldValue) {
          if (!isProductPortionWidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if(_isEmpty(this.allVendors)){
            this.widgetLoadingState[this.widgetConfig.index] = false;
            return;
          }
          if (!isProductPortionWidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            if (isProductPortionWidgetDataConfigChanged(this.previewWidgetConfig, this.widgetConfig, true)) {
              if(_isNil(this.widgetConfig.isNew)){
                this.updatePreviewWidgetConfig();
              }
            }
            return;
          }
          this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: true
      },
      internalWidgetConfig: {
        handler() {
          if(_isEmpty(this.allVendors)){
            return;
          }
          if (_isNil(this.widgetConfig)) {
            return;
          }
          if (_isNil(this.internalWidgetConfig)) {
            return;
          }
          this.updatePreviewWidgetConfig();
        },
        immediate: true
      },
      'commonUserInfo.selectedCurrency': {
        handler(newSelectedCurrency) {
          if (this.internalCommonUserInfo.selectedCurrency === newSelectedCurrency) {
            return;
          }
          this.internalCommonUserInfo.selectedCurrency = this.commonUserInfo.selectedCurrency;
        },
        immediate: true
      },
      'internalCommonUserInfo.selectedVendors': {
        handler() {
          this.loadDashboardPortionAccountWidget()
        },
        immediate: false
      },
      exchangeRate: {
        handler() {
          if (_isEqual(this.internalExchangeRate, this.exchangeRate)) {
            return;
          }
          this.internalExchangeRate = _cloneDeep(this.exchangeRate);
        },
        immediate: true
      },
      dashboardViewMode: function () {
        this.setProductAccountDropdownOptions();
      },
      '$i18n.locale': {
        handler() {
          this.changeCsvColumnNames()
          const timeFrameOptions = getTimeFrameListOption(this.previewWidgetConfig.dateType, this.productPortionData.timeFrameList, this);
          // ==== using `selectedTimeFrame` from response
          this.previewWidgetConfig.timeFrame = this.productPortionData.selectedTimeFrame;
          this.timeFrameOptions = timeFrameOptions.map(opt => {
            return {
              ...opt,
              isDefault: opt.value === this.previewWidgetConfig.timeFrame
            }
          });
          this.refreshAccountsDropdownWidths()
        },
      },
      isSidebarActive: function () {
        setTimeout(this.handleWindowResize, 100);
      },
    },
    mounted() {
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setProductAccountDropdownOptions();
      window.addEventListener('resize', this.handleWindowResize);
    },
    methods: {
      changeCsvColumnNames() {
        this.EXPORT_COLUMNS_NAMES.PRODUCT_FAMILY = this.$t('dashboard.portionByWidget.download.productFamily');
        this.EXPORT_COLUMNS_NAMES.PRODUCT = this.$t('dashboard.portionByWidget.download.product');
        this.EXPORT_COLUMNS_NAMES.VENDOR = this.$t('dashboard.portionByWidget.download.vendor');
        this.EXPORT_COLUMNS_NAMES.COST = this.$t('dashboard.portionByWidget.download.cost');
        this.EXPORT_COLUMNS_NAMES.PORTION = this.$t('dashboard.portionByWidget.download.percentage');
        this.EXPORT_COLUMNS_NAMES.REGION = this.$t('dashboard.portionByWidget.download.region');
        this.EXPORT_COLUMNS_NAMES.TAG_VALUE = this.$t('dashboard.portionByWidget.download.tagValue');
        this.EXPORT_COLUMNS_NAMES.ACCOUNT = this.$t('dashboard.portionByWidget.download.account');
        this.EXPORT_COLUMNS_NAMES.PROJECT = this.$t('dashboard.portionByWidget.download.project');
      },
      // eslint-disable-next-line vue/no-dupe-keys
      refreshAccountsDropdownWidths() {
        this.$nextTick(() => {
          if(document.getElementById(this.productPortionByWrapperId) === null){
            return;
          }

          let widthProductPortionBy = document.getElementById(this.productPortionByWrapperId).offsetWidth;
          let widthRightHeaderProductPortionBy = document.getElementById(this.rightHeaderProductPortionBy).clientWidth;
          let widthLeftHeaderProductPortionBy1 = document.getElementById(this.productPortionHeader1).clientWidth;
          let widthLeftHeaderProductPortionBy2 = document.getElementById(this.productPortionHeader2).clientWidth;
          document.getElementById(this.accountsDropdownId).style.maxWidth = `${widthProductPortionBy - widthRightHeaderProductPortionBy - widthLeftHeaderProductPortionBy1 - widthLeftHeaderProductPortionBy2 - DISTANCE_BETWEEN_OUTSIDE }px`
          let productPortionByDropdownMaxWidth = document.getElementById(this.accountsDropdownId).style.maxWidth.slice(0, -2)
          if (document.getElementById(this.accountsDropdownId).offsetWidth >= productPortionByDropdownMaxWidth) {
            this.isAccountDropdownTooltipEnabled = true
          } else {
            this.isAccountDropdownTooltipEnabled = false
          }
        })
      },
      handleWindowResize() {
        this.refreshAccountsDropdownWidths()
      },
      onClickBarChart(selectedPortionColumn) {
        if(!this.enableDrillDown){
          return
        }
        if (this.dashboardViewMode === VIEW_MODE.DEFAULT) {
          let timeFramePayload = {}
          switch (this.previewWidgetConfig.dateType) {
            case DASHBOARD_DATE_TYPE.MONTHLY:
              let monthIndex = this.previewWidgetConfig.timeFrame.slice(4, 6) - 1;
              let monthlyYear = this.previewWidgetConfig.timeFrame.slice(0, 4);
              let monthlyTimeFrame = dayjs().month(monthIndex);
              timeFramePayload = getPortionMonthlyTimeFrame(monthlyTimeFrame, monthIndex, monthlyYear, this.latestSummarizedBillDate)
              break;
            case DASHBOARD_DATE_TYPE.WEEKLY:
              let timeFrameParts = this.previewWidgetConfig.timeFrame.split("-");
              let weekNumber = timeFrameParts[0];
              let weeklyYear = timeFrameParts[1];
              let weekLyTimeFrame = dayjs().week(weekNumber);

              timeFramePayload.startDate = weekLyTimeFrame.startOf('week').set('year', weeklyYear).format(REQUEST_PAYLOAD_DATE_FORMAT);
              if (weekNumber === dayjs(this.latestSummarizedBillDate).week()+'') {
                timeFramePayload.endDate = dayjs(this.latestSummarizedBillDate).format(COST_ANALYTIC_DATE_FORMAT)
              }else{
                timeFramePayload.endDate = weekLyTimeFrame.endOf('week').set('year', weeklyYear).format(REQUEST_PAYLOAD_DATE_FORMAT);
              }
              break;
          }
          let dataPayload = [];

          let selectedPortion = this.simpleBarChartData.find(item => item.name === selectedPortionColumn.name);
          selectedPortion.children.forEach(item => {
            let prodName = this.getParsedProdName(selectedPortion.code, item);
            if(dataPayload.find(data => _isEqual(data.dataKey, prodName)) === undefined) {
              dataPayload.push({
                dataKey: prodName,
                vendor: item.vendor
              })
            }
          })

          dataPayload.push({
            dataKey: this.widgetConfig.selectedAccount,
            vendor: getSelectedVendorsByWidget(this.internalWidgetConfig, this, PRODUCT_PORTION_VENDORS),
            viewBy: DASHBOARD_VIEW_BY.ACCOUNT
          })
          this.$emit('clickToAnalyze', {
            selectedVendor : this.selectedVendor,
            widgetType: this.internalWidgetConfig.widgetType,
            viewBy: DASHBOARD_VIEW_BY.PRODUCT,
            startDate: timeFramePayload.startDate,
            endDate: timeFramePayload.endDate,
            data: dataPayload,
          })
        }
      },
      updatePreviewWidgetConfig() {
        this.setBarChartXAxisValue();
        this.previewWidgetConfig = {
          selectedAccount: this.internalWidgetConfig.selectedAccount,
          dateType: this.internalWidgetConfig.dateType,
          timeFrame: this.internalWidgetConfig.timeFrame,
          widgetCurrency : this.$store.state.common.info.currencies.KRW,
          vendors: this.internalWidgetConfig.selectedVendors,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, PRODUCT_PORTION_VENDORS)]
        };
        if (!_isNil(this.$refs.productPortionDateTypeDropdown)) {
          const selectedDateTypeOpt = this.dateTypeOptions.find(opt => opt.value === this.internalWidgetConfig.dateType);
          this.$refs.productPortionDateTypeDropdown.changeSelectedOptionText(selectedDateTypeOpt.text);
        }
        this.loadDashboardPortionAccountWidget();
      },
      setBarChartXAxisValue() {
        switch (_get(this.internalWidgetConfig, 'scale')) {
          case SCALE.VALUE:
            this.barChartFieldValue = SCALE.COST;
            break;
          case SCALE.PERCENTAGE:
            this.barChartFieldValue = SCALE.PERCENTAGE
            break;
          default:
            this.barChartFieldValue = SCALE.PERCENTAGE;
            break;
        }
      },
      loadDashboardPortionAccountWidget() {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        let defaultVendor = this.allVendors != null && this.allVendors.length > 0 ? [this.allVendors[0]] : null;
        let payload = {
          ...DASHBOARD_PORTION_REQUEST_MODEL,
          selectedAccount: _isEmpty(this.previewWidgetConfig.selectedAccount) ? PORTION_DEFAULT_SELECTED_ACCOUNT : this.previewWidgetConfig.selectedAccount,
          dateType: this.previewWidgetConfig.dateType,
          timeFrame: _isEmpty(this.previewWidgetConfig.timeFrame) ? PORTION_DEFAULT_TIME_FRAME : this.previewWidgetConfig.timeFrame,
          widgetCurrency : this.$store.state.common.info.currencies.KRW,
          vendors: this.internalCommonUserInfo.selectedVendors,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, PRODUCT_PORTION_VENDORS)],
          widgetType: DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET
        };
        this.refreshAccountsDropdownWidths()
        fetchDashboardProductPortion(payload).then((res) => {
          this.widgetLoadingState[this.widgetConfig.index] = false;
          this.productPortionData = res;
          this.latestSummarizedBillDate = _get(res, 'latestSummarizedBillDate');
          const timeFrameOptions = getTimeFrameListOption(this.previewWidgetConfig.dateType, this.productPortionData.timeFrameList, this);
          // ==== using `selectedTimeFrame` from response
          this.previewWidgetConfig.timeFrame = this.productPortionData.selectedTimeFrame;
          this.widgetConfig.selectedAccount = this.productPortionData.selectedAccount;
          this.timeFrameOptions = timeFrameOptions.map(opt => {
            return {
              ...opt,
              isDefault: opt.value === this.previewWidgetConfig.timeFrame
            }
          });

          // ====not using `selectedTimeFrame` from response
          // todo waiting for Hailey to confirm which way is used
          // if timeFrame is not clear -> set to `isCurrent`
          // if (_isEmpty(this.previewWidgetConfig.timeFrame)) {
          //   this.timeFrameOptions = timeFrameOptions.map(opt => {
          //     if (opt.isCurrent) {
          //       this.previewWidgetConfig.timeFrame = opt.value;
          //     }
          //     return {
          //       ...opt,
          //       isDefault: opt.isCurrent
          //     }
          //   });
          // } else { // timeFrame is clear
          //   this.timeFrameOptions = timeFrameOptions.map(opt => {
          //     return {
          //       ...opt,
          //       isDefault: opt.value === this.previewWidgetConfig.timeFrame
          //     }
          //   })
          // }
          this.refreshAccountsDropdownWidths()
        })
        .catch((err) => {
          this.widgetLoadingState[this.widgetConfig.index] = false;
          console.error(err);
          this.productPortionData = DEFAULT_WIDGET_DATA.PRODUCT_PORTION;
        });
      },
      setProductAccountDropdownOptions() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.productAccountDropdownOptions.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.productAccountDropdownOptions.options = DASHBOARD_DROPDOWN_OPTIONS
        }
        this.onReloadDateType(this.previewWidgetConfig.selectedVendorsByWidget)
      },
      saveWidget(widgetConfigForm) {
        let widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        this.$emit('save', widgetConfig);
        this.onReloadDateType(widgetConfig.selectedVendorsByWidget)
      },
      onChangeAccount(account) {
        if (this.previewWidgetConfig.selectedAccount === account) {
          return;
        }
        this.previewWidgetConfig.selectedAccount = account;
        this.loadDashboardPortionAccountWidget();
      },
      onChangeDateType(dateType) { // SelectBox : To check Monthly, Weekly
        if (this.previewWidgetConfig.dateType === dateType) {
          return;
        }
        this.previewWidgetConfig.dateType = dateType;
        this.previewWidgetConfig.timeFrame = PORTION_DEFAULT_TIME_FRAME;
        this.loadDashboardPortionAccountWidget()
      },
      onChangeTimeFrame(timeFrame) {
        if (this.previewWidgetConfig.timeFrame === timeFrame) {
          return;
        }
        this.previewWidgetConfig.timeFrame = timeFrame;
        this.loadDashboardPortionAccountWidget()
      },
      onSelectDropdownOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            this.exportCSVFile()
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_WIDGET: {
            this.$emit('delete');
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DUPLICATE_WIDGET: {
            this.$emit('duplicateWidget');
            break;
          }
        }
        this.$refs.productCostPopover.close();
      },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      },
      onProductPortionDateTypeDropdownMounted() {
        const selectedDateTypeOpt = this.dateTypeOptions.find(opt => opt.value === this.internalWidgetConfig.dateType);
        this.$refs.productPortionDateTypeDropdown.changeSelectedOptionText(selectedDateTypeOpt.text);
      },
      onProductPortionAccountDropdownMounted() {
        if (this.accountOptions[0]) {
          this.onChangeAccount(this.accountOptions[0].value)
        }
      },
      exportCSVFile() {
        //set default for timeFrame
        let timeFrame = _isEmpty(this.previewWidgetConfig.timeFrame) || this.previewWidgetConfig.timeFrame === PORTION_DEFAULT_TIME_FRAME
                ? this.timeFrameOptions[0].value
                : this.previewWidgetConfig.timeFrame;

        let workbook = new this.$excel.Workbook();
        initWorkBookViaExcelJs(workbook);
        let selectedAccount = this.productPortionData.accounts.find(account => account.item === this.widgetConfig.selectedAccount);
        let excelFileName = this.EXPORT_FILE_NAME;
        if (_isNil(selectedAccount)) {
          excelFileName += `${selectedAccount.vendor} ${_get(this.productPortionData, 'accounts[0].itemAlias')}(${_get(this.productPortionData, 'accounts[0].item')})`;
        } else {
          excelFileName += `${selectedAccount.vendor} ${selectedAccount.itemAlias}(${selectedAccount.item})`
        }

        let worksheet = workbook.addWorksheet(excelFileName);
        // add column headers
        let timeColumnsName = "";
        switch (this.previewWidgetConfig.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY:
            timeColumnsName = this.EXPORT_COLUMNS_NAMES.COST + this.formatMonthlyTimeFrameHeader(timeFrame);
            break;
          case DASHBOARD_DATE_TYPE.WEEKLY:
            let timeFrameParts = timeFrame.split("-");
            let weekNumber = timeFrameParts[0];
            let weeklyYear = timeFrameParts[1];

            let weekLyTimeFrame = dayjs().week(weekNumber);
            let dateFormat = getFullDateFormatByLocalization();
            timeColumnsName = `${this.EXPORT_COLUMNS_NAMES.COST} (${weekLyTimeFrame.startOf('week').set('year', weeklyYear).format(dateFormat)}  ~  ${weekLyTimeFrame.endOf('week').set('year', weeklyYear).format(dateFormat)})`;
            break;
          default:
            timeColumnsName = this.EXPORT_COLUMNS_NAMES.COST;
        }

        worksheet.columns = [
          {width: 30, header: this.EXPORT_COLUMNS_NAMES.PRODUCT_FAMILY},
          {width: 30, header: this.EXPORT_COLUMNS_NAMES.PRODUCT},
          {width: 15, header: this.EXPORT_COLUMNS_NAMES.VENDOR},
          {width: 10, header: timeColumnsName},
          {width: 10, header: this.EXPORT_COLUMNS_NAMES.PORTION}
        ];
        let rowData = prepareDataForExportProductPortionByAccount(this.csvExportData, this.internalCommonUserInfo.selectedCurrency, this.exchangeRate, this.currencySymbol);
        worksheet.addRows(rowData);
        saveAndReturnSupportedUTF18CSVFile(workbook, excelFileName);
      },
      formatMonthlyTimeFrameHeader(time) {
        return `(${formatMonthYearByLocalization(time.slice(4, 6), time.slice(0, 4))})`;
      },
      getDisplayTimePeriod() {
        if (_isEmpty(this.timeFrameOptions)) {
          return {
            startDate: '',
            endDate: ''
          };
        }
        let timeFrame = _isEmpty(this.previewWidgetConfig.timeFrame) || this.previewWidgetConfig.timeFrame === PORTION_DEFAULT_TIME_FRAME
                ? this.timeFrameOptions[0].value
                : this.previewWidgetConfig.timeFrame;
        switch (this.previewWidgetConfig.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY:
            let monthlyYear = timeFrame.slice(0, 4);
            let monthIndex = timeFrame.slice(4, 6) - 1;
            let monthlyTimeFrame = dayjs().month(monthIndex);
            let monthlyStartDate = monthlyTimeFrame.startOf('month').set('year', monthlyYear).format(TIME_PERIOD_DATE_FORMAT);
            let monthlyEndDate = monthlyTimeFrame.endOf('month').set('year', monthlyYear).format(TIME_PERIOD_DATE_FORMAT);

            if (monthlyStartDate === DAY_JS_INVALID_DATE || monthlyEndDate === DAY_JS_INVALID_DATE) {
              return {
                startDate: '',
                endDate: ''
              };
            }

            if (monthIndex === dayjs(this.latestSummarizedBillDate).month()) {
              monthlyEndDate = dayjs(this.latestSummarizedBillDate).format(TIME_PERIOD_DATE_FORMAT);
            }

            return {
              startDate: monthlyStartDate,
              endDate: monthlyEndDate
            };
          case DASHBOARD_DATE_TYPE.WEEKLY:
            let selectedTimeFrame = this.previewWidgetConfig.timeFrame
            let selectedTimeFrameList =  this.productPortionData.timeFrameList

            let weeklyStartDate = '';
            let weeklyEndDate = '';

            selectedTimeFrameList.some(
              function(item) {
                if (item.time === selectedTimeFrame){
                  let stDate = item.stDt.slice(0, 4)+'/'+item.stDt.slice(5, 7)+'/'+item.stDt.slice(8, 10);
                  let endDate = item.endDt.slice(0, 4)+'/'+item.endDt.slice(5, 7)+'/'+item.endDt.slice(8, 10);

                  weeklyStartDate = stDate;
                  weeklyEndDate = endDate;
                }
              }
            );
            // let timeFrameParts = timeFrame.split("-");
            // let weekNumber = timeFrameParts[0];
            // let weeklyYear = timeFrameParts[1];
            // let weekLyTimeFrame = dayjs().week(weekNumber);
            // let weeklyStartDate = weekLyTimeFrame.startOf('week').set('year', weeklyYear).format(TIME_PERIOD_DATE_FORMAT);
            // let weeklyEndDate = weekLyTimeFrame.endOf('week').set('year', weeklyYear).format(TIME_PERIOD_DATE_FORMAT);

            if (weeklyStartDate === DAY_JS_INVALID_DATE || weeklyEndDate === DAY_JS_INVALID_DATE) {
              return {
                startDate: '',
                endDate: ''
              };
            }
            // if (Number(weekNumber) === dayjs().utc().week()) {
            //   weeklyEndDate = dayjs.utc().format(TIME_PERIOD_DATE_FORMAT);
            // }
            return {
              startDate: weeklyStartDate,
              endDate: weeklyEndDate
            }
        }
      },
      getDefaultVendorByCheckedAuth(vendor){
        let rslt = !_isEmpty(this.allVendors) && this.allVendors.includes(vendor) ? vendor :this.allVendors.filter(v=> v != vendor).map(v=>{return v})[0];
        return rslt;
      },
      onReloadDateType(selectedVendorsByWidget){
        if(selectedVendorsByWidget.includes(SELECTED_VENDOR.NCP)){
          this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS.filter(option => option.value != DASHBOARD_DATE_TYPE.WEEKLY).map(opt => {
            return {
              ...opt,
              text: this.$t(opt.text)
            };
          });
        }else{
          this.dateTypeOptions = DASHBOARD_DATE_TYPE_OPTIONS
        }
      },
      getParsedProdName(category, item) {
        switch(this.selectedVendor.toUpperCase()) {
          case SELECTED_VENDOR.AZURE:
            let prodName = category + '|'
            if(item.name.indexOf("|") >= 0) {
              prodName = prodName + item.name.split("|")[0];
            }
            return prodName;
          case SELECTED_VENDOR.GCP:
            return item.name;
          default:
          case SELECTED_VENDOR.AWS:
            return item.childCode;
        }
      }
    },
  }
</script>
<style lang="scss">

</style>
