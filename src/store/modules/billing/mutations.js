import { getFirstMonthIdx, getLastMonthIdx } from '@/util/montlyCostTrend';
import {setValueToStorageByKey, LOCAL_STORAGE_KEY} from '@/util/localStorage';
import { TAB_INDEX } from '@/constants/billingConstants';
import {addAdditionalServiceMapToEachChargeListItem, classifyBillingSummaryData} from '@/util/billingUtils';

const SET_IS_BILL_LIST_LOADING = (state, isLoading) => {
  state.isBillListLoading = isLoading;
};

const SET_IS_CHARGE_LIST_LOADING = (state, isLoading) => {
  state.isChargeListLoading = isLoading;
};

const SET_IS_INVOICE_LIST_LOADING = (state, isLoading) => {
  state.isInvoiceListLoading = isLoading;
};

const SET_IS_INVOICE_INSIGHT_LIST_LOADING = (state, isLoading) => {
  state.isInvoiceInsightListLoading = isLoading;
};

const SET_IS_INVOICE_INSIGHT_GRID_LOADING = (state, isLoading) => {
  state.isInvoiceInsightGridLoading = isLoading;
};

const SET_IS_CLOUD_BILL_DETAIL_LOADING = (state, isLoading) => {
  state.isCloudBillDetailLoading = isLoading;
};

const SET_IS_COMPUTING_RESOURCES_LOADING = (state, isLoading) => {
  state.isComputingResourcesLoading = isLoading;
}

const SET_IS_ADDITIONAL_SERVICES_LOADING = (state, isLoading) => {
  state.isAdditionalServicesLoading = isLoading;
}
const SET_IS_SERVICE_GROUPS_LOADING = (state, isLoading) => {
  state.isServiceGroupsLoading = isLoading;
}

const SET_IS_AUTOSPOT_LOADING = (state, isLoading) => {
  state.isAutoSpotLoading = isLoading;
}

const CHART_LIST_UPDATED = (state, chargeList) => {
  state.chargeList = addAdditionalServiceMapToEachChargeListItem(chargeList);
};

const BILL_LIST_UPDATED = (state, billList) => {
  state.billList = billList;
  state.activeMonthIdx = billList.length - 1;
  state.firstMonthIdx = getFirstMonthIdx(state.activeMonthIdx, billList.length);
  state.lastMonthIdx = getLastMonthIdx(state.activeMonthIdx, billList.length);
};

const INVOICE_LIST = (state, invoiceList) => {
  state.invoiceList = invoiceList;
};

const INVOICE_INSIGHT_LIST = (state, invoiceInsightList) => {
  state.invoiceInsightList = invoiceInsightList;
};

const INVOICE_INSIGHT_GRID = (state, invoiceInsightGrid) => {
  state.invoiceInsightGrid = invoiceInsightGrid;
};

const COMPUTING_RESOURCES = (state, computingResources) => {
  state.computingResources = computingResources;
}

const AUTOSPOT = (state, autoSpot) => {
  state.autoSpot = autoSpot;
}

const ADDITIONAL_SERVICES = (state, additionalServices) => {
  state.additionalServices = additionalServices;
}

const SERVICE_GROUPS = (state, serviceGroups) => {
  state.serviceGroups = serviceGroups;
}

const SET_ACTIVE_MONTH = (state, activeMonthIdx) => {
  state.activeMonthIdx = activeMonthIdx;
};

const SET_FIRST_MONTH_IDX = (state, firstMonthIdx) => {
  state.firstMonthIdx = firstMonthIdx;
};

const SET_LAST_MONTH_IDX = (state, lastMonthIdx) => {
  state.lastMonthIdx = lastMonthIdx;
};

const SET_SELECTED_TAB_INDEX = (state, selectedTabIndex) => {
  state.selectedTabIndex = selectedTabIndex;
};

const SET_SELECTED_VENDOR = (state, vendor) => {
  state.selectedVendor = vendor;
};

const SET_DISPLAYED_WARNING_BANNER = (state, pageName) => {
  if (pageName === TAB_INDEX[state.selectedVendor].CLOUD_BILL_DETAIL) {
    state.chargeTableState.cloudBillDetails.displayedWarning = false;
  } else if (pageName === TAB_INDEX[state.selectedVendor].CLOUD_INVOICE_LIST) {
    state.chargeTableState.cloudInvoiceList.displayedWarning = false;
  } else if (pageName === TAB_INDEX[state.selectedVendor].CLOUD_INVOICE_INSIGHT) {
    state.chargeTableState.cloudInvoiceInsight.displayedWarning = false;
  } else if(pageName === TAB_INDEX[state.selectedVendor].COMPUTING_RESOURCES) {
    state.chargeTableState.computingResources.displayedWarning = false;
  } else if(pageName === TAB_INDEX[state.selectedVendor].ADDITIONAL_SERVICES) {
    state.chargeTableState.additionalServices.displayedWarning = false;
  }else if(pageName === TAB_INDEX[state.selectedVendor].SERVICE_GROUPS) {
    state.chargeTableState.serviceGroups.displayedWarning = false;
  }
  setValueToStorageByKey(LOCAL_STORAGE_KEY.BILLING_TABLE_STATE, state.chargeTableState);
};

