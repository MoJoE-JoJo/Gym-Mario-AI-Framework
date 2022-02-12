import subprocess
from py4j.java_gateway import JavaGateway
import threading
import keyboard
from MAFGym.util import readLevelFile
from MAFGym.MAFEnv import MAFEnv
import os

gateway = JavaGateway() 
marioGym = gateway.entry_point
subprocess.call(['MAFGym/RunJar.bat'])

def startMarioGym():
    marioGym.playGame("MAFGym/levels/original/lvl-1.txt", 20, 0, True)


x = threading.Thread(target=startMarioGym)
x.start()

while True:
    left, right, down, speed, jump = False, False, False, False, False

    if keyboard.is_pressed("left"):
        left = True
    if keyboard.is_pressed("right"):
        right = True
    if keyboard.is_pressed("down"):
        down = True
    if keyboard.is_pressed("a"):
        speed = True
    if keyboard.is_pressed("s"):
        jump = True
    marioGym.agentInput(left, right, down, speed, jump)