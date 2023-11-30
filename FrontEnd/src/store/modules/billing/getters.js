import BillingDetailTableViewBy
  from "../../../components/pages/billing/billing-detail-table/table-view-by/BillingDetailTableViewBy";

const chargeList = state => state.chargeList;
const invoiceList = state => state.invoiceList;
const invoiceInsightList = state => state.invoiceInsightList;
const invoiceInsightGrid = state => state.invoiceInsightGrid;
const computingResources = state => state.computingResources;
const additionalServices = state => state.additionalServices;
const serviceGroups = state => state.serviceGroups;
const autoSpot = state => state.autoSpot;
const selectedMonthToDate = state => {
  return state.selectedMonthToDate
};
const billList = state => state.billList;
const activeMonthIdx = state => state.activeMonthIdx;
const firstMonthIdx = state => state.firstMonthIdx;
const lastMonthIdx = state => state.lastMonthIdx;
const selectedTabIndex = state => state.selectedTabIndex;
const selectedVendor = state => state.selectedVendor;
const chargeTableState = state => state.chargeTableState;
const selectedViewByOption = state => {
  return state.selectedViewByOption
};
const billingDetail = state => {
  return state.billingDetail;
};
const cloudBilDetailsSelectedTagKey = state => state.cloudBilDetailsSelectedTagKey;
const billingDetailWithTag = state => state.billingDetailWithTag;
const cloudBilDetailsSelectedSvgSet = state => state.cloudBilDetailsSelectedSvgSet;
const billingDetailWithSvgSet = state => state.billingDetailWithSvgSet;
const cloudBilDetailsTagKeys = state => state.cloudBilDetailsTagKeys;
const cloudBilDetailsSvgSets = state => state.cloudBilDetailsSvgSets;
const isBillListLoading = state => state.isBillListLoading;
const isChargeListLoading = state => state.isChargeListLoading;
const isInvoiceListLoading = state => state.isInvoiceListLoading;
const isInvoiceInsightListLoading = state => state.isInvoiceInsightListLoading;
const isInvoiceInsightGridLoading = state => state.isInvoiceInsightGridLoading;
const isCloudBillDetailLoading = state => state.isCloudBillDetailLoading;
const isComputingResourcesLoading = state => state.isComputingResourcesLoading;
const isAdditionalServicesLoading = state => state.isAdditionalServicesLoading;
const isServiceGroupsLoading = state => state.isServiceGroupsLoading;
const isAutoSpotLoading = state => state.isAutoSpotLoading;
const gridLayerOption = state => state.gridLayerOption;
const invoiceCurrency = state => state.invoiceCurrency;
const companyCurrency = state => state.companyCurrency;
const recipientEmailList = state => state.recipientList;
export default {
  chargeList,
  selectedMonthToDate,
  billList,
  invoiceList,
  invoiceInsightList,
  invoiceInsightGrid,
  computingResources,
  additionalServices,
  serviceGroups,
  autoSpot,
  activeMonthIdx,
  firstMonthIdx,
  lastMonthIdx,
  selectedTabIndex,
  selectedVendor,
  chargeTableState,
  selectedViewByOption,
  billingDetail,
  cloudBilDetailsSelectedTagKey,
  billingDetailWithTag,
  cloudBilDetailsTagKeys,
  isBillListLoading,
  isChargeListLoading,
  isInvoiceListLoading,
  isInvoiceInsightListLoading,
  isInvoiceInsightGridLoading,
  isCloudBillDetailLoading,
  isComputingResourcesLoading,
  isAdditionalServicesLoading,
  isServiceGroupsLoading,
  billingDetailWithSvgSet,
  cloudBilDetailsSelectedSvgSet,
  cloudBilDetailsSvgSets,
  gridLayerOption,
  invoiceCurrency,
  companyCurrency,
  isAutoSpotLoading,
  recipientEmailList
};
