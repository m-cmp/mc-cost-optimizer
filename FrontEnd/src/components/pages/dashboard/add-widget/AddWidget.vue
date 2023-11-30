<template>
  <div id="add-widget-wrapper">
    <div
      v-for="(widget, index) of widgetOptions"
      :key="index"
    >
      <b-row class="custom-add-widget">
        <b-button
          :id="'widget-' + index"
          variant="transparent"
          @mouseover="onMouseOverPreviewWidgetBtn(index)"
          @mouseout="onMouseOutPreviewWidgetBtn()">
          <base-material
            :size="22"
            :color="activeMaterialEyeIndex === index ? 'blue-1' : 'gray-1'"
            name="remove_red_eye"/>
          <span class="custom-name-widget pl-2">{{ $t(widget.text) }}
            <p
              v-if="isMultiVendorSupport(widget.value)"
              class="-tag gray-"
              style="width:63px; margin-left:5px;">
              <img
                src="static/images/icon/ico_multicloud.png">
              Multi</p>
          </span>
        </b-button>
        <b-col class="custom-col-add-button">
          <b-button
            :disabled="canShowAddedLabel && addedWidgetOptionIndex === index"
            :class="(canShowAddedLabel && addedWidgetOptionIndex === index) ? 'custom-added-label' : ''"
            variant="transparent"
            class="only-button custom-color text-blue-1 custom-add-button"
            @click="onClickAddWidgetBtn(widget.value, widget.text, index)"
          >
            <span v-if="addedWidgetOptionIndex === index && canShowAddedLabel">
              {{ $t('dashboard.addWidget.added') }}
            </span>
            <span
              v-else
              class="font-family-notosanscjkkr-medium"
            >
              {{ $t('dashboard.addWidget.add') }}
            </span>
          </b-button>
        </b-col>
      </b-row>
      <div :id="customPopoverContainer"/>
      <b-popover
        :target="'widget-' + index"
        :container="customPopoverContainer"
        placement="right"
        triggers="hover">
        <div :id="`${widget.value}-${index}`">
          <div class="preview-widget-wrapper">
            <div :class="`preview-widget-img ${convertStringToKebabCase(widget.value)}`">
              <img
                :src="`/static/images/preview-widgets/${widget.value}.png`"
              >
            </div>
          </div>
        </div>
      </b-popover>
    </div>
  </div>
</template>

<script>
/* eslint-disable no-param-reassign */
import {
  DASHBOARD_DEFAULT_WIDGET,
  DASHBOARD_WIDGET_TYPE,
  DASHBOARD_WIDGET_TYPE_OPTIONS
} from '@/constants/dashboardConstants'
import _kebabCase from 'lodash/kebabCase';
import _isEmpty from 'lodash/isEmpty'
import {VENDOR} from "../../../../constants/constants";
import {INTEGRATED_CATEGORY_VIEW_BY} from "../../../../constants/dashboardConstants";
import _isEqual from "lodash/isEqual";

