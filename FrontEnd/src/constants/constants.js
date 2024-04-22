
export const API_VERSION = '/api/v3';
export const API_VERSION_PORTAL = '/api_v2.0';

export const API_STATUS = {
  FAIL: 'fail',
  OK: 'OK',
};

export const DEFAULT_PAGE_SIZE = 10;

export const DEFAULT_OPTION_PER_PAGE = [
  { value: 10, text: '10' },
  { value: 20, text: '20' },
  { value: 30, text: '30' },
  { value: 50, text: '50' },
  { value: 100, text: '100' },
  { value: 150, text: '150' },
  { value: 200, text: '200' },
];

export const TOTAL_MONTH_IN_YEAR = 12;

export const CURRENCY = {
  USD: 'USD',
  KRW: 'KRW',
  CNY: 'CNY',
  MXN: 'MXN',
  IDR:'IDR'
};

export const CURRENCY_SYMBOL = {
  USD: '$',
  KRW: '￦',
  CNY: '¥',
  MXN: 'Mex$',
  GCPUSD: '$',
  IDR:'Rp'
};

export const DEFAULT_CURRENCY = CURRENCY.USD;

export const CURRENCY_OPTIONS = [
  {
    value: CURRENCY.KRW,
    text: `${CURRENCY_SYMBOL.KRW} (${CURRENCY.KRW}) `
  },
  {
    value: CURRENCY.USD,
    text: `${CURRENCY_SYMBOL.USD} (${CURRENCY.USD}) `
  },
  {
    value: CURRENCY.CNY,
    text: `${CURRENCY_SYMBOL.CNY} (${CURRENCY.CNY}) `
  },
  {
    value: CURRENCY.MXN,
    text: `${CURRENCY_SYMBOL.MXN} (${CURRENCY.MXN}) `
  },
  {
    value: CURRENCY.IDR,
    text: `${CURRENCY_SYMBOL.IDR} (${CURRENCY.IDR}) `
  }
];

export const FILE_TYPE = {
  CSV: 'csv',
  XLSX: 'xlsx'
};

export const CHART_TYPE = {
  STACK: 'stack',
  LINE: 'line'
};


// just AWS is available now, GCP AZURE will be available latter
// id는 포탈에서 넘어오는 정보와 매핑용으로 사용하고 있고 value는 kenobi에서 사용하는 밴더 정보
export const AVAILABLE_VENDORS =
  [
    {
      id:"AWS",
      value:"AWS"
    },
    {
      id:"GCP",
      value:"GCP"
    },
    {
      id:"AZU",
      value:"AZURE"
    },
    {
      id:"OCI",
      value:"OCI"
    },
    {
      id:"NCP",
      value:"NCP"
    },
    {
      id: "TENCENT",
      value: "TENCENT"
    }
  ];

// all the vendors supported in this project
export const VENDOR = {
  AWS: 'AWS',
  AZURE: 'AZURE',
  GCP: 'GCP',
  ALI: 'ALI',
  OCI: 'OCI',
  NCP: 'NCP',
  TENCENT: 'TENCENT',
  OPENSTACK: 'OPENSTACK'
};

export const VENDOR_OTHERS = {
  AWS: 'AWS-others',
  AZURE: 'AZURE-others',
  GCP: 'GCP-others',
  ALI: 'ALI-others',
  OCI: 'OCI-others',
  NCP: 'NCP-others',
  // TENCENT: 'TENCENT-others'
};

export const VENDOR_NUMBER_OF_OTHERS = {
  AWS: 'AWS-numberOfOthers',
  AZURE: 'AZURE-numberOfOthers',
  GCP: 'GCP-numberOfOthers',
  ALI: 'ALI-numberOfOthers',
  OCI: 'OCI-numberOfOthers',
  NCP: 'NCP-numberOfOthers',
  // TENCENT: 'TENCENT-numberofothers'
};

