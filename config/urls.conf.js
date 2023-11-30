
let domainSplitResult = window.location.hostname.split('.');
let kenobiDomain = domainSplitResult[0];
domainSplitResult[0] = '';
let subDomain = domainSplitResult.join('.');

let ssoPrefix = '';
let portalPrefix = '';
let meteringPrefix = '';

switch (subDomain) {
  default:
    ssoPrefix = 'sso';
    portalPrefix = 'service';
    meteringPrefix = 'metering';
}


// if(domain.indexOf('honecloud.co.kr') > -1){
//   subDomain = '.cmp.honecloud.co.kr'
// }

export default {
    ssoUrl : "",
    kenobiUrl : window.location.protocol + "//" + window.location.host,
    portalUrl: "",
    meteringAdminUrl:"",
    meteringUrl:"",
    portalAdminUrl: "",
    supportUrl: "",
    srUrl:"",
    subDomain : subDomain,
    kenobiDomain: kenobiDomain,
    profile : "",
    brandUrl: "",
    consoleUrl: "",
    refresh: function() {
        switch(this.profile) {
          case 'STAGE':
          case 'PROD':
            break;
          case 'DEV':
            break;
        }
    }
}
