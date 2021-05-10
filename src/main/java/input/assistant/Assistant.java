package input.assistant;

import input.time.Day;

import java.util.List;
import java.util.Set;

public class Assistant {

    private final int id;
    private final String name;
    private final AssistantType type;
    private final Set<Integer> freeDayIds;
    private int index;


    public Assistant(int id, String name, AssistantType type, Set<Integer> freeDayIds) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.freeDayIds = freeDayIds;
    }

    public AssistantType getType() {
        return type;
    }

    public Set<Integer> getFreeDayIds() {
        return this.freeDayIds;
    }

    public String getName() {
        return name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public int getId() {
        return id;
    }

    public boolean availableOn(List<Day> days) {
        for (Day day : days) {
            if (freeDayIds.contains(day.getId())) {
                return false;
            }
        }
        return true;
    }
}
