import _isEmpty from 'lodash/isEmpty';
import {
  SCALE,
  DASHBOARD_VIEW_BY
} from '@/constants/dashboardConstants';
import {
  CHARGE_LIST_DETAILS_COSTS,
} from '@/constants/billingConstants';
import {CHART_TYPE, CURRENCY_SYMBOL, CURRENCY} from '@/constants/constants';
import {formatCost, formatCostForExport} from "./costUtils";
import _capitalize from 'lodash/capitalize'
import _get from 'lodash/get';
import { getCurrencyByField } from '@/util/currencyConfigs';
import { formatCostValue
        ,groupingByAccount
        ,groupingByInvoice
        ,groupingByRegion
} from '@/util/billingUtils';
import { getAccountKeyByVendor } from '@/util/dashboardUtils';
import { getDisplayItemBaseOnViewBy } from '@/util/formatAccountUtils';
import {getMonthYearDateFormatByLocalization} from "@/util/dateTimeUtils";
import {calculateCostByCurrency, calculateCostByCurrencyForExport, formatCostBaseOnSelectedCurrencySymbol} from '@/util/costUtils';
import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
import { VIEW_BY_OPTION_VALUE } from "@/constants/billingConstants";
import dayjs from 'dayjs';
import _isNil from "lodash/isNil";
import {Trans} from "../components/common/base-i18n/Translation";
import {SUPPORTED_LANGUAGE} from "../constants/trans";


function prepareBillDetailExportData(cloudBillDetails, viewBy) {
  let results = [];

  switch (viewBy) {
    case VIEW_BY_OPTION_VALUE.ACCOUNT:
      results = groupingByAccount(cloudBillDetails);
      break;
    case VIEW_BY_OPTION_VALUE.INVOICE:
      results = groupingByInvoice(cloudBillDetails)
      break;
    case VIEW_BY_OPTION_VALUE.REGION:
      results = groupingByRegion(cloudBillDetails);
      break;
    case VIEW_BY_OPTION_VALUE.TAG:
      //this.prepareCloudBillingDetailDataWithTag();
      break;
    case VIEW_BY_OPTION_VALUE.SERVICEGROUP:
      //this.prepareCloudBillingDetailDataWithServiceGroup();
      break;
    default:
      results = groupingByAccount(cloudBillDetails);
      break;
  }

  return results;
}

