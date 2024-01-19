public class Game extends WeeklyEvent {
    public Team homeTeam;
    public Team awayTeam;
    public String venue;
    public String time;
    public String date;
    public boolean isPrimeTime;
    public int homeScore;
    public int awayScore;
    public int overtimes;

    public static void main(String[] args) {

    }

    public Game(Team iHometeam, Team iAwayTeam, String iVenue, String iTime, String iDate, boolean iIsPrimeTime, int iHomeScore, int iAwayScore, int iOvertimes) {
        this.homeTeam = iHomeTeam;
        this.awayTeam = iAwayTeam;
        this.venue = iVenue;
        this.time = iTime;
        this.date = iDate;
        this.isPrimeTime = iIsPrimeTime;
        this.homeScore = iHomeScore;
        this.awayscore = iAwayScore;
        this.overtimes = iOvertimes;
    }
}
