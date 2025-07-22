<template>
  <div>
    <div>
      <!--Workspace ID : {{ tumblebugWorkspaceName }}, Project ID : {{ tumblebugProjectName }}, User Token : {{ tumblebugUsertoken }} -->
    </div>
    <EventListener/>
    <router-view></router-view>
  </div>

</template>

<script>
import EventListener from "@/components/EventListener.vue";
import {useSelectedOptionsStore} from "@/stores/selectedOptions";

export default {
    name: 'App',
    components: {
      EventListener
    },
    data(){
      return {
        store: useSelectedOptionsStore(),
        tumblebugWorkspaceName: null,
        tumblebugProjectName: [],
        tumblebugUsertoken: null
      }
    },
  mounted() {
    this.tumblebugWorkspaceName = this.store.selectedOptions.selectedWorksapceName
    this.tumblebugProjectName = this.store.selectedOptions.selectedProjectName
    this.tumblebugUsertoken = this.store.selectedOptions.selectedUserToekn
  },
  watch:{
    'store.selectedOptions.selectedWorksapceName': {
      handler(newVal) {
        this.tumblebugWorkspaceName = newVal
      },
      deep: true
    },
    'store.selectedOptions.selectedProjectName': {
      handler(newVal) {
        this.tumblebugProjectName = newVal
      },
      deep: true
    },
    'store.selectedOptions.selectedUserToekn': {
      handler(newVal) {
        this.tumblebugUsertoken = newVal
      },
      deep: true
    }
  },
  methods: {

  }
}
</script>

<style> 
</style>
