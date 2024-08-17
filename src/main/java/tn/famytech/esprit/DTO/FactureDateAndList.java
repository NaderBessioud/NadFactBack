package tn.famytech.esprit.DTO;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Facture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FactureDateAndList {
	private List<Facture> factures;
	private String date;

}
