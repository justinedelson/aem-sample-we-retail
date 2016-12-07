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

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.PlacedOrderResult;
import com.adobe.granite.security.user.UserProperties;
import com.day.cq.personalization.UserPropertiesUtil;
import com.day.cq.wcm.api.Page;

@Model(adaptables = SlingHttpServletRequest.class)
public class OrderHistoryModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderHistoryModel.class);

    @SlingObject
    private SlingHttpServletRequest request;

    @SlingObject
    private SlingHttpServletResponse response;

    @Inject
    private Page currentPage;

    private CommerceSession commerceSession;
    private List<PlacedOrder> orders;

    @PostConstruct
    private void activate() {
        try {
            CommerceService commerceService = currentPage.getContentResource().adaptTo(CommerceService.class);
            commerceSession = commerceService.login(request, response);
            PlacedOrderResult orderResult = commerceSession.getPlacedOrders(null, 0, 0, null);
            orders = orderResult.getOrders();
            orders.sort(orderComparator);
        } catch (CommerceException e) {
            LOGGER.error("Failed to initialize sling model", e);
        }
    }

    // Sorting by date, descending
    private Comparator<PlacedOrder> orderComparator = new Comparator<PlacedOrder>() {

        @Override
        public int compare(PlacedOrder o1, PlacedOrder o2) {
            Object p1, p2;
            try {
                p1 = o1.getOrder().get("orderPlaced");
                p2 = o2.getOrder().get("orderPlaced");
            } catch (CommerceException e) {
                return 0;
            }

            if (p1 == null || p2 == null) {
                return p1 == null ? (p2 == null ? 0 : 1) : -1;
            }

            boolean p1IsCalendar = (p1 instanceof Calendar);
            boolean p2IsCalendar = (p2 instanceof Calendar);

            if (p1IsCalendar && p2IsCalendar) {
                Calendar c1 = (Calendar) p1;
                Calendar c2 = (Calendar) p2;
                return c2.compareTo(c1);
            } else {
                return p1IsCalendar ? -1 : (p2IsCalendar ? 1 : 0);
            }
        }
    };

    public boolean isAnonymous() {
        final UserProperties userProperties = request.adaptTo(UserProperties.class);
        return userProperties == null || UserPropertiesUtil.isAnonymous(userProperties);
    }

    public List<PlacedOrder> getOrders() {
        return orders;
    }

    public boolean isEmpty() {
        return orders == null || orders.isEmpty();
    }
}
