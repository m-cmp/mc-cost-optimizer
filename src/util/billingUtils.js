import _isEmpty from 'lodash/isEmpty';
import _isNil from 'lodash/isNil';
import {
  NON_TAG_NAME,
  TREE_LAYER_SEPARATOR,
  TREE_LAYER_SIZE,
  UNDEFINED_GROUP_NAME,
  USAGE_TYPE
} from "../constants/billingConstants";
import {calculateCostByCurrency, formatCost, formatPercentage} from '@/util/costUtils';
import {closestElement} from '@/util/htmlDocument';
import {CURRENCY, CURRENCY_SYMBOL, KRW_CURRENCY_FIELDS} from '@/constants/constants';
import {formatMonthYearByLocalization} from "@/util/dateTimeUtils";
import _cloneDeep from "lodash/cloneDeep";
import _ from "lodash";

const prepareMonthToDateOptions = (data, defaultDate, replaceCurrentMonthValue) => {
  let options = [];
  data.forEach(x => {
    options.push(formatMonthYearByLocalization(x.chargeMonth, x.chargeYear));
  });
  return options.reverse();
};

function prepareBillSummaryData(idx, billList) {
  return billList[idx];
}

function getReverseArrIndex(dataLength, billingIndex) {
  return dataLength - billingIndex - 1;
}

function groupDataByTag(cloudBillDetails, selectedTag, $vm) {
  if (_isEmpty(selectedTag)) {
    return [];
  }
  let selectedTags = [];
  let nonSelectedTags = [];

  cloudBillDetails.forEach(item => {
    if (item.tagValue === NON_TAG_NAME) {
      nonSelectedTags.push(item)
    } else {
      selectedTags.push(item)
    }
  });
  //selectedTags.sort((item1, item2) => item1.tagValue.localeCompare(item2.tagValue));
  return nonSelectedTags.concat(selectedTags); // [1.Non-Tag, 2.ServiceGroup]
  //return groupByTag(selectedTags, $vm).concat(groupByTag(nonSelectedTags, $vm));
}

function groupByTag(cloudBillDetails, $vm) { // 2.0 빌링 디테일과 동일한 트리 구조를 만들기 위해 5단 레이어로 변경
  let resultTagData = {};
  cloudBillDetails.forEach(item => {
    if (resultTagData[item.tagValue] === undefined) {
      resultTagData[item.tagValue] = {cost: 0, usage: 0, text: item.tagValue}
    }

    // results[item.tagValue].cost += item.cost;
    // results[item.tagValue].usage += item.usage;

    // let accountFullName = `${item.linkedAccountId}${item.linkedAccountAlias ? ` (${item.linkedAccountAlias})` : ''}`;
    // if (results[item.tagValue][accountFullName] === undefined) {
    //   results[item.tagValue][accountFullName] = {cost: 0, usage: 0, text: ''}
    // }

    resultTagData[item.tagValue].cost += item.cost;
    resultTagData[item.tagValue].usage += item.usage;
    //results[item.tagValue].text = results[item.tagValue].text + TREE_LAYER_SEPARATOR + TREE_LAYER_SIZE.LAYER_2_SIZE;

    if (resultTagData[item.tagValue][item.productName] === undefined) {
      resultTagData[item.tagValue][item.productName] = {cost: 0, usage: 0, text: ''}
    }

    resultTagData[item.tagValue][item.productName].cost += item.cost;
    resultTagData[item.tagValue][item.productName].usage += item.usage;
    //results[item.tagValue][item.productName].text = results[item.tagValue].text + TREE_LAYER_SEPARATOR + item.productName + TREE_LAYER_SIZE.LAYER_3_SIZE;
    resultTagData[item.tagValue][item.productName].text = resultTagData[item.tagValue].text + TREE_LAYER_SEPARATOR + item.productName + TREE_LAYER_SIZE.LAYER_2_SIZE;


    if (resultTagData[item.tagValue][item.productName][item.regionName] === undefined) {
      resultTagData[item.tagValue][item.productName][item.regionName] = {cost: 0, usage: 0, text: ''}
    }

    resultTagData[item.tagValue][item.productName][item.regionName].cost += item.cost;
    resultTagData[item.tagValue][item.productName][item.regionName].usage += item.usage;
    //results[item.tagValue][item.productName][item.regionName].text = results[item.tagValue][item.productName].text + TREE_LAYER_SEPARATOR + item.regionName + TREE_LAYER_SIZE.LAYER_4_SIZE;
    resultTagData[item.tagValue][item.productName][item.regionName].text = resultTagData[item.tagValue][item.productName].text + TREE_LAYER_SEPARATOR + item.regionName + TREE_LAYER_SIZE.LAYER_3_SIZE;

    if (resultTagData[item.tagValue][item.productName][item.regionName][item.usageType] === undefined) {
      resultTagData[item.tagValue][item.productName][item.regionName][item.usageType] = {
        cost: 0,
        usage: 0,
        text: '',
        descriptions: []
      }
    }
    resultTagData[item.tagValue][item.productName][item.regionName][item.usageType].cost += item.cost;
    resultTagData[item.tagValue][item.productName][item.regionName][item.usageType].usage += item.usage;
    //results[item.tagValue][item.productName][item.regionName][item.usageType].text = results[item.tagValue][item.productName][item.regionName].text + TREE_LAYER_SEPARATOR + item.usageType + TREE_LAYER_SIZE.LAYER_5_SIZE;
    resultTagData[item.tagValue][item.productName][item.regionName][item.usageType].text = resultTagData[item.tagValue][item.productName][item.regionName].text + TREE_LAYER_SEPARATOR + item.usageType + TREE_LAYER_SIZE.LAYER_4_SIZE;

    resultTagData[item.tagValue][item.productName][item.regionName][item.usageType].descriptions.push({
      text: resultTagData[item.tagValue][item.productName][item.regionName][item.usageType].text + TREE_LAYER_SEPARATOR + item.itemDescription,
      cost: item.cost,
      usage: item.usage
    });
  });
  //return prepareDataFor6LayerTreeTable(results, $vm);
  return prepareDataFor5LayerTreeTable(resultTagData, $vm);
}

function groupDataByServiceGroup(cloudBillDetails, selectedServiceGroup, $vm) {
  if (_isEmpty(selectedServiceGroup)) {
    return [];
  }
  let selectedSvgSets = [];
  let nonSelectedSvgSets = [];

  cloudBillDetails.forEach(item => {
    if (item.serviceGroup === UNDEFINED_GROUP_NAME) {
      nonSelectedSvgSets.push(item)
    } else {
      selectedSvgSets.push(item)
    }
  });
  selectedSvgSets.sort((item1, item2) => item1.serviceGroup.localeCompare(item2.serviceGroup));
  return nonSelectedSvgSets.concat(selectedSvgSets); // [1.Undefined Group, 2.ServiceGroup]
  //return groupByServiceGroup(selectedSvgSets, $vm).concat(groupByServiceGroup(nonSelectedSvgSets, $vm));
}

function groupByServiceGroup(cloudBillDetails, $vm) { // 2.0 빌링 디테일과 동일한 트리 구조를 만들기 위해 5단 레이어로 변경
  let resultSvgData = {};
  cloudBillDetails.forEach(item => {
    if (resultSvgData[item.serviceGroup] === undefined) {
      resultSvgData[item.serviceGroup] = {cost: 0, usage: 0, text: item.serviceGroup}
    }

    resultSvgData[item.serviceGroup].cost += item.cost;
    resultSvgData[item.serviceGroup].usage += item.usage;

    if (resultSvgData[item.serviceGroup][item.productName] === undefined) {
      resultSvgData[item.serviceGroup][item.productName] = {cost: 0, usage: 0, text: ''}
    }

    resultSvgData[item.serviceGroup][item.productName].cost += item.cost;
    resultSvgData[item.serviceGroup][item.productName].usage += item.usage;
    resultSvgData[item.serviceGroup][item.productName].text = resultSvgData[item.serviceGroup].text + TREE_LAYER_SEPARATOR + item.productName + TREE_LAYER_SIZE.LAYER_2_SIZE;


    if (resultSvgData[item.serviceGroup][item.productName][item.regionName] === undefined) {
      resultSvgData[item.serviceGroup][item.productName][item.regionName] = {cost: 0, usage: 0, text: ''}
    }

    resultSvgData[item.serviceGroup][item.productName][item.regionName].cost += item.cost;
    resultSvgData[item.serviceGroup][item.productName][item.regionName].usage += item.usage;
    resultSvgData[item.serviceGroup][item.productName][item.regionName].text = resultSvgData[item.serviceGroup][item.productName].text + TREE_LAYER_SEPARATOR + item.regionName + TREE_LAYER_SIZE.LAYER_3_SIZE;

    if (resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType] === undefined) {
      resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType] = {
        cost: 0,
        usage: 0,
        text: '',
        descriptions: []
      }
    }
    resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType].cost += item.cost;
    resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType].usage += item.usage;
    resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType].text = resultSvgData[item.serviceGroup][item.productName][item.regionName].text + TREE_LAYER_SEPARATOR + item.usageType + TREE_LAYER_SIZE.LAYER_4_SIZE;

    resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType].descriptions.push({
      text: resultSvgData[item.serviceGroup][item.productName][item.regionName][item.usageType].text + TREE_LAYER_SEPARATOR + item.itemDescription,
      cost: item.cost,
      usage: item.usage
    });
  });

  return prepareDataFor5LayerTreeTable(resultSvgData, $vm);
}

