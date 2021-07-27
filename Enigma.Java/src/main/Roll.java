package main;

import java.util.Collections;
import java.util.List;


public class Roll
 {
    private int Position;          //This is the actual position of this roll starting at 0
    private byte[] Transitions;     //This is the wiring of the roll: if Transitions[0] = 0x04 the value 0x00 will be mapped to 0x04
    private List<Integer> TurnOverIndices;  //While rolling after each char encryption the next roll will also rotate, if these indices contain Position 
    private byte[] ReTransitions;   //Reverted transitionlist for decryption

    public Roll(byte[] transitions, List<Integer> turnOverIndices) {
        Transitions = transitions;
        TurnOverIndices = turnOverIndices;
        Position = 0;

        ReTransitions = new byte[256];
        for (int i = -128; i < 128; i++)
            ReTransitions[Transitions[i + 128] + 128] = (byte) i;
    }

    public void CheckInput(int transitionCount) throws Exception {
        if (Transitions.length != 256)
            throw new Exception("Wrong Transition length ");

        for (int i = -128; i < 128; i++) {
            boolean found = false;
            for (int j = 0; j < 256; j++)
            {
                if (Transitions[j] == i)
                {
                    found = true;   
                    break;
                }
            }
            if (!found)
                throw new Exception("Transitions not 1-1 complete. Problem at " + i);
        }

        if (TurnOverIndices.size() != transitionCount)
            throw new Exception("Wrong TurnOverIndices length ");

        Collections.sort(TurnOverIndices);

        for (int i = 0; i < TurnOverIndices.size() - 1; i++)
            if (TurnOverIndices.get(i) == TurnOverIndices.get(i + 1))
                throw new Exception("Turnoverindizes has doubles");
    }

    public byte Enc(byte input) {
        return Transitions[((input + Position + 128)%256)];
    }

    public byte Dec(byte input) {
        return (byte) (ReTransitions[input + 128] - Position);
    }

    public boolean RollOn() {
        Position = (Position + 1) % 256;
        return TurnOverIndices.contains(Position);
    }
}
