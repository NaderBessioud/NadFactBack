package tn.famytech.esprit.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Personel;

@Repository
public interface ClientRepo extends CrudRepository<Client, Long> {
	Client findByLibelle(String lib);
	List<Client> findByArchived(boolean archived);
	
	@Query("SELECT pers from Personel pers join pers.facturesuser f join f.client c where c.email = :email")
	List<Personel> findInteractPersonel(@Param("email") String email);
	
	Client findByEmail(String email);

}
