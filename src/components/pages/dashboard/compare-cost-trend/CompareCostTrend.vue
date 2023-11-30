<template>
  <b-card
    class="card-custom compare-cost-trend-wrapper"
    border-variant="transparent"
    header-border-variant="lightgray-1"
    header-bg-variant="transparent">
    <b-row
      slot="header"
      align-h="between"
      align-v="center"
      no-gutters
      class="px-20 py-16 dashboard-widget-header compare-cost-trend">
      <span
        class="medium font-16 color-darkgray-1 font-family-notosanscjkkr-medium"
      > {{ selectedVendor }} {{ $t('dashboard.compareCostTrend.compareCostTrend') }}
      </span>
      <b-row no-gutters>
        <b-button-group
          size="sm">
          <b-button
            :pressed="previewWidgetConfig.timeFrame === COMPARE_COST_TREND_TIME_FRAME.LAST_2_MONTHS"
            :disabled="widgetLoadingState[widgetConfig.index]"
            class="custom-color blue-1 icon-only box-shadow-none time-frame-button left"
            variant="outline-gray-4"
            @click="onChangeTimeFrame(COMPARE_COST_TREND_TIME_FRAME.LAST_2_MONTHS)"
          >
            {{ $t('dashboard.compareCostTrend.timeFrameOptions.twoMonths') }}
          </b-button>
          <b-button
            :pressed="previewWidgetConfig.timeFrame === COMPARE_COST_TREND_TIME_FRAME.LAST_3_MONTHS"
            :disabled="widgetLoadingState[widgetConfig.index]"
            class="custom-color blue-1 icon-only box-shadow-none time-frame-button"
            variant="outline-gray-4"
            @click="onChangeTimeFrame(COMPARE_COST_TREND_TIME_FRAME.LAST_3_MONTHS)"
          >
            {{ $t('dashboard.compareCostTrend.timeFrameOptions.threeMonths') }}

          </b-button>
          <b-button
            :pressed="previewWidgetConfig.timeFrame === COMPARE_COST_TREND_TIME_FRAME.LAST_6_MONTHS"
            :disabled="widgetLoadingState[widgetConfig.index]"
            class="custom-color blue-1 icon-only box-shadow-none time-frame-button right"
            variant="outline-gray-4"
            @click="onChangeTimeFrame(COMPARE_COST_TREND_TIME_FRAME.LAST_6_MONTHS)"
          >
            {{ $t('dashboard.compareCostTrend.timeFrameOptions.sixMonths') }}

          </b-button>
        </b-button-group>
        <b-button
          :id="detailTableOption.detailTableOptionBtnId"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="dropdown-button in-compare-cost-trend"
          variant="transparent">
          <base-material
            :size="24"
            name="more_vert"
            color="gray-1"/>
        </b-button>
        <div
          :id="containerCustomPopover"
          class="custom-popover-compare-cost-trend"
        />
        <BasePopoverDropdown
          ref="compareCostTrendPopover"
          :target="detailTableOption.detailTableOptionBtnId"
          :placement="detailTableOption.placement"
          :options="detailTableOption.options"
          :show-popover="detailTableOption.showPopover"
          :enabled-localization="detailTableOption.enabledLocalization"
          :container-custom-popover="containerCustomPopover"
          @selectOption="onSelectOption"
        />
      </b-row>
      <b-modal
        ref="edit-compare-cost-trend"
        v-model="widgetConfig.isEditFormVisible"
        :title="$t('dashboard.editWidget')"
        modal-class="right-wing"
        hide-footer
        hide-backdrop
      >
        <div v-if="widgetConfig.isEditFormVisible">
          <EditCompareCostTrend
            :common-user-info="internalCommonUserInfo"
            :exchange-rate="internalExchangeRate"
            :widget-config="previewWidgetConfig"
            :dashboard-view-mode="dashboardViewMode"
            :marker-height="markerHeight"
            :category-min-grid-distance="categoryMinGridDistance"
            :category-labels-color="categoryLabelsColor"
            :legend-padding-left="legendPaddingLeft"
            :legend-padding-bottom="legendPaddingBottom"
            :legend-padding-right="legendPaddingRight"
            :value-axis-number-format="valueAxisNumberFormat"
            :value-axis-labels-color="valueAxisLabelsColor"
            :marker-width="markerWidth"
            :currency-symbol="currencySymbol"
            :all-vendors="allVendors"
            @apply="applySaveWidget"
            @hideModal="hideModal"/>
        </div>
      </b-modal>
    </b-row>
    <div v-show="widgetLoadingState[widgetConfig.index]">
      <b-col
        class="dashboard-widget-height"
      >
        <BaseLoadingIndicator/>
      </b-col>
    </div>
    <div v-show="!widgetLoadingState[widgetConfig.index]">
      <b-col
        v-show="!hasCompareCostTrendData"
        class="dashboard-widget-height"
      >
        <BaseNotificationNoData v-if="typeof selectedVendor != 'undefined'"/>
        <BaseNotificationNotSupport
          v-else
          :support-vendors="supportVendors"
          class="no-data"
        />
      </b-col>
      <b-col v-show="hasCompareCostTrendData">
        <!--Back_up-->
        <!--
        <BaseLineAreaChart
          :line-chart-area-id="`line-chart-area-${widgetConfig.index}`"
          :line-chart-data="lineChartDataMapping"
          :category-labels-color="categoryLabelsColor"
          :value-axis-labels-color="valueAxisLabelsColor"
          :value-axis-number-format="valueAxisNumberFormat"
          :legend-padding-left="legendPaddingLeft"
          :legend-padding-bottom="legendPaddingBottom"
          :legend-padding-right="legendPaddingRight"
          :legend-padding-top="legendPaddingTop"
          :category-min-grid-distance="categoryMinGridDistance"
          :mapping-keys-with-label="mappingKeysWithLabel"
          :mapping-keys-with-colors="mappingMonthWithColors"
          :can-show-tooltip="true"
          :value-prefix="currencySymbol"
          :first-time-render-timeout="widgetConfig.firstTimeRenderTimeout"
          :loading-item-right-px="33"
          :use-custom-tooltip="browser === BROWSER.IE"
          :enable-drill-down="enableDrillDown"
          @hitLegend="onClickLegendCompareCostTrendChart"
        />
        -->
        <NewBaseLineAreaChart
          :base-data="lineChartDataMapping"
          :chart-width-value="300"
          :chart-height-value="310"
          :legend-padding="20"
          :value-prefix="currencySymbol"
          @hitLegend="onClickLegendCompareCostTrendChart"
        />
      </b-col>
    </div>
  </b-card>
