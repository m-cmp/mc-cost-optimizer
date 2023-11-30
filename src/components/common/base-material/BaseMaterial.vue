<template>
  <span
    :class="[
      'material-icons',
      iconColor ? 'color-' + color : '',
      iconwithText ? positioning : ''
    ]"
    :style="{ fontSize: `${ size || 16 }px` }"

  >{{ `${iconName ? name : null}` }}
  </span>
</template>

<script>
export default {
  name: 'BaseMaterial',
  props: {
      name: {
        type: String,
        required: false,
        default: ''
      },
      color:{
        type: String,
        required: false,
        default: ''
      },
      positioning : {
        type: String,
        required: false,
        default: ''
      },
      size: {
        type:Number,
        required: false,
        default: 0
      }
    },
  data() {
      return {
      }
    },
  computed: {
      iconName() {
        return this.name;
      },
      iconColor() {
        return this.color;
      },
      iconSize() {
        return this.size;
      },
      iconwithText() {
        return this.positioning;
      }
    }

};
</script>

<style lang="scss">
  @import "../../../assets/css/base/var";
  @import "../../../assets/css/base/function";

  .material-icons{
    &.before{
      margin-right:rem(4);
    }
    &.after{
      margin-left:rem(4);
    }
  }
</style>
