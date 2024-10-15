<template>
  <div class="slider-container slack-guide">
    <p class="guide-note">
      본 가이드는 Slack을 통한 알림을 받기만을 위한 과정을 포함하고 있습니다. <br/> Slack 계정이 존재하지 않을 경우에 대한 회원가입 과정은 포함하지 않고 있습니다.
    </p>
    <h2>1. Slack ChatBot 생성 및 권한 설정</h2>
    <h4>1-1.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG01.png"
        alt="`Slide 1`"></p>
    <p class="comment">https://api.slack.com 으로 접속하여 <strong>'Your apps'</strong>를 클릭합니다.</p>
    <h4>1-2.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG02.png"
        alt="`Slide 2`"></p>
    <p class="comment"><strong>'Create New App'</strong> 버튼을 클릭합니다.</p>
    <h4>1-3.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG03.png"
        alt="`Slide 3`"></p>
    <p class="comment">이후 생성할 앱 세팅은 <strong>'From scratch'</strong>로 선택합니다.</p>
    <h4>1-4.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG04.png"
        alt="`Slide 4`"></p>
    <blockquote class="guide-blockquote"><p class="comment"><strong>App Name</strong> : 알람을 보내줄 챗 봇 앱의 이름입니다.</p></blockquote>
    <p class="comment">챗봇 앱을 생성할 워크스페이스를 지정한 뒤 <strong>'Create App'</strong>을 클릭합니다.</p>
    <h4>1-5.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG05-1.png"
        alt="`Slide 5`"></p>
    <p class="comment">Bot 앱이 알림을 보낼 수 있도록 권한을 지정해 주기 위해 <strong>'OAuth & Permissions'</strong>탭에서 <strong>'Scopes'</strong> 영역으로 이동합니다. <br>
      <br>
      <strong>'Add an OAuth Scope'</strong> 버튼을 클릭하고 Bot 앱이 알림을 보낼 수 있도록 <strong>'chat:write' 권한을 허용</strong>합니다.</p>
    <h2>2. Slack 앱 설치 및 토큰 발급</h2>
    <h4>2-1.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG07.png"
        alt="`Slide 6`"></p>
    <p class="comment">지금까지 설정한 앱을 워크스페이스에 설치하기 위해 <strong>'Install to Workspace'</strong>버튼을 클릭합니다.</p>
    <h4>2-2.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG07-1.png"
        alt="`Slide 7`"></p>
    <p class="comment">생성한 앱이 워크스페이스에 접근하여 설치될 수 있도록 권한을 허용해줍니다.</p>
    <h4>2-3.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG08.png"
        alt="`Slide 8`"></p>
    <p class="comment">앱 설치가 완료되면 앱에 대한 <strong>OAuth Token을 발급</strong> 받습니다.</p>
    <blockquote class="guide-blockquote"><p class="comment"><strong>발급 받은 토큰은 MCMP 알림 설정을 위해 마지막 단계에 필요하며 토큰이 외부로 노출되지 않도록 주의해야 합니다.</strong></p></blockquote>
    <h4>2-4.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG09-1.png"
        alt="`Slide 9`"></p>
    <p class="comment">Slack App을 열고 생성한 워크스페이스, 채널로 이동합니다.</p>
    <p class="comment">채널명을 클릭합니다. (예시 : #alarm-channel)</p>
    <h4>2-5.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG10-1.png"
        alt="`Slide 10`"></p>
    <p class="comment">우측 상단에서 <strong>'이 channel의 모든 멤버 보기'</strong>를 누른 후 <strong>'통합'</strong> 탭에서 <strong>'앱 추가'</strong> 버튼을 클릭합니다. </p>
    <h4>2-6.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG12.png"
        alt="`Slide 12`"></p>
    <p class="comment">생성한 앱을 채널에 추가합니다.</p>
    <h4>2-7.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG13.png"
        alt="`Slide 13`"></p>
    <p class="comment">채널 정보창으로 돌아와서 <strong>채널 ID</strong>를 확인합니다.</p>
    <blockquote class="guide-blockquote"><p class="comment"><strong>확인한 채널의 ID는 MCMP 알림 설정을 위해 마지막 단계에 필요합니다.</strong></p></blockquote>
    <h2>3. MCMP Slack Alarm 연동</h2>
    <h4>3-1.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG14.png"
        alt="`Slide 14`"></p>
    <p class="comment">Slack 챗봇 생성 및 토큰 발급 가이드를 열 수 있습니다.</p>
    <p class="comment"><strong>발급받은 OAuth Token과 Channel ID를 입력합니다.</strong></p>
    <h4>3-2.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/slackGuide/slackIMG15.png"
        alt="`Slide 15`"></p>
    <p class="comment">입력한 Token과 Channel ID를 <strong>저장</strong>합니다.</p>
    <p class="comment">Slack 연동 설정을 끝낸 후 Slack Test 버튼을 통해 알람 테스트를 진행할 수 있습니다.</p>
    <h2>4. MCMP Slack 연동 설정</h2>
    <blockquote class="guide-blockquote">
      <p class="comment">
        <strong>2-2 단계</strong>에서 발급 받은 Workspace OAuth Token과 <strong>2-7 단계</strong>에서 확인한 채널 ID를 입력해주세요.
      </p>
      <p class="comment">
        <strong>Slack App Toekn과 Channel ID는 저장 버튼을 누를 때마다 저장되며 한 번 저장한 뒤로 추가로 입력하지 않아도 됩니다.</strong>
      </p>
    </blockquote>
  </div>
</template>

<script>
export default {
  props: {
  },
  data() {
    return {
    };
  },
  methods: {
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap');

p{
  font-family: "Noto Sans KR", sans-serif;
}

.slider-container {
  width: 100%;
  overflow: hidden;
}

.slides {
  display: flex;
  transition: transform 0.3s ease-in-out;
}
.arrow-btn {
  position: absolute;
  top: 40%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  z-index: 10;
  width: 40px;
  height: 40px;
}
.arrow-btn.left {
  left: 10px;
}
.arrow-btn.right {
  right: 10px;
}
.slide {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 0 0 100%;
  width: 100%;
  position: relative;
}

img {
  /*width: 100%;*/
  height: auto;
  display: block;
}

.caption {
  position: absolute;
  top: 10px;
  left: 10px;
  color: white;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 10px;
  border-radius: 5px;
}

.guideIMG{
  margin: 7px 0 15px;
  border: none;
  max-width: calc(100% - 14px);
  box-shadow: 1px 1px 2px 2px rgba(0, 0, 0, 0.05);
  outline: 1px solid #ccc;
  vertical-align: middle;
}

.guide-note{
  display: block;
  margin-top: 15px;
  font-size: 1rem;
  font-weight: 400;
  color: #999;
  line-height: 1.6em;
  padding-left: 5px;
}

.comment{
  line-height: 1.6;
  display: block;
  margin-block-start: 1em;
  margin-block-end: 1em;
  margin-inline-start: 0px;
  margin-inline-end: 0px;
  unicode-bidi: isolate;
  font-size: 0.875rem;
}

.guide-blockquote{
  margin: 10px 0;
  display: inline-block;
  margin-top: 18px;
  padding: 0 10px;
  font-size: 0.8125rem;
  color: #4A4A4A;
  line-height: 30px;
  border-radius: 1px;
  background-color: #E6EFFA;
  border: 0 none;
}

h2{
  font-size: 1.25rem;
  font-width: 700;
  line-height: 1.5rem;
  margin-top: 0px;
  padding-top: 80px;
  padding-bottom: 20px;
  margin: 0;
}

h4{
  font-size: 0.9375rem;
  font-weight: 700;
  line-height: 1.5em;
  padding-bottom: 10px;
  margin-top: -40px;
  padding-top: 80px;
  margind: 0;
  border: 0 none;
}

</style>
