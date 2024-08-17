package tn.famytech.esprit.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.Message;
import tn.famytech.esprit.Entites.User;

@Repository
public interface MessageRepo extends CrudRepository<Message, Long> {
	
	List<Message> findBySenderAndReciever(User sender,User receiver);
	
	@Query("Select m from Message m where (m.sender = :sender AND m.reciever = :receiver) OR (m.sender = :receiver AND m.reciever = :sender)  ORDER BY m.date DESC, m.hour DESC ")
	List<Message> getLastMsg(@Param("sender") User sender, @Param("receiver") User receiver);
}
