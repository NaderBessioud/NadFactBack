package tn.famytech.esprit.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.BonLivraison;
import tn.famytech.esprit.Entites.DepenseReelle;
import tn.famytech.esprit.Entites.TypePayment;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepRealWithAdresse {
	private DepenseReelle dep;
	private String adresse;

}