export default {
  name: 'AddWidgetForm',
  components: {
  },
  props: {
    currentDashboard: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      widgetOptions: DASHBOARD_WIDGET_TYPE_OPTIONS,
      DASHBOARD_WIDGET_TYPE: DASHBOARD_WIDGET_TYPE,
      activeMaterialEyeIndex: null,
      customPopoverContainer: 'custom-popover-container',
      test: false,
      COLUMN_MAX_SIZE: 24,
      isAddedLabel: this.$t('dashboard.addWidget.isAdded!'),
      addedWidgetOptionIndex: null,
      canShowAddedLabel: false,
      multiVendors: [DASHBOARD_WIDGET_TYPE.INTEGRATED_PRODUCT_PORTION_WIDGET, DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET
        ,DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET
      ]
    }
  },
  computed:{
    isScmp:{
      get(){
        let siteId = this.$store.state.loginUser.siteCd; // WL 적용 전까지는 글로벌만 확인 가능
        if(siteId === 'SCMP'){
          return true;
        }else{
          return false;
        }
      }
    },
    isAiWidgetAvailable: {
      get() {
        const siteId = this.$store.state.loginUser.siteCd;
        return (_isEqual(siteId, "BESPIN") && this.profile.env != "CHINA")
        || _isEqual(siteId, "MCMPDEMO")
        || _isEqual(siteId, "SKT-CT") ? true : false;
      }
    }
  },
  mounted() {
    if(!this.isAiWidgetAvailable){
      this.widgetOptions = this.widgetOptions.filter(widgetType => widgetType.value !== DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET)
    }
    if(this.profile.env == 'CHINA'){
      this.widgetOptions = this.widgetOptions.filter(widgetType => widgetType.value !== DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET)
    }
    if(this.profile.env == 'CHINA' || this.profile.env == 'MEA' || this.profile.env == 'USA' || this.profile.env == 'SCMP' ) {
      this.widgetOptions = this.widgetOptions.filter(widgetType => widgetType.value !== DASHBOARD_WIDGET_TYPE.DASHBOARD_YEAR_COST_FCST_WIDGET)
    }
  },
  methods: {
    isMultiVendorSupport(vendorType) {
      return this.multiVendors.includes(vendorType);
    },
    convertStringToKebabCase(string) {
      return _kebabCase(string);
    },
    onMouseOverPreviewWidgetBtn(index) {
      this.activeMaterialEyeIndex = index
    },
    onMouseOutPreviewWidgetBtn() {
      this.activeMaterialEyeIndex = -1
    },
    onClickAddWidgetBtn(widgetType, widgetText, index) {
      let widgetLabel = this.$t(widgetText)
      let maxYWidgets = [this.currentDashboard.widgets[0]];
      let maxXYWidgets = null;
      let isDashboardEmpty = false;
      if (!_isEmpty(maxYWidgets[0])) {
        // 위젯 1개 이상 남아있을 경우
        for (let idx = 1; idx < this.currentDashboard.widgets.length; idx++) {
          if (maxYWidgets[0].y === this.currentDashboard.widgets[idx].y) {
            maxYWidgets.push(this.currentDashboard.widgets[idx]);
          } else if (maxYWidgets[0].y < this.currentDashboard.widgets[idx].y) {
            maxYWidgets = [this.currentDashboard.widgets[idx]];
          }
        }

        maxXYWidgets = [maxYWidgets[0]];
        for (let idx = 1; idx < maxYWidgets.length; idx++) {
          if (maxXYWidgets[0].x === maxYWidgets[idx].x) {
            maxXYWidgets.push(maxYWidgets[idx])
          } else if (maxXYWidgets[0].x < maxYWidgets[idx].x) {
            maxXYWidgets = [maxYWidgets[idx]]
          }
        }
      } else {
        // 대시보드에 위젯이 없을 경우
        isDashboardEmpty = true;
        maxXYWidgets = [
          {
            x: 0,
            y: 0,
            width: 0,
            height: 0
          }
        ]
      }
      let widgetSizeItem = DASHBOARD_DEFAULT_WIDGET.find(widgetSizeItem => widgetSizeItem.widgetType === widgetType)
      if (isDashboardEmpty) {
        widgetSizeItem.index = 0;
        widgetSizeItem.i = 0;
      }
      let newWidgetConfig = {
        ...widgetSizeItem
      };
      let widgetSpace = this.COLUMN_MAX_SIZE - (maxXYWidgets[0].x + maxXYWidgets[0].width)
      let widgetX = this.COLUMN_MAX_SIZE - widgetSpace
      let defaultVendor = [this.$store.state.vendorInfo != null && this.$store.state.vendorInfo.length > 0 ? this.$store.state.vendorInfo[0] : null];
      switch (widgetType) {
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_MONTH_TO_DATE_WIDGET:
          newWidgetConfig = {
            ...newWidgetConfig,
            vendors: defaultVendor,
            selectedVendorsByWidget: defaultVendor
          };
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_ESTIMATED_COST_WIDGET:
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_YEAR_COST_FCST_WIDGET:
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.COMPARE_COST_TREND_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_COST_BY_WIDGET:
          newWidgetConfig = {
            ...newWidgetConfig,
            vendors: defaultVendor,
            selectedVendorsByWidget: defaultVendor,
            isNew: true
          };
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.PRODUCT_PORTION_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.PORTION_BY_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_TOP5_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_ABNORMAL_CHANGE_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_ML_ABNORMAL_AI_WIDGET:
          newWidgetConfig.isNew = true;
          if(this.isAiWidgetAvailable){
            setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          }
          break;
        case DASHBOARD_WIDGET_TYPE.INTEGRATED_PRODUCT_PORTION_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_PORTION_BY_SERVICE_GROUP_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
        case DASHBOARD_WIDGET_TYPE.DASHBOARD_MULTI_VENDOR_COST_BY_WIDGET:
          newWidgetConfig.isNew = true;
          setWidgetConfig(newWidgetConfig, maxXYWidgets[0], widgetSpace, widgetX, this.currentDashboard.widgets)
          break;
      }
      this.$emit('addWidget', newWidgetConfig);
      this.addedWidgetOptionIndex = index
      this.canShowAddedLabel = true
      setTimeout(() => {
        this.canShowAddedLabel = false
        this.$toasted.clear()
        this.$toasted.global.showMessage({
          message: `${widgetLabel} ${this.isAddedLabel}`
        })
      }, 700)
    }
  }
};

