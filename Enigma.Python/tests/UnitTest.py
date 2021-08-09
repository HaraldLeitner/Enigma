import unittest

from BusinessLogic import BusinessLogic
from Enums import Mode
from Roll import Roll


class MyTestCase(unittest.TestCase):
    def setUp(self):
        self.trans_linear = bytearray(256)  # here every char is mapped to itself
        self.trans_linear_invert = bytearray(256)  # match the first to the last etc
        self.trans_shift_1 = bytearray(256)  # 'a' is mapped to 'b' etc
        self.trans_shift_2 = bytearray(256)  # 'a' is mapped to 'c' etc

        self.businesslogic_encode = None
        self.businesslogic_decode = None

        self.encrypted_message = bytearray()
        self.decrypted_message = bytearray()

        self.init_test_rolls()

    def init_test_rolls(self):
        for i in range(256):
            self.trans_linear[i] = i
            self.trans_linear_invert[i] = 255 - i
            self.trans_shift_1[i] = (i + 1) % 256
            self.trans_shift_2[i] = (i + 2) % 256

    def init_business_logic(self, transitions, turnovers):
        rolls_encrypt = []
        rolls_decrypt = []

        for index in range(len(transitions)):
            rolls_encrypt.append(Roll(transitions[index], turnovers[index]))
            rolls_decrypt.append(Roll(transitions[index], turnovers[index]))

        self.businesslogic_encode = BusinessLogic(rolls_encrypt)
        self.businesslogic_decode = BusinessLogic(rolls_decrypt)

    def crypt(self, msg):
        self.encrypted_message = bytearray(msg)
        self.businesslogic_encode.transform_buffer(self.encrypted_message, Mode.ENC)
        self.decrypted_message = bytearray(self.encrypted_message)
        self.businesslogic_decode.transform_buffer(self.decrypted_message, Mode.DEC)

    def test_one_byte_one_roll_linear(self):
        for i in range(256):
            self.init_business_logic([self.trans_linear], [[0]])
            self.crypt([i])
            self.assertEqual(i, self.encrypted_message[0])
            self.assertEqual(i, self.decrypted_message[0])

    def test_one_byte_one_roll_shift_one(self):
        for i in range(256):
            self.init_business_logic([self.trans_shift_1], [[0]])
            self.crypt([i])
            self.assertEqual((i + 1) % 256, self.encrypted_message[0])
            self.assertEqual(i, self.decrypted_message[0])

    def test_one_byte_one_roll_shift_two(self):
        for i in range(256):
            self.init_business_logic([self.trans_shift_2], [[0]])
            self.crypt([i])
            self.assertEqual((i + 2) % 256, self.encrypted_message[0])
            self.assertEqual(i, self.decrypted_message[0])

    def test_two_byte_one_roll_linear(self):
        for i in range(256):
            self.init_business_logic([self.trans_linear], [[0]])
            self.crypt([i, (i + 1) % 256])
            self.assertEqual(i, self.encrypted_message[0])
            self.assertEqual((i + 2) % 256, self.encrypted_message[1])
            self.assertEqual(i, self.decrypted_message[0])
            self.assertEqual((i + 1) % 256, self.decrypted_message[1])

    def test_two_byte_one_roll_shift1(self):
        for i in range(256):
            self.init_business_logic([self.trans_shift_1], [[0]])
            self.crypt([i, (i + 1) % 256])
            self.assertEqual((i + 1) % 256, self.encrypted_message[0])
            self.assertEqual((i + 3) % 256, self.encrypted_message[1])
            self.assertEqual(i, self.decrypted_message[0])
            self.assertEqual((i + 1) % 256, self.decrypted_message[1])

    def test_two_byte_one_roll_invert(self):
        for i in range(256):
            self.init_business_logic([self.trans_linear_invert], [[0]])
            self.crypt([i, i])
            self.assertEqual(255 - i, self.encrypted_message[0])
            self.assertEqual((256 + 255 - i - 1) & 0xff, self.encrypted_message[1])
            self.assertEqual(i, self.decrypted_message[0])
            self.assertEqual(i, self.decrypted_message[1])

    def test_two_byte_two_roll_linear(self):
        for i in range(256):
            self.init_business_logic([self.trans_linear, self.trans_linear], [[0], [0]])
            self.crypt([i, (i + 1) & 0xff])
            self.assertEqual(i, self.encrypted_message[0])
            self.assertEqual((i + 2) & 0xff, self.encrypted_message[1])
            self.assertEqual(i, self.decrypted_message[0])
            self.assertEqual((i + 1) & 0xff, self.decrypted_message[1])

    def test_two_byte_two_roll_shift1(self):
        for i in range(256):
            self.init_business_logic([self.trans_shift_1, self.trans_shift_1], [[0], [0]])
            self.crypt([i, (i + 1) & 0xff])
            self.assertEqual((i + 2) & 0xff, self.encrypted_message[0])
            self.assertEqual((i + 4) & 0xff, self.encrypted_message[1])
            self.assertEqual(i, self.decrypted_message[0])
            self.assertEqual((i + 1) & 0xff, self.decrypted_message[1])

    def test_two_byte_two_roll_shift2(self):
        self.init_business_logic([self.trans_shift_2, self.trans_shift_2], [[0], [0]])
        self.crypt([7, 107])
        self.assertEqual(11, self.encrypted_message[0])
        self.assertEqual(112, self.encrypted_message[1])
        self.assertEqual(7, self.decrypted_message[0])
        self.assertEqual(107, self.decrypted_message[1])

    def test_two_byte_two_roll_invert(self):
        for i in range(256):
            self.init_business_logic([self.trans_linear_invert, self.trans_linear_invert], [[0], [0]])
            self.crypt([i, (i + 1) & 0xff])
            self.assertEqual(i, self.encrypted_message[0])
            self.assertEqual((i + 2) & 0xff, self.encrypted_message[1])
            self.assertEqual(i, self.decrypted_message[0])
            self.assertEqual((i + 1) & 0xff, self.decrypted_message[1])



if __name__ == '__main__':
    unittest.main()
