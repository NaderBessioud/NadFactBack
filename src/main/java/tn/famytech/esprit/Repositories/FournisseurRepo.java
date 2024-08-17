package tn.famytech.esprit.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import tn.famytech.esprit.Entites.Fournisseur;

public interface FournisseurRepo extends CrudRepository<Fournisseur, Long> {
	Fournisseur findByLibelle(String libelle);
	
	@Query("Select F from Fournisseur F where F.archived = false")
	List<Fournisseur> getNotArchivedFournisseur();
	

}
