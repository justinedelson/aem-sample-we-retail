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
;(function(h,$){
    var PRODUCT_GRID_CLASS = ".productgrid";

    var checkImage = function(hobs, selector, src) {
        return hobs.find(selector).attr("src") == src;
    }

    var checkSKU = function(hobs, selector, SKU) {
        return hobs.find(selector).text().toUpperCase() == SKU;
    }

    window.CQ.WeRetailIT.MenProductsPageLoadTest = function (h, $) {
        return new h.TestCase("Load Men category page")
            .navigateTo("/content/we-retail/us/en/products/men.html")
            .asserts.location("/content/we-retail/us/en/products/men.html", true);
    };

    window.CQ.WeRetailIT.WomenProductsPageLoadTest = function (h, $) {
        return new h.TestCase("Load Women category page")
            .navigateTo("/content/we-retail/us/en/products/women.html")
            .asserts.location("/content/we-retail/us/en/products/women.html", true);
    };

    window.CQ.WeRetailIT.EquipmentProductsPageLoadTest = function (h, $) {
        return new h.TestCase("Load Equipment category page")
            .navigateTo("/content/we-retail/us/en/products/equipment.html")
            .asserts.location("/content/we-retail/us/en/products/equipment.html", true);
    };

    window.CQ.WeRetailIT.ProductPageLoadTest = function (h, $) {
        return new h.TestCase("Load product page")
            .navigateTo("/content/we-retail/us/en/products/men/coats/el-gordo-down-jacket.html")
            .asserts.location("/content/we-retail/us/en/products/men/coats/el-gordo-down-jacket.html", true);
    };

    window.CQ.WeRetailIT.ProductDetailsTest = function (h, $) {
        return new h.TestCase("Check product details")
            // Test image
            .asserts.visible(".we-Product .we-Product-visual img", true)
            .asserts.isTrue(function () {return checkImage(h, ".we-Product .we-Product-visual img", "/content/dam/we-retail/en/products/apparel/coats/El Gordo Green.jpg");})
            // Test product details
            .asserts.visible(".we-Product .we-Product-brand", true)
            .asserts.visible(".we-Product .we-Product-code", true)
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.1-XS");})
            .asserts.visible(".we-Product .we-Product-name", true)
            .asserts.visible(".we-Product .we-Product-price", true)
            .asserts.visible(".we-Product .we-Rating", true)
            .asserts.visible(".we-Product .we-Product-description", true)
            .asserts.visible(".we-Product .we-Product-form", true);
    };

    window.CQ.WeRetailIT.ProductVariationsTest = function (h, $) {
        return new h.TestCase("Check product variations")
            // Test color variations
            .click(".we-Product-radio [name='product.color'][value='red']", {expectNav: false})
            .asserts.isTrue(function () {return checkImage(h, ".we-Product .we-Product-visual img", "/content/dam/we-retail/en/products/apparel/coats/El Gordo Red.jpg");})
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.3-XS");})
            .click(".we-Product-radio [name='product.color'][value='purple']", {expectNav: false})
            .asserts.isTrue(function () {return checkImage(h, ".we-Product .we-Product-visual img", "/content/dam/we-retail/en/products/apparel/coats/El Gordo Purple.jpg");})
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.2-XS");})
            // Test size variations
            .asserts.isTrue(function () {return h.find(".we-Product .we-Product-price").text() == '$119.00';})
            .click(".we-Product-radio [name='product.sku'][value*='-S']")
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.2-S");})
            .click(".we-Product-radio [name='product.sku'][value*='-M']")
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.2-M");})
            .click(".we-Product-radio [name='product.sku'][value*='-L']")
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.2-L");})
            .click(".we-Product-radio [name='product.sku'][value*='-XL']")
            .asserts.isTrue(function () {return checkSKU(h, ".we-Product .we-Product-code span", "MESKWIELT.2-XL");})
            .asserts.isTrue(function () {return h.find(".we-Product .we-Product-price").text() == '$130.00';})
        ;
    };

    new h.TestSuite("We.Retail Tests - Products", {path:"/apps/weretail/tests/product/ProductSuite.js", register: true})
        // Load men products page
        .addTestCase(window.CQ.WeRetailIT.MenProductsPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, PRODUCT_GRID_CLASS, 21))
        // Test first product
        .addTestCase(window.CQ.WeRetailIT.ProductTest(h, $, PRODUCT_GRID_CLASS + " .we-ProductsGrid-item:first"))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))

        // Load women products page
        .addTestCase(window.CQ.WeRetailIT.WomenProductsPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, PRODUCT_GRID_CLASS, 12))
        // Test first product
        .addTestCase(window.CQ.WeRetailIT.ProductTest(h, $, PRODUCT_GRID_CLASS + " .we-ProductsGrid-item:first"))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))

        // Load equipment products page
        .addTestCase(window.CQ.WeRetailIT.EquipmentProductsPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, PRODUCT_GRID_CLASS, 46))
        // Test first product
        .addTestCase(window.CQ.WeRetailIT.ProductTest(h, $, PRODUCT_GRID_CLASS + " .we-ProductsGrid-item:first"))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))

        // Load product page
        .addTestCase(window.CQ.WeRetailIT.ProductPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test breadcrumb
        .addTestCase(window.CQ.WeRetailIT.BreadcrumbTest(h, $, ["Men", "Coats"]))
        // Test product details
        .addTestCase(window.CQ.WeRetailIT.ProductDetailsTest(h, $))
        // Test product variations
        .addTestCase(window.CQ.WeRetailIT.ProductVariationsTest(h, $))
        //TODO: Test cart & favorites
        //TODO: Test reviews section
        //// Test recommended products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, ".productrecommendation " + PRODUCT_GRID_CLASS, 6))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))
    ;
})(hobs, jQuery);