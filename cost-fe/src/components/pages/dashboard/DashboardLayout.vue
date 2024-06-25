<template>
<div class="page">
    <!-- page header -->
    <DashboardHeader />
    <div class="page-wrapper">

        <!-- selectbox -->
        <DashboardSelectbox />

        <!-- page body -->
        <div class="page-body">
            <div class="container-xl">
                <div class="row row-deck row-cards">
                    <div class="col-lg-6">
                        <DashboardBilling />
                    </div>
                    <div class="col-lg-6">
                        <DashboardTop />
                    </div>
                    <div class="col-lg-6">
                    <DashboardAsset />
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

export default {
    components: {
        DashboardHeader,
        DashboardSelectbox,
        DashboardFooter,
        DashboardBilling,
        DashboardTop,
        DashboardAsset,
        DashboardCommitment
    }
}
</script>

<style>

</style>
