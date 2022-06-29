import os 
import pandas as pd
import time

jar_file = "\"/Users/dennismaes/Library/Mobile Documents/com~apple~CloudDocs/School/2021-2022/Thesis/applicatie/thesis/target/scheduler-1.0.0.jar:/Users/dennismaes/.m2/repository/org/xerial/sqlite-jdbc/3.34.0/sqlite-jdbc-3.34.0.jar:/Users/dennismaes/.m2/repository/org/javatuples/javatuples/1.2/javatuples-1.2.jar\""



NB_ITERATIONS = 2000
NB_PARENTS = [100]
NB_BEST = [0.25]
CROSS_RATES = [0.2]
MUT_RATES = [0.2, 0.4, 0.6]


def run_script():
    
    for parents in NB_PARENTS:
        print("-------" * 19)
        print("CURRENT NB PARENTS: ", parents)
        for best in NB_BEST:
            nb_best = int(parents * best)
            print(" " * 4 ,"CURRENT NB BEST: ", nb_best)
            for cr in CROSS_RATES:
                print(" " * 8 ,"CURRENT CR: ", cr)
                for mr in MUT_RATES:
                    print( " " * 12 , "CURRENT MR: ", mr)
                    run_java_program(NB_ITERATIONS, parents, nb_best, cr, mr)

    


def run_java_program(nb_iter, nb_parents, nb_best, cross_rate, mutation_rate, weight_cov=1, weight_bal=1, weight_fair=1, weight_streak=1, weight_spread=1):

    output_file = "test_results/"+ str(nb_iter) + "_" + str(nb_parents) + "_" + str(nb_best) + "_" + str(cross_rate) + "_" + str(mutation_rate) + "_" + str(weight_cov) + "_" + str(weight_bal) + "_" + str(weight_fair) + "_" + str(weight_streak) + "_" + str(weight_spread) + ".txt" 

    run_string = "java -cp "
    run_string += jar_file
    run_string += " Main "
    run_string += str(nb_iter)
    run_string += " "
    run_string += str(nb_parents)
    run_string += " "
    run_string += str(nb_best)
    run_string += " "
    run_string += str(cross_rate)
    run_string += " "
    run_string += str(mutation_rate)
    run_string += " "
    run_string += str(weight_cov)
    run_string += " "
    run_string += str(weight_bal)
    run_string += " " 
    run_string += str(weight_fair)
    run_string += " "
    run_string += str(weight_streak)
    run_string += " " 
    run_string += str(weight_spread)
    run_string += " > " + output_file 

    start = time.time()
    os.system(run_string)
    delta_time = time.time() - start

    with open(output_file, 'a') as f:
        f.write(str(delta_time))



def parse_results(filename):
    iterations = list()
    bests = list()
    coverages = list()
    balances = list()
    fairnesses = list()
    avgs = list()

    it = 1
    with open(filename, 'r') as f:
        for line in f.readlines()[1:-1]:
            splitted = line.split("|")
            best = float(splitted[0].split(":")[1])
            coverage = float(splitted[1].split(":")[1])
            balance = float(splitted[2].split(":")[1])
            fairness = float(splitted[3].replace("\n", "").split(":")[1])
            avg = float(splitted[4].replace("\n", "").split(":")[1])
            iterations.append(it)
            bests.append(best)
            coverages.append(coverage)
            balances.append(balance)
            fairnesses.append(fairness)
            avgs.append(avg)

            it += 1
    
    df = pd.DataFrame()
    df["iteration"] = iterations
    df["best"] = bests
    df["coverage"] = coverages
    df["balance"] = balances
    df["fairness"] = fairnesses
    df["avg"] = avgs
    
    return df


if __name__ == "__main__":
    run_script()

