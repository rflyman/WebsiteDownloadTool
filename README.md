# WebsiteDownloadTool
This tool written in Java downloads website source code and stores in a MySQL database for processing.<br />
<br />
Usage: <br />
Setup and configure MySQL and run create_test_db.sql<br />
Creates a database websitedownloader_db with temporary user: someuser password: somepass<br />
Update ./src/WebsiteDownloader/Constants.java if you need to update the database connection information<br />
<br />
<br />
To run the project from the command line, go to the dist folder and<br />
type the following:<br />
<br />
java -jar "WebsiteDownloader.jar" 