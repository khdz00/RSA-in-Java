/**
 * <h1>RSA Encryption/Decryption and Cracking</h1>
 * <p>This program encrypts .txt files using rsa public key and decrypts it using the private key.
 * For cracking, the program uses the public key with modular exponentiation to guess the factors making up d.</p>
 *
 * @author  Khalid Abanmy
 * @author  Suhayb Ullah
 * @version 1.0
 * @since   2021-12-11
 */
import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class RSA {
    private final TwoWayHashmap<Object, String> alphabet = new TwoWayHashmap<>();

    public RSA() {
        createDictionary();
    }

    /**
     * This method creates and sets the alphabet variable with pre-specified characters:
     * (A-z, 0-9, and specialCharacters).
     *
     * @return the crafted alphabet
     */
    private TwoWayHashmap<Object, String> createDictionary() {
        // j is the index
        int j = 1;
        // Adding A-Z,a-z
        for (char c = 'A'; c <= 'Z'; c++, j++) {
            String strJ = String.valueOf(j);
            if (j < 10)
                strJ = "0" + strJ;
            alphabet.put(c, strJ);
        }
        for (char c = 'a'; c <= 'z'; c++, j++) {
            String strJ = String.valueOf(j);
            alphabet.put(c, strJ);
        }
        // Adding numbers
        for (char i = '0'; i <= '9'; i++, j++) {
            String strJ = String.valueOf(j);
            alphabet.put(i, strJ);
        }
        // Adding special characters
        Character[] specialCharacters = {'.', '?', '!', ',', ';', ':', '-', '(', ')', '[', ']', '{',
                '}', '\'', '"', ' ', '\n'};
        for (Character c : specialCharacters) {
            String strJ = String.valueOf(j);
            alphabet.put(c, strJ);
            j++;
        }
        return alphabet;
    }

    /**
     * This is a mod method that uses Modular exponentiation.
     * @param a base
     * @param b exponent
     * @param m mod
     * @return the modulo of (a^b) mod m
     */
    private long mod(long a, long b, long m) {
        long x = 1;
        long y=a;
        while(b > 0){
            if(b%2 == 1){
                x=x*y%m;
            }
            y = y*y%m;
            b /= 2;
        }
        long output = x%m;
        if (output < 0)
            output += m;
        return output;
    }

    private long inverseMod(long a, long m) {
        long result = BigInteger.valueOf(a).modInverse(BigInteger.valueOf(m)).longValue();
        return result;
    }

    /**
     * This method runs through every character in a string and returns the encoded version according to the alphabet.
     *
     * @param plaintext the String text to be encoded
     * @return encoded text as BigInteger
     */
    private long encode(String plaintext) {
        int size = plaintext.length();
        String temp = "";
        for(int i=0; i<size; i++) {
            char c = plaintext.charAt(i);
            temp += alphabet.getForward(c);
        }
        long encodedText = Long.valueOf(temp);
        return encodedText;
    }

    /**
     * This method runs through every code in a BigInteger and returns the decoded version according to the alphabet.
     *
     * @param encodedText encoded text to be decoded
     * @return decoded text as String
     */
    private String decode(long encodedText) {
        String strEncodedText = String.valueOf(encodedText);
        int size = strEncodedText.length();
        // If text is of an odd size we need to add a zero at the beginning
        if(size%2 != 0) {
            strEncodedText = "0" + strEncodedText;
            size++;
        }
        // Iterating 2 characters at a time
        String decodedText = "";
        for(int i=0; i<size; i+=2) {
            String code = strEncodedText.substring(i, i+2);
            decodedText += alphabet.getBackward(code);
        }
        return decodedText;
    }

    /**
     * This method takes the sqrt of n then finds its factors p*q.
     *
     * @param e public key exponent e
     * @param n mod n
     * @return private key exponent d
     */
    private long findD(long e, long n) throws Exception {
        System.out.println("Factorizing n...");
        long rootN = (long) Math.sqrt(n);
        long p = rootN-1;
        if (p%2 == 0)
            p--;
        for(; n%p!=0 && p>0; p-=2);
        System.out.println("p=" + p);
        if (p<=0)
            throw new Exception("Error! Cannot find valid factors of n=p*q.");
        long q = n/p;
        long temp = (p-1)*(q-1);
        long d = inverseMod(e, temp);
        return d;
    }

    /**
     * This method is calculating the blockSize using the given n such that (encoded characters are <= n)
     *
     * @param n
     * @return blockSize
     * @throws IllegalArgumentException when n is smaller than the alphabet's size
     */
    private int getBlockSize(long n) throws IllegalArgumentException {
        long alphabetSize = alphabet.size() - 1;
        if (n <= alphabetSize) // n <= alphabetSize
            throw new IllegalArgumentException(
                    "Error! n cannot be smaller than the alphabet size.");
        // Block size = 1 character
        int blockSize = 1;
        while (alphabetSize*100 + alphabetSize < n) {
            alphabetSize =alphabetSize*100 + alphabetSize;
            blockSize++;
        }
        return blockSize;
    }

    /**
     * This method adds padding to the plaintext, so it can be evenly divided to blocks of size blockSize.
     *
     * @param plainText
     * @param blockSize
     * @return paddedText with X as padding characters
     */
    private String addPadding(String plainText, int blockSize) {
        int size = plainText.length();
        int numberOfPadding = size % blockSize;
        String paddedText = plainText;
        for (int i=0; i<numberOfPadding; i++)
            paddedText += "X";
        return paddedText;
    }

    /**
     * This method removes the padding from a padded plain text by simply removing all Xs at the end of the String
     *
     * @param paddedText
     * @return plainText
     */
    private String removePadding(String paddedText) {
        String plainText = paddedText;
        for(int i=paddedText.length()-1; paddedText.charAt(i)=='X' && i>=0; i--) {
            plainText = paddedText.substring(0, i);
        }
        return plainText;
    }

    public String encrypt(long e, long n, String plainText) {
        int blockSize = getBlockSize(n);
        int cipherBlockSize = String.valueOf(n).length();
        String paddedText = addPadding(plainText, blockSize);
        int paddedTextSize = paddedText.length();
        // Encrypting 1 block at a time
        String cipherText = "";
        for(int i=0; i<paddedTextSize; i+=blockSize) {
            // Get characters block from plain padded text
            String block = paddedText.substring(i, i+blockSize);
            // Encode characters block
            long encodedBlock = encode(block);
            // Encrypt characters block
            long cipherBlock = mod(encodedBlock, e, n);
            // Add encrypted block to the cipher text (encrypted text)
            String strCipherBlock = String.valueOf(cipherBlock);
            while (strCipherBlock.length()%(cipherBlockSize) != 0)
                strCipherBlock = "0" + strCipherBlock;
            cipherText += strCipherBlock;
        }
        return cipherText;
    }

    public String decrypt(long d, long n, String cipherText) {
        int cipherBlockSize = String.valueOf(n).length();
        int cipherSize = cipherText.length();
        // Decrypting 1 block at a time
        String plainText = "";
        for(int i=0; i<cipherSize; i+=cipherBlockSize) {
            // Get encrypted characters from cipher text
            String strCipherBlock = cipherText.substring(i, i+cipherBlockSize);
            long cipherBlock = Long.valueOf(strCipherBlock);
            // Decrypt characters block
            long encodedBlock = mod(cipherBlock, d, n);
            // Decode characters block
            String plainTextBlock = decode(encodedBlock);
            // Add decrypted and decoded block to the plain text
            plainText += plainTextBlock;
        }
        // Remove any padding
        plainText = removePadding(plainText);
        return plainText;
    }

    public String crack(long e, long n, String cipherText) throws Exception {
        // Guessing d (The private key) from n and e
        long d = findD(e, n);
        // Decrypting the cipher text using the d we found
        String plaintext = decrypt(d, n, cipherText);
        return plaintext;
    }

    /**
     * This method reads the public key e and n, and it reads the message to be encrypted from filepath. Then it writes
     * the encrypted message to filepath.rsa
     *
     * @param app RSA object instant
     * @param filepath path of the .txt file to be encrypted.
     * @throws FileNotFoundException
     */
    public static void encryptionProgram(RSA app, String filepath) throws IOException {
        // Setting up Scanner sc to read filepath
        System.out.printf("Reading %s...%n", filepath);
        FileInputStream fis = new FileInputStream(filepath);
        Scanner sc = new Scanner(fis);
        // Reading e and n from the first line of filepath
        long e = sc.nextLong();
        long n = sc.nextLong();
        sc.nextLine();
        System.out.printf("e=%s%n" +
                          "n=%s%n", e, n);
        // Reading the message from filepath
        String message = "";
        while (sc.hasNextLine())
            message += sc.nextLine() + "\n";
        message = message.trim();
        System.out.println("Plain text:");
        System.out.println(message);
        // Encrypting then printing the resulting cipher text
        String cipherText = app.encrypt(e, n, message);
        System.out.println("Cipher text:");
        System.out.println(cipherText);
        sc.close();
        fis.close();
        // Writing the ciphertext to filepath.rsa
        int extIndex = filepath.lastIndexOf('.');
        String outputFilepath = filepath.substring(0, extIndex) + ".rsa";
        FileOutputStream fos = new FileOutputStream(outputFilepath, false);
        PrintWriter pw = new PrintWriter(fos);
        pw.print(cipherText);
        pw.close();
        fos.close();
    }

    /**
     * This method reads the secret message from filepath and asks the user for the d and n values. Then it decrypts
     * it are writes the plain text to filepath.dec
     * @param app RSA object instant
     * @param filepath path of the .rsa file to be decrypted.
     * @throws IOException
     */
    public static void decryptionProgram(RSA app, String filepath) throws IOException {
        // Setting up Scanner sc and reading cipher text from filepath
        FileInputStream fis = new FileInputStream(filepath);
        Scanner sc = new Scanner(fis);
        String ciphertext = sc.nextLine();
        System.out.println("Cipher text:");
        System.out.println(ciphertext);
        sc.close();
        fis.close();
        // Reading d and n from user keyboard
        Scanner kb = new Scanner(System.in);
        System.out.print("Enter the value of d: ");
        long d = kb.nextLong();
        System.out.print("Enter the value of n: ");
        long n = kb.nextLong();
        kb.close();
        // Decrypting then printing the resulting plain text
        String plainText = app.decrypt(d, n, ciphertext);
        System.out.println("Plain text:");
        System.out.println(plainText);
        // Writing the ciphertext to filepath.rsa
        int extIndex = filepath.lastIndexOf('.');
        String outputFilepath = filepath.substring(0, extIndex) + ".dec";
        FileOutputStream fos = new FileOutputStream(outputFilepath, false);
        PrintWriter pw = new PrintWriter(fos);
        pw.print(plainText);
        pw.close();
        fos.close();
    }

    /**
     * This method reads the secret message from filepath and asks the user for the e and n values. Then it guesses the
     * d value and decrypts the secret message. Finally, it writes the plain text to filepath.dec.
     *
     * @param app RSA object instant
     * @param filepath path of the .rsa file to be decrypted.
     * @throws IOException
     */
    public static void crackProgram(RSA app, String filepath) throws Exception {
        // Setting up Scanner sc and reading cipher text from filepath
        FileInputStream fis = new FileInputStream(filepath);
        Scanner sc = new Scanner(fis);
        String ciphertext = sc.nextLine();
        sc.close();
        fis.close();
        // Reading public key (e and n) from user keyboard
        Scanner kb = new Scanner(System.in);
        System.out.print("Enter the value of e: ");
        long e = kb.nextLong();
        System.out.print("Enter the value of n: ");
        long n = kb.nextLong();
        kb.close();
        // Decrypting then printing the resulting plain text
        String plainText = app.crack(e, n, ciphertext);
        System.out.println("Plain text:");
        System.out.println(plainText);
        // Writing the ciphertext to filepath.rsa
        int extIndex = filepath.lastIndexOf('.');
        String outputFilepath = filepath.substring(0, extIndex) + ".dec";
        FileOutputStream fos = new FileOutputStream(outputFilepath, false);
        PrintWriter pw = new PrintWriter(fos);
        pw.print(plainText);
        pw.close();
        fos.close();
    }

    public static void main(String[] args) {
        try {
            RSA app = new RSA();
            Scanner kb = new Scanner(System.in);
            System.out.printf("##- RSA Encryption/Decryption -##\n" +
                              "Please enter encrypt, decrypt or crack (e/d/c): ");
            Character option = kb.nextLine().charAt(0);
            System.out.print("Enter the path to the file: ");
            String filepath = kb.nextLine();
            String fileExtension = filepath.substring(filepath.lastIndexOf('.'));
            switch (option) {
                case 'e':
                    if (fileExtension.equals(".txt"))
                        encryptionProgram(app, filepath);
                    else
                        System.out.println("Invalid file type! Make sure the file is in .txt format.");
                    break;
                case 'd':
                    if (fileExtension.equals(".rsa"))
                        decryptionProgram(app, filepath);
                    else
                        System.out.println("Invalid file type! Make sure the file is in .rsa format.");
                    break;
                case 'c':
                    if (fileExtension.equals(".rsa"))
                        crackProgram(app, filepath);
                    else
                        System.out.println("Invalid file type! Make sure the file is in .rsa format.");
                    break;
                default:
                    System.out.println("Invalid option! Options are: encrypt, decrypt, and crack (e/d/c).");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}