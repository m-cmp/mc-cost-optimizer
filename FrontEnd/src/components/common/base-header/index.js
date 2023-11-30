/**
 * Created by sungho.hong on 2020-03-10.
 */
/* GlobalMenu */
function BSPSelectBox($container) {
    this.$container = $container;
    this.$btnSelect;
    this.$listContainer;
    this.$searchContainer;
    this.$listItems;


    this.init();
}

BSPSelectBox.prototype.init = function() {
    var _ = this;

    _.$btnSelect = _.$container.find('.btn-custom-select');
    _.$listContainer = _.$container.find('.list-container');
    _.$searchContainer = _.$container.find('.search-word');
    _.$textField = _.$searchContainer.find('input[type=text]');
    _.$listItems = _.$listContainer.find('button');

    _.initEvent();
}

BSPSelectBox.prototype.initEvent = function() {
    //console.log('initEvent S');
    var _ = this;

    _.$btnSelect.off('click').on('click', function(e) {
        $(this).toggleClass("is-selected");

        _.$listContainer.one("webkitTransitionEnd otransitionend oTransitionEnd msTransitionEnd transitionend", function() {
            _.$textField.focus();
        });

    });

    _.$btnSelect.on('keydown', function(e) {
        switch(e.keyCode) {
            case 40: {
                _.$listContainer.find('button:visible').eq(0).focus();
                break;
            }
        }
    })


    _.$listItems.off('click').on('click', function(e) {
        _.select($(this));
    });

    _.$listItems.on('keydown', function(e) {
        switch(e.keyCode) {
            case 38: {
                var $items = _.$listContainer.find('button:visible');
                var index = $items.index(this);
                if( index == 0 ) {
                    _.$textField.focus();
                } else {
                    $items.eq(index-1).focus();
                }
                break;
            }

            case 40: {
                var $items = _.$listContainer.find('button:visible');
                var index = $items.index(this);
                $items.eq(index+1).focus();
                break;
            }
        }
    });

    _.$textField.on('keyup', function(e) {
        var keyword = e.target.value.toString();
        var $btns = _.$listContainer.find('.list-custom-select button');
        if( keyword.length <= 0 ) {
            $btns.closest('li').removeClass('not-match');
        }
        $.each($btns, function(index, element) {
            var orgText = $(element).text();
            orgText = orgText.replace('<mark>', '').replace('</mark>', '');
            var startIdx = orgText.toUpperCase().indexOf(keyword.toUpperCase());
            var length = keyword.length;
            var markWord = orgText.substr(startIdx, length);
            if(orgText.toUpperCase().indexOf(keyword.toUpperCase()) !== -1){
                var newText = orgText.replace(markWord, '<mark>'+markWord+'</mark>');
                $(element).html(newText);
                $(element).closest('li').removeClass('not-match');
            } else {
                $(element).closest('li').addClass('not-match');
            }
        });
        switch(e.keyCode) {
            case 40: {
                _.$listContainer.find('button:visible').eq(0).focus();
                break;
            }
        };
    });


    //console.log('initEvent E');
}

BSPSelectBox.prototype.hide = function() {
    //console.log('hide S');
    var _ = this;
    _.$btnSelect.removeClass("is-selected");
    //console.log('hide E');
}

BSPSelectBox.prototype.select = function($item) {
    //console.log('select S');
    var _ = this;
    _.$listItems.closest('li').removeClass('is-selected');
    $item.closest('li').addClass('is-selected');
    //_.$btnSelect.html($item.html());
    _.hide();
    //console.log($item,'select E',_.$listItems);
}

BSPSelectBox.prototype.resetSearchWord = function() {
    var _ = this;

    _.$textField.val('').trigger('keyup');
}


function ONCompanyBox($container) {
    this.$container = $container;
    this.$btnSelect;
    this.$listContainer;
    this.$searchContainer;
    this.$listItems;

    this.init();
}

ONCompanyBox.prototype.init = function() {
    var _ = this;

    _.$btnSelect = _.$container.find('.btn-companies');
    _.$listContainer = _.$container.find('.console-companies');
    _.$searchContainer = _.$container.find('.search-word');
    _.$textField = _.$searchContainer.find('input[type=text]');
    _.$listItems = _.$listContainer.find('button');

    _.initEvent();
}

