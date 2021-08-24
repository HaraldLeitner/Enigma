package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Program {
	private static List<Roll> rolls;
	private static Enums.Mode mode;
	private static String keyFilename;
	private static String inputFileName;
	private static int transitionCount;

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Generate key with 'keygen x key.file' where x > 3 is the number of rolls.");
			System.out.println("Encrypt a file with 'enc a.txt key.file'");
			System.out.println("Decrypt a file with 'dec a.txt key.file'");

			return;
		}

		ReadProperties();

		keyFilename = args[2];

		if (args[0].compareToIgnoreCase("keygen") == 0) {
			Keygen(Integer.parseInt(args[1]));
			return;
		}

		inputFileName = args[1];
		CreateRolls();

		if (args[0].compareToIgnoreCase("enc") == 0)
			mode = Enums.Mode.Encode;
		else if (args[0].compareToIgnoreCase("dec") == 0)
			mode = Enums.Mode.Decode;
		else
			throw new Exception("Undefined Encryption Mode.");

		BusinessLogic businessLogic = new BusinessLogic(rolls);

		businessLogic.TransformFile(inputFileName, inputFileName + "." + mode, mode);
	}

	private static void ReadProperties() throws FileNotFoundException, IOException {
		Properties prop = new Properties();

		prop.load(new FileInputStream("Enigma.properties"));

		transitionCount = Integer.parseInt(prop.getProperty("TransitionCount"));
	}

	private static void CreateRolls() throws Exception {
		rolls = new ArrayList<Roll>();

		int rollKeylength = 256 + transitionCount;

		byte[] definition = new byte[(int) new File(keyFilename).length()];

		try (FileInputStream fileInputStream = new FileInputStream(keyFilename);) {
			fileInputStream.read(definition);
		}

		if (definition.length % rollKeylength > 0)
			throw new Exception("Invalid Keysize");

		int rollCount = definition.length / rollKeylength;

		for (int rollNumber = 0; rollNumber < rollCount; rollNumber++) {
			List<Integer> transitions = new ArrayList<Integer>();

			for (int index = 0; index < transitionCount; index++)
				transitions.add((int) definition[rollNumber * rollKeylength + 256 + index]);

			byte[] singleRoll = new byte[256];
			for (int index = 0; index < 256; index++)
				singleRoll[index] = definition[rollNumber * rollKeylength + index];

			rolls.add(new Roll(singleRoll, transitions));
		}

		for (Roll roll : rolls)
			roll.CheckInput(transitionCount);
	}

	private static void Keygen(int rollCount) throws Exception {

		if (rollCount < 4)
			throw new Exception("Not enough rolls.");

		Random random = new SecureRandom();

		byte[] key = new byte[(256 + transitionCount) * rollCount];

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
				key[(256 + transitionCount) * i + index] = transform[index];

			List<Integer> transitions = new ArrayList<Integer>();

			while (transitions.size() < transitionCount) {
				int rand = random.nextInt(256);
				if (!transitions.contains(rand))
					transitions.add(rand);
			}

			for (int index = 0; index < transitionCount; index++)
				key[(256 + transitionCount) * i + 256 + index] = (byte) (int) transitions.get(index);
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(keyFilename);) {
			fileOutputStream.write(key);
		}

		System.out.println("Keys generated.");
	}

	private static boolean IsTwisted(byte[] trans) {
		for (int i = 0; i <= 255; i++)
			if (trans[i] == i)
				return false;

		return true;
	}
}
