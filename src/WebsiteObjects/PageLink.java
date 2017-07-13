package WebsiteObjects;

/**
 *
 * @author Robert Flyman
 */
public class PageLink {
    private String anchor_text;
    private String rel;
    private String title;
    private String url;
    private String external;
    
    public void setAnchorText(String anchor_text) {
        this.anchor_text = anchor_text;
    }

    public void setRel(String rel) {
        this.anchor_text = rel;
    }

    public void setTitle(String title) {
       this.anchor_text = title;
    }

    public void setURL(String url) {
        this.anchor_text = url;
    }

    public void setExternal(String external) {
        this.anchor_text = external;
    }
    
}
