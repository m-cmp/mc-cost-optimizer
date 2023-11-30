import _isEmpty from 'lodash/isEmpty';
import _isNil from 'lodash/isNil';
import _trim from 'lodash/trim';
import dayjs from 'dayjs';
import _get from 'lodash/get';
import _toLower from 'lodash/toLower';
import _uniq from 'lodash/uniq';
import _cloneDeep from 'lodash/cloneDeep';
import _common from '@/util/Common';
import {
  ADDITIONAL_FILTER_MODEL,
  COST_ANALYTICS_RANGE_COLOR,
  COST_TREND_CHART_DATA_EXTRA_FIELD,
  FILTER_REQUEST_FIELD_BY_FIELD,
  MAIN_FILTER_MODEL,
  MAIN_FILTER_FIELDS,
  PORTION_DETAIL_NO_OTHERS_NAME,
  SORT_TYPE,
  SPECIAL_FILTER_OPTION, COST_ANALYTICS_TIME_FRAME, COST_ANALYTICS_COMPARE,
  COMPARE_COST_TYPE,
  PREFIX_COMPARE,
  NO_DATA_TABLE_VALUE,
  COMPARE_LABEL,
  MAX_NUMBER_OF_PORTION,
  MAX_COLUMN_FIELDS_FOR_COMPARE_MODE
} from "@/constants/costAnalyticsConstants";
import {
  add0StringToNumberLessThan10,
  getCurrentDateWithCurrentTimezone,
  getDates,
  getFullDateFormatByLocalization
} from '@/util/dateTimeUtils';
import {calculateCostByCurrency, formatCost} from '@/util/costUtils';

import {
  FULL_TIME_CONST,
  TOTAL_CONST,
  ACTIVATED_TOTAL_COST,
  COMPARE_FULL_TIME_CONST,
  COMPARE_TOTAL_CONST,
  ACTIVATED_COMPARE_TOTAL_COST,
  COST_CONST,
  USAGE_CONST
} from '@/constants/constants';
import {
  removeAllSpecialCharacters,
} from '@/util/stringUtils';
import { getDisplayItemWithVendorBaseOnViewBy, getDisplayItemBaseOnViewBy, getDisplayCompareItemWithVendorBaseOnViewBy } from '@/util/formatAccountUtils';
import numbro from "numbro";
import _xor from 'lodash/xor';
import _isEqual from "lodash/isEqual";

/**
 * Get combine daily costs between timeFrame and compareTimeFrame
 * In case compareTimeFrame.length > timeFrame.length we will add timeFrame to combineDailyCosts first and then add compareTimeFrame from  costDates.length to compareCostDates.length
 * There is abnormal case
 * Dates is: 01/12, 02/12
 * Compare dates is 28/11, 29/11, 01/12, 02/12, 3/12, 4/12, 5/12
 * -> combine dates is:
 * [
 *   {
 *     date: 01/12,
 *     compareDate: 28/11
 *   },
 *   {
 *     date: 02/12,
 *     compareDate: 29/11
 *   },
 *   {
 *     date: 03/12,
 *     compareDate: 03/12
 *   },
 *   {
 *     date: 04/12,
 *     compareDate: 04/12
 *   },
 *   {
 *     date: 05/12,
 *     compareDate: 05/12
 *   }
 * ]
 * @param costAnalyticsParams
 * @param compareCostAnalyticsParams
 * @returns {[]}
 */
function getCombineDailyCosts(costAnalyticsParams, compareCostAnalyticsParams, isMonthly) {
  let combineDailyCosts = [];
  let costDates = getDates(new Date(costAnalyticsParams.startDate), new Date(costAnalyticsParams.endDate), isMonthly);
  let compareCostDates = getDates(new Date(compareCostAnalyticsParams.startDate), new Date(compareCostAnalyticsParams.endDate), isMonthly);

  for (let i = 0; i < costDates.length; i ++) {
    combineDailyCosts.push({
      date: costDates[i],
      isEmptyDate: false,
      compareDate: compareCostDates[i]  ? compareCostDates[i] : null,
    });
  }

  if (compareCostDates.length > costDates.length) {
    for (let i = costDates.length; i < compareCostDates.length; i ++) {
      if (!costDates.includes(compareCostDates[i])) {
        combineDailyCosts.push({
          date: compareCostDates[i],
          isEmptyDate: true,
          compareDate: compareCostDates[i],
        });
      }
    }
  }

  return combineDailyCosts;
}

/**
 * Get object items
 * Something like
 * {
 *   'AWS-21321321989855': {
 *     dailyCosts: [
 *        {
 *          cost: 2132.2121,
 *          date: '2019-11-20',
 *          compareCost: 2100.2121,
 *          compareDate: '2019-11-10',
 *        }
 *      ],
 *      item: '21321321989855',
 *      itemAlias: 'Platform Service',
 *      vendor: 'AWS',
 *      totalCost: 2132.2121,
 *      compareTotalCost: 2100.2121
 *   }
 * }
 * @param items
 * @returns {{}}
 */
function getObjectItems(items) {
  let objectItems = {}
  items.forEach(item => {
    objectItems[getAccountKeyByVendor(item)] = item;
  });

  return objectItems;
}

/**
 * Get daily costs by item
 * Something like
 *  [
 *    {
 *      cost: 2132.2121,
 *      date: '2019-11-20',
 *      compareCost: 2100.2121,
 *      compareDate: '2019-11-10',
 *    }
 *  ]
 *
 * @param combineDailyCosts
 * @param dailyCosts
 * @param compareDailyCosts
 * @returns {[]}
 */
function getDailyCostsByItem(combineDailyCosts, dailyCosts, compareDailyCosts) {
  let combineDailyCostsData = [];
  combineDailyCosts.forEach((combineDailyCost, index) => {
    let combineDailyCostData = _cloneDeep(combineDailyCost);

    let dailyCost = dailyCosts && dailyCosts[index];
    let compareDailyCost = compareDailyCosts && compareDailyCosts[index];

    combineDailyCostData.cost = dailyCost && dailyCost.cost ? dailyCost.cost : null;
    combineDailyCostData.compareCost = compareDailyCost && compareDailyCost.cost ? compareDailyCost.cost : null;

    combineDailyCostsData.push(combineDailyCostData)
  });

  return combineDailyCostsData;
}

function getCompareDatesInCombineDailyCosts(combineDailyCosts) {
  return combineDailyCosts.map(combineDailyCost => {
    return combineDailyCost.compareDate;
  });
}

function getCompareDailyCostsInCombineDailyCosts(compareDailyCosts, compareDatesInCombineDailyCosts) {
  return compareDailyCosts && compareDailyCosts.filter(combineDailyCost => {
    return compareDatesInCombineDailyCosts.includes(combineDailyCost.date);
  });
}

/**
 * Get combine items
 *  Something like
 *  {
 *    items: [
 *      dailyCosts: [
 *        {
 *          cost: 2132.2121,
 *          date: '2019-11-20',
 *          compareCost: 2100.2121,
 *          compareDate: '2019-11-10',
 *        }
 *      ],
 *      item: '21321321989855',
 *      itemAlias: 'Platform Service',
 *      vendor: 'AWS',
 *      totalCost: 2132.2121,
 *      compareTotalCost: 2100.2121
 *    ]
 *  }
 * @param costResponse
 * @param compareCostResponse
 * @param combineDailyCosts
 * @param timeFrameDays
 * @param compareTimeFrameDays
 * @param compareCostType
 * @returns {[]}
 */
function getCombineItems(costResponse, compareCostResponse, combineDailyCosts, timeFrameDays, compareTimeFrameDays, compareCostType, type) {
  const timeFrameMinDays = Math.min(timeFrameDays.length, compareTimeFrameDays.length);
  let items = (type === 'top10') ? (costResponse && costResponse.top10Items || []) : (costResponse && costResponse.items || []);
  let compareItems = (type === 'top10') ? (compareCostResponse && compareCostResponse.top10Items || []) : (compareCostResponse && compareCostResponse.items || []);
  let combineItems = [];
  let objectItems = getObjectItems(items);
  let compareObjectItems = getObjectItems(compareItems);
  let combineItemKeys = _uniq(Object.keys(objectItems).concat(Object.keys(compareObjectItems)));

  const compareDatesInCombineDailyCosts = getCompareDatesInCombineDailyCosts(combineDailyCosts);
  combineItemKeys.map(itemKey => {
    const dailyCosts = objectItems[itemKey] && objectItems[itemKey].dailyCosts;
    let compareDailyCosts = compareObjectItems[itemKey] && compareObjectItems[itemKey].dailyCosts;
    compareDailyCosts = getCompareDailyCostsInCombineDailyCosts(compareDailyCosts, compareDatesInCombineDailyCosts);

    let combineItem = {
      totalCost: null,
      compareTotalCost: null,
      averageCost: 0,
      averageCompareCost: 0,
      dailyCosts: []
    };

    if (objectItems[itemKey]) {
      Object.assign(combineItem, objectItems[itemKey]);
      combineItem.averageCost = objectItems[itemKey].totalCost / timeFrameDays.length;
    }

    if (compareObjectItems[itemKey]) {
      Object.assign(combineItem, {
        item: compareObjectItems[itemKey].item,
        itemAlias: compareObjectItems[itemKey].itemAlias,
        vendor: compareObjectItems[itemKey].vendor,
        compareTotalCost: compareObjectItems[itemKey].totalCost,
        averageCompareCost: compareObjectItems[itemKey].totalCost / compareTimeFrameDays.length
      });
    }

    combineItem.dailyCosts = getDailyCostsByItem(combineDailyCosts, dailyCosts, compareDailyCosts);
    combineItem.averageCostWithInCommonTimeFrame = getAverageCostByDailyCostsByTimeFrameMinDays(dailyCosts, timeFrameMinDays);
    combineItem.averageCompareCostInCommonTimeFrame = getAverageCostByDailyCostsByTimeFrameMinDays(compareDailyCosts, timeFrameMinDays);
    combineItem.totalCostWithInCommonTimeFrame = getTotalCostByDailyCostsByTimeFrameMinDays(dailyCosts, timeFrameMinDays);
    combineItem.compareTotalCostInCommonTimeFrame = getTotalCostByDailyCostsByTimeFrameMinDays(compareDailyCosts, timeFrameMinDays);
    if (compareCostType === COMPARE_COST_TYPE.INDIVIDUAL_COST) {
      combineItem.ratioTotalCost = getRatioBetweenCostAndCompareCost(combineItem.totalCostWithInCommonTimeFrame, combineItem.compareTotalCostInCommonTimeFrame);
    } else {
      combineItem.ratioTotalCost = getRatioBetweenCostAndCompareCost(combineItem.averageCost, combineItem.averageCompareCost);
    }

    combineItems.push(combineItem);
  });

  return combineItems;
}

