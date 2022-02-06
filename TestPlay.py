import subprocess
from py4j.java_gateway import JavaGateway
import threading
import keyboard

gateway = JavaGateway() 
marioGym = gateway.entry_point
subprocess.call(['RunJar.bat'])



def startMarioGym():
    marioGym.playGame("levels/original/lvl-15.txt", 20, 0, True)


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