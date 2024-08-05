const MainDomain = location.hostname
console.log('도메인 확인' + MainDomain)
// const isNumericAndDotsOnly = /^[0-9.]+$/.test(MainDomain);

let API_BE_URL = '';
let API_ALARM_URL = '';
if(MainDomain.includes('localhost')){
    API_BE_URL = 'http://' + MainDomain + ':9090';
    API_ALARM_URL = 'https://' + MainDomain + ':9000';
} else {
    API_BE_URL = 'https://' + MainDomain;
    API_ALARM_URL = 'https://' + MainDomain;
}
export { API_BE_URL, API_ALARM_URL }

export default {
    be: API_BE_URL,
    alaram: API_ALARM_URL
}
