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
                        TransformByteArray(buffer, mode);
                        fileOutStream.Write(buffer, 0, readCount);
                    }
                }
            }
        }

        public void TransformByteArray(byte[] input, Enums.Mode mode)
        {
            if (mode == Enums.Mode.Encode)
            {
                for (int i = 0; i < input.Length; i++)
                {
                    foreach (Roll roll in Rolls)
                        input[i] = roll.Encrypt(input[i]);
                    RollOn();
                }
            }

            if (mode == Enums.Mode.Decode)
            {
                for (int i = 0; i < input.Length; i++)
                {
                    foreach (Roll roll in RollsReverse)
                        input[i] = roll.Decrypt(input[i]);
                    RollOn();
                }
            }
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
