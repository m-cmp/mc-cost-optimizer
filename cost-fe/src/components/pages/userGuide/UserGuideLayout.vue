<template>
  <div class="page">
    <DashboardHeader />
    <div class="container-xl">
      <div class="page-wrapper">
        <Alarm/>
        <AlarmGuideGrid/>
      </div>
    </div>
  </div>
</template>

<script>
import DashboardHeader from "@/components/pages/dashboard/dashboard-header/DashboardHeader.vue";
import Alarm from "@/components/pages/userGuide/Alarm.vue";
import AlarmGuideGrid from "@/components/pages/userGuide/guideGrid/AlarmGuideGrid.vue";

export default {
  name: 'BillingInvoiceLayout',
  components: {
    DashboardHeader,
    Alarm,
    AlarmGuideGrid
  },
  data() {
    return {};
  },
  methods: {}
}
</script>

<style lang="scss">

</style>
