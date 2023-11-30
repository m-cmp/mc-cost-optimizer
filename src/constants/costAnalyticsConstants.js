import {SELECTED_VENDOR} from "./dashboardConstants";

export const COST_ANALYTICS_DATA_REQUEST_MODEL = {
  "isAmortizedCost": false,
  "companyId": 1,
  "isCompare": false,
  "compareEndDate": "2019-07-31",
  "compareStartDate": "2019-07-15",
  "compareTimeFrame": "month_to_date",
  "endDate": "2019-08-12",
  "filters": {
    "ali": {},
    "aws": {},
    "azure": {},
    "gcp": {}
  },
  "itemsPerPage": 30,
  "page": 1,
  "selectedVendors": [],
  "siteCode": "BESPIN",
  "startDate": "2019-08-01",
  "timeFrame": "month_to_date",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "viewBy": "account"
}

export const DETAIL_COST_ANALYTICS_DATA_REQUEST_MODEL = {
  "isAmortizedCost": true,
  "companyId": 1,
  "isCompare": true,
  "compareCostDate": null,
  "costDate": "2019-08-12",
  "filters": {
    "ali": {},
    "aws": {},
    "azure": {},
    "gcp": {}
  },
  "detailFilters": {},
  "selectedVendor": "AWS",
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "viewBy": "account",
  "viewByItem": "",
  "isMonthly": false
};

export const PORTION_DETAIL_COST_ANALYTICS_DATA_REQUEST_MODEL = {
  "companyId": 1,
  "compareCostDate": null,
  "costDate": "2019-08-12",
  "detailFilters": {},
  "filters": {
    "ali": {},
    "aws": {},
    "azure": {},
    "gcp": {}
  },
  "isAmortizedCost": false,
  "isCompare": false,
  "selectedVendor": "AWS",
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "viewBy": "account",
  "viewByItem": ""
};

export const COST_ANALYTICS_TIME_FRAME = {
  MONTH_TO_DATE: 'month_to_date',
  LAST_MONTH: 'last_month',
  LAST_7_DAYS: 'last_7_days',
  LAST_14_DAYS: 'last_14_days',
  LAST_60_DAYS: 'last_60_days',
  LAST_3_MONTHS: 'last_3_months',
  LAST_6_MONTHS: 'last_6_months',
  LAST_12_MONTHS: 'last_12_months',
  YEAR_TO_DATE: 'year_to_date',
  CUSTOM: 'custom'
}

