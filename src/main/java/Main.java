import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.time.Day;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<Assistant> assistants = new ArrayList<>();

        List<Day> days = new ArrayList<>();

        ModelParameters parameters = new ModelParameters(0, 0, new ArrayList<>());

        InstanceData input = new InstanceData(assistants, days);
        Algorithm algorithm = new Algorithm(input, parameters);

        Schedule schedule = algorithm.generateSchedule();

        System.out.println(schedule.toString());
    }
}
