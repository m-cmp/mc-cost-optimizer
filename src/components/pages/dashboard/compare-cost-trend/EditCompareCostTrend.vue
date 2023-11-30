<template>
  <fragment >
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span>{{ $t('dashboard.compareCostTrend.title') }}</span>
      </p>
      <p class="-description -color-darkgray-1">{{ $t('dashboard.compareCostTrend.desc') }}</p>
    </b-row>
    <!--    <span class="lb-preview font-family-notosanscjkkr-medium">{{ $t('dashboard.compareCostTrend.previews') }}</span>-->
    <!--    <div-->
    <!--      v-show="isLoading"-->
    <!--      class="dashboard-widget-height compare-cost-trend">-->
    <!--      <BaseLoadingIndicator :loading-item-right-px="0"/>-->
    <!--    </div>-->
    <!--    <div v-show="!isLoading">-->
    <!--      <div-->
    <!--        v-show="!hasCompareCostTrendData"-->
    <!--        class="dashboard-widget-height compare-cost-trend">-->
    <!--        <BaseNotificationNoData/>-->
    <!--      </div>-->
    <!--      <div v-show="hasCompareCostTrendData">-->
    <!--        <BaseLineAreaChart-->
    <!--          :line-chart-area-id="`line-chart-area-edit-${widgetConfig.index}`"-->
    <!--          :line-chart-data="lineChartDataMapping"-->
    <!--          :category-labels-color="categoryLabelsColor"-->
    <!--          :value-axis-labels-color="valueAxisLabelsColor"-->
    <!--          :value-axis-number-format="valueAxisNumberFormat"-->
    <!--          :legend-padding-left="legendPaddingLeft"-->
    <!--          :legend-padding-bottom="legendPaddingBottom"-->
    <!--          :legend-padding-right="legendPaddingRight"-->
    <!--          :category-min-grid-distance="40"-->
    <!--          :mapping-keys-with-label="mappingKeysWithLabel"-->
    <!--          :mapping-keys-with-colors="mappingMonthWithColors"-->
    <!--          :can-show-tooltip="false"-->
    <!--          :value-prefix="currencySymbol"-->
    <!--          :first-time-render-timeout="PREVIEW_FIRST_TIME_RENDER_TIMEOUT"-->
    <!--        />-->
    <!--      </div>-->
    <!--    </div>-->
    <b-form
      v-if="show"
      class="edit-abnormal-form"
      @submit="onSubmit"
      @reset="onReset"
    >

      <b-form-group
        id="cloud-service-group"
        :label="$t('dashboard.compareCostTrend.cloudService')"
        label-for="selectVendor">
        <b-form-select
          id="selectVendor"
          v-model="selectedVendorByOption"
          :options="vendorOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          required/>
      </b-form-group>
      <b-form-group
        id="view-by-group"
        :label="$t('dashboard.compareCostTrend.dateRange')"
        label-for="view-by">
        <b-form-select
          id="view-by"
          v-model="draftWidgetConfig.timeFrame"
          :disabled="vendorOptions.length > 0 ? false : true"
          required>
          <!--          @change="onChangeTimeFrame"-->
          <option
            v-for="(option,index) in viewByOptions"
            :key="index"
            :value="option.value">{{ option.text }}</option>
        </b-form-select>
      </b-form-group>

      <div class="float-right">
        <b-button
          variant="outline-secondary"
          class="cancel-button font-family-notosanscjkkr-medium"
          @click="onClickCancelButton"
        >{{ $t('dashboard.compareCostTrend.cancel') }}</b-button>
        <b-button
          :disabled="vendorOptions.length > 0 ? false : true"
          type="submit"
          variant="primary"
          class="font-family-notosanscjkkr-medium">{{ saveButtonLocalization }}</b-button>
      </div>
    </b-form>
  </fragment>
</template>

