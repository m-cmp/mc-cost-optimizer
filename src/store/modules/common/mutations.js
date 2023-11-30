const SET_LANG = (state, language) => {
  state.language = language;
};

const SET_IS_LOADING = (state, isLoading) => {
  state.isLoading = isLoading;
};

const SET_ALL_VENDORS = (state, vendors) => {
  state.allVendors = vendors;
};

const SET_AVAILABLE_VENDORS = (state, availableVendor ) => {
  state.availableVendor = availableVendor;
}

const SET_SELECTED_VENDORS = (state, selectedVendors) => {
  state.info.selectedVendors = selectedVendors;
};

const SET_INFO = (state, info) => {
  state.info = info;
};

const SET_SELECTED_CURRENCY = (state, selectedCurrency) => {
  state.info.selectedCurrency = selectedCurrency;
};

const SET_SEARCH_FROM = (state, pageURL) => {
  state.searchFrom = pageURL;
};

const SET_SUBMITTED_SURVEY_STATUS = (state, submittedState) => {
  state.submittedSurvey = submittedState;
};

const SET_BROWSER = (state, browser) => {
  state.browser = browser;
}


const SET_IS_COMPANY_MAX_DATETIME_LOADING = (state, isLoading) => {
  state.isCompanyMaxDatetimeLoading = isLoading;
};

const COMPANY_MAX_DATETIME_UPDATED = (state, data) => {
  state.companyMaxDatetime = data;
};

export default {
  SET_LANG,
  SET_IS_LOADING,
  SET_ALL_VENDORS,
  SET_INFO,
  SET_SELECTED_VENDORS,
  SET_AVAILABLE_VENDORS,
  SET_SELECTED_CURRENCY,
  SET_SEARCH_FROM,
  SET_SUBMITTED_SURVEY_STATUS,
  SET_BROWSER,

  SET_IS_COMPANY_MAX_DATETIME_LOADING,
  COMPANY_MAX_DATETIME_UPDATED
};
