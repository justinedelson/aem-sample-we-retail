/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package we.retail.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import we.retail.core.model.handler.CommerceHandler;

@Model(adaptables = SlingHttpServletRequest.class)
public class Product {

    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);
    private static final String PN_PRODUCT_MASTER = "cq:productMaster";
    public static final String PN_CQ_PRODUCT_VARIANT_AXES = "cq:productVariantAxes";
    public static final String PN_VARIATION_TITLE = "variationTitle";

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

    private com.adobe.cq.commerce.api.Product baseProduct;
    private List<ProductProperties> variants;
    private CommerceService commerceService;
    private ProductProperties properties;
    private ProductVariations variations;
    private String variationTitle;


    @PostConstruct
    private void populateProduct() {
        try {
            baseProduct = getProduct();
            if (baseProduct != null) {
                CommerceSession commerceSession = commerceService.login(request, response);
                variationTitle = baseProduct.getProperty(PN_VARIATION_TITLE, String.class);
                properties = new ProductProperties(baseProduct, commerceSession);
                populateVariations();
                populateVariants(commerceSession);
            }
        } catch (CommerceException e) {
            LOGGER.error("Can't extract product from page", e);
        }

    }

    private void populateVariations() {
        String variantAxes = baseProduct.getProperty(PN_CQ_PRODUCT_VARIANT_AXES, String.class);
        variations = new ProductVariations(variantAxes);
    }

    private void populateVariants(CommerceSession commerceSession) throws CommerceException {
        variants = new ArrayList<ProductProperties>();
        Iterator<com.adobe.cq.commerce.api.Product> productIterator = baseProduct.getVariants();
        while (productIterator.hasNext()) {
            com.adobe.cq.commerce.api.Product product = productIterator.next();
            if (StringUtils.isNotEmpty(product.getSKU())) {
                ProductProperties productProperties = new ProductProperties(product, commerceSession);
                variants.add(productProperties);
                if (variations.getType() == Type.COLOR) {
                    variations.addColorVariation(productProperties);
                } else if (variations.getType() == Type.SIZE) {
                    variations.addSizeVariation(productProperties);
                }
            }
        }
    }


    private com.adobe.cq.commerce.api.Product getProduct() throws CommerceException {
        commerceService = resource.adaptTo(CommerceService.class);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(resource);
        String productPath = currentPage.getProperties().get(PN_PRODUCT_MASTER, String.class);
        return commerceService.getProduct(productPath);
    }

    public com.adobe.cq.commerce.api.Product getBaseProduct() {
        return baseProduct;
    }

    public boolean exists() {
        return this.baseProduct != null;
    }

    public ProductProperties getProperties() {
        return properties;
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

    public ProductVariations getVariations() {
        return variations;
    }

    public String getVariationTitle() {
        return variationTitle;
    }

    public List<ProductProperties> getVariants() {
        return variants;
    }

    public class ProductVariations {
        private Type type;
        private List<ProductProperties> sizes;
        private Map<String, List<ProductProperties>> colors;

        public ProductVariations(String type) {
            this.type = Type.fromString(type);
            this.sizes = new ArrayList<ProductProperties>();
            this.colors = new HashMap<String, List<ProductProperties>>();
        }

        public Map<String, List<ProductProperties>> getColors() {
            return colors;
        }

        public List<ProductProperties> getSizes() {
            return sizes;
        }

        public Type getType() {
            return type;
        }

        public void addSizeVariation(ProductProperties properties) {
            sizes.add(properties);
        }

        public void addColorVariation(ProductProperties properties) {
            String key = properties.getColor().toLowerCase();
            if (colors.get(key) == null) {
                colors.put(key, new ArrayList<ProductProperties>());
            }
            colors.get(key).add(properties);
        }
    }

    public enum Type {
        COLOR, SIZE;

        public static Type fromString(String typeString) {
            for (Type type : Type.values()) {
                if (StringUtils.equalsIgnoreCase(typeString, type.name())) {
                    return type;
                }
            }
            return null;
        }
    }


    public class ProductProperties {
        private static final String PN_SIZE = "size";
        private static final String PN_FEATURES = "features";
        private static final String PN_SUMMARY = "summary";
        private static final String PN_COLOR = "color";
        public static final String PN_FILE_REFERENCE = "fileReference";

        private String path;
        private String pagePath;
        private Iterator<String> variants;
        private String sku;
        private String title;
        private String description;
        private String color;
        private String colorClass;
        private String size;
        private String price;
        private String summary;
        private String features;
        private String image;

        public ProductProperties(com.adobe.cq.commerce.api.Product product, CommerceSession commerceSession) {
            if (product == null) {
                return;
            }
            this.path = product.getPath();
            this.pagePath = product.getPagePath();
            this.variants = product.getVariantAxes();
            this.sku = product.getSKU();
            this.title = product.getTitle();
            this.description = product.getDescription();
            this.color = product.getProperty(PN_COLOR, String.class);
            if (color != null) {
                this.colorClass = color.toLowerCase();
            }
            this.size = product.getProperty(PN_SIZE, String.class);
            try {
                this.price = commerceSession.getProductPrice(product);
            } catch (CommerceException e) {
                LOGGER.error("Error getting the product price: {}", e);
            }
            this.summary = product.getProperty(PN_SUMMARY, String.class);
            this.features = product.getProperty(PN_FEATURES, String.class);
            this.image = getImage(product);
        }

        public String getPath() {
            return path;
        }

        public String getPagePath() {
            return pagePath;
        }

        public Iterator<String> getVariants() {
            return variants;
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

        public String getColor() {
            return color;
        }

        public String getColorClass() {
            return colorClass;
        }

        public String getSize() {
            return size;
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

        public String getImage() {
            return image;
        }

        private String getImage(com.adobe.cq.commerce.api.Product product) {
            ImageResource image = product.getImage();
            if (image == null) {
                return null;
            }
            Resource productImageRes = resourceResolver.getResource(image.getPath());
            return getFileReference(productImageRes);
        }

        private String getFileReference(Resource productImageResource) {
            if (productImageResource != null) {
                ValueMap valueMap = productImageResource.adaptTo(ValueMap.class);
                if (valueMap != null && valueMap.containsKey(PN_FILE_REFERENCE)) {
                    return valueMap.get(PN_FILE_REFERENCE, StringUtils.EMPTY);
                }
            }
            return StringUtils.EMPTY;
        }

    }
}
