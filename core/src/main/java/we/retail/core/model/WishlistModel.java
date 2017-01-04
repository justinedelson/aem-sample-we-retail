/*
 *   Copyright 2016 Adobe Systems Incorporated
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package we.retail.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.smartlist.SmartList;
import com.adobe.cq.commerce.api.smartlist.SmartListEntry;
import com.adobe.cq.commerce.api.smartlist.SmartListManager;
import com.adobe.granite.security.user.UserProperties;
import com.day.cq.personalization.UserPropertiesUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;

@Model(adaptables = SlingHttpServletRequest.class)
public class WishlistModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishlistModel.class);
    private static final String PN_FILE_REFERENCE = "fileReference";

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    private SmartList smartList;
    private List<WishlistEntry> entries = new ArrayList<WishlistEntry>();
    private String smartListUrl;
    private String cartPageUrl;

    private CommerceSession commerceSession;

    @PostConstruct
    public void activate() throws Exception {
        createCommerceSession();
        initSmartlist();

        populatePages();
        populateCartEntries();
    }

    private void createCommerceSession() {
        CommerceService commerceService = currentPage.getContentResource().adaptTo(CommerceService.class);
        try {
            commerceSession = commerceService.login(request, response);
        } catch (CommerceException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void initSmartlist() {
        if (commerceSession != null) {
            final SmartListManager smartListManager = commerceSession.getSmartListManager();
            // get all smart lists for registered users
            List<SmartList> smartLists;
            if (!isAnonymous()) {
                smartLists = smartListManager.getSmartLists("personal");
            } else {
                smartLists = new ArrayList<SmartList>();
            }

            // get specific or default smart list
            if (request.getParameterMap().containsKey("smartList")) {
                smartList = smartListManager.getSmartList(request.getParameter("smartList"));
            } else {
                if (smartLists.size() > 0) {
                    smartList = smartLists.get(0);
                }
            }

            if (smartList != null) {
                LOGGER.debug("Smart-list is {} ({})", smartList.getTitle(), smartList.getPath());
            } else {
                LOGGER.debug("No smart-list for display");
            }
        }
    }

    private void populatePages() {
        String cartPageProperty = WCMUtils.getInheritedProperty(currentPage, resourceResolver, CommerceConstants.PN_CART_PAGE_PATH);
        if (StringUtils.isNotEmpty(cartPageProperty)) {
            cartPageUrl = resourceResolver.map(request, cartPageProperty) + ".html";
        } else {
            cartPageUrl = resourceResolver.map(request, currentPage.getPath() + ".html");
        }

        smartListUrl = resourceResolver.map(request, currentPage.getPath() + ".html");
    }

    private void populateCartEntries() throws CommerceException {
        if (smartList != null) {
            for (Iterator<SmartListEntry> smartListEntries = smartList.getSmartListEntries(); smartListEntries.hasNext();) {
                SmartListEntry smartListEntry = smartListEntries.next();
                String image = StringUtils.EMPTY;
                if (smartListEntry.getProduct().getImage() != null) {
                    Resource imageResource = resourceResolver.getResource(smartListEntry.getProduct().getImage().getPath());
                    if (imageResource != null) {
                        image = imageResource.adaptTo(ValueMap.class).get(PN_FILE_REFERENCE, StringUtils.EMPTY);
                    }
                }
                entries.add(new WishlistEntry(smartListEntry, commerceSession.getProductPrice(smartListEntry.getProduct()), image));
            }
        }
    }

    /**
     * Get the smartlist page url.
     * 
     * @return the smart list page url.
     */
    public String getSmartListUrl() {
        return smartListUrl;
    }

    /**
     * Get the cart page url.
     * 
     * @return the cart page url.
     */
    public String getCartPageUrl() {
        return cartPageUrl;
    }

    /**
     * Check if the current user is anonymous.
     *
     * @return <code>true</code> if the current user is anonymous.
     */
    public boolean isAnonymous() {
        final UserProperties userProperties = request.adaptTo(UserProperties.class);
        return userProperties == null || UserPropertiesUtil.isAnonymous(userProperties);
    }

    /**
     * Check if the current user can modify the smart list or smart list entries.
     *
     * @return <code>true</code> if the current user can modify the smart list or smart list entries.
     */
    public boolean canEdit() {
        if (smartList != null && !isAnonymous() && (StringUtils.equals(request.getUserPrincipal().getName(), smartList.getOwner())
                || smartList.getPrivacy().equals(SmartList.Privacy.SHARED_EDITABLE))) {
            return true;
        }
        return false;
    }

    public SmartList getSmartList() {
        return smartList;
    }

    public List<WishlistEntry> getEntries() {
        return entries;
    }

    public class WishlistEntry {
        private SmartListEntry entry;
        private String price;
        private String image;
        private Map<String, String> variantAxesMap = new LinkedHashMap<String, String>();

        public WishlistEntry(SmartListEntry entry, String price, String image) {
            this.entry = entry;
            this.price = price;
            this.image = image;

            for (Iterator<String> variantAxis = this.entry.getProduct().getVariantAxes(); variantAxis.hasNext();) {
                final String key = variantAxis.next();
                final String value = this.entry.getProduct().getProperty(key, String.class);
                if (value != null && !variantAxesMap.containsKey(key)) {
                    variantAxesMap.put(key, value);
                }
            }
        }

        public SmartListEntry getEntry() {
            return entry;
        }

        public String getPrice() {
            return price;
        }

        public String getImage() {
            return image;
        }

        public Map<String, String> getVariantAxesMap() {
            return variantAxesMap;
        }
    }

}
