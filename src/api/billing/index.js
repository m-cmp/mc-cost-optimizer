import axios from 'axios';
import ENDPOINT from './../endpoints';
import _isEmpty from 'lodash/isEmpty';
import _get from 'lodash/get';
import {add0StringToNumberLessThan10} from '@/util/dateTimeUtils';
import {
  REQUEST_BILLING_BILLS_MODEL,
  REQUEST_BILLING_CHARGE_MODEL,
  REQUEST_BILLING_DETAILS_MODEL,
  REQUEST_BILLING_DETAILS_WITH_TAG_MODEL,
  REQUEST_BILLING_INVOICES_MODEL,
  REQUEST_BILLING_INVOICE_INSIGHT_MODEL,
  REQUEST_COMPUTING_RESOURCES_MODEL,
  REQUEST_ADDITIONAL_SERVICES_MODEL,
  REQUEST_AUTOSPOT_MODEL,
  REQUEST_SERVICE_GROUPS_MODEL,
  REQUEST_TAG_KEYS_MODEL,
  REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL,
  REQUEST_SERVICE_GROUP_VIEWS_MODEL,
  DELETE_RESERVATION_REQUEST_MODEL,
  SEND_INVOICE_BY_MAIN_REQUEST_MODEL
} from '@/constants/billingConstants';
import {isFailResponse, addLoginUserInfoToPayload} from '@/util/apiUtils';
import store from '@/store';
import mcmpAPIResponseDummyData from '@/dummys/mcmpAPIResponseDummyData.json';

/**
 * Fetch charge lists
 *
 * @returns {Promise<any>}
 */
export function fetchChargeList(payload) {
  // BackUp
  // let chargesParams;
  // if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
  //   const todayDate = new Date();
  //   chargesParams = {
  //     ...REQUEST_BILLING_CHARGE_MODEL,
  //     chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
  //     chargeYear: todayDate.getFullYear()
  //   };
  //
  //   if (payload.selectedVendor) {
  //     chargesParams.vendor = payload.selectedVendor;
  //   }
  // } else {
  //   chargesParams = {
  //     ...REQUEST_BILLING_CHARGE_MODEL,
  //     chargeMonth: payload.chargeMonth,
  //     chargeYear: payload.chargeYear,
  //     vendor: payload.selectedVendor
  //   };
  // }
  // chargesParams = addLoginUserInfoToPayload(chargesParams, store);
  //
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.BILLING[chargesParams.vendor].CHARGE_LIST, chargesParams)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(_get(response, 'data.Data.charges') || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.billing.chargeList.charges)

}

/**
 * Fetch invoices lists
 *
 * @returns {Promise<any>}
 */
export function fetchInvoicesList(payload) {
  // let invoicesParams;
  // if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
  //   const todayDate = new Date();
  //   invoicesParams = {
  //     ...REQUEST_BILLING_INVOICES_MODEL,
  //     chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
  //     chargeYear: todayDate.getFullYear()
  //   };
  //
  //   if (payload.selectedVendor) {
  //     invoicesParams.vendor = payload.selectedVendor;
  //   }
  // } else {
  //   invoicesParams = {
  //     ...REQUEST_BILLING_INVOICES_MODEL,
  //     chargeMonth: payload.chargeMonth,
  //     chargeYear: payload.chargeYear,
  //     vendor: payload.selectedVendor
  //   };
  // }
  // invoicesParams = addLoginUserInfoToPayload(invoicesParams, store);
  //
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.BILLING[invoicesParams.vendor].INVOICE_LIST, invoicesParams)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(_get(response, 'data.Data.invoices') || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.billing.invoiceList.invoices)
}

