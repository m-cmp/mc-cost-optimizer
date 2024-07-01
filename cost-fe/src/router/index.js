import {
    createRouter,
    createWebHistory
} from 'vue-router'
import DashboardLayout from '@/components/pages/dashboard/DashboardLayout.vue'
import BillingInvoiceLayout from '@/components/pages/billinginvoice/BillingInvoiceLayout.vue'

const routes = [{
        path: '/',
        name: 'DashboardLayout',
        component: DashboardLayout
    },
    {
        path: '/billing-invoice',
        name: 'BillingInvoiceLayout',
        component: BillingInvoiceLayout
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router