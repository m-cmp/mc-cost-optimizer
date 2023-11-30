<template>
  <b-card
    class="card-custom"
    border-variant="transparent"
    header-border-variant="lightgray-1"
    header-bg-variant="transparent">
    <b-row
      slot="header"
      align-h="between"
      align-v="center"
      no-gutters
      class="px-20 py-16 portion-widget-header dashboard-cost-header">
      <div class="medium font-16 color-darkgray-1 font-family-notosanscjkkr-medium dashboard-cost-view-by">
        <span class="line-height-26">{{ selectedVendor }} {{ $t('dashboard.dashboardCost.costBy.#1') }}</span>
        <BaseDropdown
          ref="dashboardCostViewByDropdown"
          :options="dashboardCostViewByOptions"
          :enabled-localization="true"
          :disabled="widgetLoadingState[widgetConfig.index]"
          class="custom-view-by-dropdown"
          @selectOption="onSelectOptionViewBy"
          @mounted="onDashboardCostViewByDropdownMounted"
        />
        <span class="line-height-26">{{ $t('dashboard.dashboardCost.costBy.#2') }}</span>
        <BaseTimePeriod
          :time-period="timePeriod"
          class="portion-time-period-pos"/>
      </div>
      <b-row
        class="dashboard-cost-header-inner portion-right-header-mr-top portion-right-header-mr-bottom">
        <BaseDashboardCostTimeFrame
          ref="baseTimeFrame"
          :disabled="widgetLoadingState[widgetConfig.index]"
          :selected-vendors-by-widget="widgetConfig.selectedVendorsByWidget"
          class="font-family-notosanscjkkr-medium dashboard-cost-time-frame"
          @changeDateTypeAndTimeFrame="onChangeDateTypeAndTimeFrame"
          @mounted="onBaseDashboardCostTimeFrameMounted"
        />
        <b-button-group
          class="button-group"
          size="sm">
          <b-button
            :pressed="previewWidgetConfig.chartType === CHART_TYPE.STACK"
            :disabled="widgetLoadingState[widgetConfig.index]"
            variant="outline-gray-4"
            class="custom-color blue-1 icon-only box-shadow-none chart-type-stack-button"
            @click="onChangeChartType(CHART_TYPE.STACK)">
            <base-material
              class="chart-type-icon"
              name="equalizer"/>
          </b-button>
          <b-button
            :pressed="previewWidgetConfig.chartType === CHART_TYPE.LINE"
            :disabled="widgetLoadingState[widgetConfig.index]"
            variant="outline-gray-4"
            class="custom-color blue-1 icon-only box-shadow-none chart-type-line-button"
            @click="onChangeChartType(CHART_TYPE.LINE)">
            <base-material
              class="chart-type-icon"
              name="timeline"/>
          </b-button>
        </b-button-group>
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
          class="custom-popover-dashboard-cost"/>
        <BasePopoverDropdown
          id="cost-by-dropdown"
          ref="costByPopover"
          :target="dashboardDropdownOptions.dashboardDropdownOptionsId"
          :placement="dashboardDropdownOptions.placement"
          :options="dashboardDropdownOptions.options"
          :show-popover="dashboardDropdownOptions.showPopover"
          :enabled-localization="dashboardDropdownOptions.enabledLocalization"
          :container-custom-popover="containerCustomPopoverId"
          @selectOption="onSelectCostByDropdownOption"
        />
      </b-row>
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
        v-show="!hasDashboardCostData"
        class="dashboard-widget-height"
      >
        <BaseNotificationNoData v-if="typeof selectedVendor != 'undefined'"/>
        <BaseNotificationNotSupport
          v-else
          :support-vendors="supportVendors"
          class="no-data"
        />
      </b-col>
      <b-col v-show="hasDashboardCostData">
        <!--Back_up-->
        <!--
        <BaseStackedColumnChart
          v-if="previewWidgetConfig.chartType === CHART_TYPE.STACK"
          ref="dashboardCostBaseStackedColumnChart"
          :stacked-column-id="`stacked-column-chart-dashboard-cost-${widgetConfig.index}`"
          :stacked-columns-data="stackedColumnsData"
          :legend-area-id="`legend-cost-by-${widgetConfig.index}`"
          :other-line-patterns="otherLinePatterns"
          :mapping-keys-with-label="mappingKeysWithLabel"
          :mapping-keys-with-colors="mappingVendorAccountsWithColors"
          :ordered-keys="orderedAccountKeysByLastPeriodCost"
          :legend-padding-left="legendPaddingLeft"
          :legend-padding-bottom="legendPaddingBottom"
          :legend-padding-right="legendPaddingRight"
          :marker-width="markerWidth"
          :marker-height="markerHeight"
          :category-min-grid-distance="categoryMinGridDistance"
          :min-width-stacked-chart="'0px'"
          :category-labels-color="categoryLabelsColor"
          :value-axis-labels-color="valueAxisLabelsColor"
          :series-field="seriesField"
          :value-axis-number-format="valueAxisNumberFormat"
          :value-prefix="currencySymbol"
          :scale="internalWidgetConfig.scale"
          :has-date-range-on-tooltip="hasDateRangeOnTooltip"
          :first-time-render-timeout="widgetConfig.firstTimeRenderTimeout"
          :loading-item-right-px="33"
          :use-custom-tooltip="browser === BROWSER.IE"
          :enable-drill-down="enableDrillDown"
          :re-size-widget="reSizeWidget"
          @clickColumn="onClickDashboardCostChart_backup"
          @hitLegend="onClickLegendDashboardCostChart"
        /> -->
        <PrimeStackedColumnChart
          v-if="previewWidgetConfig.chartType === CHART_TYPE.STACK"
          ref="dashboardCostBaseStackedColumnChart"
          :staked-chart-prime-data="stackedColumnsData"
          :value-prefix="currencySymbol"
          :scale="internalWidgetConfig.scale"
        />
        <NewBaseLineChart
          v-if="previewWidgetConfig.chartType === CHART_TYPE.LINE"
          ref="dashboardCostBaseLineChart"
          :base-data="lineChartDataMapping"
          :chart-width-value="300"
          :chart-height-value="370"
          :legend-padding="10"
          :value-prefix="currencySymbol"
          @clickChart="onClickDashboardCostChart"
        />
      </b-col>
    </div>
    <b-modal
      ref="edit-dashboard-cost-form"
      v-model="widgetConfig.isEditFormVisible"
      :title="$t('dashboard.editWidget')"
      modal-class="right-wing"
      hide-footer
      hide-backdrop
    >
      <div v-if="widgetConfig.isEditFormVisible">
        <EditDashboardCostForm
          :dashboard-view-mode="dashboardViewMode"
          :common-user-info="internalCommonUserInfo"
          :exchange-rate="internalExchangeRate"
          :widget-config="previewWidgetConfig"
          :all-vendors="allVendors"
          @hideModal="hideModal"
          @save="onSaveEditWidgetForm"
        />
      </div>
    </b-modal>
  </b-card>
</template>

