package br.com.erudio.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.erudio.data.dto.V1.security.TokenDTO;
import br.com.erudio.exception.InvalidJwtAuthenticationException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";
	
	@Value("${security.jwt.token.expire-lenght:360000}")
	private long validityInMilliseconds = 360000 ; //1H
	
	// Injeta um serviço que implementa UserDetailsService (geralmente usado para buscar os dados do usuário)
	@Autowired
	private UserDetailsService detailsService;
	
	// Algoritmo usado para assinar o token JWT
	Algorithm algorithm = null;
	
	// Método chamado automaticamente após a injeção de dependências.
	@PostConstruct 
	protected void init() {
		// Codifica a chave secreta em Base64 (recomendado pelo JWT)
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		
		// Cria o algoritmo de assinatura HMAC256 com a chave secreta
		algorithm = Algorithm.HMAC256(secretKey.getBytes());
	}
	
	// Método para criar um token de acesso (JWT) e um token de refresh
	public TokenDTO createdAccessToken(String userName, List<String> roles) {
		
		// Data e hora atual
		Date now = new Date();
		
		// Define a data de validade do token (agora + tempo definido)
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		
		// Gera o token de acesso (accessToken) com as informações do usuário
		String accessToken = getAccessToken(userName, roles, now, validity);
		
		// Gera o token de refresh (refreshToken) — usado para renovar o accessToken depois de expirar
		String refreshToken = getRefreshToken(userName, roles, now);
		
		// Retorna um objeto TokenDTO com todas as informações relevantes do token
		return new TokenDTO(userName, true, now, validity, accessToken, refreshToken);
	}

	private String getRefreshToken(String userName, List<String> roles, Date now) {

		Date refreshTokenValidity = new Date(now.getTime()+(validityInMilliseconds*3));
		
		  // Cria o token JWT
	    return JWT.create()
	            // Adiciona a claim "roles" com as permissões do usuário (lista de papéis, ex: ADMIN, USER)
	            .withClaim("roles", roles)
	            // Data de emissão do token
	            .withIssuedAt(now)
	            // Data de expiração do token (quando ele deixa de ser válido)
	            .withExpiresAt(refreshTokenValidity)
	            // Define o "subject" (assunto) como sendo o nome de usuário
	            .withSubject(userName)
	            // Assina o token com o algoritmo definido (ex: HMAC256 com uma chave secreta)
	            .sign(algorithm); // já retorna uma String, então não precisa do .toString()
	}

	private String getAccessToken(String userName, List<String> roles, Date now, Date validity) {
		// Captura a URL base da aplicação (quem está emitindo o token)
	    String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
	    
	    // Cria o token JWT
	    return JWT.create()
	            // Adiciona a claim "roles" com as permissões do usuário
	            .withClaim("roles", roles)
	            // Data de emissão do token
	            .withIssuedAt(now)
	            // Data de expiração do token (normalmente mais curta que o refresh token)
	            .withExpiresAt(validity)
	            // Define o "subject" como sendo o nome de usuário
	            .withSubject(userName)
	            // Define o emissor do token (importante para validar de onde o token veio)
	            .withIssuer(issuerUrl)
	            // Assina o token com o algoritmo definido
	            .sign(algorithm);
	}
	
	public TokenDTO refreshToken(String refreshToken) {
		var token = "";
		
		if(tokenContainsBearer(refreshToken)) {
			token = refreshToken.substring("Bearer ".length());
		}
		
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodedJWT = verifier.verify(token);
		
		String userName = decodedJWT.getSubject();
		List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
		
		return createdAccessToken(userName, roles);
	}

	
	public Authentication getAuthentication(String token) {
		DecodedJWT decodedJWT = decodedToken(token);
		UserDetails userDetails = this.detailsService
				.loadUserByUsername(decodedJWT.getSubject());
		return new UsernamePasswordAuthenticationToken(userDetails,"" , userDetails.getAuthorities());
	}

	private DecodedJWT decodedToken(String token) {
		// Cria um algoritmo HMAC256 usando uma chave secreta (secretKey)
	    Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());

	    // Cria um verificador de JWT usando o algoritmo definido
	    JWTVerifier jwtVerifier = JWT.require(alg).build();

	    // Verifica e decodifica o token recebido
	    DecodedJWT decodedJWT = jwtVerifier.verify(token);

	    // Retorna o token decodificado
	    return decodedJWT;
	} 
	
	public String resolveToken(HttpServletRequest request) throws InvalidJwtAuthenticationException {
		// Pega o valor do cabeçalho "Authorization" (normalmente vem algo como "Bearer <token>")
	    String bearerToken = request.getHeader("Authorization");

	    // Verifica se o token não é vazio E se começa com "Bearer "
	    if(tokenContainsBearer(bearerToken)) {
	        // Retorna apenas o token, removendo o prefixo "Bearer "
	        return bearerToken.substring("Bearer ".length());
	    } 
	    return null;
	}
	
	public boolean validateToken (String token) throws InvalidJwtAuthenticationException {
		 try {
			 // Decodifica o token
		     DecodedJWT decodedJWT = decodedToken(token);

		     // Verifica se o token está expirado (comparando a data de expiração com a data atual)
		     if(decodedJWT.getExpiresAt().before(new Date())) {
		    	 return false; // Token expirado
		     }

		         return true; // Token válido
		    } 
		 catch (Exception e) {
		    // Se ocorrer qualquer erro ao decodificar ou validar, lança exceção customizada
		    throw new InvalidJwtAuthenticationException("Expired or Invalid JWT Token");
		}
	}
	
	private boolean tokenContainsBearer(String refreshToken) {
		return StringUtils.isNotBlank(refreshToken) && refreshToken.startsWith("Bearer ");
	}
	
	
	
	
	
	
	
}