ONCompanyBox.prototype.initEvent = function() {
    var _ = this;

    //console.log(_.$listItems);

    _.$btnSelect.off('click').on('click', function(e) {
        //console.log(e);
        $(this).toggleClass("selected");

        _.$listContainer.one("webkitTransitionEnd otransitionend oTransitionEnd msTransitionEnd transitionend", function() {
            _.$textField.focus();
        });
    });

    _.$btnSelect.on('keydown', function(e) {
        switch(e.keyCode) {
            case 40: {
                _.$listContainer.find('button:visible').eq(0).focus();
                break;
            }
        }
    })

    _.$listItems.off('click').on('click', function(e) {
        //console.log(e);
        _.select($(this));
    });

    _.$listItems.on('keydown', function(e) {
        //console.log(e.keyCode);
        switch(e.keyCode) {
            case 38: {
                var $items = _.$listContainer.find('button:visible');
                var index = $items.index(this);
                if( index == 0 ) {
                    _.$textField.focus();
                } else {
                    $items.eq(index-1).focus();
                }
                break;
            }

            case 40: {
                var $items = _.$listContainer.find('button:visible');
                var index = $items.index(this);
                $items.eq(index+1).focus();
                break;
            }
        }
    });

    _.$textField.on('keyup', function(e) {
        var keyword = e.target.value.toString();
        var $btns = _.$listContainer.find('.list-companies button');
        if( keyword.length <= 0 ) {
            $btns.closest('li').removeClass('not-match');
        }
        $.each($btns, function(index, element) {
            var orgText = $(element).text();
            orgText = orgText.replace('<mark>', '').replace('</mark>', '');
            var startIdx = orgText.toUpperCase().indexOf(keyword.toUpperCase());
            var length = keyword.length;
            var markWord = orgText.substr(startIdx, length);
            if(orgText.toUpperCase().indexOf(keyword.toUpperCase()) !== -1){
                newText = orgText.replace(markWord, '<mark>' + markWord +'</mark>');
                $(element).html(newText);
                $(element).closest('li').removeClass('not-match');
            } else {
                $(element).closest('li').addClass('not-match');
            }
        });
        switch(e.keyCode) {
            case 40: {
                _.$listContainer.find('button:visible').eq(0).focus();
                break;
            }
        };
    });
}

ONCompanyBox.prototype.hide = function() {
    var _ = this;

    _.$btnSelect.removeClass("selected")
}

ONCompanyBox.prototype.select = function($item) {
    var _ = this;

    _.$listItems.closest('li').removeClass('selected');
    $item.closest('li').addClass('selected');

    //_.$btnSelect.html($item.html());
    _.$btnSelect.html($item.html().replace('<mark>', '').replace('</mark>', ''));
    _.hide();
}

ONCompanyBox.prototype.resetSearchWord = function() {
    var _ = this;

    _.$textField.val('').trigger('keyup');
}

exports.ConsoleGNB= (function ($) {
    var $container,
        $btnNotification,
        $btnUser,
        $listNotice,
        $layerMenusContainer,
        selectCompany,
        $submenusContainer,
        $listSubmneus,
        containerWidth,
        indexSubmenu = 0,
        pages = [0],
        $btnPrev,
        $btnNext,


        init = function() {
            $container = $('.console-gnb');
            $Main = $('#wrapper').parent();
            $layerMenusContainer = $container.find('.service-container .service-container-inner');
            $btnNotification = $container.find('.btn[data-icon=notice]');
            $btnUser = $container.find('.btn.user');
            $listNotice = $container.find('.list-gnb-notice');

            selectCompany = new ONCompanyBox($('#select-company'));

            $submenusContainer = $container.find('.submenus-container');
            $listSubmneus = $submenusContainer.find('.list-submenus > li');
            $btnPrev = $submenusContainer.find('.btn.prev');
            $btnNext = $submenusContainer.find('.btn.next');


            initLayout();
            initEvent();
        },
        setCompany = function() {
            selectCompany = new ONCompanyBox($('#select-company'));
        };

    function initLayout() {
        $('.layout-wrap').css({
            paddingTop: $container.outerHeight() + "px"
        });

    }

    function initEvent() {
        $btnNotification.on('click', function(e) {
          $(this).toggleAttr('data-state', 'open');
        });

        $btnUser.on('click', function(e) {
            $(this).toggleAttr('data-state', 'open');
        });

        $container.find('.ticker-wrapper').easyTicker({
            direction: 'up',
            interval: 5000,
            visible: 1,
            mousePause: 1
        });

        _updateNavSubmenus();
        _scrollFixed();
        $Main.scroll(_scrollFixed);
        $(window).resize(_updateNavSubmenus);

        $(document).click(function (e) {

            if( !$(e.target).closest("#select-company").length ) {
                selectCompany.resetSearchWord();
                selectCompany.hide();
            }

            if( !$(e.target).closest("[data-icon=notice]").length ) {
                $btnNotification.removeAttr('data-state');
            }

            if( !$(e.target).closest(".user").length ) {
                $btnUser.removeAttr('data-state');
            }
        });

    }

    function _moveNav() {
        var left = 0;
        $.map($listSubmneus, function(value, i) {
            if( i < pages[indexSubmenu] ) {
                left += $(value).outerWidth();
            }
        });

        if( left != 0 ) {
            left -= 20;
        }
        $submenusContainer.find('.list-submenus').animate({
            left: -left + "px"
        }, 200, function() {

        });

        _updateNavButtons();
    }

    function _updateNavButtons() {

        if( indexSubmenu < pages.length - 1 ) {
            $btnNext.show();
        } else {
            $btnNext.hide();
        }

        if( indexSubmenu != 0 ) {
            $btnPrev.show();
        } else {
            $btnPrev.hide();
        }
    }

    function _updateNavSubmenus() {
        $container = $('.console-gnb');
        $submenusContainer = $container.find('.submenus-container');
        $listSubmneus = $submenusContainer.find('.list-submenus > li');
        containerWidth = $submenusContainer.outerWidth() - 30;

        indexSubmenu = 0;
        pages = [0];
        var totalWidth = 0;
        var pageWidth = 0;

        setTimeout(function(){
            $.map($listSubmneus, function(value, index) {
                totalWidth += $(value).outerWidth();
                pageWidth += $(value).outerWidth();
                if( pageWidth > containerWidth ) {
                    pages.push(index);
                    pageWidth = 0;
                }
            });

            // prev, next button
            _updateNavButtons();
            _moveNav();

            $btnNext.off('click').on('click', function(e) {
                indexSubmenu++;
                if( indexSubmenu >= pages.length ) {
                    indexSubmenu = pages.length - 1;
                    return false;
                }

                _moveNav();
            });

            $btnPrev.off('click').on('click', function(e) {
                indexSubmenu--;
                if( indexSubmenu < 0 ) {
                    indexSubmenu = 0;
                    return false;
                }
                _moveNav();
            });
        }, 100);

    }

    function _scrollFixed() {
        $container.css("left", -$(this).scrollLeft());
    }

    return {
        init: function() {
            init();
        },
        setCompany: function() {
            setCompany();
        }
    };
}(jQuery));


