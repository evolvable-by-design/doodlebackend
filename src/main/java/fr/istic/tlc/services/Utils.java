package fr.istic.tlc.services;

import java.util.Random;

public class Utils {
    private static Random random = new Random();
    private static String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890_-";

    public static String generateSlug(int length) {
        StringBuilder slug = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            slug.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return slug.toString();
    }
}
