import {
  fetchBillingDetail,
  fetchBillingDetailWithTag,
  fetchBillList,
  fetchChargeList,
  fetchInvoicesList,
  fetchInvoiceInsightList,
  fetchInvoiceInsightGrid,
  // fetchComputingResources,
  fetchAdditionalServices,
  fetchServiceGroups,
  fetchAutoSpot,
  fetchTagOptions,
  fetchBillingDetailWithServiceGroup,
  fetchServiceGroupOptions,
  fetchRecipientList
} from '@/api/billing';
import { isFutureMonth } from '@/util/dateTimeUtils';

/**
 * Get charge list
 *
 * @param context
 * @param payload
 */
const getChargeList = (context, payload) => {
  context.commit('SET_IS_CHARGE_LIST_LOADING', true);
  fetchChargeList(payload).then((response) => {
    context.commit('SET_IS_CHARGE_LIST_LOADING', false);
    context.commit('CHART_LIST_UPDATED', response);
  }).catch((error) => {
    // eslint-disable-next-line
    context.commit('SET_IS_CHARGE_LIST_LOADING', false);
    console.error(error);
  });
};

const selectedMonthToDate = (context, payload) => {
  context.commit('SET_ACTIVE_MONTH', payload);
};

/**
 * Get bills for monthly cost trend and bill summary
 *
 * @param context
 * @param payload
 */
const getBillList = (context, payload) => {
  context.commit('SET_IS_BILL_LIST_LOADING', true);
  fetchBillList(payload)
    .then((response) => {
      //Sort bill list by charge monthYear ASC, then remove invalid data
      let standardizedBillList = response
        .filter(bill => !isFutureMonth(bill.chargeYear, bill.chargeMonth))
        .sort(function (a, b) {
          return new Date(parseInt(a.chargeYear), parseInt(a.chargeMonth), 1) - new Date(parseInt(b.chargeYear), parseInt(b.chargeMonth), 1);
        });

      context.commit('SET_IS_BILL_LIST_LOADING', false);
      context.commit('BILL_LIST_UPDATED', standardizedBillList);
      context.commit('SET_INVOICE_CURRENCY', response[0].invoiceCurrency); // 청구서가 선택한 환율 저장
      context.commit('SET_COMPANY_CURRENCY', response[0].companyCurrency); // 컴퍼니가 선택한 환율 저장
    })
    .catch((error) => {
      // eslint-disable-next-line
      context.commit('SET_IS_BILL_LIST_LOADING', false);
      console.error(error);
    });
};

/**
 * Set active month use on monthly cost trend and billing summary block
 *
 * @param context
 * @param activeMonthIdx
 */
const setActiveMonthIdx = (context, activeMonthIdx) => {
  context.commit('SET_ACTIVE_MONTH', activeMonthIdx);
};

/**
 * Set first month in monthly cost trend
 *
 * @param context
 * @param firstMonthIdx
 */
const setFirstMonthIdx = (context, firstMonthIdx) => {
  context.commit('SET_FIRST_MONTH_IDX', firstMonthIdx);
};

/**
 * Set end month in monthly cost trend
 *
 * @param context
 * @param lastMonthIdx
 */
const setLastMonthIdx = (context, lastMonthIdx) => {
  context.commit('SET_LAST_MONTH_IDX', lastMonthIdx);
};

const setSelectedTabIndex = (context, selectedTabIndex) => {
  context.commit('SET_SELECTED_TAB_INDEX', selectedTabIndex);
};

const setSelectedVendor = (context, vendor) => {
  context.commit('SET_SELECTED_VENDOR', vendor);
};

/**
 * Get bills for monthly cost trend and bill summary
 *
 * @param context
 * @param payload
 */
const getInvoiceList = (context, payload) => {
  context.commit('SET_IS_INVOICE_LIST_LOADING', true);
  fetchInvoicesList(payload)
  .then((response) => {
    context.commit('SET_IS_INVOICE_LIST_LOADING', false);
    context.commit('INVOICE_LIST', response);
  })
  .catch((error) => {
    // eslint-disable-next-line
    context.commit('SET_IS_INVOICE_LIST_LOADING', false);
    console.error(error);
  });
};

const getInvoiceInsightList = (context, payload) => {
  context.commit('SET_IS_INVOICE_INSIGHT_LIST_LOADING', true);
  context.commit('SET_IS_INVOICE_INSIGHT_GRID_LOADING', true);
  fetchInvoiceInsightList(payload).then((response) => {
    context.commit('SET_IS_INVOICE_INSIGHT_LIST_LOADING', false);
    context.commit('INVOICE_INSIGHT_LIST', response);
  }).catch((error) => {
    // eslint-disable-next-line
    context.commit('SET_IS_INVOICE_INSIGHT_LIST_LOADING', false);
    console.error(error);
  });
};

const getInvoiceInsightGrid = (context, payload) => {
  context.commit('SET_IS_INVOICE_INSIGHT_GRID_LOADING', true);
  if(payload !== undefined){
    fetchInvoiceInsightGrid(payload).then((response) => {
      context.commit('SET_IS_INVOICE_INSIGHT_GRID_LOADING', false);
      context.commit('INVOICE_INSIGHT_GRID', response);
    }).catch((error) => {
      // eslint-disable-next-line
      context.commit('SET_IS_INVOICE_INSIGHT_GRID_LOADING', false);
      console.error(error);
    });
  }else{
    context.commit('SET_IS_INVOICE_INSIGHT_GRID_LOADING', false);
    context.commit('INVOICE_INSIGHT_GRID', undefined);
  }
};

