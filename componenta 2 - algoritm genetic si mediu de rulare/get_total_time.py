def print_result(total_time):
    print(total_time, "secunde")
    print(total_time / 60, "minute")
    print((total_time / 60) / 60, "ore")
    print(((total_time / 60) / 60) / 24, "zile")


def get_total_time():
    total_real_time = 0
    total_played_time = 0

    with open("history/times/generation_times_total.txt", "r") as file:
        for line in file:
            if line.startswith("GEN"):
                current_time = float(line.split(":")[1].split("s")[0][1:])
                total_real_time += current_time
            elif line.startswith(" ---- chromosome"):
                current_time = float(line.split(":")[1].split("s")[0][1:])
                total_played_time += current_time

    print("Timp total rulat : ")
    print_result(total_real_time)
    print("")
    print("Timp total jucat : ")
    print_result(total_played_time)



def get_mean_time_first():
    total_real_time = 0
    total_played_time = 0
    generations = 0

    with open("history/times/generation_times_total.txt", "r") as file:
        for line in file:
            if line.startswith("GEN"):
                current_time = float(line.split(":")[1].split("s")[0][1:])
                total_real_time += current_time
                generations += 1
                if line.startswith("GEN 96 TIME"):
                    break
            elif line.startswith(" ---- chromosome"):
                current_time = float(line.split(":")[1].split("s")[0][1:])
                total_played_time += current_time

    print("Media primelor 97 de generatii : ")
    print_result(total_real_time / generations)
    print("")
    print("Media indivizilor din primele 97 de generatii : ")
    print_result(total_played_time / (30 * generations))



def get_mean_time_second():
    total_real_time = 0
    total_played_time = 0
    generations = 0
    found = False

    with open("history/times/generation_times_total.txt", "r") as file:
        for line in file:
            if found:
                if line.startswith("GEN"):
                    current_time = float(line.split(":")[1].split("s")[0][1:])
                    total_real_time += current_time
                    generations += 1
                elif line.startswith(" ---- chromosome"):
                    current_time = float(line.split(":")[1].split("s")[0][1:])
                    total_played_time += current_time

            if line.startswith("GEN 96 TIME"):
                found = True

    print("Media generatiilor dupa index 96 : ")
    print_result(total_real_time / generations)
    print("\n")
    print("Media indivizilor din generatiile cu index mai mare decat 96: ")
    print_result(total_played_time / (30 * generations))


get_total_time()
print("\n\n")
get_mean_time_first()
print("\n\n")
get_mean_time_second()

