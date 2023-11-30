<template>
  <!-- fluid of b-container property full width -->
  <div v-if="widgets && widgets.length > 0">
    <grid-layout
      :layout.sync="widgets"
      :col-num="24"
      :row-height="15"
      :is-mirrored="false"
      :vertical-compact="true"
      :margin="[20, 20]"
      :use-css-transforms="true"
    >
      <grid-item
        v-for="(widgetConfig) in widgets"
        :key="widgetConfig.index"
        :x="widgetConfig.x"
        :y="widgetConfig.y"
        :w="widgetConfig.w"
        :h="widgetConfig.h"
        :min-w="widgetConfig.minW"
        :min-h="widgetConfig.minH"
        :specific-sizes="widgetConfig.specificSizes"
        :is-draggable="isDraggable(widgetConfig.widgetType)"
        :is-resizable="isResizable(widgetConfig.widgetType)"
        :i="widgetConfig.i"
        :id="`${widgetConfig.widgetType}-${widgetConfig.index}`"
        :class="getGridItemClass(widgetConfig)"
        @resized="resizedEvent"
      >
        <TemplateWidget
          :widget-config="widgetConfig"
          :all-vendors="allVendors"
          :common-user-info="commonUserInfo"
          :exchange-rate="exchangeRate"
          :cost-month-to-date="costMonthToDate"
          :estimated-cost="estimatedCost"
          :budget-data="budgetData"
          :total-saving="totalSaving"
          :dashboard-view-mode="dashboardViewMode"
          :ref="widgetConfig.widgetType+'-'+widgetConfig.i"
          :compare-cost-trend-guide-index="compareCostTrendGuideIndex"
          :current-dashboard="currentDashboard"
          :widget-loading-state="widgetLoadingState"
          :browser="browser"
          :is-sidebar-active="isSidebarActive"
          @delete="onDeleteWidget(widgetConfig.i)"
          @duplicateWidget="onDuplicateWidget(widgetConfig.i)"
          @save="onSaveWidget"
          @clickToAnalyze="onClickToAnalyze"
          @showHotspot="showHotspot"
          @hoverOnWidgets="hoverOnWidgets"
        />
      </grid-item>
    </grid-layout>
  </div>
</template>

<script>
  import {BROWSER, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';

  const TemplateWidget = () => import('@/components/pages/dashboard/template-widget/TemplateWidget');
  import VueGridLayout from 'vue-grid-layout-specific-configs-to-resize';
  import {UNRESIZABLE_DASHBOARD_WIDGET_TYPES, VIEW_MODE, DASHBOARD_WIDGET_TYPE} from '@/constants/dashboardConstants';
  import _isEmpty from 'lodash/isEmpty';
  export default {
    name: 'DashboardWidgets',
    components: {
      TemplateWidget,
      GridLayout: VueGridLayout.GridLayout,
      GridItem: VueGridLayout.GridItem
    },
    props: {
      widgets: {
        type: Array,
        default() {
          return [];
        }
      },
      allVendors: {
        type: Array,
        default() {
          return []
        }
      },
      dashboardViewMode: {
        type: String,
        require: true,
        default: ''
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
      budgetData: {
        type: Object,
        require: true,
        default: function () {
          return {};
        }
      },
      totalSaving: {
        type: Object,
        require: true,
        default: function () {
          return {};
        }
      },
      dashboardTrend: {
        type: Object,
        require: true,
        default: function() {
          return {};
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
        VIEW_MODE: VIEW_MODE,
        DASHBOARD_WIDGET_TYPE: DASHBOARD_WIDGET_TYPE
      }
    },
    computed:{
      queryDashboardIndex:function () {
        return this.$route.query.dashboardIndex;
      },
      queryWidgetIndex:function () {
        return this.$route.query.widgetIndex;
      },
      gridItemClass: function(){
        let gridItemClass = '';
        if(!_isEmpty(this.widgetConfig)){
          if(this.widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET){
            gridItemClass += 'portion-by-widget'
          }
          if(this.widgetConfig.dashboardIndex == this.queryDashboardIndex && this.widgetConfig.index == this.queryWidgetIndex ){
            gridItemClass +=  ' -border -border-color-blue-1 -border-width-2'
          }
        }
        return gridItemClass
      }
    },
    watch: {
      widgets: {
        handler() {
        }
      }
    },
    mounted: function(){
    },
    methods: {
      getGridItemClass(widgetConfig){
        let gridItemClass = '';
        if(!_isEmpty(widgetConfig)){
          if(widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET){
            gridItemClass += 'portion-by-widget'
          }
          if(widgetConfig.dashboardIndex == this.queryDashboardIndex && widgetConfig.index == this.queryWidgetIndex ){
            gridItemClass +=  ' -border -border-color-blue-1 -border-width-2'
          }
        }
        return gridItemClass
      },
      hoverOnWidgets(hoveringWidgetsName) {
        this.widgets.forEach(widget => {
          if (`${widget.widgetType}-${widget.index}` === hoveringWidgetsName) {
            document.getElementById(`${widget.widgetType}-${widget.index}`).style.zIndex = "100"
          } else {
            document.getElementById(`${widget.widgetType}-${widget.index}`).style.zIndex = "50"
          }
        })
      },
      onDeleteWidget(widgetIndex) {
        this.$emit('deleteWidget', widgetIndex);
      },
      onDuplicateWidget(widgetIndex) {
        this.$emit('duplicateWidget', widgetIndex);
      },
      onSaveWidget(newWidgetConfig) {
        this.$emit('saveWidget', newWidgetConfig);
      },
      isDraggable(widgetType) {
        return this.dashboardViewMode !== VIEW_MODE.DEFAULT;
      },
      isResizable(widgetType) {
        return this.dashboardViewMode !== VIEW_MODE.DEFAULT && !UNRESIZABLE_DASHBOARD_WIDGET_TYPES.includes(widgetType);
      },
      resizedEvent: function(i, newH, newW, newHPx, newWPx){
        const widgetRef = DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET + '-' + i;
        if (this.$refs[widgetRef]) {
          // this.$refs[widgetRef][0].makeSizeColumnsToFit(i);
        }
      },
      onClickToAnalyze(payload) {
        this.$emit('clickToAnalyze', payload)
      },
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
    }
  };
</script>

