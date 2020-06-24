from genetic_algorithm import GeneticAlgorithm
from utils.load import load_config_data, load_intervals


intervals = load_intervals()
settings_data, army_data = load_config_data(0)

# Daca se ruleaza pentru prima data algoritmul (prima sesiune de rulare), se seteaza lead_file=False.
# Pentru a rula in sesiuni ulterioare, se seteaza load_file=True
# si se muta ultima populatie in fisierul din load_directory.   
ga = GeneticAlgorithm(population_size=30,
                      mutation_rate=0.01,
                      crossover_rate=0.3,
                      load_file=True)

ga.init_parameters(settings_names=settings_data.keys(),
                   army_names=army_data.keys(),
                   settings_intervals=intervals)

ga.init_population()

ga.execute(generations=1000)
