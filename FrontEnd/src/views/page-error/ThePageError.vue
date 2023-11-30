<template>
  <div id="app">
    <PageError @toggleSidebar="toggleSidebar"/>
    <McmpBaseFooter :is-sidebar-opened="isSidebarOpened"/>
  </div>
</template>

<script>
  import PageError from '@/components/pages/page-error/PageError';
  import { setDocumentDescription } from '@/util/htmlDocument';
  // import BaseFooter from '@/components/common/base-footer/BaseFooter';
  import McmpBaseFooter from '@/components/common/base-footer/McmpBaseFooter';

  export default {
    name: 'ThePageError',
    components: {
      PageError,
      // BaseFooter,
      McmpBaseFooter
    },
    data() {
      return {
        isSidebarOpened: null
      }
    },
    created() {
      this.$store.dispatch('common/getInfo');
      this.$store.dispatch('common/getAllVendors');
      document.title = 'Error Page';
    },
    methods: {
      toggleSidebar(sidebarState) {
        this.isSidebarOpened = sidebarState
      }
    }
  }
</script>
