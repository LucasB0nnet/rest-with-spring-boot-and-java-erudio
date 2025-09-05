package br.com.erudio.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.erudio.security.jwt.JwtTokenFilter;
import br.com.erudio.security.jwt.JwtTokenProvider;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Autowired
	private JwtTokenProvider provider;
	
	public SecurityConfig(JwtTokenProvider tokenProvider) {
		this.provider = tokenProvider;
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		// Cria um codificador de senhas usando o algoritmo PBKDF2 com SHA-256
	    PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
	        "",                        // Secret (vazio neste caso)
	        8,                         // Tamanho do salt (número de bytes)
	        185000,                    // Iterações (quanto maior, mais seguro, mas mais lento)
	        SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256 // Algoritmo usado
	    );

	    // Mapa de encoders disponíveis (pode adicionar outros tipos se quiser, como bcrypt, scrypt, etc.)
	    Map<String, PasswordEncoder> encoders = new HashMap<>();
	    encoders.put("pbkdf2", pbkdf2Encoder);  // Define "pbkdf2" como identificador

	    // Cria o DelegatingPasswordEncoder, que permite identificar o encoder pelo prefixo no hash
	    DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

	    // Define o encoder padrão caso não haja prefixo no hash
	    passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);

	    // Retorna o bean para ser injetado no sistema de autenticação
	    return passwordEncoder;
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		// Recupera o AuthenticationManager configurado automaticamente pelo Spring Security
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		JwtTokenFilter customFilter = new JwtTokenFilter(provider);
		//@formatter:off
		return http
				.httpBasic(AbstractHttpConfigurer::disable) // Desativa autenticação HTTP básica (usuário/senha via popup)
				.csrf(AbstractHttpConfigurer::disable)      // Desativa proteção CSRF (desnecessária em APIs REST sem sessão)
				.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona o filtro JWT antes do filtro padrão
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define que a API não usará sessão (stateless)
				.authorizeHttpRequests(authorize -> authorize.requestMatchers("/auth/signin",
						"/auth/refresh/**",
						"/auth/createUser",
						"/swagger-ui/**",
						"/v3/api-docs/**").permitAll()// Libera acesso público a essas rotas (login, criação de usuário e documentação Swagger)
						 .requestMatchers("/api/**").authenticated() // Exige autenticação para todas as rotas que começam com /api
				         .requestMatchers("/users").denyAll())       // Bloqueia totalmente o acesso à rota /users
				.cors(cors -> {})
				.build();
		//@formatter:on
	}
}
