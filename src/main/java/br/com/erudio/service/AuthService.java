package br.com.erudio.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.stereotype.Service;

import br.com.erudio.data.dto.V1.security.AccountCredencialsDTO;
import br.com.erudio.data.dto.V1.security.TokenDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.model.Users;
import br.com.erudio.repository.UsersRepository;
import br.com.erudio.security.jwt.JwtTokenProvider;

@Service
public class AuthService {
	
	private Logger log = LoggerFactory.getLogger(AuthService.class);

	@Autowired // Injeta o AuthenticationManager configurado no contexto do Spring Security
	private AuthenticationManager authenticationManager;

	@Autowired // Injeta o componente que gera e valida tokens JWT
	private JwtTokenProvider provider;

	@Autowired // Injeta o repositório de usuários para acesso ao banco de dados
	private UsersRepository repository;

	public ResponseEntity<TokenDTO> signIn(AccountCredencialsDTO credencialsDTO) {
		// Realiza a autenticação do usuário usando nome e senha
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				credencialsDTO.getUserName(),  // Nome de usuário vindo do DTO
				credencialsDTO.getPassword()   // Senha vinda do DTO
			)
		);

		// Busca o usuário no banco de dados após a autenticação
		var user = repository.findByUserName(credencialsDTO.getUserName());

		// Verifica se o usuário foi encontrado no banco; se não, lança exceção
		if (Objects.isNull(user)) {
			throw new UsernameNotFoundException(credencialsDTO.getUserName() + ", not found in Data Base!");
		}

		// Gera o token JWT passando o username e as roles (perfis) do usuário
		var token = provider.createdAccessToken(user.getUserName(), user.getRoles());

		// Retorna o token dentro de um ResponseEntity com status 200 OK
		return ResponseEntity.ok(token);
	}
	
	public ResponseEntity<TokenDTO> refreshIn(String userName, String refreshToken){
		//Busca no DB se existe o user 
		var user = repository.findByUserName(userName);
		TokenDTO token;
		if(!Objects.isNull(user)) {
			//valida se não é null
			//chama o metodo que a partir do refresh gera um novo e mantem autenticdo o userw
			token = provider.refreshToken(refreshToken);
			
		}
		else {
			//lança a exceção caso não exista
			throw new UsernameNotFoundException(userName + ", not found in Data Base!");
		}
		//retorna o token atualizado
		return ResponseEntity.ok(token);
	}
	
	public AccountCredencialsDTO create(AccountCredencialsDTO user) {
		
		if (Objects.isNull(user))
			//vereifica sé é nulo meu user e caso for lança a exceção
			throw new RequiredObjectIsNullException();
		log.info("Creating new user!");
		//caso não for nulo instancia um novo users
		var entity = new Users();
		//seta todas os campos com baase o user vindo de credencials
		entity.setFullName(user.getFullName());
		entity.setUserName(user.getUserName());
		entity.setPassword(generateHashedPassword(user.getPassword()));
		entity.setAccountNonExpired(true);
		entity.setAccountNonLocked(true);
		entity.setCredentialsNonExpired(true);
		entity.setEnabled(true);
		//retorna um  objeto dto credencials
		return ObjectMapper.parseObject(repository.save(entity), AccountCredencialsDTO.class);
		
	}
	
	private String generateHashedPassword(String password) {
		//cria um codificador de senhas (hash) usando o algoritmo PBKDF2 com HMAC-SHA256
		PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		//Mapeia o prefixo "pbkdf2" ao encoder correspondente
		Map<String, PasswordEncoder> encoders = new HashMap<String, PasswordEncoder>();
		encoders.put("pbkdf2", pbkdf2Encoder);
		// Cria o delegador de encoders, que adiciona o prefixo {pbkdf2} automaticamente
		DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
		// Define o encoder padrão para comparar senhas salvas no banco (sem prefixo)
		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
		// Retorna a senha criptografada (com prefixo {pbkdf2})
		return passwordEncoder.encode(password);
	}
}