package com.example.casino.controller;

import java.io.IOException;

import com.example.casino.model.User;
import com.example.casino.service.UserService;
import com.example.casino.config.CustomUserDetails;
import com.example.casino.service.TwoFactorAuthentication;
import com.example.casino.service.TwoFactorAuthenticationCodeVerifier;
import com.example.casino.totp.QrCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {

    private final UserService userService;

    private final TwoFactorAuthenticationCodeVerifier codeVerifier;

    private final QrCode qrCode;

    private final AuthenticationSuccessHandler successHandler;

    private final AuthenticationFailureHandler failureHandler;

    public TwoFactorAuthController(UserService userService, TwoFactorAuthenticationCodeVerifier codeVerifier,
                                   QrCode qrCode, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        this.userService = userService;
        this.codeVerifier = codeVerifier;
        this.qrCode = qrCode;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @GetMapping(path = "/enable-2fa")
    public String requestEnableTwoFactor(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        User user = customUserDetails.getUser();
        String otpAuthUrl = "otpauth://totp/%s?secret=%s&digits=6".formatted("Demo: " + user.getUsername(),
                user.twoFactorSecret());
        model.addAttribute("qrCode", this.qrCode.dataUrl(otpAuthUrl));
        model.addAttribute("secret", user.twoFactorSecret());

        System.out.println("Model attributes: " + model.asMap());
        return "enable-2fa";
    }

    @PostMapping(path = "/enable-2fa")
    public String processEnableTwoFactor(@RequestParam String code,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        User user = customUserDetails.getUser();
        if (user.twoFactorEnabled()) {
            return "redirect:/";
        }
        if (!this.codeVerifier.verify(user, code)) {
            model.addAttribute("message", "Invalid code");
            return this.requestEnableTwoFactor(customUserDetails, model);
        }
        User enabled = this.userService.enable2Fa(user);
        Authentication token = UsernamePasswordAuthenticationToken.authenticated(new CustomUserDetails(enabled), null,
                customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
        return "redirect:/";
    }

    @GetMapping(path = "/challenge/totp")
    public String requestTotp() {
        return "totp";
    }

    @PostMapping(path = "/challenge/totp")
    public void processTotp(
            @RequestParam String code,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        Authentication primaryAuthentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) primaryAuthentication.getPrincipal();
        User user = customUserDetails.getUser();
        if (this.codeVerifier.verify(user, code)) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
            );
            this.successHandler.onAuthenticationSuccess(request, response, primaryAuthentication);
        }
        else {
            this.failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Invalid code"));
        }
    }

}