function groupDataByAccount(cloudBillDetails, $vm) {
  let results = {};
  cloudBillDetails.forEach(item => {
    if (results[item.linkedAccountId] === undefined) {
      results[item.linkedAccountId] = {cost: 0, usage: 0, text: `${item.linkedAccountId}${item.linkedAccountAlias ? ` (${item.linkedAccountAlias})` : ''}`}
    }

    results[item.linkedAccountId].cost += item.cost;
    results[item.linkedAccountId].usage += item.usage;

    if (results[item.linkedAccountId][item.productName] === undefined) {
      results[item.linkedAccountId][item.productName] = {cost: 0, usage: 0, text: ''}
    }

    results[item.linkedAccountId][item.productName].cost += item.cost;
    results[item.linkedAccountId][item.productName].usage += item.usage;
    results[item.linkedAccountId][item.productName].text = results[item.linkedAccountId].text + TREE_LAYER_SEPARATOR + item.productName + TREE_LAYER_SIZE.LAYER_2_SIZE;


    if (results[item.linkedAccountId][item.productName][item.regionName] === undefined) {
      results[item.linkedAccountId][item.productName][item.regionName] = {cost: 0, usage: 0, text: ''}
    }

    results[item.linkedAccountId][item.productName][item.regionName].cost += item.cost;
    results[item.linkedAccountId][item.productName][item.regionName].usage += item.usage;
    results[item.linkedAccountId][item.productName][item.regionName].text = results[item.linkedAccountId][item.productName].text + TREE_LAYER_SEPARATOR + item.regionName + TREE_LAYER_SIZE.LAYER_3_SIZE;

    if (results[item.linkedAccountId][item.productName][item.regionName][item.usageType] === undefined) {
      results[item.linkedAccountId][item.productName][item.regionName][item.usageType] = {
        cost: 0,
        usage: 0,
        text: '',
        descriptions: []
      }
    }
    results[item.linkedAccountId][item.productName][item.regionName][item.usageType].cost += item.cost;
    results[item.linkedAccountId][item.productName][item.regionName][item.usageType].usage += item.usage;
    results[item.linkedAccountId][item.productName][item.regionName][item.usageType].text = results[item.linkedAccountId][item.productName][item.regionName].text + TREE_LAYER_SEPARATOR + item.usageType + TREE_LAYER_SIZE.LAYER_4_SIZE;

    results[item.linkedAccountId][item.productName][item.regionName][item.usageType].descriptions.push({
      text: results[item.linkedAccountId][item.productName][item.regionName][item.usageType].text + TREE_LAYER_SEPARATOR + item.itemDescription,
      cost: item.cost,
      usage: item.usage
    });
  });
  return prepareDataFor5LayerTreeTable(results, $vm);
}

function groupDataByInvoice(cloudBillDetails, $vm) {
  let results = {};
  cloudBillDetails.forEach(item => {
    if (results[item.invoiceId] === undefined) {
      results[item.invoiceId] = {cost: 0, usage: 0, text: item.invoiceId}
    }

    results[item.invoiceId].cost += item.cost;
    results[item.invoiceId].usage += item.usage;

    if (results[item.invoiceId][item.productName] === undefined) {
      results[item.invoiceId][item.productName] = {cost: 0, usage: 0, text: ''}

    }

    results[item.invoiceId][item.productName].cost += item.cost;
    results[item.invoiceId][item.productName].usage += item.usage;
    results[item.invoiceId][item.productName].text = results[item.invoiceId].text + TREE_LAYER_SEPARATOR + item.productName + TREE_LAYER_SIZE.LAYER_2_SIZE;


    if (results[item.invoiceId][item.productName][item.regionName] === undefined) {
      results[item.invoiceId][item.productName][item.regionName] = {cost: 0, usage: 0, text: ''}
    }

    results[item.invoiceId][item.productName][item.regionName].cost += item.cost;
    results[item.invoiceId][item.productName][item.regionName].usage += item.usage;
    results[item.invoiceId][item.productName][item.regionName].text = results[item.invoiceId][item.productName].text + TREE_LAYER_SEPARATOR + item.regionName + TREE_LAYER_SIZE.LAYER_3_SIZE;

    if (results[item.invoiceId][item.productName][item.regionName][item.usageType] === undefined) {
      results[item.invoiceId][item.productName][item.regionName][item.usageType] = {
        cost: 0,
        usage: 0,
        text: '',
        descriptions: []
      }
    }
    results[item.invoiceId][item.productName][item.regionName][item.usageType].cost += item.cost;
    results[item.invoiceId][item.productName][item.regionName][item.usageType].usage += item.usage;
    results[item.invoiceId][item.productName][item.regionName][item.usageType].text = results[item.invoiceId][item.productName][item.regionName].text + TREE_LAYER_SEPARATOR + item.usageType + TREE_LAYER_SIZE.LAYER_4_SIZE;

    results[item.invoiceId][item.productName][item.regionName][item.usageType].descriptions.push({
      text: results[item.invoiceId][item.productName][item.regionName][item.usageType].text + TREE_LAYER_SEPARATOR + item.itemDescription,
      cost: item.cost,
      usage: item.usage
    });
  });
  return prepareDataFor5LayerTreeTable(results, $vm);
}

function groupDataByRegion(cloudBillDetails, $vm) {
  let results = {};
  cloudBillDetails.forEach(item => {
    if (results[item.regionName] === undefined) {
      results[item.regionName] = {cost: 0, usage: 0, text: item.regionName}
    }

    results[item.regionName].cost += item.cost;
    results[item.regionName].usage += item.usage;

    if (results[item.regionName][item.productName] === undefined) {
      results[item.regionName][item.productName] = {cost: 0, usage: 0, text: ''}
    }

    results[item.regionName][item.productName].cost += item.cost;
    results[item.regionName][item.productName].usage += item.usage;
    results[item.regionName][item.productName].text = results[item.regionName].text + TREE_LAYER_SEPARATOR + item.productName + TREE_LAYER_SIZE.LAYER_2_SIZE;


    if (results[item.regionName][item.productName][item.usageType] === undefined) {
      results[item.regionName][item.productName][item.usageType] = {
        cost: 0,
        usage: 0,
        text: '',
        descriptions: []
      }
    }
    results[item.regionName][item.productName][item.usageType].cost += item.cost;
    results[item.regionName][item.productName][item.usageType].usage += item.usage;
    results[item.regionName][item.productName][item.usageType].text = results[item.regionName][item.productName].text + TREE_LAYER_SEPARATOR + item.usageType + TREE_LAYER_SIZE.LAYER_3_SIZE;

    results[item.regionName][item.productName][item.usageType].descriptions.push({
      text: results[item.regionName][item.productName][item.usageType].text + TREE_LAYER_SEPARATOR + item.itemDescription,
      cost: item.cost,
      usage: item.usage
    });
  });
  return prepareDataFor4LayerTreeTable(results, $vm);
}

function groupingByAccount(cloudBillDetails){
  const inputData = _cloneDeep(cloudBillDetails);
  let filterKey = ["linkedAccountId", "linkedAccountAlias", "productName", "regionName", "usageType", "itemDescription"]
  let costData = {usage: 0.0, cost:0.0};

  let groupedData =  [...inputData.reduce((r,o)=> {
    let accountData = {
      // linkedAccountId : `${o.linkedAccountId}${o.linkedAccountAlias ? ` (${o.linkedAccountAlias})` : ''}`,
      linkedAccountId : `${o.linkedAccountAlias?o.linkedAccountAlias:o.linkedAccountId} (${o.linkedAccountId})`,
      productName : o.productName,
      regionName : o.regionName,
      usageType : o.usageType,
      itemDescription : o.itemDescription
    }

    if(! ('linkedAccountAlias' in o)){
      accountData.linkedAccountId = o.linkedAccountId;
    }

    const key = filterKey.map(_ => `${o[_]}`).join(" :: ");
    const item = r.get(key) || Object.assign({}, accountData, costData)

    item.usage += o.usage;
    item.cost += o.cost;

    return r.set(key,item);
  }, new Map).values()];
  return groupedData;
}