function groupDataForExportCloudBillDetailsByView(cloudBillDetails, viewBy) {
  let results = {};
  let exportData = [];
  let totalAmount = 0;

  switch(viewBy){
    case VIEW_BY_OPTION_VALUE.ACCOUNT:

      cloudBillDetails.forEach(item => {
        if (results[item.linkedAccountId] === undefined) {
          results[item.linkedAccountId] = {
            //account_id:`${item.linkedAccountAlias? item.linkedAccountAlias: item.linkedAccountId}(${item.linkedAccountId})`,
            account_id: item.linkedAccountId,
            sub_total: item.cost,
            detail_usage_charges: [convertBillDetailByAccountToExportItem(item)],
          }
        } else {
          results[item.linkedAccountId].sub_total = results[item.linkedAccountId].sub_total + item.cost;

          let detailUsageCharges = results[item.linkedAccountId].detail_usage_charges;
          detailUsageCharges.push(convertBillDetailByAccountToExportItem(item));
          results[item.linkedAccountId].detail_usage_charges = detailUsageCharges;
        }
        totalAmount = totalAmount + item.cost;
      });
      break;
    case VIEW_BY_OPTION_VALUE.INVOICE:

      //let invoiceDetails = prepareBillDetailExportData(cloudBillDetails, viewBy)
      cloudBillDetails.forEach(item => {
        if (results[item.invoiceId] === undefined) {
          results[item.invoiceId] = {
            invoice_id: item.invoiceId,
            sub_total: item.cost,
            detail_usage_charges: [convertBillDetailByInvoiceToExportItem(item)],
          }
        } else {
          results[item.invoiceId].sub_total = results[item.invoiceId].sub_total + item.cost;

          let detailUsageCharges = results[item.invoiceId].detail_usage_charges;
          detailUsageCharges.push(convertBillDetailByInvoiceToExportItem(item));
          results[item.invoiceId].detail_usage_charges = detailUsageCharges;
        }
        totalAmount = totalAmount + item.cost;
      });
      break;
    case VIEW_BY_OPTION_VALUE.REGION: // 리전 > 제품 > 사용유형 > 디테일

      //let regionDetails = prepareBillDetailExportData(cloudBillDetails, viewBy)
      cloudBillDetails.forEach(item => {
        if (results[item.regionName] === undefined) {
          results[item.regionName] = {
            region_name: item.regionName,
            sub_total: item.cost,
            detail_usage_charges: [convertBillDetailByRegionToExportItem(item)],
          }
        } else {
          results[item.regionName].sub_total = results[item.regionName].sub_total + item.cost;

          let detailUsageCharges = results[item.regionName].detail_usage_charges;
          detailUsageCharges.push(convertBillDetailByRegionToExportItem(item));
          results[item.regionName].detail_usage_charges = detailUsageCharges;
        }
        totalAmount = totalAmount + item.cost;
      });
      break;
    case VIEW_BY_OPTION_VALUE.TAG:

      cloudBillDetails.forEach(item => {
        if (results[item.tagValue] === undefined) {
          results[item.tagValue] = {
            tag_value: item.tagValue,
            sub_total: item.cost,
            detail_usage_charges: [convertBillDetailByTagToExportItem(item)],
          }
        } else {
          results[item.tagValue].sub_total = results[item.tagValue].sub_total + item.cost;

          let detailUsageCharges = results[item.tagValue].detail_usage_charges;
          detailUsageCharges.push(convertBillDetailByTagToExportItem(item));
          results[item.tagValue].detail_usage_charges = detailUsageCharges;
        }
        totalAmount = totalAmount + item.cost;
      });
      break;
    case VIEW_BY_OPTION_VALUE.SERVICEGROUP:

      cloudBillDetails.forEach(item => {
        if (results[item.serviceGroup] === undefined) {
          results[item.serviceGroup] = {
            service_group: item.serviceGroup,
            sub_total: item.cost,
            detail_usage_charges: [convertBillDetailByServiceGroupToExportItem(item)],
          }
        } else {
          results[item.serviceGroup].sub_total = results[item.serviceGroup].sub_total + item.cost;

          let detailUsageCharges = results[item.serviceGroup].detail_usage_charges;
          detailUsageCharges.push(convertBillDetailByServiceGroupToExportItem(item));
          results[item.serviceGroup].detail_usage_charges = detailUsageCharges;
        }
        totalAmount = totalAmount + item.cost;
      });
      break;
    default:

      cloudBillDetails.forEach(item => {
        if (results[item.linkedAccountId] === undefined) {
          results[item.linkedAccountId] = {
            //account_id:`${item.linkedAccountAlias? item.linkedAccountAlias: item.linkedAccountId}(${item.linkedAccountId})`,
            account_id: item.linkedAccountId,
            sub_total: item.cost,
            detail_usage_charges: [convertBillDetailByAccountToExportItem(item)],
          }
        } else {
          results[item.linkedAccountId].sub_total = results[item.linkedAccountId].sub_total + item.cost;

          let detailUsageCharges = results[item.linkedAccountId].detail_usage_charges;
          detailUsageCharges.push(convertBillDetailByAccountToExportItem(item));
          results[item.linkedAccountId].detail_usage_charges = detailUsageCharges;
        }
        totalAmount = totalAmount + item.cost;
      });
      break;
  }

  Object.keys(results).forEach(accountKey => {
    exportData.push(results[accountKey])
  });
  return {
    row: exportData.sort(exportRowComparator),
    totalAmount: totalAmount
  };
}

function exportRowComparator(a, b) {
  if (a.sub_total < b.sub_total) {
    return 1;
  }
  if (a.sub_total > b.sub_total) {
    return -1;
  }
  return 0;
}

function convertBillDetailByAccountToExportItem(cloudBillDetail) { // 5 depth
  let result = {};
  //result['account_id'] = `${cloudBillDetail.linkedAccountAlias? cloudBillDetail.linkedAccountAlias: cloudBillDetail.linkedAccountId}(${cloudBillDetail.linkedAccountId})`;
  result['account_id'] =  cloudBillDetail.linkedAccountId,
  result['product_name'] = cloudBillDetail.productName;
  result['region'] = cloudBillDetail.regionName;
  result['use_type'] = cloudBillDetail.usageType;
  result['description'] = cloudBillDetail.itemDescription;
  result['use_qnt'] = cloudBillDetail.usage;
  result['total_cost'] = cloudBillDetail.cost;
  return result;
}

function convertBillDetailByInvoiceToExportItem(cloudBillDetail) { // 5 depth
  let result = {};
  result['invoice_id'] = cloudBillDetail.invoiceId;
  result['product_name'] = cloudBillDetail.productName;
  result['region'] = cloudBillDetail.regionName;
  result['use_type'] = cloudBillDetail.usageType;
  result['description'] = cloudBillDetail.itemDescription;
  result['use_qnt'] = cloudBillDetail.usage;
  result['total_cost'] = cloudBillDetail.cost;
  return result;
}

function convertBillDetailByRegionToExportItem(cloudBillDetail) { // 4 depth
  let result = {};
  result['region_name'] = cloudBillDetail.regionName;
  result['product_name'] = cloudBillDetail.productName;
  result['use_type'] = cloudBillDetail.usageType;
  result['description'] = cloudBillDetail.itemDescription;
  result['use_qnt'] = cloudBillDetail.usage;
  result['total_cost'] = cloudBillDetail.cost;
  return result;
}

