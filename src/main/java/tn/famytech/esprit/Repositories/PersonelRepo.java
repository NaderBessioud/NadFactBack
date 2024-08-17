package tn.famytech.esprit.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.User;

public interface PersonelRepo extends CrudRepository<Personel, Long> {
	
	Personel findByEmail(String email);
	
	 @Query("SELECT DISTINCT u FROM User u WHERE u.id IN " +
	           "(SELECT m.reciever.idU FROM Message m WHERE m.sender.idU = :userId) " +
	           "OR u.id IN " +
	           "(SELECT m.sender.idU FROM Message m WHERE m.reciever.idU = :userId)")
	    List<User> findContacts(@Param("userId") long userId);
	 List<Personel> findByActive(boolean active);

}