export const FILTER_DATA_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const RELATED_FILTER_REQUEST_MODEL = {
  "conditions": {
    "aws": {},
    "azure": {},
    "gcp": {},
    "ali": {}
  },
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const USER_FILTER_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SAVE_USER_FILTER_REQUEST_MODEL = {
  "companyId": 1,
  "conditions": {
    "ali": {},
    "aws": {},
    "azure": {},
    "gcp": {}
  },
  "filterName": "string",
  "index": -1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SAVE_AS_USER_FILTER_REQUEST_MODEL = {
  "companyId": 1,
  "conditions": {
    "ali": {},
    "aws": {},
    "azure": {},
    "gcp": {}
  },
  "filterName": "string",
  "index": -1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const DELETE_USER_FILTER_REQUEST_MODEL = {
  "companyId": 1,
  "filterIndex": -1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const FILTER_SEARCH_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "word": "string"
};


export const COST_ANALYTICS_VIEW_BY_VENDORS = [
  {
    text: 'header.aws',
    value: SELECTED_VENDOR.AWS
  },
  {
    text: 'header.azure',
    value: SELECTED_VENDOR.AZURE
  },
  {
    text: 'header.gcp',
    value: SELECTED_VENDOR.GCP
  },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // }
];
export const COST_ANALYTICS_TIME_FRAME_OPTIONS = [
  {
    text: 'costAnalytics.header.timeFrameOptions.thisMonthMtd',
    value: COST_ANALYTICS_TIME_FRAME.MONTH_TO_DATE
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.lastMonth',
    value: COST_ANALYTICS_TIME_FRAME.LAST_MONTH
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.last7Days',
    value: COST_ANALYTICS_TIME_FRAME.LAST_7_DAYS
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.last14Days',
    value: COST_ANALYTICS_TIME_FRAME.LAST_14_DAYS
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.last60Days',
    value: COST_ANALYTICS_TIME_FRAME.LAST_60_DAYS
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.last3Months',
    value: COST_ANALYTICS_TIME_FRAME.LAST_3_MONTHS
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.last6Months',
    value: COST_ANALYTICS_TIME_FRAME.LAST_6_MONTHS
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.last12Months',
    value: COST_ANALYTICS_TIME_FRAME.LAST_12_MONTHS
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.thisYearYtd',
    value: COST_ANALYTICS_TIME_FRAME.YEAR_TO_DATE
  },
  {
    text: 'costAnalytics.header.timeFrameOptions.custom',
    value: COST_ANALYTICS_TIME_FRAME.CUSTOM
  },
];

export const COST_TREND_BY = {
  ACCOUNT: 'account',
  PRODUCT: 'product',
  REGION: 'region',
  TAG: 'tag',
  SERVICE_GROUP: 'serviceGroup'
};

export const COST_TREND_BY_OPTION = [
  {
    text: 'costAnalytics.costAnalyticsTrend.trendByOption.account',
    value: COST_TREND_BY.ACCOUNT,
  },
  {
    text: 'costAnalytics.costAnalyticsTrend.trendByOption.product',
    value: COST_TREND_BY.PRODUCT,
  },
  {
    text: 'costAnalytics.costAnalyticsTrend.trendByOption.region',
    value: COST_TREND_BY.REGION,
  },
  { text: 'costAnalytics.costAnalyticsTrend.trendByOption.serviceGroup',
    value: COST_TREND_BY.SERVICE_GROUP
  }
];

export const DEFAULT_CATEGORY_Y = "category";

export const DEFAULT_STACKED_BAR_CATEGORIES = {
  ACCOUNT: "accounts",
  PRODUCT: "products",
  REGION: "regions",
  USAGE_TYPE: "usageType"
};

export const COST_ANALYTICS_COMPARE = {
  LAST_PERIOD: 'last_period',
  CUSTOM: 'custom'
}

export const PREFIX_COMPARE = {
  RATIO: 'ratio'
}

export const NO_DATA_TABLE_VALUE = '-';

export const COMPARE_COST_TYPE = {
  INDIVIDUAL_COST: 'individual_cost',
  AVERAGE: 'average'
}

export const COMPARE_COST_TYPE_OPTIONS = [
  {
    text: 'costAnalytics.header.individualCost',
    value: COMPARE_COST_TYPE.INDIVIDUAL_COST
  },
  {
    text: 'costAnalytics.header.average',
    value: COMPARE_COST_TYPE.AVERAGE
  }
]

export const COST_ANALYTICS_COMPARE_OPTIONS = [
  {
    text: 'costAnalytics.header.compareOptions.lastPeriod',
    value: COST_ANALYTICS_COMPARE.LAST_PERIOD
  },
  {
    text: 'costAnalytics.header.compareOptions.custom',
    value: COST_ANALYTICS_TIME_FRAME.CUSTOM
  }
]

export const COST_ANALYTICS_SEARCH_FIELD = {
  ALL: 'all',
  ACCOUNT: 'account',
  PRODUCT: 'product',
  REGION: 'region'
}

export const VIEW_OPTION_VALUES = {
  USAGE_DATE: 'usageDate',
  ACCOUNT: 'linkedAccountId',
  PRODUCT: 'productName',
  REGION: 'region',
  RESOURCE_ID: 'resourceId',
  RESOURCE_NAME: 'resourceName',
  USAGE_TYPE: 'usageType',
  TRANSFER_TYPE: 'transferType',
  SERVICE_GROUP_VIEW: 'serviceGroupView',
  SERVICE_GROUP: 'serviceGroup',
  AVAILABILITY_ZONE: 'availabilityZone',
  API_OPERATION: 'apiOperation',
  PLATFORM: 'platform',
  DATABASE_ENGINE: 'databaseEngine',
  DESCRIPTION: 'itemDescription',
  INVOICE_ID: 'invoiceId',
  CHARGE_TYPE: 'chargeType',
  USAGE: 'usage',
  INFRA_TYPE: 'infraType',
  UNIT_PRICE : 'unitPrice',
  COST: 'cost',
  VENDOR: 'vendor',
  SKU_ID: 'skuId',
  SKU_DESC: 'skuDesc',
  USE_UNIT: 'useUnit'
}
export const COMMON_VIEW_DETAIL_FIELD_OPTIONS = [
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.vendor',
    isChecked: true,
    value: VIEW_OPTION_VALUES.VENDOR
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.account',
    isChecked: true,
    value: VIEW_OPTION_VALUES.ACCOUNT
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.product',
    isChecked: true,
    value: VIEW_OPTION_VALUES.PRODUCT
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.region',
    isChecked: true,
    value: VIEW_OPTION_VALUES.REGION
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.usageDate',
    isChecked: false,
    value: VIEW_OPTION_VALUES.USAGE_DATE
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.usage',
    isChecked: true,
    value: VIEW_OPTION_VALUES.USAGE
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.cost',
    isChecked: true,
    value: VIEW_OPTION_VALUES.COST
  }
];

export const AWS_VIEW_DETAIL_FIELD_OPTIONS = [
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.resourceId',
    isChecked: false,
    value: VIEW_OPTION_VALUES.RESOURCE_ID
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.resourceName',
    isChecked: false,
    value: VIEW_OPTION_VALUES.RESOURCE_NAME
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.usageType',
    isChecked: true,
    value: VIEW_OPTION_VALUES.USAGE_TYPE
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.transferType',
    isChecked: false,
    value: VIEW_OPTION_VALUES.TRANSFER_TYPE
  },
  // {
  //   text: 'costAnalytics.costAnalyticsDetail.tableDetail.serviceGroupView',
  //   isChecked: false,
  //   value: VIEW_OPTION_VALUES.SERVICE_GROUP_VIEW
  // },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.serviceGroup',
    isChecked: false,
    value: VIEW_OPTION_VALUES.SERVICE_GROUP
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.availabilityZone',
    isChecked: false,
    value: VIEW_OPTION_VALUES.AVAILABILITY_ZONE
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.apiOperation',
    isChecked: false,
    value: VIEW_OPTION_VALUES.API_OPERATION
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.platform',
    isChecked: false,
    value: VIEW_OPTION_VALUES.PLATFORM
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.databaseEngine',
    isChecked: false,
    value: VIEW_OPTION_VALUES.DATABASE_ENGINE
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.description',
    isChecked: false,
    value: VIEW_OPTION_VALUES.DESCRIPTION
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.invoiceId',
    isChecked: false,
    value: VIEW_OPTION_VALUES.INVOICE_ID
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.chargeType',
    isChecked: false,
    value: VIEW_OPTION_VALUES.CHARGE_TYPE
  }
];

