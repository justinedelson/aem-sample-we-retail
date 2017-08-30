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
    var image = window.CQ.WeRetailIT.Image;

    var testPage = c.testPage+".html"
    var testImagePath = "/content/dam/we-retail-screens/we-retail-instore-logo.png";
    var altText = "Return to Arkham";
    var captionText = "The Last Guardian";

    /**
     * Before Test Case
     */
    image.tcExecuteBeforeTest = function(imageRT, pageRT) {
        return new TestCase("Setup Before Test")
        // common set up
            .execTestCase(c.tcExecuteBeforeTest)
            // add the component, store component path in 'cmpPath'
            .execFct(function (opts, done){
                c.addComponent(imageRT, c.testPage+c.relParentCompPath, "cmpPath", done)
            })
            // open the new page in the editor
            .navigateTo("/editor.html"+c.testPage+".html");
    };

    /**
     * After Test Case
     */
    image.tcExecuteAfterTest = function () {
        return new TestCase("Clean up after Test")
        // common clean up
            .execTestCase(c.tcExecuteAfterTest)
            // delete the component we added to the page
            .execFct(function (opts, done) {
                c.deleteComponent(h.param("cmpPath")(opts), done);
            })
    };

    /**
     * Test: minimal properties
     */
    image.tcSetMinimalProps = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase("Set Image and Alt Text")
            .execFct(function (opts,done) {c.openSidePanel(done);})
            // drag'n'drop the test image
            .cui.dragdrop("coral-card.cq-draggable[data-path='" + testImagePath + "']","coral-fileupload[name='./file'")
            // set mandatory alt text
            .fillInput("input[name='./alt']",altText)
            // close the side panel
            .execTestCase(c.closeSidePanel);
    };

    /**
     * Test: add image
     */
    image.tcAddImage = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase('Add an Image',{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set image and alt text
            .execTestCase(image.tcSetMinimalProps(tcExecuteBeforeTest, tcExecuteAfterTest))
            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // verify that the surrounding script tag has been removed and the img tag is there
            .asserts.isTrue(function () {
                return h.find("div.cmp-image img[src*='"+ c.testPage +
                        "/jcr%3acontent/root/responsivegrid/image.img.'","#ContentFrame").size() ===1;
            });
    };

    /**
     * Test: set Alt Text
     */
    image.tcAddAltText = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase('Set Alt Text',{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set image and alt text
            .execTestCase(image.tcSetMinimalProps(tcExecuteBeforeTest, tcExecuteAfterTest))
            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // verify that alt text is there
            .asserts.isTrue(function () {
                return h.find("div.cmp-image img[alt='"+altText +"']", "#ContentFrame").size() == 1;
            });
    };

    /**
     * Test: set link on image
     */
    image.tcSetLink = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase('Set Link',{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set image and alt text
            .execTestCase(image.tcSetMinimalProps(tcExecuteBeforeTest, tcExecuteAfterTest))
            // enter the link
            .simulate("foundation-autocomplete[name='./linkURL'] input[type!='hidden']", "key-sequence",
                {sequence: c.rootPage + "{enter}"})
            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to content frame
            .config.changeContext(c.getContentFrame)
            // click on the image
            .click("div.cmp-image [data-title='Return to Arkham']",{expectNav: true})
            // go back to top frame
            .config.resetContext()
            // check if the url is correct
            .asserts.isTrue(function(){
                return hobs.context().window.location.pathname.endsWith(c.rootPage + ".html")
            });
    };

    /**
     * Test: set caption
     */
    image.tcSetCaption = function(titleSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase('Set Caption',{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set image and alt text
            .execTestCase(image.tcSetMinimalProps(tcExecuteBeforeTest, tcExecuteAfterTest))
            // set caption text
            .fillInput("input[name='./jcr:title']",captionText)
            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to content frame
            .config.changeContext(c.getContentFrame)
            // check if the caption is rendered with <small> tag
            .asserts.isTrue(function(){
                return h.find(titleSelector + ":contains('" + captionText + "')").size() == 1
            });
    };

    /**
     * Test: set caption as pop up
     */
    image.tcSetCaptionAsPopup = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase('Set Caption as Pop Up',{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set image and alt text
            .execTestCase(image.tcSetMinimalProps(tcExecuteBeforeTest, tcExecuteAfterTest))
            // set caption text
            .fillInput("input[name='./jcr:title']",captionText)
            // check the 'Caption as Pop Up' flag
            .click("input[type='checkbox'][name='./displayPopupTitle']")
            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to content frame
            .config.changeContext(c.getContentFrame)
            // check if the caption is rendered with <small> tag
            .asserts.isTrue(function(){
                return h.find("div.cmp-image img[title='" + captionText + "']").size() == 1
            });
    };

    /**
     * Test: set caption as pop up
     */
    image.tcSetImageAsDecorative = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase('Set Image as decorative',{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest})

        // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set image and alt text (to see if its not rendered)
            .execTestCase(image.tcSetMinimalProps(tcExecuteBeforeTest, tcExecuteAfterTest))
            .click("input[type='checkbox'][name='./isDecorative']")
            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to content frame
            .config.changeContext(c.getContentFrame)
            // check if the image is rendered without alt text even if it is set in the edit dialog

            .asserts.isTrue(function () {
                return h.find('div.cmp-image img').attr('alt') === '';
            });
    };

    /**
     * v1 specifics
     */
    var titleSelector = "span.cmp-image__title";
    var tcExecuteBeforeTest = image.tcExecuteBeforeTest(c.rtIimage);
    var tcExecuteAfterTest = image.tcExecuteAfterTest();

    /**
     * The main test suite for Image Component
     */
    new h.TestSuite("We.Retail Tests - Image", {path: '/apps/weretail/tests/admin/components-it/Image.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(image.tcAddImage(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcAddAltText(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetLink(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetCaption(titleSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetCaptionAsPopup(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetImageAsDecorative(tcExecuteBeforeTest, tcExecuteAfterTest))

    ;

}(hobs, jQuery));