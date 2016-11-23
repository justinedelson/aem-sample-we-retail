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
package we.retail.core.components.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.xss.XSSAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;

import we.retail.core.WeRetailConstants;


/**
 * The CartEntryServlet handles delete and modify POST request for the commerce cart.
 */
@SuppressWarnings("serial")
@Component
@Service
@Properties(value={
        @Property(name = "service.description", value = "Provides cart services for We.Retail products"),
        @Property(name = "sling.servlet.resourceTypes", value = "sling/servlet/default"),
        @Property(name = "sling.servlet.selectors", value = { WeRetailConstants.DELETE_CARTENTRY_SELECTOR,
                WeRetailConstants.MODIFY_CARTENTRY_SELECTOR }),
        @Property(name = "sling.servlet.extensions", value = {"html"}),
        @Property(name = "sling.servlet.methods", value = "POST")
})
public class CartEntryServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartEntryServlet.class);

    @Reference
    private XSSAPI xssAPI;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        // Make sure commerceService is adapted from a product resource so that we get
        // the right service implementation (hybris, Geo, etc.)
        CommerceService commerceService = request.getResource().adaptTo(CommerceService.class);
        CommerceSession session;
        try {
            session = commerceService.login(request, response);
        } catch (CommerceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        String selectorString = request.getRequestPathInfo().getSelectorString();
        if (WeRetailConstants.DELETE_CARTENTRY_SELECTOR.equals(selectorString)) {
            doDeleteProduct(request, response, session);
        } else if (WeRetailConstants.MODIFY_CARTENTRY_SELECTOR.equals(selectorString)) {
            doModifyProduct(request, response, session);
        }

        String redirect = request.getParameter("redirect");
        if (AuthUtil.isRedirectValid(request, redirect)) {
            response.sendRedirect(redirect);
        } else {
            response.sendError(403);
        }
    }

    private void doModifyProduct(SlingHttpServletRequest request, SlingHttpServletResponse response, CommerceSession session) throws IOException {
        String qty = request.getParameter("quantity");
        if (StringUtils.isBlank(qty)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int quantity = xssAPI.getValidInteger(qty, 1);
        if (quantity < 0) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String entryNumber = request.getParameter("entryNumber");
        if (StringUtils.isBlank(entryNumber)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int entry = xssAPI.getValidInteger(entryNumber, -1);
        try {
            if (entry < 0 || entry >= session.getCartEntries().size()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            if (quantity > 0) {
                session.modifyCartEntry(entry, quantity);
            } else {
                session.deleteCartEntry(entry);
            }

        } catch (CommerceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void doDeleteProduct(SlingHttpServletRequest request, SlingHttpServletResponse response, CommerceSession session) throws IOException {
        String entryNumber = request.getParameter("entryNumber");
        if (StringUtils.isBlank(entryNumber)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int entry = xssAPI.getValidInteger(entryNumber, -1);
        try {
            if (entry < 0 || entry >= session.getCartEntries().size()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            session.deleteCartEntry(entry);
        } catch (CommerceException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
