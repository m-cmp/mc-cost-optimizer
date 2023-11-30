<template>
  <fragment id="dashboard-header">
    <mcmp-base-header
      @selectUserFilter="onChangeUserFilter"
    />
    <base-title
      :class="isSidebarActive ? 'sidebar-active' : ''"
      class="base-title dashboard-header"
    >
      <b-row
        v-if="dashboardViewMode === viewMode.DEFAULT"
        no-gutters>
        <b-dropdown
          ref="dashboardsPopup"
          class="dashboard-name-dropdown"
          size="sm"
          variant="transparent"
          toggle-class="text-decoration-none only-button"
          no-caret>
          <template slot="button-content">
            <span class="font-16 color-darkgray-1 text-overview-dashboard-bold">
              {{ currentDashboard.dashboardName }}
            </span>
            <base-material
              :size="24"
              color="blue-1"
              name="arrow_drop_down"/>
          </template>
          <b-dropdown-form
            v-for="(dashboard, index) in dashboardData"
            :key="index"
            class="custom-dropdown-form">
            <b-row>
              <b-col
                class="remove-padding-col"
                sm="8"
                no-gutters>
                <b-form-radio
                  v-model="internalSelectedDashboardIndex"
                  :value="dashboard.index">
                  <p>
                    {{ dashboard.dashboardName }}
                  </p>
                </b-form-radio>
              </b-col>
              <b-col
                :title="$t('dashboard.dashboardHeader.copy')"
                class="minimum-padding-duplicate float-right"
                align="center">
                <b-button
                  class="custom-button"
                  variant="transparent"
                  @click="onClickCopyDashboard(dashboard.index)"
                >
                  <base-material
                    :size="18"
                    color="blue-1"
                    name="content_copy"/>
                </b-button>
                <!--                <b-tooltip-->
                <!--                  :target="`copy1234-${index}`"-->
                <!--                  :disabled="false"-->
                <!--                  triggers="hover">-->
                <!--                  test-->
                <!--                </b-tooltip>-->
              </b-col>
              <div
                class="remove-padding-col float-right">
                <div class="border-dashboard-name"/>
              </div>
              <b-col
                :title="$t('dashboard.dashboardHeader.edit')"
                class="minimum-padding-edit float-right"
                align="center">
                <b-button
                  class="custom-button"
                  variant="transparent"
                  @click="onClickEditDashboard(dashboard.index)"
                >
                  <base-material
                    :size="18"
                    color="blue-1"
                    name="edit"/>
                </b-button>
              </b-col>
              <div
                class="remove-padding-col float-right">
                <div class="border-dashboard-name"/>
              </div>
              <b-col
                :title="$t('dashboard.dashboardHeader.share')"
                class="minimum-padding-share"
                align="center">
                <b-button
                  class="custom-button"
                  variant="transparent"
                  @click="onClickShareDashboard(dashboard.index)"
                >
                  <base-material
                    :size="18"
                    color="blue-1"
                    name="share"/>
                </b-button>
              </b-col>
              <!--              <div-->
              <!--                class="remove-padding-col float-right"-->
              <!--                align-self="center">-->
              <!--                <div class="border-dashboard-name"/>-->
              <!--              </div>-->
              <!--              <b-col-->
              <!--                class="minimum-padding-col2"-->
              <!--                align-self="center"-->
              <!--                align="center">-->
              <!--                <b-button-->
              <!--                  class="custom-button"-->
              <!--                  variant="transparent"-->
              <!--                  @click="onClickShareDashboard(dashboard.index)"-->
              <!--                >-->
              <!--                  {{ $t('dashboard.dashboardHeader.share') }}-->
              <!--                </b-button>-->
              <!--              </b-col>-->
            </b-row>
          </b-dropdown-form>
        </b-dropdown>
        <!--b-dropdown안에서 v-b-tooltip을 사용하는 경우 툴팁 작동이 제대로 안되는 현상을 확인했고 두 컴포넌트에 적용된 css가 충돌 등이 문제일 것이라고 추측했습니다.
          css 스타일 등을 비교하려고 해당 코드를 아래와 같이 추가했는데, 툴팁이 작동했다 안했다하는 현상이 발생합니다.
          재현)대시보드 드롭다운 리스트의 아이콘에 마우스 댔을 때, 툴팁 스타일 적용이 안되는 경우 그 아래의 검정 버튼(추가한 dropdown)을 눌러서 아이콘에 마우스를 올 후
          다시 대시보드 드롭다운의 아이콘에 마우스를 올리면 툴팁 적용됩니다.-->
        <!--        <b-dropdown-->
        <!--          ref="dashboardsPopup"-->
        <!--          size="sm"-->
        <!--          no-caret>-->
        <!--          <b-dropdown-form-->
        <!--            v-for="(dashboard, i) in dashboardData"-->
        <!--            :key="i">-->
        <!--            <b-row>-->
        <!--              <b-col-->
        <!--                class="remove-padding-col">-->
        <!--                <b-button-->
        <!--                  class="custom-button"-->
        <!--                  variant="transparent"-->
        <!--                  @click="onClickCopyDashboard(dashboard.index)"-->
        <!--                >-->
        <!--                  <base-material-->
        <!--                    v-b-tooltip = "{customClass: 'dashboard-icon-tooltips'}"-->
        <!--                    :title="$t('dashboard.dashboardHeader.edit')"-->
        <!--                    :size="18"-->
        <!--                    color="blue-1"-->
        <!--                    name="content_copy"/>-->
        <!--                </b-button>-->
        <!--              </b-col>-->
        <!--            </b-row>-->
        <!--          </b-dropdown-form>-->
        <!--        </b-dropdown>-->
        <!--        test2-->
        <!--        <b-dropdown>-->
        <!--          <b-dropdown-form>-->
        <!--            <b-row>-->
        <!--              <b-col>-->
        <!--                <b-button-->
        <!--                  v-b-tooltip-->
        <!--                  title="btn1 tooltip">-->
        <!--                  btn1-->
        <!--                </b-button>-->
        <!--              </b-col>-->
        <!--              <b-col>-->
        <!--                <b-button-->
        <!--                  v-b-tooltip-->
        <!--                  title="btn2 tooltip">-->
        <!--                  btn2-->
        <!--                </b-button>-->
        <!--              </b-col>-->
        <!--            </b-row>-->
        <!--          </b-dropdown-form>-->
        <!--        </b-dropdown>-->
      </b-row>
      <b-row
        v-if="dashboardViewMode === viewMode.DEFAULT"
        no-gutters
        class="row-checkbox-group-displaying mr-1 ie-dashboard-checkbox"
      >
        <!--        <span class="margin-right-displaying mr-2 font-family-notosanscjkkr-medium">{{ $t('header.displaying') }}:</span>-->
        <!--        <b-col class="checkbox-group-displaying">-->
        <!--          <b-form-group>-->
        <!--            <b-form-checkbox-group-->
        <!--              v-model="internalSelectedVendors"-->
        <!--              :class="{'disabled-checkbox': !isVendorEnabled}"-->
        <!--              :options="vendorOptions"-->
        <!--              name="checkbox-options"-->
        <!--            />-->
        <!--          </b-form-group>-->
        <!--        </b-col>-->
        <span class="margin-right-displaying mr-2 font-family-notosanscjkkr-medium">{{ $t('header.displaying') }}:&nbsp;</span>
        <div class="row-checkbox-group-displaying">
          <span
            v-for="(v,i) in vendorOptions"
            :key="i"
          >
            {{ v.alias ? v.alias : v.text }}
            <template
              v-if="i < vendorOptions.length-1"
            >, </template>
          </span>
        </div>
        <div class="group-currency group-currency-dashboard">
          <span style="margin-left: 15px"/>
          <span
            class="font-family-notosanscjkkr-medium currency-text">
            {{ $t('header.currency') }}:</span>
          <BaseDropdown
            id="dashboard-currency-tooltip"
            :options="currencyOptions"
            class="font-family-notosanscjkkr-medium"
            @selectOption="onSelectCurrency"
          />
          <b-tooltip
            target="dashboard-currency-tooltip"
            custom-class="custom-exchange-rate-dashboard-tooltip"
            placement="top"
          >
            <div>

              <p>{{ $t("header.currencyTooltip.representativeExchangeRate") }}</p>
              <span v-if="exchangeRate.KRW">
                {{ exchangeRate.USD }} {{ CURRENCY.USD }}
                <span class="exchange-rate-text">=</span>
                {{ formatExchangeRate(exchangeRate.KRW) }} {{ CURRENCY.KRW }}
              </span>
              <br v-if="exchangeRate.KRW && exchangeRate.CNY">
              <span v-if="exchangeRate.CNY">
                {{ exchangeRate.USD }} {{ CURRENCY.USD }}
                <span class="exchange-rate-text">=</span>
                {{ formatExchangeRate(exchangeRate.CNY) }} {{ CURRENCY.CNY }}
              </span>
              <br v-if="exchangeRate.KRW && exchangeRate.MXN">
              <span v-if="exchangeRate.MXN">
                {{ exchangeRate.USD }} {{ CURRENCY.USD }}
                <span class="exchange-rate-text">=</span>
                {{ formatExchangeRate(exchangeRate.MXN) }} {{ CURRENCY.MXN }}
              </span>
              <br>
              <p
                class="custom-exchange-rate-information"
                v-html="getInformationOfCurrency()"
              />
            </div>
          </b-tooltip>
        </div>
      </b-row>
      <b-row
        v-if="dashboardViewMode !== viewMode.DEFAULT"
        class="dashboard-name-wrapper"
        no-gutters>
        <input
          v-model="currentDashboard.dashboardName"
          :maxlength="DASHBOARD_NAME_MAX_LENGTH"
          :class="inputValidDashboardName ? '' : 'dashboard-name-input-invalid'"
          class="dashboard-name-input">
      </b-row>
      <p
        v-if="!inputValidDashboardName"
        class="dashboard-name-warning-message"> {{ inputWarningMessage }}</p>
      <b-row
        v-if="dashboardViewMode !== viewMode.DEFAULT"
        no-gutters
        class="row-checkbox-group-displaying mr-1 font-family-notosanscjkkr-medium">
        <div
          class="add-widget-button-wrapper">
          <b-button
            v-if="dashboardViewMode !== viewMode.DEFAULT"
            class="text-gray-1 button-background-white"
            @click="addWidget">
            {{ $t('dashboard.dashboardHeader.addWidget') }}
          </b-button>
        </div>


        <b-button
          id="cancel-dashboard-header-button"
          class="text-gray-1 button-background-white"
          @click="cancelDashboard()">
          {{ $t('dashboard.dashboardHeader.cancel') }}
        </b-button>

        <b-button
          v-if="dashboardViewMode === viewMode.EDIT"
          class="text-gray-1 button-background-white"
          @click="openSaveAsPopup">
          {{ $t('dashboard.dashboardHeader.saveAs') }}
        </b-button>

        <b-button
          v-if="dashboardViewMode === viewMode.EDIT"
          :class="saveButtonEnabled ? 'button-background-primary' : 'button-disabled'"
          @click="saveDashboard">
          {{ $t('dashboard.dashboardHeader.saveDashboard') }}
        </b-button>

        <b-button
          v-if="dashboardViewMode === viewMode.COPY"
          class="button-background-primary"
          @click="saveDashboardCopy">
          {{ $t('dashboard.dashboardHeader.saveAsNewDashboard') }}
        </b-button>
        <b-button
          v-if="dashboardViewMode === viewMode.EDIT"
          id="delete-dashboard-popover"
          class="delete-dashboard"
          variant="transparent">
          <base-material
            :size="24"
            name="more_vert"
            color="gray-1"/>
        </b-button>
        <BasePopoverDropdown
          :placement="deleteDashboardDropdownOptions.placement"
          :options="deleteDashboardDropdownOptions.options"
          :show-popover="deleteDashboardDropdownOptions.showPopover"
          :enabled-localization="deleteDashboardDropdownOptions.enabledLocalization"
          custom-class="delete-dashboard-custom-popover"
          target="delete-dashboard-popover"
          @selectOption="onSelectDropdownOption"
        />
      </b-row>
    </base-title>
    <BaseConfirmPopup
      ref="deleteDashboardPopup"
      :content="confirmDeleteDashboardPopup.content"
      :title="confirmDeleteDashboardPopup.title"
      :confirm-button-text="$t('dashboard.dashboardHeader.delete')"
      :modal-class="confirmDeleteDashboardPopup.modalClass"
      popup-ref="deleteDashboardPopup"
      confirm-button-color="#dc3545"
      @onConfirmAction="deleteDashboard"
    />
    <BasePopup
      ref="cannotDelDashboardPopup"
      :content="cannotDelDashboardPopup.content"
      :title="cannotDelDashboardPopup.title"
      :button-text="$t('dashboard.dashboardHeader.gotit')"
      :modal-class="cannotDelDashboardPopup.modalClass"
      popup-ref="cannotDelDashboardPopup"
    />
    <BaseSingleInputPopup
      ref="editModeSaveAsDashboardPopup"
      :input-label="saveAsNewDashboardPopup.inputLabel"
      :title="saveAsNewDashboardPopup.title"
      :place-holder="saveAsNewDashboardPopup.placeHolder"
      :input-invalid="saveAsNewDashboardPopup.isInvalid"
      :input-warning-message="saveAsNewDashboardPopup.warningMessage"
      popup-ref="editModeSaveAsDashboardPopup"
      @onConfirmAction="onSubmitSaveAs"
    />
    <b-modal
      :ref="ADD_WIDGET_MODAL_REF"
      :title="$t('dashboard.addWidget.addWidget')"
      modal-class="right-wing-add-widget"
      hide-footer
      hide-backdrop>
      <AddWidget
        :current-dashboard="currentDashboard"
        @addWidget="onAddWidget"
      />
    </b-modal>
  </fragment>