export function fetchInvoiceInsightList(payload) {
  let invoiceInsightParams;
  if (_isEmpty(payload) || _isEmpty(payload.yearMonth)) {
    const todayDate = new Date();
    invoiceInsightParams = {
      ...REQUEST_BILLING_INVOICE_INSIGHT_MODEL,
      yearMonth: todayDate.getFullYear()+'-'+add0StringToNumberLessThan10(todayDate.getMonth() + 1)
    };

    if (payload.selectedVendor) {
      invoiceInsightParams.vendor = payload.selectedVendor;
    }
  } else {
    invoiceInsightParams = {
      ...REQUEST_BILLING_INVOICE_INSIGHT_MODEL,
      yearMonth: payload.yearMonth,
      vendor: payload.selectedVendor
    };
  }
  invoiceInsightParams = addLoginUserInfoToPayload(invoiceInsightParams, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[invoiceInsightParams.vendor].INVOICE_INSIGHT_LIST, invoiceInsightParams)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchInvoiceInsightGrid(payload) {
  let invoiceInsightGridParams;
  if (_isEmpty(payload) ) {
    const todayDate = new Date();
    invoiceInsightGridParams = {
      ...REQUEST_BILLING_INVOICE_INSIGHT_MODEL,
      chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
      chargeYear: todayDate.getFullYear()
    };

    if (payload.selectedVendor) {
      invoiceInsightGridParams.vendor = payload.selectedVendor;
    }
    if(payload.viewBy) {
      invoiceInsightGridParams.viewBy = payload.viewBy;
    }
  } else {
    invoiceInsightGridParams = {
      ...REQUEST_BILLING_INVOICE_INSIGHT_MODEL,
      yearMonth: payload.yearMonth,
      vendor: payload.selectedVendor,
      viewBy: payload.viewBy
    };
  }
  invoiceInsightGridParams = addLoginUserInfoToPayload(invoiceInsightGridParams, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[invoiceInsightGridParams.vendor].INVOICE_INSIGHT_GRID, invoiceInsightGridParams)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}
/**
 * Fetch bills, use for monthly trend data and bill summary api
 *
 * @returns {Promise<any>}
 */
export function fetchBillList(payload) {
  // BackUp
  // let fetchBillListPayload = {
  //   ...REQUEST_BILLING_BILLS_MODEL,
  //   vendor: payload.selectedVendor
  // };
  // fetchBillListPayload = addLoginUserInfoToPayload(fetchBillListPayload, store);
  //
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.BILLING[fetchBillListPayload.vendor].BILL_LIST, fetchBillListPayload)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(_get(response, 'data.Data.bills') || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.billing.billList.bills)
}

export function fetchBillingDetail(payload) {
  // BackUp
  // let fetchBillingDetailRequest;
  // if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
  //   const todayDate = new Date();
  //   fetchBillingDetailRequest = {
  //     ...REQUEST_BILLING_DETAILS_MODEL,
  //     chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
  //     chargeYear: todayDate.getFullYear()
  //   };
  //   if (payload.selectedVendor) {
  //     fetchBillingDetailRequest.vendor = payload.selectedVendor;
  //   }
  // } else {
  //   fetchBillingDetailRequest = {
  //     ...REQUEST_BILLING_DETAILS_MODEL,
  //     chargeMonth: payload.chargeMonth,
  //     chargeYear: payload.chargeYear,
  //     vendor: payload.selectedVendor,
  //     tagKey: payload.tagKey
  //   };
  // fetchBillingDetailRequest = addLoginUserInfoToPayload(fetchBillingDetailRequest, store);
  //
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.BILLING[fetchBillingDetailRequest.vendor].BILLING_DETAIL, fetchBillingDetailRequest)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(_get(response, 'data.Data.Details') || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.billing.detailList.Details)
}

