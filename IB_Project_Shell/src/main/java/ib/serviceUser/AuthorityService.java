package ib.serviceUser;

import java.util.List;

import ib.entity.Authority;



public interface AuthorityService {
	List<Authority> findById(Long id);
	List<Authority> findByname(String name);
}
