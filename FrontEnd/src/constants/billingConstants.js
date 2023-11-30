import { SELECTED_VENDOR } from "./dashboardConstants";
import {VENDOR} from "@/constants/constants";

export const VIEW_BY_OPTION_VALUE = {
  ACCOUNT: "ACCOUNT",
  INVOICE: "INVOICE",
  REGION: "REGION",
  TAG: "TAG",
  SERVICEGROUP: "SERVICE_GROUP"
};

export const APPLY_TYPE = {
  RATIO:'R',
  STATIC:'S',
  INTERVAL:'I',
};

export const TREE_LAYER_SEPARATOR = "#!##!#!##!";

export const VIEW_BY_OPTIONS = [
  {
    text: 'billing.billingSummary.account',
    value: VIEW_BY_OPTION_VALUE.ACCOUNT
  },
  {
    text: 'billing.billingSummary.invoices',
    value: VIEW_BY_OPTION_VALUE.INVOICE
  },
  {
    text: 'billing.billingSummary.region',
    value: VIEW_BY_OPTION_VALUE.REGION
  },
  {
    text: 'billing.billingSummary.tag',
    value: VIEW_BY_OPTION_VALUE.TAG
  },
  {
    text: 'billing.billingSummary.serviceGroups',
    value: VIEW_BY_OPTION_VALUE.SERVICEGROUP
  }
];

export const TAB_INDEX = {
  AWS : {
    CHARGE_LIST: 0,
    // CLOUD_INVOICE_LIST: 1,
    // CLOUD_BILL_DETAIL: 2,
    CLOUD_BILL_DETAIL: 1,
    // CLOUD_INVOICE_INSIGHT: 3,
    // SERVICE_GROUPS: 4,
    // AUTOSPOT: 5,
    // COMPUTING_RESOURCES: 6
  },
  AZURE : {
    CHARGE_LIST: 0,
    // CLOUD_INVOICE_LIST: 1,
    // CLOUD_BILL_DETAIL: 2
    CLOUD_BILL_DETAIL: 1
  },
  GCP : {
    CHARGE_LIST: 0,
    CLOUD_BILL_DETAIL: 1,
    //ADDITIONAL_SERVICES: 3
  },
  ALI : {
    CHARGE_LIST: 0,
    // CLOUD_INVOICE_LIST: 1,
    // CLOUD_BILL_DETAIL: 2
    CLOUD_BILL_DETAIL: 1
  },
  OCI : {
    CHARGE_LIST: 0,
    CLOUD_BILL_DETAIL: 1,
    CLOUD_INVOICE_INSIGHT: 2
  },
  NCP : {
    CHARGE_LIST: 0,
    // CLOUD_INVOICE_LIST: 1,
    // CLOUD_BILL_DETAIL: 2
    CLOUD_BILL_DETAIL: 1
  },
  TENCENT : {
    CHARGE_LIST: 0,
    // CLOUD_INVOICE_LIST: 1,
    CLOUD_BILL_DETAIL: 1
  },
  OPENSTACK : {
    CHARGE_LIST: 0,
    CLOUD_BILL_DETAIL: 1
  }
};

export const CHART_DOWNLOAD_OPTION_VALUE = {
  CSV: 'CSV'
};

export const CHART_DOWNLOAD_OPTIONS = [
  {
    text: 'billing.monthlyBillTrend.download.option.monthlyBillAmountForCurrentView(CSV)',
    value: CHART_DOWNLOAD_OPTION_VALUE.CSV
  },
];

export const DETAIL_TABLE_DOWNLOAD_OPTION_VALUE = {
  CHARGE_LIST_ON_CURRENT_VIEW: 'CHARGE_LIST_ON_CURRENT_VIEW',
  CLOUD_INVOICE_LIST_ON_CURRENT_VIEW: 'CLOUD_INVOICE_LIST_ON_CURRENT_VIEW',
  CLOUD_BILL_DETAILS: 'CLOUD_BILL_DETAILS',
  COMPUTING_RESOURCES: 'COMPUTING_RESOURCES',
  SERVICE_GROUP: 'SERVICE_GROUP',
  AUTO_SPOT: 'AUTO_SPOT'
};

