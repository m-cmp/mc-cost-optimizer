<template>
  <fragment>
    <div v-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET">
      <DashboardCostMonthToDate
        :widget-config="widgetConfig"
        :all-vendors="allVendors"
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :cost-month-to-date="costMonthToDate"
        :estimated-cost="estimatedCost"
        :budget-data="budgetData"
        :total-saving="totalSaving"
        :dashboard-view-mode="dashboardViewMode"
        :widget-loading-state="widgetLoadingState"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET">
      <DashboardEstimatedCost
        :widget-config="widgetConfig"
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :cost-month-to-date="costMonthToDate"
        :estimated-cost="estimatedCost"
        :budget-data="budgetData"
        :total-saving="totalSaving"
        :dashboard-view-mode="dashboardViewMode"
        :widget-loading-state="widgetLoadingState"
        :all-vendors="allVendors"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>

    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_CLOUD_BUDGET_WIDGET">
      <DashboardCloudBudget
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :cost-month-to-date="costMonthToDate"
        :estimated-cost="estimatedCost"
        :budget-data="budgetData"
        :total-saving="totalSaving"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET">
      <DashboardCost
        :widget-config="widgetConfig"
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :dashboard-view-mode="dashboardViewMode"
        :widget-loading-state="widgetLoadingState"
        :browser="browser"
        :all-vendors="allVendors"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET">
      <DashboardCompareCostTrend
        :common-user-info="commonUserInfo"
        :widget-config="widgetConfig"
        :all-vendors="allVendors"
        :exchange-rate="exchangeRate"
        :dashboard-view-mode="dashboardViewMode"
        :compare-cost-trend-guide-index="compareCostTrendGuideIndex"
        :widget-loading-state="widgetLoadingState"
        :browser="browser"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @showHotspot="showHotspot"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET">
      <ProductPortion
        :widget-config="widgetConfig"
        :all-vendors="allVendors"
        :dashboard-view-mode="dashboardViewMode"
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :widget-loading-state="widgetLoadingState"
        :browser="browser"
        :is-sidebar-active="isSidebarActive"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET">
      <DashboardTopCost
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :widget-config="widgetConfig"
        :all-vendors="allVendors"
        :dashboard-view-mode="dashboardViewMode"
        :widget-loading-state="widgetLoadingState"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET">
      <DashboardAbnormalChange
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :widget-config="widgetConfig"
        :dashboard-view-mode="dashboardViewMode"
        :ref="'Widget-'+DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET+'-'+widgetConfig.i"
        :current-dashboard="currentDashboard"
        :widget-loading-state="widgetLoadingState"
        :all-vendors="allVendors"
        :is-sidebar-active="isSidebarActive"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
    <div v-else-if="widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET">
      <DashboardMLAbnormalUserDetection
        :common-user-info="commonUserInfo"
        :exchange-rate="exchangeRate"
        :widget-config="widgetConfig"
        :dashboard-view-mode="dashboardViewMode"
        :ref="'Widget-'+DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET+'-'+widgetConfig.i"
        :current-dashboard="currentDashboard"
        :widget-loading-state="widgetLoadingState"
        :all-vendors="allVendors"
        :is-sidebar-active="isSidebarActive"
        @delete="onDeleteWidget()"
        @duplicateWidget="onDuplicateWidget()"
        @save="onSaveWidget"
        @clickToAnalyze="onClickToAnalyze"
      />
    </div>
  </fragment>
</template>

<script>
  import {BROWSER, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';

  const DashboardCostMonthToDate = () => import('@/components/pages/dashboard/dashboard-common/DashboardCostMonthToDate');
  const DashboardEstimatedCost = () => import('@/components/pages/dashboard/dashboard-common/DashboardEstimatedCost');
  const DashboardCloudBudget = () => import('@/components/pages/dashboard/dashboard-common/DashboardCloudBudget');
  const DashboardAbnormalChange = () => import('@/components/pages/dashboard/abnormal-change/AbnormalChange');
  const DashboardMLAbnormalUserDetection = () => import('@/components/pages/dashboard/ml-abnormal-detection/MLAbnormalUserDetection');
  const DashboardCost = () => import('@/components/pages/dashboard/dashboard-cost/DashboardCost');
  const DashboardCompareCostTrend = () => import('@/components/pages/dashboard/compare-cost-trend/CompareCostTrend');
  const DashboardTopCost = () => import('@/components/pages/dashboard/top-cost/TopCost');
  const ProductPortion = () => import('@/components/pages/dashboard/product-portion/ProductPortion');

  import {DASHBOARD_WIDGET_TYPE} from '@/constants/dashboardConstants';
  import _isNil from 'lodash/isNil';
  import _isEmpty from 'lodash/isEmpty';

  export default {
    name: 'TemplateWidget',
    components: {
      DashboardCost,
      DashboardCostMonthToDate,
      DashboardEstimatedCost,
      DashboardCloudBudget,
      DashboardTopCost,
      DashboardCompareCostTrend,
      DashboardAbnormalChange,
      DashboardMLAbnormalUserDetection,
      ProductPortion,
    },
    props: {
      widgetConfig: {
        type: Object,
        required: true,
        default: function() {
          return {};
        }
      },
      allVendors: {
        type: Array,
        default() {
          return []
        }
      },
      commonUserInfo: {
        type: Object,
        require: true,
        default: function() {
          return {};
        }
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      costMonthToDate: {
        type: Object,
        require: true,
        default: function() {
          return {};
        }
      },
      estimatedCost: {
        type: Object,
        require: true,
        default: function() {
          return {};
        }
      },
      yearCostFcst: {
        type: Object,
        require: false,
        default: function() {
          return {};
        }
      },
      budgetData: {
        type: Object,
        require: true,
        default: function() {
          return {};
        }
      },
      totalSaving: {
        type: Object,
        require: true,
        default: function() {
          return {};
        }
      },
      dashboardViewMode: {
        type: String,
        require: true,
        default: ''
      },
      // currentStep: {
      //   type: Number,
      //   required: true
      // },
      compareCostTrendGuideIndex: {
        type: Number,
        default: -1
      },
      currentDashboard: {
        type: Object,
        required: true
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
      browser: {
        type: String,
        default: BROWSER.CHROME
      },
      isSidebarActive: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
        DASHBOARD_WIDGET_TYPE: DASHBOARD_WIDGET_TYPE
      }
    },
    watch: {
      widgetConfig: {
        handler() {
          if(_isNil(this.widgetConfig) || _isEmpty(this.widgetConfig.widgetType)) {
            return;
          }
          let $vm = this;
          this.$nextTick(() => {
            document.getElementById(`${this.widgetConfig.widgetType}-${this.widgetConfig.index}`).onmouseover = function () {
              $vm.$emit('hoverOnWidgets', `${$vm.widgetConfig.widgetType}-${$vm.widgetConfig.index}`)
            }
          })
        },
        immediate: true
      }
    },
    methods: {
      onDeleteWidget() {
        this.$emit('delete');
      },
      onDuplicateWidget() {
        this.$emit('duplicateWidget');
      },
      onSaveWidget(newWidgetConfig) {
        this.$emit('save', newWidgetConfig);
      },
      onClickToAnalyze(payload) {
        this.$emit('clickToAnalyze', payload)
      },
      makeSizeColumnsToFit(i) {
        this.$refs['Widget-'+DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET + '-' + i].makeSizeColumnsToFit();
      },
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
    }
  };
</script>

