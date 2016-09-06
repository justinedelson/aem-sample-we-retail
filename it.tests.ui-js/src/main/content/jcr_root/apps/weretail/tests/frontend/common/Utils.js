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

window.CQ.WeRetailIT.checkImage = function (hobs, selector, src) {
    //var img = hobs.find(selector);
    //if (img) {
    //    return img.attr("src") == src;
    //} else {
    //    return false;
    //}
    return hobs.find("img[src='"+src+"'")
};

window.CQ.WeRetailIT.checkSKU = function (hobs, selector, SKU) {
    //return hobs.find(selector).text().toUpperCase() == SKU;
    return hobs.find(selector + ":contains(" + SKU + ")")
};

window.CQ.WeRetailIT.checkItems = function(hobs, selector, items) {
    var foundItems = hobs.find(selector);
    if (foundItems.length != items.length) {
        return false;
    }
    foundItems.each(function(ix, val) {
        if (items[ix] != $(val).text().trim()) {
            return false;
        }
    });
    return true;
};

window.CQ.WeRetailIT.checkNumberOfItems = function (hobs, selector, numberOfItems) {
    return hobs.find(selector).length == numberOfItems;
};