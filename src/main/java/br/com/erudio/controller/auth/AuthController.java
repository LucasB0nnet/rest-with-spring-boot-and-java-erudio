package br.com.erudio.controller.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.controller.docs.AuthControllerDocs;
import br.com.erudio.data.dto.V1.security.AccountCredencialsDTO;
import br.com.erudio.service.AuthService;
	
@RestController
@RequestMapping("/auth")	
public class AuthController implements AuthControllerDocs {

	@Autowired
	private AuthService service;
	
	@Override
	@PostMapping(value = "/signin")
	public ResponseEntity<?> signIn(@RequestBody AccountCredencialsDTO credencialsDTO){
		System.out.println(credencialsDTO);
		if(validCredencials(credencialsDTO)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");
		}
		var token = service.signIn(credencialsDTO);
		if(token == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");
		}
		
		return token;
	}
	
	@Override
	@PutMapping(value = "/refresh/{username}")
	public ResponseEntity<?> refresh(@PathVariable("username") String username,
									@RequestHeader("Authorization") String refreshToken){
		
		if(parametersAreInvalid(username, refreshToken)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");
		}
		var token = service.refreshIn(username, refreshToken);
		if(token == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Client Request!");
		}
		
		return token;
	}
	
	@Override
	@PostMapping(value = "/createUser")
	public ResponseEntity<AccountCredencialsDTO> createUser(@RequestBody AccountCredencialsDTO credencials) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(credencials));
	}

	private boolean parametersAreInvalid(String username, String refreshToken) {
		return StringUtils.isBlank(refreshToken) || StringUtils.isBlank(username);
	}

	private boolean validCredencials(AccountCredencialsDTO credencialsDTO) {
		return credencialsDTO == null || 
			   StringUtils.isBlank(credencialsDTO.getUserName()) || 
			   StringUtils.isBlank(credencialsDTO.getPassword());
	}
}
