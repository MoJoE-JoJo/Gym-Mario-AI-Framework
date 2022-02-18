from py4j.java_gateway import JavaGateway
import time
import os

from MAFGym.MAFEnv import MAFEnv
from MAFGym.util import readLevelFile

levelFilePath = os.path.dirname(os.path.realpath(__file__)) + "\\MAFGym\\levels\\original\\lvl-1.txt"
levelString = readLevelFile(levelFilePath)

gym1 = MAFEnv([levelString], 100, True)
gym2 = MAFEnv([levelString], 100, True)
gym3 = MAFEnv([levelString], 100, True)
gym4 = MAFEnv([levelString], 100, True)
gym5 = MAFEnv([levelString], 100, True)
gym6 = MAFEnv([levelString], 100, True)
gym7 = MAFEnv([levelString], 100, True)
gym8 = MAFEnv([levelString], 100, True)
gym9 = MAFEnv([levelString], 100, True)
gym10 = MAFEnv([levelString], 100, True)
gyms = {gym1, gym2, gym3, gym4, gym5, gym6, gym7, gym8, gym9, gym10}

action = [False, True, False, False, False]
done = False
for i in range(100):
    timestart = time.time()
    while not done:
        for gym in gyms:
            obs, reward, done, info = gym.step(action)
        #obs2, reward2, done2, info2 = gym2.step(action)
        #print(obs)
    #print(obs[0][0])
        #print(reward)
        #print(done)
        #print(info[0])
        #gymgym.render()
        #gym2.render()
    print(time.time()-timestart)

    for gym in gyms:
        gym.reset()
    done = False


