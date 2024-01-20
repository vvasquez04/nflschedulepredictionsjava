import java.util.ArrayList;

public class PredictionsRunner {

    public static void main(String[] args) {
        ArrayList<Team> allTeams = new ArrayList<Team>();
        readTeams(allTeams);
    }

    public static void readTeams(ArrayList<Team> teams) {
        In in = new In("listofteams.txt");
        while(in.hasNextLine()) {
            String teamString = in.readLine();
            String[] teamDetails = teamString.split(",");
            Team newTeam = new Team(teamDetails[0], teamDetails[1], teamDetails[2]);
            teams.add(newTeam);
        }
    }
}


//TODO
//Games.txt file with some weeks filled in (for doing this midseason)