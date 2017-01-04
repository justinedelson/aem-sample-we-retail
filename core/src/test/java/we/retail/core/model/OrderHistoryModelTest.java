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
package we.retail.core.model;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.dam.commons.util.DateParser;
import com.day.cq.wcm.api.Page;

import common.AppAemContext;
import common.mock.MockCommerceSession;
import common.mock.MockDefaultJcrPlacedOrder;
import io.wcm.testing.mock.aem.junit.AemContext;

public class OrderHistoryModelTest {

    private static final String DUMMY_ORDER_ID = "dummy-order-id";
    private static final String DUMMY_ORDER_DATE = "Sun Dec 11 2016 16:42:15 GMT+0100";

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    private OrderHistoryModel orderHistoryModel;

    @Before
    public void setUp() throws Exception {
        Page page = context.currentPage(Constants.TEST_ORDER_PAGE);
        context.currentResource(Constants.TEST_ORDER_RESOURCE);

        MockSlingHttpServletRequest request = context.request();

        SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);

        CommerceService commerceService = page.getContentResource().adaptTo(CommerceService.class);
        MockCommerceSession commerceSession = (MockCommerceSession) commerceService.login(request, context.response());

        Resource orderResource = context.resourceResolver().getResource(Constants.TEST_ORDER_RESOURCE);

        // The dummy order is inserted first but should appear second in the test (see below)
        MockDefaultJcrPlacedOrder dummyOrder = new MockDefaultJcrPlacedOrder(null, DUMMY_ORDER_ID, orderResource);
        dummyOrder.setOrderId(DUMMY_ORDER_ID);
        dummyOrder.setOrderPlacedDate(DateParser.parseDate(DUMMY_ORDER_DATE));
        commerceSession.registerPlacedOrder(DUMMY_ORDER_ID, dummyOrder);

        MockDefaultJcrPlacedOrder mockDefaultJcrPlacedOrder = new MockDefaultJcrPlacedOrder(null, Constants.TEST_ORDER_ID, orderResource);
        commerceSession.registerPlacedOrder(Constants.TEST_ORDER_ID, mockDefaultJcrPlacedOrder);

        orderHistoryModel = request.adaptTo(OrderHistoryModel.class);
    }

    @Test
    public void testOrderHistory() throws Exception {
        List<PlacedOrder> orders = orderHistoryModel.getOrders();
        assertEquals(Constants.ENTRIES_SIZE, orders.size());

        // The dummy order should always appear second in the list because of the descending sorting by date
        // done in OrderHistoryModel
        Date date0 = ((Calendar) orders.get(0).getOrder().get(Constants.ORDER_PLACED)).getTime();
        Date date1 = (Date) orders.get(1).getOrder().get(Constants.ORDER_PLACED);
        assertEquals(1, date0.compareTo(date1));
        assertEquals(Constants.TEST_ORDER_ID, orders.get(0).getOrderId());
        assertEquals(DUMMY_ORDER_ID, orders.get(1).getOrderId());
    }
}
