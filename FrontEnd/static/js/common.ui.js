// console 객체가 없을 경우
if (!window.console) {
	window.console = {
		log : function(){},
		dir : function(){}
	};
} else if (!window.console.dir){
	window.console.dir = function(){};
}


$.fn.extend({
	toggleAttr: function(attr, value) {
		return this.each(function () {
			if( !$(this).is("[" + attr + "]") ) {
				$(this).attr(attr, value);
			} else {
				$(this).removeAttr(attr);
			}
		});
	}
});


$(window.document).ready(function() {

	// mfp
	if( $.magnificPopup != undefined ) {
		$.magnificPopup.defaults.fixedBgPos = true;
		$.magnificPopup.defaults.closeOnBgClick = false;
		$.magnificPopup.defaults.removalDelay = 300;
		$.magnificPopup.defaults.mainClass = "animated";
	}

	// jconfirm
	if( jconfirm != undefined ) {
		jconfirm.defaults = {
			theme: 'theme-bsp',
			animation: 'default',
			closeAnimation: 'default',
			animationSpeed: 1000,
			container: 'body',
			containerFluid: false,
			useBootstrap: false,
			boxWidth: '500px',
			closeIcon: true,
			backgroundDismiss: false,
			backgroundDismissAnimation: 'none',
		};
	}

	// 드랍다운(셀렉트박스)
	// 싱글(inline) 셀렉트
	// $('.type-inline').dropdown({});
  //
	// // 싱글 셀렉트
	// $('.type-single').dropdown({});
	//
	// // 멀티 셀렉트
	// $('.type-checkbox').dropdown({
	// 	useLabels: false,
	// });
  //
	// // 멀티 셀렉트(태그형태)
	// $('.type-tagging').dropdown({
	// 	onChange: function(value, text, $choice) {
	// 		if( value.length > 1 ) {
	// 			$('.go-vendor.type-tagging').addClass('selected');
	// 		} else {
	// 			$('.go-vendor.type-tagging').removeClass('selected');
	// 		}
	// 	},
	// });


	// 마우스 오버 툴팁
	// $('[data-trigger="tooltip"]').tooltipster({
	// 	delay: 300,
	// 	// trigger: 'click',
	// 	animation: 'fade',
	// 	updateAnimation: 'fade',
	// 	side: 'bottom',
	// 	arrow: true,
	// 	contentAsHTML: true,
	// 	interactive: true,
	// });



});


/*
$('.ui.dropdown').has('optgroup').each(function () {
    var $menu = $('<div/>').addClass('menu');
    $(this).find('select').children().each(function () {
        if ($(this).is('option')) {
            return $menu.append('<div class="item' + (this.selected ? ' active selected' : '') + (this.disabled ? ' disabled' : '') + '" data-value="' + this.value + '">' + (this.label || this.innerHTML) + "</div>");
        }
        if ($(this).is('optgroup')) {
            var isDisabled = this.disabled || false;
            var groupLabel = this.label;
            $menu.append('<div class="header' + (isDisabled ? ' item disabled' : '') + '">' + groupLabel + '</div>');
            $(this).children().each(function () {
                return $menu.append('<div class="item' + (this.selected ? ' active selected' : '') + (isDisabled || this.disabled ? ' disabled' : '') + '" data-value="' + this.value + '" data-text="' + groupLabel + ': ' + this.label + '">' + (this.label || this.innerHTML) + "</div>");
            });
            return $menu;
        }
    });
    return $(this).find('.menu').html($menu.html());
});
*/