function convertBillDetailByTagToExportItem(cloudBillDetail) { // 5 depth
  let result = {};
  result['tag_value'] = cloudBillDetail.tagValue;
  result['product_name'] = cloudBillDetail.productName;
  result['region'] = cloudBillDetail.regionName;
  result['use_type'] = cloudBillDetail.usageType;
  result['description'] = cloudBillDetail.itemDescription;
  result['use_qnt'] = cloudBillDetail.usage;
  result['total_cost'] = cloudBillDetail.cost;
  return result;
}

function convertBillDetailByServiceGroupToExportItem(cloudBillDetail) { // 5 depth
  let result = {};
  result['service_group'] = cloudBillDetail.serviceGroup;
  result['product_name'] = cloudBillDetail.productName;
  result['region'] = cloudBillDetail.regionName;
  result['use_type'] = cloudBillDetail.usageType;
  result['description'] = cloudBillDetail.itemDescription;
  result['use_qnt'] = cloudBillDetail.usage;
  result['total_cost'] = cloudBillDetail.cost;
  return result;
}

function convertCloudBillDetailToExportItem(cloudBillDetail) {
  let result = {};
  result['service_group'] = !_isEmpty(cloudBillDetail.serviceGroup) ? cloudBillDetail.serviceGroup : 'Undefined';
  result['product_name'] = cloudBillDetail.productName;
  result['region'] = cloudBillDetail.regionName;
  result['use_type'] = cloudBillDetail.usageType;
  result['description'] = cloudBillDetail.itemDescription;
  result['use_qnt'] = cloudBillDetail.usage;
  result['total_cost'] = cloudBillDetail.cost;
  return result;
}

function prepareDataForExportProductPortionByAccount(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    item.children.forEach(child => {
      let rowData = [];
      rowData.push(item.name);
      rowData.push(child.name);
      rowData.push(child.vendor);
      rowData.push(formatPortionCost(child.cost, exchangeRate, currencySymbol, currency));
      rowData.push(Number(child.cost*100/item.totalCost).toFixed(2) +'%');
      results.push(rowData);
    })
  });
  return results;
}

function prepareDataForExportProductPortionForMultiVendors(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    item.children.forEach(child => {
      let childCost = child.cost !== undefined ? child.cost : 0;
      let formattedPortionCost = formatPortionCost(childCost, exchangeRate, currencySymbol, currency);
      let rowData = [];
      rowData.push(_isEmpty(child.familyCode)?item.name:child.familyCode);
      rowData.push(child.name);
      rowData.push(child.vendor);
      rowData.push(formattedPortionCost.substring(1, formattedPortionCost.length));
      rowData.push(Number(Math.abs(child.cost)*100/portionData.totalCost).toFixed(6));
      results.push(rowData);
    })
  });
  return results;
}

function formatPortionCost(cost, exchangeRate, currencySymbol, currency) {
  return currencySymbol + calculateCostByCurrencyForExport(cost, currency, exchangeRate)
}

function prepareDataForExportPortionByAccount(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    let rowData = [];
    rowData.push(item.name + "\t" );
    rowData.push(item.vendor);
    rowData.push(formatPortionCost(item.cost, exchangeRate, currencySymbol, currency));
    rowData.push(item.percentage + "%");
    results.push(rowData);
  });
  return results;
}

function prepareDataForExportPortionByServiceGroup(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    item.children.forEach(child => {
      let rowData = [];
      let formattedPortionCost = formatPortionCost(child.cost, exchangeRate, currencySymbol, currency);
      rowData.push(child.name + "\t" );
      rowData.push(child.vendor);
      rowData.push(formattedPortionCost.substring(1, formattedPortionCost.length));
      rowData.push(Number(Math.abs(child.cost)*100/portionData.totalCost).toFixed(6));
      results.push(rowData);
    })
  })
  return results
}

function prepareDataForExportPortionByTag(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    let rowData = [];
    rowData.push(item.name + "\t" );
    rowData.push(item.vendor);
    rowData.push(formatPortionCost(item.cost, exchangeRate, currencySymbol, currency));
    rowData.push(item.percentage + "%");
    results.push(rowData);
  });
  return results;
}

function prepareDataForExportPortionByRegion(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    let rowData = [];
    rowData.push(item.name + "\t" );
    rowData.push(item.vendor);
    rowData.push(formatPortionCost(item.cost, exchangeRate, currencySymbol, currency));
    rowData.push(item.percentage + "%");
    results.push(rowData);
  });
  return results;
}

