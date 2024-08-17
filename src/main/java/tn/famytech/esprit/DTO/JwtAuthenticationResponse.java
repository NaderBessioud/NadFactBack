package tn.famytech.esprit.DTO;

import lombok.Data;
import tn.famytech.esprit.Entites.User;

@Data
public class JwtAuthenticationResponse {
	
	private String msg;
	private User user;
	private String token;
	

}
