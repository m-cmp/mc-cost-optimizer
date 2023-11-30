<template>
  <b-card
    class="card-custom top-cost-wrapper"
    border-variant="transparent"
    header-border-variant="lightgray-1"
    header-bg-variant="transparent">
    <b-row
      slot="header"
      align-h="between"
      align-v="center"
      no-gutters
      class="dashboard-widget-header">
      <b-row
        class="dashboard-widget-header-left"
        no-gutters>
        <b-col cols="12">
          <div class="font-16 medium font-family-notosanscjkkr-medium top-5-cost-view-by">
            <span>{{ selectedVendor }} {{ $t('dashboard.topCost.top5CostBy.#1') }}</span>
            <BaseDropdown
              ref="top5CostViewByDropdown"
              :options="top5CostViewByOptions"
              :enabled-localization="true"
              :disabled="widgetLoadingState[widgetConfig.index]"
              class="custom-view-by-dropdown"
              @selectOption="onSelectOptionViewBy"
              @mounted="onTop5CostViewByDropdownMounted"
            />
            <span>{{ $t('dashboard.topCost.top5CostBy.#2') }}</span>
          </div>
        </b-col>
      </b-row>
      <b-row
        class="dashboard-widget-header-right">
        <BaseDropdown
          ref="top5TimeFrameDropdown"
          :options="top5TimeFrameOptions"
          :enabled-localization="true"
          :disabled="widgetLoadingState[widgetConfig.index]"
          variant="default"
          class="font-family-notosanscjkkr-medium custom-btn-top-5-time-frame-dropdown"
          @selectOption="onSelectTimeFrame"
          @mounted="onTop5TimeFrameDropdownMounted"
        />
        <b-button
          :id="dashboardDropdownOptions.dashboardDropdownOptionsId"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="dropdown-button"
          variant="transparent">
          <base-material
            :size="24"
            name="more_vert"
            color="gray-1"/>
        </b-button>
        <div
          :id="containerCustomPopoverId"
          class="custom-popover-top-cost"/>
        <BasePopoverDropdown
          id="top-5-cost-dropdown"
          ref="top5BaseDonutChartPopover"
          :target="dashboardDropdownOptions.dashboardDropdownOptionsId"
          :placement="dashboardDropdownOptions.placement"
          :options="dashboardDropdownOptions.options"
          :show-popover="dashboardDropdownOptions.showPopover"
          :enabled-localization="dashboardDropdownOptions.enabledLocalization"
          :container-custom-popover="containerCustomPopoverId"
          @selectOption="onSelectDropdownOption"
        />
      </b-row>
      <b-col cols="12">
        <BaseTimePeriod :time-period="timePeriod"/>
      </b-col>
    </b-row>
    <b-col
      class="px-10 top-cost-chart-wrapper">
      <BaseLoadingIndicator
        v-show="widgetLoadingState[widgetConfig.index]"
        :loading-height="366"
      />
      <div v-show="!widgetLoadingState[widgetConfig.index]">
        <!--
        <BaseDonutChart
          v-show="hasTopCostData"
          ref="top5BaseDonutChart"
          :colors="top5CostData.colors"
          :donut-data="top5CostData.pieData"
          :legend-width="50"
          :line-pattern-categories="linePatternCategories"
          :donut-chart-id="`top-5-cost-chart-${widgetConfig.index}`"
          :donut-chart-legend-id="`top-5-cost-chart-legend-${widgetConfig.index}`"
          :margin-left-legend-donut-chart="'-206px'"
          :loading-item-right-px="33"
          :enable-drill-down="enableDrillDown"
          inner-radius="65"
          @clickLegend="onClickLegend"
        />
        -->
        <NewBaseDonutChart
          v-show="hasTopCostData"
          ref="top5BaseDonutChart"
          :base-data="top5CostData"
          :legend-width="50"
          :line-pattern-categories="linePatternCategories"
          :donut-chart-id="`top-5-cost-chart-${widgetConfig.index}`"
          :donut-chart-legend-id="`top-5-cost-chart-legend-${widgetConfig.index}`"
          :margin-left-legend-donut-chart="'-206px'"
          :loading-item-right-px="33"
          :enable-drill-down="enableDrillDown"
          :value-prefix="currencySymbol"
          @clickLegend="onClickLegend"
        />
        <BaseNotificationNotSupport
          v-if="typeof selectedVendor == 'undefined'"
          :support-vendors="supportVendors"
          class="no-data"
        />
        <BaseNotificationNoData
          v-if="typeof selectedVendor != 'undefined' && !hasTopCostData"
          class="no-data"
        />
      </div>
    </b-col>
    <b-modal
      ref="edit-top-5-cost-form"
      v-model="widgetConfig.isEditFormVisible"
      :title="$t('dashboard.editWidget')"
      modal-class="right-wing"
      hide-footer
      hide-backdrop
    >
      <div v-if="widgetConfig.isEditFormVisible">
        <EditTopCostForm
          :common-user-info="internalCommonUserInfo"
          :exchange-rate="internalExchangeRate"
          :dashboard-view-mode="dashboardViewMode"
          :widget-config="internalWidgetConfig"
          :all-vendors="allVendors"
          @hideModal="hideModal"
          @save="onSaveEditTop5Form"
        />
      </div>
    </b-modal>
  </b-card>
