import os
import sys
from argparse import ArgumentParser
from configparser import ConfigParser
from random import randint
from time import sleep

from BusinessLogic import BusinessLogic
from Enums import Mode
from Roll import Roll


class Program:
    def __init__(self, transition_count=0):
        self._rolls = []
        self._transitionCount = None
        config = ConfigParser()
        config.read("enigma.ini")
        if transition_count < 1:
            self._transitionCount = config.getint("DEFAULT", "TransitionCount")
        else:
            self._transitionCount = transition_count

    def parse_and_run(self, args):
        parser = ArgumentParser(description="Enigma written in python")

        subparsers = parser.add_subparsers()

        keygen_parser = subparsers.add_parser('keygen')
        keygen_parser.add_argument("roll_count", type=int, help="Number of rolls for keygen")
        keygen_parser.add_argument("key_file", type=str, help="Key file to be generated or to be used to encode/decode")
        keygen_parser.set_defaults(func=self.keygen)

        crypt_parser = subparsers.add_parser("enc")
        crypt_parser.add_argument("input_file", type=str, help="File to be encrypted")
        crypt_parser.add_argument("key_file", type=str, help="Key file to be generated or to be used to encode")
        crypt_parser.set_defaults(func=self.encrypt)

        decrypt_parser = subparsers.add_parser("dec")
        decrypt_parser.add_argument("input_file", type=str, help="File to be decrypted")
        decrypt_parser.add_argument("key_file", type=str, help="Key file to be generated or to be used to decode")
        decrypt_parser.set_defaults(func=self.decrypt)

        args = parser.parse_args(args)
        args.func(args)

    def main(self):
        self.parse_and_run(sys.argv[1:])

    def encrypt(self, parser):
        self.start(Mode.ENC, parser)

    def decrypt(self, parser):
        self.start(Mode.DEC, parser)

    def start(self, mode, parser):
        self.create_rolls(parser.key_file)
        BusinessLogic(self._rolls).transform_file(parser.input_file, parser.input_file + '.' + mode.name, mode)

    def keygen(self, parser):
        if parser.roll_count < 4:
            raise Exception("Not enough rolls.")

        if os.path.exists(parser.key_file):
            os.remove(parser.key_file)

        key = bytearray()

        for i in range(parser.roll_count):
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

        file = open(parser.key_file, 'wb')
        file.write(key)
        file.close()

        print("Keys generated.")
        sleep(1)

    def is_twisted(self, trans):
        for i in range(256):
            if trans[i] == i:
                return 0

        return 1

    def create_rolls(self, keyfile):
        roll_key_length = 256 + self._transitionCount
        file = open(keyfile, 'rb')
        key = file.read()
        file.close()

        if len(key) % roll_key_length:
            raise Exception('Invalid key_size')

        roll_count = int(len(key) / roll_key_length)

        for rollNumber in range(roll_count):
            self._rolls.append(Roll(key[rollNumber * roll_key_length: rollNumber * roll_key_length + 256],
                                    key[
                                    rollNumber * roll_key_length + 256: rollNumber * roll_key_length + 256
                                                                        + self._transitionCount]))

        for roll in self._rolls:
            roll.check_input(self._transitionCount)


if __name__ == '__main__':
    Program().main()
