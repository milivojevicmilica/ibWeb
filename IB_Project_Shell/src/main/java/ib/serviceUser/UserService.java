package ib.serviceUser;

import java.util.List;



import ib.entity.User;



public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll ();
    List<User> findByEnabled(Boolean odobren);
    
    
}