function prepareDataForExportPortionByProduct(portionData, currency, exchangeRate, currencySymbol) {
  let results = [];
  portionData.forEach(item => {
    item.children.forEach(child => {
      let rowData = [];
      rowData.push(item.name);
      rowData.push(child.name);
      rowData.push(child.vendor);
      rowData.push(formatPortionCost(child.cost, exchangeRate, currencySymbol, currency));
      rowData.push(Number(child.cost*100/item.totalCost).toFixed(2) +'%');
      results.push(rowData);
    })
  });
  return results;
}

function prepareDataForExportDashboardCost(dashboardCostByCondition, widgetConfig, currencySymbol, currency, exchangeRate) {
  let results = [];
  let maxIndex = dashboardCostByCondition.length - 1;

    for (let i = 0; i < dashboardCostByCondition[maxIndex].cost.length; i++) {

      let rowData = [];
      switch (widgetConfig.chartType) {
        case CHART_TYPE.STACK: {
          rowData = getRowDataInDashboardCost(widgetConfig, dashboardCostByCondition, currencySymbol, currency, exchangeRate, i);
          results.push(rowData);
          break;
        }
        case CHART_TYPE.LINE: {
          if (!dashboardCostByCondition[0].cost[i].isOthers) {
            rowData = getRowDataInDashboardCost(widgetConfig, dashboardCostByCondition, currencySymbol, currency, exchangeRate, i);
            results.push(rowData);
          }
          break;
        }
      }

  }

  return results;
}

function getRowDataInDashboardCost(widgetConfig, dashboardCostByCondition, currencySymbol, currency, exchangeRate, index) {
  let rowData = [];

  // 0. Get the latest cost data
  let maxIndex = dashboardCostByCondition.length - 1;
  const latestData = dashboardCostByCondition[maxIndex].cost[index];

  // 1. To insert item name
  if (widgetConfig.viewBy === DASHBOARD_VIEW_BY.ACCOUNT) {
    rowData.push(_capitalize(getDisplayItemBaseOnViewBy(latestData, widgetConfig.viewBy)));
  } else {
    rowData.push(getItemRowsInDashboardCost(latestData, widgetConfig.viewBy));
  }

  // 2. To insert vendor
  rowData.push(latestData.vendor);

  // 3. Original logic : get cost and push it "by row"
  dashboardCostByCondition.forEach(item => {
    const cost = _get(item, `cost[${index}].cost`) || 0;
    pushCostRowInDashboardCost(widgetConfig, item, cost, rowData, currencySymbol, currency, exchangeRate);
  });

  return rowData;
}

function pushCostRowInDashboardCost(widgetConfig, item, cost, rowData, currencySymbol, currency, exchangeRate) {
  if (widgetConfig.scale === SCALE.PERCENTAGE && widgetConfig.chartType === CHART_TYPE.STACK) {
    let total = 0
    for (let j = 0; j < item.cost.length; j++) {
      total += item.cost[j].cost
    }
    return rowData.push(`${(cost*100/total).toFixed(2)}% (${currencySymbol}${formatCostForExport(calculateCostByCurrencyForExport(cost, currency, exchangeRate))})`);
  } else {
    return rowData.push(`${formatCostForExport(calculateCostByCurrencyForExport(cost, currency, exchangeRate))}`);
  }
}

function getItemRowsInDashboardCost(costDashboardCostByCondition, viewBy) {
  if(costDashboardCostByCondition.isOthers) {
    return `${_capitalize(costDashboardCostByCondition.item)} (${costDashboardCostByCondition.vendor})`
  }

  return _capitalize(getDisplayItemBaseOnViewBy(costDashboardCostByCondition, viewBy))
}

function prepareDataForExportDashboardCostByVendor(dashboardCostByCondition, widgetConfig, currencySymbol, currency, exchangeRate) {
  let results = [];
  let maxIndex = dashboardCostByCondition.length - 1;

  for (let i = 0; i < dashboardCostByCondition[maxIndex].cost.length; i++) {

    let rowData = [];
    switch (widgetConfig.chartType) {
      case CHART_TYPE.STACK: {
        rowData = getRowDataInDashboardCostByVendor(widgetConfig, dashboardCostByCondition, currencySymbol, currency, exchangeRate, i);
        results.push(rowData);
        break;
      }
      case CHART_TYPE.LINE: {
        if (!dashboardCostByCondition[0].cost[i].isOthers) {
          rowData = getRowDataInDashboardCostByVendor(widgetConfig, dashboardCostByCondition, currencySymbol, currency, exchangeRate, i);
          results.push(rowData);
        }
        break;
      }
    }

  }

  return results;
}

