package com.example.bankcards.util;

import com.example.bankcards.exception.DecryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CardEncryptor {
    @Value("${card.secret-key}")
    private String secretKeyCard;

    @Value("${card.algorithm}")
    private String algorithm;

    @Value("${card.unmasked-symbols}")
    private Integer unmaskedCountOfSymbols;

    public String encrypt(String cardNumber) throws Exception {
        // спец объект - в итоге секретный ключ для шифрования
        SecretKeySpec key = new SecretKeySpec(secretKeyCard.getBytes(StandardCharsets.UTF_8), algorithm);
        // основной класс Java для шифрования и расшифровки
        // просит Java дать нам шифратор, работающий по алгоритму AES
        Cipher cipher = Cipher.getInstance(algorithm);
        // включение режима шифратора и добавление секрет ключа
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(cardNumber.getBytes(StandardCharsets.UTF_8));
        // Base64 — это способ представить байты как текст (только буквы, цифры, +, /, =)
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedCard) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKeyCard.getBytes(StandardCharsets.UTF_8), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedCard);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionException("Failed to decrypt card number", e);
        }

    }

    public String toMaskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - unmaskedCountOfSymbols);
    }
}
