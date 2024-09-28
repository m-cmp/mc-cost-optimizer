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
          <!-- <Column field="event_type" header="알람 종류" headerStyle="width: 120px; text-align: center;"></Column> -->
          <!-- <Column field="note" header="알람 내용" headerStyle="width: auto; text-align: center;"></Column> -->
          <!-- <Column field="plan" header="추천 유형" headerStyle="width: 120px; text-align: center;"></Column> -->
          
          <!-- event_type 필드를 한글로 변환하여 출력 -->
          <Column header="알람 종류" headerStyle="width: 120px; text-align: center;">
            <template #body="slotProps">
              {{ translateEventType(slotProps.data.event_type) }}
            </template>
          </Column>

          <!-- note 필드의 body 슬롯 설정 -->
          <Column header="알람 내용" headerStyle="width: auto; text-align: center;">
            <template #body="slotProps">
              <!-- event_type이 'Unused'이고, note가 비어있을 때 -->
              <span v-if="slotProps.data.event_type === 'Unused' && !slotProps.data.note">
                미사용 자원으로, 확인이 필요합니다.
              </span>
              <!-- 그 외의 경우 note 값 그대로 표시 -->
              <span v-else>
                {{ slotProps.data.note }}
              </span>
            </template>
          </Column>

          <!-- 조건부 렌더링을 위한 body 슬롯 사용 -->
          <Column header="추천 유형" headerStyle="width: 120px; text-align: center;">
            <template #body="slotProps">

              <!-- event_type이 'Abnormal'인 경우 urgency 값을 표시, 아닌 경우 plan 값 표시 -->
              <span v-if="slotProps.data.event_type === 'Abnormal'"
                    :style="getUrgencyStyle(slotProps.data.urgency)">
                {{ translateUrgency(slotProps.data.urgency) }}
              </span>

              <!-- event_type이 'Resize'인 경우 plan 값을 스타일과 함께 표시 -->
              <span v-else-if="slotProps.data.event_type === 'Resize' || slotProps.data.event_type === 'Unused'"
                    :style="slotProps.data.plan">
                {{ translatePlan(slotProps.data.plan) }}
              </span>

              <!-- 그 외의 경우 plan 값을 그대로 표시 -->
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
      rows: 10, // 한 페이지에 표시할 행 수
    };
  },
  mounted() {
    this.fetchAlarmHistory(); // 컴포넌트가 마운트되면 API 호출
  },
  methods: {
    async fetchAlarmHistory() {
      try {
        // API POST 요청 설정
        const response = await axios.post('http://localhost:9090/api/v2/alarm/history', {
          selectedCsps: ["AWS"],
          selectedWorkspace: "testWs",
          selectedProjects: ["testPrj", "testPrj2"]
        });

        //응답 데이터에서 alarmHistory 배열만 가져와 할당
        const alarmData = response.data.Data.alarmHistory;

        // 날짜 변환후 alarms에 저장, 
        this.alarms = alarmData.map(alarm => ({
          ...alarm,
          occure_time: this.formatDate(alarm.occure_time) // 날짜 형식 변환
        })); 
      } catch (error) {
        console.error('API 호출 오류:', error);
      }
    },
    // event_type을 한글로 변환
    translateEventType(eventType) {
      switch (eventType) {
        case 'Abnormal':
          return '비정상';
        case 'Resize':
          return '사이즈 변경';
        case 'Unused':
          return '미사용';
        default:
          return eventType; // 기본적으로 원래 값을 반환
      }
    },
    // 날짜 형식을 yyyy-mm-dd로 변환하는 메소드
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString); // 문자열을 Date 객체로 변환
      const yyyy = date.getFullYear();
      const mm = String(date.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 +1, 두 자리로 변환
      const dd = String(date.getDate()).padStart(2, '0'); // 두 자리로 변환
      return `${yyyy}-${mm}-${dd}`; // yyyy-mm-dd 형식으로 반환
    },
    // urgency 값에 따라 스타일 반환
    getUrgencyStyle(urgency) {
      switch (urgency) {
        case 'Caution':
          return { color: 'orange', fontWeight: 'bold' }; // 주의 (노란색)
        case 'Warning':
          return { color: 'darkorange', fontWeight: 'bold' }; // 경고 (주황색)
        case 'Critical':
          return { color: 'red', fontWeight: 'bold' }; // 긴급 (빨간색)
        default:
          return { color: 'black', fontWeight: 'normal' }; // 기본 (검정색)
      }
    },
    // urgency 값을 한국어로 변환
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
    // plan 값에 따라 스타일 반환
    // getPlanStyle(plan) {
    //   switch (plan) {
    //     case 'Up':
    //       return { color: 'red' }; // 상향 (빨간색)
    //     case 'Down':
    //       return { color: 'blue' }; // 하향 (파란색)
    //     default:
    //       return { color: 'black', fontWeight: 'normal' }; // 기본 (검정색)
    //   }
    // },
    // plan 값을 한국어로 변환
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
/* 타이틀 스타일 */
.card-title {
  font-family: 'Arial', sans-serif;
  font-size: 24px;
  font-weight: bold;
  color: #3c3c3c;
}

</style>
