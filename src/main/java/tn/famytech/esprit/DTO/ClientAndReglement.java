package tn.famytech.esprit.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import tn.famytech.esprit.Entites.Reglement;

@Getter
@Setter
@AllArgsConstructor
public class ClientAndReglement {
	
	private String lib;
	private Reglement reglement;

}