// const getComputingResources = (context, payload) => {
//   context.commit('SET_IS_COMPUTING_RESOURCES_LOADING', true);
//   fetchComputingResources(payload)
//     .then((response) => {
//       context.commit('SET_IS_COMPUTING_RESOURCES_LOADING', false);
//       context.commit('COMPUTING_RESOURCES', response);
//     })
//     .catch((error) => {
//       // eslint-disable-next-line
//       context.commit('SET_IS_COMPUTING_RESOURCES_LOADING', false);
//       console.error(error);
//     });
// };

const getAutoSpot = (context, payload) => {
  context.commit('SET_IS_AUTOSPOT_LOADING', true);
  fetchAutoSpot(payload)
    .then((response) => {
      context.commit('SET_IS_AUTOSPOT_LOADING', false);
      context.commit('AUTOSPOT', response);
    })
    .catch((error) => {
      // eslint-disable-next-line
      context.commit('SET_IS_AUTOSPOT_LOADING', false);
      console.error(error);
    });
};

const getAdditionalServices = (context, payload) => {
  context.commit('SET_IS_ADDITIONAL_SERVICES_LOADING', true);
  fetchAdditionalServices(payload)
    .then((response) => {
      context.commit('SET_IS_ADDITIONAL_SERVICES_LOADING', false);
      context.commit('ADDITIONAL_SERVICES', response);
    })
    .catch((error) => {
      // eslint-disable-next-line
      context.commit('SET_IS_ADDITIONAL_SERVICES_LOADING', false);
      console.error(error);
    });
};

const getServiceGroups = (context, payload) => {
  context.commit('SET_IS_SERVICE_GROUPS_LOADING', true);
  fetchServiceGroups(payload)
    .then((response) => {
      context.commit('SET_IS_SERVICE_GROUPS_LOADING', false);
      context.commit('SERVICE_GROUPS', response);
    })
    .catch((error) => {
      // eslint-disable-next-line
      context.commit('SET_IS_SERVICE_GROUPS_LOADING', false);
      console.error(error);
    });
};

const setChargeTableState = (context, state) => {
  context.commit('SET_CHARGE_TABLE_STATE', state);
}

const setDisplayedWarningBanner = (context, tabName) => {
  context.commit('SET_DISPLAYED_WARNING_BANNER', tabName);
}

const setSelectedViewByOption = (context, selectedViewBy) => {
  context.commit('SET_SELECTED_VIEW_BY_OPTION', selectedViewBy);
}

const getBillingDetail = (context, payload) => {
  context.commit('SET_IS_CLOUD_BILL_DETAIL_LOADING', true);
  fetchBillingDetail(payload)
    .then((response) => {
      context.commit('SET_IS_CLOUD_BILL_DETAIL_LOADING', false);
      context.commit('SET_BILLING_DETAIL', response);
    })
    .catch((error) => {
      context.commit('SET_IS_CLOUD_BILL_DETAIL_LOADING', false);
      console.error(error);
    });
}

const getBillingDetailWithTag = (context) => {
  fetchBillingDetailWithTag()
    .then((response) => {
      context.commit('SET_BILLING_DETAIL_WITH_TAG', response);
    })
    .catch((error) => {
      console.error(error);
    });
}

const getBillingDetailWithServiceGroup = (context) => {
  fetchBillingDetailWithServiceGroup()
    .then((response) => {
      context.commit('SET_BILLING_DETAIL_WITH_SERVICE_GROUP', response);
    })
    .catch((error) => {
      console.error(error);
    });
}

const setSelectedTag = (context, tag) => {
  context.commit('SET_SELECTED_TAG', tag);
}

const getTagOptions = (context, payload) => {
  fetchTagOptions(payload)
    .then((response) => {
      context.commit('SET_SELECTED_TAG', response[0] );
      context.commit('SET_TAG_OPTIONS', response );
    })
    .catch((error) => {
      console.error(error);
    });
}

const setSelectedServiceGroupSet = (context, svg) => {
  context.commit('SET_SELECTED_SERVICE_GROUP', svg);
}

const getServiceGroupSetOptions = (context, payload) => {
  fetchServiceGroupOptions(payload)
    .then((response) => {
      context.commit('SET_SELECTED_SERVICE_GROUP', response[0]);
      context.commit('SET_SERVICE_GROUP_OPTIONS', response);
    })
    .catch((error) => {
      console.error(error);
    });
}

const setCloudBillDetailLoading = (context, isLoading) => {
  return new Promise((resolve, reject) => {
    context.commit('SET_IS_CLOUD_BILL_DETAIL_LOADING', isLoading);
    resolve();
  })
};

const setGridLayerCondition = (context, gridOption) => {
  context.commit('SET_GRID_LAYER_CONDITION', gridOption);
}

const getRecipientList = (context, payload) => {
  fetchRecipientList(payload)
  .then((response) => {
    context.commit('RECIPIENT_EMAIL_LIST', response);
  })
  .catch((error) => {
    console.error(error);
  });
};

export default {
  setDisplayedWarningBanner,
  getChargeList,
  getBillList,
  getInvoiceList,
  getInvoiceInsightList,
  getInvoiceInsightGrid,
  // getComputingResources,
  getAdditionalServices,
  getServiceGroups,
  selectedMonthToDate,
  setActiveMonthIdx,
  setFirstMonthIdx,
  setLastMonthIdx,
  setSelectedTabIndex,
  setSelectedVendor,
  setSelectedViewByOption,
  getBillingDetail,
  setSelectedTag,
  getBillingDetailWithTag,
  setChargeTableState,
  getTagOptions,
  setCloudBillDetailLoading,
  getBillingDetailWithServiceGroup,
  getServiceGroupSetOptions,
  setSelectedServiceGroupSet,
  setGridLayerCondition,
  getAutoSpot,
  getRecipientList
};
