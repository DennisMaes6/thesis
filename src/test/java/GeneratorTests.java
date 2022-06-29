import input.InstanceData;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GeneratorTests {


/*
    @Test
    void testInstanceGenerator() throws SQLException {
        int nbWeeks = 26;
        int nbAssistants = 36;

        InstanceData data = InstanceGenerator.generateInstance(nbWeeks, nbAssistants);
        DbController dbc = getDBController();
        dbc.putInstance(data);

    }

    @Test
    void testInstanceGeneratorMultipleWeeks() throws SQLException {
        int nbAssistants = 36;

        for(int i = 12; i < 26; i += 4){
          for(int j = 1; j < 4; j++){
              InstanceData data = InstanceGenerator.generateInstance(i, nbAssistants);
              String dbName = "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/experiments/dbs/" +  i + "weeks_" + j + ".db";
              DbController dbc = getDBController(dbName);
              dbc.putInstance(data);
          }
        }


    }

    */




    private DbController getDBController() throws SQLException {
        return new DbController(System.getProperty("user.home") + "/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/scheduler/backend/generator_test3.db");
    }


    private DbController getDBController(String dbName) throws SQLException {
        return new DbController(System.getProperty("user.home") + dbName);
    }
}
