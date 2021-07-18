# Enigma
Modern Enigma Crypt implementation avoiding known issues
## Basics
There's a good [wikipedia](https://en.wikipedia.org/wiki/Enigma_machine) page showing the original Enigma and its usage.
This implementation will extend the functionality and avoids the weak points where the encryption could be broken.
* Encrypt any file or stream where the mechanical version only accepts characters
* Create a key of unlimited complexity
* Avoid weakness by
  * allow involution (avoid reflector)
  * fixed point free permutation (avoid reflector)
  * see the wiring in the roll as part of the key and have unlimited count and type of rolls
  * there is no plugboard, since this is an monoalphabetic substitution. You can add security by adding an extra roll, which is polyalphabetic substitution.
  * multiple turnover notches
## Usage
Generate a key file using 'keygen x key.file' where x > 3 is the number of rolls.
Encrypt a file using 'enc a.txt key.file'.
Decrypt a file using 'dec a.txt.enc key.file'.
Since you generate a new set of rolls with every keygen, there is no starting position for these rolls. 
In the mechanical machine you have secret roll wiring and changing secret starting positions. The starting positions where noted in the code book. 
When decryption started in Bletchley Park, the British Army had some machines, and so a valuable part of the key. It was their job to guess the starting positions, since they had no code book.
In this implementation all the key information is in the keygen file, so you don't need a code book or starting position.
## Key space
Each roll has 256! settings for the wiring and 256! / (256-53)! settings for the turnover notches. The last roll's turnover notches are unused, which permits 256! possibilities for this roll.
This makes 1,1*10<sup>632</sup> combinations per roll and 8,6*10<sup>506</sup> for the last roll. This is a really big keyspace!
## Limits
Modern cryptography uses typically a public algorithm and a secret key. One problem of the mechanical Enigma was a part of the key being hidden in the wiring of the rolls, which made it vulnerable.
This part is solved in this implementation.
The big advantage of modern cryptography is the public/private key exchange basing on the calculations of Diffie-Hellman. With this asymmetric key exchange you don't have to exchange a keyfile or a codebook.
This implentation depends on exchanging a keyfile between sender and receiver, which makes it a strong encryption of little practical value:-)
