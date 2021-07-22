using Enigma;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;
using System.IO;

namespace UnitTestProject1
{
    [TestClass]
    public class UnitTest1
    {

        byte[] transLinear = new byte[256];     //here every char is mapped to itself

        byte[] transLinearInvert = new byte[256];   //match the first to the last etc

        byte[] transShift1 = new byte[256];     //'a' is mapped to 'b' etc
        byte[] transShift2 = new byte[256];     //'a' is mapped to 'c' etc

        private BusinessLogic BusinessLogicEncode { get; set; }
        private BusinessLogic BusinessLogicDecode { get; set; }

        byte[] encryptedMsg;
        byte[] decryptedMsg;

        void Crypt(byte[] msg)
        {
            encryptedMsg = BusinessLogicEncode.TransformByteArray(msg, Enums.Mode.Enc);
            decryptedMsg = BusinessLogicDecode.TransformByteArray(encryptedMsg, Enums.Mode.Dec);
        }

        [TestInitialize]
        public void Init()
        {
            for (int i = 0; i < 256; i++)
                transLinear[i] = (byte)i;

            for (int i = 0; i < 256; i++)
                transLinearInvert[i] = (byte)(255 - i);

            for (int i = 0; i < 256; i++)
                transShift1[i] = (byte) ((i + 1) % 256);

            for (int i = 0; i < 256; i++)
                transShift2[i] = (byte)((i + 2) % 256);

        }

        void InitBusinessLogic(List<KeyValuePair<byte[], List<int>>> rolls)
        {
            List<Roll> rollsEncrypt = new List<Roll>();
            List<Roll> rollsDecrypt = new List<Roll>();

            foreach (KeyValuePair<byte[], List<int>> roll in rolls)
            {
                rollsEncrypt.Add(new Roll(roll.Key, roll.Value));
                rollsDecrypt.Add(new Roll(roll.Key, roll.Value));
            }

            BusinessLogicEncode = new BusinessLogic(rollsEncrypt);
            BusinessLogicDecode = new BusinessLogic(rollsDecrypt);
        }

        [TestMethod]
        public void OneByte1RollLinear()
        {
            for (int i = 0; i < 256; i++)
            {

                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() {new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() {0})});

                Crypt(new byte[] {(byte)i});

