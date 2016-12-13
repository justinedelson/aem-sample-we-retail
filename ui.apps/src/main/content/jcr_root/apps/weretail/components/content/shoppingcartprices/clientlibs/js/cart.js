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

    var refreshCartPrices = function () {
        var shoppingCartPrices = $('.we-ShoppingCartPrices');
        shoppingCartPrices.parent().load(Granite.HTTP.externalize(shoppingCartPrices.data("resource") + ".html"));
    }
    
    $CQ(document).ready(function () {
        if (window.ContextHub) {
            ContextHub.eventing.on(ContextHub.Constants.EVENT_STORE_UPDATED + ":cart", refreshCartPrices);
        }
    });

})(jQuery);