package input.shift.encoded;

import exceptions.ScheduleTooLongException;
import input.shift.FreeShift;
import input.shift.ShiftPeriod;
import input.shift.ShiftType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EncodedSchedule {

    private int duration;
    private List<EncodedShift> encodedShiftList;
    private int nbDays;
    private int balance;
    private final FR free = new FR(getBalance(), getDuration() / 7);

    private int indexLastNotFreeShift = 0;

    public EncodedSchedule(int nbDays, int balance){
        this.nbDays = nbDays;
        this.balance = balance;
        this.encodedShiftList = new ArrayList<>();
        this.duration = 0;
    }
    public EncodedSchedule(int nbDays, int balance, List<EncodedShift> encodedShifts) throws ScheduleTooLongException {
        this.nbDays = nbDays;
        this.balance = balance;
        this.encodedShiftList = new ArrayList<>();
        for(EncodedShift encodedShift : encodedShifts){
            addEncodedShift(encodedShift);
        }
        this.duration = getActualDuration();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<EncodedShift> getEncodedShiftList() {
        return encodedShiftList;
    }
    public List<EncodedShift> getFullAssignmentList(){
        List<EncodedShift> result = new ArrayList<>();
        for(EncodedShift encodedShift : getEncodedShiftList()){
            if(encodedShift.getShift().getType() == ShiftType.FREE){
                result.add(encodedShift);
            } else {
                for(int i = 0; i < encodedShift.getDuration(); i++){
                    result.add(encodedShift);
                }
            }
        }
        int currentSize = result.size();
        for(int i = currentSize ; i < getNbDays(); i++){
            result.add(free);
        }
        return result;
    }

    public void addEncodedShift(EncodedShift encodedShift) throws ScheduleTooLongException {
        int newDuration = getDuration();
        int halfBalance = getBalance() / 2;

        int nbFreeDaysBefore = 0;
        int nbFreeDaysAfter = 0;

        /* if(getEncodedShiftList().isEmpty()){
            if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK){
                newDuration += 7 + halfBalance;
            } else {
                nbFreeDaysBefore = 1;
                newDuration += 3 + halfBalance;
            }
            nbFreeDaysAfter = halfBalance;
        } else { */
            if(encodedShift.getAllowedWeeks().contains(1)){
                if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK){
                    newDuration += 7 + halfBalance;
                } else {
                    nbFreeDaysBefore = 1;
                    newDuration += 3 + halfBalance;
                }
                nbFreeDaysAfter = halfBalance;
            } else if(encodedShift.getAllowedWeeks().contains(2)){
                if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK){
                    newDuration += 14 + halfBalance;
                    nbFreeDaysBefore = 7;
                } else {
                    nbFreeDaysBefore = 8;
                    newDuration += 10 + halfBalance;
                }
                nbFreeDaysAfter = halfBalance;
            } else if(encodedShift.getAllowedWeeks().contains(encodedShift.getNbWeeks() - 2) || encodedShift.getAllowedWeeks().contains(encodedShift.getNbWeeks() - 1)){
                addLastShift(encodedShift);
                return;
            } else {
                if(getEncodedShiftList().isEmpty()){
                    if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK){ // WEEK TOEVOEGEN NA WEEK
                        nbFreeDaysBefore = 14;
                        nbFreeDaysAfter = halfBalance;
                        newDuration += 7 + halfBalance + 14 + 2;
                    } else { // WEEKEND TOEVOEGEN NA WEEK
                        nbFreeDaysBefore = 15;
                        nbFreeDaysAfter = halfBalance;
                        newDuration += 2 + halfBalance + 15 + 3;
                    }

                } else {
                    if (getEncodedShiftList().get(indexLastNotFreeShift).getShift().getPeriod() == ShiftPeriod.WEEK) {
                        if (encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK) { // WEEK TOEVOEGEN NA WEEK
                            nbFreeDaysBefore = halfBalance + 2;
                            nbFreeDaysAfter = halfBalance;
                            newDuration += 7 + getBalance() + 2;
                        } else { // WEEKEND TOEVOEGEN NA WEEK
                            nbFreeDaysBefore = halfBalance + 3;
                            nbFreeDaysAfter = halfBalance;
                            newDuration += 2 + getBalance() + 3;
                        }
                    } else {
                        if (encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK) {  // WEEK TOEVOEGEN NA WEEKEND
                            nbFreeDaysBefore = halfBalance + 6;
                            nbFreeDaysAfter = halfBalance;
                            newDuration += 7 + getBalance() + 6;
                        } else { // WEEKEND TOEVOEGEN NA WEEKEND
                            nbFreeDaysBefore = halfBalance;
                            nbFreeDaysAfter = halfBalance;
                            newDuration += 2 + getBalance() + 1;
                        }
                    }
                }
            //}
        }
        if(newDuration > getNbDays()){
            throw new ScheduleTooLongException("Schedule too long");
        } else {
            for(int i = 0; i < nbFreeDaysBefore; i++){
                getEncodedShiftList().add(free);
            }
            setDuration(newDuration);
            getEncodedShiftList().add(encodedShift);
            indexLastNotFreeShift = getEncodedShiftList().size() - 1;
            for(int i = 0; i < nbFreeDaysAfter; i++){
                getEncodedShiftList().add(free);
            }
        }

    }

    public void removeEncodedShift(int index){
        List<EncodedShift> shiftList = getEncodedShiftList();
        List<EncodedShift> newShiftList = new ArrayList<>();
        int indexCounter = 0;
        int currentIndex = 0;
        for( EncodedShift shift : getEncodedShiftList()){
            if(shift.getShift().getType() != ShiftType.FREE){
                if(indexCounter == index){
                    List<EncodedShift> before = shiftList.subList(0, currentIndex);
                    List<EncodedShift> after = shiftList.subList(currentIndex + shift.getDuration(), shiftList.size());
                    newShiftList.addAll(before);
                    for(int i = 0; i < shift.getDuration(); i++){
                        newShiftList.add(free);
                    }
                    newShiftList.addAll(after);
                    break;
                } else {
                    indexCounter++;
                }
            }
            currentIndex++;
        }
        encodedShiftList = newShiftList;
    }

    public int getActualDuration(){
        return getEncodedShiftList().stream().map(p -> p.getDuration()).mapToInt(Integer::intValue).sum();
        //return getEncodedShiftList().size();
    }

    private void addLastShift(EncodedShift encodedShift) throws ScheduleTooLongException {
        int newDuration = getActualDuration();
        int nbFreeDaysBefore = 0;
        int nbFreeDaysAfter = 0;

        int halfBalance = getBalance() / 2;

        if(encodedShift.getAllowedWeeks().contains(encodedShift.getNbWeeks() - 2)){
            if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK){
                if(getEncodedShiftList().get(indexLastNotFreeShift).getShift().getPeriod() == ShiftPeriod.WEEK){
                    nbFreeDaysBefore = halfBalance + 2;
                    nbFreeDaysAfter = 7;
                    newDuration += halfBalance + 9 + 7;
                } else {
                    nbFreeDaysBefore = halfBalance + 6;
                    nbFreeDaysAfter = 7;
                    newDuration += halfBalance + 13 + 7;
                }

            } else {
                if(getEncodedShiftList().get(indexLastNotFreeShift).getShift().getPeriod() == ShiftPeriod.WEEK){
                    nbFreeDaysBefore = halfBalance + 3;
                    nbFreeDaysAfter = 11;
                    newDuration += halfBalance + 13 + 2;
                } else {
                    nbFreeDaysBefore = halfBalance;
                    nbFreeDaysAfter = 11;
                    newDuration += halfBalance + 11 + 2;
                }
            }

        } else if(encodedShift.getAllowedWeeks().contains(encodedShift.getNbWeeks() - 1)){
            if(encodedShift.getShift().getPeriod() == ShiftPeriod.WEEK){
                if(getEncodedShiftList().get(indexLastNotFreeShift).getShift().getPeriod() == ShiftPeriod.WEEK){
                    nbFreeDaysBefore = halfBalance + 2;
                    nbFreeDaysAfter = 0;
                    newDuration += halfBalance + 7 + 2;
                } else {
                    nbFreeDaysBefore = halfBalance + 6;
                    nbFreeDaysAfter = 0;
                    newDuration += halfBalance + 7 + 6;
                }

            } else {
                if(getEncodedShiftList().get(indexLastNotFreeShift).getShift().getPeriod() == ShiftPeriod.WEEK){
                    nbFreeDaysBefore = halfBalance + 3;
                    nbFreeDaysAfter = 4;
                    newDuration += halfBalance + 2 + 7;
                } else {
                    nbFreeDaysBefore = halfBalance;
                    nbFreeDaysAfter = 4;
                    newDuration += halfBalance + 2 + 4;
                }
            }
        }
        if(newDuration > getNbDays()){
            throw new ScheduleTooLongException("Schedule too long");
        } else {
            for(int i = 0; i < nbFreeDaysBefore; i++){
                getEncodedShiftList().add(free);
            }
            setDuration(newDuration);
            getEncodedShiftList().add(encodedShift);
            indexLastNotFreeShift = getEncodedShiftList().size() - 1;
            for(int i = 0; i < nbFreeDaysAfter; i++){
                getEncodedShiftList().add(free);
            }
        }

    }

    public int getNbDays() {
        return nbDays;
    }

    public int getBalance() {
        return balance;
    }

    public List<EncodedShift> getShiftsWithoutFree(){
        return getEncodedShiftList().stream().filter(p -> !p.equals(free)).collect(Collectors.toList());
    }

    public String toString(){
        int freeCount = 0;
        StringBuilder result = new StringBuilder();
        for(EncodedShift encodedShift : getEncodedShiftList()){
            if(encodedShift.getShift().getType() == ShiftType.FREE){
                freeCount++;
            } else {
                if(freeCount > 0){
                    result.append(freeCount + "x ");
                    result.append(free.toString());
                    result.append("|");
                    freeCount = 0;
                }
                result.append(encodedShift.toString());
                result.append("|");

            }
        }
        if(freeCount > 0){
            result.append(freeCount + "x ");
            result.append(free.toString());
            result.append("|");
        }
        return result.substring(0, result.length() - 1 ).toString();
    }
}
