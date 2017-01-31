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
(function ($) {

    /**
     * Hides or Shows the ui elements depending on whether there is a current logged in user
     * or the current user is anonymous ( there is no logged in user)
     * Needed for making the header elements dispatcher compatible
     * @param currentUser The id of the current logged in user
     * (should be anonymous if there is no logged in user)
     */
    function toggleHeaderElements(currentUser) {
        if (currentUser === "anonymous") {
            $(".we-retail-anonymous").removeClass("hidden");
            $(".we-retail-not-anonymous").addClass("hidden");
        } else {
            $(".we-retail-not-anonymous").removeClass("hidden");
            $(".we-retail-anonymous").addClass("hidden");
        }
    }

    $(function () {
        var wcmmodeDisabled = typeof $(".we-retail-header").data("wcmmodeDisabled") != "undefined";
        if (wcmmodeDisabled) {
            $.ajax({
                type: "GET",
                url: "/libs/granite/security/currentuser.json",
                async: true,
                success: function (json) {
                    // toggle visibility of header elements as per the current logged in user
                    toggleHeaderElements(json['authorizableId']);

                    // On publish: load the request user into ContextHub
                    if (ContextHub) {
                        var profileStore = ContextHub.getStore('profile');
                        var requestUser = json["home"];
                        var contextHubUser = profileStore.getTree().path;
                        if (!contextHubUser || contextHubUser !== requestUser) {
                            profileStore.loadProfile(requestUser);
                        }
                    }
                }
            });
        } else {
            // toggle visibility of header elements as per the current user stored in the ContextHub
            if (ContextHub) {
                toggleHeaderElements(ContextHub.getStore("profile").getItem("authorizableId"));
            }
        }

        // toggle visibility of header elements when the current user changes in ContextHub
        // such as when simulating different personas
        if (ContextHub) {
            ContextHub.eventing.on(ContextHub.Constants.EVENT_STORE_UPDATED + ":profile", function () {
                var profileStore = ContextHub.getStore("profile");
                toggleHeaderElements(profileStore.getItem("authorizableId"));
            }, null);
        }
    });
})(jQuery);