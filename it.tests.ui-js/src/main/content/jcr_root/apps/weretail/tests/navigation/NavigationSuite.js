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

    window.CQ.WeRetailIT.ExperienceNavigationTest = function (h, $) {
        return new h.TestCase("Navigation to experiences")

            // Navigate to Experience section and back using top logo

            .click(".navbar a:contains(Experience)", {expectNav: true})
            .asserts.location("/content/we-retail/us/en/experience.html", true)

            .click("a.navbar-brand", {expectNav: true})
            .asserts.location("/content/we-retail/us/en.html", true)

            // Navigate to Experience page and back using top logo

            .click(".navbar a:contains(Experience)", {expectNav: true})
            .asserts.location("/content/we-retail/us/en/experience.html", true)

            .click(".we-ArticleTeaser a:contains(Arctic Surfing)", {expectNav: true})
            .asserts.location("/content/we-retail/us/en/experience/arctic-surfing-in-lofoten.html", true)

            .click("a.navbar-brand", {expectNav: true})
            .asserts.location("/content/we-retail/us/en.html", true)

            // Navigate to Experience page and back to Experience section using breadcrumbs

            .click(".navbar a:contains(Experience)", {expectNav: true})
            .asserts.location("/content/we-retail/us/en/experience.html", true)

            .click(".we-ArticleTeaser a:contains(Arctic Surfing)", {expectNav: true})
            .asserts.location("/content/we-retail/us/en/experience/arctic-surfing-in-lofoten.html", true)

            .click(".breadcrumb a:contains(Experience)")
            .asserts.location("/content/we-retail/us/en/experience.html", true)

            // Navigate to Experience page and back to Homepage using breadcrumbs

            .click(".we-ArticleTeaser a:contains(Arctic Surfing)", {expectNav: true})
            .asserts.location("/content/we-retail/us/en/experience/arctic-surfing-in-lofoten.html", true)

            .click(".breadcrumb a:contains(English)")
            .asserts.location("/content/we-retail/us/en.html", true)
        ;
    }

    new h.TestSuite("We.Retail Tests - Navigation", {path:"/apps/weretail/tests/navigation/NavigationSuite.js", register: true})
        .addTestCase(window.CQ.WeRetailIT.HomepageLoadTest(h, $))
        .addTestCase(window.CQ.WeRetailIT.ExperienceNavigationTest(h, $))
    ;
})(hobs, jQuery);
