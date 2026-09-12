package com.trackflow.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.trackflow.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    @Value("${qr.code.upload-dir}")
    private String uploadDir;

    @Value("${qr.code.size}")
    private int qrSize;

    /**
     * Generate QR code for given content and return the file path
     */
    public String generateQRCode(String content, String fileName) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Set QR code parameters
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);

            // Save to file
            Path filePath = uploadPath.resolve(fileName + ".png");
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

            // Return relative path
            return uploadDir + "/" + fileName + ".png";

        } catch (WriterException | IOException e) {
            throw new CustomException("Failed to generate QR code: " + e.getMessage());
        }
    }

    /**
     * Check if QR code file exists
     */
    public boolean qrCodeExists(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName + ".png");
        return Files.exists(filePath);
    }

    /**
     * Delete QR code file
     */
    public void deleteQRCode(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName + ".png");
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but don't throw
        }
    }

    /**
     * Get the QR code file path
     */
    public Path getQRCodePath(String fileName) {
        return Paths.get(uploadDir).resolve(fileName + ".png");
    }
}