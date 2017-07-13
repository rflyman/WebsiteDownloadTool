package WebsiteDownloader;

import Utils.Logger;
import WebsiteObjects.Page;
import WebsiteObjects.PageLink;
import WebsiteObjects.Website;
import java.net.URL;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

public class PageLinkParser {

    private final Website website;
    private final Page page;

    /**
     * Constructs PageLinkParser allowing for links to be parsed from the source
     * of the Page.
     *
     * @param website The website being indexed
     * @param page The page to parse for links
     */
    public PageLinkParser(Website website, Page page) {
        this.website = website;
        this.page = page;
    }

    public void parsePageSourceForLinks() {
        Logger.log("PageLinkParser Parsing: " + this.page.getFullUrlAsString());

        try {
            this.page.setLinksLastUpdatedNow();

            org.htmlparser.Parser parse = new org.htmlparser.Parser();
            parse.setInputHTML(page.getSource());

            NodeFilter filter = new org.htmlparser.filters.TagNameFilter("A");
            NodeList nodes = parse.parse(filter);
            NodeIterator iter = nodes.elements();
            Tag tag;
            while (iter.hasMoreNodes()) {
                PageLink new_page_link = new PageLink();

                org.htmlparser.Node tmpnode = iter.nextNode();
                if (tmpnode instanceof LinkTag) {
                    new_page_link.setAnchorText(((LinkTag) tmpnode).getStringText());
                }
                tag = (Tag) tmpnode;

                //Get attributes from link
                new_page_link.setRel(tag.getAttribute("rel"));
                new_page_link.setTitle(tag.getAttribute("title"));
                String url = tag.getAttribute("href");
                new_page_link.setURL(url);
                if (url != null && !url.isEmpty()) {
                    Logger.log("New link found: " + url);

                    if (url.startsWith("http") || url.startsWith("https")) {
                        URL destination = new URL(url);
                        //Adds domain / url to database
                        DatabaseConnector.getInstance().storeWebsite(new URL(destination.toExternalForm()));
                        if (destination.getHost().matches(this.page.getURL().getHost())) {
                            new_page_link.setExternal("No");
                            //Add page to website to be downloaded (Only adds if it isn't already added)
                            this.website.addPageToBeDownloaded(new Page(new URL(destination.toExternalForm()), this.website));
                        } else {
                            new_page_link.setExternal("Yes");
                        }
                        this.page.addPageLink(new_page_link);

                    } else {
                        //Possible local link
                        try {
                            URL destination = new URL(this.page.getURL().getHost() + url);
                            Logger.log(this.page.getURL().getHost() + url);
                            System.exit(1); //need to double check this

                            if (destination.getHost().matches(this.page.getURL().getHost())) {
                                new_page_link.setExternal("No");
                                //Add page to website to be downloaded (Only adds if it isn't already added)
                                this.website.addPageToBeDownloaded(new Page(new URL(destination.toExternalForm()), this.website));
                            } else {
                                new_page_link.setExternal("Yes");
                            }
                            
                            this.page.addPageLink(new_page_link);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (Constants.DEBUG_MODE) {
                                System.exit(1);
                            }
                        }
                    }
                } 
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

}
