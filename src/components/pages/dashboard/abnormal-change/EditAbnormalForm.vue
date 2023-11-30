<template>
  <fragment >
    <b-row
      no-gutters
      class="-pb-8">
      <p class="-h3 -pb-2">
        <span class="material-icons person-icon">person</span>
        <span>{{ $t('dashboard.abnormalChange.whatIsUserRuleAnomalyDetection') }}</span>
      </p>
      <p class="-description -color-darkgray-1">{{ $t('dashboard.abnormalChange.explainUserRuleAnomalyDetection') }}</p>
    </b-row>
    <b-form
      v-if="show"
      class="edit-abnormal-form"
      @submit="onSubmit"
      @reset="onReset"
    >
      <div class="-label -pb-5">
        <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.anomalyDetectionTitle') }}</div>
        <input
          :value="draftWidgetConfig.title"
          :placeholder="$t('dashboard.abnormalChange.anomalyDetectionTitlePlaceholder')"
          :class="hasValidationError ? 'validation-error' : ''"
          :maxlength="50"
          :disabled="vendorOptions.length > 0 ? false : true"
          type="text"
          class="-input w-100 cursor-text"
          @input="setTitle($event)">
        <p
          v-if="hasValidationError"
          class="color-red-1">{{ $t('dashboard.abnormalChange.unsupportedCharacters') }}</p>
      </div>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.cloudService') }}</div>
      <b-form-group
        id="cloud-service-group"
        label-for="selectVendor">
        <b-form-radio-group
          id="selectVendor"
          v-model="selectedVendorByOption"
          :options="vendorOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          required
          @change="setViewByOptions"/>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.analyzeBy') }}</div>
      <b-form-group
        id="view-by-group"
        label-for="view-by">
        <b-form-select
          id="view-by"
          v-model="internalViewBy"
          :disabled="vendorOptions.length > 0 ? false : true"
          required>
          <option
            v-for="(option,index) in viewByOptions"
            :key="index"
            :value="option.value">{{ option.text }}</option>
        </b-form-select>
      </b-form-group>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.comparisonPeriod') }}</div>
      <b-form-group
        id="time-frame-group"
        label-for="time-frame">
        <b-form-select
          id="set-time-frame"
          v-model="draftWidgetConfig.timeFrame"
          :disabled="vendorOptions.length > 0 ? false : true"
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
          :disabled="vendorOptions.length > 0 ? false : true"
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
          <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.alertBand') }}
            <base-material
              :id="abnormalAlarmRangeTooltipIcon"
              class="edit-abnormal-change-tooltip"
              color="gray-1"
              name="info"
            />
          </div>
          <b-tooltip
            :target="abnormalAlarmRangeTooltipIcon"
          >
            <p style="text-align: left">{{ $t('dashboard.abnormalChange.userRuleAlertBandTooltip') }}</p>
          </b-tooltip>
        </div>
        <b-row
          no-gutters
          align-h="between">
          <b-col cols="4">
            <div class="-wrapper-column-h-center">
              <p class="-pb-2 -color-green-1">{{ $t('dashboard.abnormalChange.alarmLevels.minor') }}</p>
              <div class="-bg-green-1 -pb-1 w-100 -wrapper-raius"/>
            </div>
          </b-col>
          <b-col cols="4">
            <div class="-wrapper-column-h-center middle-line">
              <p class="-pb-2 -color-yellow-1">{{ $t('dashboard.abnormalChange.alarmLevels.major') }}</p>
              <div class="-bg-yellow-1 -pb-1 w-100"/>
            </div>
          </b-col>
          <b-col cols="4">
            <div class="-wrapper-column-h-center">
              <p class="-pb-2 -color-red-1">{{ $t('dashboard.abnormalChange.alarmLevels.critical') }}</p>
              <div class="-bg-red-1 -pb-1 w-100 -wrapper-raius"/>
            </div>
          </b-col>
        </b-row>
        <b-row
          no-gutters
          align-h="center"
          class="alarm-input -pt-4">
          <input
            :value="getMinAlertValue"
            :class="{'invalid-alert-band-border' : !isValidAlertBand }"
            :disabled="vendorOptions.length > 0 ? false : true"
            type="text"
            class="-input -mr-7"
            placeholder="$ 20,000.00"
            @change="setMinAlertValue">
          <input
            :value="getMaxAlertValue"
            :class="{'invalid-alert-band-border' : !isValidAlertBand }"
            :disabled="vendorOptions.length > 0 ? false : true"
            type="text"
            class="-input"
            placeholder="$ 60,000.00"
            @change="setMaxAlertValue">
        </b-row>
        <div
          v-if="!isValidAlertBand"
          :class="isValidAlertBand ? '' : 'invalid-alert-band'"
        >
          {{ $t('dashboard.abnormalChange.invalidValue') }}
        </div>
      </div>
      <div class="mb-2 font-family-notosanscjkkr-medium">{{ $t('dashboard.abnormalChange.sortBy') }}</div>
      <b-form-group class="sort-setting-group">
        <!-- BackUp -->
        <!--
        <b-form-select
          v-model="draftWidgetConfig.colId"
          :value="internalSortColId"
          :options="sortByOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          class="sort-by-options"
          @change="onChangeColId"
        />
        <b-form-select
          v-model="draftWidgetConfig.sortType"
          :value="internalSortType"
          :options="sortTypeOptions"
          :disabled="vendorOptions.length > 0 ? false : true"
          class="sort-type-options"
          @change="onChangeSortType"
        />
        -->
        <b-form-select
          v-model="draftWidgetConfig.colId"
          :value="internalSortColId"
          :options="sortByOptions"
          :disabled="true"
          class="sort-by-options"
          @change="onChangeColId"
        />
        <b-form-select
          v-model="draftWidgetConfig.sortType"
          :value="internalSortType"
          :options="sortTypeOptions"
          :disabled="true"
          class="sort-type-options"
          @change="onChangeSortType"
        />
      </b-form-group>
      <b-form-group>
        <!-- BackUp -->
        <!--
        <b-form-checkbox
          v-model="internalSaveColumn"
          :true-value="draftWidgetConfig.isColumnSave"
          :disabled="vendorOptions.length > 0 ? false : true"
          name="checkbox-options"
          @change="onChangeSaveColumn"
        >
        -->
        <b-form-checkbox
          v-model="internalSaveColumn"
          :true-value="draftWidgetConfig.isColumnSave"
          :disabled="true"
          name="checkbox-options"
          @change="onChangeSaveColumn"
        >
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
        </b-form-checkbox>
      </b-form-group>
      <b-row
        no-gutters
        class="-border-top -py-5"
        align-h="between"
        align-v="center">
        <p>{{ $t('dashboard.abnormalChange.anomalyDetectAlertSend') }}</p>
        <p class="-wrapper-v-center">
          <!-- BackUp -->
          <!--
          <input
            id="toggle"
            :checked="toggleChecked()"
            :disabled="vendorOptions.length > 0 ? false : true"
            type="checkbox"
            name=""
            value=""
            class="-toggle"
            @change="toggleOnOff()">
            -->
          <input
            id="toggle"
            :checked="toggleChecked()"
            :disabled="true"
            type="checkbox"
            name=""
            value=""
            class="-toggle"
            @change="toggleOnOff()">
          <label
            for="toggle"
            class="-m-0"/>
        </p>
      </b-row>
      <div v-if="draftWidgetConfig.isAbnormalNotiOn">
        <b-form-group
          id="alarm-condition"
          :label="$t('dashboard.abnormalChange.alertSendType')"
          label-for="selectAlarmCond">
          <b-form-radio-group
            id="selectAlarmCond"
            v-model="selectedAlarmByOption"
            :options="alarmOptions"
            required/>
        </b-form-group>
      </div>
      <b-form-group
        :hidden="isReceiverHidden('I')"
        class="mr-8"
        size="sm"
      >
        <div class="mb-2 ">{{ $t('dashboard.abnormalChange.recipient') }}</div>
        <b-form-tags
          v-model="setIntegrationReceiver"
          :placeholder="$t('dashboard.abnormalChange.addRecipient')"
          :add-button-text="$t('dashboard.abnormalChange.add')"
          :tag-validator="emailValidate"
          invalid-tag-text=""
          duplicate-tag-text=""
          input-id="tags-basic-integration-receiver"
          class="mb-2"/>
      </b-form-group>
      <b-form-group
        :hidden="isReceiverHidden('C')"
        class="mr-8"
        size="sm"
      >
        <div class="mb-2 ">{{ $t('dashboard.abnormalChange.recipient') }}</div>
        <b-row
          no-gutters
          align-m="between">
          <b-col sm="2">
            <p class="-text">{{ $t('dashboard.abnormalChange.alarmLevels.minor') }}</p>
          </b-col>
          <b-col sm="10">
            <b-form-tags
              v-model="setMinorReceiver"
              :placeholder="$t('dashboard.abnormalChange.addRecipient')"
              :add-button-text="$t('dashboard.abnormalChange.add')"
              :tag-validator="emailValidate"
              invalid-tag-text=""
              duplicate-tag-text=""
              name="minor-receiver"
              input-id="tags-basic-class-minor-receiver"
              class="mb-2"/>
          </b-col>
        </b-row>
        <b-row
          no-gutters
          align-m="between">
          <b-col sm="2">
            <p class="-text">{{ $t('dashboard.abnormalChange.alarmLevels.major') }}</p>
          </b-col>
          <b-col sm="10">
            <b-form-tags
              v-model="setNormalReceiver"
              :placeholder="$t('dashboard.abnormalChange.addRecipient')"
              :add-button-text="$t('dashboard.abnormalChange.add')"
              :tag-validator="emailValidate"
              invalid-tag-text=""
              duplicate-tag-text=""
              name="normal-receiver"
              input-id="tags-basic-class-normal-receiver"
              class="mb-2"/>
          </b-col>
        </b-row>
        <b-row
          no-gutters
          align-m="between">
          <b-col sm="2">
            <p class="-text">{{ $t('dashboard.abnormalChange.alarmLevels.critical') }}</p>
          </b-col>
          <b-col sm="10">
            <b-form-tags
              v-model="setCriticalReceiver"
              :placeholder="$t('dashboard.abnormalChange.addRecipient')"
              :add-button-text="$t('dashboard.abnormalChange.add')"
              :tag-validator="emailValidate"
              invalid-tag-text=""
              duplicate-tag-text=""
              name="critical-receiver"
              input-id="tags-basic-class-critical-receiver"
              class="mb-2"
            />
          </b-col>
        </b-row>
        <b-form-checkbox
          v-model="internalIncludeLowerLevel"
          :true-value="draftWidgetConfig.isIncludeLowerLevel"
          name="checkbox-options"
          @change="onChangeIsIncludeLowerLevel"
        >
          {{ $t('dashboard.abnormalChange.includeLowerLevel') }}
        </b-form-checkbox>
      </b-form-group>

      <div v-if="draftWidgetConfig.isAbnormalNotiOn">
        <b-form-group
          id="alarm-channel"
          :label="$t('dashboard.abnormalChange.channel')"
          label-for="selectAlarmChannel">
          <b-form-checkbox-group
            id="selectAlarmChannel"
            v-model="selectedAlarmChannelByOption"
            :options="alarmChannelOptions"
            name="checkbox-options"
          />
        </b-form-group>
      </div>
      <b-row
        no-gutters
        class="-border-top -py-5">
        <div class="float-right">
          <b-button
            variant="outline-secondary"
            class="cancel-button font-family-notosanscjkkr-medium"
            @click="onClickCancelButton"
          >{{ $t('dashboard.topCost.cancel') }}</b-button>
          <b-button
            :disabled="vendorOptions.length > 0 ? false : true || !isValidAlertBand"
            type="submit"
            variant="primary"
            class="font-family-notosanscjkkr-medium"
          >{{ saveButtonLocalization }}</b-button>
        </div>
      </b-row>
    </b-form>
  </fragment>
