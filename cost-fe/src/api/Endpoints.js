const MainDomain = location.hostname
console.log('도메인 확인' + MainDomain)

export default {
    be: 'http://' + MainDomain + ':9090',
    alaram: 'http://' + MainDomain + ':9000'
}