function groupingByInvoice(cloudBillDetails){
  const inputData = _cloneDeep(cloudBillDetails);
  let filterKey = ["invoiceId", "productName", "regionName", "usageType", "itemDescription"]
  let costData = {usage: 0.0, cost:0.0};

  let groupedData =  [...inputData.reduce((r,o)=> {
    let accountData = {
      invoiceId : o.invoiceId,
      productName : o.productName,
      regionName : o.regionName,
      usageType : o.usageType,
      itemDescription : o.itemDescription
    }

    const key = filterKey.map(_ => `${o[_]}`).join(" :: ");
    const item = r.get(key) || Object.assign({}, accountData, costData)

    item.usage += o.usage;
    item.cost += o.cost;

    return r.set(key,item);
  }, new Map).values()];
  return groupedData;
}

function groupingByRegion(cloudBillDetails){
  const inputData = _cloneDeep(cloudBillDetails);
  let filterKey = ["regionName", "productName","usageType", "itemDescription"]
  let costData = {usage: 0.0, cost:0.0};

  let groupedData =  [...inputData.reduce((r,o)=> {
    let accountData = {
      regionName : o.regionName,
      productName : o.productName,
      usageType : o.usageType,
      itemDescription : o.itemDescription
    }

    const key = filterKey.map(_ => `${o[_]}`).join(" :: ");
    const item = r.get(key) || Object.assign({}, accountData, costData)

    item.usage += o.usage;
    item.cost += o.cost;

    return r.set(key,item);
  }, new Map).values()];
  return groupedData;
}

function groupingByTag(cloudBillDetails){
  const inputData = _cloneDeep(cloudBillDetails);
  let filterKey = ["tagValue", "productName","regionName","usageType", "itemDescription"]
  let costData = {usage: 0.0, cost:0.0};

  let groupedData =  [...inputData.reduce((r,o)=> {
    let accountData = {
      tagValue : o.tagValue,
      productName : o.productName,
      usageType : o.usageType,
      itemDescription : o.itemDescription
    }

    const key = filterKey.map(_ => `${o[_]}`).join(" :: ");
    const item = r.get(key) || Object.assign({}, accountData, costData)

    item.usage += o.usage;
    item.cost += o.cost;

    return r.set(key,item);
  }, new Map).values()];
  return groupedData;
}

function groupingByServiceGroup(cloudBillDetails){
  const inputData = _cloneDeep(cloudBillDetails);
  let filterKey = ["serviceGroup", "productName","regionName","usageType", "itemDescription"]
  let costData = {usage: 0.0, cost:0.0};

  let groupedData =  [...inputData.reduce((r,o)=> {
    let accountData = {
      serviceGroup : o.serviceGroup,
      productName : o.productName,
      usageType : o.usageType,
      itemDescription : o.itemDescription
    }

    const key = filterKey.map(_ => `${o[_]}`).join(" :: ");
    const item = r.get(key) || Object.assign({}, accountData, costData)

    item.usage += o.usage;
    item.cost += o.cost;

    return r.set(key,item);
  }, new Map).values()];
  return groupedData;
}