export const AZURE_VIEW_DETAIL_FIELD_OPTIONS = [
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.infraType',
    isChecked: true,
    value: VIEW_OPTION_VALUES.INFRA_TYPE
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.unitPrice',
    isChecked: true,
    value: VIEW_OPTION_VALUES.UNIT_PRICE
  }
];

export const GCP_VIEW_DETAIL_FIELD_OPTIONS = [
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.skuId',
    isChecked: true,
    value: VIEW_OPTION_VALUES.SKU_ID
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.skuDesc',
    isChecked: true,
    value: VIEW_OPTION_VALUES.SKU_DESC
  },
  {
    text: 'costAnalytics.costAnalyticsDetail.tableDetail.useUnit',
    isChecked: true,
    value: VIEW_OPTION_VALUES.USE_UNIT
  }
];

export const COST_ANALYTICS_SEARCH_FIELD_OPTIONS = [
  {
    text: 'costAnalytics.header.searchField.all',
    value: COST_ANALYTICS_SEARCH_FIELD.ALL
  },
  {
    text: 'costAnalytics.header.searchField.account',
    value: COST_ANALYTICS_SEARCH_FIELD.ACCOUNT
  },
  {
    text: 'costAnalytics.header.searchField.product',
    value: COST_ANALYTICS_SEARCH_FIELD.PRODUCT
  },
  {
    text: 'costAnalytics.header.searchField.region',
    value: COST_ANALYTICS_SEARCH_FIELD.REGION
  },
]

export const DATE_FORMAT = 'YYYY-MM-DD';