export const DETAIL_TABLE_DOWNLOAD_OPTIONS = [
  {
    text: 'billing.chargeList.download.option.gcpChargeListOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CHARGE_LIST_ON_CURRENT_VIEW,
    availableVendors : [VENDOR.GCP]
  },
  {
    text: 'billing.chargeList.download.option.chargeListOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CHARGE_LIST_ON_CURRENT_VIEW,
    availableVendors : [VENDOR.AWS, VENDOR.NCP, VENDOR.TENCENT]
  },
  {
    text: 'billing.chargeList.download.option.azureChargeListOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CHARGE_LIST_ON_CURRENT_VIEW,
    availableVendors : [VENDOR.AZURE]
  },
  {
    text: 'billing.chargeList.download.option.openstackServiceListOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CHARGE_LIST_ON_CURRENT_VIEW,
    availableVendors : [VENDOR.OPENSTACK]
  },
  {
    text: 'billing.chargeList.download.option.ociChargeListOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CHARGE_LIST_ON_CURRENT_VIEW,
    availableVendors : [VENDOR.OCI]
  },
  {
    text: 'billing.invoiceList.download.option.cloudInvoiceListOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CLOUD_INVOICE_LIST_ON_CURRENT_VIEW,
    availableVendors : [VENDOR.AWS, VENDOR.AZURE, VENDOR.NCP]
  },
  {
    text: 'billing.cloudBillDetails.download.option.cloudBillDetails(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.CLOUD_BILL_DETAILS,
    availableVendors : [VENDOR.AWS, VENDOR.AZURE, VENDOR.GCP, VENDOR.OCI, VENDOR.NCP, VENDOR.TENCENT, VENDOR.OPENSTACK]
  },
  {
    text: 'billing.serviceGroups.download.option.serviceGroupOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.SERVICE_GROUP,
    availableVendors: []
  },
  {
    text: 'billing.autoSpot.download.option.autoSpotOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.AUTO_SPOT,
    availableVendors: [VENDOR.AWS]
  },
  {
    text: 'billing.computingResources.download.option.computingResourcesOnCurrentView(CSV)',
    value: DETAIL_TABLE_DOWNLOAD_OPTION_VALUE.COMPUTING_RESOURCES,
    availableVendors : []
  },

];

export const METERING_API_VERSION = '${url}/MAZ/v3';

export const SEND_INVOICE_OPTION_VALUE = {
  SEND_ONCE: 'sendOnce',
  SEND_REPEATEDLY: 'sendRepeatedly'
};

export const SEND_INVOICE_OPTIONS = [
  {
    text: 'billing.sendInvoiceModal.sendInvoiceOptions.onlyOnce',
    value: SEND_INVOICE_OPTION_VALUE.SEND_ONCE
  },
  {
    text: 'billing.sendInvoiceModal.sendInvoiceOptions.repeatedly',
    value: SEND_INVOICE_OPTION_VALUE.SEND_REPEATEDLY
  }
];

export const SEND_INVOICE_AVAILABLE_VENDORS = [VENDOR.AWS];

// The request model use for get charges on billing page
export const REQUEST_BILLING_CHARGE_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'chargeMonth': 'string',
  'chargeYear': 'string',
};

// The request model use for get bills on billing page
export const REQUEST_BILLING_BILLS_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'term': 36,
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS'
};

// The request model use for get billing detail on billing page
export const REQUEST_BILLING_DETAILS_MODEL = {
  'chargeMonth': '06',
  'chargeYear': '2019',
  'companyId': 1,
  'siteCode': 'BESPIN',
  'tagKey': '',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'viewBy': 'default'
};

