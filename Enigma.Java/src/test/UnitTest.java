package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.stream.FileImageInputStream;
import javax.xml.stream.events.EntityDeclaration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.*;

class UnitTest {

	byte[] transLinear = new byte[256]; // here every char is mapped to itself
	byte[] transLinearInvert = new byte[256]; // match the first to the last etc
	byte[] transShift1 = new byte[256]; // 'a' is mapped to 'b' etc
	byte[] transShift2 = new byte[256]; // 'a' is mapped to 'c' etc

	private BusinessLogic businessLogicEncode;
	private BusinessLogic businessLogicDecode;

	byte[] encryptedMsg;
	byte[] decryptedMsg;

	void Crypt(byte[] msg) {
		encryptedMsg = msg.clone();
		businessLogicEncode.TransformByteArray(encryptedMsg, Enums.Mode.Encode);
		decryptedMsg = encryptedMsg.clone();
		businessLogicDecode.TransformByteArray(decryptedMsg, Enums.Mode.Decode);
	}

	@BeforeEach
	public void Init() {
		for (int i = -0; i < 256; i++)
			transLinear[i] = (byte) (i - 128);

		for (int i = 0; i < 256; i++)
			transLinearInvert[i] = (byte) (255 - i - 128);

		for (int i = 0; i < 256; i++)
			transShift1[i] = (byte) ((i + 1 - 128) % 256);

		for (int i = 0; i < 256; i++)
			transShift2[i] = (byte) ((i + 2 - 128) % 256);
	}

	void InitBusinessLogic(ArrayList<byte[]> transitions, ArrayList<ArrayList<Integer>> turnovers) {
		if (transitions.size() != turnovers.size())
			assertFalse(true, "There must be as much transitions as roll defs!");

		List<Roll> rollsEncrypt = new ArrayList<Roll>();
		List<Roll> rollsDecrypt = new ArrayList<Roll>();

		for (int i = 0; i < transitions.size(); i++) {
			rollsEncrypt.add(new Roll(transitions.get(i), turnovers.get(i)));
			rollsDecrypt.add(new Roll(transitions.get(i), turnovers.get(i)));

		}

		businessLogicEncode = new BusinessLogic(rollsEncrypt);
		businessLogicDecode = new BusinessLogic(rollsDecrypt); // need a second, because the enc BusinessLogic has
																// turned over rolls
	}

