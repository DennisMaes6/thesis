import java.sql.SQLException;
import java.util.List;

import org.javatuples.Triplet;
import org.junit.jupiter.api.Test;

import exceptions.BadInstanceException;
import exceptions.DbControllerException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.ShiftType;



public class FairnessTests {
    
    @Test
    void exampleTest() throws SQLException, DbControllerException{
        System.out.println("Test 1!");
        System.out.println();
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        List<Triplet<Integer, Integer, ShiftType>> assignments = dbc.getAssignments();

        Schedule schedule = new Schedule(data, params, assignments);

        //dbc.putSchedule(schedule);

        System.out.println(schedule.fairnessScore());
        

        /*
        for (Assistant assistant : data.getAssistants()){
            System.out.println(assistant.getIndex());
        } */
  
        
        

    }


    @Test
    void generateScheduleNormal() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException{
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);
        Algorithm algo = new Algorithm(data, params);
        System.out.println("Algorithm instance created. Start running...");

        Schedule schedule = algo.generateSchedule();

  

    
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
