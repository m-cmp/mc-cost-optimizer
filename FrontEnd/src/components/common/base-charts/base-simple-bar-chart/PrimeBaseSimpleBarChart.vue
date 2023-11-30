<template>
  <div>
    <BarChart
      ref="basicChart"
      :data="baseData"
      :options="scaleValuePercent"
      :height="chartHeight"
      type="bar"/>
    <div
      id="tooltip-simpleBar"
      ref="tooltipElement"/>
  </div>
</template>
<script>
import BarChart from 'primevue/chart'
import { calculateCostByCurrency } from '@/util/costUtils'
import { SCALE } from '@/constants/dashboardConstants';
import _cloneDeep from "lodash/cloneDeep";
export default {
  name: 'PrimeBaseSimpleBarChart',
  components: {
    BarChart
  },
  props: {
    barChartData: {
      type: [Object, Array], // Invalid prop 에러 방지
      default: null,
      require: true
    },
    scale: {
      type: String,
      default: '',
      required: true
    },
    valuePrefix: {
      type: String,
      default: ''
    },
    tooltipData: {
      type: [Object, Array],
      default: null
    },
    selectedCurrency: {
     type: String,
     default: ''
    },
    exchangeRate: {
      type: Object,
      default: null
    }
  },
  data(){
    const $vm = this;
    return{
      tooltipDataIndex: null,
      observer: null,
      baseData: null,
      chartHeight: 370,
      basicOptions: {
        responsive: true,
        maintainAspectRatio: false,
        maxBarThickness: 21,
        indexAxis: 'y',
        layout: {
          padding: {
            left: 17
          }
        },
        plugins: {
          // Primevue Tooltip BackUp
          // tooltip: {
          //   backgroundColor: '#FFFFFF',
          //   borderColor: '#495057',
          //   borderWidth: 1,
          //   titleColor: '#000000',
          //   bodyColor: '#000000',
          //   footerColor: '#000000',
          //   boxWidth: 7,
          //   boxHeight: 7,
          //   boxPadding: 4,
          //   displayColors: false,
          //   mode: 'y',
          //   intersect: false, // 마우스 커서가 같은 y 축이기만 하면 tooltip 표시
          //   callbacks: {
          //     title: function (context) {
          //       return `${$vm.formatLabels(context[0].label)}`;
          //     },
          //     label: ((chartItem) => {
          //       let tooltipData = $vm.tooltipData
          //       let result = [];
          //       let tooltipDataIndex = tooltipData.findIndex((data) => _.isEqual(data.familyCode, $vm.formatLabels(chartItem.label)))
          //       tooltipData[tooltipDataIndex].familyItems.forEach(item => {
          //         result.push(item.item + `: ` + $vm.valuePrefix + calculateCostByCurrency(item.cost, $vm.selectedCurrency, $vm.exchangeRate).toFixed(2).toLocaleString());
          //       }) ;
          //       return result;
          //     }),
          //     footer: function (context) {
          //       let sum = 0;
          //       context.forEach(function (data){
          //         sum += data.parsed.x;
          //       })
          //       if($vm.scale === SCALE.PERCENTAGE){
          //         return `Percentage : ` + sum.toFixed(2) + '%'
          //       }
          //       return `${$vm.$t('dashboard.tooltipText.total')} : ` + $vm.valuePrefix + sum.toLocaleString();
          //     },
          //   },
          // },
          tooltip: {
            intersect: false,
            mode: 'y',
            enabled: false,
            external: function(context) {
              const tooltipEl = document.getElementById('tooltip-simpleBar');
              const tooltip = context.tooltip;

              if (tooltip.opacity === 0) {
                tooltipEl.style.opacity = 0;
                return;
              }

              if (tooltip.body) {
                let baseData = $vm.baseData.labels;
                let tooltipData = $vm.tooltipData
                $vm.tooltipDataIndex = baseData.findIndex((data) => _.isEqual(data, $vm.formatLabels(tooltip.dataPoints[0].label)));
                let header = `<strong class="tooltip-simpleBar-header">${$vm.formatHeaders(context.tooltip.dataPoints[0].label)}</strong>`;
                let labels =
                  `<table id="custom-simpleBar-tooltip">
                    ${tooltipData[$vm.tooltipDataIndex].familyItems.map(item => getTooltipHtmlByChart(item, $vm)).join('')}
                   <table/>`;
                let footer = '';
                if ($vm.scale === SCALE.PERCENTAGE) {
                  footer =`<div class="tooltip-simpleBar-footer">
                             <span>Percentage : </span>
                             <span>${context.tooltip.dataPoints[0].formattedValue} %</span>
                           </div>`
                }
                else {
                  footer = `<div class="tooltip-simpleBar-footer">
                              <span>${$vm.$t('dashboard.tooltipText.total')} : </span>
                              <span>${$vm.valuePrefix}${context.tooltip.dataPoints[0].raw.toFixed(2).toLocaleString()}</span>
                            </div>`
                }
                tooltipEl.innerHTML = header + "<br>" + labels + footer;
              }
              const canvas = this._chart.canvas;
              // mousemove event listener BackUp
              // canvas.addEventListener('mousemove', (event) => {
              //   const mouseX = event.clientX - canvas.getBoundingClientRect().left;
              //
              //   let tooltipX, tooltipY;
              //   if (mouseX > canvas.width / 2.5) {
              //     tooltipX = mouseX - tooltipEl.offsetWidth - 10;
              //   } else {
              //     tooltipX = mouseX + 10;
              //   }
              //   tooltipY = context.tooltip.caretY - (tooltipHeight / 2);
              //
              //   tooltipEl.style.left = tooltipX + 'px';
              //   tooltipEl.style.top = tooltipY + 'px';
              // });
              const barEndX = context.tooltip.x;
              const barCenterY = context.tooltip.y;

              let tooltipX, tooltipY;

              if (barEndX > canvas.width / 2) {
                tooltipX = barEndX - tooltipEl.offsetWidth - 10;
              } else {
                tooltipX = barEndX + 10;
              }

              tooltipY = barCenterY - (tooltipEl.offsetHeight / 2);

              tooltipEl.style.left = tooltipX + 'px';
              tooltipEl.style.top = tooltipY + 'px';
              tooltipEl.style.opacity = 1;
            },
          },
          legend: {
            display: false
          }
        },
        scales: {
          x: {
            ticks: {
              maxTicksLimit: 8,
              color: '#495057',
              // X axes Prefix 추가
              callback: function (value) {
                if ($vm.scale === SCALE.PERCENTAGE) {
                  return Math.round(value) + '%'
                }
                if (value === 0) {
                  return $vm.valuePrefix + value;
                }
                return $vm.valuePrefix + $vm.numberToEng(value);
              },
              display: function(context) {
                if (!context || !context.scale || !context.scale.chart || !context.scale.chart.config || !context.scale.chart.config.data) return false;

                const datasets = context.scale.chart.config.data.datasets;

                if (!datasets || !Array.isArray(datasets) || datasets.length === 0) return false;

                const data = datasets[0].data;
                if (!data || !Array.isArray(data) || data.length === 0) return false;

                const isValue = data.reduce((sum, value) => sum + value, 0);

                if ($vm.scale === SCALE.PERCENTAGE) return true;

                return isValue > 0;
              },
            },
            grid: {
              color: '#ebedef'
            },
            beginAtZero: true
          },
          y: {
            textStrokeWidth: 50,
            ticks: {
              color: '#495057',
              crossAlign: "far",
              callback: function (index) {
                let labelData = $vm.barChartData.labels[index];
                if (Array.isArray(labelData)) {
                  return [labelData[0] + '     ', labelData[1]];
                }
                return ($vm.barChartData.labels[index]) + '    ';
              }
            },
            grid: {
              display: false
            },
          }
        },
      }
    }
  },
  computed: {
    scaleValuePercent: {
      cache: true,
      get () {
        let options = this.basicOptions
        switch(this.scale){
          case SCALE.VALUE:
            options.scales.x.max = null
            break;
            case SCALE.PERCENTAGE:
              options.scales.x.max = 100;
              break;
        }
        return options;
      }
    },
  },
  watch: {
    barChartData: {
      handler() {
          this.baseData = _cloneDeep(this.barChartData);
      },
      immediate: true
    },
  },
  mounted() {
    const $vm = this;
    const observer = new MutationObserver(mutations => {

      mutations.forEach(mutation => {
        if (mutation.target.style.opacity === '1') {
          this.hoverChart($vm.tooltipDataIndex);
        } else if (mutation.target.style.opacity === '0') {
          this.leaveChart();
        }
      });
    });

    const config = { attributes: true, childList: false, subtree: false };
    observer.observe(document.getElementById('tooltip-simpleBar'), config);
  },
  methods: {
    // 비용 단위 추가
    numberToEng(count) {
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
    },
    formatLabels(text) {
      if (text.includes(',&')) {
        return text.split(/,\s*(?=&)/).map(s => [s]);
        } else if (text.includes('for,')) {
        let forIndex = text.indexOf('for,');
        let splitIndex = forIndex + 'for'.length;

        let firstPart = text.slice(0, splitIndex);
        let secondPart = text.slice(splitIndex + 1);

        let result = [[firstPart], [secondPart]];
        return result;
       }
      return text
    },
    formatHeaders(text) {
      if (text.includes(',&')) {
        return text.replace(/,\s*&/, '&');
      } else if (text.includes('for,')) {
        return text.replace(/for,/, 'for ');
      }
      return text;
    },
    hoverChart(value){
      this.barChartData.datasets[0].backgroundColor.forEach((color, index, colors) => {
        this.baseData.datasets[0].backgroundColor[index] = index === value || color.length === 9 ? color : color + '4D';
        this.$refs.basicChart.refresh();
      })
    },
    leaveChart(){
      this.barChartData.datasets[0].backgroundColor.forEach((color, index, colors) => {
        this.baseData.datasets[0].backgroundColor[index] = color.length === 9 ? color.slice(0, -2) : color;
        this.$refs.basicChart.refresh();
      })
    },

  }
}
function getTooltipHtmlByChart(item, $vm) {
  return `<tr>
            <th class="tooltip-padding-left-name">${item.item}</th>
            <td align="right">${$vm.valuePrefix + calculateCostByCurrency(item.cost, $vm.selectedCurrency, $vm.exchangeRate).toFixed(2).toLocaleString()}</td>
          </tr>`;
}
</script>
<style lang="scss">
#tooltip-simpleBar {
  position: absolute;
  background: #FFFFFF;
  padding: 10px;
  border: 1px solid lightgray;
  border-radius: 6px;
  pointer-events: none;
  transition: opacity 0.2s;
  white-space: normal;
  max-width: 1000px;
  opacity: 0;
  overflow: visible;
  z-index: 9999;
  box-shadow: 2px 2px 2px lightgray;
}

#custom-simpleBar-tooltip {
  border-bottom: 1px solid lightgray;
  padding-bottom: 7px;

  .tooltip-padding-left-name {
    padding-right: 50px;
  }
  th {
    color: rgba(34,34,34,0.8);
    font-weight: normal;
  }
  th,td {
    padding: 0 5px;
  }
  td:last-child {
    padding-bottom: 5px;
  }
}
.tooltip-simpleBar-header {
  padding-bottom: 10px;
}
.tooltip-simpleBar-footer {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  padding: 7px 5px 0px 5px;
}

</style>
