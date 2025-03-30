//package com.example.casino.controller;
//
//import com.example.casino.controller;
//
//import com.example.casino.model.User;
//import com.example.casino.service.QRCodeGenerator;
//import com.example.casino.service.TwoFactorService;
//import com.example.casino.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/2fa")
//public class TwoFactorController {
//
//    @Autowired
//    private UserService userService;
//
//    private final TwoFactorService twoFactorService;
//    private final QRCodeGenerator qrCodeGenerator;
//
//    public TwoFactorController(TwoFactorService twoFactorService, QRCodeGenerator qrCodeGenerator) {
//        this.twoFactorService = twoFactorService;
//        this.qrCodeGenerator = qrCodeGenerator;
//    }
//
//    @PostMapping("/enable")
//    public ResponseEntity<String> enable2FA(@RequestParam String username, @RequestParam String appName)
//            throws Exception {
//        Optional<User> user = userService.findByEmail(username);
//
//        if (user.isEmpty()) {
//            return ResponseEntity.badRequest().body("User not found");
//        }
//
//
//        // Генерация секретного ключа
//        String secretKey = twoFactorService.generateSecretKey();
//        user.setTotpSecretKey(secretKey);
//        user.set2faEnabled(true);
//        userService.createUser(user);
//
//        // Генерация QR-кода URL
//        String qrCodeUrl = twoFactorService.generateQrCodeUrl(username, secretKey, appName);
//
//        // Возвращаем URL для отображения, либо картинку
//        return ResponseEntity.ok(qrCodeUrl);
//    }
//}