	@Test
	public void OneByte1RollLinear() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transLinear);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i });

			assertEquals(i, encryptedMsg[0]);
			assertEquals(i, decryptedMsg[0]);
		}
	}

	@Test
	public void OneByte1RollShift1() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transShift1);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i });
			assertEquals((byte) (i + 1), encryptedMsg[0]);
			assertEquals(i, decryptedMsg[0]);
		}
	}

	@Test
	public void OneByte1RollShift2() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transShift2);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i });
			assertEquals((byte) (i + 2), encryptedMsg[0]);
			assertEquals(i, decryptedMsg[0]);
		}
	}

	@Test
	public void TwoByte1RollLinear() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transLinear);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) ((i + 1)) });

			assertEquals(i, encryptedMsg[0]);
			assertEquals((byte) (i + 2), encryptedMsg[1]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals((byte) (i + 1), decryptedMsg[1]);
		}
	}

	@Test
	public void TwoByte1RollShift() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transShift1);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) ((i + 1)) });

			assertEquals((byte) (i + 1), encryptedMsg[0]);
			assertEquals((byte) (i + 3), encryptedMsg[1]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals((byte) (i + 1), decryptedMsg[1]);
		}
	}

	@Test
	public void TwoByte1RollInvert() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transLinearInvert);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) ((i)) });

			assertEquals((byte) (-i - 1), encryptedMsg[0]);
			assertEquals((byte) (-i - 2), encryptedMsg[1]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals(i, decryptedMsg[1]);
		}
	}

	@Test
	public void TwoByte2RollLinearInvert() {

		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transLinearInvert);
			transformation.add(transLinearInvert);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) ((i + 1)) });
			assertEquals(i, encryptedMsg[0]);
			assertEquals((byte) (i + 2), encryptedMsg[1]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals((byte) (i + 1), decryptedMsg[1]);
		}
	}

	@Test
	public void TwoByte2RollShift() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transShift1);
			transformation.add(transShift1);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) ((i + 1)) });
			assertEquals((byte) (i + 2), encryptedMsg[0]);
			assertEquals((byte) (i + 4), encryptedMsg[1]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals((byte) (i + 1), decryptedMsg[1]);
		}
	}

	@Test
	public void TwoByte2RollShift2() {
		ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
		ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>(0);
		turnovers.add(turnover);
		turnovers.add(turnover);

		ArrayList<byte[]> transformation = new ArrayList<byte[]>();
		transformation.add(transShift2);
		transformation.add(transShift2);

		InitBusinessLogic(transformation, turnovers);

		Crypt(new byte[] { 7, 107 });

		assertEquals(11, encryptedMsg[0]);
		assertEquals(112, encryptedMsg[1]);

		assertEquals(7, decryptedMsg[0]);
		assertEquals(107, decryptedMsg[1]);
	}

	@Test
	public void TwoByte2RollInvert() {
		for (int i = -128; i < 128; i++) {
			ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0));
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(turnover);
			turnovers.add(turnover);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transLinearInvert);
			transformation.add(transLinearInvert);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) ((i + 1)) });
			assertEquals((byte) (i), encryptedMsg[0]);
			assertEquals((byte) (i + 2), encryptedMsg[1]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals((byte) (i + 1), decryptedMsg[1]);
		}
	}

	@Test
	public void ThreeByte2RollTransit() {
		ArrayList<Integer> always = new ArrayList<>();
		for (int j = 0; j < 256; j++)
			always.add(j);

		for (int i = -128; i < 128; i++) {
			ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
			turnovers.add(always);
			turnovers.add(always);

			ArrayList<byte[]> transformation = new ArrayList<byte[]>();
			transformation.add(transLinear);
			transformation.add(transLinear);

			InitBusinessLogic(transformation, turnovers);

			Crypt(new byte[] { (byte) i, (byte) (i + 1), (byte) (i + 2) });

			assertEquals((byte) (i), encryptedMsg[0]);
			assertEquals((byte) (i + 3), encryptedMsg[1]);
			assertEquals((byte) (i + 6), encryptedMsg[2]);

			assertEquals(i, decryptedMsg[0]);
			assertEquals((byte) (i + 1), decryptedMsg[1]);
			assertEquals((byte) (i + 2), decryptedMsg[2]);
		}
	}

	@Test
	public void TwoByte2DifferentRollsTransit() {
		ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
		ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
		turnovers.add(turnover);
		turnovers.add(turnover);

		ArrayList<byte[]> transformation = new ArrayList<byte[]>();
		transformation.add(transLinear);
		transformation.add(transShift1);

		InitBusinessLogic(transformation, turnovers);

		Crypt(new byte[] { 7, 107 });

		assertEquals(8, encryptedMsg[0]);
		assertEquals(110, encryptedMsg[1]);

		assertEquals(7, decryptedMsg[0]);
		assertEquals(107, decryptedMsg[1]);
	}

	@Test
	public void TwoByte2DifferentRollsTransit3() {
		ArrayList<Integer> turnover = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
		ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
		turnovers.add(turnover);
		turnovers.add(turnover);

		ArrayList<byte[]> transformation = new ArrayList<byte[]>();
		transformation.add(transLinear);
		transformation.add(transLinearInvert);

		InitBusinessLogic(transformation, turnovers);

		Crypt(new byte[] { 7, 107 });

		assertEquals(-8, encryptedMsg[0]);
		assertEquals(-110, encryptedMsg[1]);

		assertEquals(7, decryptedMsg[0]);
		assertEquals(107, decryptedMsg[1]);
	}

    @Test
    public void RealLive()
    {
        int msgSize = 5 * 65536;

        byte[] msg = new byte[msgSize];
        for (int i = 0; i < msgSize; i++)
            msg[i] = (byte)(i);

		ArrayList<Integer> turnover1 = new ArrayList<>(Arrays.asList(0, 22, 44, 100));
		ArrayList<Integer> turnover2 = new ArrayList<>(Arrays.asList(11, 44, 122, 200));
		ArrayList<Integer> turnover3 = new ArrayList<>(Arrays.asList(33, 77, 99, 222));
		ArrayList<Integer> turnover4 = new ArrayList<>(Arrays.asList(55, 67, 79, 240));
		ArrayList<ArrayList<Integer>> turnovers = new ArrayList<ArrayList<Integer>>();
		turnovers.add(turnover1);
		turnovers.add(turnover2);
		turnovers.add(turnover3);
		turnovers.add(turnover4);

		ArrayList<byte[]> transformation = new ArrayList<byte[]>();
		transformation.add(transLinear);
		transformation.add(transLinearInvert);
		transformation.add(transShift1);
		transformation.add(transShift2);

		InitBusinessLogic(transformation, turnovers);

		Crypt(msg);

		assertEquals(msg.length, decryptedMsg.length);
		
        Crypt(msg);

        for (int i = 0; i < msgSize; i++)
            assertEquals(msg[i], decryptedMsg[i]);
    }

    @Test
    public void Integrationtest() throws Exception
    {
        int msgSize = 5 * 65536;    //bigger than buffersize:-)
        String keyname = "any.key";
        String msgFileName = "msg.file";

        byte[] msg = new byte[msgSize];
        for (int i = 0; i < msgSize; i++)
            msg[i] = (byte)(i % 256);

        FileOutputStream fileOutputStream = new FileOutputStream(msgFileName);
        fileOutputStream.write(msg);
        fileOutputStream.close();

        Program.main(new String[] { "keygen", "4", keyname});
        Program.main(new String[] { "enc", msgFileName, keyname });
        Program.main(new String[] { "dec", msgFileName + ".Enc", keyname });
 
        byte[] encdec = new byte[msgSize];
        FileInputStream fileInputStream = new FileInputStream(msgFileName + ".Enc.Dec");
        fileInputStream.read(encdec);
        fileInputStream.close();

        for(int i = 0; i < msg.length; i++)
            assertEquals(msg[i], encdec[i]);

        assertEquals(msg.length, encdec.length);
    }
}
