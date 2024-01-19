import java.time.LocalDateTime;

public class WeeklyEvent {
    public boolean isByeWeek;
    public boolean isEmptyWeek;

    public static void main(String[] args) {

    }

    public WeeklyEvent() {
        this.isByeWeek = false;
        this.isEmptyWeek = false;
    }

    public WeeklyEvent(boolean iIsByeWeek, boolean iIsEmptyWeek) {
        this.isByeWeek = iIsByeWeek;
        this.isEmptyWeek = iIsEmptyWeek;
    }
}