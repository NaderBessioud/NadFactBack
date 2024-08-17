package tn.famytech.esprit.DTO;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Facture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AvoirAndFacturesList {

	private Avoir avoir;
	private List<Facture> factures;
}