function prepareDataFor5LayerTreeTable(groupedData, $vm) {
  const results = [];
  let id = 1;
  Object.keys(groupedData).forEach(firstLayer => {
      if (!isValidTreeMapProperties(firstLayer)) {
        return
      }
      let layer1Size = Object.keys(groupedData[firstLayer]).length - 3;
      results.push({
        "id": id++,
        "description": formatDescription(groupedData[firstLayer].text),
        "cost": convertExponentialNumberToDecimal(groupedData[firstLayer].cost),
        "usage": "",
        /*"usage": formatCost(convertExponentialNumberToDecimal(groupedData[firstLayer].usage))*/
      });
      Object.keys(groupedData[firstLayer]).forEach(secondLayer => {
        if (!isValidTreeMapProperties(secondLayer)) {
          return
        }
        let layer2Size = Object.keys(groupedData[firstLayer][secondLayer]).length - 3;
        let layer2FormatParams = [
          {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
          {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE}];
        results.push({
          "id": id++,
          "description": formatDescription(groupedData[firstLayer][secondLayer].text, layer2FormatParams),
          "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer].cost),
          "usage": "",
          /*"usage": formatCost(convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer].usage))*/
        });
        Object.keys(groupedData[firstLayer][secondLayer]).forEach(thirdLayer => {
          if (!isValidTreeMapProperties(thirdLayer)) {
            return
          }
          let layer3Size = Object.keys(groupedData[firstLayer][secondLayer][thirdLayer]).length - 3;
          let layer3formatParams = [
            {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
            {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
            {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE}]
          results.push({
            "id": id++,
            "description": formatDescription(groupedData[firstLayer][secondLayer][thirdLayer].text, layer3formatParams),
            "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer].cost),
            "usage": "",
            /*"usage": formatCost(convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer].usage))*/
          });
          Object.keys(groupedData[firstLayer][secondLayer][thirdLayer]).forEach(fourthLayer => {
            if (!isValidTreeMapProperties(fourthLayer)) {
              return;
            }
            let layer4Size = groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].descriptions.length;
            let layer4formatParams = [
              {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
              {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
              {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE},
              {value: layer4Size, key: TREE_LAYER_SIZE.LAYER_4_SIZE}
            ];
            results.push({
              "id": id++,
              "description": formatDescription(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].text, layer4formatParams),
              "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].cost),
              "usage": formatCost(convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].usage)),
              "usageType": USAGE_TYPE.TOTAL_VALUE
            });
            Object.keys(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer]).forEach(fifthLayerKey => {
              let descMap = {};
              if (!isValidTreeMapProperties(fifthLayerKey)) {
                return
              }
              groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].descriptions.forEach(fifthLayer => {
                descMap[fifthLayer.text] = descMap[fifthLayer.text] ? (descMap[fifthLayer.text] + 1) : 1;

                let fifthLayerText = (descMap[fifthLayer.text] >= 2)
                  ? `${fifthLayer.text} (${descMap[fifthLayer.text]})`
                  : fifthLayer.text;

                results.push({
                  "id": id++,
                  "description": formatDescription(fifthLayerText, [
                    {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
                    {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
                    {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE},
                    {value: layer4Size, key: TREE_LAYER_SIZE.LAYER_4_SIZE}]),
                  "cost": convertExponentialNumberToDecimal(fifthLayer.cost),
                  "usage": formatCost(convertExponentialNumberToDecimal(fifthLayer.usage)),
                  "usageType": USAGE_TYPE.ADDEND_VALUE
                });
              })
            });
          })
        });
      });
    }
  )
  ;
  return results;
}

function prepareDataFor6LayerTreeTable(groupedData, $vm) {
  const results = [];
  let id = 1;
  Object.keys(groupedData).forEach(firstLayer => {
      if (!isValidTreeMapProperties(firstLayer)) {
        return
      }
      let layer1Size = Object.keys(groupedData[firstLayer]).length - 3;
      results.push({
        "id": id++,
        "description": formatDescription(groupedData[firstLayer].text),
        "cost": convertExponentialNumberToDecimal(groupedData[firstLayer].cost),
        "usage": "",
      });
      Object.keys(groupedData[firstLayer]).forEach(secondLayer => {
        if (!isValidTreeMapProperties(secondLayer)) {
          return
        }
        let layer2Size = Object.keys(groupedData[firstLayer][secondLayer]).length - 3;
        let layer2FormatParams = [
          {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
          {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE}];
        results.push({
          "id": id++,
          "description": formatDescription(groupedData[firstLayer][secondLayer].text, layer2FormatParams),
          "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer].cost),
          "usage": "",
        });
        Object.keys(groupedData[firstLayer][secondLayer]).forEach(thirdLayer => {
          if (!isValidTreeMapProperties(thirdLayer)) {
            return
          }
          let layer3Size = Object.keys(groupedData[firstLayer][secondLayer][thirdLayer]).length - 3;
          let layer3formatParams = [
            {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
            {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
            {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE}]
          results.push({
            "id": id++,
            "description": formatDescription(groupedData[firstLayer][secondLayer][thirdLayer].text, layer3formatParams),
            "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer].cost),
            "usage": "",
          });
          Object.keys(groupedData[firstLayer][secondLayer][thirdLayer]).forEach(fourthLayer => {
            if (!isValidTreeMapProperties(fourthLayer)) {
              return;
            }
            let layer4Size = Object.keys(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer]).length - 3;
            let layer4formatParams = [
              {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
              {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
              {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE},
              {value: layer4Size, key: TREE_LAYER_SIZE.LAYER_4_SIZE}
            ];
            results.push({
              "id": id++,
              "description": formatDescription(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].text, layer4formatParams),
              "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer].cost),
              "usage": ""
            });
            Object.keys(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer]).forEach(fifthLayerKey => {
              if (!isValidTreeMapProperties(fifthLayerKey)) {
                return;
              }
              let layer5Size = groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer][fifthLayerKey].descriptions.length;
              let layer5formatParams = [
                {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
                {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
                {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE},
                {value: layer4Size, key: TREE_LAYER_SIZE.LAYER_4_SIZE},
                {value: layer5Size, key: TREE_LAYER_SIZE.LAYER_5_SIZE}
              ];
              results.push({
                "id": id++,
                "description": formatDescription(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer][fifthLayerKey].text, layer5formatParams),
                "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer][fifthLayerKey].cost),
                "usage": formatCost(convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer][fifthLayerKey].usage)),
                "usageType": USAGE_TYPE.ADDEND_VALUE
              });
              Object.keys(groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer][fifthLayerKey]).forEach(sixthLater => {
                let descMap = {};
                if (!isValidTreeMapProperties(sixthLater)) {
                  return
                }
                groupedData[firstLayer][secondLayer][thirdLayer][fourthLayer][fifthLayerKey].descriptions.forEach(sixthLayer => {
                  descMap[sixthLayer.text] = descMap[sixthLayer.text] ? (descMap[sixthLayer.text] + 1) : 1;

                  let fifthLayerText = (descMap[sixthLayer.text] >= 2)
                    ? `${sixthLayer.text} (${descMap[sixthLayer.text]})`
                    : sixthLayer.text;

                  results.push({
                    "id": id++,
                    "description": formatDescription(fifthLayerText, [
                      {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
                      {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
                      {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE},
                      {value: layer4Size, key: TREE_LAYER_SIZE.LAYER_4_SIZE},
                      {value: layer5Size, key: TREE_LAYER_SIZE.LAYER_5_SIZE}]),
                    "cost": convertExponentialNumberToDecimal(sixthLayer.cost),
                    "usage": formatCost(convertExponentialNumberToDecimal(sixthLayer.usage)),
                    "usageType": USAGE_TYPE.ADDEND_VALUE
                  });
                })
              })
            });
          })
        });
      });
    }
  );
  return results;
}

function prepareDataFor4LayerTreeTable(groupedData, $vm) {
  const results = [];
  Object.keys(groupedData).forEach(firstLayer => {
    if (!isValidTreeMapProperties(firstLayer)) {
      return
    }
    let layer1Size = Object.keys(groupedData[firstLayer]).length - 3;
    results.push({
      "description": formatDescription(groupedData[firstLayer].text),
      "cost": convertExponentialNumberToDecimal(groupedData[firstLayer].cost),
      "usage": "",
    });
    Object.keys(groupedData[firstLayer]).forEach(secondLayer => {
      if (!isValidTreeMapProperties(secondLayer)) {
        return
      }
      let layer2Size = Object.keys(groupedData[firstLayer][secondLayer]).length - 3;
      let layer2FormatParams = [
        {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
        {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE}];
      results.push({
        "description": formatDescription(groupedData[firstLayer][secondLayer].text, layer2FormatParams),
        "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer].cost),
        "usage": "",
      });
      Object.keys(groupedData[firstLayer][secondLayer]).forEach(thirdLayer => {
        if (!isValidTreeMapProperties(thirdLayer)) {
          return
        }
        let layer3Size = groupedData[firstLayer][secondLayer][thirdLayer].descriptions.length;
        let layer3FormatParams = [
          {value: layer1Size, key: TREE_LAYER_SIZE.LAYER_1_SIZE},
          {value: layer2Size, key: TREE_LAYER_SIZE.LAYER_2_SIZE},
          {value: layer3Size, key: TREE_LAYER_SIZE.LAYER_3_SIZE}];
        results.push({
          "description": formatDescription(groupedData[firstLayer][secondLayer][thirdLayer].text, layer3FormatParams),
          "cost": convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer].cost),
          "usage": formatCost(convertExponentialNumberToDecimal(groupedData[firstLayer][secondLayer][thirdLayer].usage)),
          "usageType": USAGE_TYPE.TOTAL_VALUE
        });
        Object.keys(groupedData[firstLayer][secondLayer][thirdLayer]).forEach(desc => {
          if (!isValidTreeMapProperties(desc)) {
            return
          }
          let descMap = {};
          groupedData[firstLayer][secondLayer][thirdLayer].descriptions.forEach(forthLayer => {
            descMap[forthLayer.text] = descMap[forthLayer.text] ? (descMap[forthLayer.text] + 1) : 1;

            let fifthLayerText = (descMap[forthLayer.text] >= 2)
              ? `${forthLayer.text} (${descMap[forthLayer.text]})`
              : forthLayer.text;
            results.push({
              "description": formatDescription(fifthLayerText, layer3FormatParams),
              "cost": convertExponentialNumberToDecimal(forthLayer.cost),
              "usage": formatCost(convertExponentialNumberToDecimal(forthLayer.usage)),
              "usageType": USAGE_TYPE.ADDEND_VALUE
            });
          })
        });
      })
    });
  });
  return results;
}

function formatDescription(textValue, layerSizeArr) {
  try {
    let replaceText = textValue;
    if (!_isEmpty(layerSizeArr)) {
      layerSizeArr.forEach(item => {
        replaceText = replaceText.replace(item.key, " (" + item.value + ")");
      });
    }
    return replaceText.split(TREE_LAYER_SEPARATOR);
  } catch (e) {
    return ""
  }
}

function convertExponentialNumberToDecimal(exponentialNumber) {
  if (!exponentialNumber) {
    return exponentialNumber;
  }
  const str = exponentialNumber.toString();
  if (str.includes('e')) {
    const exponent = parseInt(str.split('-')[1], 10);
    const result = exponentialNumber.toFixed(exponent);
    return result;
  } else {
    return exponentialNumber;
  }
}

function isValidTreeMapProperties(prop) {
  return !['text', 'cost', 'usage'].includes(prop)
}

function classifyBillingSummaryData(data) {
  let results = {
    billingWithTagKey: [],
    billingWithNonTagKey: [],
    billingDetailWithSvgSet: [],
    billingWithUndefinedSvgSet: []
  };

  if (_isEmpty(data)) {
    return results;
  }

  data.forEach(item => {
    if (_isEmpty(item.tagKey)) {
      results.billingWithNonTagKey.push(item)
    } else {
      results.billingWithTagKey.push(item)
    }
  });

  data.forEach(item => {
    if (_isEmpty(item.serviceGroup)) {
      results.billingWithUndefinedSvgSet.push(item)
    } else {
      results.billingDetailWithSvgSet.push(item)
    }
  });

  return results;
}

// function formatCostValue(cost, config) {
//   if(KRW_CURRENCY_FIELDS.includes(config)) {
//     return formatCost(cost, {mantissa: 0})
//   }
//   return formatCost(cost)
// }

function formatCostValue(cost, config, companyCurrency, vendor) {
  switch (vendor) {
    case 'AWS':
      return formatCostValueAws(cost, config, companyCurrency);
    case 'GCP':
      return formatCostValueGcp(cost, config, companyCurrency);
    case '':
    case null:
      return;
    default:
      return commonFormatCost(cost, config, companyCurrency);
  }
}

function formatCostValueAws(cost, config, companyCurrency) {
  if(KRW_CURRENCY_FIELDS.includes(config) && (companyCurrency === undefined ||_.isEqual(CURRENCY.KRW, companyCurrency))) {
    return formatCost(cost, {mantissa: 0})
  } else {
    return formatCost(cost, {mantissa: 2});
  }
}

function commonFormatCost(cost, config, companyCurrency) {
  if(_.isEqual(CURRENCY.KRW, companyCurrency)) {
    return formatCost(cost, {mantissa: 0});
  } else {
    return formatCost(cost, {mantissa: 2});
  }
}
function formatCostValueGcp(cost, config, companyCurrency) {
  if(_.isEqual(CURRENCY.KRW, companyCurrency)) {
    let newCost = Number(parseFloat(cost));
    if(newCost <= -1) {
      newCost = Math.floor(newCost * -1);
    } else if(newCost < 0 && newCost > -1) {
      newCost = Math.floor(Math.abs(newCost));
    } else {
      newCost = Math.floor(newCost);
    }
    return formatCost(newCost, {mantissa: 0});
  } else {
    return formatCost(cost, {mantissa: 2});
  }
}

function customFormatCostValue(cost, config, selectedCurrency) {
  let mantissa;
  switch (selectedCurrency) {
    case 'KRW' :
      mantissa = 0;
      break;
    case 'USD' :
    case 'CNY' :
      mantissa = 2;
      break;
    default :
      mantissa = 2;
      break;
  }
  return formatCost(cost, {mantissa: mantissa})
}

function formatUsageValue(value, $vm) {
  let usage = formatCost(value);
  return $vm.$t('billing.cloudBillDetails.usageDescription', {'usage': usage});
}

function formatTotalUsageValue(value, $vm) {
  let usage = formatCost(value);
  return $vm.$t('billing.cloudBillDetails.totalUsageDescription', {'usage': usage});
}

function showTooltipEventListener() {
  //The best place for this code is updated() hook, but we're facing the ag-issue that in case we scroll scrollbar from left to right, the highlights is not working
  const cloudServiceChargeColIds = ['cloudCost', 'supportFee', 'ncpDiscount', 'salesDiscount', 'credit'];
  const showTooltipCells = document.getElementsByClassName('show-tooltip-cell');
  if (!showTooltipCells) {
    return;
  }

  for (let k = 0; k < showTooltipCells.length; k++) {
    const agRowClosest = closestElement(showTooltipCells[k], '.ag-row');
    if (_isNil(agRowClosest)) {
      continue;
    }
    const rowIndex = agRowClosest.getAttribute('row-index');
    const rowColId = showTooltipCells[k].getAttribute('col-id');
    const agCells = document.querySelectorAll(".ag-row[row-index='"+ rowIndex +"'] .ag-cell");

    showTooltipCells[k].addEventListener("mouseover", function() {
      for(let i = 0; i < agCells.length; i++)
      {
        const agCellColId = agCells[i].getAttribute('col-id');
        if (!agCells[i].classList.contains('highlight')) {
          if (cloudServiceChargeColIds.includes(agCellColId) && cloudServiceChargeColIds.includes(rowColId)) {
            agCells[i].className += " highlight";
          } else if (agCellColId.includes('additionalServicesObject') && rowColId.includes('additionalServicesObject')) {
            agCells[i].className += " highlight";
          }
        }
      }
    });

    showTooltipCells[k].addEventListener('mouseout', function() {
      for(let i = 0; i < agCells.length; i++)
      {
        agCells[i].classList.remove("highlight");
      }
    });
  }
}

function prepareDataForBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudCost`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudCost`), `- ${$vm.$t(`${localizationCommonLabel}cloudOriginalCost`)}`],
      total: chargeDetail.cloudOriginalCost,
      currencySymbol: invoiceCurrency
    });
    if (chargeDetail.onDemandDiscount !== 0) {
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}cloudCost`), `- ${$vm.$t(`${localizationCommonLabel}onDemandDiscount`)}`],
        total: formatNegativeValue(chargeDetail.onDemandDiscount),
        currencySymbol: invoiceCurrency
      });
    }
    if ((chargeDetail.chargeYear + chargeDetail.chargeMonth) <= '202203') {   // 2022.03 이전에는 cf할인 분리 x
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}cloudCost`), `- ${$vm.$t(`${localizationCommonLabel}cfDiscount`)}`],
        total: formatNegativeValue(chargeDetail.cloudFrontDiscount),
        currencySymbol: invoiceCurrency
      });
    }
    if (chargeDetail.cloudFrontDtoDiscount !== 0) {
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}cloudCost`), `- ${$vm.$t(`${localizationCommonLabel}cfDtoDiscount`)}`],
        total: formatNegativeValue(chargeDetail.cloudFrontDtoDiscount),
        currencySymbol: invoiceCurrency
      });
    }
    if (chargeDetail.cloudFrontReqDiscount !== 0) {
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}cloudCost`), `- ${$vm.$t(`${localizationCommonLabel}cfReqDiscount`)}`],
        total: formatNegativeValue(chargeDetail.cloudFrontReqDiscount),
        currencySymbol: invoiceCurrency
      });
    }
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}supportFee`)],
      total: chargeDetail.supportFee,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}salesDiscount`)],
      total: formatNegativeValue(chargeDetail.salesDiscount),
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}credit`)],
      total: formatNegativeValue(chargeDetail.credit),
      currencySymbol: invoiceCurrency
    });

    //todo still waiting wonBang Confirm about currency and label name
    if (!_isNil(chargeDetail.additionalServices)) {
      let totalAdditionalServiceCharge = 0
      chargeDetail.additionalServices.forEach(additionalItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`), `- ${additionalItem.additionalServiceName}`],
          total: additionalItem.additionalServiceCharge,
          currencySymbol: companyCurrency
        });
        totalAdditionalServiceCharge += additionalItem.additionalServiceCharge
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
        total: totalAdditionalServiceCharge,
        currencySymbol: companyCurrency
      });
    }

    if(chargeDetail.companyCurrency === CURRENCY.KRW ){
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}subTotal(KRW)`)],
        total: chargeDetail.totalCharge,
        currencySymbol: companyCurrency,
        isTotal: true
      });
    }

    result.push({
      description: [$vm.$t(`${localizationCommonLabel}subTotal(USD)`)],
      total: chargeDetail.totalCharge/ exchangeRate,
      currencySymbol: invoiceCurrency,
      isTotal: true
    });
  }
  return result
}

function prepareDataForAzureBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudCost`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}supportFee`)],
      total: chargeDetail.supportFee,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}salesDiscount`)],
      total: chargeDetail.salesDiscount,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}credit`)],
      total: formatNegativeValue(chargeDetail.credit),
      currencySymbol: invoiceCurrency
    });

    //todo still waiting wonBang Confirm about currency and label name
    if (!_isNil(chargeDetail.additionalServices)) {
      let totalAdditionalServiceCharge = 0
      chargeDetail.additionalServices.forEach(additionalItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`), `- ${additionalItem.additionalServiceName}`],
          total: additionalItem.additionalServiceCharge,
          currencySymbol: companyCurrency
        });
        totalAdditionalServiceCharge += additionalItem.additionalServiceCharge
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
        total: totalAdditionalServiceCharge,
        currencySymbol: companyCurrency
      });
    }

    result.push({
      description: [$vm.$t(`${localizationCommonLabel}subTotal(${chargeDetail.invoiceCurrency})`)],
      total: chargeDetail.totalCharge,
      currencySymbol: invoiceCurrency,
      isTotal: true
    });
  }
  return result
}

function prepareDataForGcpBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudCost`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}salesDiscount`)],
      total: chargeDetail.salesDiscount,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}credit`)],
      total: formatNegativeValue(chargeDetail.credit),
      currencySymbol: invoiceCurrency
    });

    //todo still waiting wonBang Confirm about currency and label name
    let totalAdditionalServiceCharge = 0
    if (!_isNil(chargeDetail.additionalServices)) {
      chargeDetail.additionalServices.forEach(additionalItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`), `- ${additionalItem.additionalServiceName}`],
          total: additionalItem.additionalServiceCharge,
          currencySymbol: companyCurrency
        });
        totalAdditionalServiceCharge += additionalItem.additionalServiceCharge
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
        total: totalAdditionalServiceCharge,
        currencySymbol: companyCurrency
      });
    }
    let totalCredit = 0
    if (!_isEmpty(chargeDetail.creditDetails) ) {
      chargeDetail.creditDetails.forEach(creditDetailItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}credit`), `- ${creditDetailItem.crdtNm}`],
          total: creditDetailItem.crdtAmt,
          currencySymbol: companyCurrency
        });
        totalCredit += creditDetailItem.crdtAmt
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}credit`)],
        total: totalCredit,
        currencySymbol: companyCurrency
      });
    }
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}subTotal(${chargeDetail.invoiceCurrency})`)],
      total: chargeDetail.totalCharge,
      currencySymbol: invoiceCurrency,
      isTotal: true
    });
  }
  return result
}

function prepareDataForOciBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudCost`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}salesDiscount`)],
      total: formatNegativeValue(chargeDetail.salesDiscount),
      currencySymbol: invoiceCurrency
    });

    //todo still waiting wonBang Confirm about currency and label name
    if (!_isNil(chargeDetail.additionalServices)) {
      let totalAdditionalServiceCharge = 0
      chargeDetail.additionalServices.forEach(additionalItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`), `- ${additionalItem.additionalServiceName}`],
          total: additionalItem.additionalServiceCharge,
          currencySymbol: companyCurrency
        });
        totalAdditionalServiceCharge += additionalItem.additionalServiceCharge
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
        total: totalAdditionalServiceCharge,
        currencySymbol: companyCurrency
      });
    }

    if(chargeDetail.companyCurrency !== CURRENCY.USD ){
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}subTotal(KRW)`)],
        total: chargeDetail.totalCharge,
        currencySymbol: companyCurrency,
        isTotal: true
      });
    }

  }
  return result
}

function prepareDataForNcpBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudCost`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}ncpDiscount`)],
      total: formatNegativeValue(chargeDetail.ncpDiscount),
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}salesDiscount`)],
      total: formatNegativeValue(chargeDetail.salesDiscount),
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}credit`)],
      total: formatNegativeValue(chargeDetail.credit),
      currencySymbol: invoiceCurrency
    });
    // result.push({
    //   description: [$vm.$t(`${localizationCommonLabel}vat`)],
    //   total: chargeDetail.vatCost,
    //   currencySymbol: invoiceCurrency
    // });

    //todo still waiting wonBang Confirm about currency and label name
    if (!_isNil(chargeDetail.additionalServices)) {
      let totalAdditionalServiceCharge = 0
      chargeDetail.additionalServices.forEach(additionalItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`), `- ${additionalItem.additionalServiceName}`],
          total: additionalItem.additionalServiceCharge,
          currencySymbol: companyCurrency
        });
        totalAdditionalServiceCharge += additionalItem.additionalServiceCharge
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
        total: totalAdditionalServiceCharge,
        currencySymbol: companyCurrency
      });
    }

    if(chargeDetail.companyCurrency === CURRENCY.KRW ){
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}subTotal(KRW)`)],
        total: chargeDetail.totalCharge,
        currencySymbol: companyCurrency,
        isTotal: true
      });
    }

  }
  return result
}

function prepareDataForTencentBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudServiceCharge`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudServiceCharge`), `- ${$vm.$t(`${localizationCommonLabel}cloudCost`)}`],
      total: chargeDetail.cloudOriginalCost,
      currencySymbol: invoiceCurrency
    });
    // result.push({
    //   description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
    //   total: 0,
    //   currencySymbol: invoiceCurrency
    // });


    //todo still waiting wonBang Confirm about currency and label name
    if (!_isNil(chargeDetail.additionalServices)) {
      let totalAdditionalServiceCharge = 0
      chargeDetail.additionalServices.forEach(additionalItem => {
        result.push({
          description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`), `- ${additionalItem.additionalServiceName}`],
          total: additionalItem.additionalServiceCharge,
          currencySymbol: companyCurrency
        });
        totalAdditionalServiceCharge += additionalItem.additionalServiceCharge
      });
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}additionalServiceCharge`)],
        total: totalAdditionalServiceCharge,
        currencySymbol: companyCurrency
      });
    }

    if(chargeDetail.companyCurrency !== CURRENCY.USD ){
      result.push({
        description: [$vm.$t(`${localizationCommonLabel}subTotal(CNY)`)],
        total: chargeDetail.totalCharge,
        currencySymbol: companyCurrency,
        isTotal: true
      });
    }
  }
  return result
}

function prepareDataForOpenStackBillingChargeDetailPrice(chargeDetail, $vm, exchangeRate) {
  let localizationCommonLabel = "billing.billingChargeDetail.priceBreakDown.";

  let companyCurrency = CURRENCY_SYMBOL[chargeDetail.companyCurrency];
  let invoiceCurrency = CURRENCY_SYMBOL[chargeDetail.invoiceCurrency];

  let result = [];
  if (!_isNil(chargeDetail)) {
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudServiceCharge`)],
      total: chargeDetail.cloudCost,
      currencySymbol: invoiceCurrency
    });
    result.push({
      description: [$vm.$t(`${localizationCommonLabel}cloudServiceCharge`), `- ${$vm.$t(`${localizationCommonLabel}cloudCost`)}`],
      total: chargeDetail.cloudOriginalCost,
      currencySymbol: invoiceCurrency
    });

    result.push({
      description: [$vm.$t(`${localizationCommonLabel}subTotal(KRW)`)],   //KRW만 지원
      total: chargeDetail.totalCharge,
      currencySymbol: companyCurrency,
      isTotal: true
    });
  }

  return result;
}

