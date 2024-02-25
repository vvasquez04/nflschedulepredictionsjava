import java.util.ArrayList;
import java.time.LocalDateTime;

public class PredictionsRunner {

    public static void main(String[] args) {
        //Read listofteams.txt and instantiate the Teams objects
        ArrayList<Team> allTeams = new ArrayList<Team>();
        readTeams(allTeams);

        //Read games.txt and instantiate the Season object (and therefore the WeeklyEvent, Week, and Game objects for this season)
        ArrayList<Game> allGames = new ArrayList<Game>();
        Season season = readSeason(allGames, allTeams);

        //Prompt User for first input
        System.out.println("Welcome to the 2024 NFL Schedule Predictor! Please enter a command (type help for a list of commands)");
        String userInput = StdIn.readString().trim();
        System.out.println(((Game)(season.weeks.get(0).events.get(0))).homeTeam.name);
        mainMenu(userInput, season, allTeams);
    }

    //Core logic for predicting each game
    public static void predictionsStart(Season season, ArrayList<Team> allTeams) {
        //Cycle through each week in the logic
        for(Week w : season.weeks) {
            System.out.println(w.weekNumber);
            //Cycle through each game (or bye week, etc) in the week
            for(WeeklyEvent e : w.events) {
                //Logic for showing a game to the user and predicting it
                if(e instanceof Game) {
                    //Display the individual game details
                    if(!((Game)e).specialTitle.equals("")) {
                        System.out.println(((Game)e).homeTeam.name + " vs " + ((Game)e).awayTeam.name + " at " + ((Game)e).venue + " on " + ((Game)e).dateTime.toString() + ", " + ((Game)e).specialTitle);
                    } else {
                        System.out.println(((Game)e).homeTeam.name + " vs " + ((Game)e).awayTeam.name + " at " + ((Game)e).venue + " on " + ((Game)e).dateTime.toString());
                    }

                    //Read overtime prediction
                    System.out.println("Does this game go to OT?");
                    String overtimeBool = StdIn.readString();

                    //Logic for if game goes to OT
                    int margin = 2147483647;
                    if(overtimeBool.trim().toLowerCase().equals("yes")) {
                        margin = 14;
                        if(overtimeBool.trim().toLowerCase().equals("yes")) { 
                            System.out.println("How many overtimes?");
                            int otNumber = StdIn.readInt();
                            //TODO: Check for if user puts in string instead
                            ((Game)e).overtimes = otNumber;
                        }
                    }

                    
                }
            }
        }
    }

    //Consider this the "start menu" when 'start' is first inputted
    public static void mainMenu(String userInput, Season season, ArrayList<Team> allTeams) {
        //Switch case depending on user input, essentially making the menu work
        switch(userInput.toLowerCase()) {
            case ("help"):
                System.out.println("help stuff");
                System.out.println();
                System.out.println("Enter a new command: ");
                userInput = StdIn.readString().trim();
                mainMenu(userInput, season, allTeams);
            case ("start"):
                System.out.println("Beginning schedule predictions...");
                predictionsStart(season, allTeams);
                userInput = StdIn.readString().trim();
                System.out.println("Enter a new command: ");
                mainMenu(userInput, season, allTeams);
            case("exit"):
                System.out.println("Thank you for using the software!");
                break;
            default:
                System.out.println("Sup Bitch");
                System.out.println();
                System.out.println("Enter a new command: ");
                userInput = StdIn.readString().trim();
                mainMenu(userInput, season, allTeams);
        }
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
    public static Season readSeason(ArrayList<Game> games, ArrayList<Team> teams) {
        In in = new In("games.txt");
        Season season = new Season();
        while(in.hasNextLine()) {
            ArrayList<WeeklyEvent> events = new ArrayList<WeeklyEvent>();
            Week week = new Week(false, 0, events);
            readWeek(in, games, teams, week);
            season.weeks.add(week);
        }
        return season;
    }

    //Read a week of games from games.txt
    public static void readWeek(In in, ArrayList<Game> games, ArrayList<Team> teams, Week week) {
        //Read week number before this week's games, create variable for current string being read
        int weekNumber = Integer.parseInt(in.readLine());
        week.weekNumber = weekNumber;
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

            //Create Game instance, add it to games ArrayList and current week's events
            Game newGame = new Game(homeTeam, awayTeam, gameDetails[2], LocalDateTime.parse(gameDetails[3]), gameDetails[4]);
            games.add(newGame);
            week.events.add(newGame);
        }
    }
}


//TODO
//Games.txt file with some weeks filled in (for doing this midseason)



//CURRENTSTATE: reading games.txt file. For some reason looping through all a week's events has way more than expected. Must debug