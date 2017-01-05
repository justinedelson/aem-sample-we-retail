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
(window.navcart = function () {
    'use strict';

    var EXPANDABLE_CLASS = 'we-Cart-expandable',
        EXPAND_SMARTLIST_VALUE = 'smartlist-expanded',
        EXPAND_CART_VALUE = 'cart-expanded',

        EXPANDABLE_SELECTOR = 'body';

    Vue.component('cart-content', {
        ready: function() {
            // move cart contents to body
            // so we won't interfere with any mobile styles
            document.body.appendChild(this.$el);
            if (window.ContextHub) {
                ContextHub.eventing.on(ContextHub.Constants.EVENT_STORE_UPDATED + ":cart", this.refreshCart);
            }
        },
        data: function() {
            var _cart = null;
            if (window.ContextHub) {
                _cart = ContextHub.getStore('cart');
            }
            return {
                cartEntries: _cart.getItem('entries'),
                cartEntriesSize: _cart.getItem('entries') ? _cart.getItem('entries').length : 0,
                cartTotalPrice: _cart.getItem('totalPrice'),
                cartPromotions: _cart.getItem('promotions')
            }
        },
        methods: {
            refreshCart: function(event) {
                if (window.ContextHub) {
                    var _cart = ContextHub.getStore('cart');
                    this.$data.cartEntries = _cart.getItem('entries');
                    this.$data.cartEntriesSize = _cart.getItem('entries') ? _cart.getItem('entries').length : 0;
                    this.$data.cartTotalPrice = _cart.getItem('totalPrice');
                    this.$data.cartPromotions = _cart.getItem('promotions');
                }
            },
            updateCart: function(event) {
                if (parseInt($(event.target).val()) < 0) {
                    return;
                }
                var $form = $(event.target).closest('form');
                $.ajax({
                    url: $form.attr('action'),
                    data: $form.serialize(),
                    cache: false,
                    type: $form.attr('method')
                }).done(function (json) {
                    if (window.ContextHub) {
                        ContextHub.getStore('cart').queryService();
                    }
                }).fail(function () {
                    alert('An error occured while trying to perform this operation.');
                });
            }
        }
    });

    var CartComponent = Vue.extend({
        ready: function() {
            this.$expandable = $(this.$el).closest(EXPANDABLE_SELECTOR);
            this.$expandable.addClass(EXPANDABLE_CLASS);
            if (window.ContextHub) {
                ContextHub.eventing.on(ContextHub.Constants.EVENT_STORE_UPDATED + ":cart", this.refreshCart);
            }
        },
        data: function() {
            var _cart = null;
            if (window.ContextHub) {
                _cart = ContextHub.getStore('cart');
            }
            return {
                cartEntriesSize: _cart.getItem('entries') ? _cart.getItem('entries').length : 0
            }
        },
        methods: {
            toggle: function() {
                var $el = this.$expandable;

                if ($el.hasClass(EXPAND_CART_VALUE)) {
                    $el.removeClass(EXPAND_CART_VALUE);
                    $(".we-Cart-content").hide();
                    this.$root.$broadcast('cart-button-expand', false);
                } else {
                    $el.addClass(EXPAND_CART_VALUE);
                    $el.removeClass(EXPAND_SMARTLIST_VALUE);
                    $(".we-Cart-content").show();
                    $(".we-Smartlist-content").hide();
                    this.$root.$broadcast('cart-button-expand', true);
                }
            },
            refreshCart: function(event) {
                if (window.ContextHub) {
                    var _cart = ContextHub.getStore('cart');
                    this.$data.cartEntriesSize = _cart.getItem('entries') ? _cart.getItem('entries').length : 0;
                }
            }
        }
    });

    $('.we-Cart').each(function() {
        new CartComponent().$mount(this);
    });

}).call(this);