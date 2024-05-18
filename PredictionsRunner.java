import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.*;
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

    //Core logic for predicting the season
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
            updateStandings(season, w, allTeams);
        }
    }

    //Update standings upon request or at end of each regular season week
    public static void updateStandings(Season season, Week week, ArrayList<Team> allTeams) {
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

        //Tiebreak divisions, put results into new arrays
        Team[] tiebrokenAfcEast = divTB(rawAfcEastArr, allTeams);
        Team[] tiebrokenAfcNorth = divTB(rawAfcNorthArr, allTeams);
        Team[] tiebrokenAfcWest = divTB(rawAfcWestArr, allTeams);
        Team[] tiebrokenAfcSouth = divTB(rawAfcSouthArr, allTeams);
        Team[] tiebrokenNfcEast = divTB(rawNfcEastArr, allTeams);
        Team[] tiebrokenNfcNorth = divTB(rawNfcNorthArr, allTeams);
        Team[] tiebrokenNfcWest = divTB(rawNfcWestArr, allTeams);
        Team[] tiebrokenNfcSouth = divTB(rawNfcSouthArr, allTeams);
    
        //Tiebreak conferences, put results into new arrays
        Team[] tiebrokenAFC = confTB(rawAfcTeamsArr, allTeams);
        Team[] tiebrokenNFC = confTB(rawNfcTeamsArr, allTeams);

        //End of Week Menu or Start Postseason Menu
        if(week.weekNumber < 18) {
            weeklyMenu(season, allTeams, week.weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest, tiebrokenAFC, tiebrokenNFC);
        } else {
            endOfSeasonMenu(season, allTeams, week.weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest, tiebrokenAFC, tiebrokenNFC);
        }
    }

    //Tiebreak conference standings
    public static Team[] confTB(Team[] confArr, ArrayList<Team> allTeams) {
        //Create an ArrayList to be added to
        ArrayList<Team> returnAR = new ArrayList<Team>();

        // Create a map of each list of teams with the same number of wins
        Map<Integer, Map<Integer, Map<Integer, List<Team>>>> groupedTeams = Arrays.asList(confArr).stream()
            .collect(Collectors.groupingBy(Team::getWins,
                        Collectors.groupingBy(Team::getLosses,
                                Collectors.groupingBy(Team::getTies))));

        for (Map.Entry<Integer, Map<Integer, Map<Integer, List<Team>>>> winsEntry : groupedTeams.entrySet()) {
            Integer wins = winsEntry.getKey();
            Map<Integer, Map<Integer, List<Team>>> lossesMap = winsEntry.getValue();

            for (Map.Entry<Integer, Map<Integer, List<Team>>> lossesEntry : lossesMap.entrySet()) {
                Integer losses = lossesEntry.getKey();
                Map<Integer, List<Team>> tiesMap = lossesEntry.getValue();

                for (Map.Entry<Integer, List<Team>> tiesEntry : tiesMap.entrySet()) {
                    Integer ties = tiesEntry.getKey();
                    List<Team> teams = tiesEntry.getValue();

                    breakTieConf(wins, losses, ties, teams, allTeams);

                    for (Team t : teams) {
                        returnAR.add(t);
                        //System.out.println("Adding " + t.name + " to returnAR");
                    }
                }
            }
        }

        Team[] returnArr = new Team[16];
        Collections.reverse(returnAR);
        returnArr = returnAR.toArray(returnArr);

        for(Team t : returnArr) {
            //System.out.println(t.name + " has " + t.wins + " wins, " + t.losses + " losses, and " + t.ties + " ties.");
        }

        return returnArr;
    }

    public static void breakTieConf(Integer wins, Integer losses, Integer Ties, List<Team> tiedTeams, ArrayList<Team> allTeams) {
        if(tiedTeams.size() > 2) {
            multiTiebreakerConfStepOne(tiedTeams, allTeams);
        } else if(tiedTeams.size() == 2) { 
            twoTeamTiebreakerConfStepOne(tiedTeams, allTeams); 
        }
    }

    //Conference tb step 1: eliminate same-division teams
    public static void multiTiebreakerConfStepOne(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        
    }

    //Two team conf tb Step 1: 
    public static void twoTeamTiebreakerConfStepOne(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        //TODO: logic for this
    }

    //Tiebreak division standings
    public static Team[] divTB(Team[] divisionArr, ArrayList<Team> allTeams) {
        //Create an ArrayList to be added to
        ArrayList<Team> returnAR = new ArrayList<Team>();

        // Create a map of each list of teams with the same number of wins
        Map<Integer, Map<Integer, Map<Integer, List<Team>>>> groupedTeams = Arrays.asList(divisionArr).stream()
            .collect(Collectors.groupingBy(Team::getWins,
                        Collectors.groupingBy(Team::getLosses,
                                Collectors.groupingBy(Team::getTies))));

        for (Map.Entry<Integer, Map<Integer, Map<Integer, List<Team>>>> winsEntry : groupedTeams.entrySet()) {
            Integer wins = winsEntry.getKey();
            Map<Integer, Map<Integer, List<Team>>> lossesMap = winsEntry.getValue();

            for (Map.Entry<Integer, Map<Integer, List<Team>>> lossesEntry : lossesMap.entrySet()) {
                Integer losses = lossesEntry.getKey();
                Map<Integer, List<Team>> tiesMap = lossesEntry.getValue();

                for (Map.Entry<Integer, List<Team>> tiesEntry : tiesMap.entrySet()) {
                    Integer ties = tiesEntry.getKey();
                    List<Team> teams = tiesEntry.getValue();

                    breakTieDiv(wins, losses, ties, teams, allTeams);

                    for (Team t : teams) {
                        returnAR.add(t);
                        //System.out.println("Adding " + t.name + " to returnAR");
                    }
                }
            }
        }

        Team[] returnArr = new Team[4];
        Collections.reverse(returnAR);
        returnArr = returnAR.toArray(returnArr);

        for(Team t : returnArr) {
            System.out.println(t.name + " has " + t.wins + " wins, " + t.losses + " losses, and " + t.ties + " ties.");
        }

        return returnArr;
    }

    //Staging ground for splitting off into different TB scenarios: Division version
    public static void breakTieDiv(Integer wins, Integer losses, Integer ties, List<Team> tiedTeams, ArrayList<Team> allTeams) {
        if(tiedTeams.size() > 2) {
            multiTiebreakerDivStepOne(tiedTeams, allTeams);
        } else if(tiedTeams.size() == 2) { 
            twoTeamTiebreakerDivStepOne(tiedTeams, allTeams); 
        }
    }

    //Start the tiebreaker process for multiple teams. This is step 1 - head to head among tied teams
    //NOTE: This doesn't work for 4-team ties. Must investigate
    public static void multiTiebreakerDivStepOne(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        
        // System.out.println("Div m step 1 with " + tiedTeams.size() + " teams");
        //Loop through each tied team and determine its winning percentage against the other teams (if no games, 0.0)
        for(Team t : tiedTeams) {
            double recordAgainstOthers = 0.0;
            double gamesAgainstOthers = 0;
            double winsAgainstOthers = 0;
            for(Team otherTeam : tiedTeams) {
                if(t.teamsPlayed.contains(otherTeam)) {
                    gamesAgainstOthers++;
                    if(t.teamsBeaten.contains(otherTeam)) { winsAgainstOthers++; }
                }
            }
            if (gamesAgainstOthers > 0) {
            recordAgainstOthers = winsAgainstOthers / gamesAgainstOthers;
            }

            t.tempWinPctAgainstOthers = recordAgainstOthers;
        }

        // for(Team t : tiedTeams) {
        //     System.out.println(t.tempWinPctAgainstOthers);
        // }

        //Sort the tied teams, best record first
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
        for(Team t : tiedTeams) {
            // System.out.println(t.name + " winning pct vs div opponents already played & tied with: " + t.tempWinPctAgainstOthers);
        }

        //Check if the first team is the only one with the best record. If so, recursively call on the rest of the list
        //If not, determine how many share a winning % and call step two on them, recursively calling step 1 on those eliminated
        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).tempWinPctAgainstOthers != tiedTeams.get(i - 1).tempWinPctAgainstOthers) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }
            // System.out.println("breakIndex: " + breakIndex);

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());

            // System.out.println("stillTiedTeams size: " + stillTiedTeams.size() + "   and restOfTeams size: " + restOfTeams.size());

            //Call step 2 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepTwo(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }      
            // System.out.println("Got past if stmt");     
            if(restOfTeams.size() == tiedTeams.size()) {
                multiTiebreakerDivStepTwo(restOfTeams, allTeams);
            } else {
                breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
            }
        }
    }

    //Division tb step 2: division records
    public static void multiTiebreakerDivStepTwo(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 2");
        //Set temp win pct to divison win %
        for(Team t : tiedTeams) {
            t.tempWinPctAgainstOthers = (double)t.divWins / (double)(t.divWins + t.divLosses);
        }
        
        //Sort based off div record, high to low
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());

        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).tempWinPctAgainstOthers != tiedTeams.get(i - 1).tempWinPctAgainstOthers) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepThree(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }            
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Multi Divison tb step 3: common games
    public static void multiTiebreakerDivStepThree(List<Team>tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 3");
        //TODO: Comment this better
        ArrayList<ArrayList<Team>> listOfLists = new ArrayList<>();

        // Populate listOfLists
        for (Team t : tiedTeams) {
            listOfLists.add(new ArrayList<>(t.teamsPlayed));
        }

        // Create a HashSet and add elements from listOfLists.get(0)
        Set<ArrayList<Team>> commonSet = new HashSet<>();
        commonSet.add(new ArrayList<>(listOfLists.get(0)));

        for (ArrayList<Team> list : listOfLists) {
            commonSet.retainAll(list);
        }

        // Convert Set to ArrayList
        ArrayList<Team> commonTeams = new ArrayList<>();
        for (ArrayList<Team> commonList : commonSet) {
            commonTeams.addAll(commonList);
        }

        //Set tempWinPct to common games win %
        for(Team t : tiedTeams) {
            int teamsBeatenCtr = 0;
            for(Team cTeam : commonTeams) {
                if(t.teamsBeaten.contains(cTeam)) { teamsBeatenCtr++; }
            }
            t.tempWinPctAgainstOthers = (double)teamsBeatenCtr / commonTeams.size();
        }

        //Sort tiedTeams by win % against commons
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());

        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).tempWinPctAgainstOthers != tiedTeams.get(i - 1).tempWinPctAgainstOthers) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepFour(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Div step 4: conference win %
    public static void multiTiebreakerDivStepFour(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 4");
        //Set temp win pct to conference win %
        for(Team t : tiedTeams) {
            t.tempWinPctAgainstOthers = (double)t.confWins / (double)(t.confWins + t.confLosses);
        }
        
        //Sort based off div record, high to low
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());

        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).tempWinPctAgainstOthers != tiedTeams.get(i - 1).tempWinPctAgainstOthers) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepFive(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }            
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Div multi tb step 5: strength of victory
    public static void multiTiebreakerDivStepFive(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 5");
        
        for(Team t : tiedTeams) {
            double wins = 0.0;
            double totalGames = 0.0;
            ArrayList<Team> teamsCovered = new ArrayList<Team>();
            for(Team beatenTeam : t.teamsBeaten) {
                if(!teamsCovered.contains(beatenTeam)) {
                    wins += beatenTeam.wins;
                    totalGames += (beatenTeam.wins + beatenTeam.losses);
                    teamsCovered.add(beatenTeam);
                }
            }
            t.tempWinPctAgainstOthers = wins / totalGames;
        }

        //Sort based off SOV, high to low
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
    
        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).tempWinPctAgainstOthers != tiedTeams.get(i - 1).tempWinPctAgainstOthers) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepSix(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }            
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Div multi step 6: strength of schedule
    public static void multiTiebreakerDivStepSix(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 6");
        
        for(Team t : tiedTeams) {
            double wins = 0.0;
            double totalGames = 0.0;
            ArrayList<Team> teamsCovered = new ArrayList<Team>();
            for(Team teamPlayed : t.teamsPlayed) {
                if(!teamsCovered.contains(teamPlayed)) {
                    wins += teamPlayed.wins;
                    totalGames += (teamPlayed.wins + teamPlayed.losses);
                    teamsCovered.add(teamPlayed);
                }
            }
            t.tempWinPctAgainstOthers = wins / totalGames;
        }

        //Sort based off SOV, high to low
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
    
        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).tempWinPctAgainstOthers != tiedTeams.get(i - 1).tempWinPctAgainstOthers) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepSeven(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }            
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Div multi tb step 7: combined ranking of PS and PA in conference
    public static void multiTiebreakerDivStepSeven(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 7");

        ArrayList<Team> conferenceTeams = new ArrayList<Team>();

        for (Team t : allTeams) {
            if (t.conference.equals(tiedTeams.get(0).conference)) {
                conferenceTeams.add(t);
            }
        }

        Collections.sort(conferenceTeams, Comparator.comparingInt(Team::getPointsScored).reversed());
        for(Team t : tiedTeams) {
            t.TBPSRank = conferenceTeams.indexOf(t);
        }

        Collections.sort(conferenceTeams, Comparator.comparingInt(Team::getPointsAllowed).reversed());
        for(Team t : tiedTeams) {
            t.TBPARank = conferenceTeams.indexOf(t);
        }

        for(Team t : tiedTeams) {
            t.paPluspsNumber = t.TBPARank + t.TBPSRank;
        }

        Collections.sort(tiedTeams, Comparator.comparingInt(Team::getPaPlusPsNumber));

        if(tiedTeams.get(0).paPluspsNumber != tiedTeams.get(1).paPluspsNumber) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).paPluspsNumber != tiedTeams.get(i - 1).paPluspsNumber) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepEight(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }            
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Div tb step 8: combined ranking of PS and PA
    public static void multiTiebreakerDivStepEight(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div m step 8");

        Collections.sort(allTeams, Comparator.comparingInt(Team::getPointsScored).reversed());
        for(Team t : tiedTeams) {
            t.TBPSRank = allTeams.indexOf(t);
        }

        Collections.sort(allTeams, Comparator.comparingInt(Team::getPointsAllowed).reversed());
        for(Team t : tiedTeams) {
            t.TBPARank = allTeams.indexOf(t);
        }

        for(Team t : tiedTeams) {
            t.paPluspsNumber = t.TBPARank + t.TBPSRank;
        }

        Collections.sort(tiedTeams, Comparator.comparingInt(Team::getPaPlusPsNumber));

        if(tiedTeams.get(0).paPluspsNumber != tiedTeams.get(1).paPluspsNumber) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            List<Team> restOfTeams = tiedTeams.subList(1, tiedTeams.size());
            // for(Team t : tiedTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            // for(Team t : restOfTeams) { System.out.print(t.name + " "); }
            // System.out.println();
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        } else {
            int breakIndex = 0;
            boolean foundFirstNonTied = false;
            for (int i = 1; i < tiedTeams.size(); i++) {
                if (tiedTeams.get(i).paPluspsNumber != tiedTeams.get(i - 1).paPluspsNumber) {
                    breakIndex = i;
                    foundFirstNonTied = true;
                    break; // Exit the loop once the first non-tied element is found
                }
            }

            //Separate into sublists of teams that passed step 1 and didn't
            List<Team> stillTiedTeams = tiedTeams.subList(0, breakIndex);
            List<Team> restOfTeams = tiedTeams.subList(breakIndex, tiedTeams.size());
            
            //Call step 3 on those still tied, step 1 on the rest
            if(stillTiedTeams.size() > 2) {
                multiTiebreakerDivStepNine(stillTiedTeams, allTeams);
            } else {
                twoTeamTiebreakerDivStepOne(stillTiedTeams, allTeams);
            }            
            breakTieDiv(tiedTeams.get(0).wins, tiedTeams.get(0).losses, tiedTeams.get(0).ties, restOfTeams, allTeams);
        }
    }

    //Multi Div tb step 9: net points in common games
    public static void multiTiebreakerDivStepNine(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        //Finish this later, no tb is gonna go 9 steps lmao
        // System.out.println("Div m step 9");
    }

    //Two team div tb step 1: head to head
    public static void twoTeamTiebreakerDivStepOne(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        if(tiedTeams.size() < 2) { return; }
        // System.out.println("Div t step 1");
        // for(Team t : tiedTeams){
        //     System.out.print(t.name + " ");
        // }
        // System.out.println();
        
        if(tiedTeams.size() == 1 || tiedTeams.size() == 0) { return; }

        for(Team t : tiedTeams) {
            double recordAgainstOthers = 0.0;
            double gamesAgainstOthers = 0;
            double winsAgainstOthers = 0;
            for(Team otherTeam : tiedTeams) {
                if(t.teamsPlayed.contains(otherTeam)) {
                    gamesAgainstOthers++;
                    if(t.teamsBeaten.contains(otherTeam)) { winsAgainstOthers++; }
                }
            }
            if (gamesAgainstOthers > 0) {
            recordAgainstOthers = winsAgainstOthers / gamesAgainstOthers;
            }

            t.tempWinPctAgainstOthers = recordAgainstOthers;
        }

        //Sort the tied teams, best record first
        Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());

        for(Team t : tiedTeams) {
            // System.out.println(t.name + " winning pct vs div opponents already played & tied with: " + t.tempWinPctAgainstOthers);
        }

        //Check if the first team is the only one with the best record. If so, recursively call on the rest of the list
        //If not, determine how many share a winning % and call step two on them, recursively calling step 1 on those eliminated
        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            // System.out.println(tiedTeams.get(0).name + " has won the tiebreaker based off of h2h win percentage over: " + tiedTeams.get(1).name);
            return;
        } else {
            twoTeamTiebreakerDivStepTwo(tiedTeams, allTeams);
        }
    }
    
    //Two team div tb step 2: division record
    public static void twoTeamTiebreakerDivStepTwo(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 2");
        
        tiedTeams.get(0).tempWinPctAgainstOthers = (double)tiedTeams.get(0).divWins / ((double)tiedTeams.get(0).divWins + (double)tiedTeams.get(0).divLosses);
        tiedTeams.get(1).tempWinPctAgainstOthers = (double)tiedTeams.get(1).divWins / ((double)tiedTeams.get(1).divWins + (double)tiedTeams.get(1).divLosses);

        if(!(tiedTeams.get(0).tempWinPctAgainstOthers == tiedTeams.get(1).tempWinPctAgainstOthers)) {
            Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
            return;
        } else {
            twoTeamTiebreakerDivStepThree(tiedTeams, allTeams);
        }
    }

    //Two team div tb step 3: common games
    public static void twoTeamTiebreakerDivStepThree(List<Team>tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 3");
        
        //TODO: Comment this better
        ArrayList<ArrayList<Team>> listOfLists = new ArrayList<>();

        // Populate listOfLists
        for (Team t : tiedTeams) {
            listOfLists.add(new ArrayList<>(t.teamsPlayed));
        }

        // Create a HashSet and add elements from listOfLists.get(0)
        Set<ArrayList<Team>> commonSet = new HashSet<>();
        commonSet.add(new ArrayList<>(listOfLists.get(0)));

        for (ArrayList<Team> list : listOfLists) {
            commonSet.retainAll(list);
        }

        // Convert Set to ArrayList
        ArrayList<Team> commonTeams = new ArrayList<>();
        for (ArrayList<Team> commonList : commonSet) {
            commonTeams.addAll(commonList);
        }

        //Set tempWinPct to common games win %
        for(Team t : tiedTeams) {
            int teamsBeatenCtr = 0;
            for(Team cTeam : commonTeams) {
                if(t.teamsBeaten.contains(cTeam)) { teamsBeatenCtr++; }
            }
            t.tempWinPctAgainstOthers = (double)teamsBeatenCtr / commonTeams.size();
        }

        //Sort tiedTeams by win % against commons
        if(tiedTeams.get(0).tempWinPctAgainstOthers != tiedTeams.get(1).tempWinPctAgainstOthers) {
            Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
            return;
        } else {
            twoTeamTiebreakerDivStepFour(tiedTeams, allTeams);
        }
    }

    //2 team div tb Step 4: conference games
    public static void twoTeamTiebreakerDivStepFour(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 4");
        
        tiedTeams.get(0).tempWinPctAgainstOthers = (double)tiedTeams.get(0).confWins / ((double)tiedTeams.get(0).confWins + (double)tiedTeams.get(0).confLosses);
        tiedTeams.get(1).tempWinPctAgainstOthers = (double)tiedTeams.get(1).confWins / ((double)tiedTeams.get(1).confWins + (double)tiedTeams.get(1).confLosses);

        if(!(tiedTeams.get(0).tempWinPctAgainstOthers == tiedTeams.get(1).tempWinPctAgainstOthers)) {
            Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
            return;
        } else {
            twoTeamTiebreakerDivStepFive(tiedTeams, allTeams);
        }
    }

    //2 team div tb Step 5: strength of victory
    public static void twoTeamTiebreakerDivStepFive(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 5");
        
        for(Team t : tiedTeams) {
            double wins = 0.0;
            double totalGames = 0.0;
            ArrayList<Team> teamsCovered = new ArrayList<Team>();
            for(Team beatenTeam : t.teamsBeaten) {
                if(!teamsCovered.contains(beatenTeam)) {
                    wins += beatenTeam.wins;
                    totalGames += (beatenTeam.wins + beatenTeam.losses);
                    teamsCovered.add(beatenTeam);
                }
            }
            t.tempWinPctAgainstOthers = wins / totalGames;
        }

        if(!(tiedTeams.get(0).tempWinPctAgainstOthers == tiedTeams.get(1).tempWinPctAgainstOthers)) {
            Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
            return;
        } else {
            twoTeamTiebreakerDivStepSix(tiedTeams, allTeams);
        }
    }

    //2 team div tb Step 6: Strength of schedule
    public static void twoTeamTiebreakerDivStepSix(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 6");
        
        for(Team t : tiedTeams) {
            double wins = 0.0;
            double totalGames = 0.0;
            ArrayList<Team> teamsCovered = new ArrayList<Team>();
            for(Team teamPlayed : t.teamsPlayed) {
                if(!teamsCovered.contains(teamPlayed)) {
                    wins += teamPlayed.wins;
                    totalGames += (teamPlayed.wins + teamPlayed.losses);
                    teamsCovered.add(teamPlayed);
                }
            }
            t.tempWinPctAgainstOthers = wins / totalGames;
        }

        if(!(tiedTeams.get(0).tempWinPctAgainstOthers == tiedTeams.get(1).tempWinPctAgainstOthers)) {
            Collections.sort(tiedTeams, Comparator.comparingDouble(Team::getTempWinPctAgainstOthers).reversed());
            return;
        } else {
            twoTeamTiebreakerDivStepSeven(tiedTeams, allTeams);
        }
    }

    //2-team div tb Step 7: PS, PA combined conference score
    public static void twoTeamTiebreakerDivStepSeven(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 7");
        
        ArrayList<Team> conferenceTeams = new ArrayList<Team>();

        for (Team t : allTeams) {
            if (t.conference.equals(tiedTeams.get(0).conference)) {
                conferenceTeams.add(t);
            }
        }

        Collections.sort(conferenceTeams, Comparator.comparingInt(Team::getPointsScored).reversed());
        for(Team t : tiedTeams) {
            t.TBPSRank = conferenceTeams.indexOf(t);
        }

        Collections.sort(conferenceTeams, Comparator.comparingInt(Team::getPointsAllowed).reversed());
        for(Team t : tiedTeams) {
            t.TBPARank = conferenceTeams.indexOf(t);
        }

        for(Team t : tiedTeams) {
            t.paPluspsNumber = t.TBPARank + t.TBPSRank;
        }


        if(tiedTeams.get(0).paPluspsNumber != tiedTeams.get(1).paPluspsNumber) {
            Collections.sort(tiedTeams, Comparator.comparingInt(Team::getPaPlusPsNumber));
            return;
        } else {
            twoTeamTiebreakerDivStepEight(tiedTeams, allTeams);
        }
    }

    public static void twoTeamTiebreakerDivStepEight(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 8");
        
        Collections.sort(allTeams, Comparator.comparingInt(Team::getPointsScored).reversed());
        for(Team t : tiedTeams) {
            t.TBPSRank = allTeams.indexOf(t);
        }

        Collections.sort(allTeams, Comparator.comparingInt(Team::getPointsAllowed).reversed());
        for(Team t : tiedTeams) {
            t.TBPARank = allTeams.indexOf(t);
        }

        for(Team t : tiedTeams) {
            t.paPluspsNumber = t.TBPARank + t.TBPSRank;
        }


        if(tiedTeams.get(0).paPluspsNumber != tiedTeams.get(1).paPluspsNumber) {
            Collections.sort(tiedTeams, Comparator.comparingInt(Team::getPaPlusPsNumber));
            return;
        } else {
            twoTeamTiebreakerDivStepNine(tiedTeams, allTeams);
        }
    }

    public static void twoTeamTiebreakerDivStepNine(List<Team> tiedTeams, ArrayList<Team> allTeams) {
        // System.out.println("Div t step 9");
        //TODO: Finish this later. Tiebreakers won't get this far
    }

    public static void predictGame(Game e) {
        //Display the individual game details
        if(!e.specialTitle.equals("")) {
            System.out.println(e.homeTeam.name + " vs " + e.awayTeam.name + " at " + e.venue + " on " + e.dateTime.toString() + ", " + e.specialTitle);
        } else {
            System.out.println(e.homeTeam.name + " vs " + e.awayTeam.name + " at " + e.venue + " on " + e.dateTime.toString());
        }

        //Max margin of victory possible
        int margin = 2147483647;

        //Set margin depending on if game went to OT
        margin = overtimeGameLogic(e, margin);

        //Read score of game
        scoringGameLogic(e, margin);

        //Add matchup to list of teams played for each team
        e.homeTeam.teamsPlayed.add(e.awayTeam);
        e.awayTeam.teamsPlayed.add(e.homeTeam);
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

        //Update game scores, add points to Teams' point differentials, and add losing team to winning team's teamsBeaten arraylist
        e.homeScore = homeScore;
        e.awayScore = awayScore;
        e.homeTeam.pointsScored += homeScore;
        e.awayTeam.pointsScored += awayScore;
        if(homeScore > awayScore) {
            e.homeTeam.teamsBeaten.add(e.awayTeam);
        } else if (awayScore > homeScore) {
            e.awayTeam.teamsBeaten.add(e.homeTeam);
        }

        //Update general W/L/T records for both teams
        if(homeScore > awayScore) {
            e.homeTeam.wins++;
            e.awayTeam.losses++;
        } else if (awayScore > homeScore) {
            e.awayTeam.wins++;
            e.homeTeam.losses++;
        } else {
            //TODO: check to make sure they didn't say it's a tie without overtime
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
    
    //Print Standings for Specific Division
    public static void printStandingsDiv(Season season, ArrayList<Team> allTeams, int weekNumber, Team[] tiebrokenAfcEast, Team[] tiebrokenAfcNorth, Team[] tiebrokenAfcSouth, Team[] tiebrokenAfcWest, Team[] tiebrokenNfcEast, Team[] tiebrokenNfcNorth, Team[] tiebrokenNfcSouth, Team[] tiebrokenNfcWest, Team[] tiebrokenAFC, Team[] tiebrokenNFC) {
        System.out.println("Which Division?");
        String userInput = StdIn.readString().trim().toLowerCase();

        switch(userInput) {
            case ("afc east"):
                System.out.println("Week " + weekNumber + " AFC East Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcEast.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("afc north"):
                System.out.println("Week " + weekNumber + " AFC North Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcNorth.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("afc south"):
                System.out.println("Week " + weekNumber + " AFC South Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcSouth.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("afc west"):
                System.out.println("Week " + weekNumber + " AFC West Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcWest.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc east"):
                System.out.println("Week " + weekNumber + " NFC East Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcEast.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc north"):
                System.out.println("Week " + weekNumber + " NFC North Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcNorth.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc south"):
                System.out.println("Week " + weekNumber + " NFC South Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcSouth.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc west"):
                System.out.println("Week " + weekNumber + " NFC West Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcWest.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            default:
                System.out.println("Invalid input, try again.");
                printStandingsDiv(season, allTeams, weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest, tiebrokenAFC, tiebrokenNFC);
        }
        
        System.out.println("AFC");
        for(int i = 0; i < tiebrokenAFC.length; i++) {
            Team t = tiebrokenAfcEast[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println("NFC");
        for(int i = 0; i < tiebrokenNFC.length; i++) {
            Team t = tiebrokenAfcEast[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
    }

    //Print Standings for Specific Division
    public static void printStandingsDiv(Season season, ArrayList<Team> allTeams, int weekNumber, Team[] tiebrokenAfcEast, Team[] tiebrokenAfcNorth, Team[] tiebrokenAfcSouth, Team[] tiebrokenAfcWest, Team[] tiebrokenNfcEast, Team[] tiebrokenNfcNorth, Team[] tiebrokenNfcSouth, Team[] tiebrokenNfcWest) {
        //Get user input for division
        System.out.println("Which Division?");
        String userInput = StdIn.readLine().trim().toLowerCase();

        //Print division standings based on user input
        switch(userInput) {
            case ("afc east"):
                System.out.println("Week " + weekNumber + " AFC East Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcEast.length; i++) {
                    Team t = tiebrokenAfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("afc north"):
                System.out.println("Week " + weekNumber + " AFC North Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcNorth.length; i++) {
                    Team t = tiebrokenAfcNorth[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("afc south"):
                System.out.println("Week " + weekNumber + " AFC South Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcSouth.length; i++) {
                    Team t = tiebrokenAfcSouth[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("afc west"):
                System.out.println("Week " + weekNumber + " AFC West Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAfcWest.length; i++) {
                    Team t = tiebrokenAfcWest[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc east"):
                System.out.println("Week " + weekNumber + " NFC East Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcEast.length; i++) {
                    Team t = tiebrokenNfcEast[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc north"):
                System.out.println("Week " + weekNumber + " NFC North Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcNorth.length; i++) {
                    Team t = tiebrokenNfcNorth[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc south"):
                System.out.println("Week " + weekNumber + " NFC South Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcSouth.length; i++) {
                    Team t = tiebrokenNfcSouth[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc west"):
                System.out.println("Week " + weekNumber + " NFC West Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNfcWest.length; i++) {
                    Team t = tiebrokenNfcWest[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            //Error catching
            default:
                System.out.println("Invalid division name! Try again");
                printStandingsDiv(season, allTeams, weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest);
        }
    }

    //Print conference standings upon user input
    public static void printStandingsConf(Season season, ArrayList<Team> allTeams, int weekNumber, Team[] tiebrokenAFC, Team[] tiebrokenNFC) {
        //Get user input for conference
        System.out.println("Which Conference?");
        String userInput = StdIn.readString().trim().toLowerCase();

        //Print conference standings based on user input
        switch(userInput) {
            case ("afc"):
                System.out.println("Week " + weekNumber + " AFC Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenAFC.length; i++) {
                    Team t = tiebrokenAFC[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            case ("nfc"):
                System.out.println("Week " + weekNumber + " NFC Standings:");
                System.out.println();
                for(int i = 0; i < tiebrokenNFC.length; i++) {
                    Team t = tiebrokenNFC[i];
                    System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
                }
            //Error catching
            default:
                System.out.println("Invalid conference name! Try again");
                printStandingsConf(season, allTeams, weekNumber, tiebrokenAFC, tiebrokenNFC);
        }
    }

    //End of season menu, initiate postseason
    public static void endOfSeasonMenu(Season season, ArrayList<Team> allTeams, int weekNumber, Team[] tiebrokenAfcEast, Team[] tiebrokenAfcNorth, Team[] tiebrokenAfcSouth, Team[] tiebrokenAfcWest, Team[] tiebrokenNfcEast, Team[] tiebrokenNfcNorth, Team[] tiebrokenNfcSouth, Team[] tiebrokenNfcWest, Team[] tiebrokenAFC, Team[] tiebrokenNFC) {
        System.out.println("END OF SEASON RESULTS:");
        System.out.println();
        System.out.println("Conference Standings:");
        System.out.println();
        System.out.println("AFC");
        for(int i = 0; i < tiebrokenAFC.length; i++) {
            Team t = tiebrokenAFC[i];
            System.out.println((i+1) + " - " + t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("NFC");
        for(int i = 0; i < tiebrokenNFC.length; i++) {
            Team t = tiebrokenNFC[i];
            System.out.println((i+1) + " - " + t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println("Division Standings:");
        System.out.println();
        System.out.println("AFC East Final Standings:");
        for(int i = 0; i < tiebrokenAfcEast.length; i++) {
            Team t = tiebrokenAfcEast[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("AFC North Final Standings:");
        for(int i = 0; i < tiebrokenAfcNorth.length; i++) {
            Team t = tiebrokenAfcNorth[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("AFC South Final Standings:");
        for(int i = 0; i < tiebrokenAfcSouth.length; i++) {
            Team t = tiebrokenAfcSouth[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("AFC West Final Standings:");
        for(int i = 0; i < tiebrokenAfcWest.length; i++) {
            Team t = tiebrokenAfcWest[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("NFC East Final Standings:");
        for(int i = 0; i < tiebrokenNfcEast.length; i++) {
            Team t = tiebrokenNfcEast[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("NFC North Final Standings:");
        for(int i = 0; i < tiebrokenNfcNorth.length; i++) {
            Team t = tiebrokenNfcNorth[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("NFC South Final Standings:");
        for(int i = 0; i < tiebrokenNfcSouth.length; i++) {
            Team t = tiebrokenNfcSouth[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }
        System.out.println();
        System.out.println("NFC West Final Standings:");
        for(int i = 0; i < tiebrokenNfcWest.length; i++) {
            Team t = tiebrokenNfcWest[i];
            System.out.println(t.name + " " + t.wins + "W " + t.losses + "L " + t.ties + "T ");
        }

        //WILD CARD ROUND

        ArrayList<WeeklyEvent> eventsWC = new ArrayList<WeeklyEvent>();
        Week wildCardWeekend = new Week(true, 19, eventsWC);
        //TODO: Add home stadiums to Team.java
        Game wcGm1Afc = new Game(tiebrokenAFC[1], tiebrokenAFC[6], "Playoff Venue", LocalDateTime.parse("2024-01-13T13:00"), "Wild Card Weekend Game 1");
        Game wcGm2Afc = new Game(tiebrokenAFC[2], tiebrokenAFC[5], "Playoff Venue", LocalDateTime.parse("2024-01-14T16:30"), "Wild Card Weekend Game 5");
        Game wcGm3Afc = new Game(tiebrokenAFC[3], tiebrokenAFC[4], "Playoff Venue", LocalDateTime.parse("2024-01-13T20:30"), "Wild Card Weekend Game 3");
        Game wcGm1Nfc = new Game(tiebrokenNFC[1], tiebrokenNFC[6], "Playoff Venue", LocalDateTime.parse("2024-01-14T13:00"), "Wild Card Weekend Game 4");
        Game wcGm2Nfc = new Game(tiebrokenNFC[1], tiebrokenNFC[6], "Playoff Venue", LocalDateTime.parse("2024-01-13T16:30"), "Wild Card Weekend Game 2");
        Game wcGm3Nfc = new Game(tiebrokenNFC[1], tiebrokenNFC[6], "Playoff Venue", LocalDateTime.parse("2024-01-14T20:30"), "Wild Card Weekend Game 6");
        wildCardWeekend.events.add(wcGm1Afc);
        wildCardWeekend.events.add(wcGm2Afc);
        wildCardWeekend.events.add(wcGm3Afc);
        wildCardWeekend.events.add(wcGm1Nfc);
        wildCardWeekend.events.add(wcGm2Nfc);
        wildCardWeekend.events.add(wcGm3Nfc);

        Team[] afcDivisionalRdTeams = new Team[4];
        Team[] nfcDivisionalRdTeams = new Team[4];
        int wcGm1AfcWinnerSeed = 0;
        int wcGm2AfcWinnerSeed = 0;
        int wcGm3AfcWinnerSeed = 0;
        int wcGm1NfcWinnerSeed = 0;
        int wcGm2NfcWinnerSeed = 0;
        int wcGm3NfcWinnerSeed = 0;
        int[] afcWinningSeedsWC = {wcGm1AfcWinnerSeed, wcGm2AfcWinnerSeed, wcGm3AfcWinnerSeed};
        int[] nfcWinningSeedsWC = {wcGm1NfcWinnerSeed, wcGm2NfcWinnerSeed, wcGm3NfcWinnerSeed};

        System.out.println("WILD CARD WEEKEND:");
        //TODO: Playoff games can't tie. Add logic
        predictGame(wcGm1Afc);
        if(wcGm1Afc.homeScore > wcGm1Afc.awayScore) {
            Team wcGm1AfcWinner = wcGm1Afc.homeTeam;
            wcGm1AfcWinnerSeed = 2;
        } else {
            Team wcGm1AfcWinner = wcGm1Afc.awayTeam; 
            wcGm1AfcWinnerSeed = 7;
        }
        predictGame(wcGm2Afc);
        if(wcGm2Afc.homeScore > wcGm2Afc.awayScore) {
            Team wcGm2AfcWinner = wcGm2Afc.homeTeam;
            wcGm2AfcWinnerSeed = 3;
        } else { 
            Team wcGm2AfcWinner = wcGm2Afc.awayTeam; 
            wcGm2AfcWinnerSeed = 6;
        }
        predictGame(wcGm3Afc);
        if(wcGm3Afc.homeScore > wcGm3Afc.awayScore) {
            Team wcGm3AfcWinner = wcGm3Afc.homeTeam;
            wcGm3AfcWinnerSeed = 4;
        } else { 
            Team wcGm3AfcWinner = wcGm3Afc.awayTeam; 
            wcGm3AfcWinnerSeed = 5;
        }
        predictGame(wcGm1Nfc);
        if(wcGm1Nfc.homeScore > wcGm1Nfc.awayScore) {
            Team wcGm1NfcWinner = wcGm1Nfc.homeTeam;
            wcGm1NfcWinnerSeed = 2;
        } else { 
            Team wcGm1NfcWinner = wcGm1Nfc.awayTeam; 
            wcGm1NfcWinnerSeed = 7;
        }
        predictGame(wcGm2Nfc);
        if(wcGm2Nfc.homeScore > wcGm2Nfc.awayScore) {
            Team wcGm2NfcWinner = wcGm2Nfc.homeTeam;
            wcGm2NfcWinnerSeed = 3;
        } else { 
            Team wcGm2NfcWinner = wcGm2Nfc.awayTeam; 
            wcGm2NfcWinnerSeed = 6;
        }
        predictGame(wcGm3Nfc);
        if(wcGm3Nfc.homeScore > wcGm3Nfc.awayScore) {
            Team wcGm3NfcWinner = wcGm3Nfc.homeTeam;
            wcGm3NfcWinnerSeed = 4;
        } else { 
            Team wcGm3NfcWinner = wcGm3Nfc.awayTeam; 
            wcGm3AfcWinnerSeed = 5;
        }

        // Sort the AFC WC Winners array using a simple sorting algorithm
        for (int i = 0; i < afcWinningSeedsWC.length - 1; i++) {
            for (int j = i + 1; j < afcWinningSeedsWC.length; j++) {
                if (afcWinningSeedsWC[i] > afcWinningSeedsWC[j]) {
                    int temp = afcWinningSeedsWC[i];
                    afcWinningSeedsWC[i] = afcWinningSeedsWC[j];
                    afcWinningSeedsWC[j] = temp;
                }
            }
        }
        
        afcDivisionalRdTeams[0] = tiebrokenAFC[0];
        for(int i = 0; i < afcWinningSeedsWC.length; i++) {
            afcDivisionalRdTeams[i+1] = tiebrokenAFC[afcWinningSeedsWC[i]];
        }

        // Sort the NFC WC Winners array using a simple sorting algorithm
        for (int i = 0; i < nfcWinningSeedsWC.length - 1; i++) {
            for (int j = i + 1; j < nfcWinningSeedsWC.length; j++) {
                if (nfcWinningSeedsWC[i] > nfcWinningSeedsWC[j]) {
                    int temp = nfcWinningSeedsWC[i];
                    nfcWinningSeedsWC[i] = nfcWinningSeedsWC[j];
                    nfcWinningSeedsWC[j] = temp;
                }
            }
        }

        nfcDivisionalRdTeams[0] = tiebrokenNFC[0];
        for(int i = 0; i < nfcWinningSeedsWC.length; i++) {
            nfcDivisionalRdTeams[i+1] = tiebrokenNFC[nfcWinningSeedsWC[i]];
        }

        //DIVISIONAL ROUND

        ArrayList<WeeklyEvent> eventsDiv = new ArrayList<WeeklyEvent>();
        Week divisionalWeekend = new Week(true, 19, eventsDiv);
        //TODO: Add home stadiums to Team.java
        Game divGm1Afc = new Game(afcDivisionalRdTeams[0], afcDivisionalRdTeams[3], "Playoff Venue", LocalDateTime.parse("2024-01-14T16:30"), "Wild Card Weekend Game 3");
        Game divGm2Afc = new Game(afcDivisionalRdTeams[1], afcDivisionalRdTeams[2], "Playoff Venue", LocalDateTime.parse("2024-01-13T20:30"), "Wild Card Weekend Game 2");
        Game divGm1Nfc = new Game(nfcDivisionalRdTeams[1], nfcDivisionalRdTeams[2], "Playoff Venue", LocalDateTime.parse("2024-01-13T16:30"), "Wild Card Weekend Game 1");
        Game divGm2Nfc = new Game(nfcDivisionalRdTeams[0], nfcDivisionalRdTeams[3], "Playoff Venue", LocalDateTime.parse("2024-01-14T20:30"), "Wild Card Weekend Game 4");
        divisionalWeekend.events.add(divGm1Afc);
        divisionalWeekend.events.add(divGm2Afc);
        divisionalWeekend.events.add(divGm1Nfc);
        divisionalWeekend.events.add(divGm2Nfc);

        Team[] afcConfRdTeams = new Team[2];
        Team[] nfcConfRdTeams = new Team[2];
        int divGm1AfcWinnerSeed = 0;
        int divGm2AfcWinnerSeed = 0;
        int divGm1NfcWinnerSeed = 0;
        int divGm2NfcWinnerSeed = 0;
        int[] afcWinningSeedsDiv = {divGm1AfcWinnerSeed, divGm2AfcWinnerSeed};
        int[] nfcWinningSeedsDiv = {divGm1NfcWinnerSeed, divGm2NfcWinnerSeed};

        System.out.println("DIVISIONAL ROUND WEEKEND:");
        //TODO: Playoff games can't tie. Add logic
        predictGame(divGm1Afc);
        if(divGm1Afc.homeScore > divGm1Afc.awayScore) {
            Team divGm1AfcWinner = divGm1Afc.homeTeam;
            divGm1AfcWinnerSeed = 1;
        } else {
            Team divGm1AfcWinner = divGm1Afc.awayTeam; 
            divGm1AfcWinnerSeed = afcWinningSeedsWC[3];
        }
        predictGame(divGm2Afc);
        if(divGm2Afc.homeScore > divGm2Afc.awayScore) {
            Team divGm2AfcWinner = divGm2Afc.homeTeam;
            divGm2AfcWinnerSeed = afcWinningSeedsWC[1];
        } else { 
            Team divGm2AfcWinner = divGm2Afc.awayTeam; 
            divGm2AfcWinnerSeed = afcWinningSeedsWC[2];
        }
        predictGame(divGm1Nfc);
        if(divGm1Nfc.homeScore > divGm1Nfc.awayScore) {
            Team divGm1NfcWinner = divGm1Nfc.homeTeam;
            divGm1NfcWinnerSeed = nfcWinningSeedsWC[1];
        } else { 
            Team divGm1NfcWinner = divGm1Nfc.awayTeam; 
            divGm1NfcWinnerSeed = nfcWinningSeedsWC[2];
        }
        predictGame(divGm2Nfc);
        if(divGm2Nfc.homeScore > divGm2Nfc.awayScore) {
            Team divGm2NfcWinner = divGm2Nfc.homeTeam;
            divGm2NfcWinnerSeed = 1;
        } else { 
            Team divGm2NfcWinner = divGm2Nfc.awayTeam; 
            divGm2NfcWinnerSeed = nfcWinningSeedsWC[3];
        }

        // Sort the AFC WC Winners array using a simple sorting algorithm
        for (int i = 0; i < afcWinningSeedsDiv.length - 1; i++) {
            for (int j = i + 1; j < afcWinningSeedsDiv.length; j++) {
                if (afcWinningSeedsDiv[i] > afcWinningSeedsDiv[j]) {
                    int temp = afcWinningSeedsDiv[i];
                    afcWinningSeedsDiv[i] = afcWinningSeedsDiv[j];
                    afcWinningSeedsDiv[j] = temp;
                }
            }
        }
        
        for(int i = 0; i < afcWinningSeedsDiv.length; i++) {
            afcConfRdTeams[i] = tiebrokenAFC[afcWinningSeedsDiv[i]];
        }

        // Sort the NFC WC Winners array using a simple sorting algorithm
        for (int i = 0; i < nfcWinningSeedsDiv.length - 1; i++) {
            for (int j = i + 1; j < nfcWinningSeedsDiv.length; j++) {
                if (nfcWinningSeedsDiv[i] > nfcWinningSeedsDiv[j]) {
                    int temp = nfcWinningSeedsDiv[i];
                    nfcWinningSeedsDiv[i] = nfcWinningSeedsDiv[j];
                    nfcWinningSeedsDiv[j] = temp;
                }
            }
        }

        for(int i = 0; i < nfcWinningSeedsDiv.length; i++) {
            nfcConfRdTeams[i] = tiebrokenNFC[nfcWinningSeedsDiv[i]];
        }

        //CONFERENCE ROUND

        ArrayList<WeeklyEvent> eventsConf = new ArrayList<WeeklyEvent>();
        Week conferenceWeekend = new Week(true, 19, eventsConf);
        //TODO: Add home stadiums to Team.java
        Game afcChampionship = new Game(afcConfRdTeams[0], afcConfRdTeams[1], "Playoff Venue", LocalDateTime.parse("2024-01-14T16:30"), "Wild Card Weekend Game 3");
        Game nfcChampionship = new Game(nfcConfRdTeams[1], nfcConfRdTeams[2], "Playoff Venue", LocalDateTime.parse("2024-01-13T16:30"), "Wild Card Weekend Game 1");
        divisionalWeekend.events.add(afcChampionship);
        divisionalWeekend.events.add(nfcChampionship);

        Team[] superBowlTeams = new Team[2];
        //int confGm1AfcWinnerSeed = 0;
        //int confGm1NfcWinnerSeed = 0;
        //int[] afcWinningSeedsDiv = {confGm1AfcWinnerSeed, confGm2AfcWinnerSeed};
        //int[] nfcWinningSeedsDiv = {confGm1NfcWinnerSeed, confGm2NfcWinnerSeed};

        System.out.println("CONFERENCE CHAMPIONSHIP WEEKEND:");
        //TODO: Playoff games can't tie. Add logic
        predictGame(afcChampionship);
        if(afcChampionship.homeScore > afcChampionship.awayScore) {
            superBowlTeams[0] = afcChampionship.homeTeam;
            //afcChampionshipWinnerSeed = 1;
        } else {
            superBowlTeams[0] = afcChampionship.awayTeam; 
            //afcChampionshipWinnerSeed = afcWinningSeedsWC[3];
        }
        predictGame(nfcChampionship);
        if(nfcChampionship.homeScore > nfcChampionship.awayScore) {
            superBowlTeams[1] = nfcChampionship.homeTeam;
            //nfcChampionshipWinnerSeed = nfcWinningSeedsWC[1];
        } else { 
            superBowlTeams[1] = nfcChampionship.awayTeam; 
            //nfcChampionshipWinnerSeed = nfcWinningSeedsWC[2];
        }

        //SUPER BOWL 59

        ArrayList<WeeklyEvent> superBowlWeek = new ArrayList<WeeklyEvent>();
        Week superBowlWeekend = new Week(true, 19, superBowlWeek);
        //TODO: Add home stadiums to Team.java
        Game superBowl = new Game(superBowlTeams[0], superBowlTeams[1], "Caesars Superdome", LocalDateTime.parse("2025-02-09T18:30"), "Super Bowl 59");
        divisionalWeekend.events.add(superBowl);

        //int confGm1AfcWinnerSeed = 0;
        //int confGm1NfcWinnerSeed = 0;
        //int[] afcWinningSeedsDiv = {confGm1AfcWinnerSeed, confGm2AfcWinnerSeed};
        //int[] nfcWinningSeedsDiv = {confGm1NfcWinnerSeed, confGm2NfcWinnerSeed};

        System.out.println("SUPER BOWL 59:");
        //TODO: Playoff games can't tie. Add logic
        predictGame(superBowl);
        if(superBowl.homeScore > superBowl.awayScore) {
            System.out.println("The season has concluded! The Champions of Super Bowl 59 are the " + superBowl.homeTeam.name);
            //afcChampionshipWinnerSeed = 1;
        } else {
            System.out.println("The season has concluded! The Champions of Super Bowl 59 are the " + superBowl.homeTeam.name);
            //afcChampionshipWinnerSeed = afcWinningSeedsWC[3];
        }
    }

    //In-Regular Season Menu
    public static void weeklyMenu(Season season, ArrayList<Team> allTeams, int weekNumber, Team[] tiebrokenAfcEast, Team[] tiebrokenAfcNorth, Team[] tiebrokenAfcSouth, Team[] tiebrokenAfcWest, Team[] tiebrokenNfcEast, Team[] tiebrokenNfcNorth, Team[] tiebrokenNfcSouth, Team[] tiebrokenNfcWest, Team[] tiebrokenAFC, Team[] tiebrokenNFC) {
        System.out.println("Week " + weekNumber + " complete! Type 'c' to continue or 'help' for a different command");
        String userInput = StdIn.readString().trim();
        switch(userInput.trim().toLowerCase()) {
            case ("c"):
                System.out.println("Continuing to Week " + weekNumber);
            case ("help"):
                System.out.println("COMMANDS:");
                System.out.println("Standings - shows standings of conferences or divisions based on input");
                System.out.println("Schedule  - shows full season schedule or a specific team's schedule based on input");
            case ("standings"):
                System.out.println("Type: 'Conference' for Conference Standings or 'Division' for Division Standings");
                StdIn.readLine();
                String ui = StdIn.readString().trim().toLowerCase();
                if(ui.equals("conference")) { 
                    printStandingsConf(season, allTeams, weekNumber, tiebrokenAFC, tiebrokenNFC);
                } else if(ui.equals("division")) { 
                    printStandingsDiv(season, allTeams, weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest);
                } else {
                    System.out.println("Invalid input! Enter a command ('help' for commands)");
                }
            case ("schedule"):
                System.out.println("Type 'Team' for Team Schedule or 'Full' for the full NFL Schedule");
                String uInp = StdIn.readString().trim().toLowerCase();
                if(uInp.equals("team")) {
                    System.out.println("Which team? Type the full team name please.");
                    StdIn.readLine();
                    String uInput = StdIn.readLine().trim().toLowerCase();
                    System.out.println(uInput);
                    //TODO: error check input for valid team name and add acronyms/city/name only

                    for(Week w : season.weeks) {
                        for(WeeklyEvent e : w.events) {
                            Game g = (Game)e;
                            if((uInput.equals(g.homeTeam.name.toLowerCase())) || (uInput.equals(g.awayTeam.name.toLowerCase()))) {
                                if(!((g.homeScore == 0) && (g.awayScore == 0))) {
                                    if(g.awayScore > g.homeScore) {
                                        System.out.print(g.awayTeam.name + " " + g.awayScore + ", " + g.homeTeam.name + " " + g.homeScore);
                                        if(g.overtimes > 0) { System.out.print(g.overtimes + "OT"); }
                                        System.out.println();
                                    } else {
                                        System.out.print(g.homeTeam.name + " " + g.homeScore + ", " + g.awayTeam.name + " " + g.awayScore);
                                        if(g.overtimes > 0) { System.out.print(g.overtimes + "OT"); }
                                        System.out.println();
                                    }
                                } else {
                                    System.out.println(g.awayTeam.name + " at " + g.homeTeam.name + ", " + g.dateTime + " " + g.specialTitle);
                                }
                            }
                        }
                    }
                } else if(uInp.equals("full")) { 
                    for(Week w : season.weeks) {
                        System.out.println("WEEK " + w.weekNumber);
                        for(WeeklyEvent e : w.events) {
                            Game g = (Game)e;
                            if(!((g.homeScore == 0) && (g.awayScore == 0))) {
                                if(g.awayScore > g.homeScore) {
                                    System.out.print(g.awayTeam.name + " " + g.awayScore + ", " + g.homeTeam.name + " " + g.homeScore);
                                    if(g.overtimes > 0) { System.out.print(g.overtimes + "OT"); }
                                    System.out.println();
                                } else {
                                    System.out.print(g.homeTeam.name + " " + g.homeScore + ", " + g.awayTeam.name + " " + g.awayScore);
                                    if(g.overtimes > 0) { System.out.print(g.overtimes + "OT"); }
                                    System.out.println();
                                }
                            } else {
                                System.out.println(g.awayTeam.name + " at " + g.homeTeam.name + ", " + g.dateTime + " " + g.specialTitle);
                            }
                        }
                    }
                } else {
                    System.out.println("Invalid input! Enter a command ('help' for commands)");
                    weeklyMenu(season, allTeams, weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest, tiebrokenAFC, tiebrokenNFC);
                }
            default:
                System.out.println("Invalid input! Enter a command ('help for commands)");
                weeklyMenu(season, allTeams, weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest, tiebrokenAFC, tiebrokenNFC);
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