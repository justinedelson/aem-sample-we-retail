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
package apps.weretail.components.content.shoppingcartprices;

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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;

public class CartPrices extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(CartPrices.class);

    private static final String SHOW_SUB_TOTAL = "showSubTotal";
    private static final String SHOW_SHIPPING_TOTAL = "showShippingTotal";
    private static final String SHOW_TAX_TOTAL = "showTaxTotal";
    private static final String SHOW_TOTAL = "showTotal";
    
    private CommerceSession commerceSession;

    private Boolean showSubTotal;
    private Boolean showShippingTotal;
    private Boolean showTaxTotal;
    private Boolean showTotal;
    private boolean isEmpty;
    
    private String shippingTotal;
    private String subTotal;
    private String taxTotal;
    private String total;

    @Override
    public void activate() throws Exception {
        showSubTotal = getProperties().get(SHOW_SUB_TOTAL, Boolean.class);
        showShippingTotal = getProperties().get(SHOW_SHIPPING_TOTAL, Boolean.class);
        showTaxTotal = getProperties().get(SHOW_TAX_TOTAL, Boolean.class);
        showTotal = getProperties().get(SHOW_TOTAL, Boolean.class);

        CommerceService commerceService = getCurrentPage().getContentResource().adaptTo(CommerceService.class);
        try {
            commerceSession = commerceService.login(getRequest(), getResponse());
        } catch (CommerceException e) {
            LOG.error(e.getMessage());
        }
        
        isEmpty = commerceSession.getCartEntries().isEmpty();
        
        shippingTotal = formatShippingPrice(commerceSession.getCartPriceInfo(new PriceFilter("SHIPPING")));
        subTotal = commerceSession.getCartPrice(new PriceFilter("PRE_TAX"));
        taxTotal = commerceSession.getCartPrice(new PriceFilter("TAX"));
        total = commerceSession.getCartPrice(new PriceFilter("TOTAL"));
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

    public boolean getShowSubTotal() {
        return Boolean.TRUE.equals(showSubTotal);
    }

    public boolean getShowShippingTotal() {
        return Boolean.TRUE.equals(showShippingTotal);
    }
    
    public boolean getShowTaxTotal() {
        return Boolean.TRUE.equals(showTaxTotal);
    }
    
    public boolean getShowTotal() {
        return Boolean.TRUE.equals(showTotal);
    }
    
    public String getShippingTotal() {
        return shippingTotal;
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
    
    public boolean isEmpty() {
        return isEmpty;
    }
}
