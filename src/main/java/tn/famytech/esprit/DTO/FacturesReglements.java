package tn.famytech.esprit.DTO;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Reglement;

@NoArgsConstructor
@Getter
@Setter
public class FacturesReglements {
	
	private List<ClientAndReglement> reglements;
	private List<Facture> factures;

}
