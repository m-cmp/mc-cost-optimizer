// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import VueCookies from 'vue-cookies'
import SvgIcon from 'vue-svgicon';
import BootstrapVue, {BVConfig} from 'bootstrap-vue';
import Fragment from 'vue-fragment';
import dayjs from 'dayjs';
import en from 'dayjs/locale/en';
import weekOfYear from 'dayjs/plugin/weekOfYear';
import weekday from 'dayjs/plugin/weekday';
import isBetween from 'dayjs/plugin/isBetween'
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'
import utc from'dayjs/plugin/utc';
import App from './App';
import router from './router';
import store from './store';
import BaseMaterial from './components/common/base-material/index';
import BaseTitle from './components/common/base-title/index';
import BaseIcon from './components/common/base-icon/index';
import { i18n } from './components/common/base-i18n/i18n';
import { LICENSE_KEY_AG_GRID } from './constants/constants';
import BasePopoverDropdown from './components/common/base-popover-dropdown/index';
import VueScrollTo from 'vue-scrollto'
import Toasted from 'vue-toasted'
import VueTour from 'vue-tour'
import VueLodash from 'vue-lodash'
import 'vue-tour/dist/vue-tour.css';
import Common from './util/Common';
import axios from 'axios'
import Multiselect from 'vue-multiselect';

import GlobalConstants from './constants/globalConstants'
import Profile from './components/common/profile';
import UrlsConfig from "../config/urls.conf"

import * as excel from "exceljs-lightweight";
import Tooltip from 'primevue/tooltip';
import DataTable from 'primevue/datatable';
import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';

Vue.prototype.Common = Common;
Vue.directive('tooltip', Tooltip)

Vue.component('multi-select', Multiselect);
Vue.component('DataTable', DataTable);

axios.defaults.headers.post['Content-Type'] = 'application/json';

axios.interceptors.request.use(function(conf) {

  var config = conf;
  let tokenKey = Profile.env==='DEV'? VueCookies.get(GlobalConstants.ACCESS_TOKEN_DEV) : VueCookies.get(GlobalConstants.ACCESS_TOKEN) ;

  const { excludeToken = false } = config;

  if(tokenKey && !excludeToken && tokenKey !== 'null') {
    config.headers.Authorization = `Bearer ${tokenKey}`;
  }
  else{
    // console.log("no TWoken!~!");
  }
  return config;
}, function(err) {
  return Promise.reject(err);
});

axios.interceptors.response.use((response) => {
  return response
}, function (error) {

  console.log(error);
  if (error.response.status === 401) {
    console.log('unauthorized, logging out ...')
  }

  return Promise.reject(error.response)
});

Vue.config.productionTip = false;
Vue.prototype.urls = UrlsConfig;
Vue.prototype.constants = GlobalConstants;
Vue.prototype.profile = Profile;

Vue.prototype.$excel = excel;

Vue.use(VueScrollTo)
Vue.use(VueTour)
Vue.use(VueLodash)
Vue.use(require('vue-moment'))

Vue.use(BVConfig, {
  BTooltip: {
    delay: {
      show: 50,
      hide: 50,
    },
  },
});

dayjs.extend(isSameOrBefore);
dayjs.extend(isBetween);
dayjs.extend(weekOfYear);
dayjs.extend(weekday);
dayjs.extend(utc);
dayjs.locale({
  ...en,
  weekStart: 0
});

Vue.prototype.$dayjs = dayjs;
Vue.prototype.Base64 = require('js-base64').Base64; // Sample in VueFile : this.Base64.encode({XXXXXXX}) / this.Base64.decode({XXXXXXX})

Vue.use(BaseMaterial);
Vue.use(BaseIcon);
Vue.use(BaseTitle);
Vue.use(BootstrapVue);
Vue.use(Fragment.Plugin);
Vue.use(BasePopoverDropdown);
Vue.use(Toasted)

Vue.use(VueCookies)
Vue.use(SvgIcon, {
  tagName: 'svgicon',
});

let options = {
  containerClass: 'mcmp-toasted-container',
  position: 'top-center',
  mode: 'override',
  duration: 3000
}

Vue.toasted.register('showMessage',
  (payload) => {
    if (!payload.message) {
      return 'Oops.. Something Went Wrong..'
    }
    return payload.message
  },
  options
)

Vue.config.productionTip = false;



/* eslint-disable no-new */
new Vue({
  el: '#app',
  components: { App },
  render: h => h(App),
  router,
  i18n,
  store,
  template: '<App/>',
});
