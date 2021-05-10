import input.InstanceData;
import input.assistant.Assistant;
import input.assistant.AssistantType;
import input.time.Day;
import input.time.Date;

import java.util.*;


public class InstanceGenerator {

    static public InstanceData generateInstance(int nb_weeks, int nb_assistants) {
        List<Day> days = generateDays(nb_weeks);
        List<Assistant> assistants = generateAssistants(nb_assistants, days);
        return new InstanceData(assistants, days);
    }

    static private List<Day> generateDays(int nb_weeks) {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2021);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        List<Day> days = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 7*nb_weeks; i++) {
            Date date = new Date(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
            int ran = random.nextInt(1000);
            days.add(new Day(i, ran < 33, date));
            cal.roll(Calendar.DATE, true);
        }
        return days;
    }

    static private List<Assistant> generateAssistants(int nb_assistants, List<Day> days) {
        List<Assistant> result = new ArrayList<>();
        List<AssistantType> types = generateTypes(nb_assistants);
        Collections.shuffle(types);

        Random random = new Random();
        for (int i = 0; i < nb_assistants; i++) {
            int ran = random.nextInt(100);
            if (ran < 8) {      // pregnancy leave
                int untilDayNb = days.get( random.nextInt(days.size())).getId();
                result.add(new Assistant(i, String.format("test %d", i), types.get(i), getIntsUpTill(untilDayNb)));
            } else {            // normal leaves
                result.add(new Assistant(i, String.format("test %d", i), types.get(i), getRandomFreeDays(days)));
            }
        }

        return result;

    }

    private static Set<Integer> getRandomFreeDays(List<Day> days) {
        Set<Integer> result = new HashSet<>();
        Random random = new Random();

        for (int free_weeks = 0; free_weeks < 2; free_weeks++) {
            int ran = random.nextInt(days.size()/7);
            if (ran == 0) {
                result.addAll(List.of(1, 2, 3));
            } else if (ran == days.size()/7 - 1) {
                result.addAll(List.of(days.size()-1, days.size()-2, days.size()-3, days.size()-4, days.size()-5, days.size()-6));
            } else {
                int startDay = 7*ran - 6;
                for (int i = 0; i < 9; i++) {
                    result.add(startDay + i);
                }
            }
        }

        for (int free_weekends = 0; free_weekends < 2; free_weekends++) {
            int ran = 7*random.nextInt(days.size()/7);
            result.addAll(List.of(ran+2, ran+3));
        }

        return result;
    }


    private static Set<Integer> getIntsUpTill(int untilDayNb) {
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < untilDayNb; i++) {
            result.add(i);
        }
        return result;
    }

    static private List<AssistantType> generateTypes(int nb_assistants) {
        Random random = new Random();
        List<AssistantType> result = new ArrayList<>();
        for (int i = 0; i < nb_assistants; i++) {
            int ran = random.nextInt(100);
            if (ran < 34) {
                result.add(AssistantType.JA);
            } else if (ran < 39) {
                result.add(AssistantType.JA_F);
            } else if (ran < 44) {
                result.add(AssistantType.SA);
            } else if (ran < 46) {
                result.add(AssistantType.SA_F);
            } else if (ran < 82) {
                result.add(AssistantType.SA_NEO);
            } else {
                result.add(AssistantType.SA_F_NEO);
            }
        }
        return result;
    }
}
