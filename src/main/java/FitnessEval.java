import exceptions.NotSolvableException;
import input.assistant.Assistant;
import input.shift.FreeShift;
import input.shift.Shift;
import input.shift.ShiftType;

import java.util.List;

public class FitnessEval {


    public FitnessEval(){}


    public static double getFitness(WeeklySchedule ws, double weightCov, double weightBalance, double weightFairness, double weightStreak, double weightSpread) throws NotSolvableException {
        double normConstant = weightBalance + weightCov + weightFairness;
        weightCov = weightCov / normConstant;
        weightBalance = weightBalance / normConstant;
        weightFairness = weightFairness / normConstant;

        Schedule decoded = ScheduleDecoder.scheduleFromWeeklySchedule(ws);

        return weightCov * decoded.getFitnessScore() + weightBalance * getBalanceScore(ws, weightStreak, weightSpread) + weightFairness * ws.getFairnessScore()  ;
    }

    public static double getBalanceScore(WeeklySchedule ws, double weightStreak, double weightSpread){
        double normConst = weightStreak + weightSpread;
        weightSpread = weightSpread / normConst;
        weightStreak = weightStreak / normConst;


        return getStreakBalance(ws) * weightStreak  + getSpreadBalance(ws) * weightSpread;
    }

    public static double getSpreadBalance(WeeklySchedule ws){
        // Spread is een maat voor de spreiding tussen shifts.
        // De formule hiervoor is:
        //       MAX(0, 3 - AANTAL WEKEN RUST) * DE GEMIDDELDE WORKLOAD VAN DE 2 SHIFTS
        double result = 0.0;
        for(Assistant assistant : ws.getAssistants()){
            List<Shift> currentShifts = ws.getScheduleWeeks().get(assistant);

            double currentResult = 0.0;
            Shift shift1 = currentShifts.get(0);
            boolean shift1Assigned = false;
            int currentWeeksRest = 0;
            for(Shift shift : currentShifts){
                if(shift.getType() != ShiftType.FREE){
                    if(shift1Assigned){
                        currentResult += Math.max(0, 3 - currentWeeksRest) * ((shift1.getDailyWorkload() + shift.getDailyWorkload()) / 2); // 3 - AANTAL WEKEN RUST * DE GEMIDDELDE WORKLOAD VAN DE 2 SHIFTS
                        currentWeeksRest = 0;
                        shift1 = shift;
                    } else {
                        shift1 = shift;
                        shift1Assigned = true;
                    }
                } else {
                    if(shift1Assigned){
                        currentWeeksRest++;
                    }
                }
            }
            result += currentResult;
        }
        return result;
    }

    public static double getStreakBalance(WeeklySchedule ws){
        // Streak is een maat voor het aantal weken dat na elkaar gewerkt wordt. Dit verhoogt exponentieel.
        // Bv: 2 weken na elkaar is +1, 3 weken na elkaar is +4, 4 weken na elkaar is +9, enz.
        // maw: telkens er 2 of meer weken na elkaar gewerkt wordt: score += (aw - 1)  ^ 2, met aw = aantal weken na elkaar
        double result = 0.0;
        for(Assistant assistant : ws.getAssistants()){
            List<Shift> currentShifts = ws.getScheduleWeeks().get(assistant);

            double currentScore = 0.0;

            int currentStreak = 0;
            boolean firstWeek = true;
            for(Shift shift : currentShifts){
                if(shift.getType() == ShiftType.FREE){
                    currentScore += currentStreak * currentStreak;
                    currentStreak = 0;
                    firstWeek = true;
                } else {
                    if(!firstWeek){
                        currentStreak++;
                    } else {
                        firstWeek = false;
                    }
                }
            }
            currentScore += currentStreak * currentStreak;
            result += currentScore;
        }
        return result;
    }


    public static double avgDaysRest(Schedule schedule, Assistant assistant){
        double totalRestDays = 0;
        double count = 0;

        boolean firstShift = true;
        for( ShiftType st : schedule.schedule[assistant.getIndex()] ){
            if(st == ShiftType.FREE){
                if(!firstShift){
                    totalRestDays += 1;
                }
            } else {
                if(!firstShift){
                    count += 1;
                } else {
                    firstShift = false;
                }
            }
        }
        if (count == 0){
            return 0;
        }
        return totalRestDays / count;
    }

    public static int daysWorked(Schedule schedule, Assistant assistant){
        int count = 0;

        for( ShiftType st : schedule.schedule[assistant.getIndex()] ){
            if(st != ShiftType.FREE){
                count += 1;
            }
        }
        return count;
    }

}