export const REQUEST_BILLING_DETAILS_WITH_TAG_MODEL = {
  'chargeMonth': 'string',
  'chargeYear': 'string',
  'companyId': 1,
  'siteCode': 'BESPIN',
  'tagKey': 'string',
  'tagValue': 'string',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'viewBy': 'default'
};

export const REQUEST_BILLING_DETAILS_WITH_SERVICE_GROUP_MODEL = {
  'chargeMonth': 'string',
  'chargeYear': 'string',
  'companyId': 1,
  'siteCode': 'BESPIN',
  'serviceGroupSetNm': 'string',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'viewBy': 'default'
};

// The request model use for get invoices on billing page
export const REQUEST_BILLING_INVOICES_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor' : 'AWS',
  'chargeMonth': 'string',
  'chargeYear': 'string'
};

export const REQUEST_BILLING_INVOICE_INSIGHT_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'string',
  'yearMonth': 'string',
  'viewBy' : 'string'
};

export const REQUEST_COMPUTING_RESOURCES_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'chargeMonth': 'string',
  'chargeYear': 'string'
};

export const REQUEST_ADDITIONAL_SERVICES_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'chargeMonth': 'string',
  'chargeYear': 'string'
};

export const REQUEST_SERVICE_GROUPS_MODEL = {
  'companyId': 1,
  'siteCode': 'BESPIN',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'vendor': 'AWS',
  'chargeMonth': 'string',
  'chargeYear': 'string'
};

export const REQUEST_AUTOSPOT_MODEL = {
  'accId': null,
  'awsBillCurr' : 'string',
  'cmpnId' : 'string',
  'userId': 'string',
  'month': 'string',
  'year': 'string'
};

export const REQUEST_TAG_KEYS_MODEL = {
  'companyId': 1,
  'endDate': '2019-09-30',
  'vendor' : 'AWS',
  'siteCode': 'BESPIN',
  'startDate': '2019-09-01',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'yearMonth': '2019-09'
};

export const REQUEST_SERVICE_GROUP_VIEWS_MODEL = {
  'companyId': 1,
  'endDate': '2019-09-30',
  'vendor': 'AWS',
  'siteCode': 'BESPIN',
  'startDate': '2019-09-01',
  'userEmail': 'string',
  'userId': 'string',
  'userName': 'string',
  'yearMonth': '2019-09'
};

export const SEND_INVOICE_BY_MAIN_REQUEST_MODEL = {
  siteId: 'string',
  cmpnId: 'string',
  cmpnNm: 'string',
  vendor: 'string',
  rprtId: 'BILL',                    // REPORT의 고유 ID : Report 종류별로 개발자가 정의하여 사용
  rprtTitl: '',
  lang: 'string',
  sendType: 'string',
  currMnthYn: 'Y',
  lnkdAccId: 'string',
  rprtType: 'string',
  creaUser: 'string',
  invoiceContent: 'string',
  alias: 'string',
  billCurr:'string',
  dwldType: 'O',                            // (OWNMENU:O, PEPORTMENU:R, BATCH:B)
  statVal: 'string',                               // I : 등록, U : 갱신, D : 삭제
  recipientList: 'string',
  srchVal: '',
  userId: 'string',
  userNm: 'string',
  srchSt: 'string',
  srchEnd: 'string',
  subsTitl: '',
  ownSubsYn: 'N',
  cyleLocale: 'string',
  cyleType: 'M',
  cyleDayMnth : 11,
  cyleTm : '10:00',
  /*
  rprtSeq: 0,
  cyleDayWeek: '',
  maxSubsCnt: 5,
  rprtDesc: '',
  seq: 0,
   */
}

export const DELETE_RESERVATION_REQUEST_MODEL = {
  'cmpnId': 1,
  'siteId': 'BESPIN',
  'userId': 'string',
  'seq': 'string',
  'delYn': 'Y'
};

