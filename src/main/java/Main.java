import exceptions.BadInstanceException;
import exceptions.DbControllerException;
import exceptions.NoSuitableAssistantException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import org.apache.commons.cli.CommandLineParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
        //runAlgo();
        //runGenetic(args);
        System.out.println("RUNNING MAIN");
        //runSmallerSubPopulations(args);
        getScheduleFromGenetic();

    }

    public static void getScheduleFromGenetic() throws SQLException, DbControllerException, NotSolvableException {
        DbController dbController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
        InstanceData data = dbController.getInstanceData();
        ModelParameters params = dbController.getModelParameters();

        double covParam = params.getCoverageParameter();
        double balParam = params.getBalanceParameter();
        double fairParam = params.getFairnessParameter();


        double[] top = new double[]{covParam, balParam, fairParam};
        double[] lm = new double[]{covParam * 2, balParam * 2, fairParam};
        double[] mm = new double[]{covParam * 2, balParam, fairParam * 2};
        double[] rm = new double[]{covParam, balParam * 2, fairParam * 2};
        double[] lo = new double[]{covParam * 4, balParam, fairParam};
        double[] mo = new double[]{covParam, balParam * 4, fairParam};
        double[] ro = new double[]{covParam, balParam, fairParam * 4};

        Genetic genetic = new Genetic(data, params);

        int nbIterations = 1;
        int nbParents = 900;
        int nbBest = 300;
        double crossoverRate = 0.8;
        double mutationRate = 0.8;

        System.out.println("NB ITERS: " + nbIterations
                + " | NB PARENTS: "  + nbParents
                + " | NB BEST: " + nbBest);

        WeeklySchedule ws = genetic.runSubPopulationAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate,
                top, lm, mm, rm, lo, mo, ro);

        //Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        dbController.putScheduleWeekly(ws);


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

    public static void runGenetic(String[] args) throws SQLException, DbControllerException, NotSolvableException {

        int nbIterations = Integer.parseInt(args[0]);
        int nbParents = Integer.parseInt(args[1]);
        int nbBest = Integer.parseInt(args[2]);
        double crossoverRate = Double.parseDouble(args[3]);
        double mutationRate = Double.parseDouble(args[4]);
        double weightCoverage = Double.parseDouble(args[5]);
        double weightBalance = Double.parseDouble(args[6]);
        double weightFairness = Double.parseDouble(args[7]);
        double weightStreak = Double.parseDouble(args[8]);
        double weightSpread = Double.parseDouble(args[9]);


        DbController dbController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
        InstanceData data = dbController.getInstanceData();
        ModelParameters params = dbController.getModelParameters();

        Genetic genetic = new Genetic(data, params);

        WeeklySchedule ws = genetic.runAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate,  weightCoverage, weightBalance, weightFairness, weightStreak, weightSpread);


    }


    public static void runSmallerSubPopulations(String[] args) throws SQLException, DbControllerException, NotSolvableException {
        // Volgorde arguments: TOP, LM, MM, RM, LO, MO, RO

        String topS = args[0];
        String lmS = args[1];
        String mmS = args[2];
        String rmS = args[3];
        String loS = args[4];
        String moS = args[5];
        String roS = args[6];

        double[] top = parseInputString(topS);
        double[] lm = parseInputString(lmS);
        double[] mm = parseInputString(mmS);
        double[] rm = parseInputString(rmS);
        double[] lo = parseInputString(loS);
        double[] mo = parseInputString(moS);
        double[] ro = parseInputString(roS);

        DbController dbController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
        InstanceData data = dbController.getInstanceData();
        ModelParameters params = dbController.getModelParameters();

        Genetic genetic = new Genetic(data, params);

        int nbIterations = Integer.parseInt(args[7]);
        int nbParents = Integer.parseInt(args[8]);
        int nbBest = Integer.parseInt(args[9]);
        double crossoverRate = Double.parseDouble(args[10]);
        double mutationRate = Double.parseDouble(args[11]);

        System.out.println("NB ITERS: " + nbIterations
                + " | NB PARENTS: "  + nbParents
                + " | NB BEST: " + nbBest);

        WeeklySchedule ws = genetic.runSubPopulationAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate,
                top, lm, mm, rm, lo, mo, ro);

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        dbController.putSchedule(decoded);

    }

    public static void runSubPopulations(String[] args) throws SQLException, DbControllerException, NotSolvableException {
        // Volgorde arguments: TOP, LM, MM, RM, LO, MO, RO, LLO, LRO, MLO, MRO, RLO, RRO


        String topS = args[0];
        String lmS = args[1];
        String mmS = args[2];
        String rmS = args[3];
        String loS = args[4];
        String moS = args[5];
        String roS = args[6];
        String lloS = args[7];
        String lroS = args[8];
        String mloS = args[9];
        String mroS = args[10];
        String rloS = args[11];
        String rroS = args[12];

        double[] top = parseInputString(topS);
        double[] lm = parseInputString(lmS);
        double[] mm = parseInputString(mmS);
        double[] rm = parseInputString(rmS);
        double[] lo = parseInputString(loS);
        double[] mo = parseInputString(moS);
        double[] ro = parseInputString(roS);
        double[] llo = parseInputString(lloS);
        double[] lro = parseInputString(lroS);
        double[] mlo = parseInputString(mloS);
        double[] mro = parseInputString(mroS);
        double[] rlo = parseInputString(rloS);
        double[] rro = parseInputString(rroS);

        DbController dbController = new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
        InstanceData data = dbController.getInstanceData();
        ModelParameters params = dbController.getModelParameters();

        Genetic genetic = new Genetic(data, params);

        int nbIterations = Integer.parseInt(args[13]);
        int nbParents = Integer.parseInt(args[14]);
        int nbBest = Integer.parseInt(args[15]);
        double crossoverRate = Double.parseDouble(args[16]);
        double mutationRate = Double.parseDouble(args[17]);

        System.out.println("NB ITERS: " + nbIterations
                + " | NB PARENTS: "  + nbParents
                + " | NB BEST: " + nbBest);

        WeeklySchedule ws = genetic.runSubPopulationAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate,
                top, lm, mm, rm, lo, mo, ro, llo, lro, mlo, mro, rlo, rro);

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        dbController.putSchedule(decoded);

    }

    private static double[] parseInputString(String inputString){

        List<String> splitted = List.of(inputString.split("_"));

        System.out.println(splitted);
        double[] result = new double[3];
        result[0] = Double.parseDouble(splitted.get(0));
        result[1] = Double.parseDouble(splitted.get(1));
        result[2] = Double.parseDouble(splitted.get(2));
        return result;
    }

    
}
