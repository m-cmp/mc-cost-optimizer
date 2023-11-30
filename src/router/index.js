import Vue from 'vue';
import Router from 'vue-router';
import {ROUTE_NAME} from '@/constants/constants';


const TheDashboard = resolve => {
  // require.ensure is Webpack's special syntax for a code-split point.
  require.ensure(['@/views/TheDashboard'], () => {
    resolve(require('@/views/TheDashboard'))
  })
};

const TheDashboardAbnormalList = resolve => {
  // require.ensure is Webpack's special syntax for a code-split point.
  require.ensure(['@/views/TheDashboard'], () => {
    resolve(require('@/views/TheAbnormalList'))
  })
};



const TheBilling = resolve => {
  // require.ensure is Webpack's special syntax for a code-split point.
  require.ensure(['@/views/TheBilling'], () => {
    resolve(require('@/views/TheBilling'))
  })
};


const ThePageError = resolve => {
  // require.ensure is Webpack's special syntax for a code-split point.
  require.ensure(['@/views/page-error/ThePageError'], () => {
    resolve(require('@/views/page-error/ThePageError'))
  })
};

Vue.use(Router);

export default new Router({
  mode: 'history',
  linkActiveClass: 'is-selected',
  routes: [
    {
      path: '/',
      redirect: {
        name: ROUTE_NAME.DASHBOARD,
      }
    },
    {
      path: '/dashboard',
      name: ROUTE_NAME.DASHBOARD,
      component: TheDashboard
    },
    {
      path: '/anomaly-detection',
      name: ROUTE_NAME.ANOMALY_LIST,
      component: TheDashboardAbnormalList
    },
    {
      path: '/billing',
      name: ROUTE_NAME.BILLING,
      component: TheBilling,
    },
    {
      path: '*',
      name: 'ThePageError',
      component: ThePageError
    }
  ],
});
