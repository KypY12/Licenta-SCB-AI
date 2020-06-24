from globals import *
from os.path import join
from pandas import read_csv


def load_config_data(engine_index):
    settings_data = dict()
    army_data = dict()

    settings_on = True

    with open(join(CONFIG_PATH[engine_index], CONFIG_FILENAME), "r") as file:
        for line in file:
            if line.startswith("---"):
                if "END FOR GA TRAINING" in line:
                    break
                else:
                    continue

            if line.startswith("==="):
                if "SETTINGS" in line:
                    settings_on = True
                elif "ARMY" in line:
                    settings_on = False
                continue

            content = line.split(":")
            name = content[0].strip()
            value = int(content[1].strip())
            if settings_on:
                settings_data[name] = value
            else:
                army_data[name] = value

    return settings_data, army_data


def load_game_results(engine_index):
    game_results = read_csv(join(GAME_RESULTS_PATH[engine_index], GAME_RESULTS_FILENAME))

    ai_index = 0
    enemy_ai_index = 0

    for index in range(0, len(game_results["name"])):
        if game_results["name"][index] == AI_NAME:
            ai_index = index
        else:
            enemy_ai_index = index

    return game_results, ai_index, enemy_ai_index


def load_intervals():
    intervals_data = dict()

    with open(join(INTERVALS_PATH, INTERVALS_FILENAME), "r") as file:
        for line in file:
            if line.startswith("---") or line.startswith("==="):
                continue

            content = line.split(":")
            name = content[0].strip()
            value = (int(content[1].strip()), int(content[2].strip()))
            intervals_data[name] = value

    return intervals_data


def load_population():
    population = []
    generation_index = 0
    with open(join(LOAD_POPULATION_PATH, LOAD_POPULATION_FILENAME), "r") as file:
        content = file.read().split(":")
        generation_index = int(content[0])
        population_str = content[2].split("|")
        population = [[int(gene) for gene in chromosome.split(",")] for chromosome in population_str]

    return generation_index, population
