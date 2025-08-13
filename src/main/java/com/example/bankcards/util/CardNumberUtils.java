package com.example.bankcards.util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CardNumberUtils {

    @Value("${app.security.algorithm}")
    private String ALGORITHM;

    @Value("${app.security.secret-key:defaultKey}")
    private String SECRET_KEY;


    public String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < 16; i++){
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }

    public String encryptCardNumber(String cardNumber) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(cardNumber.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Произошла ошибка во время шифрования номера карты", e);
        }
    }

    public String decryptCardNumber(String encryptedCardNumber) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedCardNumber));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Произошла ошибка во время расшифровки номера карты", e);
        }
    }

    public String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(12);
    }
}
