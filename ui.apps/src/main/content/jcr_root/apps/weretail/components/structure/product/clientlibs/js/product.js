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
            
            if (!!parseInt(self.isBase, 10)) {
                self.$parent.defaultProduct = data;
            }
        }
    });

    if (document.querySelector('.we-Product')) {
        new Vue({
            name: 'we-Product',
            el: '.we-Product',
            data: {
                variants: [],
                defaultProduct: null,
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
                this.processHash();
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
                setProduct: function(event) {
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
                processHash: function() {
                    var done = false;
                    if (window.location.hash) {
                        var self = this;
                        var sku = window.location.hash.slice(1);
                        this.variants.forEach(function (product) {
                            if (done) {
                                return;
                            }
                            
                            if (sku == product.sku) {
                                self.product = product;
                                self.variantAxes = JSON.parse(JSON.stringify(product.variantAxes));
                                done = true;
                            }
                        });
                    }
                    
                    // if we didn't get a valid hash, we fallback to the default base product
                    if (!done) {
                        self.product = self.defaultProduct;
                        self.variantAxes = JSON.parse(JSON.stringify(self.defaultProduct.variantAxes));
                        history.pushState(null, null, '#' + self.defaultProduct.sku);
                    }
                }
            }
        });
    }

}).call(this);
