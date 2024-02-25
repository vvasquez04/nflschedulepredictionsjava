import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
            System.out.println("Week " + w.weekNumber);
            //Cycle through each game (or bye week, etc) in the week
            for(WeeklyEvent e : w.events) {
                //Logic for showing a game to the user and predicting it
                if(e instanceof Game) {
                    predictGame((Game)e);
                }
            }
            updateStandings(allTeams);
        }
    }

    //Update standings upon request or at end of each regular season week
    public static void updateStandings(ArrayList<Team> allTeams) {
        //Instantiate arrayLists for conferences
        ArrayList<Team> afcTeams = new ArrayList<Team>();
        ArrayList<Team> nfcTeams = new ArrayList<Team>();

        //Instantiate arrayLists for divisions
        ArrayList<Team> afcEast = new ArrayList<Team>();
        ArrayList<Team> afcNorth = new ArrayList<Team>();
        ArrayList<Team> afcWest = new ArrayList<Team>();
        ArrayList<Team> afcSouth = new ArrayList<Team>();
        ArrayList<Team> nfcEast = new ArrayList<Team>();
        ArrayList<Team> nfcNorth = new ArrayList<Team>();
        ArrayList<Team> nfcWest = new ArrayList<Team>();
        ArrayList<Team> nfcSouth = new ArrayList<Team>();

        //Add teams to conference arrayLists
        for(Team t : allTeams) {
            if(t.conference.equals("AFC")) {
                afcTeams.add(t);
            } else {
                nfcTeams.add(t);
            }
        }

        //Add teams to division arrayLists
        for(Team t : afcTeams) {
            if(t.division.equals("East")) {
                afcEast.add(t);
            } else if (t.division.equals("North")) {
                afcNorth.add(t);
            } else if (t.division.equals("West")) {
                afcWest.add(t);
            } else {
                afcSouth.add(t);
            }
        }
        for(Team t : nfcTeams) {
            if(t.division.equals("East")) {
                nfcEast.add(t);
            } else if (t.division.equals("North")) {
                nfcNorth.add(t);
            } else if (t.division.equals("West")) {
                nfcWest.add(t);
            } else {
                nfcSouth.add(t);
            }
        }

        //Convert arrayLists to unsorted arrays
        Team[] rawAfcTeamsArr = afcTeams.toArray(new Team[afcTeams.size()]);
        Team[] rawNfcTeamsArr = nfcTeams.toArray(new Team[nfcTeams.size()]);
        Team[] rawAfcEastArr = afcEast.toArray(new Team[afcEast.size()]);
        Team[] rawAfcNorthArr = afcNorth.toArray(new Team[afcNorth.size()]);
        Team[] rawAfcWestArr = afcWest.toArray(new Team[afcWest.size()]);
        Team[] rawAfcSouthArr = afcSouth.toArray(new Team[afcSouth.size()]);
        Team[] rawNfcEastArr = nfcEast.toArray(new Team[nfcEast.size()]);
        Team[] rawNfcNorthArr = nfcNorth.toArray(new Team[nfcNorth.size()]);
        Team[] rawNfcWestArr = nfcWest.toArray(new Team[nfcWest.size()]);
        Team[] rawNfcSouthArr = nfcSouth.toArray(new Team[nfcSouth.size()]);

        //Sort arrays based on wins with no tiebreakers yet
        Arrays.sort(rawAfcTeamsArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawNfcTeamsArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawAfcEastArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawAfcNorthArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawAfcWestArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawAfcSouthArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawNfcEastArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawNfcNorthArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawNfcWestArr, Comparator.comparingInt(Team::getWins).reversed());
        Arrays.sort(rawNfcSouthArr, Comparator.comparingInt(Team::getWins).reversed());

        for(Team t : rawAfcTeamsArr) {
            System.out.println(t.wins);
        }
    }

    public static void predictGame(Game e) {
        //Display the individual game details
        if(!((Game)e).specialTitle.equals("")) {
            System.out.println(((Game)e).homeTeam.name + " vs " + ((Game)e).awayTeam.name + " at " + ((Game)e).venue + " on " + ((Game)e).dateTime.toString() + ", " + ((Game)e).specialTitle);
        } else {
            System.out.println(((Game)e).homeTeam.name + " vs " + ((Game)e).awayTeam.name + " at " + ((Game)e).venue + " on " + ((Game)e).dateTime.toString());
        }

        int margin = 2147483647;

        margin = overtimeGameLogic((Game)e, margin);

        scoringGameLogic((Game)e, margin);
    }

    public static void scoringGameLogic(Game e, int margin) {
        //Read score totals
        System.out.println("Home team's score:");
        int homeScore = StdIn.readInt();
        if(homeScore < 0) {
            System.out.println("Invalid score");
            scoringGameLogic(e, margin);
        }
        System.out.println("Away team's score:");
        int awayScore = StdIn.readInt();
        if(awayScore < 0) {
            System.out.println("Invalid score");
            scoringGameLogic(e, margin);
        }
        //TODO: error checking for string input

        //If game went to OT, the score has to be within the margin
        if(margin == 14) {
            int gameMargin = homeScore - awayScore;
            if((gameMargin > margin) || (gameMargin < -(margin))) {
                System.out.println("Game margin is too large for an overtime score! Re-enter the scores:");
                scoringGameLogic(e, margin);
            }
        }

        //Update game scores and add points to Teams' point differentials
        e.homeScore = homeScore;
        e.awayScore = awayScore;
        e.homeTeam.pointsScored += homeScore;
        e.awayTeam.pointsScored += awayScore;

        //Update general W/L/T records for both teams
        if(homeScore > awayScore) {
            e.homeTeam.wins++;
            e.awayTeam.losses++;
        } else if (awayScore > homeScore) {
            e.awayTeam.wins++;
            e.homeTeam.losses++;
        } else {
            e.homeTeam.ties++;
            e.awayTeam.ties++;
        }

        //Update conference W/L/T records, if applicable
        if(e.homeTeam.conference.equals(e.awayTeam.conference)) {
            if(homeScore > awayScore) {
                e.homeTeam.confWins++;
                e.awayTeam.confLosses++;
            } else if (awayScore > homeScore) {
                e.awayTeam.confWins++;
                e.homeTeam.confLosses++;
            } else {
                e.homeTeam.confTies++;
                e.awayTeam.confTies++;
            }
        }

        //Update division W/L/T records, if applicable
        if(e.homeTeam.division.equals(e.awayTeam.division)) {
            if(homeScore > awayScore) {
                e.homeTeam.divWins++;
                e.awayTeam.divLosses++;
            } else if (awayScore > homeScore) {
                e.awayTeam.divWins++;
                e.homeTeam.divLosses++;
            } else {
                e.homeTeam.divTies++;
                e.awayTeam.divTies++;
            }
        }

    }

    public static int overtimeGameLogic(Game e, int margin) {
        //Read overtime prediction
        System.out.println("Does this game go to OT?");
        String overtimeBool = StdIn.readString();
        //TODO: Make sure overtimeBool is y/n

        //Logic for if game goes to OT
        if(overtimeBool.trim().toLowerCase().equals("yes")) {
            margin = 14;
            if(overtimeBool.trim().toLowerCase().equals("yes")) { 
                System.out.println("How many overtimes?");
                int otNumber = StdIn.readInt();
                //TODO: Check for if user puts in string instead
                ((Game)e).overtimes = otNumber;
            }
        }

        return margin;
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