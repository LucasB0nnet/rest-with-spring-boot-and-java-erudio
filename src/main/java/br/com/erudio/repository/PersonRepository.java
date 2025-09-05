package br.com.erudio.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.erudio.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Person p SET p.enabled = false WHERE p.id =:id")//Atualizar o atributo person chamado enabled que for = a false e o id passado for igual ao id do banco
	void disablePerson(@Param("id")Long id);
	
	@Query("SELECT p FROM Person p WHERE p.firstName LIKE LOWER(CONCAT ('%',:firstName,'%'))")// trazer a person apelidada de p aonde for igual ao paramentro firtName
	Page<Person> findPersonByName(@Param("firstName") String firstName, Pageable pageable);//for igual do banco e e torna ela minuscula e o % identifica em qualquer parte do nome
}
