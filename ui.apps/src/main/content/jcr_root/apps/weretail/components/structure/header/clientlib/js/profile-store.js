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
    $(function () {
        var wcmmodeDisabled = typeof $(".we-retail-header").data("wcmmodeDisabled") != "undefined";
        // On publish: load the request user into ContextHub
        if (wcmmodeDisabled) {
            $.ajax({
                type: "GET",
                url: "/libs/granite/security/currentuser.json",
                async: true,
                success: function(json) {
                    var profileStore = ContextHub.getStore('profile');
                    var requestUser = json["home"];
                    var contextHubUser = profileStore.getTree().path;
                    if (!contextHubUser || contextHubUser !== requestUser) {
                        profileStore.loadProfile(requestUser);
                    }
                }
            });
        }
    });
})(jQuery);