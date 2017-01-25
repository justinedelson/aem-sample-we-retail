package we.retail.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

import common.AppAemContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import we.retail.core.model.Constants;

public class WeRetailHelperTest {

    private static final String MOCK_RESOURCE_TITLE = "mockResourceTitle";

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    private Page page;

    @Mock
    private Resource resource;

    @Before
    public void setUp() throws Exception {
        page = context.currentPage(Constants.TEST_ORDER_PAGE);
        resource = context.currentResource(Constants.TEST_ORDER_RESOURCE);
    }

    @Test
    public void testMethods() throws Exception {
        Resource mockResource = mock(Resource.class);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(NameConstants.PN_TITLE, MOCK_RESOURCE_TITLE);
        ValueMap vm = new ValueMapDecorator(map);
        when(mockResource.adaptTo(ValueMap.class)).thenReturn(vm);
        assertEquals(MOCK_RESOURCE_TITLE, WeRetailHelper.getTitle(mockResource, page));
        assertEquals("Order Details", WeRetailHelper.getPageTitle(page));
        assertEquals("Order Details", WeRetailHelper.getTitle(page));
    }
}
