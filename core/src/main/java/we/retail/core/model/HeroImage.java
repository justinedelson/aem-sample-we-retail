/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package we.retail.core.model;

import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.SightlyWCMMode;

@Model(adaptables = SlingHttpServletRequest.class)
public class HeroImage {

    private static final String PN_FULL_WIDTH = "useFullWidth";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private SightlyWCMMode wcmmode;

    private String classList;
    private Image image;

    @PostConstruct
    private void initModel() {
        classList = getClassList();
        image = getImage();
    }

    public String getClassList() {
        if (classList != null) {
            return classList;
        }
        classList = "we-HeroImage";
        if ("true".equals(properties.get(PN_FULL_WIDTH, ""))) {
            classList += " width-full";
        }
        return classList;
    }

    public Image getImage() {
        if (image != null) {
            return image;
        }
        String escapedResourcePath = Text.escapePath(resource.getPath());
        long lastModifiedDate = getLastModifiedDate(properties);
        String src = request.getContextPath() + escapedResourcePath + ".img.jpeg" +
                (!wcmmode.isDisabled() && lastModifiedDate > 0 ? "/" + lastModifiedDate + ".jpeg" : "");
        image = new Image(src);
        return image;
    }

    public class Image {
        private String src;

        public Image(String src) {
            this.src = src;
        }

        public String getSrc() {
            return src;
        }
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
