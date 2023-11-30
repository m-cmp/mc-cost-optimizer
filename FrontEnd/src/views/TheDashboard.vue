<template>
  <div
    id="app"
    :class="{'base-welcome-popup' : welcomePopup.canShow}"
    class="dashboard-page"
  >
    <DashboardLayout
      @toggleSidebar="toggleSidebar"
    />
    <McmpBaseFooter :is-sidebar-opened="isSidebarOpened"/>
  </div>
</template>

<script>


  import DashboardLayout from '@/components/pages/dashboard/DashboardLayout';
  import { setDocumentDescription } from './../util/htmlDocument'
  import {ROUTE_NAME} from '@/constants/constants';
  import {DASHBOARD_NEW_UPDATE_OPTIONS} from '@/constants/dashboardConstants';
  import McmpBaseFooter from '@/components/common/base-footer/McmpBaseFooter';

  export default {
    name: ROUTE_NAME.DASHBOARD,
    components: {
      DashboardLayout,
      // BaseFooter,
      McmpBaseFooter
    },
    data() {
      return {
        welcomePopup: {
          canShow: false,
          pageName: this.$t('onboarding.welcome.pageName.dashboard'),
          newUpdateOptions: DASHBOARD_NEW_UPDATE_OPTIONS
        },
        isSidebarOpened: null,
      }
    },
    created() {
      this.$store.dispatch('common/getInfo');
      this.$store.dispatch('common/getAllVendors');
      // document.title = this.$t('pageTitle.dashboard')
      setDocumentDescription(this.$t('pageDescription.dashboard'));
    },
    methods: {
      toggleSidebar(sidebarState) {
        this.isSidebarOpened = sidebarState
      },
    }
  }
</script>

