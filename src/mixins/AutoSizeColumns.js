/**This mixins to help the ag grid table auto resize columns base on column' content**/
export default {
  methods: {
    handleWindowResize() {
      this.autoSizeAll();
    },
    autoSizeAll() {
      const allColumnIds = [];
      this.gridColumnApi.getAllColumns().forEach(function(column) {
        allColumnIds.push(column.colId);
      });
      this.gridColumnApi.autoSizeColumns(allColumnIds);
    },
  },
};
