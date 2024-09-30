<template>
  <div class="col-12">
    <div class="card">
      <div class="card-header">
        <h3 class="card-title">추천 이력</h3>
      </div>
      <div>
        <!-- PrimeVue DataTable 컴포넌트 -->
        <DataTable :value="alarms" :paginator="true" :rows="rows" tableStyle="min-width: 50rem">
          <Column field="occure_time" header="일시"
                  headerStyle="width: 130px; display: flex; font-weight: bold;"></Column>
          <Column field="csp_type" header="CSP" headerStyle="width: auto; justify-content: center"></Column>
          <Column field="resource_id" header="리소스 ID" headerStyle="width: 200px; justify-content: center"></Column>
          <Column field="resource_type" header="리소스 타입" headerStyle="width: 120px; text-align: center;"></Column>
          
          <Column header="알람 종류" headerStyle="width: 120px; text-align: center;">
            <template #body="slotProps">
              {{ translateEventType(slotProps.data.event_type) }}
            </template>
          </Column>

          <!-- note 필드의 body 슬롯 설정 -->
          <Column header="알람 내용" headerStyle="width: auto; text-align: center;">
            <template #body="slotProps">

              <span v-if="slotProps.data.event_type === 'Unused' && !slotProps.data.note">
                미사용 자원으로, 확인이 필요합니다.
              </span>
              <span v-else>
                {{ slotProps.data.note }}
              </span>
            </template>
          </Column>

          <!-- 조건부 렌더링을 위한 body 슬롯 사용 -->
          <Column header="추천 유형" headerStyle="width: 120px; text-align: center;">
            <template #body="slotProps">

              <span v-if="slotProps.data.event_type === 'Abnormal'"
                    :style="getUrgencyStyle(slotProps.data.urgency)">
                {{ translateUrgency(slotProps.data.urgency) }}
              </span>

              <span v-else-if="slotProps.data.event_type === 'Resize' || slotProps.data.event_type === 'Unused'"
                    :style="slotProps.data.plan">
                {{ translatePlan(slotProps.data.plan) }}
              </span>

              <span v-else>
                {{ slotProps.data.plan }}
              </span>
            </template>
          </Column>

        </DataTable>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';

export default {
  props: {
  },
  data() {
    return {
      alarms: Array.from({ length: 10 }, () => ({
        occure_time: '',
        csp_type: '',
        resource_id: '',
        resource_type: '',
        event_type: '',
        note: '',
        plan: ''
      })),
      rows: 10,
    };
  },
  mounted() {
    this.fetchAlarmHistory();
  },
  methods: {
    async fetchAlarmHistory() {
      try {
        const response = await axios.post('http://localhost:9090/api/v2/alarm/history', {
          selectedCsps: ["AWS"],
          selectedWorkspace: "testWs",
          selectedProjects: ["testPrj", "testPrj2"]
        });

        const alarmData = response.data.Data.alarmHistory;

        this.alarms = alarmData.map(alarm => ({
          ...alarm,
          occure_time: this.formatDate(alarm.occure_time)
        })); 
      } catch (error) {
        console.error('API 호출 오류:', error);
      }
    },
    translateEventType(eventType) {
      switch (eventType) {
        case 'Abnormal':
          return '비정상';
        case 'Resize':
          return '사이즈 변경';
        case 'Unused':
          return '미사용';
        default:
          return eventType;
      }
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      const yyyy = date.getFullYear();
      const mm = String(date.getMonth() + 1).padStart(2, '0'); 
      const dd = String(date.getDate()).padStart(2, '0'); 
      return `${yyyy}-${mm}-${dd}`;
    },
    getUrgencyStyle(urgency) {
      switch (urgency) {
        case 'Caution':
          return { color: 'orange', fontWeight: 'bold' };
        case 'Warning':
          return { color: 'darkorange', fontWeight: 'bold' };
        case 'Critical':
          return { color: 'red', fontWeight: 'bold' };
        default:
          return { color: 'black', fontWeight: 'normal' };
      }
    },
    translateUrgency(urgency) {
      switch (urgency) {
        case 'Caution':
          return '주의';
        case 'Warning':
          return '경고';
        case 'Critical':
          return '긴급';
        default:
          return urgency;
      }
    },
    translatePlan(plan) {
      switch (plan) {
        case 'Up':
          return '상향';
        case 'Down':
          return '하향';
        case 'Unused':
          return '미사용';
        default:
          return plan;
      }
    }
  },
  components: {
    DataTable,
    Column
  },
}
</script>

<style>
.card-title {
  font-family: 'Arial', sans-serif;
  font-size: 24px;
  font-weight: bold;
  color: #3c3c3c;
}

</style>
