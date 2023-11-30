/* eslint-disable no-param-reassign */
import _remove from 'lodash/remove';
import dayjs from 'dayjs';
import _toString from 'lodash/toString';
import _isEqual from 'lodash/isEqual';
import _isNil from 'lodash/isNil';
import _toLower from 'lodash/toLower';
import _get from 'lodash/get';
import _cloneDeep from 'lodash/cloneDeep'
import store from '@/store'
import { i18n } from '../components/common/base-i18n/i18n'
import _common from '@/util/Common';
import {getFullDateFormatByLocalization} from "@/util/dateTimeUtils";
import {
  ALI_COLORS,
  AWS_COLORS,
  AZURE_COLORS,
  COMPARE_COST_TREND_COLORS,
  CHECK_DUPLICATED_TEMPLATE_REGEX,
  COPY_OF_DASHBOARD_TEMPLATE_NAME,
  DASHBOARD_DATE_TYPE,
  DASHBOARD_WIDGET_SIZES,
  DASHBOARD_WIDGET_TYPE,
  DEFAULT_TOP5_COLORS,
  GCP_COLORS,
  IDC_COLORS,
  NCP_COLORS,
  OCI_COLORS,
  NUMBER_OF_COPY_WILDCARD,
  VENDORS,
  WIDGET_MAX_SIZE,
  OTHER_NAME,
  WIDGET_FIELDS_NOT_IN_EDIT_FORM,
  DEFAULT_OTHER_COLOR,
  COST_ANALYTIC_DATE_FORMAT,
  AWS_PORTION_COLORS,
  GCP_PORTION_COLORS,
  AZURE_PORTION_COLORS,
  OCI_PORTION_COLORS,
  NCP_PORTION_COLORS,
  AWS_PORTION_BORDER_COLORS,
  GCP_PORTION_BORDER_COLORS,
  AZURE_PORTION_BORDER_COLORS,
  OCI_PORTION_BORDER_COLORS,
  NCP_PORTION_BORDER_COLORS,
  DEFAULT_OTHER_BORDER_COLOR,
  MONTHLY_COST_TIME_FRAME,
  TENCENT_COLORS,
  Tencent_PORTION_COLORS,
  Tencent_PORTION_BORDER_COLORS,
  SCALE
} from '@/constants/dashboardConstants';
import {
  formatMonthYearByLocalization,
  splitYearMonthFromYYYYMM
} from '@/util/dateTimeUtils';
import {
  removeAllSpecialCharacters,
} from '@/util/stringUtils';
import {
  CHART_ITEM_LABEL,
  CURRENCY_SYMBOL,
  FILTER_TIME,
  FORMAT_COST,
  TIME_CONST,
  NUMBER_OF_OTHERS,
  START_DATE_WEEK,
  END_DATE_WEEK,
  MONTHLY_SUM
} from '@/constants/constants';
import _isEmpty from 'lodash/isEmpty';
import {calculateCostByCurrency, calculateCostByCurrencyForExport, formatCost, formatCostForExport, formatPercentage} from '@/util/costUtils';
import { getDisplayItemWithVendorBaseOnViewBy, getDisplayItemBaseOnViewBy, getDisplayProductPortionBaseOnProductFamily, getDisplayItemBaseOnPortionViewBy } from '@/util/formatAccountUtils';
import {getDisplayProductPortionBaseOnProductFamilyForPrime} from "./formatAccountUtils";

function getStandardizedWidgetsForRender(widgets) {
  return widgets.map(configWidget => {
    const minHeightAndMinWidth = getSizeByWidgetType(configWidget.widgetType);

    return Object.assign({}, configWidget, {
      x: configWidget.x,
      y: configWidget.y,
      w: (configWidget && configWidget.w) ? configWidget.w : configWidget.width,
      h: (configWidget && configWidget.h) ? configWidget.h : configWidget.height,
      minH: minHeightAndMinWidth && minHeightAndMinWidth.minHeight,
      minW: minHeightAndMinWidth && minHeightAndMinWidth.minWidth,
      specificSizes: minHeightAndMinWidth && minHeightAndMinWidth.specificSizes,
      i: configWidget.index,
      isEditFormVisible: false,
    });
  });
}
function getStandardizedWidgetsForSave(widgets) {
  return widgets.map(widget => {
    return {
      ...widget,
      height: widget.h,
      width: widget.w
    };
  });
}

function getWidgetConfigWithFieldsInEditFormOnly(widgetConfig) {
  let result = {};
  Object.keys(widgetConfig).forEach(key => {
    if (WIDGET_FIELDS_NOT_IN_EDIT_FORM.includes(key)) {
      return;
    }
    result[key] = widgetConfig[key];
  });
  return result;
}

function prepareDataForTop5Cost(top5CostRawData, selectedCurrency, exchangeRate, i18nOthersText, i18nOthersTextUpper, viewBy) {
  let defaultVendors = [];
  let dynamicVendors = [];
  let formData = {
    labels: [],
    datasets: [
      {
        data: [],
        backgroundColor: []
      }
    ],
    vendor: [],
    item: [],
    name: [],
    date: [],
    percentage: [],
    isOthers: []
  };
  let sumOfCost = 0;
  top5CostRawData && top5CostRawData.cost && top5CostRawData.cost.forEach(item => {
    sumOfCost += item.cost;
  });
  let top5Data = top5CostRawData && top5CostRawData.cost && top5CostRawData.cost.map(item => {
    return {
      category: !item.isOthers
        ? getDisplayItemWithVendorBaseOnViewBy(item, viewBy)
        : `${item.vendor} ${i18nOthersText}`,
      value: item.cost,
      vendor: item.vendor,
      item: item.item,
      name: item.isOthers
        ? `${i18nOthersTextUpper} (${item.vendor})`
        : (getDisplayItemBaseOnViewBy(item, viewBy)),
      date: formatCostForExport(item.cost),
      percentage: sumOfCost ? `${formatPercentage(item.cost * 100 / sumOfCost)}%` : 0,
      isOthers: item.isOthers
    }
  });

  if (_isEmpty(top5Data)) {
    return {
      pieData: [],
      colors: []
    }
  }

  top5Data.forEach(item => {
    if (item.isOthers) {
      defaultVendors.push(item)
    } else {
      dynamicVendors.push(item)
    }
  });

  dynamicVendors.sort((dynamicVendor1, dynamicVendor2) => dynamicVendor1.category.localeCompare(dynamicVendor2.category));
  let dynamicVendorsMetaData = prepareDynamicVendorsForTop5Cost(dynamicVendors);

  let pieData = dynamicVendorsMetaData.dynamicVendors.concat(defaultVendors);
  let currencySymbol = CURRENCY_SYMBOL[selectedCurrency];
  pieData.forEach(data => {
    let costInSelectedCurrency = calculateCostByCurrencyForExport(data.value, selectedCurrency, exchangeRate);
    // eslint-disable-next-line no-param-reassign
    data.value = formatCostForExport(costInSelectedCurrency)
    data.date = `${formatCostForExport(costInSelectedCurrency)}`
  });

  pieData.forEach(item => {
    formData.labels.push(item.category);
    formData.datasets[0].data.push(item.value);
    formData.isOthers.push(item.isOthers);
    formData.item.push(item.item);
    formData.name.push(item.name);
    formData.percentage.push(item.percentage);
    formData.date.push(item.date);
    formData.vendor.push(item.vendor);
  })
  for(let i=0; i<dynamicVendorsMetaData.colors.length; i++){
    formData.datasets[0].backgroundColor.push(dynamicVendorsMetaData.colors[i]);
  }
  formData.datasets[0].borderWidth = 1;

  return formData;
}

function prepareDataForTop5Cost_backup(top5CostRawData, selectedCurrency, exchangeRate, i18nOthersText, i18nOthersTextUpper, viewBy) {
  let defaultVendors = [];
  let dynamicVendors = [];
  let sumOfCost = 0;
  top5CostRawData && top5CostRawData.cost && top5CostRawData.cost.forEach(item => {
    sumOfCost += item.cost;
  });
  let top5Data = top5CostRawData && top5CostRawData.cost && top5CostRawData.cost.map(item => {
    return {
      category: !item.isOthers
        ? getDisplayItemWithVendorBaseOnViewBy(item, viewBy)
        : `${item.vendor} ${i18nOthersText}`,
      value: item.cost,
      vendor: item.vendor,
      item: item.item,
      name: item.isOthers
        ? `${i18nOthersTextUpper} (${item.vendor})`
        : (getDisplayItemBaseOnViewBy(item, viewBy)),
      date: formatCostForExport(item.cost),
      percentage: sumOfCost ? `${formatPercentage(item.cost * 100 / sumOfCost)}%` : 0,
      isOthers: item.isOthers
    }
  });

  if (_isEmpty(top5Data)) {
    return {
      pieData: [],
      colors: []
    }
  }

  top5Data.forEach(item => {
    if (item.isOthers) {
      defaultVendors.push(item)
    } else {
      dynamicVendors.push(item)
    }
  });

  dynamicVendors.sort((dynamicVendor1, dynamicVendor2) => dynamicVendor1.category.localeCompare(dynamicVendor2.category));
  let dynamicVendorsMetaData = prepareDynamicVendorsForTop5Cost(dynamicVendors);

  let pieData = dynamicVendorsMetaData.dynamicVendors.concat(defaultVendors);
  let currencySymbol = CURRENCY_SYMBOL[selectedCurrency];
  pieData.forEach(data => {
    let costInSelectedCurrency = calculateCostByCurrencyForExport(data.value, selectedCurrency, exchangeRate);
    // eslint-disable-next-line no-param-reassign
    data.value = `${currencySymbol}${formatCostForExport(costInSelectedCurrency)}`
    data.date = `${formatCostForExport(costInSelectedCurrency)}`
  });
  return {
    pieData: dynamicVendorsMetaData.dynamicVendors.concat(defaultVendors),
    colors: dynamicVendorsMetaData.colors
  }
}

function prepareDynamicVendorsForTop5Cost(dynamicVendors) {
  let AWSVendors = [];
  let GCPVendors = [];
  let AZUREVendors = [];
  let ALIVendors = [];
  let IDCVendors = [];
  let OCIVendors = [];
  let NCPVendors = [];
  let TencentVendors = [];

  let AWSColors = [];
  let GCPColors = [];
  let AZUREColors = [];
  let ALIColors = [];
  let IDCColors = [];
  let OCIColors = [];
  let NCPColors = [];
  let TencentColors = [];

  dynamicVendors.forEach(vendorData => {
    switch (vendorData.vendor) {
      case VENDORS.AWS:
        AWSVendors.push(vendorData);
        break;
      case VENDORS.GCP:
        GCPVendors.push(vendorData);
        break;
      case VENDORS.AZURE:
        AZUREVendors.push(vendorData);
        break;
      case VENDORS.ALI:
        ALIVendors.push(vendorData);
        break;
      case VENDORS.IDC:
        IDCVendors.push(vendorData);
        break;
      case VENDORS.OCI:
        OCIVendors.push(vendorData);
        break;
      case VENDORS.NCP:
        NCPVendors.push(vendorData);
        break;
      case VENDORS.TENCENT:
        TencentVendors.push(vendorData)
        break;
    }
  });

  for (let i = 0; i < AWSVendors.length; i++) {
    AWSColors.push(AWS_COLORS[AWS_COLORS.length - 1 - i])
  }

  for (let i = 0; i < GCPVendors.length; i++) {
    GCPColors.push(GCP_COLORS[GCP_COLORS.length - 1 - i])
  }

  for (let i = 0; i < AZUREVendors.length; i++) {
    AZUREColors.push(AZURE_COLORS[AZURE_COLORS.length - 1 - i])
  }

  for (let i = 0; i < ALIVendors.length; i++) {
    ALIColors.push(ALI_COLORS[ALI_COLORS.length - 1 - i])
  }

  for (let i = 0; i < IDCVendors.length; i++) {
    IDCColors.push(IDC_COLORS[IDC_COLORS.length - 1 - i])
  }

  for (let i = 0; i < OCIVendors.length; i++) {
    OCIColors.push(OCI_COLORS[OCI_COLORS.length - 1 - i])
  }

  for (let i = 0; i < NCPVendors.length; i++) {
    NCPColors.push(NCP_COLORS[NCP_COLORS.length - 1 - i])
  }

  for (let i = 0; i < TencentVendors.length; i++) {
    TencentColors.push(TENCENT_COLORS[TENCENT_COLORS.length - 1 - i])
  }
  let colors = AWSColors.concat(GCPColors).concat(AZUREColors).concat(ALIColors).concat(IDCColors).concat(OCIColors).concat(NCPColors).concat(TencentColors);

  AWSVendors.sort(compareByValue);
  GCPVendors.sort(compareByValue);
  AZUREVendors.sort(compareByValue);
  ALIVendors.sort(compareByValue);
  IDCVendors.sort(compareByValue);
  OCIVendors.sort(compareByValue);
  NCPVendors.sort(compareByValue);
  TencentVendors.sort(compareByValue);

  let sortedVendors = AWSVendors.concat(GCPVendors).concat(AZUREVendors).concat(ALIVendors).concat(IDCVendors).concat(OCIVendors).concat(NCPVendors).concat(TencentVendors);
  return {
    colors: _isEmpty(colors) ? DEFAULT_TOP5_COLORS : colors,
    dynamicVendors: sortedVendors
  };
}

function compareByValue(vendor1, vendor2) {
  return vendor2.value - vendor1.value;
}

function compareByCost(vendor1, vendor2) {
  return parseFloat(vendor2.cost) - parseFloat(vendor1.cost);
}

function compareByIsOthers(vendor1, vendor2) {
  return vendor1.isOthers - vendor2.isOthers;
}

function compareByTotalCost(vendor1, vendor2) {
  return parseFloat(vendor2.totalCost) - parseFloat(vendor1.totalCost);
}

/**
 *  Get account key by vendor
 * @param accountVendor {vendor, item}
 */
