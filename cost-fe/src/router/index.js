import {
    createRouter,
    createWebHistory
} from 'vue-router'
import DashboardLayout from '@/components/pages/dashboard/DashboardLayout.vue'
import BillingInvoiceLayout from '@/components/pages/billinginvoice/BillingInvoiceLayout.vue'
import UserGuideLayout from '@/components/pages/userGuide/UserGuideLayout.vue'

const routes = [{
        path: '/',
        name: 'DashboardLayout',
        component: DashboardLayout
    },
    {
        path: '/billing-invoice',
        name: 'BillingInvoiceLayout',
        component: BillingInvoiceLayout
    },
    {
        path: '/guide',
        name: 'UserGuideLayout',
        component: UserGuideLayout
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router
