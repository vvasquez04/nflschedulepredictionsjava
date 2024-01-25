import java.util.ArrayList;

public class PredictionsRunner {

    public static void main(String[] args) {
        //Read listofteams.txt and instantiate the Teams objects
        ArrayList<Team> allTeams = new ArrayList<Team>();
        readTeams(allTeams);

        //Read games.txt and instantiate the Season object (and therefore the WeeklyEvent, Week, and Game objects for this season)
        ArrayList<Game> allGames = new ArrayList<Game>();
        readSeason(allGames);
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
    public static void readSeason(ArrayList<Game> games) {
        In in = new In("games.txt");
        while(in.hasNextLine()) {
            readWeek(in, games);
        }
    }

    //Read a week of games from games.txt
    public static void readWeek(In in, ArrayList<Game> games) {
        int weekNumber = Integer.parseInt(in.readLine());
        String endStr = "";
        while(!(endStr.equals("END"))) {
            endStr = in.readLine();
            if(endStr.equals("END")) { break; }
            String[] gameDetails = endStr.split(",");
            for(int i = 0; i < 5; i++) {
                System.out.println(gameDetails[i]);
                if(gameDetails[4].equals("`")) { gameDetails[4] = ""; }
                //TODO: have to match home/away with the Teams in the ArrayList
            }
        }
    }
}


//TODO
//Games.txt file with some weeks filled in (for doing this midseason)



//CURRENTSTATE: reading games.txt file. Index oob exception, the emptystring after the comma didnt work. Need to make it like a special character or something and then read off of that