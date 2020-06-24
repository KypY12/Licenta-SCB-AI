from os import system

from utils.dump import *
from utils.load import *


class GameRunner:

    def __init__(self):
        self.config_data = ()
        self.game_results = ()
        self.engine_index = 0

        pass

    def set_engine_index(self, engine_index):
        self.engine_index = engine_index

    def set_config_data(self, data):
        self.config_data = data

    def set_config_data_file(self):
        settings_data, army_data = self.config_data
        dump_config_data(settings_data, army_data, self.engine_index)

    def get_game_results(self):
        return self.game_results

    def get_results_data_file(self):
        game_results, ai_index, enemy_ai_index = load_game_results(self.engine_index)

        ai_results = dict()
        enemy_results = dict()
        for key in game_results.keys():
            ai_results[key] = game_results[key][ai_index]
            enemy_results[key] = game_results[key][enemy_ai_index]

        self.game_results = (ai_results, enemy_results)

    def run_game(self, test_game=None):
        self.set_config_data_file()
        if test_game is not None:
            system(RUN_GAME_COMMAND_0)
        else:
            system(RUN_GAME_HEADLESS_COMMAND[self.engine_index])
        self.get_results_data_file()
