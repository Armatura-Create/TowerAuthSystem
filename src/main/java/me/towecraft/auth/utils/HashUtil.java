package me.towecraft.auth.utils;

import unsave.plugin.context.annotations.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class HashUtil {

    public boolean match(String password, String hashPass) {
        return hashPass.equals(toHash(password));
    }

    public String toHash(String pass) {
        return md5(md5(pass));
    }

    private String md5(String string) {
        String hash = null;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(string.getBytes(), 0, string.length());
            hash = new BigInteger(1, messageDigest.digest()).toString(16);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
        return hash;
    }
}
