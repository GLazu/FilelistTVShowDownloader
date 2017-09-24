import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class searches for the latest tv episodes and downloads them
 *
 * @author Lazu George Mihai
 * @version 0.2
 */
public class TVShow
{
    private File show;

    /**
     * Constructor for objects of class TVShow
     */
    public TVShow(File show)
    {
        this.show = show;
    }

    /**
     * Fetches the last season of this TV Show
     *
     * @return    the latest season's episode list as a File[] object
     */
    public String getLastSeason()
    {
        File[] seasons = new File(this.show.toString()).listFiles();
        
        int latestSeason = 0;
        int latestSeasonIndex = -1;
        
        //Navigate each season of the show in order to find the newest one. Using regex because we cannot trust that their lexicographic order is their numerical order.
        for (File season : seasons) {
            String seasonFilename = season.toString();
            Pattern p = Pattern.compile("S\\d\\d");
            Matcher m = p.matcher(seasonFilename);
            
            if (m.find()) {
                int currentSeason = Integer.parseInt(seasonFilename.substring(m.start() + 1, m.end()));
                if (currentSeason > latestSeason) {
                    latestSeason = currentSeason;
                    latestSeasonIndex++;
                }
            } else {
                latestSeasonIndex++;
            }
        }
        
        if (latestSeason > 0) {
            String latestSeasonData = seasons[latestSeasonIndex].toString();
            return latestSeasonData;
        } else {
            return null;
        }
    }
    
    /**
     * Fetches the last episode of this TV Show
     *
     * @param   lastSeason a string containing the last season's path
     * @return    the latest episode path
     */
    public String getLastEpisode(String lastSeasonPath) {
       File[] lastSeason = new File(lastSeasonPath).listFiles();
        
       int latestEpisode = 0;
       int latestEpisodeIndex = -1;

        File[] episodes = lastSeason;
        
        for (File episode : episodes) {
            String episodeFilename = episode.toString();
            Pattern p = Pattern.compile("S\\d\\dE\\d\\d");
            Matcher m = p.matcher(episodeFilename);
            
            if (m.find()) {
                int currentEpisode = Integer.parseInt(episodeFilename.substring(m.start() + 4, m.end()));
                if (currentEpisode > latestEpisode) {
                    latestEpisode = currentEpisode;
                    latestEpisodeIndex++;
                }
            } else {
                System.out.println("An error occured while searching for " + show.toString());
                latestEpisodeIndex++;
            }
        }
        if (latestEpisode > 0) {
            String latestEpisodeData = episodes[latestEpisodeIndex].toString();
            return latestEpisodeData;
        } else {
            return null;
        }
    }
}
