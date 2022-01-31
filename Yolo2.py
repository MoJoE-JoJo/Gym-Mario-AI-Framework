from py4j.java_gateway import JavaGateway
import time
#import subprocess
#subprocess.call(['java', '-jar', 'Mario_AI_Framework.jar'])
#from subprocess import *
#process = Popen(['java', '-jar']+list('Mario_AI_Framework.jar'), stdout=PIPE, stderr=PIPE)
#import os
#os.system('java -jar Mario_AI_Framework.jar')
import subprocess
subprocess.call(['RunJar.bat'])

gateway = JavaGateway()                   # connect to the JVM
random = gateway.jvm.java.util.Random()   # create a java.util.Random instance
number1 = random.nextInt(10)              # call the Random.nextInt method
number2 = random.nextInt(10)
print(number1, number2)

addition_app = gateway.entry_point               # get the AdditionApplication instance
value = addition_app.addition(number1, number2) # call the addition method
print(value)