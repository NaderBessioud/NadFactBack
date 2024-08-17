package tn.famytech.esprit.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserMobile {
	
	private long idU;
	private String fullname;
	private String email;
	private boolean online;
	String image;
	private int nbmsg;
	private String content;
	private String day;
	private boolean seen;

}
