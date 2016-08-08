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
import com.adobe.cq.commerce.common.CommerceHelper;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class)
public class CommerceHandler {

    public static final String ADD_CART_ENTRY_SELECTOR = ".commerce.addcartentry.html";
    public static final String ADD_SELECTOR = ".add.html";
    public static final String PN_ADD_TO_CART_REDIRECT = "addToCartRedirect";
    public static final String PN_CART_ERROR_REDIRECT = "cartErrorRedirect";
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

    private String addToCardUrl;
    private Page currentPage;
    private String redirect;
    private String errorRedirect;

    @PostConstruct
    private void initHandler() {
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
}