function combineCompareCostAnalyticsData(costResponse, compareCostResponse, combineDailyCosts, timeFrameDays, compareTimeFrameDays, compareCostType) {
  let combineItems = getCombineItems(costResponse, compareCostResponse, combineDailyCosts, timeFrameDays, compareTimeFrameDays, compareCostType, 'normal');
  let combineItemsTop10 = getCombineItems(costResponse, compareCostResponse, combineDailyCosts, timeFrameDays, compareTimeFrameDays, compareCostType, 'top10');
  combineItemsTop10 = combineItemsTop10.filter((x) => x.item != 'others').concat(combineItemsTop10.filter((x) => x.item === 'others'))

  let totalCostWithInCommonTimeFrame = 0;
  let compareTotalCostInCommonTimeFrame = 0;
  combineItems.map(combineItem => {
    totalCostWithInCommonTimeFrame += combineItem.totalCostWithInCommonTimeFrame;
    compareTotalCostInCommonTimeFrame += combineItem.compareTotalCostInCommonTimeFrame;
  });

  totalCostWithInCommonTimeFrame = 0;
  compareTotalCostInCommonTimeFrame = 0;
  combineItemsTop10.map(combineItem => {
    totalCostWithInCommonTimeFrame += combineItem.totalCostWithInCommonTimeFrame;
    compareTotalCostInCommonTimeFrame += combineItem.compareTotalCostInCommonTimeFrame;
  });

  return {
    items: combineItems,
    top10Items : combineItemsTop10,
    totalCost: costResponse.totalCost,
    compareTotalCost: compareCostResponse.totalCost,
    totalCostWithInCommonTimeFrame: totalCostWithInCommonTimeFrame,
    compareTotalCostInCommonTimeFrame: compareTotalCostInCommonTimeFrame
  };
}

/**
 * Get average cost by daily costs with timeFrameMinDays (same with timeFrame and compareTimeFrame)
 * For example:
 * timeFrame.length <= compareTimeFrame.length -> timeFrameMinDays = timeFrame.length
 * timeFrame.length > compareTimeFrame.length -> timeFrameMinDays = compareTimeFrame.length
 *
 * @param dailyCosts
 * @param timeFrameMinDays
 * @returns {number}
 */
function getAverageCostByDailyCostsByTimeFrameMinDays(dailyCosts, timeFrameMinDays) {
  return getTotalCostByDailyCostsByTimeFrameMinDays(dailyCosts, timeFrameMinDays) / timeFrameMinDays;
}

/**
 * Get total cost by daily costs with timeFrameMinDays (same with timeFrame and compareTimeFrame)
 * For example:
 * timeFrame.length <= compareTimeFrame.length -> timeFrameMinDays = timeFrame.length
 * timeFrame.length > compareTimeFrame.length -> timeFrameMinDays = compareTimeFrame.length
 *
 * @param dailyCosts
 * @param timeFrameMinDays
 * @returns {number}
 */
function getTotalCostByDailyCostsByTimeFrameMinDays(dailyCosts, timeFrameMinDays) {
  let sumOfCost = 0;
  for (let i = 0; i < timeFrameMinDays && dailyCosts && i < dailyCosts.length; i ++) {
    sumOfCost += dailyCosts[i].cost ? dailyCosts[i].cost : 0;
  }

  return sumOfCost;
}

function formatCostAnalyticsDate(dateString) {
  return dayjs.utc(dateString).format(getFullDateFormatByLocalization())
}

function prepareCostAnalyticsDetailPortionData(oneDayRawData, compareData) {
  let result = {
    accountPortionData: [],
    productPortionData: [],
    regionPortionData: [],
    usageTypePortionData: [],
    otherData: {
      accountOthers: [],
      productOthers: [],
      regionOthers: [],
      usageTypeOthers: []
    }
  };

  let othersName = {
    product: PORTION_DETAIL_NO_OTHERS_NAME,
    region: PORTION_DETAIL_NO_OTHERS_NAME,
    usageTypes: PORTION_DETAIL_NO_OTHERS_NAME,
    account: PORTION_DETAIL_NO_OTHERS_NAME
  };

  if (_isEmpty(oneDayRawData.accounts) && _isEmpty(oneDayRawData.products) && _isEmpty(oneDayRawData.regions) && _isEmpty(oneDayRawData.usageTypes)) {
    return result;
  }

  if (!_isEmpty(oneDayRawData.accounts)) {
    let portionByAccount = {};
    let accountIDNameMap = {};
    oneDayRawData.accounts.forEach(item => {
      if (item.isOthers) {
        portionByAccount[`${item.item} (${item.numberOfOthers})`] = {
          portion: item.portion,
          cost: item.cost,
          vendor: item.vendor,
          textValue: `${item.item} (${item.numberOfOthers})`,
          relation: item.relationsForAws,
          itemID: `${item.item} (${item.numberOfOthers})`,
          isOthers: item.isOthers
        };
        result.otherData.accountOthers = getPortionOtherItems(item.otherItems);
        othersName.account = `${item.item} (${(item.numberOfOthers)})`;
        accountIDNameMap[`${item.item} (${item.numberOfOthers})`] = `${item.item} (${item.numberOfOthers})`;
      } else {
        portionByAccount[`${item.itemAlias}(${item.item})`] = {
          portion: item.portion,
          cost: item.cost,
          vendor: item.vendor,
          textValue: `${item.itemAlias}(${item.item})`,
          relation: item.relationsForAws,
          itemID: item.item,
          isOthers: item.isOthers
        };
        accountIDNameMap[item.item] = portionByAccount[`${item.itemAlias}(${item.item})`].textValue
      }
    });
    result.accountPortionData.push(portionByAccount);
    result.accountIDNameMap = accountIDNameMap;
  }
  if (!_isEmpty(oneDayRawData.products)) {
    let portionByProduct = {};
    oneDayRawData.products.forEach(item => {
      let portionData = {
        portion: item.portion,
        cost: item.cost,
        vendor: item.vendor,
        textValue: item.itemAlias ? item.itemAlias : `${item.item} (${item.numberOfOthers})`,
        relation: item.relationsForAws,
        itemID: item.item,
        isOthers: item.isOthers
      };
      if (item.isOthers) {
        portionByProduct[`${item.item} (${item.numberOfOthers})`] = portionData;
        result.otherData.productOthers = getPortionOtherItems(item.otherItems);
        othersName.product = `${item.item} (${item.numberOfOthers})`;
      } else {
        portionByProduct[item.item] = portionData;
      }
    });
    result.productPortionData.push(portionByProduct);
  }
  if (!_isEmpty(oneDayRawData.regions)) {
    let portionByRegion = {};
    oneDayRawData.regions.forEach(item => {
      let portionData = {
        portion: item.portion,
        cost: item.cost,
        vendor: item.vendor,
        textValue: item.itemAlias ? item.itemAlias : `${item.item} (${item.numberOfOthers})`,
        relation: item.relationsForAws,
        itemID: item.item,
        isOthers: item.isOthers
      };
      if (item.isOthers) {
        portionByRegion[`${item.item} (${item.numberOfOthers})`] = portionData;
        result.otherData.regionOthers = getPortionOtherItems(item.otherItems);
        othersName.region = `${item.item} (${item.numberOfOthers})`;
      } else {
        portionByRegion[item.item] = portionData;
      }
    });
    result.regionPortionData.push(portionByRegion);
  }
  if (!_isEmpty(oneDayRawData.usageTypes)) {
    let portionByUsageType = {};
    oneDayRawData.usageTypes.forEach(item => {
      let portionData = {
        portion: item.portion,
        cost: item.cost,
        vendor: item.vendor,
        textValue: item.isOthers ? `${item.item} (${item.numberOfOthers})` : item.item,
        relation: item.relationsForAws,
        itemID: item.item,
        isOthers: item.isOthers
      };
      if (item.isOthers) {
        portionByUsageType[`${item.item} (${item.numberOfOthers})`] = portionData;
        result.otherData.usageTypeOthers = getPortionOtherItems(item.otherItems);
        othersName.usageTypes = `${item.item} (${item.numberOfOthers})`;
      } else {
        portionByUsageType[item.item] = portionData
      }
    });
    result.usageTypePortionData.push(portionByUsageType)
  }
  result.otherName = othersName;

  prepareHorizontalStackedChartCompareData(compareData, result);
  return result;
}

function prepareHorizontalStackedChartCompareData(compareRawData, oneDayData) {
  let result = {
    accountPortionData: oneDayData.accountPortionData,
    productPortionData: oneDayData.productPortionData,
    regionPortionData: oneDayData.regionPortionData,
    usageTypePortionData: oneDayData.usageTypePortionData,
  };

  if (_isNil(compareRawData) || (_isEmpty(compareRawData.accounts) && _isEmpty(compareRawData.products) && _isEmpty(compareRawData.regions) && _isEmpty(compareRawData.usageTypes))) {
    return result;
  }

  if (!_isEmpty(compareRawData.accounts)) {
    let portionByAccount = {};
    let accountIDNameMap = {};
    compareRawData.accounts.forEach(item => {
      if (item.isOthers) {
        portionByAccount[`${item.item} (${item.numberOfOthers})`] = {
          portion: item.portion,
          cost: item.cost,
          vendor: item.vendor,
          textValue: `${item.item} (${item.numberOfOthers})`,
          isOthers: true
        };
        accountIDNameMap[`${item.item} (${item.numberOfOthers})`] = `${item.item} (${item.numberOfOthers})`;
      } else {
        portionByAccount[`${item.itemAlias}(${item.item})`] = {
          portion: item.portion,
          cost: item.cost,
          vendor: item.vendor,
          textValue: `${item.itemAlias}(${item.item})`,
          isOthers: false
        };
        accountIDNameMap[item.item] = portionByAccount[`${item.itemAlias}(${item.item})`].textValue
      }
    });
    result.accountPortionData.push(portionByAccount);
    result.accountIDNameMap = accountIDNameMap;
  }
  if (!_isEmpty(compareRawData.products)) {
    let portionByProduct = {};
    compareRawData.products.forEach(item => {
      let portionData = {
        portion: item.portion,
        cost: item.cost,
        vendor: item.vendor,
        textValue: item.itemAlias ? item.itemAlias : `${item.item} (${item.numberOfOthers})`,
        isOthers: item.isOthers
      };
      if (item.isOthers) {
        portionByProduct[`${item.item} (${item.numberOfOthers})`] = portionData;
      } else {
        portionByProduct[item.item] = portionData;
      }
    });
    result.productPortionData.push(portionByProduct);
  }
  if (!_isEmpty(compareRawData.regions)) {
    let portionByRegion = {};
    compareRawData.regions.forEach(item => {
      let portionData = {
        portion: item.portion,
        cost: item.cost,
        vendor: item.vendor,
        textValue: item.itemAlias ? item.itemAlias : `${item.item} (${item.numberOfOthers})`,
        isOthers: item.isOthers
      };
      if (item.isOthers) {
        portionByRegion[`${item.item} (${item.numberOfOthers})`] = portionData;
      } else {
        portionByRegion[item.item] = portionData;
      }
    });
    result.regionPortionData.push(portionByRegion);
  }
  if (!_isEmpty(compareRawData.usageTypes)) {
    let portionByUsageType = {};
    compareRawData.usageTypes.forEach(item => {
      let portionData = {
        portion: item.portion,
        cost: item.cost,
        vendor: item.vendor,
        textValue: item.isOthers ? `${item.item} (${item.numberOfOthers})` : item.item,
        isOthers: item.isOthers
      };
      if (item.isOthers) {
        portionByUsageType[`${item.item} (${item.numberOfOthers})`] = portionData;
      } else {
        portionByUsageType[item.item] = portionData
      }
    });
    result.usageTypePortionData.push(portionByUsageType)
  }

  return result;
}

