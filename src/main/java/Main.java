import exceptions.BadInstanceException;
import exceptions.DbControllerException;
import exceptions.NoSuitableAssistantException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.StandardCopyOption;
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

         /*
        DbController realInstanceController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
        InstanceData realData = realInstanceController.getInstanceData();
        DbController testInstanceController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo_copy.db");

        FileWriter writer = new FileWriter("performance-test.txt");
        writer.write("nb_weeks, min_balance, time, succeeded\n");
        

        //System.out.println(realData.getWeeks().size());

        for(int nbWeeks = realData.getWeeks().size(); nbWeeks < realData.getWeeks().size() + 1; nbWeeks++){
        //for (int nbWeeks = 4; nbWeeks < 28; nbWeeks++) {
            System.out.println( nbWeeks);
            InstanceData testData = new InstanceData(realData.getAssistants(), realData.getDays().subList(0, 7*nbWeeks));
            testInstanceController.putInstance(testData);

            int minBalance = 12;
            boolean solved = true;
            while (solved) {
                testInstanceController.putMinBalance(minBalance);
                long startTime = System.nanoTime();
                URL url = new URL("http://localhost:8080/backend/db-schedule");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                if (con.getResponseCode() > 299) {
                    long endTime = System.nanoTime();
                    System.out.println("ERROR RESPONSE CODE: "+  con.getResponseCode());
                    solved = false;
                    writer.write(String.format("%d,%d,%f,%b\n", nbWeeks, minBalance, (endTime-startTime)/1000000.0, false));
                    System.out.printf("FAILURE... nb weeks: %d, min balance: %d, time: %f\n", nbWeeks, minBalance, (endTime-startTime)/1000000.0);
                } else {
                    long endTime = System.nanoTime();
                    File targetFile = new File(String.format("src/main/resources/schedule-%d-weeks-%d-min-balance.json", nbWeeks, minBalance));
                    java.nio.file.Files.copy(
                            con.getInputStream(),
                            targetFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    writer.write(String.format("%d,%d,%f,%b\n", nbWeeks, minBalance, (endTime-startTime)/1000000.0, true));
                    System.out.printf("SUCCESS! nb weeks: %d, min balance: %d, time: %f\n", nbWeeks, minBalance, (endTime-startTime)/1000000.0);
                }
                writer.flush();
                minBalance++;
                if (minBalance > 21) {
                    solved = false;
                }
            }
        }
        writer.close();
        */
        runAlgo();

    }


    public static void runAlgo() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException{
        
        DbController dbController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
        InstanceData instanceData = dbController.getInstanceData();
        ModelParameters modelParameters = dbController.getModelParameters();
        


        System.out.println("Instance data & model parameters loaded. Start loading algorithm...");

        Algorithm algo = new Algorithm(instanceData, modelParameters);
        System.out.println("Algorithm instance created. Start running...");

        Schedule schedule = algo.generateSchedule();

        System.out.println("Schedule generated. Writing to database...");

        dbController.putSchedule(schedule);
        System.out.println("Writing to database done.");

    } 

    
}
