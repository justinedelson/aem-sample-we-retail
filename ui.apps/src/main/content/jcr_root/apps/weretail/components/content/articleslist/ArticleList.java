package apps.weretail.components.content.articleslist;

import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;

public class ArticleList extends WCMUsePojo {

    private static final String PN_TYPE = "displayAs";
    private static final String TYPE_DEFAULT = "default";

    private String type;
    
    @Override
    public void activate() throws Exception {
        ValueMap properties = getProperties();
        type = properties.get(PN_TYPE, TYPE_DEFAULT);
    }

    public String getType() {
        return type;
    }
}
