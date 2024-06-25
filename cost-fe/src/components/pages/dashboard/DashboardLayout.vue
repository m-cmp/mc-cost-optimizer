<template>
<div class="page">
    <!-- page header -->
    <DashboardHeader />
    <div class="page-wrapper">

        <!-- selectbox -->
        <DashboardSelectbox
          @selectOptions="getWidgetData"/>

        <!-- page body -->
        <div class="page-body">
            <div class="container-xl">
                <div class="row row-deck row-cards">
                    <div class="col-lg-6">
                        <DashboardBilling />
                    </div>
                    <div class="col-lg-6">
                        <DashboardTop
                        :origData="top5costData"/>
                    </div>
                    <div class="col-lg-6">
                    <DashboardAsset
                      :origData="usageAssetData"/>
                    </div>
                    <div class="col-lg-6">
                        <DashboardCommitment />
                    </div>
                </div>
            </div>
        </div>

        <!-- footer -->
        <DashboardFooter />

    </div>
</div>
</template>

<script>
import DashboardHeader from './dashboard-header/DashboardHeader.vue'
import DashboardSelectbox from './dashboard-selectbox/DashboardSelectbox.vue'
import DashboardFooter from './dashboard-footer/DashboardFooter.vue'
import DashboardBilling from './dashboard-billing-amount/DashboardBilling.vue'
import DashboardTop from './dashboard-top-resources/DashboardTop5.vue'
import DashboardAsset from './dashboard-asset/DashboardAsset.vue'
import DashboardCommitment from './dashboard-commitment/DashboardCommitment.vue'
import axios from "axios";
import {useSelectedOptionsStore} from "@/stores/selectedOptions";

export default {
    components: {
        DashboardHeader,
        DashboardSelectbox,
        DashboardFooter,
        DashboardBilling,
        DashboardTop,
        DashboardAsset,
        DashboardCommitment
    },
  data() {
    return {
      usageAssetData: null,
      top5costData: null
    }
  },
  setup() {
    const store = useSelectedOptionsStore();
    return {
      store
    };
  },
  methods: {
    getWidgetData(){
      this.getDBoardUsageAssetData();
      this.getTop5CostData();
    },
    getDBoardUsageAssetData(){
      axios.post('http://localhost:9090/api/v2/getBillAsset', {
        today: new Date().toISOString().split('T')[0].replace(/-/g, ''),
        selectedProjects: this.store.selectedOptions.project,
        selectedCsps: this.store.selectedOptions.csp,
        selectedWorkspace: this.store.selectedOptions.workspace
      })
          .then((res) => {
            if (res.data.status === "OK") {
              this.usageAssetData = res.data;
            } else {
              console.error('api 호출 실패: ', res.data);
            }
          })
          .catch(err => {
            console.log(err);
          })
    },
    getTop5CostData(){
      axios.post('http://localhost:9090/api/v2/getTop5Bill', {
        today: new Date().toISOString().split('T')[0].replace(/-/g, ''),
        selectedProjects: this.store.selectedOptions.project,
        selectedCsps: this.store.selectedOptions.csp,
        selectedWorkspace: this.store.selectedOptions.workspace
      })
          .then((res) => {
            if (res.data.status === "OK") {
              this.top5costData = res.data;
            } else {
              console.error('api 호출 실패: ', res.data);
            }
          })
          .catch(err => {
            console.log(err);
          })
    }
  }
}
</script>

<style>

</style>
