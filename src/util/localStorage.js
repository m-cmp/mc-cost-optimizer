export const LOCAL_STORAGE_KEY = {
  BILLING_TABLE_STATE: 'billingTablesState',
  BILLING_CURRENT_STEP: 'billingCurrentStep',
  COST_ANALYTICS_CURRENT_STEP: 'costAnalyticsCurrentStep',
  DASHBOARD_CURRENT_STEP: 'dashboardCurrentStep',
  LANGUAGE: 'language',
  RECENT_SEARCH: 'recentSearch',
  //true: 팝업, false: 팝업X
  DASHBOARD_WELCOME_POPUP: 'dashboardWelcomePopup'
};

export function getValueFromStorageByKey(key) {
  return JSON.parse(localStorage.getItem(key))
}

export function setValueToStorageByKey(key, value) {
  localStorage.setItem(key, JSON.stringify(value))
}
