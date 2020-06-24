import concurrent.futures
import numpy as np
from game_runner import GameRunner

from utils.load import *
from utils.dump import *

import time

from datetime import datetime
import math


def decimal(value_list):
    return int("".join([str(elem) for elem in value_list]), 2)


def get_timestamp():
    return datetime.now().timestamp()


def get_bit_length(interval):
    a, b = interval
    return math.ceil(math.log(b - a, 2))


def decode_one_value(bits, interval):
    a, b = interval
    return a + decimal(bits) * (b - a) // (2 ** len(bits) - 1)


class GeneticAlgorithm:

    def __init__(self, population_size=0, mutation_rate=0.1, crossover_rate=0.3, load_file=False):
        if load_file:
            self.starting_gen_index, self.population = load_population()
            self.population_size = len(self.population)

        else:
            self.population = []
            self.starting_gen_index = 0
            self.population_size = population_size

        self.fitness_results = [0 for count in range(0, population_size)]
        self.game_results = [0 for count in range(0, population_size)]

        self.army_names = []
        self.settings_names = []
        self.settings_intervals = dict()
        self.settings_sizes = dict()

        self.chromosome_size = 0

        self.mutation_rate = mutation_rate
        self.crossover_rate = crossover_rate

        self.history_additions = "_".join(
            [str(population_size), str(self.mutation_rate), str(self.crossover_rate), str(get_timestamp())])

    # Inits ============================================================================================================

    def init_settings_sizes(self):
        if len(self.settings_intervals.keys()) > 0:
            for setting, value in self.settings_intervals.items():
                self.settings_sizes[setting] = get_bit_length(value)

    def init_chromosome_size(self):
        if len(self.settings_sizes.keys()) > 0:
            # Luam in calcul lungimea in biti pentru fiecare valoare a fiecarei setari
            for setting, value in self.settings_sizes.items():
                self.chromosome_size += value

            # Luam in calcul si numarul de unitati din setari (deoarece acestea vor avea un singur bit)
            self.chromosome_size += len(self.army_names)

    def init_parameters(self, settings_names, army_names, settings_intervals):
        self.army_names = list(army_names)
        self.settings_names = list(settings_names)
        self.settings_intervals = settings_intervals

        self.init_settings_sizes()
        self.init_chromosome_size()

    def init_population(self):
        if len(self.population) == 0:
            for index in range(0, self.population_size):
                self.population.append(np.random.randint(0, 2, size=self.chromosome_size).tolist())

    # Functions for eval and fitness ===================================================================================

    def decode(self, chromosome):
        current_index = 0
        settings_data = dict()
        army_data = dict()
        for index in range(0, len(self.settings_names)):
            name = self.settings_names[index]
            size = self.settings_sizes[name]
            bits = chromosome[current_index: current_index + size]
            interval = self.settings_intervals[name]

            settings_data[name] = decode_one_value(bits, interval)

            current_index += size

        for index in range(0, len(self.army_names)):
            name = self.army_names[index]
            army_data[name] = chromosome[current_index]
            current_index += 1

        return settings_data, army_data

    # de la generatia 58 pana la final (generatia 402)
    def eval(self, game_results):
        ai_results, enemy_results = game_results

        timeAlive = ai_results["timeOfDeath"]
        metalIndex = ai_results["metalProduced"] - ai_results["metalUsed"]
        energyIndex = ai_results["energyProduced"] - ai_results["energyUsed"]
        unitsIndex = ai_results["unitsKilled"] - ai_results["unitsDied"]

        if ai_results["gameStatus"] == "won":
            metalIndex = 200 / metalIndex
            energyIndex = 200 / energyIndex

            eval_value = metalIndex + energyIndex + unitsIndex - timeAlive + 9999
        else:
            metalIndex = 200 / metalIndex
            energyIndex = 200 / energyIndex

            eval_value = metalIndex + energyIndex + unitsIndex + timeAlive

        if eval_value < 0.001:
            eval_value = 0.001

        return eval_value

    # de la generatia 44 pana la 57
    # def eval(self, game_results):
    #     ai_results, enemy_results = game_results
    #
    #     timeAlive = ai_results["timeOfDeath"]
    #     metalIndex = ai_results["metalProduced"] - ai_results["metalUsed"]
    #     energyIndex = ai_results["energyProduced"] - ai_results["energyUsed"]
    #     unitsIndex = ai_results["unitsKilled"] - ai_results["unitsDied"]
    #
    #
    #     if ai_results["gameStatus"] == "won":
    #         if metalIndex > 200:
    #             metalIndex = 200 / metalIndex
    #
    #         if energyIndex > 200:
    #             energyIndex = 200 / energyIndex
    #
    #         eval_value = metalIndex + energyIndex + unitsIndex - timeAlive + 9999
    #     else:
    #         if metalIndex > 100:
    #             metalIndex = 100 / metalIndex
    #
    #         if energyIndex > 100:
    #             energyIndex = 100 / energyIndex
    #
    #         eval_value = metalIndex + energyIndex + unitsIndex + timeAlive
    #
    #     if eval_value < 0.001:
    #         eval_value = 0.001
    #
    #     return eval_value

    # de la generatia 22 pana la 43
    # def eval(self, game_results):
    #     ai_results, enemy_results = game_results
    #
    #     timeAlive = ai_results["timeOfDeath"]
    #     metalIndex = ai_results["metalProduced"] - ai_results["metalUsed"]
    #     energyIndex = ai_results["energyProduced"] - ai_results["energyUsed"]
    #     unitsIndex = ai_results["unitsKilled"] - ai_results["unitsDied"]
    #
    #     if ai_results["gameStatus"] == "won":
    #         if metalIndex > 200:
    #             metalIndex = 200 / metalIndex
    #
    #         if energyIndex > 200:
    #             energyIndex = 200 / energyIndex
    #
    #         eval_value = metalIndex + energyIndex + unitsIndex - timeAlive + 999_999
    #     else:
    #         if metalIndex > 100:
    #             metalIndex = 100 / metalIndex
    #
    #         if energyIndex > 100:
    #             energyIndex = 100 / energyIndex
    #
    #         eval_value = metalIndex + energyIndex + unitsIndex + timeAlive
    #
    #     if eval_value < 0.001:
    #         eval_value = 0.001
    #
    #     return eval_value

    # de la generatia 0 pana la 21
    # def eval(self, game_results):
    #     ai_results, enemy_results = game_results
    #
    #     result_value = 0.5
    #     if ai_results["gameStatus"] == "won":
    #         result_value = 4
    #
    #     timeAlive = ai_results["timeOfDeath"]
    #     metalIndex = min([ai_results["metalProduced"] - ai_results["metalUsed"], 100])
    #     energyIndex = min([ai_results["energyProduced"] - ai_results["energyUsed"], 100])
    #     unitsIndex = ai_results["unitsKilled"] - ai_results["unitsDied"]
    #
    #     eval_value = result_value * timeAlive + metalIndex + energyIndex + unitsIndex
    #
    #     return eval_value

    def eval_game(self, chromosome, chr_index, engine_index):
        start = time.time_ns()

        # Decodifica din genotip in fenotip
        decoded = self.decode(chromosome)

        gr = GameRunner()
        # Ruleaza o instanta a jocului si obtine rezultatele
        gr.set_config_data(decoded)
        gr.set_engine_index(engine_index)
        gr.run_game()

        end = time.time_ns()

        return gr.get_game_results(), chr_index, engine_index, (end - start) / 1e9

    def eval_population(self):
        with concurrent.futures.ThreadPoolExecutor(max_workers=12) as th_executor:
            # engine_ids => un dictionar care indica ce engine este liber
            engines = {0: True, 1: True, 2: True, 3: True, 4: True, 5: True,
                       6: True, 7: True, 8: True, 9: True, 10: True, 11: True}
            tasks = []

            still_running = True
            chromosome_index = 0

            while still_running:
                full_usage = True

                if chromosome_index < self.population_size:
                    # Caut un engine liber si adaug un task daca gasesc unul
                    for engine_index, is_free in engines.items():
                        if is_free:
                            full_usage = False
                            engines[engine_index] = False

                            chromosome = self.population[chromosome_index]
                            tasks.append(th_executor.submit(self.eval_game, chromosome, chromosome_index, engine_index))

                            chromosome_index += 1

                # Daca toate engine-urile sunt ocupate asteptam sa se elibereze unul
                if full_usage:
                    done, not_done = concurrent.futures.wait(tasks, return_when=concurrent.futures.FIRST_COMPLETED)
                    if len(not_done) == 0 and len(done) == 0:
                        still_running = False
                    else:
                        for future in done:
                            results, chr_index, engine_index, time_elapsed = future.result()

                            # Preia rezultatele
                            self.game_results[chr_index] = results
                            # Evalueaza cromozomul si retine rezultatul
                            self.fitness_results[chr_index] = self.eval(results)

                            # Scrie in fisier cat a durat rularea meciului
                            with open("history/times/generation_times.txt", "a") as file:
                                file.write(
                                    " ---- chromosome " + str(chr_index) + " time : " + str(time_elapsed)
                                    + " seconds" + "\n")

                            # Scoatem task-ul din lista de task-uri
                            tasks.remove(future)
                            # Eliberam engine-ul respectiv
                            engines[engine_index] = True

    # de la generatia 22 pana la final (generatia 401)
    def compute_fitnesses(self):
        min_eval = min(self.fitness_results)
        max_eval = max(self.fitness_results)
        eval_distance = max_eval - min_eval
        factor = 8

        for fit_index in range(0, len(self.fitness_results)):
            self.fitness_results[fit_index] = (1 + (self.fitness_results[fit_index] - min_eval) / eval_distance) \
                                              ** factor

    # de la generatia 0 la 21
    # def compute_fitnesses(self):
    #     min_eval = min(self.fitness_results)
    #     max_eval = max(self.fitness_results)
    #     eval_distance = max_eval - min_eval
    #     factor = 4
    #
    #     for fit_index in range(0, len(self.fitness_results)):
    #         self.fitness_results[fit_index] = (1 + (
    #                 self.fitness_results[fit_index] - min_eval) / eval_distance) ** factor

    # Operators ========================================================================================================

    def roulette_wheel(self):
        # Calculez suma valorilor functiei fitness
        fitness_sum = sum(self.fitness_results)

        # Calculez probabilitatile de selectie
        fitness_probabs = [fit_val / fitness_sum for fit_val in self.fitness_results]

        # Construiesc intervalele de selectie
        probab_cumulative = []
        current_cumulative_sum = 0
        for probab in fitness_probabs:
            probab_cumulative.append(current_cumulative_sum + probab)
            current_cumulative_sum += probab

        probab_cumulative[-1] = 1

        # Selectez indivizi pentru noua populatie
        new_population = []
        for selected_index in range(0, self.population_size):
            current_rand = np.random.rand()
            for index in range(0, len(probab_cumulative)):
                if current_rand <= probab_cumulative[index]:
                    new_population.append(self.population[index])
                    break

        self.population = new_population

    def selection(self):
        self.roulette_wheel()

    def mutation(self):
        for chromosome in self.population:
            for gene_index in range(0, len(chromosome)):
                if np.random.random() < self.mutation_rate:
                    if chromosome[gene_index] == 0:
                        chromosome[gene_index] = 1
                    else:
                        chromosome[gene_index] = 0

    def cross_two(self, chromosome_1, chromosome_2):
        # pentru a evita echivalenta cu mutatia pe prima/ultima pozitie,
        # am generat un punct de taiere intre a doua si penultima pozitie
        cut_index = np.random.randint(2, len(chromosome_1) - 1)
        chromosome_1[0:cut_index], chromosome_2[0:cut_index] = chromosome_2[0:cut_index], chromosome_1[0:cut_index]

    def crossover(self):
        # se face selectia cromozomilor pentru incrucisare
        crossover_selected = []
        for chromosome in self.population:
            if np.random.random() < self.crossover_rate:
                crossover_selected.append(chromosome)

        # daca numarul cromozomilor selectati este impar,
        # ignoram ultimul selectat, pentru a putea forma perechi
        # intre toti cromozomii selectati
        if len(crossover_selected) % 2 == 1:
            crossover_selected = crossover_selected[:-1]

        # se face incrucisarea cromozomilor selectati,
        # doi cate doi, consecutiv
        for index in range(0, len(crossover_selected), 2):
            self.cross_two(crossover_selected[index], crossover_selected[index + 1])

    # Execute ==========================================================================================================

    def execute(self, generations):

        for generation_index in range(self.starting_gen_index, generations):
            start = time.time_ns()

            # Salveaza in istoric populatia curenta
            dump_current_population(self.history_additions, generation_index, self.population)

            # Evalueaza populatia curenta
            self.eval_population()
            # Calculeaza valorea functiei fitness a fiecarui cromozom
            self.compute_fitnesses()

            # Salveza in istoric rezultatele meciurilor din generatia curenta
            dump_current_game_results(self.history_additions, self.game_results)
            # Salveaza in istoric valorile functiei fitness, ale populatiei curente
            dump_current_fitness(self.history_additions, generation_index, self.fitness_results)

            # Aplica operatorii genetici:
            # 1. Selectie
            self.selection()
            # 2. Incrucisare (Crossover)
            self.crossover()
            # 3. Mutatie
            self.mutation()

            end = time.time_ns()
            print("GENERETION " + str(generation_index) + " TIME : " + str((end - start) * 1e-9) + " seconds")
            with open("history/times/generation_times.txt", "a") as file:
                file.write("GEN " + str(generation_index) + " TIME : " + str((end - start) * 1e-9) + " seconds" +
                           "\n")
