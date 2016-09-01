package we.retail.core.model.handler;


import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.CommerceHelper;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class)
public class CommerceHandler {

    private static final String ADD_CART_ENTRY_SELECTOR = ".commerce.addcartentry.html";
    private static final String ADD_SELECTOR = ".add.html";
    private static final String PN_ADD_TO_CART_REDIRECT = "addToCartRedirect";
    private static final String PN_CART_ERROR_REDIRECT = "cartErrorRedirect";
    private static final String REQ_ATTR_CQ_COMMERCE_PRODUCT = "cq.commerce.product";

    @SlingObject
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Style currentStyle;

    @RequestAttribute(name = CommerceConstants.REQ_ATTR_CARTPAGE, optional = true)
    private String cartPage;

    @RequestAttribute(name = CommerceConstants.REQ_ATTR_CARTOBJECT, optional = true)
    private String cartObject;

    @RequestAttribute(name = CommerceConstants.REQ_ATTR_PRODNOTFOUNDPAGE, optional = true)
    private String productNotFound;

    @RequestAttribute(name = REQ_ATTR_CQ_COMMERCE_PRODUCT, optional = true)
    private Product product;

    private String addToCardUrl;
    private Page currentPage;
    private String redirect;
    private String errorRedirect;
    private boolean productPageProxy = false;

    @PostConstruct
    private void initHandler() throws CommerceException {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        currentPage = pageManager.getContainingPage(resource);
        addToCardUrl = currentPage.getPath() + ADD_CART_ENTRY_SELECTOR;
        redirect = CommerceHelper.mapPathToCurrentLanguage(currentPage, currentStyle.get(PN_ADD_TO_CART_REDIRECT, StringUtils.EMPTY));
        errorRedirect = CommerceHelper.mapPathToCurrentLanguage(currentPage, currentStyle.get(PN_CART_ERROR_REDIRECT, StringUtils.EMPTY));

        if(StringUtils.isEmpty(redirect) && StringUtils.isNotEmpty(cartObject)) {
            redirect = cartPage;
            errorRedirect = productNotFound;
            addToCardUrl = cartObject + ADD_SELECTOR;
        }
        if(StringUtils.isEmpty(redirect) || StringUtils.equals(redirect, ".")) {
            redirect = currentPage.getPath();
        }
        if(StringUtils.isEmpty(errorRedirect)) {
            errorRedirect = currentPage.getPath();
        }
        if(product == null) {
            product = resource.adaptTo(Product.class);
        } else {
            productPageProxy = true;
        }
    }

    public String getAddToCardUrl() {
        return addToCardUrl;
    }

    public String getRedirect() {
        return redirect;
    }

    public String getErrorRedirect() {
        return errorRedirect;
    }

    public boolean isProductPageProxy() {
        return productPageProxy;
    }

    public Product getProduct() {
        return product;
    }
}
