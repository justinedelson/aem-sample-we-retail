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
package apps.weretail.components.content.cart;

import java.util.ArrayList;
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
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.PriceFilter;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;

public class Cart extends WCMUsePojo {

    private static final String ORDER_ID = "orderId";
    private static final String SHOW_MINI_CART = "showMiniCart";
    private static final String IS_READ_ONLY = "isReadOnly";
    private static final Logger LOG = LoggerFactory.getLogger(Cart.class);

    private String checkoutPage;
    private List<CartEntry> entries = new ArrayList<CartEntry>();
    private CommerceSession commerceSession;
    
    private String shippingPrice;
    private String subTotal;
    private String taxTotal;
    private String total;
    private Boolean showMiniCart;
    private Boolean isReadOnly;

    @Override
    public void activate() throws Exception {
        createCommerceSession();
        populateCheckoutPage();
        populateCartEntries();
        
        showMiniCart = getResource().getValueMap().get(SHOW_MINI_CART, Boolean.class);
        isReadOnly = getResource().getValueMap().get(IS_READ_ONLY, Boolean.class);
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
            shippingPrice = formatShippingPrice(placedOrder.getCartPriceInfo(new PriceFilter("SHIPPING")));
            subTotal = placedOrder.getCartPrice(new PriceFilter("PRE_TAX"));
            taxTotal = placedOrder.getCartPrice(new PriceFilter("TAX"));
            total = placedOrder.getCartPrice(new PriceFilter("TOTAL"));
        } else {
            cartEntries = commerceSession.getCartEntries();
            shippingPrice = formatShippingPrice(commerceSession.getCartPriceInfo(new PriceFilter("SHIPPING")));
            subTotal = commerceSession.getCartPrice(new PriceFilter("PRE_TAX"));
            taxTotal = commerceSession.getCartPrice(new PriceFilter("TAX"));
            total = commerceSession.getCartPrice(new PriceFilter("TOTAL"));
        }
        
        for (CommerceSession.CartEntry cartEntry : cartEntries) {
            CartEntry entry = new CartEntry(commerceSession, cartEntry);
            entries.add(entry);
        }
    }

    private String formatShippingPrice(List<PriceInfo> prices) {
        if (prices.isEmpty()) {
            return getRequest().getResourceBundle(getRequest().getLocale()).getString("Unknown");
        }
        else {
            PriceInfo priceInfo = prices.get(0);
            if (priceInfo.getAmount() != null && priceInfo.getAmount().signum() == 0) {
                return getRequest().getResourceBundle(getRequest().getLocale()).getString("Free");
            }
            else {
                return priceInfo.getFormattedString();
            }
        }
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

    public String getShippingPrice() {
        return shippingPrice;
    }
    
    public String getSubTotal() {
        return subTotal;
    }
    
    public String getTaxTotal() {
        return taxTotal;
    }
    
    public String getTotal() {
        return total;
    }

    public boolean showMiniCart() {
        return Boolean.TRUE.equals(showMiniCart);
    }
    
    public boolean getIsReadOnly() {
        Page currentPage = getCurrentPage();
        return Boolean.TRUE.equals(isReadOnly) || currentPage.getPath().endsWith("checkout") || currentPage.getPath().endsWith("checkout/order");
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
                String[] variantAxes = baseProduct.getProperty(CommerceConstants.PN_PRODUCT_VARIANT_AXES, String[].class);
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
