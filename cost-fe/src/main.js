import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
// import 'primeflex/primeflex.css'
import 'primevue/resources/themes/aura-light-green/theme.css';
// import 'primevue/resources/themes/saga-blue/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';
import PrimeVue from "primevue/config";

// Bootstrap CSS, JavaScript
// import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';


createApp(App)
    .use(router)
    .use(PrimeVue)
    .use(createPinia())
    .mount('#app')