export function fetchBillingDetailWithTag(payload) {
  //const payload = addLoginUserInfoToPayload(REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL, store);

  let fetchBillingDetailWithTagRequest = {
    ...REQUEST_BILLING_DETAILS_MODEL,
    chargeMonth: payload.chargeMonth,
    chargeYear: payload.chargeYear,
    vendor: payload.selectedVendor, // payload : selectedVendor > fetch request : vendor
    tagKey: payload.tagKey,
    viewBy: payload.viewBy
  };

  fetchBillingDetailWithTagRequest = addLoginUserInfoToPayload(fetchBillingDetailWithTagRequest, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[fetchBillingDetailWithTagRequest.vendor].BILLING_DETAIL, fetchBillingDetailWithTagRequest)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.Details') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}


export function fetchBillingDetailWithServiceGroup(payload) {
  //const payload = addLoginUserInfoToPayload(REQUEST_BILLING_DETAILS_WITH_TAG_MODEL, store);

  let fetchBillingDetailWithSvgRequest = {
    ...REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL,
    chargeMonth: payload.chargeMonth,
    chargeYear: payload.chargeYear,
    serviceGroupSetNm: payload.serviceGroupSetNm,
    vendor: payload.selectedVendor, // payload : selectedVendor > fetch request : vendor
    viewBy: payload.viewBy
  };

  fetchBillingDetailWithSvgRequest = addLoginUserInfoToPayload(fetchBillingDetailWithSvgRequest, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[fetchBillingDetailWithSvgRequest.vendor].BILLING_DETAIL, fetchBillingDetailWithSvgRequest)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.Details') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchTagOptions(payload) {
  let fetchTagKeysRequestModel = {
    ...REQUEST_TAG_KEYS_MODEL,
    vendor: payload.selectedVendor,
    yearMonth: payload.yearMonth
  };
  fetchTagKeysRequestModel = addLoginUserInfoToPayload(fetchTagKeysRequestModel, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[payload.selectedVendor].TAG_KEYS, fetchTagKeysRequestModel)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.tagInfo') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchServiceGroupOptions(payload) {
  let fetchServiceGroupViewsRequestModel = {
    ...REQUEST_SERVICE_GROUP_VIEWS_MODEL,
    vendor: payload.selectedVendor,
    yearMonth: payload.yearMonth
  };
  fetchServiceGroupViewsRequestModel = addLoginUserInfoToPayload(fetchServiceGroupViewsRequestModel, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.COMMON.SERVICE_GROUP_SETS, fetchServiceGroupViewsRequestModel)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.serviceGroupInfo') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

/**
 * Fetch Computing Resources
 *
 * @returns {Promise<any>}
 */
// export function fetchComputingResources(payload) {
//   let computingResourcesParams;
//   if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
//     const todayDate = new Date();
//     computingResourcesParams = {
//       ...REQUEST_COMPUTING_RESOURCES_MODEL,
//       chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
//       chargeYear: todayDate.getFullYear()
//     };
//
//     if (payload.selectedVendor) {
//       computingResourcesParams.vendor = payload.selectedVendor;
//     }
//   } else {
//     computingResourcesParams = {
//       ...REQUEST_COMPUTING_RESOURCES_MODEL,
//       chargeMonth: payload.chargeMonth,
//       chargeYear: payload.chargeYear,
//       vendor: payload.selectedVendor
//     };
//   }
//   computingResourcesParams = addLoginUserInfoToPayload(computingResourcesParams, store);
//
//   return new Promise((resolve, reject) => {
//     return axios.post(ENDPOINT.BILLING[computingResourcesParams.vendor].COMPUTING_RESOURCES, computingResourcesParams)
//       .then((response) => {
//         if (isFailResponse(response)) {
//           reject(response.data.error);
//         } else {
//           resolve(_get(response, 'data.Data.computingResources') || []);
//         }
//       })
//       .catch((error) => {
//         reject(error);
//       });
//   });
// }


/**
 * Fetch Additional Services
 *
 * @returns {Promise<any>}
 */
export function fetchAdditionalServices(payload) {
  let additionalServicesParams;
  if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
    const todayDate = new Date();
    additionalServicesParams = {
      ...REQUEST_ADDITIONAL_SERVICES_MODEL,
      chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
      chargeYear: todayDate.getFullYear()
    };

    if (payload.selectedVendor) {
      additionalServicesParams.vendor = payload.selectedVendor;
    }
  } else {
    additionalServicesParams = {
      ...REQUEST_ADDITIONAL_SERVICES_MODEL,
      chargeMonth: payload.chargeMonth,
      chargeYear: payload.chargeYear,
      vendor: payload.selectedVendor
    };
  }
  additionalServicesParams = addLoginUserInfoToPayload(additionalServicesParams, store);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING[additionalServicesParams.vendor].ADDITIONAL_SERVICES, additionalServicesParams)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.additionalServices') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

/**
 * Fetch Service Groups
 *
 * @returns {Promise<any>}
 */
export function fetchServiceGroups(payload) {
  // BackUp
  // let serviceGroupsParams;
  // if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
  //   const todayDate = new Date();
  //   serviceGroupsParams = {
  //     ...REQUEST_SERVICE_GROUPS_MODEL,
  //     chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
  //     chargeYear: todayDate.getFullYear()
  //   };
  //
  //   if (payload.selectedVendor) {
  //     serviceGroupsParams.vendor = payload.selectedVendor;
  //   }
  // } else {
  //   serviceGroupsParams = {
  //     ...REQUEST_SERVICE_GROUPS_MODEL,
  //     chargeMonth: payload.chargeMonth,
  //     chargeYear: payload.chargeYear,
  //     vendor: payload.selectedVendor
  //   };
  // }
  // serviceGroupsParams = addLoginUserInfoToPayload(serviceGroupsParams, store);
  //
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.BILLING[serviceGroupsParams.vendor].SERVICE_GROUPS, serviceGroupsParams)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(_get(response, 'data.Data.serviceGroups') || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.billing.serviceGroupList.serviceGroups);
}

