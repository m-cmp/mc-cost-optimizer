<template>
  <div class="slider-container slack-guide">
    <p class="guide-note">
      본 가이드는 Gmail을 통해 알림 받는 과정만을 포함하고 있습니다. <br/> 사전에 메일 제공 주최가 되는 계정을 만들어 주시기 바랍니다.
    </p>
    <h2>1. Gmail 설정 변경</h2>
    <h4>1-1.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/mailingGuide/mailingIMG01.png"
        alt="`Slide 1`"></p>
    <p class="comment">메일을 제공할 계정으로 로그인한 후 Gamil의 <strong>설정 탭</strong>에서 '전달 및 POP/IMAP' 탭에 접속하여 <strong>IMAP 사용</strong>을 체크하고 저장합니다.</p>
    <blockquote class="guide-blockquote">
      <p class="comment">
        IMAP은 전자 메일에 액세스하기 위한 방법입니다.
      </p>
    </blockquote>
    <h2>2. 앱 비밀번호 설정</h2>
    <h4>2-1.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/mailingGuide/mailingIMG02.png"
        alt="`Slide 2`"></p>
    <p class="comment">Google 계정 설정에 접속하여 <strong>'앱 비밀번호'</strong> 설정을 위한 페이지로 이동합니다.</p>
    <blockquote class="guide-blockquote">
      <p class="comment">
        앱 비밀번호는 <strong>2단계 인증</strong>이 설정된 계정에서만 이용할 수 있습니다. '앱 비밀번호'가 뜨지 않는 경우 <strong>2단계 인증</strong>을 먼저 설정해주시기 바랍니다. <br>
        <img
        class="guideIMG"
        src="@/assets/images/mailingGuide/mailingIMG02-1.png"
        alt="`Slide 2`">
      </p>
    </blockquote>
    <h4>2-2.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/mailingGuide/mailingIMG03.png"
        alt="`Slide 3`"></p>
    <p class="comment">앱 이름으로 원하는 이름을 입력한 뒤 <strong>'만들기'버튼</strong>을 선택합니다.</p>
    <h4>2-3.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/mailingGuide/mailingIMG04.png"
        alt="`Slide 4`"></p>
    <p class="comment">앱 비밀번호가 발급됩니다.</p>
    <blockquote class="guide-blockquote"><p class="comment"><strong>발급 받은 앱 비밀번호는 MCMP Mail 알림 설정을 위해 마지막 단계에 필요하며 앱 비밀번호는 다시 조회할 수 없으므로 잘 기억해두시기 바랍니다.</strong></p></blockquote>
    <h2>3. Mail 연동</h2>
    <h4>3-1.</h4>
    <p><img
        class="guideIMG"
        src="@/assets/images/mailingGuide/mailingIMG05.png"
        alt="`Slide 5`"></p>
    <p class="comment">MCMP Alarm 탭에서 Mailing Guide 버튼을 선택합니다.</p>
    <p class="comment">4 단계의 Mail 연동 설정 파트에서 Mail User ID와 App Password를 입력하고 저장합니다.</p>
    <p class="comment">모달 외부의 Mail Test 버튼을 통해 Mail 연동을 테스트할 수 있습니다.</p>
    <h2>4. Mail 연동 설정</h2>
    <blockquote class="guide-blockquote">
      <p class="comment">
        메일을 발송할 주체가 되는 Gmail계정 ID와 <strong>2-3 단계</strong>에서 발급받은 App Password를 입력해주세요.
      </p>
      <p class="comment">
        <strong>Mail User ID와 App Password는 저장 버튼을 누를 때마다 저장되며 한 번 저장한 뒤로 추가로 입력하지 않아도 됩니다.</strong>
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
