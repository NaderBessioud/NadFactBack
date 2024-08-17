package tn.famytech.esprit.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.BonLivraison;

@Repository
public interface BLRepo  extends CrudRepository<BonLivraison, Long>{

}
