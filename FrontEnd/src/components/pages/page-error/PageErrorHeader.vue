<template>
  <div class="page-error-header">
    <b-container
      fluid>
      <fragment>
        <mcmp-base-header
          @selectUserFilter="onChangeUserFilter"
        />
      </fragment>
    </b-container>
  </div>
</template>
<script>
  import {CURRENCY, CURRENCY_SYMBOL, DEFAULT_VENDOR_OPTIONS } from '@/constants/constants';
  import {VENDOR_NAMES} from "@/constants/billingConstants";
  import { formatCost } from '@/util/costUtils';
  // import BaseHeader from '@/components/common/base-header/BaseHeader';
  import McmpBaseHeader from '@/components/common/base-header/McmpBaseHeader';
  import { getFullDateFormatByLocalization } from "@/util/dateTimeUtils";

  export default {
    name: 'BillingHeader',
    components: {
      // BaseHeader,
      McmpBaseHeader
    },
    props: {
      isSidebarActive: {
        type: Boolean,
        default: false
      },
    },
    data() {
      return {
      }
    },
    computed: {
    },
    watch: {
    },
    methods: {
      onChangeUserFilter(userFilterIdx) {
        this.$emit('changeUserFilter', userFilterIdx)
      },
    }
  };
</script>
<style lang="scss">
  .page-error-header {
  }
</style>
