import java.util.ArrayList;

public class PredictionsRunner {

    public static void main(String[] args) {
        //Read listofteams.txt and instantiate the Teams objects
        ArrayList<Team> allTeams = new ArrayList<Team>();
        readTeams(allTeams);

        //Read games.txt and instantiate the Season object (and therefore the WeeklyEvent, Week, and Game objects for this season)
        ArrayList<Game> allGames = new ArrayList<Game>();
        readGames(allGames);
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
    public static void readGames(ArrayList<Game> games) {
        In in = new In("games.txt");
        while(in.hasNextLine()) {
            String gameString = in.readLine();
            String[] gameDetails = gameString.split(",");
            for(int i = 0; i < 5; i++) {
                System.out.println(gameDetails[i]);
            }
        }
    }
}


//TODO
//Games.txt file with some weeks filled in (for doing this midseason)



//CURRENTSTATE: reading games.txt file. Index oob exception, the emptystring after the comma didnt work. Need to make it like a special character or something and then read off of that