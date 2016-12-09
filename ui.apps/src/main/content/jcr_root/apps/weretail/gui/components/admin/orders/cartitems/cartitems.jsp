<%--
    Copyright 2016 Adobe Systems Incorporated
  
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
  
        http://www.apache.org/licenses/LICENSE-2.0
  
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    ==============================================================================

    Vendor order admin component which displays the cart items in the order.

    ==============================================================================
--%>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" import="
		java.util.List,
		com.adobe.cq.commerce.api.CommerceSession,
		com.adobe.cq.commerce.api.Product,
		com.day.cq.i18n.I18n,
		com.day.cq.wcm.foundation.forms.FormsConstants,
        com.adobe.granite.ui.components.ComponentHelper,
        com.adobe.granite.ui.components.Config,
		com.adobe.cq.commerce.common.VendorJcrPlacedOrder,
		org.apache.commons.lang.StringUtils"
        %><%
    final I18n i18n = new I18n(slingRequest);

    ComponentHelper cmp = new ComponentHelper(pageContext);
    Config cfg = cmp.getConfig();

    boolean showPrice = cfg.get("showPrice", true);
    String title = cfg.get("jcr:title", "");

    VendorJcrPlacedOrder order = (VendorJcrPlacedOrder) slingRequest.getAttribute("cq.commerce.vendorplacedorder");
    List<CommerceSession.CartEntry> entries = order != null ? order.getCartEntries() : null;

    if (entries == null || entries.size() == 0) {
        %><div class="cq-order-details-entries"><h3 class="empty"><%= i18n.get("Order contains no items.")%></h3></div><%
        return;
    }
%>
<div>
    <h3><%= xssAPI.encodeForHTML(title) %></h3>
    <table class="cq-order-details-entries">
<%
    for (CommerceSession.CartEntry entry : entries) {
        Product product = entry.getProduct();
        if (product == null) {
            continue;
        }
        String description = "";
        if (product.getDescription() != null) {
            description = product.getDescription();
        }
        String size = "";
        if (product.axisIsVariant("size") && product.getProperty("size", String.class) != null) {
            size = i18n.get("Size: {0}", "product size", product.getProperty("size", String.class));
        }
        String entryId = "cq-commerce-cartentry" + entry.getEntryIndex();
        Boolean wrapping = entry.getProperty("wrapping-selected", Boolean.class);
        if (wrapping == null)
            wrapping = false;

        String label = entry.getProperty("wrapping-label", String.class);
        if (label == null)
            label = "";
%>
        <tr class="entry">
            <td class="thumbnail">
                <a href="<%= xssAPI.getValidHref(product.getPagePath()) %>">
                    <span class="image"><img src="<%= xssAPI.getValidHref(product.getThumbnailUrl(80)) %>" alt="<%= xssAPI.encodeForHTMLAttr(product.getTitle()) %>"/></span>
                </a>
            </td>
            <td class="foundation-field-editable product">
                <span class="foundation-field-readonly coral-Form-fieldwrapper">
                    <span class="coral-Heading--4"><a href="<%= xssAPI.getValidHref(product.getPagePath()) %>"><%= xssAPI.filterHTML(product.getTitle()) %></a></span>
                    <span class="coral-Heading--5"><%= xssAPI.filterHTML(description) %></span>
                    <span class="coral-Heading--5"><%= xssAPI.filterHTML(size) %></span>
                    <br>
                    <% if (wrapping) {
                        if (StringUtils.isNotBlank(label)) {
                        %><span class="coral-Heading--5"><%= i18n.get("Gift wrapping message: ") + xssAPI.filterHTML(label) %></span><%
                        } else {
                        %><span class="coral-Heading--5"><%= i18n.get("Gift wrapping selected") %></span><%
                        }
                    } %>
                </span>
                <span class="foundation-field-edit coral-Form-fieldwrapper">
                    <label class="coral-Form-fieldlabel" for="<%= entryId %>-path" style="display: none">Path for <%= xssAPI.encodeForHTMLAttr(product.getTitle()) %></label>
                    <input class="coral-Form-field coral-Textfield" id="<%= entryId %>-path" type="text" name="cartentry<%=entry.getEntryIndex()%>:path" value="<%= xssAPI.encodeForHTMLAttr(product.getPath()) %>"/>
                    <label class="coral-Form-fieldlabel" for="<%= entryId %>-path" style="display: none">Path for <%= xssAPI.encodeForHTMLAttr(product.getTitle()) %></label>
                    <label class="coral-Form-fieldlabel"><%= i18n.get("Gift wrapping")%>&nbsp;<input class="coral-Form-field coral-Checkbox" style="width: 20px" id="<%= entryId %>-wrapping" type="checkbox" <%=wrapping?"checked":""%> name="cartentry<%= entry.getEntryIndex() %>:wrapping-selected" value="true"/></label>
                    <label class="coral-Form-fieldlabel"><%= i18n.get("Gift wrapping message")%>&nbsp;<input class="coral-Form-field coral-Textfield"  id="<%= entryId %>-label" type="text" name="cartentry<%= entry.getEntryIndex() %>:wrapping-label" value="<%=label%>"/></label>
                </span>
            </td>
            <td class="foundation-field-editable quantity">
                <span class="foundation-field-readonly"><%= entry.getQuantity() %></span>
                <div class="foundation-field-edit coral-Form-fieldwrapper">
                    <label class="coral-Form-fieldlabel" for="<%= entryId %>-quantity" style="display: none">Quantity for <%= xssAPI.encodeForHTMLAttr(product.getTitle()) %></label>
                    <input class="coral-Form-field coral-Textfield"  id="<%= entryId %>-quantity" type="text" name="cartentry<%= entry.getEntryIndex() %>:quantity" value="<%= entry.getQuantity() %>"/>
                </div>
            </td>
<%      if (showPrice) { %>
            <td class="foundation-field-editable price">
                <span class="foundation-field-readonly"><%= entry.getPrice(null) %></span>
                <div class="foundation-field-edit coral-Form-fieldwrapper">
                    <label class="coral-Form-fieldlabel" for="<%= entryId %>-price" style="display: none">Price for <%= xssAPI.encodeForHTMLAttr(product.getTitle()) %></label>
                    <input class="coral-Form-field coral-Textfield"  id="<%= entryId %>-price" type="text" name="cartentry<%= entry.getEntryIndex() %>:price" value="<%= entry.getPrice(null) %>"/>
                </div>
            </td>
<%      }
%>
        </tr>
<%
    }
%>
    </table>
</div>