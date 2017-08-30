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

;(function(h, $){


    // shortcuts
    var c = window.CQ.WeRetailIT.commons;
    var breadcrumb = window.CQ.WeRetailIT.Breadcrumb;
    var testPage = c.testPage+".html"

    /**
     * Before Test Case
     */
    breadcrumb.tcExecuteBeforeTest = function(breadcrumbRT, pageRT) {
        return new h.TestCase("Create Sample Content")
            // common set up
            .execTestCase(c.tcExecuteBeforeTest)

            // add the component to the deepest level
            .execFct(function (opts, done){
                c.addComponent(breadcrumbRT, c.testPage+c.relParentCompPath, "cmpPath", done)
            })

            // open the deepest level in the editor
            .navigateTo("/editor.html"+c.testPage+".html");
    };

    /**
     * After Test Case
     */
    breadcrumb.tcExecuteAfterTest = function() {
        return new TestCase("Clean up after Test")
            // common clean up
            .execTestCase(c.tcExecuteAfterTest)
            // delete the component we added to the page
            .execFct(function (opts, done) {
                c.deleteComponent(h.param("cmpPath")(opts), done);
            });
    };

    /**
     * Test: Set the Hide Current flag
     */
    breadcrumb.testHideCurrent = function(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check Hide Current Flag",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

            // check first if current page is shown
            .config.changeContext(c.getContentFrame)
            // the li entry for current page
            .assert.exist(itemSelector.active + ":contains('Arctic Surfing In Lofoten')",true)
            .config.resetContext()

            // Open the configuration dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // check the checkbox th make the current page hidden
            .click("input[name='./hideCurrent']")
            // Close the configuration dialog
            .execTestCase(c.tcSaveConfigureDialog)
            // got to the content frame
            .config.changeContext(c.getContentFrame)

            // the li entry for current page should not be found
            .assert.exist(itemSelector.active + ":contains('Arctic Surfing In Lofoten')", false);
    };

    /**
     * Test: Set the Show Hidden flag
     */
    breadcrumb.testShowHidden = function(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase("Check Show Hidden Flag",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

            // go to content frame
            .config.changeContext(c.getContentFrame)
            // verify level 3 is no longer available
            .assert.exist(itemSelector.normal + " > a:contains('Language Masters')",false)
            // go back to edit frame
            .config.resetContext()

            // Open the configuration dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // check the checkbox to show hidden pages
            .click("input[name='./showHidden']")
            // Close the configuration dialog
            .execTestCase(c.tcSaveConfigureDialog)
            // got to the content frame
            .config.changeContext(c.getContentFrame)

            // the level 3 should be visible again
            .assert.exist(itemSelector.normal + " > a:contains('Language Masters')",true);
    };

    /**
     * Test: Change the start level
     */
    breadcrumb.changeStartLevel = function(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase("Change Start Level",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

            // check the current number of parent levels
            .assert.isTrue(function(){
                return h.find(itemSelector.normal,"iframe#ContentFrame").size() === 5})

            // Open the configuration dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // check the current config setting
            .assert.isTrue(function(){
                return h.find("input[name='./startLevel']").val() == 2})
            // increase start level by 2
            .fillInput("input[name='./startLevel']", 4)
            // Close the configuration dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // check the current number
            .assert.isTrue(function(){
                return h.find(itemSelector.normal,"iframe#ContentFrame").size() === 4});
    };

    /**
     * Test: Set the start level to lowest allowed value of 0.
     * This shouldn't render anything since level 0 is not a valid page.
     */
    breadcrumb.setZeroStartLevel = function(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase("Set Start Level to 0",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // check the current number of items
            .assert.isTrue(function(){
                return h.find(itemSelector.normal,"iframe#ContentFrame").size() == 5})

            // Open the configuration dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set it to 0
            .fillInput("input[name='./startLevel']", 0)
            // Close the configuration dialog
            .asserts.visible(c.selConfigDialog)
            // check if element name is marked as invalid
            .asserts.isTrue(function() {
                return h.find("input[name='./startLevel'].is-invalid").size() == 1
            });
    };

    /**
     * Test: Set the start level to the highest possible value 100.
     * This shouldn't render anything since level 100 is higher the the current's page level.
     */
    breadcrumb.set100StartLevel = function(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase("Set Start Level to 100",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // check the current number of items
            .assert.isTrue(function(){
                return h.find(itemSelector.normal,"iframe#ContentFrame").size() === 5})
            // Open the configuration dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set it to 100
            .fillInput("input[name='./startLevel']", 100)
            // Close the configuration dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // 100 is higher then current level so nothing should get rendered
            .assert.isTrue(function(){
                return h.find(itemSelector.normal,"iframe#ContentFrame").size() === 2 &&
                    h.find(itemSelector.active,"iframe#ContentFrame").size() === 0
            });
    };

    /**
     * test specifics
     */
    var itemSelector = {
        normal: ".cmp-breadcrumb__item",
        active: ".cmp-breadcrumb__item--active"
    };
    var tcExecuteBeforeTest = breadcrumb.tcExecuteBeforeTest(c.rtBreadcrumb);
    var tcExecuteAfterTest = breadcrumb.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("We.Retail Tests - Breadcrumb", {path:"/apps/weretail/tests/admin/components-it/Breadcrumb.js", register: true,
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(breadcrumb.testHideCurrent(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.testShowHidden(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.changeStartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.setZeroStartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.set100StartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));
