<template>
  <div id="test">
    <StackChart
      ref="basicChart"
      :data="stakedChartPrimeData"
      :options="optionsDataByScaleSet"
      :plugins="plugins"
      :height="370"
      type="bar" />
    <div
      id="tooltip-cost-stack"
      ref="tooltipBox"
      :class="tooltipStyle"
    />
  </div>
</template>

<script>
import StackChart from 'primevue/chart'
import {SCALE}from '@/constants/dashboardConstants';
import ChartJSPluginStacked100 from 'chartjs-plugin-stacked100';

export default {
  name: 'PrimeStackedColumnChart',
  components: {
    StackChart
  },
  props: {
    stakedChartPrimeData: {
      type: Object,
      required: true
    },
    valuePrefix: {
      type: String,
      default: ''
    },
    scale: {
      required: true,
      type: String,
      default: ''
    }

  },
  data() {
    const $vm = this;
    return {
      tooltipIndex: null,
      plugins: [ChartJSPluginStacked100],
      stackedOptions: {
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: true
        },
        plugins: {
          stacked100: {},
          tooltip: {
            // enabled: true,
            // backgroundColor: '#ffffff',
            // borderColor: '#495057',
            // borderWidth: 1,
            // titleColor: '#000000',
            // bodyColor: '#000000',
            // footerColor: '#000000',
            // boxWidth: 7,
            // boxHeight: 7,
            // boxPadding: 4,
            // callbacks: {
            //   title: function (context) {
            //     return `${context[0].label}`;
            //   },
            //   label: ((tooltipItem) => {
            //     if (this.scale == SCALE.PERCENTAGE) {
            //       return tooltipItem.dataset.label + `:  ` + tooltipItem.formattedValue + ' %';
            //     }
            //     return tooltipItem.dataset.label + `:  ` + $vm.valuePrefix + tooltipItem.formattedValue;
            //   }),
            //   footer: function (context) {
            //     let sum = 0;
            //     context.forEach(function (data) {
            //       sum += data.parsed.y;
            //     })
            //     if ($vm.scale == SCALE.PERCENTAGE) {
            //       return '총계 : ' + Math.round(sum).toLocaleString() + '%';
            //     }
            //     return `총계 : ` + $vm.valuePrefix + sum.toLocaleString();
            //   }
            // }
            enabled: false,
            external: function (model) {
              const tooltip = document.getElementById("tooltip-cost-stack");

              if (model.tooltip.opacity === 0) {
                tooltip.style.opacity = 0;
                return;
              }

              if (model.tooltip.body) {
                let dataIndex = model.tooltip.dataPoints[0].dataIndex;
                let dataLength = model.tooltip.dataPoints[0].dataset.data.length;
                let sum = 0
                model.tooltip.dataPoints.forEach(function (data){
                  sum += data.parsed.y;
                })
                $vm.setTooltipIndex(dataIndex, dataLength);
                const title = "<strong>" + model.tooltip.title[0] + "</strong>";
                let label;
                if ($vm.scale == SCALE.PERCENTAGE){
                  label = `<table id="custom-tooltip-cost-stack-chart">
                      ${model.tooltip.dataPoints.map(item=> getTooltipHtmlByCategoryFieldPercentage(item, dataIndex, $vm)).join('')}
                   <table/>`;
                } else {
                  label = `<table id="custom-tooltip-cost-stack-chart">
                      ${model.tooltip.dataPoints.map(item=> getTooltipHtmlByCategoryField(item, dataIndex, $vm)).join('')}
                   <table/>`;
                }
                let footer;
                if ($vm.scale == SCALE.PERCENTAGE) {
                  footer = `<div style="display: flex; justify-content: space-between;">
                                    <strong>${$vm.$t('dashboard.tooltipText.total')} </strong>
                                    <strong>${Math.round(sum).toLocaleString()}%</strong>
                                </div>`;
                } else {
                  footer = `<div style="display: flex; justify-content: space-between;">
                                    <strong>${$vm.$t('dashboard.tooltipText.total')} </strong>
                                    <strong>${$vm.valuePrefix}${sum.toLocaleString()}</strong>
                                </div>`;
                }

                tooltip.innerHTML = title + "<br />" + label + footer;

                tooltip.style.left = "auto";
                tooltip.style.right = "auto";

                //const pos = model.chart.canvas.getBoundingClientRect();

                if ($vm.tooltipIndex === "left") {
                  tooltip.style.left = model.tooltip.caretX + 10 + "px";
                } else if($vm.tooltipIndex === "right"){
                  tooltip.style.left =  model.tooltip.caretX - $vm.$refs.tooltipBox.getBoundingClientRect().width - 10 + 'px';  //pos.right -model.tooltip.caretX -85 + "px"
                }

                tooltip.style.top = - 110 + model.tooltip.y +'px';
                tooltip.style.opacity = 1;
              }
            }
          },
          legend: {
            labels: {
              boxWidth: 7,
              boxHeight: 7,
              padding: this.legendPadding
            },
            position: 'bottom',
            align: 'start',
          },
        },
        scales: {
          x: {
            stacked: true,
            ticks: {
              color: '#495057'
            },
            grid: {
              display: false,
              color: '#ebedef'
            }
          },
          y: {
            stacked: true,
            ticks: {
              color: '#495057',
              maxTicksLimit : 7,
              callback: function (label){
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
    optionsDataByScaleSet: {
      cache: true,
      get() {
        const options = this.stackedOptions;
        switch (this.scale) {
          case SCALE.VALUE :
            options.plugins.stacked100.enable = false
            options.plugins.stacked100.replaceTooltipLabel = false
            options.scales.y.max = null
            break;
          case SCALE.PERCENTAGE:
            options.plugins.stacked100.enable = true
            options.plugins.stacked100.replaceTooltipLabel = false
            options.scales.y.max = 100
            break;
        }
        return options;
      }
    },
    tooltipStyle: function () {
      if(this.tooltipIndex === 'right'){
        return "tooltip-right"
      } else {
        return "tooltip-left"
      }
    }
  },
  methods: {
    reloadChart() {
      this.$refs.basicChart.reinit();
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

function getTooltipHtmlByCategoryField(stackData, dataIndex, $vm){
  const itemLabel = stackData.dataset.label;
  const itemColor = stackData.element.options.backgroundColor;
  return `
      <tr>
        <th align="left" class="tooltip-padding-left-name">
        <span class="rectangle-left" style="border: 2px solid ${itemColor}"></span>${itemLabel}
        </th>
        <td align="right">${$vm.valuePrefix}${stackData.formattedValue}</td>
      </tr>
    `
}

function getTooltipHtmlByCategoryFieldPercentage(stackData, dataIndex, $vm){
  const itemLabel = stackData.dataset.label;
  const itemColor = stackData.element.options.backgroundColor;
  return `
      <tr>
        <th align="left" class="tooltip-padding-left-name">
        <span class="rectangle-left" style="border: 2px solid ${itemColor}"></span>${itemLabel}
        </th>
        <td align="right">${stackData.formattedValue} %</td>
      </tr>
    `
}
</script>

<style lang="scss">

#tooltip-cost-stack {
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
  padding: 7px;
  //font-family: 'Helvetica Neue', 'Helvetica', 'Arial', sans-serif;
  font-size: 13px;
}

#custom-tooltip-cost-stack-chart {
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

#tooltip-cost-stack::before {
  content: "";
  position: absolute;
  top: 85%;
  border-width: 5px;
  border-style: solid;
}

#tooltip-cost-stack::after{
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
