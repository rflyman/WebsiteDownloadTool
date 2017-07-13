package WebsiteObjects;

import Utils.Logger;
import WebsiteDownloader.PageLinkParser;
import WebsiteDownloader.SourceGrabber;
import WebsiteDownloader.URLWrapper;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;

/**
 * Page object
 *
 * @author Robert Flyman
 */
public class Page {

    private URL full_page_url;
    private WebsiteObjects.Website website;
    private String source;
    private Date downloaded_on;
    private URLWrapper[] outgoing_links;
    private URLWrapper[] local_links;
    private int status_code; //200, 403, 500 etc.
    private String response_message;
    private LinkedList<PageLink> page_links;
    private String links_last_updated;
    private String page_title;
    private long source_last_updated;

    /**
     * Constructs page data for all fields. Used for new pages
     *
     * @param website website of page
     * @param status_code status code of retrieval I.E. 500, 404, 300, 200...
     * @param contents Source code of page (Cleaned up)
     * @param downloaded_on Date Downloaded
     * @param outgoing_links Outgoing Links Parsed
     * @param local_links Local Links Parsed
     */
    public Page(Website website, int status_code, String source, Date downloaded_on, URLWrapper[] outgoing_links, URLWrapper[] local_links) {
        page_links = new LinkedList<PageLink>();
        this.website = website;
        this.status_code = status_code;
        this.source = source;
        this.downloaded_on = downloaded_on;
        this.outgoing_links = outgoing_links;
        this.local_links = local_links;
    }

    /**
     * Constructs Page by loading most recent Page data from database for
     * provided page URL
     *
     * @param page_url URL
     * @param website
     */
    public Page(URL page_url, Website website) {
        page_links = new LinkedList<PageLink>();
        this.website = website;
        this.full_page_url = page_url;
    }

    public Page(String page_url, Website website) throws MalformedURLException {
        page_links = new LinkedList<PageLink>();
        this.website = website;
        this.full_page_url = new URL(page_url);
    }

    /**
     * Returns a String representation of the full URL
     *
     * @return Full URL as a String
     */
    public String getFullUrlAsString() {
        return this.full_page_url.toExternalForm();
    }

    public void download() {
        SourceGrabber sg = new SourceGrabber();
        sg.updatePageSource(this);
        this.source_last_updated = (new Date()).getTime() / 1000;
    }

    public void setStatusCode(int status_code) {
        this.status_code = status_code;
    }

    public void setResponseMessage(String response_message) {
        this.response_message = response_message;
    }

    public int getStatusCode() {
        return this.status_code;
    }

    public String getResponseMessage() {
        return this.response_message;
    }

    public String getStatusCodeStr() {
        return String.valueOf(this.status_code);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public URL getURL() {
        return this.full_page_url;
    }

    public void parseLinks() {
        PageLinkParser link_parser = new PageLinkParser(this.website, this);
        link_parser.parsePageSourceForLinks();
    }

    public String getSource() {
        return this.source;
    }

    public WebsiteObjects.Website getWebsite() {
        return this.website;
    }

    public void addPageLink(PageLink new_page_link) {
        page_links.add(new_page_link);
    }

    public long getLastUpdated() {
        return this.source_last_updated;
    }

    public String getPageTitle() {
        return this.page_title;
    }

    public String getLinksLastUpdated() {
        return this.links_last_updated;
    }

    public void setLinksLastUpdatedNow() {
        this.links_last_updated = String.valueOf((new Date()).getTime() / 1000); //sets to time since unix epoch in seconds

    }

    /**
     * Override for the equals  method to use LinkedList.contains() for Page object
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Page page = (Page) obj;
        return (this.full_page_url != null && getFullUrlAsString().equals(page.getFullUrlAsString()));
    }
    /**
     * Override for the hasCode method to use LinkedList.contains() for Page object
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.full_page_url != null ? this.full_page_url.hashCode() : 0);
        return hash;
    }

}
