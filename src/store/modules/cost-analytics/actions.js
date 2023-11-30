import _isEmpty from 'lodash/isEmpty';
import _isNil from 'lodash/isNil';
import {
  callDeleteUserFilter,
  callSaveAsUserFilter,
  callSaveUserFilter,
  fetchCostAnalyticsData,
  fetchDetailCostAnalyticsData,
  fetchFilterOptions,
  fetchPortionDetailCostAnalyticsData,
  fetchUserFilters,
} from '@/api/cost-analytics';
import {DEFAULT_COST_ANALYTICS_DATA, COST_TREND_BY} from '@/constants/costAnalyticsConstants';
import _cloneDeep from "lodash/cloneDeep";
import {buildDetailCADataWithCompare, mockDataForComparePortionDetail, makeItemsToUniqByKey} from "@/util/costAnalyticsUtil";

const getCostAnalyticsData = (context, payload) => {
  let newPayload = {...payload};
  // 태그가 없는 상태에서 viewBy가 태그인 경우 -> account로 처리
  if(_.isEmpty(payload.filters.aws.tags) && _.isEqual(payload.viewBy, COST_TREND_BY.TAG)) {
    newPayload.viewBy = COST_TREND_BY.ACCOUNT;
  }

  if (_isEmpty(newPayload.selectedVendors)) {
    context.commit('GET_COST_ANALYTICS_DATA', DEFAULT_COST_ANALYTICS_DATA);
    return;
  }
  context.commit('common/SET_IS_LOADING',  true, { root: true });
  fetchCostAnalyticsData(newPayload).then((response) => {
    context.commit('GET_COST_ANALYTICS_DATA', response || DEFAULT_COST_ANALYTICS_DATA);
    context.commit('common/SET_IS_LOADING',  false, { root: true });
  }).catch((error) => {
    context.commit('GET_COST_ANALYTICS_DATA', DEFAULT_COST_ANALYTICS_DATA);
    context.commit('common/SET_IS_LOADING',  false, { root: true });
    console.error(error);
  });
};

const buildDetailCADataWithCompareWithDisplayedItems = (originalDetailCostAnalyticsData, displayedItems, compareCostType) => {
  if (originalDetailCostAnalyticsData[0] && originalDetailCostAnalyticsData[1]) {
    const originalDetailCostAnalyticsDataItems = _cloneDeep(originalDetailCostAnalyticsData[0].items);
    const originalCompareDetailCostAnalyticsDataItems = _cloneDeep(originalDetailCostAnalyticsData[1].items);
    let uniqueDetailDataItems = makeItemsToUniqByKey(originalDetailCostAnalyticsDataItems, displayedItems);
    let uniqueCompareDetailDataItems = makeItemsToUniqByKey(originalCompareDetailCostAnalyticsDataItems, displayedItems);
    // eslint-disable-next-line no-param-reassign
    originalDetailCostAnalyticsData[0].items = uniqueDetailDataItems;
    // eslint-disable-next-line no-param-reassign
    originalDetailCostAnalyticsData[1].items = uniqueCompareDetailDataItems;
  }

  return buildDetailCADataWithCompare(originalDetailCostAnalyticsData[0], originalDetailCostAnalyticsData[1], compareCostType, displayedItems)
};

const setDetailCADataWithCompareWhenChangeDisplayedItems = (context, payload) => {
  let detailCADataWithCompare = buildDetailCADataWithCompareWithDisplayedItems(payload.originalDetailCostAnalyticsData, payload.displayedItems, payload.compareCostType)

  context.commit('GET_DETAIL_COST_ANALYTICS_DATA', detailCADataWithCompare);
};

