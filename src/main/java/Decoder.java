import input.assistant.Assistant;
import input.shift.encoded.EncodedSchedule;
import input.shift.encoded.EncodedShift;
import input.shift.ShiftPeriod;
import input.shift.ShiftType;

import java.util.*;

public class Decoder {

    public Decoder(){}


    public static Schedule decode2(Schedule schedule, Map<Assistant, EncodedSchedule> assistantShifts){

        for(Assistant assistant : schedule.getData().getAssistants()){
            ShiftType[] shiftTypes = new ShiftType[0];
            for(EncodedShift encodedShift : assistantShifts.get(assistant).getEncodedShiftList()){

                ShiftType[] currentShiftTypes = encodedShift.getShiftTypes();
                shiftTypes = mergeShifts2(shiftTypes, currentShiftTypes);
                //System.out.println(Arrays.deepToString(shiftTypes));
            }
            if(shiftTypes.length < schedule.getData().getDays().size()){
                shiftTypes = fillupSchedule(shiftTypes, schedule.getData().getDays().size());
            }
            schedule.schedule[assistant.getIndex()] = shiftTypes;
        }
        return schedule;
    }

    private static ShiftType[] mergeShifts2(ShiftType[] decodedShift1, ShiftType[] decodedShift2){
        ShiftType[] result = new ShiftType[decodedShift1.length + decodedShift2.length];
        for(int i = 0; i < decodedShift1.length; i++){
            result[i] = decodedShift1[i];
        }
        for(int i = 0; i < decodedShift2.length; i++){
            result[i + decodedShift1.length] = decodedShift2[i];
        }
        return result;
    }

    public static Schedule decode(Schedule schedule, Map<Assistant, EncodedSchedule> assistantShifts){

        for(Assistant assistant : schedule.getData().getAssistants()){
            ShiftType[] shiftTypes = new ShiftType[0];
            int extraFreeDays = 0;
            boolean firstShift = true;
            int freeDaysCounter = 0;

            ShiftPeriod previousShiftPeriod = ShiftPeriod.WEEK; // maakt niet uit, initiële waarde wordt niet gebruikt.
            for(EncodedShift encodedShift : assistantShifts.get(assistant).getEncodedShiftList()){
                if(encodedShift.getShift().getType() == ShiftType.FREE){
                    freeDaysCounter = Math.min(freeDaysCounter + 1, encodedShift.getBalance());
                    extraFreeDays = 0;
                } else {

                    if(!firstShift){
                        extraFreeDays = getExtraFreeDays(previousShiftPeriod, encodedShift.getShift().getPeriod(), encodedShift.getBalance()) - freeDaysCounter;
                        //extraFreeDays = 0;
                    } else {
                        extraFreeDays = 0;
                        firstShift = false;
                    }
                    previousShiftPeriod = encodedShift.getShift().getPeriod();
                    freeDaysCounter = 0;
                }

                ShiftType[] currentShiftTypes = encodedShift.getShiftTypes();

                shiftTypes = mergeDecodedShift(shiftTypes, currentShiftTypes, extraFreeDays , encodedShift.getBalance());
            }
            if(shiftTypes.length < schedule.getData().getDays().size()){
                shiftTypes = fillupSchedule(shiftTypes, schedule.getData().getDays().size());
            }
            schedule.schedule[assistant.getIndex()] = shiftTypes;
        }

        return schedule;
    }
    public static ShiftType[] fillupSchedule(ShiftType[] inputShiftList, int nbDays){
        ShiftType[] result = new ShiftType[nbDays];

        for(int i = 0; i < inputShiftList.length; i++){
            result[i] = inputShiftList[i];
        }
        for(int i = inputShiftList.length; i < nbDays; i++){
            result[i] = ShiftType.FREE;
        }
        return result;

    }
    public static ShiftType[] mergeDecodedShift(ShiftType[] decodedShift1, ShiftType[] decodedShift2, int extraFreeDays, int balance){
        int length1 = decodedShift1.length;
        int length2 = decodedShift2.length;

        ShiftType[] result = new ShiftType[length1 + length2 + extraFreeDays];
        for(int i = 0; i < length1; i++){
            result[i] = decodedShift1[i];
        }
        int offset1 = 0;
        int offset2 = 0;
        if(extraFreeDays > 0){
            offset1 = -extraFreeDays;
            for(int i = 0; i < extraFreeDays; i++){
                result[i + length1] = ShiftType.FREE;
            }
        } else {
            int remainingFreeDays = Math.abs(extraFreeDays);

            while(remainingFreeDays > 0 & offset1 < (balance / 2)){
                remainingFreeDays--;
                offset1++;
            }
            while(remainingFreeDays > 0 & offset2 <= balance / 2  ){
                remainingFreeDays--;
                offset2++;
            }
        }
        for(int i = 0; i < length2 - offset2; i++){
            result[length1 + i - offset1] = decodedShift2[i + offset2];
        }
        return result;
    }

    public static int getDuration(List<EncodedShift> encodedShifts){
        // Assumptie: bevat max 1 shifttype zoals A2 of A3, en max 1 zoals A4 of A5
        //            en staat op volgorde.
        int result = 0;

        boolean previousWeekend = false;
        for(EncodedShift encodedShift : encodedShifts){
            if(encodedShift.getAllowedWeeks().contains(1) || encodedShift.getAllowedWeeks().contains(2)){
                result += encodedShift.getDuration();
                if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEKEND){
                    previousWeekend = true;
                }
            } else if(encodedShift.getAllowedWeeks().contains(encodedShift.getNbWeeks() - 2)){

            } else if(encodedShift.getAllowedWeeks().contains(encodedShift.getNbWeeks() - 1)){

            } else {
                boolean currentWeekend = (encodedShift.getShift().getPeriod() == ShiftPeriod.WEEKEND);
                if(previousWeekend){
                    if(currentWeekend){

                    } else {

                    }
                } else {
                    if(currentWeekend){

                    } else {

                    }
                }
                previousWeekend = currentWeekend;
            }
        }
        return result;

    }

    public static int getExtraFreeDays(ShiftPeriod period1, ShiftPeriod period2, int balance){
        // Find the extra free days that need to be inserted between 2 shifts in order to make sure
        // that weekend shifts start on saturday and weekshifts on friday
        if(period1 == ShiftPeriod.WEEK && period2 == ShiftPeriod.WEEKEND){
            int multiplier;
            if(balance < 2){
                multiplier = 0;
            } else {
                multiplier = ((balance - 2) / 7) + 1;
            }
            return  (multiplier * 7 + 1) - balance ;
        }

        else if(period1 == ShiftPeriod.WEEKEND && period2 == ShiftPeriod.WEEK) {
            int multiplier;
            if(balance < 5){
                multiplier = 0;
            } else {
                multiplier = ((balance - 5) / 7) + 1;
            }
            return (multiplier * 7 + 4) - balance ;
        } else {
            return ((balance / 7) + 1) * 7 - balance;
        }
    }

}
