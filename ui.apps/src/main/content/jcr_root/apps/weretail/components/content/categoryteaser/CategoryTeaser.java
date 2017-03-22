/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package apps.weretail.components.content.categoryteaser;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

public class CategoryTeaser extends WCMUsePojo {

    public static final Logger log = LoggerFactory.getLogger(CategoryTeaser.class);

    public static final String PROP_BUTTON_LINK_TO = "buttonLinkTo";
    public static final String PROP_BUTTON_LABEL = "buttonLabel";
    private static final String PN_FILE_REFERENCE = "fileReference";

    private String imagePath;
    private String buttonLinkTo;
    private String buttonLabel;

    @Override
    public void activate() throws Exception {
        Resource resource = getResource();
        ValueMap properties = getProperties();
        ResourceResolver resolver = getResourceResolver();
        if(StringUtils.isNotEmpty(properties.get(PN_FILE_REFERENCE, String.class))) {
            String escapedResourcePath = Text.escapePath(resource.getPath());
            imagePath = getRequest().getContextPath() + escapedResourcePath + ".img.jpeg";
            if (getWcmMode().isEdit()) {
                imagePath += "/" + getLastModifiedDate(properties) + ".jpeg";
            }
        }
        buttonLinkTo = properties.get(PROP_BUTTON_LINK_TO, "");
        buttonLabel = properties.get(PROP_BUTTON_LABEL, "");
        if (StringUtils.isNotEmpty(buttonLinkTo)) {
            // if button label is not set, try to get it from target page's title
            if (StringUtils.isEmpty(buttonLabel)) {
                Resource linkResource = resolver.getResource(buttonLinkTo);
                if (linkResource != null) {
                    Page targetPage = linkResource.adaptTo(Page.class);
                    if (targetPage != null) {
                        buttonLabel = targetPage.getTitle();
                    }
                }
            }
            buttonLinkTo = buttonLinkTo + ".html";
        }
        log.debug("resource: {}", resource.getPath());
        log.debug("imagePath: {}", imagePath);
        log.debug("buttonLinkTo: {}", buttonLinkTo);
        log.debug("buttonLabel: {}", buttonLabel);
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getButtonLinkTo() {
        return buttonLinkTo;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    private long getLastModifiedDate(ValueMap properties) {
        long lastMod = 0L;
        if (properties.containsKey(JcrConstants.JCR_LASTMODIFIED)) {
            lastMod = properties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class).getTimeInMillis();
        } else if (properties.containsKey(JcrConstants.JCR_CREATED)) {
            lastMod = properties.get(JcrConstants.JCR_CREATED, Calendar.class).getTimeInMillis();
        }
        return lastMod;
    }

}