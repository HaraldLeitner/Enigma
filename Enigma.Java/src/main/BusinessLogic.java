package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusinessLogic {
	private List<Roll> Rolls;
	private List<Roll> RollsReverse;

	public BusinessLogic(List<Roll> rolls) {

		Rolls = rolls;

		RollsReverse = new ArrayList<Roll>(rolls);
		Collections.reverse(RollsReverse);
	}

	public void TransformFile(String inputFfilename, String outputFilename, Enums.Mode mode) throws IOException {
		final int buffersize = 65536;

		FileInputStream fileInStream = new FileInputStream(inputFfilename);
		FileOutputStream fileOutStream = new FileOutputStream(outputFilename);

		byte[] buffer = new byte[buffersize];
		int readCount = 0;

		while ((readCount = fileInStream.read(buffer, 0, buffersize)) > 0) {
			TransformByteArray(buffer, mode);
			fileOutStream.write(buffer, 0, readCount);
		}

		fileInStream.close();
		fileOutStream.close();
	}

	public void TransformByteArray(byte[] input, Enums.Mode mode) {

		if (mode == Enums.Mode.Encode) {

			for (int i = 0; i < input.length; i++) {
				for (Roll roll : Rolls)
					input[i] = roll.Encrypt(input[i]);

				RollOn();
			}
		}

		if (mode == Enums.Mode.Decode) {
			for (int i = 0; i < input.length; i++) {
				for (Roll roll : RollsReverse)
					input[i] = roll.Decrypt(input[i]);

				RollOn();
			}
		}
	}

	private void RollOn() {
		for (Roll roll : Rolls) {
			if (!roll.RollOn())
				break;
		}
	}
}
