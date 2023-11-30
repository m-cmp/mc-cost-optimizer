import Vue from 'vue';

export default Vue.extend({
  data: function () {
    return{
      ascSort: null,
      descSort: null,
      noSort: null
    };
  },beforeMount() {
  },
  mounted() {
  },
  template: `
    <div>
     <!--<div class=""> <i class="fa" :class="params.menuIcon"></i></div>-->
     <div>{{params.displayName}}</div>
     <div class="custom-tooltip-left"  ></div>
    </div>
  `,



})