</template>

<script>
  import {CURRENCY, CURRENCY_OPTIONS, CURRENCY_SYMBOL, DEFAULT_VENDOR_OPTIONS, DEFAULT_EXCHANGE_RATE} from '@/constants/constants';
  import BaseDropdown from '@/components/common/BaseDropdown';
  import BaseConfirmPopup from '@/components/common/BaseConfirmPopup';
  import BasePopup from '@/components/common/BasePopup';
  import BaseSingleInputPopup from '@/components/common/BaseSingleInputPopup';
  import {isWidgetDataConfigChanged, isWidgetLayoutConfigChanged} from "@/util/dashboardUtils";
  import {DASHBOARD_DROPDOWN_OPTIONS_VALUE, DEFAULT_DASHBOARD, VIEW_MODE, DASHBOARD_HEADER_DROPDOWN_OPTIONS} from "@/constants/dashboardConstants";
  import _isEmpty from 'lodash/isEmpty';
  import _isNil from 'lodash/isNil';
  import { formatCost } from '@/util/costUtils';
  // import BaseHeader from '@/components/common/base-header/BaseHeader';
  const AddWidget = () => import('@/components/pages/dashboard/add-widget/AddWidget');
  import McmpBaseHeader from '@/components/common/base-header/McmpBaseHeader';
  import { getFullDateFormatByLocalization } from "@/util/dateTimeUtils";

  const DASHBOARD_TEMPLATE_SELECTION = 'dashboard-template-selection'
  const CREATE_DASHBOARD = 'create-dashboard'
  const ADD_WIDGET_GUIDE = 'add-widget-guide'

  export default {
    name: 'DashboardHeader',
    components: {
      BasePopup,
      BaseDropdown,
      BaseConfirmPopup,
      AddWidget,
      BaseSingleInputPopup,
      // BaseHeader,
      McmpBaseHeader,
    },
    props: {
      dashboardData: {
        type: Array,
        default() {
          return [];
        }
      },
      commonUserInfo: {
        type: Object,
        required: true
      },
      currentDashboard: {
        type: Object,
        default() {
          return DEFAULT_DASHBOARD
        }
      },
      dashboardViewMode: {
        type: String,
        default: VIEW_MODE.DEFAULT
      },
      allVendors: {
        type: Array,
        default() {
          return []
        }
      },
      exchangeRate: {
        type: Object,
        default() {
          return DEFAULT_EXCHANGE_RATE
        }
      },
      // currentStep: {
      //   type: Number,
      //   required: true
      // },
      isSidebarActive: {
        type: Boolean,
        default: false
      },
      widgetLoadingState: {
        type: Object,
        required: true,
      },
    },
    data() {
      return {
        CURRENCY: CURRENCY,
        CURRENCY_SYMBOL: CURRENCY_SYMBOL,
        viewMode: VIEW_MODE,
        inputValidDashboardName: true,
        inputWarningMessage: '',
        ADD_WIDGET_MODAL_REF: 'add-widget',
        confirmDeleteDashboardPopup: {
          content: '',
          title: '',
      },
        cannotDelDashboardPopup: {
          content: '',
          title: '',
        },
        saveAsNewDashboardPopup: {
          title: '',
          inputLabel: '',
          placeHolder: '',
          isInvalid: false,
          warningMessage: '',
        },
        DASHBOARD_NAME_MAX_LENGTH: 32,
        saveButtonEnabled: false,
        DASHBOARD_TEMPLATE_SELECTION: DASHBOARD_TEMPLATE_SELECTION,
        CREATE_DASHBOARD: CREATE_DASHBOARD,
        ADD_WIDGET_GUIDE: ADD_WIDGET_GUIDE,
        deleteDashboardDropdownOptions: {
          dashboardDropdownOptionsId: `deleteDashboardDropdown`,
          options: DASHBOARD_HEADER_DROPDOWN_OPTIONS,
          showPopover: false,
          placement: 'bottomleft',
          enabledLocalization: true
        }
      }
    },
    computed: {
      dashboardNames: function () {
        return this.dashboardData.map(dashboard => {
          return dashboard.dashboardName
        });
      },
      vendorOptions() {
        return DEFAULT_VENDOR_OPTIONS.filter(option => this.allVendors.includes(option.value));
      },
      currencyOptions: function() {
        return CURRENCY_OPTIONS.filter(item => this.exchangeRate[item.value] > 0).map(option => {
          let newOption = {...option};
          if (newOption.value === this.commonUserInfo.defaultCurrency) {
            newOption.text = `${option.text} (${this.$t('default')})`
          }
          newOption.isDefault = newOption.value === this.commonUserInfo.selectedCurrency
          return newOption;
        });
      },
      internalSelectedVendors: {
        get() {
          return this.commonUserInfo.selectedVendors;
        },
        set(selectedVendors) {
          this.$emit('selectVendors', selectedVendors)
        }
      },
      internalSelectedDashboardIndex: {
        get() {
          return this.currentDashboard.index;
        },
        set(selectedDashboardIndex) {
          if (selectedDashboardIndex === this.internalSelectedDashboardIndex) {
            return;
          }
          this.$emit('selectDashboard', selectedDashboardIndex);
          this.hideDashboardDropdown();
        },
      },
      isVendorEnabled() {
        return false;
        // if (_isEmpty(this.widgetLoadingState)) {
        //   return false;
        // }
        // return Object.keys(this.widgetLoadingState).every(widgetIdx => !this.widgetLoadingState[widgetIdx]);
      },
    },
    watch: {
      currentDashboard: {
        handler() {
          const originalCurrentDashboard = this.getOriginalCurrentDashboard();
          this.saveButtonEnabled = this.isAnyWidgetChanged(originalCurrentDashboard.widgets, this.currentDashboard.widgets)
            || this.currentDashboard.dashboardName.trim() !== originalCurrentDashboard.dashboardName.trim()
        },
        immediate: true,
        deep: true
      },
      dashboardViewMode: {
        handler() {
          this.inputValidDashboardName =  true;
          this.inputWarningMessage = "";
        },
      },
    },
    mounted() {
      // Ignore ALL PopUp contents in the mounted()
      // this.confirmDeleteDashboardPopup.title = this.$t('dashboard.dashboardHeader.confirmDeleteDashboardPopup.title');
      // this.saveAsNewDashboardPopup.title = this.$t('dashboard.dashboardHeader.saveAsNewDashboardPopup.title');
      // this.saveAsNewDashboardPopup.inputLabel = this.$t('dashboard.dashboardHeader.saveAsNewDashboardPopup.inputLabel');
      // this.saveAsNewDashboardPopup.placeHolder = this.$t('dashboard.dashboardHeader.saveAsNewDashboardPopup.placeHolder');
    },
    methods: {
      onSelectDropdownOption(selectedOption) {
        switch (selectedOption) {
          case DASHBOARD_DROPDOWN_OPTIONS_VALUE.DELETE_DASHBOARD: {
            this.openDeleteDashboardPopup();
          }
        }
      },
      hideDashboardDropdown() {
        this.$refs.dashboardsPopup.hide();
      },
      deleteDashboard() {
        this.$emit('deleteDashboard', this.currentDashboard.index);
      },
      onSelectCurrency(selectedCurrency) {
        this.$emit('selectCurrency', selectedCurrency)
      },
      onClickCopyDashboard(dashboardIndex) {
        this.$emit('clickCopyDashboard', dashboardIndex);
      },
      onClickEditDashboard(dashboardIndex) {
        this.$emit('clickEditDashboard', dashboardIndex);
      },
      onClickShareDashboard(dashboardIndex) {
        this.$emit('clickShareDashboard', dashboardIndex);
      },
      setDashboardViewMode(viewMode) {
        this.inputValidDashboardName =  true;
        this.inputWarningMessage = "";
        this.$store.dispatch('dashboard/setDashboardViewMode', viewMode);
      },
      cancelDashboard() {
        this.$emit('cancelDashboard');
      },
      removeDashboardName() {
        this.inputValidDashboardName = true;
        this.inputWarningMessage = "";
        this.currentDashboard.dashboardName = '';
      },
      saveDashboard() {
        if (!this.saveButtonEnabled) {
          return;
        }
        if (this.isEmptyDashboardName(this.currentDashboard.dashboardName)) {
          return;
        }
        const selectedDashboard = this.getOriginalCurrentDashboard()
        if (this.isExistedNewDashboardNameInEditMode(this.currentDashboard.dashboardName, this.dashboardNames, selectedDashboard.dashboardName)) {
          return;
        }
        this.$emit('saveDashboard');
      },
      saveDashboardCopy() {
        // if(!this.validateDashboardLimite()){
        //   return;
        // }
        if (!this.validateDashboardName(this.currentDashboard.dashboardName, this.dashboardNames)) {
          return;
        }
        this.inputValidDashboardName = true;
        this.inputWarningMessage = "";

        this.$emit('saveDashboardCopy');
      },
      showHotspot(selector) {
        this.$emit('showHotspot', selector);
      },
      addWidget() {
        this.showAddWidgetModal()
      },
      openDeleteDashboardPopup() {
        const dashboardName = this.getOriginalCurrentDashboard().dashboardName;
        if(this.dashboardData.length > 1) {
          this.confirmDeleteDashboardPopup.title = this.$t('dashboard.dashboardHeader.confirmDeleteDashboardPopup.title');
          this.confirmDeleteDashboardPopup.content = this.$t('dashboard.dashboardHeader.confirmDeleteDashboardPopup.content', {'dashboardName': dashboardName});
          this.$refs.deleteDashboardPopup.show();
        }
        else {
            this.cannotDelDashboardPopup.title = this.$t('dashboard.dashboardHeader.confirmDeleteDashboardPopup.title');
            this.cannotDelDashboardPopup.content = this.$t('dashboard.dashboardHeader.cannotDelDashboardPopup.content');
            this.$refs.cannotDelDashboardPopup.show();
          }
      },
      showAddWidgetModal() {
        this.$refs[this.ADD_WIDGET_MODAL_REF].show()
      },
      hideAddWidgetModal() {
        this.$refs[this.ADD_WIDGET_MODAL_REF].hide()
      },
      openSaveAsPopup() {
        this.$refs.editModeSaveAsDashboardPopup.show();
        this.saveAsNewDashboardPopup.title = this.$t('dashboard.dashboardHeader.saveAsNewDashboardPopup.title');
        this.saveAsNewDashboardPopup.inputLabel = this.$t('dashboard.dashboardHeader.saveAsNewDashboardPopup.inputLabel');
        this.saveAsNewDashboardPopup.placeHolder = this.$t('dashboard.dashboardHeader.saveAsNewDashboardPopup.placeHolder');
        this.saveAsNewDashboardPopup.isInvalid = false;
        this.saveAsNewDashboardPopup.warningMessage = "";
      },
      closeSaveAsPopup() {
        this.$refs.editModeSaveAsDashboardPopup.reset();
        this.$refs.editModeSaveAsDashboardPopup.hide();
      },
      onSubmitSaveAs(newName) {
        if(_isEmpty(newName)){
          this.saveAsNewDashboardPopup.isInvalid = true;
          this.saveAsNewDashboardPopup.warningMessage = this.$t('dashboard.dashboardHeader.needToEnterDashboardName');
          return;
        }

        let existedName = this.dashboardNames.find(dashboardName => dashboardName.trim() === newName.trim());
        if (!_isEmpty(existedName)) {
          this.saveAsNewDashboardPopup.isInvalid = true;
          this.saveAsNewDashboardPopup.warningMessage = this.$t('dashboard.dashboardHeader.duplicateName');
          return;
        }
        this.$emit('submitSaveAs', newName);
        this.closeSaveAsPopup();
      },
      validateDashboardName(newName, dashboardNames) {
        return !this.isEmptyDashboardName(newName) && !this.isExistedDashboardName(dashboardNames, newName);
      },
      validateDashboardLimite(){
        if(this.dashboardData != undefined && this.dashboardData.length > 9){
          this.inputValidDashboardName = false;
          this.inputWarningMessage = this.$t('dashboard.dashboardHeader.limiteCreate')
          return false;
        }
        return true;
      },
      isEmptyDashboardName(newDashboardName) {
        if (_isEmpty(newDashboardName)) {
          this.inputValidDashboardName = false;
          this.inputWarningMessage = this.$t('dashboard.dashboardHeader.needToEnterDashboardName')
          return true;
        }
        return false;
      },
      isExistedNewDashboardNameInEditMode(newName, dashboardNames, currentDashboardName) {
        let existedName = dashboardNames.find(dashboardName => (dashboardName.trim() === newName.trim()) && (dashboardName.trim() !== currentDashboardName.trim()));
        if (!_isEmpty(existedName)) {
          this.inputValidDashboardName = false;
          this.inputWarningMessage = this.$t('dashboard.dashboardHeader.duplicateName')
          return true;
        }
        return false;
      },
      isExistedDashboardName(dashboardNames, newDashboardName) {
        let existedName = dashboardNames.find(dashboardName => dashboardName.trim() === newDashboardName.trim());
        if (!_isEmpty(existedName)) {
          this.inputValidDashboardName = false;
          this.inputWarningMessage = this.$t('dashboard.dashboardHeader.duplicateName')
          return true;
        }
        return false;
      },
      isAnyWidgetChanged(originalWidgets, draftWidgets) {
        if (draftWidgets == originalWidgets) {
          return false;
        }
        if (_isNil(draftWidgets)
          || _isNil(originalWidgets)
          || draftWidgets.length !== originalWidgets.length
        ) {
          return true;
        }
        for (let index = 0; index < draftWidgets.length; index++) {
          if (draftWidgets[index].index !== originalWidgets[index].index
            || draftWidgets[index].widgetType !== originalWidgets[index].widgetType
            || isWidgetLayoutConfigChanged(originalWidgets[index], draftWidgets[index])
            || isWidgetDataConfigChanged(originalWidgets[index].widgetType, originalWidgets[index], draftWidgets[index])
          ) {
            return true;
          }
        }
        return false;
      },
      getOriginalCurrentDashboard() {
        return this.dashboardData.find(db => db.index === this.currentDashboard.index) || DEFAULT_DASHBOARD;
      },
      onAddWidget(newWidget) {
        this.$emit('addWidget', newWidget);
      },
      onChangeUserFilter(userFilterIdx) {
        this.$emit('changeUserFilter', userFilterIdx)
      },
      getNameWrapperById(name) {
        return `${name}-wrapper`
      },
      formatExchangeRate(exchangeRate) {
        return formatCost(exchangeRate, {mantissa: 2})
      },
      getInformationOfCurrency() {
        let applyExchangeRateDate = this.$dayjs(this.commonUserInfo.exchangeRateDate).format(getFullDateFormatByLocalization())
        return `
          <div class="txt-left">
            <p>${this.$t("header.currencyTooltip.actualChargeCanBeChanged")}</p>
            <p>${this.$t("header.currencyTooltip.currentExchangeRateTime", {'applyExchangeRateDate': applyExchangeRateDate})}</p>
            <p>${this.$t("header.currencyTooltip.provider#1")}</p>
            <p>${this.$t("header.currencyTooltip.provider#2")}</p>
            <p>${this.$t("header.currencyTooltip.provider#3")}</p>
          </div>
        `;
      }
    },
  };
