package WebsiteDownloader;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URLWrapper is used to add functions and limit access to URL properties as needed within the app. URL is final and therefore cannot be extended.
 * @author Robert Flyman
 */
public class URLWrapper {
    private final URL page_url;
    
    public URLWrapper(URL page_url){
        this.page_url = page_url;
    }
    
    public URLWrapper(String page_url) throws MalformedURLException{
        this.page_url = new URL(page_url);
    }
    
    public String getFullUrlAsString(){
        return this.page_url.toExternalForm();
    }
    
    //returns just the name without extension I.E. "https://www.example.com/something/page" would return "example"
    public String getRootOfDomainWithoutExtension(){
        return page_url.getHost();
    }
}
