<template>
  <!-- fluid of b-container property full width -->
  <b-container
    id="wrapper"
    class="wrapper-alert-page"
    fluid>
    <fragment>
      <mcmp-base-header/>
      <base-title class="base-title">
        <b-row
          no-gutters
          align-v="center">
          <span class="font-16 -mr-1 bold">{{ '청구서 알림' }}</span>
          <base-material
            id="alarmListTooltip"
            :size="16"
            color="gray-1"
            name="info"
          />
          <b-tooltip
            target="alarmListTooltip"
            placement="right"
          >
            <span
              class="tooltip-info">
              <div
                style="text-align:left">
                {{ '알림을 받고자 하는 채널의 id와 챗 봇의 OAuth token 값을 입력하고 저장하면 이상비용 발생 시 해당 채널로 알림을 받을 수 있습니다.' }}
              </div>
            </span>
          </b-tooltip>
        </b-row>
      </base-title>
    </fragment>
    <b-row
      class="mt-20"
      no-gutters/>
    <BaseLeftMenu >
      <template v-slot:mainRight>
        <div>
          <b-row
            no-gutters
            align-v="center"
            class="-mt-5"
            style="margin-bottom: 48px">
            <Alarm/>
          </b-row>
        </div>
      </template>
    </BaseLeftMenu>
  </b-container>
</template>

<script>
import BaseLeftMenu from '@/components/common/base-left-menu/BaseLeftMenu';
import McmpBaseHeader from '@/components/common/base-header/McmpBaseHeader';
import Alarm from "@/components/pages/alert/slack/Alarm";

export default {
  name: 'AbnormalList',
  components: {
    BaseLeftMenu,
    // BaseHeader,
    McmpBaseHeader,
    Alarm
  },
  directives: {
  },
  inheritAttrs: false,
  props: {
  },
  data() {
    return {
    }
  },
  computed: {
  },
  watch: {
  },
  mounted() {
  },
  created() {
  },
  methods: {
  }
};

</script>

<style lang="scss">
.wrapper-alert-page {
  div {
    font-size: 12px;
    text-align: left;
  }
  .decoration{
    text-decoration: underline;
  }
}
</style>