function prepareDataForCostTrend(costAnalyticsData, currency, exchangeRate, activatedAccountKeys, filterSettings, isGCP = false) {
  let rawData = costAnalyticsData.items;
  let results = [];
  if (_isEmpty(rawData)) {
    return results;
  }

  rawData.sort((a, b) => compareByTotalCost(a, b, filterSettings));
  rawData = rawData.filter((x) => x.item != 'others').concat(rawData.filter((x) => x.item === 'others'))

  let hasOthers = false
  let othersNm = ''
  let seriesKey = rawData.map(item => {
    if(item.item === 'others'){
      hasOthers = true
      othersNm = getAccountKeyByVendor(item)
    }
    return getAccountKeyByVendor(item)
  });
  let seriesKeys = hasOthers ? seriesKey.filter((x) => x !== othersNm).concat(seriesKey.filter((x) => x === othersNm)) : seriesKey;

  let dates = rawData[0].dailyCosts;
  let isMonthly = rawData[0].monthly;
  let dateFormat = getFullDateFormatByLocalization(isMonthly);

  for (let dateIdx = 0; dateIdx < dates.length; dateIdx++) {
    let totalOfDate = 0;
    let totalOfDateForActivatedAccounts = null;
    let compareTotalOfDate = 0;
    let compareTotalOfDateForActivatedAccounts = null;
    const costDate = dates[dateIdx].date;
    const compareCostDate = dates[dateIdx].compareDate;

    let itemData = {
      time: isMonthly? dayjs(costDate).format(dateFormat) : dayjs(costDate).format(dateFormat),
      [FULL_TIME_CONST]: isMonthly? dayjs(costDate).format('YYYY/MM') : dayjs(costDate).format(dateFormat),
      isEmptyDate: dates[dateIdx] && dates[dateIdx].isEmptyDate,
      isMonthly : isMonthly
    };
    if (filterSettings.isCompare) {
      itemData[COMPARE_FULL_TIME_CONST] = compareCostDate ?
        (isMonthly ? dayjs(compareCostDate).format('YYYY/MM') : dayjs(compareCostDate).format(dateFormat))
        : null;
    }

    results.push(itemData);
    for (let seriesIdx = 0; seriesIdx < rawData.length; seriesIdx++) {
      let costValue =  _get(rawData, `[${seriesIdx}].dailyCosts[${dateIdx}].cost`);
      const compareItem =  _get(rawData, `[${seriesIdx}]`, {});
      let compareCostValue =  _get(compareItem, `dailyCosts[${dateIdx}].compareCost`);
      costValue = (costValue === null && compareCostValue !== null) ? 0 : costValue;
      compareCostValue = (compareCostValue === null && costValue !== null) ? 0 : compareCostValue;

      const isEmptyData =  _get(rawData, `[${seriesIdx}].dailyCosts[${dateIdx}].isEmptyData`, false);
      if (!isEmptyData && costValue !== null) {
        totalOfDate += costValue;
        if (activatedAccountKeys && activatedAccountKeys.includes(getAccountKeyByVendor(rawData[seriesIdx]))) {
          totalOfDateForActivatedAccounts = totalOfDateForActivatedAccounts ? totalOfDateForActivatedAccounts + costValue : costValue;
        }
        results[dateIdx][getAccountKeyByVendor(rawData[seriesIdx])] = formatCost(calculateCostByCurrency(costValue, currency, exchangeRate, isGCP));
      }

      if (filterSettings.isCompare) {
        if (compareCostValue !== null) {
          compareTotalOfDate += compareCostValue;
          if (activatedAccountKeys && activatedAccountKeys.includes(getAccountKeyByVendor(rawData[seriesIdx]))) {
            compareTotalOfDateForActivatedAccounts = compareTotalOfDateForActivatedAccounts ? compareTotalOfDateForActivatedAccounts + compareCostValue : compareCostValue;
          }

          if (filterSettings.compareCostType === COMPARE_COST_TYPE.INDIVIDUAL_COST) {
            results[dateIdx][getCompareAccountKeyByVendor(rawData[seriesIdx])] = formatCost(calculateCostByCurrency(compareCostValue, currency, exchangeRate, isGCP));
            results[dateIdx][getRatioAccountKey(rawData[seriesIdx])] = getRatioBetweenCostAndCompareCost(calculateCostByCurrency(costValue, currency, exchangeRate, isGCP), calculateCostByCurrency(compareCostValue, currency, exchangeRate, isGCP));
          } else {
            results[dateIdx][getCompareAccountKeyByVendor(rawData[seriesIdx])] = formatCost(calculateCostByCurrency(compareItem.averageCompareCost, currency, exchangeRate, isGCP));
            results[dateIdx][getRatioAccountKey(rawData[seriesIdx])] = getRatioBetweenCostAndCompareCost(calculateCostByCurrency(costValue, currency, exchangeRate, isGCP), calculateCostByCurrency(compareItem.averageCompareCost, currency, exchangeRate, isGCP));
          }
        }
      }
    }
    results[dateIdx][TOTAL_CONST] = formatCost(calculateCostByCurrency(totalOfDate, currency, exchangeRate, isGCP));
    results[dateIdx][ACTIVATED_TOTAL_COST] = totalOfDateForActivatedAccounts !== null ? formatCost(calculateCostByCurrency(totalOfDateForActivatedAccounts, currency, exchangeRate, isGCP)) : null;

    if (filterSettings.isCompare) {
      results[dateIdx][COMPARE_TOTAL_CONST] = formatCost(calculateCostByCurrency(compareTotalOfDate, currency, exchangeRate, isGCP));
      results[dateIdx][ACTIVATED_COMPARE_TOTAL_COST] = compareTotalOfDateForActivatedAccounts !== null ? formatCost(calculateCostByCurrency(compareTotalOfDateForActivatedAccounts, currency, exchangeRate, isGCP)) : null;
      let compareSeriesKeys = [];
      compareSeriesKeys = rawData.map(item => {
        return getCompareAccountKeyByVendor(item)
      });
      results[COST_TREND_CHART_DATA_EXTRA_FIELD.COMPARE_DATA] = compareSeriesKeys;
    }
  }
  results[COST_TREND_CHART_DATA_EXTRA_FIELD.SOURCE_DATA] = seriesKeys;
  return results
}

function getRatioCostBaseOnDateItem(dateItem, item, filterSettings) {
  let compareCost = dateItem.compareCost;
  if (filterSettings && filterSettings.compareCostType === COMPARE_COST_TYPE.AVERAGE) {
    compareCost = item.averageCompareCost;
  }
  let ratioCost;
  //[OIO-13311] CA Compare개선
  if (filterSettings.isCompare /* && dateItem.compareDate */) { //[OIO-13311] CA Compare개선
    if (dateItem.isEmptyDate) {
      // ratioCost = compareCost === null ? NO_DATA_TABLE_VALUE : compareCost; //[OIO-13311] CA Compare개선
      ratioCost = compareCost === null ? NO_DATA_TABLE_VALUE : '-100%';
    } else {
      if (compareCost === null && dateItem.cost === null) {
        ratioCost = NO_DATA_TABLE_VALUE;
      } else {
        ratioCost = `${getRatioBetweenCostAndCompareCost(dateItem.cost || 0, compareCost)}%`;
      }
    }
  } else {
    ratioCost = (dateItem.isEmptyData || dateItem.cost === null) ? NO_DATA_TABLE_VALUE : dateItem.cost;
  }

  return ratioCost;
}

function prepareDataForOverviewTable(costAnalyticsList, filterSettings) {
  let tableData = [];
  if (_isEmpty(costAnalyticsList) || _isEmpty(costAnalyticsList.items)) {
    return;
  }
  costAnalyticsList.items.sort((a, b) => compareByTotalCost(a, b, filterSettings));
  if (!_isEmpty(costAnalyticsList) && !_isEmpty(costAnalyticsList.items)) {
    costAnalyticsList.items.forEach((item, index) => {
      const cost = {
        vendor: item.vendor,
        item: item.item,
        displayItem: getDisplayItemBaseOnViewBy(item, filterSettings.viewBy),
        totalCost: getTotalCostOfAccount(item),
        monthly: item.monthly,
      };
      if (_.isEqual(filterSettings.viewBy, "serviceGroup")) {
        cost.itemId = !_.isEqual(item.itemId, "") ? item.itemId : "Undefined Group";
      }

      if (filterSettings.isCompare) {
        //[OIO-13311] CA Compare개선
        cost.totalCost = `${getRatioBetweenCostAndCompareCost(item.totalCost, item.compareTotalCost)}%`;
        cost.color = COST_ANALYTICS_RANGE_COLOR[index + 1]
      }
      item.dailyCosts.forEach(dateItem => {
        cost[`${dateItem.date}`] = getRatioCostBaseOnDateItem(dateItem, item, filterSettings);
      });

      tableData.push(cost);
    });
  }

  return tableData;
}

