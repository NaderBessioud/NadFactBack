package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.famytech.esprit.DTO.JwtAuthenticationResponse;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	@Autowired
	private final UserRepository rep;
	
	@Autowired
	private final PasswordEncoder encoder;
	
	@Autowired
	private final AuthenticationManager authenticationManager;
	@Autowired
	private final JWTService jwtService;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	 private List<User> loggedInUsers = new ArrayList<>();
	
	
	private int authenticate(String username, String pass){
		try {
		SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, pass)));
			return 0;
			


			
		}
		catch (DisabledException ex) {
			return 1;
		}
		catch (BadCredentialsException ex) {
			
			return 2;
		}catch (UsernameNotFoundException e) {
			return 3;
		}catch (Exception e) {
			System.out.print(e.getStackTrace());
			return 4;
		}
		
	}
	
public JwtAuthenticationResponse signin(String email, String password, HttpServletResponse response) {
	JwtAuthenticationResponse authenticationResponse = new JwtAuthenticationResponse();
		
		int result=authenticate(email, password);
		if(result == 0) {
			var user = rep.findByEmail(email);
			loggedInUsers.add(user);
			var jwt =jwtService.generateToken(user);
			authenticationResponse.setMsg("good");
			authenticationResponse.setUser(user);
			authenticationResponse.setToken(jwt);
			Cookie cookie = new Cookie("auth_token", jwt);
		    cookie.setHttpOnly(true);
		    
		    cookie.setPath("/");
		    response.addCookie(cookie);
			
			template.convertAndSend("/topic/online", user.getIdU());
		}
		
		else if(result==1) {
			authenticationResponse.setMsg("Disabled");
			authenticationResponse.setUser(null);
			authenticationResponse.setToken("");
		}
		else if(result==2) {
			authenticationResponse.setMsg("Bad Credentials");
			authenticationResponse.setUser(null);
			authenticationResponse.setToken("");
		}
		else if(result==3) {
			authenticationResponse.setMsg("Not Exist");
			authenticationResponse.setUser(null);
			authenticationResponse.setToken("");
		}
		else {
			
		
		authenticationResponse.setMsg("exception");
		authenticationResponse.setUser(null);
		authenticationResponse.setToken("");
		}
		
		return authenticationResponse;
		
	}

public JwtAuthenticationResponse signinWithGoogle(String email,HttpServletResponse response) {
	User user = rep.findByEmail(email);
	JwtAuthenticationResponse authenticationResponse = new JwtAuthenticationResponse();
	var jwt =jwtService.generateToken(user);
	authenticationResponse.setMsg("good");
	authenticationResponse.setUser(user);
	authenticationResponse.setToken(jwt);
	Cookie cookie = new Cookie("auth_token", jwt);
    cookie.setHttpOnly(true);
    
    cookie.setPath("/");
    response.addCookie(cookie);
	return authenticationResponse;
}

public void logout( String email,HttpServletRequest request, HttpServletResponse response) {
	User user = rep.findByEmail(email);
	
	
	for(int i=0;i<loggedInUsers.size();i++) {
		if(loggedInUsers.get(i).getEmail()==email) {
			loggedInUsers.remove(i);
		}
	}
	
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("auth_token".equals(cookie.getName())) {
                // Create a new cookie with the same name
                Cookie deleteCookie = new Cookie("auth_token", null);
                deleteCookie.setHttpOnly(true); // Ensure that the cookie is HTTP-only
                deleteCookie.setSecure(true);  // Ensure that the cookie is sent over HTTPS
                deleteCookie.setPath(cookie.getPath()); // Use the same path as the original cookie
                deleteCookie.setMaxAge(0);     // Set the maximum age to 0 to delete the cookie

                response.addCookie(deleteCookie); // Add the cookie to the response
                break; // Exit loop after deleting the cookie
            }
        }
    }
	template.convertAndSend("/topic/offline", user.getIdU());
}


public List<User> getLoggedIndUser(){
	return loggedInUsers;
}

public String getUserType(String email) {
	User user=rep.findByEmail(email);
	return user.getRole().toString();
}
}
