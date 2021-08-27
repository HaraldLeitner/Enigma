from Enums import Mode


class BusinessLogic:
    def __init__(self, rolls):
        self._rolls = rolls
        self._rolls_reverse = rolls.copy()
        self._rolls_reverse.reverse()

    def transform_file(self, infile_name, outfile_name, mode):
        buffer_size = 65536

        with open(infile_name, 'rb', buffer_size) as in_file, open(outfile_name, 'wb', buffer_size) as out_file:

            buffer = bytearray(in_file.read(buffer_size))

            while len(buffer):
                self.transform_buffer(buffer, mode)
                out_file.write(buffer)
                buffer = bytearray(in_file.read(buffer_size))

    def transform_buffer(self, buffer, mode):
        if mode == Mode.ENC:
            for i in range(len(buffer)):
                for roll in self._rolls:
                    roll.encrypt(buffer, i)
                self.roll_on()

        if mode == Mode.DEC:
            for i in range(len(buffer)):
                for roll in self._rolls_reverse:
                    roll.decrypt(buffer, i)
                self.roll_on()

    def roll_on(self):
        for roll in self._rolls:
            if not roll.roll_on():
                break
