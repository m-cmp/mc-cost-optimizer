<template>
  <div class="prime-tree-table-cloud-bill-detail">
    <TreeTable
      :value="treeData"
      :class="['p-treetable-sm', ]"
      :resizable-columns="true"
      :expanded-keys="expandedKeys"
      column-resize-mode="expand"
      sort-field="single"
      scroll-height="390px"
      removable-sort
    >
      <Column
        :expander="true"
        :sortable="true"
        :header="headerDescription"
        :header-style="{
          backgroundColor: '#F6F8FA',
          fontSize: '12px',
          color: '#6C7994',
          fontWeight: 'bold',
          border: '0 solid #E8EBEF',
          width: '850px'
        }"
        field="description">
        <template #body="slotProps">
          <span
            :class="getNodeClass(slotProps.node.data.depth)"
            :title="`${slotProps.node.data.description}`"
            class="ellipsis-text"
          >
            {{ slotProps.node.data.description }}
          </span>
        </template>
      </Column>
      <Column
        :sortable="true"
        :header="column[4].headerName"
        :body-style="{
          textAlign : 'right'
        }"
        :header-style="{
          backgroundColor: '#F6F8FA',
          fontSize: '12px',
          color: '#6C7994',
          fontWeight: 'bold',
        }"
        body-class="prime-number-cell-style"
        field="totalUsage"/>
      <Column
        :sortable="true"
        :header="column[5].headerName"
        :body-style="{
          textAlign: 'right'
        }"
        :header-style="{
          backgroundColor: '#F6F8FA',
          fontSize: '12px',
          color: '#6C7994',
          fontWeight: 'bold',
        }"
        body-class="prime-number-cell-style"
        field="totalCost"/>
    </TreeTable>
  </div>
</template>

<script>
import TreeTable from "primevue/treetable/TreeTable";
import Column from "primevue/column";
import {mapGetters} from "vuex";
export default {
  name: "PrimeCloudBillDetailTreeTable",
  components: {
    TreeTable,
    Column
  },
  props: {
    rowData: {
      type: Array,
      default: null
    },
    column: {
      type: Array,
      default: null
    },
    selectedVendor: {
      type: String,
      default: ""
    },
    headerDescription: {
      type: String,
      default: ''
    },
    expandedKeys: {
      type: Object,
      default: null
    },
  },
  data() {
    return {
      layerOrder: [],
    }
  },
  computed: {
    ...mapGetters({
      gridLayerOption: 'billing/gridLayerOption'
    }),
    treeData() {
      let formatCost;
      switch (this.selectedVendor) {
        case 'AWS':
        case 'GCP':
        case 'OCI':
        case 'TENCENT':
          formatCost = function(value) {
            return Number(value).toFixed(2);
          }
          break;
        case 'AZURE':
        case 'NCP':
          formatCost = function(value) {
            return Math.round(Number(value))
          }
          break;
        default:
          formatCost = function(value) {
            return Number(value).toLocaleString();
          }
      }
      const findChildren = (layerIdx, parentKey = '', remainingData = this.rowData) => {
        if (layerIdx >= this.layerOrder.length) return [];
        const layerValue = this.layerOrder[layerIdx];
        const layerKey = Array.from(
          new Set(remainingData.map((data) => data[layerValue]))
        );

        return layerKey.map((key, idx) => {
          const currentKey = parentKey ? `${parentKey}-${idx}` : `${this.selectedVendor}${idx}`;
          const currentLayerData = remainingData.filter((data) => data[layerValue] === key);
          let totalUsage = 0;
          let totalCost = 0;

          const childrenNodes = findChildren(layerIdx + 1, currentKey, currentLayerData);
          const totalDescriptionCount = countItemDescriptions({ children: childrenNodes})

          if (layerValue === 'itemDescription') {
            totalUsage = this.convertLocaleString(this.fixedNumber(currentLayerData[0].usage));
            totalCost = this.convertLocaleString(formatCost(currentLayerData[0].cost));
            return {
              'key': currentKey,
              'data': {
                ['description']: key,
                totalUsage,
                totalCost,
                'depth': layerIdx
              }
            };
          } else {
            totalUsage = this.convertLocaleString(this.fixedNumber(childrenNodes.reduce((sum, node) => sum + this.commaStringToNum(node.data.totalUsage), 0)));
            totalCost = this.convertLocaleString(formatCost(childrenNodes.reduce((sum, node) => sum + this.commaStringToNum(node.data.totalCost), 0)));
            return {
              'key': currentKey,
              'data': {
                ['description']: `${key} (${totalDescriptionCount})`,
                totalUsage,
                totalCost,
                'depth': layerIdx
              },
              children: childrenNodes,
            };
          }
        });
      };
      let result = findChildren(0);
      return result;
    }
  },
  watch: {
    rowData: {
      handler() {
        if (Array.isArray(this.rowData) && this.rowData.length > 0 && typeof this.rowData[0] === 'object') {
          this.layerOrder = Object.keys(this.rowData[0]);
        }
      },
    },
    gridLayerOption: function () {
      if (this.gridLayerOption) {
        this.expandAll();
      }else{
        this.collapseAll();
      }
    },
  },
  methods: {
    fixedNumber(value) {
      return Number(value).toFixed(2);
    },
    commaStringToNum(value) {
      return Number(value.replace(/\,/g,''));
    },
    convertLocaleString(value) {
      return Number(value).toLocaleString();
    },
    getNodeClass(depth) {
      switch (depth) {
        case 0:
          return 'prime-node-top'
        break;
        case 4:
          return 'prime-node-bottom'
        break;
        default:
          return 'prime-node-middle'
        break;
      }
    },
    expandAll() {
      for (let node of this.treeData) {
        this.expandNode(node);
      }
      this.expandedKeys = {...this.expandedKeys};
    },
    collapseAll() {
      this.expandedKeys = {};
    },
    expandNode(node) {
      if (node.children && node.children.length) {
        this.expandedKeys[node.key] = true;

        for (let child of node.children) {
          this.expandNode(child);
        }
      }
    },
  }
}
const countItemDescriptions = (node) => {
  if (!node.children || node.children.length === 0) {
    if (node.data && node.data.description) {
      return 1;
    }
    return 0;
  }

  let count = 0;
  node.children.forEach(child => {
    count += countItemDescriptions(child);
  });

  return count;
};
</script>
<style lang="scss">
.prime-tree-table-cloud-bill-detail {
  display: flex;
  user-select: none;
  .p-treetable-thead {
    position: sticky;
    top: 0;
    z-index: 1;
    box-sizing: border-box;
    .th {
      border: 0 solid #e8ebef;
    }
  }
  .p-treetable-wrapper {
    max-height:390px;
    height: 390px;
    background-color: #f6f6f6;
  }
  .prime-number-cell-style {
    text-align: right;
    font-size: 14px;
    font-family: NotoSansCJKkr-Regular !important;
    color: #222222;
  }
  .prime-node-top {
    font-size: 14px;
    font-family: NotoSansCJKkr-Regular !important;
    color: #0672FF;
  }
  .prime-node-middle {
    font-size: 14px;
    font-family: NotoSansCJKkr-Regular !important;
    color: #222222;
  }
  .prime-node-bottom {
    font-size: 12px;
    font-family: NotoSansCJKkr-Regular !important;
    color: #7B8088;
  }
  .ellipsis-text {
    min-width: 0;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
}

</style>
