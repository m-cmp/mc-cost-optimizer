<template>
  <div>
  </div>
</template>

<script>
import {useSelectedOptionsStore} from "@/stores/selectedOptions";

export default {
  name: 'EventListener',
  components: {

  },
  data(){
    return{
      store: useSelectedOptionsStore(),
      messageReceived :false,
      messageTimeout: null
    }
  },
  mounted() {
    this.messageReceived = false;

    window.addEventListener('message', this.handleMessage);

    this.messageTimeout = setTimeout(() => {
      if(!this.messageReceived){
        this.handleEmptyEvent()
      }
    }, 1500);

  },
  beforeUnmount() {
    // 컴포넌트가 파괴되기 전에 이벤트 리스너를 제거해 중복 발생 방지
    window.removeEventListener('message', this.handleMessage);
  },
  methods: {
    handleMessage(event){
      this.messageReceived = true;
      const projectCode = event.data && event.data.projectid !== undefined;
      if (projectCode) {
        this.tumblebugWorkspaceid = event.data.workspaceid;
        this.store.setTumblebugWorkspace(this.tumblebugWorkspaceid);
        this.tumblebugProjectid = event.data.projectid;
        this.store.setTumblebugProject(this.tumblebugProjectid);
        this.tumblebugUsertoken = event.data.usertoken;
        this.store.setTumblebugUserToken(this.tumblebugUsertoken);
        this.$emit('selectOptions');
      } else {
        alert('프로젝트 코드를 전달받지 못했습니다.')
        this.tumblebugWorkspaceid = 'testWs';
        this.store.setTumblebugWorkspace(this.tumblebugWorkspaceid);
        this.tumblebugProjectid = ['testPrj'];
        this.store.setTumblebugProject(this.tumblebugProjectid);
        this.tumblebugUsertoken = 'Null';
        this.store.setTumblebugUserToken(this.tumblebugUsertoken);
        this.$emit('selectOptions');
      }
    }
  },
  handleEmptyEvent(){
    alert('프로젝트 코드를 전달받지 못했습니다. 임시 코드로 대체합니다.')
    this.tumblebugWorkspaceid = 'testWs';
    this.store.setTumblebugWorkspace(this.tumblebugWorkspaceid);
    this.tumblebugProjectid = ['testPrj'];
    this.store.setTumblebugProject(this.tumblebugProjectid);
    this.tumblebugUsertoken = 'Null';
    this.store.setTumblebugUserToken(this.tumblebugUsertoken);
    this.$emit('selectOptions');
  }
}
</script>

<style>

</style>
