<template>
  <fragment >
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <base-icon
          :original="true"
          width="20"
          height="20"
          name="icon_ai" />
        <span>AI Rule 이상 비용 탐지 서비스란?</span>
      </p>
      <p class="-description -color-darkgray-1">클라우드 사용량과 비용을 머신러닝 알고리즘을 통해 실시간 분석하여, 평소와 다른 사용 패턴을 즉시 탐지하고 사용자가 설정한 AI 탐지 민감도에 따라 알람을 발송하는 서비스입니다. </p>
    </b-row>
    <b-form
      v-if="show"
      class="edit-abnormal-user-form"
      @submit="onSubmit"
      @reset="onReset"
    >
      <div class="-label -pb-5">
        <p class="-text">위젯 이름</p>
        <input
          type="text"
          class="-input w-100"
          placeholder="User Rule 이상 비용 탐지">
      </div>

      <div class="-pb-5">
        <div class="-label">
          <p class="-text">클라우드 서비스</p>
        </div>
        <b-row no-gutters>
          <input
            id="radio8"
            type="radio"
            class="-check"
            checked>
          <label for="radio8">
            <em class="-label -wrapper-v-center">
              <span class="ci ci-vendor-aws"/>
              <span class="-ml-1">AWS</span>
            </em>
          </label>
          <input
            id="radio9"
            type="radio"
            class="-check -ml-4">
          <label for="radio9">
            <em class="-label -wrapper-v-center">
              <span class="ci ci-vendor-gcp medium-"/>
              <span class="-ml-1">GCP</span>
            </em>
          </label>
          <input
            id="radio10"
            type="radio"
            class="-check -ml-4">
          <label for="radio10">
            <em class="-label -wrapper-v-center">
              <span class="ci ci-vendor-azure"/>
              <span class="-ml-1">Azure</span>
            </em>
          </label>
        </b-row>
      </div>

      <b-form-group
        id="cloud-service-group"
        :label="$t('dashboard.abnormalChange.cloudService')"
        label-for="selectVendor">
        <b-form-select
          id="selectVendor"
          v-model="selectedVendorByOption"
          :options="vendorOptions"
          required
          @change="setViewByOptions"/>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.viewBy') }}</div>
      <b-form-group
        id="view-by-group"
        label-for="view-by">
        <b-form-select
          id="view-by"
          v-model="draftWidgetConfig.viewBy"
          required>
          <option
            v-for="(option,index) in viewByOptions"
            :key="index"
            :value="option.value">{{ option.text }}</option>
        </b-form-select>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.dateRange') }}</div>
      <b-form-group
        id="time-frame-group"
        label-for="time-frame">
        <b-form-select
          id="set-time-frame"
          v-model="draftWidgetConfig.timeFrame"
          required
          @change="setColIdOptions"
        >
          <option
            v-for="(option,index) in timeFrameOptions"
            :key="index"
            :value="option.value">{{ option.text }}</option>
        </b-form-select>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.change') }}</div>
      <b-form-group
        id="change-group"
        label-for="change-label">
        <b-form-select
          id="change-label"
          v-model="draftWidgetConfig.threshold"
          required
        >
          <option
            v-for="(option,index) in thresholdOptions"
            :key="index"
            :value="option.value">{{ $t('dashboard.abnormalChange.change') + option.text }}</option>
        </b-form-select>
      </b-form-group>


      <div class="-pb-5">
        <div class="-label">
          <p class="-text">민감도
            <base-material
              :id="abnormalChangeAlarmTooltipIcon"
              class="edit-abnormal-change-tooltip"
              color="gray-1"
              name="info"
            />
          </p>
        </div>
        <div class="-wrapper fitting- nowrap-">
          <div class="-border -p-3 -wrapper-raius">
            <input
              id="radio3"
              type="radio"
              class="-check">
            <label for="radio3">
              <em class="-label">Low</em>
            </label>
            <div class="sensitivity low"/>
          </div>
          <div class="-border -p-3 -mx-2 -wrapper-raius">
            <input
              id="radio4"
              type="radio"
              class="-check"
              checked>
            <label for="radio4">
              <em class="-label">Middle</em>
            </label>
            <div class="sensitivity middle"/>
          </div>
          <div class="-border -p-3 -wrapper-raius">
            <input
              id="radio5"
              type="radio"
              class="-check">
            <label for="radio5">
              <em class="-label">High</em>
            </label>
            <div class="sensitivity high"/>
          </div>
        </div>
        <b-row
          no-gutters
          align-h="center"
          class="-pt-4">
          <p class="-wrapper-v-center legend01">민감도</p>
          <p class="-wrapper-v-center legend02 -pl-4">예측비용</p>
          <p class="-wrapper-v-center legend03 -pl-4">실제비용</p>
          <p class="-wrapper-v-center legend04 -pl-4">이상비용</p>
        </b-row>
      </div>


      <div class="-pb-5">
        <div class="-label">
          <p class="-text">비용 알람 구간
            <base-material
              :id="abnormalChangeAlarmTooltipIcon"
              class="edit-abnormal-change-tooltip"
              color="gray-1"
              name="info"
            />
          </p>
        </div>
        <b-row
          no-gutters
          align-h="between">
          <b-col cols="4">
            <div class="-wrapper-column-h-center">
              <p class="-pb-2 -color-green-1">Minor</p>
              <div class="-bg-green-1 -pb-1 w-100 -wrapper-raius"/>
            </div>
          </b-col>
          <b-col cols="4">
            <div class="-wrapper-column-h-center middle-line">
              <p class="-pb-2 -color-yellow-1">Normal</p>
              <div class="-bg-yellow-1 -pb-1 w-100"/>
            </div>
          </b-col>
          <b-col cols="4">
            <div class="-wrapper-column-h-center">
              <p class="-pb-2 -color-red-1">Critical</p>
              <div class="-bg-red-1 -pb-1 w-100 -wrapper-raius"/>
            </div>
          </b-col>
        </b-row>
        <b-row
          no-gutters
          align-h="center"
          class="alram-input -pt-4">
          <input
            type="text"
            class="-input -mr-7"
            placeholder="$ 20,000.00">
          <input
            type="text"
            class="-input"
            placeholder="$ 60,000.00">
        </b-row>
        <div class="-bg-lightgray-4 -p-4 -mt-5">
          <p class="-description">- 최소 7일 이상 서비스 사용 후, AI Rule적용이 가능합니다.</p>
          <p class="-description">- 지난 14일간(최초설정 시 7일간) 사용패턴을 분석하여, 모델을 생성합니다.</p>
          <p class="-description">- 매주 사용패턴을 분석하여, 재학습합니다.</p>
        </div>
      </div>

      <b-row
        no-gutters
        class="-border-top -py-5"
        align-h="between"
        align-v="center">
        <p>이상 탐지 시 알람 발송</p>
        <p class="-wrapper-v-center">
          <input
            id="toggle"
            type="checkbox"
            name=""
            value=""
            class="-toggle">
          <label
            for="toggle"
            class="-m-0"/>
        </p>
      </b-row>


      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.sortBy') }}</div>
      <b-form-group class="sort-setting-group">
        <div class="-wrapper nowrap-">
          <b-form-select
            v-model="draftWidgetConfig.colId"
            :value="internalSortColId"
            :options="sortByOptions"
            class="sort-by-options -mr-2"
            @change="onChangeColId"
          />
          <b-form-select
            v-model="draftWidgetConfig.sortType"
            :value="internalSortType"
            :options="sortTypeOptions"
            class="sort-type-options"
            @change="onChangeSortType"
          />
        </div>

      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">
        {{ $t('dashboard.abnormalChange.saveColumnStatus') }}
        <base-material
          :id="abnormalChangeColumnSaveTooltipIcon"
          class="edit-abnormal-change-tooltip"
          color="gray-1"
          name="info"
        />
        <b-tooltip
          :target="abnormalChangeColumnSaveTooltipIcon"
        >
          <span class="font-12">
            {{ $t('dashboard.abnormalChange.abnormalChangeColumnSaveTooltip') }}
          </span>
        </b-tooltip>
      </div>
      <b-form-group>
        <b-form-checkbox
          v-model="internalSaveColumn"
          :true-value="draftWidgetConfig.isColumnSave"
          name="checkbox-options"
          @change="onChangeSaveColumn"
        >
          {{ $t('dashboard.abnormalChange.save') }}
        </b-form-checkbox>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">
        {{ $t('dashboard.abnormalChange.alarm') }}
        <base-material
          :id="abnormalChangeAlarmTooltipIcon"
          class="edit-abnormal-change-tooltip"
          color="gray-1"
          name="info"
        />
        <b-tooltip
          :target="abnormalChangeAlarmTooltipIcon"
        >
          <span class="font-12">
            {{ $t('dashboard.abnormalChange.abnormalChangeNotiTooltip') }}
          </span>
        </b-tooltip>
      </div>
      <b-form-group>
        <b-button-group
          size="sm">
          <b-button
            :pressed="draftWidgetConfig.isAbnormalNotiOn === ABNORMAL_NOTIFICATION.NOTIFICATION_OFF"
            class="custom-color blue-1 icon-only box-shadow-none time-frame-button left"
            variant="outline-gray-4"
            @click="onChangeNotiStatus(ABNORMAL_NOTIFICATION.NOTIFICATION_OFF)"
          >
            {{ $t('dashboard.abnormalChange.notiOff') }}
          </b-button>
          <b-button
            :pressed="draftWidgetConfig.isAbnormalNotiOn === ABNORMAL_NOTIFICATION.NOTIFICATION_ON"
            class="custom-color blue-1 icon-only box-shadow-none time-frame-button right"
            variant="outline-gray-4"
            @click="onChangeNotiStatus(ABNORMAL_NOTIFICATION.NOTIFICATION_ON)"
          >
            {{ $t('dashboard.abnormalChange.notiOn') }}
          </b-button>
        </b-button-group>
      </b-form-group>
      <div class="float-right">
        <b-button
          variant="outline-secondary"
          class="cancel-button font-family-notosanscjkkr-medium"
          @click="onClickCancelButton"
        >{{ $t('dashboard.topCost.cancel') }}</b-button>
        <b-button
          type="submit"
          variant="primary"
          class="font-family-notosanscjkkr-medium"
        >{{ saveButtonLocalization }}</b-button>
      </div>
    </b-form>
  </fragment>
