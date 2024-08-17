package tn.famytech.esprit.Entites;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
public class Servicefact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idP;
	private String libelle;
	private double putnd;
	private double pueuro;
	private boolean archived;
	
	
	@JsonIgnore
	  @OneToMany(mappedBy = "produit")
    private List<Lignecommande> lignescommandes;

	@Override
	public int hashCode() {
		return Objects.hash(libelle, putnd,pueuro);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Servicefact produit = (Servicefact) obj;
        return Double.compare(produit.putnd, putnd) == 0 &&
        		Double.compare(produit.pueuro, pueuro) == 0 &&
                Objects.equals(libelle, produit.libelle) ;
                
	}
	
	 

	
}
