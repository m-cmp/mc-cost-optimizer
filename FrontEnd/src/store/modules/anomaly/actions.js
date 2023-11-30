import {
  fetchTotalCostAnomalyDetectionAlerts
} from "@/api/anomalyDetail";

const getTotalAlertsData = (context, payload) => {
  // return new Promise((resolve, reject) => {
  //   fetchTotalCostAnomalyDetectionAlerts(payload)
  //     .then(res => {
  //       context.commit('SET_TOTAL_ALERTS', res);
  //       resolve();
  //     });
  // });
  return;
}

export default {
  getTotalAlertsData
};