function getAccountKeyByVendor(accountVendor) {
  return `${removeAllSpecialCharacters(accountVendor.vendor, '_')}-${removeAllSpecialCharacters(accountVendor.item, '_')}`;
}

/**
 *  Get Original item key by vendor without function removeAllSpecialCharacters
 * @param itemKeyVendor {vendor, item}
 */
function getOriginalKeyByVendor(accountVendor) {
  return `${removeAllSpecialCharacters(accountVendor.vendor, '_')}-${accountVendor.item}`;
}

function getDashboardCostChartData(rawDashboardCost, filterTime, selectedCurrency, exchangeRate, orderedAccountKeysByLastPeriodCost, isOthersIncluded) {
  if (_isEmpty(rawDashboardCost)) {
    return [];
  }
  let standardizedRawDashboardCost = cleanRawDashboard(rawDashboardCost, filterTime);
  let newDashboardCostData = {};
  standardizedRawDashboardCost = standardizedRawDashboardCost.filter(function(item) {
    return !_isEmpty(item.cost)
  })

  newDashboardCostData[`labels`] = [];
  newDashboardCostData[`time`] = [];
  newDashboardCostData[`datasets`] = [];
  newDashboardCostData[NUMBER_OF_OTHERS] = [];
  for(let i=0; i<standardizedRawDashboardCost.length; i++) {
    let item = standardizedRawDashboardCost[i];
    newDashboardCostData[`labels`].push(formatTimeLabelChart(item.date, filterTime));

    let datePeiodObject = {};
    datePeiodObject[`startDate`] = item.startDate;
    datePeiodObject[`endDate`] = item.endDate;
    if(Object.keys(item).includes(MONTHLY_SUM)){
      datePeiodObject[MONTHLY_SUM] = calculateCostByCurrencyForExport(item.monthlySum, selectedCurrency, exchangeRate);
    }
    newDashboardCostData[`time`].push(datePeiodObject);
  }

  let colorSet = getDashboardCostChartColors(standardizedRawDashboardCost[0].cost[0].vendor);
  for(let j=0; j<standardizedRawDashboardCost[0].cost.length; j++){
    let costObject = {};
    costObject[`data`] = [];
    if (!standardizedRawDashboardCost[0].cost[j].isOthers || isOthersIncluded) {
      standardizedRawDashboardCost.map(function (element) {
        costObject[`label`] = getOriginalKeyByVendor(element.cost[j]);
        costObject[`data`].push(calculateCostByCurrencyForExport(element.cost[j].cost, selectedCurrency, exchangeRate));
      });

      costObject[`fill`] = false;
      costObject[`borderColor`] = colorSet[j] ? colorSet[j] : '#A9A9A9';
      costObject[`backgroundColor`] = '#ffffff';
      costObject[`tension`] = 0;

      newDashboardCostData[`datasets`].push(costObject);
    }
    newDashboardCostData[NUMBER_OF_OTHERS].push(standardizedRawDashboardCost[0].cost[j].numberOfOthers);
  }

  return newDashboardCostData;
}

// 마지막 기간 기준으로 금액이 가장 높은 item 내림차순
function cleanRawDashboard(rawDashboardCost,filterTime){
  let standardizedRawDashboardCost = filterTime === FILTER_TIME.MONTH
    ? buildMonthlyDashboardCostData(rawDashboardCost, _get(rawDashboardCost, 'payload.timeFrame'))
    : buildWeeklyDashboardCostData(rawDashboardCost);
  let standardizedCostByCondition = standardizedRawDashboardCost.costByCondition;

  const lastObject = standardizedCostByCondition[standardizedCostByCondition.length - 1];
  lastObject.cost.sort(compareByCost).sort(compareByIsOthers);
  for (let i = 0; i < standardizedCostByCondition.length - 1; i++) {
    standardizedCostByCondition[i].cost.sort((a, b) => {
      const indexA = lastObject.cost.findIndex(item => item.item === a.item);
      const indexB = lastObject.cost.findIndex(item => item.item === b.item);
      return indexA - indexB;
    });
  }
  const cleanCostByConditionData = standardizedCostByCondition;

  return cleanCostByConditionData;
}

// 기존의 stackChart에 들어갈 data 가공하는 함수 -> 마지막에 제거 필요.
function getDashboardCostChartData_backup(rawDashboardCost, filterTime, selectedCurrency, exchangeRate, orderedAccountKeysByLastPeriodCost, isOthersIncluded) {
  if (_isEmpty(rawDashboardCost)) {
    return [];
  }
  let newDashboardCostData = [];
    let standardizedRawDashboardCost = filterTime === FILTER_TIME.MONTH
      ? buildMonthlyDashboardCostData(rawDashboardCost, _get(rawDashboardCost, 'payload.timeFrame'))
      : buildWeeklyDashboardCostData(rawDashboardCost);
  for(let i=0; i< standardizedRawDashboardCost.costByCondition.length; i++){
    let costObject = {};
    costObject[FORMAT_COST] = {};

    let item = standardizedRawDashboardCost.costByCondition[i];
    if(!_isEmpty(item.cost)){
      for(let j=0; j< item.cost.length; j++){

        let dataByDate = item.cost[j];

        if (!dataByDate.isOthers || isOthersIncluded) {
          const exchangedCost = calculateCostByCurrencyForExport(dataByDate.cost, selectedCurrency, exchangeRate);
          costObject[getOriginalKeyByVendor(dataByDate)] = exchangedCost;
          costObject[FORMAT_COST][getOriginalKeyByVendor(dataByDate)] = formatCost(exchangedCost);
          costObject[NUMBER_OF_OTHERS] = dataByDate.numberOfOthers
        }
      }

      costObject[`time`] = formatTimeLabelChart(item.date, filterTime);
      costObject[START_DATE_WEEK] = item.startDate;
      costObject[END_DATE_WEEK] = item.endDate;
      if(Object.keys(item).includes(MONTHLY_SUM)){
        costObject[MONTHLY_SUM] = calculateCostByCurrencyForExport(item.monthlySum, selectedCurrency, exchangeRate);
      }

      newDashboardCostData.push(costObject)
    }
  }

  // standardizedRawDashboardCost.costByCondition.forEach(item => {
  //   let costObject = {};
  //   costObject[FORMAT_COST] = {};
  //
  //   orderedAccountKeysByLastPeriodCost.forEach(orderedAccountKey => {
  //     item.cost.forEach(el => {
  //       if (getAccountKeyByVendor(el) === orderedAccountKey) {
  //         if (!el.isOthers || isOthersIncluded) {
  //           const exchangedCost = calculateCostByCurrency(el.cost, selectedCurrency, exchangeRate);
  //           costObject[getAccountKeyByVendor(el)] = exchangedCost;
  //           costObject[FORMAT_COST][getAccountKeyByVendor(el)] = formatCost(exchangedCost);
  //           costObject[NUMBER_OF_OTHERS] = el.numberOfOthers
  //         }
  //       }
  //     });
  //   });
  //   costObject[`time`] = formatTimeLabelChart(item.date, filterTime)
  //
  //   newDashboardCostData.push(costObject)
  // });

  return newDashboardCostData
}

// getDashboardCostChartData 함수 primevue에 맞게 커스텀
function getDashboardCostPrimeChartData(rawDashboardCost, filterTime, selectedCurrency, exchangeRate, orderedAccountKeysByLastPeriodCost){
  if (_isEmpty(rawDashboardCost) || _isEmpty(rawDashboardCost.costByCondition)) {
    return [];
  }
  let PrimeStackedChartDataFrom = {
    labels:[],
    datasets:[]
  }
  // 원본 API 데이터 정제
  let cleanRawCostByCondition = cleanRawDashboard(rawDashboardCost,filterTime);

  // 빈 값제거
  cleanRawCostByCondition = cleanRawCostByCondition.filter(function (item) {
    return !_isEmpty(item.cost);
  });
  // 컬러 테이블
  const colorByItemsTable = getMappingVendorAccountsWithColors(orderedAccountKeysByLastPeriodCost)
  Object.keys(colorByItemsTable).forEach(
    // other 부분 컬러 설정
    function (key){
      if(key.includes("others")){
        colorByItemsTable[key] = DEFAULT_OTHER_COLOR;
      }
  })

  // chart 데이터 생성
  const itemNames = cleanRawCostByCondition[0].cost.map(itemByCost => getOriginalKeyByVendor(itemByCost));
  PrimeStackedChartDataFrom.labels = cleanRawCostByCondition.map(dateByCosts => formatTimeLabelChart(dateByCosts.date,filterTime));
  for (let i = 0; i< cleanRawCostByCondition[0].cost.length; i++){
    let itemByCosts = {
      type : 'bar',
      label: itemNames[i],
      backgroundColor :
        // 월별 조회시 마지막 기간(예상 비용)은 color opacity 적용
        function (context){
          if(isEstimatedOpacityApply(cleanRawCostByCondition,filterTime)){
            if(context.dataIndex == context.chart.data.datasets[context.datasetIndex].data.length - 1){
              return colorByItemsTable[itemNames[i]]+'80' // rgb hex 값 뒤 80붙이면 opacity 적용 값
            }
          }
          return colorByItemsTable[itemNames[i]]
      },
      data : cleanRawCostByCondition.map(
        // 통화별 cost 값 변경
        function (dateByCosts){
          let cost = calculateCostByCurrencyForExport(dateByCosts.cost[i].cost,selectedCurrency,exchangeRate);
          return cost
        })
    }
    PrimeStackedChartDataFrom.datasets.push(itemByCosts)
  }
  return PrimeStackedChartDataFrom
}

/* API 데이터 정제
* 1. 월별, 주별 데이터 순서 정렬
* 2. 가장 최근 비용기준 정렬 후 Item별 인덱스 기준으로 나머지 기간의 Item 인덱스도 똑같이 정렬
* 3. 원본 API에서 필요한 costByCondition 배열만 반환
* */
function cleanRawDashboard(rawDashboardCost,filterTime){
  let standardizedCostByCondition = rawDashboardCost.costByCondition

  const lastObject = standardizedCostByCondition[standardizedCostByCondition.length -1]
  lastObject.cost.sort(compareByCost).sort(compareByIsOthers);
  for(let i =0; i<standardizedCostByCondition.length-1; i ++){
    standardizedCostByCondition[i].cost.sort((a,b) => {
      const indexA = lastObject.cost.findIndex(item => item.item === a.item);
      const indexB = lastObject.cost.findIndex(item => item.item === b.item);
      return indexA - indexB;
    })
  }
  rawDashboardCost.costByCondition = standardizedCostByCondition
  let standardizedRawDashboardCost = filterTime === FILTER_TIME.MONTH
    ? buildMonthlyDashboardCostData(rawDashboardCost, _get(rawDashboardCost, 'payload.timeFrame'))
    : buildWeeklyDashboardCostData(rawDashboardCost);

  const cleanCostByConditionData = standardizedRawDashboardCost.costByCondition

  return cleanCostByConditionData;

}


/* Color 투명도 설정 여부
* 조건 1 : 월별 데이터 조회
* 조건 2 : 월별 데이터 중 가장 마지막 date가 estimated일 때
* */
function isEstimatedOpacityApply(cleanRawCostByCondition,filterTime){
  if(filterTime == FILTER_TIME.MONTH && cleanRawCostByCondition[cleanRawCostByCondition.length-1].date === CHART_ITEM_LABEL.ESTIMATED){
    return true
  }
  return false
}

function getMultiDashboardCostChartData(rawDashboardCost, filterTime, selectedCurrency, exchangeRate, isOthersIncluded) {
  if (_isEmpty(rawDashboardCost)) {
    return [];
  }
  let newDashboardCostData = [];
  let standardizedRawDashboardCost = filterTime === FILTER_TIME.MONTH
    ? buildMonthlyDashboardCostData(rawDashboardCost, _get(rawDashboardCost, 'payload.timeFrame'))
    : buildWeeklyDashboardCostData(rawDashboardCost);

  for(let i=0; i< standardizedRawDashboardCost.costByCondition.length; i++){
    let costObject = {};
    costObject[FORMAT_COST] = {};

    let item = standardizedRawDashboardCost.costByCondition[i];
    if(item.cost!=undefined){
      for(let j=0; j< item.cost.length; j++){

        let dataByDate = item.cost[j];

        if (!dataByDate.isOthers || isOthersIncluded) {
          const exchangedCost = calculateCostByCurrencyForExport(dataByDate.cost, selectedCurrency, exchangeRate);
          costObject[getOriginalKeyByVendor(dataByDate)] = exchangedCost;
          costObject[FORMAT_COST][getOriginalKeyByVendor(dataByDate)] = formatCost(exchangedCost);
          if (dataByDate.item === "others"){
            costObject[dataByDate.vendor+"-"+NUMBER_OF_OTHERS] = dataByDate.numberOfOthers
          }
        }
      }
    }

    costObject[`time`] = formatTimeLabelChart(item.date, filterTime);
    costObject[START_DATE_WEEK] = item.startDate;
    costObject[END_DATE_WEEK] = item.endDate;
    if(Object.keys(item).includes(MONTHLY_SUM) ){ // GCP 월별 합계 처리
      costObject[MONTHLY_SUM] = calculateCostByCurrencyForExport(item.monthlySum, selectedCurrency, exchangeRate);
    }
    newDashboardCostData.push(costObject)
  }

  return newDashboardCostData
}