/**
 * When active month is 6, first month in monthly cost trend should be 6 - 5 = 1
 *
 * @type {number}
 */
export const DEFAULT_NUMBER_OF_PREVIOUS_MONTHS = 5;

/**
 * When active month is 6, last month in monthly cost trend should be 6 + 6 = 12
 *
 * @type {number}
 */
export const DEFAULT_NUMBER_OF_NEXT_MONTHS = 6;

/**
 * This fields with negative value (-)
 *
 * @type {Array}
 */
export const NEGATIVE_FIELDS = [
  'salesDiscount',
  'ncpDiscount',
  'credit'
];

export const TREE_LAYER_SIZE = {
  LAYER_1_SIZE : "%layer1Size%",
  LAYER_2_SIZE : "%layer2Size%",
  LAYER_3_SIZE : "%layer3Size%",
  LAYER_4_SIZE : "%layer4Size%",
  LAYER_5_SIZE : "%layer5Size%",
};

export const VENDOR_NAMES = {
  AWS: 'AWS',
  AZURE: 'AZURE',
  GCP: 'GCP',
  ALI: 'ALI',
  OCI: 'OCI',
  NCP: 'NCP',
  TENCENT: 'TENCENT'
};

export const NON_TAG_NAME = "NON-TAG";

export const UNDEFINED_GROUP_NAME = "Undefined Group";

export const BILLING_NEW_UPDATE = {
  BILL_SUMMARY: 'bill_summary',
  CHARGE_LIST: 'charge_list',
  CLOUD_INVOICE_LIST: 'cloud_invoice_list',
  CLOUD_BILL_DETAIL: 'cloud_bill_detail',
  CLOUD_INVOICE_INSIGHT: 'cloud_invoice_insight',
  COMPUTING_RESOURCES: 'computing_resources'
};

export const BILLING_NEW_UPDATE_OPTIONS = [
  {
    text: 'onboarding.welcome.newUpdates.billing.billSummary',
    value: BILLING_NEW_UPDATE.BILL_SUMMARY
  },
  {
    text: 'onboarding.welcome.newUpdates.billing.chargeList',
    value: BILLING_NEW_UPDATE.CHARGE_LIST
  },
  {
    text: 'onboarding.welcome.newUpdates.billing.invoiceList',
    value: BILLING_NEW_UPDATE.CLOUD_INVOICE_LIST
  },
  {
    text: 'onboarding.welcome.newUpdates.billing.cloudBillDetail',
    value: BILLING_NEW_UPDATE.CLOUD_BILL_DETAIL
  },
  {
    text: 'onboarding.welcome.newUpdates.billing.invoiceInsight',
    value: BILLING_NEW_UPDATE.CLOUD_INVOICE_INSIGHT
  },
  {
    text: 'onboarding.welcome.newUpdates.billing.computingResources',
    value: BILLING_NEW_UPDATE.COMPUTING_RESOURCES
  },
];

export const CHARGE_LIST_DETAILS_COST_VALUE = {
  CLOUD_ORIGINAL_COST: 'cloudOriginalCost',
  ON_DEMAND_DISCOUNT: 'onDemandDiscount',
  CF_DISCOUNT: 'cloudFrontDiscount',
  CF_DTO_DISCOUNT: 'cloudFrontDtoDiscount',
  CF_REQ_DISCOUNT: 'cloudFrontReqDiscount',
  SUPPORT_FEE: 'supportFee',
  NCP_DISCOUNT: 'ncpDiscount',
  SALES_DISCOUNT: 'salesDiscount',
  CREDIT: 'credit',
  VAT: 'vatCost'
};

