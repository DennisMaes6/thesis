import exceptions.BadInstanceException;
import exceptions.DbControllerException;
import exceptions.NotSolvableException;
import exceptions.ScheduleTooLongException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.*;
import input.shift.encoded.EncodedSchedule;
import input.shift.encoded.EncodedShift;
import input.shift.encoded.janw.A1;
import input.shift.encoded.janw.A2;
import input.shift.encoded.jawe.B1;
import input.shift.encoded.jawe.B2;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

public class EncodeTests {

    private SeniorAssistantWeekend sawe = new SeniorAssistantWeekend(1);
    private SeniorAssistantEveningWeek saew = new SeniorAssistantEveningWeek(1);
    private JuniorAssistantNightWeek janw = new JuniorAssistantNightWeek(1);
    private JuniorAssistantWeekend jawe = new JuniorAssistantWeekend(1);
    private FreeShift free = new FreeShift(1);


    @Test
    void crossoverEncodedScheduleTest() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        GA ga = new GA(data, params);
        Schedule schedule = ga.generateRandomSchedule();


        dbc.putSchedule(schedule);
    }


    @Test
    void generateEncodedScheduleTest() throws SQLException, DbControllerException, NotSolvableException, BadInstanceException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        GA ga = new GA(data, params);
        Schedule schedule = ga.generateRandomSchedule();

        dbc.putSchedule(schedule);
    }


    @Test
    void generateRosterAssistantTest() throws SQLException, DbControllerException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Assistant assistantU = data.getAssistants().get(0);

        GA ga = new GA(data, params);
        EncodedSchedule result = ga.generateRandomIndividualSchedule(assistantU);
        System.out.println(result);
        System.out.println(result.getDuration());
    }

    @Test
    void encodedScheduleTest(){
        int nbDays = 91;
        int balance = 12;
        double workload = 1;
        EncodedSchedule encodedSchedule = new EncodedSchedule(nbDays, balance);
        B2 b2 = new B2(balance, nbDays / 7, workload);
        B1 b1 = new B1(balance, nbDays/ 7, workload);

        A2 a2 = new A2(balance, nbDays / 7, workload);
        A1 a1 = new A1(balance, nbDays/ 7, workload);

        try {
            encodedSchedule.addEncodedShift(a2);
            encodedSchedule.addEncodedShift(a1);
            encodedSchedule.addEncodedShift(a1);
            encodedSchedule.addEncodedShift(a1);
            encodedSchedule.addEncodedShift(a1);
        } catch (ScheduleTooLongException e) {
            e.printStackTrace();
        }

        System.out.println(encodedSchedule.getDuration());
    }

    @Test
    void testDecode1() throws SQLException, DbControllerException, NotSolvableException {
        DbController dbc = getDBController();
        InstanceData data = getInstanceData(dbc);
        ModelParameters params = getModelParams(dbc);

        Map<Assistant, List<EncodedShift>> assistantListMap = new HashMap<>();


        Set<Integer> allWeeksAllowed = new HashSet<>();
        Set<Integer> firstWeekAllowed = new HashSet<>();
        Set<Integer> secondWeekAllowed = new HashSet<>();
        firstWeekAllowed.add(1);
        secondWeekAllowed.add(2);

        int balance = params.getMinBalance();
        int nbWeeks = data.getWeeks().size();

        EncodedShift A1 = new EncodedShift(sawe, allWeeksAllowed, "A1", balance, nbWeeks);
        EncodedShift A2 = new EncodedShift(sawe, firstWeekAllowed, "A2", balance, nbWeeks);
        EncodedShift A3 = new EncodedShift(sawe, secondWeekAllowed, "A3", balance, nbWeeks);

        EncodedShift B1 = new EncodedShift(saew, allWeeksAllowed, "B1", balance, nbWeeks);
        EncodedShift B2 = new EncodedShift(saew, firstWeekAllowed, "B2", balance, nbWeeks);
        EncodedShift B3 = new EncodedShift(saew, secondWeekAllowed, "B3", balance, nbWeeks);

        EncodedShift C1 = new EncodedShift(janw, allWeeksAllowed, "C1", balance, nbWeeks);
        EncodedShift C2 = new EncodedShift(janw, firstWeekAllowed, "C2", balance, nbWeeks);
        EncodedShift C3 = new EncodedShift(janw, secondWeekAllowed, "C3", balance, nbWeeks);

        EncodedShift D1 = new EncodedShift(jawe, allWeeksAllowed, "D1", balance, nbWeeks);
        EncodedShift D2 = new EncodedShift(jawe, firstWeekAllowed, "D2", balance, nbWeeks);
        EncodedShift D3 = new EncodedShift(jawe, secondWeekAllowed, "D3", balance, nbWeeks);


        EncodedShift F = new EncodedShift(free, allWeeksAllowed, "F", balance, nbWeeks);

        Assistant assistantU = data.getAssistants().get(0);
        Assistant assistantX = data.getAssistants().get(1);
        Assistant assistantA = data.getAssistants().get(2);

        List<EncodedShift> assistantUShifts = new ArrayList<>(Arrays.asList(B2, F, F, F, F, F, F, F, F, F, F, A1));
        List<EncodedShift> assistantXShifts = new ArrayList<>(Arrays.asList(A2, A1));
        List<EncodedShift> assistantAShifts = new ArrayList<>(Arrays.asList(D2, C1, C1));

        assistantListMap.put(assistantU, assistantUShifts);
        assistantListMap.put(assistantX, assistantXShifts);
        assistantListMap.put(assistantA, assistantAShifts);

        Schedule schedule = new Schedule(data, params);
        //Schedule result = Decoder.decode(schedule, assistantListMap);

        //System.out.println(result.toString());
        //dbc.putSchedule(result);
    }

    @Test
    void testMerge1(){
        SeniorAssistantEveningWeek saew = new SeniorAssistantEveningWeek(1);
        SeniorAssistantWeekend sawe = new SeniorAssistantWeekend(1);


        Set<Integer> allowedWeeks1 = new HashSet<>();
        Set<Integer> allowedWeeks2 = new HashSet<>();
        allowedWeeks2.add(1);
        EncodedShift encodedShift1 = new EncodedShift(sawe, allowedWeeks2, "B2", 12, 12);
        EncodedShift encodedShift2 = new EncodedShift(saew, allowedWeeks2, "A2", 12, 12);
        EncodedShift encodedShift3 = new EncodedShift(sawe, allowedWeeks1, "B1", 12, 12);
        EncodedShift encodedShift4 = new EncodedShift(saew, allowedWeeks1, "A1", 12, 12);



        ShiftType[] mergedShifts1 = Decoder.mergeDecodedShift(encodedShift2.getShiftTypes(), encodedShift4.getShiftTypes(), Decoder.getExtraFreeDays(encodedShift2.getShift().getPeriod(), encodedShift4.getShift().getPeriod(), encodedShift1.getBalance()), encodedShift1.getBalance());
        ShiftType[] mergedShifts2 = Decoder.mergeDecodedShift(mergedShifts1, encodedShift4.getShiftTypes(), Decoder.getExtraFreeDays(encodedShift4.getShift().getPeriod(), encodedShift4.getShift().getPeriod(), encodedShift1.getBalance()), encodedShift1.getBalance() );
        System.out.println(Arrays.toString(mergedShifts1));
        //System.out.println(Arrays.toString(mergedShifts2));

    }


    @Test
    void extraFreeDaysTest() {
        int result = Decoder.getExtraFreeDays(ShiftPeriod.WEEKEND, ShiftPeriod.WEEK, 12);
        System.out.println("Test 1!");
        System.out.println(result);
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