export const COST_TREND_CHART_DATA_EXTRA_FIELD = {
  SOURCE_DATA: "costTrendSourceDataKeys",
  COMPARE_DATA: "costTrendCompareDataKeys"
}
export const COST_ANALYTICS_RANGE_COLOR = {
  1: '#95AAE6',
  2: '#63CFC7',
  3: '#E47BCC',
  4: '#DDC79A',
  5: '#9ECA78',
  6: '#65BEE5',
  7: '#F0AA80',
  8: '#C6D763',
  9: '#AC88EE',
  10: '#C3BE7A',
  11: '#8099E1',
  12: '#55C6BC',
  13: '#DF6AC2',
  14: '#D7BC86',
  15: '#8BC067',
  16: '#57B2E0',
  17: '#EE996E',
  18: '#BBCF55',
  19: '#9C75EB',
  20: '#B8B169',
  21: '#6B87D9',
  22: '#47BAAE',
  23: '#D658B5',
  24: '#CDAE71',
  25: '#77B355',
  26: '#48A3D7',
  27: '#E8875B',
  28: '#ADC447',
  29: '#8A61E4',
  30: '#A9A157',
  31: '#5674D0',
  32: '#39AEA1',
  33: '#CD47A9',
  34: '#C3A15C',
  35: '#62A645',
  36: '#3A94CE',
  37: '#E1744A',
  38: '#9FB939',
  39: '#784FDD',
  40: '#9C9247',
  41: '#4363C7',
  42: '#2DA295',
  43: '#C6389C',
  44: '#BA9548',
  45: '#4F9936',
  46: '#2E86C6',
  47: '#DA633A',
  48: '#93AF2D',
  49: '#683ED6',
  50: '#8F8537',

};

export const OPACITY_VALUES = {
  NO_CHANGE: 0.2,
  FADEOUT: 0.5,
  DEFAULT: 1
}

export const STROKE_OPACITY_VALUES = {
  INACTIVE: 0.3,
  ACTIVE: 2
}

export const COST_ANALYTICS_DROPDOWN_OPTIONS_VALUE = {
  CSV_DOWNLOAD: 'CSV_DOWNLOAD',
  EMAIL_EXPORT: 'EMAIL_EXPORT'
};

export const COST_ANALYTICS_DROPDOWN_OPTIONS = [
  {
    icon: 'get_app',
    text: 'costAnalytics.dropdown.option.csvDownload',
    value: COST_ANALYTICS_DROPDOWN_OPTIONS_VALUE.CSV_DOWNLOAD
  }
];

export const AWS_COST_ANALYTICS_DROPDOWN_OPTIONS = [
  {
    icon: 'get_app',
    text: 'costAnalytics.dropdown.option.csvDownload',
    value: COST_ANALYTICS_DROPDOWN_OPTIONS_VALUE.CSV_DOWNLOAD
  },
  {
    icon: 'email',
    text: 'costAnalytics.dropdown.option.emailExport',
    value: COST_ANALYTICS_DROPDOWN_OPTIONS_VALUE.EMAIL_EXPORT
  },
];

export const AZURE_COST_ANALYTICS_DROPDOWN_OPTIONS = [
  {
    icon: 'get_app',
    text: 'costAnalytics.dropdown.option.csvDownload',
    value: COST_ANALYTICS_DROPDOWN_OPTIONS_VALUE.CSV_DOWNLOAD
  },
  {
    icon: 'email',
    text: 'costAnalytics.dropdown.option.emailExport',
    value: COST_ANALYTICS_DROPDOWN_OPTIONS_VALUE.EMAIL_EXPORT
  },
];

export const MAX_FILTER_NAME_LENGTH = 30;

// from API `/filter`
export const FILTER_RESPONSE_FIELD_BY_FIELD = {
  product: 'product',
  account: 'linkedAccount',
  region: 'region',
  serviceGroup: 'serviceGroup',
  tags: 'tags',
  instanceType: 'instanceType',
  usageType: 'usageType',
  apiOperation: 'apiOperation',
  chargeType: 'chargeType',
  availabilityZone: 'availabilityZone',
  platform: 'platform',
  purchaseOption: 'purchaseOption',
  database: 'database',
  cacheEngine: 'cacheEngine',
  instanceTypeFamily: 'instanceTypeFamily',
  resourceId: 'resourceId',
  productFamily: 'productFamily',
  vendor: 'vendor',
  invoiceId: 'invoiceId',
  transferType: 'transferType',
};

