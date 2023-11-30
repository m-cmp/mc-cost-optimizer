<template>
  <div class="chart-wrapper">
    <head>
      <link
        rel="stylesheet"
        href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,400,0,0">
    </head>
    <div class="sliderArea">
      <tc-range-slider
        id="slider"
        ref="slider"
        :value1="leftFixedValue"
        :value2="rightFixedValue"
        range-dragging="true"
        min="1"
        max="31"
        generate-labels="true"
        slider-width="1345px"
        slider-height="2.5rem"
        slider-bg-fill="rgba(225,224,224, 0.3)"
        slider-bg="white"
        pointer-bg="rgba(225,224,224)"
        pointer-border="1px solid white"
        pointer-width="20px"
        pointer-height="20px"
        round="0"
        slider-radius="0"
      />
    </div>
    <div class="chart-container">
      <button
        v-show="zoomoutBoolean"
        class="round-button"
        @click="zoomoutAction"
      >
        <span class="material-symbols-outlined">
          remove
        </span>
      </button>
      <NewBaseLineAreaChart
        ref="newBaseLineAreaChart"
        :data="basicData"
        :options="basicOptions"
        :width="chartWidth"
        :height="chartHeight"
        type="line"
      />
      <div
        id="tooltip-costTrend"
        ref="tooltipBox"
        :class="tooltipStyle"
      />
    </div>
  </div>
</template>

<script>
import NewBaseLineAreaChart from "primevue/chart";
import PButton from 'primevue/button';
import 'toolcool-range-slider/dist/plugins/tcrs-storage.min';
import 'toolcool-range-slider';
import _cloneDeep from 'lodash/cloneDeep';
import dayjs from "dayjs";
import _isEmpty from "lodash/isEmpty";

export default {
  components: {
    NewBaseLineAreaChart,
    PButton
  },
  props: {
    baseData: {
      type: Object,
      default: null,
      require: true
    },
    chartWidthValue: {
      type: Number,
      default: 300,
      require: false
    },
    chartHeightValue: {
      type: Number,
      default: 310,
      require: false
    },
    legendPadding: {
      type: Number,
      default: 10,
      require: false
    },
    valuePrefix: {
      type: String,
      default: ''
    }
  },
  data() {
    const $vm = this;
    return {
      $slider: null,
      tooltipIndex: null,
      leftFixedValue: 1,
      rightFixedValue: 31,
      labelsValue: null,
      datasetsValue: null,
      zoomoutBoolean: false,
      chartWidth: this.chartWidthValue,
      chartHeight: this.chartHeightValue,
      basicData: null,
      basicOptions: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: false
        },
        elements: {
          point: {
            radius: 0
          }
        },
        animation: {
          duration: 0,
        },
        plugins: {
          tooltip: {
            enabled: false,
            external: function (model) {
              const tooltip = document.getElementById("tooltip-costTrend");

              if (model.tooltip.opacity === 0) {
                tooltip.style.opacity = 0;
                return;
              }

              if (model.tooltip.body) {
                let dataIndex = model.tooltip.dataPoints[0].dataIndex;
                let dataLength = model.tooltip.dataPoints[0].dataset.data.length;
                $vm.setTooltipIndex(dataIndex, dataLength);
                const title = "<strong>" + $vm.$t('billing.monthlyBillTrend.actualBillCost') + "</strong>";
                const label =
                  `<table id="custom-tooltip-costTrend-chart">
                      ${model.tooltip.dataPoints.map(item=> getTooltipHtmlByCategoryField(item, dataIndex, $vm)).join('')}
                   <table/>`;

                tooltip.innerHTML = title + "<br />" + label;

                tooltip.style.left = "auto";
                tooltip.style.right = "auto";

                // const pos = model.chart.canvas.getBoundingClientRect();

                if ($vm.tooltipIndex === "left") {
                  tooltip.style.left = model.tooltip.caretX + 10 + "px";
                } else if($vm.tooltipIndex === "right"){
                  tooltip.style.left =  model.tooltip.caretX - $vm.$refs.tooltipBox.getBoundingClientRect().width - 10 + 'px';  //pos.right -model.tooltip.caretX -85 + "px"
                }

                tooltip.style.top = -80 + model.tooltip.caretY +'px';
                tooltip.style.opacity = 1;
              }
            }
          },
          legend: {
            labels: {
              boxWidth: 20,
              boxHeight: 0,
              padding: this.legendPadding
            },
            position: 'bottom',
            align: 'center',
            onClick: ((e, legendItem) => {
              $vm.$emit('hitLegend', legendItem.text);
            })
          }
        },
        scales: {
          x: {
            ticks: {
              color: '#495057'
            },
            grid: {
              display: false,
              color: '#ebedef'
            }
          },
          y: {
            ticks: {
              maxTicksLimit: 6,
              color: '#495057',
              callback: function (label, index){
                let _label = setStringNumber(label);
                return $vm.valuePrefix + _label;
              }
            },
            grid: {
              color: '#ebedef'
            }
          }
        }
      }
    }
  },
  computed: {
    tooltipStyle: function () {
      if(this.tooltipIndex === 'right'){
        return "tooltip-right"
      } else {
        return "tooltip-left"
      }
    }
  },
  watch: {
    baseData: {
      handler(newVal){
        if(_isEmpty(newVal)){
          return
        }
        this.basicData = _cloneDeep(newVal);
        this.$slider.value = 1;
        this.$slider.value2 = 31;
        this.labelsValue = newVal.labels.slice();
        this.datasetsValue = newVal.datasets.slice();
      },
      immediate: true
    }
  },
  mounted(){
    this.$slider = document.getElementById('slider');
    this.zoomoutBoolean = false;

    this.$slider.addEventListener('change', (evt) => {
      this.updateMinMax(evt.detail.value, evt.detail.value2);
      if(evt.detail.value !== 1 || evt.detail.value2 !== 31){
        this.zoomoutBoolean = true;
      }else{
        this.zoomoutBoolean = false;
      }
    });

  },
  methods: {
    reloadChart(){
      this.$refs.newBaseLineChart.reinit();
    },
    updateMinMax(range1, range2){
      let labelsValue = this.labelsValue.slice();
      this.basicData.labels = labelsValue.slice(range1 -1, range2);

      let dataValue = this.baseData.datasets.slice();
      dataValue.forEach((item, index) => {
        this.basicData.datasets[index].data = item.data.slice(range1 -1, range2);
      });
    },
    zoomoutAction(){
      this.$slider.value = 1;
      this.$slider.value2 = 31;
      this.basicData.labels = this.labelsValue.slice();
      this.baseData.datasets.forEach((item, index) => {
        this.basicData.datasets[index].data = item.data.slice();
      })
    },
    setTooltipIndex(index, length){
      if(index +1 > length/2){
        this.tooltipIndex = 'right';
      } else {
        this.tooltipIndex = 'left';
      }
    }
  }
}