                Assert.AreEqual(i, encryptedMsg[0]);
                Assert.AreEqual(i, decryptedMsg[0]);
            }
        }

        [TestMethod]
        public void OneByte1RollShift()
        {
            for (int i = 0; i < 256; i++)
            {
                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() {new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() {0})});
                Crypt(new byte[] { (byte)i });

                Assert.AreEqual((i+1) % 256, encryptedMsg[0]);
                Assert.AreEqual(i, decryptedMsg[0]);
            }
        }
        [TestMethod]
        public void OneByte1RollShift2()
        {
            for (int i = 0; i < 256; i++)
            {
                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() {new KeyValuePair<byte[], List<int>>(transShift2, new List<int>() {0})});
                Crypt(new byte[] {(byte) i});

                Assert.AreEqual((i + 2) % 256, encryptedMsg[0]);
                Assert.AreEqual(i, decryptedMsg[0]);
            }
        }


        [TestMethod]
        public void TwoByte1RollLinear()
        {
            for (int i = 0; i < 256; i++)
            {

                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() {new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() {0})});
                Crypt(new byte[] { (byte)i, (byte)((i+1)%256) });

                Assert.AreEqual(i, encryptedMsg[0]);
                Assert.AreEqual((i + 2) % 256, encryptedMsg[1]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual((i + 1) % 256, decryptedMsg[1]);
            }
        }

        [TestMethod]
        public void TwoByte1RollShift()
        {
            for (int i = 0; i < 256; i++)
            {
                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() { 0 }) });

                Crypt(new byte[] { (byte)i, (byte)((i + 1) % 256) });

                Assert.AreEqual((i + 1) % 256, encryptedMsg[0]);
                Assert.AreEqual((i + 3) % 256, encryptedMsg[1]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual((i + 1) % 256, decryptedMsg[1]);
            }
        }
        [TestMethod]
        public void TwoByte1RollInvert()
        {
            for (int i = 0; i < 256; i++)
            {
                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() {new KeyValuePair<byte[], List<int>>(transLinearInvert, new List<int>() {0})});
                Crypt(new byte[] { (byte)i, (byte)i });

                Assert.AreEqual(255-i, encryptedMsg[0]);
                Assert.AreEqual((256 + 255 -i -1)%256, encryptedMsg[1]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual(i, decryptedMsg[1]);
            }
        }

        [TestMethod]
        public void TwoByte2RollLinear()
        {
            for (int i = 0; i < 256; i++)
            {

                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>()
                    {new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() {0}), new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() {0})});

                Crypt(new byte[] { (byte)i, (byte)((i + 1) % 256) });

                Assert.AreEqual(i, encryptedMsg[0]);
                Assert.AreEqual((i + 2) % 256, encryptedMsg[1]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual((i + 1) % 256, decryptedMsg[1]);
            }

        }

        [TestMethod]
        public void TwoByte2RollShift()
        {
            for (int i = 0; i < 256; i++)
            {

                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>()
                    {new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() {0}), new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() {0})});

                Crypt(new byte[] { (byte)i, (byte)((i + 1) % 256) });

                Assert.AreEqual((i + 2) % 256, encryptedMsg[0]);
                Assert.AreEqual((i + 2) % 256, encryptedMsg[0]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual((i + 1) % 256, decryptedMsg[1]);
            }
        }

        [TestMethod]
        public void TwoByte2RollShift2()
        {
            InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transShift2, new List<int>() { 0 }), new KeyValuePair<byte[], List<int>>(transShift2, new List<int>() { 0 }) });
            Crypt(new byte[] { 7, 107 });

            Assert.AreEqual(11, encryptedMsg[0]);
            Assert.AreEqual(112, encryptedMsg[1]);

            Assert.AreEqual(7, decryptedMsg[0]);
            Assert.AreEqual(107, decryptedMsg[1]);

        }


        [TestMethod]
        public void TwoByte2RollInvert()
        {
            for (int i = 0; i < 256; i++)
            {

                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transLinearInvert, new List<int>() { 0 }), new KeyValuePair<byte[], List<int>>(transLinearInvert, new List<int>() { 0 }) });

                Crypt(new byte[] { (byte)i, (byte)((i + 1) % 256) });

                Assert.AreEqual(i, encryptedMsg[0]);
                Assert.AreEqual((i + 2) % 256, encryptedMsg[1]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual((i + 1) % 256, decryptedMsg[1]);
            }
        }


        [TestMethod]
        public void ThreeByte2RollTransit()
        {
            List<int>always = new List<int>();
            for(int j = 0; j < 256; j++)
                always.Add(j);


            for (int i = 0; i < 256; i++)
            {
                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transLinear,always), new KeyValuePair<byte[], List<int>>(transLinear, always) });

                Crypt(new byte[] { (byte)i, (byte)((i + 1) % 256), (byte)((i + 2) % 256) });

                Assert.AreEqual(i, encryptedMsg[0]);
                Assert.AreEqual((i + 3) % 256, encryptedMsg[1]);
                Assert.AreEqual((i + 6) % 256, encryptedMsg[2]);

                Assert.AreEqual(i, decryptedMsg[0]);
                Assert.AreEqual((i + 1) % 256, decryptedMsg[1]);
                Assert.AreEqual((i + 2) % 256, decryptedMsg[2]);
            }
        }

        [TestMethod]
        public void TwoByte2DifferentRollsTransit()
        {
            InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() { 0, 1, 2, 3 }), new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() { 0, 1, 2, 3 }) });
            Crypt(new byte[] { 7, 107 });

            Assert.AreEqual(8, encryptedMsg[0]);
            Assert.AreEqual(110, encryptedMsg[1]);

            Assert.AreEqual(7, decryptedMsg[0]);
            Assert.AreEqual(107, decryptedMsg[1]);
        }

        [TestMethod]
        public void TwoByte2DifferentRollsTransit2()
        {
            InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() { 0, 1, 2, 3 }), new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() { 0, 1, 2, 3 }) });
            Crypt(new byte[] { 7, 107 });

            Assert.AreEqual(9, encryptedMsg[0]);
            Assert.AreEqual(111, encryptedMsg[1]);

            Assert.AreEqual(7, decryptedMsg[0]);
            Assert.AreEqual(107, decryptedMsg[1]);
        }


        [TestMethod]
        public void TwoByte2DifferentRollsTransit3()
        {
            InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() { new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() { 0, 1, 2, 3 }), new KeyValuePair<byte[], List<int>>(transLinearInvert, new List<int>() { 0, 1, 2, 3 }) });
            Crypt(new byte[] { 7, 107 });

            Assert.AreEqual(248, encryptedMsg[0]);
            Assert.AreEqual(146, encryptedMsg[1]);

            Assert.AreEqual(7, decryptedMsg[0]);
            Assert.AreEqual(107, decryptedMsg[1]);
        }

        [TestMethod]
        public void RealLive()
        {
            int msgSize = 65536;

            byte[] msg = new byte[msgSize];
            for (int i = 0; i < msgSize; i++)
                msg[i] = (byte)(i % 256);

            InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>()
                { new KeyValuePair<byte[], List<int>>(transLinear, new List<int>() { 0,  22, 44,  100 }),
                  new KeyValuePair<byte[], List<int>>(transLinearInvert, new List<int>() { 11, 44, 122, 200 }),
                  new KeyValuePair<byte[], List<int>>(transShift1, new List<int>() { 33, 77, 99,  222 }),
                  new KeyValuePair<byte[], List<int>>(transShift2, new List<int>() { 55, 67, 79,  240 })
                });

            Crypt(msg);

            for (int i = 0; i < msgSize; i++)
                Assert.AreEqual(msg[i], decryptedMsg[i]);
        }

        [TestMethod]
        public void OneByte1RollInverted()
        {

            for (int i = 0; i < 256; i++)
            {
                InitBusinessLogic(new List<KeyValuePair<byte[], List<int>>>() {new KeyValuePair<byte[], List<int>>(transLinearInvert, new List<int>() {0})});
                Crypt(new byte[] {(byte)i});

                Assert.AreEqual((255 - i)%256, encryptedMsg[0]);
                Assert.AreEqual(i, decryptedMsg[0]);
            }
        }
        [TestMethod]
        public void Integrationtest()
        {
            int msgSize = 5 * 65536;    //bigger than buffersize:-)
            string keyname = "any.key";
            string msgFileName = "msg.file";

            byte[] msg = new byte[msgSize];
            for (int i = 0; i < msgSize; i++)
                msg[i] = (byte)(i % 256);

            File.WriteAllBytes(msgFileName, msg);

            Program.Main(new string[] { "keygen", "4", $"{keyname}"});
            Program.Main(new string[] { "enc", $"{msgFileName}", $"{keyname}" });
            Program.Main(new string[] { "dec", $"{msgFileName}.Enc", $"{keyname}" });

            byte[] encdec = File.ReadAllBytes($"{msgFileName}.Enc.Dec");

            for(int i = 0; i < msg.Length; i++)
                Assert.AreEqual(msg[i], encdec[i]);

            Assert.AreEqual(msg.Length, encdec.Length);
        }
    }
}
