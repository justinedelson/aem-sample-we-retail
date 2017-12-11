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
;(function(h) {
    var ARTICLES_LIST_CLASS = ".we-ArticlesList";

    window.CQ.WeRetailIT.ArticlesTest = function (h, $, count) {
        return new h.TestCase("Check articles")
            // Check articles list is visible
            .asserts.visible(ARTICLES_LIST_CLASS, true)
            // Check articles are visible
            .asserts.visible(ARTICLES_LIST_CLASS + " .we-ArticleTeaser")
            // Check visuals
            .asserts.visible(ARTICLES_LIST_CLASS + " .we-ArticleTeaser .we-ArticleTeaser-visual img")
            // Check titles
            .asserts.visible(ARTICLES_LIST_CLASS + " .we-ArticleTeaser .we-ArticleTeaser-title")
            // Check articles count
            .asserts.isTrue( function() { return window.CQ.WeRetailIT.checkNumberOfItems(h, ARTICLES_LIST_CLASS + " .we-ArticleTeaser", count) });
    }
})(hobs);