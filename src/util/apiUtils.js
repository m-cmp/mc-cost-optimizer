import {API_STATUS, VENDOR} from '@/constants/constants';
import _get from 'lodash/get';

function isFailResponse(response) {
  return _get(response, 'data.status') === API_STATUS.FAIL;
}

function addLoginUserInfoToPayload(payload, store) {

  // TODO Vendor 선택 문제로 인한 오류 발생으로 임시 처리
  let vendors = null;
  try {
    vendors = store.state.vendorInfo != null && store.state.vendorInfo.length > 0 ? store.state.vendorInfo[0] : (payload.selectedVendors != null ? payload.selectedVendors[0] : null);
  } catch(e) {
    //console.error(">>>>>>>>>>>> error : ", e)
  }

  return Object.assign({}, payload, {
    userId: store.state.loginUser.userId,
    userEmail: store.state.loginUser.userEmail,
    userName: store.state.loginUser.userNm,
    siteCode: store.state.loginUser.siteCd,
    companyId: store.state.loginUser.curCmpnId,
    defaultVendor: vendors
  });
}

export {
  isFailResponse,
  addLoginUserInfoToPayload
};
