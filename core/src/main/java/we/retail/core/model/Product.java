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
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;

import we.retail.core.model.handler.CommerceHandler;
import we.retail.core.view.ProductView;
import we.retail.core.view.ProductViewPopulator;

@Model(adaptables = SlingHttpServletRequest.class)
public class Product {

    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    private CommerceHandler commerceHandler;

    private CommerceService commerceService;
    private ProductView product;

    @PostConstruct
    private void populateProduct() {
        try {
            commerceService = resource.adaptTo(CommerceService.class);
            if (commerceService != null) {
                CommerceSession commerceSession = commerceService.login(request, response);
                product = ProductViewPopulator.populate(resource, commerceSession);
            }
        } catch (CommerceException e) {
            LOGGER.error("Can't extract product from page", e);
        }
    }

    public boolean exists() {
        return product != null;
    }

    public boolean hasVariants() {
        return product != null && !product.getVariants().isEmpty();
    }

    public String getImage() {
        return product.getImage();
    }

    public String getPrice() {
        return product.getPrice();
    }

    public String getSku() {
        return product.getSku();
    }

    public String getSummary() {
        return product.getSummary();
    }

    public String getTitle() {
        return product.getTitle();
    }

    public List<ProductView> getVariants() {
        return product.getVariants();
    }

    public Map<String, Map<String, List<ProductView>>> getVariantsAxesMap() {
        return product.getAllVariantsByAxes();
    }

    public String getAddToCartUrl() {
        return commerceHandler.getAddToCardUrl();
    }

    public String getRedirect() {
        return commerceHandler.getRedirect();
    }

    public String getErrorRedirect() {
        return commerceHandler.getErrorRedirect();
    }

    public String getProductTrackingPath() {
        return commerceHandler.getProductTrackingPath();
    }
}
