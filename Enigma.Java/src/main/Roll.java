package main;

import java.util.Collections;
import java.util.List;


public class Roll
 {
    private int position;          //This is the actual position of this roll starting at 0
    private byte[] transitions;     //This is the wiring of the roll: if Transitions[0] = 0x04 the value 0x00 will be mapped to 0x04
    private List<Integer> turnOverIndices;  //While rolling after each char encryption the next roll will also rotate, if these indices contain Position 
    private byte[] reTransitions;   //Reverted transitionlist for decryption
    private static final int  UNSIGNED_BYTE_MAX = 256;	
    private static final int  UNSIGNED_BYTE_OFFSET = 128;	//to calculate with unsigned bytes, this offset moves the range to [0,255]
    

    public Roll(byte[] transitions, List<Integer> turnOverIndices) {
        this.transitions = transitions;
        this.turnOverIndices = turnOverIndices;
        position = 0;

        reTransitions = new byte[UNSIGNED_BYTE_MAX];
        for (int i = -UNSIGNED_BYTE_OFFSET; i < UNSIGNED_BYTE_OFFSET; i++)
            reTransitions[transitions[i + UNSIGNED_BYTE_OFFSET] + UNSIGNED_BYTE_OFFSET] = (byte) i;
    }

    public void CheckInput(int transitionCount) throws Exception {
        if (transitions.length != UNSIGNED_BYTE_MAX)
            throw new Exception("Wrong Transition length ");

        for (int i = -UNSIGNED_BYTE_OFFSET; i < UNSIGNED_BYTE_OFFSET; i++) {
            boolean found = false;
            for (int j = 0; j < UNSIGNED_BYTE_MAX; j++)
            {
                if (transitions[j] == i)
                {
                    found = true;   
                    break;
                }
            }
            if (!found)
                throw new Exception("Transitions not 1-1 complete. Problem at " + i);
        }

        if (turnOverIndices.size() != transitionCount)
            throw new Exception("Wrong TurnOverIndices length ");

        Collections.sort(turnOverIndices);

        for (int i = 0; i < turnOverIndices.size() - 1; i++)
            if (turnOverIndices.get(i) == turnOverIndices.get(i + 1))
                throw new Exception("Turnoverindizes has doubles");
    }

    public byte Encrypt(byte input) {
        return transitions[((input + position + UNSIGNED_BYTE_OFFSET)%UNSIGNED_BYTE_MAX)];
    }

    public byte Decrypt(byte input) {
        return (byte) (reTransitions[input + UNSIGNED_BYTE_OFFSET] - position);
    }

    public boolean RollOn() {
        position = (position + 1) % UNSIGNED_BYTE_MAX;
        return turnOverIndices.contains(position);
    }
}
