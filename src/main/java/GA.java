import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import exceptions.*;
import input.InstanceData;
import input.ModelParameters;
import input.ShiftTypeModelParameters;
import input.assistant.Assistant;
import input.shift.*;
import input.shift.encoded.EncodedSchedule;
import input.shift.encoded.EncodedShift;
import input.shift.encoded.call.*;
import input.shift.encoded.janw.*;
import input.shift.encoded.jawe.*;
import input.shift.encoded.saew.*;
import input.shift.encoded.sawe.*;
import input.shift.encoded.tpwe.*;
import input.time.Day;
import input.time.Week;

public class GA {
    
    private final InstanceData data;
    private final ModelParameters parameters;
    private final Map<String, Integer> log = new HashMap<>();
    private Random random;

    List<EncodedShift> weekOneShifts = new ArrayList<>();
    List<EncodedShift> weekTwoShifts = new ArrayList<>();
    List<EncodedShift> weekEndMinusTwoShifts = new ArrayList<>();
    List<EncodedShift> weekEndMinusOneShifts = new ArrayList<>();
    List<EncodedShift> restEncodedShifts = new ArrayList<>();

    List<Shift> shifts = new ArrayList<>();

    public GA(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
        this.random = new Random(1111);
        initializeEncodedShifts();
    }

    private void initializeEncodedShifts(){
        for (ShiftTypeModelParameters stp : parameters.getShiftTypeModelParameters()){
            switch(stp.getShiftType()){
                case JANW -> initializeJANWShifts(stp.getWorkload(), stp.getCoverage());
                case JAWE -> initializeJAWEShifts(stp.getWorkload(), stp.getCoverage());
                case SAEW -> initializeSAEWShifts(stp.getWorkload(), stp.getCoverage());
                case SAWE -> initializeSAWEShifts(stp.getWorkload(), stp.getCoverage());
                case TPWE -> initializeTPWEShifts(stp.getWorkload(), stp.getCoverage());
                case CALL -> initializeCALLShifts(stp.getWorkload(), stp.getCoverage());
            }
        }
    }

    private void initializeJANWShifts(double workload, int coverage){

        JuniorAssistantNightWeek janw = new JuniorAssistantNightWeek(workload);
        A1 a1 = new A1(parameters.getMinBalance(), data.getWeeks().size(), workload);
        A2 a2 = new A2(parameters.getMinBalance(), data.getWeeks().size(), workload);
        A3 a3 = new A3(parameters.getMinBalance(), data.getWeeks().size(), workload);
        A4 a4 = new A4(parameters.getMinBalance(), data.getWeeks().size(), workload);
        A5 a5 = new A5(parameters.getMinBalance(), data.getWeeks().size(), workload);
        for(Day day : data.getDays()){
            janw.setCoverage(day, coverage);
            a1.getShift().setCoverage(day, coverage);
            a2.getShift().setCoverage(day, coverage);
            a3.getShift().setCoverage(day, coverage);
            a4.getShift().setCoverage(day, coverage);
            a5.getShift().setCoverage(day, coverage);
        }
        shifts.add(janw);
        restEncodedShifts.add(a1);
        weekOneShifts.add(a2);
        weekTwoShifts.add(a3);
        weekEndMinusTwoShifts.add(a4);
        weekEndMinusOneShifts.add(a5);
    }

    private void initializeJAWEShifts(double workload, int coverage){
        JuniorAssistantWeekend jawe = new JuniorAssistantWeekend(workload);
        B1 b1 = new B1(parameters.getMinBalance(), data.getWeeks().size(), workload);
        B2 b2 = new B2(parameters.getMinBalance(), data.getWeeks().size(), workload);
        B3 b3 = new B3(parameters.getMinBalance(), data.getWeeks().size(), workload);
        B4 b4 = new B4(parameters.getMinBalance(), data.getWeeks().size(), workload);
        B5 b5 = new B5(parameters.getMinBalance(), data.getWeeks().size(), workload);

        for(Day day : data.getDays()){
            jawe.setCoverage(day, coverage);
            b1.getShift().setCoverage(day, coverage);
            b2.getShift().setCoverage(day, coverage);
            b3.getShift().setCoverage(day, coverage);
            b4.getShift().setCoverage(day, coverage);
            b5.getShift().setCoverage(day, coverage);
        }
        shifts.add(jawe);
        restEncodedShifts.add(b1);
        weekOneShifts.add(b2);
        weekTwoShifts.add(b3);
        weekEndMinusTwoShifts.add(b4);
        weekEndMinusOneShifts.add(b5);
    }

