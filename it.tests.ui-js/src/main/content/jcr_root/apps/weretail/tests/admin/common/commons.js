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

;(function(h, $) {

    // don't remove this setting
    hobs.config.pacing_delay = 1;

    // shortcut
    var c = window.CQ.WeRetailIT.commons;

    // the root page defined in the test content package
    c.rootPage = "/content/we-retail/language-masters/en";
    c.testPage ="/content/we-retail/language-masters/en/experience/arctic-surfing-in-lofoten"
    // the template defined in the test content package
    c.template = "/conf/core-components/settings/wcm/templates/core-components";
    // relative path from page node to the root layout container
    c.relParentCompPath = "/jcr:content/root/responsivegrid/";
    // breadcrumb component
    c.rtBreadcrumb = "weretail/components/content/breadcrumb";
    c.rtIimage = "weretail/components/content/image"

    // configuration dialog
    c.selConfigDialog = ".cq-dialog.foundation-form.foundation-layout-form";
    // save button on a configuration dialog
    c.selSaveConfDialogButton = ".cq-dialog-actions button[is='coral-button'][title='Done']";

    c.addComponent = function (component, parentCompPath, dynParName, done, nameHint, order) {
        // mandatory check
        if (component == null || parentCompPath == null || done == null) {
            if (done) done(false, "addComponent failed! mandatory parameter(s) missing!");
            return;
        }

        // default settings
        if (nameHint == null) nameHint = component.substring(component.lastIndexOf("/") + 1);
        if (order == null) order = "last";

        // the ajax call
        jQuery.ajax({
            url: parentCompPath,
            method: "POST",
            data: {
                "./sling:resourceType": component,
                ":order": order,
                "_charset_": "utf-8",
                ":nameHint": nameHint

            }
        })
            .done(function (data, textStatus, jqXHR) {
                // extract the component path from the returned HTML
                if (dynParName != null) {
                    h.param(dynParName, jQuery(data).find("#Path").text());
                }
            })
            // in case of failure
            .fail(function (jqXHR, textStatus, errorThrown) {
                done(false, "addComponent failed: POST failed with " + textStatus + "," + errorThrown);
            })
            // always executed, fail or success
            .then(function () {
                done(true);
            })
    };

    c.deleteComponent = function (component_path, done) {
        // mandatory check
        if (component_path == null || done == null) {
            if (done) done(false, "deleteComponent failed! mandatory parameter(s) missing!");
            return;
        }

        // the ajax call
        jQuery.ajax({
            url: component_path,
            method: "POST",
            data: {
                ":operation": "delete"
            }
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            done(false, "deletePolicy failed: POST failed with " + textStatus + "," + errorThrown);
        })
        // always executed, fail or success
        .then(function () {
            done(true);
        })
    };

    /**
     * returns the content frame.
     */
    c.getContentFrame = function () {
        return h.find('iframe#ContentFrame').get(0);
    };

    c.closeSidePanel = new hobs.TestCase("Close side panel", {timeout: 2000})
        .ifElse(function (opts) {
                var clickToggle = hobs.find('.toggle-sidepanel.editor-GlobalBar-item').length > 0 &&
                    hobs.find('#SidePanel.sidepanel-opened').length > 0 &&
                    hobs.find('.toggle-sidepanel.editor-GlobalBar-item').length > 0;
                return clickToggle;
            },
            click('.toggle-sidepanel.editor-GlobalBar-item')
        );

    c.disableTutorials = new hobs.TestCase("Disable Tutorials (preferences)")
        .execFct(function (opts, done) {
                // set language to config locale value
                var result = Granite.HTTP.eval("/libs/granite/csrf/token.json");
                var user = Granite.HTTP.eval(hobs.config.context_path + "/libs/cq/security/userinfo.json");
                var data = {
                    "cq.authoring.editor.page.showTour62": false,
                    "cq.authoring.editor.page.showOnboarding62": false,
                    "cq.authoring.editor.template.showTour": false,
                    "cq.authoring.editor.template.showOnboarding": false,
                    "granite.shell.showonboarding620": false
                };
                data[':cq_csrf_token'] = result.token;
                jQuery.post(hobs.config.context_path + user.home + "/preferences", data)
                    .always(function () {
                        done();
                    });
            }
    );

    c.openSidePanel = function (done) {

        maxRetries = 5;
        timeout = 500;

        // retry counter
        var retries = 0;

        // the polling function
        var poll = function () {
            // if the panel is closed
            if (h.find("#SidePanel.sidepanel-closed").size() == 1) {
                // click on the toggle button, wait for the click to finish, then check
                click(".toggle-sidepanel.editor-GlobalBar-item").exec().then(
                    function () {
                        // check if the panel is open
                        if (h.find("#SidePanel.sidepanel-opened").size() == 1) {
                            done(true);
                        } else {
                            // check if max retries was reached
                            if (retries++ === maxRetries) {
                                done(false, "Opening the Side Panel failed!");
                                return;
                            }
                            // set for next retry
                            setTimeout(poll, timeout);
                        }
                    }
                )
            }
            done();

        };
        // start polling
        poll();
    };

    /**
    * Common stuff that should be done before each test case starts.
    */
    c.tcExecuteBeforeTest = new TestCase("Common Set up")
        // reset the context
        .config.resetContext()

        .navigateTo("/content/we-retail/language-masters/en.html")
        .execFct(function(opts) {
            // make sure we start in edit mode
            $.cookie('cq-editor-layer.page', 'Edit', { path : "/" });
            // make sure the  side panel starts  closed
            $.cookie('cq-editor-sidepanel', 'closed', { path : "/" });
        })
        // editing cookies requires a reload
        .reloadPage();

    /**
    * Common stuff that should be done at the end of each test case.
     */
    c.tcExecuteAfterTest = new TestCase("Common Clean Up")
    // reset the context
        .config.resetContext()
        // make sure the side panel is closed
        .execTestCase(c.closeSidePanel);

    /**
    * Stuff that should be done before a testsuite starts
     */
    c.tcExecuteBeforeTestSuite =  new TestCase("Setup Before Testsuite")
    // disable tutorial popups
        .execTestCase(c.disableTutorials);

    /**
     * Opens the configuration dialog for a component. Uses 'data-path' attribute to identify the correct
     * component.
     *
     * @param cmpPath   mandatory. the absolute component path, used as the value for 'data-path' attribue
     */
    c.tcOpenConfigureDialog = function(cmpPath) {
        return new TestCase("Open Configure Dialog")
        //click on the component to see the Editable Toolbar
            .click(".cq-Overlay.cq-draggable.cq-droptarget%dataPath%",{
                before: function() {
                    // set the data-path attribute so we target the correct component
                    h.param("dataPath", "[data-path='" + h.param(cmpPath)() + "']");
                }
            })

            // make sure its visible
            .asserts.visible("#EditableToolbar")
            // click on the 'configure' button
            .click("button.cq-editable-action[is='coral-button'][title='Configure']")
            // verify the dialog has become visible
            .asserts.visible(c.selConfigDialog);
    };

    /**
     * Closes any open configuration dialog
     */
    c.tcSaveConfigureDialog = new TestCase ("Save Configure Dialog")
    // if full Screen mode was used make sure the click waits for the navigation change
        .ifElse(
            // check if the dialog opened in a different URL
            function(){ return hobs.context().window.location.pathname.startsWith("/mnt/override")}
            ,
            TestCase("Close Fullscreen Dialog")
            // NOTE: this will cause test to fail if the dialog can't be closed e.g. due to missing mandatory values
                .click(c.selSaveConfDialogButton,{expectNav:true})
            ,
            TestCase("Close Modal Dialog")
                .click(c.selSaveConfDialogButton,{expectNav:false})
            ,{ timeout:10 });

}(hobs, jQuery));
