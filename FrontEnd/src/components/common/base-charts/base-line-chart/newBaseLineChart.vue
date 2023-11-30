<template>
  <div class="chart-container">
    <NewBaseLineChart
      ref="newBaseLineChart"
      :data="baseData"
      :options="baseOptions"
      :width="chartWidth"
      :height="chartHeight"
      type="line"
      @select="handleChartClick"/>
    <div
      id="tooltip-cost-line"
      ref="tooltipBox"
      :class="tooltipStyle"
    />
  </div>
</template>

<script>
import NewBaseLineChart from 'primevue/chart';
import _get from "lodash/get";

export default {
  components: {
    NewBaseLineChart
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
      default: 370,
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
  data(){
    const $vm = this;
    return{
      tooltipIndex: null,
      chartWidth: this.chartWidthValue,
      chartHeight: this.chartHeightValue, // reSizing을 위한 필수요소
      baseOptions: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: false
        },
        plugins: {
          tooltip: {
            enabled: false,
            external: function (model) {
              const tooltip = document.getElementById("tooltip-cost-line");

              if (model.tooltip.opacity === 0) {
                tooltip.style.opacity = 0;
                return;
              }

              if (model.tooltip.body) {
                let dataIndex = model.tooltip.dataPoints[0].dataIndex;
                let dataLength = model.tooltip.dataPoints[0].dataset.data.length;
                let sum = 0
                model.tooltip.dataPoints.forEach(function (data){
                  sum += data.raw;
                })
                $vm.setTooltipIndex(dataIndex, dataLength);
                const title = "<strong>" + model.tooltip.title[0] + "</strong>";
                const label =
                  `<table id="custom-tooltip-cost-line-chart">
                      ${model.tooltip.dataPoints.map(item=> getTooltipHtmlByCategoryField(item, dataIndex, $vm)).join('')}
                   <table/>`;
                const footer = `<div style="display: flex; justify-content: space-between;">
                                    <strong>${$vm.$t('dashboard.tooltipText.total')} </strong>
                                    <strong>${$vm.valuePrefix}${sum.toLocaleString()}</strong>
                                </div>`;

                tooltip.innerHTML = title + "<br />" + label + footer;

                tooltip.style.left = "auto";
                tooltip.style.right = "auto";

                //const pos = model.chart.canvas.getBoundingClientRect();

                if ($vm.tooltipIndex === "left") {
                  tooltip.style.left = model.tooltip.caretX + 10 + "px";
                } else if($vm.tooltipIndex === "right"){
                  tooltip.style.left =  model.tooltip.caretX - $vm.$refs.tooltipBox.getBoundingClientRect().width - 10 + 'px';  //pos.right -model.tooltip.caretX -85 + "px"
                }

                tooltip.style.top = - 240 + model.tooltip.caretY +'px';
                tooltip.style.opacity = 1;
              }
            }
          },
          legend: {
            labels: {
              boxWidth: 3,
              boxHeight: 3,
              padding: this.legendPadding
            },
            position: 'bottom',
            align: 'start',
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
      handler(){
        this.reloadChart();
      }
    },
    valuePrefix: {
      handler(){
        this.reloadChart();
      }
    }
  },
  mounted(){

  },
  methods: {
    reloadChart(){
      this.$refs.newBaseLineChart.reinit();
    },
    handleChartClick(event){
      let selectedLineChartData = {};
      selectedLineChartData[`time`] = this.baseData.labels[event.element.index];
      selectedLineChartData[`startDate`] = this.baseData.time[event.element.index].startDate;
      selectedLineChartData[`endData`] = this.baseData.time[event.element.index].endDate;
      this.$emit('clickChart', selectedLineChartData);
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
  const itemLabel = lineData.dataset.label;
  const itemColor = lineData.dataset.borderColor;
  return `
      <tr>
        <th align="left" class="tooltip-padding-left-name">
        <span class="rectangle-left" style="border: 2px solid ${itemColor}"></span>${itemLabel}
        </th>
        <td align="right">${$vm.valuePrefix}${lineData.formattedValue}</td>
      </tr>
    `
}


</script>
<style lang="scss">

#tooltip-cost-line {
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
}

#custom-tooltip-cost-line-chart {
  font-size: 12px;
  border-bottom: 1px solid #dcdcdc;

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
    width: 8px;
    height: 8px;
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

#tooltip-cost-line::before {
  content: "";
  position: absolute;
  top: 85%;
  border-width: 5px;
  border-style: solid;
}

#tooltip-cost-line::after{
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