    private void initializeSAEWShifts(double workload, int coverage){
        SeniorAssistantEveningWeek saew = new SeniorAssistantEveningWeek(workload);
        C1 c1 = new C1(parameters.getMinBalance(), data.getWeeks().size(), workload);
        C2 c2 = new C2(parameters.getMinBalance(), data.getWeeks().size(), workload);
        C3 c3 = new C3(parameters.getMinBalance(), data.getWeeks().size(), workload);
        C4 c4 = new C4(parameters.getMinBalance(), data.getWeeks().size(), workload);
        C5 c5 = new C5(parameters.getMinBalance(), data.getWeeks().size(), workload);

        for(Day day : data.getDays()){
            saew.setCoverage(day, coverage);
            c1.getShift().setCoverage(day, coverage);
            c2.getShift().setCoverage(day, coverage);
            c3.getShift().setCoverage(day, coverage);
            c4.getShift().setCoverage(day, coverage);
            c5.getShift().setCoverage(day, coverage);
        }
        shifts.add(saew);
        restEncodedShifts.add(c1);
        weekOneShifts.add(c2);
        weekTwoShifts.add(c3);
        weekEndMinusTwoShifts.add(c4);
        weekEndMinusOneShifts.add(c5);
    }

    private void initializeSAWEShifts(double workload, int coverage){
        SeniorAssistantWeekend sawe = new SeniorAssistantWeekend(workload);
        D1 d1 = new D1(parameters.getMinBalance(), data.getWeeks().size(), workload);
        D2 d2 = new D2(parameters.getMinBalance(), data.getWeeks().size(), workload);
        D3 d3 = new D3(parameters.getMinBalance(), data.getWeeks().size(), workload);
        D4 d4 = new D4(parameters.getMinBalance(), data.getWeeks().size(), workload);
        D5 d5 = new D5(parameters.getMinBalance(), data.getWeeks().size(), workload);

        for(Day day : data.getDays()){
            sawe.setCoverage(day, coverage);
            d1.getShift().setCoverage(day, coverage);
            d2.getShift().setCoverage(day, coverage);
            d3.getShift().setCoverage(day, coverage);
            d4.getShift().setCoverage(day, coverage);
            d5.getShift().setCoverage(day, coverage);
        }
        shifts.add(sawe);
        restEncodedShifts.add(d1);
        weekOneShifts.add(d2);
        weekTwoShifts.add(d3);
        weekEndMinusTwoShifts.add(d4);
        weekEndMinusOneShifts.add(d5);
    }

    private void initializeTPWEShifts(double workload, int coverage){
        TransportWeekend tpwe = new TransportWeekend(workload);
        E1 e1 = new E1(parameters.getMinBalance(), data.getWeeks().size(), workload);
        E2 e2 = new E2(parameters.getMinBalance(), data.getWeeks().size(), workload);
        E3 e3 = new E3(parameters.getMinBalance(), data.getWeeks().size(), workload);
        E4 e4 = new E4(parameters.getMinBalance(), data.getWeeks().size(), workload);
        E5 e5 = new E5(parameters.getMinBalance(), data.getWeeks().size(), workload);

        for(Day day : data.getDays()){
            tpwe.setCoverage(day, coverage);
            e1.getShift().setCoverage(day, coverage);
            e2.getShift().setCoverage(day, coverage);
            e3.getShift().setCoverage(day, coverage);
            e4.getShift().setCoverage(day, coverage);
            e5.getShift().setCoverage(day, coverage);
        }
        shifts.add(tpwe);
        restEncodedShifts.add(e1);
        weekOneShifts.add(e2);
        weekTwoShifts.add(e3);
        weekEndMinusTwoShifts.add(e4);
        weekEndMinusOneShifts.add(e5);
    }

    private void initializeCALLShifts(double workload, int coverage){
        Call call = new Call(workload);
        F1 f1 = new F1(parameters.getMinBalance(), data.getWeeks().size(), workload);
        F2 f2 = new F2(parameters.getMinBalance(), data.getWeeks().size(), workload);
        F3 f3 = new F3(parameters.getMinBalance(), data.getWeeks().size(), workload);
        F4 f4 = new F4(parameters.getMinBalance(), data.getWeeks().size(), workload);
        F5 f5 = new F5(parameters.getMinBalance(), data.getWeeks().size(), workload);

        for(Day day : data.getDays()){
            call.setCoverage(day, coverage);
            f1.getShift().setCoverage(day, coverage);
            f2.getShift().setCoverage(day, coverage);
            f3.getShift().setCoverage(day, coverage);
            f4.getShift().setCoverage(day, coverage);
            f5.getShift().setCoverage(day, coverage);
        }
        shifts.add(call);
        restEncodedShifts.add(f1);
        weekOneShifts.add(f2);
        weekTwoShifts.add(f3);
        weekEndMinusTwoShifts.add(f4);
        weekEndMinusOneShifts.add(f5);
    }


