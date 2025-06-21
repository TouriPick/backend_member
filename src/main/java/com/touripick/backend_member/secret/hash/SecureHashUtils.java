package com.touripick.backend_member.secret.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureHashUtils {
    /**
     * 입력된 평문 비밀번호를 SHA-256으로 해싱하여 반환합니다.
     * @param password 해싱할 평문 비밀번호
     * @return SHA-256으로 해싱된 16진수 문자열
     */
    public static String hashSha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 알고리즘을 찾을 수 없는 경우 (거의 발생하지 않음)
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    /**
     * 입력된 평문 비밀번호를 SHA-256으로 해싱한 후 저장된 해시 값과 비교합니다.
     * @param rawPassword 사용자가 입력한 평문 비밀번호
     * @param encodedPassword 데이터베이스에 저장된 해싱된 비밀번호
     * @return 두 비밀번호가 일치하면 true, 그렇지 않으면 false
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        // 사용자가 입력한 비밀번호를 동일한 SHA-256 방식으로 해싱
        String hashedRawPassword = hashSha256(rawPassword);
        // 해싱된 값과 저장된 값을 비교
        return hashedRawPassword.equals(encodedPassword);
    }
}