function formatNegativeValue(value) {
  if (value === 0) {
    return 0
  }
  return -value;
}

function getBillingChargeDetailChartData(item, applyExchangeRate, $vm) {
  let chartDataAdditionalServices = [];
  let additionalServiceTotalCost = 0;
  let exchangeRate = {};
  exchangeRate[item.companyCurrency] = applyExchangeRate;

  let cloudServiceChargeCost = calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
   - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
   - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
   + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
   - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
   - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate));
  if (!_isEmpty(item.additionalServices)) {
    let serviceStartValue = cloudServiceChargeCost;
    item.additionalServices.forEach((eachService) => {
      chartDataAdditionalServices.push({
        itemName: eachService.additionalServiceName,
        defaultValue: eachService.additionalServiceCharge,
        value: serviceStartValue + eachService.additionalServiceCharge,
        open: serviceStartValue,
        stepValue: serviceStartValue + eachService.additionalServiceCharge,
        isCost: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#FF2D47'),
      });
      serviceStartValue += eachService.additionalServiceCharge
    })
  }
  chartDataAdditionalServices.forEach(additionalService => {
    additionalServiceTotalCost += additionalService.defaultValue;
  });
  return [
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudOriginalCost'),
      defaultValue: item.cloudOriginalCost,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      isCost: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#FF2D47'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.onDemandDiscount'),
      defaultValue: item.onDemandDiscount,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cfDiscount'),
      defaultValue: item.cloudFrontDiscount,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate),
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudCost'),
      defaultValue: item.cloudCost,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.supportFee'),
      defaultValue: _isNil(item.supportFee) ? 0 : item.supportFee,
      value: _isNil(item.supportFee)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      stepValue: _isNil(item.supportFee)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate),
      isCost: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#FF2D47'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.salesDiscount'),
      defaultValue: _isNil(item.salesDiscount) ? 0 : item.salesDiscount,
      sss: item.salesDiscount,
      value: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
          - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
       + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate)),
      stepValue: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
          - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.credit'),
      defaultValue: _isNil(item.credit) ? 0 : item.credit,
      value: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
          - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
       + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
       - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)),
      stepValue: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
          - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
          + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)
          - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
      defaultValue: item.cloudServiceCharge,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
       + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
       - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
       - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate)),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
       - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
       + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
       - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
       - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate)),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    ...chartDataAdditionalServices,
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.subTotal'),
      defaultValue: item.totalCharge,
      value: cloudServiceChargeCost + additionalServiceTotalCost,
      open: 0,
      isSubTotal: true,
      currency: item.companyCurrency,
      color: $vm.$am4core.color('#112E5F'),
    }
  ]
}

function getAzureBillingChargeDetailChartData(item, applyExchangeRate, $vm) {
  let chartDataAdditionalServices = [];
  let additionalServiceTotalCost = 0;
  let exchangeRate = {};
  exchangeRate[item.companyCurrency] = applyExchangeRate;

  let cloudServiceChargeCost = calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
    + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
    + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
    - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate));
  if (!_isEmpty(item.additionalServices)) {
    let serviceStartValue = cloudServiceChargeCost;
    item.additionalServices.forEach((eachService) => {
      chartDataAdditionalServices.push({
        itemName: eachService.additionalServiceName,
        defaultValue: eachService.additionalServiceCharge,
        value: serviceStartValue + eachService.additionalServiceCharge,
        open: serviceStartValue,
        stepValue: serviceStartValue + eachService.additionalServiceCharge,
        isCost: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#FF2D47'),
      });
      serviceStartValue += eachService.additionalServiceCharge
    })
  }
  chartDataAdditionalServices.forEach(additionalService => {
    additionalServiceTotalCost += additionalService.defaultValue;
  });
  return [
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudCost'),
      defaultValue: item.cloudCost,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.supportFee'),
      defaultValue: _isNil(item.supportFee) ? 0 : item.supportFee,
      value: _isNil(item.supportFee)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      stepValue: _isNil(item.supportFee)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
          + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate),
      isCost: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#FF2D47'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.salesDiscount'),
      defaultValue: _isNil(item.salesDiscount) ? 0 : item.salesDiscount,
      sss: item.salesDiscount,
      value: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate)),
      stepValue: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.credit'),
      defaultValue: _isNil(item.credit) ? 0 : item.credit,
      value: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate)
        + calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)),
      stepValue: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate)
        + calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
      defaultValue: item.cloudServiceCharge,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate)),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate)),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    ...chartDataAdditionalServices,
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.subTotal'),
      defaultValue: item.totalCharge,
      value: cloudServiceChargeCost + additionalServiceTotalCost,
      open: 0,
      isSubTotal: true,
      currency: item.companyCurrency,
      color: $vm.$am4core.color('#112E5F'),
    }
  ]
}

