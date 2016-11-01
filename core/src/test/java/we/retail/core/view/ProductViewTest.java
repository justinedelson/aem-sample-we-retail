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
package we.retail.core.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.sightly.WCMBindings;
import com.day.cq.wcm.api.designer.Style;

import common.AppAemContext;
import io.wcm.testing.mock.aem.junit.AemContext;

@RunWith(MockitoJUnitRunner.class)
public class ProductViewTest {

    private static final String CURRENT_RESOURCE = "/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe/jcr:content/root/product";
    private static final String CURRENT_PAGE = "/content/we-retail/us/en/products/equipment/running/fleet-cross-training-shoe";

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    @Mock
    private Style style;

    private ProductView productView;

    @Before
    public void setUp() throws Exception {
        context.currentPage(CURRENT_PAGE);
        SlingBindings attribute = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        attribute.put("currentStyle", style);
        attribute.put(WCMBindings.CURRENT_PAGE, context.currentPage());
        context.currentResource(CURRENT_RESOURCE);
        productView = ProductViewPopulator.populate(context.request().getResource(), null);
    }

    @Test
    public void testProduct() throws Exception {
        assertNotNull(productView);
        assertNull(productView.getVariantValueForAxis("size"));
        assertEquals("eqrusufle", productView.getSku());
    }

    @Test
    public void testVariants() throws Exception {
        assertEquals(3, productView.getVariants().size());
        ProductView variantView = productView.getVariants().get(0);
        assertEquals(CURRENT_RESOURCE + "/eqrusufle-9", variantView.getPath());
        assertNotNull(variantView.getPagePath());
        assertEquals("eqrusufle-9", variantView.getSku());
        assertNull(variantView.getTitle());
        assertNull(variantView.getDescription());
        assertNull(variantView.getVariantValueForAxis("color"));
        assertEquals("9", variantView.getVariantValueForAxis("size"));
        assertNull(variantView.getPrice());
        assertNull(variantView.getSummary());
        assertNull(variantView.getFeatures());
        assertNull(variantView.getImage());
    }

}
