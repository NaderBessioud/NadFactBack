package tn.famytech.esprit.Entites;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DemandeDepense implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idDD;
	private String motif;
	private double budget;
	private DepenseCat cat;
	@Temporal(TemporalType.DATE)
	private Date date;
	private DemandeStatus status;
	
	@JsonIgnore
	@OneToOne(mappedBy = "ddepense")
	private BonCommande bc;
	
	
	@ManyToOne
	private Personel userde;
	
	
	@ManyToOne
	private Fournisseur fournisseur;

}