exports.GlobalMenu = (function ($) {
    var scope,
        $container,
        // $homeSummary,
        //selectService,
        selectCompany,
        $user,
        $btnNotification,
        //$allMenus,
        init = function() {
            $container = $('.global-menu');
            // $homeSummary = $('.home-summary');

            //selectService = new BSPSelectBox($('#select-service'));
            selectCompany = new BSPSelectBox($('#select-company'));

            $user = $container.find('.user');
            $btnNotification = $container.find('.btn-notification');
            //$allMenus = $container.find('.btn-all-menus')

            initLayout();
            initEvent();
        },
        setCompany = function() {
            selectCompany = new BSPSelectBox($('#select-company'));

            $(document).click(function (event) {
                if( !$(event.target).closest("#select-company").length ) {
                    selectCompany.hide();
                }
            });
        };
    // setService = function() {
    //   selectService = new BSPSelectBox($('#select-service'));
    //
    //   $(document).click(function (event) {
    //     if( !$(event.target).closest("#select-service").length ) {
    //       selectService.hide();
    //     }
    //   });
    // };

    function initLayout() {
        if( !$('body').hasClass('page-home') ) {
            $container.attr('data-state', '');
        }
    }

    function initEvent() {


        $user.on('click', function() {
            $(this).toggleAttr('data-state', 'open');
        });

        $btnNotification.on('click', function(e) {
            $(this).closest('.notification').toggleAttr('data-state', 'open');
        });

        // $allMenus.on('click', function() {
        //   $(this).closest('.all-menus').toggleAttr('data-state', 'open');
        // })

        $(window).scroll(function (e) {
            if( $('body.service-portal.page-home').length ) {
                var scrollTop = $(window).scrollTop();
                // var heightSummary = $homeSummary.height() - $container.height();

                if( scrollTop > heightSummary ) {
                    $container.attr('data-state', '');
                } else {
                    $container.attr('data-state', 'change');
                }
            }
        });

        $(document).click(function (e) {
            if( !$(event.target).closest(".user").length ) {
                $user.removeAttr('data-state');
            }

            // if( !$(event.target).closest(".all-menus").length ) {
            //   $allMenus.closest('.all-menus').removeAttr('data-state');
            // }

            if( !$(event.target).closest(".notification").length ) {
                $btnNotification.closest('.notification').removeAttr('data-state');
            }

            // if( !$(event.target).closest("#select-service").length ) {
            //   selectService.hide();
            // }

            if( !$(event.target).closest("#select-company").length ) {
                selectCompany.hide();
            }
        });
    }

    return {
        init: function() {
            init();
        },
        setCompany: function() {
            setCompany();
        }
        // setService: function() {
        //   setService();
        // }
    };
}(jQuery));
