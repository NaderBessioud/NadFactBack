package tn.famytech.esprit.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.Servicefact;

@Repository
public interface ServicefactRepo extends JpaRepository<Servicefact, Long> {
	
	boolean existsByLibelle(String libelle);
	
	 Servicefact findByLibelle(String libelle);
	 
	 List<Servicefact> findByArchived(boolean archived);

}
