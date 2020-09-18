package ib.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.security.Principal;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ib.entity.User;
import ib.service.impl.UserServiceImpl;



//Celom kontroleru mogu pristupiti samo autentifikovani korisnici
@RestController
@RequestMapping( value = "/api", produces = MediaType.APPLICATION_JSON_VALUE )
public class UserController {

  @Autowired
  private UserServiceImpl userService;

  //Putanja => localhost:8080/api/user/1
  
  //Za pristup ovoj metodi neophodno je da ulogovani korisnik ima ADMIN ulogu
  //Ukoliko nema, server ce vratiti gresku 403 Forbidden
  //Korisnik jeste autentifikovan, ali nije autorizovan da pristupi resursu
  @RequestMapping( method = GET, value = "/user/{userId}" )
  @PreAuthorize("hasRole('ADMIN')")
  public User loadById( @PathVariable Long userId ) {
      return this.userService.findById( userId );
  }

  @RequestMapping( method = GET, value= "/user/all")
  @PreAuthorize("hasRole('ADMIN')")
  public List<User> loadAll() {
      return this.userService.findAll();
  }


  @RequestMapping("/whoami")
  @PreAuthorize("hasRole('REGULAR')")
  public User user(Principal user) {
      return this.userService.findByUsername(user.getName());
  }
  


  
}


