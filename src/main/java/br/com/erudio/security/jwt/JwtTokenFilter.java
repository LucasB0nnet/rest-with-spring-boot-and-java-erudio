package br.com.erudio.security.jwt;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import br.com.erudio.exception.InvalidJwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class JwtTokenFilter extends GenericFilterBean {

	@Autowired
	private JwtTokenProvider provider;
	
	public JwtTokenFilter(JwtTokenProvider tokenProvider) {
		this.provider = tokenProvider;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
			throws IOException, ServletException {
		try {
	        // Extrai o token JWT da requisição HTTP
	        var token = provider.resolveToken((HttpServletRequest) request);

	        // Verifica se o token não está em branco e se é válido
	        if(StringUtils.isNotBlank(token) && provider.validateToken(token)) {
	            
	            // Obtém a autenticação associada ao token
	            Authentication authentication = provider.getAuthentication(token);
	            
	            // Se a autenticação for válida, define-a no contexto de segurança do Spring
	            if(authentication != null) {
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	            }
	        }

	        // Continua com a cadeia de filtros, permitindo que a requisição prossiga
			filter.doFilter(request, response);
		} catch (InvalidJwtAuthenticationException e) {
			throw new InvalidJwtAuthenticationException("Invalid Token!"); 
		}
		
	}

}
