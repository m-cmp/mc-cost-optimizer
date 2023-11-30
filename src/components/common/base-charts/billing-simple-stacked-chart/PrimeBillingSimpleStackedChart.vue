<template>
  <div id = "test">
    <BillingStackChart
      :data="stackedChartData[0]"
      :options="stackedOptions"
      :height="280"
      type="bar"
    />
  </div>
</template>

<script>
import BillingStackChart from 'primevue/chart'
export default {
  name: "PrimeBillingSimpleStackedChart",
  components:{
    BillingStackChart
  },
  props:{
    stackedChartData :{
      type: Array,
      required: true
    },
    firstMonthIdx:{
      type: Number,
      required: true
    },
    lastMonthIdx:{
      type: Number,
      required: true
    },
    startChartIdx:{
      type:Number,
      required: true
    }
  },
  data(){
    const $vm = this
    return{
      stackedOptions: {
        onClick: (event,elements) => {
          this.selectActiveIdx(elements)
        },
        maintainAspectRatio: false,
        interaction: {
          mode: 'index',
          intersect: true
        },
        plugins: {
          tooltip: {
            enabled: true,
            backgroundColor: '#ffffff',
            borderColor: '#495057',
            borderWidth: 1,
            titleColor: '#000000',
            bodyColor: '#000000',
            footerColor: '#000000',
            boxWidth: 7,
            boxHeight: 7,
            boxPadding: 4,
            callbacks: {
              title: function (context) {
                return '';
              },
              label: ((tooltipItem) => {
                let currency = this.stackedChartData[2][tooltipItem.dataIndex]
                return tooltipItem.dataset.label + `:  ` + currency + tooltipItem.formattedValue;

              }),
              footer: function (context) {
                let sum = 0;
                context.forEach(function (data) {
                  sum += data.parsed.y;
                })
                let currency = $vm.stackedChartData[2][context[0].dataIndex]
                return `총계 : ` + currency + sum.toLocaleString();
              }
            }
          },
          legend: {
            onClick:false,
            labels: {
              boxWidth:20,
              boxHeight:5,
              generateLabels: (chart) => {
                const desiredLabels = [$vm.$t('billing.monthlyBillTrend.cloudServiceCharge'), $vm.$t('billing.monthlyBillTrend.additionalServiceFee')]; // 원하는 범례 항목으로 하드코딩
                const desiredColors = ['#0672ff', '#99a3bf']; // desiredLabels 배열과 동일한 순서로 색상 지정

                const labels = desiredLabels.map((label, index) => {
                  const color = desiredColors[index]; // 해당 인덱스에 대한 색상 선택
                  return {
                    text: label,
                    fillStyle: color,
                    hidden: false,
                    lineDash: [],
                    lineDashOffset: 0,
                    rotation: 0
                  };
                });

                return labels;
              }
            },
            position:'bottom'
          }
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
            min:0,
            ticks: {
              color: '#495057',
              maxTicksLimit : 7,
              callback: function (label){
                let _label = setStringNumber(label);
                return _label;
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
  methods:{
    selectActiveIdx(elements){
      let activeIdx
      if(this.lastMonthIdx < 12){
        activeIdx =elements[0].index - this.startChartIdx
      } else {
        activeIdx = this.firstMonthIdx + elements[0].index
      }
      this.$emit('selectColumn',activeIdx)
      return activeIdx;
    },
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
</script>

<style scoped>

</style>
