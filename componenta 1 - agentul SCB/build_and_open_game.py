import pyautogui
import os
import shutil
import autogui
import time


autogui.setWindow("IntelliJ IDEA")
time.sleep(0.2)

# Save files
pyautogui.hotkey("ctrl", "s")

# Build IntelliJ artifacts
pyautogui.hotkey("ctrl","alt","num0")
pyautogui.press("down")
pyautogui.press("enter")
time.sleep(2)

# Move .jar to engine AI folder
build_item_location = "C:/Users/alexd/IdeaProjects/SpringJavaAI-2.0/out/artifacts/JavaAI___2_0_jar/JavaAI - 2.0.jar"

engine_ai_location = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32/AI/Skirmish/SCB/0.1/SkirmishAI.jar"

engine_ai_location_0 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_0/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_1 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_1/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_2 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_2/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_3 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_3/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_4 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_4/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_5 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_5/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_6 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_6/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_7 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_7/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_8 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_8/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_9 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_9/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_10 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_10/AI/Skirmish/SCB/0.1/SkirmishAI.jar"
engine_ai_location_11 = "C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32_11/AI/Skirmish/SCB/0.1/SkirmishAI.jar"


shutil.copyfile(build_item_location, engine_ai_location)

shutil.copyfile(build_item_location, engine_ai_location_0)
shutil.copyfile(build_item_location, engine_ai_location_1)
shutil.copyfile(build_item_location, engine_ai_location_2)
shutil.copyfile(build_item_location, engine_ai_location_3)
shutil.copyfile(build_item_location, engine_ai_location_4)
shutil.copyfile(build_item_location, engine_ai_location_5)
shutil.copyfile(build_item_location, engine_ai_location_6)
shutil.copyfile(build_item_location, engine_ai_location_7)
shutil.copyfile(build_item_location, engine_ai_location_8)
shutil.copyfile(build_item_location, engine_ai_location_9)
shutil.copyfile(build_item_location, engine_ai_location_10)
shutil.copyfile(build_item_location, engine_ai_location_11)

time.sleep(10)

# Open the game
open_cmd = '"C:/Users/alexd/Documents/My Games/Spring/engine/spring-{maintenance}104.0.1-1466-g9ee29da_win32/spring.exe" run_scripts/script.txt'
os.system(open_cmd)
