package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Program {
    private static List<Roll> Rolls;
    private static Enums.Mode Mode;
    private static String KeyFilename;
    private static String InputFileName;
    private static int TransitionCount;

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Generate key with 'keygen x key.file' where x > 3 is the number of rolls.");
            System.out.println("Encrypt a file with 'enc a.txt key.file'");
            System.out.println("Decrypt a file with 'dec a.txt key.file'");

            return;
        }

        ReadProperties();

        KeyFilename = args[2];

        if (args[0].compareToIgnoreCase("keygen") == 0) {
            Keygen(Integer.parseInt(args[1]));
            return;
        }

        InputFileName = args[1];
        CreateRolls();

        if (args[0].compareToIgnoreCase("enc") == 0)
            Mode = Enums.Mode.Enc;
        else if (args[0].compareToIgnoreCase("dec") == 0)
            Mode = Enums.Mode.Dec;
        else
            throw new Exception("Undefined Encryption Mode.");

        BusinessLogic businessLogic = new BusinessLogic(Rolls);

        businessLogic.TransformFile(InputFileName, InputFileName + "." + Mode, Mode);
    }

    private static void ReadProperties() throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        
        prop.load(new FileInputStream("Enigma.properties"));

        TransitionCount = Integer.parseInt(prop.getProperty("TransitionCount"));
    }

    private static void CreateRolls() throws Exception {
        Rolls = new ArrayList<Roll>();

        int rollKeylength = 256 + TransitionCount;

        byte[] definition = new byte[(int) new File(KeyFilename).length()];
        FileInputStream fileInputStream = new FileInputStream(KeyFilename);
        fileInputStream.read(definition);
        fileInputStream.close();

        if (definition.length % rollKeylength > 0)
            throw new Exception("Invalid Keysize");

        int rollCount = definition.length / rollKeylength;

        for (int rollNumber = 0; rollNumber < rollCount; rollNumber++) {
            List<Integer> transitions = new ArrayList<Integer>();

            for (int index = 0; index < TransitionCount; index++)
                transitions.add((int) definition[rollNumber * rollKeylength + 256 + index]); 

            byte[] singleRoll = new byte[256];
            for(int index = 0; index < 256; index ++)
                singleRoll[index] = definition[rollNumber * rollKeylength + index];

            Rolls.add(new Roll(singleRoll, transitions));
        }

        for (Roll roll : Rolls)
            roll.CheckInput();
    }

    private static void Keygen(int rollCount) throws Exception {
        
        if (rollCount < 4)
            throw new Exception("Not enough rolls.");

        Random random = new Random();

        if((new File(KeyFilename)).exists())
            Files.delete(Paths.get(KeyFilename));

        byte[] key = new byte[(256 + TransitionCount) * rollCount] ;

        for (int i = 0; i < rollCount; i++) {
            byte[] transform = new byte[256];
            for (int j = 0; j <= 255; j++)
                transform[j] = (byte) j;

            while (!IsTwisted(transform)) {
                for (int j = 0; j < 256 * 2; j++) {
                    int rand1 = random.nextInt(256);
                    int rand2 = random.nextInt(256);

                    byte temp = transform[rand1];
                    transform[rand1] = transform[rand2];
                    transform[rand2] = temp;
                }
            }

            for (int index = 0; index < 256; index++)
                key[(256 + TransitionCount) * i + index] = transform[index];

            List<Integer> transitions = new ArrayList<Integer>();

            while (transitions.size() < TransitionCount) 
            {
                int rand = random.nextInt(256);
                if (!transitions.contains(rand))
                    transitions.add(rand);
            }

            for (int index = 0; index < TransitionCount; index++)
                key[(256 + TransitionCount) * i + 256 + index] = (byte)(int) transitions.get(index);            
        }

        FileOutputStream fileOutputStream = new FileOutputStream(KeyFilename);
        fileOutputStream.write(key);
        fileOutputStream.close();

        System.out.println("Keys generated.");
        Thread.sleep(1000);
    }

    private static boolean IsTwisted(byte[] trans) {
        for (int i = 0; i <= 255; i++)
            if (trans[i] == i)
                return false;

        return true;
    }
}
