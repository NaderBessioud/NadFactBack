package tn.famytech.esprit.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import tn.famytech.esprit.Entites.Lignecommande;

@Repository
public interface LigneCommandeRepo extends CrudRepository<Lignecommande, Long> {
	@Query("SELECT lc FROM Lignecommande lc WHERE lc.commentaire = :commantaire AND lc.qte = :qte " +
	           "AND lc.produit.putnd = :pu AND lc.produit.pueuro = :pueuro AND lc.produit.libelle = :libelle")
	    Lignecommande findExistingLigneCommande(@Param("commantaire") String commantaire,
	                                            @Param("qte") int qte,
	                                            @Param("pu") double pu,
	                                            @Param("pueuro") double pueuro,
	                                            @Param("libelle") String libelle);
	
	boolean existsByQteAndCommentaire(double qte,String commentaire);

}
