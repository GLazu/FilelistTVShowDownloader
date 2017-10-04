import com.jaunt.*;
import com.jaunt.component.*;
import java.io.*;
import java.net.*;
/**
 * This class will create Torrent objects which will search for and download a said TV Show
 *
 * @author Lazu George Mihai
 * @version 0.2
 */
public class Torrent
{
    private static final String USERNAME = "Joe96";
    private static final String PASSWORD = "pa$$w0rdD";
    private static final String TEMP_DOWNLOAD_LOC = System.getProperty("java.io.tmpdir");
    String phrase;
    UserAgent userAgent = new UserAgent();
    TrayIconDemo notify = new TrayIconDemo();
    
    public void connect() {
        try {
            userAgent.visit("http://filelist.ro/login.php");
            Form form = userAgent.doc.getForm(0);
            form.setTextField("username", USERNAME);
            form.setPassword("password", PASSWORD);
            form.submit("Login");
        } catch(JauntException e){                   
          System.err.println(e);
        }
    }

    /**
     * Search filelist.ro for the phrase
     *
     * @param   phrase formatted torrent name for search
     * @return    the torrent id
     */
    public String search(String phrase)
    {
        this.phrase = phrase;
        try {
            userAgent.visit("http://filelist.ro/browse.php");
            Form form = userAgent.doc.getForm(1);
            form.setTextField("search", phrase);
            form.submit("Caută!");
            if (userAgent.doc.innerHTML().contains("Nu s-a găsit nimic!")) return null;
            Element firstResult = userAgent.doc.findFirst("<div class=\"torrentrow\">").findFirst("a data-toggle=\"tooltip\"");
            String url = firstResult.getAt("href");
            return url.substring(34, url.length());
        } catch (JauntException e) {
            System.err.println(e);
            return null;
        }
    }
    
    /**
     * Search filelist.ro for the phrase
     *
     * @param   path the path where the torrent will be downloaded
     * @param   torrentID the torrent's id
     * @return    the torrent id
     */
    public void download(String path, String torrentID) throws java.lang.InterruptedException
    {
      String downloadURL = "http://filelist.ro/download.php?id=" + torrentID;
      String downloadLocation = TEMP_DOWNLOAD_LOC + "\\" + torrentID + ".torrent";
      File file = new File(downloadLocation);
      try {
          userAgent.download(downloadURL, file);
          String command = "deluge-console add -p '" + path + "' '" + downloadLocation + "'";
          try {
            Process child = Runtime.getRuntime().exec(command);
            Thread.sleep(5000);
            file.delete();
            try {
                String notifMsg = String.format("%s is now downloading", phrase);
                notify.displayTray("Torrent Downloader", notifMsg);
            } catch (java.awt.AWTException e) {
                System.err.println(e);
            } catch (java.net.MalformedURLException e) {
                System.err.println(e);
            }
          } catch (IOException e) {
              System.err.println(e);
          }
      } catch (ResponseException e) {
          System.err.println(e);
      }
    }
}
