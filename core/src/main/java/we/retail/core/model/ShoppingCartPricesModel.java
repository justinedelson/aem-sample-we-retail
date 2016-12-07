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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.common.PriceFilter;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class)
public class ShoppingCartPricesModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartPricesModel.class);
    
    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private Page currentPage;
    
    private CommerceSession commerceSession;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean showSubTotal;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean showShippingTotal;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean showTaxTotal;
    
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    private boolean showTotal;
    
    private boolean isEmpty;
    private String shippingTotal;
    private String subTotal;
    private String taxTotal;
    private String total;

    @PostConstruct
    public void activate() throws Exception {

        CommerceService commerceService = currentPage.getContentResource().adaptTo(CommerceService.class);
        try {
            commerceSession = commerceService.login(request, response);
        } catch (CommerceException e) {
            LOGGER.error(e.getMessage());
        }
        
        isEmpty = commerceSession.getCartEntries().isEmpty();
        
        shippingTotal = formatShippingPrice(commerceSession.getCartPriceInfo(new PriceFilter("SHIPPING")));
        subTotal = commerceSession.getCartPrice(new PriceFilter("PRE_TAX"));
        taxTotal = commerceSession.getCartPrice(new PriceFilter("TAX"));
        total = commerceSession.getCartPrice(new PriceFilter("TOTAL"));
    }

    private String formatShippingPrice(List<PriceInfo> prices) {
        if (prices.isEmpty()) {
            return request.getResourceBundle(request.getLocale()).getString("Unknown");
        }
        else {
            PriceInfo priceInfo = prices.get(0);
            if (priceInfo.getAmount() != null && priceInfo.getAmount().signum() == 0) {
                return request.getResourceBundle(request.getLocale()).getString("Free");
            }
            else {
                return priceInfo.getFormattedString();
            }
        }
    }

    public boolean getShowSubTotal() {
        return showSubTotal;
    }

    public boolean getShowShippingTotal() {
        return showShippingTotal;
    }
    
    public boolean getShowTaxTotal() {
        return showTaxTotal;
    }
    
    public boolean getShowTotal() {
        return showTotal;
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
