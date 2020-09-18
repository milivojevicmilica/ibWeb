package ib.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import ib.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

	User findOne(Long id);
	
	
	
}