    public EncodedSchedule generateRandomIndividualSchedule(Assistant assistant){

        EncodedSchedule result = new EncodedSchedule(data.getDays().size(), parameters.getMinBalance());

        List<EncodedShift> firstShifts;
        List<EncodedShift> lastShifts;
        List<EncodedShift> restShifts = restEncodedShifts
                .stream()
                .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                .collect(Collectors.toList());

        if(random.nextBoolean()){
            firstShifts = weekOneShifts
                    .stream()
                    .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                    .collect(Collectors.toList());
        } else {
            firstShifts = weekTwoShifts
                    .stream()
                    .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                    .collect(Collectors.toList());
        }
        if(random.nextBoolean()){
            lastShifts = weekEndMinusOneShifts
                    .stream()
                    .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                    .collect(Collectors.toList());
        } else {
            lastShifts = weekEndMinusTwoShifts
                    .stream()
                    .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                    .collect(Collectors.toList());
        }

        while(true){
            EncodedShift nextShift;
            if(result.getEncodedShiftList().isEmpty()){
                nextShift = firstShifts.get(random.nextInt(firstShifts.size()));
            } else {
                nextShift = restShifts.get(random.nextInt(restShifts.size()));
            }
            try {
                result.addEncodedShift(nextShift);
            } catch (ScheduleTooLongException e) {
                break;
            }
        }
        EncodedShift lastShift = lastShifts.get(random.nextInt(lastShifts.size()));
        try {
            result.addEncodedShift(lastShift);
        } catch (ScheduleTooLongException ignored) {}

        try {
            EncodedSchedule mutated = mutatedSchedule(result, assistant);
            System.out.println(result);
            System.out.println(mutated + " MUTATED");
        } catch (ScheduleTooLongException e) {
            e.printStackTrace();
        }

        return result;
    }

    public EncodedSchedule crossoverSchedule(EncodedSchedule e1, EncodedSchedule e2) throws ScheduleTooLongException {

        List<EncodedShift> newShifts = new ArrayList<>();
        int size1 = e1.getShiftsWithoutFree().size();
        int size2 = e2.getShiftsWithoutFree().size();

        int resultSize = Math.min(size1, size2);

        if(random.nextBoolean()){
            newShifts.add(e1.getShiftsWithoutFree().get(0));
        } else {
            newShifts.add(e2.getShiftsWithoutFree().get(0));
        }

        for(int i = 1; i < resultSize - 1; i++){
            if(random.nextBoolean()){
                newShifts.add(e1.getShiftsWithoutFree().get(i));
            } else {
                newShifts.add(e2.getShiftsWithoutFree().get(i));
            }
        }

        if(random.nextBoolean()){
            newShifts.add(e1.getShiftsWithoutFree().get(size1 - 1));
        } else {
            newShifts.add(e1.getShiftsWithoutFree().get(size2 - 1));
        }

        return new EncodedSchedule(e1.getNbDays(), e1.getBalance(), newShifts);
    }

