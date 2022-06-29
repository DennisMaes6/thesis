import exceptions.DbControllerException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.Shift;
import input.shift.ShiftType;
import input.time.Day;
import input.time.Week;
import org.javatuples.Triplet;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class WeeklyGeneticTest {



        @Test
        void testSubPopulationsHolidays() throws SQLException, DbControllerException, NotSolvableException {
            DbController dbc = getDBController();
            InstanceData data = getInstanceData(dbc);
            ModelParameters params = getModelParams(dbc);



            Genetic genetic = new Genetic(data, params);

            int nbIterations = 100;
            int nbParents = 4000;
            int nbBest = 300;
            double crossoverRate = 0.6;
            double mutationRate = 0.6;

            double[] weightsLO = {8, 0.5, 0.25}; // LINKSONDER
            double[] weightsMO = { 2, 1, 0.25}; // MIDDENONDER
            double[] weightsRO = {2, 0.25, 1}; // RECHTSONDER
            double[] weightsLM = {4, 0.8, 0.25}; // LINKSMIDDEN
            double[] weightsMM = {4, 0.25, 0.5}; // MIDDENMIDDEN
            double[] weightsRM = {2, 0.5, 0.5}; // RECHTSMIDDEN

            double[] weightsT = {2, 0.25, 0.25}; // TOP


            WeeklySchedule ws = genetic.runSubPopulationAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsT, weightsLM, weightsMM, weightsRM, weightsLO, weightsMO, weightsRO);
            //Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

            //dbc.putSchedule(decoded);
            dbc.putScheduleWeekly(ws);


        } /**/
    /*
    @Test
    void testAlgo()  throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Genetic genetic = new Genetic(data, params);

        int nbIterations = 1200;
        int nbParents = 1500;
        int nbBest = 700;
        double crossoverRate = 0.95;
        double mutationRate = 0.95;

        double weightCoverage = 1;
        double weightBalance = 2;
        double weightFairness = 1;

        double weightStreak = 1; // Bestraft schema's met meerdere weken wachtdienst achtereen voor een werknemer
        double weightSpread = 1; // Bestraft schema's met weinig rust tussen verschillende wachtdiensten voor een werknemer

        WeeklySchedule ws = genetic.runAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate,  weightCoverage, weightBalance, weightFairness, weightStreak, weightSpread);

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        //dbc.putSchedule(decoded);
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println(FitnessEval.getSpreadBalance(ws));
        System.out.println(FitnessEval.getStreakBalance(ws));

    }

    @Test
    void testSubPopulations() throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Genetic genetic = new Genetic(data, params);

        int nbIterations = 50;
        int nbParents = 1200;
        int nbBest = 500;
        double crossoverRate = 0.8;
        double mutationRate = 0.6;

        WeeklySchedule ws = genetic.runSubPopulationAlgo(nbIterations, nbParents, nbBest, crossoverRate, mutationRate);
        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        dbc.putSchedule(decoded);


    }

        @Test
        void testHolidays() throws SQLException, DbControllerException, NotSolvableException {
            DbController dbc = getDBController();
            InstanceData data = getInstanceData(dbc);
            ModelParameters params = getModelParams(dbc);

            Genetic genetic = new Genetic(data, params);

            WeeklySchedule ws = genetic.randomWeeklyScedule();

            for(Assistant assistant : ws.getAssistants()) {

                List<Shift> allowedShifts = ws.getShifts().values()
                        .stream()
                        .filter(s -> s.getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                System.out.println(assistant.getType() + ": " + allowedShifts.stream().map(s -> s.getType()).collect(Collectors.toList()));
            }

        }

        @Test
        void testInitialize() throws SQLException, DbControllerException {
            DbController dbc = getDBController();
            InstanceData data = getInstanceData(dbc);
            ModelParameters params = getModelParams(dbc);
            WeeklySchedule ws = new WeeklySchedule(data, params);
        }

        @Test
        void testCovFitness() throws SQLException, DbControllerException {
            DbController dbc = getDBController();
            InstanceData data = getInstanceData(dbc);
            ModelParameters params = getModelParams(dbc);

            List<Triplet<Integer, Integer, ShiftType>> assignments = dbc.getAssignments();

            Schedule schedule = new Schedule(data, params, assignments);

            System.out.println(schedule.getFitnessScore());

        }

        @Test
        void testJAEV() throws SQLException, DbControllerException {
            DbController dbc = getDBController();
            InstanceData data = getInstanceData(dbc);
            ModelParameters params = getModelParams(dbc);

            WeeklySchedule ws = new WeeklySchedule(data, params);
            System.out.println(ws.getShifts().keySet());

        }
        // */
    /*
    @Test
    void testMutation()  throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Genetic genetic = new Genetic(data, params);
        WeeklySchedule ws = genetic.randomWeeklyScedule();

        WeeklySchedule mutated = genetic.mutation(ws);

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(mutated);

        dbc.putSchedule(decoded);

    }

    @Test
    void testCrossover()  throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Genetic genetic = new Genetic(data, params);
        WeeklySchedule ws1 = genetic.randomWeeklyScedule();
        WeeklySchedule ws2 = genetic.randomWeeklyScedule();

        WeeklySchedule ws = genetic.crossover(ws1, ws2);

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        dbc.putSchedule(decoded);

    }

    @Test
    void testRandomInit()  throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Genetic genetic = new Genetic(data, params);
        WeeklySchedule ws = genetic.randomWeeklyScedule();

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        dbc.putSchedule(decoded);

    }


    */
    private DbController getDBController() throws SQLException {
        return new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
    }

    private InstanceData getInstanceData(DbController dbc) throws SQLException{
        return dbc.getInstanceData();
    }

    private ModelParameters getModelParams(DbController dbc) throws SQLException, DbControllerException {
        return dbc.getModelParameters();
    }

}
