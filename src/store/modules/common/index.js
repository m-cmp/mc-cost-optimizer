import actions from './actions';
import mutations from './mutations';
import getters from './getters';
import {BROWSER, DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';

const state = {
  language: '',
  allVendors: [],
  availableVendor: [],
  info: {
    currencies: DEFAULT_EXCHANGE_RATE,
    selectedCurrency: DEFAULT_CURRENCY,
    defaultCurrency: DEFAULT_CURRENCY,
    selectedVendors: [],
    exchangeRateDate: null
  },
  searchFrom: '/',
  isLoading: true, //true -> show loading, false -> hide loading,
  submittedSurvey: false,
  browser: BROWSER.CHROME,
  companyMaxDatetime: ''
};

export default {
  namespaced: true,
  state,
  actions,
  getters,
  mutations,
};
