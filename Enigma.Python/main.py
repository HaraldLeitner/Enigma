from configparser import ConfigParser
import os
import sys
from random import random, randbytes, randint
from time import sleep

from BusinessLogic import BusinessLogic
from Enums import Mode
from Roll import Roll


class Program:
    def __init__(self, transitioncount=0):
        self._mode = None
        self._rolls = []
        self._inputFilename = None
        self._keyFilename = None
        self._transitionCount = None
        config = ConfigParser()
        config.read("enigma.ini")
        if transitioncount < 1:
            self._transitionCount = config.getint("DEFAULT", "TransitionCount")
        else:
            self._transitionCount = transitioncount

    def main(self):
        if len(sys.argv) != 4:
            print("Generate key with 'keygen x key.file' where x > 3 is the number of rolls.")
            print("Encrypt a file with 'enc a.txt key.file'")
            print("Decrypt a file with 'dec a.txt key.file'")
            exit(1)

        self.run_main(sys.argv[1], sys.argv[2], sys.argv[3])

    def run_main(self, arg1, arg2, arg3):

        self._keyFilename = arg3

        if arg1 == "keygen":
            self.keygen(int(arg2))
            return

        self._inputFilename = arg2

        if arg1.lower() == 'enc':
            self._mode = Mode.ENC
        elif arg1 == 'dec':
            self._mode = Mode.DEC
        else:
            raise Exception("Undefined Encryption Mode.")

        self.create_rolls()
        BusinessLogic(self._rolls).transform_file(self._inputFilename, self._inputFilename + '.' + self._mode.name,
                                                  self._mode)

    def keygen(self, roll_count):
        if roll_count < 4:
            raise Exception("Not enough rolls.")

        if os.path.exists(self._keyFilename):
            os.remove(self._keyFilename)

        key = bytearray()

        for i in range(roll_count):
            transform = bytearray(256)
            for j in range(256):
                transform[j] = j

            while not self.is_twisted(transform):
                for j in range(256):
                    rand1 = randint(0, 255)
                    rand2 = randint(0, 255)

                    temp = transform[rand1]
                    transform[rand1] = transform[rand2]
                    transform[rand2] = temp

            key += transform

            transitions = bytearray()
            while len(transitions) < self._transitionCount:
                rand = randint(0, 255)
                if not transitions.count(rand):
                    transitions.append(rand)

            key += transitions

        file = open(self._keyFilename, 'wb')
        file.write(key)
        file.close()

        print("Keys generated.")
        sleep(1)

    def is_twisted(self, trans):
        for i in range(256):
            if trans[i] == i:
                return 0

        return 1

    def create_rolls(self):
        roll_keylength = 256 + self._transitionCount
        file = open(self._keyFilename, 'rb')
        key = file.read()
        file.close()

        if len(key) % roll_keylength:
            raise Exception('Invalid Keysize')

        rollcount = int(len(key) / roll_keylength)

        for rollNumber in range(rollcount):
            self._rolls.append(Roll(key[rollNumber * roll_keylength: rollNumber * roll_keylength + 256],
                                    key[
                                    rollNumber * roll_keylength + 256: rollNumber * roll_keylength + 256 + self._transitionCount]))

        for roll in self._rolls:
            roll.check_input(self._transitionCount)


if __name__ == '__main__':
    Program().main()
