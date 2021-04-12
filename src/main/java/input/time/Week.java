package input.time;

import java.util.List;
import java.util.stream.Collectors;

public class Week {

    private final List<Day> days;

    public int getWeekNumber() {
        return weekNumber;
    }

    private final int weekNumber;

    public Week(List<Day> days, int weekNumber) {
        this.days = days;
        this.weekNumber = weekNumber;
    }

    public List<Day> getDays() {
        return this.days;
    }

    public List<Day> getWeekendDays() {
        return this.days.subList(1, 3);
    }

    public List<Day> getHolidays() {
        return this.days.stream().filter(Day::isHoliday).collect(Collectors.toList());
    }
}
