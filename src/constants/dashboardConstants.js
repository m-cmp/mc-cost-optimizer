import {CHART_TYPE} from '@/constants/constants';
import {VENDOR} from "./constants";

export const DASHBOARD_BOOTSTRAP_SETTINGS_BREAKPOINT = { lg: 2536, md: 1920, sm: 1304, xs: 1000, xxs: 0}

// export const DASHBOARD_BOOTSTRAP_SETTINGS_COL = { lg: 24, md: 18, sm: 12, xs: 6, xxs: 3 }
export const DASHBOARD_BOOTSTRAP_SETTINGS_COL = { lg: 24, md: 24, sm: 24, xs: 12, xxs: 6 }


export const COST_MONTH_TO_DATE_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ]
};

export const ESTIMATED_COST_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ]
};

export const BUDGET_DATA_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ]
};

export const TOTAL_SAVING_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ]
};

export const SAVE_AS_DASHBOARD_REQUEST_MODEL = {
  "companyId": 1,
  "dashboard": {
    "dashboardName": "string",
    "index": 0,
    "isDashboardSelected": true,
    "isTemplate": true,
    "widgets": [
      {
        "chartType": "string",
        "customFilter": [
          {
            "item": "string",
            "vendor": "string"
          }
        ],
        "dateType": "string",
        "filter": "string",
        "height": 0,
        "index": 0,
        "scale": "string",
        "threshold": 0,
        "timeFrame": "string",
        "viewBy": "string",
        "widgetType": "string",
        "width": 0,
        "x": 0,
        "y": 0,
        "useYn": "string"
      }
    ]
  },
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SAVE_DASHBOARD_REQUEST_MODEL = {
  "companyId": 1,
  "dashboard": {
    "dashboardName": "string",
    "index": 0,
    "isDashboardSelected": true,
    "isTemplate": true,
    "widgets": [
      {
        "chartType": "string",
        "customFilter": [
          {
            "item": "string",
            "vendor": "string"
          }
        ],
        "dateType": "string",
        "filter": "string",
        "height": 0,
        "index": 0,
        "scale": "string",
        "threshold": 0,
        "timeFrame": "string",
        "viewBy": "string",
        "widgetType": "string",
        "width": 0,
        "x": 0,
        "y": 0,
        "useYn": "string"
      }
    ]
  },
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const DELETE_DASHBOARD_REQUEST_MODEL = {
  "companyId": 1,
  "dashboardIndex": null,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const FETCH_PORTION_REQUEST_MODEL = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "dateType": "monthly",
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "last_3_months",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "string"
  ],
  "viewBy": "account",
  "widgetType": "dashboard_product_portion_by_widget"
};

export const YEAR_COST_FCST_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ]
};

export const DASHBOARD_DATE_TYPE = {
  MONTHLY: 'monthly',
  WEEKLY: 'weekly',
};

export const DASHBOARD_WIDGET_TYPE = {
  DASHBOARD_COST_MONTH_TO_DATE_WIDGET: 'dashboard_cost_month_to_date_widget',
  DASHBOARD_ESTIMATED_COST_WIDGET: 'dashboard_estimated_cost_widget',
  COMPARE_COST_TREND_WIDGET: 'dashboard_compare_cost_trend_widget',
  DASHBOARD_COST_BY_WIDGET: 'dashboard_cost_by_widget',
  PRODUCT_PORTION_WIDGET: 'dashboard_product_portion_by_widget',
  DASHBOARD_TOP5_WIDGET: 'dashboard_top_5_widget',
  DASHBOARD_ABNORMAL_CHANGE_WIDGET: 'dashboard_abnormal_change_widget',
};

export const UNRESIZABLE_DASHBOARD_WIDGET_TYPES = [
  DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET /*툴팁 잘림 이슈 해결되면 RESIZING 다시 적용하기*/
];

/**
 * This config to support set min width and min height and set specific sizes when user resize widget on dashboard screen
 */
export const DASHBOARD_WIDGET_SIZES = [
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET,
    minWidth: 6,
    minHeight: 4,
    specificSizes: [{w: 6, h: 4}, {w: 8, h: 4}, {w: 12, h: 4}, {w: 18, h: 4}, {w: 24, h: 4}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET,
    minWidth: 6,
    minHeight: 4,
    specificSizes: [{w: 6, h: 4}, {w: 8, h: 4}, {w: 12, h: 4}, {w: 18, h: 4}, {w: 24, h: 4}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_CLOUD_BUDGET_WIDGET,
    minWidth: 4,
    minHeight: 4,
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_TOTAL_SAVING_WIDGET,
    minWidth: 4,
    minHeight: 4,
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_ALERTS_WIDGET,
    minWidth: 6,
    minHeight: 13,
    specificSizes: [{w: 6, h: 13}, {w: 12, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.INTEGRATED_PRODUCT_PORTION_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET,
    minWidth: 12,
    minHeight: 13,
    specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET,
    minWidth: 24,
    minHeight: 13
    /*,specificSizes: [{w: 12, h: 13}, {w: 18, h: 13}, {w: 24, h: 13}]*/ /*툴팁 잘림 이슈 해결되면 RESIZING 다시 적용하기*/
  },
  {
    widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_YEAR_COST_FCST_WIDGET,
    minWidth: 6,
    minHeight: 4,
    specificSizes: [{w: 6, h: 4}, {w: 8, h: 4}, {w: 12, h: 4}, {w: 18, h: 4}, {w: 24, h: 4}]
  }
];

export const DASHBOARD_DATE_TYPE_OPTIONS = [
  {
    text: 'dashboard.dateTypeOptions.monthly',
    value: DASHBOARD_DATE_TYPE.MONTHLY
  },
  {
    text: 'dashboard.dateTypeOptions.weekly',
    value: DASHBOARD_DATE_TYPE.WEEKLY
  },
];

export const TOP_5_COST = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "string",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "string"
  ],
  "viewBy": "account",
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET
};

export const DASHBOARD_COST_REQUEST_MODEL = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "last_12_months",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "String"
  ],
  "viewBy": "account",
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET
};

export const DASHBOARD_ABNORMAL_REQUEST_MODEL = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "string",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "string"
  ],
  "viewBy": "account",
  "sensitivity": "M",
  "minAlert": 100,
  "maxAlert":1000,
  "mailSendCond": {},
  "alarmCondition": "I",
  "mailReceivers" : {},
  "alarmChannel" : [],
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET
}

export const DASHBOARD_ML_ABNORMAL_USER_REQUEST_MODEL = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "string",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "string"
  ],
  "viewBy": "account",
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET
}

