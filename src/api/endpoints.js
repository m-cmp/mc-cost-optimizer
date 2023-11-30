import { API_VERSION } from '@/constants/constants';
import { API_VERSION_PORTAL } from '@/constants/constants';
import {METERING_API_VERSION} from "@/constants/billingConstants";

// The big object with all the endpoints URLS
export default {
  BILLING: {
    AWS: {
      BILL_LIST: API_VERSION + '/billing/aws/bills',
      CHARGE_LIST: API_VERSION + '/billing/aws/charge',
      INVOICE_LIST: API_VERSION + '/billing/aws/invoices',
      INVOICE_INSIGHT_LIST: API_VERSION + '/billing/aws/invoice_insight',
      INVOICE_INSIGHT_GRID: API_VERSION + '/billing/aws/invoice_insight_grid',
      BILLING_DETAIL: API_VERSION + '/billing/aws/details',
      COMPUTING_RESOURCES: API_VERSION + '/billing/aws/compute/resources',
      TAG_KEYS: API_VERSION + '/common/tagkeys',
      SERVICE_GROUPS: API_VERSION + '/billing/aws/service_groups',
      AUTOSPOT: API_VERSION + '/billing/aws/autospot'
    },
    AZURE : {
      BILL_LIST: API_VERSION + '/billing/azr/bills',
      CHARGE_LIST: API_VERSION + '/billing/azr/charge',
      INVOICE_LIST: API_VERSION + '/billing/azr/invoices',
      BILLING_DETAIL: API_VERSION + '/billing/azr/details',
    },
    GCP : {
      BILL_LIST: API_VERSION + '/billing/gcp/bills',
      CHARGE_LIST: API_VERSION + '/billing/gcp/charge',
      BILLING_DETAIL: API_VERSION + '/billing/gcp/details',
    },
    OCI : {
      BILL_LIST: API_VERSION + '/billing/oci/bills',
      CHARGE_LIST: API_VERSION + '/billing/oci/charge',
      INVOICE_LIST: API_VERSION + '/billing/oci/invoices',
      INVOICE_INSIGHT_LIST: API_VERSION + '/billing/oci/invoice_insight',
      INVOICE_INSIGHT_GRID: API_VERSION + '/billing/oci/invoice_insight_grid',
      BILLING_DETAIL: API_VERSION + '/billing/oci/details',
    },
    NCP: {
      BILL_LIST: API_VERSION + '/billing/ncp/bills',
      CHARGE_LIST: API_VERSION + '/billing/ncp/charge',
      INVOICE_LIST: API_VERSION + '/billing/ncp/invoices',
      BILLING_DETAIL: API_VERSION + '/billing/ncp/details',
      TAG_KEYS: API_VERSION + '/billing/ncp/tagkeys'
    },
    TENCENT: {
      BILL_LIST: API_VERSION + '/billing/tencent/bills',
      CHARGE_LIST: API_VERSION + '/billing/tencent/charge',
      INVOICE_LIST: API_VERSION + '/billing/tencent/invoices',
      BILLING_DETAIL: API_VERSION + '/billing/tencent/details',
    },
    OPENSTACK: {
      BILL_LIST: API_VERSION + '/billing/openstack/bills',
      CHARGE_LIST: API_VERSION + '/billing/openstack/charge',
      BILLING_DETAIL: API_VERSION + '/billing/openstack/details'
    },
    SEND_INVOICE: {
      RECIPIENT_LIST: METERING_API_VERSION + '/user/common/subs/recipientList',
      SEND_EMAIL: METERING_API_VERSION + '/user/billing/invoice/main/send/email',
      CHECK_RESERVATION_SENT: METERING_API_VERSION + '/user/billing/bill/main/mail/report',
      RESERVATION_DELETE: METERING_API_VERSION + '/subs/subscribe/remove'
    }
  },
  COST_ANALYTICS: {
    COST_DATA: API_VERSION + '/ca/cost',
    DETAIL_COST_DATA: API_VERSION + '/ca/cost/detail',
    PORTION_DETAIL_COST_DATA: API_VERSION + '/ca/cost/detail/portion',
    EXPORT_EMAIL_DATA: API_VERSION + '/ca/cost/email/export',
    FILTER: {
      ALL_OPTIONS: API_VERSION + '/filter',
      SUB_FILTER: API_VERSION + '/subFilter',
      RELATED: API_VERSION + '/filter/related',
      USER_FILTER: API_VERSION + '/filter/user_filter',
      SAVE: API_VERSION + '/filter/save',
      SAVE_AS: API_VERSION + '/filter/saveas',
      DELETE: API_VERSION + '/filter/delete',
      SEARCH: API_VERSION + '/filter/search',
    }
  },
  DASHBOARD: {
    COST_MONTH_TO_DATE: API_VERSION + '/dashboard/cost_month_to_date',
    ESTIMATED_COST: API_VERSION + '/dashboard/estimated_cost',
    YEAR_COST_FCST: API_VERSION + '/dashboard/year_cost_fcst',
    BUDGET_DATA: API_VERSION + '/dashboard/budget_data',
    TOTAL_SAVING: API_VERSION + '/dashboard/total_saving',
    TOP_5_COST: API_VERSION + '/dashboard/top5',
    DASHBOARD_COST: API_VERSION + '/dashboard/cost',
    DASHBOARD_COST_BY_VENDOR: API_VERSION + '/dashboard/costByVendor',
    ABNORMAL: API_VERSION + '/dashboard/abnormal',
    ML_ABNORMAL_USER: API_VERSION + '/dashboard/ml_abnormal/user',
    ML_ABNORMAL_AI: API_VERSION + '/dashboard/ml_abnormal/ai',
    COMPARE_COST_TREND: API_VERSION + '/dashboard/trend',
    WIDGET: {
      DASHBOARDS: API_VERSION + '/dashboard/widget/dashboards',
      SAVE_WIDGET: API_VERSION + '/dashboard/widget/save_widget',
      SAVE_AS_DASHBOARD: API_VERSION + '/dashboard/widget/save_as_dashboard',
      SAVE_DASHBOARD: API_VERSION + '/dashboard/widget/save_dashboard',
      DELETE_DASHBOARD: API_VERSION + '/dashboard/widget/delete_dashboard',
      SET_SELECTED_DASHBOARD: API_VERSION + '/dashboard/widget/select_dashboard',
      SET_SHARED_DASHBOARD: API_VERSION + '/dashboard/widget/set_dashboard_by_uuid'
    },
    PRODUCT_PORTION: API_VERSION + '/dashboard/product_portion',
    INTEGRATED_PRODUCT_PORTION: API_VERSION + '/dashboard/multi/product_portion',
    PORTION_BY: API_VERSION + '/dashboard/portion_by',
    UUID: API_VERSION + '/dashboard/widget/get_dashboard_uuid',
    SERVICE_REQUEST_BY_EMAIL: API_VERSION + '/general/serviceRequest',
    MULTI_VENDOR_PORTION_BY_SERVICE_GROUP: API_VERSION + '/dashboard/multi/portion_by_service_group',
    TIME_FRAME: API_VERSION + '/dashboard/multi/time_frame'
  },
  ANOMALY_DETAIL: {
    AI_MODEL_ANALYSIS: API_VERSION + '/anomaly/aiModelAnalysis',
    AI_POSSIBLE_CAUSE: API_VERSION + '/anomaly/aiPossibleCause',
    ABNORMAL_DETECT_TIME: API_VERSION + '/anomaly/detectedTime'
  },
  ANOMALY_LIST: {
    TOTAL_ALERTS: API_VERSION + '/anomaly/totalAlerts',
    DETECTED_LIST: API_VERSION + '/anomaly/detectedList',
    AI_ANOMALY_CHANGE_TOP5: API_VERSION + '/anomaly/aiChangeTop5',
    USER_ANOMALY_ALERT_TOP5: API_VERSION + '/anomaly/userAlertTop5',
    ML_ABNORMAL_AI: API_VERSION + '/anomaly/ml_abnormal/ai',
  },
  SAVINGS_PLANS: {
    INVENTORY: {
      INVENTORY_LIST: API_VERSION + '/optimization/savingsplans/inventory/list',
      FILTER_DATA: API_VERSION + '/optimization/savingsplans/inventory/filter'
    },
    COVERAGE: {
      USAGE_LIST: API_VERSION + '/optimization/savingsplans/coverage/list',
      FILTER_DATA: API_VERSION + '/optimization/savingsplans/coverage/filter',
      TOTAL_SUMMARY: API_VERSION + '/optimization/savingsplans/coverage/total_summary',
      TRANSITION: API_VERSION + '/optimization/savingsplans/coverage/transition',
    },
    UTILIZATION: {
      USAGE_LIST: API_VERSION + '/optimization/savingsplans/utilization/list',
      FILTER_DATA: API_VERSION + '/optimization/savingsplans/utilization/filter',
      TOTAL_SUMMARY: API_VERSION + '/optimization/savingsplans/utilization/total_summary',
      TRANSITION: API_VERSION + '/optimization/savingsplans/utilization/transition',
      TRANSITION_TYPES: API_VERSION + '/optimization/savingsplans/utilization/transition_types',
      TRANSITION_REGIONS: API_VERSION + '/optimization/savingsplans/utilization/transition_regions',
      TRANSITION_FAMILISE: API_VERSION + '/optimization/savingsplans/utilization/transition_familise',
    },
    RECOMMENDATION: {
      RECOMMENDATION_RESULTS : API_VERSION + '/Recommendation/SavingsPlans',
      ACCOUNT : API_VERSION + '/Recommendation/Account',
      COVERAGE : API_VERSION + '/optimization/integration/coverage/list',
      SIMULATION : API_VERSION + '/Recommendation/SpSimulation'
    },

  },

  MIS_OPERATION_STATUS: {
    CSP_OPERATION_STATUS: {
      CSP_LIST: API_VERSION + '/mis/CspOperationStatus/list',
      VIEW_LIST: API_VERSION + '/mis/CspOperationStatus/view/list',
      REPAIR_CSP_LIST: API_VERSION + '/mis/CspOperationStatus/list/repair',
      DELETE_CSP_LIST: API_VERSION + '/mis/CspOperationStatus/list/delete',
      UPDATE_CSP_LIST: API_VERSION + '/mis/CspOperationStatus/list/update',
      DISTRIBUTION_LIST: API_VERSION + '/mis/CspOperationStatus/distribution/list',
      INSERT_DISTRIBUTION_LIST: API_VERSION + '/mis/CspOperationStatus/distribution/list/insert',
    },
    CONVERT_CLOUD_ROI: {
      ROI_LIST: API_VERSION + '/mis/CloudCnvRoiCal/list',
      UPDATE_ROI_LIST: API_VERSION + '/mis/CloudCnvRoiCal/list/update',
      DELETE_ROI_LIST: API_VERSION + '/mis/CloudCnvRoiCal/list/delete',
    }
  },

  RESERVED: {
    INVENTORY: {
      INVENTORY_LIST: API_VERSION + '/optimization/reserved/inventory/list',
      FILTER_DATA: API_VERSION + '/optimization/reserved/inventory/filter'
    },
    COVERAGE: {
      USAGE_LIST: API_VERSION + '/optimization/reserved/coverage/list',
      FILTER_DATA: API_VERSION + '/optimization/reserved/coverage/filter',
      TOTAL_SUMMARY: API_VERSION + '/optimization/reserved/coverage/total_summary',
      TRANSITION: API_VERSION + '/optimization/reserved/coverage/transition',
    },
    UTILIZATION: {
      AGREEMENT_UTILIZATION_LIST: API_VERSION + '/optimization/reserved/utilization/agreementList',
      AGREEMENT_UTILIZATION_DETAIL: API_VERSION + '/optimization/reserved/utilization/agreementDetail',
      INSTANCE_FAMILY_UTILIZATION_LIST: API_VERSION + '/optimization/reserved/utilization/instanceFamilyList',
      SERVICE_UTILIZATION_LIST: API_VERSION + '/optimization/reserved/utilization/serviceList',
      FILTER_DATA: API_VERSION + '/optimization/reserved/utilization/filter',
      TOTAL_SUMMARY: API_VERSION + '/optimization/reserved/utilization/total_summary',
      TRANSITION: API_VERSION + '/optimization/reserved/utilization/transition',
      TRANSITION_TYPES: API_VERSION + '/optimization/reserved/utilization/transition_types',
      TRANSITION_REGIONS: API_VERSION + '/optimization/reserved/utilization/transition_regions',
      TRANSITION_FAMILISE: API_VERSION + '/optimization/reserved/utilization/transition_familise',
    },
    RDS_RECOMMENDATION: {
      RECOMMENDATION_RESULTS : API_VERSION + '/RdsRecommendation/RdsRi',
      ACCOUNT : API_VERSION + '/RdsRecommendation/RdsRiAccount',
      COVERAGE : API_VERSION + '/optimization/integration/coverage/list',
      SIMULATION : API_VERSION + '/RdsRecommendation/RdsRiSimulation'
    },
    EC2_RECOMMENDATION: {
      RECOMMENDATION_RESULTS : API_VERSION + '/Ec2Recommendation/Ec2Ri',
      ACCOUNT : API_VERSION + '/Ec2Recommendation/Ec2RiAccount',
      COVERAGE : API_VERSION + '/optimization/integration/coverage/list',
      SIMULATION : API_VERSION + '/Ec2Recommendation/Ec2RiSimulation'
    },
    OPNSRCH_RECOMMENDATION: {
      RECOMMENDATION_RESULTS : API_VERSION + '/OpnsrchRecommendation/OpnsrchRi',
      ACCOUNT : API_VERSION + '/OpnsrchRecommendation/OpnsrchRiAccount',
      COVERAGE : API_VERSION + '/optimization/integration/coverage/list',
      SIMULATION : API_VERSION + '/OpnsrchRecommendation/OpnsrchRiRiSimulation'
    },
  },

  AZURE_RESERVED: {
    AZURE_INVENTORY: {
      INVENTORY_LIST: API_VERSION + '/optimization/azure/reserved/inventory/list',
      FILTER_DATA: API_VERSION + '/optimization/azure/reserved/inventory/filter'
    },
    AZURE_UTILIZATION: {
      AGREEMENT_UTILIZATION_LIST: API_VERSION + '/optimization/azure/reserved/utilization/agreementList',
      AGREEMENT_UTILIZATION_DETAIL: API_VERSION + '/optimization/azure/reserved/utilization/agreementDetail',
      INSTANCE_FAMILY_UTILIZATION_LIST: API_VERSION + '/optimization/azure/reserved/utilization/instanceFamilyList',
      SERVICE_UTILIZATION_LIST: API_VERSION + '/optimization/azure/reserved/utilization/serviceList',
      FILTER_DATA: API_VERSION + '/optimization/azure/reserved/utilization/filter',
      TOTAL_SUMMARY: API_VERSION + '/optimization/azure/reserved/utilization/total_summary',
      TRANSITION: API_VERSION + '/optimization/azure/reserved/utilization/transition',
      TRANSITION_TYPES: API_VERSION + '/optimization/azure/reserved/utilization/transition_types',
      TRANSITION_REGIONS: API_VERSION + '/optimization/azure/reserved/utilization/transition_regions',
      TRANSITION_FAMILISE: API_VERSION + '/optimization/azure/reserved/utilization/transition_familise',
    },
    AZURE_RI: {
      RI_LIST: API_VERSION + '/optimization/azure/reserved/ri/list',
      FILTER_DATA: API_VERSION + '/optimization/azure/reserved/ri/filter'
    }
  },

  COMMON: {
    SERVICE_GROUP_SETS: API_VERSION + '/common/serviceGroups',
    INFO: API_VERSION + '/common/info',
    SAVE_CURRENCY: API_VERSION + '/common/save_currency',
    SAVE_VENDORS: API_VERSION + '/common/save_vendors',
    VENDORS: API_VERSION + '/common/vendors',
    EXIST: API_VERSION + '/common/exist',
    COMPANY_MAX_DATE: API_VERSION + '/common/companyMaxDatetime',
  },
  SURVEY: {
    SUBMIT_SURVEY: API_VERSION + '/survey/save',
    CHECK: API_VERSION + '/survey/check',
  },
  USER: {
    CURRENT_LOGGED_IN: API_VERSION + '/users/current-logged-in',
    COMPANY_LIST : API_VERSION + '/login/first',
    UPDATE_CUR_COMPANY : API_VERSION + '/login/userinfo/update'
  },
  GNB:{
    COMPANY_LIST: API_VERSION + '/authgroup/companyList',
    PORTAL_EACH_WL: API_VERSION + '/user/portalurl',
    MENU_PERMISSION: API_VERSION + '/authgroup/menuList',
    NOTICE_LIST: API_VERSION + '/support/noticelist',
    NOTICE_SCROLL: API_VERSION + '/external/scrolltextdata',
    HOMEPAGE_INFO: API_VERSION + '/internal/homepage/info',
    RECENT_MENU: API_VERSION + '/users/recent/menu'
  },
  GENERAL: {
    FRONT_PROFILE: API_VERSION + '/general/frontProfile'
  },
  CLOUD_PORTAL: {
    VENDOR: API_VERSION_PORTAL + '/cloud/vendor'
  }
}
