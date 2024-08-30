package tn.famytech.esprit.DTO;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Facture;


@NoArgsConstructor
@Getter
@Setter
public class FacturesReglementsRetenue {
	private List<ClientAndReglement> reglements;
	private List<Facture> factures;
	private String retenue;
}
