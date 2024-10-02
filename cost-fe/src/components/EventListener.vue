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
    }
  },
  mounted() {
    window.removeEventListener('message', this.handleMessage);
    window.addEventListener('message', this.handleMessage);
  },
  beforeUnmount() {
    // 컴포넌트가 파괴되기 전에 이벤트 리스너를 제거해 중복 발생 방지
    window.removeEventListener('message', this.handleMessage);
  },
  methods: {
    handleMessage(event){
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
  }
}
</script>

<style>

</style>
