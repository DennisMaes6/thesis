package com.scheduler.main;

import com.scheduler.algorithm.AlgorithmInput;
import com.scheduler.shifttype.*;

import java.util.Arrays;
import java.util.List;

public class Main {

    List<ShiftType> shiftTypes = Arrays.asList(
        new JuniorAssistantEvening(),
        new JuniorAssistantNightWeek(),
        new JuniorAssistantWeekendHoliday(),
        new SeniorAssistantEveningWeek(),
        new SeniorAssistantWeekendHoliday(),
        new Transport(),
        new Call()
    );

    AlgorithmInput input = new AlgorithmInput();

    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