const SET_CHARGE_TABLE_STATE = (state, storageState) => {
  state.chargeTableState = storageState;
};

const SET_SELECTED_VIEW_BY_OPTION = (state, option) => {
  state.selectedViewByOption = option;
};

const SET_SELECTED_TAG = (state, tag) => {
  state.cloudBilDetailsSelectedTagKey = tag;
}

const SET_SELECTED_SERVICE_GROUP = (state, svg) => {
  state.cloudBilDetailsSelectedSvgSet = svg;
}

const SET_BILLING_DETAIL =(state, billingDetail) => {
  let results = classifyBillingSummaryData(billingDetail)
  state.billingDetail = results.billingWithNonTagKey;
  state.billingDetailWithTag = results.billingWithTagKey;
  state.billingDetailWithSvgSet = results.billingDetailWithSvgSet;
};

const SET_BILLING_DETAIL_WITH_TAG =(state, billingDetail) => {
  state.billingDetailWithTag = billingDetail;
};

const SET_BILLING_DETAIL_WITH_SERVICE_GROUP =(state, billingDetail) => {
  state.billingDetailWithSvgSet = billingDetail;
};

const SET_TAG_OPTIONS = (state, tagKeys) => {
  //let sortedTagKeys = tagKeys.sort();
  state.cloudBilDetailsTagKeys = tagKeys;
  //state.cloudBilDetailsSelectedTagKey = sortedTagKeys[0];
};

const SET_SERVICE_GROUP_OPTIONS = (state, svgSets) => {
  //let sortedSvgSets = svgSets.sort();
  state.cloudBilDetailsSvgSets = svgSets;
  //state.cloudBilDetailsSelectedSvgSet = sortedSvgSets[0];
};

const SET_GRID_LAYER_CONDITION = (state, option) => {
  state.gridLayerOption = option;
}

const SET_INVOICE_CURRENCY = (state, currency) => {
  state.invoiceCurrency = currency;
}

const SET_COMPANY_CURRENCY = (state, currency) => {
  state.companyCurrency = currency;
}

const RECIPIENT_EMAIL_LIST = (state, recipientList) => {
  state.recipientList = recipientList;
}

export default {
  CHART_LIST_UPDATED,
  BILL_LIST_UPDATED,
  INVOICE_LIST,
  INVOICE_INSIGHT_LIST,
  INVOICE_INSIGHT_GRID,
  COMPUTING_RESOURCES,
  ADDITIONAL_SERVICES,
  SERVICE_GROUPS,
  AUTOSPOT,
  SET_ACTIVE_MONTH,
  SET_FIRST_MONTH_IDX,
  SET_LAST_MONTH_IDX,
  SET_SELECTED_TAB_INDEX,
  SET_SELECTED_VENDOR,
  SET_DISPLAYED_WARNING_BANNER,
  SET_SELECTED_VIEW_BY_OPTION,
  SET_BILLING_DETAIL,
  SET_SELECTED_TAG,
  SET_BILLING_DETAIL_WITH_TAG,
  SET_CHARGE_TABLE_STATE,
  SET_TAG_OPTIONS,
  SET_IS_BILL_LIST_LOADING,
  SET_IS_CHARGE_LIST_LOADING,
  SET_IS_INVOICE_LIST_LOADING,
  SET_IS_INVOICE_INSIGHT_LIST_LOADING,
  SET_IS_INVOICE_INSIGHT_GRID_LOADING,
  SET_IS_CLOUD_BILL_DETAIL_LOADING,
  SET_IS_COMPUTING_RESOURCES_LOADING,
  SET_IS_ADDITIONAL_SERVICES_LOADING,
  SET_IS_SERVICE_GROUPS_LOADING,
  SET_IS_AUTOSPOT_LOADING,
  SET_BILLING_DETAIL_WITH_SERVICE_GROUP,
  SET_SELECTED_SERVICE_GROUP,
  SET_SERVICE_GROUP_OPTIONS,
  SET_GRID_LAYER_CONDITION,
  SET_INVOICE_CURRENCY,
  SET_COMPANY_CURRENCY,
  RECIPIENT_EMAIL_LIST
};
