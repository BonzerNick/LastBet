package com.example.casino.totp;

import com.example.casino.model.User;
import com.example.casino.service.TwoFactorAuthenticationCodeVerifier;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import org.springframework.util.StringUtils;

import java.security.GeneralSecurityException;

public class TotpAuthenticationCodeVerifier implements TwoFactorAuthenticationCodeVerifier {

	@Override
	public boolean verify(User user, String code) {
		try {
			return TimeBasedOneTimePasswordUtil.validateCurrentNumber(user.twoFactorSecret(),
					StringUtils.hasText(code) ? Integer.parseInt(code) : 0, 10000);
		}
		catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}

}
