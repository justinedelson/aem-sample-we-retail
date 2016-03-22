<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

--%><%@page session="false"
            import="com.day.cq.commons.jcr.JcrConstants,
                    com.day.cq.commons.jcr.JcrUtil,
                    com.day.cq.dam.api.Asset,
                    com.day.cq.dam.api.s7dam.utils.PublishUtils,
                	com.day.cq.dam.commons.util.DynamicMediaServicesConfigUtil,
                    com.day.cq.dam.scene7.api.constants.Scene7Constants,
                    com.day.cq.dam.scene7.api.net.NetUtils,
                    com.day.cq.i18n.I18n,
                    com.day.cq.wcm.api.WCMMode,
                    org.apache.sling.api.resource.PersistableValueMap,
                    java.util.Locale,
                    java.util.ResourceBundle,
					javax.jcr.Node,
                  	javax.jcr.RepositoryException,
                  	javax.jcr.Session,
                    com.day.cq.wcm.foundation.Placeholder,
                    com.day.cq.wcm.api.components.DropTarget,
            	com.day.cq.dam.commons.util.DynamicMediaHelper"
%><%@include file="/libs/foundation/global.jsp"%>
<%
    Locale pageLocale = currentPage.getLanguage(true);
    ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLocale);
    I18n i18n = new I18n(resourceBundle);

   	String idPrefix = "id-" + resource.getPath().replaceAll("[^0-9a-z_]", "_");
    if (idPrefix.length() > 64) {
        idPrefix = idPrefix.substring(idPrefix.length() - 64);
    }
    String viewerInstanceId = xssAPI.encodeForHTMLAttr(idPrefix);
    viewerInstanceId = viewerInstanceId.replace("-","_");

	Session currentSession  = resourceResolver.adaptTo(Session.class);

    String viewerPath = getViewerRootPath(currentSession, request.getContextPath());

	//Get placeholder
	String ddClassName = DropTarget.CSS_CLASS_PREFIX + "image";
    String classicPlaceholder = "<div class=\"" + ddClassName
                              + (WCMMode.fromRequest(request) == WCMMode.EDIT ? " cq-image-placeholder" : "")
                              + "\"></div>";
    String placeholder = Placeholder.getDefaultPlaceholder(slingRequest, component, classicPlaceholder, ddClassName);




	//Get settings
    String width = properties.get("width","-1");
    String height = properties.get("height","-1");
	String stageSize = "";
    String title = properties.get("./jcr:title", String.class);
    String altText = properties.get("./alt", String.class);
    String viewerPreset = properties.get("s7ViewerPreset", currentStyle.get("s7ViewerPreset", String.class));
    String fallbackViewerPreset = properties.get("s7ViewerPresetFallback", currentStyle.get("s7ViewerPresetFallback", String.class));
	String imagePreset = properties.get("s7ImagePreset", String.class);
    String breakpoints = properties.get("breakpoints","");
	String linkUrl = properties.get("./linkUrl","");
	String linkTarget = properties.get("./linkTarget","");
    String urlModifiers = properties.get("./urlModifiers","");
    String fileReference = properties.get("fileReference",String.class);
    String productionImageServerUrl = properties.get("imageserverurl",String.class);
    String imageServerUrl = request.getContextPath() + "/is/image/";
    String productionVideoServerUrl = properties.get("videoserverurl",String.class);
    String videoServerUrl = "";
	String assetType = null;
	boolean enabledDynamicMedia = DynamicMediaHelper.isDynamicMediaEnabled(resourceResolver);
	boolean renderedAsDM = false;

	//Check asset first
    if (fileReference != null) {
        Resource assetResource =  resourceResolver.getResource(fileReference);
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            Node assetNode = assetResource.adaptTo(Node.class);
            boolean hadS7damType = assetNode.hasProperty("jcr:content/dam:s7damType");
            if (WCMMode.fromRequest(request) != WCMMode.DISABLED) {//only author node - we check for dm enabled
                renderedAsDM = hadS7damType && enabledDynamicMedia; //only when asset is dm asset and dm enabled
            }
            else { //otherwise, we decide on asset type
                renderedAsDM = hadS7damType;
            }


            if (hadS7damType) {
                assetType = assetNode.getProperty("jcr:content/dam:s7damType").getString();
            }

        }
    }
	if (Integer.parseInt(height) > 0 && Integer.parseInt(width) > 0) {
        stageSize = height + "," + width;
	}

    if (assetType.equalsIgnoreCase("image")) {
        viewerPreset = "Zoom_light|ZOOM|/etc/dam/presets/viewer/Zoom_light|false";
    } else if (assetType.equalsIgnoreCase("imageset")) {
        viewerPreset = "ImageSet_light|IMAGE_SET|/etc/dam/presets/viewer/ImageSet_light|false";
    } else if (assetType.equalsIgnoreCase("mixedmediaset")) {
        viewerPreset = "InlineMixedMedia_light|MIXED_MEDIA|/etc/dam/presets/viewer/InlineMixedMedia_light";
    }


    // According to the documentation, on the publish instance the normal WCM capabilities are disabled.
    // We only want to use the published content when running on the publish node(s), otherwise we will
    // be referencing content that might not have been published yet.
    if (WCMMode.fromRequest(request) != WCMMode.DISABLED) {
        // Image Server URL
        PublishUtils publishUtils = sling.getService(PublishUtils.class);
        productionImageServerUrl = publishUtils.externalizeImageDeliveryUrl(resource, imageServerUrl);

        //Video Server URL
        String videoProxyServlet = DynamicMediaServicesConfigUtil.getServiceUrl(resource.getResourceResolver());
        String videoRegistrationId = DynamicMediaServicesConfigUtil.getRegistrationId(resource.getResourceResolver());
        if (videoRegistrationId != null && videoRegistrationId.contains("|")){
            videoRegistrationId = videoRegistrationId.substring(0, videoRegistrationId.indexOf("|"));
        }
        String videoPublicKey = DynamicMediaServicesConfigUtil.getPublicKey(resource.getResourceResolver());
        String previewVideoProxyUrl = "";
        String productionVideoProxyUrl = "";
        if (videoProxyServlet != null) {
            if (!videoProxyServlet.endsWith("/")) {
                //add trailing /
                videoProxyServlet += "/";
            }
            if (videoRegistrationId != null) {
                previewVideoProxyUrl = videoProxyServlet + "private/" + videoRegistrationId;
            }
            if (videoPublicKey != null) {
                productionVideoProxyUrl = videoProxyServlet + "public/" + videoPublicKey;
            }
        }

        // Always use the preview view proxy when in preview mode
        videoServerUrl = previewVideoProxyUrl;

        // Save the production image delivery and video proxy to be used in publish instance
        try {
            PersistableValueMap props = resource.adaptTo(PersistableValueMap.class);
            props.put("imageserverurl", productionImageServerUrl);
            props.put("videoserverurl", productionVideoProxyUrl);
            props.put("assetType", assetType);
            if (!isViewerPresetMatchedType(viewerPreset, assetType)) {
                // props.put("s7ViewerPreset", "");
                viewerPreset = fallbackViewerPreset;
            }
            if (!assetType.equalsIgnoreCase("image")) {
                props.put("s7ImagePreset", "");
                imagePreset = "";
            }
            props.save();
        } catch (Exception e) {
            log.error("Unable to save imageserverurl and videoserverurl", e);
        }

    } else {
        // publish instance is using imageserverurl and videoserverurl that we store during authoring
        imageServerUrl = productionImageServerUrl;
        videoServerUrl = productionVideoServerUrl;
    }

    String embedCode = "";
    if (renderedAsDM) {
        if (assetType.equalsIgnoreCase("image") && (viewerPreset != null && !viewerPreset.isEmpty())) {
            assetType = getPresetType(viewerPreset);
        }
    	embedCode  = buildEmbedCode(currentSession,
                                    request.getContextPath(),
                                    assetType,
                                    fileReference,
                                    getViewerPresetValue(viewerPreset),
                                    isCustomPreset(viewerPreset),
                                    imagePreset,
                                    stageSize,
                                    breakpoints,
                                    imageServerUrl,
                                    videoServerUrl,
                                    linkUrl,
                                    linkTarget,
                                    title,
                                    altText,
                                    urlModifiers,
                                    (WCMMode.fromRequest(request)),
                                    viewerInstanceId);
    }
    else {
        embedCode = placeholder;
    }

