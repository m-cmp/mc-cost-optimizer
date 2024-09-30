<template>
  <div>
    Workspace ID : {{ tumblebugWorkspaceid }}, Project ID : {{ tumblebugProjectid }}, User Token : {{ tumblebugUsertoken }}
  </div>
<router-view></router-view>
</template>

<script>

import {useSelectedOptionsStore} from "@/stores/selectedOptions";

export default {
    name: 'App',
    components: {
    },
    data(){
      return {
        store: useSelectedOptionsStore(),
        tumblebugWorkspaceid: null,
        tumblebugProjectid: [],
        tumblebugUsertoken: null
      }
    },
  mounted() {
    this.tumblebugWorkspaceid = this.store.selectedOptions.selectedWorkspace
    this.tumblebugProjectid = this.store.selectedOptions.selectedProjects
    this.tumblebugUsertoken = this.store.selectedOptions.selectedUserToekn
  },
  watch:{
    'store.selectedOptions.selectedWorkspace': {
      handler(newVal) {
        this.tumblebugWorkspaceid = newVal
      },
      deep: true
    },
    'store.selectedOptions.selectedProjects': {
      handler(newVal) {
        this.tumblebugProjectid = newVal
      },
      deep: true
    },
    'store.selectedOptions.selectedUserToekn': {
      handler(newVal) {
        this.tumblebugUsertoken = newVal
      },
      deep: true
    }
  }
}
</script>

<style> 
</style>