export const MENU_URL = {
  BILLING: '/billing',
  COST_ANALYTICS: '/cost-analytics',
  DASHBOARD: '/dashboard',
  COST_ANOMALY_DETECTION: '/anomaly-detection',
};

export const DEFAULT_VENDOR_OPTIONS = [
  {
    text: 'AWS',
    value: VENDOR.AWS
  },
  {
    text: 'Azure',
    value: VENDOR.AZURE
  },
  {
    text: 'GCP',
    value: VENDOR.GCP
  },
  {
    text: 'ALI',
    value: VENDOR.ALI,
    disabled: true
  },
  {
    text: 'OCI',
    value: VENDOR.OCI
  },
  {
    text: 'Ncloud',
    value: VENDOR.NCP
  },
  {
    text: 'TENCENT',
    value: VENDOR.TENCENT,
    alias: 'Tencent'
  },
  {
    text: 'OpenStack',
    value: VENDOR.OPENSTACK
  }
];

    //disabled: true
export const DEFAULT_EXCHANGE_RATE = {
  KRW: 1200,
  USD: 1,
  CNY: 7,
  MXN: 20
};

/**
 * This fields use currency is USD
 *
 * @type {Array}
 */
export const USD_CURRENCY_FIELDS = [
  'cloudCost',
  'supportFee',
  'salesDiscount',
  'credit',
  'lastCost',
  'currentCost',
  'cloudServiceChargeByUSD',
  'additionalServiceCharge',
  'cloudOriginalCost',
  'onDemandDiscount',
  'cloudFrontDiscount',
  'cfDiscount',
  'totalChargeUSD'
];

/**
 * This fields use currency is KRW (￦)
 *
 * @type {Array}
 */
export const KRW_CURRENCY_FIELDS = [
  'cloudServiceCharge',
  'MCMPUsageCharge',
  'autospotUsageCharge',
  'additionalService1Fee',
  'additionalServiceFee',
  'additionalServicesObject',
  'totalCharge',
  'exchangedCloudServiceCharge',
];

export const FILTER_TIME = {
  MONTH: 'MONTH',
  WEEK: 'WEEK',
  DAY: 'DAY',
};

export const CHART_ITEM_LABEL = { ESTIMATED: 'estimated' };
export const TIME_CONST = 'time';
export const FULL_TIME_CONST = '__full_time__';
export const START_DATE_WEEK = 'startDate';
export const END_DATE_WEEK = 'endDate';
export const COMPARE_FULL_TIME_CONST = '__compare_full_time__';
export const TOTAL_CONST = '__total__';
export const COMPARE_TOTAL_CONST = '__compare_total__';
export const ACTIVATED_TOTAL_COST= '__activated_total_cost__';
export const ACTIVATED_COMPARE_TOTAL_COST= '_activated_compare_total_cost_';
export const FORMAT_COST = 'format_cost';
export const OPACITY = 'opacity';
export const LINE_DASH = 'lineDash';
export const NUMBER_OF_OTHERS = 'numberOfOthers';
export const SERIES_DASHARRAY = 'seriesDasharray';
export const COMPARE_TIME = 'compareTime';
export const IS_EMPTY_DATE = 'isEmptyDate';
export const RATIO = 'ratio-';
export const COMPARE = 'compare-';
export const MONTHLY_SUM = 'monthlySum';
export const START_DATE = 'startDate';
export const END_DATE = 'endDate';
export const COST_CONST = 'cost';
export const USAGE_CONST = 'usage';
export const IS_MONTHLY = "isMonthly";

// We're facing the issue is that the tooltips with rich HTML content is not showing
// in case the data not full enough on the line chart, so we have to add hidden series to resolve it
// Please refer this link to know more discussion about it https://github.com/amcharts/amcharts4/issues/1759
export const HIDDEN_SERI = 'hidden_seri';

