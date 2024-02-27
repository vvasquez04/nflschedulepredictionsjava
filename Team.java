import java.util.ArrayList;

public class Team {
    public String name;
    public String conference;
    public String division;
    public int wins;
    public int losses;
    public int ties;
    public int divWins;
    public int divLosses;
    public int divTies;
    public int confWins;
    public int confLosses;
    public int confTies;
    public boolean madePlayoffs;
    public boolean wonDivision;
    public boolean oneSeed;
    public boolean wonConference;
    public int pointsScored;
    public int pointsAllowed;
    public ArrayList<Team> teamsPlayed;
    public ArrayList<Team> teamsBeaten;
    public double tempWinPctAgainstOthers;

    public static void main(String[] args) {

    }

    public Team(String iName, String iConference, String iDivision) {
        this.name = iName;
        this.conference = iConference;
        this.division = iDivision;
        this.wins = 0;
        this.losses = 0;
        this.ties = 0;
        this.divWins = 0;
        this.divLosses = 0;
        this.divTies = 0;
        this.confWins = 0;
        this.confLosses = 0;
        this.confTies = 0;
        this.madePlayoffs = false;
        this.wonDivision = false;
        this.oneSeed = false;
        this.wonConference = false;
        this.pointsAllowed = 0;
        this.pointsScored = 0;
        this.teamsPlayed = new ArrayList<Team>();
        this.teamsBeaten = new ArrayList<Team>();
        this.tempWinPctAgainstOthers = 0.0;
    }

    public Team() {
        
    }

    public int getWins() {
        return this.wins;
    }

    public double getTempWinPctAgainstOthers() {
        return this.tempWinPctAgainstOthers;
    }
}