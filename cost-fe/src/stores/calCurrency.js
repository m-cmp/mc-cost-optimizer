// src/stores/calCurrency.js
import { defineStore } from 'pinia';
export const useCalCurrencyStore = defineStore('calCurrency', {
    state: () => ({
    }),
    actions: {
        usdToKrw(usd) {
            const exchangeRate = 1389.92;
            return usd * exchangeRate;
        }
    }
});
