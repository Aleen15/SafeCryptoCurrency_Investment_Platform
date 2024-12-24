package in.projectjwt.main.repositories;

import org.springframework.stereotype.Repository;

import in.projectjwt.main.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findById(Long userId);


	

}
