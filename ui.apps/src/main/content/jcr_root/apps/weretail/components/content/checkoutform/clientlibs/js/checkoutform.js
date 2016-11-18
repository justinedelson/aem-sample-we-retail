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

	var checkbox = "input:checkbox[name='billing-is-shipping-address']";
	var payment = "input:radio[name='payment-option']";

    if ($(checkbox).is(':checked')) {
		$("input[name^='billing.']").hide();
		$("div.we-retail-select-billing").hide();
    }

    $(payment).each(function() {
        var $payment = $(this);
        if ($payment.is(':checked')) {
            if ($payment.val().endsWith('paypal')) {
                $("input[name^='card.']").hide();
            }
            else {
                $('#checkout .cmp-text').hide();
            }
        }
    });
    
    $(checkbox).change(function() {
        $("input[name^='billing.']").toggle();
        $("div.we-retail-select-billing").toggle();
    });

    $(payment).change(function() {
        var $payment = $(this);
        if ($payment.is(':checked') && !$payment.val().endsWith('creditcard')) {
            $("input[name^='card.']").hide().val('');
            $('#checkout .cmp-text').show();
        }
        else {
            $("input[name^='card.']").show();
            $('#checkout .cmp-text').hide();
        }
    });
    
    $('#checkout').submit(function() {
        if ($(checkbox).is(':checked')) {
            $("input[name^='billing.']").each(function() {
                var $this = $(this);
                var shippingName = $this.attr('name').replace('billing.', 'shipping.');
                $this.val($("input[name='" + shippingName + "']").val());
            });
            $("select[name^='billing.']").each(function() {
                var $this = $(this);
                var shippingName = $this.attr('name').replace('billing.', 'shipping.');
                $this.val($("select[name='" + shippingName + "']").val());
            });
        }
    });

})(jQuery);