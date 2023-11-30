<template>
  <div class="prime-ml-abnormal-table">
    <DataTable
      v-if="(rowData.length > 0)"
      :value="rowData"
      :scrollable="true"
      :reorderable-columns="true"
      :class="['custom-datatable-font', 'p-datatable-sm']"
      scroll-height="800px"
    >
      <Column
        :field="filteredColumns[0].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 ${filteredColumns[0].width}px`
        }"
        :body-style="{
          flex : `0 0 ${filteredColumns[0].width}px`
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[0].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span style="margin-left: 15%">
            {{ slotProps.data[filteredColumns[0].field] }}
          </span>
        </template>
      </Column>
      <Column
        :field="filteredColumns[1].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 ${filteredColumns[1].width}px`
        }"
        :body-style="{
          flex : `0 0 ${filteredColumns[1].width}px`
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[1].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span
            :class="alarmLevelRenderClass(slotProps.data[filteredColumns[1].field])"
          >
            {{ alarmLevelRenderText(slotProps.data[filteredColumns[1].field]) }}
          </span>
        </template>
      </Column>
      <Column
        :field="filteredColumns[2].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 ${filteredColumns[2].width}px`
        }"
        :body-style="{
          flex : `0 0 ${filteredColumns[2].width}px`
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[2].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span class="-tag gray-">
            <span
              v-if="slotProps.data[filteredColumns[2].field] === 'AI'"
              :class="analysisTypeRenderClass(slotProps.data[filteredColumns[2].field])"
            />
            <span
              v-else-if="slotProps.data[filteredColumns[2].field] === 'USER'"
              :class="analysisTypeRenderClass(slotProps.data[filteredColumns[2].field])"
            >person</span>
            <p>
              {{ slotProps.data[filteredColumns[2].field] }}
            </p>
          </span>
        </template>
      </Column>
      <Column
        :field="filteredColumns[3].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 110px`
        }"
        :body-style="{
          flex : `0 0 110px`
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[3].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span
            :class="vendorRenderIcon(slotProps.data[filteredColumns[3].field])"
          />
          <span class="-font-size-12 -ml-2">
            {{ slotProps.data[filteredColumns[3].field] }}
          </span>
        </template>
      </Column>
      <Column
        :field="filteredColumns[4].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 240px`
        }"
        :body-style="{
          flex : `0 0 240px`,
          flexWrap : 'wrap',
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[4].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <p class="analDtDate">
            {{ divideAnalDtTxt(slotProps.data[filteredColumns[4].field])[0] }}
          </p>
          <p class="analDtText">
            {{ divideAnalDtTxt(slotProps.data[filteredColumns[4].field])[1] }}
          </p>
        </template>
      </Column>
      <Column
        :field="filteredColumns[5].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 570px`
        }"
        :body-style="{
          flex : `0 0 570px`
        }"
        resizable-columns
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[5].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span class="inline-block">
            <span>
              {{ messageRender(slotProps.data[filteredColumns[5].field])[0] }}
            </span>
            <strong>
              {{ messageRender(slotProps.data[filteredColumns[5].field])[1] }}
            </strong>
            <span>
              {{ messageRender(slotProps.data[filteredColumns[5].field])[2] }}
            </span>
          </span>
        </template>
      </Column>
      <Column
        :field="filteredColumns[6].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
          flex : `0 0 120px`
        }"
        :body-style="{
          flex : `0 0 120px`
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[6].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span>
            {{ slotProps.data[filteredColumns[6].field] }}
          </span>
        </template>
      </Column>
      <Column
        :field="filteredColumns[7].field"
        :header-style="{
          backgroundColor : '#F6F8FA',
        }"
        :body-style="{
        }"
      >
        <template #header="slotPros">
          <div class="header-class">
            {{ filteredColumns[7].headerName }}
          </div>
        </template>
        <template #body="slotProps">
          <span style="margin-left: 15%">
            <button
              class="material-icons -font-size-14 -ml-1 -color-gray-1"
              @click="onClickEvent(slotProps.data)"
            >
              launch
            </button>
          </span>
        </template>
      </Column>
    </DataTable>
  </div>

</template>

<script>
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import 'primevue/resources/themes/fluent-light/theme.css';
export default {
  name: "PrimeMLAbnormalTable",
  components: {
    DataTable,
    Column
  },
  props: {
    columns: {
      type: [Object, Array],
      default: null
    },
    rowData: {
      type: [Object, Array],
      default: null
    },
    valuePrefix: {
      type: String,
      default: ''
    },
  },
  data() {
    return {
      excludedFields: ["analDt", "message_dt", "detcDt"],
    }
  },
  computed: {
    filteredColumns() {
      return this.columns.filter(col => !this.excludedFields.includes(col.field));
    },
  },
  created() {
  },
  methods: {
    alarmLevelRenderClass(alarmLevel){
      switch (alarmLevel) {
        case 'minor':
          return "-tag green-";
        case 'critical':
          return "-tag red-";
        case 'major':
        default:
          return "-tag yellow-";
      }
    },
    alarmLevelRenderText(alarmLevel){
      switch (alarmLevel) {
        case 'minor':
          return this.$t('dashboard.abnormalChange.alarmLevels.minor');
        case 'critical':
          return this.$t('dashboard.abnormalChange.alarmLevels.critical');
        case 'major':
        default:
          return this.$t('dashboard.abnormalChange.alarmLevels.major');
      }
    },
    analysisTypeRenderClass(value) {
      switch (value) {
        case 'USER':
          return "material-icons";
        case 'AI':
        default:
          return "ci ci-icon-ai";
      }
    },
    vendorRenderIcon(value) {
      switch (value){
        case 'AWS':
          return "ci -wrapper-v-h-center ci-vendor-aws"
        case 'GCP':
          return "ci -wrapper-v-h-center ci-vendor-gcp"
        case 'AZURE':
          return "ci -wrapper-v-h-center ci-vendor-azure"
        case 'OCI':
          return "ci -wrapper-v-h-center ci-vendor-oci"
      }
    },
    divideAnalDtTxt(value) {
      let pattern = /^((\d{4}\/\d{2}\/\d{2})|(\d{2}\/\d{2}\/\d{4}))\(([^)]+)\)/;
      let matches = value.match(pattern)

      if (matches) {
        let date = matches[1];
        let text = matches[4];

        return [date, text]
      }
    },
    messageRender(param) {
      let currency = this.valuePrefix

      let start = param.indexOf(currency);
      let end = param.indexOf(")", start) + 1;
      let cost = param.substring(start, end);
      let front = param.substring(0, start);
      let back = param.substring(end);
      let result = [front, cost, back];

      return result;
    },
    onClickEvent(target) {
      // if(target){
      //   return this.$emit(`cellClicked`, target)
      // }
      alert("미구현 기능입니다.")
    },
  }
}
</script>

<style lang="scss" scoped>
.prime-ml-abnormal-table {
  .custom-datatable-font {
  font-size: 14px;
  font-family: NotoSansCJKkr-Regular !important;
  user-select: none;
  backgroud-color: #F6F6F6;
  }
  .header-class {
  /*background-color: #F6F8FA;*/
  font-size: 12px;
  color: #333333;
  font-weight: 700;
    white-space: nowrap;
  }
  .analDtDate {
  font-size: .875rem;
  font-weight: 400;
  flex-grow: 1;
  flex-basis: 100%
  }
  .analDtText {
  font-size: .70rem;
  font-weight: 400;
  color: #7e8793!important;
  flex-grow: 1;
  flex-basis: 100%;
  }
}

</style>
