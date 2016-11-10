/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package we.retail.core;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.commons.collections.Predicate;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.common.AbstractJcrCommerceService;
import com.adobe.cq.commerce.common.AbstractJcrCommerceSession;
import com.day.cq.i18n.I18n;

public class WeRetailCommerceSessionImpl extends AbstractJcrCommerceSession {

    private static final Map<String, BigDecimal> shippingCosts = new HashMap<String, BigDecimal>() {
        {
            // A simple shipping pricing architecture with fixed shipping costs.

            put("/etc/commerce/shipping-methods/we-retail/two-business-day", BigDecimal.ZERO);
            put("/etc/commerce/shipping-methods/we-retail/standard-shipping", BigDecimal.ZERO);
            put("/etc/commerce/shipping-methods/we-retail/ground-shipping", new BigDecimal("10.00"));
            put("/etc/commerce/shipping-methods/we-retail/one-business-day", new BigDecimal("25.00"));
        }
    };

    public WeRetailCommerceSessionImpl(AbstractJcrCommerceService commerceService,
                                  SlingHttpServletRequest request,
                                  SlingHttpServletResponse response,
                                  Resource resource) throws CommerceException {
        super(commerceService, request, response, resource);
        PN_UNIT_PRICE = WeRetailProductImpl.PN_PRICE;
    }

    @Override
    protected BigDecimal getShipping(String method) {
        BigDecimal shippingCost = shippingCosts.get(method);
        return shippingCost != null ? shippingCost : BigDecimal.ZERO;
    }

    @Override
    protected String tokenizePaymentInfo(Map<String, String> paymentDetails) throws CommerceException {
        //
        // This is only a stub implementation for the Geometrixx-Outdoors demo site, for which there is no
        // real payment processing (or payment info tokenization).
        //
        return "faux-payment-token";
    }

    @Override
    protected void initiateOrderProcessing(String orderPath) throws CommerceException {
        //
        // This is only a stub implementation for the Geometrixx-Outdoors demo site, for which there is no
        // real order processing.
        //
        try {
            Node order = resolver.getResource(orderPath).adaptTo(Node.class);
            order.setProperty("orderStatus", "Processing");
            order.getSession().save();
        } catch (Exception e) {
            log.error("Failed to update order", e);
        }
    }

    @Override
    protected String getOrderStatus(String orderId) throws CommerceException {
        //
        // Status is kept in the vendor section (/etc/commerce); need to find corresponding order there.
        //
        Session serviceSession = null;
        try {
            serviceSession = commerceService.serviceContext().slingRepository.loginService("orders", null);
            //
            // example query: /jcr:root/etc/commerce/orders//element(*)[@orderId='foo')]
            //
            StringBuilder buffer = new StringBuilder();
            buffer.append("/jcr:root/etc/commerce/orders//element(*)[@orderId = '")
                    .append(Text.escapeIllegalXpathSearchChars(orderId).replaceAll("'", "''"))
                    .append("']");

            final Query query = serviceSession.getWorkspace().getQueryManager().createQuery(buffer.toString(), Query.XPATH);
            NodeIterator nodeIterator = query.execute().getNodes();
            if (nodeIterator.hasNext()) {
                return nodeIterator.nextNode().getProperty("orderStatus").getString();
            }
        } catch (Exception e) {
            // fail-safe when the query above contains errors
            log.error("Error while fetching order status for orderId '" + orderId + "'", e);
        } finally {
            if (serviceSession != null) {
                serviceSession.logout();
            }
        }
        final I18n i18n = new I18n(request);
        return i18n.get("unknown", "order status");
    }

    @Override
    protected Predicate getPredicate(String predicateName) {
        //
        // This stub implementation supports only the openOrders predicate.
        //
        if (predicateName != null && predicateName.equals(CommerceConstants.OPEN_ORDERS_PREDICATE)) {
            return new Predicate() {
                public boolean evaluate(Object object) {
                    try {
                        PlacedOrder order = (PlacedOrder) object;
                        String status = (String) order.getOrder().get("orderStatus");
                        return (status != null && !status.equals("Completed") && !status.equals("Cancelled"));
                    } catch (CommerceException e) {
                        return false;
                    }
                }
            };
        }
        return null;
    }
}
