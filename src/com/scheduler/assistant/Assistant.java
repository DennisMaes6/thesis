package com.scheduler.assistant;

import com.scheduler.time.Day;

import java.util.Set;

public class Assistant {

    private String name;
    private AssistantType type;
    private Set<Day> freeDays;


    public Assistant(String name, AssistantType type, Set<Day> freeDays) {
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
}
