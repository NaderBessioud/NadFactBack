package tn.famytech.esprit.Repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.DTO.FactureDateAndNumber;
import tn.famytech.esprit.DTO.TotalAndTva;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FacturePayementStatus;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.TypeFacture;

@Repository
public interface FactureRepo extends CrudRepository<Facture, Long>, PagingAndSortingRepository<Facture, Long>  {
	
	 
	    Facture findFirstByDateemissionAndType(Date date,TypeFacture type);
	List<Facture> findByPayementstatus(FacturePayementStatus status);
	 
	 @Query("SELECT COUNT(f) FROM Facture f WHERE YEAR(f.dateemission) = :year AND f.archived = :archived AND(f.status = 6 OR f.status = 7) ")
	    long countByDateemissionAndType(@Param("year") int year,@Param("archived") boolean archived);
	 
	 @Query("SELECT MAX(f.number) FROM Facture f WHERE YEAR(f.dateemission) = :year AND f.archived = false AND (f.status = 7 OR f.status = 6)")
	 long facturenumber(@Param("year") int year);
	 
	 @Query("SELECT MAX(f.number) FROM Facture f WHERE YEAR(f.dateemission) = :year AND f.archived = :archived AND (f.status = 0 OR f.status = 1 OR f.status = 2 OR f.status = 3 OR f.status = 4)")
	    Long countProformaByDateemissionAndType(@Param("year") int year,@Param("archived") boolean archived);
	 
	
	    List<Facture> findByClientAndArchivedAndStatusAndPayementstatus(Client client,boolean archived,FactureStatus status,FacturePayementStatus payementStatus);
	 
	    List<Facture> findByClientAndArchivedAndStatusAndPayementstatusAndType(Client client,boolean archived,FactureStatus status,FacturePayementStatus payementStatus,TypeFacture type);
	    
	    List<Facture> findByClientAndArchived(Client client,boolean archived);
	    
	    List<Facture> findByTypeAndArchived(TypeFacture type,boolean archived);
	    
	    List<Facture> findByClientAndTypeAndArchived(Client client,TypeFacture type,boolean archived);
	    
	    @Query("SELECT f FROM Facture f WHERE f.archived = :archived AND f.traited = 0")
	    List<Facture> findFacturesByArchived(@Param("archived") boolean archived);
	    
	    @Query("SELECT f FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate AND f.archived = :archived")
	    List<Facture> findFacturesByDateEmissionRangeAndArchived(Date startDate, Date endDate,@Param("archived") boolean archived);
	    
	    @Query("SELECT f FROM Facture f JOIN f.client c WHERE f.dateemission BETWEEN :startDate AND :endDate AND c.idU = :client AND f.archived = :archived")
	    List<Facture> findFacturesByDateEmissionRangeAndClientAndArchived(Date startDate, Date endDate,long client,@Param("archived") boolean archived);
	    
	    @Query("SELECT f FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate AND f.type = :factureType AND f.archived = :archived")
	    List<Facture> findFacturesByDateEmissionRangeAndTypeAndArchived(Date startDate, Date endDate, TypeFacture factureType,@Param("archived") boolean archived);
	    
	    @Query("SELECT f FROM Facture f JOIN f.client c WHERE f.dateemission BETWEEN :startDate AND :endDate AND f.type = :factureType AND c.idU = :client AND f.archived = :archived ")
	    List<Facture> findFacturesByDateEmissionRangeAndTypeAndClientAndArchived(Date startDate, Date endDate, TypeFacture factureType,long client,@Param("archived") boolean archived);
	    
	    Facture  findByPdfpath(String pdfpath);
	    
	    Facture findByNumber(long number);
	    Facture findByNumberAndStatus(long number,FactureStatus status);
	    @Query(value = "SELECT * FROM facture WHERE (status = 7 OR status = 6) AND traited=false AND number = :number  LIMIT 1", nativeQuery = true)
	    Facture findFactureByNumber(long number);
	    
	    @Query(value = "SELECT * FROM facture WHERE (f.status = 0 OR f.status = 1 OR f.status = 2 OR f.status = 3 OR f.status = 4)  AND number = :number  LIMIT 1", nativeQuery = true)
	    Facture findProformaByNumber(long number);
	    
	    @Query(value = "SELECT * FROM facture WHERE (status = 7 OR status = 6) AND traited=false AND number = (SELECT MAX(number) FROM Facture)  LIMIT 1", nativeQuery = true)
	    Facture findFactureWithMaxNumber();
	    
