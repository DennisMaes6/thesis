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

import exceptions.BadInstanceException;
import exceptions.InvalidDayException;
import exceptions.InvalidShiftTypeException;
import exceptions.NoSuitableAssistantException;
import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.Shift;
import input.shift.ShiftType;
import input.time.Day;
import input.time.Week;

public class GA {
    
    private final InstanceData data;
    private final ModelParameters parameters;
    private final Map<String, Integer> log = new HashMap<>();
    private Random random;

    public GA(InstanceData data, ModelParameters parameters) {
        this.data = data;
        this.parameters = parameters;
        this.random = new Random(1111);
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
