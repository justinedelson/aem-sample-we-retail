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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.Product;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.Page;

import we.retail.core.model.handler.CommerceHandler;

@Model(adaptables = SlingHttpServletRequest.class)
public class ProductModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductModel.class);

    @SlingObject
    private Resource resource;

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    @Self
    private CommerceHandler commerceHandler;

    private CommerceService commerceService;
    private ProductItem productItem;
    private boolean isAnonymous;

    @PostConstruct
    private void populateProduct() {
        try {
            commerceService = currentPage.getContentResource().adaptTo(CommerceService.class);
            if (commerceService != null) {
                CommerceSession commerceSession = commerceService.login(request, response);
                Product product = resource.adaptTo(Product.class);

                // If the product is null, it might be a proxy page and the commerceHandler handles that
                if (product == null) {
                    product = commerceHandler.getProduct();
                }

                if (product != null) {
                    productItem = new ProductItem(product, commerceSession, resource.getResourceResolver());
                }
            }
        } catch (CommerceException e) {
            LOGGER.error("Can't extract product from page", e);
        }

        isAnonymous = resourceResolver.getUserID() == null || resourceResolver.getUserID().equals("anonymous") ? true
                : false;
    }

    public ProductItem getProductItem() {
        return productItem;
    }

    public boolean hasVariants() {
        return productItem != null && !productItem.getVariants().isEmpty();
    }

    public String getAddToCartUrl() {
        return commerceHandler.getAddToCardUrl();
    }

    public String getAddToSmartListUrl() {
        return commerceHandler.getAddToSmartListUrl();
    }

    public String getProductTrackingPath() {
        return commerceHandler.getProductTrackingPath();
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public class ProductItem {

        private static final String PN_FEATURES = "features";
        private static final String PN_SUMMARY = "summary";

        private String path;
        private String pagePath;
        private String sku;
        private String title;
        private String description;
        private String price;
        private String summary;
        private String features;
        private String imageUrl;
        private String thumbnailUrl;

        private List<ProductItem> variants = new ArrayList<ProductItem>();

        private List<String> variantAxes = new ArrayList<String>();
        private Map<String, String> variantAxesMap = new LinkedHashMap<String, String>();

        public ProductItem(Product product, CommerceSession commerceSession, ResourceResolver resourceResolver) {
            this(product, commerceSession, resourceResolver, null);
        }

        private ProductItem(Product product, CommerceSession commerceSession, ResourceResolver resourceResolver,
                ProductItem baseProductItem) {

            path = product.getPath();
            pagePath = product.getPagePath();
            if (StringUtils.isNotBlank(pagePath)) {
                pagePath = resourceResolver.map(request, pagePath);
            }
            sku = product.getSKU();
            title = product.getTitle();
            description = product.getDescription();

            summary = product.getProperty(PN_SUMMARY, String.class);
            features = product.getProperty(PN_FEATURES, String.class);

            ImageResource image = product.getImage();
            imageUrl = image != null ? image.getFileReference() : null;
            if (StringUtils.isNotBlank(imageUrl)) {
                imageUrl = resourceResolver.map(request, imageUrl);
            }

            thumbnailUrl = product.getThumbnailUrl();
            if (StringUtils.isNotBlank(thumbnailUrl)) {
                thumbnailUrl = resourceResolver.map(request, thumbnailUrl);
            }

            if (commerceSession != null) {
                try {
                    price = commerceSession.getProductPrice(product);
                } catch (CommerceException e) {
                    LOGGER.error("Error getting the product price: {}", e);
                }
            }

            if (baseProductItem == null) {
                String[] productVariantAxes = product.getProperty(CommerceConstants.PN_PRODUCT_VARIANT_AXES, String[].class);
                if (productVariantAxes != null) {
                    setVariantAxes(productVariantAxes);
                }
                populateAllVariants(product, commerceSession, resourceResolver);
            } else {
                populateVariantAxesValues(baseProductItem.variantAxes, product);
            }
        }

        private void populateAllVariants(Product product, CommerceSession commerceSession, ResourceResolver resourceResolver) {

            try {
                Iterator<Product> productVariants = product.getVariants();
                while (productVariants.hasNext()) {
                    ProductItem variant = new ProductItem(productVariants.next(), commerceSession, resourceResolver, this);
                    variants.add(variant);
                }

                // If there are no variants, the product itself is defined as the first variant
                if (variants.isEmpty()) {
                    variants.add(this);
                }
            } catch (CommerceException e) {
                LOGGER.error("Error getting the product variants: {}", e);
            }
        }

        private void populateVariantAxesValues(List<String> variantAxes, Product product) {
            for (String variantAxis : variantAxes) {
                String value = product.getProperty(variantAxis, String.class);
                if (value != null && !variantAxesMap.containsKey(variantAxis)) {
                    variantAxesMap.put(variantAxis, value);
                }
            }
        }

        private void setVariantAxes(String[] variantAxes) {
            if (variantAxes != null) {
                for (String axis : variantAxes) {
                    this.variantAxes.add(axis.trim());
                }
            }
        }

        public String getPath() {
            return path;
        }

        public String getPagePath() {
            return pagePath;
        }

        public String getSku() {
            return sku;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPrice() {
            return price;
        }

        public String getSummary() {
            return summary;
        }

        public String getFeatures() {
            return features;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public List<ProductItem> getVariants() {
            return variants;
        }

        /**
         * This method returns the value (if any) for the given variant axis.
         * 
         * @param axis
         *            The name of the variant axis, for example "color" or "size".
         * @return The value (for example, "red") for that axis, or null if the variant does not have a value for that axis.
         */
        public String getVariantValueForAxis(String axis) {
            return variantAxesMap.get(axis);
        }

        /**
         * This method returns a JSON representation of the variant axes and values for a product variant.<br/>
         * For example and since the variant axes and values are typically represented as a map, this method might return the following
         * String for a variant product with 2 axes color and size:<br/>
         * <br/>
         * <code>{'color':'red','size':'XS'}</code>
         * 
         * @return The JSON representation of the variant axes and values.
         */
        public String getVariantAxesMapJson() {
            return new JSONObject(variantAxesMap).toString();
        }

        /**
         * This method returns a map of variant axes and all their respective values by axis.<br/>
         * The keys of the map represent the axis names (e.g. color, size), and the values are stored in a Collection (e.g. <red, green
         * blue> for the 'color' key).<br/>
         * <br/>
         * For example, the returned map can look like<br/>
         * <code>color --> red, green, blue<br/>
         * size --> XS, S, M</code>
         * 
         * @return The map of all variant axes and their respective values.
         */
        public Map<String, Collection<String>> getVariantsAxesValues() {
            if (variants.isEmpty() || variantAxes.isEmpty()) {
                return Collections.emptyMap();
            }

            Map<String, Collection<String>> map = new LinkedHashMap<String, Collection<String>>();
            for (String axis : variantAxes) {
                for (ProductItem variant : variants) {
                    String axisValue = variant.variantAxesMap.get(axis);
                    if (axisValue != null) {
                        Collection<String> set = map.get(axis);
                        if (set == null) {
                            set = new LinkedHashSet<String>();
                            map.put(axis, set);
                        }

                        if (!set.contains(axisValue)) {
                            set.add(axisValue);
                        }
                    }
                }
            }

            return map;
        }
    }
}
