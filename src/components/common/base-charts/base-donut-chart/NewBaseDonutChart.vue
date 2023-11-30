<template>
  <div
    :style="{
      height: divParentHeightDonutChart,
      padding: divParentPaddingDonutChart}">
    <div class="donut-chart-wrapper">
      <div class="donut-chart">
        <div>
          <NewBaseDonutChart
            ref="newBaseDonutChart"
            :id="donutChartId ? donutChartId : 'donut-chart'"
            :style="{
              width: widthDonutChart,
              float: floatDonutChart,
              height: heightDonutChart,
              marginTop: marginTopDonutChart,
              paddingLeft: paddingLeftDonutChart,
              paddingRight: paddingRightDonutChart
            }"
            :data="basicData"
            :options="basicOptions"
            type="doughnut"
          />
        </div>
        <div
          :id="donutChartLegendId ? donutChartLegendId : 'donut-chart-legend'"
          :style="{
            width: legendWidth + '%',
            float: floatLegendDonutChart,
            marginLeft: marginLeftLegendDonutChart}"
          class="legend-container">
          <div v-if="basicData !== null">
            <div
              v-for="(option, i) in basicData.labels"
              :key="i"
              :id="donutChartLegendId + i"
              :class="enableDrillDown?'legend-item enable-drill-down':'legend-item disable-drill-down'"
              @click="onClickLegend"
            >
              <div
                :style="getLegendBoxStyle(i)"
                :class="getLegendBoxClass(i)"
                class="legend-box"
              />
              <p
                :title="option"
                :style="{fontSize: legendFontSize + 'px', paddingTop: legendPaddingTop + 'px', paddingBottom: legendPaddingBottom + 'px', paddingRight: '30px'}"
                class="ellipsis legend-item-category">{{ option }}</p>
              <p
                :style="{fontSize: legendFontSize + 'px', paddingTop: legendPaddingTop + 'px', paddingBottom: legendPaddingBottom + 'px' }"
                class="legend-item-value">{{ formatLegendCategoryValue(basicData.datasets[0].data[i]) }}</p>
            </div>
          </div>
        </div>
      </div>
      <div
        :id="donutChartId ? `${donutChartId}-loading` : 'donut-chart-loading'"
        class="loading-wrapper"
      >
        <div
          :style="{right: `calc(50% + ${loadingItemRightPx}px)`}"
          class="loading-items"
        >
          <div class="spinner-icon"/>
          <div class="text">{{ $t('loadingChart') }}...</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NewBaseDonutChart from "primevue/chart";
import {formatCost} from '@/util/costUtils';
import _isEmpty from "lodash/isEmpty";
import _cloneDeep from 'lodash/cloneDeep';