</template>

<script>
  /* eslint-disable no-param-reassign */

  import {
    ABNORMAL_TIME_FRAME,
    ABNORMAL_TIME_FRAME_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS_NO_REGION,
    DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
    THRESHOLD_DELTA_OPTIONS,
    VIEW_MODE,
    GRID_SORT_TYPE_OPTIONS,
    ABNORMAL_GRID_COLUMNS_LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE,
    ABNORMAL_GRID_COLUMNS_LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE,
    ABNORMAL_GRID_COLUMNS_LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS,
    ABNORMAL_GRID_COLUMNS_THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH,
    ABNORMAL_VIEW_BY_VENDORS,
    ABNORMAL_NOTIFICATION
  } from '@/constants/dashboardConstants';
  import { getSelectedVendorsByWidget, availableVendors } from "@/util/dashboardUtils";
  import _cloneDeep from 'lodash/cloneDeep';
  import _isEmpty from 'lodash/isEmpty';
  import {DEFAULT_EXCHANGE_RATE, DEFAULT_VENDOR_OPTIONS} from '@/constants/constants';
  import _isEqual from "lodash/isEqual";

  export default {
    name: 'EditAbnormalUserForm',
    props: {
      commonUserInfo: {
        type: Object,
        required: true
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      widgetConfig: {
        type: Object,
        required: true,
        default() {
          return DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG
        }
      },
      dashboardViewMode: {
        type: String,
        required: true,
        default: VIEW_MODE.DEFAULT
      },
    },
    data() {
      return {
        vendorOptions: availableVendors(ABNORMAL_VIEW_BY_VENDORS, this),
        draftWidgetConfig: DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
        abnormalChangeColumnSaveTooltipIcon: `abnormal-change-column-save-tooltip-icon-${this.widgetConfig.index}`,
        abnormalChangeAlarmTooltipIcon: `abnormal-change-alarm-tooltip-icon-${this.widgetConfig.index}`,
        viewByOptions: DASHBOARD_VIEW_BY_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        timeFrameOptions: ABNORMAL_TIME_FRAME_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        thresholdOptions: THRESHOLD_DELTA_OPTIONS,
        show: true,
        sortByOptions: ABNORMAL_GRID_COLUMNS_LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        sortTypeOptions: GRID_SORT_TYPE_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          };
        }),
        ABNORMAL_NOTIFICATION: ABNORMAL_NOTIFICATION
      }
    },
    computed:{
      saveButtonLocalization: function() {
        return this.dashboardViewMode === VIEW_MODE.DEFAULT ? this.$t('dashboard.dashboardHeader.save') : this.$t('dashboard.dashboardHeader.apply')
      },
      internalSortColId: {
        get() {
          return this.draftWidgetConfig.colId;
        },
        set(selectedColId) {
        }
      },
      internalSortType: {
        get() {
          return this.draftWidgetConfig.sortType;
        },
        set(selectedSortType) {
        }
      },
      internalSaveColumn: {
        get() {
          return this.draftWidgetConfig.isColumnSave;
        },
        set(isColumnSave) {
        }
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.draftWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS);
        },
        set(selectedVendorByOption) {
          this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [selectedVendorByOption]);
        }
      }
    },
    watch: {
      widgetConfig: {
        handler: function() {
          this.draftWidgetConfig = _cloneDeep(this.widgetConfig);
          if (_isEmpty(this.draftWidgetConfig.colId) && _isEmpty(this.draftWidgetConfig.sortType)) {
            this.draftWidgetConfig.colId = 'increaseDecreaseRate';
            this.draftWidgetConfig.sortType = 'desc';
          }
          this.setColIdOptions(this.draftWidgetConfig.timeFrame);
          this.setViewByOptions(this.draftWidgetConfig.selectedVendorsByWidget[0]);
        },
        immediate: true
      }
    },
    mounted() {
    },
    methods: {
      onSubmit(evt) {
        evt.preventDefault();
        if (this.draftWidgetConfig.isAbnormalNotiOn) {
          this.$set(this.draftWidgetConfig, 'cmpnNm', this.$store.state.loginUser.curCmpnNm);
          this.$set(this.draftWidgetConfig, 'userLangCd', this.$store.state.loginUser.userLangCd);
        }
        this.$emit('save', this.draftWidgetConfig);
      },
      onReset(evt) {
        evt.preventDefault()
      },
      onClickCancelButton() {
        this.$emit('hideModal');
      },
      setColIdOptions(timeFrame) {
        switch (timeFrame) {
          case ABNORMAL_TIME_FRAME.LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE: {
            this.sortByOptions = ABNORMAL_GRID_COLUMNS_LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE.map(option => {
              return {
                ...option,
                text: this.$t(option.text)
              };
            });
            break;
          }
          case ABNORMAL_TIME_FRAME.LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE: {
            this.sortByOptions = ABNORMAL_GRID_COLUMNS_LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE.map(option => {
              return {
                ...option,
                text: this.$t(option.text)
              };
            });
            break;
          }
          case ABNORMAL_TIME_FRAME.LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS: {
            this.sortByOptions = ABNORMAL_GRID_COLUMNS_LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS.map(option => {
              return {
                ...option,
                text: this.$t(option.text)
              };
            });
            break;
          }
          case ABNORMAL_TIME_FRAME.THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH: {
            this.sortByOptions = ABNORMAL_GRID_COLUMNS_THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH.map(option => {
              return {
                ...option,
                text: this.$t(option.text)
              };
            });
            break;
          }
        }
      },
      onChangeColId(colId) {
        this.draftWidgetConfig.colId = colId;
      },
      onChangeSortType(sortType) {
        this.draftWidgetConfig.sortType = sortType;
      },
      onChangeSaveColumn(saveColumn) {
        if (saveColumn) {
          this.draftWidgetConfig.isColumnSave = true;
        } else {
          this.draftWidgetConfig.isColumnSave = false;
        }
      },
      setViewByOptions(vendor) {
        if (_isEqual("GCP", vendor)) {
          this.viewByOptions = DASHBOARD_VIEW_BY_OPTIONS_NO_REGION.map(option => {
            return {
              ...option,
              text: this.$t(option.text)
            };
          });
        } else {
          this.viewByOptions = DASHBOARD_VIEW_BY_OPTIONS.map(option => {
            return {
              ...option,
              text: this.$t(option.text)
            };
          });
        }
      },
      onChangeNotiStatus(notiStatus) {
        this.draftWidgetConfig.isAbnormalNotiOn = notiStatus;
      }
      // availableVendors() {
      //   return ABNORMAL_VIEW_BY_VENDORS.filter(option =>this.allVendors().includes(option.value)).map(vendor => {
      //     return {
      //       ...vendor,
      //       text: this.$t(vendor.text)
      //     };
      //   });
      // },
      // allVendors(){
      //   // return ['AWS', 'GCP' , 'AZURE'];
      //   let curCmpnId = this.$store.state.loginUser.curCmpnId;
      //   let vendorInfo = this.$store.state.vendorInfo;
      //   if(curCmpnId && vendorInfo &&vendorInfo.length > 0){
      //     return vendorInfo.map(option => {
      //       //return this.$t(option.cloudVndrId).toUpperCase();
      //       return option;
      //     });
      //   }else{
      //     return [''];
      //   }
      // },
      // isSelectedVendorsNotEmpty($vm) {
      //   return !_isEmpty($vm.selectedVendorsByWidget) && !_isEmpty($vm.selectedVendorsByWidget[0]);
      // },
      // setSelectedVendors() {
      //   if (this.isSelectedVendorsNotEmpty(this.draftWidgetConfig)) {
      //     this.selectedVendor = this.draftWidgetConfig.selectedVendorsByWidget[0];
      //     return this.draftWidgetConfig.selectedVendorsByWidget[0];
      //   } else {
      //     this.selectedVendor = this.commonUserInfo.selectedVendors[0];
      //     return this.commonUserInfo.selectedVendors[0];
      //   }
      // }
    },
  };
