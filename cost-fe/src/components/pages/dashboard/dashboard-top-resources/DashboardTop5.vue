<template>
  <div class="card">
    <div class="card-body">
      <h3 class="card-title">요금 상위 5개 서비스</h3>
      <div id="chart-top5cost-pie" style="min-height: 201.9px;">
      </div>
    </div>
  </div>
</template>

<script>
import ApexCharts from 'apexcharts';

export default {
  name: 'DashboardTop',
  props: {
    origData: {
      type: Object,
      required: true,
    }
  },
  data() {
    return {
      init: true,
      chart: null,
      chartOptions: {
        chart: {
          type: "donut",
          fontFamily: 'inherit',
          height: 300,
          sparkline: {
            enabled: true
          },
          animations: {
            enabled: true
          },
        },
        fill: {
          opacity: 1,
        },
        series: [0, 0, 0, 0, 0, 0],
        labels: ["AmazonECS", "AmazonRDS", "AmazonEC2", "AWSELB", "awswaf", "others"],
        tooltip: {
          theme: 'dark',
          fillSeriesColor: false,
          y: {
            formatter: function (val) {
              return val.toLocaleString('ko-KR') + " USD";
            },
          }
        },
        grid: {
          strokeDashArray: 4,
        },
        colors: ['#008FFB', '#00E396', '#FEB019', '#d6336c', '#4263eb', '#CED4DA'],
        legend: {
          show: true,
          position: 'bottom',
          offsetY: 12,
          markers: {
            width: 10,
            height: 10,
            radius: 100,
          },
          itemMargin: {
            horizontal: 8,
            vertical: 8
          },
        },
      }
    }
  },
  setup() {
  },
  mounted() {
    this.init = true;
    this.renderChart();
  },
  watch: {
    origData: {
      handler(newVal) {
        const data = newVal.Data.top5bill

        if (data !== null && data.length > 0) {
          data.sort((a, b) => b.bill - a.bill);
          const others = data.filter(item => item.resourceNm === "others");
          const nonOthers = data.filter(item => item.resourceNm !== "others");
          const sortedData = [...nonOthers, ...others];

          let labels = [];
          let series = [];
          sortedData.forEach(item => {
            labels.push(item.resourceNm);
            series.push(item.bill);
          })

          this.chartOptions.series = series;
          this.chartOptions.labels = labels;

          this.init = false;

          this.renderChart();
        } else {
          this.chartOptions.series = [];
          this.chartOptions.labels = [];
          this.init = false;
          this.renderChart();
        }
      },
      deep: true
    }
  },
  methods: {
    renderChart() {
      if (this.init === true) {
        this.chart = new ApexCharts(document.querySelector("#chart-top5cost-pie"), this.chartOptions);
        this.chart.render();
      } else {
        this.chart.updateOptions(this.chartOptions)
      }
    },
  }
}
</script>

<style></style>
