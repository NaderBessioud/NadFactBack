package tn.famytech.esprit.Repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.DTO.FactureDateAndNumber;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.TypeFacture;

@Repository
public interface AvoirRepo extends CrudRepository<Avoir, Long> {
	
	@Query("SELECT MAX(a.number) FROM Avoir a WHERE YEAR(a.dateemission) = :year ")
	 Long Avoirnumber(@Param("year") int year);
	
	@Query("SELECT a1.number FROM Avoir a1 WHERE YEAR(a1.dateemission) = :year AND (a1.status = 1 OR a1.status = 2) AND NOT EXISTS (SELECT a2 FROM Avoir a2 WHERE a2.number = a1.number + 1) AND a1.number < (SELECT MAX(a3.number) FROM Avoir a3 WHERE YEAR(a3.dateemission) = :year AND (a3.status = 1 OR a3.status = 2))")
    List<Long> findTheGapNumber(@Param("year") int year);
	
    
    @Query("Select a.montant FROM Avoir a WHERE a.dateemission BETWEEN :startDate AND :endDate And (a.status = :status)")
    List<Double> TotalFactureAvoirMonth(Date startDate,Date endDate,AvoirStatus status);
    
    @Query("Select a FROM Avoir a WHERE a.dateemission BETWEEN :startDate AND :endDate And (a.status = :status)")
    List<Avoir> AvoirsByMonth(Date startDate,Date endDate,AvoirStatus status);
    
    @Query("Select a FROM Avoir a WHERE YEAR(a.dateemission) = :year And (a.status = :status)")
    List<Avoir> AvoirsByYear(int year,AvoirStatus status);
    
    @Query("SELECT new tn.famytech.esprit.DTO.FactureDateAndNumber(a.number, a.dateemission) FROM Avoir a WHERE a.dateemission BETWEEN :startDate AND :endDate And (a.status = 2)")
    List<FactureDateAndNumber> AvoirBydate(Date startDate,Date endDate);
    
   @Query("SELECT new tn.famytech.esprit.DTO.FactureDateAndNumber(a.number, a.dateemission) FROM Avoir a WHERE YEAR(a.dateemission) = :year And (a.status = 2)")
    List<FactureDateAndNumber> AvoirByYear(int year);
    
    List<Avoir> findByFact(Facture fact);
    
    @Query("SELECT MAX(a.number) FROM Avoir a WHERE (a.status = 1 or a.status=2 ) AND FUNCTION('YEAR', a.dateemission) = :year")
    Long findMaxNumberByYearAndStatus(@Param("year") int year);
    

}
