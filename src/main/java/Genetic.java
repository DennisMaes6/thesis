import exceptions.NotSolvableException;
import input.InstanceData;
import input.ModelParameters;
import input.assistant.Assistant;
import input.shift.FreeShift;
import input.shift.Shift;
import input.time.Week;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileWriter;   // Import the FileWriter class


public class Genetic {


    private final Random random;
    private final InstanceData data;
    private final ModelParameters parameters;

    public Genetic(InstanceData data, ModelParameters parameters){
        this.data = data;
        this.parameters = parameters;
        random = new Random(1111);
    }


    public WeeklySchedule runSubPopulationAlgo(int nbIterations, int nbParents, int nbBest, double crossoverRate, double mutationRate) throws NotSolvableException {


        double[] weightsLLO = {2, 0.35, 4}; // LINKS-LINKS-ONDER
        double[] weightsLRO = {0.5, 0.35, 4}; // LINKS-RECHTS-ONDER
        double[] weightsMLO = {0.5, 2, 4}; // MIDDEN-LINKS-ONDER
        double[] weightsMRO = {0.5, 0.5, 4}; // MIDDEN-RECHTS-ONDER
        double[] weightsRLO = {0.5, 0.35, 32}; // RECHTS-LINKS-ONDER
        double[] weightsRRO = {0.5, 0.35, 4}; // RECHTS-RECHTS-ONDER

        double[] weightsLO = {1, 0.35, 4}; // LINKSONDER
        double[] weightsMO = {0.5, 1, 4}; // MIDDENONDER
        double[] weightsRO = {0.5, 0.35, 16}; // RECHTSONDER
        double[] weightsLM = {1, 1, 8}; // LINKSMIDDEN
        double[] weightsMM = {1, 0.6, 16}; // MIDDENMIDDEN
        double[] weightsRM = {0.5, 1, 16}; // RECHTSMIDDEN

        double[] weightsT = {1.2, 1, 8}; // TOP

        double weightStreak = 1.0;
        double weightSpread = 1.0;


        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LLO");
        List<WeeklySchedule> parentsLLO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLLO[0], weightsLLO[1], weightsLLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LRO");
        List<WeeklySchedule> parentsLRO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLRO[0], weightsLRO[1], weightsLRO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MLO");
        List<WeeklySchedule> parentsMLO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMLO[0], weightsMLO[1], weightsMLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MRO");
        List<WeeklySchedule> parentsMRO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMRO[0], weightsMRO[1], weightsMRO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RLO");
        List<WeeklySchedule> parentsRLO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRLO[0], weightsRLO[1], weightsRLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RRO");
        List<WeeklySchedule> parentsRRO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRRO[0], weightsRRO[1], weightsRRO[2], weightStreak, weightSpread);

        List<List<WeeklySchedule>> parentsLO = new ArrayList<List<WeeklySchedule>>(){
            {
                add(parentsLLO);
                add(parentsLRO);
            }
        };
        List<List<WeeklySchedule>> parentsMO = new ArrayList<List<WeeklySchedule>>(){
            {
                add(parentsMLO);
                add(parentsMRO);
            }
        };
        List<List<WeeklySchedule>> parentsRO = new ArrayList<List<WeeklySchedule>>(){
            {
                add(parentsRLO);
                add(parentsRRO);
            }
        };

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LO");
        List<WeeklySchedule> LO = basicGenetic(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLO[0], weightsLO[1], weightsLO[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsLO));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MO");
        List<WeeklySchedule> MO = basicGenetic(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMO[0], weightsMO[1], weightsMO[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsMO));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RO");
        List<WeeklySchedule> RO = basicGenetic(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRO[0], weightsRO[1], weightsRO[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsRO));