function buildMonthlyDashboardCostData(rawData, timeFrame) {
  let startDate = null
  let endDate = dayjs.utc().add(1, 'month').startOf('month')
  let rawDataClone = _cloneDeep(rawData)

  switch (timeFrame) {
    case MONTHLY_COST_TIME_FRAME.YEAR_TO_MONTH:
      startDate = dayjs.utc().startOf('year')
      break;
    case MONTHLY_COST_TIME_FRAME.LAST_3_MONTHS:
      startDate = dayjs(endDate).set('month', endDate.month() - 3)
      break;
    case MONTHLY_COST_TIME_FRAME.LAST_6_MONTHS:
      startDate = dayjs(endDate).set('month', endDate.month() - 6)
      break;
    case MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS:
      startDate = dayjs(endDate).set('month', endDate.month() - 12)
      break;
  }
  if (startDate) {
    let monthToMonthlyCostMap = {}
    rawDataClone.costByCondition.forEach(monthlyCost => {
      monthToMonthlyCostMap[monthlyCost.date] = monthlyCost;
    })
    for (let d = startDate; d.isBefore(endDate); d = d.add(1, 'month')) {
      let month = d.month() < 9 ? `0${d.month() + 1}` : d.month() + 1
      let key = `${d.year()}${month}`
      if (_isNil(monthToMonthlyCostMap[key])) {
        rawDataClone.costByCondition.push({ date: key, cost: [] })
      }
    }
  }

  rawDataClone.costByCondition.sort(function(a, b) {
    if (a.date === CHART_ITEM_LABEL.ESTIMATED) {
      return 1
    } else if (b.date === CHART_ITEM_LABEL.ESTIMATED) {
      return -1
    }
    const monthlyTimeA = {
      month: a.date.slice(4,6),
      year: a.date.slice(0,4)
    };
    const monthlyTimeB = {
      month: b.date.slice(4,6),
      year: b.date.slice(0,4)
    };
    return dayjs.utc().month(monthlyTimeA.month - 1).year(monthlyTimeA.year).startOf('month')
     - dayjs.utc().month(monthlyTimeB.month - 1).year(monthlyTimeB.year).startOf('month')
  });
  return rawDataClone
}

function buildWeeklyDashboardCostData(rawData) {
  let rawDataClone = _cloneDeep(rawData)

  rawDataClone.costByCondition.sort(function(a, b) {
    const monthlyTimeA = {
      week: a.date.match(/\d+/g)[0],
      year: a.date.match(/\d+/g)[1]
    };
    const monthlyTimeB = {
      week: b.date.match(/\d+/g)[0],
      year: b.date.match(/\d+/g)[1]
    };
    return getDateOfWeek(monthlyTimeA.week, monthlyTimeA.year) - getDateOfWeek(monthlyTimeB.week, monthlyTimeB.year)
  });
  return rawDataClone
}

function getDateOfWeek(week, year) {
  return new Date(year, 0, (week - 1) * 7 + 1);
}

/**
 * Loop all costs and then mapping keys with label
 *  Return something like
 *  {
 *    'AWS_11111111': 'AWS 1111111 (Alias 1)'
 *    'AWS_22222222': 'AWS 22222222 (Alias 2)'
 *  }
 * @param costs
 * @param i18nOthersText
 * @param isOthersIncluded
 * @param viewBy
 * @return {}
 */
function mappingKeysWithLabelForCosts(costs, i18nOthersText, isOthersIncluded, viewBy) { // Others 레전드 처리
  const costByCondition = getCostsForMappingKeysWithLabel(costs)
  if (_isEmpty(costByCondition)) {
    return {};
  }

  let mappingKeysWithLabel = {};
  costByCondition.forEach(cost => {
    if (!cost.isOthers) {
      mappingKeysWithLabel[getOriginalKeyByVendor(cost)] = getDisplayItemWithVendorBaseOnViewBy(cost, viewBy);
    } else if (isOthersIncluded) {
      mappingKeysWithLabel[getOriginalKeyByVendor(cost)] = `+${cost.numberOfOthers} ${cost.vendor} ${i18nOthersText}`;
    }
  });

  return mappingKeysWithLabel;
}

function mappingKeysWithLabelForVendor(costs, i18nOthersText, isOthersIncluded, viewBy) { // Others 레전드 처리
  const costByCondition = getCostsForMappingKeysWithVendor(costs)

  if (_isEmpty(costByCondition)) {
    return {};
  }

  let mappingKeysWithLabel = {};
  costByCondition.forEach(cost => {
    if (!cost.isOthers) {
      mappingKeysWithLabel[cost.item] = getDisplayItemWithVendorBaseOnViewBy(cost, viewBy);
    } else if (isOthersIncluded) {
      mappingKeysWithLabel[cost.item] = `+${cost.numberOfOthers} ${cost.vendor} ${i18nOthersText}`;
    }
  });

  return mappingKeysWithLabel;
}


function getCostsForMappingKeysWithLabel(costByCondition) {
  let arrayCostByCondition = []
  if (_isEmpty(costByCondition)) {
    return {};
  }
  costByCondition.forEach(data => {
    if(_isEmpty(data)){
      data.cost.forEach(item => {
        arrayCostByCondition.push(item)
      })
    }
  })
  let lastCostByCondition
  for (let i = costByCondition.length - 1; i >= 0; i--) {
    if (!_isEmpty(costByCondition[i].cost)) {
      lastCostByCondition = costByCondition[i].cost
      break;
    }
  }
  if (_isNil(lastCostByCondition) || _isNil(lastCostByCondition[lastCostByCondition.length - 1])) {
    return;
  }
  let numberOfOthers = lastCostByCondition[lastCostByCondition.length - 1].numberOfOthers
  return Array.from(new Set(arrayCostByCondition.map(cost => cost.item)))
    .map(item => {
      return {
        item: item,
        isOthers: arrayCostByCondition.find(cost => cost.item === item).isOthers,
        itemAlias: arrayCostByCondition.find(cost => cost.item === item).itemAlias,
        vendor: arrayCostByCondition.find(cost => cost.item === item).vendor,
        numberOfOthers: numberOfOthers ? numberOfOthers : arrayCostByCondition.find(cost => cost.item === item).numberOfOthers,
        order: arrayCostByCondition.find(cost => cost.item === item).order,
        cost: arrayCostByCondition.find(cost => cost.item === item).cost,
      }
    })
}

function getCostsForMappingKeysWithVendor(costByCondition) {
  let arrayCostByCondition = []
  if (_isEmpty(costByCondition)) {
    return {};
  }
  costByCondition.forEach(data => {
    if(data.cost!=undefined){
      data.cost.forEach(item => {
        arrayCostByCondition.push(item)
      })
    }
  })
  let lastCostByCondition
  for (let i = costByCondition.length - 1; i >= 0; i--) {
    if (!_isEmpty(costByCondition[i].cost)) {
      lastCostByCondition = costByCondition[i].cost
      break;
    }
  }
  if (_isNil(lastCostByCondition) || _isNil(lastCostByCondition[lastCostByCondition.length - 1])) {
    return;
  }

  let isOthers = lastCostByCondition.filter(d=> {return d.isOthers === true })
  let numOthersByVendor = {};
  isOthers.forEach(d=>{ numOthersByVendor[d.vendor] = d.numberOfOthers })

  return Array.from(new Set(arrayCostByCondition.map(cost => cost.vendor+"-"+cost.item)))
    .map(item => {
      return {
        item: item,
        isOthers: arrayCostByCondition.find(cost => cost.item === item.substring(item.indexOf("-")+1)).isOthers,
        itemAlias: arrayCostByCondition.find(cost => cost.item === item.substring(item.indexOf("-")+1)).itemAlias,
        //vendor: arrayCostByCondition.find(cost => cost.item === item).vendor,
        vendor: item.substring(0,item.indexOf("-")),
        numberOfOthers: numOthersByVendor[ item.substring(0,item.indexOf("-"))],
        order: arrayCostByCondition.find(cost => cost.item === item.substring(item.indexOf("-")+1)).order,
        cost: arrayCostByCondition.find(cost => cost.item === item.substring(item.indexOf("-")+1)).cost,
      }
    })
}


function mappingKeysWithLabelForTrend(trends) {
  if (_isEmpty(trends)) {
    return {}
  }
  let listMonth = Object.keys(trends[0])
  listMonth = _remove(listMonth, (item) => {
    return item !== TIME_CONST;
  });
  let mappingKeysWithLabel = {};
  listMonth.forEach(key => {
    mappingKeysWithLabel[key] = `${key}`;
  });
  return mappingKeysWithLabel;
}

function getLastPeriodCost(dateType, dashboardCost) {
  let dataLength = dashboardCost.costByCondition.length;
  if (dataLength === 0) {
    return [];
  }
  let lastPeriodData = dashboardCost.costByCondition[dataLength - 1];

  for (let i = dataLength - 1; i >= 0; i--) {
    if (dashboardCost.costByCondition[i].cost && !_isEmpty(dashboardCost.costByCondition[i].cost)) {
      lastPeriodData = dashboardCost.costByCondition[i];
      break;
    }
  }
  return lastPeriodData.cost;
}

function getLastPeriodCostByVendor(dateType, dashboardCost) {
  let dataLength = dashboardCost.costByCondition.length;
  if (dataLength === 0) {
    return [];
  }
  let lastPeriodData = dashboardCost.costByCondition[dataLength - 1];

  for (let i = dataLength - 1; i >= 0; i--) {
    if (dashboardCost.costByCondition[i].cost && !_isEmpty(dashboardCost.costByCondition[i].cost)) {
      lastPeriodData = dashboardCost.costByCondition[i];
      break;
    }
  }
  return lastPeriodData.cost;
}

/**
 * Get order account keys by last period cost
 * Return accounts keys like
 * ["AWS-Amazon Elastic Compute", "AWS-Elastic Search", "AWS-AWS Lambda", "AWS-Amazon RDS", "AWS-Simple Storage Service", "AWS-others", "GCP-Cloud Storage", "GCP-Compute Engine",
 * "GCP-Data Transfer", "GCP-others", "AZURE-File Storage", "AZURE-Blob Storage", "AZURE-others"]
 *
 * @param lastPeriodCost
 */
function getOrderedAccountKeysByLastPeriodCost(lastPeriodCost) {
  if(_isEmpty(lastPeriodCost)){
    return;
  }
  let AWSAccounts = [];
  let GCPAccounts = [];
  let AZUREAccounts = [];
  let ALIAccounts = [];
  let IDCAccounts = [];
  let OCIAccounts = [];
  let NCPAccounts = [];
  let TencentAccounts = [];

  let awsTotalCost = 0;
  let gcbTotalCost = 0;
  let azureTotalCost = 0;
  let aliTotalCost = 0;
  let idcTotalCost = 0;
  let ociTotalCost = 0;
  let ncpTotalCost = 0;
  let tencentTotalCost = 0;

  //Group accounts by vendors
  for (let i = 0; i < lastPeriodCost.length; i++) {
    switch (lastPeriodCost[i].vendor) {
      case VENDORS.AWS:
        awsTotalCost += lastPeriodCost[i].cost;
        AWSAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.GCP:
        gcbTotalCost += lastPeriodCost[i].cost;
        GCPAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.AZURE:
        azureTotalCost += lastPeriodCost[i].cost;
        AZUREAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.ALI:
        aliTotalCost += lastPeriodCost[i].cost;
        ALIAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.IDC:
        idcTotalCost += lastPeriodCost[i].cost;
        IDCAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.OCI:
        ociTotalCost += lastPeriodCost[i].cost;
        OCIAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.NCP:
        ncpTotalCost += lastPeriodCost[i].cost;
        NCPAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.TENCENT:
        tencentTotalCost += lastPeriodCost[i].cost;
        TencentAccounts.push(lastPeriodCost[i]);
        break;
    }
  }

  //Sort accounts by value & isOthers
  AWSAccounts.sort(compareByCost).sort(compareByIsOthers);
  GCPAccounts.sort(compareByCost).sort(compareByIsOthers);
  AZUREAccounts.sort(compareByCost).sort(compareByIsOthers);
  ALIAccounts.sort(compareByCost).sort(compareByIsOthers);
  IDCAccounts.sort(compareByCost).sort(compareByIsOthers);
  OCIAccounts.sort(compareByCost).sort(compareByIsOthers);
  NCPAccounts.sort(compareByCost).sort(compareByIsOthers);
  TencentAccounts.sort(compareByCost).sort(compareByIsOthers);

  const totalCostVendors = [
    {
      'vendor': VENDORS.AWS,
      'totalCost': awsTotalCost
    },
    {
      'vendor': VENDORS.GCP,
      'totalCost': gcbTotalCost
    },
    {
      'vendor': VENDORS.AZURE,
      'totalCost': azureTotalCost
    },
    {
      'vendor': VENDORS.ALI,
      'totalCost': aliTotalCost
    },
    {
      'vendor': VENDORS.IDC,
      'totalCost': idcTotalCost
    },
    {
      'vendor': VENDORS.OCI,
      'totalCost': ociTotalCost
    },
    {
      'vendor': VENDORS.NCP,
      'totalCost': ncpTotalCost
    },
    {
      'vendor': VENDORS.TENCENT,
      'totalCost': tencentTotalCost
    }
  ];
  //Sort by total cost of vendors
  totalCostVendors.sort(compareByTotalCost);

  let orderedAccounts = [];
  for (let i = 0; i < totalCostVendors.length; i++) {
    switch (totalCostVendors[i].vendor) {
      case VENDORS.AWS:
        orderedAccounts = orderedAccounts.concat(AWSAccounts);
        break;
      case VENDORS.GCP:
        orderedAccounts = orderedAccounts.concat(GCPAccounts);
        break;
      case VENDORS.AZURE:
        orderedAccounts = orderedAccounts.concat(AZUREAccounts);
        break;
      case VENDORS.ALI:
        orderedAccounts = orderedAccounts.concat(ALIAccounts);
        break;
      case VENDORS.IDC:
        orderedAccounts = orderedAccounts.concat(IDCAccounts);
        break;
      case VENDORS.OCI:
        orderedAccounts = orderedAccounts.concat(OCIAccounts);
        break;
      case VENDORS.NCP:
        orderedAccounts = orderedAccounts.concat(NCPAccounts);
        break;
      case VENDORS.TENCENT:
        orderedAccounts = orderedAccounts.concat(TencentAccounts);
        break;
    }
  }

  let orderedAccountKeys = [];
  orderedAccounts.forEach(orderedAccount => { // No func : getAccountKeyByVendor
    orderedAccountKeys.push(getOriginalKeyByVendor(orderedAccount));
  });
  return orderedAccountKeys;
}

