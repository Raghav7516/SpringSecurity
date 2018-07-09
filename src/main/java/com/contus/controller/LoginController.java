package com.contus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contus.config.JwtTokenUtil;
import com.contus.dto.LoginUser;
import com.contus.model.AuthToken;
import com.contus.model.Users;
import com.contus.repository.UserRepository;
import com.contus.service.UserService;

@RequestMapping("/login")
@RestController
public class LoginController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@PostMapping("/generate-token")
	public ResponseEntity<?> register(@RequestBody LoginUser loginUser) throws AuthenticationException {

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		final Users user = userService.findOne(loginUser.getUsername());
		final String token = jwtTokenUtil.generateToken(user);
		return ResponseEntity.ok(new AuthToken(token));
	}

	@PostMapping("/save")
	public String createUser(@RequestBody Users user) {
		Users users = new Users();
		users.setName(user.getName());
		users.setLastName(user.getLastName());
		users.setEmail(user.getEmail());
		users.setPassword(user.getPassword());
		users.setRoles(user.getRoles());
		users.setActive(user.getActive());
		userRepository.save(users);
		return "inserted";
	}

	@GetMapping("/all")
	public String hello() {
		return "Welcome to spring security";
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@GetMapping("/signup/user")
	public String securedUser() {
		return "Secured User";
	}

	@PreAuthorize("hasAnyRole('GUEST')")
	@GetMapping("/signup/guest")
	public String securedGuest() {
		return "Secured Guest";
	}
	
	@PostAuthorize("returnObject.name == authentication.name")
	@GetMapping("/signup/alternate")
	public Users alternate() {
		System.out.println("ok");
		return userRepository.findById(1).get();
	}
}
