<template>
  <section
      id="alert-set-table"
      class="-wrapper-column -wrapper-tile w-100 ">
    <div class="alert-set">
      <div class="btn-space">
        <button
            :disabled="toggleSaveBtn"
            class="guide-btn btn btn-primary"
            data-bs-toggle="modal"
            data-bs-target="#mailingGuideModal"
        >
          Mailing Apply/Guide
        </button>
        <button
            :disabled="toggleSaveBtn"
            class="guide-btn btn btn-primary"
            data-bs-toggle="modal"
            data-bs-target="#slackGuideModal"
        >
          Slack Apply/Guide
        </button>
        <button
            :disabled="toggleSaveBtn"
            class="guide-btn btn"
            @click="alarmSend"
        >
          Slack Test
        </button>
        <button
            :disabled="toggleSaveBtn"
            class="guide-btn btn"
            data-bs-toggle="modal"
            data-bs-target="#mailSendModal"
        >
          Mail Test
        </button>
      </div>

      <div class="modal" id="slackGuideModal" tabindex="-1">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Slack Alarm Guide & Setting Token</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div style="color: #666; margin-left: 10px;">
                <div>
                  <SlackGuideScript/>
                </div>
                <div>
                  <table>
                    <tr>
                      <th>
                        Slack App Token
                      </th>
                      <td style="">
                        <input
                            :type="inputTkType"
                            v-model="token"
                            class="form-control save-token-input"
                            name="example-text-input"
                            placeholder="Input Slack App Token"
                            @focus="showTextTk"
                            @blur="hideTextTk"/>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        Channel ID
                      </th>
                      <td style="display: flex;">
                        <input
                            :type="inputChType"
                            v-model="channel"
                            class="form-control save-token-input"
                            name="example-text-input"
                            placeholder="Input Slack Channel ID"
                            @focus="showTextCh"
                            @blur="hideTextCh"/>
                      </td>
                    </tr>
                  </table>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn me-auto" data-bs-dismiss="modal">Close</button>
              <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="saveToken">Save Toekns</button>
            </div>
          </div>
        </div>
      </div>

      <div class="modal" id="mailingGuideModal" tabindex="-1">
        <div class="modal-dialog modal-lg" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Modal title</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <MailingGuideScript/>
              <div>
                <table>
                  <tr>
                    <th>
                      Mail User ID
                    </th>
                    <td style="">
                      <input
                          :type="inputTkType"
                          v-model="mailuserID"
                          class="form-control save-token-input"
                          name="example-text-input"
                          placeholder="Input Mail User ID"
                          @focus="showTextTk"
                          @blur="hideTextTk"/>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      Mail App Password
                    </th>
                    <td style="display: flex;">
                      <input
                          :type="inputChType"
                          v-model="mailAppPasswd"
                          class="form-control save-token-input"
                          name="example-text-input"
                          placeholder="Input Mail App Password"
                          @focus="showTextCh"
                          @blur="hideTextCh"/>
                    </td>
                  </tr>
                </table>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn me-auto" data-bs-dismiss="modal">Close</button>
              <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="saveMailpw">Save Password</button>
            </div>
          </div>
        </div>
      </div>

      <div class="modal" id="mailSendModal" tabindex="-1">
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Modal title</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <div>
                <p class="guide-note">메일 테스트에서 받는 사람은 한 사람만 지정 가능합니다.<br/>메일이 30초 이내에 발송됩니다.</p>
              </div>
              <div>
                <table>
                  <tr>
                    <th>
                      받는사람
                    </th>
                    <td style="">
                      <input
                          type="text"
                          v-model="testMailTo"
                          class="form-control save-token-input"
                          name="example-text-input"
                          placeholder="Input To"/>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      제목
                    </th>
                    <td style="display: flex;">
                      <input
                          type="text"
                          v-model="testMailTitle"
                          class="form-control save-token-input"
                          name="example-text-input"
                          placeholder="Input Mail Title"/>
                    </td>
                  </tr>
                </table>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn me-auto" data-bs-dismiss="modal">Close</button>
              <button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="mailSendTest">Send Mail</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import axios from "axios";
import ENDPOINT from '@/api/Endpoints'
import SlackGuideScript from "@/components/pages/userGuide/SlackGuideScript.vue";
import MailingGuideScript from "@/components/pages/userGuide/MailingGuideScript.vue";

