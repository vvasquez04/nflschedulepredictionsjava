import java.time.LocalDateTime;

public class Game extends WeeklyEvent {
    public Team homeTeam;
    public Team awayTeam;
    public String venue;
    public LocalDateTime dateTime;
    public String specialTitle;
    public int homeScore;
    public int awayScore;
    public int overtimes;

    public static void main(String[] args) {

    }

    public Game(Team iHomeTeam, Team iAwayTeam, String iVenue, LocalDateTime iDateTime, String iSpecialTitle, int iHomeScore, int iAwayScore, int iOvertimes) {
        this.homeTeam = iHomeTeam;
        this.awayTeam = iAwayTeam;
        this.venue = iVenue;
        this.dateTime = iDateTime;
        this.specialTitle = iSpecialTitle;
        this.homeScore = iHomeScore;
        this.awayScore = iAwayScore;
        this.overtimes = iOvertimes;
    }

    public Game(Team iHomeTeam, Team iAwayTeam, String iVenue, LocalDateTime iDateTime, String iSpecialTitle) {
        this.homeTeam = iHomeTeam;
        this.awayTeam = iAwayTeam;
        this.venue = iVenue;
        this.dateTime = iDateTime;
        this.specialTitle = iSpecialTitle;
        this.homeScore = 0;
        this.awayScore = 0;
        this.overtimes = 0;
    }
}
