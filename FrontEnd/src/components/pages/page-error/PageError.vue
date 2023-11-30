<template>
  <b-container
    id="wrapper"
    class="wrapper-page-error"
  >
    <Header/>
    <b-row
      :class="{'active': isSidebarActive}"
      class="justify-content-md-center"
      no-gutters>
      <BaseLeftMenu
        @setSidebarActive="setSidebarActive">
        <template v-slot:mainRight>
          <div
            class="-wrapper-column-v-center h-center- -width-100">
            <div class="-error-img"/>
            <p class="-font-size-32 -color-darkgray-2 -font-weight-medium -pt-8 -pb-2">{{ $t('errorPage.404.title') }}</p>
            <p class="-font-size-16 -color-darkgray-2 -font-weight-medium -mb-8">{{ $t('errorPage.404.content') }}</p>
            <button
              class="-btn primary-"
              type="button"
              @click="goHome">
              <span>{{ $t('errorPage.button.goHome') }}</span>
            </button>
          </div>
        </template>
      </BaseLeftMenu>
    </b-row>
  </b-container>
</template>
<iframe id="txtArea1" style="display:none"></iframe>
<script>
  import Header from "./PageErrorHeader";
  import BaseLeftMenu from '@/components/common/base-left-menu/BaseLeftMenu';
  export default {
    name: 'PageError',
    components: {
      Header,
      BaseLeftMenu
    },
    filters:{
    },
    props: {
      isSidebarActive: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return{
      }
    },
    methods: {
      // goHome(){
      //   window.location.href = this.urls.portalUrl
      // },
      goHome(){
        window.location.href = '/dashboard'
      },
      setSidebarActive(isSidebarActive) {
        this.isSidebarActive = isSidebarActive;
        this.$emit('toggleSidebar', isSidebarActive)
      },
    }
  }
</script>
<style lang="scss">
</style>
