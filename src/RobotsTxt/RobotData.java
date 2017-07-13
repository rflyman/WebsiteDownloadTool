package RobotsTxt;

import WebsiteDownloader.Constants;
import java.util.HashMap;

/**
 * The main class for holding all robot rules retrieved from robots.txt.
 */
public class RobotData {

    /**
     * Collection of rules to use, specifying whether they are allowed or
     * disallowed, with the truth variable
     */
    private HashMap<String, Boolean> rules;
    /**
     * The delay to be used between opening each URL when crawling the site -
     * this is provided in robots.txt
     */
    private Integer delay;

    /**
     * Default constructor initialising all local global variables.
     */
    public RobotData() {
        rules = new HashMap<String, Boolean>();

        delay = Constants.DEFAULT_DELAY;
    }

    /**
     * Adds an individual rule to the collection, along with a truth flag of
     * whether it can be visited - 0 being for disallowed, and 1 for disallowed.
     *
     * @param rule The rule, in the form of a URL, as a string value
     * @param toggle Allowed or disallowed to visit URL specified by rule
     */
    public void setRule(String rule, Boolean toggle) {
        if (!rules.containsKey(rule)) {
            rules.put(rule, toggle);
        }
    }

    /**
     * Sets the delay of spider crawling speed between each URL visited.
     *
     * @param delay The delay in milliseconds
     */
    public void setDelay(Integer delay) {
        if (!(delay < Constants.DEFAULT_DELAY)) {
            this.delay = delay;
        }
    }

    /**
     * Gets the delay set as crawling speed, in milliseconds
     *
     * @return Integer Returns the delay to use for crawling
     */
    public Integer getDelay() {
        return delay;
    }

    /**
     * Returns a collection of all the spider rules.
     *
     * @return HashMap<String, Boolean> The collection of rules, in string form,
     * associated with a boolean value for disallowed and allowed to visit
     * specified URL for said rule
     */
    public HashMap<String, Boolean> getRules() {
        return rules;
    }
}
