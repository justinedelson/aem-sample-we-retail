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

    private static final String CURRENT_PAGE = "/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe";

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    @Mock
    private Style style;

    private Product underTest;

    @Before
    public void setUp() throws Exception {
        context.currentPage(CURRENT_PAGE);
        SlingBindings attribute = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        attribute.put("currentStyle", style);
        underTest = context.request().adaptTo(Product.class);
    }

    @Test
    public void testProduct() throws Exception {
        assertTrue("Page should contain product path", underTest.exists());
        com.adobe.cq.commerce.api.Product baseProduct = underTest.getBaseProduct();
        assertNotNull(baseProduct);
        assertNotNull(baseProduct.getSKU());
        assertNull(baseProduct.getPagePath());
        Product.ProductProperties productProperties = underTest.getProperties();
        assertNotNull(productProperties);
        assertEquals("65", productProperties.getPrice());
        assertEquals("Fleet Cross-Training Shoe", productProperties.getTitle());
        assertNotNull(productProperties.getSummary());
    }

    @Test
    public void testVariants() throws Exception {
        assertEquals(3, underTest.getVariants().size());
        Product.ProductProperties properties = underTest.getVariants().get(0);
        assertEquals("/etc/commerce/products/we-retail/eq/running/eqrusufle/size-9", properties.getPath());
        assertNull(properties.getPagePath());
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