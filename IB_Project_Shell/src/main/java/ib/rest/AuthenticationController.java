package ib.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ib.entity.User;
import ib.entity.UserDTO;
import ib.entity.UserTokenState;
import ib.security.auth.JwtAuthenticationRequest;
import ib.service.impl.CustomUserDetailsService;
import ib.serviceUser.UserService;
import ib.token.TokenHelper;




//KOntroler zaduzen za autentifikaciju korisnika
//Na osnovni URL se dodaje /auth kako bi se pristupilo citavom kontroleru
//Ukoliko je aplikacija podignuta lokalno => localhost:8080/api
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	private TokenHelper tokenHelper;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;

	// Prvi endpoint koji pogadja korisnik kada se loguje.
	// Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
	@PostMapping("/login")
	public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			HttpServletResponse response) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
						authenticationRequest.getPassword()));

		// Ubaci korisnika u trenutni security kontekst
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Kreiraj token za tog korisnika
		User user = (User) authentication.getPrincipal();
		String jwt = tokenHelper.generateToken(user.getUsername(), user.getAuthoritiesAsString());
		int expiresIn = tokenHelper.getExpiredIn();

		// Vrati token kao odgovor na uspesnu autentifikaciju
		return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	  
	  public ResponseEntity<?> register(@RequestBody UserDTO userDTO) throws ParseException {
	      User user = new User();
	      
	      
	      String username = userDTO.getUsername();
	      user.setUsername(username);
	      String password=userDTO.getPassword();
	      user.setPassword(passwordEncoder.encode(password));
	      user.setEnabled(false);
	      user.setEmail(userDTO.getEmail());
	      userDetailsService.saveUser(user);
	      
	      Map<String, String> result = new HashMap<>();
	      result.put( "result", "success" );
	      return ResponseEntity.accepted().body(result);
	  }
	  @RequestMapping( method = GET, value= "/user/loadAllUnenabled", produces = MediaType.APPLICATION_JSON_VALUE)
	  
	  public List<User> loadAllUnenabled() {
		  List<User>lista=(List<User>)userDetailsService.findAll();
		  List<User>unenabled = new ArrayList();
		  for (User user : lista) {
			  if(user.isEnabled()==false) {
				  unenabled.add(user);
			  }
			
		}
		  //this.userDetailsService.findAll();
	      return unenabled;
	  }
	@RequestMapping(value = "/enable/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	  
	  public ResponseEntity<?> enable(@PathVariable Long userId) throws ParseException {
		User userFind=(User)userDetailsService.findById( userId );
		userFind.setEnabled(true);
		userDetailsService.saveUser(userFind);
		Map<String, String> result = new HashMap<>();
	    result.put( "result", "success" );
	    return ResponseEntity.accepted().body(result);
	}

	// Endpoint za registraciju novog korisnika

	// U slucaju isteka vazenja JWT tokena, endpoint koji se poziva da se token osvezi
	@PostMapping(value = "/refresh")
	public ResponseEntity<UserTokenState> refreshAuthenticationToken(HttpServletRequest request) {

		String token = tokenHelper.getToken(request);
		String username = this.tokenHelper.getUsernameFromToken(token);
		User user = (User) this.userDetailsService.loadUserByUsername(username);

		if (this.tokenHelper.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			String refreshedToken = tokenHelper.refreshToken(token);
			int expiresIn = tokenHelper.getExpiredIn();

			return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
		} else {
			UserTokenState userTokenState = new UserTokenState();
			return ResponseEntity.badRequest().body(userTokenState);
		}
	}

	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	@PreAuthorize("hasRole('REGULAR')")
	public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
		userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);

		Map<String, String> result = new HashMap<>();
		result.put("result", "success");
		return ResponseEntity.accepted().body(result);
	}

	static class PasswordChanger {
		public String oldPassword;
		public String newPassword;
	}
 
}