package common.mock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.CommerceSession.CartEntry;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.AbstractJcrCommerceSession;
import com.adobe.cq.commerce.common.DefaultJcrPlacedOrder;

public class MockDefaultJcrPlacedOrder extends DefaultJcrPlacedOrder {

    public MockDefaultJcrPlacedOrder(AbstractJcrCommerceSession abstractJcrCommerceSession, String orderId) {
        super(abstractJcrCommerceSession, orderId);
    }

    public MockDefaultJcrPlacedOrder(AbstractJcrCommerceSession abstractJcrCommerceSession, String orderId, Resource order) {
        super(abstractJcrCommerceSession, orderId);
        this.order = order;
    }

    @Override
    protected Resource getPlacedOrder(String orderId) {
        return order;
    }

    @Override
    public Map<String, Object> getOrder() throws CommerceException {
        if (details == null) {
            lazyLoadOrderDetails();
        }
        return details;
    }

    private void lazyLoadOrderDetails() throws CommerceException {
        details = new HashMap<String, Object>();
        if (order != null) {
            final SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM, yyyy");

            details.put("orderPath", order.getPath());

            ValueMap orderProperties = order.getValueMap();
            for (Map.Entry<String, Object> entry : orderProperties.entrySet()) {
                String key = entry.getKey();
                if ("cartItems".equals(key)) {
                    // returned by getPlacedOrderEntries()
                } else {
                    Object property = entry.getValue();
                    if (property instanceof Calendar) {
                        // explode date into 'property' and 'propertyFormatted'
                        details.put(key, property);
                        details.put(key + "Formatted", dateFmt.format(((Calendar) property).getTime()));
                    } else {
                        details.put(key, property);
                    }
                }
            }

            Resource orderDetailsChild = order.getChild("order-details");
            if (orderDetailsChild != null) {
                ValueMap orderDetailProperties = orderDetailsChild.getValueMap();
                for (ValueMap.Entry<String, Object> detailProperty : orderDetailProperties.entrySet()) {
                    String key = detailProperty.getKey();
                    Object property = detailProperty.getValue();
                    if (property instanceof Calendar) {
                        // explode date into 'property' and 'propertyFormatted'
                        details.put(key, property);
                        details.put(key + "Formatted", dateFmt.format(((Calendar) property).getTime()));
                    } else {
                        details.put(key, property);
                    }
                }
            }
        }
    }

    @Override
    protected void lazyLoadCartEntries() throws CommerceException {
        entries = new ArrayList<CommerceSession.CartEntry>();

        if (order != null) {
            String[] serializedEntries = order.getValueMap().get("cartItems", String[].class);
            for (String serializedEntry : serializedEntries) {
                try {
                    CommerceSession.CartEntry entry = deserializeCartEntry(serializedEntry, entries.size());
                    entries.add(entry);
                } catch (Exception e) { // NOSONAR (catch any errors thrown attempting to parse/decode entry)
                    log.error("Unable to load product from order: {}", serializedEntry);
                }
            }
        }
    }

    protected CartEntry deserializeCartEntry(String str, int index) throws CommerceException {
        Object[] entryData = deserializeCartEntryData(str);
        Product product = (Product) entryData[0];
        int quantity = (Integer) entryData[1];
        MockDefaultJcrCartEntry entry = new MockDefaultJcrCartEntry(index, product, quantity);
        if (entryData[2] == null) {
            return entry;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) entryData[2];
        entry.updateProperties(properties);
        return entry;
    }

    public Object[] deserializeCartEntryData(String str) throws CommerceException {
        Object[] entryData = new Object[3];
        String[] entryFields = str.split(";", 3);
        Product product = new MockProduct(order.getResourceResolver().resolve(entryFields[0]));
        entryData[0] = product;
        int quantity = Integer.parseInt(entryFields[1]);
        entryData[1] = quantity;
        if (entryFields.length == 2) {
            return entryData;
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        String[] propertyFields = entryFields[2].split("\f");
        for (String field : propertyFields) {
            if (StringUtils.isNotBlank(field)) {
                String[] property = field.split("=", 2);
                properties.put(property[0], property[1]);
            }
        }

        entryData[2] = properties;
        return entryData;
    }
}
