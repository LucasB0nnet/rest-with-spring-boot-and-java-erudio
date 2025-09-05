package br.com.erudio.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.erudio.repository.UsersRepository;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UsersRepository repository;

	public UserService(UsersRepository repository) {
		this.repository = repository;
	}


	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {		
		var user = repository.findByUserName(userName);
		if(!Objects.isNull(userName)) {
			return user;
		}
		else {
			throw new UsernameNotFoundException("Username "+ userName +" it is records!");
		}
	}
}
