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

    Vue.component('we-product-variant', {
        props: [
            'isBase',
            'path',
            'pagePath',
            'variants',
            'sku',
            'title',
            'description',
            'price',
            'summary',
            'features',
            'image',
            'variantAxes'
        ],
        compiled: function () {
            var self = this, data = {};

            Object.getOwnPropertyNames(this._props).forEach(function (prop) {
                if (prop == 'variantAxes') {
                    data[prop] = JSON.parse(self[prop]);
                }
                else {
                    data[prop] = self[prop];
                }
            });

            self.$parent.variants.push(data);

            if (window.location.hash) {
                var sku = window.location.hash.slice(1);
                if (sku == self.sku) {
                    self.$parent.product = data;
                    self.$parent.variantAxes = JSON.parse(JSON.stringify(data.variantAxes));
                }
            }
            else if (!!parseInt(self.isBase, 10)) {
                self.$parent.product = data;
                self.$parent.variantAxes = JSON.parse(JSON.stringify(data.variantAxes));
                history.pushState(null, null, '#' + data.sku);
            }
        }
    });

    if (document.querySelector('.we-Product')) {
        new Vue({
            name: 'we-Product',
            el: '.we-Product',
            data: {
                variants: [],
                product: null,
                variantAxes: null,

                isChecked: function(name, value) {
                    return this.product.variantAxes[name] == value;
                }
            },
            props: [
                'sku',
                'title',
                'pagePath'
            ],
            ready: function() {
                this.trackView();
            },
            methods: {
                _setProduct: function(name, value) {
                    var self = this;
                    self.variantAxes[name] = value;

                    var done = false;
                    self.variants.forEach(function (product) {
                        if (done) {
                            return;
                        }
                        
                        var ok = true;
                        for (var key in self.variantAxes) {
                            if (self.variantAxes.hasOwnProperty(key)) {
                                if (product.variantAxes[key] != self.variantAxes[key]) {
                                    ok = false;
                                    break;
                                }
                            }
                        }
                        
                        if (ok)
                        {
                            done = true;
                            self.product = product;
                            history.pushState(null, null, '#' + product.sku);
                        }
                    });
                },
                setProduct: function (event) {
                    var name = event.currentTarget.attributes['name'].value;
                    var value = event.currentTarget.attributes['value'].value;
                    this._setProduct(name, value);
                },
                trackView: function() {
                    if (this.product && window.ContextHub && ContextHub.getStore("recentlyviewed")) {
                        ContextHub.getStore("recentlyviewed").record(
                            this.pagePath,
                            this.product.title,
                            this.product.image,
                            this.product.price
                        );
                    }

                    if (this.product && window.CQ_Analytics && CQ_Analytics.ViewedProducts) {
                        CQ_Analytics.ViewedProducts.record(
                            this.pagePath,
                            this.product.title,
                            this.product.image,
                            this.product.price
                        );
                    }
                },
                toggleWishlist: function (event) {
                    event.preventDefault();
                    if (this.product) {
                        var smartListUrl = event.currentTarget.getAttribute("data-smartlist-url");
                        var data = {
                            ":operation": this.isFavorited ? "deleteSmartListEntry" : "addToSmartList",
                            "product-path": this.product.path,
                            "redirect": this.product.pagePath
                        };
                        if (event.currentTarget.hasAttribute("data-smartlist")) {
                            data["smartlist-path"] = event.currentTarget.getAttribute("data-smartlist");
                        }

                        $.ajax({
                            type: "POST", data: data, url: smartListUrl
                        });
                        this.isFavorited = !this.isFavorited;
                    }
                }
            }
        });
    }

}).call(this);
