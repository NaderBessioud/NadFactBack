package tn.famytech.esprit.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Repositories.PersonelRepo;
import tn.famytech.esprit.Repositories.UserRepository;

@Service
public class PersonelService {
	@Autowired
	private PersonelRepo repo;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	public Personel getPersonelByEmail(String email) {
		return repo.findByEmail(email);
	}
	
	public List<User> findContact(String email){
		User u=repository.findByEmail(email);
		return repo.findContacts(u.getIdU());
	}
	
	  public String AuthenticateUser(String email) {
	    	try {
	    	User user=repository.findByEmail(email);
	    	 Authentication authentication = authenticationManager.authenticate(
	                 new UsernamePasswordAuthenticationToken(email, user.getPassword())
	             );
	    	 
	    	  SecurityContextHolder.getContext().setAuthentication(authentication);

	          return "home"; // Authentication success
	      } catch (Exception e) {
	          return "login"; // Authentication failed
	      }
	  }

}
