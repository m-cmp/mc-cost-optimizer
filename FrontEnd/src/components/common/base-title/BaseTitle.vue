<template>
  <b-row
    id="title"
    align-h="between"
    align-v="center"
    no-gutters
    class="pr-20">
    <slot/>
  </b-row>
</template>

<script>
export default {
  name: 'BaseTitle',
  props: {
  }
};
</script>
<style lang="scss">
@import "../../../assets/css/base/var";
@import "../../../assets/css/base/function";

#title {
  background:#fff;
  position:absolute;
  top:$header-height;
  left:0;
  right:0;
  height:$title-header;
  padding-left:$title-header;
  z-index:z('title');
  border-bottom: 1px solid #e8ebef;
}
.dropdown-menu {
  height:300px;
  overflow:hidden;
  overflow-y:auto;
}
</style>
