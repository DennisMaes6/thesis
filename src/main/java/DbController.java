import exceptions.DbControllerException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import input.assistant.AssistantType;
import input.shift.Shift;
import input.shift.ShiftType;
import input.time.Date;
import input.time.Day;

import java.sql.*;
import java.util.*;

import org.javatuples.Triplet;

import static java.lang.Integer.parseInt;

public class DbController {

    Connection conn;

    DbController(String filePath) throws SQLException {
        System.out.println(filePath);

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String connectionString = "jdbc:sqlite:" + filePath;
        this.conn = DriverManager.getConnection(connectionString);
        conn.setAutoCommit(false);
    }

    public InstanceData getInstanceData() throws SQLException {
        return new InstanceData(getAssistants(), getDays());
    }

    public ModelParameters getModelParameters() throws SQLException, DbControllerException {
        String sql = "SELECT min_balance, min_balance_jaev FROM model_parameters WHERE id = 1";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);
        if (!rs.next())
            throw new DbControllerException("no model parameters found in table");
        
        int minBalance = rs.getInt("min_balance");
        int minBalanceJaev = rs.getInt("min_balance_jaev");
        
        List<ShiftTypeModelParameters> stmps = getShiftTypeModelParameters();

        List<Double> weightParams = getWeightParameters();

