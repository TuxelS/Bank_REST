package com.example.bankcards.util;

import java.util.Random;

public class CardNumberGenerator {

    private static final Random random = new Random();

    public static String generateValidCardNumber() {
        // Генерируем первые 15 цифр случайно
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }

        // Добавляем контрольную цифру по алгоритму Луна
        int checkDigit = calculateLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);

        return cardNumber.toString();
    }

    private static int calculateLuhnCheckDigit(String partialCardNumber) {
        int sum = 0;
        boolean isEvenPosition = false;

        // Обрабатываем с конца
        for (int i = partialCardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(partialCardNumber.charAt(i));

            if (isEvenPosition) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            isEvenPosition = !isEvenPosition;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit;
    }
}