// from other (than `/filter`) APIs
export const FILTER_REQUEST_FIELD_BY_FIELD = {
  product: 'productCode',
  account: 'linkedAccountId',
  region: 'region',
  serviceGroup: 'serviceGroup',
  tags: 'tags',
  instanceType: 'instanceType',
  usageType: 'usageType',
  apiOperation: 'apiOperation',
  chargeType: 'chargeType',
  availabilityZone: 'availabilityZone',
  platform: 'platform',
  purchaseOption: 'purchaseOption',
  database: 'database',
  cacheEngine: 'cacheEngine',
  instanceTypeFamily: 'instanceTypeFamily',
  resourceId: 'resourceId',
  productFamily: 'productFamily',
  vendor: 'vendor',
  invoiceId: 'invoiceId',
  transferType: 'transferType',
};

export const FILTER_TREE_FORM_FIELDS = [
  FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup,
  FILTER_REQUEST_FIELD_BY_FIELD.tags
  // FILTER_REQUEST_FIELD_BY_FIELD.invoiceId
];

export const FILTER_VENDOR_SELECT_ENABLE = false
export const FILTER_TREE_FORM_SINGLE_KEY_FIELDS = [
  FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup,
  // FILTER_REQUEST_FIELD_BY_FIELD.tags
];
export const FILTER_TREE_FORM_MULTI_EXCEPT_VALUES = [
  // 'NON-TAG'
]

export const SPECIAL_FILTER_OPTION = {
  NO_VALUE: 'NO_VALUE',
};

