package input;

import input.assistant.Assistant;
import input.time.Day;
import input.time.Week;

import java.util.ArrayList;
import java.util.List;

public class InstanceData {

    private final List<Assistant> assistants;
    private final List<Day> days;
    private final List<Week> weeks = new ArrayList<>();

    public InstanceData(List<Assistant> assistants, List<Day> days) {
        this.assistants = assistants;
        this.days = days;
        for (int i = 0; i < days.size() / 7; i++) {
            weeks.add(new Week(days.subList(7*i, 7*(i+1))));
        }
    }


    public List<Assistant> getAssistants() {
        return assistants;
    }

    public List<Day> getDays() {
        return days;
    }

    public List<Week> getWeeks() {
        return this.weeks;
    }


}
