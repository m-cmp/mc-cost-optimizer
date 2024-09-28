import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import PrimeVue from 'primevue/config'

// Bootstrap CSS, JavaScript
// import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

// PrimeVue styles
import 'primevue/resources/themes/saga-blue/theme.css'  // theme
import 'primevue/resources/primevue.min.css'  // core CSS
import 'primeicons/primeicons.css'  // icons

const app = createApp(App)

app.use(router).use(createPinia()).use(PrimeVue)

app.mount('#app')