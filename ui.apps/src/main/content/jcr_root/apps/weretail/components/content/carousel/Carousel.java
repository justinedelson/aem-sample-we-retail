package apps.weretail.components.content.carousel;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.jcr.JcrConstants;

public class Carousel extends WCMUsePojo {

    private static final String TYPE_DEFAULT = "default";
    private static final String PN_TYPE = "displayAs";
    private String id;
    private String type;
    private Resource resource;

    @Override
    public void activate() throws Exception {
        this.resource = getResource();
        this.id = getGeneratedId();
        ValueMap properties = getProperties();
        this.type = properties.get(PN_TYPE, TYPE_DEFAULT);
    }

    private String getGeneratedId() {
        String path = resource.getPath();
        String inJcrContent = JcrConstants.JCR_CONTENT + "/";
        int root = path.indexOf(inJcrContent);
        if (root >= 0) {
            path = path.substring(root + inJcrContent.length());
        }
        return path.replace("/", "_");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
