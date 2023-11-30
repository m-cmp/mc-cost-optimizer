/**
 * Created by sungho.hong on 2019-03-06.
 */

export default Object.freeze({

    BRAND_SITE: 'https://localhost',

    /* Language */
    ENGLISH: 'en',
    KOREAN: 'ko',
    CHINESE: 'zh',

    /* Language 코드 (타 서비스와 공통으로 사용함 Value 변경 권장하지 않음) */
    LangCode: 'BSP_LangCode',

    SERVICE: {
        PORTAL: "portal",
        ASSET: "asset",
        GOVERNANCE: "gov",
        METERING:"metering",
        MAIN:"main"
    },
    /* Menu Code */
    MENU: {
        DASHBOARD: 'MTV3010'
    },
    /* kenobi Menu Pre code  MTV3### */
    MENU_PRE_CODE: 'MTV3',
    MENU_EXCEPT:['MTV3130','MTV3120','MTV3150', 'MTV3190', 'MTV3190_01', 'MTV3190_02', 'MTV3190_03'],
    AUTH_TYPE: {
        EDIT: 'AUTH_TYPE_010',
        VIEW: 'AUTH_TYPE_020',
        NONE: 'AUTH_TYPE_030'
    },

    /* SSO 관련 */
    ACCESS_TOKEN: 'access_token',         // 운영계 엑세스 토큰
    ACCESS_TOKEN_DEV: 'access_token_dev', // 개발계 엑세스 토큰

    /* Environment */
    LOCAL: 'LOCAL', // 로컬
    DEV: 'DEV', // 개발
    STAGE: 'STAGE',//스테이지
    PROD: 'PROD', // 운영
    CHINA: 'CHINA', // 운영
    LGE: 'LGE', // 운영
    SCMP: 'SCMP', // 운영
    HCMP: 'HCMP', // 운영

    /* Data type */
    UNDEFINED: 'undefined',

    /* Site code */
    BESPIN: 'BESPIN', // *.MCMP.com

    /* Value */
    CMPN_ID:' cmpnId', // Company ID

    // I18N
    I18N: {
        CHINA: "cn",
        KOREA: "ko"
    },
    PARAM: {
      SELECT : {
          ALL : 'A',        // 모두보기
          UPDATE : 'U',     // Update
          NAME : 'N',       // Name
          SYSTEM: 'S',      // System
          CUSTOM : 'C',     // Custom
          RUNNING : 'R',     // Running
          FINISH : 'FINISH',     // Finish
          NULL: null,     // Null
          NA: "",     // N/A
          Y: "Y",
          N: "N"
      }
    },
    // MCMP Code 정보
    LOGO_TYPE_IMG : "MSP_CMPN_LOGO_TYPE_010", // 로고 타입이 그림일 때 정의 코드
    LOGO_TYPE_TXT : "MSP_CMPN_LOGO_TYPE_020", // 로고 타입이 텍스트일 때 정의 코드
    //... more of your variables
    TYPE: {
        UNDEFINED: 'undefined'
    },
    SYSTEM_NAME: 'system',

    HIST_TYPE: {
        POLICY_CREATE: "policyCreate",
        POLICY_MODIFY: "policyModify",
        POLICY_COPY: "policyCopy",
        POLICY_DELETE: "policyDelete",
        RULE_CREATE: "ruleCreate",
        RULE_MODIFY: "ruleModify",
        RULE_COPY: "ruleCopy",
        RULE_DELETE: "ruleDelete",
        WHITELIST_CREATE: "whiteListCreate",
        WHITELIST_MODIFY: "whiteListUpdate",
        ACCOUNT_ADD: "accountAdd",
        ACCOUNT_DELETE: "accountDelete",
        ACCOUNT_UPDATE: "accountUpdate",
        INSPECTION_ADD: "inspectionAdd",
        INSPECTION_DELETE: "inspectionDelete",
        INSPECTION_UPDATE: "inspectionUpdate",
        COMPLIANCE_START: "complianceStart",
        COMPLIANCE_EXECUTE: "complianceExecute",
        COMPLIANCE_FINISH: "complianceFinish"
    },
    HIST_FILTER: [
        {key: "policy", value: "POLICY" },
        {key: "rule", value: "RULE" },
        {key: "whiteList", value: "WHITELIST" },
        {key: "account", value: "ACCOUNT" },
        {key: "inspection", value: "SET_EVAL_ITEM" },
        {key: "compliance", value: "EVAL_POLICY" },

    ],
    COMPLIANCE_STAT: {
        FAIL: "F",
        PASS: "P",
        EXAMINING: "R",
        ERROR: "E",
        UNEXAMINED: "N",
        FAIL_UNSOLVED: "FU",
        FAIL_SOLVED: "FS"
    },
    COMPLIANCE_STAT_FILTER:[
        {key : 'FU', val: 'POLICY.EVAL_STAT.FAIL_UNSOLVED'},
        {key : 'FS', val: 'POLICY.EVAL_STAT.FAIL_SOLVED'},
        {key : 'P', val: 'POLICY.EVAL_STAT.PASS'},
        {key : 'R', val: 'POLICY.EVAL_STAT.EXAMINING'},
        {key : 'E', val: 'POLICY.EVAL_STAT.ERROR'},
        {key : 'N', val: 'POLICY.EVAL_STAT.UNEXAMINED'}
    ],
    POLICY_TYPE_FILTER:[
        {key : '', val: 'FILTER.SELECT_POLICY_TYPE'},
        {key : 'A', val: 'FILTER.SELECT_POLICY_TYPE'},
        {key : 'N', val: 'LABEL.VIEW_SYSTEM_POLICY_ONLY'},
        {key : 'Y', val: 'LABEL.VIEW_USER_SETTINGS_ONLY'}
    ],
    SVRTY_FILTER:[
        {key : 4, val: 'LABEL.SVRTY.4'},
        {key : 3, val: 'LABEL.SVRTY.3'},
        {key : 2, val: 'LABEL.SVRTY.2'},
        {key : 1, val: 'LABEL.SVRTY.1'}
    ]

})

