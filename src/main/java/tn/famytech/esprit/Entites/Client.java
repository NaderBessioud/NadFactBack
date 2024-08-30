package tn.famytech.esprit.Entites;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client extends User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	private String libelle;
	private String idfiscal;	
	private Boolean exonere;
	private Date dateexonere;
	private TypeClient type;
	private boolean archived;
	
	@JsonIgnore
	@OneToMany(mappedBy = "client")
	private Set<Facture> factures;
	
	
	
	
	
	

}
