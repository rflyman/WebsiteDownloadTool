package WebsiteObjects;

import Utils.Logger;
import WebsiteDownloader.DatabaseConnector;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Website object that contains Page objects to be downloaded, those to be
 * saved, and those recently downloaded. Note recently downloaded items are not
 * populated from database as they are not accessible or used other than to log
 * the progress.
 *
 * @author Robert Flyman
 */
public class Website {

    private final String domain; //domain that is being worked on (No trailing slash) See setDomain function
    private LinkedList<Page> to_be_downloaded; //CustomLinks to be processed
    private LinkedList<Page> recently_downloaded; //contains all CustomLinks that have recently been downloaded and whose links are parsed
    private LinkedList<Page> just_downloaded_not_stored_yet; //contains all pages downloaded this session and not yet stored. Move page objects to recently_downloaded once stored.
    private final int page_wait_time_seconds = 8; //minimum wait time between access attemps (default: 8 seconds)
    private long last_page_download_timestamp = 0;
    private String website_id;

    /**
     * if no initialization information is passed to constructor Next
     * domain/website that needs to be processed will be loaded. Website data
     * will be populated fully from database.
     */
    public Website() throws Throwable {
        to_be_downloaded = new LinkedList<Page>();
        recently_downloaded = new LinkedList<Page>();
        just_downloaded_not_stored_yet = new LinkedList<Page>();
        
        //determine website (This process could be optimized better in the future to load the website that is next and workload in one query
        this.domain = setDomain(determineNextDomainToProcess());
        if(this.domain.isEmpty()){
            throw new Throwable("No domain found to index.");
        }
        loadWebsiteFromDatabase();
        this.initializeWorkloadForProvidedDomain();
    }

    /**
     * if domain is provided the website data is loaded for provided domain
     *
     * @param domain
     */
    public Website(URL domain) {
        to_be_downloaded = new LinkedList<Page>();
        recently_downloaded = new LinkedList<Page>();
        just_downloaded_not_stored_yet = new LinkedList<Page>();
        this.domain = this.setDomain(domain);
        /* Since we are passed a domain it might not yet exist in the datbase. Lets check and if it isn't stored store the Website */
        if (!isWebsiteStored()) {
            storeWebsite();
        }
        loadWebsiteFromDatabase();
    }

    /**
     * Add a Page to be downloaded. Adds to the end of the linked list only if
     * it doesn't exist in any current linked list
     *
     * @param p Page to be added
     */
    public void addPageToBeDownloaded(Page p) {
        Logger.log("Adding a page to be downloaded");
        if (!this.to_be_downloaded.contains(p) && !this.recently_downloaded.contains(p) && !this.just_downloaded_not_stored_yet.contains(p)) {
            this.to_be_downloaded.addLast(p);
            Logger.log("Page successfully added total pages remaining: "+to_be_downloaded.size());
        }
    }

    /**
     * Load all pages to be processed. Call this function for the domain being
     * worked on.
     */
    private void initializeWorkloadForProvidedDomain() {
        if (domain.isEmpty()) {
            throw new UnsupportedOperationException("No domain initialized.");
        }

        /* Add all pages that need to be processed to to_be_downloaded
         */
        this.to_be_downloaded = DatabaseConnector.getInstance().getAllUnindexedPagesForWebsite(this);
        
        try {
            /* Also need to add a page for the root of the website in case it hasn't yet been indexed. Applicable to new websites added to the database as they don't have a page entry yet. */
            Page tmp = new Page(new URL(this.getWebsiteURL()), this);
            this.to_be_downloaded.add(tmp);
        } catch (MalformedURLException ex) {
            Logger.err("Website has a malformed URL in Website.java line 95");
        }
        
    }

    /**
     * Determines which domain is next, marks domain as used to block other
     * instances from grabbing the same domain
     *
     * @return URL of next domain to process
     */
    private URL determineNextDomainToProcess() {
        return DatabaseConnector.getInstance().getNextDomain(); //internally calls updateDomainLastTouched method
    }