function getTotalCostOfDaily(dataItems, tableCostValue, costAnalyticsList, filterSettings) {
  const totalCostOfDaily = {
    item : 'Total',
    totalCost: 0,
  };

  if (_isEmpty(tableCostValue)) {
    return totalCostOfDaily;
  }
  Object.keys(tableCostValue[0]).forEach(column => {
    if(['vendor', 'item', 'totalCost'].includes(column)) {
      return;
    }
    totalCostOfDaily[column] = 0;
    let totalCost = 0;
    let totalCompareCost = 0;
    let isEmptyCostData = true;
    let isEmptyCompareCostData = true;
    costAnalyticsList.items.forEach(item => {
      if (!dataItems || (dataItems && dataItems.includes(item.item))) {
        const findItem = item.dailyCosts.find( i => i.date === column);
        if (_get(findItem, 'isEmptyDate') !== true) {
          isEmptyCostData = false;
          let rawTotal = findItem && findItem.cost || 0;
          totalCost += rawTotal;
        }
        if (findItem && findItem.compareCost !== null) {
          isEmptyCompareCostData = false;
          let rawCompareTotal = findItem && findItem.compareCost || 0;
          totalCompareCost += rawCompareTotal;
        }
      }
    });
    if (filterSettings.isCompare) {
      let compareCost = totalCompareCost;
      if (filterSettings.compareCostType && filterSettings.compareCostType === COMPARE_COST_TYPE.AVERAGE) {
        compareCost = costAnalyticsList.averageCompareCost;
      }
      if (isEmptyCostData) {
        totalCostOfDaily[column] = isEmptyCompareCostData ? NO_DATA_TABLE_VALUE : compareCost;
      } else {
        //[OIO-13311] CA Compare개선
        //totalCostOfDaily[column] = isEmptyCompareCostData ? totalCost : `${getRatioBetweenCostAndCompareCost(totalCost, compareCost)}%`;
        totalCostOfDaily[column] = `${getRatioBetweenCostAndCompareCost(totalCost, compareCost)}%`;
      }
    } else {
      totalCostOfDaily[column] = isEmptyCostData ? NO_DATA_TABLE_VALUE : totalCost;
    }
    totalCostOfDaily.totalCost += totalCost;
  });

  if (filterSettings.isCompare) {
    let totalCostWithInCommonTimeFrame = 0;
    let compareTotalCostInCommonTimeFrame = 0;

    costAnalyticsList.items.forEach(item => {
      if (!dataItems || (dataItems && dataItems.includes(item.item))) {
        //[OIO-13311] CA Compare개선
        totalCostWithInCommonTimeFrame += item.totalCost;
        compareTotalCostInCommonTimeFrame += item.compareTotalCost;
      }
    });
    totalCostOfDaily.totalCost = `${getRatioBetweenCostAndCompareCost(totalCostWithInCommonTimeFrame, compareTotalCostInCommonTimeFrame)}%`

    let range = costAnalyticsList.dateRange
    range.forEach(dt => {
      if((totalCostOfDaily[dt]+'').indexOf('%') == -1){
        totalCostOfDaily[dt] = '-100%';
      }
    })
  }

  return totalCostOfDaily;
}

function prepareColorForCostByStackedChart(rawData) {
  let results = [];
  if(_isEmpty(rawData)) {
    return results;
  }
  for (let i = 0; i < rawData.length; i++) {
    results.push(COST_ANALYTICS_RANGE_COLOR[i + 1])
  }
  return results;
}

function getMappingAccountWithColors(costTrendSourceDataKeys) {
  let mappingAccountWithColors = {};

  for (let i = 0; i < costTrendSourceDataKeys.length; i++) {
    let color = COST_ANALYTICS_RANGE_COLOR[i + 1]
    mappingAccountWithColors[costTrendSourceDataKeys[i]] = color;
  }
  return mappingAccountWithColors
}

function formatCostToNumber(cost) {
  return Number(formatCost(cost, {thousandSeparated: false}));
}

function compareByTotalCost(vendor1, vendor2, filterSettings) {
  if (filterSettings && filterSettings.compare) {
    let formattedValueA = typeof vendor1.ratioTotalCost === 'string' ? vendor1.ratioTotalCost.replace('-', '') : vendor1.ratioTotalCost;
    let formattedValueB = typeof vendor2.ratioTotalCost === 'string' ? vendor2.ratioTotalCost.replace('-', '') : vendor2.ratioTotalCost;
    return formatCostToNumber(formattedValueB) - formatCostToNumber(formattedValueA);
  }
  return parseFloat(getTotalCostOfAccount(vendor2)) - parseFloat(getTotalCostOfAccount(vendor1));
}

function getTotalCostOfAccount(accountItem) {
  let totalCostOfAccount = 0;
  accountItem.dailyCosts.forEach(dateItem => {
    totalCostOfAccount += dateItem.cost;
  });

  return totalCostOfAccount;
}

/**
 * Loop all costs and then mapping keys with label
 *  Return something like
 *  {
 *    'AWS_11111111': 'AWS 1111111 (Alias 1)'
 *    'AWS_22222222': 'AWS 22222222 (Alias 2)'
 *  }
 * @param costs
 * @param viewBy
 * @return {}
 */
function mappingKeysWithLabelForTrend(costs, viewBy, compareLabel) {
  if (_isEmpty(costs)) {
    return {};
  }

  let mappingKeysWithLabel = {};
  costs.forEach(cost => {
    mappingKeysWithLabel[getAccountKeyByVendor(cost)] = getDisplayItemWithVendorBaseOnViewBy(cost, viewBy);
    mappingKeysWithLabel[getCompareAccountKeyByVendor(cost)] = getDisplayCompareItemWithVendorBaseOnViewBy(cost, viewBy, compareLabel);
  });

  return mappingKeysWithLabel;
}

/**
 * Get compare account key by vendor
 * @param accountVendor {vendor, item}
 */
function getCompareAccountKeyByVendor(accountVendor) {
  return `${COMPARE_LABEL}-${getAccountKeyByVendor(accountVendor)}`;
}

/**
 * Get account key by vendor
 * @param accountVendor {vendor, item}
 */
function getAccountKeyByVendor(accountVendor) {
  return `${removeAllSpecialCharacters(accountVendor.vendor, '_')}-${removeAllSpecialCharacters(accountVendor.item, '_')}`;
}

/**
 * Get ratio account key by vendor
 * @param accountVendor {vendor, item}
 */
function getRatioAccountKey(accountVendor) {
  return `${PREFIX_COMPARE.RATIO}-${getAccountKeyByVendor(accountVendor)}`;
}

/**
 * Get ratio between cost and compare cost
 *
 * - cost && compareCost > 0 -> (cost - compareCost) / compareCost * 100
 * - cost = compareCost = 0 -> 0%
 * - cost = 0 && compareCost > 0-> -100%
 * - cost > 0 && compareCost = 0 -> 100%
 *
 * If ratio < 0.05 -> 0%, ratio >= 0.05 -> 0.1%
 *
 * @param cost
 * @param compareCost
 * @returns {number}
 */
function getRatioBetweenCostAndCompareCost(cost, compareCost) {
  let ratio = getOriginalRatioBetweenCostAndCompareCost(cost, compareCost);

  return Math.abs(ratio) < 0.05 ? 0 : numbro(ratio).format({
    thousandSeparated: true,
    mantissa: 2
  });
}

function getOriginalRatioBetweenCostAndCompareCost(cost, compareCost) {
  let ratio = 0;

  const roundCost = Math.round(cost * 100.00) / 100.00;
  const roundCompareCost = Math.round(compareCost * 100.00) / 100.00;
  // const roundCost = cost
  // const roundCompareCost = compareCost
  if (roundCompareCost > 0 && roundCost > 0) {
    ratio = (roundCost - roundCompareCost) / roundCompareCost * 100;
  } else if (roundCost === 0 && roundCompareCost === 0) {
    ratio = 0;
  } else if (roundCost === 0 && roundCompareCost > 0) {
    ratio = -100;
  } else if (roundCost > 0 && roundCompareCost === 0) {
    ratio = 100;
  }

  return ratio;
  // return Math.round(ratio * 100) / 100
}

/**
 * Instead of Math.abs function
 * Because of in case formattedRatioCost = 2,070.5 -> Math.abs(formattedRatioCost) = NaN
 *
 * @param formattedRatioCost
 * @returns {*|number}
 */
function getAbsForFormattedRatioCost(formattedRatioCost) {
  return formattedRatioCost && formattedRatioCost.replace('-', '') || 0;
}

function highlightColumnsByDateOnCAOverviewTable(rowColIds) {
  rowColIds.forEach(function(rowColId) {
    const agCellsByCol = document.querySelectorAll(".ag-cell-value[col-id='" + rowColId + "']");
    highlightForAgColCells(agCellsByCol);
  });
}

function fadeOutAllPinnedRowsInCAOverviewTable() {
  const agCellValues = document.querySelectorAll('.cost-analytics-single-table-inner .ag-floating-bottom .ag-cell-value');
  if (!agCellValues) {
    return;
  }

  for (let k = 0; k < agCellValues.length; k++) {
    if (!agCellValues[k].classList.contains('row-bg-opacity')) {
      // eslint-disable-next-line no-param-reassign
      agCellValues[k].className += " row-bg-opacity";
    }
  }
}

function highlightForAgCells(agCells) {
  for (let k = 0; k < agCells.length; k++) {
    if (agCells[k].classList.contains('row-bg-opacity')) {
      agCells[k].classList.remove("row-bg-opacity");
    }
    if (agCells[k].classList.contains('col-bg-active')) {
      agCells[k].classList.remove("col-bg-active");
    }
  }
}

function highlightForAgColCells(agCells) {
  for (let k = 0; k < agCells.length; k++) {
    if (!agCells[k].classList.contains('col-bg-active')) {
      // eslint-disable-next-line no-param-reassign
      agCells[k].className += " col-bg-active";
    }
  }
}

function resetHighlightOnCAOverviewTable() {
  const agCellValues = document.getElementsByClassName('ag-cell-value');
  if (!agCellValues) {
    return;
  }

  highlightForAgCells(agCellValues);
}

function getPortionOtherItems(rawData) {
  if (!rawData) {
    return []
  }
  return rawData.map(data => {
    return data.item
  })
}

// trim [] (empty array) and flatten tags
function standardizeFilterConditionsForRequest(conditions) {
  if (_isNil(conditions)) {
    return {};
  }
  let result = {};
  Object.keys(conditions).forEach(field => {
    if (_isEmpty(conditions[field])) {
      return;
    }
    result[field] = getFilterConditionOfFieldForRequest(conditions, field);
  });
  return result;
}

function getFilterConditionOfFieldForRequest(conditions, field) {
  let result;
  switch (field) {
    case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
      //result = conditions[field];
      result = [];
      Object.keys(conditions[field]).forEach(svgKey => {
        let svgKeyList = svgKey.split(':::')
        if (_isEmpty(conditions[field][svgKey])) {
          return;
        }
        const svgsBySvgKey = conditions[field][svgKey].map(svgValue => {
          let svgValueList = svgValue.split(':::')
          return {
            serviceGroupKey: svgKey,
            serviceGroupValue: svgValue,
            serviceGroupSetNm: convertFilterTextNm(svgKey,field),
            serviceGroupSetId: convertFilterTextId(svgKey,field),
            serviceGroupNm: convertFilterTextNm(svgValue,field),
            serviceGroupId: convertFilterTextId(svgValue,field),
          };
        });
        result.push(...svgsBySvgKey);
      });
      break;
    case FILTER_REQUEST_FIELD_BY_FIELD.tags:
      result = [];
      Object.keys(conditions[field]).forEach(tagKey => {
        if (_isEmpty(conditions[field][tagKey])) {
          return;
        }
        const tagsByTagKey = conditions[field][tagKey].map(tagValue => {
          return {
            tagKey: tagKey,
            tagValue: tagValue
          };
        });
        result.push(...tagsByTagKey);
      });
      break;
    default:
      result = conditions[field];
  }
  return result;
}

