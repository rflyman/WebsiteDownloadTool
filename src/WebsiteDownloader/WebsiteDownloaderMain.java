package WebsiteDownloader;

public class WebsiteDownloaderMain {
    public WebsiteDownloaderMain(){
    }
    
    public static void main(String[] args) {
        try{
            //Get content of pages parse page content for more links and add to db
            //For debugging lets only start with one thread. This can be scaled up.
            new ContentUpdaterThread().start(); 
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
