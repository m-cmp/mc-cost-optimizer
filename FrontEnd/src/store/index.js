import Vue from 'vue';
import Vuex from 'vuex';

import commonModule from './modules/common';
import billingModule from './modules/billing';
import costAnalyticsModule from './modules/cost-analytics';
import dashboardModule from './modules/dashboard';
import anomalyModule from './modules/anomaly';
import GlobalConstants from "../constants/globalConstants";
import {AVAILABLE_VENDORS} from "../constants/constants";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    common: commonModule,
    billing: billingModule,
    costAnalytics: costAnalyticsModule,
    dashboard: dashboardModule,
    anomaly: anomalyModule,
  },
  state: {
    loginUser : {               // Login User Info
      siteCd : null,
      userNm : null,
      userId : null,
      userEmail : null,
      curCmpnId: null,
      curCmpnNm : null,
      userLangCd : null
    },
    cmpnList:[],
    sub_domain: null,          // Sub doamin
    CMPN_MENUl_AUTH: null,      // Company Menu Auth
    SECURITY_MENU_AUTH: null,   // Security Menu Auth
    CONSOLE_MENU_AUTH: null,    // Console Menu Auth
    templateList: null,         // Policy template list
    KENOBI_MENU_AUTH: null,        // kenobi Menu Auth
    METERING_MENU_AUTH: null,        // metering Menu Auth
    svcMenuList:[],           // Available Service Menu List
    vendorInfo: null,           // Cloud Vendor Information
    vendorInfoLoadedFlag: false, // The status of Vendor Information Loaded or not
    ACCESS_TOKEN_KEY : GlobalConstants.ACCESS_TOKEN_DEV ,
    accessAuthority: {
      accessDenied : false,
      otpDenied:false,
      allowedIpNull:false,
      allowedIpDenied:false,
      loginDeniedConCurrent: false,
      isConcurrentLogin: false,
    },
    homepageInfo: null
  },
  getters: {
    isConcurrentLogin: state => state.accessAuthority.isConcurrentLogin,
    homepageInfo: state => state.homepageInfo,
    portalUrl: state => state.homepageInfo.availableSvc ? state.homepageInfo.availableSvc.find((svc) => (svc.svcId === 'portal')).svcUrl : null,
    logOutUrl: state => state.homepageInfo.availableSvc ? state.homepageInfo.availableSvc.find((svc) => (svc.svcId === 'portal')).svcUrl : null
  },
  mutations: {
    setLoginUser(state, loginUser) {
      state.loginUser = loginUser
    },
    setSubDomain(state, subDomain) {
      state.sub_domain = subDomain
    },
    setUserCurCompany(state, cmpn) {
      state.loginUser.curCmpnId = cmpn.cmpnId
      state.loginUser.curCmpnNm = cmpn.cmpnNm
    },
    setCompanyList(state, cmpnList){
      state.cmpnList = cmpnList;
    },
    setTemplateList(state, data) {
      state.templateList = data.templateList;
    },
    setKenobiMenuAuth(state, data) {
      state.KENOBI_MENU_AUTH = data.KENOBI_MENU_AUTH
    },
    setMeteringMenuAuth(state, data) {
      state.METERING_MENU_AUTH = data.METERING_MENU_AUTH
    },
    setSvcMenuList(state, data) {
      state.svcMenuList = data.svcMenuList
    },
    setVendorInfo(state, data) {
      let vendorList = data.vendorInfo.map( vendor => vendor.cloudVndrId.toUpperCase());
      state.vendorInfo = AVAILABLE_VENDORS.filter(option => vendorList.includes(option.id)).map(option => {return option.value});
      state.vendorInfoLoadedFlag = true;
    },
    setAccessDenied(state, accessDenied){
      state.accessAuthority.accessDenied = accessDenied;
    },
    setOtpDenied(state, otpDenied){
      state.accessAuthority.otpDenied = otpDenied;
    },
    setAllowedIpNull(state, allowedIpNull){
      state.accessAuthority.allowedIpNull = allowedIpNull;
    },
    setAllowedIpDenied(state, allowedIpDenied){
      state.accessAuthority.allowedIpDenied = allowedIpDenied;
    },
    setLoginDeniedConCurrent(state, loginDeniedConCurrent){
      state.accessAuthority.loginDeniedConCurrent = loginDeniedConCurrent;
    },
    setConcurrentLogin(state, concurrentLogin) {
      state.accessAuthority.isConcurrentLogin = concurrentLogin
    },
    setTokenKey(state, tokenKey){
      state.ACCESS_TOKEN_KEY = tokenKey;
    },
    setHomepageInfo(state, homepageInfo){
      state.homepageInfo = homepageInfo;
    },
  }
});