export const COMMON_INFO_REQUEST_MODE = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SAVE_SELECTED_VENDORS = {
  "companyId": 1,
  "selectedVendors": [],
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SAVE_SELECTED_CURRENCY = {
  "companyId": 1,
  "currency": DEFAULT_CURRENCY,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const GET_ALL_VENDORS_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SUBMIT_SURVEY_REQUEST_MODEL = {
  "comment": "",
  "companyId": 1,
  "isHelpful": true,
  "siteCode": "BESPIN",
  "userEmail": "string@gmail.com",
  "userId": "string",
  "userName": "string"
};

export const CHECK_SURVEY_SUBMITTED = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "TDB",
  "userName": "string"
};

export const COST_ANALYTICS_ACTION_TYPE = {
  SEARCH_BAR: 'searchBar',
  CLICK_TO_ANALYZE: 'clickToAnalyze',
  LINK_ANALYZE: 'linkAnalyze'
};

export const ROUTE_NAME = {
  MVP: 'TheMvp',
  COST_ANALYTICS: "TheCostAnalytics",
  BILLING: "TheBilling",
  DASHBOARD: "TheDashboard",
  ALERT: "Alert",
  SAVINGS_PLANS: {
    INDEX: "SavingsPlanIndex",
    COVERAGE: "SavingsPlanCoverage",
    UTILIZATION: "TheSavingsPlansUtilization",
    INVENTORY: "TheSavingsPlansInventory",
    RECOMMENDATION: "TheSavingsPlansRecommendation"
  },
  RESERVED: {
    INDEX: "ReservedIndex",
    COVERAGE: "ReservedCoverage",
    UTILIZATION: "TheReservedUtilization",
    INVENTORY: "TheReservedInventory",
    EC2_RECOMMENDATION: "TheEc2RiRecommendation",
    OPNSRCH_RECOMMENDATION: "TheOpnsrchRiRecommendation",
    RDS_RECOMMENDATION: "TheRdsRiRecommendation",
  },
  AZURE_RESERVED: {
    AZUREINVENTORY: "TheAzureReservedInventory",
    AZUREUTILIZATION: "TheAzureReservedUtilization",
    AZURERIANALYSIS: "TheAzureReservedRianalysis"
  },
  ANOMALY_DETAIL: "TheDashboardAbnormalDetail",
  ANOMALY_LIST:"TheDashboardAbnormalList",
  MIS_OPERATION_STATUS: {
    INDEX: "TheMisOperationStatusIndex",
    CSP: "TheCspOperationStatus",
    ROI: "TheConvertCloudROI",
  }
};

export const SEARCH_BAR_RESULT_GROUP = {
  SUGGESTION: 'suggestion',
  SAVED_FILTER: 'savedFilter',
  RECENT_SEARCH: 'recentSearch',
  NORMAL_OPTIONS: 'normalOptions',
  MORE_FILTERS: 'moreFilters',
  ALL_RESULT: 'allResult'
};

export const CLASSIC_VERSION_PAGE = {
  BILLING: "BILLING",
  DASHBOARD: "DASHBOARD",
  COST_ANALYTICS: "COST_ANALYTICS",
  SERVICE_DASHBOARD: "SERVICE_DASHBOARD",
  SAVINGS_PLANS: "SAVINGS_PLANS",
  MIS_OPERATION_STATUS: "MIS_OPERATION_STATUS",
  PROFILE: "PROFILE",
  SERVICE: "SERVICE",
  PRIVACY_POLICY: "PRIVACY_POLICY",
  SUPPORT: "SUPPORT"
};

export const DEFAULT_PAGE_PATH = {
  BILLING: "/billing",
  DASHBOARD: "/dashboard",
  COST_ANALYTICS: "/cost-analytics",
  SAVINGS_PLANS: "/savings-plans",
  MIS_OPERATION_STATUS: "/mis-operation-status",
  RESRVED: "/reserved"
};

export const BROWSER = {
  IE: 'IE',
  CHROME: 'CHROME'
}

export const MAX_RECENT_SEARCH_COUNT = 10;

export const EXCEPTION = {
  NO_DATA_COLLECTED : {
    message: "Collected data is not exist.",
    code: "10109"
  }
}
