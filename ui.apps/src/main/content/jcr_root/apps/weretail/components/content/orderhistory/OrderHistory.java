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
package apps.weretail.components.content.orderhistory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceConstants;
import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PlacedOrder;
import com.adobe.cq.commerce.api.PlacedOrderResult;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.common.PriceFilter;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.security.user.UserProperties;

import com.day.cq.personalization.UserPropertiesUtil;

public class OrderHistory extends WCMUsePojo {

    private static final Logger LOG = LoggerFactory.getLogger(OrderHistory.class);
    
    private CommerceSession commerceSession;
    private List<PlacedOrder> orders;
    
    @Override
    public void activate() throws Exception {
        createCommerceSession();    
        PlacedOrderResult orderResult = commerceSession.getPlacedOrders(null, 0, 0, null);
        orders = orderResult.getOrders();
        Collections.reverse(orders);
    }
    
    private void createCommerceSession() {
        CommerceService commerceService = getCurrentPage().getContentResource().adaptTo(CommerceService.class);
        try {
            commerceSession = commerceService.login(getRequest(), getResponse());
        } catch (CommerceException e) {
            LOG.error(e.getMessage());
        }
    }
    
    public boolean isAnonymous() {
        final UserProperties userProperties = getRequest().adaptTo(UserProperties.class);
        if (userProperties == null || UserPropertiesUtil.isAnonymous(userProperties)) {
            return true;
        } else {
            return false;
        }
    }
    
    public List<PlacedOrder> getOrders() {
        return orders;
    }
    
    public boolean isEmpty() {
        return orders == null || orders.isEmpty();
    }
}
