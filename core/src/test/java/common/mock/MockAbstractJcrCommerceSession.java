package common.mock;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.common.AbstractJcrCommerceService;
import com.adobe.cq.commerce.common.AbstractJcrCommerceSession;

public class MockAbstractJcrCommerceSession extends AbstractJcrCommerceSession {

    private Map<String, PlacedOrder> placedOrders = new HashMap<String, PlacedOrder>();

    public MockAbstractJcrCommerceSession(AbstractJcrCommerceService commerceService, SlingHttpServletRequest request,
            SlingHttpServletResponse response, Resource resource) throws CommerceException {
        super(commerceService, request, response, resource);
    }

    @Override
    public PlacedOrder getPlacedOrder(String orderId) throws CommerceException {
        return placedOrders.get(orderId);
    }

    public void registerPlacedOrder(String orderId, PlacedOrder placedOrder) {
        placedOrders.put(orderId, placedOrder);
    }
}