<script>
  import {
    COMPARE_COST_TREND_TIME_FRAME_OPTION,
    DEFAULT_COMPARE_COST_TREND_WIDGET_CONFIG,
    VIEW_MODE,
    COMPARE_COST_TREND_VENDORS
  } from '@/constants/dashboardConstants';
  import _cloneDeep from 'lodash/cloneDeep';
  import _isEmpty from 'lodash/isEmpty';
  import _isNil from 'lodash/isNil';
  import _isEqual from 'lodash/isEqual';
  // import _includes from 'lodash/includes';
  // import { fetchDataCompareCostTrend } from '@/api/dashboard';
  // import { getMappingMonthWithColors, getCompareCostTrendChartData, mappingKeysWithLabelForTrend } from '@/util/dashboardUtils';
  import { getSelectedVendorsByWidget , availableVendors } from '@/util/dashboardUtils';
  import {DEFAULT_EXCHANGE_RATE, VENDOR} from '@/constants/constants';
  import BaseLoadingIndicator from '@/components/common/BaseLoadingIndicator';
  import BaseNotificationNoData from '@/components/common/BaseNotificationNoData';

  export default {
    name: 'EditCompareCostTrend',
    components: {
      BaseLoadingIndicator,
      BaseNotificationNoData
    },
    props: {
      hideModal: {
        type: Function,
        require: false,
        default: null
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      // allVendors: {
      //   type: Array,
      //   required: true,
      //   default() {
      //     return []
      //   }
      // },
      legendPaddingLeft:{
        type: Number,
        required: true,
        default: 0
      },
      legendPaddingBottom:{
        type: Number,
        required: true,
        default: 0
      },
      legendPaddingRight:{
        type: Number,
        required: true,
        default: 0
      },
      markerWidth: {
        type: Number,
        required: true,
        default: 0
      },
      markerHeight: {
        type: Number,
        required: true,
        default: 0
      },
      categoryMinGridDistance: {
        type: Number,
        required: true,
        default: 0
      },
      categoryLabelsColor: {
        type: Object,
        required: true,
      },
      valueAxisLabelsColor: {
        type: Object,
        required: true,
      },
      currencySymbol: {
        type: String,
        required: true,
      },
      valueAxisNumberFormat: {
        type: String,
        required: true,
      },
      widgetConfig: {
        type: Object,
        required: true,
        default() {
          return DEFAULT_COMPARE_COST_TREND_WIDGET_CONFIG
        }
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      dashboardViewMode: {
        type: String,
        required: true,
        default: VIEW_MODE.DEFAULT
      }
    },
    data() {
      return {
        vendorOptions: availableVendors(COMPARE_COST_TREND_VENDORS, this),
        viewByOptions: COMPARE_COST_TREND_TIME_FRAME_OPTION.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        dashboardCompareCostTrend: {
          trendCost: []
        },
        draftWidgetConfig: {},
        selectedVendorsByWidget : [],
        show: true,
        PREVIEW_FIRST_TIME_RENDER_TIMEOUT: 800,
        isLoading: true,
        defaultVendor: ""
      };
    },
    computed: {
      // lineChartDataMapping: {
      //   cache: true,
      //   get() {
      //     if (_isEmpty(this.dashboardCompareCostTrend && this.dashboardCompareCostTrend.trendCost)) {
      //       return [];
      //     }
      //     let dashboardTrendCost = _cloneDeep(this.dashboardCompareCostTrend.trendCost);
      //     return getCompareCostTrendChartData(dashboardTrendCost, this.commonUserInfo.selectedCurrency, this.exchangeRate);
      //   }
      // },
      // mappingKeysWithLabel: {
      //   cache: true,
      //   get() {
      //     if (_isEmpty(this.lineChartDataMapping)) {
      //       return {};
      //     }
      //     return mappingKeysWithLabelForTrend(this.lineChartDataMapping);
      //   }
      // },
      // mappingMonthWithColors: {
      //   cache: true,
      //   get() {
      //     if (_isEmpty(this.lineChartDataMapping)) {
      //       return {};
      //     }
      //     return getMappingMonthWithColors(this.lineChartDataMapping);
      //   }
      // },
      // hasCompareCostTrendData() {
      //   if (!this.dashboardCompareCostTrend || _isEmpty(this.dashboardCompareCostTrend.trendCost)) {
      //     return false;
      //   }
      //   return this.dashboardCompareCostTrend.trendCost.some(trendCostByDate => trendCostByDate && !_isEmpty(trendCostByDate.monthlyCost));
      // },
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      selectedVendorByOption: {
        get() {
         return getSelectedVendorsByWidget(this.draftWidgetConfig, this, COMPARE_COST_TREND_VENDORS);
        },
        set(selectedVendorByOption) {
          this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [selectedVendorByOption]);
        }
      }
    },
    watch: {
      widgetConfig: {
        handler: function() {
          this.draftWidgetConfig = _cloneDeep(this.widgetConfig);
        },
        immediate: true
      },
      draftWidgetConfig: {
        handler(newVal, oldVal) {
          if (_isEqual(newVal, oldVal)) {
            return;
          }
          if (_isNil(this.draftWidgetConfig)) {
            return;
          }
          // this.loadDashboardTrend(this.draftWidgetConfig)
        },
        immediate: true
      }
    },
		methods: {
      onSubmit(evt) {
        evt.preventDefault();
        this.$emit('apply', this.draftWidgetConfig);
      },
      onReset(evt) {
        evt.preventDefault();
      },
      onClickCancelButton() {
        this.$emit('hideModal');
      }
      // loadDashboardTrend(widgetConfig) {
      //   this.isLoading = true;
      //   let payload = {
      //     ...widgetConfig,
      //     vendors: this.commonUserInfo.selectedVendors
      //   };
      //   fetchDataCompareCostTrend(payload)
      //   .then((res) => {
      //     this.isLoading = false;
      //     this.dashboardCompareCostTrend = res;
      //   })
      //   .catch((err) => {
      //     this.isLoading = false;
      //     console.error(err);
      //   });
      // },
      // onChangeTimeFrame() {
      //   this.loadDashboardTrend(this.draftWidgetConfig);
      // },
		}
  };
</script>

<style lang="scss">
  .lb-preview {
    font-size: 12px;
    font-weight: 500;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.33;
    letter-spacing: normal;
    color: #222222;
  }
  .edit-abnormal-form {
    #view-by-group {
      #view-by-group__BV_label_ {
        font-family: 'NotoSansCJKkr-Medium';
      }
    }
  }
</style>
