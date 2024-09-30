<template>
    <div class="card">
        <div class="card-body">
            <h3 class="card-title">서비스별 누적 비용 현황</h3>
            <div class="row">
                <div class="card card-sm">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-auto">
                                <span class="bg-green text-white avatar">
                                    <img :src="virtualMachineIcon" alt="Virtual Machine Icon" height="32" width="32">
                                </span>
                            </div>
                            <div class="col">
                                <div class="font-weight-medium"> Virtual Machine </div>
                                <div class="text-muted"> {{ assets.vmCost.toFixed(3) }} USD </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card card-sm">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-auto">
                                <span class="bg-green text-white avatar">
                                    <img :src="lbIcon" alt="Load Balancer Icon" height="32" width="32">
                                </span>
                            </div>
                            <div class="col">
                                <div class="font-weight-medium"> LB </div>
                                <div class="text-muted"> {{ assets.lbCost.toFixed(3) }} USD </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card card-sm">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-auto">
                                <span class="bg-green text-white avatar">
                                    <img :src="storageIcon" alt="Storage Icon" height="32" width="32">
                                </span>
                            </div>
                            <div class="col">
                                <div class="font-weight-medium"> Storage </div>
                                <div class="text-muted"> {{ assets.storageCost.toFixed(3) }} USD </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card card-sm">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-auto">
                                <span class="bg-green text-white avatar">
                                    <img :src="databaseIcon" alt="Database Icon" height="32" width="32">
                                </span>
                            </div>
                            <div class="col">
                                <div class="font-weight-medium"> DataBase </div>
                                <div class="text-muted"> {{ assets.dbCost.toFixed(3) }} USD </div>
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
            databaseIcon,
            assets: {
                vmCost: 0.0,
                vmUnit: 0,
                storageCost: 0.0,
                storageUnit: 0,
                lbCost: 0.0,
                lbUnit: 0,
                dbCost: 0.0,
                dbUnit: 0
            }
        };
    },
    props: {
        origData: {
            type: Object,
            required: true,
        }
    },
    watch: {
        origData: {
            handler(newVal) {
                newVal.Data.billingAsset.forEach(item => {
                    switch (item.familyProductCode) {
                        case "Virtual Machine":
                            this.assets.vmCost = item.totalCost;
                            this.assets.vmUnit = item.totalUnit;
                            break;
                        case "Storage":
                            this.assets.storageCost = item.totalCost;
                            this.assets.storageUnit = item.totalUnit;
                            break;
                        case "Database":
                            this.assets.dbCost = item.totalCost;
                            this.assets.dbUnit = item.totalUnit;
                            break;
                        case "LB":
                            this.assets.lbCost = item.totalCost;
                            this.assets.lbUnit = item.totalUnit;
                            break;
                    }
                })
            },
            deep: true
        }
    }

}
</script>

<style></style>