export const DASHBOARD_ML_ABNORMAL_AI_REQUEST_MODEL = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "sensitivity": "M",
  "minAlert": 100,
  "maxAlert":1000,
  "mailSendCond": {},
  "alarmCondition": "I",
  "mailReceivers" : {},
  "alarmChannel" : [],
  "timeFrame": "string",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "string"
  ],
  "viewBy": "account",
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET
}

export const DASHBOARD_TREND_REQUEST_MODEL = {
  "companyId": 1,
  "customFilter": [
    {
      "item": "string",
      "vendor": "string"
    }
  ],
  "dateType": "string",
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "last_3_months",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "string"
  ],
  "viewBy": "account",
  "widgetType": DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET
}

export const DASHBOARD_MULTI_VENDOR_COST_BY_REQUEST_MODEL = {
  "companyId": 1,
  "filter": "top_10_by_cost",
  "siteCode": "BESPIN",
  "threshold": 0,
  "timeFrame": "last_3_months", // 로컬 테스트용
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "String"
  ],
  "viewBy": "account",
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET
};

export const DASHBOARD_WIDGET_DASHBOARDS_REQUEST_MODEL = {
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const SET_SELECTED_DASHBOARD_REQUEST_MODEL = {
  "companyId": 1,
  "dashboardIndex": 0,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
};

export const FETCH_DASHBOARD_UUID_REQUEST_MODEL ={
  "companyId": 1,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string"
}

export const DASHBOARD_DROPDOWN_OPTIONS_VALUE = {
  EDIT_WIDGET: 'EDIT_WIDGET',
  EXPORT_AS_CSV: 'EXPORT_AS_CSV',
  DELETE_WIDGET: 'DELETE_WIDGET',
  DUPLICATE_WIDGET: 'DUPLICATE_WIDGET',
  DELETE_DASHBOARD: 'DELETE_DASHBOARD'
};

export const DASHBOARD_DROPDOWN_OPTIONS = [
  {
    icon: 'edit',
    text: 'dashboard.dropdown.option.editWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET
  },
  {
    icon: 'get_app',
    text: 'dashboard.dropdown.option.exportAsCSV',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.EXPORT_AS_CSV
  }
];

export const DASHBOARD_AI_ABNORMAL_WIDGET_DROPDOWN_OPTIONS = [
  {
    icon: 'edit',
    text: 'dashboard.dropdown.option.editWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET
  }
];

export const DASHBOARD_DROPDOWN_OPTION_EDIT = [
  {
    icon: 'edit',
    text: 'dashboard.dropdown.option.editWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET
  }
];

export const DASHBOARD_HEADER_DROPDOWN_OPTIONS = [
  {
    icon: 'delete',
    text: 'dashboard.dashboardHeader.delete',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_DASHBOARD
  }
];

export const DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS = [
  {
    icon: 'edit',
    text: 'dashboard.dropdown.option.editWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.EDIT_WIDGET
  },
  {
    icon: 'file_copy',
    text: 'dashboard.dropdown.option.duplicateWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.DUPLICATE_WIDGET
  },
  {
    icon: 'delete',
    text: 'dashboard.dropdown.option.deleteWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_WIDGET
  }
];

export const DASHBOARD_DROPDOWN_EDIT_MODE_OPTIONS_EXCEPT_EDIT = [
  {
    icon: 'file_copy',
    text: 'dashboard.dropdown.option.duplicateWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.DUPLICATE_WIDGET
  },
  {
    icon: 'delete',
    text: 'dashboard.dropdown.option.deleteWidget',
    value: DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_WIDGET
  }
];

export const DASHBOARD_VIEW_BY = {
  ACCOUNT: 'account',
  PRODUCT: 'product',
  REGION: 'region',
  TAG: 'tag',
  SERVICE_GROUP: 'serviceGroup',
  INSTANCE_TYPE:'instanceType',
  USAGE_TYPE:'usageType',
  PROJECT:'project'
};

export const INTEGRATED_CATEGORY_VIEW_BY = {
  PRODUCT: 'product',
  REGION: 'region',
};

export const SELECTED_VENDOR = {
  AWS: 'AWS',
  AZURE: 'AZURE',
  GCP: 'GCP',
  ALI: 'ALI',
  OCI: 'OCI',
  NCP: 'NCP',
  TENCENT: 'TENCENT'
};

export const DASHBOARD_VIEW_BY_OPTIONS = [
  {
    text: 'dashboard.viewByOption.account',
    value: DASHBOARD_VIEW_BY.ACCOUNT
  },
  {
    text: 'dashboard.viewByOption.product',
    value: DASHBOARD_VIEW_BY.PRODUCT
  },
  {
    text: 'dashboard.viewByOption.region',
    value: DASHBOARD_VIEW_BY.REGION
  },
];


export const DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR = {
  'GCP': [
    {
      text: 'dashboard.viewByOption.project',
      value: DASHBOARD_VIEW_BY.PROJECT
    },
    {
      text: 'dashboard.viewByOption.product',
      value: DASHBOARD_VIEW_BY.PRODUCT
    },
  ]
};

export const DASHBOARD_VIEW_BY_OPTIONS_NO_REGION = [
  {
    text: 'dashboard.viewByOption.account',
    value: DASHBOARD_VIEW_BY.ACCOUNT
  },
  {
    text: 'dashboard.viewByOption.product',
    value: DASHBOARD_VIEW_BY.PRODUCT
  }
];

export const PORTION_VIEW_BY_OPTIONS = [
  {
    text: 'dashboard.viewByOption.account',
    value: DASHBOARD_VIEW_BY.ACCOUNT
  },
  {
    text: 'dashboard.viewByOption.product',
    value: DASHBOARD_VIEW_BY.PRODUCT
  },
  {
    text: 'dashboard.viewByOption.region',
    value: DASHBOARD_VIEW_BY.REGION
  }
];

export const PORTION_VIEW_BY_OPTIONS_BY_VENDOR = {
  'AWS': [
    {
      text: 'dashboard.viewByOption.account',
      value: DASHBOARD_VIEW_BY.ACCOUNT
    },
    {
      text: 'dashboard.viewByOption.product',
      value: DASHBOARD_VIEW_BY.PRODUCT
    },
    {
      text: 'dashboard.viewByOption.region',
      value: DASHBOARD_VIEW_BY.REGION
    },
    {
      text: 'dashboard.viewByOption.tag',
      value: DASHBOARD_VIEW_BY.TAG
    }
  ],
  'GCP': [
    {
      text: 'dashboard.viewByOption.project',
      value: DASHBOARD_VIEW_BY.PROJECT
    },
    {
      text: 'dashboard.viewByOption.product',
      value: DASHBOARD_VIEW_BY.PRODUCT
    },
  ],
};

export const INTEGRATED_PORTION_VIEW_BY_OPTIONS = [
  {
    text: 'dashboard.viewByOption.category.product',
    value: INTEGRATED_CATEGORY_VIEW_BY.PRODUCT
  },
  {
    text: 'dashboard.viewByOption.category.region',
    value: INTEGRATED_CATEGORY_VIEW_BY.REGION
  }
];

export const PRODUCT_PORTION_VENDORS = [
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
  // },
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const INTEGRATED_PRODUCT_PORTION_VENDORS = [
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
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const PORTION_VIEW_BY_VENDORS = [
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
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const MULTI_VENDORS_PORTION_VENDORS = [
  {
    text: 'header.aws',
    value: SELECTED_VENDOR.AWS
  },
  {
    text: 'header.azure',
    value: SELECTED_VENDOR.AZURE
  },
  // {
  //   text: 'header.gcp',
  //   value: SELECTED_VENDOR.GCP
  // }
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  // {
  //   text: 'header.tencent',
  //   value: SELECTED_VENDOR.TENCENT
  // }
];

export const TOP_5_VIEW_BY_VENDORS = [
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
  // },
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  // {
  //   text: 'header.ncp',
  //   value: SELECTED_VENDOR.NCP
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const COMPARE_COST_TREND_VENDORS = [
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
  // },
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const TOP_5_TIME_FRAME = {
  MONTH_TO_DATE: 'month_to_date',
  LAST_7_DAYS: 'last_7_days',
  LAST_10_DAYS: 'last_10_days',
  LAST_14_DAYS: 'last_14_days',
  LAST_30_DAYS: 'last_30_days',
  LAST_60_DAYS: 'last_60_days',
};

export const COMPARE_COST_TREND_TIME_FRAME = {
  LAST_2_MONTHS: 'last_2_months',
  LAST_3_MONTHS: 'last_3_months',
  LAST_6_MONTHS: 'last_6_months',
};

export const COMPARE_COST_TREND_TIME_FRAME_OPTION = [
  {
    text: 'dashboard.compareCostTrend.timeFrameOptions.twoMonths',
    value: COMPARE_COST_TREND_TIME_FRAME.LAST_2_MONTHS
  },{
    text: 'dashboard.compareCostTrend.timeFrameOptions.threeMonths',
    value: COMPARE_COST_TREND_TIME_FRAME.LAST_3_MONTHS
  },{
    text: 'dashboard.compareCostTrend.timeFrameOptions.sixMonths',
    value: COMPARE_COST_TREND_TIME_FRAME.LAST_6_MONTHS
  },
];

export const COST_MONTH_TO_DATE_VIEW_BY_VENDORS = [
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
  // },
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const TOP_5_TIME_FRAME_OPTIONS = [
  {
    text: 'dashboard.topCost.timeFrameOptions.monthToDate',
    value: TOP_5_TIME_FRAME.MONTH_TO_DATE
  },
  {
    text: 'dashboard.topCost.timeFrameOptions.last7Days',
    value: TOP_5_TIME_FRAME.LAST_7_DAYS
  },
  {
    text: 'dashboard.topCost.timeFrameOptions.last10Days',
    value: TOP_5_TIME_FRAME.LAST_10_DAYS
  },
  {
    text: 'dashboard.topCost.timeFrameOptions.last14Days',
    value: TOP_5_TIME_FRAME.LAST_14_DAYS
  },
  {
    text: 'dashboard.topCost.timeFrameOptions.last30Days',
    value: TOP_5_TIME_FRAME.LAST_30_DAYS
  },
  {
    text: 'dashboard.topCost.timeFrameOptions.last60Days',
    value: TOP_5_TIME_FRAME.LAST_60_DAYS
  },
];

export const ABNORMAL_TIME_FRAME = {
  LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE: 'compare_last_3_days',
  LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE: 'compare_last_7_days',
  LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS: 'compare_latest_day_vs_average_of_7_days',
  THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH: 'compare_this_month_vs_last_month',

};

export const ABNORMAL_TIME_FRAME_OPTIONS = [
  {
    text: 'dashboard.abnormalChange.latest3DaysTotalAnd3DaysBeforeThat',
    value: ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE
  },
  {
    text: 'dashboard.abnormalChange.latest7DaysTotalAnd7DaysBeforeThat',
    value: ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE
  },
  {
    text: 'dashboard.abnormalChange.latestTotalCostAndAverageCostOfLatest7Days',
    value: ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS
  },
  {
    text: 'dashboard.abnormalChange.thisMonthSoFarAndSamePeriodOfLastMonth',
    value: ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH
  },
];

export const ABNORMAL_VIEW_BY_VENDORS = [
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
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  // {
  //   text: 'header.ncp',
  //   value: SELECTED_VENDOR.NCP
  // },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const AI_ABNORMAL_VIEW_BY_VENDORS = [
  {
    text: 'header.aws',
    value: SELECTED_VENDOR.AWS
  },
  // {
  //   text: 'header.gcp',
  //   value: SELECTED_VENDOR.GCP
  // },
  // {
  //   text: 'header.azure',
  //   value: SELECTED_VENDOR.AZURE
  // },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
];

export const AI_ABNORMAL_ALARM = {
  BY_INTEGRATION : 'I',
  BY_CLASS : 'C'
};

export const AI_ABNORMAL_ALARM_OPTIONS = [
  {text:'dashboard.abnormalChange.sendAllAlertLevel', value: 'I'},
  {text:'dashboard.abnormalChange.sendByAlertLevel', value: 'C'},
];

export const AI_ABNORMAL_ALARM_CHANNEL = {
  BY_EMAIL : 'EMAIL',
  BY_ALERTNOW : 'ALERTNOW'
};

export const AI_ABNORMAL_ALARM_CHANNEL_OPTIONS = [
  {text:'dashboard.abnormalChange.channels.email', value: 'EMAIL'},
  {text:'dashboard.abnormalChange.channels.alertNow', value: 'ALERTNOW', disabled: true},
];

export const ABNORMAL_THRESHOLD = {
  _5: 5,
  _10: 10,
  _15: 15,
};

export const AI_ABNORMAL_SENSITIVITY = {
  HIGH:'H',
  MIDDLE:'M',
  LOW:'L'
};

export const SENSITIVITY_OPTIONS = [
  {text:'dashboard.abnormalChange.sensitivityGroup.high', value: 'H'},
  {text:'dashboard.abnormalChange.sensitivityGroup.middle', value: 'M'},
  {text:'dashboard.abnormalChange.sensitivityGroup.low', value: 'L'},
];

export const THRESHOLD_OPTIONS = [
  {text: '≥5%', value: ABNORMAL_THRESHOLD._5},
  {text: '≥10%', value: ABNORMAL_THRESHOLD._10},
  {text: '≥15%', value: ABNORMAL_THRESHOLD._15},
]

export const THRESHOLD_DELTA_OPTIONS = [
  {text: '≥5%', value: ABNORMAL_THRESHOLD._5},
  {text: '≥10%', value: ABNORMAL_THRESHOLD._10},
  {text: '≥15%', value: ABNORMAL_THRESHOLD._15},
]

export const ABNORMAL_NOTIFICATION = {
  NOTIFICATION_OFF: false,
  NOTIFICATION_ON: true,
};

export const MONTHLY_COST_TIME_FRAME = {
  YEAR_TO_MONTH: 'year_to_month',
  LAST_3_MONTHS: 'last_3_months',
  LAST_6_MONTHS: 'last_6_months',
  LAST_12_MONTHS: 'last_12_months',
};

export const MONTHLY_COST_TIME_FRAME_OPTIONS = [
  {
    text: 'dashboard.monthlyCost.timeFrameOptions.yearToMonth',
    value: MONTHLY_COST_TIME_FRAME.YEAR_TO_MONTH
  },
  {
    text: 'dashboard.monthlyCost.timeFrameOptions.last3Months',
    value: MONTHLY_COST_TIME_FRAME.LAST_3_MONTHS
  },
  {
    text: 'dashboard.monthlyCost.timeFrameOptions.last6Months',
    value: MONTHLY_COST_TIME_FRAME.LAST_6_MONTHS
  },
  {
    text: 'dashboard.monthlyCost.timeFrameOptions.last12Months',
    value: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS
  },
];

export const WEEKLY_COST_TIME_FRAME = {
  YEAR_TO_WEEK: 'year_to_week',
  LAST_4_WEEKS: 'last_4_weeks',
  LAST_8_WEEKS: 'last_8_weeks',
  LAST_16_WEEKS: 'last_16_weeks',
  LAST_24_WEEKS: 'last_24_weeks',
};

export const WEEKLY_COST_TIME_FRAME_OPTIONS = [
  {
    text: 'dashboard.weeklyCost.timeFrameOptions.yearToWeek',
    value: WEEKLY_COST_TIME_FRAME.YEAR_TO_WEEK
  },
  {
    text: 'dashboard.weeklyCost.timeFrameOptions.last4Weeks',
    value: WEEKLY_COST_TIME_FRAME.LAST_4_WEEKS
  },
  {
    text: 'dashboard.weeklyCost.timeFrameOptions.last8Weeks',
    value: WEEKLY_COST_TIME_FRAME.LAST_8_WEEKS
  },
  {
    text: 'dashboard.weeklyCost.timeFrameOptions.last16Weeks',
    value: WEEKLY_COST_TIME_FRAME.LAST_16_WEEKS
  },
  {
    text: 'dashboard.weeklyCost.timeFrameOptions.last24Weeks',
    value: WEEKLY_COST_TIME_FRAME.LAST_24_WEEKS
  },
];

export const OPACITY_VALUES = {
  NO_CHANGE: 0.3,
  FADEOUT: 0.5,
  DEFAULT: 1
}

// export const AWS_COLORS = ["#ffeed4", "#FFE0B2", "#FFCC80", "#FFB74D", "#FFA726", "#FF9800", "#FB8C00", "#F57C00", '#EF6C00', '#E65100'];
export const AWS_COLORS = ["#FFE3AF", "#EAD988", "#FFE76E", "#FFD327", "#F19100", "#D7A772", "#FFAC15", "#CC7722", "#BE5504", "#F05E23"];

export const GCP_COLORS = ["#D9EABD", "#F3F791", "#DEE782", "#67C386", "#A0E1B4", "#6E9A61", "#C5E554", "#0E841D", "#8CB50B", "#00A841"];

export const AZURE_COLORS = ["#CAEBF3", "#B8CEE8", "#869DC5", "#96D1E4", "#6F96EA", "#A0C4FF", "#75A5DD", "#417EBC", "#0E4D92", "#0080FF"];

export const ALI_COLORS = ["#F6DEFF", "#DABEF1", "#B19AE7", "#C0BEFA", "#6040B5", "#A67FFF", "#AB94FF", "#8427C1", "#9140E6", "#6F3EDF"];

export const IDC_COLORS = ["#ACC1DB", "#6E93C3", "#6E93C3", "#4E7BB6", "#3E659B", "#365A8A", "#314E78", "#284467", "#213958", "#192D46"];

export const OCI_COLORS = ["#FFCAD8", "#E07292", "#AD0040", "#ffBADF", "#871F3E", "#AE0012", "#D64988", "#DB003F", "#FF7095", "#FF4A4E"];

export const NCP_COLORS = ["#CEE7E1", "#8FE8C7", "#00C8D0", "#C5FBDE", "#1E989F", "#9BDFD0", "#5DD1D9", "#5AF0EB", "#37818D", "#06BAC0"];

export const TENCENT_COLORS = ["#539D06", "#8CB50B", "#008752", "#B1BB1D", "#8F9613", "#C9DC2B", "#628822", "#9FD4B5", "#EFFFA6", "#D9EABD"];


export const DEFAULT_TOP5_COLORS = ["#d16f02", "#e87b02", "#0288d1", "#4dd689", "#5bcee3"];

export const COMPARE_COST_TREND_COLORS = ["#95aae6","#63cfc7", "#e47bcc", "#ddc79a", "#9eca78", "#65bee5", "#f0aa80", "#c6d763", '#ac88ee','#c3be7a'];

export const DEFAULT_OTHER_COLOR =  "#d5dae0";

// export const AWS_PORTION_COLORS = ["#ffeed4", "#ffe0b2", "#ffcc80", "#ffb74d", "#ffa726", "#ff9800", "#fb8c00", "#f57c00", "#ef6c00", "#e65100"];
export const AWS_PORTION_COLORS = ["#FFE3AF", "#EAD988", "#FFE76E", "#FFD327", "#F19100", "#D7A772", "#FFAC15", "#CC7722", "#BE5504", "#F05E23"];

export const AZURE_PORTION_COLORS = ["#d3f2fb", "#b9ebf9", "#96e0f6", "#73d6f3", "#50ccf0", "#26bbe6", "#17a7da", "#1393c1", "#057fa9", "#046c90"];

export const GCP_PORTION_COLORS = ["#e5f1cc", "#d5e9ae", "#c1dd86", "#acd25d", "#97c735", "#59c42c", "#4eb425", "#3fa321", "#35921c", "#2a8117"];

export const OCI_PORTION_COLORS = ["#FFCAD8", "#E07292", "#AD0040", "#ffBADF", "#871F3E", "#AE0012", "#D64988", "#DB003F", "#FF7095", "#FF4A4E"];

export const NCP_PORTION_COLORS = ["#CEE7E1", "#8FE8C7", "#00C8D0", "#C5FBDE", "#1E989F", "#9BDFD0", "#5DD1D9", "#5AF0EB", "#37818D", "#06BAC0"];

export const Tencent_PORTION_COLORS = ["#D9EABD", "#EFFFA6", "#9FD4B5", "#628822", "#C9DC2B", "#8F9613", "#B1BB1D", "#008752", "#8CB50B", "#539D06"]
// export const AWS_PORTION_BORDER_COLORS = ["#ffeed4", "#FFE0B2", "#FFCC80", "#FFB74D", "#FFA726", "#FF9800", "#FB8C00", "#F57C00", '#EF6C00', '#E65100'];
export const AWS_PORTION_BORDER_COLORS = ["#FFE3AF", "#EAD988", "#FFE76E", "#FFD327", "#F19100", "#D7A772", "#FFAC15", "#CC7722", "#BE5504", "#F05E23"];

export const GCP_PORTION_BORDER_COLORS = ["#e5f1cc", "#d5e9ae", "#c1dd86", "#acd25d", "#97c735", "#59c42c", "#4eb425", "#3fa321", '#35921c', '#2a8117'];

export const AZURE_PORTION_BORDER_COLORS= ["#d3f2fb", "#b9ebf9", "#96e0f6", "#73d6f3", "#50ccf0", "#26bbe6", "#17a7da", "#1393c1", '#057fa9', '#046c90'];

export const OCI_PORTION_BORDER_COLORS = ["#FFCAD8", "#E07292", "#AD0040", "#ffBADF", "#871F3E", "#AE0012", "#D64988", "#DB003F", "#FF7095", "#FF4A4E"];

export const NCP_PORTION_BORDER_COLORS = ["#CEE7E1", "#8FE8C7", "#00C8D0", "#C5FBDE", "#1E989F", "#9BDFD0", "#5DD1D9", "#5AF0EB", "#37818D", "#06BAC0"];

export const Tencent_PORTION_BORDER_COLORS = ["#D9EABD", "#EFFFA6", "#9FD4B5", "#628822", "#C9DC2B", "#8F9613", "#B1BB1D", "#008752", "#8CB50B", "#539D06"];

export const DEFAULT_OTHER_BORDER_COLOR =  "#D5DAE0";

export const VENDORS = {
  AWS: "AWS",
  GCP: "GCP",
  AZURE: "AZURE",
  ALI: "ALI",
  IDC: "IDC",
  OCI: "OCI",
  NCP: "NCP",
  TENCENT: 'TENCENT'
};

export const OTHER_LINE_PATTERNS = [
  {value: "AWS OTHERS", class: "AWS-others-line-pattern", color: "#FFCC80", borderColor: "#FFF3E0"},
  {value: "GCP OTHERS", class: "GCP-others-line-pattern", color: "#c1dd86", borderColor: "#e5f1cc"},
  {value: "AZURE OTHERS", class: "AZURE-others-line-pattern", color: "#96e0f6", borderColor: "#d3f2fb"},
  {value: "ALI OTHERS", class: "AZURE-others-line-pattern", color: "#B952EE", borderColor: "#D292F4"},
  {value: "IDC OTHERS", class: "AZURE-others-line-pattern", color: "#6E93C3", borderColor: "#ACC1DB"},
  {value: "OCI OTHERS", class: "OCI-others-line-pattern", color: "#E51873", borderColor: "#FAA6E3"},
  {value: "NCP OTHERS", class: "NCP-others-line-pattern", color: "#06A79A", borderColor: "#89DCDE"},
  {value: "TENCENT OTHERS", class: "TENCENT-others-line-pattern", color: "#17A290", borderColor: "#ADC447"},
];

export const CHECK_DUPLICATED_TEMPLATE_REGEX = "OVERVIEW DASHBOARD COPY\\((\\d*)\\)";

export const NUMBER_OF_COPY_WILDCARD = "{{numberOfCopy}}";

export const COPY_OF_DASHBOARD_TEMPLATE_NAME = "Dashboard Copy(" + NUMBER_OF_COPY_WILDCARD + ")";

export const PORTION_DEFAULT_SELECTED_ACCOUNT = "";

export const PORTION_DEFAULT_TIME_FRAME = "";

export const PORTION_DEFAULT_SELECTED_TAG = "";

export const VIEW_MODE = {
  EDIT: "EDIT",
  COPY: "COPY",
  DEFAULT: "DEFAULT",
}


export const CONST_TREND_TIME_FRAME = {
  TWO_MONTH: "2M",
  THREE_MONTH: "3M",
  SIX_MONTH: "6M",
}

export const ABNORMAL_FORMAT_DATE = `MM/DD`
export const EXPORT_FORMAT_DATE = `YYYY/MM/DD`
export const EXPORT_FORMAT_MONTH = `YYYY/MM`

export const DASHBOARD_WIDGET_TYPE_OPTIONS = [
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET,
    text: 'dashboard.addWidget.costMonthToDate'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET,
    text: 'dashboard.addWidget.estimatedCost'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET,
    text: 'dashboard.addWidget.compareTotalCostTrend'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
    text: 'dashboard.addWidget.costBy'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET,
    text: 'dashboard.addWidget.productPortionOfAccount'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET,
    text: 'dashboard.addWidget.portionBy'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET,
    text: 'dashboard.addWidget.abnormalChangeBy'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET,
    text: 'dashboard.addWidget.abnormalDetection'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET,
    text: 'dashboard.addWidget.top5CostBy'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.INTEGRATED_PRODUCT_PORTION_WIDGET,
    text: 'dashboard.addWidget.integratedProductPortion'
  },
  // {
  //   value: DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET,
  //   text: 'dashboard.addWidget.abnormalDetection'
  // },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET,
    text: 'dashboard.addWidget.multiVendorPortionByServiceGroup'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET,
    text: 'dashboard.addWidget.multiVendorCostBy'
  },
  {
    value: DASHBOARD_WIDGET_TYPE.DASHBOARD_YEAR_COST_FCST_WIDGET,
    text: 'dashboard.addWidget.yearCostFcst'
  }
];

export const SCALE = {
  VALUE: 'value',
  PERCENTAGE: 'percentage',
  COST: 'cost'
};

export const FILTER = {
  TOP_10_BY_COST: 'top_10_by_cost',
  TOP_5_BY_COST: 'top_5_by_cost',
  CUSTOM: 'custom',
};

export const SAVE_WIDGET_REQUEST_MODEL = {
  "companyId": 1,
  "dashboardIndex": 0,
  "siteCode": "BESPIN",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "widget": {
    "chartType": "string",
    "customFilter": [
      {
        "item": "string",
        "vendor": "string"
      }
    ],
    "dateType": "string",
    "filter": "string",
    "height": 0,
    "index": 0,
    "scale": "string",
    "threshold": 0,
    "timeFrame": "string",
    "viewBy": "string",
    "widgetType": "string",
    "width": 0,
    "x": 0,
    "y": 0,
    "useYn": "string"
  }
}

export const DASHBOARD_DEFAULT_WIDGET = [
  {
    "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET,
    "index": 0,
    "x": 0,
    "y": 0,
    "width": 12,
    "height": 4,
  },
  {
    "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET,
    "index": 1,
    "x": 0,
    "y": 0,
    "width": 12,
    "height": 4
  },
  {
    "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_YEAR_COST_FCST_WIDGET,
    "index": 14,
    "x": 0,
    "y": 0,
    "width": 8,
    "height": 4
  },
  {
    "widgetType": DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET,
    "index": 4,
    "x": 0,
    "y": 0,
    "width": 24,
    "height": 13,
    "timeFrame": COMPARE_COST_TREND_TIME_FRAME.LAST_2_MONTHS
  },
  {
    "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET,
    "index": 6,
    "x": 0,
    "y": 17,
    "width": 24,
    "height": 13,
    "viewBy": "account",
    "dateType": DASHBOARD_DATE_TYPE.MONTHLY,
    "timeFrame": MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
    "chartType": CHART_TYPE.STACK,
    "scale": SCALE.VALUE,
    "filter": FILTER.TOP_10_BY_COST
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET,
    "index": 7,
    "x": 0,
    "y": 30,
    "width": 12,
    "height": 13,
    "dateType": DASHBOARD_DATE_TYPE.MONTHLY,
    "threshold": 5,
    "timeFrame": PORTION_DEFAULT_TIME_FRAME,
    "viewBy": "account",
    "selectedAccount": "",
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET,
    "index": 8,
    "x": 12,
    "y": 30,
    "width": 12,
    "height": 13,
    "viewBy": "tag",
    "dateType": DASHBOARD_DATE_TYPE.MONTHLY,
    "threshold": 5,
    "timeFrame": PORTION_DEFAULT_TIME_FRAME,
    "selectedAccount": "",
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET,
    "index": 9,
    "x": 0,
    "y": 43,
    "width": 12,
    "height": 13,
    "viewBy": "account",
    "timeFrame": ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH,
    "threshold": 10,
    "minAlert": 100,
    "maxAlert": 1000,
    "mailSendCond": {},
    "alarmCondition": "I",
    "mailReceivers" : {},
    "alarmChannel" : []
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET,
    "index": 10,
    "x": 12,
    "y": 43,
    "width": 12,
    "height": 13,
    "timeFrame": TOP_5_TIME_FRAME.LAST_14_DAYS,
    "chartType": "pie",
    "viewBy": "account",
    "threshold": 5
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_USER_WIDGET,
    "index": 11,
    "x": 0,
    "y": 56,
    "width": 12,
    "height": 13,
    "viewBy": "account",
    "timeFrame": ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH,
    "threshold": 10
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET,
    "index": 12,
    "x": 12,
    "y": 56,
    "width": 12,
    "height": 13,
    "viewBy": "account",
    "timeFrame": ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE,
    "sensitivity": "M",
    "minAlert": 100,
    "maxAlert":1000,
    "mailSendCond": {},
    "alarmCondition": "I",
    "mailReceivers" : {},
    "alarmChannel" : [],
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.INTEGRATED_PRODUCT_PORTION_WIDGET,
    "index": 13,
    "x": 0,
    "y": 30,
    "width": 12,
    "height": 13,
    "dateType": DASHBOARD_DATE_TYPE.MONTHLY,
    "threshold": 5,
    "timeFrame": PORTION_DEFAULT_TIME_FRAME,
    "viewBy": "product",
    "selectedAccount": "",
  },
  {
    'widgetType': DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET,
    "index": 8,
    "x": 12,
    "y": 30,
    "width": 12,
    "height": 13,
    "viewBy": "serviceGroup",
    "dateType": DASHBOARD_DATE_TYPE.MONTHLY,
    "threshold": 5,
    "timeFrame": PORTION_DEFAULT_TIME_FRAME,
    "selectedAccount": "",
  },
  {
    "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET,
    "index": 6,
    "x": 0,
    "y": 17,
    "width": 24,
    "height": 13,
    "viewBy": "account",
    "dateType": DASHBOARD_DATE_TYPE.MONTHLY,
    "timeFrame": MONTHLY_COST_TIME_FRAME.LAST_3_MONTHS,
    "chartType": CHART_TYPE.STACK,
    "scale": SCALE.VALUE,
    "filter": FILTER.TOP_10_BY_COST
  }
];

export const DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_REQUEST_MODEL = {
  "companyId": 1,
  "dateType": "monthly",
  "siteCode": "BESPIN",
  "selectedTagKey": PORTION_DEFAULT_SELECTED_TAG,
  "selectedAccount": PORTION_DEFAULT_SELECTED_ACCOUNT,
  "timeFrame": PORTION_DEFAULT_TIME_FRAME,
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ],
  "viewBy": "serviceGroup",
  "widgetType": DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET
};

export const DASHBOARD_PORTION_REQUEST_MODEL = {
  "companyId": 1,
  "dateType": "monthly",
  "siteCode": "BESPIN",
  "selectedTagKey": PORTION_DEFAULT_SELECTED_TAG,
  "selectedAccount": PORTION_DEFAULT_SELECTED_ACCOUNT,
  "timeFrame": PORTION_DEFAULT_TIME_FRAME,
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ],
  "viewBy": "product",
  "widgetType": "dashboard_product_portion_by_widget"
};

export const DASHBOARD_INTEGRATED_PORTION_REQUEST_MODEL = {
  "companyId": 1,
  "dateType": "monthly",
  "siteCode": "BESPIN",
  "selectedTagKey": PORTION_DEFAULT_SELECTED_TAG,
  "selectedAccount": PORTION_DEFAULT_SELECTED_ACCOUNT,
  "timeFrame": PORTION_DEFAULT_TIME_FRAME,
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
  "vendors": [
    "AWS"
  ],
  "viewBy": "product",
  "widgetType": "dashboard_integrated_product_portion_by_widget"
};

export const DASHBOARD_TIME_FRAME_MODEL = {
  "companyId": 1,
  "dateType": "monthly",
  "siteCode": "BESPIN",
  "timeFrame": "",
  "userEmail": "string",
  "userId": "string",
  "userName": "string",
};

export const COST_BY_WIDGET_VENDORS = [
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
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const INTERGRATED_PRODUCT_PORTION_WIDGET_VENDORS = [
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
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const COST_BY_VENDOR_WIDGET_VENDORS = [
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
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const DEFAULT_COST_BY_WIDGET_CONFIG = {
  viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
  dateType: DASHBOARD_DATE_TYPE.MONTHLY,
  timeFrame: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
  chartType: CHART_TYPE.STACK,
  scale: SCALE.VALUE,
  filter: FILTER.TOP_10_BY_COST,
  customFilter: [],
  widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET
};

export const DEFAULT_MULTI_COST_BY_WIDGET_CONFIG = {
  viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
  dateType: DASHBOARD_DATE_TYPE.MONTHLY,
  timeFrame: MONTHLY_COST_TIME_FRAME.LAST_12_MONTHS,
  chartType: CHART_TYPE.STACK,
  scale: SCALE.VALUE,
  filter: FILTER.TOP_10_BY_COST,
  //customFilter: [],
  widgetType: DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET
};

export const DEFAULT_TOP_COST_WIDGET_CONFIG = {
  viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
  timeFrame: TOP_5_TIME_FRAME.LAST_14_DAYS,
};

export const DEFAULT_COMPARE_COST_TREND_WIDGET_CONFIG = {
  timeFrame: COMPARE_COST_TREND_TIME_FRAME.LAST_2_MONTHS,
};

export const DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG = {
  threshold: ABNORMAL_THRESHOLD._10,
  timeFrame: ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH,
  viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
  isAbnormalNotiOn: ABNORMAL_NOTIFICATION.NOTIFICATION_OFF,
  // title: '',
  sensitivity: AI_ABNORMAL_SENSITIVITY.MIDDLE,
  minAlert: 100,
  maxAlert: 1000,
  mailSendCond: {},
  mailReceivers: {},
  alarmCondition: AI_ABNORMAL_ALARM.BY_INTEGRATION,
  alarmChannel: [AI_ABNORMAL_ALARM_CHANNEL.BY_EMAIL],
  hasAbnormalDetect: false
};

export const DEFAULT_AI_ABNORMAL_WIDGET_CONFIG = {
  threshold: ABNORMAL_THRESHOLD._10,
  timeFrame: ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH,
  viewBy: DASHBOARD_VIEW_BY.ACCOUNT,
  title:'',
  sensitivity: AI_ABNORMAL_SENSITIVITY.MIDDLE,
  minAlert:100,
  maxAlert:1000,
  isAbnormalNotiOn: ABNORMAL_NOTIFICATION.NOTIFICATION_OFF,
  mailSendCond:{},
  mailReceivers:{},
  alarmCondition:AI_ABNORMAL_ALARM.BY_INTEGRATION,
  alarmChannel:[AI_ABNORMAL_ALARM_CHANNEL.BY_EMAIL],
  hasAbnormalDetect:false,
  isIncludeLowerLevel:false
};

export const DEFAULT_PRODUCT_PORTION_BY_WIDGET_CONFIG = {
  selectedAccount: null,
  dateType: DASHBOARD_DATE_TYPE.MONTHLY,
  scale: SCALE.COST,
  timeFrame: null
};

export const DEFAULT_INTEGRATED_PRODUCT_PORTION_BY_WIDGET_CONFIG = {
  selectedAccount: null,
  selectedVendors: null,
  selectedVendorsByWidget: [],
  dateType: DASHBOARD_DATE_TYPE.MONTHLY,
  scale: SCALE.COST,
  timeFrame: null,
  viewBy:INTEGRATED_CATEGORY_VIEW_BY.PRODUCT,
};

export const DEFAULT_PORTION_BY_WIDGET_CONFIG = {
  viewBy: DASHBOARD_VIEW_BY.TAG,
  selectedVendors:null,
  selectedTagKey: null,
  dateType: DASHBOARD_DATE_TYPE.MONTHLY,
  timeFrame: null
};

export const DEFAULT_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET_CONFIG = {
  viewBy: DASHBOARD_VIEW_BY.SERVICE_GROUP,
  selectedVendorsByWidget: null,
  selectedServiceGroupSet: null,
  dateType: DASHBOARD_DATE_TYPE.MONTHLY,
  timeFrame: null
};

export const DEFAULT_ESTIMATED_COST_WIDGET_CONFIG = {
  selectedVendor: 'AWS'
}

export const ESTIMATED_COST_VIEW_BY_VENDORS = [
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
  // },
  {
    text: 'header.oci',
    value: SELECTED_VENDOR.OCI
  },
  {
    text: 'header.ncp',
    value: SELECTED_VENDOR.NCP
  },
  {
    text: 'header.tencent',
    value: SELECTED_VENDOR.TENCENT
  }
];

export const DEFAULT_YEAR_COST_FCST_WIDGET_CONFIG = {
  selectedVendor: 'AWS'
}

export const YEAR_COST_FCST_VIEW_BY_VENDORS = [
  {
    text: 'header.aws',
    value: SELECTED_VENDOR.AWS
  },
  // {
  //   text: 'header.gcp',
  //   value: SELECTED_VENDOR.GCP
  // },
  // {
  //   text: 'header.azure',
  //   value: SELECTED_VENDOR.AZURE
  // },
];

export const DEFAULT_WIDGET_DATA = {
  COST_MONTH_TO_DATE: {},
  ESTIMATED_COST: {
    selectedVendor: ''
  },
  BUDGET_DATA: {},
  TOTAL_SAVING: {},
  COMPARE_COST_TREND: {
    trendCost: []
  },
  DASHBOARD_COST: {
    costByCondition: [],
    customFilters: [],
    topOthersItems : [],
    timeFrameScope : [],
    payload: {}
  },
  PRODUCT_PORTION: {
    portion: [],
    accounts: [],
    timeFrameList: []
  },
  PORTION_BY: {
    portion: [],
    tagKeys: [],
    timeFrameList: []
  },
  ABNORMAL_CHANGE: [],
  TOP_COST: [],
  INTEGRATED_PRODUCT_PORTION: {
    portion: [],
    accounts: [],
    selectedVendorsByWidget: [],
    timeFrameList: []
  },
  MULTI_VENDOR_PORTION_BY_SERVICE_GROUP: {
    portion: [],
    serviceGroupSetList: [],
    timeFrameList: []
  },
};

// null-safe purpose only
export const DEFAULT_DASHBOARD = {
  dashboardName: 'Dashboard',
  widgets: []
};

export const WIDGET_FIELDS_NOT_IN_EDIT_FORM = [
  'x',
  'y',
  'w',
  'width',
  'h',
  'height',
  'minW',
  'minH',
  'i',
  'index',
  'isEditFormVisible',
  'widgetType',
  'moved', // ask Quang if this field is unused
  'firstTimeRenderTimeout',
];

export const DEFAULT_DASHBOARD_INDEX = 0;

export const DASHBOARD_COST_MAX_CUSTOM_FILTER_SIZE = 10;

export const WIDGET_MAX_SIZE = 24;

export const OTHER_NAME = "others";

export const COST_ANALYTIC_DATE_FORMAT = 'YYYY-MM-DD';

export const DASHBOARD_NEW_UPDATE = {
  EDIT_WIDGET: 'edit-widget',
  DASHBOARD_TEMPLATE_SELECTION: 'dashboard-template-selection',
  CREATE_DASHBOARD: 'create-dashboard',
  ADD_WIDGET_GUIDE: 'add-widget-guide',
  CUSTOMIZE_WIDGET: 'customize-widget',
};

export const DASHBOARD_NEW_UPDATE_OPTIONS = [
  {
    text: 'onboarding.welcome.newUpdates.dashboard.editWidget',
    value: DASHBOARD_NEW_UPDATE.EDIT_WIDGET
  },
  {
    text: 'onboarding.welcome.newUpdates.dashboard.dashboardTemplateSelection',
    value: DASHBOARD_NEW_UPDATE.DASHBOARD_TEMPLATE_SELECTION
  },
  {
    text: 'onboarding.welcome.newUpdates.dashboard.createDashboard',
    value: DASHBOARD_NEW_UPDATE.CREATE_DASHBOARD
  },
  {
    text: 'onboarding.welcome.newUpdates.dashboard.addWidgetGuide',
    value: DASHBOARD_NEW_UPDATE.ADD_WIDGET_GUIDE
  },
  {
    text: 'onboarding.welcome.newUpdates.dashboard.customizeWidget',
    value: DASHBOARD_NEW_UPDATE.CUSTOMIZE_WIDGET
  },
];

export const STATIC_WIDGETS = [
  DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET,
  DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET,
  DASHBOARD_WIDGET_TYPE.DASHBOARD_CLOUD_BUDGET_WIDGET,
  DASHBOARD_WIDGET_TYPE.DASHBOARD_TOTAL_SAVING_WIDGET,
  DASHBOARD_WIDGET_TYPE.DASHBOARD_ALERTS_WIDGET,
];

export const ABNORMAL_GRID_COLUMNS = {
  ALARM_LEVEL: 'alarmLevel',
  LINKED_ACCOUNT_ALIAS: 'linkedAccountAlias',
  CURRENT_COST: 'currentCost',
  LAST_COST: 'lastCost',
  INCREASE_DECREASE_RATE: 'increaseDecreaseRate'
};

export const ABNORMAL_GRID_COLUMNS_LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE = [
  {
    text: 'dashboard.abnormalChange.alarmLevel',
    value: ABNORMAL_GRID_COLUMNS.ALARM_LEVEL
  },
  {
    text: 'dashboard.abnormalChange.service',
    value: ABNORMAL_GRID_COLUMNS.LINKED_ACCOUNT_ALIAS
  },
  {
    text: 'dashboard.abnormalChange.latest3DayTotal',
    value: ABNORMAL_GRID_COLUMNS.CURRENT_COST
  },
  {
    text: 'dashboard.abnormalChange.threeDaysBeforeThat',
    value: ABNORMAL_GRID_COLUMNS.LAST_COST
  },
  {
    text: 'dashboard.abnormalChange.change',
    value: ABNORMAL_GRID_COLUMNS.INCREASE_DECREASE_RATE
  },
];

export const ABNORMAL_GRID_COLUMNS_LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE = [
  {
    text: 'dashboard.abnormalChange.alarmLevel',
    value: ABNORMAL_GRID_COLUMNS.ALARM_LEVEL
  },
  {
    text: 'dashboard.abnormalChange.service',
    value: ABNORMAL_GRID_COLUMNS.LINKED_ACCOUNT_ALIAS
  },
  {
    text: 'dashboard.abnormalChange.latest7DaysTotal',
    value: ABNORMAL_GRID_COLUMNS.CURRENT_COST
  },
  {
    text: 'dashboard.abnormalChange.sevenDaysBeforeThat',
    value: ABNORMAL_GRID_COLUMNS.LAST_COST
  },
  {
    text: 'dashboard.abnormalChange.change',
    value: ABNORMAL_GRID_COLUMNS.INCREASE_DECREASE_RATE
  },
];

export const ABNORMAL_GRID_COLUMNS_LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS = [
  {
    text: 'dashboard.abnormalChange.alarmLevel',
    value: ABNORMAL_GRID_COLUMNS.ALARM_LEVEL
  },
  {
    text: 'dashboard.abnormalChange.service',
    value: ABNORMAL_GRID_COLUMNS.LINKED_ACCOUNT_ALIAS
  },
  {
    text: 'dashboard.abnormalChange.latestTotalCost',
    value: ABNORMAL_GRID_COLUMNS.CURRENT_COST
  },
  {
    text: 'dashboard.abnormalChange.averageCostOfLatest7Days',
    value: ABNORMAL_GRID_COLUMNS.LAST_COST
  },
  {
    text: 'dashboard.abnormalChange.change',
    value: ABNORMAL_GRID_COLUMNS.INCREASE_DECREASE_RATE
  },
];

export const ABNORMAL_GRID_COLUMNS_THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH = [
  {
    text: 'dashboard.abnormalChange.alarmLevel',
    value: ABNORMAL_GRID_COLUMNS.ALARM_LEVEL
  },
  {
    text: 'dashboard.abnormalChange.service',
    value: ABNORMAL_GRID_COLUMNS.LINKED_ACCOUNT_ALIAS
  },
  {
    text: 'dashboard.abnormalChange.thisMonthSoFar',
    value: ABNORMAL_GRID_COLUMNS.CURRENT_COST
  },
  {
    text: 'dashboard.abnormalChange.samePeriodOfLastMonth',
    value: ABNORMAL_GRID_COLUMNS.LAST_COST
  },
  {
    text: 'dashboard.abnormalChange.change',
    value: ABNORMAL_GRID_COLUMNS.INCREASE_DECREASE_RATE
  },
];

export const GRID_SORT_TYPE = {
  SORT_TYPE_ASC: 'asc',
  SORT_TYPE_DESC: 'desc'
};

export const GRID_SORT_TYPE_OPTIONS = [
  {
    text: 'dashboard.abnormalChange.asc',
    value: GRID_SORT_TYPE.SORT_TYPE_ASC
  },
  {
    text: 'dashboard.abnormalChange.desc',
    value: GRID_SORT_TYPE.SORT_TYPE_DESC
  },
];

export const ABNORMAL_ALARM_LEVEL =  {
  Critical : 'Critical',
  Major : 'Major',
  Minor : 'Minor'
};

export const ABNORMAL_GRID_SORT_ALARM_LEVEL =  {
  Critical : 2,
  Major : 1,
  Minor : 0
};


export const YEAR_MONTH_FORMAT = {
  EN : 'MM/YYYY',
  DEFAULT : 'YYYY/MM'
};
