package common;


import java.io.IOException;
import javax.annotation.Nullable;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.Product;
import com.google.common.base.Function;
import common.mock.MockCommerceService;
import common.mock.MockProduct;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextCallback;

public class AppAemContext {

    public static final String CONTENT_ROOT = "/content/we-retail/us/en";
    public static final String PRODUCT_ROOT = "/etc/commerce/products/we-retail";


    private AppAemContext() {
        // only static methods
    }

    public static AemContext newAemContext() {
        return new AemContext(new SetUpCallback(), ResourceResolverType.RESOURCERESOLVER_MOCK);
    }

    private static final class SetUpCallback implements AemContextCallback {

        @Override
        public void execute(AemContext context) throws IOException {
            context.registerAdapter(Resource.class, CommerceService.class,
                    new Function<Resource, CommerceService>() {
                        @Nullable
                        @Override
                        public CommerceService apply(@Nullable Resource resource) {
                            return new MockCommerceService(resource);
                        }
                    });
            context.registerAdapter(Resource.class, Product.class, new Function<Resource, Product>() {
                @Nullable
                @Override
                public Product apply(@Nullable Resource resource) {
                    return new MockProduct(resource);
                }
            });
            context.addModelsForPackage("we.retail.core.model");
            context.load().json("/sample-content.json", CONTENT_ROOT);
            context.load().json("/sample-product.json", PRODUCT_ROOT);
        }
    }
}
