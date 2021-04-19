import exceptions.DbControllerException;
import input.InstanceData;
import input.ModelParameters;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import input.assistant.AssistantType;
import input.shift.ShiftType;
import input.time.Day;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DbController {

    Connection conn;

    DbController(String filePath) throws SQLException {
        String connectionString = "jdbc:sqlite:" + filePath;
        this.conn = DriverManager.getConnection(connectionString);
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
        
        return new ModelParameters(minBalance, minBalanceJaev, stmps);
    }

    private List<ShiftTypeModelParameters> getShiftTypeModelParameters() throws SQLException {
        String sql = "SELECT shift_type, shift_workload, max_buffer FROM shift_type_parameters";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);

        List<ShiftTypeModelParameters> result = new ArrayList<>();
        while (rs.next()) {
            ShiftTypeModelParameters stmp = new ShiftTypeModelParameters(
                    ShiftType.valueOf(rs.getString("shift_type")),
                    rs.getFloat("shift_workload"),
                    rs.getInt("max_buffer")

            );
            result.add(stmp);
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
        String sql = "SELECT id, is_holiday FROM day";
        ResultSet rs = this.conn.createStatement().executeQuery(sql);

        List<Day> result = new ArrayList<>();
        while (rs.next())
            result.add(new Day(rs.getInt("id"), rs.getBoolean("is_holiday")));
        return result;
    }


    private Set<Integer> parseIntList(String inputStr) {
        Set<Integer> result = new HashSet<>();
        StringTokenizer st = new StringTokenizer(inputStr, ",");
        while(st.hasMoreTokens())
            result.add(Integer.parseInt(st.nextToken().trim()));
        return result;
    }
}