const getDetailCostAnalyticsData = (context, payload) => {
  return new Promise((resolve, reject) => {
    if (payload.isCompare) {
      if (_isNil(payload.costDate) || _isNil(payload.compareCostDate)) {
        let singlePayload = {
          ...payload,
          costDate: payload.costDate || payload.compareCostDate,
        };
        fetchDetailCostAnalyticsData(singlePayload).then((response) => {
          context.commit('GET_DETAIL_COST_ANALYTICS_DATA', {...response, compareTotalCost: 0});
          resolve();
        })
      } else {
        let comparedPayload = {
          ...payload,
          costDate: payload.compareCostDate,
        };

        let displayedItems = _cloneDeep(payload.visibleColumnFields);
        displayedItems = displayedItems.filter((displayedItem) => {
          return !['cost', 'usage'].includes(displayedItem);
        });
        delete comparedPayload.visibleColumnFields;
        let fetchPromises = [
          fetchDetailCostAnalyticsData(payload),
          fetchDetailCostAnalyticsData(comparedPayload),
        ];
        Promise.all(fetchPromises).then(results => {
          let detailCADataWithCompare = buildDetailCADataWithCompareWithDisplayedItems(results, displayedItems, payload.compareCostType);
          context.commit('GET_DETAIL_COST_ANALYTICS_DATA', detailCADataWithCompare);
          context.commit('SET_ORIGINAL_DETAIL_COST_ANALYTICS_DATA', results);
          resolve();
        })
      }
    } else {
      fetchDetailCostAnalyticsData(payload).then((response) => {
        context.commit('GET_DETAIL_COST_ANALYTICS_DATA', {...response, compareTotalCost: 0});
        resolve();
      })
    }
  })
};

const getPortionDetailCostAnalyticsData = (context, currentPayload) => {
  let comparedPayload = _cloneDeep(currentPayload);
  //assign compareCostDate to costDate for call API twice time
  comparedPayload.costDate = comparedPayload.compareCostDate;

  if(currentPayload.isCompare) {
    let fetchPromises = [
      fetchPortionDetailCostAnalyticsData(currentPayload),
      fetchPortionDetailCostAnalyticsData(comparedPayload),
    ];
    Promise.all(fetchPromises).then(results => {
      let currentData = results[0]
      let compareData = results[1]

      let combineResponse = mockDataForComparePortionDetail(compareData, currentData);
      context.commit('GET_PORTION_DETAIL_COST_ANALYTICS_DATA', combineResponse.currentData);
      context.commit('GET_PORTION_DETAIL_COST_ANALYTICS_COMPARE_DATA', combineResponse.compareData);
    })
  } else {
    fetchPortionDetailCostAnalyticsData(currentPayload).then((response) => {
      context.commit('GET_PORTION_DETAIL_COST_ANALYTICS_DATA', response);
    }).catch((error) => {
      // eslint-disable-next-line
      console.error(error);
    });
  }
};

const setViewBy = (context, payload) => {
  context.commit('SET_VIEW_BY', payload);
};

const setFilterSettings = (context, payload) => {
  context.commit('SET_FILTER_SETTINGS', payload)
}

const setVisibleColumnFields = (context, payload) => {
  context.commit('SET_VISIBLE_COLUMN_FIELDS', payload)
}

const setIsCloseQuickFilterDropdown = (context, payload) => {
  context.commit('SET_IS_CLOSE_QUICK_FILTER_DROPDOWN', payload)
}

const getFilterOptions = (context, payload) => {
  return new Promise((resolve, reject) => {
    fetchFilterOptions(payload)
      .then(res => {
        context.commit('SET_FILTER_OPTIONS', res);
        resolve();
      });
  });
};

const getUserFilters = (context, payload) => {
  return new Promise((resolve, reject) => {
    fetchUserFilters(payload)
      .then(res => {
        context.commit('SET_USER_FILTERS', res);
        resolve();
      });
  });
};

const saveUserFilter = (context, payload) => {
  return callSaveUserFilter(payload);
};

const saveAsUserFilter = (context, payload) => {
  return callSaveAsUserFilter(payload);
};

const deleteUserFilter = (context, filterIndex) => {
  return callDeleteUserFilter(filterIndex);
};

const clearHorizontalStackedChartOneDayData = (context) => {
  return new Promise((resolve) => {
    context.commit('CLEAR_HORIZONTAL_STACKED_CHART_SINGLE_DATA')
    resolve();
  })
}

const setInitialLoad = (context, value) => {
  context.commit('SET_INITIAL_LOAD', value);
}

export default {
  getCostAnalyticsData,
  getDetailCostAnalyticsData,
  getPortionDetailCostAnalyticsData,
  setViewBy,
  setFilterSettings,
  getFilterOptions,
  getUserFilters,
  saveUserFilter,
  saveAsUserFilter,
  deleteUserFilter,
  clearHorizontalStackedChartOneDayData,
  setDetailCADataWithCompareWhenChangeDisplayedItems,
  setVisibleColumnFields,
  setIsCloseQuickFilterDropdown,
  setInitialLoad
};
