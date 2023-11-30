export default {
  created() {
    //Set timeout to show edit form modal
    setTimeout(function() {
      this.showEditFormModal = true;
    }, 3000);
  },
};