/**
 * Get order account keys by last period cost
 * Return accounts keys like
 * ["AWS-Amazon Elastic Compute", "AWS-Elastic Search", "AWS-AWS Lambda", "AWS-Amazon RDS", "AWS-Simple Storage Service", "AWS-others", "GCP-Cloud Storage", "GCP-Compute Engine",
 * "GCP-Data Transfer", "GCP-others", "AZURE-File Storage", "AZURE-Blob Storage", "AZURE-others"]
 *
 * @param lastPeriodCost
 */
function getOrderedItemKeysByVendorLastPeriodCost(lastPeriodCost,vendorOrder) {
  let AWSAccounts = [];
  let GCPAccounts = [];
  let AZUREAccounts = [];
  let ALIAccounts = [];
  let IDCAccounts = [];
  let OCIAccounts = [];
  let NCPAccounts = [];
  let TencentAccounts = [];

  let awsTotalCost = 0;
  let gcbTotalCost = 0;
  let azureTotalCost = 0;
  let aliTotalCost = 0;
  let idcTotalCost = 0;
  let ociTotalCost = 0;
  let ncpTotalCost = 0;
  let tencentTotalCost = 0;

  //Group accounts by vendors
  for (let i = 0; i < lastPeriodCost.length; i++) {
    switch (lastPeriodCost[i].vendor) {
      case VENDORS.AWS:
        awsTotalCost += lastPeriodCost[i].cost;
        AWSAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.GCP:
        gcbTotalCost += lastPeriodCost[i].cost;
        GCPAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.AZURE:
        azureTotalCost += lastPeriodCost[i].cost;
        AZUREAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.ALI:
        aliTotalCost += lastPeriodCost[i].cost;
        ALIAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.IDC:
        idcTotalCost += lastPeriodCost[i].cost;
        IDCAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.OCI:
        ociTotalCost += lastPeriodCost[i].cost;
        OCIAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.NCP:
        ncpTotalCost += lastPeriodCost[i].cost;
        NCPAccounts.push(lastPeriodCost[i]);
        break;
      case VENDORS.TENCENT:
        tencentTotalCost += lastPeriodCost[i].cost;
        TencentAccounts.push(lastPeriodCost[i]);
        break;
    }
  }

  //Sort accounts by value & isOthers
  AWSAccounts.sort(compareByCost).sort(compareByIsOthers);
  GCPAccounts.sort(compareByCost).sort(compareByIsOthers);
  AZUREAccounts.sort(compareByCost).sort(compareByIsOthers);
  ALIAccounts.sort(compareByCost).sort(compareByIsOthers);
  IDCAccounts.sort(compareByCost).sort(compareByIsOthers);
  OCIAccounts.sort(compareByCost).sort(compareByIsOthers);
  NCPAccounts.sort(compareByCost).sort(compareByIsOthers);
  TencentAccounts.sort(compareByCost).sort(compareByIsOthers);

  const totalCostVendors = [];

  vendorOrder.forEach(v=>{
    let tempObj = {'vendor':''}
    tempObj['vendor'] = v;
    totalCostVendors.push(tempObj);
  })

  let orderedAccounts = [];
  for (let i = 0; i < totalCostVendors.length; i++) {
    switch (totalCostVendors[i].vendor) {
      case VENDORS.AWS:
        orderedAccounts = orderedAccounts.concat(AWSAccounts);
        break;
      case VENDORS.GCP:
        orderedAccounts = orderedAccounts.concat(GCPAccounts);
        break;
      case VENDORS.AZURE:
        orderedAccounts = orderedAccounts.concat(AZUREAccounts);
        break;
      case VENDORS.ALI:
        orderedAccounts = orderedAccounts.concat(ALIAccounts);
        break;
      case VENDORS.IDC:
        orderedAccounts = orderedAccounts.concat(IDCAccounts);
        break;
      case VENDORS.OCI:
        orderedAccounts = orderedAccounts.concat(OCIAccounts);
        break;
      case VENDORS.NCP:
        orderedAccounts = orderedAccounts.concat(NCPAccounts);
      case VENDORS.TENCENT:
        orderedAccounts = orderedAccounts.concat(TencentAccounts);
        break;
    }
  }

  let orderedAccountKeys = [];
  orderedAccounts.forEach(orderedAccount => { // No func : getAccountKeyByVendor
    orderedAccountKeys.push(getOriginalKeyByVendor(orderedAccount));
  });
  return orderedAccountKeys;
}

function getDashboardCostChartColors(vendor){
  let colors = [];
  //void eval()
  switch (vendor.toUpperCase()){
    case "AWS":
      colors = AWS_COLORS.slice();
      break;
    case "GCP":
      colors = GCP_COLORS.slice();
      break;
    case "AZURE":
      colors = AZURE_COLORS.slice();
      break;
    case "ALI":
      colors = ALI_COLORS.slice();
      break;
    case "IDC":
      colors = IDC_COLORS.slice();
      break;
    case "OCI":
      colors = OCI_COLORS.slice();
      break;
    case "NCP":
      colors = NCP_COLORS.slice();
      break;
    case "TENCENT":
      colors = TENCENT_COLORS.slice();
      break;
  }

  return colors.reverse();
}

/***
 * Mapping vendor account with colors
 * Return object like
 *   {
 *     'AWS 1111111111111(Alias1)': #ac5b02,
 *     'AWS 222222222222(Alias2)': #be6501,
 *     'GCP 444444444444(Alias3)': #13773b,
 *   }
 *
 * @param orderedAccountKeysByLastPeriodCost
 */
function getMappingVendorAccountsWithColors(orderedAccountKeysByLastPeriodCost) {
  let mappingVendorAccountsWithColors = {};
  let awsIndex = 0;
  let azureIndex = 0;
  let gcpIndex = 0;
  let aliIndex = 0;
  let idcIndex = 0;
  let ociIndex = 0;
  let ncpIndex = 0;
  let tencentIndex = 0;

  for (let i = 0; i < orderedAccountKeysByLastPeriodCost.length; i++) {
    let color = '';
    if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.AWS) === 0) {
      color = AWS_COLORS[AWS_COLORS.length - 1 - awsIndex];
      awsIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.GCP) === 0) {
      color = GCP_COLORS[GCP_COLORS.length - 1 - gcpIndex];
      gcpIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.AZURE) === 0) {
      color = AZURE_COLORS[AZURE_COLORS.length - 1 - azureIndex];
      azureIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.ALI) === 0) {
      color = ALI_COLORS[ALI_COLORS.length - 1 - aliIndex];
      aliIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.IDC) === 0) {
      color = IDC_COLORS[IDC_COLORS.length - 1 - idcIndex];
      idcIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.OCI) === 0) {
      color = OCI_COLORS[OCI_COLORS.length - 1 - ociIndex];
      ociIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.NCP) === 0) {
      color = NCP_COLORS[NCP_COLORS.length - 1 - ncpIndex];
      ncpIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.TENCENT) === 0) {
      color = TENCENT_COLORS[TENCENT_COLORS.length - 1 - tencentIndex];
      tencentIndex ++;
    }

    mappingVendorAccountsWithColors[`${orderedAccountKeysByLastPeriodCost[i]}`] = color;
  }
  return mappingVendorAccountsWithColors;
}

function getMappingItemKeysByVendorWithColors(orderedAccountKeysByLastPeriodCost) {
  let mappingVendorAccountsWithColors = {};
  let awsIndex = 0;
  let azureIndex = 0;
  let gcpIndex = 0;
  let aliIndex = 0;
  let idcIndex = 0;
  let ociIndex = 0;
  let ncpIndex = 0;
  let tencentIndex = 0;


  for (let i = 0; i < orderedAccountKeysByLastPeriodCost.length; i++) {
    let color = '';
    if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.AWS) === 0) {
      color = AWS_COLORS[AWS_COLORS.length - 1 - awsIndex];
      awsIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.GCP) === 0) {
      color = GCP_COLORS[GCP_COLORS.length - 1 - gcpIndex];
      gcpIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.AZURE) === 0) {
      color = AZURE_COLORS[AZURE_COLORS.length - 1 - azureIndex];
      azureIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.ALI) === 0) {
      color = ALI_COLORS[ALI_COLORS.length - 1 - aliIndex];
      aliIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.IDC) === 0) {
      color = IDC_COLORS[IDC_COLORS.length - 1 - idcIndex];
      idcIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.OCI) === 0) {
      color = OCI_COLORS[OCI_COLORS.length - 1 - ociIndex];
      ociIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.NCP) === 0) {
      color = NCP_COLORS[NCP_COLORS.length - 1 - ncpIndex];
      ncpIndex ++;
    } else if (orderedAccountKeysByLastPeriodCost[i].indexOf(VENDORS.TENCENT) === 0) {
      color = TENCENT_COLORS[TENCENT_COLORS.length - 1 - tencentIndex];
      tencentIndex ++;
    }

    mappingVendorAccountsWithColors[`${orderedAccountKeysByLastPeriodCost[i]}`] = color;
  }
  return mappingVendorAccountsWithColors;
}


function getMappingMonthWithColors(trends) {
  let mappingMonthWithColors = {};
  let indexColor = 0;
  let listColor = [...COMPARE_COST_TREND_COLORS]
  if (_isEmpty(trends)) {
    return {}
  }
  let listMonth = Object.keys(trends[0]).reverse()
  listMonth = _remove(listMonth, (item) => {
    return item !== TIME_CONST;
  });
  for (let i = 0; i < listMonth.length; i++) {
    let color = '';
    color = listColor[indexColor];
    indexColor++;
    mappingMonthWithColors[listMonth[i]] = color;
  }
  return mappingMonthWithColors;
}

function formatTimeLabelChart(value, type) {
  if (value === CHART_ITEM_LABEL.ESTIMATED) {
    return value;
  }
  switch (type) {
    case FILTER_TIME.MONTH:
      return formatMonthYearByLocalization(value.substring(4, 6), value.substring(0, 4));
    case FILTER_TIME.DAY:
      return `${value.substring(6, 8)}/${value.substring(4, 6)}/${value.substring(0, 4)}`;
    default:
      return value;
  }
}

/**
 * Get week number and year from formatted weekly
 *
 * @param formattedWeekly
 * @returns {{weekNumber: number, year: number}}
 */
function getWeekNumberAndYearFromFormattedWeekly(formattedWeekly) {
  const formattedWeeklyArray = formattedWeekly.split('-');

  return {
    weekNumber: parseInt(formattedWeeklyArray[0].replace('W', '')),
    year: parseInt(formattedWeeklyArray[1])
  }
}

function generateNameForCopyOfDefaultDashboard(dashboardData) {

  const regex = new RegExp(CHECK_DUPLICATED_TEMPLATE_REGEX);
  let copyTimes = 0;

  dashboardData.forEach(item => {
    if (regex.test(item.dashboardName.toUpperCase())) {
      let matchResults = item.dashboardName.toUpperCase().match(CHECK_DUPLICATED_TEMPLATE_REGEX)[1];

      if (parseInt(matchResults) > copyTimes) {
        copyTimes = matchResults;
      }
    }
  });

  copyTimes++;

  return COPY_OF_DASHBOARD_TEMPLATE_NAME.replace(NUMBER_OF_COPY_WILDCARD, copyTimes);
}

function getSizeByWidgetType(widgetType) {
  return DASHBOARD_WIDGET_SIZES.find(widgetSizeItem => widgetSizeItem.widgetType === widgetType);
}
function isCostMonthToDateWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  // if (isPreviewCompare) {
  //   return currentWidget.selectedVendor !== newWidget.selectedVendor;
  // }
  return !(_.isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget));
}

function isEstimatedCostWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget === newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

function isYearCostFcstWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

function isYearCostFcstWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

function isCompareCostTrendWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.timeFrame !== newWidget.timeFrame ||
      !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget)||
          currentWidget.timeFrame !== newWidget.timeFrame;
}

function isDashboardCostWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget === newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.dateType !== newWidget.dateType
      || currentWidget.timeFrame !== newWidget.timeFrame
      || currentWidget.chartType !== newWidget.chartType
      || currentWidget.viewBy !== newWidget.viewBy
      || !_isEqual(currentWidget.selectedVendorsByWidget,newWidget.selectedVendorsByWidget)
      || !_isEqual(currentWidget.customFilter, newWidget.customFilter)
  }
  return currentWidget.viewBy !== newWidget.viewBy ||
    currentWidget.dateType !== newWidget.dateType ||
    currentWidget.timeFrame !== newWidget.timeFrame ||
    currentWidget.chartType !== newWidget.chartType ||
    currentWidget.scale !== newWidget.scale ||
    currentWidget.filter !== newWidget.filter ||
    !_isEqual(currentWidget.selectedVendorsByWidget,newWidget.selectedVendorsByWidget)||
    !_isEqual(currentWidget.customFilter, newWidget.customFilter);
}

function isMultiDashboardCostWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget === newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.dateType !== newWidget.dateType
      || currentWidget.timeFrame !== newWidget.timeFrame
      || currentWidget.chartType !== newWidget.chartType
      || currentWidget.viewBy !== newWidget.viewBy
      || !_isEqual(currentWidget.selectedVendorsByWidget,newWidget.selectedVendorsByWidget)
      //|| !_isEqual(currentWidget.customFilter, newWidget.customFilter) // 커스텀 아이템 필터
  }
  return currentWidget.viewBy !== newWidget.viewBy ||
    currentWidget.dateType !== newWidget.dateType ||
    currentWidget.timeFrame !== newWidget.timeFrame ||
    currentWidget.chartType !== newWidget.chartType ||
    currentWidget.scale !== newWidget.scale ||
    currentWidget.filter !== newWidget.filter ||
    !_isEqual(currentWidget.selectedVendorsByWidget,newWidget.selectedVendorsByWidget)
    //!_isEqual(currentWidget.customFilter, newWidget.customFilter); // 커스텀 아이템 필터
}

function isProductPortionWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.dateType !== newWidget.dateType
      || currentWidget.timeFrame !== newWidget.timeFrame
      || currentWidget.selectedAccount !== newWidget.selectedAccount
      || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return currentWidget.dateType !== newWidget.dateType ||
    currentWidget.timeFrame !== newWidget.timeFrame ||
    currentWidget.scale !== newWidget.scale ||
    currentWidget.selectedAccount !== newWidget.selectedAccount ||
    !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

function isIntegratedProductPortionWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.dateType !== newWidget.dateType
      || currentWidget.timeFrame !== newWidget.timeFrame
      // || currentWidget.selectedAccount !== newWidget.selectedAccount
      || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return currentWidget.dateType !== newWidget.dateType ||
    currentWidget.timeFrame !== newWidget.timeFrame ||
    currentWidget.scale !== newWidget.scale ||
    // currentWidget.selectedAccount !== newWidget.selectedAccount ||
    currentWidget.viewBy !== newWidget.viewBy ||
    !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

function allVendors(){
  // return ['GCP' , 'AZURE'];
  let curCmpnId = store.state.loginUser.curCmpnId;
  let vendorInfo = store.state.vendorInfo;
  if(curCmpnId && vendorInfo &&vendorInfo.length > 0){
    return vendorInfo;
  }else{
    return [''];
  }
}


function getSelectedVendorsByWidgetForMultiVendor(widgetConfig, $vm, availabVendorsByWidget, isText){ // 멀티 벤더 테스트
  let savedVendor = (!_isNil(widgetConfig) && !_isEmpty(widgetConfig.selectedVendorsByWidget) // [''],[],[null]
    &&!_isEmpty(widgetConfig.selectedVendorsByWidget[0])
    &&!_isEqual(widgetConfig.selectedVendorsByWidget[0], ''))
    ? widgetConfig.selectedVendorsByWidget
    : _isEmpty(_common.allVendors()) ? [''] : _common.allVendors();
  if(isText){
    return _common.getDefaultMultiVendorsByCheckedAuth(savedVendor, availableVendors(availabVendorsByWidget, $vm).map(vendorOption => {return vendorOption.text;}), isText);
  }
  return _common.getDefaultMultiVendorsByCheckedAuth(savedVendor, availableVendors(availabVendorsByWidget, $vm).map(vendorOption => {return vendorOption.value;}));
}

function getSelectedVendorsByWidget(widgetConfig, $vm, availabVendorsByWidget, isText){ //각 위젯에서 사용가능한 벤더 필터링
  let savedVendor = (!_isNil(widgetConfig) && !_isEmpty(widgetConfig.selectedVendorsByWidget) // [''],[],[null]
    &&!_isEmpty(widgetConfig.selectedVendorsByWidget[0])
    &&!_isEqual(widgetConfig.selectedVendorsByWidget[0], ''))
    ? widgetConfig.selectedVendorsByWidget[0]
    : _isEmpty(_common.allVendors()) ? '' : _common.allVendors()[0];

  if(isText){
    return _common.getDefaultVendorByCheckedAuth(savedVendor, availableVendors(availabVendorsByWidget, $vm).map(vendorOption => {return vendorOption.text;}), isText);
  }
  return _common.getDefaultVendorByCheckedAuth(savedVendor, availableVendors(availabVendorsByWidget, $vm).map(vendorOption => {return vendorOption.value;}));
}

// function getSelectedVendorsByWidgetForMultiVendor(widgetConfig, $vm, availableVendorsByWidget, isText){ //각 위젯에서 사용가능한 벤더 필터링
//   let savedVendor = (!_isNil(widgetConfig) && !_isEmpty(widgetConfig.selectedVendorsByWidget) // [''],[],[null]
//     &&!_isEmpty(widgetConfig.selectedVendorsByWidget[0])
//     &&!_isEqual(widgetConfig.selectedVendorsByWidget[0], ''))
//     ? widgetConfig.selectedVendorsByWidget
//     : _isEmpty(_common.allVendors()) ? '' : _common.allVendors();
//   if(isText){
//     return _common.getDefaultMultiVendorsByCheckedAuth(savedVendor, availableVendors(availableVendorsByWidget, $vm).map(vendorOption => {return vendorOption.text;}), isText);
//   }
//   return _common.getDefaultMultiVendorsByCheckedAuth(savedVendor, availableVendors(availableVendorsByWidget, $vm).map(vendorOption => {return vendorOption.value;}));
// }

function availableVendors(availabVendorsByWidget, $vm){ //위젯별로 가용한 벤더 리스트 조회
  return availabVendorsByWidget.filter(option =>_common.allVendors().includes(option.value)).map(vendor => {
    return {
      ...vendor,
      text: $vm.$t(vendor.text)
    };
  });
}

// function getDefaultVendorByCheckedAuth(vendor, availabVendors, isText){ // 권한 조회
//   if(isText){
//     let upperCase = availabVendors.map(vendor => {return vendor.toUpperCase()});
//     return !_isEmpty(upperCase) && upperCase.includes(vendor) && upperCase.indexOf(vendor) > -1
//       ? availabVendors[upperCase.indexOf(vendor)] : availabVendors.filter(v=> v.toUpperCase() != vendor).map(v=>{return v})[0];
//   }
//   return !_isEmpty(availabVendors) && availabVendors.includes(vendor)
//     ? vendor : availabVendors.filter(v=> v != vendor).map(v=>{return v})[0];
// }

function isPortionByWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }

  if (isPreviewCompare) {
    return currentWidget.viewBy !== newWidget.viewBy
      || currentWidget.dateType !== newWidget.dateType
      || currentWidget.timeFrame !== newWidget.timeFrame
      || currentWidget.selectedTagKey !== newWidget.selectedTagKey
      || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  return currentWidget.viewBy !== newWidget.viewBy ||
    currentWidget.dateType !== newWidget.dateType ||
    currentWidget.timeFrame !== newWidget.timeFrame ||
    currentWidget.scale !== newWidget.scale ||
    currentWidget.selectedTagKey !== newWidget.selectedTagKey ||
    !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);

}

function isAbnormalChangeWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.threshold !== newWidget.threshold
      || currentWidget.viewBy !== newWidget.viewBy
      || currentWidget.title !== newWidget.title
      || currentWidget.minAlert !== newWidget.minAlert
      || currentWidget.maxAlert !== newWidget.maxAlert
      || currentWidget.sensitivity !== newWidget.sensitivity
      || currentWidget.isAbnormalNotiOn !== newWidget.isAbnormalNotiOn
      || currentWidget.alarmCondition !== newWidget.alarmCondition
      || !_isEqual(currentWidget.mailSendCond, newWidget.mailSendCond)
      || !_isEqual(currentWidget.mailReceivers, newWidget.mailReceivers)
      || !_isEqual(currentWidget.alarmChannel, newWidget.alarmChannel)
      || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  if(newWidget.isColumnSave) {
    return true;
  }
  if (currentWidget.isColumnSave !== newWidget.isColumnSave) {
    return true;
  }
  return currentWidget.viewBy !== newWidget.viewBy ||
    currentWidget.timeFrame !== newWidget.timeFrame ||
    currentWidget.threshold !== newWidget.threshold ||
    currentWidget.colId !== newWidget.colId ||
    currentWidget.sortType !== newWidget.sortType ||
    currentWidget.isColumnSave !== newWidget.isColumnSave ||
    currentWidget.title !== newWidget.title ||
    currentWidget.minAlert !== newWidget.minAlert ||
    currentWidget.maxAlert !== newWidget.maxAlert ||
    currentWidget.sensitivity !== newWidget.sensitivity ||
    currentWidget.isAbnormalNotiOn !== newWidget.isAbnormalNotiOn ||
    currentWidget.alarmCondition !== newWidget.alarmCondition ||
    !_isEqual(currentWidget.mailSendCond, newWidget.mailSendCond) ||
    !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget) ||
    !_isEqual(currentWidget.mailReceivers, newWidget.mailReceivers) ||
    currentWidget.isAbnormalNotiOn !== newWidget.isAbnormalNotiOn;
}

function isAIAbnormalWidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  if (isPreviewCompare) {
    return currentWidget.threshold !== newWidget.threshold
      || currentWidget.viewBy !== newWidget.viewBy
      || currentWidget.title !== newWidget.title
      || currentWidget.minAlert !== newWidget.minAlert
      || currentWidget.maxAlert !== newWidget.maxAlert
      || currentWidget.sensitivity !== newWidget.sensitivity
      || currentWidget.isAbnormalNotiOn !== newWidget.isAbnormalNotiOn
      || currentWidget.mailSendCond !== newWidget.mailSendCond
      || currentWidget.mailReceivers !== newWidget.mailReceivers
      || currentWidget.alarmCondition !== newWidget.alarmCondition
      || !_isEqual(currentWidget.alarmChannel, newWidget.alarmChannel)
      // || !_isEqual(currentWidget.mailReceivers, newWidget.mailReceivers)
      || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }
  if(newWidget.isColumnSave) {
    return true;
  }
  if (currentWidget.isColumnSave !== newWidget.isColumnSave) {
    return true;
  }
  return currentWidget.viewBy !== newWidget.viewBy ||
    currentWidget.title !== newWidget.title ||
    currentWidget.minAlert !== newWidget.minAlert ||
    currentWidget.maxAlert !== newWidget.maxAlert ||
    currentWidget.sensitivity !== newWidget.sensitivity ||
    currentWidget.isAbnormalNotiOn !== newWidget.isAbnormalNotiOn ||
    currentWidget.mailSendCond !== newWidget.mailSendCond ||
    currentWidget.mailReceivers !== newWidget.mailReceivers ||
    currentWidget.alarmCondition !== newWidget.alarmCondition ||
    !_isEqual(currentWidget.alarmChannel, newWidget.alarmChannel)
    // !_isEqual(currentWidget.mailReceivers, newWidget.mailReceivers)
    !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

function isTop5WidgetDataConfigChanged(currentWidget, newWidget, isPreviewCompare) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }

  if (isPreviewCompare) {
    return currentWidget.timeFrame !== newWidget.timeFrame
      || currentWidget.viewBy !== newWidget.viewBy
      || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
  }

  return currentWidget.viewBy !== newWidget.viewBy
    || currentWidget.timeFrame !== newWidget.timeFrame
    || !_isEqual(currentWidget.selectedVendorsByWidget, newWidget.selectedVendorsByWidget);
}

/**
 * Check widget data is changed to enable save dashboard button
 *
 * @param widgetType
 * @param currentWidget
 * @param newWidget
 * @returns {*}
 */