export const CHARGE_LIST_DETAILS_COSTS = {
  AWS: [
    {
        name: 'billing.billingChargeDetail.download.costNames.cloudOriginalCost',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.CLOUD_ORIGINAL_COST
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.onDemandDiscount',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.ON_DEMAND_DISCOUNT
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.cfDiscount',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.CF_DISCOUNT
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.cfDtoDiscount',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.CF_DTO_DISCOUNT
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.cfReqDiscount',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.CF_REQ_DISCOUNT
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.supportFee',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.SUPPORT_FEE
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.salesDiscount',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.SALES_DISCOUNT
      },
      {
        name: 'billing.billingChargeDetail.download.costNames.credit',
        type: 'billing.billingChargeDetail.download.costTypes.cloud',
        value: CHARGE_LIST_DETAILS_COST_VALUE.CREDIT
      }
  ],
  NCP : [
    {
      name: 'billing.billingChargeDetail.download.costNames.cloudCost',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CLOUD_ORIGINAL_COST
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.ncpDiscount',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.NCP_DISCOUNT
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.salesDiscount',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.SALES_DISCOUNT
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.credit',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CREDIT
    }
    // ,
    // {
    //   name: 'billing.billingChargeDetail.download.costNames.vat',
    //   type: 'billing.billingChargeDetail.download.costTypes.cloud',
    //   value: CHARGE_LIST_DETAILS_COST_VALUE.VAT
    // }
  ],
  AZURE: [
    {
      name: 'billing.billingChargeDetail.download.costNames.cloudCost',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CLOUD_ORIGINAL_COST
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.supportFee',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.SUPPORT_FEE
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.salesDiscount',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.SALES_DISCOUNT
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.credit',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CREDIT
    }
  ],
  TENCENT : [
    {
      name: 'billing.billingChargeDetail.download.costNames.cloudOriginalCost',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CLOUD_ORIGINAL_COST
    }
  ],
  OCI : [
    {
      name: 'billing.billingChargeDetail.download.costNames.cloudCost',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CLOUD_ORIGINAL_COST
    }
  ],
  GCP : [
    {
      name: 'billing.billingChargeDetail.download.costNames.cloudOriginalCost',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CLOUD_ORIGINAL_COST
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.salesDiscount',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.SALES_DISCOUNT
    },
    {
      name: 'billing.billingChargeDetail.download.costNames.credit',
      type: 'billing.billingChargeDetail.download.costTypes.cloud',
      value: CHARGE_LIST_DETAILS_COST_VALUE.CREDIT
    }
  ]
};

export const USAGE_TYPE = {
  ADDEND_VALUE: 'addendValue',
  TOTAL_VALUE: 'totalValue'
}

export const STANDARD_COLUMN_DEF = {
  headerName: '',
  field: '',
  rowGroupIndex: 0,
  hide: true
}

export const BILLING_VIEW_BY_VENDORS = [
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
  }
  // {
  //   text: 'header.ali',
  //   value: SELECTED_VENDOR.ALI
  // }
];

export const AVAILABLE_VENDORS_BY_REGION = {
  PROD: [VENDOR.AWS,  VENDOR.NCP, VENDOR.AZURE, VENDOR.GCP, VENDOR.OCI],
  MEA: [VENDOR.AWS, VENDOR.AZURE, VENDOR.GCP],
  USA: [VENDOR.AWS, VENDOR.AZURE, VENDOR.GCP],
  DEV: [VENDOR.AWS,  VENDOR.NCP, VENDOR.AZURE, VENDOR.GCP, VENDOR.OCI],
  CHINA: [VENDOR.AWS, VENDOR.AZURE],
  KECMP: [VENDOR.AWS, VENDOR.AZURE, VENDOR.GCP],
  SEA: [VENDOR.AWS, VENDOR.AZURE, VENDOR.GCP]
}

export const AVAILABLE_VENDORS_IN_BILLING_INVOICE = {
  AWS: 'AWS',
  GCP: 'GCP',
  AZU: 'AZURE',
  NCP: 'NCP',
  OCI: 'OCI',
  OPENSTACK: 'OPENSTACK'
}
