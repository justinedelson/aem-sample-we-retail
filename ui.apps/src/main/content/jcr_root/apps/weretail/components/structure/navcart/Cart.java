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
package apps.weretail.components.structure.navcart;

import java.util.ArrayList;
import java.util.List;

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
import com.adobe.cq.commerce.common.PriceFilter;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.commons.WCMUtils;

public class Cart extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(Cart.class);
    private static final String PN_FILE_REFERENCE = "fileReference";
    private static final String PN_TYPES = "types";

    private String checkoutPage;
    private List<CartEntry> entries = new ArrayList<CartEntry>();
    private CommerceSession commerceSession;
    private String total;

    @Override
    public void activate() throws Exception {
        createCommerceSession();
        populateCheckoutPage();
        populateCartEntries();
    }

    private void createCommerceSession() {
        CommerceService commerceService = getCurrentPage().getContentResource().adaptTo(CommerceService.class);
        try {
            commerceSession = commerceService.login(getRequest(), getResponse());
        } catch (CommerceException e) {
            LOG.error(e.getMessage());
        }
    }

    private void populateCartEntries() throws CommerceException {
        PriceFilter priceFilter = getPriceFilter();  
        List<CommerceSession.CartEntry> cartEntries = commerceSession.getCartEntries();
        total = commerceSession.getCartPrice(priceFilter);
        
        for (CommerceSession.CartEntry cartEntry : cartEntries) {
            String image = StringUtils.EMPTY;
            if (cartEntry.getProduct().getImage() != null) {
                Resource imageResource = getResourceResolver().getResource(cartEntry.getProduct().getImage().getPath());
                if (imageResource != null) {
                    image = imageResource.adaptTo(ValueMap.class).get(PN_FILE_REFERENCE, StringUtils.EMPTY);
                }
            }
            CartEntry entry = new CartEntry(cartEntry, commerceSession.getProductPrice(cartEntry.getProduct()), cartEntry.getProduct(), image);
            entries.add(entry);
        }
    }

    private PriceFilter getPriceFilter() {
        String[] types = getProperties().get(PN_TYPES, new String[]{});
        return new PriceFilter(types);
    }

    private void populateCheckoutPage() {
        String checkoutPageProperty = WCMUtils.getInheritedProperty(getCurrentPage(), getResourceResolver(), CommerceConstants.PN_CHECKOUT_PAGE_PATH);
        if (StringUtils.isNotEmpty(checkoutPageProperty)) {
            checkoutPage = getResourceResolver().map(getRequest(), checkoutPageProperty) + ".html";
        }
    }

    public String getCheckoutPage() {
        return checkoutPage;
    }

    public List<CartEntry> getEntries() {
        return entries;
    }

    public String getTotal() {
        return total;
    }

    public class CartEntry {
        private CommerceSession.CartEntry entry;
        private String price;
        private Product product;
        private String image;

        public CartEntry(CommerceSession.CartEntry entry, String price, Product product, String image) {
            this.entry = entry;
            this.product = product;
            this.price = price;
            this.image = image;
        }

        public CommerceSession.CartEntry getEntry() {
            return entry;
        }

        public String getPrice() {
            return price;
        }

        public Product getProduct() {
            return product;
        }

        public String getImage() {
            return image;
        }
    }

}
