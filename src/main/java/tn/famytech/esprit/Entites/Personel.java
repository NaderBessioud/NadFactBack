package tn.famytech.esprit.Entites;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
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

@Getter
@Setter
public class Personel extends User implements Serializable  {
private static final long serialVersionUID = 1L;
	
	


	private String firstname;
	private String lastname;
	
	private String rib;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private Set<Facture> facturesuser;
	
	
	
	@JsonIgnore
	@OneToMany(mappedBy = "userde")
	private Set<DemandeDepense> demandes;
	
	public Personel() {
		this.facturesuser= new HashSet<Facture>();
	}

}
