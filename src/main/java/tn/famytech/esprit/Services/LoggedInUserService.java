package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import tn.famytech.esprit.Entites.User;

@Service
public class LoggedInUserService {
	
	 private List<User> loggedInUsers = new ArrayList<>();

	   public List<User> getloggedInUsers(){
		   return loggedInUsers;
	   }
	   
	   public void SetloggedInUsers(List<User> users) {
		   loggedInUsers=users;
	   }

}
