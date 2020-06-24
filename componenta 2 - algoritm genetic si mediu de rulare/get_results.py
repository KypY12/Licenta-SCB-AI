import time

from game_runner import GameRunner
from genetic_algorithm import GeneticAlgorithm
from utils.load import *
from utils.dump import *

file_to_eval = "history/history_total.txt"


def read_generations_results():
    populations = []
    fitnesses = []
    game_results = []

    with open(file_to_eval, "r") as file:
        for line in file:
            if line[0] == "t":
                # team...
                content = line.split("{}")[:-1]

                current_game_results = []
                for elem in content:
                    proprs = [prop.split("=") for prop in elem.split("|")[:-1]]
                    indv_game_results = {prop[0]: prop[1] for prop in proprs}
                    current_game_results.append(indv_game_results)

                game_results.append(current_game_results)

            else:
                content = line.split(":")
                if content[1] == "p":
                    current_population = [[int(elem) for elem in indv.split(",")] for indv in content[2].split("|")]
                    populations.append(current_population)
                elif content[1] == "f":
                    current_fitness = [float(elem) for elem in content[2].split(",")]
                    fitnesses.append(current_fitness)

    populations = populations[:-1]

    pop_size = len(populations[0])
    generations_results = []
    for pop_index in range(0, len(populations)):
        pop = populations[pop_index]
        fit = fitnesses[pop_index]
        gr = game_results[pop_index]

        current_generation = [(pop[chr_index], fit[chr_index], gr[chr_index]) for chr_index in range(0, pop_size)]
        generations_results.append(current_generation)

    return generations_results


def test_game(chromosome):
    intervals = load_intervals()
    settings_data, army_data = load_config_data(0)

    ga = GeneticAlgorithm(population_size=30,
                          mutation_rate=0.01,
                          crossover_rate=0.3,
                          load_file=False)
    ga.init_parameters(settings_names=settings_data.keys(),
                       army_names=army_data.keys(),
                       settings_intervals=intervals)
    # Decodifica din genotip in fenotip
    decoded = ga.decode(chromosome)

    start = time.time_ns()

    gr = GameRunner()
    # Ruleaza o instanta a jocului si obtine rezultatele
    gr.set_config_data(decoded)
    gr.set_engine_index(0)
    gr.run_game("test_game")

    end = time.time_ns()

    return gr.get_game_results(), (end - start) / 1e9


generations_results = read_generations_results()
max_fitnesses = [max(generation, key=lambda t: float(t[1])) for generation in generations_results]
min_fitnesses = [min(generation, key=lambda t: float(t[1])) for generation in generations_results]
wins = [list(filter(lambda t: t[2]["gameStatus"] == "won", generation)) for generation in generations_results]

wins_array = []
for arr in wins:
    for elem in arr:
        wins_array.append(elem)

# cei din penultima sesiune de rulare
golden_generations_wins = []
for index, elem in enumerate(wins):
    if 351 <= index <= 387:
        golden_generations_wins.append(elem)

golden_generations_wins_array = []
for index, elem in enumerate(wins):
    if 351 <= index <= 387:
        for w in elem:
            golden_generations_wins_array.append(w)

win_max_fitnesses = [elem for elem in wins_array if elem in max_fitnesses]
win_max_fitnesses = win_max_fitnesses[::-1]

win_max_fitnesses_golden = [elem for elem in golden_generations_wins_array if elem in max_fitnesses]

print(len(max_fitnesses))
print(len(wins_array))
print(len(win_max_fitnesses))

print(len(golden_generations_wins))
print(len(golden_generations_wins_array))
print(len(win_max_fitnesses_golden))

# In cadrul acestui test s-au remarcat: index 6 (cel mai bun - fiind din generatia 362) si index 9
# for elem in win_max_fitnesses_golden:
#     test_game(elem[0])

for gen_index, wins_same_gen in enumerate(wins):
    for win in wins_same_gen:
        if win == win_max_fitnesses_golden[6]:
            print("Cromozomul cu strategia tank-rush este in generatia: ", gen_index)

for gen_index, wins_same_gen in enumerate(wins):
    for win in wins_same_gen:
        if win == win_max_fitnesses_golden[9]:
            print("Cromozomul cu strategia kbot-and-scout este in generatia: ", gen_index)

for gen_index, wins_same_gen in enumerate(wins):
    for win in wins_same_gen:
        if win == win_max_fitnesses_golden[19]:
            print("Cromozomul cu strategia airplane-first este in generatia: ", gen_index)


# Tank rush
# test_game(win_max_fitnesses_golden[6][0])

# Kbot start + early agressive scout
# test_game(win_max_fitnesses_golden[9][0])

# Airplane start
# test_game(win_max_fitnesses_golden[19][0])


# Kbot army, fara fitness maxim
# test_game(wins[366][3][0])


def dump_test_game_results(filename_additions, game_results):
    filename = "TEST_" + filename_additions + HISTORY_FILE_EXT
    with open(join(HISTORY_PATH, filename), "a") as file:
        for result in game_results:
            # Rezultatele ai-ului
            for key, value in result.items():
                file.write(str(key) + "=" + str(value) + "|")

            file.write("\n")
        file.write(" ---- \n")


test_subjects = [(win_max_fitnesses_golden[6][0], "6")]

for test_subject in test_subjects:
    chromosome = test_subject[0]
    addition = test_subject[1]

    for test_index in range(0, 70):
        game_results, game_total_time = test_game(chromosome)

        dump_test_game_results(addition, game_results)
        with open("history/times/testing/testing_times.txt", "a") as file:
            file.write(str(game_total_time) + " seconds" + "\n")

    with open("history/times/testing/testing_times.txt", "a") as file:
        file.write(" ---- " + str(addition) + " finished \n")

