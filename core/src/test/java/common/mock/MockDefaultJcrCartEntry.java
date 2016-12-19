package common.mock;

import java.util.Map;

import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.DefaultJcrCartEntry;

public class MockDefaultJcrCartEntry extends DefaultJcrCartEntry {

    public MockDefaultJcrCartEntry(int index, Product product, int quantity) {
        super(index, product, quantity);
    }

    public void updateProperties(Map<String, Object> propertyMap) {
        super.updateProperties(propertyMap);
    }
}
