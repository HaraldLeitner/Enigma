using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Threading;

namespace Enigma
{
    public class Program
    {
        private static List<Roll> Rolls;
        private static Enums.Mode Mode;
        private static string KeyFilename;
        private static string InputFileName;
        private static int TransitionCount = int.Parse(ConfigurationManager.AppSettings["TransitionCount"]);

        public static void Main(string[] args)
        {
            if(args.Length != 3)
            {
                Console.WriteLine("Generate key with 'keygen x key.file' where x > 3 is the number of rolls.");
                Console.WriteLine("Encrypt a file with 'enc a.txt key.file'");
                Console.WriteLine("Decrypt a file with 'dec a.txt key.file'");

                return;
            }

            KeyFilename = args[2];

            if (args[0].ToLower() == "keygen")
            {
                Keygen(Int32.Parse(args[1]));
                return;
            }

            InputFileName = args[1];
            CreateRolls();

            if (args[0].ToLower() == "enc")
                Mode = Enums.Mode.Enc;
            else if (args[0].ToLower() == "dec")
                Mode = Enums.Mode.Dec;
            else
                throw new ArgumentOutOfRangeException("Undefined Encryption Mode.");

            BusinessLogic businessLogic = new BusinessLogic(Rolls);

            businessLogic.TransformFile(InputFileName, InputFileName + "." + Mode, Mode);
        }

        private static void CreateRolls()
        {
            Rolls = new List<Roll>();

            int rollKeylength = 256 + TransitionCount; 

            List<byte> definition = File.ReadAllBytes(KeyFilename).ToList();

            if(definition.Count % rollKeylength > 0)
                throw new ArgumentOutOfRangeException("Invalid Keysize");

            int rollCount = definition.Count / rollKeylength;

            for (int rollNumber = 0; rollNumber < rollCount; rollNumber++)
            {
                List<int>transitions = new List<int>();
                foreach (byte transition in definition.GetRange(rollNumber * rollKeylength + 256, TransitionCount).ToArray())
                    transitions.Add(transition);

                Rolls.Add(new Roll(definition.GetRange(rollNumber * rollKeylength, 256).ToArray(), transitions));
            }

            foreach (Roll roll in Rolls)
                roll.CheckInput();
        }

        private static void Keygen(int rollCount)
        {
            if (rollCount < 4)
                throw new ArgumentOutOfRangeException("Not enough rolls.");

            Random random = new Random((int)DateTime.Now.Ticks);
            File.Delete(KeyFilename);
            List<byte> key = new List<byte>();

            for (int i = 0; i < rollCount; i++)
            {
                byte[] transform = new byte[256];
                for (int j = 0; j <= 255; j++)
                    transform[j] = (byte)j;

                while (!IsTwisted(transform))
                {
                    for (int j = 0; j < 256 * 2; j++)
                    {
                        int rand1 = random.Next(0, 256);
                        int rand2 = random.Next(0, 256);

                        byte temp = transform[rand1];
                        transform[rand1] = transform[rand2];
                        transform[rand2] = temp;
                    }
                }

                key.AddRange(transform);

                List<byte>transitions = new List<byte>();
                while (transitions.Count < TransitionCount)
                {
                    byte rand = (byte)random.Next(0, 256);
                    if(!transitions.Contains(rand))
                        transitions.Add(rand);
                }
                key.AddRange(transitions);
            }

            File.WriteAllBytes(KeyFilename, key.ToArray());

            Console.Write("Keys generated.");
            Thread.Sleep(1000);
        }

        private static bool IsTwisted(byte[] trans)
        {
            for(int i = 0; i <= 255; i++)
                if (trans[i] == i)
                    return false;

            return true;
        }
    }
}
