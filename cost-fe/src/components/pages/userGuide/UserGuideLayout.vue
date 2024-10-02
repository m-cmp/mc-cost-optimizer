<template>
  <div class="page">
    <DashboardHeader />
    <div class="container-xl">
      <div class="page-wrapper">
<!--        <DashboardSelectbox-->
<!--            @selectOptions="getWidgetData"/>-->
        <Alarm/>
        <AlarmGuideGrid
            ref="alarmGrid"/>
      </div>
    </div>
  </div>
</template>

<script>
import DashboardHeader from "@/components/pages/dashboard/dashboard-header/DashboardHeader.vue";
import Alarm from "@/components/pages/userGuide/Alarm.vue";
import AlarmGuideGrid from "@/components/pages/userGuide/guideGrid/AlarmGuideGrid.vue";
// import DashboardSelectbox from "@/components/pages/dashboard/dashboard-selectbox/DashboardSelectbox.vue";

export default {
  name: 'BillingInvoiceLayout',
  components: {
    DashboardHeader,
    Alarm,
    AlarmGuideGrid,
    // DashboardSelectbox
  },
  data() {
    return {};
  },
  methods: {
    getWidgetData(){
      this.$refs.alarmGrid.fetchAlarmHistory();
    }
  }
}
</script>

<style lang="scss">

</style>
