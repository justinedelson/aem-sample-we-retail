package common.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.api.VariantFilter;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class MockProduct extends ResourceWrapper implements Product {

    private final Resource resource;

    public MockProduct(@Nonnull Resource resource) {
        super(resource);
        this.resource = resource;
    }

    @Override
    public boolean axisIsVariant(String axis) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPagePath() {
        PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        Page page = pageManager.getContainingPage(resource);
        return (page != null ? page.getPath() : null);
    }

    @Override
    public String getSKU() {
        String sku = getProperty("identifier", String.class);
        String size = getProperty("size", String.class);
        if (size != null && size.length() > 0) {
            sku += "-" + size;
        }
        return sku;
    }

    @Override
    public String getTitle() {
        return getValueMap().get("jcr:title", String.class);
    }

    @Override
    public String getTitle(String selectorString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDescription() {
        return getValueMap().get("jcr:description", String.class);
    }

    @Override
    public String getDescription(String selectorString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getThumbnailUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getThumbnailUrl(int width) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getThumbnailUrl(String selectorString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Resource getAsset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Resource> getAssets() {
        return null;
    }

    @Override
    public ImageResource getImage() {
        return null;
    }

    @Override
    public List<ImageResource> getImages() {
        return null;
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        return getValueMap().get(name, type);
    }

    @Override
    public <T> T getProperty(String name, String selectorString, Class<T> type) {
        return null;
    }

    @Override
    public Iterator<String> getVariantAxes() {
        return null;
    }

    @Override
    public Iterator<Product> getVariants() throws CommerceException {
        final List<Product> variants = new ArrayList<Product>();
        for (Resource child : resource.getChildren()) {
            if (StringUtils.equals(child.getValueMap().get("cq:commerceType", String.class), "variant")) {
                variants.add(new MockProduct(child));
            }
        }
        return variants.iterator();
    }

    @Override
    public Iterator<Product> getVariants(VariantFilter filter) throws CommerceException {
        return null;
    }

    @Override
    public Product getBaseProduct() throws CommerceException {
        return null;
    }

    @Override
    public Product getPIMProduct() throws CommerceException {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public ImageResource getThumbnail() {
        return null;
    }
}