</template>

<script>
  /* eslint-disable no-param-reassign */
  import NewBaseLineAreaChart from '@/components/common/base-charts/base-line-area-chart/NewBaseLineAreaChrat';
  import {BROWSER, CURRENCY, CURRENCY_SYMBOL, DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import _isEmpty from 'lodash/isEmpty';
  import _isNil from 'lodash/isNil';
  import _isEqual from 'lodash/isEqual';
  import {
    COMPARE_COST_TREND_TIME_FRAME,
    CONST_TREND_TIME_FRAME,
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DEFAULT_COMPARE_COST_TREND_WIDGET_CONFIG,
    DEFAULT_WIDGET_DATA,
    VIEW_MODE,
    DASHBOARD_VIEW_BY,
  } from '@/constants/dashboardConstants';
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from '@/constants/costAnalyticsConstants';
  import _cloneDeep from 'lodash/cloneDeep';
  import {
    getMappingMonthWithColors,
    isCompareCostTrendWidgetDataConfigChanged,
    getCompareCostTrendChartData,
    mappingKeysWithLabelForTrend,
    getSelectedVendorsByWidget ,
    availableVendors
  } from '@/util/dashboardUtils';
  import { fetchDataCompareCostTrend } from '@/api/dashboard';
  import { initWorkBookViaExcelJs, saveAndReturnSupportedUTF18CSVFile } from '@/util/excelJS';
  import {calculateCostByCurrencyForExport, formatCostForExport} from '@/util/costUtils';
  import dayjs from 'dayjs';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNotSupport from '@/components/common/BaseNotificationNotSupport';
  import {getMonthYearDateFormatByLocalization} from "@/util/dateTimeUtils";
  import {
    COMPARE_COST_TREND_VENDORS,
    COST_MONTH_TO_DATE_VIEW_BY_VENDORS, YEAR_COST_FCST_VIEW_BY_VENDORS
  } from "../../../../constants/dashboardConstants";
  import {Trans} from "@/components/common/base-i18n/Translation";
  import {SUPPORTED_LANGUAGE} from "@/constants/trans";

  const EditCompareCostTrend = () => import('@/components/pages/dashboard/compare-cost-trend/EditCompareCostTrend');
  const EDIT_WIDGET = 'edit-widget'
  const CUSTOMIZE_WIDGET = 'customize-widget'
  const DATE_FORMAT = 'YYYY-MM-DD'

  export default {
    name: 'CompareCostTrend',
    components: {
      NewBaseLineAreaChart,
      EditCompareCostTrend,
      BaseNotificationNoData,
      BaseLoadingIndicator,
      BaseNotificationNotSupport
    },
    props: {
      commonUserInfo: {
        type: Object,
        required: true
      },
      widgetConfig: {
        type: Object,
        required: true,
        default: null
      },
      allVendors: {
        type: Array,
        required: true,
        default() {
          return []
        }
      },
      dashboardViewMode: {
        type: String,
        require: true,
        default: ''
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      // currentStep: {
      //   type: Number,
      //   required: true
      // },
      compareCostTrendGuideIndex: {
        type: Number,
        default: -1
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
      browser: {
        type: String,
        default: BROWSER.CHROME
      }
    },
    data() {
      return {
        legendPaddingLeft: 30,
        legendPaddingBottom: 1,
        legendPaddingRight: 20,
        legendPaddingTop: 10,
        markerWidth: 10,
        markerHeight: 10,
        categoryField: 'time',
        categoryMinGridDistance: 10,
        CONST_TREND_TIME_FRAME: CONST_TREND_TIME_FRAME,
        COMPARE_COST_TREND_TIME_FRAME: COMPARE_COST_TREND_TIME_FRAME,
        containerCustomPopover: `compare-cost-trend-custom-popover-${this.widgetConfig.index}`,
        detailTableOption: {
          options: DASHBOARD_DROPDOWN_OPTIONS,
          detailTableOptionBtnId: `detail-table-option-btn-${this.widgetConfig.index}`,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        dashboardCompareCostTrend: DEFAULT_WIDGET_DATA.COMPARE_COST_TREND,
        endDate:'',
        internalWidgetConfig: DEFAULT_COMPARE_COST_TREND_WIDGET_CONFIG,
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendorsByWidget: []
        },
        internalExchangeRate: DEFAULT_EXCHANGE_RATE,
        previewWidgetConfig: {
          timeFrame: COMPARE_COST_TREND_TIME_FRAME.LAST_2_MONTHS,
          selectedVendorsByWidget: []
        },
        EDIT_WIDGET: EDIT_WIDGET,
        CUSTOMIZE_WIDGET: CUSTOMIZE_WIDGET,
        BROWSER: BROWSER,
        supportVendors: COMPARE_COST_TREND_VENDORS
      };
    },
    computed: {
      lineChartDataMapping: {
        cache: true,
        get() {
          if (_isEmpty(this.dashboardCompareCostTrend && this.dashboardCompareCostTrend.trendCost)) {
            return {};
          }
          let dashboardTrendCost = _cloneDeep(this.dashboardCompareCostTrend.trendCost);
          let compareCost = getCompareCostTrendChartData(dashboardTrendCost, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate);
          compareCost.datasets.forEach(element => {
            if(element.label === "thisMonth"){
              element.label = this.$t('dashboard.compareCostTrend.thisMonth');
            }
          });
          return compareCost;
        }
      },
      mappingKeysWithLabel: {
        cache: true,
        get() {
          if (_isEmpty(this.lineChartDataMapping)) {
            return {};
          }
          return mappingKeysWithLabelForTrend(this.lineChartDataMapping);
        }
      },
      mappingMonthWithColors: {
        cache: true,
        get() {
          if (_isEmpty(this.lineChartDataMapping)) {
            return {};
          }
          return getMappingMonthWithColors(this.lineChartDataMapping);
        }
      },
      currencySymbol: function () {
        return CURRENCY_SYMBOL[this.internalCommonUserInfo ? this.internalCommonUserInfo.selectedCurrency : CURRENCY.USD];
      },
      valueAxisNumberFormat: function () {
        return `${this.currencySymbol}#a`;
      },
      hasCompareCostTrendData() {
        if (!this.dashboardCompareCostTrend || _isEmpty(this.dashboardCompareCostTrend.trendCost)) {
          return false;
        }
        return this.dashboardCompareCostTrend.trendCost.some(trendCostByDate => trendCostByDate && !_isEmpty(trendCostByDate.monthlyCost));
      },
      selectedVendor: {
        get() {
          let vendor = getSelectedVendorsByWidget(this.widgetConfig, this, COMPARE_COST_TREND_VENDORS, true);
          return vendor;
        }
      },
      enableDrillDown: {
        get() {
          if(_isEqual(this.profile.env, "CHINA") && _isEqual(this.selectedVendor, "GCP"))
            return false
          return this.Common.checkCostAnalyticsMenuAuth(this) && this.Common.checkVendorAvailableFromSelectedVendor(getSelectedVendorsByWidget(this.widgetConfig, this, COMPARE_COST_TREND_VENDORS),COST_ANALYTICS_VIEW_BY_VENDORS)
        }
      }
    },
    watch: {
      // 'commonUserInfo.selectedVendors': {
      //   handler(newSelectedVendors) {
      //     if (_isEqual(this.internalCommonUserInfo.selectedVendors, newSelectedVendors)) {
      //       return;
      //     }
      //     this.internalCommonUserInfo.selectedVendors = _cloneDeep(this.commonUserInfo.selectedVendors);
      //   },
      //   immediate: true
      // },
      dashboardViewMode: function () {
        this.setDetailTableOption();
      },
      widgetConfig: {
        handler(newValue, oldValue) {
          if (!isCompareCostTrendWidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if(_isNil(this.allVendors)){
            return;
          }
          if (!isCompareCostTrendWidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            //In case user change preview widget config in dashboard page & click to open edit form modal but make no change and then click save button
            // -> we have to update preview widget config like edit form modal
            if (isCompareCostTrendWidgetDataConfigChanged(this.previewWidgetConfig, this.widgetConfig, true)) {
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
      allVendors: {
        handler() {
          if(_isNil(this.widgetConfig)){
            return;
          }
          this.internalWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: false
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
      // 'internalCommonUserInfo.selectedVendorsByWidget': {
      //   handler() {
      //     const widgetConfig = {
      //       ...this.internalWidgetConfig,
      //       ...this.previewWidgetConfig
      //     };
      //     this.loadCompareCostTrend(widgetConfig)
      //   },
      //   immediate: false
      // },
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
    mounted(){
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setDetailTableOption();
    },
    methods: {
      onClickLegendCompareCostTrendChart(monthYearString) {
        if(!this.enableDrillDown){
          return
        }
        let payload = {
          selectedVendor : this.selectedVendor,
          viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
          startDate: '',
          endDate: ''
        };
        const monthYearObj = getMonthYearObj(monthYearString);
        if (monthYearString === this.$t('dashboard.compareCostTrend.thisMonth')) {
          payload.startDate = dayjs(this.endDate).startOf('month').format(DATE_FORMAT);
          payload.endDate = dayjs(this.endDate).format(DATE_FORMAT);
        } else {
          payload.startDate = dayjs.utc().month(monthYearObj.month - 1).year(monthYearObj.year).startOf('month').format(DATE_FORMAT);
          payload.endDate = dayjs.utc().month(monthYearObj.month - 1).year(monthYearObj.year).endOf('month').format(DATE_FORMAT);
        }
        this.$emit('clickToAnalyze', payload)
      },
      updatePreviewWidgetConfig() {

        let selectedVendors = getSelectedVendorsByWidget(this.widgetConfig, this, COMPARE_COST_TREND_VENDORS);
        this.previewWidgetConfig = {
          timeFrame: this.internalWidgetConfig.timeFrame,
          selectedVendorsByWidget: [selectedVendors]
        };

        this.loadCompareCostTrend(this.previewWidgetConfig)
      },
      setDetailTableOption() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.detailTableOption.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.detailTableOption.options = DASHBOARD_DROPDOWN_OPTIONS
        }
      },
      onChangeTimeFrame(timeFrame) {
        if(this.previewWidgetConfig.timeFrame === timeFrame){
          return;
        }
        this.previewWidgetConfig.timeFrame = timeFrame;
        let payload = {
          ...this.widgetConfig,
          ...this.previewWidgetConfig,
        };
        this.loadCompareCostTrend(payload);
      },
      loadCompareCostTrend(widgetConfig) {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;

        let payload = {
          ...widgetConfig,
          selectedVendorsByWidget:widgetConfig.selectedVendorsByWidget,
          widgetCurrency : this.$store.state.common.info.currencies.KRW
        };
        fetchDataCompareCostTrend(payload)
          .then((res) => {
            this.dashboardCompareCostTrend = res;
            this.endDate = res.endDate;
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.internalWidgetConfig.timeFrame = this.previewWidgetConfig.timeFrame;
          })
          .catch((err) => {
            console.error(err);
            this.widgetLoadingState[this.widgetConfig.index] = false;
            this.dashboardCompareCostTrend = DEFAULT_WIDGET_DATA.COMPARE_COST_TREND;
          });
      },
      onSelectOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            this.exportCSVFile();
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
        this.$refs.compareCostTrendPopover.close();
      },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      },
      applySaveWidget(widgetConfigForm) {
        const widgetConfig = {
          ...this.widgetConfig,
          ...widgetConfigForm
        };
        this.$emit('save', widgetConfig);
      },
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
      exportCSVFile() {
        let workbook = new this.$excel.Workbook();
        initWorkBookViaExcelJs(workbook);
        let excelFileName = `CostTrend(${this.commonUserInfo.selectedCurrency})`;
        let worksheet = workbook.addWorksheet(excelFileName);
        let columns = [
          { width: 30, header: this.$t('dashboard.compareCostTrend.date') },
          { width: 35, header: this.$t('dashboard.compareCostTrend.total') },
        ];
        for (let i = 1; i <= 31; i++) {
          columns.push({ width: 35, header: `${i}` });
        }
        worksheet.columns = columns

        let rowData = this.prepareDataExportCompareTrend(this.dashboardCompareCostTrend.trendCost);
        worksheet.addRows(rowData);
        saveAndReturnSupportedUTF18CSVFile(workbook, excelFileName);
      },
      prepareDataExportCompareTrend(trendCosts) {
        const result = [];
        trendCosts.forEach(item => {
          let rowData = [];
          rowData.push(dayjs.utc(item.date).format(getMonthYearDateFormatByLocalization()))
          rowData.push('sumOfMonth')
          let sumOfMonth = 0 ;
          for (let i = 0; i < item.monthlyCost.length; i++) {
            sumOfMonth += item.monthlyCost[i].cost;
            rowData.push(formatCostForExport(calculateCostByCurrencyForExport(item.monthlyCost[i].cost, this.internalCommonUserInfo.selectedCurrency,this.exchangeRate)))
          }
          rowData[1] = formatCostForExport(calculateCostByCurrencyForExport(sumOfMonth, this.internalCommonUserInfo.selectedCurrency,this.exchangeRate))
          result.push(rowData);
        });
        return result;
      },
      getNameWrapperById(name) {
        return `${name}-wrapper-${this.widgetConfig.index}`
      }
    }
  };

  function getMonthYearObj(monthYearString) {
    const splittedMonthYear = monthYearString.split('/');
    if (Trans.currentLanguage === SUPPORTED_LANGUAGE.EN) {
      return {
        year: splittedMonthYear[1],
        month: splittedMonthYear[0]
      };
    } else {
      return {
        year: splittedMonthYear[0],
        month: splittedMonthYear[1]
      };
    }
  }
</script>

<style lang="scss">
  .compare-cost-trend-wrapper {
    .compare-cost-trend {
      padding-right: 0 !important;
    }
    .time-frame-button {
      &:disabled {
        pointer-events: none;
      }
      &.left {
        border-top-left-radius: 4px;
        border-bottom-left-radius: 4px;
        border-right: 0;
      }
      &.right {
        border-left: 0;
        border-top-right-radius: 4px;
        border-bottom-right-radius: 4px;
      }
    }
  }
  #line-chart-area {
    width: 100%;
    height: 350px;
  }
  .custom-popover-compare-cost-trend {
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
  .edit-widget {
    position: absolute;
    z-index: 999;
    right: 4px;
    top: 9px;
  }
  .move-widget {
    position: absolute;
    z-index: 999;
    right: -15px;
    top: -18px;
  }
  .btn-outline-gray-4:not(:disabled):not(.disabled):active, .btn-outline-gray-4:not(:disabled):not(.disabled).active, .show > .btn-outline-gray-4.dropdown-toggle {
    color: #ffffff !important;
  }
  .btn-outline-gray-4 {
    color: #7b8088;
    &:hover {
      color: #ffffff !important;
    }
  }
</style>
