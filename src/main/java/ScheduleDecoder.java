import exceptions.NotSolvableException;
import input.assistant.Assistant;
import input.shift.Shift;
import input.shift.ShiftPeriod;
import input.shift.ShiftType;
import input.time.Week;

import java.util.List;

public class ScheduleDecoder {


    public ScheduleDecoder(){}


    public static Schedule scheduleFromWeeklySchedule(WeeklySchedule ws) throws NotSolvableException {
        Schedule result = new Schedule(ws.getData(), ws.getParameters());

        for(int i = 0; i < ws.getAssistants().size(); i++){
            Assistant currentAssistant = ws.getAssistants().get(i);
            for(int j = 0; j <  ws.getData().getWeeks().size(); j++){
                Shift shift = ws.getScheduleWeeks().get(currentAssistant).get(j);
                    if(shift.getType() == ShiftType.FREE || shift.getPeriod() == ShiftPeriod.WEEK){
                        for(int n = 0; n < 7; n++){
                            if(!ws.getData().getDays().get(j * 7 + n).isHoliday()){
                                result.schedule[i][j * 7 + n] = shift.getType();
                            } else {
                                result.schedule[i][j * 7 + n] = ShiftType.FREE;
                            }

                        }
                    } else if(shift.getPeriod() == ShiftPeriod.WEEKDAY){
                        for(int n = 0; n < 1; n++){
                            if(!ws.getData().getDays().get(j * 7 + n).isHoliday()){
                                result.schedule[i][j * 7 + n] = shift.getType();
                            } else {
                                result.schedule[i][j * 7 + n] = ShiftType.FREE;
                            }
                        }
                        for(int n = 1; n < 3; n++){
                            result.schedule[i][j * 7 + n] = ShiftType.FREE;
                        }
                        for(int n = 3; n < 7; n++){
                            if(!ws.getData().getDays().get(j * 7 + n).isHoliday()){
                                result.schedule[i][j * 7 + n] = shift.getType();
                            } else {
                                result.schedule[i][j * 7 + n] = ShiftType.FREE;
                            }
                        }
                    } else if(shift.getPeriod() == ShiftPeriod.HOLIDAY){
                        for(int n = 0; n < 7; n++){
                            if(ws.getData().getDays().get(j * 7 + n).isHoliday()){
                                result.schedule[i][j * 7 + n] = shift.getType();
                            } else {
                                result.schedule[i][j * 7 + n] = ShiftType.FREE;
                            }
                        }
                    } else if(shift.getPeriod() == ShiftPeriod.WEEKEND){
                        result.schedule[i][j * 7] = ShiftType.FREE;
                        for(int n =  1; n <  3; n++){
                            if(!ws.getData().getDays().get(j * 7 + n).isHoliday()){
                                result.schedule[i][j * 7 + n] = shift.getType();
                            } else {
                                result.schedule[i][j * 7 + n] = ShiftType.FREE;
                            }
                        }
                        for(int n = 3; n <  7; n++){
                            result.schedule[i][j * 7 + n] = ShiftType.FREE;
                        }
                    } else if(shift.getPeriod() == ShiftPeriod.WEEK_WITHOUT_FRIDAY){
                        for(int n =  0; n < 3; n++){
                            result.schedule[i][j * 7 + n] = ShiftType.FREE;
                        }
                        for(int n = 3; n < 7; n++){
                            if(!ws.getData().getDays().get(j * 7 + n).isHoliday()){
                                result.schedule[i][j * 7 + n] = shift.getType();
                            } else {
                                result.schedule[i][j * 7 + n] = ShiftType.FREE;
                            }
                        }
                    }

                    else if(shift.getPeriod() == ShiftPeriod.FRIDAY){
                        if(!ws.getData().getDays().get(j * 7).isHoliday()){
                            result.schedule[i][j * 7] = shift.getType();
                        } else {
                            result.schedule[i][j * 7] = ShiftType.FREE;
                        }
                        for(int n =  1; n < 7; n++){
                            result.schedule[i][j * 7 + n] = ShiftType.FREE;
                        }
                    }
            }
        }
        return result;
    }


}