function setStringNumber(count){
  if(count >= 1000 && count < 10000){ //1k
    return (count / 1000) + "k";
  }
  else if(count >= 10000 && count < 100000){ //10k
    return (count / 1000) + "k";
  }
  else if(count >= 100000 && count < 1000000){ //100k
    return (count / 1000) + "k";
  }
  else if(count >= 1000000 && count < 10000000){ //1M
    return (count / 1000000) + "M";
  }
  else if(count >= 10000000 && count < 100000000){ //10M
    return (count / 1000000) + "M";
  }
  else if(count >= 100000000 && count < 1000000000){ //100M
    return (count / 1000000) + "M";
  }
  else if(count >= 1000000000){ //1B
    return (count / 1000000000) + "B";
  }

  return count+"";
}

function getTooltipHtmlByCategoryField(lineData, dataIndex, $vm){
  let itemLabel;
  if(lineData.dataset.label === $vm.$t('dashboard.compareCostTrend.thisMonth')){
    itemLabel = dayjs().format('YYYY/MM').toString() + `/` + lineData.label + ` (` + lineData.dataset.label + `)`;
  } else {
    itemLabel = lineData.dataset.label+'/'+lineData.label;
  }
  const itemColor = lineData.dataset.borderColor;
  return `
      <tr>
        <th align="left" class="tooltip-padding-left-name">
        <span class="rectangle-left" style="border: 1px solid ${itemColor}"></span>${itemLabel}
        </th>
        <td align="right">${$vm.valuePrefix}${lineData.formattedValue}</td>
      </tr>
    `
}

</script>

<style lang="scss">
.sliderArea {
  display: flex;
  padding-top: 20px;
  padding-bottom: 10px;
  padding-left: 50px;
  padding-right: 10px;
}

#slider {
  flex: 1;
}

.chart-container {
  position: relative;
}

.round-button {
  width: 35px;
  height: 35px;
  position: absolute;
  z-index: 9000;
  top: 18px;
  right: 20px;
  border-radius: 50%;
  background-color: rgba(121,148,214, 0.7);
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);
  padding: 0px;
}

.round-button:hover {
  background-color: rgba(121,148,214, 1);
}

.material-symbols-outlined {
  font-size: 17px;
  top: 50%;
  left: 50%;
  position: absolute;
  transform: translate(-50%, -50%);
  color : white;
}

#tooltip-costTrend {
  opacity: 0;
  position: absolute;
  background: white;
  color: black;
  border: 1px solid lightgray;
  box-shadow: 2px 2px 2px lightgray;
  border-radius: 6px;
  -webkit-transition: all 0.1s ease;
  transition: opacity 0.2s;
  pointer-events: none;
  padding: 10px;
  //font-family: 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;
  font-size: 13px;
  z-index: 9001;
}

#custom-tooltip-costTrend-chart {
  font-size: 12px;

  .tooltip-padding-left-name {
    padding-right: 50px;
  }
  .custom-tr {
    border-top: 1px solid rgba(34, 34, 34, 0.4);
  }
  .custom-text-total {
    color: #222222;
    font-weight: bold;
  }
  .rectangle-left {
    width: 20px;
    height: 0px;
    display: inline-block;
    margin-right: 8px;
    margin-top: 4px;
  }
  th {
    color: rgba(34, 34, 34, 0.8);
    font-weight: normal;
  }
  th, td {
    padding: 2px 0;
  }
}

.tooltip-title-time {
  font-weight: bold;
}

#tooltip-costTrend::before {
  content: "";
  position: absolute;
  top: 85%;
  border-width: 5px;
  border-style: solid;
}

#tooltip-costTrend::after{
  border: solid 1px #6e7884;
}

.tooltip-left::before {
  right: 100%;
  transform: translateX(-10%);
  border-color: transparent #495057 transparent transparent;
}

.tooltip-right::before {
  left: 100%;
  transform: translateX(10%);
  border-color: transparent transparent transparent #495057;
}

</style>

