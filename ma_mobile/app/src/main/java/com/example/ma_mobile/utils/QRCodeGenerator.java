package com.example.ma_mobile.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {

    /**
     * Generates a QR code bitmap from the given content (user ID)
     * @param userId The user ID to encode in the QR code
     * @param width The width of the QR code in pixels
     * @param height The height of the QR code in pixels
     * @return Bitmap of the QR code, or null if generation fails
     */
    public static Bitmap generateQRCodeForUser(Long userId, int width, int height) {
        if (userId == null) {
            return null;
        }

        // Format: USER_ID:{userId}
        String qrContent = "USER_ID:" + userId;
        return generateQRCode(qrContent, width, height);
    }

    /**
     * Generates a QR code bitmap from the given content
     */
    public static Bitmap generateQRCode(String content, int width, int height) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a QR code for a user with default size (400x400)
     */
    public static Bitmap generateQRCodeForUser(Long userId) {
        return generateQRCodeForUser(userId, 400, 400);
    }

    /**
     * Parses a scanned QR code to extract user ID
     * @param qrContent The scanned QR code content
     * @return User ID if valid format, null otherwise
     */
    public static Long parseUserIdFromQRCode(String qrContent) {
        if (qrContent == null || !qrContent.startsWith("USER_ID:")) {
            return null;
        }

        try {
            String userIdStr = qrContent.substring("USER_ID:".length());
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
