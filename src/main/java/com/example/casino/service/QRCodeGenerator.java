//package com.example.casino.service;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.WriterException;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//
//public class QRCodeGenerator {
//
//    public BufferedImage generateQrCodeImage(String barcodeText) throws WriterException, IOException {
//        QRCodeWriter barcodeWriter = new QRCodeWriter();
//        var bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200); // Размер изображения
//        return MatrixToImageWriter.toBufferedImage(bitMatrix);
//    }
//}