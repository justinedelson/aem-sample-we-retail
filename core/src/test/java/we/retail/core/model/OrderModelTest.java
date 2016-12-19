package we.retail.core.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.dam.commons.util.DateParser;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;

import common.AppAemContext;
import common.mock.MockCommerceSession;
import common.mock.MockDefaultJcrPlacedOrder;
import io.wcm.testing.mock.aem.junit.AemContext;
import we.retail.core.model.ShoppingCartModel.CartEntry;

@RunWith(MockitoJUnitRunner.class)
public class OrderModelTest {

    private static final String ORDER_ID = "5ad90db8-593f-4403-af91-2dd6df0aefd7";
    private static final String CURRENT_RESOURCE = "/etc/commerce/orders/2016/12/12/order";
    private static final String CURRENT_PAGE = "/content/we-retail/us/en/user/account/order-history/order-details";

    // The order is defined in src/test/resources/sample-order.json
    private static final String SUB_TOTAL = "$167.00";
    private static final String SHIPPING_TOTAL = "$10.00";
    private static final String TAX_TOTAL = "$10.02";
    private static final String TOTAL = "$187.02";
    private static final String BILLING_ADDRESS = "John Doe, 601 Townsend St 3rd floor, US-94103 San Francisco, CA";
    private static final String SHIPPING_ADDRESS = "John Doe, 94103 San Francisco, CA";
    private static final String ORDER_STATUS = "Processing";
    private static final String ORDER_DATE = "Mon Dec 12 2016 16:42:15 GMT+0100";

    private static final String ENTRY_0_PATH = "/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe/jcr:content/root/product/eqrusufle-9";
    private static final String ENTRY_0_PRICE = "$24.00";

    private static final String ENTRY_1_PATH = "/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe/jcr:content/root/product/eqrusufle-11";
    private static final String ENTRY_1_PRICE = "$119.00";

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    private SlingBindings slingBindings;
    private OrderModel orderModel;
    private MockDefaultJcrPlacedOrder mockDefaultJcrPlacedOrder;

    @Before
    public void setUp() throws Exception {
        Page page = context.currentPage(CURRENT_PAGE);
        context.currentResource(CURRENT_RESOURCE);

        MockSlingHttpServletRequest request = context.request();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("orderId", ORDER_ID);
        request.setParameterMap(parameters);

        request.setAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);

        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);
        slingBindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(request));

        Resource orderResource = context.resourceResolver().getResource(CURRENT_RESOURCE);
        mockDefaultJcrPlacedOrder = new MockDefaultJcrPlacedOrder(null, ORDER_ID, orderResource);

        CommerceService commerceService = page.getContentResource().adaptTo(CommerceService.class);
        MockCommerceSession commerceSession = (MockCommerceSession) commerceService.login(request, context.response());
        commerceSession.registerPlacedOrder(ORDER_ID, mockDefaultJcrPlacedOrder);

        orderModel = request.adaptTo(OrderModel.class);
    }

    @Test
    public void testOrder() throws Exception {
        assertEquals(ORDER_ID, orderModel.getOrderId());
        assertEquals(SUB_TOTAL, orderModel.getSubTotal());
        assertEquals(SHIPPING_TOTAL, orderModel.getShippingTotal());
        assertEquals(TAX_TOTAL, orderModel.getTaxTotal());
        assertEquals(TOTAL, orderModel.getTotal());
        assertEquals(BILLING_ADDRESS, orderModel.getBillingAddress());
        assertEquals(SHIPPING_ADDRESS, orderModel.getShippingAddress());
        assertEquals(ORDER_STATUS, orderModel.getOrderStatus());
        Date orderDate = DateParser.parseDate(ORDER_DATE);
        assertEquals(orderDate, orderModel.getOrderDate().getTime());

        List<CartEntry> entries = orderModel.getEntries();
        assertEquals(2, entries.size());

        CartEntry entry0 = entries.get(0);
        assertEquals(ENTRY_0_PATH, entry0.getProduct().getPath());
        assertEquals(ENTRY_0_PRICE, entry0.getPrice());

        CartEntry entry1 = entries.get(1);
        assertEquals(ENTRY_1_PATH, entry1.getProduct().getPath());
        assertEquals(ENTRY_1_PRICE, entry1.getPrice());
    }

}