        return new ModelParameters(minBalance, minBalanceJaev, stmps, weightParams);
    }

    public void putMinBalance(int newMinBalance) throws SQLException {
        String sql = "UPDATE model_parameters SET min_balance = ? WHERE id = 1";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        pstmt.setInt(1, newMinBalance);
        pstmt.execute();
        this.conn.commit();
    }

    public void putStats(WeeklySchedule ws ) throws SQLException, NotSolvableException {
        String sql = "UPDATE stats SET Coverage = ?, Balance = ?, Fairness = ? WHERE id = 1";



        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        pstmt.setDouble(1, ScheduleDecoder.scheduleFromWeeklySchedule(ws).getFitnessScore());
        pstmt.setDouble(2, FitnessEval.getBalanceScore(ws, 1, 1)); // TODO: aanpassen om streak/spread weight niet te hardcoden
        pstmt.setDouble(3, ws.getFairnessScore());
        pstmt.execute();
        this.conn.commit();
    }




    private List<ShiftTypeModelParameters> getShiftTypeModelParameters() throws SQLException {
        String sql = "SELECT shift_type, shift_workload, shift_coverage, max_buffer FROM shift_type_parameters";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);

        List<ShiftTypeModelParameters> result = new ArrayList<>();
        while (rs.next()) {
            ShiftTypeModelParameters stmp = new ShiftTypeModelParameters(
                    ShiftType.valueOf(rs.getString("shift_type")),
                    rs.getFloat("shift_workload"),
                    rs.getInt("shift_coverage"),
                    rs.getInt("max_buffer")

            );
            result.add(stmp);
        }
        return result;
    }

    private List<Double> getWeightParameters() throws SQLException {
        String sql = "SELECT coverage, balance, fairness FROM weights";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);

        List<Double> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getDouble("coverage"));
            result.add(rs.getDouble("balance"));
            result.add(rs.getDouble("fairness"));
        }
        return result;
    }

    private List<Assistant> getAssistants() throws SQLException {
        String sql = "SELECT id, name, type, free_days FROM assistant";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);

        List<Assistant> result = new ArrayList<>();
        while (rs.next()) {
           result.add(new Assistant(
                   rs.getInt("id"),
                   rs.getString("name"),
                   AssistantType.valueOf(rs.getString("type")),
                   parseIntList(rs.getString("free_days"))
           ));
        }
        return result;
    }

    private List<Day> getDays() throws SQLException {
        String sql = "SELECT id, date, is_holiday FROM day";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);

        List<Day> result = new ArrayList<>();
        while (rs.next()) {
            String datestring = rs.getString("date");
            int day = parseInt(datestring.split("-")[0]);
            int month = parseInt(datestring.split("-")[1]);
            int year = parseInt(datestring.split("-")[2]);
            Date date = new Date(day, month, year);
            result.add(new Day(rs.getInt("id"), rs.getBoolean("is_holiday"), date));
        }
        return result;
    }


    private Set<Integer> parseIntList(String inputStr) {
        Set<Integer> result = new HashSet<>();
        StringTokenizer st = new StringTokenizer(inputStr, ",");
        while(st.hasMoreTokens())
            result.add(parseInt(st.nextToken().trim()));
        return result;
    }

    public void putSchedule(Schedule schedule) throws SQLException {
        putScores(schedule);
        putIndividualSchedules(schedule);
        putAssignments(schedule);
        conn.commit();
    }

    public void putScheduleWeekly(WeeklySchedule ws) throws SQLException, NotSolvableException {
        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);
        putScores(ws);
        putStats(ws);
        putIndividualSchedules(ws);
        putAssignments(decoded);
        conn.commit();
    }

    private void putScores(Schedule schedule) throws SQLException {
        String deleteSql = "DELETE FROM schedule";
        this.conn.createStatement().execute(deleteSql);

        String sql = "INSERT OR REPLACE INTO schedule(id, fairness_score, balance_score, " +
                "jaev_fairness_score, jaev_balance_score) " +
                "VALUES(1, ?, ?, ?, ?)";

        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        pstmt.setDouble(1, schedule.fairnessScore());
        pstmt.setDouble(2, schedule.balanceScore());
        pstmt.setDouble(3, schedule.jaevFairnessScore());
        pstmt.setDouble(4, schedule.jaevBalanceScore());

        pstmt.execute();
    }
    private void putScores(WeeklySchedule ws) throws SQLException, NotSolvableException {
        Schedule schedule = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        String deleteSql = "DELETE FROM schedule";
        this.conn.createStatement().execute(deleteSql);

        String sql = "INSERT OR REPLACE INTO schedule(id, fairness_score, balance_score, " +
                "jaev_fairness_score, jaev_balance_score, Coverage, Balance, Fairness, TotalNbShifts, TotalNbShiftsAssigned) " +
                "VALUES(1, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        pstmt.setDouble(1, schedule.fairnessScore());
        pstmt.setDouble(2, schedule.balanceScore());
        pstmt.setDouble(3, schedule.jaevFairnessScore());
        pstmt.setDouble(4, schedule.jaevBalanceScore());
        pstmt.setDouble(5, schedule.getFitnessScore());
        pstmt.setDouble(6, FitnessEval.getBalanceScore(ws, 1, 1)); // TODO: aanpassen om streak/spread weight niet te hardcoden
        pstmt.setDouble(7, ws.getFairnessScore());
        pstmt.setInt(8, ws.getTotalNbShifts());
        pstmt.setInt(9, ws.getAssignedShiftsTotal());
        pstmt.execute();
    }




    private void putIndividualSchedules(Schedule schedule) throws SQLException {
        schedule.getData().getAssistants().sort(Comparator.comparing(Assistant::getType));
        String deleteSql = "DELETE FROM individual_schedule";
        this.conn.createStatement().execute(deleteSql);

        String sql = "INSERT INTO individual_schedule(assistant_id, workload) VALUES (?, ?)";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        for (Assistant assistant : schedule.getData().getAssistants()) {

            pstmt.setInt(1, assistant.getId());
            pstmt.setDouble(2, schedule.workloadForAssistant(assistant));
            pstmt.addBatch();
        }
        pstmt.executeBatch();

    }


    private void putIndividualSchedules(WeeklySchedule ws) throws SQLException, NotSolvableException {
        ws.getData().getAssistants().sort(Comparator.comparing(Assistant::getType));
        String deleteSql = "DELETE FROM individual_schedule";
        this.conn.createStatement().execute(deleteSql);

        String sql = "INSERT INTO individual_schedule(assistant_id, absolute_workload, relative_workload, days_available, days_worked, days_vacation, avg_days_rest) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);


        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        for(Assistant assistant : ws.getAssistants()) {
            double currentTotalWL = ws.getScheduleWeeks().get(assistant)
                    .stream().
                    filter(s -> s.getType() != ShiftType.FREE)
                    .mapToDouble(Shift::getDailyWorkload)
                    .sum();
            double relativeWL = currentTotalWL / (getInstanceData().getDays().size() - assistant.getFreeDayIds().size()); // Normaliseren vakantiedagen


            pstmt.setInt(1, assistant.getId());
            pstmt.setDouble(2, currentTotalWL);
            pstmt.setDouble(3, relativeWL);
            pstmt.setInt(4, ws.getData().getDays().size() - assistant.getFreeDayIds().size());
            pstmt.setInt(5, FitnessEval.daysWorked(decoded, assistant));
            pstmt.setInt(6, assistant.getFreeDayIds().size());
            pstmt.setDouble(7, FitnessEval.avgDaysRest(decoded, assistant ));


            pstmt.addBatch();
        }


        pstmt.executeBatch();

    }


    private void putAssignments(Schedule schedule) throws SQLException {
        String deleteSql = "DELETE FROM assignment";
        this.conn.createStatement().execute(deleteSql);

        String sql = "INSERT INTO assignment(assistant_id, day_nb, shift_type) VALUES (?, ?, ?)";

        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        for (Assistant assistant : schedule.getData().getAssistants()) {
            for (Day day : schedule.getData().getDays()) {
                pstmt.setInt(1, assistant.getId());
                pstmt.setInt(2, day.getId());
                pstmt.setString(3, schedule.assignmentOn(assistant, day).toString());
                pstmt.addBatch();
            }
        }
        pstmt.executeBatch();
    }

    public List<Triplet<Integer, Integer, ShiftType>> getAssignments() throws SQLException{
        String sql = "SELECT assistant_id, day_nb, shift_type FROM assignment";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);
        System.out.println(rs);
        
        List<Triplet<Integer, Integer, ShiftType>> result = new ArrayList<>();

        while (rs.next()) {
            int currentAssistantID = rs.getInt("assistant_id");
            int currentDayNb = rs.getInt("day_nb");
            ShiftType currentST = ShiftType.valueOf(rs.getString("shift_type"));
            Triplet<Integer, Integer, ShiftType> currentEntry = Triplet.with(currentAssistantID, currentDayNb, currentST);
            result.add(currentEntry);
        }
       return result;  
    } 

    public void putInstance(InstanceData data) throws SQLException {
        this.putDays(data.getDays());
        this.putAssistants(data.getAssistants());
        conn.commit();
    }

    private void putDays(List<Day> days) throws SQLException {
       String deleteSql = "DELETE FROM day";
       this.conn.createStatement().execute(deleteSql);

       String sql = "INSERT INTO day(id, date, is_holiday) VALUES (?, ?, ?)";
       PreparedStatement pstmt = this.conn.prepareStatement(sql);
       for (Day day : days) {
           pstmt.setInt(1, day.getId());
           pstmt.setString(2, day.getDate().toString());
           pstmt.setBoolean(3, day.isHoliday());
           pstmt.addBatch();
       }
       pstmt.executeBatch();
    }



    private void putAssistants(List<Assistant> assistants) throws SQLException {
        String deleteSql = "DELETE FROM assistant";
        this.conn.createStatement().execute(deleteSql);

        String sql = "INSERT INTO assistant(id, name, type, free_days) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        for (Assistant assistant : assistants) {
            pstmt.setInt(1, assistant.getId());
            pstmt.setString(2, assistant.getName());
            pstmt.setString(3, assistant.getType().toString());
            pstmt.setString(4, assistant.getFreeDaysAsString());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
    }

    private void putMinBalanceScore(int minBalance) throws SQLException {
        String sql = "UPDATE model_parameters SET min_balance = ? WHERE id = 1";
        PreparedStatement pstmt = this.conn.prepareStatement(sql);
        pstmt.setInt(1, minBalance);
        pstmt.execute();
        this.conn.commit();
    }
}
