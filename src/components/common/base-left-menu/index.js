/**
 * Created by sungho.hong on 2020-03-17.
 */

exports.LNB = (function ($) {
  var $container,
    $menuWrapper,
    $menuScroll,
    $menuSelect,
    $closeBtn,
    $settingArea,
    $serviceFold,
    $serviceFoldList,

    init = function() {
      $container = $('.console-gnb');
      $menuWrapper = $('.menu-wrapper');
      $menuScroll = $menuWrapper.find('.menu-scroll');
      $settingArea = $menuWrapper.find('.setting-btn-area');
      $menuSelect = $menuScroll.find('>ul>li').not('.no-service-list');
      $closeBtn = $menuWrapper.find('[data-icon=closelist]');
      $serviceFold = $menuWrapper.find('.service-fold');
      $serviceFoldList = $serviceFold.find('[data-service] li .arrow');

      initLayout();
      initEvent();
    },
    event = function() {
      $container = $('.console-gnb');
      $menuWrapper = $('.menu-wrapper');
      $menuScroll = $menuWrapper.find('.menu-scroll');
      $settingArea = $menuWrapper.find('.setting-btn-area');
      $menuSelect = $menuScroll.find('>ul>li').not('.no-service-list');
      $closeBtn = $menuWrapper.find('[data-icon=closelist]');
      $serviceFold = $menuWrapper.find('.service-fold');
      $serviceFoldList = $serviceFold.find('[data-service] li .arrow');

      updateEvent();
    }

  function initLayout() {

    if($menuWrapper.is(':visible')){
      $('.layout-wrap').css({
        paddingTop: $container.outerHeight() + "px",
        paddingLeft : $menuWrapper.outerWidth() + "px"
      });
    }else{
      $('.layout-wrap').css({
        paddingTop: $container.outerHeight() + "px"
      });
    }
  }

  function initEvent() {

    _gnbInteraction();
    _gnbScrollBottom();
    _menuScrollTop();
    _tooltip();

    $(window).scroll(_gnbScrollBottom);
    $(window).resize(_gnbInteraction);

    $menuScroll.scroll(_menuScrollTop)

  }

  function updateEvent() {
    _gnbInteraction();

    $(window).resize(_gnbInteraction);

  }

  // type에 따라 덮는 ui 콘텐츠를 미는 ui를 선택할수있음
  // true : 덮는 false : 미는
  function _checkedData(type){
    if(!type){
      $('.layout-wrap').css({
        paddingLeft : $menuWrapper.outerWidth() + "px"
      });
    }else{
      $('.layout-wrap').css({
        paddingLeft : ($menuWrapper.outerWidth() + $serviceFold.outerWidth()) + "px"
      });
    }
  }

  // document resize이벤트에 따라 gnb 덮는 or 미는 UI 함수
  function _gnbInteraction(){
    var resizeWidth = $(document).width();
    var $body = window.document.body;
    var clientHeight = $body.clientHeight;
    var scrollHeight  = $body.scrollHeight;


    var layoutContents = $('.layout-contents').outerWidth();
    var menuListHeight = $menuScroll.find('li').outerHeight() * $menuScroll.find('li').length;
    var settingHeight;
    var headerHeight = $('.logo-n-companies').outerHeight();

    $settingArea.legnth > 0 ? settingHeight = $settingArea.outerHeight() : settingHeight = 0;

    var lowHeightShow = clientHeight - settingHeight - headerHeight;


    $menuScroll.css({'height' : 'calc(100% - '+ settingHeight +'px)'});


    var toggles = false;

    // gnb전체값 292에서 full hd 1920을 뺀 1628부터 292를 더한 2212까지는 밀리는 형식

    // if(layoutContents < 1440){
    // 	if(resizeWidth > 1628){
    // 		toggles = true;
    // 	} else{
    // 		toggles = null;
    // 	}
    // }else{
    // 	if(resizeWidth < 1628){
    // 		toggles = null;
    // 	} else{
    // 		toggles = true;
    // 	}
    // }


    if( menuListHeight > lowHeightShow ){
      $menuWrapper.addClass('low-height');
    }else{
      $menuWrapper.removeClass('low-height');
    }



    _gnbAction(toggles)

  }

  // gnb메뉴 스크롤할때 아래 스크롤할수있다는 UI삭제 함수
  function _menuScrollTop(){
    var topValue = $menuScroll.scrollTop();
    topValue === 0 ? $menuWrapper.addClass('scrolling') : $menuWrapper.removeClass('scrolling');
  }

  function _gnbScrollBottom(){
    var $body = window.document.body;
    var scrollHeight  = $body.scrollHeight;
    var clientHeight = $body.clientHeight;
    var scrolls = this.pageYOffset;

    if((scrollHeight - scrolls) === clientHeight || (scrollHeight - scrolls) < clientHeight){
      $menuWrapper.addClass('height-bottom');
    }else{
      $menuWrapper.removeClass('height-bottom')
    }
  }

  // GNB인터렉션을 모아놓은 함수

  function _tooltip() {

    var TOOLTIPS = "gnb-tooltip"; // 툴팁 class
    var CLS_ON = "tooltip_ON";
    var FOLLOW = true;
    var DATA = "_tooltip_text_save"; //툴팁 텍스트 넣어두는곳
    var OFFSET_X = 0;
    var OFFSET_Y = 40;


    showAt = function (e) {
      var ntop = e.pageY + OFFSET_Y, nleft = e.pageX + OFFSET_X;
      $("." + TOOLTIPS).html( "<p> "+ $(e.target).data(DATA) +"</p>").css({
        position: "absolute", top: ntop, left: nleft
      }).show();
    };
    $(document).on("mouseenter", ".menu-scroll *[title]:not([title=''])", function (e) {
      if (!$(this).attr('title')) return;
      $(this).data(DATA, $(this).attr("title"));
      $(this).removeAttr("title").addClass(CLS_ON);
      $("<div class='" + TOOLTIPS + "' />").appendTo("body");
      showAt(e);
    });

    $(document).on("mouseleave", "." + CLS_ON, function (e) {
      $(this).attr("title", $(this).data(DATA)).removeClass(CLS_ON);
      $("." + TOOLTIPS).remove();
    });
    if (FOLLOW) { $(document).on("mousemove", "." + CLS_ON, showAt); }


  }
  function _gnbAction(push){
      $.map($menuSelect,function(value){
          $(value).on('mouseenter',function(){
              var serviceName = $(this).find('>button').attr('data-service');
              var foldService = $(this).closest('.menu-wrapper').find('.service-fold');
              $(this).siblings().removeClass('selected');
              $(this).addClass('selected');
              $(this).closest('.menu-wrapper').attr('data-open','open');

        _checkedData(push);

        foldService.find('[data-service]').attr('hidden',true);
        foldService.find('[data-service='+serviceName+']').removeAttr('hidden');
      });
    })

      let curService = $('#svc_metering'); // svc_ + Service ID

      $menuWrapper.on('mouseleave',function(){
        $menuWrapper.attr('data-open','');
        $menuSelect.removeClass('selected');
        curService.addClass('selected');
        //_checkedData(null);
      });
      // $closeBtn.on('click',function(){
      //     var $this = $(this);
      //     $(this).closest('.menu-wrapper').attr('data-open','');
      //
      //     //닫기 시 현재 서비스로 선택유지를 위해 스크립트 변경 by sungho.hong
      //     // 이렇게 작업 시 각 서비스마다 해당 코드를 변경해줘야 하는 번거로움이 있음
      //     var curService = $('#svc_metering'); // svc_ + Service ID
      //     curService.siblings().removeClass('selected');
      //     curService.addClass('selected');
      //
      //     //$menuSelect.removeClass('selected');
      //     _checkedData(null);
      // });

    $closeBtn.on('click',function(){
      $menuWrapper.attr('data-open','');
      $menuSelect.removeClass('selected');

      curService.addClass('selected');

      //_checkedData(null);
    });


    $.map($serviceFoldList,function(value){
      var toggled = false;
      $(value).on('click',function(e){
        toggled = !toggled;

        if($(this).closest('li').attr('data-depth')){
          $(this).closest('li').attr('data-depth',toggled ? 'on' : 'off');
        }

        e.stopImmediatePropagation();
      });

    });
  }

  return {
    init: function() {
      init();
    },
    event: function() {
      event();
    }
  };
}(jQuery));