export default {
  components: {
    NewBaseDonutChart
  },
  props: {
    baseData: {
      type: Object,
      default: null,
      require: true
    },
    widthDonutChart: {
      type: String,
      default: '34.6%'
    },
    floatDonutChart: {
      type: String,
      default: 'left'
    },
    heightDonutChart: {
      type: String,
      default: '318px'
    },
    divParentHeightDonutChart: {
      type: String,
      default: '366px'
    },
    divParentPaddingDonutChart: {
      type: String,
      default: '0px'
    },
    marginTopDonutChart: {
      type: String,
      default: '30px'
    },
    paddingLeftDonutChart: {
      type: String,
      default: '15px'
    },
    paddingRightDonutChart: {
      type: String,
      default: '15px'
    },
    donutChartId: {
      type: String,
      required: false,
      default: 'donut-chart'
    },
    donutChartLegendId: {
      type: String,
      required: false,
      default: 'donut-chart-legend'
    },
    linePatternCategories: {
      type: Array,
      required: false,
      default: null
    },
    legendWidth: {
      type: Number,
      required: false,
      default: 30
    },
    legendFontSize: {
      type: Number,
      default: 14
    },
    legendPaddingBottom: {
      type: Number,
      default: 7
    },
    legendPaddingTop: {
      type: Number,
      default: 7
    },
    floatLegendDonutChart: {
      type: String,
      default: 'right'
    },
    marginLeftLegendDonutChart: {
      type: String,
      default: '-80px'
    },
    legendBoxTop: {
      type: Number,
      default: -12
    },
    enableDrillDown:{
      default: false,
      type: Boolean
    },
    loadingItemRightPx: {
      type: Number,
      default: 0
    },
    valuePrefix:{
      type: String,
      default: ''
    }
  },
  data() {
    const $vm = this;
    return {
      testValue: null,
      chartWidth: this.chartWidthValue,
      chartHeight: this.chartHeightValue,
      basicData: null,
      basicOptions: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '75%',
        animation: {
          duration: 0,
        },
        interaction: {
          intersect: true
        },
        onHover: function (evt, chartElement){
          if(chartElement.length === 1){
            $vm.hoverChart(chartElement[0].index);
          } else{
            $vm.leaveChart();
          }
        },
        plugins: {
          tooltip: {
            backgroundColor: '#ffffff',
            borderColor: '#495057',
            borderWidth: 1,
            titleColor: '#000000',
            bodyColor: '#000000',
            footerColor: '#000000',
            boxWidth: 10,
            boxHeight: 10,
            callbacks: {
              label: ((tooltipItem) => {
                let checkZero = $vm.basicData.percentage.every(element => element === 0);
                if(checkZero){
                  return '0.00%';
                } else {
                  return $vm.basicData.percentage[tooltipItem.dataIndex];
                }
              })
            }
          },
          legend: {
            display: false
          }
        }
      }
    }
  },
  watch: {
    baseData: function() {
      this.basicData = _cloneDeep(this.baseData);
    },
    basicData: function (){
      if (_isEmpty(this.basicData)) {
        return;
      }
      this.initChart();

      let itemIDs = this.basicData.labels.map((data, idx) => this.donutChartLegendId + idx);
      let $vm = this;

      document.getElementById(this.donutChartLegendId).onmouseover = function (event) {
        let targetID = event.target.parentNode.id;
        let hoveringIndex = itemIDs.indexOf(targetID);
        if (hoveringIndex < 0) {
          hoveringIndex = itemIDs.indexOf(event.target.id);
        }
        $vm.hoverChart(hoveringIndex);
      }

      document.getElementById(this.donutChartLegendId).onmouseout = function (event) {
        $vm.leaveChart();
      }
    }
  },
  mounted() {
  },
  updated() {
  },
  methods: {
    initChart(){
      this.$refs.newBaseDonutChart.reinit();

      const loadingId = this.donutChartId ? `${this.donutChartId}-loading` : 'donut-chart-loading';
      const loadingWrapper = document.getElementById(loadingId);
      loadingWrapper && loadingWrapper.parentElement.removeChild(loadingWrapper);
    },
    getLegendBoxStyle(index) {
      return {
        background: this.basicData.datasets[0].backgroundColor[index],
        top: this.legendBoxTop + 'px',
      }
    },
    getLegendBoxClass(index) {
      for (let i = 0; i < this.linePatternCategories.length; i++) {
        const uppercaseField = this.basicData.labels[index].toUpperCase()
        if (uppercaseField.includes(this.linePatternCategories[i].value)) {
          return this.linePatternCategories[i].class;
        }
      }
    },
    formatLegendCategoryValue(value){
      return this.valuePrefix + formatCost(Math.round(Number(value)*100)/100);
    },
    onClickLegend() {
      this.$emit('clickLegend');
    },
    hoverChart(value){
      this.baseData.datasets[0].backgroundColor.forEach((color, index, colors) => {
        this.basicData.datasets[0].backgroundColor[index] = index === value || color.length === 9 ? color : color + '4D';
        this.$refs.newBaseDonutChart.refresh();
      })
      this.fadeLegends('over', value);
    },
    leaveChart(){
      this.basicData.datasets[0].backgroundColor.forEach((color, index, colors) => {
        this.basicData.datasets[0].backgroundColor[index] = color.length === 9 ? color.slice(0, -2) : color;
        this.$refs.newBaseDonutChart.refresh();
      })
      this.fadeLegends('out', 10);
    },
    fadeLegends(action, hoveringIndex) {
      let chartValue = this.$refs.newBaseDonutChart.getChart();

      switch (action) {
        case 'over':
          this.tooltipActive(chartValue, hoveringIndex);
          for (let i = 0; i < this.basicData.labels.length; i++) {
            if (i === hoveringIndex) {
              document.getElementById(this.donutChartLegendId + i).style.opacity = 1;
            } else {
              document.getElementById(this.donutChartLegendId + i).style.opacity = 0.4;
            }
          }
          break;
        case 'out':
          this.tooltipInactive(chartValue);
          for (let i = 0; i < this.basicData.labels.length; i++) {
            document.getElementById(this.donutChartLegendId + i).style.opacity = 1;
          }
          break;
      }
    },
    tooltipActive(chart, index){
      chart.tooltip.setActiveElements([
          {datasetIndex: 0, index: index}
        ]);
      this.$refs.newBaseDonutChart.refresh();
    },
    tooltipInactive(chart){
      chart.tooltip.setActiveElements([
      ]);
      this.$refs.newBaseDonutChart.refresh();
    }
  }
}
</script>

<style lang="scss" scoped>
  .donut-chart-wrapper {
    width: 100%;
    height: 100%;
    position: relative;
    .donut-chart, .loading-wrapper {
      width: 100%;
      height: 100%;
      position: absolute;
    }
  }

  #donut-chart {
    width: 100%;
    height: 300px;
  }

  .legend-box {
    position: relative;
    font-size: 14px;
    width: 10px;
    height: 10px;
    display: inline-block;
    background-size: 10px 10px;
  }

  .ellipsis {
    white-space: nowrap;
    width: 70%;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .legend-item {
    border-bottom: solid rgba(29, 28, 209, 0.08) 1px;
    width: 100%;
    position: relative
  }

  .legend-item.enable-drill-down {
    cursor: pointer; /*드릴다운 포인터 : Top5*/
  }

  .legend-item.disable-drill-down {
    cursor: default; /*드릴다운 포인터 삭제 : Top5*/
  }

  .legend-container {
    position: absolute;
    margin-left: 35.4% !important;
    transform: translate(0,-49%);
    top: 50%;
    width: 61.4% !important;
  }

  .legend-item-category {
    display: inline-block;
    font-size: 14px;
    padding: 7px
  }

  .legend-item-value {
    display: inline-block;
    position: absolute;
    right:  0;
    font-size: 14px;
    padding: 7px 13px 7px 7px;
  }

  @media not all and (min-resolution:.001dpcm) {
    @supports (-webkit-appearance:none) {
      .legend-box {
        top: 0px!important;
      }
    }
  }

</style>
