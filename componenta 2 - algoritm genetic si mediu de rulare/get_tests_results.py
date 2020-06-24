import numpy as np

tests_file_to_eval = r"history\100_tests_for_best_of_362.txt"
tests_times_file = r"history\times\testing\testing_times.txt"


results_all = {
    "totalTimeSeconds": [],

    "gameStatus": [],
    "timeOfDeath": [],

    "currentLevelMetal": [],
    "storageMetal": [],
    "pullMetal": [],
    "incomeMetal": [],
    "expenseMetal": [],

    "currentLevelEnergy": [],
    "storageEnergy": [],
    "pullEnergy": [],
    "incomeEnergy": [],
    "expenseEnergy": [],

    "metalUsed": [],
    "metalProduced": [],
    "energyUsed": [],
    "energyProduced": [],

    "unitsKilled": [],
    "unitsDied": [],
}


def read_times(file_name):
    with open(file_name, "r") as file:
        for line in file:
            results_all["totalTimeSeconds"].append(float(line.split(" ")[0]))


def read_tests_results(file_name):
    with open(file_name, "r") as file:
        for line in file:
            if "----" not in line and "name=SCB" in line:
                current_test_stats = line.split("|")[:-1]

                for test in current_test_stats:
                    current_key = test.split("=")[0]
                    if current_key in results_all:
                        current_value = test.split("=")[1]
                        if current_value == "won":
                            current_value = 1
                        elif current_value == "lost":
                            current_value = 0
                        else:
                            current_value = float(current_value)

                        results_all.get(current_key).append(current_value)


indexes = [index for index in range(1, 101)]
read_times(tests_times_file)

read_tests_results(tests_file_to_eval)


def split_win_lose(array_param):
    game_result_array = results_all["gameStatus"]

    win = []
    win_index = []

    lost = []
    lost_index = []

    for index in range(0, len(array_param)):
        if game_result_array[index] == 1:
            win.append(array_param[index])
            win_index.append(index)
        else:
            lost.append(array_param[index])
            lost_index.append(index)

    return win, win_index, lost, lost_index


win, win_index, lost, lost_index = split_win_lose(results_all["totalTimeSeconds"])

print("Cel mai scurt meci testat : ", min(results_all["totalTimeSeconds"]))
print("Cel mai lung meci testat : ", max(results_all["totalTimeSeconds"]))
print("Media duratei meciurilor testate : ", np.mean(results_all["totalTimeSeconds"]))
print("")
print("Cel mai scurt meci testat câștigat : ", min(win))
print("Cel mai lung meci testat câștigat : ", max(win))
print("Media duratei meciurilor testate câștigate : ", np.mean(win))
print("")
print("Cel mai scurt meci testat pierdut : ", min(lost))
print("Cel mai lung meci testat pierdut : ", max(lost))
print("Media duratei meciurilor testate pierdute : ", np.mean(lost))