function getGcpBillingChargeDetailChartData(item, applyExchangeRate, $vm) {
  let chartDataAdditionalServices = [];
  let chartDataCreditServices = [];
  let additionalServiceTotalCost = 0;
  let creditServiceTotalCost = 0;
  let exchangeRate = {};
  exchangeRate[item.companyCurrency] = applyExchangeRate;

  let cloudServiceChargeCost = calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
    + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
    - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate,true));

  let salesDiscount = calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
    + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true));

  if (!_isEmpty(item.additionalServices)) {
    let serviceStartValue = cloudServiceChargeCost;
    item.additionalServices.forEach((eachService) => {
      chartDataAdditionalServices.push({
        itemName: eachService.additionalServiceName,
        defaultValue: eachService.additionalServiceCharge,
        value: serviceStartValue + eachService.additionalServiceCharge,
        open: serviceStartValue,
        stepValue: serviceStartValue + eachService.additionalServiceCharge,
        isCost: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#FF2D47'),
      });
      serviceStartValue += eachService.additionalServiceCharge
    })
  }
  chartDataAdditionalServices.forEach(additionalService => {
    additionalServiceTotalCost += additionalService.defaultValue;
  });

  if(!_isEmpty(item.creditDetails)) {
    let detailStartValue = salesDiscount;

    item.creditDetails.forEach((creditDetail) => {
      const creditAmt = parseInt(creditDetail.crdtAmt);
      chartDataCreditServices.push({
        itemName: $vm.$t('billing.billingChargeDetail.download.costNames.' + creditDetail.crdtTypeCd),
        defaultValue: creditAmt,
        value: detailStartValue + creditAmt,
        open: detailStartValue,
        stepValue: detailStartValue + creditAmt,
        isDiscount: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#6CB41E'),
      });
      detailStartValue += creditAmt
    })
  }

  chartDataCreditServices.forEach(credit => {
    creditServiceTotalCost += credit.defaultValue;
  });

  let res = [];
  res.push({
    itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudCost'),
    defaultValue: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate,true),
    value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true),
    open: 0,
    stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true),
    isSubTotal: true,
    currency: item.invoiceCurrency,
    color: $vm.$am4core.color('#112E5F'),
  })
  res.push({
    itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.salesDiscount'),
    defaultValue: _isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true),
    sss: item.salesDiscount,
    value: _isNil(item.salesDiscount)
      ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
      : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
      + calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true),
    open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true),
    stepValue: _isNil(item.salesDiscount)
      ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
      : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
      + calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true),
    isDiscount: true,
    currency: item.invoiceCurrency,
    color: $vm.$am4core.color('#6CB41E'),
  })
  if(_isEmpty(item.creditDetails)) {
    res.push({
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.credit'),
      defaultValue: _isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate,true),
      value: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
        - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true)),
      stepValue: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
        : calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
        - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    })
  } else {
    res.push(...chartDataCreditServices)
  }
  res.push({
    itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
    defaultValue: calculateCostByCurrency(item.cloudServiceCharge, item.companyCurrency, exchangeRate,true),
    value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
      + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
      - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate,true)),
    open: 0,
    stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate,true)
      + (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate,true))
      - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate,true)),

    isSubTotal: true,
    currency: item.invoiceCurrency,
    color: $vm.$am4core.color('#112E5F'),
  })
  res.push(...chartDataAdditionalServices)
  res.push({
    itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.subTotal'),
    defaultValue: calculateCostByCurrency(item.totalCharge, item.companyCurrency, exchangeRate,true),
    value: calculateCostByCurrency(item.totalCharge, item.companyCurrency, exchangeRate,true) ,
    open: 0,
    isSubTotal: true,
    currency: item.companyCurrency,
    color: $vm.$am4core.color('#112E5F'),
  })

  return res
}


function getOciBillingChargeDetailChartData(item, applyExchangeRate, $vm) {
  let chartDataAdditionalServices = [];
  let additionalServiceTotalCost = 0;
  let exchangeRate = {};
  exchangeRate[item.companyCurrency] = applyExchangeRate;

  let cloudServiceChargeCost = calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
    - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
    - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
    + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
    - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
    - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate));
  if (!_isEmpty(item.additionalServices)) {
    let serviceStartValue = cloudServiceChargeCost;
    item.additionalServices.forEach((eachService) => {
      chartDataAdditionalServices.push({
        itemName: eachService.additionalServiceName,
        defaultValue: eachService.additionalServiceCharge,
        value: serviceStartValue + eachService.additionalServiceCharge,
        open: serviceStartValue,
        stepValue: serviceStartValue + eachService.additionalServiceCharge,
        isCost: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#FF2D47'),
      });
      serviceStartValue += eachService.additionalServiceCharge
    })
  }
  chartDataAdditionalServices.forEach(additionalService => {
    additionalServiceTotalCost += additionalService.defaultValue;
  });
  return [
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudCost'),
      defaultValue: item.cloudCost,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.salesDiscount'),
      defaultValue: _isNil(item.salesDiscount) ? 0 : formatNegativeValue(item.salesDiscount),
      sss: item.salesDiscount,
      value: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate) - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate),
      stepValue: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate) - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
      defaultValue: item.cloudServiceCharge,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    ...chartDataAdditionalServices,
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.subTotal'),
      defaultValue: item.totalCharge,
      value: cloudServiceChargeCost + additionalServiceTotalCost,
      open: 0,
      isSubTotal: true,
      currency: item.companyCurrency,
      color: $vm.$am4core.color('#112E5F'),
    }
  ]
}


function getNcpBillingChargeDetailChartData(item, applyExchangeRate, $vm) {
  let chartDataAdditionalServices = [];
  let additionalServiceTotalCost = 0;
  let exchangeRate = {};
  exchangeRate[item.companyCurrency] = applyExchangeRate;

  let cloudServiceChargeCost = calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
    - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
    - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
    - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate))
    // + (_isNil(item.vatCost) ? 0 : calculateCostByCurrency(item.vatCost, item.companyCurrency, exchangeRate));
  if (!_isEmpty(item.additionalServices)) {
    let serviceStartValue = cloudServiceChargeCost;
    item.additionalServices.forEach((eachService) => {
      chartDataAdditionalServices.push({
        itemName: eachService.additionalServiceName,
        defaultValue: eachService.additionalServiceCharge,
        value: serviceStartValue + eachService.additionalServiceCharge,
        open: serviceStartValue,
        stepValue: serviceStartValue + eachService.additionalServiceCharge,
        isCost: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#FF2D47'),
      });
      serviceStartValue += eachService.additionalServiceCharge
    })
  }
  chartDataAdditionalServices.forEach(additionalService => {
    additionalServiceTotalCost += additionalService.defaultValue;
  });
  return [
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudCost'),
      defaultValue: item.cloudCost,
      value: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.ncpDiscount'),
      defaultValue: _isNil(item.ncpDiscount) ? 0 : formatNegativeValue(item.ncpDiscount),
      sss: item.ncpDiscount,
      value: _isNil(item.ncpDiscount)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate) - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate),
      stepValue: _isNil(item.ncpDiscount)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate) - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.salesDiscount'),
      defaultValue: _isNil(item.salesDiscount) ? 0 : formatNegativeValue(item.salesDiscount),
      sss: item.salesDiscount,
      value: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate)),
      stepValue: _isNil(item.salesDiscount)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate)
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.credit'),
      defaultValue: _isNil(item.credit) ? 0 : formatNegativeValue(item.credit),
      value: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      open: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)),
      stepValue: _isNil(item.credit)
        ? calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        : calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate)
        - calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate),
      isDiscount: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#6CB41E'),
    },
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
      defaultValue: item.cloudServiceCharge,
      value: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate)),
      open: 0,
      stepValue: calculateCostByCurrency(item.cloudCost, item.companyCurrency, exchangeRate)
        - (_isNil(item.ncpDiscount) ? 0 : calculateCostByCurrency(item.ncpDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
        - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate)),
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    // {
    //   itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.vat'),
    //   defaultValue: _isNil(item.vatCost) ? 0 : item.vatCost,
    //   value: (_isNil(item.cloudServiceCharge) ? 0 :  calculateCostByCurrency(item.cloudServiceCharge, item.companyCurrency, exchangeRate))
    //     +  (_isNil(item.vatCost) ? 0 : calculateCostByCurrency(item.vatCost, item.companyCurrency, exchangeRate)),
    //   open: (_isNil(item.cloudServiceCharge) ? 0 : calculateCostByCurrency(item.cloudServiceCharge, item.companyCurrency, exchangeRate)) ,
    //   stepValue: (_isNil(item.cloudServiceCharge) ? 0 : calculateCostByCurrency(item.cloudServiceCharge, item.companyCurrency, exchangeRate))
    //     + (_isNil(item.vatCost) ? 0 : calculateCostByCurrency(item.vatCost, item.companyCurrency, exchangeRate)),
    //   isSubTotal: true,
    //   currency: item.invoiceCurrency,
    //   color: $vm.$am4core.color('#FF2D47'),
    // },
    ...chartDataAdditionalServices,
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.subTotal'),
      defaultValue: item.totalCharge,
      value: cloudServiceChargeCost + additionalServiceTotalCost,
      open: 0,
      isSubTotal: true,
      currency: item.companyCurrency,
      color: $vm.$am4core.color('#112E5F'),
    }
  ]
}

