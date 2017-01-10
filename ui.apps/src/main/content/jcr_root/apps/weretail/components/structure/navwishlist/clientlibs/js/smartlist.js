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
(function () {
    'use strict';

    var EXPANDABLE_CLASS = 'we-Smartlist-expandable',
        EXPAND_SMARTLIST_VALUE = 'smartlist-expanded',
        EXPAND_CART_VALUE = 'cart-expanded',
        EXPANDABLE_SELECTOR = 'body';

    Vue.component('smartlist-content', {
        ready: function() {
            // move smartlist contents to body
            // so we won't interfere with any mobile styles
            document.body.appendChild(this.$el);
            if (window.ContextHub) {
                ContextHub.eventing.on(ContextHub.Constants.EVENT_STORE_UPDATED + ":smartlists", this.refreshSmartlist);
            }
        },
        data: function() {
            var _smartlists = null;
            if (window.ContextHub) {
                _smartlists = ContextHub.getStore('smartlists');
            }
            return {
                smartlist: _smartlists.getTree()[0],
                smartlistEntriesSize: _smartlists.getTree()[0] ? _smartlists.getTree()[0].entries.length : 0
            }
        },
        methods: {
            refreshSmartlist: function(event) {
                if (window.ContextHub) {
                    var _smartlists = ContextHub.getStore('smartlists');
                    this.$data.smartlist = _smartlists.getTree()[0];
                    this.$data.smartlistEntriesSize = _smartlists.getTree()[0] ? _smartlists.getTree()[0].entries.length : 0;
                }
            },
            updateSmartlist: function(event) {
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
                        ContextHub.getStore('smartlists').queryService();
                    }
                }).fail(function () {
                    alert('An error occured while trying to perform this operation.');
                });
            }
        }
    });

    Vue.component('we-Smartlist-button', {});

    var SmartlistComponent = Vue.extend({
        ready: function() {
            this.$expandable = $(this.$el).closest(EXPANDABLE_SELECTOR);
            this.$expandable.addClass(EXPANDABLE_CLASS);
            if (window.ContextHub) {
                ContextHub.eventing.on(ContextHub.Constants.EVENT_STORE_UPDATED + ":smartlists", this.refreshSmartlist);
            }
            window.smartlistComponent = this;
        },
        data: function() {
            var _smartlists = null;
            if (window.ContextHub) {
                _smartlists = ContextHub.getStore('smartlists');
            }
            return {
                smartlistEntriesSize: _smartlists.getTree()[0] ? _smartlists.getTree()[0].entries.length : 0
            }
        },
        methods: {
            toggle: function() {
                var $el = this.$expandable;

                if ($el.hasClass(EXPAND_SMARTLIST_VALUE)) {
                    $el.removeClass(EXPAND_SMARTLIST_VALUE);
                    $(".we-Smartlist-content").hide();
                    this.$root.$broadcast('smartlist-button-expand', false);
                } else {
                    $el.removeClass(EXPAND_CART_VALUE);
                    $el.addClass(EXPAND_SMARTLIST_VALUE);
                    $(".we-Cart-content").hide();
                    $(".we-Smartlist-content").show()
                    this.$root.$broadcast('smartlist-button-expand', true);
                }
            },
            refreshSmartlist: function(event) {
                if (window.ContextHub) {
                    var _smartlists = ContextHub.getStore('smartlists');
                    this.$data.smartlistEntriesSize = _smartlists.getTree()[0] ? _smartlists.getTree()[0].entries.length : 0;
                }
            },
            show: function() {
                var $el = this.$expandable;

                if (!$el.hasClass(EXPAND_SMARTLIST_VALUE)) {
                    $el.removeClass(EXPAND_CART_VALUE);
                    $el.addClass(EXPAND_SMARTLIST_VALUE);
                    $(".we-Cart-content").hide();
                    $(".we-Smartlist-content").show()
                    this.$root.$broadcast('smartlist-button-expand', true);
                }
            }
        }
    });

    $('.we-Smartlist').each(function() {
        new SmartlistComponent().$mount(this);
    });

}).call(this);