	    @Query("Select f FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Facture> FactureBydateAndTypeAndStatus(Date startDate,Date endDate,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f FROM Facture f join f.client c  WHERE  c.idU = :id AND f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Facture> FactureBydateAndTypeAndStatusByClient(Date startDate,Date endDate,TypeFacture type,FactureStatus status,long id);
	    
	    @Query("Select f FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Facture> FactureByDateAndTypeAndStatus(Date startDate,Date endDate,TypeFacture type,FactureStatus status);
	    
	    @Query("SELECT new tn.famytech.esprit.DTO.FactureDateAndNumber(f.number, f.dateemission) FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = 7 or f.status = 6)")
	    List<FactureDateAndNumber> FactureBydate(Date startDate,Date endDate);
	    
	    @Query("SELECT new tn.famytech.esprit.DTO.FactureDateAndNumber(f.number, f.dateemission) FROM Facture f WHERE YEAR(f.dateemission) = :year And (f.status = 7)")
	    List<FactureDateAndNumber> FactureByYear(int year);
	    
	    @Query("Select f FROM Facture f WHERE YEAR(f.dateemission) = :year And (f.status = :status) AND f.type=:type")
	    List<Facture> FactureByYearAndTypeAndStatus(int year,TypeFacture type,FactureStatus status);
	    
	    
	    
	    @Query("Select f FROM Facture f join f.client c  WHERE  c.idU = :id AND YEAR(f.dateemission) = :year And (f.status = :status) AND f.type=:type")
	    List<Facture> FactureByYearAndTypeAndStatusByClient(int year,TypeFacture type,FactureStatus status,long id);
	    
	    @Query("SELECT f FROM Facture f WHERE  YEAR(f.dateemission) = :year And (f.status =7)")
	    List<Facture> getCanceledInvoice(@Param("year") int year);
	
	    
	    
	    @Query("SELECT COUNT(f) FROM Facture f WHERE MONTH(f.dateemission) = :month AND YEAR(f.dateemission) = :year And (f.status = 7)")
	    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);
	
	    @Query("SELECT COUNT(f) FROM Facture f WHERE YEAR(f.dateemission) = :year And (f.status = 7)")
	    long countByYear(@Param("year") int year);
	    
	    @Query("SELECT f1.number FROM Facture f1 WHERE YEAR(f1.dateemission) = :year AND (f1.status = 7 OR f1.status = 6) AND NOT EXISTS (SELECT f2 FROM Facture f2 WHERE f2.number = f1.number + 1 And (f2.status = 7 OR f2.status = 6)) AND f1.number < (SELECT MAX(f3.number) FROM Facture f3 WHERE YEAR(f3.dateemission) = :year AND (f3.status = 7 OR f3.status = 6))")
	    List<Long> findTheGapNumber(@Param("year") int year);
	    
	    @Query("SELECT f1 FROM Facture f1 WHERE f1.ref=:ref AND f1.idF != :id")
	    List<Facture> factureRemplacement(@Param("ref") long ref,@Param("id") long id);
	    
	    boolean existsByDatecvAndTvaAndCoursAndClient(Date datecv,double tva,double cours,Client client);
	    
	    
	    @Query("Select f.totalttc FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Double> TotalSumMonth(Date startDate,Date endDate,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f.totalttc FROM Facture f join f.client c  WHERE  c.idU = :id AND  f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Double> TotalSumMonthByClient(Date startDate,Date endDate,TypeFacture type,FactureStatus status,long id);
	    
	    @Query("Select f.tva FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Double> TotalTVASumMonth(Date startDate,Date endDate,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f.ctvtimbre FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Double> TotalTimbreSumMonth(Date startDate,Date endDate,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f.ctvtimbre FROM Facture f join f.client c  WHERE  c.idU = :id AND f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Double> TotalTimbreSumMonthByClient(Date startDate,Date endDate,TypeFacture type,FactureStatus status,long id);
	    
	    
	    
	    @Query("Select f.totalttc FROM Facture f WHERE YEAR(f.dateemission) = :year And (f.status = :status) AND f.type=:type")
	    List<Double> TotalSumYear(int year,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f.totalttc FROM Facture f join f.client c  WHERE  c.idU = :id AND YEAR(f.dateemission) = :year And (f.status = :status) AND f.type=:type")
	    List<Double> TotalSumYearByClient(int year,TypeFacture type,FactureStatus status,long id);
	    
	    @Query("Select f.tva FROM Facture f WHERE f.dateemission BETWEEN :startDate AND :endDate And (f.status = :status) AND f.type=:type")
	    List<Double> TotalTVASumYear(Date startDate,Date endDate,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f.ctvtimbre FROM Facture f WHERE YEAR(f.dateemission) = :year And (f.status = :status) AND f.type=:type")
	    List<Double> TotalTimbreSumYear(int year,TypeFacture type,FactureStatus status);
	    
	    @Query("Select f.ctvtimbre FROM Facture f join f.client c  WHERE  c.idU = :id AND YEAR(f.dateemission) = :year And (f.status = :status) AND f.type=:type")
	    List<Double> TotalTimbreSumYearByClient(int year,TypeFacture type,FactureStatus status,long id);
	    
	    List<Facture> findByClientAndStatus(Client client,FactureStatus status);
	    
	    
	    
	
	    

}
