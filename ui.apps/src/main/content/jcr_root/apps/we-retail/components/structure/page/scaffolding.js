"use strict";

use(function() {
    var scaffoldHostPath = "/libs/wcm/core/content/editor/scaffoldhost.html",
        resourceResolver = resource.getResourceResolver();

    // first check if the page has a scaffold specified
    var scaffoldPath = pageProperties.get("cq:scaffolding", "");
    if (scaffoldPath.length() == 0) {
        // search all scaffolds for the correct template
        // this should be improved and respect template + best content path
        var scRoot = resourceResolver.getResource("/etc/scaffolding");
        var root = scRoot == null ? null : scRoot.adaptTo(javax.jcr.Node);
        if (root != null) {
            scaffoldPath = com.day.cq.wcm.core.utils.ScaffoldingUtils.findScaffoldByTemplate(root, pageProperties.get("cq:template", ""));
            if (scaffoldPath == null) {
                scaffoldPath = com.day.cq.wcm.core.utils.ScaffoldingUtils.findScaffoldByPath(root, currentPage.getPath());
            }
        }
    }
    if (scaffoldPath == null || scaffoldPath.length() == 0) {
        // use default
        scaffoldPath = "/etc/scaffolding";
    }
    scaffoldPath += "/jcr:content";
    if (resourceResolver.getResource(scaffoldPath + "/cq:dialog") != null) {
        request.setAttribute(com.adobe.granite.ui.components.Value.CONTENTPATH_ATTRIBUTE, currentPage.getPath());
        try {
            sling.include(scaffoldHostPath + scaffoldPath);
        } finally {
            request.removeAttribute(com.adobe.granite.ui.components.Value.CONTENTPATH_ATTRIBUTE);
        }


    }

    return {
        scaffoldPath: scaffoldPath
    }
});