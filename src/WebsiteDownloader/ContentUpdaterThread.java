package WebsiteDownloader;

import Utils.Logger;
import WebsiteObjects.Website;
import java.util.logging.Level;


/**
 * Main thread for WebsiteDownloader
 * 
 * Repeats the following steps:
 * 1) Get a website (domain) to work on that isn't currently being worked on
 * 2) Load all pages recently completed to be worked on
 * 3) Go through each page that needs to be worked on and process the page.
 * 4) Repeat above until no more pages need to be worked on
 * 5) Once complete upload all new data to database and go to next domain to be worked on.
 * 
 * @author Robert Flyman
 */

public class ContentUpdaterThread extends Thread {

    public ContentUpdaterThread() {
    }
    private final int store_every_n_pages = 3; // stores every X pages to limit number of inserts but still ensure progress is not entirely lost if errors are encountered

    @Override
    public void run() {
        while (true) {
            try {
                if(Constants.DEBUG_MODE){
                    //Manually set the test domain to available to index.
                    DatabaseConnector.getInstance().overrideLastTouched(Constants.TEST_WEBSITE, 0);
                }
                //Step 1: Find a website to work on and load data
                Website w = new Website(); //if no data sent it will initialize to the next website that needs working on. Loads all data required.
                Logger.log("Starting to work on "+w.getWebsiteURL());
                int i = 0;
                //Step 3: Work on all pages
               
                while (w.processNextPage()) {
                    //continue processing all pages but lets save progress to the database every so often
                    i++;
                    
                    if (i > store_every_n_pages) {
                        Logger.log("Storing "+i+" pages to database.");
                        w.storeAllNewData();
                        i = 0;
                    }
                    Thread.sleep(w.getMinimumTimeBetweenProcessingPages());
                }
                Logger.log("Finished processing now storing "+i+" pages to database.");
                //Step 5: Finished all pages for this website so lets store new data
                w.storeAllNewData();
                
                if(Constants.DEBUG_MODE){
                    //Just exit for now as we don't want to run forever when testing...
                    Logger.log("Exiting for now as we don't want thread to run forever.");
                    System.exit(0);
                }
                
            } catch (Throwable ex) {
                /* Throwable upon no new domain found lets catch it and display. Might as well sleep and wait as well. */
                Logger.log(ex.getMessage());
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ex1) {
                    java.util.logging.Logger.getLogger(ContentUpdaterThread.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } 
        }
    }
}
