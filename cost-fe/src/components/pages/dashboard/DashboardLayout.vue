<template>
<div class="page">
    <!-- page header -->
    <DashboardHeader />
    <div class="page-wrapper">

        <!-- selectbox -->
<!--        <DashboardSelectbox-->
<!--          @selectOptions="getWidgetData"/>-->

        <!-- page body -->
        <div class="page-body">
            <div class="container-xl">
                <div class="row row-deck row-cards">
                    <div class="col-lg-6">
                        <DashboardBilling
                        ref="dashboardBilling"/>
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
                        <!-- <DashboardCommitment /> -->
                    </div>
                </div>
            </div>
        </div>

        <!-- footer -->
        <!-- <DashboardFooter /> -->

    </div>
</div>
</template>

<script>
import DashboardHeader from './dashboard-header/DashboardHeader.vue'
// import DashboardSelectbox from './dashboard-selectbox/DashboardSelectbox.vue'
// import DashboardFooter from './dashboard-footer/DashboardFooter.vue'
import DashboardBilling from './dashboard-billing-amount/DashboardBilling.vue'
import DashboardTop from './dashboard-top-resources/DashboardTop5.vue'
import DashboardAsset from './dashboard-asset/DashboardAsset.vue'
// import DashboardCommitment from './dashboard-commitment/DashboardCommitment.vue'
import axios from "axios";
import ENDPOINT from '@/api/Endpoints'
import {useSelectedOptionsStore} from "@/stores/selectedOptions";

export default {
    components: {
        DashboardHeader,
        // DashboardSelectbox,
        //DashboardFooter,
        DashboardBilling,
        DashboardTop,
        DashboardAsset,
        // DashboardCommitment
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
  mounted() {
    if (this.store.selectedOptions.selectedProjects.length > 0) {
      this.getWidgetData();
    }
  },
  watch:{
    'store.selectedOptions.selectedProjects': {
      handler() {
        this.getWidgetData()
      },
      deep: true
    },
  },
  methods: {
    getWidgetData(){
      this.getDBoardUsageAssetData();
      this.getTop5CostData();
      this.$refs.dashboardBilling.fetchBillingData();
    },
    getDBoardUsageAssetData(){
      axios.post(ENDPOINT.be + '/api/costopti/be/getBillAsset', {
        today: new Date().toISOString().split('T')[0].replace(/-/g, ''),
        selectedProjects: this.store.selectedOptions.selectedProjects,
        selectedCsps: this.store.selectedOptions.selectedCsps,
        selectedWorkspace: this.store.selectedOptions.selectedWorkspace
      })
          .then((res) => {
            if (res.data.status === "OK") {
              this.usageAssetData = res.data;
            } else {
              console.error('api 호출 실패: ', res.data);
              if(this.store.selectedOptions.selectedProjects.length < 1){
                alert('[ERROR] Project 코드에 맞는 값을 불러올 수 없습니다.')
              }
            }
          })
          .catch(err => {
            console.log(err);
          })
    },
    getTop5CostData(){
      axios.post(ENDPOINT.be + '/api/costopti/be/getTop5Bill', {
        today: new Date().toISOString().split('T')[0].replace(/-/g, ''),
        selectedProjects: this.store.selectedOptions.selectedProjects,
        selectedCsps: this.store.selectedOptions.selectedCsps,
        selectedWorkspace: this.store.selectedOptions.selectedWorkspace
      })
          .then((res) => {
            if (res.data.status === "OK") {
              this.top5costData = res.data;
            } else {
              console.error('api 호출 실패: ', res.data);
            }
          })
          .catch(err => {
            if (err.code === 'ECONNREFUSED' || err.message.includes('Network Error') || !err.response) {
              alert('서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.');
            } else if (err.response) {
              // 서버에서 에러 상태 코드를 반환한 경우 (4xx, 5xx)
              const status = err.response.status;
              if (status >= 500) {
                alert('서버에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.');
              } else if (status >= 400) {
                alert('요청에 문제가 있습니다. 잠시 후 다시 시도해 주세요.');
              }
            } else {
              alert('알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.');
            }
          })
    }
  }
}
</script>

<style>

</style>
