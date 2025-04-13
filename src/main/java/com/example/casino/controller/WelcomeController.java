package com.example.casino.controller;

import com.example.casino.model.User;
import com.example.casino.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class WelcomeController {

	private final UserService userService;

	public WelcomeController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(path = "/")
	public String index(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		String email = userDetails.getUsername();
		Optional<User> userOptional = userService.findByEmail(email);

		if (userOptional.isPresent()) {
			User user = userOptional.get();
			model.addAttribute("user", user);
		} else {
			model.addAttribute("user", null);
		}
		System.out.println("Model attributes: " + model.asMap());
		return "index";
	}

}
