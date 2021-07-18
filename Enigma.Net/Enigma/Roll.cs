using System;
using System.Collections.Generic;
using System.Data;

namespace Enigma
{
    public class Roll
    {
        private byte Position { get; set; }
        private byte[] Transitions { get; }
        private List<int> TurnOverIndices { get; }
        private byte[] ReTransitions { get; }

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
                for(int j = 0; j < 256; j++)
                    if (Transitions[j] == i)
                        found = true;
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