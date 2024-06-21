<template>
<div class="page">
    <DashboardHeader />
    <div class="page-wrapper">
        <!-- selectbox -->
        <DashboardSelectbox />
        <!-- body -->
        
        <!-- footer -->
        <DashboardFooter />

    </div>
</div>
</template>

<script>
import DashboardHeader from './dashboard-header/DashboardHeader.vue'
import DashboardSelectbox from './dashboard-header/DashboardSelectbox.vue'
import DashboardFooter from './dashboard-header/DashboardFooter.vue'

export default {
    components: {
        DashboardHeader,
        DashboardSelectbox,
        DashboardFooter
    }
}
</script>

<style>
</style>