function standardizeUserFilterForSave(userFilter, isSaveAs) {
  return {
    ...userFilter,
    conditions: {
      ...userFilter.conditions,
      ...buildFilterParameters(userFilter)
    },
    filterName: _trim(userFilter.filterName),
    index: isSaveAs ? -1 : userFilter.index
  }
}

function buildFilterParameters(currentFilter) {
  return {
    ali: standardizeFilterConditionsForRequest(currentFilter.conditions.ali),
    aws: standardizeFilterConditionsForRequest(currentFilter.conditions.aws),
    azure: standardizeFilterConditionsForRequest(currentFilter.conditions.azure),
    gcp: standardizeFilterConditionsForRequest(currentFilter.conditions.gcp)
  };
}

function getItemInfoFromCombineDailyCostsByCostDate(combineDailyCosts, clickedDate) {
  const findItem = combineDailyCosts && combineDailyCosts.find(combineDailyCost => combineDailyCost.date === clickedDate);
  let costDate = findItem.isEmptyDate === true ? null : findItem.date;
  let compareCostDate = findItem && findItem.compareDate || null;

  return {
    costDate: costDate,
    compareCostDate: compareCostDate,
  }
}

function getTagKeyToTagValuesMap(tags) {
  if (_isEmpty(tags)) {
    return {};
  }
  let result = {};
  tags.forEach(tag => {
    if (_isNil(result[tag.tagKey])) {
      result[tag.tagKey] = [];
    }
    if(!_isNil(tag.tagValue)){
      result[tag.tagKey].push(tag.tagValue)
    }
  });
  // Object.keys(result).forEach(tagKey => {
  //   if(!result[tagKey].includes('NON-TAG')) {
  //     result[tagKey].push('NON-TAG')
  //   }
  // })
  return result;
}


function getServiceGroupKeyToServiceGroupKeyValuesMap(serviceGroups) {
  if (_isEmpty(serviceGroups)) {
    return {};
  }
  let result = {};
  serviceGroups.forEach(serviceGroup => {
    if (_isNil(result[serviceGroup.serviceGroupKey])) {
      result[serviceGroup.serviceGroupKey] = [];
    }
    if(!_isNil(serviceGroup.serviceGroupValue)){
      result[serviceGroup.serviceGroupKey].push(serviceGroup.serviceGroupValue);
    }
  });
  // Object.keys(result).forEach(serviceGroupKey => {
  //   if(!result[serviceGroupKey].includes('Undefined Group:::Undefined Group')) {
  //     result[serviceGroupKey].push('Undefined Group:::Undefined Group')
  //   }
  // })
  return result;
}


function countRelatedFilterOptions(filterOptions) {
  let count = 0;
  filterOptions.forEach(opt => {
    if (opt.related) {
      count++;
    }
  });
  return count;
}

function sortFilterOptions(filterOptions, sortType) {
  const sortTypeFactor = _isNil(sortType) || sortType === SORT_TYPE.ASC ? 1 : -1;
  filterOptions.sort((a, b) => {
    let aLowerCaseRelated = _toLower(_get(a, 'related'));
    let bLowerCaseRelated = _toLower(_get(b, 'related'));
    if(aLowerCaseRelated==='true' && bLowerCaseRelated==='false'){
      return -1 * sortTypeFactor
    }
    if(aLowerCaseRelated==='false' && bLowerCaseRelated==='true'){
      return sortTypeFactor
    }
    let aLowerCaseText = _toLower(_get(a, 'text'));
    let bLowerCaseText = _toLower(_get(b, 'text'));
    if (aLowerCaseText < bLowerCaseText) {
      return -1 * sortTypeFactor
    }
    if (aLowerCaseText > bLowerCaseText) {
      return sortTypeFactor
    }
    return 0
  })
}

function getOptionText(optValue, field, $vm, defaultOptText) {
  if (optValue === SPECIAL_FILTER_OPTION.NO_VALUE) {
    const FILTER_MODEL = MAIN_FILTER_FIELDS.includes(field) ? MAIN_FILTER_MODEL : ADDITIONAL_FILTER_MODEL;
    return `${$vm.$t('costAnalytics.optionText.noValue', {field: $vm.$t(FILTER_MODEL[field].text)})}`;
  }
  return _isNil(defaultOptText) ? optValue : defaultOptText;
}

function getTreeFilterOptionText(dataKey, dataValue, $vm, defaultOptText) {
  if (dataValue === SPECIAL_FILTER_OPTION.NO_VALUE) {
    return `${$vm.$t('costAnalytics.optionText.noValue', {field: dataKey})}`;
  }
  return _isNil(defaultOptText) ? dataValue : defaultOptText;
}

function getDateRangePresets($vm) {
  return {
    month_to_date: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.thisMonthMtd'),
      startDate: $vm.$dayjs.utc().startOf('month').startOf('day'),
      endDate: $vm.$dayjs.utc().startOf('day'),
    },
    last_month: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.lastMonth'),
      startDate: $vm.$dayjs.utc().subtract(1, 'month').startOf('month').startOf('day'),
      endDate: $vm.$dayjs.utc().subtract(1, 'month').endOf('month').startOf('day')
    },
    last_7_days: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.last7Days'),
      startDate: $vm.$dayjs.utc().subtract(6, 'day').startOf('day'),
      endDate: $vm.$dayjs.utc().subtract(0, 'day').startOf('day'),
    },
    last_14_days: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.last14Days'),
      startDate: $vm.$dayjs.utc().subtract(13, 'day').startOf('day'),
      endDate: $vm.$dayjs.utc().subtract(0, 'day').startOf('day')
    },
    last_60_days: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.last60Days'),
      startDate: $vm.$dayjs.utc().subtract(59, 'day').startOf('day'),
      endDate: $vm.$dayjs.utc().subtract(0, 'day').startOf('day')
    },
    last_3_months: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.last3Months'),
      startDate: $vm.$dayjs.utc().subtract(2, 'month').startOf('month').startOf('day'),
      endDate: $vm.$dayjs.utc().startOf('day')
    },
    last_6_months: {
      label: $vm.$t('costAnalytics.header.timeFrameOptions.last6Months'),
      startDate: $vm.$dayjs.utc().subtract(5, 'month').startOf('month').startOf('day'),
      endDate: $vm.$dayjs.utc().startOf('day')
    },
    //TODO (2020.11.06) 릴리즈 이후 다시 복구예정
    // last_12_months: {
    //   label: $vm.$t('costAnalytics.header.timeFrameOptions.last12Months'),
    //   startDate: $vm.$dayjs.utc().subtract(11, 'month').startOf('month').startOf('day'),
    //   endDate: $vm.$dayjs.utc().startOf('day')
    // },
    // year_to_date: {
    //   label: $vm.$t('costAnalytics.header.timeFrameOptions.thisYearYtd'),
    //   startDate: $vm.$dayjs.utc().startOf('year').startOf('day'),
    //   endDate: $vm.$dayjs.utc().startOf('day')
    // },
  }
}

function getFilterSettings() {
  const today = new Date();
  const currentYear = today.getFullYear();
  const currentMonth = add0StringToNumberLessThan10(today.getMonth() + 1);
  const previousMonth = currentMonth === '01' ? add0StringToNumberLessThan10(12): add0StringToNumberLessThan10(today.getMonth());
  const previousYear = previousMonth === '12' ? (today.getFullYear() - 1) : today.getFullYear();
  return  {
    timeFrame: COST_ANALYTICS_TIME_FRAME.MONTH_TO_DATE,
    startDate: `${currentYear}-${currentMonth}-01`,
    endDate: getCurrentDateWithCurrentTimezone('YYYY-MM-DD'),
    compareTimeFrame: COST_ANALYTICS_COMPARE.LAST_PERIOD,
    compareStartDate: `${previousYear}-${previousMonth}-01`,
    compareEndDate: `${previousYear}-${previousMonth}-31`,
    compareCostType: COMPARE_COST_TYPE.INDIVIDUAL_COST,
    viewBy: 'account',
    isCompare: false,
    isCustomTimeFrame: false,
    isCompareCustom: false,
  }
}

function compareByCost(portion1, portion2) {
  return parseFloat(portion2.cost) - parseFloat(portion1.cost);
}

function portionDetailItemComparator(item1, item2) {
  return item1.item === item2.item;
}

