import exceptions.DbControllerException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.SeniorAssistantEveningWeek;
import input.shift.SeniorAssistantWeekend;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class ScheduleDecoderTests {


    /*
    @Test
    void testDecoder1() throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        WeeklySchedule ws = new WeeklySchedule(data, params);

        System.out.println(ws.getShifts());


        Assistant testU = data.getAssistants().get(0);
        SeniorAssistantWeekend sawe = new SeniorAssistantWeekend(1);
        SeniorAssistantEveningWeek saew = new SeniorAssistantEveningWeek(1);

        ws.assignShift(testU, saew, 6);


        System.out.println(ws.getScheduleWeeks().get(testU));

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);



        //dbc.putSchedule(decoded);


    }


    private DbController getDBController() throws SQLException {
        return new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/demo.db");
    }

    private InstanceData getInstanceData(DbController dbc) throws SQLException{
        return dbc.getInstanceData();
    }

    private ModelParameters getModelParams(DbController dbc) throws SQLException, DbControllerException {
        return dbc.getModelParameters();
    }
 //*/
}
