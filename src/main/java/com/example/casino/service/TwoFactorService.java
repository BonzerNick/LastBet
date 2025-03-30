//package com.example.casino.service;
//
//import org.apache.commons.codec.binary.Base32;
//import java.security.SecureRandom;
//
//public class TwoFactorService {
//
//    public String generateSecretKey() {
//        SecureRandom random = new SecureRandom();
//        byte[] bytes = new byte[10]; // Длина ключа
//        random.nextBytes(bytes);
//
//        Base32 base32 = new Base32();
//        return base32.encodeToString(bytes); // Секретный ключ в Base32
//    }
//
//    public String generateQrCodeUrl(String username, String secretKey, String appName) {
//        return "otpauth://totp/" + appName + ":" + username +
//                "?secret=" + secretKey +
//                "&issuer=" + appName;
//    }
//}