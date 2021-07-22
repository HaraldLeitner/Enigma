using System;
using System.Collections.Generic;
using System.Data;

namespace Enigma
{
    public class Roll
    {
        private byte Position { get; set; }     //This is the actual position of this roll starting at 0
        private byte[] Transitions { get; }     //This is the wiring of the roll: if Transitions[0] = 0x04 the value 0x00 will be mapped to 0x04
        private List<int> TurnOverIndices { get; }  //While rolling after each char encryption the next roll will also rotate, if these indices contain Position
        private byte[] ReTransitions { get; }   //Reverted transitionlist for decryption

        public Roll(byte[] transitions, List<int> turnOverIndices)
        {
            Transitions = transitions;
            TurnOverIndices = turnOverIndices;
            Position = 0;

            ReTransitions = new byte[256];
            for (int i = 0; i < 256; i++)
                ReTransitions[Transitions[i]] = (byte)i;
        }

        public void CheckInput()
        {
            if (Transitions.Length != 256)
                throw new ArgumentOutOfRangeException("Wrong Transition length ");

            for (int i = 0; i < 256; i++)
            {
                bool found = false;
                for (int j = 0; j < 256; j++)
                    if (Transitions[j] == i)
                    {
                        found = true;
                        break;
                    }

                if(!found)
                    throw new ConstraintException("Transitions not 1-1 complete");
            }

            if (TurnOverIndices.Count != 53)
                throw new ArgumentOutOfRangeException("Wrong TurnOverIndices length ");

            TurnOverIndices.Sort();
            for(int i = 0; i < TurnOverIndices.Count - 1; i++)
                if(TurnOverIndices[i] == TurnOverIndices[i+1])
                    throw new ConstraintException("Turnoverindizes has doubles");
        }

        public byte Enc(byte input)
        {
            return Transitions[(byte)(input + Position)];
        }

        public byte Dec(byte input)
        {
            return (byte)(ReTransitions[input] - Position);
        }

        public bool RollOn()
        {
            ++Position;
            return TurnOverIndices.Contains(Position);
        }
    }
}