</template>

<script>
  /* eslint-disable no-param-reassign */

  import {
    ABNORMAL_TIME_FRAME,
    ABNORMAL_TIME_FRAME_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS,
    DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR,
    DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
    THRESHOLD_DELTA_OPTIONS,
    VIEW_MODE,
    GRID_SORT_TYPE_OPTIONS,
    ABNORMAL_GRID_COLUMNS_LATEST_3_DAYS_TOTAL_VS_3_DAYS_BEFORE,
    ABNORMAL_GRID_COLUMNS_LATEST_7_DAYS_TOTAL_VS_7_DAYS_BEFORE,
    ABNORMAL_GRID_COLUMNS_LATEST_TOTAL_VS_AVERAGE_COST_OF_LATEST_7_DAYS,
    ABNORMAL_GRID_COLUMNS_THIS_MONTH_SO_FAR_VS_SAME_PERIOD_OF_LAST_MONTH,
    ABNORMAL_VIEW_BY_VENDORS,
    ABNORMAL_NOTIFICATION,
    AI_ABNORMAL_ALARM_OPTIONS,
    AI_ABNORMAL_ALARM_CHANNEL_OPTIONS,
    AI_ABNORMAL_ALARM
  } from '@/constants/dashboardConstants';
  import { getSelectedVendorsByWidget, availableVendors } from "@/util/dashboardUtils";
  import _cloneDeep from 'lodash/cloneDeep';
  import _isEmpty from 'lodash/isEmpty';
  import {CURRENCY_SYMBOL, DEFAULT_CURRENCY, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import {calculateCostByCurrency, formatCost} from '@/util/costUtils';
  import _isEqual from "lodash/isEqual";
  import {
    email_blur
  } from '@/util/stringUtils';
  import {SELECTED_VENDOR} from "../../../../constants/dashboardConstants";

  export default {
    name: 'EditAbnormalForm',
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
        isValidAlertBand : true,
        vendorOptions: availableVendors(ABNORMAL_VIEW_BY_VENDORS, this),
        draftWidgetConfig: DEFAULT_ABNORMAL_CHANGE_WIDGET_CONFIG,
        abnormalChangeColumnSaveTooltipIcon: `abnormal-change-column-save-tooltip-icon-${this.widgetConfig.index}`,
        abnormalChangeAlarmTooltipIcon: `abnormal-change-alarm-tooltip-icon-${this.widgetConfig.index}`,
        abnormalAlarmRangeTooltipIcon: `abnormal-alarm-range-tooltip-icon-${this.widgetConfig.index}`,
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
        ABNORMAL_NOTIFICATION: ABNORMAL_NOTIFICATION,
        internalCommonUserInfo: {
          selectedCurrency: DEFAULT_CURRENCY,
          exchangeRate: DEFAULT_EXCHANGE_RATE
        },
        hasValidationError: false
      }
    },
    computed:{
      alarmOptions() {
        return AI_ABNORMAL_ALARM_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          }
        });
      },
      alarmChannelOptions() {
        return AI_ABNORMAL_ALARM_CHANNEL_OPTIONS.map(option => {
          return {
            ...option,
            text: this.$t(option.text)
          }
        });
      },
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
      internalIncludeLowerLevel: {
        get() {
          return this.draftWidgetConfig.isIncludeLowerLevel;
        },
        set(isIncludeLowerLevel) {
        }
      },
      selectedVendorByOption: {
        get() {
          return getSelectedVendorsByWidget(this.draftWidgetConfig, this, ABNORMAL_VIEW_BY_VENDORS);
        },
        set(selectedVendorByOption) {
          this.$set(this.draftWidgetConfig, 'selectedVendorsByWidget', [selectedVendorByOption]);
        }
      },
      getMinAlertValue: {
        set(alertValue){
          let alertVal = parseFloat(alertValue);
          if(!isNaN(alertVal)){
            this.draftWidgetConfig.minAlert = alertVal;
          }
        },
        get() {
          return this.cellRendererWithCurrency(this.draftWidgetConfig.minAlert, this.internalCommonUserInfo.selectedCurrency, this.internalCommonUserInfo.exchangeRate);
        }
      },
      getMaxAlertValue: {
        set(alertVal){
          this.draftWidgetConfig.maxAlert = alertVal;
        },
        get() {
          return this.cellRendererWithCurrency(this.draftWidgetConfig.maxAlert, this.internalCommonUserInfo.selectedCurrency, this.internalCommonUserInfo.exchangeRate);
        }
      },
      selectedAlarmByOption: {
        get() {
          return this.draftWidgetConfig.alarmCondition;
        },
        set(selectedAlarmByOption) {
          this.$set(this.draftWidgetConfig, 'alarmCondition', selectedAlarmByOption);
        }
      },
      selectedAlarmChannelByOption: {
        get() {
          return this.draftWidgetConfig.alarmChannel;
        },
        set(alarmChannel) {
          this.$set(this.draftWidgetConfig, 'alarmChannel', alarmChannel);
        }
      },
      setIntegrationReceiver:{
        get(){
          return this.draftWidgetConfig.mailReceivers.integration;
        },
        set(integration){
          this.$set(this.draftWidgetConfig.mailReceivers, 'integration', integration);
        }
      },
      setMinorReceiver:{
        get(){
          return this.draftWidgetConfig.mailReceivers.minor;
        },
        set(minor){
          this.$set(this.draftWidgetConfig.mailReceivers, 'minor', minor);
        }
      },
      setNormalReceiver:{
        get(){
          return this.draftWidgetConfig.mailReceivers.normal;
        },
        set(normal){
          this.$set(this.draftWidgetConfig.mailReceivers, 'normal', normal);
        }
      },
      setCriticalReceiver:{
        get(){
          return this.draftWidgetConfig.mailReceivers.critical;
        },
        set(critical){
          this.$set(this.draftWidgetConfig.mailReceivers, 'critical', critical);
        }
      },
      internalViewBy: {
        get() {
          if(_isEqual(this.draftWidgetConfig.viewBy, "project") || _isEqual(this.draftWidgetConfig.viewBy, "account")) {
            if(this.draftWidgetConfig.selectedVendorsByWidget.includes(SELECTED_VENDOR.GCP)) {
              return "project";
            } else {
              return "account";
            }
          } else {
            return this.draftWidgetConfig.viewBy;
          }
        },
        set(selectedViewBy) {
          this.$set(this.draftWidgetConfig, 'viewBy', selectedViewBy);
          this.$set(this.draftWidgetConfig, 'customFilter', []);
        }
      },
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
        if (this.hasValidationError)
          return false;
        if (this.draftWidgetConfig.isAbnormalNotiOn) {
          this.$set(this.draftWidgetConfig, 'cmpnNm', this.$store.state.loginUser.curCmpnNm);
          this.$set(this.draftWidgetConfig, 'userLangCd', this.$store.state.loginUser.userLangCd);
        }
        this.resetMailReceivers();
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
      onChangeIsIncludeLowerLevel(includeLowerLevel) {
        if (includeLowerLevel) {
          this.draftWidgetConfig.isIncludeLowerLevel = true;
        } else {
          this.draftWidgetConfig.isIncludeLowerLevel = false;
        }
      },
      setViewByOptions(selectedVendorsByWidget) {
        if(!_isEmpty(DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget])){
          this.viewByOptions = DASHBOARD_VIEW_BY_OPTIONS_BY_VENDOR[selectedVendorsByWidget].map(option => {
            return {
              ...option,
              text: this.$t(option.text)
            }
          })
        }else{
          this.viewByOptions = DASHBOARD_VIEW_BY_OPTIONS.map(option => {
            return {
              ...option,
              text: this.$t(option.text)
            };
          })
        }
      },
      onChangeNotiStatus(notiStatus) {
        this.draftWidgetConfig.isAbnormalNotiOn = notiStatus;
      },
      setTitle($event) {
        // const regex = new RegExp('^[0-9a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\u4e00-\u9fa5~!@#$%^&*()_+\\-=|<>?:{},./?;\\[\\]\\\\\"\'\x20]*$');
        const regex = /[\u{1f300}-\u{1f5ff}\u{1f900}-\u{1f9ff}\u{1f600}-\u{1f64f}\u{1f680}-\u{1f6ff}\u{2600}-\u{26ff}\u{2700}-\u{27bf}\u{1f1e6}-\u{1f1ff}\u{1f191}-\u{1f251}\u{1f004}\u{1f0cf}\u{1f170}-\u{1f171}\u{1f17e}-\u{1f17f}\u{1f18e}\u{3030}\u{2b50}\u{2b55}\u{2934}-\u{2935}\u{2b05}-\u{2b07}\u{2b1b}-\u{2b1c}\u{3297}\u{3299}\u{303d}\u{00a9}\u{00ae}\u{2122}\u{23f3}\u{24c2}\u{23e9}-\u{23ef}\u{25b6}\u{23f8}-\u{23fa}]/gu;
        if (regex.test($event.target.value)) {
          this.hasValidationError = true;
          return;
        } else {
          this.hasValidationError = false;
          this.draftWidgetConfig.title = $event.target.value;
        }
      },
      cellRendererWithCurrency(value, currency, exchangeRate) {
        if (value === '-') {
          return value;
        }
        return `${CURRENCY_SYMBOL[currency]} ${formatCost(calculateCostByCurrency(value, currency, exchangeRate))}`;
      },
      setMinAlertValue($event){
        let alertVal = parseFloat($event.target.value.replace(CURRENCY_SYMBOL.USD, '').replace(CURRENCY_SYMBOL.KRW, '').replace(CURRENCY_SYMBOL.CNY, '').replace(CURRENCY_SYMBOL.MXN, ''));
        if(!isNaN(alertVal)){
          this.draftWidgetConfig.minAlert = alertVal;
          this.checkValidAlertValue();
          return this.cellRendererWithCurrency(alertVal, this.internalCommonUserInfo.selectedCurrency, this.internalCommonUserInfo.exchangeRate);
        }
      },
      setMaxAlertValue($event){
        let alertVal = parseFloat($event.target.value.replace(CURRENCY_SYMBOL.USD, '').replace(CURRENCY_SYMBOL.KRW, '').replace(CURRENCY_SYMBOL.CNY, '').replace(CURRENCY_SYMBOL.MXN, ''));
        if(!isNaN(alertVal)){
          this.draftWidgetConfig.maxAlert = alertVal;
          this.checkValidAlertValue();
          return this.cellRendererWithCurrency(alertVal, this.internalCommonUserInfo.selectedCurrency, this.internalCommonUserInfo.exchangeRate);
        }
      },
      toggleChecked(){
        return _isEqual(this.draftWidgetConfig.isAbnormalNotiOn, ABNORMAL_NOTIFICATION.NOTIFICATION_OFF) ? '':'checked'
      },
      toggleOnOff(){
        _isEqual(this.draftWidgetConfig.isAbnormalNotiOn, ABNORMAL_NOTIFICATION.NOTIFICATION_OFF) ?
          this.draftWidgetConfig.isAbnormalNotiOn = ABNORMAL_NOTIFICATION.NOTIFICATION_ON :
          this.draftWidgetConfig.isAbnormalNotiOn = ABNORMAL_NOTIFICATION.NOTIFICATION_OFF;
      },
      isReceiverHidden(type){
        if(this.draftWidgetConfig.isAbnormalNotiOn){
          if(this.draftWidgetConfig.alarmCondition === AI_ABNORMAL_ALARM.BY_CLASS){
            if(type === AI_ABNORMAL_ALARM.BY_CLASS){
              return false
            }else if(type === AI_ABNORMAL_ALARM.BY_INTEGRATION){
              return true;
            }
          }else if(this.draftWidgetConfig.alarmCondition === AI_ABNORMAL_ALARM.BY_INTEGRATION){
            if(type === AI_ABNORMAL_ALARM.BY_CLASS){
              return true;
            }else if(type === AI_ABNORMAL_ALARM.BY_INTEGRATION){
              return false;
            }
          }
        }else{
          return true
        }
      },
      resetMailReceivers() {
        if (AI_ABNORMAL_ALARM.BY_CLASS === this.draftWidgetConfig.alarmCondition) {
          this.$set(this.draftWidgetConfig.mailReceivers, 'integration', []);
        } else if (AI_ABNORMAL_ALARM.BY_INTEGRATION === this.draftWidgetConfig.alarmCondition) {
          this.$set(this.draftWidgetConfig.mailReceivers, 'minor', []);
          this.$set(this.draftWidgetConfig.mailReceivers, 'normal', []);
          this.$set(this.draftWidgetConfig.mailReceivers, 'critical', []);
        }
      },
      emailValidate(email){
        return email_blur(email)
      },
      checkValidAlertValue() {
        this.isValidAlertBand = this.draftWidgetConfig.maxAlert > this.draftWidgetConfig.minAlert ? true : false;
        return this.isValidAlertBand
      }
    },
  };
</script>

<style lang="scss">

  .edit-abnormal-form {
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
      vertical-align: sub;
    }

    .cursor-text {
      cursor: text;
    }

    .validation-error {
      border-color: #FF2D47 !important;
    }

    .btn-outline-secondary {
      white-space: nowrap;
    }

    .float-right {
      position: absolute;
      right: 20px;
    }
  }

  .material-icons.person-icon {
    font-size: 1.2rem !important;
    color: #7e8793 !important;
    vertical-align: sub !important;
  }

  .invalid-alert-band-border {
    border-color: red !important;
  }

  .invalid-alert-band {
    color: red !important;
    text-align: center;
    margin-top: 5px;
  }
</style>