</template>
<script>
  import { prepareDataForTop5Cost, isTop5WidgetDataConfigChanged, getSelectedVendorsByWidget,availableVendors } from '@/util/dashboardUtils';
  import NewBaseDonutChart from '@/components/common/base-charts/base-donut-chart/NewBaseDonutChart';
  import BaseDropdown from '@/components/common/BaseDropdown';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNotSupport from '@/components/common/BaseNotificationNotSupport';
  import {
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DEFAULT_TOP_COST_WIDGET_CONFIG,
    OTHER_LINE_PATTERNS,
    TOP_5_TIME_FRAME,
    TOP_5_TIME_FRAME_OPTIONS,
    DASHBOARD_WIDGET_TYPE,
    DEFAULT_WIDGET_DATA,
    DASHBOARD_VIEW_BY_OPTIONS,
  } from '@/constants/dashboardConstants';
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from '@/constants/costAnalyticsConstants';
  const EditTopCostForm = () => import('@/components/pages/dashboard/top-cost/EditTopCostForm');
  import { DEFAULT_CURRENCY, CURRENCY, CURRENCY_SYMBOL, DEFAULT_EXCHANGE_RATE, FILE_TYPE } from '@/constants/constants';
  import {
    COST_MONTH_TO_DATE_VIEW_BY_VENDORS,
    DASHBOARD_VIEW_BY, DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR,
    SELECTED_VENDOR,
    TOP_5_VIEW_BY_VENDORS,
    VIEW_MODE
  } from '../../../../constants/dashboardConstants';
  import _isEqual from 'lodash/isEqual';
  import ShowEditFormModalMixin from '@/mixins/ShowEditFormModalMixin';
  import _cloneDeep from 'lodash/cloneDeep';
  import _isNil from 'lodash/isNil';
  import _isEmpty from 'lodash/isEmpty';
  import {fetchTop5Cost} from '@/api/dashboard';
  import dayjs from 'dayjs';
  import BaseTimePeriod from "@/components/common/BaseTimePeriod";
  import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
  import _get from "lodash/get";
  import {initWorkBookViaExcelJs, saveAndReturnSupportedUTF18CSVFile} from "@/util/excelJS";

  const DATE_FORMAT = 'YYYY-MM-DD';
  const AWS_OTHERS = 'Others (AWS)';
  const AZURE_OTHERS = 'Others (AZURE)';
  const GCP_OTHERS = 'Others (GCP)';
  const ALI_OTHERS = 'Others (ALI)';
  const TENCENT_OTHERS = 'Others (TECENT)';
  const TIME_PERIOD_DATE_FORMAT = "MM/DD/YYYY";
  const OTHERS_TEXT = 'others'
  const OTHERS_UPPER_TEXT = 'Others'

  export default {
    name: 'DashboardTopCost',
    components: {
      NewBaseDonutChart,
      EditTopCostForm,
      BaseDropdown,
      BaseNotificationNoData,
      BaseLoadingIndicator,
      BaseTimePeriod,
      BaseNotificationNotSupport
    },
    mixins: [ShowEditFormModalMixin],
    props: {
      commonUserInfo: {
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
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      widgetConfig: {
        type: Object,
        required: true
      },
      dashboardViewMode: {
        type: String,
        default: VIEW_MODE.DEFAULT
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
    },
    data() {
      return {
        top5Cost: DEFAULT_WIDGET_DATA.TOP_COST,
        internalWidgetConfig: DEFAULT_TOP_COST_WIDGET_CONFIG,
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendors: []
        },
        internalExchangeRate: DEFAULT_EXCHANGE_RATE,
        previewWidgetConfig: {
          timeFrame: TOP_5_TIME_FRAME.LAST_14_DAYS,
        },

        dashboardDropdownOptions: {
          dashboardDropdownOptionsId: `dashboard-dropdown-options-${this.widgetConfig.index}`,
          options: DASHBOARD_DROPDOWN_OPTIONS,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        donutChartsExportColumns: null,
        containerCustomPopoverId: `top-5-cost-custom-popover-${this.widgetConfig.index}`,
        top5TimeFrameOptions: TOP_5_TIME_FRAME_OPTIONS,
        linePatternCategories: OTHER_LINE_PATTERNS,
        top5CostViewByOptions: DASHBOARD_VIEW_BY_OPTIONS,
        supportVendors: TOP_5_VIEW_BY_VENDORS
      }
    },
    computed: {
      i18nViewBy() {
        const langKey = `dashboard.viewByOption.${this.internalWidgetConfig.viewBy}`;
        return this.$t(langKey);
      },
      top5CostData: function () {
        if (_isEmpty(this.top5Cost)) {
          return {};
        }
        let dataForTop5Cost = prepareDataForTop5Cost(this.top5Cost, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, OTHERS_TEXT, OTHERS_UPPER_TEXT, this.previewWidgetConfig.viewBy);
        if(dataForTop5Cost.datasets[0].data.length !== dataForTop5Cost.datasets[0].backgroundColor.length) {
          dataForTop5Cost.datasets[0].backgroundColor.push('#dee2e6');
        }
        return dataForTop5Cost;
      },
      currencySymbol: function () {
        return CURRENCY_SYMBOL[this.internalCommonUserInfo ? this.internalCommonUserInfo.selectedCurrency : CURRENCY.USD];
      },
      hasTopCostData() {
        return !_isEmpty(this.top5Cost) && !_isEmpty(this.top5Cost.cost);
      },
      timePeriod: function () {
        let timePeriod = getDateTimePeriod(this.previewWidgetConfig.timeFrame, TIME_PERIOD_DATE_FORMAT, this).split(" ~ ");
        if(_isNil(this.selectedVendor)){
          return {
            startDate: '',
            endDate: ''
          };
        }else{
          return {
            startDate: timePeriod[0],
            endDate: timePeriod[1],
          }
        }
      },
      // selectedVendor: {
      //   get() {
      //     return (!_isEmpty(this.internalWidgetConfig.selectedVendorsByWidget) && !_isEqual(this.internalWidgetConfig.selectedVendorsByWidget[0], ''))
      //       ? this.internalWidgetConfig.selectedVendorsByWidget[0]
      //       : this.commonUserInfo.selectedVendors[0];
      //   }
      // },
      selectedVendor: {
        get() {
          return getSelectedVendorsByWidget(this.internalWidgetConfig, this, TOP_5_VIEW_BY_VENDORS, true);
        }
      },
      enableDrillDown: {
        get() {
          if(_isEqual(this.profile.env, "CHINA") && _isEqual(this.selectedVendor, "GCP"))
            return false
          return this.Common.checkCostAnalyticsMenuAuth(this) && this.Common.checkVendorAvailableFromSelectedVendor(getSelectedVendorsByWidget(this.internalWidgetConfig, this, TOP_5_VIEW_BY_VENDORS),COST_ANALYTICS_VIEW_BY_VENDORS)
        }
      }
    },
    watch: {
      dashboardViewMode: function () {
        this.setDashboardDropdownOptions();
      },
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
          if (!isTop5WidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if(_isNil(this.allVendors)){
            return;
          }
          if (!isTop5WidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            //In case user change preview widget config in dashboard page & click to open edit form modal but make no change and then click save button
            // -> we have to update preview widget config like edit form modal
            if (isTop5WidgetDataConfigChanged(this.previewWidgetConfig, this.widgetConfig, true)) {
              if(_isNil(this.widgetConfig.isNew)){
                this.updatePreviewWidgetConfig();
              }
            } else {
              this.hideModal();
            }
            return;
          }
          this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: true
      },
      internalWidgetConfig: {
        handler() {
          if(_isNil(this.allVendors)){
            return;
          }
          if (_isNil(this.widgetConfig)) {
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
          const widgetConfig = {
            ...this.internalWidgetConfig,
            ...this.previewWidgetConfig
          };
          this.loadTopCost(widgetConfig)
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
    },
    mounted() {
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setDashboardDropdownOptions();
    },
    methods: {
      onTop5CostViewByDropdownMounted() {
        const selectedTop5CostViewByOpt = this.top5CostViewByOptions.find(opt => opt.value === this.internalWidgetConfig.viewBy);
        if(!_isEmpty(this.$refs.top5CostViewByDropdown)){
          this.$refs.top5CostViewByDropdown.changeSelectedOptionText(selectedTop5CostViewByOpt.text);
        }
      },
      onSelectOptionViewBy(option) {
        if (this.previewWidgetConfig.viewBy === option) {
          return;
        }
        this.previewWidgetConfig.viewBy = option;
        this.internalWidgetConfig.viewBy = option;
        let widgetConfig = {
          ...this.internalWidgetConfig,
          ...this.previewWidgetConfig
        };
        this.loadTopCost(widgetConfig)
      },
      updatePreviewWidgetConfig() {
        this.previewWidgetConfig = {
          viewBy: this.internalWidgetConfig.viewBy,
          timeFrame: this.internalWidgetConfig.timeFrame,
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, TOP_5_VIEW_BY_VENDORS)]
        };
        if(this.previewWidgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP)) {
          if(_isEqual(this.internalWidgetConfig.viewBy, "account")) {
            this.internalWidgetConfig.viewBy = "project";
          }
        }
        this.top5CostViewByOptions = this.funcTop5CostViewByOptions(this.previewWidgetConfig.selectedVendorsByWidget[0]);
        if (!_isNil(this.$refs.top5TimeFrameDropdown)) {
          const selectedTimeFrameOpt = this.top5TimeFrameOptions.find(opt => opt.value === this.internalWidgetConfig.timeFrame);
          this.$refs.top5TimeFrameDropdown.changeSelectedOptionText(selectedTimeFrameOpt.text);
        }
        if (!_isNil(this.$refs.top5CostViewByDropdown)) {
          const selectedTop5CostViewByOpt = this.top5CostViewByOptions.find(opt => opt.value === this.internalWidgetConfig.viewBy);
          this.$refs.top5CostViewByDropdown.changeSelectedOptionText(selectedTop5CostViewByOpt.text);
        }
        this.loadTopCost(this.internalWidgetConfig);
      },
      setDashboardDropdownOptions() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.dashboardDropdownOptions.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.dashboardDropdownOptions.options = DASHBOARD_DROPDOWN_OPTIONS
        }
      },
      loadTopCost(widgetConfig) {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;
        let payload = {
          viewBy: widgetConfig.viewBy,
          timeFrame: widgetConfig.timeFrame,
          widgetCurrency : this.$store.state.common.info.currencies.KRW,
          vendors: this.allVendors.filter(vendor => this.internalCommonUserInfo.selectedVendors),
          selectedVendorsByWidget: [getSelectedVendorsByWidget(this.internalWidgetConfig, this, TOP_5_VIEW_BY_VENDORS)]
        };
        fetchTop5Cost(payload)
          .then(res => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.top5Cost = res;
            this.onTop5CostViewByDropdownMounted();
          })
          .catch(err => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            console.error(err);
            this.top5Cost = DEFAULT_WIDGET_DATA.TOP_COST;
            this.onTop5CostViewByDropdownMounted();
          })
      },
      onSelectDropdownOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            let fileName = `Top5 ${this.i18nViewBy}(${this.commonUserInfo.selectedCurrency})`;

            let workbook = new this.$excel.Workbook();
            initWorkBookViaExcelJs(workbook);
            let worksheet = workbook.addWorksheet(fileName);

            worksheet.columns = [
              {
                width: 50,
                header: this.$t(`dashboard.topCost.download.${this.internalWidgetConfig.viewBy}`)
              },
              {
                width: 30,
                header: this.$t('dashboard.topCost.download.vendor')
              },
              {
                width: 40,
                header: getDateTimePeriod(this.previewWidgetConfig.timeFrame, getFullDateFormatByLocalization(), this)
              },
              {
                width: 30,
                header: this.$t('dashboard.topCost.download.percentage')
              }
            ];

            let rowData = [];
            for(let i =0; i<this.top5CostData.labels.length; i++){
              let data = [
                this.top5CostData.name[i],
                this.top5CostData.vendor[i],
                this.top5CostData.date[i],
                this.top5CostData.percentage[i]
              ];
              rowData.push(data);
            }

            worksheet.addRows(rowData);
            saveAndReturnSupportedUTF18CSVFile(workbook, fileName);
            break;
          }
          /* back_up
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            let fileName = `Top5 ${this.i18nViewBy}(${this.commonUserInfo.selectedCurrency})`;
            let dateField = getDateTimePeriod(this.previewWidgetConfig.timeFrame, getFullDateFormatByLocalization(), this)
            let nameField = this.$t(`dashboard.topCost.download.${this.internalWidgetConfig.viewBy}`)
            let dataField = {
              name: nameField,
              vendor: this.$t('dashboard.topCost.download.vendor'),
              date: dateField,
              percentage: this.$t('dashboard.topCost.download.percentage'),
            };
            this.$refs.top5BaseDonutChart.exportChart(FILE_TYPE.XLSX, fileName, dataField)
            break;
          }
           */
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_WIDGET: {
            this.$emit('delete');
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DUPLICATE_WIDGET: {
            this.$emit('duplicateWidget');
            break;
          }
        }
        this.$refs.top5BaseDonutChartPopover.close();
      },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      },
      onSelectTimeFrame(selectedTimeFrame) {
        if (this.previewWidgetConfig.timeFrame === selectedTimeFrame) {
          return;
        }
        this.previewWidgetConfig.timeFrame = selectedTimeFrame;
        let widgetConfig = {
          ...this.internalWidgetConfig,
          ...this.previewWidgetConfig
        };
        this.loadTopCost(widgetConfig);
      },
      onSaveEditTop5Form(widgetConfigForm) {
        let widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        this.$emit('save', widgetConfig);
      },
      onTop5TimeFrameDropdownMounted() {
        const selectedTimeFrameOpt = this.top5TimeFrameOptions.find(opt => opt.value === this.internalWidgetConfig.timeFrame);
        this.$refs.top5TimeFrameDropdown.changeSelectedOptionText(selectedTimeFrameOpt.text);
      },
      funcTop5CostViewByOptions(selectedVendorsByWidget){
        if(!_isEmpty(DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget])){
          return DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget]
        }else{
          return DASHBOARD_VIEW_BY_OPTIONS
        }
      },
      onClickLegend() {
        if(!this.enableDrillDown){
          return
        }
        const latestSummarizedBillDate = _get(this.top5Cost, 'latestSummarizedBillDate')
        let startDate = ''
        let endDate = dayjs(latestSummarizedBillDate).format(DATE_FORMAT);
        switch (this.previewWidgetConfig.timeFrame) {
          case TOP_5_TIME_FRAME.MONTH_TO_DATE: {
            startDate = dayjs.utc().startOf('month').format(DATE_FORMAT);
            break;
          }
          case TOP_5_TIME_FRAME.LAST_7_DAYS: {
            startDate = dayjs(latestSummarizedBillDate).subtract(6, 'day').format(DATE_FORMAT);
            break;
          }
          case TOP_5_TIME_FRAME.LAST_10_DAYS: {
            startDate = dayjs(latestSummarizedBillDate).subtract(9, 'day').format(DATE_FORMAT);
            break;
          }
          case TOP_5_TIME_FRAME.LAST_14_DAYS: {
            startDate = dayjs(latestSummarizedBillDate).subtract(13, 'day').format(DATE_FORMAT);
            break;
          }
          case TOP_5_TIME_FRAME.LAST_30_DAYS: {
            startDate = dayjs(latestSummarizedBillDate).subtract(29, 'day').format(DATE_FORMAT);
            break;
          }
          case TOP_5_TIME_FRAME.LAST_60_DAYS: {
            startDate = dayjs(latestSummarizedBillDate).subtract(59, 'day').format(DATE_FORMAT);
            break
          }
        }
        let indexArray = [];
        for(let i =0; i<this.top5CostData.name.length; i++){
          if(![AWS_OTHERS, ALI_OTHERS, GCP_OTHERS, AZURE_OTHERS, TENCENT_OTHERS].includes(this.top5CostData.name[i])){
            indexArray.push(i);
          }
        }

        // back_up : 현재 로직엔 맞지 않음
        // let top5CostData = this.top5CostData.pieData.filter(item => {
        //   return ![AWS_OTHERS, ALI_OTHERS, GCP_OTHERS, AZURE_OTHERS, TENCENT_OTHERS].includes(item.name)
        // })

        let payload =  {
          selectedVendor : this.selectedVendor,
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET,
          viewBy: this.internalWidgetConfig.viewBy,
          startDate: startDate,
          endDate: endDate,
          data: indexArray.map(item => {
            return {
              dataKey: this.top5CostData.item[item],
              vendor: this.top5CostData.vendor[item]
            }
          })
          // back_up : 현재 로직엔 맞지 않음
          // data: top5CostData.map(item => {
          //   return {
          //     dataKey: item.item,
          //     vendor: item.vendor
          //   }
          // })
        }
        this.$emit('clickToAnalyze', payload)
      }
    },
  };
  function getDateTimePeriod(timeFrame, dateFormat, $vm) {
    const latestSummarizedBillDate = _get($vm.top5Cost ,'latestSummarizedBillDate');
    switch (timeFrame) {
      case TOP_5_TIME_FRAME.LAST_7_DAYS:
       return `${dayjs(latestSummarizedBillDate).subtract(6,'day').format(dateFormat)} ~ ${dayjs(latestSummarizedBillDate).subtract(0,'day').format(dateFormat)}`;
      case TOP_5_TIME_FRAME.LAST_10_DAYS:
        return`${dayjs(latestSummarizedBillDate).subtract(9,'day').format(dateFormat)} ~ ${dayjs(latestSummarizedBillDate).subtract(0,'day').format(dateFormat)}`;
      case TOP_5_TIME_FRAME.LAST_14_DAYS:
        return`${dayjs(latestSummarizedBillDate).subtract(13,'day').format(dateFormat)} ~ ${dayjs(latestSummarizedBillDate).subtract(0,'day').format(dateFormat)}`;
      case TOP_5_TIME_FRAME.LAST_30_DAYS:
        return`${dayjs(latestSummarizedBillDate).subtract(29,'day').format(dateFormat)} ~ ${dayjs(latestSummarizedBillDate).subtract(0,'day').format(dateFormat)}`;
      case TOP_5_TIME_FRAME.LAST_60_DAYS:
        return`${dayjs(latestSummarizedBillDate).subtract(59,'day').format(dateFormat)} ~ ${dayjs(latestSummarizedBillDate).subtract(0,'day').format(dateFormat)}`;
      case TOP_5_TIME_FRAME.MONTH_TO_DATE:
        return`${dayjs(latestSummarizedBillDate).startOf('month').format(dateFormat)} ~ ${dayjs(latestSummarizedBillDate).format(dateFormat)}`;
    }
    return '';
  }