export const INTERACTION_MAIN_FILTER_MODEL = {
  [FILTER_REQUEST_FIELD_BY_FIELD.account]: {
    text: 'costAnalytics.filterInteraction.field.account',
    field: FILTER_REQUEST_FIELD_BY_FIELD.account,
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.product]: {
    text: 'costAnalytics.filterInteraction.field.product',
    field: FILTER_REQUEST_FIELD_BY_FIELD.product,
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.region]: {
    text: 'costAnalytics.filterInteraction.field.region',
    field: FILTER_REQUEST_FIELD_BY_FIELD.region,
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
};

export const INTERACTION_ADDITIONAL_FILTER_MODEL = {
  [FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup]: {
    text: 'costAnalytics.filterInteraction.field.serviceGroup',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.tags]: {
    text: 'costAnalytics.filterInteraction.field.tag',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.instanceType]: {
    text: 'costAnalytics.filterInteraction.field.instanceType',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.usageType]: {
    text: 'costAnalytics.filterInteraction.field.usageType',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.apiOperation]: {
    text: 'costAnalytics.filterInteraction.field.APIOperation',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.chargeType]: {
    text: 'costAnalytics.filterInteraction.field.chargeType',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.availabilityZone]: {
    text: 'costAnalytics.filterInteraction.field.availabilityZone',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.platform]: {
    text: 'costAnalytics.filterInteraction.field.platform',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.purchaseOption]: {
    text: 'costAnalytics.filterInteraction.field.purchaseOption',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.database]: {
    text: 'costAnalytics.filterInteraction.field.databaseEngine',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.cacheEngine]: {
    text: 'costAnalytics.filterInteraction.field.cacheEngine',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.instanceTypeFamily]: {
    text: 'costAnalytics.filterInteraction.field.instanceFamily',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.resourceId]: {
    text: 'costAnalytics.filterInteraction.field.resourceID',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.productFamily]: {
    text: 'costAnalytics.filterInteraction.field.productFamily',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.invoiceId]: {
    text: 'costAnalytics.filterInteraction.field.invoiceID',
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.transferType]: {
    text: 'costAnalytics.filterInteraction.field.transferType',
  },
};

export const MAIN_FILTER_MODEL = {
  [FILTER_REQUEST_FIELD_BY_FIELD.account]: {
    text: 'costAnalytics.header.advancedFilter.filterField.account',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.product]: {
    text: 'costAnalytics.header.advancedFilter.filterField.product',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.region]: {
    text: 'costAnalytics.header.advancedFilter.filterField.region',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
};

export const ADDITIONAL_FILTER_MODEL = {
  //TODO (2020.11.06) 릴리즈 이후 다시 복구예정
  [FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup]: {
    text: 'costAnalytics.header.advancedFilter.filterField.serviceGroup',
    value: FILTER_REQUEST_FIELD_BY_FIELD.serviceGroup,
    vendors: {
      aws: {},
      azure: {},
      gcp: {},
      ali: {},
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.tags]: {
    text: 'costAnalytics.header.advancedFilter.filterField.tag',
    value: FILTER_REQUEST_FIELD_BY_FIELD.tags,
    vendors: {
      aws: {},
      azure: {},
      gcp: {},
      ali: {},
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.instanceType]: {
    text: 'costAnalytics.header.advancedFilter.filterField.instanceType',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.usageType]: {
    text: 'costAnalytics.header.advancedFilter.filterField.usageType',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.apiOperation]: {
    text: 'costAnalytics.header.advancedFilter.filterField.APIOperation',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.chargeType]: {
    text: 'costAnalytics.header.advancedFilter.filterField.chargeType',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  //TODO (2020.11.06) 릴리즈 이후 다시 복구예정
  // [FILTER_REQUEST_FIELD_BY_FIELD.availabilityZone]: {
  //   text: 'costAnalytics.header.advancedFilter.filterField.availabilityZone',
  //   vendors: {
  //     aws: [],
  //     azure: [],
  //     gcp: [],
  //     ali: [],
  //   }
  // },
  [FILTER_REQUEST_FIELD_BY_FIELD.platform]: {
    text: 'costAnalytics.header.advancedFilter.filterField.platform',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  // [FILTER_REQUEST_FIELD_BY_FIELD.purchaseOption]: {
  //   text: 'costAnalytics.header.advancedFilter.filterField.purchaseOption',
  //   vendors: {
  //     aws: [],
  //     azure: [],
  //     gcp: [],
  //     ali: [],
  //   }
  // }, // 구매옵션은 데이터를 가져올 수 없어서 일단 제거
  [FILTER_REQUEST_FIELD_BY_FIELD.database]: {
    text: 'costAnalytics.header.advancedFilter.filterField.databaseEngine',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.cacheEngine]: {
    text: 'costAnalytics.header.advancedFilter.filterField.cacheEngine',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.instanceTypeFamily]: {
    text: 'costAnalytics.header.advancedFilter.filterField.instanceFamily',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  //TODO (2020.11.06) 릴리즈 이후 다시 복구예정
  // [FILTER_REQUEST_FIELD_BY_FIELD.resourceId]: {
  //   text: 'costAnalytics.header.advancedFilter.filterField.resourceID',
  //   vendors: {
  //     aws: [],
  //     azure: [],
  //     gcp: [],
  //     ali: [],
  //   }
  // },
  [FILTER_REQUEST_FIELD_BY_FIELD.productFamily]: {
    text: 'costAnalytics.header.advancedFilter.filterField.productFamily',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.invoiceId]: {
    text: 'costAnalytics.header.advancedFilter.filterField.invoiceID',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
  [FILTER_REQUEST_FIELD_BY_FIELD.transferType]: {
    text: 'costAnalytics.header.advancedFilter.filterField.transferType',
    vendors: {
      aws: [],
      azure: [],
      gcp: [],
      ali: [],
    }
  },
};

export const MAIN_FILTER_FIELDS = [
  FILTER_REQUEST_FIELD_BY_FIELD.account,
  FILTER_REQUEST_FIELD_BY_FIELD.product,
  FILTER_REQUEST_FIELD_BY_FIELD.region
];

export const PORTION_DEFAULT_HORIZONTAL_STATES = {
  account: [],
  product: [],
  region: [],
  usageType: [],
};

export const PORTION_CHART_TYPE = {
  ACCOUNT: "ACCOUNT",
  PRODUCT: "PRODUCT",
  REGION: "REGION",
  USAGE_TYPE: "USAGE_TYPE"
};


export const SORT_TYPE = {
  NONE: 'NONE',
  ASC: 'ASC',
  DESC: 'DESC'
};

export const SEARCH_BAR_SUGGESTION_VALUE = {
  COST_OF_THIS_MONTH: 'costOfThisMonth',
  COST_OF_THIS_MONTH_BY_ACCOUNT: 'costOfThisMonthByAccount',
  COST_OF_THIS_MONTH_BY_PRODUCT: 'costOfThisMonthByProduct',
};

export const PORTION_DETAIL_NO_OTHERS_NAME = 'PORTION_DETAIL_NO_OTHERS';

export const DEFAULT_COST_ANALYTICS_DATA = {
  items: [],
  totalCost : 0
};

export const SEARCH_DEBOUNCE_TIME = {
  MAIN_FILTER: 200,
  ADDITIONAL_FILTER: 250,
  TREE_ADDITIONAL_FILTER: 200,
};

export const INFINITE_LOADING_CONFIG = {
  PER_PAGE: 50,
  SIMULATED_LOADING_TIME: 200,
  SCROLL_TO_TOP_ON_SEARCH_DURATION: 1,
  APPLIED_FIELDS: [
    //TODO (2020.11.06) 릴리즈 이후 다시 복구예정
    //FILTER_REQUEST_FIELD_BY_FIELD.resourceId,
  ]
};

export const FILTER_SAVED_TOAST_TIMEOUT = 500;

export const COST_ANALYTICS_NEW_UPDATE = {
  DATE: 'date',
  MORE_FILTERS: 'more_filters',
  VIEW_BY_OPTION: 'view_by_option',
  DRILL_DOWN_TO_THE_COST_OF_THE_DAY: 'drill_down_to_the_cost_of_the_day',
};

export const COST_ANALYTICS_NEW_UPDATE_OPTIONS = [
  {
    text: 'onboarding.welcome.newUpdates.costAnalytics.date',
    value: COST_ANALYTICS_NEW_UPDATE.DATE
  },
  {
    text: 'onboarding.welcome.newUpdates.costAnalytics.moreFilters',
    value: COST_ANALYTICS_NEW_UPDATE.MORE_FILTERS
  },
  {
    text: 'onboarding.welcome.newUpdates.costAnalytics.viewByOption',
    value: COST_ANALYTICS_NEW_UPDATE.VIEW_BY_OPTION
  },
  {
    text: 'onboarding.welcome.newUpdates.costAnalytics.drillDownToTheCostOfTheDay',
    value: COST_ANALYTICS_NEW_UPDATE.DRILL_DOWN_TO_THE_COST_OF_THE_DAY
  },
];

export const DETAIL_DISPLAY_MODE = {
  DEFAULT: 'DEFAULT',
  COMPARE_PORTION: 'COMPARE_PORTION',
  COMPARE_ABSOLUTE: 'COMPARE_ABSOLUTE',
  COMPARE_LEGEND: 'COMPARE_LEGEND'
};

export const MAX_NUMBER_OF_PORTION = 5;

export const COMPARE_LABEL = 'compare';

export const MAX_COLUMN_FIELDS_FOR_COMPARE_MODE = [
    'invoiceId'
  , 'linkedAccountId'
  , 'linkedAccountAlias'
  , 'productCode'
  , 'productName'
  , 'region'
  , 'regionName'
  , 'resourceId'
  , 'usageType'
  , 'transferType'
  , 'serviceGroupView'
  , 'serviceGroup'
  , 'availabilityZone'
  , 'apiOperation'
  , 'platform'
  , 'databaseEngine'
  , 'invoiceId'
  , 'chargeType'
  , 'itemDescription'
];

// Detail Grid > 보기 옵션 Dropbox 의 Default
export const DEFAULT_VISIBLE_COLUMN_FIELDS = ['vendor', 'linkedAccountId', 'productName', 'region', 'usageType', 'usage', 'cost'];

export const DEFAULT_AZURE_VISIBLE_COLUMN_FIELDS = ['vendor', 'linkedAccountId', 'productName', 'region', 'infraType', 'unitPrice', 'usage', 'cost'];

export const DEFAULT_GCP_VISIBLE_COLUMN_FIELDS = ['vendor', 'linkedAccountId', 'productName', 'region', 'skuId', 'skuDesc', 'useCpct', 'useUnit'];

export const TOOLTIP_POSITION = {
  TOOLTIP_RIGHT: 'tooltip-right-custom',
  TOOLTIP_LEFT: 'tooltip-left-custom',
}
