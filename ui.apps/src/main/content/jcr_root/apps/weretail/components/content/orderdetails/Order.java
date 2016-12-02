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
package apps.weretail.components.content.orderdetails;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.adobe.cq.address.api.Address;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.common.PriceFilter;


public class Order extends apps.weretail.components.content.shoppingcart.Cart {

    private static final String ORDER_ID = "orderId";
    private static final String ORDER_PLACED_FORMATTED = "orderPlacedFormatted";
    private static final String ORDER_STATUS = "orderStatus";
    private static final String CART_SUB_TOTAL = "CART";
    private static final String ORDER_SHIPPING = "SHIPPING";
    private static final String ORDER_TOTAL_TAX = "TAX";
    private static final String ORDER_TOTAL_PRICE = "TOTAL";
    
    private static final String BILLING_PREFIX = "billing.";
    private static final String SHIPPING_PREFIX = "shipping.";
    
    private String orderId;
    private PlacedOrder placedOrder;
    private Map<String, Object> orderDetails;
    
    protected void populateCartEntries() throws CommerceException {
        boolean isEditMode = getWcmMode().isEdit();
        orderId = getRequest().getParameter(ORDER_ID);
        
        if (!isEditMode && StringUtils.isBlank(orderId)) {
            try {
                getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        
        List<CommerceSession.CartEntry> cartEntries = null;
        
        if (StringUtils.isNotEmpty(orderId)) {
            placedOrder = commerceSession.getPlacedOrder(orderId);
            if (!isEditMode && placedOrder.getOrderId() == null) {
                try {
                    getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
            
            cartEntries = placedOrder.getCartEntries();
            orderDetails = placedOrder.getOrder();
        }

        for (CommerceSession.CartEntry cartEntry : cartEntries) {
            CartEntry entry = new CartEntry(cartEntry);
            entries.add(entry);
        }
    }
    
    private String getOrderProperty(String property) {
        Object obj = orderDetails.get(property);
        return obj != null ? obj.toString() : null;
    }
    
    @Override
    public void activate() throws Exception {
        super.activate();
        isReadOnly = true;
    }

    public String getOrderId() {
        return orderId;
    }
    
    public String getOrderDate() {
        return getOrderProperty(ORDER_PLACED_FORMATTED);
    }
    
    public String getOrderStatus() {
        return getOrderProperty(ORDER_STATUS);
    }
    
    public String getSubTotal() throws CommerceException {
        return placedOrder.getCartPrice(new PriceFilter(CART_SUB_TOTAL));
    }
    
    public String getShippingTotal() throws CommerceException {
        return placedOrder.getCartPrice(new PriceFilter(ORDER_SHIPPING));
    }
    
    public String getTaxTotal() throws CommerceException {
        return placedOrder.getCartPrice(new PriceFilter(ORDER_TOTAL_TAX));
    }
    
    public String getTotal() throws CommerceException {
        return placedOrder.getCartPrice(new PriceFilter(ORDER_TOTAL_PRICE));
    }
    
    public String getShippingAddress() {
        return getAddress(SHIPPING_PREFIX);
    }
    
    public String getBillingAddress() {
        return getAddress(BILLING_PREFIX);
    }
    
    private String getAddress(String prefix) {
        String firstname = getOrderProperty(prefix + Address.FIRST_NAME);
        String lastname = getOrderProperty(prefix + Address.LAST_NAME);
        String street1 = getOrderProperty(prefix + Address.STREET_LINE1);
        String street2 = getOrderProperty(prefix + Address.STREET_LINE2);
        String zipCode = getOrderProperty(prefix + Address.ZIP_CODE);
        String city = getOrderProperty(prefix + Address.CITY);
        String state = getOrderProperty(prefix + Address.STATE);
        String country = getOrderProperty(prefix + Address.COUNTRY);
        
        String name = StringUtils.join(new String[] {firstname, lastname}, " ");
        String street = StringUtils.join(new String[] {street1, street2}, " ");
        String countryZip = StringUtils.join(new String[] {country, zipCode}, "-");
        String countryZipCity = StringUtils.join(new String[] {countryZip, city}, " ");
        
        return StringUtils.join(new String[] {name, street, countryZipCity, state}, ", ");
    }
}
