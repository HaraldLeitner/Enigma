class Roll:

    def __init__(self, transitions, turnOverIndices):
        self._transitions = transitions
        self._turn_over_indices = turnOverIndices
        self._re_transitions = bytearray(256)

        for x in self._transitions:
            self._re_transitions[self._transitions[x]] = x

        self._position = 0


    def check_input(self, turnoverIndicesCount):
        if len(self._transitions) != 256:
            raise ValueError ("Wrong Transition length ")

        for i in range(256):
            found = 0
            for j in self._transitions:
                if self._transitions[j] == i:
                    found = 1
                    continue

            if not found:
                raise ValueError ("Transitions not 1-1 complete");


        if len(self._turn_over_indices) != turnoverIndicesCount:
            raise ValueError("Wrong TurnOverIndices length ");

        for i in range (len(self._turn_over_indices) - 1):
            if self._turn_over_indices[i] == self._turn_over_indices[i+1]:
                raise ValueError("Turnoverindizes has doubles");

    def encrypt(self, input, index):
        input[index] = self._transitions[int((input[index] + self._position) % 256)]

    def decrypt(self, input, index):
        input[index] = self._re_transitions[int(input[index])] - self._position

    def roll_on(self):
        ++self._position
        return self._turn_over_indices.count(self._position)