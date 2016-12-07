/*
 *  Copyright 2016 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
(function ($) {
    'use strict';

	if ($('div.we-ShoppingCart-empty').length > 0) {
	    $('a.btn-primary').hide();
	}

	var bindQuantityButtons = function() {
	    $('input[name="quantity"]').change(function() {
	        $(this).parents('form').submit();
	    });
	}
	
	var bindCartForms = function() {
    	$('.we-ShoppingCart form, .we-Cart-content form, .we-Product-form').submit(function(event) {
    	    event.preventDefault();
    	    var $form = $(event.target);
    	    $.ajax({
                url: $form.attr('action'),
                data: $form.serialize(),
                cache: false,
                type: 'POST',
                success: function(json) {
                    $('.we-ShoppingCart').replaceWith(json.shoppingCart);
                    $('.we-Cart').replaceWith(json.navCart);
                    
                    // This reinitializes the navcart Vue.js component
                    // See /apps/weretail/components/structure/navcart/clientlibs/js/cart.js
                    navcart.call(this);
                    
                    if (json.entries == '0') {
                        $('.shoppingcart-prices').remove();
                    }
                    else {
                        $('.shoppingcart-prices').replaceWith(json.cartPrices);
                    }
                    
                    if ($('div.we-ShoppingCart-empty').length > 0) {
                        $('a.btn-primary').hide();
                    }
                    
                    // We have to rebind the jQuery event handlers for the new content
                    bindCartForms();
                    bindQuantityButtons();
                },
                error: function() {
                    alert('An error occured while trying to perform this operation.');
                }
            });
    	});
	}
	
	bindCartForms();
	bindQuantityButtons();
	
})(jQuery);