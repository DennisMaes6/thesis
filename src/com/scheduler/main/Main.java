package com.scheduler.main;

import com.scheduler.algorithm.Algorithm;
import com.scheduler.algorithm.AlgorithmInput;
import com.scheduler.assistant.Assistant;
import com.scheduler.assistant.AssistantType;
import com.scheduler.schedule.Schedule;
import com.scheduler.shifttype.*;
import com.scheduler.time.Day;
import com.scheduler.time.Week;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws ParseException {
        List<Assistant> assistants = Arrays.asList(
                new Assistant("#1", AssistantType.JA, new HashSet<>()),
                new Assistant("#2", AssistantType.JA, new HashSet<>()),
                new Assistant("#3", AssistantType.JA, new HashSet<>()),
                new Assistant("#4", AssistantType.JA_F, new HashSet<>()),
                new Assistant("#5", AssistantType.JA_F, new HashSet<>()),
                new Assistant("#6", AssistantType.JA_F, new HashSet<>()),
                new Assistant("#7", AssistantType.SA, new HashSet<>()),
                new Assistant("#8", AssistantType.SA, new HashSet<>()),
                new Assistant("#9", AssistantType.SA, new HashSet<>()),
                new Assistant("#10", AssistantType.SA_F, new HashSet<>()),
                new Assistant("#11", AssistantType.SA_F, new HashSet<>()),
                new Assistant("#12", AssistantType.SA_F, new HashSet<>()),
                new Assistant("#13", AssistantType.SA_N, new HashSet<>()),
                new Assistant("#14", AssistantType.SA_N, new HashSet<>()),
                new Assistant("#15", AssistantType.SA_N, new HashSet<>()),
                new Assistant("#16", AssistantType.SA_F_N, new HashSet<>()),
                new Assistant("#17", AssistantType.SA_F_N, new HashSet<>()),
                new Assistant("#18", AssistantType.SA_F_N, new HashSet<>())
        );

        List<Week> weeks = Arrays.asList(
            new Week(1,
                Arrays.asList(
                    new Day(2020, 1, 3),
                    new Day(2020, 1, 4),
                    new Day(2020, 1, 5),
                    new Day(2020, 1, 6),
                    new Day(2020, 1, 7),
                    new Day(2020, 1, 8),
                    new Day(2020, 1, 9)
                )
            ),
            new Week(2,
                Arrays.asList(
                    new Day(2020, 1, 10),
                    new Day(2020, 1, 11),
                    new Day(2020, 1, 12),
                    new Day(2020, 1, 13),
                    new Day(2020, 1, 14),
                    new Day(2020, 1, 15),
                    new Day(2020, 1, 16)
                )
            )
        );

        Set<ShiftType> shiftTypes = new HashSet<>(
            Arrays.asList(
                new JuniorAssistantEvening(),
                new JuniorAssistantNightWeek(),
                new JuniorAssistantWeekendHoliday(),
                new SeniorAssistantEveningWeek(),
                new SeniorAssistantWeekendHoliday(),
                new Transport(),
                new Call()
            )
        );

        AlgorithmInput input = new AlgorithmInput(assistants, weeks, shiftTypes);
        Algorithm algorithm = new Algorithm(input);

        Schedule schedule = algorithm.generateSchedule();

        System.out.println(schedule.toString());
    }
}
