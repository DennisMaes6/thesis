import exceptions.DbControllerException;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, DbControllerException {

        String dbPath = System.getProperty("user.home") + "/scheduler/backend/sqlite-database-new.db";
        DbController controller = new DbController(dbPath);

        Algorithm algorithm = new Algorithm(controller.getInstanceData(), controller.getModelParameters());
        Schedule schedule = algorithm.generateSchedule();

        System.out.println(schedule.fairnessScore());
        System.out.println(schedule.balanceScore());
        System.out.println(schedule.toString());
    }
}
