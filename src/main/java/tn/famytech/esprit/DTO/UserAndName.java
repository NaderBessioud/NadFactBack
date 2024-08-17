package tn.famytech.esprit.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAndName {
	private User user;
	private String name;

}