function mockDataForComparePortionDetail(rawCompareData, rawCurrentData) {
  let currentData = _cloneDeep(rawCurrentData);
  let compareData = _cloneDeep(rawCompareData);

  let combineResponse = {};
  let useFulCompareData = {
    accounts: [],
    products: [],
    regions: [],
    usageTypes: []
  };

  let compareAccountMap = {};
  let compareProductMap = {};
  let compareRegionMap = {};
  let compareUsageTypeMap = {};

  let compareTotal = 0;

  let sumCompareAccountsNoOther = 0;
  let sumCompareProductsNoOther = 0;
  let sumCompareRegionsNoOther = 0;
  let sumCompareUsageTypesNoOther = 0;

  let compareAccountOther = null;
  let compareProductOther = null;
  let compareRegionOther = null;
  let compareUsageTypeOther = null;

  let currentAccountOther = null;
  let currentProductOther = null;
  let currentRegionOther = null;
  let currentUsageTypeOther = null;

  let currentAccountMap = {};
  let currentProductMap = {};
  let currentRegionMap = {};
  let currentUsageTypeMap = {};

  let currentTotalAccount = 0;
  let currentTotalProduct = 0;
  let currentTotalRegion = 0;
  let currentTotalUsageType = 0;

  compareData.accounts.forEach(item => {
    if (item.isOthers) {
      compareAccountOther = item;
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        compareAccountMap[otherItem.item] = otherItem;
      })
    } else {
      compareAccountMap[`${item.item} (${item.itemAlias})`] = item;
    }
    compareTotal += item.cost;
  });

  compareData.products.forEach(item => {
    if (item.isOthers) {
      compareProductOther = item;
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        compareProductMap[otherItem.item] = otherItem;
      })
    } else {
      compareProductMap[item.item] = item;
    }
  });

  compareData.regions.forEach(item => {
    if (item.isOthers) {
      compareRegionOther = item;
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        compareRegionMap[otherItem.item] = otherItem;
      })
    } else {
      compareRegionMap[item.item] = item;
    }
  });

  compareData.usageTypes.forEach(item => {
    if (item.isOthers) {
      compareUsageTypeOther = item;
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        compareUsageTypeMap[otherItem.item] = otherItem
      })
    } else {
      compareUsageTypeMap[item.item] = item;
    }
  });

  currentData.accounts.forEach(item => {
    if (item.isOthers) {
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        currentAccountMap[otherItem.item] = otherItem;
      })
    } else {
      currentAccountMap[`${item.item} (${item.itemAlias})`] = item;
    }
    currentTotalAccount += item.cost;
  });

  currentData.products.forEach(item => {
    if (item.isOthers) {
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        currentProductMap[otherItem.item] = otherItem;
      })
    } else {
      currentProductMap[item.item] = item;
    }
    currentTotalProduct += item.cost;
  });

  currentData.regions.forEach(item => {
    if (item.isOthers) {
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        currentRegionMap[otherItem.item] = otherItem;
      })
    } else {
      currentRegionMap[item.item] = item;
    }
    currentTotalRegion += item.cost;
  });

  currentData.usageTypes.forEach(item => {
    if (item.isOthers) {
      item.otherItems.forEach(otherItem => {
        // eslint-disable-next-line no-param-reassign
        otherItem.relationsForAws = item.relationsForAws;
        currentUsageTypeMap[otherItem.item] = otherItem
      })
    } else {
      currentUsageTypeMap[item.item] = item;
    }
    currentTotalUsageType += item.cost;
  });

  let step2CurrentData = {
    accounts: [],
    products: [],
    regions: [],
    usageTypes: []
  };

  let step2BufferCurrentData = {
    accounts: [],
    products: [],
    regions: [],
    usageTypes: []
  };

  let step2CompareData = {
    accounts: [],
    products: [],
    regions: [],
    usageTypes: []
  };

  let step2BufferCompareData = {
    accounts: [],
    products: [],
    regions: [],
    usageTypes: []
  };

  let accountSet = new Set();
  let productSet = new Set();
  let regionSet = new Set();
  let usageTypeSet = new Set();

  Object.keys(currentAccountMap).forEach(key => {
    accountSet.add(key)
  });
  Object.keys(compareAccountMap).forEach(key => {
    accountSet.add(key)
  });
  Object.keys(currentProductMap).forEach(key => {
    productSet.add(key)
  });
  Object.keys(compareProductMap).forEach(key => {
    productSet.add(key)
  });
  Object.keys(currentRegionMap).forEach(key => {
    regionSet.add(key)
  });
  Object.keys(compareRegionMap).forEach(key => {
    regionSet.add(key)
  });
  Object.keys(currentUsageTypeMap).forEach(key => {
    usageTypeSet.add(key)
  });
  Object.keys(compareUsageTypeMap).forEach(key => {
    usageTypeSet.add(key)
  });
  //init unique key
  let uniqueAccountKey = Array.from(accountSet);
  let uniqueProductKey = Array.from(productSet);
  let uniqueRegionKey = Array.from(regionSet);
  let uniqueUsageTypeKey = Array.from(usageTypeSet);

  uniqueProductKey.forEach(key => {
    if (currentProductMap[key]) {
      step2CurrentData.products.push(currentProductMap[key])
    } else {
      let bufferProduct = _cloneDeep(compareProductMap[key])
      bufferProduct.cost = 0;
      bufferProduct.portion = 0;
      step2BufferCurrentData.products.push(bufferProduct)
    }

    if (compareProductMap[key]) {
      step2CompareData.products.push(compareProductMap[key])
    } else {
      let bufferProduct = _cloneDeep(currentProductMap[key])
      bufferProduct.cost = 0;
      bufferProduct.portion = 0;
      step2BufferCompareData.products.push(bufferProduct)
    }
  });

  uniqueRegionKey.forEach(key => {
    if (currentRegionMap[key]) {
      step2CurrentData.regions.push(currentRegionMap[key])
    } else {
      let bufferRegion = _cloneDeep(compareRegionMap[key])
      bufferRegion.cost = 0;
      bufferRegion.portion = 0;
      step2BufferCurrentData.regions.push(bufferRegion)
    }

    if (compareRegionMap[key]) {
      step2CompareData.regions.push(compareRegionMap[key])
    } else {
      let bufferRegion = _cloneDeep(currentRegionMap[key])
      bufferRegion.cost = 0;
      bufferRegion.portion = 0;
      step2BufferCompareData.regions.push(bufferRegion)
    }
  });

  uniqueUsageTypeKey.forEach(key => {
    if (currentUsageTypeMap[key]) {
      step2CurrentData.usageTypes.push(currentUsageTypeMap[key])
    } else {
      let bufferUsageType = _cloneDeep(compareUsageTypeMap[key])
      bufferUsageType.cost = 0;
      bufferUsageType.portion = 0;
      step2BufferCurrentData.usageTypes.push(bufferUsageType)
    }

    if (compareUsageTypeMap[key]) {
      step2CompareData.usageTypes.push(compareUsageTypeMap[key])
    } else {
      let bufferUsageType = _cloneDeep(currentUsageTypeMap[key])
      bufferUsageType.cost = 0;
      bufferUsageType.portion = 0;
      step2BufferCompareData.usageTypes.push(bufferUsageType)
    }
  });

  uniqueAccountKey.forEach(key => {
    if (currentAccountMap[key]) {
      step2CurrentData.accounts.push(currentAccountMap[key])
    } else {
      let bufferAcc = _cloneDeep(compareAccountMap[key])
      bufferAcc.cost = 0;
      bufferAcc.portion = 0;
      step2BufferCurrentData.accounts.push(bufferAcc)
    }
    if (compareAccountMap[key]) {
      step2CompareData.accounts.push(compareAccountMap[key])
    } else {
      let bufferAcc = _cloneDeep(currentAccountMap[key])
      bufferAcc.cost = 0;
      bufferAcc.portion = 0;
      step2BufferCompareData.accounts.push(bufferAcc)
    }
  });

  //sort it by cost first
  step2CurrentData.accounts.sort(compareByCost);
  step2CurrentData.products.sort(compareByCost);
  step2CurrentData.regions.sort(compareByCost);
  step2CurrentData.usageTypes.sort(compareByCost);

  step2BufferCurrentData.accounts.sort(compareByCost);
  step2BufferCurrentData.products.sort(compareByCost);
  step2BufferCurrentData.regions.sort(compareByCost);
  step2BufferCurrentData.usageTypes.sort(compareByCost);

  //merge current data and bufferData
  let currentGroupedData = {
    accounts: [],
    products: [],
    regions: [],
    usageTypes: []
  };

  let combineCompareAccount = step2CompareData.accounts.concat(step2BufferCompareData.accounts);
  let combineCompareProduct = step2CompareData.products.concat(step2BufferCompareData.products);
  let combineCompareRegion = step2CompareData.regions.concat(step2BufferCompareData.regions);
  let combineCompareUsageType = step2CompareData.usageTypes.concat(step2BufferCompareData.usageTypes);

  let combineAccount =  step2CurrentData.accounts.concat(step2BufferCurrentData.accounts);
  let combineProduct =  step2CurrentData.products.concat(step2BufferCurrentData.products);
  let combineRegion =  step2CurrentData.regions.concat(step2BufferCurrentData.regions);
  let combineUsageType =  step2CurrentData.usageTypes.concat(step2BufferCurrentData.usageTypes);

  //combine compareData
  let combineCompareAccountMap = {};
  let combineCompareProductMap = {};
  let combineCompareRegionMap = {};
  let combineCompareUsageTypeMap = {};

  combineCompareAccount.forEach(item => {
    combineCompareAccountMap[item.item] = item
  });

  combineCompareProduct.forEach(item => {
    combineCompareProductMap[item.item] = item
  });

  combineCompareRegion.forEach(item => {
    combineCompareRegionMap[item.item] = item
  });

  combineCompareUsageType.forEach(item => {
    combineCompareUsageTypeMap[item.item] = item
  });

  for(let index = 0; index < combineAccount.length; index ++) {
    let account = combineAccount[index];
    if(index < MAX_NUMBER_OF_PORTION - 1) {
      currentGroupedData.accounts.push(account);
    } else {
      let isCreateNew = _isNil(currentAccountOther);
      if(isCreateNew) {
        currentAccountOther = {
          cost: account.cost,
          numberOfOthers: 1,
          relationsForAws: {},
          item: "others",
          isOthers: true,
          portion: 0,
          otherItems: []
        };
      } else {
        currentAccountOther.cost = currentAccountOther.cost + account.cost;
        currentAccountOther.numberOfOthers ++;
      }
      currentAccountOther.otherItems.push(account)
      currentAccountOther.relationsForAws = mergeRelationship(currentAccountOther.relationsForAws, account.relationsForAws);
    }
  }

  for(let index = 0; index < combineProduct.length; index ++) {
    let product = combineProduct[index];
    if(index < MAX_NUMBER_OF_PORTION - 1) {
      currentGroupedData.products.push(product);
    } else {
      let isCreateNew = _isNil(currentProductOther);
      if(isCreateNew) {
        currentProductOther = {
          cost: product.cost,
          numberOfOthers: 1,
          relationsForAws: {},
          item: "others",
          isOthers: true,
          portion: 0,
          otherItems: []
        };
      } else {
        currentProductOther.cost = currentProductOther.cost + product.cost;
        currentProductOther.numberOfOthers ++;
      }
      currentProductOther.otherItems.push(product)
      currentProductOther.relationsForAws = mergeRelationship(currentProductOther.relationsForAws, product.relationsForAws);
    }
  }

  for(let index = 0; index < combineRegion.length; index ++) {
    let region = combineRegion[index];
    if(index < MAX_NUMBER_OF_PORTION - 1) {
      currentGroupedData.regions.push(region);
    } else {
      let isCreateNew = _isNil(currentRegionOther);
      if(isCreateNew) {
        currentRegionOther = {
          cost: region.cost,
          numberOfOthers: 1,
          relationsForAws: {},
          item: "others",
          isOthers: true,
          portion: 0,
          otherItems: []
        };
      } else {
        currentRegionOther.cost = currentRegionOther.cost + region.cost;
        currentRegionOther.numberOfOthers ++;
      }
      currentRegionOther.otherItems.push(region)
      currentRegionOther.relationsForAws = mergeRelationship(currentRegionOther.relationsForAws, region.relationsForAws);
    }
  }

  for(let index = 0; index < combineUsageType.length; index ++) {
    let usageType = combineUsageType[index];
    if(index < MAX_NUMBER_OF_PORTION - 1) {
      currentGroupedData.usageTypes.push(usageType);
    } else {
      let isCreateNew = _isNil(currentUsageTypeOther);
      if(isCreateNew) {
        currentUsageTypeOther = {
          cost: usageType.cost,
          numberOfOthers: 1,
          relationsForAws: {},
          item: "others",
          isOthers: true,
          portion: 0,
          otherItems: []
        };
      } else {
        currentUsageTypeOther.cost = currentUsageTypeOther.cost + usageType.cost;
        currentUsageTypeOther.numberOfOthers ++;
      }
      currentUsageTypeOther.otherItems.push(usageType)
      currentUsageTypeOther.relationsForAws = mergeRelationship(currentUsageTypeOther.relationsForAws, usageType.relationsForAws);
    }
  }
  //re-calculate portion of other item.

  if (currentAccountOther) {
    currentAccountOther.portion = currentAccountOther.cost ? (currentAccountOther.cost / currentTotalAccount) * 100 : 0;
    currentGroupedData.accounts.push(currentAccountOther);
  }
  if (currentProductOther) {
    currentProductOther.portion = currentProductOther.cost ? (currentProductOther.cost / currentTotalProduct) * 100 : 0;
    currentGroupedData.products.push(currentProductOther);
  }
  if (currentRegionOther) {
    currentRegionOther.portion = currentRegionOther.cost ? (currentRegionOther.cost / currentTotalRegion) * 100 : 0;
    currentGroupedData.regions.push(currentRegionOther);
  }
  if (currentUsageTypeOther) {
    currentUsageTypeOther.portion = currentUsageTypeOther.cost ? (currentUsageTypeOther.cost / currentTotalUsageType) * 100 : 0;
    currentGroupedData.usageTypes.push(currentUsageTypeOther);
  }

  //buffer compare data base on current data
  currentGroupedData.accounts.forEach(item => {
    if (item.isOthers) {
      let compareOther = _cloneDeep(currentAccountOther);
      useFulCompareData.accounts.push(compareOther)
    } else {
      useFulCompareData.accounts.push(combineCompareAccountMap[item.item])
    }
  })

  currentGroupedData.products.forEach(item => {
    if (item.isOthers) {
      let compareOther = _cloneDeep(currentProductOther);
      useFulCompareData.products.push(compareOther)
    } else {
      useFulCompareData.products.push(combineCompareProductMap[item.item])
    }
  });

  currentGroupedData.regions.forEach(item => {
    if (item.isOthers) {
      let compareOther = _cloneDeep(currentRegionOther);
      useFulCompareData.regions.push(compareOther)
    } else {
      useFulCompareData.regions.push(combineCompareRegionMap[item.item])
    }
  });

  currentGroupedData.usageTypes.forEach(item => {
    if (item.isOthers) {
      let compareOther = _cloneDeep(currentUsageTypeOther);
      useFulCompareData.usageTypes.push(compareOther)
    } else {
      useFulCompareData.usageTypes.push(combineCompareUsageTypeMap[item.item])
    }
  });

  //get total cost no other after group
  useFulCompareData.accounts.forEach(item => {
    if(!item.isOthers) {
      sumCompareAccountsNoOther += item.cost;
    }
  });

  useFulCompareData.products.forEach(item => {
    if(!item.isOthers) {
      sumCompareProductsNoOther += item.cost;
    }
  });


  useFulCompareData.regions.forEach(item => {
    if(!item.isOthers) {
      sumCompareRegionsNoOther += item.cost;
    }
  });


  useFulCompareData.usageTypes.forEach(item => {
    if(!item.isOthers) {
      sumCompareUsageTypesNoOther += item.cost;
    }
  });

  useFulCompareData.accounts.forEach(item => {
    if (item.isOthers) {
      // eslint-disable-next-line no-param-reassign
      item.cost = compareTotal - sumCompareAccountsNoOther;
      // eslint-disable-next-line no-param-reassign
      item.portion = (item.cost / compareTotal) * 100;
    }
  });

  useFulCompareData.products.forEach(item => {
    if (item.isOthers) {
      // eslint-disable-next-line no-param-reassign
      item.cost = compareTotal - sumCompareProductsNoOther;
      // eslint-disable-next-line no-param-reassign
      item.portion = (item.cost / compareTotal) * 100;
    }
  });

  useFulCompareData.regions.forEach(item => {
    if (item.isOthers) {
      // eslint-disable-next-line no-param-reassign
      item.cost = compareTotal - sumCompareRegionsNoOther;
      // eslint-disable-next-line no-param-reassign
      item.portion = (item.cost / compareTotal) * 100;
    }
  });

  useFulCompareData.usageTypes.forEach(item => {
    if (item.isOthers) {
      // eslint-disable-next-line no-param-reassign
      item.cost = compareTotal - sumCompareUsageTypesNoOther;
      // eslint-disable-next-line no-param-reassign
      item.portion = (item.cost / compareTotal) * 100;
    }
  });

  combineResponse.compareData = useFulCompareData;
  combineResponse.currentData = currentGroupedData
  return combineResponse;
}

