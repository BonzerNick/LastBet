package com.example.casino.handler;

import com.example.casino.model.User;
import com.example.casino.config.CustomUserDetails;
import com.example.casino.service.TwoFactorAuthentication;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final AuthenticationSuccessHandler primarySuccessHandler;

	private final AuthenticationSuccessHandler secondarySuccessHandler;

	public TwoFactorAuthenticationSuccessHandler(String secondAuthUrl,
                                                 AuthenticationSuccessHandler primarySuccessHandler) {
		this.primarySuccessHandler = primarySuccessHandler;
		this.secondarySuccessHandler = new SimpleUrlAuthenticationSuccessHandler(secondAuthUrl);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = customUserDetails.getUser();
		if (user.twoFactorEnabled()) {
			SecurityContextHolder.getContext().setAuthentication(new TwoFactorAuthentication(authentication));
			this.secondarySuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
		else {
			this.primarySuccessHandler.onAuthenticationSuccess(request, response, authentication);
		}
	}

}
