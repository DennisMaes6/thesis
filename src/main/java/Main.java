import exceptions.DbControllerException;
import exceptions.NoSuitableAssistantException;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, DbControllerException, NoSuitableAssistantException, IOException {

        String dbPath = System.getProperty("user.home") + "/scheduler/backend/real-instance.db";
        DbController controller = new DbController(dbPath);

        FileWriter writer = new FileWriter("result.csv");
        Algorithm algorithm = new Algorithm(controller.getInstanceData(), controller.getModelParameters());

        for (int i = 0; i < 1000; i++) {
            long startTime = System.nanoTime();
            Schedule schedule = algorithm.generateSchedule();
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            writer.append(String.format("%f, %f \n", schedule.fairnessScore(), duration/1000000.0));
        }
        writer.flush();
        writer.close();

/*
        System.out.println("fairness: " + schedule.fairnessScore());
        System.out.println("balance: " + schedule.balanceScore());
        System.out.println(schedule.toString());

 */

        //controller.putSchedule(schedule);
    }
}
