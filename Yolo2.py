from py4j.java_gateway import JavaGateway
import time

from MAFEnv import MAFEnv
#import subprocess
#subprocess.call(['java', '-jar', 'Mario_AI_Framework.jar'])
#from subprocess import *
#process = Popen(['java', '-jar']+list('Mario_AI_Framework.jar'), stdout=PIPE, stderr=PIPE)
#import os
#os.system('java -jar Mario_AI_Framework.jar')
#import subprocess
#subprocess.call(['RunJar.bat'])

#gateway = JavaGateway()                   # connect to the JVM

#marioGym = gateway.entry_point               # get the AdditionApplication instance
#LEFT,RIGHT,DOWN,SPEED,JUMP = False,False,False,False,False
#returnVal = marioGym.step(LEFT,RIGHT,DOWN,SPEED,JUMP)

#int_array = returnVal.getState()
#info_list = returnVal.getInfo()
#for i in range(16):
#    print(int_array[i][i])
#marioGym.reset()

gymgym = MAFEnv("levels/original/lvl-1.txt", 10, True)
action = [False, True, False, False, False]
done = False
for i in range(100):
    timestart = time.time()
    while not done:
        obs, reward, done, info = gymgym.step(action)
    #print(obs[0][0])
        #print(reward)
        #print(done)
        #print(info[0])
        #gymgym.render()
    print(time.time()-timestart)

    gymgym.reset()
    done = False


