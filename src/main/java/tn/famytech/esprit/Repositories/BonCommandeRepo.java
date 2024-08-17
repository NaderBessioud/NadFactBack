package tn.famytech.esprit.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.BonCommande;

@Repository
public interface BonCommandeRepo extends CrudRepository<BonCommande, Long> {

}
