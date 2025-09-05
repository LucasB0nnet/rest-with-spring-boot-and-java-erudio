package br.com.erudio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.erudio.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

	@Query("SELECT u FROM Users u WHERE u.userName =:userName")
	Users findByUserName(@Param("userName") String userName);
}