function getTencentBillingChargeDetailChartData(item, applyExchangeRate, $vm) {
  let chartDataAdditionalServices = [];
  let additionalServiceTotalCost = 0;
  let exchangeRate = {};
  exchangeRate[item.companyCurrency] = applyExchangeRate;

  let cloudServiceChargeCost = calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
    - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
    - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate)
    + (_isNil(item.supportFee) ? 0 : calculateCostByCurrency(item.supportFee, item.companyCurrency, exchangeRate))
    - (_isNil(item.salesDiscount) ? 0 : calculateCostByCurrency(item.salesDiscount, item.companyCurrency, exchangeRate))
    - (_isNil(item.credit) ? 0 : calculateCostByCurrency(item.credit, item.companyCurrency, exchangeRate));
  if (!_isEmpty(item.additionalServices)) {
    let serviceStartValue = cloudServiceChargeCost;
    item.additionalServices.forEach((eachService) => {
      chartDataAdditionalServices.push({
        itemName: eachService.additionalServiceName,
        defaultValue: eachService.additionalServiceCharge,
        value: serviceStartValue + eachService.additionalServiceCharge,
        open: serviceStartValue,
        stepValue: serviceStartValue + eachService.additionalServiceCharge,
        isCost: true,
        currency: item.companyCurrency,
        color: $vm.$am4core.color('#FF2D47'),
      });
      serviceStartValue += eachService.additionalServiceCharge
    })
  }
  chartDataAdditionalServices.forEach(additionalService => {
    additionalServiceTotalCost += additionalService.defaultValue;
  });
  return [
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
      defaultValue: item.cloudOriginalCost,
      value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
      open: 0,
      isSubTotal: true,
      currency: item.invoiceCurrency,
      color: $vm.$am4core.color('#112E5F'),
    },
    // {
    //   itemName: $vm.$t('billing.billingChargeDetail.priceBreakDown.additionalServiceCharge'),
    //   defaultValue: 0,
    //   value: 0,
    //   open: 0,
    //   cost: true,
    //   currency: item.companyCurrency,
    //   color: $vm.$am4core.color('#FF2D47'),
    // },
    // {
    //   itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudCost'),
    //   defaultValue: item.cloudCost,
    //   value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
    //     - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
    //     - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
    //   open: 0,
    //   stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate)
    //     - calculateCostByCurrency(item.onDemandDiscount, item.companyCurrency, exchangeRate)
    //     - calculateCostByCurrency(item.cloudFrontDiscount, item.companyCurrency, exchangeRate),
    //   isSubTotal: true,
    //   currency: item.invoiceCurrency,
    //   color: $vm.$am4core.color('#112E5F'),
    // },
    // {
    //   itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.cloudServiceCharge'),
    //   defaultValue: item.cloudServiceCharge,
    //   value: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
    //   open: 0,
    //   stepValue: calculateCostByCurrency(item.cloudOriginalCost, item.companyCurrency, exchangeRate),
    //   isSubTotal: true,
    //   currency: item.invoiceCurrency,
    //   color: $vm.$am4core.color('#112E5F'),
    // },
    // ...chartDataAdditionalServices,
    {
      itemName: $vm.$t('billing.billingChargeDetail.chart.categoryName.subTotal'),
      defaultValue: item.totalCharge,
      value: cloudServiceChargeCost + additionalServiceTotalCost,
      open: 0,
      isSubTotal: true,
      currency: item.companyCurrency,
      color: $vm.$am4core.color('#112E5F'),
    }
  ]
}
/**
 * this is in-place modification<br>
 * for each charge item, create a map (code -> service) to represent its services in "Object" way
 */
function addAdditionalServiceMapToEachChargeListItem(chargeListItems) {
  chargeListItems.forEach(item => {
    if (_isEmpty(item.additionalServices)) {
      return;
    }
    // eslint-disable-next-line no-param-reassign
    item.additionalServicesObject = {};
    item.additionalServices.forEach(service => {
      // eslint-disable-next-line no-param-reassign
      item.additionalServicesObject[service.additionalServiceCode] = service;
    });
  });
  return chargeListItems;
}

/**
 * get all unique additional services existing in chargeList<br>
 * returns Object {'code': {additionalServiceCode: 'code', additionalServiceName: 'name', additionalServiceCharge: 0}}
 */
function getAllCodeToServiceFromChargeList(chargeList) {
  if (_isEmpty(chargeList)) {
    return {};
  }
  let codeToService = {};
  chargeList.forEach(item => {
    if (!item.additionalServices) {
      return;
    }
    item.additionalServices.forEach(service => {
      codeToService[service.additionalServiceCode] = {
        ...service,
        additionalServiceCharge: 0
      };
    });
  });
  return codeToService;
}

function getAllCodeToGcpCreditFromChargeList(chargeList) {
  if (_isEmpty(chargeList)) {
    return {};
  }
  let codeToService = {};
  chargeList.forEach(item => {
    if (!item.creditDetails) {
      return;
    }
    item.creditDetails.forEach(detail => {
      codeToService[detail.crdtTypeCd] = {
        ...detail,
        crdtAmt: 0,
        crdtNm: 'CREDIT'
      };
    });
  });
  return codeToService;
}

function getInvoiceInsightTop5(rawData) {
  let newData = [];
  let order = 1;
  rawData.forEach(item => {
    let data = {
      thisMonthCost : item.thisMonthCost,
      lastMonthCost : item.lastMonthCost,
      averageCost : item.averageCost,
      maxCost : item.maxCost,
      productName : item.productName,
      percent : 0,
      increaseItems : [],
      decreaseItems : [],
      status : 0,
      order: order++
    };

    let percent;
    if(item.lastMonthCost === 0) {
      if(item.thisMonthCost === 0) {
        percent = 0;
      } else {
        percent = 100;
      }
    } else {
      percent = ((item.thisMonthCost - item.lastMonthCost) / item.lastMonthCost) * 100;
    }

    if(percent > 0) {
      data.status = 1;
    } else if (percent < 0) {
      data.status = -1;
    } else {
      data.status = 0;
    }

    setIncreaseDecreaseItems(item.thisMonthCost, data, 'lastMonthCost');
    setIncreaseDecreaseItems(item.thisMonthCost, data, 'maxCost');
    setIncreaseDecreaseItems(item.thisMonthCost, data, 'averageCost');

    data.percent = formatPercentage(percent);
    newData.push(data);
  })

  return newData;
}

function setIncreaseDecreaseItems(thisMonthCost, data, element) {
  if(thisMonthCost > data[element]) {
    data.increaseItems.push(element);
    return;
  }
  if(thisMonthCost < data[element]) {
    data.decreaseItems.push(element);
  }
}

// gcp credit detail 오름차순 정렬
function sortCrdtDetailNmAsc(creditDetails, $vm) {
  creditDetails.forEach((creditDetailItem, idx) => {
    // eslint-disable-next-line
    creditDetails[idx].crdtNm = ''
  })
  creditDetails.forEach((creditDetailItem) => {
    const crdtNm = $vm.$t('billing.billingChargeDetail.download.costNames.' + creditDetailItem.crdtTypeCd);
    Object.assign(creditDetailItem, {'crdtNm':crdtNm});
  })
  const creditDetailsOrderByCrdtNm = _.sortBy(creditDetails, 'crdtNm');
  Object.assign(creditDetails,creditDetailsOrderByCrdtNm )

  return creditDetails;
}

export {
  prepareMonthToDateOptions,
  prepareBillSummaryData,
  getReverseArrIndex,
  groupDataByAccount,
  groupDataByInvoice,
  groupDataByRegion,
  groupDataByTag,
  groupDataByServiceGroup,
  classifyBillingSummaryData,
  formatCostValue,
  customFormatCostValue,
  showTooltipEventListener,
  prepareDataForBillingChargeDetailPrice,
  prepareDataForAzureBillingChargeDetailPrice,
  prepareDataForOciBillingChargeDetailPrice,
  prepareDataForNcpBillingChargeDetailPrice,
  prepareDataForGcpBillingChargeDetailPrice,
  prepareDataForOpenStackBillingChargeDetailPrice,
  getBillingChargeDetailChartData,
  getAzureBillingChargeDetailChartData,
  getOciBillingChargeDetailChartData,
  getNcpBillingChargeDetailChartData,
  getGcpBillingChargeDetailChartData,
  addAdditionalServiceMapToEachChargeListItem,
  getAllCodeToServiceFromChargeList,
  getAllCodeToGcpCreditFromChargeList,
  convertExponentialNumberToDecimal,
  groupingByInvoice,
  groupingByAccount,
  groupingByRegion,
  getInvoiceInsightTop5,
  prepareDataForTencentBillingChargeDetailPrice,
  getTencentBillingChargeDetailChartData,
  sortCrdtDetailNmAsc
};
