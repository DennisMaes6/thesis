package input.assistant;

import java.util.Set;

public class Assistant {

    private final int id;
    private final String name;
    private final AssistantType type;
    private final Set<Integer> freeDayIds;


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
}
