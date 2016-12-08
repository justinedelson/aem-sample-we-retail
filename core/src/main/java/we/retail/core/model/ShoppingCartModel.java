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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.PriceFilter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;

@Model(adaptables = SlingHttpServletRequest.class)
public class ShoppingCartModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartModel.class);

    @SlingObject
    protected SlingHttpServletRequest request;

    @SlingObject
    protected SlingHttpServletResponse response;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(booleanValues = false)
    protected boolean isReadOnly;

    private String checkoutPage;
    private String currentPageUrl;

    protected List<CartEntry> entries = new ArrayList<CartEntry>();
    protected CommerceSession commerceSession;

    @PostConstruct
    public void activate() throws Exception {
        createCommerceSession();
        populatePageUrls();
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

    protected void populateCartEntries() throws CommerceException {
        for (CommerceSession.CartEntry cartEntry : commerceSession.getCartEntries()) {
            CartEntry entry = new CartEntry(cartEntry);
            entries.add(entry);
        }
    }

    private void populatePageUrls() {
        String checkoutPageProperty = WCMUtils.getInheritedProperty(currentPage, resourceResolver, CommerceConstants.PN_CHECKOUT_PAGE_PATH);
        if (StringUtils.isNotEmpty(checkoutPageProperty)) {
            checkoutPage = resourceResolver.map(request, checkoutPageProperty) + ".html";
        }

        currentPageUrl = resourceResolver.map(request, currentPage.getPath() + ".html");
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
        return isReadOnly;
    }

    public class CartEntry {
        private CommerceSession.CartEntry entry;
        private Map<String, String> variantAxesMap = new LinkedHashMap<String, String>();

        public CartEntry(CommerceSession.CartEntry entry) {
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
                LOGGER.error(e.getMessage());
            }
        }

        public CommerceSession.CartEntry getEntry() {
            return entry;
        }

        public String getPrice() throws CommerceException {
            List<PriceInfo> priceInfos = entry.getPriceInfo(new PriceFilter("UNIT"));
            return priceInfos.get(0).getFormattedString();
        }

        public Product getProduct() throws CommerceException {
            return entry.getProduct();
        }

        public String getProductPagePath() throws CommerceException {
            return resourceResolver.map(request, entry.getProduct().getPagePath());
        }

        public String getImage() throws CommerceException {
            if (entry.getProduct().getImage() != null) {
                String imageUrl = entry.getProduct().getImage().getFileReference();
                return resourceResolver.map(request, imageUrl);
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