<script>
  /* eslint-disable no-param-reassign */
  import {
    CHART_ITEM_LABEL,
    CHART_TYPE,
    CURRENCY,
    CURRENCY_SYMBOL,
    DEFAULT_CURRENCY,
    DEFAULT_EXCHANGE_RATE,
    FILTER_TIME,
    FORMAT_COST,
    OPACITY,
    TIME_CONST,
    NUMBER_OF_OTHERS,
    VENDOR_OTHERS,
    SERIES_DASHARRAY,
    BROWSER,
    START_DATE,
    END_DATE,
    HIDDEN_SERI
  } from '@/constants/constants';
  import {COST_ANALYTICS_VIEW_BY_VENDORS} from '@/constants/costAnalyticsConstants';
  import BaseDashboardCostTimeFrame from "@/components/common/BaseDashboardCostTimeFrame";
  import PrimeStackedColumnChart from '@/components/common/base-charts/base-stacked-column-chart/PrimeStackedColumnChart';
  import NewBaseLineChart from "@/components/common/base-charts/base-line-chart/newBaseLineChart";
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import BaseNotificationNotSupport from '@/components/common/BaseNotificationNotSupport';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import {
    DASHBOARD_DATE_TYPE,
    DASHBOARD_DROPDOWN_OPTIONS,
    DASHBOARD_DROPDOWN_OPTIONS_VALUE,
    DASHBOARD_WIDGET_TYPE,
    MONTHLY_COST_TIME_FRAME,
    OPACITY_VALUES,
    OTHER_LINE_PATTERNS,
    VIEW_MODE,
    DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS,
    DEFAULT_COST_BY_WIDGET_CONFIG,
    DASHBOARD_VIEW_BY,
    OTHER_NAME,
    WEEKLY_COST_TIME_FRAME,
    DEFAULT_WIDGET_DATA,
    DASHBOARD_VIEW_BY_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR,
    COST_BY_WIDGET_VENDORS,
    SELECTED_VENDOR,
    FILTER,
    YEAR_MONTH_FORMAT
  } from "@/constants/dashboardConstants";
  import BaseDropdown from "@/components/common/BaseDropdown";
  import {
    getMappingVendorAccountsWithColors,
    getDashboardCostChartData,
    getDashboardCostChartData_backup,
    getDashboardCostPrimeChartData,
    getOrderedAccountKeysByLastPeriodCost,
    getLastPeriodCost,
    mappingKeysWithLabelForCosts,
    isDashboardCostWidgetDataConfigChanged,
    getWeekNumberAndYearFromFormattedWeekly,
    formatTimeLabelChart,
    getSelectedVendorsByWidget
  } from "@/util/dashboardUtils";
  import {
    getFullDateFormatByLocalization,
    getMonthYearDashOfTodayBySlash,
    getMonthYearDateFormatByLocalization
  } from "@/util/dateTimeUtils";
  import _get from 'lodash/get';
  import _isNil from 'lodash/isNil';
  import _isEqual from 'lodash/isEqual';
  import _isEmpty from 'lodash/isEmpty';
  import _cloneDeep from 'lodash/cloneDeep';

  const EditDashboardCostForm = () => import('@/components/pages/dashboard/dashboard-cost/EditDashboardCostForm');
  import {fetchDashboardCost} from '@/api/dashboard';
  import ShowEditFormModalMixin from '@/mixins/ShowEditFormModalMixin';
  import {initWorkBookViaExcelJs, saveAndReturnSupportedUTF18CSVFile} from "@/util/excelJS";
  import {prepareDataForExportDashboardCost} from "@/util/exportUtils";
  import dayjs from 'dayjs';
  import _capitalize from 'lodash/capitalize'
  import BaseTimePeriod from "@/components/common/BaseTimePeriod";
  import {} from "../../../../constants/constants";
  import {Trans} from "@/components/common/base-i18n/Translation";
  import {SUPPORTED_LANGUAGE} from "@/constants/trans";

  const DATE_FORMAT = 'YYYY-MM-DD';
  const SERIES_DASHARRAY_VALUE = '1,1';
  const TIME_PERIOD_DATE_FORMAT = "MM/DD/YYYY";

  export default {
    name: 'DashboardCost',
    components: {
      PrimeStackedColumnChart,
      NewBaseLineChart,
      BaseDashboardCostTimeFrame,
      BaseNotificationNoData,
      EditDashboardCostForm,
      BaseLoadingIndicator,
      BaseDropdown,
      BaseTimePeriod,
      BaseNotificationNotSupport
    },
    mixins: [ShowEditFormModalMixin],
    props: {
      widgetConfig: {
        type: Object,
        required: true
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      dashboardViewMode: {
        type: String,
        default: VIEW_MODE.DEFAULT
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
      browser: {
        type: String,
        default: BROWSER.CHROME
      },
      allVendors: {
        type: Array,
        default() {
          return []
        }
      }
    },
    data() {
      return {
        dashboardCost: DEFAULT_WIDGET_DATA.DASHBOARD_COST,
        internalWidgetConfig: DEFAULT_COST_BY_WIDGET_CONFIG,
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          selectedVendorsByWidget: []
        },
        internalExchangeRate: DEFAULT_EXCHANGE_RATE,
        // this widget config is for preview in the main page. Preview in edit form has its own state
        previewWidgetConfig: {
          dateType: DASHBOARD_DATE_TYPE.MONTHLY,
          timeFrame: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
          chartType: CHART_TYPE.STACK,
          filter: FILTER.TOP_10_BY_COST,
          viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET
        },
        dashboardDropdownOptions: {
          dashboardDropdownOptionsId: `dashboard-cost-by-dropdown-options-${this.widgetConfig.index}`,
          options: DASHBOARD_DROPDOWN_OPTIONS,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        },
        containerCustomPopoverId: `dashboard-cost-by-custom-popover-${this.widgetConfig.index}`,
        CHART_TYPE: CHART_TYPE,
        legendPaddingLeft: 30,
        legendPaddingBottom: 1,
        legendPaddingRight: 20,
        markerWidth: 10,
        markerHeight: 10,
        categoryField: 'time',
        categoryMinGridDistance: 80,
        seriesField: 'time',
        otherLinePatterns: OTHER_LINE_PATTERNS,
        lastPeriodCost: [],
        orderedAccountKeysByLastPeriodCost: [],
        showEditFormModal: false,
        WEEK_SIGN: 'W',
        dashboardCostViewByOptions: DASHBOARD_VIEW_BY_OPTIONS,
        BROWSER: BROWSER,
        supportVendors: COST_BY_WIDGET_VENDORS
      }
    },
    computed: {
      lineChartDataMapping: {
        cache: true,
        get() {
          let chartData = [];
          // this.dashboardCost이 적잘한 값으로 초기화되기 전에 함수가 동작하여 Cannot read property of undefined 에러가 뜨는 것을 막기 위해 추가함.
          // 양방향 데이터 바인딩이므로 this.dashboardCost에 백에서 받은 데이터가 잘 들어오면 (자동으로) 함수가 동작하여 line 차트가 문제없이 보여짐.
          if (_isEmpty(this.dashboardCost.costByCondition)) {
            return {};
          }
          //Note: We have to get dateType from this.dashboardCost.payload.dateType to avoid stackedColumnsData re-evaluation (computed) two times when dashboardCost & this.previewWidgetConfig.dateType on changed
          const dateType = _get(this.dashboardCost, 'payload.dateType', DASHBOARD_DATE_TYPE.MONTHLY);
          switch (dateType) {
            case DASHBOARD_DATE_TYPE.MONTHLY: {
              chartData = getDashboardCostChartData(this.dashboardCost, FILTER_TIME.MONTH, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, this.dashboardCost.topOthersItems, false);
              chartData[`labels`].forEach((element, index) => {
                if(element === CHART_ITEM_LABEL.ESTIMATED) {
                  chartData[`labels`][index] = this.$t('dashboard.dashboardCommon.forecastThisMonth');
                }
              });
              break;
            }
            case DASHBOARD_DATE_TYPE.WEEKLY: {
              chartData = getDashboardCostChartData(this.dashboardCost, FILTER_TIME.WEEK, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, this.orderedAccountKeysByLastPeriodCost, false);
              chartData[`labels`].forEach((element, index) => {
                if(element === CHART_ITEM_LABEL.ESTIMATED) {
                  chartData[`labels`][index] = this.$t('dashboard.dashboardCommon.forecastThisMonth');
                }
              });
              break;
            }
          }

          return chartData;
        }
      },
      /* Back Up
       * stackedColumnsData: {
       *         cache: true,
       *         get() {
       *           let chartData = [];
       *           if (_isEmpty(this.dashboardCost.costByCondition)) {
       *             return [];
       *           }
       *           //Note: We have to get dateType from this.dashboardCost.payload.dateType to avoid stackedColumnsData re-evaluation (computed) two times when dashboardCost & this.previewWidgetConfig.dateType on changed
       *           const dateType = _get(this.dashboardCost, 'payload.dateType', DASHBOARD_DATE_TYPE.MONTHLY);
       *           const lastPeriodCost = getLastPeriodCost(dateType, this.dashboardCost);
       *           const orderedAccountKeysByLastPeriodCost = getOrderedAccountKeysByLastPeriodCost(lastPeriodCost);
       *           switch (dateType) {
       *             case DASHBOARD_DATE_TYPE.MONTHLY: {
       *               chartData = getDashboardCostChartData(this.dashboardCost, FILTER_TIME.MONTH, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, orderedAccountKeysByLastPeriodCost, true);
       *               chartData.some(data => {
       *                 if (data.time === CHART_ITEM_LABEL.ESTIMATED) {
       *                   data.time = this.$t('dashboard.dashboardCommon.forecastThisMonth');
       *                   data['opacity'] = OPACITY_VALUES.NO_CHANGE;
       *                   data[SERIES_DASHARRAY] = SERIES_DASHARRAY_VALUE;
       *                 }
       *                 // else if (data.time === getMonthYearDashOfTodayBySlash()) {
       *                 //   data.time = this.$t('dashboard.monthlyCost.thisMonthSoFar');
       *                 // }
       *               });
       *               break;
       *             }
       *             case DASHBOARD_DATE_TYPE.WEEKLY: {
       *               chartData = getDashboardCostChartData(this.dashboardCost, FILTER_TIME.WEEK, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, orderedAccountKeysByLastPeriodCost, true);
       *               if(!_isEmpty(chartData)){
       *                 chartData.some(data => {
       *                   if (data.time === CHART_ITEM_LABEL.ESTIMATED) {
       *                     data['opacity'] = OPACITY_VALUES.NO_CHANGE;
       *                     data[SERIES_DASHARRAY] = SERIES_DASHARRAY_VALUE;
       *                     data.time = this.$t('dashboard.dashboardCommon.forecastThisMonth');
       *                   }
       *                 });
       *               }
       *               break;
       *             }
       *           }
       *           return chartData;
       *         }
       *       },
       * */
      stackedColumnsData:{
        cache:true,
        get(){
          let chartData = {};
          if (_isEmpty(this.dashboardCost.costByCondition)) {
            return {};
          }
          const dateType = _get(this.dashboardCost, 'payload.dateType', DASHBOARD_DATE_TYPE.MONTHLY);
          const lastPeriodCost = getLastPeriodCost(dateType, this.dashboardCost); // 가장 마지막 기간 cost 반환
          const orderedAccountKeysByLastPeriodCost = getOrderedAccountKeysByLastPeriodCost(lastPeriodCost); // cost sort 후 vendor+item => 컬러테이블 생성에 필요
          switch (dateType) {
            case DASHBOARD_DATE_TYPE.MONTHLY: {
              /*
              chartData = getDashboardCostChartData_backup(this.dashboardCost, FILTER_TIME.MONTH, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, orderedAccountKeysByLastPeriodCost, true);
              chartData.some(data => {
                if (data.time === CHART_ITEM_LABEL.ESTIMATED) {
                  data.time = this.$t('dashboard.dashboardCommon.forecastThisMonth');
                  data['opacity'] = OPACITY_VALUES.NO_CHANGE;
                  data[SERIES_DASHARRAY] = SERIES_DASHARRAY_VALUE;
                  */
              chartData = getDashboardCostPrimeChartData(this.dashboardCost, FILTER_TIME.MONTH, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, orderedAccountKeysByLastPeriodCost);
              chartData[`labels`].forEach((element, index) => {
                if(element === CHART_ITEM_LABEL.ESTIMATED) {
                  chartData[`labels`][index] = this.$t('dashboard.dashboardCommon.forecastThisMonth');
                }
              });
              break;
            }
            case DASHBOARD_DATE_TYPE.WEEKLY: {
              /*
              chartData = getDashboardCostChartData_backup(this.dashboardCost, FILTER_TIME.WEEK, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate, orderedAccountKeysByLastPeriodCost, true);
              if(!_isEmpty(chartData)){
                chartData.some(data => {
                  if (data.time === CHART_ITEM_LABEL.ESTIMATED) {
                    data['opacity'] = OPACITY_VALUES.NO_CHANGE;
                    data[SERIES_DASHARRAY] = SERIES_DASHARRAY_VALUE;
                    data.time = this.$t('dashboard.dashboardCommon.forecastThisMonth');
                  }
                });
              }
               */
              chartData = getDashboardCostPrimeChartData(this.dashboardCost, FILTER_TIME.WEEK, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate,orderedAccountKeysByLastPeriodCost);
              chartData[`labels`].forEach((element, index) => {
                if(element === CHART_ITEM_LABEL.ESTIMATED) {
                  chartData[`labels`][index] = this.$t('dashboard.dashboardCommon.forecastThisMonth');
                }
              });
              break;
            }
          }
          return chartData;
        }
      },
      mappingKeysWithLabel: {
        cache: false,
        get() {
          return mappingKeysWithLabelForCosts(this.dashboardCost.costByCondition, this.$t('dashboard.others'), true, this.previewWidgetConfig.viewBy);
        }
      },
      mappingKeysWithLabelWithNoOthers: {
        cache: false,
        get() {
          return mappingKeysWithLabelForCosts(this.dashboardCost.costByCondition, this.$t('dashboard.others'), false, this.previewWidgetConfig.viewBy);
        }
      },
      mappingVendorAccountsWithColors: {
        cache: false,
        get() {
          const orderedAccountKeysByLastPeriodCost = this.orderedAccountKeysByLastPeriodCost.filter(account => {
            return !account.toLowerCase().includes(OTHER_NAME);
          });
          return getMappingVendorAccountsWithColors(orderedAccountKeysByLastPeriodCost);
        }
      },
      currencySymbol: function () {
        return CURRENCY_SYMBOL[this.internalCommonUserInfo ? this.internalCommonUserInfo.selectedCurrency : CURRENCY.USD];
      },
      valueAxisNumberFormat: function () {
        return `${this.currencySymbol}#a`;
      },
      hasDateRangeOnTooltip() {
        if (this.previewWidgetConfig.dateType === DASHBOARD_DATE_TYPE.WEEKLY) {
          return true
        }
      },
      i18nViewBy() {
        const langKey = `dashboard.viewByOption.${this.internalWidgetConfig.viewBy}`;
        return this.$t(langKey);
      },
      hasDashboardCostData() {
        if (!this.dashboardCost || _isEmpty(this.dashboardCost.costByCondition)) {
          return false;
        }
        return this.dashboardCost.costByCondition.some(costByDate => costByDate && !_isEmpty(costByDate.cost));
      },
      timePeriod: function () {
        return getDateTimePeriod(this.previewWidgetConfig.timeFrame, TIME_PERIOD_DATE_FORMAT, this.dashboardCost.timeFrameScope);
      },
      selectedVendor: {
        get() {
          let vendor  = getSelectedVendorsByWidget(this.widgetConfig, this, COST_BY_WIDGET_VENDORS, true)
          return vendor;
        }
      },
      enableDrillDown: {
        get() {
          if(_isEqual(this.profile.env, "CHINA") && _isEqual(this.selectedVendor, "GCP"))
            return false
          return this.Common.checkCostAnalyticsMenuAuth(this) && this.Common.checkVendorAvailableFromSelectedVendor(getSelectedVendorsByWidget(this.previewWidgetConfig, this, COST_BY_WIDGET_VENDORS),COST_ANALYTICS_VIEW_BY_VENDORS)
        }
      },
      reSizeWidget: {
       get() {
         let result = false;

         let isSingleItem = Object.keys(this.mappingKeysWithLabel).length > 1
         if(isSingleItem) {
           result = true
         }
        return result
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
          this.updatePreviewWidgetConfig();
        },
        immediate: false
      },
      widgetConfig: {
        handler(newValue, oldValue) {
          if (!isDashboardCostWidgetDataConfigChanged(oldValue, newValue)){
            return;
          }
          if(_isNil(this.allVendors)){
            return;
          }
          if (!isDashboardCostWidgetDataConfigChanged(this.internalWidgetConfig, this.widgetConfig)) {
            //In case user change preview widget config in dashboard page & click to open edit form modal but make no change and then click save button
            // -> we have to update preview widget config like edit form modal
            if (isDashboardCostWidgetDataConfigChanged(this.previewWidgetConfig, this.widgetConfig, true)) {
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

          if(this.internalWidgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP)) {
            if(_isEqual(this.internalWidgetConfig.viewBy, "account")) {
              this.internalWidgetConfig.viewBy = "project";
            }
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
      // 'internalCommonUserInfo.selectedVendors': {
      //   handler() {
      //     const widgetConfig = {
      //       ...this.internalWidgetConfig,
      //       ...this.previewWidgetConfig
      //     };
      //     this.loadDashboardCost(widgetConfig)
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
      dashboardCost: {
        handler(newVal, oldVal) {
          if (_isEqual(newVal, oldVal)) {
            return;
          }
          this.lastPeriodCost = getLastPeriodCost(this.previewWidgetConfig.dateType, newVal);
        },
        deep: true
      },
      lastPeriodCost: {
        handler(newVal, oldVal) {
          if (_isEmpty(newVal) || _isEqual(newVal, oldVal)) {
            return;
          }
          this.orderedAccountKeysByLastPeriodCost = getOrderedAccountKeysByLastPeriodCost(this.lastPeriodCost);
        },
        deep: true
      },
      dashboardViewMode: function () {
        this.setDashboardDropdownOptions();
      },
    },
    mounted() {
      //Because when you delete any widgets, this widget will be mounted so we have to reset dropdown options
      this.setDashboardDropdownOptions();
    },
    methods: {
      onDashboardCostViewByDropdownMounted(){
        const selectedDashboardCostViewByOpt = this.dashboardCostViewByOptions.find(opt => opt.value === this.previewWidgetConfig.viewBy);
        this.$refs.dashboardCostViewByDropdown.changeSelectedOptionText(selectedDashboardCostViewByOpt.text);
      },
      onSelectOptionViewBy(option) {
        if (this.previewWidgetConfig.viewBy === option) {
          return;
        }
        this.previewWidgetConfig.viewBy = option;

        let widgetConfig = {};
        if(this.previewWidgetConfig.viewBy !== this.internalWidgetConfig.viewBy){
         this.previewWidgetConfig.filter = FILTER.TOP_10_BY_COST;
         this.previewWidgetConfig.customFilter = [];
          widgetConfig = {
            selectedVendorsByWidget:
            [getSelectedVendorsByWidget(this.previewWidgetConfig, this, COST_BY_WIDGET_VENDORS)],
            viewBy : this.previewWidgetConfig.viewBy,
            dateType : this.previewWidgetConfig.dateType,
            timeFrame : this.previewWidgetConfig.timeFrame,
            filter : this.previewWidgetConfig.filter,
            customFilter : this.previewWidgetConfig.customFilter,
            scale : this.previewWidgetConfig.scale
          };
        }else{
          widgetConfig = {
            selectedVendorsByWidget:
            [getSelectedVendorsByWidget(this.internalWidgetConfig, this, COST_BY_WIDGET_VENDORS)],
            viewBy : this.internalWidgetConfig.viewBy,
            dateType : this.previewWidgetConfig.dateType,
            timeFrame : this.previewWidgetConfig.timeFrame,
            filter : this.internalWidgetConfig.filter,
            customFilter : this.internalWidgetConfig.customFilter,
            scale : this.internalWidgetConfig.scale
          };
        }
        this.loadDashboardCost(widgetConfig);
      },
      updatePreviewWidgetConfig() {
        let selectedVendors = getSelectedVendorsByWidget(this.internalWidgetConfig, this, COST_BY_WIDGET_VENDORS);
        this.dashboardCostViewByOptions = this.funcCostByWidgetViewByOptions(selectedVendors);
        this.previewWidgetConfig = {
          selectedVendorsByWidget: [selectedVendors],
          chartType: this.internalWidgetConfig.chartType,
          scale : this.internalWidgetConfig.scale,
          dateType: this.internalWidgetConfig.dateType,
          timeFrame: this.internalWidgetConfig.timeFrame,
          filter: this.internalWidgetConfig.filter,
          viewBy: this.internalWidgetConfig.viewBy,
          customFilter: this.internalWidgetConfig.customFilter,
          widgetType: this.internalWidgetConfig.widgetType
        };
        if (!_isNil(this.$refs.baseTimeFrame)) {
          const selectedDateTypeAndTimeFrame = {
            dateType: this.internalWidgetConfig.dateType,
            timeFrame: this.internalWidgetConfig.timeFrame
          };
          this.$refs.baseTimeFrame.selectDateTypeAndTimeFrameExternally(selectedDateTypeAndTimeFrame);
        }
        if (!_isNil(this.$refs.dashboardCostViewByDropdown)) {
          const selectedDashboardCostViewByOpt = this.dashboardCostViewByOptions.find(opt => opt.value === this.previewWidgetConfig.viewBy);
          this.$refs.dashboardCostViewByDropdown.changeSelectedOptionText(selectedDashboardCostViewByOpt.text);
        }
        this.loadDashboardCost(this.previewWidgetConfig);
      },
      setDashboardDropdownOptions() {
        if (this.dashboardViewMode !== VIEW_MODE.DEFAULT) {
          this.dashboardDropdownOptions.options = DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS
        } else {
          this.dashboardDropdownOptions.options = DASHBOARD_DROPDOWN_OPTIONS
        }
      },
      loadDashboardCost(widgetConfig) {
        this.widgetLoadingState[this.widgetConfig.index] = true;
        this.widgetConfig.isEditFormVisible = false;
        let payload = {
          selectedVendorsByWidget: widgetConfig.selectedVendorsByWidget,
          customFilter: widgetConfig.customFilter,
          filter: widgetConfig.filter,
          timeFrame: widgetConfig.timeFrame,
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
          dateType: widgetConfig.dateType,
          viewBy: widgetConfig.viewBy,
          widgetCurrency : this.$store.state.common.info.currencies.KRW,
          scale: widgetConfig.scale
        };
        fetchDashboardCost(payload)
          .then((res) => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            res.payload = payload;
            this.dashboardCost = res;
            this.previewWidgetConfig.filter = res.payload.filter
            this.previewWidgetConfig.customFilter = res.payload.customFilter
            this.previewWidgetConfig.scale = res.payload.scale
            this.onDashboardCostViewByDropdownMounted();
          })
          .catch((err) => {
            this.widgetLoadingState[this.widgetConfig.index] = false;
            console.error(err);
            this.dashboardCost = {
              ...DEFAULT_WIDGET_DATA.DASHBOARD_COST,
              payload: payload
            };
            this.onDashboardCostViewByDropdownMounted();
          });
      },
      onChangeChartType(type) {
        if (type === this.previewWidgetConfig.chartType) return;
        this.previewWidgetConfig.chartType = type;
        const $vm = this;
        if (type === CHART_TYPE.LINE) {
          this.$nextTick(function () {
            $vm.$refs['dashboardCostBaseLineChart'].reloadChart();
          });
        } else {
          this.$nextTick(function () {
            $vm.$refs['dashboardCostBaseStackedColumnChart'].reloadChart();
          });
        }
      },
      onChangeDateTypeAndTimeFrame(selectedDateTypeAndTimeFrame) {
        this.previewWidgetConfig.dateType = selectedDateTypeAndTimeFrame.dateType;
        this.previewWidgetConfig.timeFrame = selectedDateTypeAndTimeFrame.timeFrame;
        let widgetConfigPayload = {
          ...this.internalWidgetConfig,
          ...this.previewWidgetConfig
        };
        this.loadDashboardCost(widgetConfigPayload);
      },
      onSelectCostByDropdownOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET: {
            // this.showModal();
            break;
          }
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV: {
            this.exportCsvMonthlyAndWeeklyDashboardCost()
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
        this.$refs.costByPopover.close();
      },
      showModal() {
        this.widgetConfig.isEditFormVisible = true;
      },
      hideModal() {
        this.widgetConfig.isEditFormVisible = false;
      },
      onSaveEditWidgetForm(editWidgetForm) {
        let widgetConfig = {
          ...this.widgetConfig,
          ...editWidgetForm
        };
        this.$emit('save', widgetConfig);
        this.$refs.baseTimeFrame.onReloadDateType(widgetConfig.selectedVendorsByWidget);
      },
      onBaseDashboardCostTimeFrameMounted() {
        const selectedDateTypeAndTimeFrame = {
          dateType: this.internalWidgetConfig.dateType,
          timeFrame: this.internalWidgetConfig.timeFrame
        };
        this.$refs.baseTimeFrame.selectDateTypeAndTimeFrameExternally(selectedDateTypeAndTimeFrame);
      },
      exportCsvMonthlyAndWeeklyDashboardCost() {
        let fileName = '';
        let tempList = _cloneDeep(this.dashboardCost.costByCondition).reverse();
        let i18nViewByParams = {
          viewBy: _capitalize(this.previewWidgetConfig.viewBy)
        };
        switch (this.previewWidgetConfig.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY: {
            fileName = `${this.$t('dashboard.dashboardCost.download.monthly')}${this.$t('dashboard.dashboardCost.download.costBy', i18nViewByParams)}(${this.commonUserInfo.selectedCurrency})`
            break;
          }
          case DASHBOARD_DATE_TYPE.WEEKLY: {
            fileName = `${this.$t('dashboard.dashboardCost.download.weekly')}${this.$t('dashboard.dashboardCost.download.costBy', i18nViewByParams)}(${this.commonUserInfo.selectedCurrency})`
            break;
          }
        }
        let workbook = new this.$excel.Workbook();
        initWorkBookViaExcelJs(workbook);
        let worksheet = workbook.addWorksheet(fileName);
        let timeColumns = [];

        tempList.forEach(item => {
          if (this.previewWidgetConfig.dateType === DASHBOARD_DATE_TYPE.MONTHLY) {
            return timeColumns.push({
              width: 15,
              header: getThisMonthSoFarAndForecastThisMonthLabel(item.date, this)
            })
          } else {
            return timeColumns.push({width: 15, header: getFirstDayOfWeek(item.date, this)})
          }
        });

        worksheet.columns = [
          {
            width: 30,
            header: this.previewWidgetConfig.viewBy === DASHBOARD_VIEW_BY.ACCOUNT
              ? `${this.$t('dashboard.dashboardCost.download.cloud')} ${_capitalize(this.previewWidgetConfig.viewBy)}`
              : _capitalize(this.previewWidgetConfig.viewBy)
          },
          {
            width: 25,
            header: this.$t('dashboard.dashboardCost.download.vendor')
          },
          ...timeColumns
        ];
        let widgetConfig = {
          viewBy: this.previewWidgetConfig.viewBy,
          scale: this.internalWidgetConfig.scale,
          chartType: this.previewWidgetConfig.chartType
        };

      let rowData = prepareDataForExportDashboardCost(tempList, widgetConfig, this.currencySymbol, this.internalCommonUserInfo.selectedCurrency, this.internalExchangeRate);

        worksheet.addRows(rowData);
        saveAndReturnSupportedUTF18CSVFile(workbook, fileName);
      },
      onClickDashboardCostChart(selectedData) {
        if (_isEmpty(selectedData)) {
          return
        }
        if(!this.enableDrillDown){
          return
        }
        let objectDataKeys = Object.keys(selectedData);
        let dataKeys = objectDataKeys.filter(key => {
          return ![TIME_CONST, FORMAT_COST, OPACITY, /*VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.GCP, VENDOR_OTHERS.ALI,*/ NUMBER_OF_OTHERS, HIDDEN_SERI, SERIES_DASHARRAY, START_DATE, END_DATE].includes(key)
            && selectedData[key] != 0;
        });
        let isUnderMaxKeys =  dataKeys.length <= 10 // OTHERS 아이텀 포함한 아이템 개수 카운트
        let startDate = '';
        let endDate = '';
        let yearMonthFormat = '';
        switch (this.previewWidgetConfig.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY:
            const monthlyTimeArr = selectedData.time.split('/');
            const monthlyTime = {
              year: '',
              month: ''
            }

            if(Trans.currentLanguage === SUPPORTED_LANGUAGE.EN){
              monthlyTime.year = monthlyTimeArr[1]
              monthlyTime.month = monthlyTimeArr[0]
              yearMonthFormat = YEAR_MONTH_FORMAT.EN
            }else{
              monthlyTime.year = monthlyTimeArr[0]
              monthlyTime.month = monthlyTimeArr[1]
              yearMonthFormat = YEAR_MONTH_FORMAT.DEFAULT
            }

            if (selectedData.time === this.$t('dashboard.monthlyCost.thisMonthSoFar')) { // 이번달 -> 실제 당월 날짜로 변경됨
              //startDate = dayjs.utc().startOf('month').format(DATE_FORMAT)
              //endDate = dayjs.utc().format(DATE_FORMAT)
              startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).startOf('month').format(DATE_FORMAT)
              endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).format(DATE_FORMAT)
            }else if (selectedData.time === this.$t('dashboard.dashboardCommon.forecastThisMonth')) { // 이번달 예상 비용(월별 조회에서만 등장.)
              return;
            }else {
              //startDate = dayjs.utc().month(monthlyTime.month - 1).year(monthlyTime.year).startOf('month').format(DATE_FORMAT);
              //endDate = dayjs.utc().month(monthlyTime.month - 1).year(monthlyTime.year).endOf('month').format(DATE_FORMAT);
              startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).month(monthlyTime.month - 1).year(monthlyTime.year).startOf('month').format(DATE_FORMAT);
              let latestYearMonth = dayjs(this.dashboardCost.timeFrameScope.endDate).format(yearMonthFormat)
              if(selectedData.time === latestYearMonth){ // 현재 월의 현재 날짜
                endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).format(DATE_FORMAT)
              }else{ // 현재 월을 제외한 과거 월별 조회
                endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).month(monthlyTime.month - 1).year(monthlyTime.year).endOf('month').format(DATE_FORMAT);
              }
            }
            break;
          case DASHBOARD_DATE_TYPE.WEEKLY:
            const weekTimeArr = selectedData.time.split('-');
            const weeklyTime = {
              week: weekTimeArr[0].split(this.WEEK_SIGN)[1],
              year: weekTimeArr[1]
            };
            startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).week(weeklyTime.week).year(weeklyTime.year).startOf('week').format(DATE_FORMAT);
            if (selectedData.endDate === this.dashboardCost.timeFrameScope.endDate) { // 현재 주차(week): 이번주의 마지막 날짜 === maxDate
              endDate = selectedData.endDate
            }else{// 주간 조회범위 (일요일 ~ 토요일)
              endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).week(weeklyTime.week).year(weeklyTime.year).endOf('week').format(DATE_FORMAT);
            }
            break;
        }
        let payload = {
          selectedVendor : this.selectedVendor,
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
          viewBy: this.previewWidgetConfig.viewBy, // 화면에서 선택한 viewBy로 변경
          startDate: startDate,
          endDate: endDate
        };
        const objectDataKeySet = new Set(dataKeys);
        if (![VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.ALI, VENDOR_OTHERS.GCP, VENDOR_OTHERS.OCI, VENDOR_OTHERS.NCP].some(others => objectDataKeySet.has(others))) {
          if(isUnderMaxKeys){ //  필터 모드(TOP_10_COST , CUSTOM) 상관없이 아이템 개수 10개YN
            payload.data = dataKeys.map(key => {
              let dashIdx = key.indexOf('-');
              return {
                dataKey: key.substring(dashIdx + 1),
                vendor: key.substring(0, dashIdx)
              }
            })
          }
        }
        this.$emit('clickToAnalyze', payload)
      },
      onClickDashboardCostChart_backup(selectedData) {
        if (_isEmpty(selectedData)) {
          return
        }
        if(!this.enableDrillDown){
          return
        }
        let objectDataKeys = Object.keys(selectedData);
        let dataKeys = objectDataKeys.filter(key => {
          return ![TIME_CONST, FORMAT_COST, OPACITY, /*VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.GCP, VENDOR_OTHERS.ALI,*/ NUMBER_OF_OTHERS, HIDDEN_SERI, SERIES_DASHARRAY, START_DATE, END_DATE].includes(key)
            && selectedData[key] != 0;
        });
        let isUnderMaxKeys =  dataKeys.length <= 10 // OTHERS 아이텀 포함한 아이템 개수 카운트
        let startDate = '';
        let endDate = '';
        let yearMonthFormat = '';
        switch (this.previewWidgetConfig.dateType) {
          case DASHBOARD_DATE_TYPE.MONTHLY:
            const monthlyTimeArr = selectedData.time.split('/');
            const monthlyTime = {
              year: '',
              month: ''
            }

            if(Trans.currentLanguage === SUPPORTED_LANGUAGE.EN){
              monthlyTime.year = monthlyTimeArr[1]
              monthlyTime.month = monthlyTimeArr[0]
              yearMonthFormat = YEAR_MONTH_FORMAT.EN
            }else{
              monthlyTime.year = monthlyTimeArr[0]
              monthlyTime.month = monthlyTimeArr[1]
              yearMonthFormat = YEAR_MONTH_FORMAT.DEFAULT
            }

            if (selectedData.time === this.$t('dashboard.monthlyCost.thisMonthSoFar')) { // 이번달 -> 실제 당월 날짜로 변경됨
              //startDate = dayjs.utc().startOf('month').format(DATE_FORMAT)
              //endDate = dayjs.utc().format(DATE_FORMAT)
              startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).startOf('month').format(DATE_FORMAT)
              endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).format(DATE_FORMAT)
            }else if (selectedData.time === this.$t('dashboard.dashboardCommon.forecastThisMonth')) { // 이번달 예상 비용(월별 조회에서만 등장.)
              return;
            }else {
              //startDate = dayjs.utc().month(monthlyTime.month - 1).year(monthlyTime.year).startOf('month').format(DATE_FORMAT);
              //endDate = dayjs.utc().month(monthlyTime.month - 1).year(monthlyTime.year).endOf('month').format(DATE_FORMAT);
              startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).month(monthlyTime.month - 1).year(monthlyTime.year).startOf('month').format(DATE_FORMAT);
              let latestYearMonth = dayjs(this.dashboardCost.timeFrameScope.endDate).format(yearMonthFormat)
              if(selectedData.time === latestYearMonth){ // 현재 월의 현재 날짜
                endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).format(DATE_FORMAT)
              }else{ // 현재 월을 제외한 과거 월별 조회
                endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).month(monthlyTime.month - 1).year(monthlyTime.year).endOf('month').format(DATE_FORMAT);
              }
            }
            break;
          case DASHBOARD_DATE_TYPE.WEEKLY:
            const weekTimeArr = selectedData.time.split('-');
            const weeklyTime = {
              week: weekTimeArr[0].split(this.WEEK_SIGN)[1],
              year: weekTimeArr[1]
            };
            startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).week(weeklyTime.week).year(weeklyTime.year).startOf('week').format(DATE_FORMAT);
            if (selectedData.endDate === this.dashboardCost.timeFrameScope.endDate) { // 현재 주차(week): 이번주의 마지막 날짜 === maxDate
              endDate = selectedData.endDate
            }else{// 주간 조회범위 (일요일 ~ 토요일)
              endDate = dayjs(this.dashboardCost.timeFrameScope.endDate).week(weeklyTime.week).year(weeklyTime.year).endOf('week').format(DATE_FORMAT);
            }
            break;
        }
        let payload = {
          selectedVendor : this.selectedVendor,
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
          viewBy: this.previewWidgetConfig.viewBy, // 화면에서 선택한 viewBy로 변경
          startDate: startDate,
          endDate: endDate
        };
        const objectDataKeySet = new Set(dataKeys);
        if (![VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.ALI, VENDOR_OTHERS.GCP, VENDOR_OTHERS.OCI, VENDOR_OTHERS.NCP].some(others => objectDataKeySet.has(others))) {
          if(isUnderMaxKeys){ //  필터 모드(TOP_10_COST , CUSTOM) 상관없이 아이템 개수 10개YN
            payload.data = dataKeys.map(key => {
              let dashIdx = key.indexOf('-');
              return {
                dataKey: key.substring(dashIdx + 1),
                vendor: key.substring(0, dashIdx)
              }
            })
          }
        }
        this.$emit('clickToAnalyze', payload)
      },
      onClickLegendDashboardCostChart(legendName) {
        if(!this.enableDrillDown){
          return
        }
        //let startDate = '';
        // switch (this.previewWidgetConfig.dateType) { // 최초 형태
        //   case DASHBOARD_DATE_TYPE.MONTHLY: {
        //     switch (this.previewWidgetConfig.timeFrame) {
        //       case MONTHLY_COST_TIME_FRAME.YEAR_TO_MONTH: {
        //         //startDate = dayjs.utc().startOf('year').format(DATE_FORMAT);
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).startOf('year').format(DATE_FORMAT);
        //         break;
        //       }
        //       case MONTHLY_COST_TIME_FRAME.LAST_3_MONTHS: {
        //         //startDate = dayjs.utc().subtract(2, 'month').startOf('month').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(2, 'month').startOf('month').format(DATE_FORMAT);
        //         break;
        //       }
        //       case MONTHLY_COST_TIME_FRAME.LAST_6_MONTHS: {
        //         //startDate = dayjs.utc().subtract(5, 'month').startOf('month').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(5, 'month').startOf('month').format(DATE_FORMAT);
        //         break;
        //       }
        //       case MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS: {
        //         //startDate = dayjs.utc().subtract(11, 'month').startOf('month').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(11, 'month').startOf('month').format(DATE_FORMAT);
        //         break;
        //       }
        //     }
        //
        //     break;
        //   }
        //   case DASHBOARD_DATE_TYPE.WEEKLY: {
        //     switch (this.previewWidgetConfig.timeFrame) {
        //       case WEEKLY_COST_TIME_FRAME.YEAR_TO_WEEK: {
        //         //startDate = dayjs.utc().startOf('year').format(DATE_FORMAT);
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).startOf('year').format(DATE_FORMAT);
        //         break;
        //       }
        //       case WEEKLY_COST_TIME_FRAME.LAST_4_WEEKS: {
        //         //startDate = dayjs.utc().subtract(3, 'week').startOf('week').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(4, 'week').startOf('week').format(DATE_FORMAT);
        //         break;
        //       }
        //       case WEEKLY_COST_TIME_FRAME.LAST_8_WEEKS: {
        //         //startDate = dayjs.utc().subtract(7, 'week').startOf('week').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(8, 'week').startOf('week').format(DATE_FORMAT);
        //         break;
        //       }
        //       case WEEKLY_COST_TIME_FRAME.LAST_16_WEEKS: {
        //         //startDate = dayjs.utc().subtract(15, 'week').startOf('week').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(16, 'week').startOf('week').format(DATE_FORMAT);
        //         break;
        //       }
        //       case WEEKLY_COST_TIME_FRAME.LAST_24_WEEKS: {
        //         //startDate = dayjs.utc().subtract(23, 'week').startOf('week').format(DATE_FORMAT); //변경 전: 무조건 당일 날짜 기준
        //         startDate = dayjs(this.dashboardCost.timeFrameScope.endDate).subtract(24, 'week').startOf('week').format(DATE_FORMAT);
        //         break;
        //       }
        //     }
        //     break;
        //   }
        // }
        let startDate =  this.dashboardCost.timeFrameScope.startDate;
        let endDate = this.dashboardCost.timeFrameScope.endDate;
        const itemKey = Object.keys(this.mappingKeysWithLabel).find(key => this.mappingKeysWithLabel[key] === legendName);
        let dashIdx = itemKey.indexOf("-");
        const isOthersClicked = [VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.ALI, VENDOR_OTHERS.GCP, VENDOR_OTHERS.OCI, VENDOR_OTHERS.NCP].includes(itemKey);
        let payloadData;
        //let startDateOfOtherItem;
        if (isOthersClicked) { // Others 클릭시 상위 10개 아이템으로 ca 전달(X) -> others 아이템 전체 로딩 처리
          // payloadData = Object.keys(this.mappingKeysWithLabel)
          //     .filter(key => {return ![VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.ALI, VENDOR_OTHERS.GCP].includes(key)})
          //     .map(key => {
          //       let dashIdx = key.indexOf('-');
          //       return {
          //         dataKey: key.substring(dashIdx + 1),
          //         vendor: key.substring(0, dashIdx)
          //       }
          //     }); //others 최초 버전
          // switch (this.previewWidgetConfig.dateType) { // others 아이템 전체 로딩처리로 최초버전의 others 날짜 파라미터 주석처리
          //   case DASHBOARD_DATE_TYPE.MONTHLY: {
          //     startDateOfOtherItem = dayjs.utc().startOf('month').format(DATE_FORMAT);
          //     break;
          //   }
          //   case DASHBOARD_DATE_TYPE.WEEKLY: {
          //     let lastWeekByCondition = this.dashboardCost.costByCondition[this.dashboardCost.costByCondition.length - 1].date;
          //     const weekTimeArr = lastWeekByCondition.split('-');
          //     const weeklyTime = {
          //       week: weekTimeArr[0].split(this.WEEK_SIGN)[1],
          //       year: weekTimeArr[1]
          //     };
          //     startDateOfOtherItem = dayjs.utc().week(weeklyTime.week).year(weeklyTime.year).startOf('week').format(DATE_FORMAT);
          //     break;
          //   }
          // }
          let allItems = this.dashboardCost.customFilters
            .map(key => {
            return key.item
            });
          let topItems = Object.keys(this.mappingKeysWithLabel)
            .filter(key => {return ![VENDOR_OTHERS.AWS, VENDOR_OTHERS.AZURE, VENDOR_OTHERS.ALI, VENDOR_OTHERS.GCP, VENDOR_OTHERS.OCI, VENDOR_OTHERS.NCP].includes(key)})
            .map(key => {
              let dashIdx = key.indexOf('-');
              return   key.substring(dashIdx + 1)
              });
          payloadData = allItems.filter(key => {return !topItems.includes(key)})
            .map(item=>{
            return {
              dataKey : item,
              vendor : this.selectedVendor
            }
            })
        } else {
            payloadData = [
              {
                dataKey: itemKey.substring(dashIdx + 1),
                vendor: itemKey.substring(0, dashIdx)
              }
            ];
        }
        let payload = {
          widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
          viewBy: this.previewWidgetConfig.viewBy,
          startDate: startDate, // timeFrameScope
          endDate: endDate,     // timeFrameScope
          data: payloadData,
          //endDate: dayjs.utc().format(DATE_FORMAT)
          //startDateOfOtherItem: startDateOfOtherItem, // others 아이템 전체 로딩처리로 최초버전의 others 날짜 파라미터 주석처리
          //endDateOfOtherItem: dayjs.utc().format(DATE_FORMAT)
          //endDateOfOtherItem: isOthersClicked === true ? this.dashboardCost.timeFrameScope.endDate : null //timeFrameScope 적용
        };
        this.$emit('clickToAnalyze', payload)
      },
      funcCostByWidgetViewByOptions(selectedVendorsByWidget) {
        if(!_isEmpty(DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget])){
          return DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget]
        }else{
          return DASHBOARD_VIEW_BY_OPTIONS
        }
      }
    }
  }


  function getFirstDayOfWeek(date, $vm) {
    let getWeekNumber = getWeekNumberAndYearFromFormattedWeekly(date).weekNumber;
    let firstDayOfWeek = dayjs().week(getWeekNumber);

    return `${$vm.WEEK_SIGN}${getWeekNumber}(${firstDayOfWeek.startOf('week').format(getFullDateFormatByLocalization())})`
  }

  // function getFirstDayOfWeek(week, date, $vm) {
  //   let getWeekNumber = getWeekNumberAndYearFromFormattedWeekly(week);
  //   const dateFormat = getFullDateFormatByLocalization();
  //   let firstDayOfWeek = dayjs(date).format(dateFormat);
  //   return `${$vm.WEEK_SIGN}${getWeekNumber.weekNumber}(${firstDayOfWeek})`
  // }

  function getThisMonthSoFarAndForecastThisMonthLabel(date, $vm) {
    if (date === CHART_ITEM_LABEL.ESTIMATED) {
      return $vm.$t('dashboard.dashboardCommon.forecastThisMonth')
    } else {
      // if (formatTimeLabelChart(date, FILTER_TIME.MONTH) === getMonthYearDashOfTodayBySlash()) {
      //   return $vm.$t('dashboard.monthlyCost.thisMonthSoFar')
      // }
      return dayjs(date).format(getMonthYearDateFormatByLocalization())
    }
  }

  function getDateTimePeriod(timeFrame, dateFormat, timeFrameScope) {
    let stDt = timeFrameScope.startDate;
    let endDt = timeFrameScope.endDate;

    //const today = dayjs().now() ;
    switch (timeFrame) {
      case MONTHLY_COST_TIME_FRAME.LAST_3_MONTHS:
        return {
          //startDate: today.subtract(2, 'month').startOf('month').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        };
      case MONTHLY_COST_TIME_FRAME.LAST_6_MONTHS:
        return {
          //startDate: today.subtract(5, 'month').startOf('month').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        };
      case MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS:
        return {
          //startDate: today.subtract(11, 'month').startOf('month').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        };
      case MONTHLY_COST_TIME_FRAME.YEAR_TO_MONTH:
        return {
          //startDate: today.startOf('year').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        };
      case WEEKLY_COST_TIME_FRAME.YEAR_TO_WEEK: {
        // let weekLyTimeFrame = dayjs().week(1);
        return {
          //startDate:  weekLyTimeFrame.startOf('week').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        }
      }
      case WEEKLY_COST_TIME_FRAME.LAST_4_WEEKS: {
        return {
          //startDate: today.subtract(3, 'week').startOf('week').day(1,'day').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        }
      }
      case WEEKLY_COST_TIME_FRAME.LAST_8_WEEKS: {
        return {
          //startDate: today.subtract(7, 'week').startOf('week').day(1,'day').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        }
      }
      case WEEKLY_COST_TIME_FRAME.LAST_16_WEEKS: {
        return {
          //startDate: today.subtract(15, 'week').startOf('week').day(1,'day').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        }
      }
      case WEEKLY_COST_TIME_FRAME.LAST_24_WEEKS: {
        return {
          //startDate: today.subtract(23, 'week').startOf('week').day(1,'day').format(dateFormat),
          //endDate: today.format(dateFormat)
          startDate: stDt,
          endDate: endDt
        }
      }
    }
    return '';
  }

</script>
<style lang="scss">
  .dashboard-cost-header {
    padding: 0 12px 6px 20px !important;
    height: 55px;
    .dashboard-cost-view-by {
      width: 40%;
      display: flex;
      span {
        line-height: 26px;
      }
    }
    .dashboard-cost-header-inner {
      .dashboard-cost-time-frame {
        height: 100%;
      }
      .button-group {
        width: 48px;
        height: 24px;
        button {
          &:disabled {
            pointer-events: none;
          }
        }
        .chart-type-stack-button {
          width: 50%;
          padding: 0;
          border-top-left-radius: 4px;
          border-bottom-left-radius: 4px;
          .chart-type-icon {
            margin-left: 3px;
          }
        }
        .chart-type-line-button {
          width: 50%;
          padding: 0;
          border-top-right-radius: 4px;
          border-bottom-right-radius: 4px;
          .chart-type-icon {
            margin-left: 3px;
          }
        }
      }
    }
  }
  .custom-popover-dashboard-cost {
    .popover {
      top: 0 !important;
      left: 16px !important;
      .arrow {
        left: 77% !important;
      }
      .arrow:before {
        border-bottom-color: #ffffff;
      }
    }
  }
</style>
