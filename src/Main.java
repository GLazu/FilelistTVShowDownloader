import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class searches for new TV Shows and calls for the Torrent class to download
 *
 * @author Lazu George Mihai
 * @version 0.2
 */
public class Main
{
    public static final String SHOW_FOLDER = "D:\\TV Shows";
    
    public static void main(String [] args) {
        File[] tvShows = new File(SHOW_FOLDER).listFiles();
        String defaultTorrentQuality = "1080p"; //your default resolution preference
        String fallbackTorrentQuality = "720p"; //your fallback resolution preference
        TrayIconDemo notify = new TrayIconDemo();
        
        Torrent torrent = new Torrent();
        try {
            torrent.connect();
        } catch (Exception e) {
            System.err.println("Caught Exception: " + e.getMessage());
        }
        
        for (File show : tvShows) {
            TVShow currentShow = new TVShow(show);
            String lastSeason = currentShow.getLastSeason();
            
            if (show.toString().contains("@")) continue;
            
            if (lastSeason == null) {
                System.out.println("An error occured while searching for " + show.toString());
                continue;
            }
            
            String lastEpisode = currentShow.getLastEpisode(lastSeason);
            
            if (lastEpisode == null) {
                System.out.println("An error occured while searching for " + show.toString());
                continue;
            }
            
            //find the season and episode from the string
            Pattern p = Pattern.compile("S\\d\\dE\\d\\d");
            Matcher m = p.matcher(lastEpisode);
            //if found
            if (m.find()) {
                //substract the show name
                String showName = show.toString();
                showName = showName.substring(SHOW_FOLDER.length()+1,showName.length());
                //substract the season number
                int nextSeasonNo = Integer.parseInt(lastEpisode.substring(m.start()+1,m.end()-3));
                int nextEpisodeNo = Integer.parseInt(lastEpisode.substring(m.start()+4,m.end())) + 1;
                
                //here we call the torrent class to download the episode
                String phrase = "";
                String searchResult = null;
                
                int tries = 0;
                
                while (searchResult == null) {
                    if (tries == 0) phrase = String.format("%s S%02dE%02d %s", showName, nextSeasonNo, nextEpisodeNo, defaultTorrentQuality);
                    if (tries == 1 || tries == 3) {
                        phrase = String.format("%s S%02dE%02d %s", showName, nextSeasonNo, nextEpisodeNo, fallbackTorrentQuality);
                    }
                    if (tries == 2) {
                        nextSeasonNo++; nextEpisodeNo = 1;
                        phrase = String.format("%s S%02dE%02d %s", showName, nextSeasonNo, nextEpisodeNo, defaultTorrentQuality);
                    }
                    if (tries == 4) {
                        lastSeason = show.toString();
                        phrase = String.format("%s S%02d %s", showName, nextSeasonNo, defaultTorrentQuality);
                    }
                    if (tries == 5) {
                        phrase = String.format("%s S%02d %s", showName, nextSeasonNo, fallbackTorrentQuality);
                    }
                    if (tries == 6) break;
                    tries++;
                    searchResult = torrent.search(phrase);
                }
                
                if (searchResult == null) {
                    System.out.format("[*] No new episodes for %s\n", showName);
                } else {
                    if (tries - 1 == 2) {
                        String newSeasonFolder = String.format("%s\\%s S%02d", show.toString(), showName, nextSeasonNo);
                        File file = new File(newSeasonFolder);
                        file.mkdir();
                        lastSeason = newSeasonFolder;
                    }
                    System.out.format("[+] %s was found. Downloading...\n",phrase);
                    try { torrent.download(lastSeason, searchResult); } catch(java.lang.InterruptedException e) { };
                }
            }
        }
        try {
            notify.displayTray("Torrent Downloader", "The script has ended successfully");
        } catch (java.awt.AWTException e) {
            System.err.println(e);
        } catch (java.net.MalformedURLException e) {
            System.err.println(e);
        }
    }
}
