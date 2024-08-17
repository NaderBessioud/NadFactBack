package tn.famytech.esprit.Repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Reglement;
import tn.famytech.esprit.Entites.ReglementStatus;
import tn.famytech.esprit.Entites.ReglementType;


@Repository
public interface ReglementRepo extends CrudRepository<Reglement, Long> {
	List<Reglement> findByRegclientAndStatus(Client c,ReglementStatus s);
	List<Reglement> findByStatus(ReglementStatus s);
	
	 @Query("Select r.montant FROM Reglement r WHERE r.datepayement BETWEEN :startDate AND :endDate AND r.type=:type")
	    List<Double> TotalMonth(Date startDate,Date endDate,ReglementType type);
	 
	 @Query("Select r.montant FROM Reglement r WHERE YEAR(r.datepayement) = :year AND r.type=:type")
	    List<Double> TotalYear(int year,ReglementType type);
	 
	 @Query("Select r.montant FROM Reglement r join r.regclient c WHERE c.idU = :id AND r.datepayement BETWEEN :startDate AND :endDate AND r.type=:type")
	    List<Double> TotalMonthByClient(Date startDate,Date endDate,ReglementType type,long id);
	 
	 @Query("Select r.montant FROM Reglement r join r.regclient c WHERE c.idU = :id AND YEAR(r.datepayement) = :year AND r.type=:type")
	    List<Double> TotalYearByClient(int year,ReglementType type,long id);
	 
	 
	 @Query("Select r.fb FROM Reglement r WHERE r.datepayement BETWEEN :startDate AND :endDate AND r.type=:type")
	    List<Double> TotalFbByMonth(Date startDate,Date endDate,ReglementType type);
	 
	 @Query("Select r.fb FROM Reglement r join r.regclient c WHERE c.idU = :id AND r.datepayement BETWEEN :startDate AND :endDate AND r.type=:type")
	    List<Double> TotalFbMonthByClient(Date startDate,Date endDate,ReglementType type,long id);
	 
	 @Query("Select r.fb FROM Reglement r WHERE YEAR(r.datepayement) = :year AND r.type=:type")
	    List<Double> TotalFbYear(int year,ReglementType type);
	 
	 @Query("Select r.fb FROM Reglement r join r.regclient c WHERE c.idU = :id AND YEAR(r.datepayement) = :year AND r.type=:type")
	    List<Double> TotalFbYearByClient(int year,ReglementType type,long id);
}

