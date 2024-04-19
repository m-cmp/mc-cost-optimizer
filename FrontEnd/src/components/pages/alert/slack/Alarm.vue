<template>
  <section
    id="alert-set-table"
    class="-wrapper-column -wrapper-tile w-100 ">
    <div class="alert-set">
      <table>
        <tr>
          <th>
            Slack App Token
          </th>
          <td style="display: flex;">
            <b-input
              :type="inputTkType"
              v-model="token"
              class="save-token-input"
              @focus="showTextTk"
              @blur="hideTextTk"
            />
          </td>
        </tr>
        <tr>
          <th>
            Channel ID
          </th>
          <td style="display: flex;">
            <b-input
              :type="inputChType"
              v-model="channel"
              class="save-token-input"
              @focus="showTextCh"
              @blur="hideTextCh"
            />
          </td>
        </tr>
      </table>
      <div class="alert-footer">
        <b-button
          :disabled="toggleSaveBtn"
          :variant="'blue-2'"
          class="save-token-btn"
          @click="saveToken"
        >
          저장
        </b-button>
      </div>
      <div class="alert-footer">
        <b-button
          :disabled="toggleSaveBtn"
          :variant="'gray-2'"
          class="test-btn"
          @click="alarmSend"
        >
          Alarm Test
        </b-button>
      </div>
    </div>
  </section>
</template>

<script>
import axios from "axios";
import ENDPOINT from "../../../../api/endpoints";
import _get from "lodash/get";

export default {
  name: "Alarm",
  data() {
    return {
      token: "",
      channel: "",
      toggleSaveBtn: false,
      inputTkType: "password",
      inputChType: "password",
    }
  },
  methods: {
    saveToken() {
      this.toggleSaveBtn = true
      let userId = '2194155'
      let token = this.token
      let channel = this.channel
      const params = {
        id: userId,
        token: token,
        channel: channel
      }
      axios.post('http://localhost:9000/insertToken', params)
        .then(() => {
          alert('Save.')
          this.toggleSaveBtn = false
        })
        .catch((err) =>{
          console.log(err)
          this.toggleSaveBtn = false
        })
    },
    alarmSend() {
      this.toggleSaveBtn = true
      let userId = '2194155'
      let message = "이상 비용이 발생하였습니다. 자세한 내용은 우측 링크를 클릭해주세요.(테스트 메세지)"
      let linkUrl = 'http://localhost:8080/dashboard'
      let linkText = '이동하기'
      const params = new URLSearchParams({
        userId: userId,
        message: message
      });
      if (linkUrl) {
        params.append('linkUrl', linkUrl);
      }
      if (linkText) {
        params.append('linkText', linkText);
      }
      axios.post('http://localhost:9000/sendAC', params)
        .then((res) => {
          alert(res.data)
          this.toggleSaveBtn = false
        })
        .catch((err) =>{
          console.log(err)
          this.toggleSaveBtn = false
        })
    },
    showTextTk() {
      this.inputTkType = 'text';
    },
    showTextCh() {
      this.inputChType = 'text';
    },
    hideTextTk() {
      this.inputTkType = 'password';
    },
    hideTextCh() {
      this.inputChType = 'password';
    },
  }
}
</script>

<style lang="scss">
#alert-set-table {

}
.alert-set {
  margin: 25px 0 5px 30px;
  table {
    border-collapse: collapse;
    width: 100%;
  }

  th, td {
    border: 1px solid #dddddd;
    text-align: left;
    padding: 8px;
    height: 50px;
  }

  th {
    background-color: #f2f2f2;
    width: 200px;
    padding-left: 30px;
    font-size: 1rem;
  }

  .save-token-input {
    width: 210px;
    font-size: 12px;
    background-color: white;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }

  .test-btn {
    font-size: 0.8rem;
    margin-left: auto;
    text-align: center;
  }

  .alert-footer {
    margin: 15px 10px;
    width: 100%;
    display: flex;
  }

  .save-token-btn {
    margin: 10px 10px 0 auto;
  }
}
</style>