        List<List<WeeklySchedule>> parentsLM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LO);
                add(MO);
            }
        };
        List<List<WeeklySchedule>> parentsMM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LO);
                add(RO);
            }
        };
        List<List<WeeklySchedule>> parentsRM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(MO);
                add(RO);
            }
        };

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LM");
        List<WeeklySchedule> LM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsLM[0], weightsLM[1], weightsLM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsLM));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MM");
        List<WeeklySchedule> MM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsMM[0], weightsMM[1], weightsMM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsMM));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RM");
        List<WeeklySchedule> RM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsRM[0], weightsRM[1], weightsRM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsRM));

        List<List<WeeklySchedule>> parentsTop = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LM);
                add(MM);
                add(RM);
            }
        };

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("START RUNNING: TOP");
        List<WeeklySchedule> TOP = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsTop));

        return selectBest(TOP, 1, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread).get(0);

    }

    public WeeklySchedule runSubPopulationAlgo(int nbIterations, int nbParents, int nbBest, double crossoverRate, double mutationRate,
                                               double[] weightsT, double[] weightsLM, double[] weightsMM, double[] weightsRM, double[] weightsLO,
                                               double[] weightsMO, double[] weightsRO, double[] weightsLLO, double[] weightsLRO,
                                               double[] weightsMLO, double[] weightsMRO, double[] weightsRLO, double[] weightsRRO) throws NotSolvableException {

        double weightStreak = 1.0;
        double weightSpread = 1.0;

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LLO");
        List<WeeklySchedule> parentsLLO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLLO[0], weightsLLO[1], weightsLLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LRO");
        List<WeeklySchedule> parentsLRO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLRO[0], weightsLRO[1], weightsLRO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MLO");
        List<WeeklySchedule> parentsMLO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMLO[0], weightsMLO[1], weightsMLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MRO");
        List<WeeklySchedule> parentsMRO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMRO[0], weightsMRO[1], weightsMRO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RLO");
        List<WeeklySchedule> parentsRLO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRLO[0], weightsRLO[1], weightsRLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RRO");
        List<WeeklySchedule> parentsRRO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRRO[0], weightsRRO[1], weightsRRO[2], weightStreak, weightSpread);

        List<List<WeeklySchedule>> parentsLO = new ArrayList<List<WeeklySchedule>>(){
            {
                add(parentsLLO);
                add(parentsLRO);
            }
        };
        List<List<WeeklySchedule>> parentsMO = new ArrayList<List<WeeklySchedule>>(){
            {
                add(parentsMLO);
                add(parentsMRO);
            }
        };
        List<List<WeeklySchedule>> parentsRO = new ArrayList<List<WeeklySchedule>>(){
            {
                add(parentsRLO);
                add(parentsRRO);
            }
        };

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LO");
        List<WeeklySchedule> LO = basicGenetic(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLO[0], weightsLO[1], weightsLO[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsLO));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MO");
        List<WeeklySchedule> MO = basicGenetic(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMO[0], weightsMO[1], weightsMO[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsMO));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RO");
        List<WeeklySchedule> RO = basicGenetic(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRO[0], weightsRO[1], weightsRO[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsRO));

        List<List<WeeklySchedule>> parentsLM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LO);
                add(MO);
            }
        };
        List<List<WeeklySchedule>> parentsMM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LO);
                add(RO);
            }
        };
        List<List<WeeklySchedule>> parentsRM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(MO);
                add(RO);
            }
        };

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LM");
        List<WeeklySchedule> LM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsLM[0], weightsLM[1], weightsLM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsLM));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MM");
        List<WeeklySchedule> MM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsMM[0], weightsMM[1], weightsMM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsMM));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RM");
        List<WeeklySchedule> RM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsRM[0], weightsRM[1], weightsRM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsRM));

        List<List<WeeklySchedule>> parentsTop = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LM);
                add(MM);
                add(RM);
            }
        };

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("START RUNNING: TOP");
        List<WeeklySchedule> TOP = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsTop));

        return selectBest(TOP, 1, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread).get(0);

    }


    public WeeklySchedule runSubPopulationAlgo(int nbIterations, int nbParents, int nbBest, double crossoverRate, double mutationRate,
                                               double[] weightsT, double[] weightsLM, double[] weightsMM, double[] weightsRM, double[] weightsLO,
                                               double[] weightsMO, double[] weightsRO) throws NotSolvableException {

        double weightStreak = 1.0;
        double weightSpread = 1.0;


        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LO");
        List<WeeklySchedule> LO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsLO[0], weightsLO[1], weightsLO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MO");
        List<WeeklySchedule> MO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsMO[0], weightsMO[1], weightsMO[2], weightStreak, weightSpread);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RO");
        List<WeeklySchedule> RO = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate, mutationRate, weightsRO[0], weightsRO[1], weightsRO[2], weightStreak, weightSpread);

        List<List<WeeklySchedule>> parentsLM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LO);
                add(MO);
            }
        };
        List<List<WeeklySchedule>> parentsMM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LO);
                add(RO);
            }
        };
        List<List<WeeklySchedule>> parentsRM = new ArrayList<List<WeeklySchedule>>(){
            {
                add(MO);
                add(RO);
            }
        };

        List<WeeklySchedule> listParentsLM = new ArrayList<>();
        List<WeeklySchedule> listParentsMM = new ArrayList<>();
        List<WeeklySchedule> listParentsRM = new ArrayList<>();
        for(WeeklySchedule ws : LO){
            listParentsLM.add(ws);
            listParentsMM.add(ws);
        }
        for(WeeklySchedule ws : MO){
            listParentsLM.add(ws);
            listParentsRM.add(ws);
        }
        for(WeeklySchedule ws : RO){
            listParentsMM.add(ws);
            listParentsRM.add(ws);
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: LM");
        List<WeeklySchedule> LM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsLM[0], weightsLM[1], weightsLM[2], weightStreak, weightSpread, selectBestRoulette(listParentsLM, nbParents, 1, 1, 1, 1, 1, false));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: MM");
        List<WeeklySchedule> MM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsMM[0], weightsMM[1], weightsMM[2], weightStreak, weightSpread, selectBestRoulette(listParentsMM, nbParents, 1, 1, 1, 1, 1, false));
        //List<WeeklySchedule> MM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsMM[0], weightsMM[1], weightsMM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsMM));
        System.out.println("----------------------------------------------------------------------");
        System.out.println("START RUNNING: RM");
        List<WeeklySchedule> RM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsRM[0], weightsRM[1], weightsRM[2], weightStreak, weightSpread, selectBestRoulette(listParentsRM, nbParents, 1, 1, 1, 1, 1, false));
        //List<WeeklySchedule> RM = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsRM[0], weightsRM[1], weightsRM[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsRM));

        List<List<WeeklySchedule>> parentsTop = new ArrayList<List<WeeklySchedule>>(){
            {
                add(LM);
                add(MM);
                add(RM);
            }
        };

        List<WeeklySchedule> listParentsTop = new ArrayList<>();
        for(WeeklySchedule ws : LM){
            listParentsTop.add(ws);
        }
        for(WeeklySchedule ws : MM){
            listParentsTop.add(ws);
        }
        for(WeeklySchedule ws : RM){
            listParentsTop.add(ws);
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("START RUNNING: TOP");
        List<WeeklySchedule> TOP = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread, selectBestRoulette(listParentsTop, nbParents, 1, 1, 1, 1, 1, false));
        //List<WeeklySchedule> TOP = basicGenetic(nbIterations,  nbParents, nbBest, crossoverRate, mutationRate, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread, randomMergeWeeklySchedules(parentsTop));



        return selectBest(TOP, 1, weightsT[0], weightsT[1], weightsT[2], weightStreak, weightSpread).get(0);

    }


    public WeeklySchedule runAlgo(int nbIterations, int nbParents, int nbBest, double crossoverRate, double mutationRate, double weightCov, double weightBal, double weightFairness, double weightStreak, double weightSpread) throws NotSolvableException {
        List<WeeklySchedule> parents = basicGeneticFromRandom(nbIterations, nbParents, nbBest, crossoverRate,  mutationRate, weightCov, weightBal, weightFairness, weightStreak, weightSpread);
        return selectBest(parents, 1, weightCov, weightBal, weightFairness, weightStreak, weightSpread).get(0);
    }

    private List<WeeklySchedule> basicGeneticFromRandom(int nbIterations, int nbParents, int nbBest, double crossoverRate, double mutationRate, double weightCov, double weightBal, double weightFairness, double weightStreak, double weightSpread) throws NotSolvableException {
        List<WeeklySchedule> parents = generateRandomParents(nbParents);

        for(int i = 0; i < nbIterations; i++){
            //parents = selectBestRoulette(parents, nbBest, weightCov, weightBal, weightFairness, weightStreak, weightSpread, true);
            parents = selectBest(parents, nbBest, weightCov, weightBal, weightFairness, weightStreak, weightSpread);
            parents = generateCrossoverChildren(parents, nbParents, crossoverRate);
            parents = generateMutatedChildren(parents, mutationRate);
        }
        return parents;
    }

    private List<WeeklySchedule> basicGenetic(int nbIterations, int nbParents, int nbBest, double crossoverRate, double mutationRate, double weightCov, double weightBal, double weightFairness, double weightStreak, double weightSpread, List<WeeklySchedule> parents) throws NotSolvableException {
        for(int i = 0; i < nbIterations; i++){
            //parents = selectBestRoulette(parents, nbBest, weightCov, weightBal, weightFairness, weightStreak, weightSpread, true);
            parents = selectBest(parents, nbBest, weightCov, weightBal, weightFairness, weightStreak, weightSpread);
            parents = generateCrossoverChildren(parents, nbParents, crossoverRate);
            parents = generateMutatedChildren(parents, mutationRate);
        }
        return parents;
    }


    private List<WeeklySchedule> randomMergeWeeklySchedules(List<List<WeeklySchedule>> lists){
        List<WeeklySchedule> result = new ArrayList<>();

        for(int i = 0; i < lists.get(0).size(); i++){
            result.add(lists.get(getRandom().nextInt(lists.size())).get(i));
        }

        return result;
    }

    public List<WeeklySchedule> generateRandomParents(int nbParents){
        List<WeeklySchedule> result = new ArrayList<>();
        for(int i = 0; i < nbParents; i++){
            result.add(randomWeeklyScedule());
        }
        return result;
    }

    public List<WeeklySchedule> generateCrossoverChildren(List<WeeklySchedule> parents, int nbChildren, double crossoverRate){
        // Gaat ervanuit dat parents gesorteerd zijn
        List<WeeklySchedule> result = new ArrayList<>();

        int currentNextChildIndex = 0;
        for(int i = 0; i < nbChildren; i++){
            if(getRandom().nextDouble() < crossoverRate){
                WeeklySchedule parent1 = parents.get(getRandom().nextInt(parents.size()));
                WeeklySchedule parent2 = parents.get(getRandom().nextInt(parents.size()));
                //WeeklySchedule child = crossover(parent1, parent2);
                WeeklySchedule child = onePointCrossover(parent1, parent2);
                result.add(child);
            } else {
                result.add(parents.get(currentNextChildIndex));
                if(currentNextChildIndex > parents.size() - 2){
                    currentNextChildIndex = 0;
                } else {
                    currentNextChildIndex++;
                }
            }
        }
        return result;
    }


    public List<WeeklySchedule> generateMutatedChildren(List<WeeklySchedule> parents, double mutationRate){
        List<WeeklySchedule> result = new ArrayList<>();

        for(WeeklySchedule ws : parents){
            if(getRandom().nextDouble() < mutationRate){
                WeeklySchedule mutated = mutation(ws);
                result.add(mutated);
            } else {
                result.add(ws);
            }
        }
        return result;
    }


    public WeeklySchedule randomWeeklyScedule(){
        WeeklySchedule result = new WeeklySchedule(getData(), getParameters());
        FreeShift free = new FreeShift(1);
        for(Assistant assistant : result.getAssistants()){

            List<Shift> allowedShifts = result.getShifts().values()
                    .stream()
                    .filter(s -> s.getAllowedAssistantTypes().contains(assistant.getType()))
                    .collect(Collectors.toList());

            for(int i = 0; i < getNbWeeks(); i++){
                if(assistant.getFreeDayIds().contains( (i+1) * 7)){ // GAAT ER VAN UIT DAT VERLOF TELKENS BLOKKEN ZIJN VAN EEN HELE WEEK
                    result.assignShift(assistant, free, i);
                } else {
                    Shift randomShift = allowedShifts.get(getRandom().nextInt(allowedShifts.size()));
                    result.assignShift(assistant, randomShift, i);
                }
            }
        }
        return result;
    }

    public List<WeeklySchedule> selectBest(List<WeeklySchedule> schedules, int nbBest, double weightCov, double weightBal, double weightFairness, double weightStreak, double weightSpread) throws NotSolvableException {
        Map<WeeklySchedule, Double> scores = new HashMap<>();
        for(WeeklySchedule schedule : schedules){
            double currentScore = FitnessEval.getFitness(schedule, weightCov, weightBal, weightFairness, weightStreak, weightSpread);
            scores.put(schedule, currentScore);
        }

        Map<WeeklySchedule, Double> resultMap =
                scores.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                        .limit(nbBest)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        WeeklySchedule daBest =
                scores.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                        .limit(1)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                        .keySet().stream().collect(Collectors.toList()).get(0);


        double avg = 0.0;
        for(double cs : scores.values()){
            avg += cs;
        }
        avg = avg / scores.values().size();

        //System.out.println("BEST SCORE: " + new ArrayList<>(resultMap.values()).get(0) + " | AVG SCORE: " + avg);
        double normC = weightBal + weightCov + weightFairness;
        double nWeightBal = weightBal / normC;
        double nWeightFair = weightFairness / normC;
        double nWeightCov = weightCov / normC;



        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH/mm/ss");
        LocalDateTime now = LocalDateTime.now();
        /*
        System.out.println("BEST SCORE: " + new ArrayList<>(resultMap.values()).stream().sorted().collect(Collectors.toList()).get(0)
                + " | COVERAGE: " + ScheduleDecoder.scheduleFromWeeklySchedule(daBest).getFitnessScore() //* nWeightCov
                + " | BALANCE: " + daBest.getBalanceScore() //* nWeightBal
                + " | FAIRNESS: " + daBest.getFairnessScore() //* nWeightFair
                + " | AVG SCORE: " + avg
                + " | TIME: " + dtf.format(now)); */

        System.out.println("BEST SCORE: " + new ArrayList<>(resultMap.values()).stream().sorted().collect(Collectors.toList()).get(0)
                + " | COVERAGE: " + ScheduleDecoder.scheduleFromWeeklySchedule(daBest).getFitnessScore() //* nWeightCov
                + " | BALANCE: " + FitnessEval.getBalanceScore(daBest, weightStreak, weightSpread) //* nWeightBal
                + " | FAIRNESS: " + daBest.getFairnessScore() //* nWeightFair
                + " | AVG SCORE: " + avg
                + " | TIME: " + dtf.format(now));

        List<WeeklySchedule> result = new ArrayList<WeeklySchedule>(resultMap.keySet());

        return result;
    }

    public List<WeeklySchedule> selectBestRoulette(List<WeeklySchedule> schedules, int nbBest, double weightCov, double weightBal, double weightFairness, double weightStreak, double weightSpread, boolean printStats) throws NotSolvableException {
        Map<WeeklySchedule, Double> scores = new HashMap<>();
        double[] cumulativeFitnesses = new double[schedules.size()];
        int i = 0;
        for (WeeklySchedule schedule : schedules) {
            double currentScore = FitnessEval.getFitness(schedule, weightCov, weightBal, weightFairness, weightStreak, weightSpread);
            double adjustedScore = 1 / currentScore;
            if(i == 0){
                cumulativeFitnesses[i] = adjustedScore;
            } else {
                cumulativeFitnesses[i] = cumulativeFitnesses[i-1] + adjustedScore;
            }
            i += 1;
            scores.put(schedule, currentScore);
        }

        List<WeeklySchedule> result = new ArrayList<WeeklySchedule>(nbBest);

        for(int n = 0; n < nbBest; n++){
            double randomFitness = getRandom().nextDouble() * cumulativeFitnesses[cumulativeFitnesses.length - 1];
            int index = Arrays.binarySearch(cumulativeFitnesses, randomFitness);
            if(index < 0){
                index = Math.abs(index + 1);
            }
            result.add(schedules.get(index));
        }



        if(printStats){
            Map<WeeklySchedule, Double> resultMap =
                    scores.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(nbBest)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            WeeklySchedule daBest =
                    scores.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                            .limit(1)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                            .keySet().stream().collect(Collectors.toList()).get(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH/mm/ss");
            LocalDateTime now = LocalDateTime.now();

            double avg = 0.0;
            for(double cs : scores.values()){
                avg += cs;
            }
            avg = avg / scores.values().size();

            System.out.println("BEST SCORE: " + new ArrayList<>(resultMap.values()).stream().sorted().collect(Collectors.toList()).get(0)
                    + " | COVERAGE: " + ScheduleDecoder.scheduleFromWeeklySchedule(daBest).getFitnessScore() //* nWeightCov
                    + " | BALANCE: " + FitnessEval.getBalanceScore(daBest, weightStreak, weightSpread) //* nWeightBal
                    + " | FAIRNESS: " + daBest.getFairnessScore() //* nWeightFair
                    + " | AVG SCORE: " + avg
                    + " | TIME: " + dtf.format(now));
        }

        return result;



    }

    public WeeklySchedule mutation(WeeklySchedule ws){
        Assistant randomAssistant = ws.getAssistants().get(getRandom().nextInt(ws.getAssistants().size()));
        int randomWeek = getRandom().nextInt(getNbWeeks());
        if(randomAssistant.getFreeDayIds().contains( (randomWeek+1) * 7)){ // GAAT ER VAN UIT DAT VERLOF TELKENS BLOKKEN ZIJN VAN EEN HELE WEEK
            ws.assignShift(randomAssistant, new FreeShift(1), randomWeek);
            return ws;
        }

        Shift currentShift = ws.getScheduleWeeks().get(randomAssistant).get(randomWeek);
        List<Shift> allowedShifts = ws.getShifts().values()
                .stream()
                .filter(s -> s.getAllowedAssistantTypes().contains(randomAssistant.getType()) && s != currentShift)
                .collect(Collectors.toList());
        Shift newShift = allowedShifts.get(getRandom().nextInt(allowedShifts.size()));
        ws.assignShift(randomAssistant, newShift, randomWeek);


        return ws;
    }

    public WeeklySchedule crossover(WeeklySchedule ws1, WeeklySchedule ws2){
        WeeklySchedule result = new WeeklySchedule(getData(), getParameters());
        for(Assistant assistant : ws1.getAssistants()){
            List<Shift> shifts1 = ws1.getScheduleWeeks().get(assistant);
            List<Shift> shifts2 = ws2.getScheduleWeeks().get(assistant);

            List<Shift> resultShifts = new ArrayList<>();

            for(int i = 0; i < shifts1.size(); i++){
                Shift chosenShift;
                if(getRandom().nextBoolean()){
                    chosenShift = shifts1.get(i);
                } else {
                    chosenShift = shifts2.get(i);
                }
                resultShifts.add(chosenShift);
            }
            result.getScheduleWeeks().put(assistant, resultShifts);
        }
        return result;
    }


    public WeeklySchedule onePointCrossover(WeeklySchedule ws1, WeeklySchedule ws2){
        WeeklySchedule result = new WeeklySchedule(getData(), getParameters());

        for(Assistant assistant : ws1.getAssistants()){
            List<Shift> shifts1 = ws1.getScheduleWeeks().get(assistant);
            List<Shift> shifts2 = ws2.getScheduleWeeks().get(assistant);

            List<Shift> resultShifts = new ArrayList<>();

            int cxPoint = getRandom().nextInt(shifts1.size());

            // Random assistant 1 of assistant 2 als eerste pakken.
            if(getRandom().nextBoolean()){
                for(int i = 0; i < cxPoint; i++){
                    Shift chosenShift = shifts1.get(i);
                    resultShifts.add(chosenShift);
                }
                for(int i = cxPoint; i < shifts2.size(); i++){
                    Shift chosenShift = shifts2.get(i);
                    resultShifts.add(chosenShift);
                }
            } else {
                for(int i = 0; i < cxPoint; i++){
                    Shift chosenShift = shifts2.get(i);
                    resultShifts.add(chosenShift);
                }
                for(int i = cxPoint; i < shifts1.size(); i++){
                    Shift chosenShift = shifts1.get(i);
                    resultShifts.add(chosenShift);
                }
            }
            result.getScheduleWeeks().put(assistant, resultShifts);
        }
        return result;
    }



    private int getNbWeeks(){
        return getData().getWeeks().size();
    }


    public InstanceData getData() {
        return data;
    }

    public ModelParameters getParameters() {
        return parameters;
    }

    public Random getRandom() {
        return random;
    }
}
