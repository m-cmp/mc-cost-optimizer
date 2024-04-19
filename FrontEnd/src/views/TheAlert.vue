<template>
  <div id="app">
    <AlertLayout @toggleSidebar="toggleSidebar"/>
    <McmpBaseFooter :is-sidebar-opened="isSidebarOpened"/>
  </div>
</template>

<script>
  import AlertLayout from '@/components/pages/alert/AlertLayout';
  import {setDocumentDescription} from '@/util/htmlDocument'
  import {ROUTE_NAME} from '@/constants/constants';
  // import BaseFooter from '@/components/common/base-footer/BaseFooter';
  import McmpBaseFooter from '@/components/common/base-footer/McmpBaseFooter';

  export default {
    name: ROUTE_NAME.BILLING,
    components: {
      AlertLayout,
      // BaseFooter,
      McmpBaseFooter,
    },
    data() {
      return {
        isSidebarOpened: null,
      }
    },
    created() {
      this.$store.dispatch('common/getInfo');
      this.$store.dispatch('common/getAllVendors');
      // document.title = this.$t('pageTitle.billing');
      setDocumentDescription('Alert page');
    },
    methods: {
      toggleSidebar(sidebarState) {
        this.isSidebarOpened = sidebarState
      }
    }
  }
</script>