</script>

<style lang="scss">
  .custom-exchange-rate-dashboard-tooltip {
     .exchange-rate-text {
       margin: 0 2px;
     }
     .tooltip-inner {
       font-size: 14px;
       text-align: left !important;
       padding: 10px 16px 16px 16px !important;
       max-width: 328px!important;
       .custom-exchange-rate-information {
         padding-top: 8px;
         font-size: 12px;
       }
     }
     .arrow {
       &::before {
         border-bottom-color: #212121;
         opacity: 1;
       }
     }
   }
  .dashboard-template-selection {
    position: relative;
    margin-left: -10px;
    z-index: 999;
    right: 17px;
  }
  .create-dashboard {
    position: absolute;
    margin-left: 190px;
    margin-top: 13px;
    z-index: 999;
    bottom: 15px;
  }
  .add-widget-button-wrapper {
    position: relative;
    .add-widget-guide {
      position: absolute;
      z-index: 999;
      top: 2px;
      left: 26px;
    }
  }
  .base-title {
    [type="radio"]:not(:checked) + label {
      box-shadow: none !important;
      color: #7b8088;
    }
    .remove-padding-col{
      padding: 0 !important;
    }
    .dashboard-name-dropdown {
      border-bottom: 1px solid #eaecef !important;
      .dropdown-menu {
        max-height: 600px;
        min-width: 300px;
        width: auto;
        padding: 0;
        li {
          border-bottom: 1px solid #F2F4F6;
          label {
            cursor: pointer;
            p {
              word-break: break-word;
            }
          }
          &:hover {
            background-color: #F2F4F6;
            .custom-control-label {
              color: #222222;
            }
            .custom-button {
              background-color: #F2F4F6;
            }
          }
        }
        li:last-child {
          border-bottom: none;
        }
      }
    }

    .custom-dropdown-form {
      padding-top: 10px !important;
      padding-bottom: 10px !important;
      outline: none !important;
      min-width: 350px;
      .color-text-dashboard {
        color: #6c7994;
        font-size: 12px;
        font-weight: 500;
      }
      .custom-badge {
        background-color: #e9ebf5;
        margin-left: 4px;
        border-radius: 4px;
      }
      .border-dashboard-name {
        width: 1px;
        height: 14px;
        background-color: #eaecef;
        margin-top: 4px;
      }
      .border-dashboard-name2{
        width: 1px;
        height: 14px;
        background-color: #eaecef;
        margin-right: 12px;
        margin-top: 4px;
      }
      .minimum-padding-duplicate {
        padding-right: 5px !important;
        padding-left: 0px !important;
      }
      .minimum-padding-edit {
        padding-right: 5px !important;
        padding-left: 5px !important;
      }
      .minimum-padding-share {
        padding-right: 0px !important;
        padding-left: 5px !important;
      }
      .custom-button {
        color: #0672ff;
        padding-left: 0 !important;
        padding-right: 0 !important;
      }
    }
    .dropdown-menu {
      height: auto;
      width: 290px;
      -webkit-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
      -moz-box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
      box-shadow: 0 3px 10px 0 rgba(0, 0, 0, 0.3);
      border: none !important;
      top: 6px;
    }
    .text-overview-dashboard-bold {
      font-weight: bold ;
    }
    .row-checkbox-group-displaying {
      align-items: center;
      justify-content: center;
      flex-wrap: nowrap;
      .dashboard-name-wrapper {
        position: relative;
      }
      .border-left-notification-icon {
        border-left: 2px solid rgba(184,191,202,0.6);
        margin-left: 16px;
        .padding-left-icon-notification-icon {
          padding-left: 10px;
        }
      }
      .dropdown-toggle {
        padding-left: 8px;
        padding-right: 0;
      }
      .checkbox-group-displaying {
        height: 19px;
        padding-left: 13px;
        .disabled-checkbox {
          pointer-events: none;
          label {
            cursor: pointer;
          }
          [type="checkbox"]:checked + label:before,
          [type="checkbox"]:not(:checked) + label:before {
            top: 4px;
            width: 12px;
            height: 12px;
            background-color: rgba(0, 123, 255, 0.4) !important;
            border: 1px solid rgba(0, 123, 255, 0.4) !important;
          }
          [type="checkbox"]:checked + label:after,
          [type="checkbox"]:not(:checked) + label:after {
            top: 2px;
            width: 13px;
          }
        }
        .custom-control-input:focus ~ .custom-control-label::before {
          box-shadow: none !important;
        }
        .currency-text {
          color: #222222;
        }
        label {
          cursor: pointer;
        }
        [type="checkbox"]:checked + label:before,
        [type="checkbox"]:not(:checked) + label:before {
          top: 4px;
          width: 12px;
          height: 12px;
        }
        [type="checkbox"]:checked + label:after,
        [type="checkbox"]:not(:checked) + label:after {
          top: 2px;
          width: 13px;
        }
      }
      .margin-right-displaying {
        margin-right: 8px;
        color: #222222;
      }
      .custom-currency {
        color: #0672ff
      }
    }
  }

  .dashboard-header {
    &.sidebar-active {
      padding-left: 300px;
    }
    #dashboard-currency-tooltip {
      .dropdown-menu {
        width: auto !important;
      }
    }
    .row-checkbox-group-displaying {
      .custom-control-label {
        color: #222222;
      }
    }
    .close-icon {
      padding-left:0;
      padding-right: 0;
      font-size: 15px;
      position: absolute;
      right: 5px;
      top: 7px;
      cursor: pointer;
    }
    .button-background-white {
      background-color: white !important;
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
      margin-right: 25px !important;
    }

    .button-background-primary {
      background-color: #0672FF !important;
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
      margin-right: 25px !important;
    }

    .button-disabled {
      background-color: #dddee0 !important;
      outline: none;
      outline: 0;
      box-shadow: none !important;
      border-radius: 4px !important;
      margin-right: 25px !important;
    }

    .dashboard-name-input {
      width: 400px;
      border: 1px #898D94 solid;
      height: 30px;
      padding-left: 10px;
      border-radius: 2px !important;
      &.dashboard-name-input-invalid {
        border: 1px solid red;
      }
    }

    .delete-dashboard {
      .dropdown-menu {
        transform: translate3d(-75px, 30px, 0px) !important;
        min-width: 100px;
        width: 100px;
        padding-top: 3px;
        padding-bottom: 3px;
      }
      .dropdown-item {
        padding-left: 15px;
      }
    }

    .delete-dashboard-dropdown {
      right: 5px;
      height: 40px;
    }

    .dashboard-name-warning-message {
      color: red;
      display: block;
      position: absolute;
      bottom: 0;
      font-size: 11px
    }
  }

  .dashboard-del-popup {
    .modal-header2 {
      text-align: center;
      .modal-title2 {
        position: absolute;
        left: 50%;
        margin-left: -13%;
        font-size: 16px;
      }
    }

    .modal-body2 {
      padding: 0 !important;
      .content-confirm-popup {
        padding: 30px;
        p {
          font-size: 14px;
        }
      }
    }

    .modal-content2 {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%) !important;
    }
  }

  @media only screen and (max-width: 1024px) {
    #app {
      .wrapper-dashboard {
        #header {
          position: fixed!important;
          width: auto;
          top: -0px !important;
          left: 0px;
        }
      }
      #title {
        padding-left: 4.375rem !important;
      }
    }
  }
  @media all and (-ms-high-contrast: none), (-ms-high-contrast: active) {
    .group-currency-dashboard {
      padding-left: 63px;
    }
  }

</style>