    public EncodedSchedule mutatedSchedule(EncodedSchedule e1, Assistant assistant) throws ScheduleTooLongException {
        List<EncodedShift> shiftList = e1.getShiftsWithoutFree();
        List<EncodedShift> mutatedShiftList = new ArrayList<EncodedShift>(shiftList);
        int indexToReplace = random.nextInt(shiftList.size());


        List<EncodedShift> newShifts;

        if(indexToReplace == 0){
            //mutatedShiftList = mutatedShiftList.subList(1, mutatedShiftList.size() );
            try {
                newShifts = weekOneShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                EncodedShift nShift = newShifts.get(random.nextInt(newShifts.size()));

                mutatedShiftList.set(0, nShift);
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);

                return result;
            } catch (ScheduleTooLongException ignored) {}
            try {
                newShifts = weekTwoShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                EncodedShift nShift = newShifts.get(random.nextInt(newShifts.size()));
                mutatedShiftList.set(0, nShift);
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);
                return result;
            } catch (ScheduleTooLongException ignored) {}
            try {
                newShifts = restEncodedShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                mutatedShiftList.set(0, newShifts.get(random.nextInt(newShifts.size())));
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);
                return result;
            } catch (ScheduleTooLongException ignored) {
                System.out.println("Failed");
                return e1;
            }
        } else if(indexToReplace == shiftList.size() - 1){
            try {
                newShifts = weekEndMinusTwoShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                //mutatedShiftList.add(newShifts.get(random.nextInt(newShifts.size())));
                EncodedShift nShift = newShifts.get(random.nextInt(newShifts.size()));
                mutatedShiftList.set(mutatedShiftList.size() - 1, nShift);
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);
                return result;
            } catch (ScheduleTooLongException ignored) {}
            try {
                newShifts = weekEndMinusOneShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                EncodedShift nShift = newShifts.get(random.nextInt(newShifts.size()));
                mutatedShiftList.set(mutatedShiftList.size() - 1, nShift);
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);
                return result;
            } catch (ScheduleTooLongException ignored) {}
            try {
                newShifts = restEncodedShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());
                EncodedShift nShift = newShifts.get(random.nextInt(newShifts.size()));
                mutatedShiftList.set(mutatedShiftList.size() - 1, nShift);
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);
                return result;
            } catch (ScheduleTooLongException ignored) {
                System.out.println("Failed");
                return e1;
            }
        } else {
            try {
                newShifts = restEncodedShifts
                        .stream()
                        .filter(p -> p.getShift().getAllowedAssistantTypes().contains(assistant.getType()))
                        .collect(Collectors.toList());

                EncodedShift nShift = newShifts.get(random.nextInt(newShifts.size()));

                mutatedShiftList.set(indexToReplace, nShift);
                EncodedSchedule result = new EncodedSchedule(e1.getNbDays(), e1.getBalance(), mutatedShiftList);
                return result;
            } catch (ScheduleTooLongException ignored) {
                System.out.println("Failed");
                return e1;
            }
        }

    } // TODO: klopt nog niet


    private boolean assistantAllowed(Assistant assistant, EncodedShift encodedShift){
        return encodedShift.getShift().getAllowedAssistantTypes().contains(assistant.getType());
    }


    public Schedule generateRandomSchedule() throws NotSolvableException {
        Schedule schedule = new Schedule(data, parameters);
        Map<Assistant, EncodedSchedule> assistantListMap = new HashMap<>();

        for(Assistant assistant : data.getAssistants()){
            EncodedSchedule currentSchedule = generateRandomIndividualSchedule(assistant);
            assistantListMap.put(assistant, currentSchedule);
            System.out.println(currentSchedule);
        }

        Schedule result = Decoder.decode2(schedule, assistantListMap);
        return result;
    }



    public Schedule runGenetic(int nbIterations, int nbBest, int nbParents, double crossoverRate, double mutationRate, int nbMutations) throws NotSolvableException, BadInstanceException{
        List<Schedule> parents = new ArrayList<>();
        for(int i = 0; i < nbParents; i++){
            Schedule newParent = generateSchedule();
            parents.add(newParent);
            System.out.println(newParent.toString());
            System.out.println("----------------------------------------------------------------------------------------------");
        }
        
        List<Schedule> mutatedParents = new ArrayList<>(parents); 
        for(int i = 0; i < nbIterations; i++){
            List<Schedule> bestParents = selectBest(mutatedParents, nbBest);
            List<Schedule> crossoverParents = generateChildren(bestParents, crossoverRate, nbParents);
            mutatedParents = generateMutatedChildren(crossoverParents, mutationRate, nbMutations);
        }
        return selectBest(mutatedParents, 1).get(0);
    }

    public List<Schedule> generateChildren(List<Schedule> parents, double crossoverRate, int nbChildren) throws NotSolvableException{
        // Ervanuitgaande dat parents al gesorteerd is, wat normaal zo is.
        List<Schedule> result = new ArrayList<>();
        int count = 0;
        int currentNextBest = 0;
        while(count < nbChildren){
            Schedule newChild;
            if(random.nextDouble() < crossoverRate){
                // Als getal kleiner is dan crossover rate: combineer 2 random parents
                Schedule parent1 =  parents.get(0); //parents.get(random.nextInt(parents.size() - 1));
                Schedule parent2 =  parents.get(1); //parents.get(random.nextInt(parents.size() - 1));
                newChild = performCrossover(parent1, parent2);
                
            } else {
                newChild = parents.get(currentNextBest);
                if(currentNextBest == parents.size() / 2 ){ // als de beste helft bereikt is, terug naar de eerste gaan.
                    currentNextBest = 0;
                } else {
                    currentNextBest++;
                }
            }
            result.add(newChild);
            count++;
        }

        return result;

    }

    public List<Schedule> generateMutatedChildren(List<Schedule> parents, double mutationRate, int nbMutations){
        List<Schedule> result = new ArrayList<>();
        for(Schedule newChild : parents){
            if(random.nextDouble() < mutationRate){
                performMutation(newChild, nbMutations);
            }
            result.add(newChild);
        }
        return result;
    }

    public Schedule generateSchedule() throws NotSolvableException, BadInstanceException  {
        Schedule result = initialSchedule();
        randomInitializeSchedule(result);
        return result;
    }

    public Schedule initialSchedule() throws NotSolvableException, BadInstanceException {
        Schedule schedule = new Schedule(data, parameters);
        
        return schedule;
    }

    private void randomInitializeSchedule(Schedule schedule){

        for (Week week : data.getWeeks()) {
            for (Shift shift : schedule.getShifts().values()) {

                boolean keepAdding = random.nextBoolean();
                while(keepAdding){
                    switch (shift.getPeriod()) {
                        case WEEK:
                            try {
                                assignRandomShift(schedule, week.getDays(), shift);
                            } catch (NoSuitableAssistantException e) {
                                break;
                            }
                            //completeScheduleFor(schedule, week.getDays(), shift);
                            break;
                        case WEEKEND:
                            if(!week.getWeekendDays().isEmpty()){
                                try {
                                    assignRandomShift(schedule, week.getWeekendDays(), shift);
                                } catch (NoSuitableAssistantException e) {
                                    break;
                                }
                                //completeScheduleFor(schedule, week.getWeekendDays(), shift);
                        }
                            break;
                        case HOLIDAY:
                            if(!week.getHolidays().isEmpty()){
                                try {
                                    assignRandomShift(schedule, week.getHolidays(), shift);
                                } catch (NoSuitableAssistantException e) {
                                    break;
                                }
                                //completeScheduleFor(schedule, week.getHolidays(), shift);
                            }                 
                            break;         
                    }
                    keepAdding = false;//random.nextBoolean();
                }     
            } 
        }
    }

    public List<Schedule> selectBest(List<Schedule> schedules, int nbBest){
        Map<Schedule, Double> scores = new HashMap<>();
        for(Schedule schedule : schedules){
            double currentScore = schedule.getFitnessScore();
            scores.put(schedule, currentScore);
        }
        Map<Schedule, Double> resultMap =
        scores.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
        .limit(nbBest)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        
        double avg = 0.0;
        for(double cs : scores.values()){
            avg += cs;
        }
        avg = avg / scores.values().size();

        System.out.println("SCORES: " + resultMap.values() + " | " + avg);

        List<Schedule> result = new ArrayList<Schedule>(resultMap.keySet());

        return result;
    }

    public Schedule performCrossover(Schedule schedule1, Schedule schedule2) throws NotSolvableException {
        Schedule result = new Schedule(data, parameters);
        for(Assistant assistant : result.getData().getAssistants()){
            System.out.println();
            System.out.print(assistant.getIndex() + " ");
            System.out.print(assistant.getName());

            if(random.nextBoolean()){
                System.out.print(" Schedule 1");
                result.schedule[assistant.getIndex()] = schedule1.schedule[assistant.getIndex()];
            } else {
                System.out.print(" Schedule 2");
                result.schedule[assistant.getIndex()] = schedule2.schedule[assistant.getIndex()];
            }
        }
        System.out.println();
        return result;
    }

    private List<Shift> getAllowedShiftsAssistant(Schedule schedule, Assistant assistant){
        List<Shift> result = new ArrayList<Shift>();
        for(Shift shift : schedule.getShifts().values()){
            if(shift.getAllowedAssistantTypes().contains( assistant.getType())){
                result.add(shift);
            }
        }
        return result;
    }

    private List<Shift> getAllAssignedShifts(Schedule schedule, Assistant assistant){
        List<Shift> result = new ArrayList<Shift>();
        int i = 0;
        
        while(i < data.getDays().size()){
            if(schedule.assignmentOn(assistant, data.getDays().get(i)) != ShiftType.FREE){
                    
            }
    
           
        }

        return result;
    }

    public void performMutation(Schedule schedule, int nbMutations) {
        boolean mutationDone = false;
        
        int performedMutations = 0;
        int mutationCase = random.nextInt(1);
        double removeOrAdd = random.nextDouble();
        
        if(removeOrAdd > 0.99){
            mutationCase = 1;
        } 
        while(!mutationDone){
            Assistant randomAssistant = data.getAssistants().get(random.nextInt(data.getAssistants().size() - 1));
            if(mutationCase == 0){
                // Randomly add shift
                assignRandomShift(schedule, randomAssistant);
                performedMutations++;
                mutationDone = performedMutations == nbMutations;
                

            } else if(mutationCase == 1){
                removeRandomShift(schedule, randomAssistant);
                performedMutations++;
                mutationDone = performedMutations == nbMutations;
                // Randomly remove shift
            } else {
                mutationDone = performedMutations == nbMutations;
                // TODO, misschien niet nodig
                // switch shifts between two assistants
            }
        }

    }
    

    private void assignRandomShift(Schedule schedule, Assistant assistant) {
        List<Week> shuffledWeeks = new ArrayList<Week>(data.getWeeks());
        Collections.shuffle(shuffledWeeks);
        boolean isAssigned = false;
        for(Week week : shuffledWeeks){
            for (Shift shift : schedule.getShifts().values()){
                switch(shift.getPeriod()){
                    case HOLIDAY:
                        if(!week.getHolidays().isEmpty()){
                            try {
                                schedule.assign(assistant, week.getHolidays(), shift);
                                isAssigned = true;
                            } catch (InvalidDayException | InvalidShiftTypeException e) {
                                isAssigned = false;
                            }
                        }
                        break;
                    case WEEK:
                        try {
                            schedule.assign(assistant, week.getDays(), shift);
                            isAssigned = true;
                        } catch (InvalidDayException | InvalidShiftTypeException e) {
                            isAssigned = false;
                        }
                        break;
                    case WEEKEND:
                        try {
                            schedule.assign(assistant, week.getWeekendDays(), shift);
                            isAssigned = true;
                        } catch (InvalidDayException | InvalidShiftTypeException e) {
                            isAssigned = false;
                        }
                        break;
                }
                if(isAssigned){
                    break;
                }
            }
            if(isAssigned){
                break;
            }
        }
    }



    private void assignRandomShift(Schedule schedule, List<Day> days, Shift shift) throws NoSuitableAssistantException{
        List<Assistant> invalidAssistants = new ArrayList<>();
        

        boolean notAssigned = true;
        while(notAssigned){
            Assistant assistant = randomAssistantForShift(invalidAssistants, shift);
            try {
                schedule.assign(assistant, days, shift);
                notAssigned = false;
            } catch (InvalidDayException | InvalidShiftTypeException e) {
                if (this.log.containsKey(e.getMessage())) {
                    this.log.put(e.getMessage(), this.log.get(e.getMessage())+1);
                } else {
                    this.log.put(e.getMessage(), 1);
                }
                invalidAssistants.add(assistant);
            }
        }
    }

    private Assistant randomAssistantForShift(List<Assistant> excludedAssistants, Shift shift) throws NoSuitableAssistantException {
        List<Assistant> allowedAssistants = data.getAssistants()
                .stream()
                .filter(assistant -> !excludedAssistants.contains(assistant))
                .filter(assistant -> shift.getAllowedAssistantTypes().contains(assistant.getType()))
                .collect(Collectors.toList());
    
        if (allowedAssistants.size() == 0) {
            throw new NoSuitableAssistantException("no allowed assistants left");
        }
        return allowedAssistants.get(random.nextInt(allowedAssistants.size()));

    }

    private void removeRandomShift(Schedule schedule, Assistant assistant){
        List<Day> shuffledDays = new ArrayList<Day>(data.getDays());
        Collections.shuffle(shuffledDays);
        for(Day day : shuffledDays){
            if(schedule.assignmentOn(assistant, day) != ShiftType.FREE){

                List<Day> toRemove;
                if(day.isHoliday()){
                    toRemove = getWeekFromDay(day).getHolidays();
                } else if(day.isWeekend()){

                    toRemove = getWeekFromDay(day).getWeekendDays();
                } else {

                    toRemove = getWeekFromDay(day).getDays();
                }
                
                schedule.clear(assistant, toRemove);
                break;
            } 
        }
    }

    public Week getWeekFromDay(Day day){
        
        int weekNumber = (day.getId() - 1) / 7;
        List<Day> days = data.getDays().subList(weekNumber * 7, weekNumber * 7 + 7);
        return new Week(days, weekNumber);
    }


    

}