function getRowDataInDashboardCostByVendor(widgetConfig, dashboardCostByCondition, currencySymbol, currency, exchangeRate, index) {
  let rowData = [];

  // 0. Get the latest cost data
  let maxIndex = dashboardCostByCondition.length - 1;
  const latestData = dashboardCostByCondition[maxIndex].cost[index];

  // 1. To insert item name
  if (widgetConfig.viewBy === DASHBOARD_VIEW_BY.ACCOUNT) {
    rowData.push(_capitalize(getDisplayItemBaseOnViewBy(latestData, widgetConfig.viewBy)));
  } else {
    rowData.push(getItemRowsInDashboardCostByVendor(latestData, widgetConfig.viewBy));
  }

  // 2. To insert vendor
  rowData.push(latestData.vendor);

  // 3. Original logic : get cost and push it "by row"
  dashboardCostByCondition.forEach(item => {
    const cost = _get(item, `cost[${index}].cost`) || 0;
    pushCostRowInDashboardCostByVendor(widgetConfig, item, cost, rowData, currencySymbol, currency, exchangeRate);
  });

  return rowData;
}

function pushCostRowInDashboardCostByVendor(widgetConfig, item, cost, rowData, currencySymbol, currency, exchangeRate) {
  if (widgetConfig.scale === SCALE.PERCENTAGE && widgetConfig.chartType === CHART_TYPE.STACK) {
    let total = 0
    for (let j = 0; j < item.cost.length; j++) {
      total += item.cost[j].cost
    }
    return rowData.push(`${(cost*100/total).toFixed(2)}% (${currencySymbol}${formatCostForExport(calculateCostByCurrencyForExport(cost, currency, exchangeRate))})`);
  } else {
    return rowData.push(`${formatCostForExport(calculateCostByCurrencyForExport(cost, currency, exchangeRate))}`);
  }
}

function getItemRowsInDashboardCostByVendor(costDashboardCostByCondition, viewBy) {
  if(costDashboardCostByCondition.isOthers) {
    return `${_capitalize(costDashboardCostByCondition.item)} (${costDashboardCostByCondition.vendor})`
  }

  return _capitalize(getDisplayItemBaseOnViewBy(costDashboardCostByCondition, viewBy))
}


function prepareDataForExportCostAnalyticsCostTrend(costAnalyticsData, currency, exchangeRate, currencySymbol, $vm, isGCP = false) {
  let results = [];
  let lastRow = []
  let totalOfTotal = 0

  costAnalyticsData.items.forEach(caDataItem => {
    let rowData = []
    totalOfTotal += caDataItem.totalCost
    rowData.push(caDataItem.vendor);
    rowData.push(getDisplayItemBaseOnViewBy(caDataItem, $vm.filterSettings.viewBy));
    caDataItem.dailyCosts.forEach(dailyCost => {
      rowData.push(`${formatCost(calculateCostByCurrency(dailyCost.cost, currency, exchangeRate, isGCP))}`)
    })
    rowData.push(`${formatCost(calculateCostByCurrency(caDataItem.totalCost, currency, exchangeRate, isGCP))}`)
    results.push(rowData);
  })

  lastRow.push(`${$vm.$t('costAnalytics.costAnalyticsTrend.download.total')} (${currencySymbol})`)
  lastRow.push('')

  let dates = costAnalyticsData.items[0].dailyCosts.map(dailyCost => {
    return dailyCost.date;
  });
  let totalOfDates = {}
  for (let dateIdx = 0; dateIdx < dates.length; dateIdx++) {
    let totalOfDate = 0;
    for (let seriesIdx = 0; seriesIdx < costAnalyticsData.items.length; seriesIdx++) {
      const costValue =  _get(costAnalyticsData.items, `[${seriesIdx}].dailyCosts[${dateIdx}].cost`)
      if (costValue) {
        totalOfDate += costValue;
      }
    }
    totalOfDates[dateIdx] = formatCost(calculateCostByCurrency(totalOfDate, currency, exchangeRate, isGCP));
    lastRow.push(totalOfDates[dateIdx])
  }
  lastRow.push(formatCost(calculateCostByCurrency(totalOfTotal, currency, exchangeRate, isGCP)))
  results.push(lastRow);
  return results;
}

function getBillingInvoiceListExportingDataRows(cloudInvoices, isShowExchangedValue) {
  let results = [];
  let option = {mantissa: 2};
  if(cloudInvoices.includes(CURRENCY.KRW)){
    option = {mantissa: 0}
  }

  cloudInvoices.forEach(item => {
    option = item.invoiceCurrency == CURRENCY.USD ? {mantissa: 2} : {mantissa: 0};
    let rowData = [];
    rowData.push(item.invoiceId);
    rowData.push(item.linkedAccountAlias);
    rowData.push(item.linkedAccountId);
    rowData.push(formatCost(item.cloudCost, option));
    rowData.push(formatCost(item.supportFee, option));
    rowData.push(item.salesDiscount === 0 ? 0 : `-${formatCost(item.salesDiscount, option)}`);
    rowData.push(formatCost(item.credit, option));
    rowData.push(formatCost(item.cloudServiceCharge, option));
    if (isShowExchangedValue) {
      option = item.companyCurrency ==  CURRENCY.KRW ? {mantissa: 0} : option;
      rowData.push(formatCost(item.exchangedCloudServiceCharge, option));
      rowData.push(item.applyExchangeRate);
    }

    if(_isEmpty(item.applyExchangeRateDate)) {
      rowData.push('-');
    } else {
      rowData.push(dayjs.utc(item.applyExchangeRateDate).format(getFullDateFormatByLocalization()));
    }

    results.push(rowData)
  });
  results.sort((row1, row2) => row2[0].localeCompare(row1[0]));
  return results;
}

