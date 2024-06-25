<template>
<div class="card">
    <div class="card-body">
        <h3 class="card-title">사용 자산 현황</h3>
        <div class="row">
            <div class="card card-sm">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-auto">
                            <span class="bg-green text-white avatar">
                                <!-- Download SVG icon from http://tabler-icons.io/i/shopping-cart -->
                                <img :src="virtualMachineIcon" alt="Virtual Machine Icon" height="32" width="32">
                            </span>
                        </div>
                        <div class="col">
                            <div class="font-weight-medium"> Virtual Machine </div>
                            <div class="text-muted"> 100 Unit </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card card-sm">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-auto">
                            <span class="bg-green text-white avatar">
                                <!-- Download SVG icon from http://tabler-icons.io/i/shopping-cart -->
                                <img :src="lbIcon" alt="Load Balancer Icon" height="32" width="32">
                            </span>
                        </div>
                        <div class="col">
                            <div class="font-weight-medium"> LB </div>
                            <div class="text-muted"> 100 Unit </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card card-sm">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-auto">
                            <span class="bg-green text-white avatar">
                                <!-- Download SVG icon from http://tabler-icons.io/i/shopping-cart -->
                                <img :src="storageIcon" alt="Storage Icon" height="32" width="32">
                            </span>
                        </div>
                        <div class="col">
                            <div class="font-weight-medium"> Storage </div>
                            <div class="text-muted"> 1.0 Tb </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card card-sm">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-auto">
                            <span class="bg-green text-white avatar">
                                <!-- Download SVG icon from http://tabler-icons.io/i/shopping-cart -->
                                <img :src="databaseIcon" alt="Database Icon" height="32" width="32">
                            </span>
                        </div>
                        <div class="col">
                            <div class="font-weight-medium"> DataBase </div>
                            <div class="text-muted"> 10 Unit </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</template>

<script>
import virtualMachineIcon from '@/assets/images/icon/server.svg';
import lbIcon from '@/assets/images/icon/load-balancer.svg';
import storageIcon from '@/assets/images/icon/bucket.svg';
import databaseIcon from '@/assets/images/icon/database.svg';

export default {
    name: 'DashboardAsset',
    data() {
        return {
            virtualMachineIcon,
            lbIcon,
            storageIcon,
            databaseIcon
        };
    }

}
</script>

<style scoped>

</style>
