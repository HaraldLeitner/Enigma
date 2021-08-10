class Roll:

    def __init__(self, transitions, turn_over_indices):
        self._transitions = transitions
        self._turn_over_indices = turn_over_indices
        self._re_transitions = bytearray(256)

        for x in self._transitions:
            self._re_transitions[self._transitions[x]] = x

        self._position = 0

    def check_input(self, turnover_indices_count):
        if len(self._transitions) != 256:
            raise ValueError("Wrong Transition length ")

        for i in range(256):
            found = 0
            for j in self._transitions:
                if self._transitions[j] == i:
                    found = 1
                    continue

            if not found:
                raise ValueError("Transitions not 1-1 complete")

        if len(self._turn_over_indices) != turnover_indices_count:
            raise ValueError("Wrong TurnOverIndices length ")

        for i in range(len(self._turn_over_indices) - 1):
            if self._turn_over_indices[i] == self._turn_over_indices[i + 1]:
                raise ValueError("turn_over_indices has doubles")

    def encrypt(self, buffer, index):
        buffer[index] = self._transitions[(buffer[index] + self._position) & 0xff]

    def decrypt(self, buffer, index):
        buffer[index] = (self._re_transitions[int(buffer[index])] - self._position) & 0xff

    def roll_on(self):
        self._position = (self._position + 1) & 0xff
        return self._turn_over_indices.count(self._position)