    /**
     * Moves the Page object to the linked list of just downloaded items. This
     * list is specific to those that need to be stored during the next bulk
     * insert
     *
     * @param p The page to be moved
     */
    private void movePageToJustDownloaded(Page p) {
        this.just_downloaded_not_stored_yet.add(p);
        this.to_be_downloaded.remove(p);
    }

    /**
     * Moves the Page object to the linked list of recently downloaded items.
     * This list is specific to those that have already been stored and
     * processed.
     *
     * @param p The page to be moved
     */
    private void movePageToRecentlyDownloadedFromJustDownloaded(Page p) {
        this.recently_downloaded.add(p);
        this.just_downloaded_not_stored_yet.remove(p);
    }

    /**
     * Processes next page and returns true on success, false if no more pages
     * to process. Uses Source Grabber to download page Uses RobotParser to
     * verify that page is valid for download
     *
     * @return
     */
    public boolean processNextPage() {
        
        //Get next page. If no more to be downloaded return false
        if (to_be_downloaded.isEmpty()) {
            return false;
        } else {
            //not done so lets get one
            Page nextpage = this.to_be_downloaded.getFirst();
            nextpage.download();
            //Update timestamp (unix timestamp) number of seconds since epoch
            this.last_page_download_timestamp = (long) (new Date()).getTime()/1000;
            //Page is downloaded lets move to recently downloaded linked list
            movePageToJustDownloaded(nextpage);
            //Now lets add new pages from the page just downloaded
            nextpage.parseLinks();
        }
        return true;
    }

    /**
     * Stores all unsaved data to the database and adds all pages to
     * recently_downloaded Clears just_downloaded_not_stored
     */
    public void storeAllNewData() {
        Logger.log("Storing all data");
        ListIterator list_it = this.just_downloaded_not_stored_yet.listIterator();
        while (list_it.hasNext()) {
            Page tmp = (Page) list_it.next();
            DatabaseConnector.getInstance().storePage(this, tmp);
            this.recently_downloaded.add(tmp);
        }
        this.just_downloaded_not_stored_yet.clear();
    }

    /**
     * Return the current website (returns in external form)
     *
     * @return String of domain name
     */
    public String getWebsiteURL() {
        return this.domain;
    }

    /**
     * Used to set the domain ensuring that there is common formatting during constructor call.
     * @param domain The URL for the domain
     */
    private String setDomain(URL domain) {
        if(domain == null){ 
            return ""; 
        }
        String hoststr = domain.getProtocol() + "://" + domain.getHost();
        if (hoststr.endsWith("/")) {
            hoststr = hoststr.substring(0, hoststr.length() - 1);
        }
        return hoststr;
    }

    /**
     * Returns the website_id
     * @return String website_id
     */
    public String getWebsiteID() {
        return this.website_id;
    }

    /**
     * Loads the website from the database
     */
    private void loadWebsiteFromDatabase() {
        DatabaseConnector.getInstance().initializeWebsiteForURL(this);
    }

    /**
     * Checks to see if the website already exists in the database
     *
     * @return Boolean true if exists and false if it does not exist. Also sets
     * this.website_id
     */
    private boolean isWebsiteStored() {
        return DatabaseConnector.getInstance().isWebsiteStored(this);
    }

    /**
     * Stores the website to the database
     */
    private void storeWebsite() {
        DatabaseConnector.getInstance().storeWebsite(this);
    }

    /**
     * Set the website_id to passed value
     * @param website_id The website_id value
     */
    public void setWebsiteID(String website_id) {
        this.website_id = website_id;
    }

    /**
     * Returns the minimum wait time between processing each page to not overwhelm the servers.
     * @return time to wait in milliseconds
     */
    public long getMinimumTimeBetweenProcessingPages() {
        return this.page_wait_time_seconds*1000; // needs to return in millis
    }

}
