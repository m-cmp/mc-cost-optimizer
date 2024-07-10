<template>
  <div class="slider-container">
    <div
        :style="{ transform: `translateX(-${currentIndex * 100}%)` }"
        class="slides">
      <div
          v-for="(item, index) in items"
          :key="index"
          class="slide">
        <div class="caption">{{ index + 1 }}</div>
        <img
            :src="item.src"
            :alt="`Slide ${index + 1}`">
        <div v-html="comments[index]" />
      </div>
    </div>
    <button
        class="arrow-btn left"
        @click="prevImage">
      <img
          src="@/assets/images/slack/previous-icon.png"
          alt="Left Arrow"
      >
    </button>
    <button
        class="arrow-btn right"
        @click="nextImage">
      <img
          src="@/assets/images/slack/next-icon.png"
          alt="Right Arrow"
      >
    </button>
  </div>
</template>

<script>
export default {
  props: {
    items: {
      type: Array,
      default: function() {
        return [];
      }
    },
    comments: {
      type: Array
    }
  },
  data() {
    return {
      currentIndex: 0,
    };
  },
  methods: {
    nextImage() {
      this.currentIndex = (this.currentIndex + 1) % this.items.length;
    },
    prevImage() {
      this.currentIndex = (this.currentIndex + this.items.length - 1) % this.items.length;
    }
  }
}
</script>

<style scoped>
.slider-container {
  width: 100%;
  overflow: hidden;
}

.slides {
  display: flex;
  transition: transform 0.3s ease-in-out;
}
.arrow-btn {
  position: absolute;
  top: 40%;
  transform: translateY(-50%);
  background: none;
  border: none;
  cursor: pointer;
  z-index: 10;
  width: 40px;
  height: 40px;
}
.arrow-btn.left {
  left: 10px;
}
.arrow-btn.right {
  right: 10px;
}
.slide {
  /*display: flex;*/
  justify-content: center;
  align-items: center;
  flex: 0 0 100%;
  width: 100%;
  position: relative;
}

img {
  width: 100%;
  height: auto;
  display: block;
}

.caption {
  position: absolute;
  top: 10px;
  left: 10px;
  color: white;
  background-color: rgba(0, 0, 0, 0.5);
  padding: 10px;
  border-radius: 5px;
}
</style>