function getBillingInvoiceInsightListExportingDataRows(cloudInvoiceInsights, isShowExchangedValue) {
  let results = [];
  let option = {mantissa: 2};
  if(cloudInvoiceInsights.includes(CURRENCY.KRW)){
    option = {mantissa: 0}
  }

  cloudInvoiceInsights.forEach(item => {
    let rowData = [];
    rowData.push(item.account);
    rowData.push(item.product);
    rowData.push(item.region);
    rowData.push(item.currentMonthCost);
    rowData.push(item.lastMonthCost);
    rowData.push(item.averageCost);
    rowData.push(item.maxCost);
    rowData.push(item.useType);
    rowData.push(item.details);
    results.push(rowData)
  });
  results.sort((row1, row2) => row2[0].localeCompare(row1[0]));
  return results;
}

function getBillingInvoiceListExportingDataRowsKRWonly(cloudInvoices) { // OCI 케이스
  let results = [];
  let option = {mantissa: 2};
  if(cloudInvoices[0].companyCurrency.includes(CURRENCY.KRW)){
    option = {mantissa: 0}
  }

  cloudInvoices.forEach(item => {
    let rowData = [];
    rowData.push(item.invoiceId);
    rowData.push(item.linkedAccountAlias);
    rowData.push(item.linkedAccountId);
    rowData.push(formatCost(item.cloudCost, option));
    rowData.push(formatCost(item.supportFee, option));
    rowData.push(item.salesDiscount === 0 ? 0 : `-${formatCost(item.salesDiscount, option)}`);
    rowData.push(formatCost(item.credit, option));
    rowData.push(formatCost(item.exchangedCloudServiceCharge, option));
    if(item.applyExchangeRateDate === 'notPaidUp') {
      rowData.push('-');
    } else {
      rowData.push(dayjs.utc(item.applyExchangeRateDate).format(getFullDateFormatByLocalization()));
    }

    results.push(rowData)
  });
  results.sort((row1, row2) => row2[0].localeCompare(row1[0]));
  return results;
}

function getBillingInvoiceListExportingDataRowsCNYonly(cloudInvoices) { // TENCENT 케이스
  let results = [];
  let option = {mantissa: 2};
  if(cloudInvoices[0].companyCurrency.includes(CURRENCY.CNY)){
    option = {mantissa: 0}
  }

  cloudInvoices.forEach(item => {
    let rowData = [];
    rowData.push(item.invoiceId);
    rowData.push(item.linkedAccountAlias);
    rowData.push(item.linkedAccountId);
    rowData.push(formatCost(item.cloudCost, option));
    rowData.push(formatCost(item.supportFee, option));
    rowData.push(item.salesDiscount === 0 ? 0 : `-${formatCost(item.salesDiscount, option)}`);
    rowData.push(formatCost(item.credit, option));
    rowData.push(formatCost(item.exchangedCloudServiceCharge, option));
    rowData.push(dayjs.utc(item.applyExchangeRateDate).format(getFullDateFormatByLocalization()));

    results.push(rowData)
  });
  results.sort((row1, row2) => row2[0].localeCompare(row1[0]));
  return results;
}


function getNcpBillingInvoiceListExportingDataRows(cloudInvoices) {
  let results = [];
  let option = {mantissa: 2};
  if(cloudInvoices[0].companyCurrency.includes(CURRENCY.KRW)){
    option = {mantissa: 0}
  }

  cloudInvoices.forEach(item => {
    let rowData = [];
    rowData.push(item.invoiceId);
    rowData.push(item.linkedAccountAlias);
    rowData.push(item.linkedAccountId);
    rowData.push(formatCost(item.cloudCost, option));
    //rowData.push(formatCost(item.supportFee, option));
    rowData.push(item.ncpDiscount === 0 ? 0 : `-${formatCost(item.ncpDiscount, option)}`);
    rowData.push(item.salesDiscount === 0 ? 0 : `-${formatCost(item.salesDiscount, option)}`);
    rowData.push(item.credit === 0 ? 0 : `-${formatCost(item.credit, option)}`);
    // rowData.push(formatCost(item.vatCost, option));
    rowData.push(formatCost(item.exchangedCloudServiceCharge, option));
    if(item.applyExchangeRateDate === 'notPaidUp') {
      rowData.push('-');
    } else {
      rowData.push(dayjs.utc(item.applyExchangeRateDate).format(getFullDateFormatByLocalization()));
    }

    results.push(rowData)
  });
  results.sort((row1, row2) => row2[0].localeCompare(row1[0]));
  return results;
}