function getItemKeyBaseOnDisplayedItems(item, displayedItems) {
  let itemKey = '';
  displayedItems.forEach(displayedItem => {
    if (!_isEmpty(item[displayedItem]) && item[displayedItem] && item[displayedItem].trim()) {
      itemKey += item[displayedItem].trim();
    }
  });

  return itemKey;
}

function mergeRelationship(totalRelation, relationObject) {
  if (_isEmpty(totalRelation)) {
    // eslint-disable-next-line no-param-reassign
    totalRelation.accounts = [];
    // eslint-disable-next-line no-param-reassign
    totalRelation.products = [];
    // eslint-disable-next-line no-param-reassign
    totalRelation.regions = [];
    // eslint-disable-next-line no-param-reassign
    totalRelation.usageTypes = [];
  }
  let totalAccRel = _cloneDeep(totalRelation.accounts);
  let totalProRel = _cloneDeep(totalRelation.products);
  let totalRegionRel = _cloneDeep(totalRelation.regions);
  let totalUsageTypeRel = _cloneDeep(totalRelation.usageTypes);

  totalAccRel = totalAccRel.concat(relationObject.accounts);
  totalProRel = totalProRel.concat(relationObject.products);
  totalRegionRel = totalRegionRel.concat(relationObject.regions);
  totalUsageTypeRel = totalUsageTypeRel.concat(relationObject.usageTypes);

  const relationAccSet = new Set(totalAccRel);
  const relationProSet = new Set(totalProRel);
  const relationRegionSet = new Set(totalRegionRel);
  const relationUsageTypeSet = new Set(totalUsageTypeRel);

  return {
    accounts: Array.from(relationAccSet),
    products: Array.from(relationProSet),
    regions: Array.from(relationRegionSet),
    usageTypes: Array.from(relationUsageTypeSet),
  }
}

function getCombineDetailItems(uniqueDetailDataItems, uniqueCompareDetailDataItems, displayedItems) {
  let combineDetailItems = [];
  //let objectDetailItems = getObjectDetailItems(uniqueDetailDataItems, displayedItems);
  //let compareObjectDetailItems = getObjectDetailItems(uniqueCompareDetailDataItems, displayedItems);
  let mainDetailData = getDetailedDataGroupedByMultiKey(uniqueDetailDataItems, MAX_COLUMN_FIELDS_FOR_COMPARE_MODE, false)
  let compareDetailData = getDetailedDataGroupedByMultiKey(uniqueCompareDetailDataItems, MAX_COLUMN_FIELDS_FOR_COMPARE_MODE, false)
  let objectDetailItems = getObjectDetailItems(mainDetailData, displayedItems);
  let compareObjectDetailItems = getObjectDetailItems(compareDetailData, displayedItems);
  let combineDetailItemKeys = _uniq(Object.keys(objectDetailItems).concat(Object.keys(compareObjectDetailItems)));

  combineDetailItemKeys.forEach(combineDetailItemKey => {
    let combineDetailItem = {
      cost: null,
      comparedCost: null,
      usage: null,
      comparedUsage: null,
    };

    if (typeof objectDetailItems[combineDetailItemKey] !== 'undefined') {
      Object.assign(combineDetailItem, objectDetailItems[combineDetailItemKey]);
    }

    if (typeof compareObjectDetailItems[combineDetailItemKey] !== 'undefined') {
      Object.assign(combineDetailItem, compareObjectDetailItems[combineDetailItemKey], {cost: combineDetailItem.cost, usage: combineDetailItem.usage});
      combineDetailItem.comparedUsage = compareObjectDetailItems[combineDetailItemKey].usage;
      combineDetailItem.comparedCost = compareObjectDetailItems[combineDetailItemKey].cost;
    }

    combineDetailItems.push(combineDetailItem);
  });

  return combineDetailItems;
}

function getObjectDetailItems(items, displayedItems) {
  let objectDetailItems = {};
  items.forEach(item => {
    let itemKey = getItemKeyBaseOnDisplayedItems(item, displayedItems);
    objectDetailItems[itemKey] = item;
  });

  return objectDetailItems;
}

function getSelectedVendors(selectedVendors, $vm, availableVendors, isText){ //각 위젯에서 사용가능한 벤더 필터링
  let savedVendor = (!_isNil(selectedVendors) // [''],[],[null]
    &&!_isEmpty(selectedVendors[0])
    &&!_isEqual(selectedVendors[0], ''))
    ? selectedVendors[0]
    : _isEmpty(_common.allVendors()) ? '' : _common.allVendors()[0];

  if(isText){
    return _common.getDefaultVendorByCheckedAuth(savedVendor, availableVendorsOptions(availableVendors, $vm).map(vendorOption => {return vendorOption.text;}), isText);
  }
  return _common.getDefaultVendorByCheckedAuth(savedVendor, availableVendorsOptions(availableVendors, $vm).map(vendorOption => {return vendorOption.value;}), isText);
}

function availableVendorsOptions(availableVendors, $vm){ //위젯별로 가용한 벤더 리스트 조회
  return availableVendors.filter(vendor => {
    if(_isEqual($vm.profile.env, "CHINA")){
      return vendor.value !== 'GCP'
    } else return true;
  }).filter(option =>_common.allVendors().includes(option.value)).map(vendor => {
    return {
      ...vendor,
      text: $vm.$t(vendor.text)
    };
  });
}



/**
 * Input:
 * key1||key2||...||key8: usage = 10, cost = 5
 * key1||key2||...||key8: usage = 20, cost = 10
 *
 * Output:
 * key1||key2||...||key8: usage = 30, cost = 15
 *
 * @param items
 * @param displayedItems
 */
function makeItemsToUniqByKey(items, displayedItems) {
  let costItems = {};
  let results = [];

  items.forEach(item => {
    let itemKey = getItemKeyBaseOnDisplayedItems(item, displayedItems);

    if (costItems[itemKey]) {
      costItems[itemKey].cost += item.cost;
      costItems[itemKey].usage += item.usage;

      const foundIndex = results.findIndex(resultItem => getItemKeyBaseOnDisplayedItems(resultItem, displayedItems) === itemKey);
      results[foundIndex] = {
        ...item,
        cost: costItems[itemKey].cost,
        usage: costItems[itemKey].usage,
      };
    } else {
      costItems[itemKey] = {
        cost: item.cost,
        usage: item.usage,
      };

      results.push(item);
    }
  });

  return results;
}

