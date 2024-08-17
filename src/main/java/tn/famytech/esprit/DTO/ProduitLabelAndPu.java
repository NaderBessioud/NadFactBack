package tn.famytech.esprit.DTO;



import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Facture;


@AllArgsConstructor
@Getter
@Setter
public class ProduitLabelAndPu {
	private String label;
	private double putnd;
	private double pueuro;
}