</script>
<style lang="scss">
  .top-cost-wrapper {
    .dashboard-widget-header {
      padding: 5px 12px 5px 20px !important;
      height: 55px;
      .dashboard-widget-header-left {
        .top-5-cost-view-by {
          display: flex;
          span {
            line-height: 26px;
          }
        }
      }
      .dashboard-widget-header-right {
        height: 24px;
        padding-top: 8px;
        .base-dropdown {
          .dropdown-menu {
            min-width: 70px !important;
            li {
              a {
                padding-left: 18px !important;
                padding-right: 18px !important;
              }
            }
          }
        }
      }
    }
    .top-cost-chart-wrapper {
      min-height: 350px;
      padding-left : 0 !important;
      padding-right: 0 !important;
      background: white;
      .no-data {
        width: 360px;
      }
    }
  }
  #top-5-cost-chart {
    height: 380px;
  }
  .custom-btn-top-5-time-frame-dropdown {
    height: 100%;
    .base-dropdown {
      button {
        padding-top: 2px !important;
        padding-right: 0 !important;
      }
    }
  }
  .custom-popover-top-cost {
    .popover {
      top: 0 !important;
      left: 16px !important;
      .arrow {
        left: 75% !important;
      }
      .arrow:before {
        border-bottom-color: #ffffff;
      }
    }
  }
  .modal-dialog {
    height: 94.7%;
  }
  .pbt-9 {
    padding-top: 9px;
    padding-bottom: 9px;
  }
  @media screen and (-ms-high-contrast: active), (-ms-high-contrast: none) {
    .top-cost-header {
      .top-cost-header-left {
        display: inline-block!important;
        float: left;
      }
      .top-cost-header-right {
        display: inline-block!important;
        float: right;
        .custom-btn-top-5-time-frame-dropdown {
          display: inline-block;
        }
        .dropdown-button {
          display: inline-block;
        }
      }
    }
  }
</style>
