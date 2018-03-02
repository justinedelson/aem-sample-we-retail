/*
 *   Copyright 2018 Adobe Systems Incorporated
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

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import common.AppAemContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class CountriesFormOptionsDataSourceTest {

    @Rule
    public final AemContext context = AppAemContext.newAemContext();

    private CountriesFormOptionsDataSource dataSource = new CountriesFormOptionsDataSource();

    @Before
    public void setup() {
        context.registerService(ResourceBundleProvider.class, new ResourceBundleProvider() {
            @Override
            public Locale getDefaultLocale() {
                return Locale.UK;
            }

            @Override
            public ResourceBundle getResourceBundle(Locale locale) {
                return getResourceBundle(null, locale);
            }

            @Override
            public ResourceBundle getResourceBundle(String baseName, Locale locale) {
                return REFLECTIVE_RESOURCE_BUNDLE;
            }
        });
    }

    @Test
    public void test() throws Exception {
        PageManager pageManager = context.pageManager();
        Page page = pageManager.getPage(AppAemContext.CONTENT_ROOT);

        MockSlingHttpServletRequest request = context.request();
        SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
        bindings.put(WCMBindings.CURRENT_PAGE, page);
        MockSlingHttpServletResponse response = context.response();

        dataSource.doGet(request, response);

        DataSource dataSource = (DataSource) request.getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        assertTrue(dataSource.iterator().hasNext());

    }

    private static ResourceBundle REFLECTIVE_RESOURCE_BUNDLE = new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
            return key;
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }
    };
}
