import java.util.ArrayList;
import java.time.LocalDateTime;

public class PredictionsRunner {

    public static void main(String[] args) {
        //Read listofteams.txt and instantiate the Teams objects
        ArrayList<Team> allTeams = new ArrayList<Team>();
        readTeams(allTeams);

        //Read games.txt and instantiate the Season object (and therefore the WeeklyEvent, Week, and Game objects for this season)
        ArrayList<Game> allGames = new ArrayList<Game>();
        readSeason(allGames, allTeams);
    }

    //Use StdIn to read listofteams.txt and create a new Team object for each line
    public static void readTeams(ArrayList<Team> teams) {
        In in = new In("listofteams.txt");
        while(in.hasNextLine()) {
            String teamString = in.readLine();
            String[] teamDetails = teamString.split(",");
            Team newTeam = new Team(teamDetails[0], teamDetails[1], teamDetails[2]);
            teams.add(newTeam);
        }
    }

    //Use StdIn to read games.txt and populate a Season object (and therefore the objects it depends on)
    public static void readSeason(ArrayList<Game> games, ArrayList<Team> teams) {
        In in = new In("games.txt");
        while(in.hasNextLine()) {
            ArrayList<WeeklyEvent> events = new ArrayList<WeeklyEvent>();
            Week week = new Week(false, 0, events);
            readWeek(in, games, teams, week);
        }
    }

    //Read a week of games from games.txt
    public static void readWeek(In in, ArrayList<Game> games, ArrayList<Team> teams, Week week) {
        //Read week number before this week's games, create variable for current string being read
        int weekNumber = Integer.parseInt(in.readLine());
        System.out.println(weekNumber);
        String currentStr = "";

        //While week is ongoing, match team names in matchups to their team & populate the schedule
        while(!(currentStr.equals("END"))) {
            //Check if more games in the week
            currentStr = in.readLine();
            if(currentStr.equals("END")) { break; }

            //Create new Team instances to be populated by the line
            Team homeTeam = new Team();
            Team awayTeam = new Team();

            //Split up the string into the actual variables, read them, match home/away team names to the Teams in the ArrayList, and create a new Game instance
            String[] gameDetails = currentStr.split(",");
            //If game event isn't special, it should be an emptystring. I had to use accent marks to debug an error w the input file
            if(gameDetails[4].equals("`")) { gameDetails[4] = ""; }
            
            //Match home team
            for(Team t : teams) {
                if(t.name.equals(gameDetails[0])) {
                    awayTeam = t;
                }
            }

            //Match away team
            for(Team t : teams) {
                if(t.name.equals(gameDetails[1])) {
                    homeTeam = t;
                }
            }
            
            System.out.println(homeTeam.name + " " + awayTeam.name);
            Game newGame = new Game(homeTeam, awayTeam, gameDetails[2], LocalDateTime.parse(gameDetails[3]), gameDetails[4]);
            games.add(newGame);

            if(weekNumber > 18) { week.isPlayoffs = true; }
            week.weekNumber = weekNumber;
            for(Game g : games) {
                week.events.add(g);
            }
            for(WeeklyEvent e : week.events) {
                System.out.println("x");
            }
        }
    }
}


//TODO
//Games.txt file with some weeks filled in (for doing this midseason)



//CURRENTSTATE: reading games.txt file. For some reason looping through all a week's events has way more than expected. Must debug