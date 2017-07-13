package WebsiteDownloader;

import java.io.File;

/**
 * All static strings are defined here so they can be used throughout the
 * crawler in multiple places - ensures hardcoded variables are centralized
 * and easy to change.
 */
public class Constants {
    
    /* Debug Mode */
    
    public final static Boolean DEBUG_MODE = true; // Set to false to disable
    public final static String TEST_WEBSITE = "http://refsalessystems.com";

    /* Database Settings */
    public final static String SERVER_NAME = "localhost";
    public final static String DATABASE_NAME = "websitedownloader_db";
    public final static String DATABASE_USERNAME = "someuser";
    public final static String DATABASE_PASSWORD = "somepass";
    
    /* Crawler Settings */
    public final static Integer DEFAULT_DELAY = 200;
    public final static String AGENT_NAME = "Google";
    public final static String ALL_AGENTS = "*";
    public final static int CONNECTION_TIMEOUT = 12000; // One fifth of a minute should be long enough
    public final static int READ_TIMEOUT = 60000; // A full minute should be plenty to get what we need

    /* Properties */
    public final static String PROPERTY_CRAWLING = "crawling";

    /* Potocols */
    public final static String PROTOCOL_HTTP = "http";
    public final static String PROTOCOL_HTTPS = "https";
    public final static String PROTOCOL_MAILTO = "mailto:";

    /* Delimiters */
    public final static String DELIMITER_DOTS = "..";
    public final static String DELIMITER_FORWARD_SLASH = "/";
    public final static String DELIMITER_HASH = "#";
    public final static String DELIMITER_LOGGER = " :: ";

    /* Loggers */
    public final static String LOGGER_ERROR = "errorLogger";
    public final static String LOGGER_BROKEN_LINK = "brokenLinkLogger";
    public final static String LOGGER_ERROR_ROBOT_MESSAGE = "Cannot parse robot file!";
    public final static String LOGGER_ERROR_FOLDER_MESSAGE = "Cannot create folders!";

    /* File Paths */
    public final static String FOLDER_LINKS = "Output" + File.separatorChar + "Spider";
    public final static String FILE_EXTERNAL_LINKS = "externalIWURLs";
    public final static String FILE_INTERNAL_LINKS = "localIWURLs";
    public final static String FOLDER_LOGS = "logs";
    public final static String FILE_ROBOTS_LOG = "robots_log.txt";
    public final static String FILE_LOGGER_ERROR = "errors ";
    public final static String FILE_LOGGER_BROKEN_LINKS = "broken_links ";
    public final static String FILE_LOGGER_EXTENSION = ".log";
    public final static String FILE_IMAGE_WEB = 
               "resources" + File.separatorChar + "images" + 
               File.separatorChar + "web.png";

    /* System Properties */
    public final static String SYSTEM_LINE_SEPARATOR = "line.separator";
    public final static String SYSTEM_LINE_SEPARATOR_PATTERN = "\r\n";

    /* ============================= GUI Stuff ============================= */
    /* Buttons */
    public final static String BUTTON_RUN = "Run Spider";
    public final static String BUTTON_PAUSE = "Pause";
    public final static String BUTTON_STOP = "Stop";
    public final static String BUTTON_RESUME = "Resume";

    /* Labels */
    public final static String LABEL_URL = "URL: ";
    public final static String LABEL_INTERNAL_URLS = "Internal URLs";
    public final static String LABEL_EXTERNAL_URLS = "External URLs";
    /* Frames */
    public final static String APPLICATION_NAME = "Spider by Aleksandar Mavrodiev & Piotr Murach";

    /* Border Titles */
    public final static String TITLE_LINK_TYPES = "Link Types";
    public final static String TITLE_SPIDER_PANEL = "Spider Panel";
    public final static String TITLE_STATUS = "Status";
    public final static String TTITLE_LINKS_VISITED = "Currently Processing...";

    /* Progress Bars */
    public final static String PROGRESS_SELECTION_BACKGROUND = "ProgressBar.selectionBackground";
    public final static String PROGRESS_SELECTION_FOREGROUND = "ProgressBar.selectionForeground";
    public final static String PROGRESS_FOREGROUND = "ProgressBar.foreground";
    public final static String PROGRESS_CELL_LENGTH = "ProgressBar.cellLength";

    /* Status Bars */
    public final static String STATUS_PROCESSING = "PROCESSING...";
    public final static String STATUS_RESUMING = "RESUMING...";
    public final static String STATUS_PAUSING = "PAUSING...";
    public final static String STATUS_STOPPED = "SPIDER STOPPED.";
    public final static String STATUS_FINISHED =
            "CRAWLING FINISHED! PLEASE ENTER ANOTHER URL OR FINISH.";
    public final static String STATUS_SPIDER_READY = "SPIDER IS READY - PLEASE PROVIDE AN URL";

    /* Dialogs */
    public final static String DIALOG_ERROR = "Error";

    /*Messages */
    public final static String URL_VERIFICATOIN_ERROR_MSG =
            "URL cannot be parsed or connection cannot be established";
    
    /*Component Names*/
    public final static String PROCESSING_AREA_NAME = "OUTPUT_LINKS";
    public final static String INTERNAL_AREA_NAME = "INTERNAL_LINKS";
    public final static String EXTERNAL_AREA_NAME = "EXTERNAL_LINKS";

    /* Frame sizes*/
    public final static Integer FRAME_WIDTH = 500;
    public final static Integer FRAME_HEIGHT = 500;
}