function getAzureBillingInvoiceListExportingDataRows(cloudInvoices){
  let results = [];
  let option = {mantissa: 2};
  if(cloudInvoices[0].companyCurrency.includes(CURRENCY.KRW)){
    option = {mantissa: 0}
  }

  cloudInvoices.forEach(item => {
    let rowData = [];
    rowData.push(item.invoiceId);
    rowData.push(item.linkedAccountAlias);
    rowData.push(item.linkedAccountId);
    rowData.push(formatCost(item.cloudCost, option));
    rowData.push(formatCost(item.supportFee, option));
    rowData.push(item.salesDiscount === 0 ? 0 : `-${formatCost(Math.abs(item.salesDiscount), option)}`);
    rowData.push(item.credit === 0 ? 0 : `${formatCost(Math.abs(item.credit), option)}`);
    rowData.push(formatCost(item.exchangedCloudServiceCharge, option));

    if(_isEmpty(item.applyExchangeRateDate) || '-' === item.applyExchangeRateDate) {
      rowData.push('-');
    } else {
      rowData.push(dayjs.utc(item.applyExchangeRateDate).format(getFullDateFormatByLocalization()));
    }

    results.push(rowData)
  });

  return results;
}

function getChargeListExportingDataRows(chargeListItem, selectedVendor, $vm, companyCurrency, invoiceCurrency) {

  let results = [];
  const selectedDate = dayjs.utc(`${chargeListItem.chargeYear}-${chargeListItem.chargeMonth}-01`).format(getMonthYearDateFormatByLocalization());
  const cfrcDiscountSeparation = (chargeListItem.chargeYear + chargeListItem.chargeMonth) > '202203';
  // let accountFullName = chargeListItem.linkedAccountId ? `${selectedVendor} ${chargeListItem.linkedAccountId}${chargeListItem.linkedAccountAlias ? ` (${chargeListItem.linkedAccountAlias})` : ''}` :
  //   $vm.$t('billing.billingChargeDetail.chargeAccountList.all');
  let accountFullName = chargeListItem.linkedAccountId ? `${selectedVendor} ${chargeListItem.linkedAccountAlias ? chargeListItem.linkedAccountAlias : chargeListItem.linkedAccountId} (${chargeListItem.linkedAccountId})` :
    $vm.$t('billing.billingChargeDetail.chargeAccountList.all');
  CHARGE_LIST_DETAILS_COSTS[selectedVendor].forEach(function(chargeListDetailsCost) {
    if (selectedVendor==='GCP' && !_isEmpty(chargeListItem.creditDetails) && chargeListDetailsCost.value === 'credit') return;
    let rowData = [];
    rowData.push(selectedDate);
    rowData.push(chargeListItem.linkedAccountAlias);
    rowData.push(chargeListItem.linkedAccountId);
    rowData.push($vm.$t(chargeListDetailsCost.type));
    rowData.push($vm.$t(chargeListDetailsCost.name));
    rowData.push(getCurrencyByField(chargeListDetailsCost.value, invoiceCurrency));
    rowData.push(formatCostBaseOnSelectedCurrencySymbol(
      getProcessCostSignForExport(chargeListDetailsCost.value , chargeListItem[chargeListDetailsCost.value],invoiceCurrency,selectedVendor) // 마이너스 금액 처리
      , CURRENCY_SYMBOL[invoiceCurrency])); // 추후 변동환율로 처리해야 함

    if (cfrcDiscountSeparation) {
      if(chargeListDetailsCost.name !== 'billing.billingChargeDetail.download.costNames.cfDiscount')
        results.push(rowData);
    } else {
      if((chargeListDetailsCost.name !== 'billing.billingChargeDetail.download.costNames.cfDtoDiscount'
        && chargeListDetailsCost.name !== 'billing.billingChargeDetail.download.costNames.cfReqDiscount'))
        results.push(rowData);
    }
  });

  chargeListItem.additionalServices && chargeListItem.additionalServices.forEach(additionalItem => {
    let additionalItemData = [];
    additionalItemData.push(selectedDate);
    additionalItemData.push(chargeListItem.linkedAccountAlias);
    additionalItemData.push(chargeListItem.linkedAccountId);
    additionalItemData.push($vm.$t('billing.billingChargeDetail.download.costTypes.additionalService'));
    additionalItemData.push(additionalItem.additionalServiceName);
    additionalItemData.push(getCurrencyByField('additionalServiceFee', companyCurrency));
    additionalItemData.push(getProcessCostSignForExport('', additionalItem.additionalServiceCharge,companyCurrency,selectedVendor)); // DB 테이블에서 마이너스 금액으로 받아오고 있음
    results.push(additionalItemData);
  });

 chargeListItem.creditDetails && chargeListItem.creditDetails.forEach(creditDetail => {
      let creditDetailItemData = [];
      creditDetailItemData.push(selectedDate);
      creditDetailItemData.push(chargeListItem.linkedAccountAlias);
      creditDetailItemData.push(chargeListItem.linkedAccountId);
      creditDetailItemData.push($vm.$t('billing.billingChargeDetail.download.costTypes.cloud_credit'));
      creditDetailItemData.push($vm.$t('billing.billingChargeDetail.download.costNames.' + creditDetail.crdtTypeCd));
      creditDetailItemData.push(getCurrencyByField('credit', companyCurrency));
      creditDetailItemData.push(getProcessCostSignForExport('', creditDetail.crdtAmt,companyCurrency,selectedVendor));
      results.push(creditDetailItemData);
    });

  return results;
}

