using System.Collections.Generic;
using System.IO;

namespace Enigma
{
    public class BusinessLogic
    {
        private List<Roll> Rolls { get; set; }
        private List<Roll> RollsReverse { get; set; }

        public BusinessLogic(List<Roll>rolls)
        {
            Rolls = rolls;

            RollsReverse = new List<Roll>();
            RollsReverse.AddRange(rolls);
            RollsReverse.Reverse();
        }

        public void TransformFile(string inputFfilename, string outputFilename, Enums.Mode mode)
        {
            const int buffersize = 65536;

            using (FileStream fileInStream = new FileStream(inputFfilename, FileMode.Open, FileAccess.Read))
            {
                using (FileStream fileOutStream = new FileStream(outputFilename, FileMode.Create, FileAccess.Write))
                {
                    byte[] buffer = new byte[buffersize];
                    int readCount = 0;
                    while ((readCount = fileInStream.Read(buffer, 0, buffersize)) > 0)
                    {
                        fileOutStream.Write(TransformByteArray(buffer, mode), 0, readCount);
                    }
                }
            }
        }

        public byte[] TransformByteArray(byte[] input, Enums.Mode mode)
        {
            byte[] output = new byte[input.Length];

            for(int i  = 0; i < input.Length; i++)
            {
                byte outByte = input[i];

                if (mode == Enums.Mode.Enc)
                    foreach (Roll roll in Rolls)
                        outByte = roll.Enc(outByte);

                else if (mode == Enums.Mode.Dec)
                    foreach (Roll roll in RollsReverse)
                        outByte = roll.Dec(outByte);


                output[i] = outByte;
                RollOn();
            }

            return output;
        }

        private void RollOn()
        {
            foreach(Roll roll in Rolls)
            {
                if(!roll.RollOn())
                    break;
            }
        }
    }
}
