package tn.famytech.esprit.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import tn.famytech.esprit.DTO.ClientAndReglement;
import tn.famytech.esprit.DTO.FacturesReglements;
import tn.famytech.esprit.DTO.FacturesReglementsRetenue;
import tn.famytech.esprit.DTO.FacturesStat;
import tn.famytech.esprit.DTO.ReglementPoint;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FacturePayementStatus;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Reglement;
import tn.famytech.esprit.Entites.ReglementStatus;
import tn.famytech.esprit.Entites.ReglementType;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Repositories.ClientRepo;
import tn.famytech.esprit.Repositories.FactureRepo;
import tn.famytech.esprit.Repositories.ReglementRepo;

@Service
public class ReglementService {
	
	@Autowired
	private ReglementRepo reglementRepo;
	
	@Autowired
	private ClientRepo clientRepo;
	
	@Autowired
	private FactureRepo factureRepo;
	
	
	@Autowired
	 private  ObjectMapper objectMapper;
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	public List<ClientAndReglement> addReglement(ClientAndReglement reg) {
		List<Reglement> result=new ArrayList<Reglement>();
		List<ClientAndReglement> result1=new ArrayList<ClientAndReglement>();
		reg.getReglement().setStatus(ReglementStatus.Reglement);
		reg.getReglement().setMontantrestant(reg.getReglement().getMontant()-reg.getReglement().getFb());
		
		 reglementRepo.save(reg.getReglement());
		 if(reg.getLib().length() == 0) {
			 result=reglementRepo.findByStatus(ReglementStatus.Reglement);
			 result.addAll(reglementRepo.findByStatus(ReglementStatus.Reglement_Affecte_Partiel));
			 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
		                .thenComparing(Reglement::getIdR).reversed());
			 for (Reglement reglement : result) {
				result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
			}
			 return  result1;
		 }
		 else {
			 result=reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(reg.getLib()).getIdU(), ReglementStatus.Reglement);
			 result.addAll(reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(reg.getLib()).getIdU(), ReglementStatus.Reglement_Affecte_Partiel));
			 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
		                .thenComparing(Reglement::getIdR).reversed());
			 for (Reglement reglement : result) {
					result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
				}
				 return  result1;
		 }
		
	}
	
	public List<ClientAndReglement> DisplayAll(){
		List<ClientAndReglement> result=new ArrayList<ClientAndReglement>();
		 for (Reglement reglement : reglementRepo.findAll()) {
				result.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
			}
			 return  result;
		
	}
	
	
	
	public List<ClientAndReglement> DisplayClientReglement(String lib){
		List<Reglement> result=new ArrayList<Reglement>();
		List<ClientAndReglement> result1=new ArrayList<ClientAndReglement>();
		result=reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(lib).getIdU(), ReglementStatus.Reglement);
		 result.addAll(reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(lib).getIdU(), ReglementStatus.Reglement_Affecte_Partiel));
		 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
	                .thenComparing(Reglement::getIdR).reversed());
		 for (Reglement reglement : result) {
			 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
			}
			 return  result1;
		
		
	}
	
	
	public List<ClientAndReglement> DisplayReglementByFacture(long idf){
		
		List<Reglement> result=new ArrayList<Reglement>();
		List<ClientAndReglement> result1=new ArrayList<ClientAndReglement>();
		Facture f=factureRepo.findById(idf).get();
		long idu=f.getClient().getIdU();
		if(f.getType()==TypeFacture.Export) {
			
			result=reglementRepo.ReglementByClientandstatus(idu, ReglementStatus.Reglement,ReglementType.Euro);
			 result.addAll(reglementRepo.ReglementByClientandstatus(idu, ReglementStatus.Reglement_Affecte_Partiel,ReglementType.Euro));
		}
		else {
			result=reglementRepo.ReglementByClientandstatus(idu, ReglementStatus.Reglement,ReglementType.TND);
			 result.addAll(reglementRepo.ReglementByClientandstatus(idu, ReglementStatus.Reglement_Affecte_Partiel,ReglementType.TND));
		}
		
		 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
	                .thenComparing(Reglement::getIdR).reversed());
		 for (Reglement reglement : result) {
			 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
			}
			 return  result1;
		
		
	}
	
	
	
	public List<ClientAndReglement> DisplayReglemensNotAffected(){
		List<ClientAndReglement> result1=new ArrayList<ClientAndReglement>();
		List<Reglement> result=reglementRepo.findByStatus(ReglementStatus.Reglement);
		result.addAll(reglementRepo.findByStatus(ReglementStatus.Reglement_Affecte_Partiel));
		Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
                .thenComparing(Reglement::getIdR).reversed());
		 for (Reglement reglement : reglementRepo.findAll()) {
			 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
			}
			 return  result1;
	
	}
	
	@Transactional
	public FacturesReglements AffectRegelementToFacture(long idR,long idF,FacturesReglements frr) {
		FacturesReglements result=new FacturesReglements();
		Facture facture=factureRepo.findById(idF).get();
		Reglement reglement=reglementRepo.findById(idR).get();
		
		if(facture.getTotalrestant() == reglement.getMontantrestant()) {
			reglement.setStatus(ReglementStatus.Reglement_Affecte);
			reglement.getRegFactures().add(facture);
			reglement= reglementRepo.save(reglement);
			facture.setPayementstatus(FacturePayementStatus.Paye);
			facture.getReglementsFact().add(reglement);
			facture= factureRepo.save(facture);
		}
		
		else if(facture.getTotalrestant() > reglement.getMontantrestant()) {
			
			reglement.setStatus(ReglementStatus.Reglement_Affecte);
			reglement.getRegFactures().add(facture);
			reglement= reglementRepo.save(reglement);
			facture.setPayementstatus(FacturePayementStatus.Paye_Partiel);
			facture.setTotalrestant(facture.getTotalrestant()-reglement.getMontantrestant());
			facture.getReglementsFact().add(reglement);
			 facture= factureRepo.save(facture);
		}
		else {
			
			reglement.setStatus(ReglementStatus.Reglement_Affecte_Partiel);
			reglement.getRegFactures().add(facture);
			reglement.setMontantrestant(reglement.getMontantrestant()-facture.getTotalrestant());
			reglement= reglementRepo.save(reglement);
			facture.setTotalrestant(0);
			facture.setPayementstatus(FacturePayementStatus.Paye);
			facture.getReglementsFact().add(reglement);
			facture= factureRepo.save(facture);
		}
		for (int i=0;i<frr.getReglements().size();i++) {
			if(frr.getReglements().get(i).getReglement().getIdR() == idR) {
				frr.getReglements().set(i, new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(), reglement));
			}
		}
		
		for (int i=0;i<frr.getFactures().size();i++) {
			if(frr.getFactures().get(i).getIdF() == idF) {
				frr.getFactures().set(i, facture);
			}
		}
		result.setReglements(frr.getReglements());
		result.setFactures(frr.getFactures());
		return result;
	
	}
	
	@Transactional
	public FacturesReglements AffectRegelementToFactureWithRetenue(long idR,long idF,FacturesReglementsRetenue frr) {
		FacturesReglements result=new FacturesReglements();
		Facture facture=factureRepo.findById(idF).get();
		Reglement reglement=reglementRepo.findById(idR).get();
		facture.setRetenueaff(true);
		facture.setRetenuepath(frr.getRetenue());
		if(facture.getTotalrestant() == reglement.getMontantrestant()) {
			reglement.setStatus(ReglementStatus.Reglement_Affecte);
			reglement= reglementRepo.save(reglement);
			facture.setPayementstatus(FacturePayementStatus.Paye);
			facture= factureRepo.save(facture);
		}
		
		else if(facture.getTotalrestant() > reglement.getMontantrestant()) {
			reglement.setStatus(ReglementStatus.Reglement_Affecte);
			reglement= reglementRepo.save(reglement);
			facture.setPayementstatus(FacturePayementStatus.Paye_Partiel);
			facture.setTotalrestant(facture.getTotalrestant()-reglement.getMontantrestant());
			facture= factureRepo.save(facture);
		}
		else {
		
			 reglement.setStatus(ReglementStatus.Reglement_Affecte_Partiel);
			reglement.setMontantrestant(reglement.getMontantrestant()-facture.getTotalrestant());
			reglement= reglementRepo.save(reglement);
			facture.setPayementstatus(FacturePayementStatus.Paye);
			facture= factureRepo.save(facture);
		}
		for (int i=0;i<frr.getReglements().size();i++) {
			if(frr.getReglements().get(i).getReglement().getIdR() == idR) {
				frr.getReglements().set(i, new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(), reglement));
			}
		}
		
		for (int i=0;i<frr.getFactures().size();i++) {
			if(frr.getFactures().get(i).getIdF() == idF) {
				frr.getFactures().set(i, facture);
			}
		}
		result.setReglements(frr.getReglements());
		result.setFactures(frr.getFactures());
		return result;
	}

	 public double getTotalReglement(int year,int month,ReglementType type) throws ParseException {
	    	
	   	 String formattedMonth = String.format("%02d", month);
	   	 String start=year+"-"+formattedMonth+"-01";
		     String end=year+"-"+formattedMonth+"-28";
		     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		     Date startdate=dateFormat.parse(start);
		     Date enddate=dateFormat.parse(end);
		     double result=reglementRepo.TotalMonth(startdate, enddate, type) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     
	   	return result ;
	   }
	 
	 public double getTotalReglementCTV(int year,int month,double cours) throws ParseException {
	    	
	   	 String formattedMonth = String.format("%02d", month);
	   	 String start=year+"-"+formattedMonth+"-01";
		     String end=year+"-"+formattedMonth+"-28";
		     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		     Date startdate=dateFormat.parse(start);
		     Date enddate=dateFormat.parse(end);
		     double result=reglementRepo.TotalMonth(startdate, enddate, ReglementType.Euro) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     result=result*cours;
		     
	   	return result ;
	   }
	 
	 public double getTotalReglementByYear(int year,ReglementType type) throws ParseException {
	    	
	   
		     double result=reglementRepo.TotalYear(year, type) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     
	   	return result ;
	   }
	 
	 public double getTotalReglementCTVByYear(int year,double cours) throws ParseException {
	    	
	 
		     double result=reglementRepo.TotalYear(year, ReglementType.Euro) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     result=result*cours;
		    double resulttnd=reglementRepo.TotalYear(year, ReglementType.TND) .stream()
		 	        .mapToDouble(Double::doubleValue).sum();
		     result+=resulttnd;  
	   	return result ;
	   }
	 

	 public double getTotalReglementByClient(int year,int month,ReglementType type,long id) throws ParseException {
	    	
	   	 String formattedMonth = String.format("%02d", month);
	   	 String start=year+"-"+formattedMonth+"-01";
		     String end=year+"-"+formattedMonth+"-28";
		     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		     Date startdate=dateFormat.parse(start);
		     Date enddate=dateFormat.parse(end);
		     double result=reglementRepo.TotalMonthByClient(startdate, enddate, type,id) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     
	   	return result ;
	   }
	 
	 public double getTotalReglementCTVByClient(int year,int month,double cours,long id) throws ParseException {
	    	
	   	 String formattedMonth = String.format("%02d", month);
	   	 String start=year+"-"+formattedMonth+"-01";
		     String end=year+"-"+formattedMonth+"-28";
		     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		     Date startdate=dateFormat.parse(start);
		     Date enddate=dateFormat.parse(end);
		     double result=reglementRepo.TotalMonthByClient(startdate, enddate, ReglementType.Euro,id) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     result=result*cours;
		     
	   	return result ;
	   }
	 
	 public double getTotalReglementByYearByClient(int year,ReglementType type,long id) throws ParseException {
	    	
	   
		     double result=reglementRepo.TotalYearByClient(year, type,id) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     
	   	return result ;
	   }
	 
	 public double getTotalReglementCTVByYearByClient(int year,double cours,long id) throws ParseException {
	    	
	 
		     double result=reglementRepo.TotalYearByClient(year, ReglementType.Euro,id) .stream()
	        .mapToDouble(Double::doubleValue).sum();
		     result=result*cours;
		     
	   	return result ;
	   }
	 
	 private double CalculFB(int year,int month,ReglementType type) throws ParseException {
		 String formattedMonth = String.format("%02d", month);
	   	 String start=year+"-"+formattedMonth+"-01";
		     String end=year+"-"+formattedMonth+"-28";
		     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		     Date startdate=dateFormat.parse(start);
		   
		     Date enddate=dateFormat.parse(end);
		     
		     double result=reglementRepo.TotalFbByMonth(startdate, enddate, type).stream()
		    	        .mapToDouble(Double::doubleValue).sum();
		    		     
		    	   	return result ;
	 }
	 
	 private double CalculFBByClient(int year,int month,ReglementType type,long id) throws ParseException {
		 String formattedMonth = String.format("%02d", month);
	   	 String start=year+"-"+formattedMonth+"-01";
		     String end=year+"-"+formattedMonth+"-28";
		     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		     Date startdate=dateFormat.parse(start);
		   
		     Date enddate=dateFormat.parse(end);
		     
		     double result=reglementRepo.TotalFbMonthByClient(startdate, enddate, type,id).stream()
		    	        .mapToDouble(Double::doubleValue).sum();
		    		     
		    	   	return result ;
	 }
	 
	 private double CalculFBYear(int year,ReglementType type) throws ParseException {
		
	   	 
		     
		     double result=reglementRepo.TotalFbYear(year, type).stream()
		    	        .mapToDouble(Double::doubleValue).sum();
		    		     
		    	   	return result ;
	 }
	 
	 private double CalculFBByYearByClient(int year,ReglementType type,long id) throws ParseException {
		 
		     
		     double result=reglementRepo.TotalFbYearByClient(year, type,id).stream()
		    	        .mapToDouble(Double::doubleValue).sum();
		    		     
		    	   	return result ;
	 }
	 public FacturesStat calculReglementsStatMonth(int year,int month) throws ParseException, JsonMappingException, JsonProcessingException {
		 double cours=GetChangeRate();
		 FacturesStat result=new FacturesStat();
		 result.setTotaltnd(getTotalReglement(year, month, ReglementType.TND));
		 result.setTotaleuro(getTotalReglement(year, month, ReglementType.Euro));
		 result.setTotaleuroctv(getTotalReglementCTV(year, month,cours));
		 result.setTotalfact(result.getTotaltnd()+result.getTotaleuroctv());
		 
		 result.setTotalfb(CalculFB(year, month,ReglementType.TND));
		 result.setTotalfbeuro(CalculFB(year, month, ReglementType.Euro));
		 result.setTotalfbeuroctv(CalculFB(year, month, ReglementType.Euro) * cours);
		 result.setTotalfball(result.getTotalfb()+result.getTotalfbeuroctv());
		 result.setRegmontant(result.getTotalfact()-result.getTotalfball());
		 return result;
		 
	 }
	 
	 public FacturesStat calculReglementsStatYear(int year) throws ParseException, JsonMappingException, JsonProcessingException {
		 double cours=GetChangeRate();
		 FacturesStat result=new FacturesStat();
		 result.setTotaltnd(getTotalReglementByYear(year,ReglementType.TND));
		 result.setTotaleuro(getTotalReglementByYear(year,ReglementType.Euro));
		 result.setTotaleuroctv(getTotalReglementCTVByYear(year,cours));
		 result.setTotalfact(result.getTotaltnd()+result.getTotaleuroctv());
		 
		 result.setTotalfb(CalculFBYear(year,ReglementType.TND));
		 result.setTotalfbeuro(CalculFBYear(year, ReglementType.Euro));
		 result.setTotalfbeuroctv(CalculFBYear(year, ReglementType.Euro) * cours);
		 result.setTotalfball(result.getTotalfb()+result.getTotalfbeuroctv());
		 result.setRegmontant(result.getTotalfact()-result.getTotalfball());
		 
		 return result;
		 
	 }
	 
	 public FacturesStat calculReglementsStatMonthByClient(int year,int month,long id) throws ParseException, JsonMappingException, JsonProcessingException {
		 double cours=GetChangeRate();
		 FacturesStat result=new FacturesStat();
		 result.setTotaltnd(getTotalReglementByClient(year, month, ReglementType.TND,id));
		 result.setTotaleuro(getTotalReglementByClient(year, month, ReglementType.Euro,id));
		 result.setTotaleuroctv(getTotalReglementCTVByClient(year, month,cours,id));
		 result.setTotalfact(result.getTotaltnd()+result.getTotaleuro());
		 
		 result.setTotalfb(CalculFBByClient(year, month,ReglementType.TND,id));
		 result.setTotalfbeuro(CalculFBByClient(year, month, ReglementType.Euro,id));
		 result.setTotalfbeuroctv(CalculFBByClient(year, month, ReglementType.Euro,id) * cours);
		 result.setTotalfball(result.getTotalfb()+result.getTotalfbeuroctv());
		 result.setRegmontant(result.getTotalfact()-result.getTotalfball());
		 return result;
		 
	 }
	 
	 public FacturesStat calculReglementsStatYearByClient(int year,long id) throws ParseException, JsonMappingException, JsonProcessingException {
		 double cours=GetChangeRate();
		 FacturesStat result=new FacturesStat();
		 result.setTotaltnd(getTotalReglementByYearByClient(year,ReglementType.TND,id));
		 result.setTotaleuro(getTotalReglementByYearByClient(year,ReglementType.Euro,id));
		 result.setTotaleuroctv(getTotalReglementCTVByYearByClient(year,cours,id));
		 result.setTotalfact(result.getTotaltnd()+result.getTotaleuro());
		 
		 result.setTotalfb(CalculFBByYearByClient(year,ReglementType.TND,id));
		 result.setTotalfbeuro(CalculFBByYearByClient(year, ReglementType.Euro,id));
		 result.setTotalfbeuroctv(CalculFBByYearByClient(year, ReglementType.Euro,id) * cours);
		 result.setTotalfball(result.getTotalfb()+result.getTotalfbeuroctv());
		 result.setRegmontant(result.getTotalfact()-result.getTotalfball());
		 return result;
		 
	 }
	 
	 public double GetChangeRate() throws JsonMappingException, JsonProcessingException {
	        String API_URL = "https://v6.exchangerate-api.com/v6/b5c37627df67ccba6b2f50b9/latest/EUR";
	        RestTemplate restTemplate = restTemplate(); 
	    	ResponseEntity<String> response = restTemplate.getForEntity(API_URL, String.class);
	    	 String responseBody = response.getBody();
	         JsonNode root = objectMapper.readTree(responseBody);
	         JsonNode conversionRates = root.path("conversion_rates");
	     
	         return conversionRates.path("TND").asDouble();
	    	
	    	
	    }
	 
	 
	 public Reglement getReglementById(long id) {
		 return reglementRepo.findById(id).get();
	 }
	 
	 
		public List<ClientAndReglement> updateReglement(ClientAndReglement reg) {
			List<ClientAndReglement> result1=new ArrayList<ClientAndReglement>();
			List<Reglement> result=new ArrayList<Reglement>();
			reg.getReglement().setStatus(ReglementStatus.Reglement);
			reg.getReglement().setMontantrestant(reg.getReglement().getMontant()-reg.getReglement().getFb());
			
			
			 reglementRepo.save(reg.getReglement());
			 
			 
			 if(reg.getLib().length() == 0) {
				 result=reglementRepo.findByStatus(ReglementStatus.Reglement);
				 result.addAll(reglementRepo.findByStatus(ReglementStatus.Reglement_Affecte_Partiel));
				 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
			                .thenComparing(Reglement::getIdR).reversed());

				for (Reglement reglement : result) {
							 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
							}
			 return  result1;
			 }
			 else {
				 result=reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(reg.getLib()).getIdU(), ReglementStatus.Reglement);
				 result.addAll(reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(reg.getLib()).getIdU(), ReglementStatus.Reglement_Affecte_Partiel));
				 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
			                .thenComparing(Reglement::getIdR).reversed());
				 for (Reglement reglement : result) {
					 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
					}
				 return  result1;
			 }
			
		}
		
		
		public List<ClientAndReglement> DeleteReglement(String lib,long id) {
			List<Reglement> result=new ArrayList<Reglement>();
			List<ClientAndReglement> result1=new ArrayList<ClientAndReglement>();
			
			 reglementRepo.deleteById(id);
			 if(lib.length() == 0) {
				 result=reglementRepo.findByStatus(ReglementStatus.Reglement);
				 result.addAll(reglementRepo.findByStatus(ReglementStatus.Reglement_Affecte_Partiel));
				 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
			                .thenComparing(Reglement::getIdR).reversed());
				 for (Reglement reglement : result) {
					 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
					}
				 return  result1;
			 }
			 else {
				 result=reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(lib).getIdU(), ReglementStatus.Reglement);
				 result.addAll(reglementRepo.ReglementByClientandstatus(clientRepo.findByLibelle(lib).getIdU(), ReglementStatus.Reglement_Affecte_Partiel));
				 Collections.sort(result, Comparator.comparing(Reglement::getDatepayement)
			                .thenComparing(Reglement::getIdR).reversed());
				 for (Reglement reglement : result) {
					 result1.add(new ClientAndReglement(clientRepo.findById(reglement.getIdc()).get().getLibelle(),reglement));
					}
				 return  result1;
			 }
			
		}
		
		
		
		public List<ReglementPoint> getReglementsPoints() throws JsonMappingException, JsonProcessingException, ParseException{
			List<ReglementPoint> result=new ArrayList<ReglementPoint>();
			double cours=GetChangeRate();
			int year=LocalDate.now().getYear();
			for(int i=year-10;i<=year;i++) {
				result.add(new ReglementPoint(i, getTotalReglementCTVByYear(i,cours)));
			}
			return result;
			
		}
	 
	 
	 
}
