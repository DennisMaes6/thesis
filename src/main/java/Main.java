import exceptions.BadInstanceException;
import exceptions.DbControllerException;
import exceptions.NoSuitableAssistantException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, DbControllerException, NoSuitableAssistantException, IOException, NotSolvableException, BadInstanceException {
/*
        InstanceGenerator.generateInstance(26, 53);
        String dbPath = System.getProperty("user.home") + "/scheduler/backend/real-instance.db";
        DbController controller = new DbController(dbPath);
        FileWriter writer = new FileWriter("result.csv");
        writer.append("nb_assistants, fairness, duration\n");
        for (int i = 40; i < 70; i++) {
            List<Double> durations = new ArrayList<>();
            List<Double> fairnessScores = new ArrayList<>();
            int count = 0;
           while (count < 10) {
                System.out.println("start!");
                try {
                    Algorithm algorithm = new Algorithm(InstanceGenerator.generateInstance(26, i), controller.getModelParameters());
                    long startTime = System.nanoTime();
                    Schedule schedule = algorithm.generateSchedule();
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    durations.add(duration/1000000.0);
                    fairnessScores.add(schedule.fairnessScore());
                    count++;
                } catch (NotSolvableException e) {
                    System.out.println("not solvable");
                }

            }
            System.out.println(fairnessScores.toString());
            writer.append(String.format("%d, %f, %f \n", i, fairnessScores.stream().mapToDouble(a -> a).average().orElse(0.0), durations.stream().mapToDouble(a -> a).average().orElse(0.0)));
            System.out.println("result!");
        }
        writer.flush();
        writer.close();
*/
        /*


        FileWriter writer = new FileWriter("result.csv");
        for (int i = 0; i < 1000; i++) {
            try {
                long startTime = System.nanoTime();
                Schedule schedule = algorithm.generateSchedule();
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                writer.append(String.format("%f, %f \n", schedule.fairnessScore(), duration/1000000.0));
                System.out.println("result!");
            } catch (NotSolvableException e) {
                e.printStackTrace();
            }

        }
        writer.flush();
        writer.close();

         */
        String dbPath = System.getProperty("user.home") + "/scheduler/backend/real-instance-second-semester.db";
        DbController controller = new DbController(dbPath);
        ModelParameters params = controller.getModelParameters();
        InstanceData data = controller.getInstanceData();
        Algorithm algorithm = new Algorithm(data, params);
        Schedule schedule = algorithm.generateSchedule();
        controller.putSchedule(schedule);
    }
}