function buildDetailCADataWithCompare(mainData, comparedData, compareCostType, displayedItems) {
  let result = {
    ...mainData,
    compareTotalCost: comparedData.totalCost || 0,
  };
  if (compareCostType === COMPARE_COST_TYPE.INDIVIDUAL_COST) {
    result.items = getCombineDetailItems(mainData.items, comparedData.items, displayedItems);
  } else {
    let avgComparedUsage = 0;
    let avgComparedCost = 0;
    if (comparedData.items && comparedData.items.length > 0) {
      let totalComparedUsage = 0;
      let totalComparedCost = 0;
      comparedData.items.forEach(item => {
        totalComparedUsage += item.usage;
        totalComparedCost += item.cost;
      });
      avgComparedUsage = totalComparedUsage / comparedData.items.length;
      avgComparedCost = totalComparedCost / comparedData.items.length;
    }
    result.items = mainData.items.map(item => {
      return {
        ...item,
        comparedUsage: avgComparedUsage,
        comparedCost: avgComparedCost,
      }
    });
  }
  return result;
}

function getPreviewDetailFieldOptions(viewDetailFieldOptions, orderedVisibleColumnFields) {
  let previewDetailFieldOptions = [];
  orderedVisibleColumnFields.forEach(function(orderedVisibleColumnField) {
    const viewDetailFieldOption = viewDetailFieldOptions.find(viewDetailFieldOption => viewDetailFieldOption.value === orderedVisibleColumnField);
    previewDetailFieldOptions.push(viewDetailFieldOption);
  });

  viewDetailFieldOptions.forEach(function(viewDetailFieldOption) {
    if (!orderedVisibleColumnFields || !orderedVisibleColumnFields.includes(viewDetailFieldOption.value)) {
      previewDetailFieldOptions.push(viewDetailFieldOption);
    }
  });

  return previewDetailFieldOptions;
}

function sortViewDetailFieldOptionsByCheckedStatus(viewDetailFieldOptions) {
  viewDetailFieldOptions.sort(function (a, b) {
    if (a.isChecked < b.isChecked) return 1;
    if (a.isChecked > b.isChecked) return -1;

    return 0;
  });

  return viewDetailFieldOptions;
}

function isConditionOfFieldEqual(newConditions, oldConditions, field) {
  if (newConditions === oldConditions) {
    return true;
  }
  if (_isNil(newConditions) || _isNil(oldConditions)) {
    return false;
  }
  switch (field) {
    case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
      return Object.keys(newConditions).every(vendor => {
        return Object.keys(newConditions[vendor][field]).every(svgKey => _isEmpty(_xor(newConditions[vendor][field][svgKey], oldConditions[vendor][field][svgKey])));
      });
    case FILTER_REQUEST_FIELD_BY_FIELD.tags:
      return Object.keys(newConditions).every(vendor => {
        return Object.keys(newConditions[vendor][field]).every(tagKey => _isEmpty(_xor(newConditions[vendor][field][tagKey], oldConditions[vendor][field][tagKey])));
      });
    default:
      return Object.keys(newConditions).every(vendor => _isEmpty(_xor(newConditions[vendor][field], oldConditions[vendor][field])));
  }
}

function getFirstNotEmptyConditionField(conditions) {
  for (let vendor in conditions) {
    for (let field in conditions[vendor]) {
      switch (field) {
        case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
          for (let svgKey in conditions[vendor][field]) {
            if (!_isEmpty(conditions[vendor][field][svgKey])) {
              return field;
            }
          }
          break;
        case FILTER_REQUEST_FIELD_BY_FIELD.tags:
          for (let tagKey in conditions[vendor][field]) {
            if (!_isEmpty(conditions[vendor][field][tagKey])) {
              return field;
            }
          }
          break;
        default:
          if (!_isEmpty(conditions[vendor][field])) {
            return field;
          }
      }
    }
  }
  return null;
}

function isAnyConditionSelected(conditions, vendor) {
  //벤더 라디오 셀렉트일 때 까지 사용
  for (let field in conditions[vendor]) {
    switch (field) {
      case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
        for (let svgKey in conditions[vendor][field]) {
          if (!_isEmpty(conditions[vendor][field][svgKey])) {
            return true;
          }
        }
        break;
      case FILTER_REQUEST_FIELD_BY_FIELD.tags:
        for (let tagKey in conditions[vendor][field]) {
          if (!_isEmpty(conditions[vendor][field][tagKey])) {
            return true;
          }
        }
        break;
      default:
        if (!_isEmpty(conditions[vendor][field])) {
          return true;
        }
    }
  }
  // 벤더 멀티 셀렉트 적용 후 아래로 대체 작업 필요
  // for (let vendor in conditions) {
  //   for (let field in conditions[vendor]) {
  //     switch (field) {
  //       case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
  //         for (let svgKey in conditions[vendor][field]) {
  //           if (!_isEmpty(conditions[vendor][field][svgKey])) {
  //             return true;
  //           }
  //         }
  //         break;
  //       case FILTER_REQUEST_FIELD_BY_FIELD.tags:
  //         for (let tagKey in conditions[vendor][field]) {
  //           if (!_isEmpty(conditions[vendor][field][tagKey])) {
  //             return true;
  //           }
  //         }
  //         break;
  //       default:
  //         if (!_isEmpty(conditions[vendor][field])) {
  //           return true;
  //         }
  //     }
  //   }
  // }
  return false;
}

function isAnyConditionOfFieldUnselectedByToggleAction(newConditions, oldConditions, field) {
  if (_isNil(oldConditions)) {
    return false;
  }
  switch (field) {
    case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
      return Object.keys(newConditions).some(vendor => {
        return Object.keys(newConditions[vendor][field]).some(svgKey => {
          if (_isEmpty(oldConditions[vendor][field][svgKey])) {
            return false;
          }
          const newSvgValueSet = new Set(newConditions[vendor][field][svgKey]);
          return oldConditions[vendor][field][svgKey].some(oldSvgValue => !newSvgValueSet.has(oldSvgValue));
        });
      });
    case FILTER_REQUEST_FIELD_BY_FIELD.tags:
      return Object.keys(newConditions).some(vendor => {
        return Object.keys(newConditions[vendor][field]).some(tagKey => {
          if (_isEmpty(oldConditions[vendor][field][tagKey])) {
            return false;
          }
          const newTagValueSet = new Set(newConditions[vendor][field][tagKey]);
          return oldConditions[vendor][field][tagKey].some(oldTagValue => !newTagValueSet.has(oldTagValue));
        });
      });
    default:
      return Object.keys(newConditions).some(vendor => {
        const newValueSet = new Set(newConditions[vendor][field]);
        return oldConditions[vendor][field].some(oldValue => !newValueSet.has(oldValue));
      });
  }
}

function getCodeToProductMap(productOptions) {
  if (!productOptions) {
    return {};
  }
  const codeToProductMap = {};
  productOptions.forEach(product => {
    codeToProductMap[product.productCode] = product;
  });
  return codeToProductMap;
}

function getIdToAccountMap(accountOptions) {
  if (!accountOptions) {
    return {};
  }
  const idToAccountMap = {};
  accountOptions.forEach(account => {
    idToAccountMap[account.linkedAccountId] = account;
  });
  return idToAccountMap;
}

function getCodeToRegionMap(regionOptions) {
  if (!regionOptions) {
    return {};
  }
  const codeToRegionMap = {};
  regionOptions.forEach(region => {
    codeToRegionMap[region.region] = region;
  });
  return codeToRegionMap;
}

function convertFilterTextNm(text, field){
  if(field === FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup){
    return text.split(':::')[0]
  }else{
    return text
  }
}

function convertFilterTextId(text, field){
  if(field === FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup){
    return text.split(':::')[1]
  }else{
    return text
  }
}

function getTreeFilterKey(filterValue, field){
  switch (field) {
    case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
      return filterValue.serviceGroupKey;
    case FILTER_REQUEST_FIELD_BY_FIELD.tags:
      return filterValue.tagKey
    default:
      return '';
  }
}

function getTreeFilterValue(filterValue, field){
  switch (field) {
    case FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup:
      return filterValue.serviceGroupValue
    case FILTER_REQUEST_FIELD_BY_FIELD.tags:
      return filterValue.tagValue
    default:
      return '';
  }
}

function getDetailedDataGroupedByMultiKey(detailedData , visibleField, isCompare){
  const inputData = _cloneDeep(detailedData);
  let filterKey = visibleField.filter(field =>{return ![COST_CONST,USAGE_CONST].includes(field)});
  let costData = {usage: 0.0, cost:0.0};

  if(isCompare){
    Object.assign(costData, { comparedUsage: 0.0, comparedCost:0.0} );
  }

  let groupedData =  [...inputData.reduce((r,o)=> {
    const key = filterKey.map(_ => `${o[_]}`).join(" :: ");
    const item = r.get(key) || Object.assign({}, o, costData)

    item.usage += o.usage;
    item.cost += o.cost;

    if(isCompare){
      item.comparedUsage += o.comparedUsage;
      item.comparedCost += o.comparedCost;
    }

    return r.set(key,item);
  }, new Map).values()];
  return groupedData;
}

export {
  getCompareAccountKeyByVendor,
  prepareCostAnalyticsDetailPortionData,
  formatCostAnalyticsDate,
  prepareDataForCostTrend,
  prepareColorForCostByStackedChart,
  getMappingAccountWithColors,
  getTotalCostOfAccount,
  compareByTotalCost,
  mappingKeysWithLabelForTrend,
  getAccountKeyByVendor,
  getFilterConditionOfFieldForRequest,
  standardizeUserFilterForSave,
  getTagKeyToTagValuesMap,
  getServiceGroupKeyToServiceGroupKeyValuesMap,
  countRelatedFilterOptions,
  sortFilterOptions,
  getOptionText,
  getTreeFilterOptionText,
  getDateRangePresets,
  getFilterSettings,
  buildFilterParameters,
  combineCompareCostAnalyticsData,
  getCombineDailyCosts,
  prepareDataForOverviewTable,
  getRatioCostBaseOnDateItem,
  getRatioBetweenCostAndCompareCost,
  getTotalCostOfDaily,
  buildDetailCADataWithCompare,
  mockDataForComparePortionDetail,
  getItemInfoFromCombineDailyCostsByCostDate,
  getAbsForFormattedRatioCost,
  highlightColumnsByDateOnCAOverviewTable,
  resetHighlightOnCAOverviewTable,
  makeItemsToUniqByKey,
  fadeOutAllPinnedRowsInCAOverviewTable,
  sortViewDetailFieldOptionsByCheckedStatus,
  getPreviewDetailFieldOptions,
  isConditionOfFieldEqual,
  getFirstNotEmptyConditionField,
  isAnyConditionSelected,
  isAnyConditionOfFieldUnselectedByToggleAction,
  getCodeToProductMap,
  getIdToAccountMap,
  getCodeToRegionMap,
  getSelectedVendors,
  availableVendorsOptions,
  convertFilterTextNm,
  convertFilterTextId,
  getTreeFilterKey,
  getTreeFilterValue,
  getDetailedDataGroupedByMultiKey
}