export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Alarm",
  components: {
    SlackGuideScript,
    MailingGuideScript
  },
  data() {
    let imgItem = [];
    for (let i = 1; i <= 17; i++) {
      imgItem.push({ src: require(`@/assets/images/slack/slack-guide-${i}.png`) });
    }
    return {
      token: "",
      channel: "",
      toggleSaveBtn: false,
      inputTkType: "password",
      inputChType: "password",
      linkUrl: null,
      linkText: null,
      imgItem,
      testMsg: "",
      mailuserID: null,
      mailAppPasswd: null,
      testMailTo: "",
      testMailTitle: "MCMP Test Mail"
    }
  },
  mounted() {
    this.getInitSlackInfo();
    this.getInitMailInfo();
  },
  methods: {
    getInitSlackInfo(){
      axios.get(ENDPOINT.alaram + '/alert/getSlackIF', {
        params: {
          userId: '2194155'
        }})
          .then((res) => {
            if(res.data.status === 'OK'){
              if(res.data.Data && res.data.Data.channel && res.data.Data.token){
                this.token = res.data.Data.token;
                this.channel = res.data.Data.channel;
              }
            }
          })
          .catch((err) => {
            console.log(err)
          })
    },
    getInitMailInfo(){
      axios.get(ENDPOINT.alaram + '/alert/getMailInfo')
          .then((res) => {
            if(res.data.status === 'OK'){
              if(res.data.Data && res.data.Data.username){
                this.mailuserID = res.data.Data.username
                this.mailAppPasswd = res.data.Data.password
              }
            }
          })
          .catch((err) => {
            console.log(err)
          })
    },
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
      axios.post(ENDPOINT.alaram + '/alert/insertSlackToken', params)
          .then(() => {
            alert('Save.')
            this.toggleSaveBtn = false
          })
          .catch((err) =>{
            console.log(err)
            alert('Error.')
            this.toggleSaveBtn = false
          })
    },
    saveMailpw(){
      this.toggleSaveBtn = true
      if(this.mailuserID && this.mailAppPasswd){
        axios.post(ENDPOINT.alaram + '/alert/insertMailInfo', {
          username: this.mailuserID,
          password: this.mailAppPasswd
        })
            .then((res) => {
              if (!res.data.status === "OK") {
                console.log(res)
                alert('Error.')
              }
              this.toggleSaveBtn = false
            })
            .catch(err => {
              console.log(err);
              this.toggleSaveBtn = false
            })
      }else{
        alert('Error. 빈 값이 있습니다.')
        this.toggleSaveBtn = false
      }
    },
    alarmSend() {
      this.toggleSaveBtn = true
      let userId = '2194155'
      // let message = "이상 비용이 발생하였습니다. 자세한 내용은 우측 링크를 클릭해주세요.(테스트 메세지)"
      // let linkUrl = 'http://localhost:8080/dashboard'
      // let linkText = '이동하기'
      const params = new URLSearchParams({
        userId: userId,
        message: this.testMsg
      });
      if (this.linkUrl) {
        params.append('linkUrl', this.linkUrl);
      }
      if (this.linkText) {
        params.append('linkText', this.linkText);
      }
      axios.post(ENDPOINT.alaram + '/alert/sendSlackAC', params)
          .then((res) => {
            alert(res.data)
            this.toggleSaveBtn = false
          })
          .catch((err) =>{
            console.log(err)
            alert('오류가 발생하여 전송에 실패했습니다.')
            this.toggleSaveBtn = false
          })
    },
    mailSendTest(){
      if(!this.testMailTo){
        alert('받는 사람을 지정해주세요')
      }else{
        const mailTo = [];
        mailTo.push(this.testMailTo)
        axios.post(ENDPOINT.alaram + '/alert/sendAlertMail', {
          to: mailTo,
          subject: this.testMailTitle
        })
            .then((res) => {
              if (!res.data.status === "OK") {
                console.log(res)
                alert('Error.')
              }
              this.toggleSaveBtn = false
            })
            .catch(err => {
              alert('오류가 발생하여 전송에 실패했습니다.')
              console.log(err);
              this.toggleSaveBtn = false
            })
      }
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
    closeGuide() {
      this.$refs['bv-modal-ch-guide'].hide()
    },
    initSlackInfo(){
      this.token = "";
      this.channel = "";
    }
    // openTest() {
    //   this.$refs['bv-modal-alm-test'].show()
    // },
    // closeTest() {
    //   this.testMsg = ""
    //   this.$refs['bv-modal-alm-test'].hide()
    // }
  }
}
</script>

<style lang="scss">
#alert-set-table {
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
      font-size: 0.8rem;
    }

    .save-token-input {
      width: 100%;
      font-size: 12px;
      background-color: white;
      text-overflow: ellipsis;
      overflow: hidden;
      white-space: nowrap;
    }
    .btn-space {
      width: 100%;
      display: flex;
      margin-bottom: 20px;
    }

    .guide-btn {
      font-size: 0.8rem;
      margin-right: 20px;
      text-align: center;
      //color: #FFF;
    }

    .alert-footer {
      margin: 15px 10px;
      width: 100%;
      display: flex;
    }

    .save-token-btn {
      margin: 10px 10px 0 auto;
    }
    .alert-guide {
      margin-top: 25px;
    }
  }
  //#bv-modal-ch-guide___BV_modal_body_ {
  //  .modal-title {
  //    font-size: large;
  //    font-weight: bold;
  //  }
  //  .modal-body {
  //    height: 750px;
  //    overflow-y: auto;
  //  }
  //}
}
.modal-title {
  font-size: 24px;
  font-weight: bold;
}

.modal{
  .guide-note{
    display: block;
    margin-top: 15px;
    font-size: 1rem;
    font-weight: 400;
    color: #999;
    line-height: 1.6em;
    padding-left: 5px;
  }
}
</style>
