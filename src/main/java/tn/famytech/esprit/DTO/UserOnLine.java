package tn.famytech.esprit.DTO;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class UserOnLine {
	private long idU;
	private String fullname;
	private String name;
	private String email;
	private boolean online;
	private String image;

}