%>

<%  if (WCMMode.fromRequest(request) != WCMMode.DISABLED) { %>
<cq:includeClientLib categories="cq.dam.scene7.dynamicmedia" />
<% } %>


<%
    if (enabledDynamicMedia) {
    // include libs only when DM enabled
    %>

<script type="text/javascript" src="<%=viewerPath%>libs/responsive_image.js"></script>
<script type="text/javascript" src="<%=viewerPath%>html5/js/BasicZoomViewer.js"></script>
<script type="text/javascript" src="<%=viewerPath%>html5/js/ZoomViewer.js"></script>
<script type="text/javascript" src="<%=viewerPath%>html5/js/SpinViewer.js"></script>
<script type="text/javascript" src="<%=viewerPath%>html5/js/VideoViewer.js"></script>
<script type="text/javascript" src="<%=viewerPath%>html5/js/MixedMediaViewer.js"></script>
<script type="text/javascript" src="<%=viewerPath%>html5/js/eCatalogViewer.js"></script>
<% } %>
<% if (renderedAsDM) { %>
    <div class="<%=ddClassName%>">
    	<%=embedCode%>
    </div>
<%} else {%>
	<%=placeholder%>
<%}%>



<%!

    String getS7ViewerPath(Node viewerNode,
                           String templateType,
                           String viewerRootPath) {
        String key = "html5." + templateType.toLowerCase();
        String path = null;
        try {
            if(viewerNode.hasProperty(key)){
                path = viewerNode.getProperty(key).getString();
                path = viewerRootPath + path;
            }

        } catch (RepositoryException rex){
        }
        return path;
    }

    String getViewerConstructor(String templateType) {
        String viewerConstructor = "";
        if (templateType.equalsIgnoreCase("basiczoom")){
            viewerConstructor = "BasicZoom";
        }
        else if (templateType.equalsIgnoreCase("mixedmedia")){
            viewerConstructor = "MixedMedia";
        }
        else {
            String firstChar = templateType.substring(0,1);
            viewerConstructor = firstChar.toUpperCase() + templateType.substring(1);
        }
        viewerConstructor += "Viewer";
        return viewerConstructor;
    }

    String getViewerRootPath(Session session, String contextPath){
        //Get viewer info
        String viewerRootPath = "/etc/dam/viewers/isv/";
        String viewerVersion = "5.1aem";
        String viewerPath = "";
        try {
       	 	Node thisRoot = session.getRootNode();
            if (thisRoot.hasNode("etc/dam/viewers/default/jcr:content")) {
                Node s7ViewerNode = thisRoot.getNode("etc/dam/viewers/default/jcr:content");
                if (s7ViewerNode.hasProperty("viewerVersion")){
                    viewerVersion = s7ViewerNode.getProperty("viewerVersion").getString();
                }
                if (s7ViewerNode.hasProperty("viewerRootPath")) {
                    viewerRootPath = s7ViewerNode.getProperty("viewerRootPath").getString();
                }
            }
            viewerPath = contextPath + viewerRootPath + viewerVersion + "/";
        }catch (PathNotFoundException pnfe) {

        }catch (RepositoryException re) {

        }
        return viewerPath;
    }


	String buildEmbedCode(Session session,
                          String contextPath,
                          String templateType,
                          String fileReference,
                          String config,
                          boolean isCustomPreset,
                          String imagePreset,
                          String stageSize,
                          String breakpoints,
                          String imageServer,
                          String videoServer,
                          String linkUrl,
                          String linkTarget,
                          String title,
                          String altText,
                          String urlModifiers,
                          WCMMode wcmMode,
                          String uniqueInstanceId) {
        String embedCode = "test";
        String VIEWER_CONFIG_NODE = "etc/dam/viewers/default/jcr:content";
        String viewerRootPath = getViewerRootPath(session, contextPath);
        try {
        	Node thisRoot = session.getRootNode();
            if (thisRoot.hasNode(VIEWER_CONFIG_NODE)) {
                Node viewerNode = thisRoot.getNode(VIEWER_CONFIG_NODE);

                if (!config.isEmpty() || (templateType != null && !templateType.equalsIgnoreCase("image"))) {

                    embedCode = buildViewerCode(viewerNode,
                                                templateType,
                                                viewerRootPath,
                                                contextPath,
                                                fileReference,
                                                config,
                                                isCustomPreset,
                                                stageSize,
                                                uniqueInstanceId,
                                                imageServer,
                                                videoServer,
                                                wcmMode);
                }

                else if (templateType.equalsIgnoreCase("image") && !breakpoints.isEmpty()) {
                    embedCode = buildResponsiveImageCode(viewerNode,
                                     					 viewerRootPath,
                                     					 imageServer + fileReference,
                                     					 uniqueInstanceId,
                                     					 breakpoints,
                                                         imagePreset,
                                                         urlModifiers,
                                                         wcmMode);
                }
                else {
					String fullImagePath = imageServer + fileReference;
                    String[] stageSizePart = stageSize.split(",");
                    if (imagePreset != null) {
                        fullImagePath += "?$" + imagePreset + "$";
                    }
                    if (urlModifiers != null) {
                        if (fullImagePath.contains("?")) {
                            fullImagePath += "&" + urlModifiers;
                        }
                        else {
                            fullImagePath += "?" + urlModifiers;
                        }

                    }
                    embedCode = "";

                    if (linkUrl != null && !linkUrl.isEmpty()) {
                        embedCode += "<a href=\"" + linkUrl + "\"";
                        if (linkTarget != null && !linkTarget.isEmpty()) {
                            embedCode += "target=\"" + linkTarget + "\"";
                        }
                        //if there is alt text, then we use it as alt; otherwise, we try to use title when it's not empty
                        if (altText != null && !altText.isEmpty()) {
                            embedCode += "alt=\"" + altText +"\" ";
                        }
                        else if (title != null && !title.isEmpty()) {
                            embedCode += "alt=\"" + title +"\" ";
                        }
                        if (title != null && !title.isEmpty()) {
                            embedCode += "title=\"" + title +"\" ";
                        }
                        embedCode += ">";
                    }


                    embedCode += "<img src=\"" + fullImagePath + "\"";
                    if (stageSizePart.length > 1) {
                        embedCode += "height=\"" + stageSizePart[0] + "\"";
                        embedCode += "width=\"" + stageSizePart[1] + "\"";
                    }
                    embedCode += ">";
					if (linkUrl != null && !linkUrl.isEmpty()) {
                        embedCode += "</a>";
                    }
                }
            }
        }catch (PathNotFoundException pnfe) {
        }catch (RepositoryException re) {
        }
        return embedCode;
    }




    String buildViewerCode(Node viewerNode,
                          String templateType,
                          String viewerRootPath,
                          String contextPath,
                          String fileReference,
                          String config,
                          boolean isCustomPreset,
                          String stageSize,
                          String uniqueInstanceId,
                          String imageServer,
                          String videoServer,
                          WCMMode wcmMode){
        String embedCode = "";
        boolean isAVS = false;

        if (templateType.equalsIgnoreCase("flyout_zoom") || templateType.equalsIgnoreCase("flyoutzoom")) {
            templateType = "flyout";
        }
        else if ( templateType.equalsIgnoreCase("imageset") ) {
            templateType = "zoom";
        }
        else if ( templateType.equalsIgnoreCase("zoom") ){
            templateType = "basiczoom";
        }
        else if ( templateType.equalsIgnoreCase("VideoAVS") ) {
            templateType = "video";
            isAVS = true;
        }
        else {
        	templateType = templateType.replace("Set", "");
        }
        String viewerPath =  getS7ViewerPath(viewerNode,
                							templateType,
                                            viewerRootPath + "html5/js/");
        embedCode = "";
        //from HTML5SDKEmbed in s7ondemand
        if ( viewerPath != null ) {
            String instanceId = uniqueInstanceId + "s7" + templateType + "viewer";
            String containerId = uniqueInstanceId + "s7" + templateType + "_div";
            String viewerConstructor = getViewerConstructor(templateType);

            if (!templateType.equalsIgnoreCase("flyout") || ( templateType.equalsIgnoreCase("flyout") && stageSize == null ) ) {
                //for any viewer except flyout and flyout without size, we add responsive CSS
                embedCode = "<style type=\"text/css\">\n"
                        + "\t#" + containerId + ".s7" + viewerConstructor.toLowerCase() + "{\n"
                        + "\t\t width:100%; \n"
                        + "\t\t height:auto;\n"
                        + "\t}\n"
                        + "</style>\n";
            }
            embedCode += "<script type=\"text/javascript\" src=\""+ viewerPath +"\"></script>\n"
                    + "<div id=\"" + containerId + "\"></div>\n"
                    + "<script type=\"text/javascript\">\n";
            //only include delay for author mode due to issue when asset replacement occurs
            if (wcmMode == WCMMode.EDIT) {
            	embedCode += "setTimeout( function() {\n";
            }
            embedCode += "\tvar "+ instanceId +" = new s7viewers." + viewerConstructor + "({\n";
            embedCode +=  "\t\t\"containerId\" : \"" + containerId + "\",\n";
            embedCode +=  "\t\t\"params\" : { \n"
                    + "\t\t\t\"serverurl\" : \"" + imageServer + "\",\n";
            if (config != null) {
                embedCode += "\t\t\t\"config\" : \""+ config + "\",\n";
            }
            if (stageSize != null) {
                embedCode += "\t\t\t\"stagesize\" : \""+ stageSize + "\",\n";
            }

            /*
             For custom viewer preset:
               published node: serve from delivery
               author node: server from /is/content
             For OOTB viewer preset
               serve from / regardless
             */
            embedCode += "\t\t\t\"contenturl\" : \"";
            if (isCustomPreset) {
                if (wcmMode == WCMMode.DISABLED) {
                    embedCode += imageServer.replace("/is/image", "/is/content");
                } else {
                    embedCode += contextPath + "/is/content";
                }
            } else {
                embedCode += contextPath + "/";
            }

            embedCode += "\", \n";

            if (templateType.equalsIgnoreCase("flyout")) {
                embedCode += "\t\t\t\"imagereload\" : \"1,breakpoint,100;320;480\",\n";
            }

            if (videoServer != null && !videoServer.isEmpty()
                && (templateType.equalsIgnoreCase("Video") || templateType.equalsIgnoreCase("MixedMedia"))) {
            	embedCode += "\t\t\t\"videoserverurl\" : \"" + videoServer + "\",\n";
                if (!isAVS && templateType.equalsIgnoreCase("Video")) { //only video and non-AVS to force native playback
                    embedCode += "\t\t\t\"playback\" : \"native\",\n";
                }
            }

            embedCode += "\t\t\t\"asset\" : \"" + fileReference + "\" }\n";
            embedCode += "\t}).init();\n";
            //only include delay for author mode due to issue when asset replacement occurs
            if (wcmMode == WCMMode.EDIT) {
                embedCode += "},100);\n";
            }
            embedCode += "</script>";


        }
        return embedCode;
    }

    /**
     *
     * @param viewerPreset
     * @param assetType
     * @return true if the viewer preset is same as asset type
     */
    boolean isViewerPresetMatchedType(String viewerPreset, String assetType) {
        String viewerPresetType = "";
        if ( viewerPreset!= null ) {
            String[] viewerPresetPart = viewerPreset.split("\\|");
            if ( viewerPresetPart.length > 1 ){
                viewerPresetType = viewerPresetPart[1];
            }
        }
        viewerPresetType = viewerPresetType.replace("_", "");

        if (assetType.equalsIgnoreCase("image")) {
            return (viewerPresetType.equalsIgnoreCase("flyoutzoom") || viewerPresetType.equalsIgnoreCase("zoom"));
        }
        else if (assetType.equalsIgnoreCase("zoom")) {
            return (viewerPresetType.equalsIgnoreCase("imageset"));
        }
        else if (assetType.equalsIgnoreCase("videoavs")){
            return (viewerPresetType.equalsIgnoreCase("video"));
        }
        else if (assetType.equalsIgnoreCase("mixedmediaset")){
            return (viewerPresetType.equalsIgnoreCase("mixedmedia"));
        }
        else {
            return (assetType.equalsIgnoreCase(viewerPresetType));
        }
    }

    String buildResponsiveImageCode( Node viewerNode,
                                     String viewerRootPath,
                                     String fileReference,
                                     String uniqueInstanceId,
                                     String breakpoints,
                                     String imagePreset,
                                     String urlModifiers,
                                     WCMMode wcmMode) {
        String embedCode = "";
        String viewerPath = getS7ViewerPath( viewerNode,
                "image",
                viewerRootPath + "html5/js/");
        if ( viewerPath != null ) {
            embedCode = "<style type=\"text/css\"> \n"
                    + "\t.s7responsiveContainer {\n"
                    + "\t\twidth: 100%;\n"
                    + "\t}\n"
                    + "\t.fluidimage {\n"
                    + "\t\tmax-width: 100%;\n"
                    + "\t}\n"
                    + "</style>\n";
            embedCode += "<div class=\"s7responsiveContainer\"> \n"
                    + "<img id=\"" + uniqueInstanceId + "\" src=\"" + fileReference;
            if (imagePreset != null && !imagePreset.isEmpty()) {
                embedCode += "?$" + imagePreset + "$";
            }
            if (urlModifiers != null && !urlModifiers.isEmpty()) {
                if (embedCode.contains("?")) {
                    embedCode += "&" + urlModifiers;
                }
                else {
                    embedCode += "?" + urlModifiers;
                }
            }
            embedCode += "\"  data-breakpoints=\"" + breakpoints + "\" class=\"fluidimage\">\n"
                    + "</div>\n"
                    + "<script type=\"text/javascript\" src=\"" + viewerPath + "\"></script>\n"
                    + "<script type=\"text/javascript\"> \n";
            embedCode += "\ts7responsiveImage(document.getElementById(\"" + uniqueInstanceId + "\"));\n";
            embedCode += "</script>";
        }
        return embedCode;
    }

	String getViewerPresetValue(String viewerPreset) {
        String viewerPresetVal = "";
        if ( viewerPreset!= null ) {
            String[] viewerPresetPart = viewerPreset.split("\\|");
            if ( viewerPresetPart.length > 2 ){
                viewerPresetVal = viewerPresetPart[2];
            }
        }
        return viewerPresetVal;
	}

    String getPresetType(String viewerPreset) {
        String presetType = "";
        if ( viewerPreset!= null ) {
            String[] viewerPresetPart = viewerPreset.split("\\|");
            if ( viewerPresetPart.length > 2 ){
                presetType = viewerPresetPart[1];
            }
        }
        return presetType;
    }

    boolean isCustomPreset(String viewerPreset) {
        if ( viewerPreset!= null ) {
            String[] viewerPresetPart = viewerPreset.split("\\|");
            if ( viewerPresetPart.length > 3 ){
                return viewerPresetPart[3].equals("true");
            }
        }
        return false;
    }
%>