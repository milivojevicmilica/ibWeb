package ib.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ib.entity.Authority;



public interface AuthorityRepository extends JpaRepository<Authority, Long> {
	Authority findByName(String name);
}
