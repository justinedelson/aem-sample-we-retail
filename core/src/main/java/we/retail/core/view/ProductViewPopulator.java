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
package we.retail.core.view;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.Product;
import com.day.cq.commons.ImageResource;

public class ProductViewPopulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductViewPopulator.class);

    private static final String PN_FEATURES = "features";
    private static final String PN_SUMMARY = "summary";
    private static final String PN_FILE_REFERENCE = "fileReference";

    public static ProductView populate(Resource resource, CommerceSession commerceSession) {

        // The product node represents the node /content/.../jcr:content/root/product
        // Because WeRetailProductImpl extends AbstractJcrProduct, it will properly extracts the
        // properties and variants under /etc/commerce/products/we-retail/...

        Product product = resource.adaptTo(Product.class);
        if (product != null) {
            ResourceResolver resourceResolver = resource.getResourceResolver();
            return populate(product, commerceSession, resourceResolver, null);
        } else {
            return null;
        }
    }

    private static ProductView populate(Product product, CommerceSession commerceSession, ResourceResolver resourceResolver,
            ProductView baseProductView) {

        ProductView productView = new ProductView();
        productView.setPath(product.getPath());
        productView.setPagePath(product.getPagePath());
        productView.setSku(product.getSKU());
        productView.setTitle(product.getTitle());
        productView.setDescription(product.getDescription());

        productView.setSummary(product.getProperty(PN_SUMMARY, String.class));
        productView.setFeatures(product.getProperty(PN_FEATURES, String.class));

        productView.setImage(getImage(product, resourceResolver));

        if (commerceSession != null) {
            try {
                productView.setPrice(commerceSession.getProductPrice(product));
            } catch (CommerceException e) {
                LOGGER.error("Error getting the product price: {}", e);
            }
        }

        if (baseProductView == null) {
            String[] productVariantAxes = product.getProperty(CommerceConstants.PN_PRODUCT_VARIANT_AXES, String[].class);
            if (productVariantAxes != null) {
                productView.setVariantAxes(productVariantAxes);
            }
            populateAllVariants(product, commerceSession, resourceResolver, productView);
        } else {
            populateTheVariantAxesValues(baseProductView, product, productView, resourceResolver);
        }

        return productView;
    }

    private static void populateAllVariants(Product product, CommerceSession commerceSession, ResourceResolver resourceResolver,
            ProductView productView) {

        try {
            Iterator<Product> variants = product.getVariants();
            while (variants.hasNext()) {
                ProductView variant = populate(variants.next(), commerceSession, resourceResolver, productView);
                productView.addVariant(variant);
            }

            // If there are no variants, the product itself is defined as the first variant
            if (productView.getVariants().isEmpty()) {
                productView.addVariant(productView);
            }
        } catch (CommerceException e) {
            LOGGER.error("Error getting the product variants: {}", e);
        }
    }

    private static void populateTheVariantAxesValues(ProductView baseProductView, Product product, ProductView productView,
            ResourceResolver resourceResolver) {

        for (String variantAxis : baseProductView.getVariantAxes()) {
            String value = product.getProperty(variantAxis, String.class);
            if (value != null) {
                productView.addVariantAxisValue(variantAxis, value);
            }
        }
    }

    private static String getImage(Product product, ResourceResolver resourceResolver) {
        ImageResource image = product.getImage();
        if (image == null) {
            return null;
        }
        Resource productImageRes = resourceResolver.getResource(image.getPath());
        return getFileReference(productImageRes);
    }

    private static String getFileReference(Resource productImageResource) {
        if (productImageResource != null) {
            ValueMap valueMap = productImageResource.adaptTo(ValueMap.class);
            if (valueMap != null && valueMap.containsKey(PN_FILE_REFERENCE)) {
                return valueMap.get(PN_FILE_REFERENCE, StringUtils.EMPTY);
            }
        }
        return StringUtils.EMPTY;
    }

}
