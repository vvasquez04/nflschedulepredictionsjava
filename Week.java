import java.util.ArrayList;

public class Week {
    public boolean isPlayoffs;
    public int weekNumber;
    public ArrayList<WeeklyEvent> events;

    public static void main(String[] args) {

    }

    public Week(boolean iIsPlayoffs, int iWeekNumber, ArrayList<WeeklyEvent> iEvents) {
        this.isPlayoffs = iIsPlayoffs;
        this.weekNumber = iWeekNumber;
        this.events = iEvents;
    }
}