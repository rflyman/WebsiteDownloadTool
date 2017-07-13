package WebsiteDownloader;

import Utils.HtmlTidy;
import RobotsTxt.RobotParser;
import Utils.Logger;
import WebsiteObjects.Page;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.Proxy;

public class SourceGrabber {

    /**
     * Returns source for provided URL 1) Checks for robots.txt and verifies
     * provided URL can be accessed 2) Uses proxy to connect and download page
     * 3) Returns Page object with source and error code / success code if
     * applicable. I.E. 500, 404, 300, 200 - success Could override destination
     * by passing in new URL("http://www.some.com/page.html"); for example
     *
     * @param page The page to update with source and response code
     */
    public void updatePageSource(Page page) {
        URL destination = page.getURL();
        RobotParser rp = new RobotsTxt.RobotParser(page.getFullUrlAsString());
        String SourceList = "";
        try {

            //RobotParser rp = new RobotParser(destination.getProtocol()+"://"+destination.getHost()+"/robots.txt", destination.toExternalForm());
            if (rp.isLinkSafe(destination.toExternalForm())) {
                Logger.log("URL passed to SourceGrabber is considered safe to download as per robots.txt");

                /* Use a proxy server to hide current IP Address when requesting source of page */
                java.net.Proxy proxy = new Proxy(Proxy.Type.HTTP, new java.net.InetSocketAddress("210.48.147.84", 80));
                HttpURLConnection urlConnection = (HttpURLConnection) destination.openConnection();

                /* Setup request properties to mimic a Mozilla browser, in a production evironment this should be setup to properly identify the robot */
                urlConnection.setRequestProperty("HTTP_USER_AGENT", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 1.1.4322; .NET CLR 3.5.21022; .NET CLR 3.5.30729; .NET CLR 3.0.30618; InfoPath.2)");
                urlConnection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0");
                urlConnection.setRequestProperty("From", "yourwebsite.com");  //urlConnection.setRequestProperty("From", "google.com");

                /* Get reponse from connection */
                page.setStatusCode(urlConnection.getResponseCode());
                page.setResponseMessage(urlConnection.getResponseMessage());

                /* Display the response code and response for the page */
                Logger.log("HTTP/1.x " + page.getStatusCodeStr() + " " + page.getResponseMessage() + "\n");

                if (page.getStatusCode() == 200) {
                    int count = 1;
                    while (true) {
                        String header = urlConnection.getHeaderField(count);
                        String key = urlConnection.getHeaderFieldKey(count);
                        if (header == null || key == null) {
                            break;
                        }
                        System.out.println(urlConnection.getHeaderFieldKey(count) + ": " + header + "\n");
                        count++;
                    }
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    Reader r = new InputStreamReader(in);
                    int c;
                    SourceList = "";
                    while ((c = r.read()) != -1) {
                        SourceList += String.valueOf((char) c);
                    }
                    if (!SourceList.isEmpty()) {
                        page.setSource(new HtmlTidy().cleanupHtml(SourceList));
                        Logger.log("Page source set with length: "+page.getSource().length());
                    } // else source is empty
                }
            } else {
                page.setSource("");
                page.setStatusCode(403);
                Logger.err("Destination error for page is not safe");
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            if (Constants.DEBUG_MODE) {
                System.exit(1);
            }
        }
    }
}