/**
 * Fetch AutoSpot
 *
 * @returns {Promise<any>}
 */
export function fetchAutoSpot(payload) {
  // BackUp
  // let autoSpotParams;
  // if (_isEmpty(payload) || _isEmpty(payload.chargeMonth) || _isEmpty(payload.chargeYear)) {
  //   const todayDate = new Date();
  //   autoSpotParams = {
  //     ...REQUEST_AUTOSPOT_MODEL,
  //     chargeMonth: add0StringToNumberLessThan10(todayDate.getMonth() + 1),
  //     chargeYear: todayDate.getFullYear()
  //   };
  //
  //   if (payload.selectedVendor) {
  //     autoSpotParams.vendor = payload.selectedVendor;
  //   }
  // } else {
  //   autoSpotParams = {
  //     ...REQUEST_AUTOSPOT_MODEL,
  //     chargeMonth: payload.chargeMonth,
  //     chargeYear: payload.chargeYear,
  //     vendor: payload.selectedVendor
  //   };
  // }
  // autoSpotParams = addLoginUserInfoToPayload(autoSpotParams, store);
  //
  // return new Promise((resolve, reject) => {
  //   return axios.post(ENDPOINT.BILLING[autoSpotParams.vendor].AUTOSPOT, autoSpotParams)
  //     .then((response) => {
  //       if (isFailResponse(response)) {
  //         reject(response.data.error);
  //       } else {
  //         resolve(_get(response, 'data.Data.autoSpot') || []);
  //       }
  //     })
  //     .catch((error) => {
  //       reject(error);
  //     });
  // });
  return Promise.resolve(mcmpAPIResponseDummyData.billing.autoSpots.autoSpot);
}

export function fetchRecipientList(payload) {

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING.SEND_INVOICE.RECIPIENT_LIST, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data.result') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function sendInvoiceByEmail(payload){
  let invoiceSendPayload = Object.assign(SEND_INVOICE_BY_MAIN_REQUEST_MODEL, payload);

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING.SEND_INVOICE.SEND_EMAIL, invoiceSendPayload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function fetchReservationSettings(payload) {

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING.SEND_INVOICE.CHECK_RESERVATION_SENT, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(_get(response, 'data.Data') || []);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}

export function callDeleteInvoiceReservation(delPayload) {

  let payload = {
    ...DELETE_RESERVATION_REQUEST_MODEL,
    ...delPayload
  };

  return new Promise((resolve, reject) => {
    return axios.post(ENDPOINT.BILLING.SEND_INVOICE.RESERVATION_DELETE, payload)
      .then((response) => {
        if (isFailResponse(response)) {
          reject(response.data.error);
        } else {
          resolve(response.data.Data);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}
