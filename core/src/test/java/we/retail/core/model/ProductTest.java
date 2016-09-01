package we.retail.core.model;

import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.day.cq.wcm.api.designer.Style;
import common.AppAemContext;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductTest {

    private static final String CURRENT_RESOURCE = "/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe/jcr:content/root/product";

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    @Mock
    private Style style;

    private Product underTest;

    @Before
    public void setUp() throws Exception {
        context.currentResource(CURRENT_RESOURCE);
        SlingBindings attribute = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        attribute.put("currentStyle", style);
        underTest = context.request().adaptTo(Product.class);
    }

    @Test
    public void testProduct() throws Exception {
        assertTrue("Page should contain product path", underTest.exists());
        com.adobe.cq.commerce.api.Product baseProduct = underTest.getBaseProduct();
        assertNotNull(baseProduct);
        Product.ProductProperties productProperties = underTest.getProperties();
        assertNotNull(productProperties);
        assertEquals("9", productProperties.getSize());
        assertNotNull(productProperties.getSku());
    }

    @Test
    public void testVariants() throws Exception {
        assertEquals(3, underTest.getVariants().size());
        Product.ProductProperties properties = underTest.getVariants().get(0);
        assertEquals("/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe/jcr:content/root/product/eqrusufle-9", properties.getPath());
        assertNotNull(properties.getPagePath());
        assertEquals("eqrusufle-9", properties.getSku());
        assertNull(properties.getTitle());
        assertNull(properties.getDescription());
        assertNull(properties.getColor());
        assertNull(properties.getColorClass());
        assertEquals("9", properties.getSize());
        assertNull(properties.getPrice());
        assertNull(properties.getSummary());
        assertNull(properties.getFeatures());
        assertNull(properties.getImage());
    }

}