package tn.famytech.esprit.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.DepenseReelle;

@Repository
public interface DepenseReelleRepo extends CrudRepository<DepenseReelle, Long> {

}
