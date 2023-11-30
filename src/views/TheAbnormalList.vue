<template>
  <div
    id="app"
    class="dashboard-page"
  >
    <m-l-abnormal-list-layout
      @toggleSidebar="toggleSidebar"
    />
    <McmpBaseFooter :is-sidebar-opened="isSidebarOpened"/>
  </div>
</template>

<script>
  import MLAbnormalListLayout from "../components/pages/dashboard/ml-abnormal-detection-list/MLAbnormalListLayout";
  import { setDocumentDescription } from './../util/htmlDocument'
  import {ROUTE_NAME} from '@/constants/constants';
  // import BaseFooter from '@/components/common/base-footer/BaseFooter';
  import McmpBaseFooter from '@/components/common/base-footer/McmpBaseFooter';

  export default {
    name: ROUTE_NAME.ANOMALY_LIST,
    components: {
      MLAbnormalListLayout,
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
      // document.title = this.$t('pageTitle.dashboard')
      setDocumentDescription(this.$t('pageDescription.abnormal'));
    },
    methods: {
      toggleSidebar(sidebarState) {
        this.isSidebarOpened = sidebarState
      },
    }
  }
</script>

