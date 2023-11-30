<template>
  <fragment>
    <BaseLoadingIndicator
      v-show="isLoading"
      :loading-height="200"
      :loading-item-right-px="0"
    />
    <!--    <div-->
    <!--      v-show="!isLoading"-->
    <!--      class="preview-top-5-cost-chart">-->
    <!--      <b-card class="base-donut-chart-wrapper">-->
    <!--        <BaseDonutChart-->
    <!--          v-show="hasPreviewTop5CostChartData"-->
    <!--          :colors="previewTop5CostChartData.colors"-->
    <!--          :donut-data="previewTop5CostChartData.pieData"-->
    <!--          :legend-width="60"-->
    <!--          :legend-font-size="7"-->
    <!--          :legend-padding-bottom="2"-->
    <!--          :legend-padding-top="2"-->
    <!--          :legend-box-top="-2"-->
    <!--          :legend-chart-width="150"-->
    <!--          :line-pattern-categories="linePatternCategories"-->
    <!--          :div-parent-height-donut-chart="divParentHeightDonutChart"-->
    <!--          :height-donut-chart="heightDonutChart"-->
    <!--          :margin-top-donut-chart="marginTopDonutChart"-->
    <!--          :margin-left-donut-chart="marginLeftDonutChart"-->
    <!--          :margin-left-legend-donut-chart="marginLeftLegendDonutChart"-->
    <!--          :div-parent-padding-donut-chart="divParentPaddingDonutChart"-->
    <!--          :is-preview-mode="true"-->
    <!--          donut-chart-legend-id="preview-top-cost-chart-legend"-->
    <!--          donut-chart-id="preview-top-cost-chart"-->
    <!--          inner-radius="62"-->
    <!--        />-->
    <!--        <BaseNotificationNoData v-show="!hasPreviewTop5CostChartData"/>-->
    <!--      </b-card>-->
    <!--    </div>-->
  </fragment>
</template>

<script>
  import {
    DASHBOARD_VIEW_BY,
    TOP_5_TIME_FRAME,
    OTHER_LINE_PATTERNS
  } from '@/constants/dashboardConstants';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';
  import {prepareDataForTop5Cost} from '@/util/dashboardUtils';
  import {DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import {fetchTop5Cost} from '@/api/dashboard';
  import _isEqual from 'lodash/isEqual';
  import _isEmpty from 'lodash/isEmpty';

  export default {
    name: 'PreviewTopCost',
    components: {
      BaseLoadingIndicator,
      BaseNotificationNoData,
    },
    props: {
      viewBy: {
        type: String,
        default: DASHBOARD_VIEW_BY.ACCOUNT
      },
      timeFrame: {
        type: String,
        default: TOP_5_TIME_FRAME.LAST_14_DAYS
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
    },
    data() {
      return {
        top5Cost: [],
        linePatternCategories: OTHER_LINE_PATTERNS,
        divParentHeightDonutChart: '162px',
        heightDonutChart: '130px',
        marginTopDonutChart: '15px',
        marginLeftDonutChart: '0',
        marginLeftLegendDonutChart: '-80px',
        divParentPaddingDonutChart: '0 60px',
        isLoading: true,
      };
    },
    computed: {
      previewTop5CostChartData: function () {
        return prepareDataForTop5Cost(this.top5Cost, this.commonUserInfo.selectedCurrency, this.exchangeRate, this.$t('dashboard.others'), this.$t('dashboard.othersUpper'), this.viewBy);
      },
      top5CostRequestParams() {
        return {
          vendors: this.commonUserInfo.selectedVendors,
          viewBy: this.viewBy,
          timeFrame: this.timeFrame
        }
      },
      hasPreviewTop5CostChartData () {
        if (!this.previewTop5CostChartData || _isEmpty(this.previewTop5CostChartData.pieData)) {
          return false;
        }
        return Object.values(this.previewTop5CostChartData.pieData).some(costsByCurrentView => !_isEmpty(costsByCurrentView));
      },
    },
    watch: {
      top5CostRequestParams: {
        handler(newReqParams, oldReqParams) {
          if (isTop5CostRequestParamsEqual(newReqParams, oldReqParams)) {
            return;
          }
          this.refetchTopCostData(newReqParams);
        },
        immediate: true
      }
    },
    mounted() {
    },
    methods: {
      refetchTopCostData(payload) {
        this.isLoading = true
        fetchTop5Cost(payload)
          .then(res => {
            this.top5Cost = res;
            this.isLoading = false;
          })
          .catch((err) => {
            this.isLoading = false;
            console.error(err);
          })
      },
    }
  };

  function isTop5CostRequestParamsEqual(req1, req2) {
    if (req1 == req2) {
      return true;
    }
    if (req1 == null || req2 == null) {
      return false;
    }
    return _isEqual(req1.vendors, req2.vendors)
      && req1.viewBy === req2.viewBy
      && req1.timeFrame === req2.timeFrame;
  }
</script>
<style lang="scss">
  #preview-top-cost-chart {
    height: 150px;
  }
  .preview-top-5-cost-chart {
    .base-donut-chart-wrapper {
      height: 200px;
    }
    .card-body {
      padding: 1.25rem 0 !important;
    }
  }
  #preview-top-cost-chart-legend {
    .legend-item {
      min-width: 190px !important;
    }
  }
</style>