function isWidgetDataConfigChanged(widgetType, currentWidget, newWidget) {
  if (currentWidget == newWidget) {
    return false;
  }
  if (_isNil(currentWidget) || _isNil(newWidget)) {
    return true;
  }
  switch (widgetType) {
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET: {
      return isEstimatedCostWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_YEAR_COST_FCST_WIDGET: {
      return isYearCostFcstWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET: {
      return isCompareCostTrendWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET: {
      return isDashboardCostWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET: {
      return isProductPortionWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET: {
      return isPortionByWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET: {
      return isAbnormalChangeWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET: {
      return isTop5WidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET: {
      return isAIAbnormalWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET: {
      return isAIAbnormalWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.INTEGRATED_PRODUCT_PORTION_WIDGET: {
      return isIntegratedProductPortionWidgetDataConfigChanged(currentWidget, newWidget);
    }
    case DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET: {
      return isMultiDashboardCostWidgetDataConfigChanged(currentWidget, newWidget)
    }
  }

  return false;
}

// newWidget is standardized by getStandardizedWidgetsForRender(), and layout changes are written to x, y, w, h
// originalWidget is not standardized, so it doesn't have w, h,...
function isWidgetLayoutConfigChanged(originalWidget, newWidget) {
  return newWidget.x !== originalWidget.x
    || newWidget.y !== originalWidget.y
    || newWidget.w !== originalWidget.width
    || newWidget.h !== originalWidget.height;
}

function getCompareCostTrendChartData(rawTrendData, currency, exchangeRate) {
  if (_isEmpty(rawTrendData)) {
    return [];
  }
  const maxDate = 31;
  let count = 0;
  let newDashboardCostData = {};
  newDashboardCostData[`labels`] = [];
  newDashboardCostData[`datasets`] = [];
  const currentMonth = dayjs().format('YYYYMM').toString();  // 현재 날짜 구하기
  for (let i = 0; i < maxDate; i++) {
    newDashboardCostData[`labels`].push(i+1);
  }

  for(let j=rawTrendData.length -1; j >= 0; j--){
    let dataObject = {};
    dataObject[`data`] = [];
    let item = rawTrendData[j];
    if(item.monthlyCost){
      for(let z=0; z<item.monthlyCost.length; z++) {
        let originalCost = item.monthlyCost[z].cost;
        dataObject[`data`].push(calculateCostByCurrency(originalCost, currency, exchangeRate));
      }
      dataObject[`tension`] = 0;
      dataObject[`borderColor`] = COMPARE_COST_TREND_COLORS.slice()[count];
      count += 1;
      if (item.date === currentMonth) {
        dataObject[`label`] = `thisMonth`;
        dataObject[`fill`] = true;
        dataObject[`borderColor`] = '#0672FF';
        dataObject[`backgroundColor`] = 'rgba(211, 234, 255, 0.28)';
        dataObject[`borderWidth`] = 2;
      } else {
        dataObject[`label`] = formatMonthYearByLocalization(item.date.substring(4, 6), item.date.substring(0, 4));
        dataObject[`fill`] = false;
        dataObject[`borderWidth`] = 1;
      }
    }
    newDashboardCostData[`datasets`].push(dataObject);
  }
  return _cloneDeep(newDashboardCostData);
}

function getCompareCostTrendChartData_backup(rawTrendData, currency, exchangeRate) {
  if (_isEmpty(rawTrendData)) {
    return [];
  }
  const maxDate = 31;
  const output = [];
  const currentMonth = dayjs().format('YYYYMM').toString();
  for (let dateIdx = 0; dateIdx < maxDate; dateIdx++) {
    output.push({time: formatDate(dateIdx + 1)});
    for (let lineIdx = 0; lineIdx < rawTrendData.length; lineIdx++) {
      if (rawTrendData[lineIdx].monthlyCost[dateIdx]) {
        const originalCost = rawTrendData[lineIdx].monthlyCost[dateIdx].cost;
        const displayedCost = `${formatCost(calculateCostByCurrency(originalCost, currency, exchangeRate))}`;
        if (rawTrendData[lineIdx].date === currentMonth) {
          output[dateIdx][`thisMonth`] = displayedCost;
        } else {
          output[dateIdx][rawTrendData[lineIdx].date] = displayedCost;
        }
      }
    }
  }
  return _cloneDeep(output);
}

function getAbnormalAIChartData(rawAIData, currency, exchangeRate){
  if (_isEmpty(rawAIData)) {
    return [];
  }

  let currencyCost = exchangeRate[currency];
  for(let aiData of rawAIData){
    let predictData = aiData['predictData'];
    predictData.forEach(data=>{
      if(data['close']!=undefined){
        data['close'] = formatCost(Math.round(Number(data['close'])*currencyCost*100)/100);
      }
      if(data['open']!=undefined){
        data['open'] = formatCost(Math.round(Number(data['open'])*currencyCost*100)/100);
      }
      if(data['date']!=undefined){
        let dateFormat = getFullDateFormatByLocalization();
        let formattedForecastDate = dayjs(data['date']).format(dateFormat);
        data['date'] = formattedForecastDate
      }
      return data;
    });

    let upperLowerData = aiData['upperLowerData'];
    upperLowerData.forEach(data=>{
      if(data['close']!=undefined){
        data['close'] = formatCost(Math.round(Number(data['close'])*currencyCost*100)/100);
      }
      if(data['open']!=undefined){
        data['open'] = formatCost(Math.round(Number(data['open'])*currencyCost*100)/100);
      }
      if(data['date']!=undefined){
        let dateFormat = getFullDateFormatByLocalization();
        let formattedForecastDate = dayjs(data['date']).format(dateFormat);
        data['date'] = formattedForecastDate
      }
      return data;
    });

    let trendData = aiData['trendData'];
    trendData.forEach(data=>{
                                if(data['close']!=undefined){
                                  data['close'] = formatCost(Math.round(Number(data['close'])*currencyCost*100)/100);
                                }
                                if(data['open']!=undefined){
                                  data['open'] = formatCost(Math.round(Number(data['open'])*currencyCost*100)/100);
                                }
                                if(data['date']!=undefined){
                                  let dateFormat = getFullDateFormatByLocalization();
                                  let formattedForecastDate = dayjs(data['date']).format(dateFormat);
                                  data['date'] = formattedForecastDate
                                }
                                if(data['detail']!=undefined && !_isEmpty(data['detail'])){
                                  for(let dt of data['detail']){
                                    if(dt['real']!=undefined){
                                      dt['real'] = formatCost(Math.round(Number(dt['real'])*currencyCost*100)/100);
                                    }
                                    if(dt['fcst']!=undefined){
                                      dt['fcst'] = formatCost(Math.round(Number(dt['fcst'])*currencyCost*100)/100);
                                    }
                                  }
                                }
                                return data;
                              });
  }

  return rawAIData;
}

function formatDate(date) {
  if (date < 10) {
    return '0'.concat(date);
  }
  return date.toString();
}

function getPortionMonthlyTimeFrameLabel(timeFrame, $vm) {
  if (timeFrame.isCurrent) {
    return $vm.$t('dashboard.portionTimeFrame.thisMonthSoFar')
  } else {
    const dateObject = splitYearMonthFromYYYYMM(timeFrame.time);
    return formatMonthYearByLocalization(dateObject.month, dateObject.year);
  }
}

function getPortionMonthlyTimeFrameListOption(timeFrames, $vm) {
  if (_isEmpty(timeFrames)) {
    return [];
  }
  return timeFrames.map(timeFrame => {
    return {
      text: getPortionMonthlyTimeFrameLabel(timeFrame, $vm),
      value: _toString(timeFrame.time),
      isCurrent: timeFrame.isCurrent
    }
  })
}

function getWeeklyTimeFrameLabel(timeFrame, $vm) {
  if (timeFrame.isCurrent) {
    return $vm.$t('dashboard.portionTimeFrame.thisWeekSoFar')
  } else {
    return `W${timeFrame.time}`;
  }
}

function getPortionWeeklyTimeFrameListOption(timeFrames, $vm) {
  if (_isEmpty(timeFrames)) {
    return [];
  }

  /*Sort time frame options by year and month, ex: ['W21-2019', 'W20-2019', ..., 'W52-2018', 'W51-2018']*/
  let sortedTimeFrames = timeFrames.sort(function(timeFrameOne, timeFrameTwo) {
    const timeFrameOneDate = timeFrameOne.time.split('-');
    const timeFrameOneMonth = timeFrameOneDate[0];
    const timeFrameOneYear = timeFrameOneDate[1];
    const timeFrameTwoDate = timeFrameTwo.time.split('-');
    const timeFrameTwoMonth = timeFrameTwoDate[0];
    const timeFrameTwoYear = timeFrameTwoDate[1];
    if (timeFrameTwoYear === timeFrameOneYear) {
      return timeFrameTwoMonth - timeFrameOneMonth;
    }

    return timeFrameTwoYear - timeFrameOneYear;
  });

  return sortedTimeFrames.map(timeFrame => {
    return {
      text: getWeeklyTimeFrameLabel(timeFrame, $vm),
      value: _toString(timeFrame.time),
      isCurrent: timeFrame.isCurrent
    }
  })
}

function getTimeFrameListOption(dateType, timeFrameList, $vm) {
  switch (dateType) {
    case DASHBOARD_DATE_TYPE.MONTHLY: {
      return getPortionMonthlyTimeFrameListOption(timeFrameList, $vm);
    }
    case DASHBOARD_DATE_TYPE.WEEKLY: {
      return getPortionWeeklyTimeFrameListOption(timeFrameList, $vm);
    }
  }

  return [];
}

function getWidgetConfigsAfterDuplicated(widgets, duplicatedWidget) {
  let positionUnchangedWidgets = [];
  let positionChangedWidgets = [];
  let defaultX = duplicatedWidget.x;

  widgets.forEach(widget => {
    if (widget.y > duplicatedWidget.y && duplicatedWidget.x + duplicatedWidget.w > WIDGET_MAX_SIZE / 2) {
      positionChangedWidgets.push(widget)
    } else {
      positionUnchangedWidgets.push(widget)
    }
  });

  //set new index for duplicatedWidget
  duplicatedWidget.index = Math.max.apply(Math, widgets.map(function (widget) {
    return widget.index;
  })) + 1;

  //set new i for duplicatedWidget
  duplicatedWidget.i = Math.max.apply(Math, widgets.map(function (widget) {
    return widget.i;
  })) + 1;

  positionChangedWidgets.push(duplicatedWidget);

  //calculate X
  if (duplicatedWidget.w <= WIDGET_MAX_SIZE / 2) {
    calculateXAfterDuplicated(widgets, duplicatedWidget)
  }

  //calculate Y
  if (!isMovedToRight(defaultX, duplicatedWidget.x)) {
    positionChangedWidgets.forEach(widget => {
      widget.y = widget.y + duplicatedWidget.h;
      widget.moved = true;
    });
  }
  return positionUnchangedWidgets.concat(positionChangedWidgets);
}

function isMovedToRight(defaultX, currentX) {
  return defaultX !== currentX
}

function prepareColorsForPortionByWidget(portion, hasOthers) {
  let colors = [];
  let colorRange = hasOthers ? portion.length - 1 : portion.length;
  for (let i = 0; i < portion.length; i++) {
    switch (portion[i].vendor) {
      case VENDORS.AWS:
        colors = getColorsRangeForPortion(colorRange, AWS_PORTION_COLORS);
        break;
      case VENDORS.GCP:
        colors = getColorsRangeForPortion(colorRange, GCP_PORTION_COLORS);
        break;
      case VENDORS.AZURE:
        colors = getColorsRangeForPortion(colorRange, AZURE_PORTION_COLORS);
        break;
      case VENDORS.ALI:
        colors = getColorsRangeForPortion(colorRange, ALI_COLORS);
        break;
      case VENDORS.IDC:
        colors = getColorsRangeForPortion(colorRange, IDC_COLORS);
        break;
      case VENDORS.OCI:
        colors = getColorsRangeForPortion(colorRange, OCI_PORTION_COLORS);
        break;
      case VENDORS.NCP:
        colors = getColorsRangeForPortion(colorRange, NCP_PORTION_COLORS);
        break;
      case VENDORS.TENCENT:
        colors = getColorsRangeForPortion(colorRange, Tencent_PORTION_COLORS);
        break;
      default :
        colors.push(DEFAULT_OTHER_COLOR);
    }
  }
  for (let i = 0; i < portion.length; i++) {
    portion[i].color = colors[i]
  }
}

function calculateXAfterDuplicated(widgets, duplicatedWidget) {
  if (duplicatedWidget.w <= WIDGET_MAX_SIZE / 2) {
    let totalWidth = duplicatedWidget.w;
    widgets.forEach(widget => {
      if (widget.y === duplicatedWidget.y) {
        totalWidth = totalWidth + widget.w;
      }
    });

    if (totalWidth <= WIDGET_MAX_SIZE && (duplicatedWidget.x + duplicatedWidget.w) <= WIDGET_MAX_SIZE / 2) {
      duplicatedWidget.x = totalWidth - duplicatedWidget.w;
    }
  }
}

function prepareColorsForPortion(portion, vendor, hasOthers) {
  let colors = [];
  let colorRange = hasOthers ? portion.length - 1 : portion.length;
  switch (vendor) {
    case VENDORS.AWS:
      colors = getColorsRangeForPortion(colorRange, AWS_PORTION_COLORS);
      break;
    case VENDORS.GCP:
      colors = getColorsRangeForPortion(colorRange, GCP_PORTION_COLORS);
      break;
    case VENDORS.AZURE:
      colors = getColorsRangeForPortion(colorRange, AZURE_PORTION_COLORS);
      break;
    case VENDORS.ALI:
      colors = getColorsRangeForPortion(colorRange, ALI_COLORS);
      break;
    case VENDORS.IDC:
      colors = getColorsRangeForPortion(colorRange, IDC_COLORS);
      break;
    case VENDORS.OCI:
      colors = getColorsRangeForPortion(colorRange, OCI_PORTION_COLORS);
      break;
    case VENDORS.NCP:
      colors = getColorsRangeForPortion(colorRange, NCP_PORTION_COLORS);
      break;
    case VENDORS.TENCENT:
      colors = getColorsRangeForPortion(colorRange, Tencent_PORTION_COLORS);
      break;
    default :
      colors = getColorsRangeForPortion(colorRange, AWS_PORTION_COLORS);
  }
  if (hasOthers) {
    colors.push(DEFAULT_OTHER_COLOR)
  }
  for (let i = 0; i < portion.length; i++) {
    portion[i].color = colors[i]
  }
}

function prepareBorderColorsForPortion(portion, vendor, hasOthers) {
  let borderColors = [];
  let borderColorRange = hasOthers ? portion.length - 1 : portion.length;
  switch (vendor) {
    case VENDORS.AWS:
      borderColors = getColorsRangeForPortion(borderColorRange, AWS_PORTION_BORDER_COLORS);
      break;
    case VENDORS.GCP:
      borderColors = getColorsRangeForPortion(borderColorRange, GCP_PORTION_BORDER_COLORS);
      break;
    case VENDORS.AZURE:
      borderColors = getColorsRangeForPortion(borderColorRange, AZURE_PORTION_BORDER_COLORS);
      break;
    case VENDORS.ALI:
      break;
    case VENDORS.IDC:
      break;
    case VENDORS.OCI:
      borderColors = getColorsRangeForPortion(borderColorRange, OCI_PORTION_BORDER_COLORS);
      break;
    case VENDORS.TENCENT:
      borderColors = getColorsRangeForPortion(borderColorRange, Tencent_PORTION_BORDER_COLORS);
      break;
    default :
      borderColors = getColorsRangeForPortion(borderColorRange, AWS_PORTION_BORDER_COLORS);
  }
  if (hasOthers) {
    borderColors.push(DEFAULT_OTHER_BORDER_COLOR)
  }
  for (let i = 0; i < portion.length; i++) {
    portion[i].borderColor = borderColors[i]
  }
}

function prepareColorsForMultiVendorIntegratedPortion(vendor) {
  switch (vendor.toUpperCase()) {
    case VENDORS.AWS:
      return AWS_COLORS[6]; //#FFAC15;
    case VENDORS.GCP:
      return GCP_COLORS[6]; //#C5E554;
    case VENDORS.AZURE:
      return AZURE_COLORS[6]; //#75A5DD;
    case VENDORS.ALI:
      return ALI_COLORS[6]; //#AB94FF;
    case VENDORS.IDC:
      return IDC_COLORS[6]; //#314E78;
    case VENDORS.OCI:
      return OCI_COLORS[6]; //#314E78;
    case VENDORS.NCP:
      return NCP_COLORS[6]; //#314E78;
    case VENDORS.TENCENT:
      return TENCENT_COLORS[6]; //#C9DC2B;
    default :
      return AWS_COLORS[6]; //#FFAC15;
  }
}

function prepareColorsForSingleVendorIntegratedPortion(portion, hasOthers, selectedVendors) {
  let colors = [];
  let colorRange = hasOthers ? portion.length - 1 : portion.length;
  selectedVendors.forEach(function(vendor) {
    switch (vendor.toUpperCase()) {
      case VENDORS.AWS:
        colors.push(getColorsRangeForPortion(colorRange, AWS_PORTION_COLORS));
        break;
      case VENDORS.GCP:
        colors.push(getColorsRangeForPortion(colorRange, GCP_PORTION_COLORS));
        break;
      case VENDORS.AZURE:
        colors.push(getColorsRangeForPortion(colorRange, AZURE_PORTION_COLORS));
        break;
      case VENDORS.ALI:
        colors.push(getColorsRangeForPortion(colorRange, ALI_COLORS));
        break;
      case VENDORS.IDC:
        colors.push(getColorsRangeForPortion(colorRange, IDC_COLORS));
        break;
      case VENDORS.OCI:
        colors.push(getColorsRangeForPortion(colorRange, OCI_PORTION_COLORS));
        break;
      case VENDORS.TENCENT:
        colors.push(getColorsRangeForPortion(colorRange, Tencent_PORTION_COLORS));
        break;
      case VENDORS.NCP:
        colors.push(getColorsRangeForPortion(colorRange, NCP_PORTION_COLORS));
        break;
      default :
        colors.push(getColorsRangeForPortion(colorRange, AWS_PORTION_COLORS));
    }
  })

  if (hasOthers) {
    colors.push(DEFAULT_OTHER_COLOR)
  }
  for (let i = 0; i < portion.length; i++) {
    selectedVendors.forEach(vendor => {
      portion[i][vendor+"_color"] = colors[selectedVendors.indexOf(vendor)][i];
    })
  }
}

function prepareBorderColorsForSingleVendorIntegratedPortion(portion, hasOthers, selectedVendors) {
  let borderColors = [];
  let borderColorRange = hasOthers ? portion.length - 1 : portion.length;
  selectedVendors.forEach(function(vendor) {
    switch (vendor.toUpperCase()) {
      case VENDORS.AWS:
        borderColors.push(getColorsRangeForPortion(borderColorRange, AWS_PORTION_COLORS));
        break;
      case VENDORS.GCP:
        borderColors.push(getColorsRangeForPortion(borderColorRange, GCP_PORTION_COLORS));
        break;
      case VENDORS.AZURE:
        borderColors.push(getColorsRangeForPortion(borderColorRange, AZURE_PORTION_COLORS));
        break;
      case VENDORS.ALI:
        borderColors.push(getColorsRangeForPortion(borderColorRange, ALI_COLORS));
        break;
      case VENDORS.IDC:
        borderColors.push(getColorsRangeForPortion(borderColorRange, IDC_COLORS));
        break;
      case VENDORS.OCI:
        borderColors.push(getColorsRangeForPortion(borderColorRange, OCI_PORTION_COLORS));
        break;
      case VENDORS.TENCENT:
        borderColors.push(getColorsRangeForPortion(borderColorRange, Tencent_PORTION_COLORS));
        break;
      case VENDORS.NCP:
        borderColors.push(getColorsRangeForPortion(borderColorRange, NCP_PORTION_COLORS));
        break;
      default :
        borderColors.push(getColorsRangeForPortion(borderColorRange, AWS_PORTION_COLORS));
    }
  })

  if (hasOthers) {
    borderColors.push(DEFAULT_OTHER_COLOR)
  }
  for (let i = 0; i < portion.length; i++) {
    selectedVendors.forEach(vendor => {
      portion[i][vendor+"_borderColor"] = borderColors[selectedVendors.indexOf(vendor)][i];
    })
  }
}

function prepareBorderColorsForPortionByWidget(portion, hasOthers) {
  let borderColors = [];
  let borderColorRange = hasOthers ? portion.length - 1 : portion.length;
  for (let i = 0; i < portion.length; i++) {
    switch (portion[i].vendor) {
      case VENDORS.AWS:
        borderColors = getColorsRangeForPortion(borderColorRange, AWS_PORTION_BORDER_COLORS);
        break;
      case VENDORS.GCP:
        borderColors = getColorsRangeForPortion(borderColorRange, GCP_PORTION_BORDER_COLORS);
        break;
      case VENDORS.AZURE:
        borderColors = getColorsRangeForPortion(borderColorRange, AZURE_PORTION_BORDER_COLORS);
        break;
      case VENDORS.ALI:
        break;
      case VENDORS.IDC:
        break;
      case VENDORS.OCI:
        borderColors = getColorsRangeForPortion(borderColorRange, OCI_PORTION_BORDER_COLORS);
        break;
      case VENDORS.TENCENT:
        borderColors = getColorsRangeForPortion(borderColorRange, Tencent_PORTION_BORDER_COLORS);
        break;
      default :
        borderColors.push(DEFAULT_OTHER_BORDER_COLOR);
    }
  }
  for (let i = 0; i < portion.length; i++) {
    portion[i].borderColor = borderColors[i]
  }
}

function getColorsRangeForPortion(colorRange, colors) {
  let results = [];
  for (let i = 0; i < colorRange; i++) {
    results.push(colors[colors.length - 1 - i])
  }
  return results;
}

// productPortion 데이터 primevue에 맞게 처리
function getPrimePortionChartData(rawPortionData, selectedVendors, currency, exchangeRate, scale) {
  let primeDashboardPortionData = {
    labels: [],
    datasets: [{
      data: [],
      backgroundColor: [],
      borderColor: [],
      borderWidth: 1
    }],
  }
  let temp = []
  let totalValue = 0;
  rawPortionData.forEach(portion => {
    totalValue += portion.cost;
  });

  rawPortionData.forEach(itemDashboardPortion => {
    let label = getDisplayProductPortionBaseOnProductFamilyForPrime(itemDashboardPortion.familyCode);
    primeDashboardPortionData.labels.push(label);
    if (scale == SCALE.VALUE) {
      let value = calculateCostByCurrency(itemDashboardPortion.cost, currency, exchangeRate);
      temp.push(value);
    } else if (scale == SCALE.PERCENTAGE) {
        let percentage = (itemDashboardPortion.cost * 100 / totalValue).toFixed(2);
        temp.push(percentage)
    };
    });
  primeDashboardPortionData.datasets[0].data = temp;

  primeDashboardPortionData.datasets[0].data.forEach(value => {
    primeDashboardPortionData.totalCost += value;
  });

  // vendor 별 Color 적용
  let vendor = rawPortionData[0].familyItems[0].vendor;

  primeDashboardPortionData.datasets[0].backgroundColor = primePortionDashboardBackgroundColor(primeDashboardPortionData.datasets[0].backgroundColor, vendor);
  primeDashboardPortionData.datasets[0].borderColor = primePortionDashboardBorderColor(primeDashboardPortionData.datasets[0].borderColor, vendor);

  // others 처리
  const othersIndex = primeDashboardPortionData.labels.findIndex(label => label === "others");
  if (othersIndex > -1) {
    primeDashboardPortionData.labels.splice(othersIndex, 1);
    const othersData = primeDashboardPortionData.datasets[0].data.splice(othersIndex, 1);

    primeDashboardPortionData.labels.push("others");
    primeDashboardPortionData.datasets[0].data.push(othersData[0]);

    const dataLength = primeDashboardPortionData.datasets[0].data.length;
    primeDashboardPortionData.datasets[0].backgroundColor = primeDashboardPortionData.datasets[0].backgroundColor.slice(0,dataLength - 1);
    primeDashboardPortionData.datasets[0].borderColor = primeDashboardPortionData.datasets[0].borderColor.slice(0,dataLength - 1);
    primeDashboardPortionData.datasets[0].backgroundColor.push(DEFAULT_OTHER_COLOR);
    primeDashboardPortionData.datasets[0].borderColor.push(DEFAULT_OTHER_BORDER_COLOR);
  }

  return primeDashboardPortionData;
}

function primePortionDashboardBackgroundColor(color, vendor) {
  let colors =[]
  switch (vendor) {
    case VENDORS.AWS:
      colors = AWS_PORTION_COLORS.slice().reverse();
      break;
    case VENDORS.GCP:
      colors = GCP_PORTION_COLORS.slice().reverse();
      break;
    case VENDORS.AZURE:
      colors = AZURE_PORTION_COLORS.slice().reverse();
      break;
    case VENDORS.ALI:
      colors = ALI_COLORS.slice().reverse();
      break;
    case VENDORS.IDC:
      colors = IDC_COLORS.slice().reverse();
      break;
    case VENDORS.OCI:
      colors = OCI_COLORS.slice().reverse();
      break;
    case VENDORS.NCP:
      colors = NCP_COLORS.slice().reverse();
      break;
    case VENDORS.TENCENT:
      colors = Tencent_PORTION_COLORS.slice().reverse();
      break;
    default :
      colors = AWS_PORTION_COLORS.slice().reverse();
  }
  return colors
}

function primePortionDashboardBorderColor(color, vendor) {
  let colors = []
  switch (vendor) {
    case VENDORS.AWS:
      colors = AWS_PORTION_BORDER_COLORS.slice().reverse();
      break;
    case VENDORS.GCP:
      colors = GCP_PORTION_BORDER_COLORS.slice().reverse();
      break;
    case VENDORS.AZURE:
      colors = AZURE_PORTION_BORDER_COLORS.slice().reverse();
      break;
    case VENDORS.ALI:
      colors = ALI_COLORS.slice().reverse();
      break;
    case VENDORS.IDC:
      colors = IDC_COLORS.slice().reverse();
      break;
    case VENDORS.OCI:
      colors = OCI_PORTION_BORDER_COLORS.slice().reverse();
      break;
    case VENDORS.NCP:
      colors = NCP_PORTION_BORDER_COLORS.slice().reverse();
      break;
    case VENDORS.TENCENT:
      colors = Tencent_PORTION_BORDER_COLORS.slice().reverse();
      break;
    default :
      colors = AWS_PORTION_BORDER_COLORS.slice().reverse();
  }
  return colors
}

function getPortionChartData(rawPortionData, vendor, currency, exchangeRate, widgetConfig, isAscendantSortByCost, i18nOthersText) {
  let dashboardPortionData = []
  rawPortionData.forEach(itemDashboardPortion => {
    let itemDashboardPortionData = {
      code: itemDashboardPortion.familyCode,
      children: [],
      viewBy: widgetConfig.viewBy,
      widgetType: widgetConfig.widgetType,
      vendor: itemDashboardPortion.vendor,
      isOthers: itemDashboardPortion.isOthers
    };
    let familyName = getDisplayProductPortionBaseOnProductFamily(itemDashboardPortion.familyCode);
    let totalChildrenCost = 0;
    if (itemDashboardPortion.familyItems) {
      itemDashboardPortion.familyItems.forEach(item => {
        if (item.item === itemDashboardPortion.familyCode) {
          familyName = getDisplayItemBaseOnPortionViewBy(item, widgetConfig.viewBy);
        }
        let childrenCost = calculateCostByCurrency(item.cost, currency, exchangeRate);
        totalChildrenCost += childrenCost;
        itemDashboardPortionData.children.push({
          name: item.item,
          cost: childrenCost,
          vendor: item.vendor,
          childCode: item.itemAlias
        });
      })
    }

    itemDashboardPortionData.name = familyName;
    itemDashboardPortionData.cost = totalChildrenCost;
    dashboardPortionData.push(itemDashboardPortionData);
  });
  let totalValue = 0;
  dashboardPortionData.forEach(portion => {
    totalValue += portion.cost;
  });

  let defaultPortion = dashboardPortionData.find(portion => portion.isOthers === true);

  dashboardPortionData = _remove(dashboardPortionData, (item) => {
    return item !== defaultPortion
  });

  dashboardPortionData.sort(compareByCost)

  if(defaultPortion) {
    defaultPortion.vendor = "default";
    defaultPortion.name = i18nOthersText
    dashboardPortionData.push(defaultPortion);
  }

  if (widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET) {
    prepareColorsForPortionByWidget(dashboardPortionData, defaultPortion);
    prepareBorderColorsForPortionByWidget(dashboardPortionData, defaultPortion);
  } else {
    prepareColorsForPortion(dashboardPortionData, vendor, defaultPortion);
    prepareBorderColorsForPortion(dashboardPortionData, vendor, defaultPortion);
  }
  preparePortionPercentage(dashboardPortionData, totalValue);
  return isAscendantSortByCost ? dashboardPortionData.reverse() : dashboardPortionData
}

function getIntegratedPortionChartData(rawPortionData, selectedVendors, currency, exchangeRate, widgetConfig, isAscendantSortByCost, i18nOthersText) {
  let dashboardPortionData = []
  const numOfSelectedVendors = selectedVendors.length;

  let totalValue = 0;
  rawPortionData.forEach(itemDashboardPortion => {
    let itemDashboardPortionData = {
      code: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? itemDashboardPortion.familyCode : itemDashboardPortion.familyCode.split(':::')[1],
      name: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? getDisplayProductPortionBaseOnProductFamily(itemDashboardPortion.familyCode) : itemDashboardPortion.familyItems[0].itemAlias.split(':::')[1],
      viewBy: widgetConfig.viewBy,
      widgetType: widgetConfig.widgetType,
      //others 처리
      isOthers: itemDashboardPortion.familyCode == "others" ? true : false,
      children: []
    };
    //totalChildrenCost는 해당 카테고리에서 발생한 모든 벤더의 비용 총합
    let totalChildrenCost = 0;
    let totalCostOfVendors = Array(numOfSelectedVendors).fill(0);

    if (itemDashboardPortion.familyItems) {
      itemDashboardPortion.familyItems.forEach(item => {
        const vendor = item.vendor;
        const idx = selectedVendors.indexOf(vendor);

        const childrenCost = calculateCostByCurrency(item.cost, currency, exchangeRate);
        totalValue += Math.abs(childrenCost);
        totalChildrenCost += childrenCost;
        totalCostOfVendors[idx] += childrenCost;
        const childrenName = selectedVendors[idx]+"_children";
        const colorName = selectedVendors[idx]+"_color";
        const borderColorName = selectedVendors[idx]+"_borderColor";

        let children = {
          name: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? item.item : item.itemAlias.split(':::')[1],
          //카테고리에 해당하는 각각의 데이터에 대한 비용
          cost: childrenCost,
          vendor: item.vendor,
          childCode: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? item.itemAlias : item.itemAlias.split(':::')[1]
        }
        if(itemDashboardPortionData[childrenName] === undefined) {
          itemDashboardPortionData[childrenName] = [];
        }
        itemDashboardPortionData[childrenName].push(children);
        itemDashboardPortionData.children.push(children);
        if(numOfSelectedVendors > 1) {
          const color = prepareColorsForMultiVendorIntegratedPortion(vendor);
          itemDashboardPortionData[colorName] = color;
          itemDashboardPortionData[borderColorName] = color;
        }
      })
    }

    //벤더별 해당 카테고리의 비용과 전체 비용 중 차지하는 비율 계산
    selectedVendors.forEach(function(v, i) {
      const cost = v + "_cost";
      const costOfVendor = totalCostOfVendors[selectedVendors.indexOf(v)];
      const colorName = v + "_color";
      const borderColor = v + "_borderColor";
      const children = v + "_children";
      if(itemDashboardPortionData[children] === undefined) {
        itemDashboardPortionData[children] = [];
        const color = prepareColorsForMultiVendorIntegratedPortion(v);
        itemDashboardPortionData[colorName] = color;
        itemDashboardPortionData[borderColor] = color;
      }
      //현재 벤더의 카테고리에서의 발생 비용
      itemDashboardPortionData[cost] = costOfVendor;
    })
    //totalChildrenCost는 해당 카테고리에서 발생한 모든 벤더의 비용 총합
    itemDashboardPortionData.cost = totalChildrenCost;

    dashboardPortionData.push(itemDashboardPortionData);
  });

  let defaultPortion = dashboardPortionData.find(portion => portion.isOthers === true);

  dashboardPortionData = _remove(dashboardPortionData, (item) => {
    return item !== defaultPortion
  });

  dashboardPortionData.sort(compareByCost)

  if(defaultPortion) {
    defaultPortion.vendor = "default";
    defaultPortion.name = i18nOthersText;
    dashboardPortionData.push(defaultPortion);
  }

  if(numOfSelectedVendors < 2) {
    prepareColorsForSingleVendorIntegratedPortion(dashboardPortionData, defaultPortion, selectedVendors);
    prepareBorderColorsForSingleVendorIntegratedPortion(dashboardPortionData, defaultPortion, selectedVendors);
  }

  preparePortionPercentageForMultiVendors(dashboardPortionData, totalValue, selectedVendors);

  return isAscendantSortByCost ? dashboardPortionData.reverse() : dashboardPortionData
}

function getCsvExportData(rawPortionData, vendor, currency, exchangeRate, widgetConfig, isAscendantSortByCost, i18nOthersText) {
  let dashboardPortionData = []
  rawPortionData.forEach(itemDashboardPortion => {
    let itemDashboardPortionData = {
      code: itemDashboardPortion.familyCode,
      children: [],
      viewBy: widgetConfig.viewBy,
      widgetType: widgetConfig.widgetType,
      vendor: itemDashboardPortion.vendor,
      isOthers: itemDashboardPortion.isOthers
    };
    let familyName = itemDashboardPortion.familyCode;
    let totalChildrenCost = 0;
    if (itemDashboardPortion.familyItems) {
      itemDashboardPortion.familyItems.forEach(item => {
        if (item.item === itemDashboardPortion.familyCode) {
          familyName = getDisplayItemBaseOnViewBy(item, widgetConfig.viewBy);
        }
        //let childrenCost = calculateCostByCurrencyForExport(item.cost, currency, exchangeRate);
        let childrenCost = item.cost;
        totalChildrenCost += childrenCost;
        itemDashboardPortionData.children.push({
          name: item.item,
          cost: childrenCost,
          vendor: item.vendor,
          childCode: item.itemAlias
        });
      })
    }

    itemDashboardPortionData.name = familyName;
    itemDashboardPortionData.cost = totalChildrenCost;
    dashboardPortionData.push(itemDashboardPortionData);
  });
  let totalValue = 0;
  dashboardPortionData.forEach(portion => {
    totalValue += portion.cost;
  });
  let defaultPortion = dashboardPortionData.find(portion => portion.isOthers === true);

  dashboardPortionData = _remove(dashboardPortionData, (item) => {
    return item !== defaultPortion
  });

  dashboardPortionData.sort(compareByCost)

  if(defaultPortion) {
    defaultPortion.vendor = "default";
    defaultPortion.name = i18nOthersText
    dashboardPortionData.push(defaultPortion);
  }
  if (widgetConfig.widgetType === DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET) {
    prepareColorsForPortionByWidget(dashboardPortionData, defaultPortion);
    prepareBorderColorsForPortionByWidget(dashboardPortionData, defaultPortion);
  } else {
    prepareColorsForPortion(dashboardPortionData, vendor, defaultPortion);
    prepareBorderColorsForPortion(dashboardPortionData, vendor, defaultPortion);
  }
  preparePortionPercentage(dashboardPortionData, totalValue);
  return isAscendantSortByCost ? dashboardPortionData.reverse() : dashboardPortionData
}

function getCsvExportDataForMultiVendors(rawPortionData, selectedVendors, currency, exchangeRate, widgetConfig, isAscendantSortByCost, i18nOthersText) {
  let dashboardPortionData = [];
  let totalValue = 0;
  rawPortionData.forEach(itemDashboardPortion => {
    let itemDashboardPortionData = {
      code: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? itemDashboardPortion.familyCode : itemDashboardPortion.familyCode.split(':::')[1],
      name: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? getDisplayProductPortionBaseOnProductFamily(itemDashboardPortion.familyCode) : itemDashboardPortion.familyItems[0].itemAlias.split(':::')[1],
      children: [],
      viewBy: widgetConfig.viewBy,
      widgetType: widgetConfig.widgetType,
      vendor: itemDashboardPortion.vendor,
      isOthers: itemDashboardPortion.isOthers
    };
    let familyName = itemDashboardPortion.familyCode;
    let totalChildrenCost = 0;
    if (itemDashboardPortion.familyItems) {
      itemDashboardPortion.familyItems.forEach(item => {
        if (item.item === itemDashboardPortion.familyCode) {
          familyName = getDisplayItemBaseOnViewBy(item, widgetConfig.viewBy);
        }
        //let childrenCost = calculateCostByCurrencyForExport(item.cost, currency, exchangeRate);
        let childrenCost = item.cost;
        totalValue += Math.abs(childrenCost);
        totalChildrenCost += childrenCost;
        itemDashboardPortionData.children.push({
          familyCode: item.familyCode,
          name: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? item.item : item.itemAlias.split(':::')[1],
          cost: childrenCost,
          vendor: item.vendor,
          childCode: !_.isEqual(widgetConfig.viewBy, "serviceGroup") ? item.itemAlias : item.itemAlias.split(':::')[1]
        });
      })
    }

    itemDashboardPortionData.name = familyName;
    itemDashboardPortionData.cost = totalChildrenCost;
    dashboardPortionData.push(itemDashboardPortionData);
  });
  let defaultPortion = dashboardPortionData.find(portion => portion.isOthers === true);

  dashboardPortionData = _remove(dashboardPortionData, (item) => {
    return item !== defaultPortion
  });

  dashboardPortionData.sort(compareByCost)

  if(defaultPortion) {
    defaultPortion.vendor = "default";
    defaultPortion.name = i18nOthersText
    dashboardPortionData.push(defaultPortion);
  }

  preparePortionPercentage(dashboardPortionData, totalValue);
  dashboardPortionData.totalCost = totalValue;
  return isAscendantSortByCost ? dashboardPortionData.reverse() : dashboardPortionData
}

function preparePortionPercentage(portionData, totalCost) {
  portionData.forEach(portion => {
    portion.percentage = Number(portion.cost * 100 / totalCost).toFixed(2);
    portion.totalCost = totalCost;
  })
}

function preparePortionPercentageForMultiVendors(portionData, totalCost, selectedVendors) {
  portionData.forEach(portion => {
    selectedVendors.forEach(vendor => {
      portion[vendor+"_percentage"] = portion[vendor+"_cost"] != 0 ? Number(Math.abs(portion[vendor+"_cost"]) * 100 / totalCost).toFixed(2) : "0.00";
      // portion.children.forEach(item => {
      //   //csv report에서 사용하기 위해 소수점 6자리까지 계산
      //   item.percentage =  item.cost !== undefined ? Number(Math.abs(item.cost) * 100 / totalCost).toFixed(6) : "0.000000";
      // })
    })
  })
}

function formatMonthlyTimeFrameRequestPayload(timeFrame, dateType) {
  if (_isEmpty(timeFrame)) {
    return "";
  }
  return dateType === DASHBOARD_DATE_TYPE.MONTHLY ? timeFrame.substring(0, 4) + "-" + timeFrame.substring(4, 6) : timeFrame
}

function getPortionMonthlyTimeFrame(monthlyTimeFrame, monthIndex, year, latestSummarizedBillDate) {
  let timeFramePayload = {
    startDate: '',
    endDate: ''
  };
  let date = new Date();
  timeFramePayload.startDate = monthlyTimeFrame.startOf('month').set('year', year).format(COST_ANALYTIC_DATE_FORMAT);
  if (monthIndex === dayjs(latestSummarizedBillDate).month()) {
    timeFramePayload.endDate = dayjs(latestSummarizedBillDate).format(COST_ANALYTIC_DATE_FORMAT);
  } else {
    timeFramePayload.endDate = monthlyTimeFrame.endOf('month').set('year', year).format(COST_ANALYTIC_DATE_FORMAT);
  }

  return timeFramePayload;
}

function getTagKeyListOption(tagKeys) {
  if (_isEmpty(tagKeys)) {
    return [];
  }
  orderTagKeys(tagKeys);
  return tagKeys.map(tagKey => {
    return {
      text: tagKey,
      value: _toString(tagKey)
    }
  })
}

function orderServiceGroupSets(sgSets) {
  if (_isEmpty(sgSets)) {
    return;
  }
  sgSets.sort((a, b) => {
    const aOptText = _toLower(a.serviceGroupSetNm);
    const bOptText = _toLower(b.serviceGroupSetNm);
    if( aOptText > bOptText ) {
      return 1;
    } else if ( aOptText < bOptText ) {
      return -1;
    } else {
      return 0;
    }
  });
}

function getServiceGroupSetListOption(sgSetList) {
  if (_isEmpty(sgSetList)) {
    return [];
  }
  orderServiceGroupSets(sgSetList);
  return sgSetList.map(sgSet => {
    return {
      text: sgSet.serviceGroupSetNm,
      value: sgSet.serviceGroupSetId,
    }
  })
}

function orderTagKeys(tagKeys) {
  if (_isEmpty(tagKeys)) {
    return;
  }
  tagKeys.sort((a,b) => {
    const aOptText = _toLower(a);
    const bOptText = _toLower(b);
    if (aOptText > bOptText) {
      return 1
    }
    if (aOptText < bOptText) {
      return -1;
    }
    return 0;
  });
}

export {
  orderTagKeys,
  getTagKeyListOption,
  getStandardizedWidgetsForRender,
  getStandardizedWidgetsForSave,
  getWidgetConfigWithFieldsInEditFormOnly,
  getMappingVendorAccountsWithColors,
  prepareDataForTop5Cost,
  getDashboardCostChartData,
  getDashboardCostChartData_backup,
  getDashboardCostPrimeChartData,
  getWeekNumberAndYearFromFormattedWeekly,
  mappingKeysWithLabelForCosts,
  mappingKeysWithLabelForVendor,
  generateNameForCopyOfDefaultDashboard,
  getSizeByWidgetType,
  mappingKeysWithLabelForTrend,
  getMappingMonthWithColors,
  isCostMonthToDateWidgetDataConfigChanged,
  isEstimatedCostWidgetDataConfigChanged,
  isYearCostFcstWidgetDataConfigChanged,
  isCompareCostTrendWidgetDataConfigChanged,
  isDashboardCostWidgetDataConfigChanged,
  isMultiDashboardCostWidgetDataConfigChanged,
  isProductPortionWidgetDataConfigChanged,
  isIntegratedProductPortionWidgetDataConfigChanged,
  isPortionByWidgetDataConfigChanged,
  isAbnormalChangeWidgetDataConfigChanged,
  isAIAbnormalWidgetDataConfigChanged,
  isTop5WidgetDataConfigChanged,
  getLastPeriodCost,
  getLastPeriodCostByVendor,
  getOrderedAccountKeysByLastPeriodCost,
  getOrderedItemKeysByVendorLastPeriodCost,
  isWidgetDataConfigChanged,
  isWidgetLayoutConfigChanged,
  getCompareCostTrendChartData,
  getAbnormalAIChartData,
  getTimeFrameListOption,
  getWidgetConfigsAfterDuplicated,
  prepareColorsForPortion,
  formatTimeLabelChart,
  getPrimePortionChartData,
  getPortionChartData,
  getIntegratedPortionChartData,
  getCsvExportData,
  getCsvExportDataForMultiVendors,
  getAccountKeyByVendor,
  formatMonthlyTimeFrameRequestPayload,
  getPortionMonthlyTimeFrame,
  getSelectedVendorsByWidget,
  availableVendors,
  getMultiDashboardCostChartData,
  getSelectedVendorsByWidgetForMultiVendor,
  getServiceGroupSetListOption,
  orderServiceGroupSets,
  getMappingItemKeysByVendorWithColors
};
