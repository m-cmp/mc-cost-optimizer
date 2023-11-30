<template>
  <div class="prime-abnormal-change-table">
    <DataTable
      v-if="rowData.length > 0 & showGrid"
      :value="rowData"
      :resizable-columns="true"
      :row-hover="true"
      :class="['custom-datatable-font', 'p-datatable-sm']"
      :reorderable-columns="true"
      :scrollable="true"
      selection-mode="single"
      data-key="id"
      removable-sort
      scroll-height="304.2px"
      column-resize-mode="expand"
      @row-click="onClickEvent"
    >
      <Column
        v-for="col of columns"
        :field="col.field"
        :key="col.field"
        :body-style="{
          color : '#222222',
        }"
        :header-style="{
          backgroundColor: '#F6F8FA',
          fontSize: '12px',
          color: '#6C7994',
          fontWeight: 'normal',
        }"
        sortable
      >
        <template #header="slotPros">
          <div
            :title="col.headerName"
            class="p-header-class"
          >
            {{ col.headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span
            v-if="col.field === 'itemAlias'"
            class="ellipsis-text"
          >
            <span
              v-if="internalWidgetConfig.viewBy === 'account'"
              :title="getTooltipText(slotProps.data, col.field)"
              class="ellipsis-text"
            >
              {{ selectedVendor }} {{ slotProps.data[col.field] }} ({{ findItem(slotProps.data[col.field]) }})
            </span>
            <span
              v-else
              :title="getTooltipText(slotProps.data, col.field)"
              class="ellipsis-text"
            >
              {{ selectedVendor }} {{ slotProps.data[col.field] }}
            </span>
          </span>
          <span
            v-else-if="selectedCurrency != 'KRW' & (col.field === 'currentCost' || col.field === 'lastCost')"
            :title="getTooltipText(slotProps.data, col.field, selectedCurrency)"
            class="ellipsis-text align-right"
          >
            {{ valuePrefix }}{{ formatValue(slotProps.data[col.field]) }}
          </span>
          <span
            v-else-if="selectedCurrency === 'KRW' & (col.field === 'currentCost' || col.field === 'lastCost')"
            :title="getTooltipText(slotProps.data, col.field, selectedCurrency)"
            class="ellipsis-text align-right"
          >
            {{ valuePrefix }}{{ Math.round(slotProps.data[col.field]).toLocaleString() }}
          </span>
          <span
            v-else-if="col.field === 'increaseDecreaseRate'"
            :title="getTooltipText(slotProps.data, col.field)"
            class="ellipsis-text align-right"
          >
            <span
              v-if="slotProps.data[col.field] < 0"
              :class="increaseDecreaseRateRender(slotProps.data[col.field])">arrow_downward
            </span>
            <span
              v-else
              :class="increaseDecreaseRateRender(slotProps.data[col.field])">arrow_upward
            </span>
            {{ formatValue(Math.abs(slotProps.data[col.field])) }}{{ percentSymbol }}
          </span>
          <span
            v-else
            :class="alarmLevelRenderClass(slotProps.data[col.field])"
            :title="getTooltipText(slotProps.data, col.field)"
            class="center"
          >
            {{ alarmLevelRenderText(slotProps.data[col.field]) }}
          </span>
        </template>
      </Column>
    </DataTable>
    <div
      v-if="rowData.length === 0"
      class="custom-no-anomaly-cost-data-overlay"
    >
      <p class="no-rows-overlay-content">
        <span class="material-icons">done</span>
        {{ content }}
      </p>
    </div>
  </div>
</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import _isNil from "lodash/isNil";

export default {
  name: "PrimeTable",
  components: {
    DataTable,
    Column,
  },
  props: {
    columns: {
      type: [Object, Array],
      default: null,
      require: true
    },
    rowData: {
      type: [Object, Array],
      default: null,
      require: true
    },
    selectedVendor: {
      type: String,
      default: ''
    },
    selectedCurrency: {
      type: String,
      default: ''
    },
    valuePrefix: {
      type: String,
      default: ''
    },
    internalWidgetConfig: {
      type: Object,
      default: null
    },
    widgetSizeW: {
      type: Number,
      default: 0
    },
    widgetSizeH: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      percentSymbol: '%',
      content: this.$t('dashboard.abnormalChange.noAnomalyData'),
      showGrid: Boolean
    };
  },
  computed: {
  },
  watch: {
    widgetSizeW() {
      this.resetGrid();
    },
    widgetSizeH() {
      this.resetGrid();
    },
  },
  created() {
    this.showGrid = true
  },
  // mounted() {
    // this.$nextTick(() => {
    //   const wrapperElement = this.$el.querySelector('.p-datatable-wrapper');
    //   if (wrapperElement) {
    //     wrapperElement.style.height = '304.2px';
    //   }
    // });

    // this.observerParentSize(this.$el.parentNode);
  // },
  methods: {
    formatValue(value) {
      return value.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    },
    alarmLevelRenderClass(alarmLevel){
      switch (alarmLevel) {
        case 'Minor':
          return "-tag green-";
        case 'Critical':
          return "-tag red-";
        case 'Major':
        default:
          return "-tag yellow-";
      }
    },
    alarmLevelRenderText(alarmLevel){
      switch (alarmLevel) {
        case 'Minor':
          return this.$t('dashboard.abnormalChange.alarmLevels.minor');
        case 'Critical':
          return this.$t('dashboard.abnormalChange.alarmLevels.critical');
        case 'Major':
        default:
          return this.$t('dashboard.abnormalChange.alarmLevels.major');
      }
    },
    increaseDecreaseRateRender(params) {
      if (!_isNil(params)) {
        if (params < 0) {

          return "material-icons -color-green-1 -font-size-16 arrow-align";
        } else {
          return "material-icons -color-red-1 -font-size-16 arrow-align";
        }
      }
    },
    onClickEvent(target) {
      return this.$emit(`cellClicked`, target)
    },
    getTooltipText(data, field, currency) {
      if (this.internalWidgetConfig.viewBy != 'account' & field === 'itemAlias') {
        return `${this.selectedVendor} ${data[field]}`;
      }
      else if (this.internalWidgetConfig.viewBy === 'account' & field === 'itemAlias') {
        return `${this.selectedVendor} ${data[field]}(${this.findItem(data[field])})`;
      }
      else if (currency != 'KRW' & (field === 'currentCost' || field === 'lastCost')) {
        return `${this.valuePrefix}${this.formatValue(data[field])}`;
      }
      else if (currency === 'KRW' & (field === 'currentCost' || field === 'lastCost')) {
        return `${this.valuePrefix}${Math.round(data[field]).toLocaleString()}`;
      }
      else if (field === 'increaseDecreaseRate') {
        return `${this.formatValue(data[field])}${this.percentSymbol}`;
      }
      return `${this.alarmLevelRenderText(data[field])}`;
    },
    findItem(value) {
      let findObject = this.rowData.find(element => element.itemAlias === value)
      let result = findObject.item
      return result
    },
    // observerParentSize(element) {
    //   if (!element) {
    //     return;
    //   }
    //   const parentElement = document.querySelector('.card-body')
    //   const observer = new ResizeObserver(entries => {
    //     for (const entry of entries) {
    //       this.resetGrid();
    //     }
    //   });
    //   observer.observe(element);
    //
    //   // this.observerParentSize(element.parentElement);
    // },
    resetGrid() {
      if (this.rowData.length > 0 && this.showGrid) {
        this.showGrid = false;
        this.$nextTick(() => {
          this.showGrid = true;
        });
      }
    },
  }
}
</script>



<style lang="scss">
.prime-abnormal-change-table {
  .custom-datatable-font {
    font-size: 14px;
    font-family: NotoSansCJKkr-Regular !important;
    //font-weight: normal;
    user-select: none;
  }
  .ellipsis-text {
    min-width: 0;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
  .align-right {
    margin-left: auto;
  }
  .center {
    margin-left: 15%;
  }
  .custom-no-anomaly-cost-data-overlay {
    font-size: 14px;
    position: absolute;
    top: 170px;
    left: 50%;
    width: 100%;
    text-align: center;
    -ms-transform: translate(-50%, -50%);
    transform: translate(-50%, -50%);
    .no-rows-overlay-content {
      color: #7b8088;
      margin-top: 8px;
      font-size: 12px;
      .material-icons {
        font-size: 12px !important;
        color: #02975b !important;
        font-weight: bold;
        vertical-align: middle;
      }
    }
  }
  .p-datatable-wrapper {
    max-height:304.2px;
    height: 304.2px;
  }
  .p-header-class {
    margin-left: 7px;
  }
}

</style>
