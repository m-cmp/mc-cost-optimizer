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
      messageTimeout: null,
      tumblebugWorkspaceid: null,
      tumblebugProjectid: null,
      tumblebugUsertoken: null,
      tumblebugWorkspaceName: null,
      tumblebugProjectName: null,
      tumblebugProjectUUID: null
    }
  },
  mounted() {
    this.messageReceived = false;

    window.addEventListener('message', this.handleMessage);

    this.messageTimeout = setTimeout(() => {
      if(!this.messageReceived){
        console.log("메시지가 수신되지 않았습니다. 임시 project 코드를 적용하겠습니다.");
        this.handleEmptyEvent();
      }
    }, 3000);

  },
  beforeUnmount() {
    // 컴포넌트가 파괴되기 전에 이벤트 리스너를 제거해 중복 발생 방지
    window.removeEventListener('message', this.handleMessage);
  },
  methods: {
    handleMessage(event){
      this.messageReceived = true;

      //projectCode 확인
      console.log('event', event);

      const projectCode = event.data && event.data.accessToken !== undefined;
      if (projectCode) {
        this.tumblebugWorkspaceid = event.data.workspaceInfo.id;
        this.store.setTumblebugWorkspace(this.tumblebugWorkspaceid);
        this.tumblebugWorkspaceName = event.data.workspaceInfo.name;
        this.store.setTumblebugWorkspaceName(this.tumblebugWorkspaceName);
        this.tumblebugProjectid = event.data.projectInfo.ns_id;
        this.store.setTumblebugProject(this.tumblebugProjectid);
        this.tumblebugProjectUUID = event.data.projectInfo.id;
        this.store.setTumblebugProjectUUID(this.tumblebugProjectUUID);
        this.tumblebugProjectName = event.data.projectInfo.name;
        this.store.setTumblebugProjectName(this.tumblebugProjectName);
        this.tumblebugUsertoken = event.data.accessToken;
        this.store.setTumblebugUserToken(this.tumblebugUsertoken);
      } else {
        if (process.env.NODE_ENV !== 'development') {
          alert('프로젝트 코드를 전달받지 못했습니다.')
        }
        this.tumblebugWorkspaceid = 'testWs';
        this.store.setTumblebugWorkspace(this.tumblebugWorkspaceid);
        this.tumblebugWorkspaceName = 'testWs';
        this.store.setTumblebugWorkspaceName(this.tumblebugWorkspaceName);
        this.tumblebugProjectid = 'undefined';
        this.store.setTumblebugProject(this.tumblebugProjectid);
        this.tumblebugProjectName = 'undefined';
        this.store.setTumblebugProjectName(this.tumblebugProjectName);
        this.tumblebugUsertoken = 'Null';
        this.store.setTumblebugUserToken(this.tumblebugUsertoken);
      }
    },
    handleEmptyEvent(){
      if (process.env.NODE_ENV !== 'development') {
        alert('프로젝트 코드를 전달받지 못했습니다. 임시 코드로 대체합니다.')
      }
      this.tumblebugWorkspaceid = 'testWs';
      this.store.setTumblebugWorkspace(this.tumblebugWorkspaceid);
      this.tumblebugWorkspaceName = 'testWs';
      this.store.setTumblebugWorkspaceName(this.tumblebugWorkspaceName);
      this.tumblebugProjectid = 'undefined';
      this.store.setTumblebugProject(this.tumblebugProjectid);
      this.tumblebugProjectName = 'undefined';
      this.store.setTumblebugProjectName(this.tumblebugProjectName);
      this.tumblebugUsertoken = 'Null';
      this.store.setTumblebugUserToken(this.tumblebugUsertoken);
    }
  }
}
</script>

<style>

</style>
