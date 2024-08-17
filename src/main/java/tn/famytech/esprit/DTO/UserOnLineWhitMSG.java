package tn.famytech.esprit.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserOnLineWhitMSG {
	
	private long idU;
	private String fullname;
	private String name;
	private String email;
	private boolean online;
	private String image;
	private String content;
	private String day;
	private boolean seen;

}
