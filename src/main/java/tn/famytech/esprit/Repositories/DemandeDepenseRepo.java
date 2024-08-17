package tn.famytech.esprit.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.DemandeDepense;

@Repository
public interface DemandeDepenseRepo extends CrudRepository<DemandeDepense, Long> {
	@Query("SELECT D from DemandeDepense D where D.status=2 order by D.date desc")
	List<DemandeDepense> getDemandeEnAttente();

}