function getProcessCostSignForExport(value , detailCost,invoiceCurrency, selectedVendor) {
    const DASHES = '-'

    let detailCost1 = Math.abs(detailCost);

    switch (value) {
      case 'salesDiscount': {
        return `${detailCost1 !== 0 ? DASHES : ''}${formatCostValue(detailCost1, 'salesDiscount',invoiceCurrency,selectedVendor)}`
      }
      case 'ncpDiscount': {
        return `${detailCost1 !== 0 ? DASHES : ''}${formatCostValue(detailCost1, 'ncpDiscount',invoiceCurrency,selectedVendor)}`
      }
      case 'credit': {
        return `${detailCost1 !== 0 ? DASHES : ''}${formatCostValue(detailCost1, 'credit',invoiceCurrency,selectedVendor)}`
      }
      case 'onDemandDiscount': {
        return `${detailCost1 !== 0 ? DASHES : ''}${formatCostValue(detailCost1, 'onDemandDiscount',invoiceCurrency,selectedVendor)}`
      }
      case 'cloudFrontDiscount': {
        return `${detailCost1 !== 0 ? DASHES : ''}${formatCostValue(detailCost1, 'cloudFrontDiscount',invoiceCurrency,selectedVendor)}`
      }
      case 'cloudFrontDtoDiscount': {
        return `${detailCost !== 0 ? DASHES : ''}${formatCostValue(detailCost, 'cloudFrontDtoDiscount')}`
      }
      case 'cloudFrontReqDiscount': {
        return `${detailCost !== 0 ? DASHES : ''}${formatCostValue(detailCost, 'cloudFrontReqDiscount')}`
      }
      default: {
        return `${detailCost < 0 ? DASHES : ''}${formatCostValue(detailCost1, '',invoiceCurrency,selectedVendor)}`
      }
    }
  }

function getColumnHeaderByPredefinedColumn(csvDownloadColumns, activeMonthBill, $vm, columnPrefix){
  let columnHeader, i18nHeader, columnHeaderArr = [];

  csvDownloadColumns.forEach(columnObj => {
    if('currency' in columnObj){
      i18nHeader = activeMonthBill[columnObj.currency];
      columnHeader = $vm.$t(`${columnPrefix}.${columnObj.name}`);
      columnHeader = `${columnHeader}(${i18nHeader})`
    }else{
      columnHeader = $vm.$t(`${columnPrefix}.${columnObj.name}`);
    }

    columnHeaderArr.push({width: 50, header: columnHeader});
  });

  return columnHeaderArr;
}

export {
  prepareDataForExportProductPortionByAccount,
  prepareDataForExportPortionByAccount,
  prepareDataForExportPortionByTag,
  prepareDataForExportPortionByRegion,
  prepareDataForExportPortionByProduct,
  prepareDataForExportPortionByServiceGroup,
  prepareDataForExportDashboardCost,
  prepareDataForExportCostAnalyticsCostTrend,
  prepareDataForExportProductPortionForMultiVendors,
  getBillingInvoiceListExportingDataRows,
  getBillingInvoiceInsightListExportingDataRows,
  getChargeListExportingDataRows,
  prepareDataForExportDashboardCostByVendor,
  groupDataForExportCloudBillDetailsByView,
  prepareBillDetailExportData,
  getBillingInvoiceListExportingDataRowsKRWonly,
  getBillingInvoiceListExportingDataRowsCNYonly,
  getNcpBillingInvoiceListExportingDataRows,
  getAzureBillingInvoiceListExportingDataRows,
  getColumnHeaderByPredefinedColumn
}
