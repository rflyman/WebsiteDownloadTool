package RobotsTxt;

import WebsiteDownloader.Constants;
import Utils.Logger;
import WebsiteDownloader.DatabaseConnector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class used to check specific URLs against the rules contained in robot data
 * in order to find out whether they are safe to visit or not.
 */
public class RobotParser {

    /**
     * The data containing all the robot rules
     */
    private RobotData data = null;
    /**
     * The link to robot file to be parsed.
     */
    private String urlToParse = null;
    /**
     * The link to base of crawling.
     */
    private String rootURL = "";

    /**
     * Default parameterized constructor, setting the current URL to check
     * against the robot data rules.
     *
     * @param url
     */
    public RobotParser(String url) {
        data = new RobotData();
        urlToParse = url;
        if (this.rootURL.isEmpty()) {
            try {
                this.rootURL = new URL(url).getProtocol() + "://" + new URL(url).getHost();
            } catch (Exception e) {
                e.printStackTrace();
                if(Constants.DEBUG_MODE){
                    System.exit(1);
                }
            }
        }
        this.openConnection();
    }

    /**
     * Constructor that takes two String URL addresses, first one
     * to robot.txt file location, and the base link from which crawling starts.
     *
     * @param robotURL URL string of the robot.txt file.
     * @param rootURL URL string of the base link for crawling.
     */
    public RobotParser(String robotURL, String rootURL) {
        this(robotURL);
        this.rootURL = rootURL;
    }

    /**
     * Determines whether a particular URL is safe to visit or not.
     *
     * @param URL The URL to check for safety
     * @return boolean Returns true or false,depending on whether URL is safe to
     * visit or not
     */
    public boolean isLinkSafe(String URL) {
        String keyWithSlash;
        String linkWithSlash = URL;

        for (String key : data.getRules().keySet()) {
            keyWithSlash = key;

            /**
             * Slashes are added to avoid containment of one string in another
             * when comparing using equals method - Bug fix.
             */
            if (!key.endsWith("/")) {
                keyWithSlash += "/";
            }
            if (!URL.endsWith("/")) {
                linkWithSlash += "/";
            }

            if (data.getRules().get(key).equals(Boolean.FALSE)
                    && (linkWithSlash.contains(keyWithSlash) || linkWithSlash.equals(keyWithSlash))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Opens a connection to the robots.txt file in order to retrieve it and
     * parse it.
     *
     * @return boolean True or false, depending on whther robots.txt was
     * successfully parsed
     */
    public boolean openConnection() {
        try {
            // TODO: Remove all debugging information when done testing!!!
            Logger.log("ENTERING ROBOT CONNECTION...");

            String robotscontent;
            robotscontent = DatabaseConnector.getInstance().getRobots(rootURL);

            if (robotscontent.isEmpty()) {
                System.out.println("checking robots: " + urlToParse);
                URLConnection connection = new URL(urlToParse).openConnection();
                connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
                connection.setReadTimeout(Constants.READ_TIMEOUT);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                //parse(buffer);
                DatabaseConnector.getInstance().saveRobots(buffer, rootURL);
                return openConnection();
            } else {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(new java.io.ByteArrayInputStream(robotscontent.getBytes())));
                parse(buffer);
            }
            System.out.println("ROBOT PARSED...");

        } catch (MalformedURLException murle) {
            Logger.err("MURLE: " + murle.getMessage() + " " + murle.getCause());
            return false;
        } catch (IOException ioe) {
            Logger.err("IOE: " + ioe.getMessage() + " " + ioe.getCause());
            return false;
        } catch (Exception e) {
            Logger.err("E: " + e.getMessage() + Constants.DELIMITER_LOGGER + e.getCause());
            return false;
        }

        return true; // if successfully parsed the file !!!
    }

    /**
     * Parse the robots.txt file, avoiding all rules which are not aimed at our
     * particular robot, based on name.
     *
     * @param buffer The file buffer with the entire robots.txt data.
     * @throws IOException Input/Output Exception thrown if robots.txt is not
     * found.
     * @throws NumberFormatException Exception if delay can not be parsed
     * properly.
     */
    private void parse(BufferedReader buffer) throws Exception {
        String line = null;
        String[] rules = null;
        String currentAgent = null;

        URL baseURL = new URL(rootURL);

        String baseStr = baseURL.getProtocol() + "://" + baseURL.getHost();

        while ((line = buffer.readLine()) != null) {
            if (!(line.startsWith("#")) && !(line.trim().equals(""))) {
                rules = line.split(":");
                if (rules[0].trim().equalsIgnoreCase("user-agent")) {
                    currentAgent = rules[1].trim();

                } else if (rules[0].trim().equalsIgnoreCase("crawl-delay")) {
                    data.setDelay(new Float(Float.parseFloat(rules[1]) * 1000).intValue());

                } else if (rules[0].trim().equalsIgnoreCase("allow") && checkAgent(currentAgent)) {
                    data.setRule(baseStr + rules[1].trim(), true);

                } else if (rules[0].trim().equalsIgnoreCase("disallow") && checkAgent(currentAgent)) {
                    data.setRule(baseStr + rules[1].trim(), false);

                }
            }
        }
    }

    /**
     * Checks whether the crawler is our own, or is "any", if not either, then
     * returns false.
     *
     * @param agent The name of the agent to check
     * @return boolean True or false depending on agent name
     */
    private boolean checkAgent(String agent) {
        if (agent.equals(Constants.AGENT_NAME) || agent.equals(Constants.ALL_AGENTS)) {
            return true;
        }

        return false;
    }

    /**
     * Returns the data with robot rules.
     *
     * @return RobotData Get all the robot data
     */
    public RobotData getRobotData() {
        return data;
    }

}
