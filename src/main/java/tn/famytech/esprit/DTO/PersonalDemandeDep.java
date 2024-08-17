package tn.famytech.esprit.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.BonCommande;
import tn.famytech.esprit.Entites.DemandeDepense;
import tn.famytech.esprit.Entites.DemandeStatus;
import tn.famytech.esprit.Entites.DepenseCat;
import tn.famytech.esprit.Entites.Fournisseur;
import tn.famytech.esprit.Entites.Personel;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonalDemandeDep {
	
	private long id;
	private long idf;
	private DemandeDepense demande;

}
