package input.assistant;

import input.time.Day;

import java.util.Set;

public class Assistant {

    private final int id;
    private final String name;
    private final AssistantType type;
    private final Set<Day> freeDays;


    public Assistant(int id, String name, AssistantType type, Set<Day> freeDays) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.freeDays = freeDays;
    }

    public AssistantType getType() {
        return type;
    }

    public Set<Day> getFreeDays() {
        return this.freeDays;
    }

    public String getName() {
        return name;
    }
}
