import gym
from gym import spaces
import numpy as np
import subprocess
from py4j.java_gateway import JavaGateway


class MAFEnv(gym.Env):
  """Custom Environment that follows gym interface"""
  metadata = {'render.modes': ['human']}
  gateway = JavaGateway() 
  marioGym = gateway.entry_point
  useRender = False

  def __init__(self, levelFilePath, gameTime, initRender):
    super(MAFEnv, self).__init__()
    self.useRender = initRender
    # Define action and observation space
    # They must be gym.spaces objects
    # Example when using discrete actions:
    self.action_space = spaces.MultiBinary(5)
    # Example for using image as input:
    self.observation_space = spaces.Box(low=-100, high=100, shape=
                    (16, 16, 1), dtype=np.uint8)
    subprocess.call(['RunJar.bat'])
    self.marioGym.init(levelFilePath, gameTime, 0, self.useRender)


  def step(self, action):
    # Execute one time step within the environment
    LEFT,RIGHT,DOWN,SPEED,JUMP = action[0], action[1], action[2], action[3], action[4]
    returnVal = self.marioGym.step(LEFT,RIGHT,DOWN,SPEED,JUMP)
    return returnVal.getState(), returnVal.getReward(), returnVal.getDone(), returnVal.getInfo()

  def reset(self):
    # Reset the state of the environment to an initial state
    self.marioGym.reset(self.useRender)

  def render(self, mode='human', close=False):
    # Render the environment to the screen
    self.marioGym.render()