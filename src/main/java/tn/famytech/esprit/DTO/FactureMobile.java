package tn.famytech.esprit.DTO;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.FacturePayementStatus;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.Reglement;
import tn.famytech.esprit.Entites.TypeFacture;

@AllArgsConstructor
@Getter
@Setter
public class FactureMobile {

	private long idF;
	private long number;
	private String libelle;
	private double totalttc;
	@Temporal(TemporalType.DATE)
	private Date dateemission;
	private String status;
	private String type;
	private String pdfname;
}
