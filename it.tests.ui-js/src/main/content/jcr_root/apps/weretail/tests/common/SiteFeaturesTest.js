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
    window.CQ.WeRetailIT.SiteFeaturesTest = function (h, $) {
        return new h.TestCase("Check site features")
            // Check features are visible
            .asserts.visible(".site-feature", true)
            // Check features title/text is visible
            .asserts.visible(".site-feature h3", true)
            // Check there are three features blocks
            .asserts.isTrue(function() {return h.find(".site-feature").length == 3;})
            // Check titles and subtitles not empty
            .asserts.isTrue(function() {
                var res = true;
                h.find(".site-feature h3").each(function(ix, val){
                    res = res && $(val).text().trim().length > 0;
                });
                return res;
            });
    }
})(hobs);