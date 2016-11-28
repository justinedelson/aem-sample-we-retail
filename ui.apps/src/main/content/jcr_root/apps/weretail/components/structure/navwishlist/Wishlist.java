/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package apps.weretail.components.structure.navwishlist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.api.smartlist.SmartList;
import com.adobe.cq.commerce.api.smartlist.SmartListEntry;
import com.adobe.cq.commerce.api.smartlist.SmartListManager;
import com.adobe.cq.commerce.common.PriceFilter;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.security.user.UserProperties;
import com.day.cq.personalization.UserPropertiesUtil;
import com.day.cq.wcm.commons.WCMUtils;

public class Wishlist extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Wishlist.class);
    private static final String PN_FILE_REFERENCE = "fileReference";

    private SmartList smartList;
    private List<WishlistEntry> entries = new ArrayList<WishlistEntry>();
    private CommerceSession commerceSession;

    @Override
    public void activate() throws Exception {
        createCommerceSession();
        initSmartlist();

        populateCartEntries();
    }

    private void createCommerceSession() {
        CommerceService commerceService = getCurrentPage().getContentResource().adaptTo(CommerceService.class);
        try {
            commerceSession = commerceService.login(getRequest(), getResponse());
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
            if (getRequest().getParameterMap().containsKey("smartList")) {
                smartList = smartListManager.getSmartList(getRequest().getParameter("smartList"));
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

    private void populateCartEntries() throws CommerceException {
        if (smartList != null) {
            for (Iterator<SmartListEntry> smartListEntries = smartList.getSmartListEntries(); smartListEntries.hasNext(); ) {
                SmartListEntry smartListEntry = smartListEntries.next();
                String image = StringUtils.EMPTY;
                if (smartListEntry.getProduct().getImage() != null) {
                    Resource imageResource = getResourceResolver().getResource(
                            smartListEntry.getProduct().getImage().getPath());
                    if (imageResource != null) {
                        image = imageResource.adaptTo(ValueMap.class).get(PN_FILE_REFERENCE, StringUtils.EMPTY);
                    }
                }
                entries.add(new WishlistEntry(smartListEntry,
                        commerceSession.getProductPrice(smartListEntry.getProduct()), image));
            }
        }
    }

    /**
     * Check if the current user is anonymous.
     *
     * @return <code>true</code> if the current user is anonymous.
     */
    public boolean isAnonymous() {
        final UserProperties userProperties = getRequest().adaptTo(UserProperties.class);
        return userProperties == null || UserPropertiesUtil.isAnonymous(userProperties);
    }

    /**
     * Check if the current user can modify the smart list or smart list entries.
     *
     * @return <code>true</code> if the current user can modify the smart list or smart list entries.
     */
    public boolean canEdit() {
        if (smartList != null && !isAnonymous() && (StringUtils.equals(getRequest().getUserPrincipal().getName(),
                smartList.getOwner()) || smartList.getPrivacy().equals(SmartList.Privacy.SHARED_EDITABLE))) {
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

            for (Iterator<String> variantAxis = this.entry.getProduct().getVariantAxes(); variantAxis.hasNext(); ) {
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
