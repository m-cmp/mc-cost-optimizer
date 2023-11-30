const language = (state) => state.language;
const allVendors = (state) => state.allVendors;
const availableVendor = (state) => state.availableVendor;
const info = (state) => state.info;
// another getter because it may be from a separate API in the future. Assume state.info does not have currencies.
const exchangeRate = (state) => state.info.currencies;
const searchFrom = (state) => state.searchFrom;
const isLoading = (state) => state.isLoading;
const submittedSurvey = (state) => state.submittedSurvey;
const browser = (state) =>  state.browser;

const getCompanyMaxDatetime = state  => state.companyMaxDatetime;
const isCompanyMaxDatetimeLoading = state  => state.isCompanyMaxDatetimeLoading;
const portalUrl = (state) => state.homepageInfo.availableSvc ? state.homepageInfo.availableSvc.find((svc) => (svc.svcId === 'portal')).svcUrl : null;

export default {
  language,
  allVendors,
  availableVendor,
  info,
  exchangeRate,
  searchFrom,
  isLoading,
  submittedSurvey,
  browser,
  getCompanyMaxDatetime,
  isCompanyMaxDatetimeLoading,
  portalUrl
};
