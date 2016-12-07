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
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
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
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.PriceFilter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.WCMUtils;

@Model(adaptables = SlingHttpServletRequest.class)
public class NavCartModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavCartModel.class);

    private static final String PN_FILE_REFERENCE = "fileReference";

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] types;

    private String checkoutPage;
    private List<CartEntry> entries = new ArrayList<CartEntry>();
    private CommerceSession commerceSession;
    private String total;

    @PostConstruct
    public void activate() throws Exception {
        createCommerceSession();
        populateCheckoutPage();
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

    private void populateCartEntries() throws CommerceException {
        PriceFilter priceFilter = getPriceFilter();
        List<CommerceSession.CartEntry> cartEntries = commerceSession.getCartEntries();
        total = commerceSession.getCartPrice(priceFilter);

        for (CommerceSession.CartEntry cartEntry : cartEntries) {
            String image = StringUtils.EMPTY;
            if (cartEntry.getProduct().getImage() != null) {
                Resource imageResource = resourceResolver.getResource(cartEntry.getProduct().getImage().getPath());
                if (imageResource != null) {
                    image = imageResource.adaptTo(ValueMap.class).get(PN_FILE_REFERENCE, StringUtils.EMPTY);
                }
            }
            CartEntry entry = new CartEntry(cartEntry, commerceSession.getProductPrice(cartEntry.getProduct()), cartEntry.getProduct(),
                    image);
            entries.add(entry);
        }
    }

    private PriceFilter getPriceFilter() {
        return types != null ? new PriceFilter(types) : null;
    }

    private void populateCheckoutPage() {
        String checkoutPageProperty = WCMUtils.getInheritedProperty(currentPage, resourceResolver, CommerceConstants.PN_CHECKOUT_PAGE_PATH);
        if (StringUtils.isNotEmpty(checkoutPageProperty)) {
            checkoutPage = resourceResolver.map(request, checkoutPageProperty) + ".html";
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
