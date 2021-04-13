package by.petrovlad.test;

import java.util.Random;

public class StringGenerator {
    private static final String alphabet = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
    private static final int alphabetLength = alphabet.length();
    private Random random;

    public static String generateString(int length) {
        Random random = new Random();
        char[] res = new char[length];
        for (int i = 0; i < length; i++) {
            res[i] = alphabet.charAt(random.nextInt(length));
        }
        return new String(res);
    }
}
