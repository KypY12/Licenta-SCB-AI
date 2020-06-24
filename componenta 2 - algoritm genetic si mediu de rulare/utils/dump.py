from os.path import join
from globals import *


def dump_config_data(settings_data, army_data, engine_index):
    file_content = []
    with open(join(CONFIG_PATH[engine_index], CONFIG_FILENAME), "r") as file:
        for line in file:
            file_content.append(line)

    with open(join(CONFIG_PATH[engine_index], CONFIG_FILENAME), "w") as file:
        for line in file_content:
            if "---" in line or "===" in line:
                file.write(line)
            else:
                name = line.split(":")[0]
                if name in settings_data.keys():
                    file.write(name + ":" + str(settings_data[name]) + '\n')
                elif name in army_data.keys():
                    file.write(name + ":" + str(army_data[name]) + '\n')
                else:
                    file.write(line)


def dump_current_population(filename_additions, generation, population):
    filename = HISTORY_FILENAME + "_" + filename_additions + HISTORY_FILE_EXT
    with open(join(HISTORY_PATH, filename), "a") as file:
        dump_content = str(generation) + \
                       ":p:" + \
                       "|".join([",".join([str(gene) for gene in chromosome]) for chromosome in population])
        file.write(dump_content + "\n")


def dump_current_game_results(filename_additions, game_results):
    filename = HISTORY_FILENAME + "_" + filename_additions + HISTORY_FILE_EXT
    with open(join(HISTORY_PATH, filename), "a") as file:
        for result in game_results:
            # Rezultatele ai-ului
            for key, value in result[0].items():
                file.write(str(key) + "=" + str(value) + "|")

            file.write("{}")

        file.write("\n")


def dump_current_fitness(filename_additions, generation, fitness_values):
    filename = HISTORY_FILENAME + "_" + filename_additions + HISTORY_FILE_EXT
    with open(join(HISTORY_PATH, filename), "a") as file:
        dump_content = str(generation) + \
                       ":f:" + \
                       ",".join([str(fit_val) for fit_val in fitness_values])
        file.write(dump_content + "\n")
