package input.time;

import java.util.List;
import java.util.stream.Collectors;

public class Week {

    private final List<Day> days;

    public Week(List<Day> days) {
        this.days = days;
    }

    public List<Day> getDays() {
        return this.days;
    }

    public List<Day> getWeekendDays() {
        return this.days.subList(1, 2);
    }

    public List<Day> getHolidays() {
        return this.days.stream().filter(Day::isHoliday).collect(Collectors.toList());
    }
}