</script>

<style lang="scss">

  .sensitivity{
    background:url('/static/svg/ml_chart_low.svg') no-repeat;
    width:117px;
    height:89px;
    background-size:100% 100%;
    background-position: center center;
    &.middle{
      background:url('/static/svg/ml_chart_middle.svg') no-repeat;
    }
    &.high{
      background:url('/static/svg/ml_chart_high.svg') no-repeat;
    }
  }

  .alram-input{
    position: relative;
    z-index: z('default');
    input{
      width:120px;
      background:white;
    }
  }

  .middle-line{
    position:relative;
    &:before{
      content:'';
      border:1px dashed #D3D8E0;
      position:absolute;
      top:6px;
      left:-1px;
      height:50px;
    }
    &:after{
      content:'';
      border:1px dashed #D3D8E0;
      position:absolute;
      top:6px;
      right:-1px;
      height:50px;
    }
  }

  .legend01{
    &:before{
      content:'';
      display:inline-flex;
      margin-right:8px;
      width:24px;
      height:10px;
      background:url('/static/svg/ml_chart_legend_01.svg') no-repeat;
      background-size:100% 100%;
      background-position: center center;
    }
  }

  .legend02{
    &:before{
      content:'';
      display:inline-flex;
      margin-right:8px;
      width:24px;
      height:1px;
      background:url('/static/svg/ml_chart_legend_02.svg') no-repeat;
      background-size:100% 100%;
      background-position: center center;
    }
  }
  .legend03{
    &:before{
      content:'';
      display:inline-flex;
      margin-right:8px;
      width:24px;
      height:5px;
      background:url('/static/svg/ml_chart_legend_03.svg') no-repeat;
      background-size:100% 100%;
      background-position: center center;
    }
  }
  .legend04{
    &:before{
      content:'';
      display:inline-flex;
      margin-right:8px;
      width:24px;
      height:12px;
      background:url('/static/svg/ml_chart_legend_04.svg') no-repeat;
      background-size:100% 100%;
      background-position: center center;
    }
  }

  .edit-abnormal-user-form {
    #view-by-group {
      position: relative;

      #view-by {
        &:focus {
          box-shadow: none !important;
        }
      }
    }

    #time-frame-group,
    .sort-setting-group{
      position: relative;

      #set-time-frame {
        &:focus {
          box-shadow: none !important;
        }
      }
    }

    .cancel-button {
      margin-right: 6px;
    }

    .edit-abnormal-change-tooltip {
      font-size: 10px;
      height: 10px;
      cursor: pointer;
      position: relative;
      top: 3px;
    }
  }
</style>
