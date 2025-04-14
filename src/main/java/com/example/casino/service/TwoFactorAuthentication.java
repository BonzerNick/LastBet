package com.example.casino.service;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;

import java.util.List;

@Getter
public class TwoFactorAuthentication extends AbstractAuthenticationToken {

	private final Authentication primary;

	public TwoFactorAuthentication(Authentication primary) {
		super(List.of());
		this.primary = primary;
		super.setAuthenticated(false);
	}

	public Object getPrincipal() {
		return this.primary.getPrincipal();
	}

	@Override
	public Object getCredentials() {
		return this.primary.getCredentials();
	}

	@Override
	public void eraseCredentials() {
		if (this.primary instanceof CredentialsContainer) {
			((CredentialsContainer) this.primary).eraseCredentials();
		}
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

}
