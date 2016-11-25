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
package apps.weretail.components.content.shoppingcart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.PriceFilter;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.commons.WCMUtils;

public class Cart extends WCMUsePojo {

    private static final String ORDER_ID = "orderId";
    private static final String IS_READ_ONLY = "isReadOnly";
    private static final Logger LOG = LoggerFactory.getLogger(Cart.class);

    private String checkoutPage;
    private String currentPageUrl;
    private List<CartEntry> entries = new ArrayList<CartEntry>();
    private CommerceSession commerceSession;

    private Boolean isReadOnly;

    @Override
    public void activate() throws Exception {
        isReadOnly = getProperties().get(IS_READ_ONLY, Boolean.class);

        createCommerceSession();
        populatePageUrls();
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
        String orderId = getRequest().getParameter(ORDER_ID);
        List<CommerceSession.CartEntry> cartEntries;

        if (StringUtils.isNotEmpty(orderId)) {
            PlacedOrder placedOrder = commerceSession.getPlacedOrder(orderId);
            cartEntries = placedOrder.getCartEntries();
            isReadOnly = true;
        } else {
            cartEntries = commerceSession.getCartEntries();
        }

        for (CommerceSession.CartEntry cartEntry : cartEntries) {
            CartEntry entry = new CartEntry(commerceSession, cartEntry);
            entries.add(entry);
        }
    }

    private void populatePageUrls() {
        final String checkoutPageProperty = WCMUtils.getInheritedProperty(getCurrentPage(), getResourceResolver(),
                CommerceConstants.PN_CHECKOUT_PAGE_PATH);
        if (StringUtils.isNotEmpty(checkoutPageProperty)) {
            checkoutPage = getResourceResolver().map(getRequest(), checkoutPageProperty) + ".html";
        }

        currentPageUrl = getResourceResolver().map(getRequest(), getCurrentPage().getPath() + ".html");
    }

    public String getCheckoutPage() {
        return checkoutPage;
    }

    public String getCurrentPageUrl() {
        return currentPageUrl;
    }

    public List<CartEntry> getEntries() {
        return entries;
    }

    public boolean getIsReadOnly() {
        return Boolean.TRUE.equals(isReadOnly);
    }

    public class CartEntry {
        private CommerceSession commerceSession;
        private CommerceSession.CartEntry entry;
        private Map<String, String> variantAxesMap = new LinkedHashMap<String, String>();

        public CartEntry(CommerceSession commerceSession, CommerceSession.CartEntry entry) {
            this.commerceSession = commerceSession;
            this.entry = entry;

            try {
                Product product = entry.getProduct();
                Product baseProduct = product.getBaseProduct();
                String[] variantAxes = baseProduct.getProperty(CommerceConstants.PN_PRODUCT_VARIANT_AXES,
                        String[].class);
                if (variantAxes != null) {
                    for (String variantAxis : variantAxes) {
                        String value = product.getProperty(variantAxis, String.class);
                        if (value != null && !variantAxesMap.containsKey(variantAxis)) {
                            variantAxesMap.put(StringUtils.capitalize(variantAxis), value);
                        }
                    }
                }
            } catch (CommerceException e) {
                LOG.error(e.getMessage());
            }
        }

        public CommerceSession.CartEntry getEntry() {
            return entry;
        }

        public String getPrice() throws CommerceException {
            return commerceSession.getProductPrice(entry.getProduct());
        }

        public Product getProduct() throws CommerceException {
            return entry.getProduct();
        }

        public String getImage() throws CommerceException {
            if (entry.getProduct().getImage() != null) {
                return entry.getProduct().getImage().getFileReference();
            }

            return null;
        }

        public String getTotalPrice() throws CommerceException {
            List<PriceInfo> priceInfos = entry.getPriceInfo(new PriceFilter("LINE"));
            return priceInfos.get(0).getFormattedString();
        }

        public Map<String, String> getVariantAxesMap() {
            return variantAxesMap;
        }
    }

}
