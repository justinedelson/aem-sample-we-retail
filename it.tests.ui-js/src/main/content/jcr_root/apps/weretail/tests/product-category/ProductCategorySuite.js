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

    window.CQ.WeRetailIT.MenPageLoadTest = function (h, $) {
        return new h.TestCase("Load Men category page")
            .navigateTo("/content/we-retail/us/en/men.html")
            .asserts.location("/content/we-retail/us/en/men.html", true);
    };

    window.CQ.WeRetailIT.WomenPageLoadTest = function (h, $) {
        return new h.TestCase("Load Men category page")
            .navigateTo("/content/we-retail/us/en/women.html")
            .asserts.location("/content/we-retail/us/en/women.html", true);
    };

    window.CQ.WeRetailIT.EquipmentPageLoadTest = function (h, $) {
        return new h.TestCase("Load Equipment category page")
            .navigateTo("/content/we-retail/us/en/equipment.html")
            .asserts.location("/content/we-retail/us/en/equipment.html", true);
    };

    new h.TestSuite("We.Retail Tests - Product Category", {path:"/apps/weretail/tests/product-category/ProductCategorySuite.js", register: true})
        // Load men page
        .addTestCase(window.CQ.WeRetailIT.MenPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test hero image
        .addTestCase(window.CQ.WeRetailIT.HeroImageTest(h, $, false))
        // Test featured products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, PRODUCT_GRID_CLASS + ":first", 6))
        .addTestCase(window.CQ.WeRetailIT.ProductTest(h, $, PRODUCT_GRID_CLASS + ":first .we-ProductsGrid-item:first"))
        // Test "All products" button
        .addTestCase(window.CQ.WeRetailIT.ButtonTest(h, $, "div.button a.btn", "All men", "content/we-retail/us/en/products/men.html"))
        // Test featured categories
        .addTestCase(window.CQ.WeRetailIT.TeasersTest(h, $, 2))
        // Test "Top stories"
        .addTestCase(window.CQ.WeRetailIT.ArticlesTest(h, $, 3))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))

        // Load women page
        .addTestCase(window.CQ.WeRetailIT.WomenPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test hero image
        .addTestCase(window.CQ.WeRetailIT.HeroImageTest(h, $, false))
        // Test featured products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, PRODUCT_GRID_CLASS + ":first", 6))
        .addTestCase(window.CQ.WeRetailIT.ProductTest(h, $, PRODUCT_GRID_CLASS + ":first .we-ProductsGrid-item:first"))
        // Test "All products" button
        .addTestCase(window.CQ.WeRetailIT.ButtonTest(h, $, "div.button a.btn", "All women", "content/we-retail/us/en/products/women.html"))
        // Test featured categories
        .addTestCase(window.CQ.WeRetailIT.TeasersTest(h, $, 2))
        // Test "Top stories"
        .addTestCase(window.CQ.WeRetailIT.ArticlesTest(h, $, 3))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))

        // Load equipment page
        .addTestCase(window.CQ.WeRetailIT.EquipmentPageLoadTest(h, $))
        // Test navbar
        .addTestCase(window.CQ.WeRetailIT.NavbarTest(h, $))
        // Test hero image
        .addTestCase(window.CQ.WeRetailIT.HeroImageTest(h, $, false))
        // Test featured categories
        .addTestCase(window.CQ.WeRetailIT.TeasersTest(h, $, 7))
        // Test featured products
        .addTestCase(window.CQ.WeRetailIT.ProductsGridTest(h, $, PRODUCT_GRID_CLASS + ":first", 6))
        .addTestCase(window.CQ.WeRetailIT.ProductTest(h, $, PRODUCT_GRID_CLASS + ":first .we-ProductsGrid-item:first"))
        // Test "All products" button
        .addTestCase(window.CQ.WeRetailIT.ButtonTest(h, $, "div.button a.btn", "All equipment", "content/we-retail/us/en/products/equipment.html"))
        // Test footer
        .addTestCase(window.CQ.WeRetailIT.FooterTest(h, $))
    ;
})(hobs, jQuery);