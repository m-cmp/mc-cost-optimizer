import actions from './actions';
import getters from './getters';
import mutations from './mutations';
import {VENDOR_NAMES} from '@/constants/billingConstants';

const state = {
  chargeList: [],
  billList: [],
  invoiceList: [],
  invoiceInsightList: [],
  invoiceInsightGrid: [],
  computingResources: [],
  additionalServices: [],
  serviceGroups: [],
  autoSpot: [],
  activeMonthIdx: 0,
  firstMonthIdx : 0,
  lastMonthIdx : 0,
  selectedTabIndex : 0,
  selectedVendor: VENDOR_NAMES.AWS,
  chargeTableState: {
    chargeList: {
      displayedWarning: false
    },
    cloudInvoiceList: {
      displayedWarning: true
    },
    cloudBillDetails: {
      displayedWarning: true
    },
    cloudInvoiceInsight: {
      displayedWarning: true
    },
    computingResources: {
      displayedWarning: true
    },
    additionalServices: {
      displayedWarning: false
    },
    autoSpot: {
      displayedWarning: true
    }
  },
  selectedViewByOption: '',
  billingDetail: [],
  billingDetailWithTag: [],
  billingDetailWithSvgSet: [],
  cloudBilDetailsSelectedTagKey: '',
  cloudBilDetailsSelectedSvgSet: '',
  cloudBilDetailsTagKeys: [],
  cloudBilDetailsSvgSets: [],
  isBillListLoading: true,
  isChargeListLoading: true,
  isInvoiceListLoading: true,
  isInvoiceInsightListLoading: true,
  isInvoiceInsightGridLoading: true,
  isCloudBillDetailLoading: true,
  isComputingResourcesLoading: true,
  isAdditionalServicesLoading: true,
  isServiceGroupsLoading: true,
  isAutoSpotLoading: true,
  gridLayerOption: false,
  invoiceCurrency: '',
  companyCurrency: '',
  recipientList: []
};

export default {
  namespaced: true,
  state,
  actions,
  getters,
  mutations,
};
