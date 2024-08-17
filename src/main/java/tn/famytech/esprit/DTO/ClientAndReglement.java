package tn.famytech.esprit.DTO;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Reglement;

@Getter
@Setter
public class ClientAndReglement {
	
	private String lib;
	private Reglement reglement;

}
