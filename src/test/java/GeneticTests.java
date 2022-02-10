import java.sql.SQLException;
import java.util.List;

import org.javatuples.Triplet;
import org.junit.jupiter.api.Test;

import exceptions.BadInstanceException;
import exceptions.DbControllerException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.shift.ShiftType;
import input.time.Day;

public class GeneticTests {
    


    @Test
    void basicFitnessTest() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException{


        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);


        GA ga = new GA(data, params);
        Schedule initSched = ga.generateSchedule();
        dbc.putSchedule(initSched);

        System.out.println("fitness score: " + initSched.getFitnessScore());
    }

    @Test
    void geneticTest() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException{
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);


        GA ga = new GA(data, params);

        int nbIterations = 1;
        int nbBest = 2;
        int nbParents = 2;
        double crossoverRate = 1;
        double mutationRate = 0;
        int nbMutations = 1;

        

        Schedule result = ga.runGenetic(nbIterations, nbBest, nbParents, crossoverRate, mutationRate, nbMutations);
        System.out.println("Result: " + result.toString());
        System.out.println("fitness score genetic: " + result.getFitnessScore());
        dbc.putSchedule(result);
    }


    @Test
    void crossoverTest1() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException{
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        
        GA ga = new GA(data, params);
        Schedule initSched1 = ga.generateSchedule();
        Schedule initSched2 = ga.generateSchedule();

        Schedule result = ga.performCrossover(initSched1, initSched2);

        System.out.println("fitness score1: " + initSched1.getFitnessScore());
        System.out.println("fitness score2: " + initSched2.getFitnessScore());
        System.out.println("fitness score combined: " + result.getFitnessScore());

        dbc.putSchedule(initSched1);

    } 

    @Test
    void mutationTest1() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException{
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);


        GA ga = new GA(data, params);
        Schedule initSched = ga.generateSchedule();
        System.out.println("fitness score: " + initSched.getFitnessScore());
        ga.performMutation(initSched, 1);
        dbc.putSchedule(initSched);

        System.out.println("fitness score: " + initSched.getFitnessScore());
    }

    @Test
    void weekFromDaysTest() throws SQLException, DbControllerException{
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);


        GA ga = new GA(data, params);
        Day testDay = data.getDays().get(9);
        for(Day weekday : ga.getWeekFromDay(testDay).getDays()){
            System.out.println(weekday.getDate());
        }
      
    }

    private DbController getDBController() throws SQLException{
        return new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
    }

    private InstanceData getInstanceData(DbController dbc) throws SQLException{
        return dbc.getInstanceData();
    }
    
    private ModelParameters getModelParams(DbController dbc) throws SQLException, DbControllerException{
        return dbc.getModelParameters();
    }

}
