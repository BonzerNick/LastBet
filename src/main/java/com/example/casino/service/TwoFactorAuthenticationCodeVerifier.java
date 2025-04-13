package com.example.casino.service;

import com.example.casino.model.User;

public interface TwoFactorAuthenticationCodeVerifier {

	boolean verify(User user, String code);

}
