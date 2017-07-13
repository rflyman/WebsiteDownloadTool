package WebsiteDownloader;

import Utils.Logger;
import WebsiteObjects.Page;
import WebsiteObjects.Website;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.LinkedList;

/**
 * MySQL Database Connection Built as a singleton to share one database
 * connection with all threads
 *
 * @author Robert Flyman
 */
public class DatabaseConnector {

    public static Connection dbconnection;
    private static final DatabaseConnector instance = new DatabaseConnector();

    /* Returns the current instance of the DatabaseConnector */
    public static DatabaseConnector getInstance() {
        return instance;
    }

    /* Constructor for DatabaseConnector, called once to create DB instance */
    private DatabaseConnector() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            // Create a connection to the database using settings from Constants
            String url = "jdbc:mysql://" + Constants.SERVER_NAME + "/" + Constants.DATABASE_NAME; // a JDBC url
            dbconnection = DriverManager.getConnection(url, Constants.DATABASE_USERNAME, Constants.DATABASE_PASSWORD);
        } catch (ClassNotFoundException e) {
            // Could not find the database driver
            Logger.err("Could not find the database driver");
            System.exit(1);
        } catch (SQLException e) {
            // Could not connect to the database
            Logger.err("Could not connect to the database");
            //Also show why and exit
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

    public boolean createUrl(URL url) {
        try {
            //get extension
            String tmp = url.toString();
            //log("Viewing TMP: "+tmp);
            //get domain name
            //get time
            //insert for all extensions if not exists
            //add all to list to be checked

            if (tmp.contains(".com") || tmp.contains(".net") || tmp.contains(".org") || tmp.contains(".ca") || tmp.contains(".info") || tmp.contains(".de") || tmp.contains(".name") || tmp.contains(".us") || tmp.contains(".edu")) {
                //valid domain
                int lastindex = 0;

                if ((lastindex = tmp.indexOf(".com")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".net")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".org")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".info")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".edu")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".ca")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".name")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".us")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                } else if ((lastindex = tmp.indexOf(".de")) != -1) {
                    tmp = tmp.substring(0, lastindex);
                }

                Statement stmt;
                stmt = dbconnection.createStatement();

                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".com', '.com', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".info', '.info', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".de', '.de', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".ca', '.ca', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".net', '.net', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".org', '.org', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".name', '.name', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".us', '.us', '" + getTime() + "', '" + getTime() + "')");
                stmt.executeUpdate("insert ignore into domain (domain_name, domain_extension, domain_last_verified, domain_added_on) values ('" + tmp.replaceAll("'", "") + ".edu', '.edu', '" + getTime() + "', '" + getTime() + "')");
                stmt.close();

            } else {
                //Not a extension we are interested in
                Logger.log("Domain found but not approved for indexing: " + tmp);
                return false;
            }
            Logger.log("Domain found (again): " + tmp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
            return false;
        }
    }

    /**
     * Updates the content of a url and sets the content_last_updated and
     * response code received. Useful if response code was not 200 and no
     * content was received.
     *
     * @param url The URL to update
     * @param tmpsource The new source code for the page
     * @param respCode The response code received
     * @return Boolean true on success, false on fail
     */
    public boolean updateContent(URL url, String tmpsource, int respCode) {
        String host = url.getHost();
        if (!host.contains("http://")) {
            host = "http://" + host;
        }

        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("update website_page set content = ?, content_last_updated = UNIX_TIMESTAMP(), response_code = ? where page_url = ?");
            stmt.setString(1, tmpsource);
            stmt.setString(2, String.valueOf(respCode));
            stmt.setString(3, url.toString());
            stmt.executeUpdate();
            stmt.close();
            //update last touched
            return DatabaseConnector.getInstance().updateDomainLastTouched(host);
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return true;

    }

    /**
     * This function updates the domain last touched to ensure that it isn't
     * grabbed by more than one thread / instance
     *
     * @param host The domain to mark as used
     * @return Boolean true on success, false on failure.
     */
    public boolean updateDomainLastTouched(String host) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("update domain set indexer_last_touched = UNIX_TIMESTAMP() where root_url = ?");
            stmt.setString(1, host);
            stmt.execute();
            stmt.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return false;
    }
    /*
     public static String join(Iterator iterator, String separator) {
     // handle null, zero and one elements before building a buffer 
     Object first = iterator.next();
     if (!iterator.hasNext()) {
     return org.apache.commons.lang3.ObjectUtils.toString(first);
     }
     // two or more elements 
     StringBuffer buf
     = new StringBuffer(256); // Java default is 16, probably too small 
     if (first != null) {
     buf.append(first);
     }
     while (iterator.hasNext()) {
     if (separator != null) {
     buf.append(separator);
     }
     Object obj = iterator.next();
     if (obj != null) {
     buf.append(obj);
     }
     }
     return buf.toString();
     }
     */

    public long getTime() {
        return (new Date().getTime());
    }

    /**
     * Returns the next page of a website to download
     *
     * @return
     */
    public URL getNextContentURL() {
        URL tmp;
        try {
            Statement stmt;
            stmt = dbconnection.createStatement();
            long timestamp = System.currentTimeMillis() / 1000;
            ResultSet rs = stmt.executeQuery("SELECT u.page_url as url from website_page u, domain d where d.index = 'Yes' and (UNIX_TIMESTAMP() - d.indexer_last_touched) > 120 and u.domain_id = d.domain_id and UNIX_TIMESTAMP()-u.last_updated > 864000 order by d.scan_priority desc, u.last_updated asc limit 1");
            if (rs.next()) {
                String domain = rs.getString("url");
                stmt.close();
                tmp = new URL(domain);
                System.out.println("Starting content: " + domain);
                return tmp;
            } else {
                Logger.log("No new website_page found, maybe need to wait.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return null;
    }

    /**
     * Returns the robots.txt content if saved in the database. This prevents
     * grabbing the robots.txt from the server too often. Set to 86400 to grab
     * once per day. Only returns a result if a recent robots.txt exists.
     *
     * @param rootURL The domain to grab the robots.txt content for
     * @return String containing the contents of the robots.txt or empty if
     * failed or no recent robots.txt found.
     */
    public String getRobots(String rootURL) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("SELECT robots_txt_contents from domain where (UNIX_TIMESTAMP() - robots_txt_last_updated) < 86400 and root_url = ? limit 1");
            stmt.setString(1, rootURL);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String robots_content = rs.getString("robots_txt_contents");
                stmt.close();
                return robots_content;
            } else {
                Logger.log("No robots txt found in db."); //Comment out in production
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return "";
    }

    /**
     * Sets the date to now for when a page has had its links parsed and updated
     * in the database
     *
     * @param page_url URL to mark as parsed now
     */
    public void updateLinksLastUpdated(String page_url) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("update website_page set links_last_updated = UNIX_TIMESTAMP() where page_url = ?");
            stmt.setString(1, page_url);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

    /**
     * Gets the next Page from the database that has not yet had it's links
     * parsed. Used by LinkParserThread currently not implemented in this test
     * example
     *
     * @return
     */
    public String[] getNextLinkParserContent() {
        String tmp;
        try {
            Statement stmt;
            stmt = dbconnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT domain_id, content, page_url from website_page where content_last_updated > links_last_updated limit 1");
            if (rs.next()) {
                String[] tmpdata = {rs.getString("domain_id"), rs.getString("content"), rs.getString("url")};
                stmt.close();
                Logger.log("Got content to parse");
                return tmpdata;
            } else {
                Logger.log("No new content to parse");
                //return "sleep";
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return null;
    }

    public void saveRobots(java.io.BufferedReader buffer, String root) {
        try {
            PreparedStatement stmt;
            String buffertostring = "";
            String line = "";

            while ((line = buffer.readLine()) != null) {
                buffertostring += line + "\n";
            }

            stmt = dbconnection.prepareStatement("update domain set robots_txt_last_updated = UNIX_TIMESTAMP(), robots_txt_contents = ? where root_url = ?");
            stmt.setString(1, buffertostring);
            stmt.setString(2, root);
            stmt.execute();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

    public String getUrlId(String url) {
        try {
            PreparedStatement stmt;
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            stmt = dbconnection.prepareStatement("SELECT url_id from website_page where page_url = ? limit 1");
            stmt.setString(1, url);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String url_id = rs.getString("url_id");
                stmt.close();
                return url_id;
            } else {
                Logger.log("No url_id found.");

            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return "";
    }

    /**
     * Updates an existing UrlLink in the database and updates the timestamp it
     * was last detected as well as anchor_text, rel, and page title This method
     * has no return and upon fail there is no notification. Malformed URL and
     * valid data checks still need to be implemented.
     *
     * @param to_id The destination URL id
     * @param from_id The source URL id (current page URL being indexed)
     * @param title The page title
     * @param rel The rel attribute of the link if applicable
     * @param anchor_text The anchor text of the link
     */
    public boolean updateUrlLinkLastFound(String to_id, String from_id, String title, String rel, String anchor_text) {
        try {
            PreparedStatement stmt;

            stmt = dbconnection.prepareStatement("update url_link set last_found = UNIX_TIMESTAMP(), rel = ?, anchor_text = ?, title = ? where from_id = ? and to_id = ?");
            stmt.setString(1, rel);
            stmt.setString(2, anchor_text);
            stmt.setString(3, title);
            stmt.setString(4, from_id);
            stmt.setString(5, to_id);
            int affected = stmt.executeUpdate();
            stmt.close();
            if (affected > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return false;
    }

    /**
     * Adds a new UrlLink to the database, first checking to see if the domain
     * exists and if not adds the domain as well This method has no return and
     * upon fail there is no notification. Malformed URL and valid data checks
     * still need to be implemented.
     *
     * @param to The URL destination of the link
     * @param from The URL source of the link (current page URL being indexed)
     * @param title The page title
     * @param rel The rel attribute of the link if applicable
     * @param anchor_text The anchor text of the link
     * @param is_external Is the link pointing to an external page? ENUM Yes or
     * No
     */
    public void addUrlLink(String to, String from, String title, String rel, String anchor_text, String is_external) {
        try {
            PreparedStatement stmt;
            String to_id = getUrlId(to);
            String from_id = getUrlId(from);
            if (!to_id.isEmpty() && !from_id.isEmpty()) {
                if (!updateUrlLinkLastFound(to_id, from_id, title, rel, anchor_text)) {
                    stmt = dbconnection.prepareStatement("insert into url_link (from_id, to_id, anchor_text, last_found, rel, title, is_external) values (?,?,?,UNIX_TIMESTAMP(),?,?,?)");
                    stmt.setString(1, from_id);
                    stmt.setString(2, to_id);
                    stmt.setString(3, anchor_text);
                    stmt.setString(4, rel);
                    stmt.setString(5, title);
                    stmt.setString(6, is_external);
                    stmt.execute();
                    stmt.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

    /**
     * Adds a new Website to the database, first checking to see if the domain
     * exists and if not adds the domain as well
     *
     * @param newurl The new URL to be added
     */
    public void storeWebsite(URL newurl) {
       this.storeWebsite(new Website(newurl));
    }

    /**
     * Return a URL for the next domain to be processed and marks as currently
     * being used. Only returns results not accessed in the past 2 minutes.
     *
     * @return
     */
    public URL getNextDomain() {
        try {
            Statement stmt;
            stmt = dbconnection.createStatement();
            long timestamp = System.currentTimeMillis() / 1000;
            ResultSet rs = stmt.executeQuery("SELECT d.root_url as url from domain d where d.index = 'Yes' and (UNIX_TIMESTAMP() - d.indexer_last_touched) > 120 order by d.scan_priority desc limit 1");
            if (rs.next()) {
                String domain = rs.getString("url");
                stmt.close();
                this.updateDomainLastTouched(domain);
                return new URL(domain);
            } else {
                Logger.log("No new url found, maybe need to wait.");
            }
        } catch (SQLException e) {
            Logger.err("MysqlUpdater has SQL error in getNextDomain");
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        } catch (MalformedURLException e) {
            Logger.err("Database contains a malformed URL string.");
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        Logger.err("No website available that needs to be indexed. Most likely all have been recently attached to threads. Waiting...");
        return null;
    }

    /**
     * Returns all pages to be indexed for a given website
     *
     * @param current_website The website of interest
     * @return Linked list containing all Page objects to be set in website
     * to_be_downloaded
     */
    public LinkedList<Page> getAllUnindexedPagesForWebsite(Website current_website) {
        LinkedList<Page> pages;
        pages = new LinkedList<Page>();

        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("SELECT u.page_url as url from website_page u, domain d where d.root_url = ? and d.index = 'Yes' and u.domain_id = d.domain_id and UNIX_TIMESTAMP()-u.last_updated > 864000");
            stmt.setString(1, current_website.getWebsiteURL());
            ResultSet rs_pages = stmt.executeQuery();
            while (rs_pages.next()) {
                String page_url = rs_pages.getString("url");
                pages.add(new Page(page_url, current_website));
            }
            Logger.log("Found "+pages.size()+" pages to index");
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return pages;
    }

    public void storePage(Website website, Page page) {
        /* Only store pages that have been requested. */
        if(page.getStatusCodeStr() != null && !page.getStatusCodeStr().isEmpty()){
            try {
                PreparedStatement stmt;
                stmt = dbconnection.prepareStatement("INSERT INTO `website_page` (`page_url`, `domain_id`, `links_last_parsed`, `index`, `source`, `last_updated`, `response_message`, `status_code`) VALUES (?,?,?,?,?,?,?,?);");
                stmt.setString(1, page.getFullUrlAsString()); //page_url
                stmt.setString(2, website.getWebsiteID()); //domain_id
                stmt.setString(3, page.getLinksLastUpdated()); //links_last_update
                //stmt.setString(4, page.getPageTitle()); //page_title
                //stmt.setInt(5, 99); //shortest_link_depth_from_homepage (99 is unknown - not implemented yet)
                stmt.setString(4, "Yes"); //index ("Yes" or "No") can later implement an algorithm to limit indexing of unwanted pages
                stmt.setString(5, page.getSource()); //source
                stmt.setLong(6, page.getLastUpdated()); //last_updated
                stmt.setString(7, page.getResponseMessage()); //response_message
                stmt.setString(8, page.getStatusCodeStr()); //status_code
                stmt.executeUpdate();
                stmt.close();
                Logger.log("New page just stored.");
            } catch (Exception e) {
                e.printStackTrace();
                if (Constants.DEBUG_MODE) {
                    System.exit(1);
                }
            }
        }
    }

    public boolean isWebsiteStored(Website website) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("SELECT domain_id from domain where root_url = ? limit 1");
            stmt.setString(1, website.getWebsiteURL());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
        return false;
    }

    /**
     * Stores website in database. Ignores insert on duplicate key
     *
     * @param website
     */
    public void storeWebsite(Website website) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("INSERT IGNORE INTO `domain` (`root_url`, `indexer_last_touched`) VALUES (?, UNIX_TIMESTAMP())");
            stmt.setString(1, website.getWebsiteURL()); //root_url
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

    /**
     * Currently only sets the website_id
     *
     * @param website The website to initialize
     */
    public void initializeWebsiteForURL(Website website) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("SELECT domain_id from domain where root_url = ? limit 1");
            stmt.setString(1, website.getWebsiteURL());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                website.setWebsiteID(rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

    /**
     * Used for debugging purposes only.
     *
     * @param website_url The website of which the indexer_last_touched is to be updated
     * @param timestamp timestamp to set it to
     */
    void overrideLastTouched(String website_url, int timestamp) {
        try {
            PreparedStatement stmt;
            stmt = dbconnection.prepareStatement("update domain set indexer_last_touched = ?");
            stmt.setInt(1, timestamp);
            int affected = stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }

}
