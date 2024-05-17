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

        //End of Week Menu
        weeklyMenu(season, allTeams, week.weekNumber, tiebrokenAfcEast, tiebrokenAfcNorth, tiebrokenAfcSouth, tiebrokenAfcWest, tiebrokenNfcEast, tiebrokenNfcNorth, tiebrokenNfcSouth, tiebrokenNfcWest, tiebrokenAFC, tiebrokenNFC);
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
        String userInput = StdIn.readString().trim().toLowerCase();

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