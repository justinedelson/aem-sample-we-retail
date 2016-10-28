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
package we.retail.core.controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
import com.adobe.cq.commerce.api.Product;

import we.retail.core.components.ProductViewPopulator;
import we.retail.core.model.handler.CommerceHandler;
import we.retail.core.view.ProductView;

@Model(adaptables = SlingHttpServletRequest.class)
public class ProductController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	@SlingObject
	private Resource resource;

	@Inject
	private ProductViewPopulator productViewPopulator;

	@SlingObject
	private SlingHttpServletRequest request;

	@SlingObject
	private SlingHttpServletResponse response;

	@SlingObject
	private ResourceResolver resourceResolver;

	@Self
	private CommerceHandler commerceHandler;

	private CommerceService commerceService;
	private Product product;
	private ProductView productView;

	@PostConstruct
	private void populateProduct() {
		try {
			// The product node represents the node /content/.../jcr:content/root/product
			// Because WeRetailProductImpl extends AbstractJcrProduct, it will properly extracts the
			// properties and variants under /etc/commerce/products/we-retail/...

			product = resource.adaptTo(Product.class);
			if (product != null) {
				commerceService = resource.adaptTo(CommerceService.class);
				CommerceSession commerceSession = commerceService.login(request, response);
				productView = productViewPopulator.populate(product, commerceSession, resourceResolver);
			}
		} catch (CommerceException e) {
			LOGGER.error("Can't extract product from page", e);
		}
	}

	public boolean productExists() {
		return product != null;
	}

	public boolean productHasVariants() {
		return productView != null && !productView.getVariants().isEmpty();
	}

	public ProductView getProductView() {
		return productView;
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
}
