<template>
  <svgicon
    :class="{
      [$options.name]: true,
      [`${positioning}`]: positioning,
    }"
    :name="name"
    :original="original"
    :width="width"
    :height="height"
    :color="color"
  />
</template>

<script>
export default {
  name: 'BaseIcon',
  props: {
    name: {
      type: String,
      required: false,
      default: ''
    },
    color: {
      type: String,
      required: false,
      default: ''
    },
    width: {
      type: String,
      required: false,
      default: ''
    },
    height: {
      type: String,
      required: false,
      default: ''

    },
    original: {
      type: Boolean,
      default : false
    },
    positioning: {
      type: String,
      required: false,
      default: ''

    }
  }
};
</script>

<style lang="scss">
@import "../../../assets/css/base/var";
@import "../../../assets/css/base/function";
.svg-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    color: inherit;
    vertical-align: middle;
    fill: none;
    stroke: currentColor;
    &.before{
      margin-right:rem(4);
    }
    &.after{
      margin-left:rem(4);
    }
}

.svg-fill {
    fill: currentColor;
    stroke: none;
}

.svg-up {
    /* default */
    transform: rotate(0deg);
}

.svg-right {
    transform: rotate(90deg);
}

.svg-down {
    transform: rotate(180deg);
}

.svg-left {
    transform: rotate(-90deg);
}

</style>
