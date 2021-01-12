import algorithm.Algorithm;
import algorithm.AlgorithmInput;
import assistant.Assistant;
import assistant.AssistantType;
import schedule.Schedule;
import shifttype.*;
import time.Day;
import time.Week;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        List<Assistant> assistants = Arrays.asList(
                new Assistant("#1", AssistantType.JA, new HashSet<>(
                    Arrays.asList(// WEEK 1
                        new Day(2020, 1, 3), // Friday
                        new Day(2020, 1, 4),
                        new Day(2020, 1, 5),
                        new Day(2020, 1, 6),
                        new Day(2020, 1, 7),
                        new Day(2020, 1, 8),
                        new Day(2020, 1, 9)
                    )
                )),
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
                new Assistant("#12", AssistantType.SA_N, new HashSet<>()),
                new Assistant("#13", AssistantType.SA_N, new HashSet<>()),
                new Assistant("#14", AssistantType.SA_F_N, new HashSet<>()),
                new Assistant("#15", AssistantType.SA_F_N, new HashSet<>())
        );

        List<Week> weeks = Arrays.asList(
            new Week(1,
                Arrays.asList(
                    new Day(2020, 1, 3), // Friday
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
                    new Day(2020, 1, 10), // Friday
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