function setWidgetConfig(newWidgetSizeConfig, maxXYWidget, widgetSpace, widgetX, widgets) {
  if (!_isEmpty(widgets)) {
    newWidgetSizeConfig.index = Math.max.apply(Math, widgets.map(function (widget) {
      return widget.index;
    })) + 1;
  }
  if (newWidgetSizeConfig.width <= widgetSpace) {
    newWidgetSizeConfig.x = widgetX
    newWidgetSizeConfig.y = maxXYWidget.y
  } else if (newWidgetSizeConfig.width > widgetSpace) {
    newWidgetSizeConfig.x = 0
    newWidgetSizeConfig.y = maxXYWidget.y + maxXYWidget.height
  }
}
</script>

<style lang="scss">
.custom-add-widget {
  border-bottom: 1px solid #D5DAE0;
  padding: 18px 0 18px 0;
  button {
    cursor: default !important;
  }
  .custom-name-widget {
    color: #222222;
    font-size: 13px;
    font-weight: normal;
  }
  .custom-col-add-button {
    padding-right: 25px;
    .custom-add-button {
      float: right;
      margin-top: 4px;
      cursor: pointer !important;
    }
    .custom-added-label {
      color: #898D94 !important;
    }
  }
}
.modal {
  &.right-wing-add-widget {
    .modal-body {
      padding-top: 0;
    }
  }
}
.preview-widget-wrapper {
  background-color: var(--white);
  .preview-widget-img {
    img {
      width: 100%;
      height: 112px;
      object-fit: contain;
    }
    &.dashboard-cost-month-to-date-widget {
      padding: 16.4px 9px 16px 8px;
      img {
        height: 112px;
      }
    }
    &.dashboard-estimated-cost-widget {
      padding: 16.4px 9px 16px 8px;
      img {
        height: 112px;
      }
    }
    &.dashboard-compare-cost-trend-widget {
      padding: 16.4px 9px 16px 8px;
      img {
        height: 112px;
      }
    }
    &.dashboard-cost-by-widget {
      padding: 16.4px 9px 16px 8px;
      img {
        height: 112px;
      }
    }
    /*&.dashboard-cost-by-widget {*/
    /*  padding: 12.9px 9px 15.9px 8px;*/
    /*  img {*/
    /*    height: 88px;*/
    /*  }*/
    /*}*/
    &.dashboard-product-portion-by-widget {
      padding: 13.4px 16px 16.1px 16px;
      img {
        height: 170px;
      }
    }
    &.dashboard-portion-by-widget {
      padding: 13.4px 16px 16.1px 16px;
      img {
        height: 170px;
      }
    }
    &.dashboard-abnormal-change-widget {
      padding: 13.9px 15px 16.1px 15px;
      img {
        height: 160px;
      }
    }
    &.dashboard-top-5-widget {
      padding: 5.3px 8px 8.1px 8px;
      img {
        height: 188px;
      }
    }
  }
}
#add-widget-wrapper {
  .popover {
    -webkit-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    -moz-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
    border: none !important;
    max-width: 400px !important;
    max-height: 100% !important;
    width: 100% !important;
    position: absolute !important;
    top: 0 !important;
    left: -386px !important;
    padding: 0 !important;
    .popover-body {
      padding: 0 !important;
    }
    .arrow {
      left: 100% !important;
      &:before {
        border-bottom: none !important;
        border-left: none !important;
      }
    }
  }
}
</style>
