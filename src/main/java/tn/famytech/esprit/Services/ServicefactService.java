package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Servicefact;
import tn.famytech.esprit.Repositories.FactureRepo;
import tn.famytech.esprit.Repositories.ServicefactRepo;

@Service
public class ServicefactService {
	@Autowired
	private ServicefactRepo produitRepo;
	
	@Autowired
	private FactureRepo factureRepo;
	
	@Autowired
	private ServicefactRepo servicefactRepo;
	

	public Servicefact getProduitById(long id) {
		return produitRepo.findById(id).orElse(null);
	}
	
	public Servicefact getProduitFromList(List<Servicefact> produits,long id) {
		for (Servicefact produit : produits) {
			if(produit.getIdP()==id) {
				return produit;
			}
		}
		return null;
	}
	
	public List<Servicefact> DisplayAll(){
		return produitRepo.findAll();
	}
	
	public List<Servicefact> DisplayNotArchiverService(){
		return produitRepo.findByArchived(false);
	}
	
	public  List<Servicefact> addProduct(Servicefact produit){
		produit.setArchived(false);
		produitRepo.save(produit);
		return DisplayAll();
	}
	
	public List<Servicefact> SearchProduit(String search){
		List<Servicefact> result=new ArrayList<Servicefact>();
		for (Servicefact produit : DisplayAll()) {
			String pu = Double.toString(produit.getPutnd());
			String pueuro = Double.toString(produit.getPueuro());
			if(produit.getLibelle().toLowerCase().contains(search.toLowerCase()) || pu.contains(search) || pueuro.contains(search)) {
				result.add(produit);
			}
		}
		return result;
	}
	
	   
    public Page<Servicefact> findPaginated(int pageNum, int pageSize){
    	List<Servicefact> services=DisplayAll();
    	Pageable pageable = PageRequest.of(pageNum-1, pageSize);
    	int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), services.size());

        List<Servicefact> sublist = services.subList(start, end);

    	
    	
    	return new PageImpl<Servicefact>(sublist, pageable, services.size());
    }
    
    
    public List<Servicefact> ArchiverService(long id){
    	Servicefact service=servicefactRepo.findById(id).get();
    	service.setArchived(true);
    	servicefactRepo.save(service);
    	return DisplayAll();
    }
    
    public List<Servicefact> DesarchiverService(long id){
    	Servicefact service=servicefactRepo.findById(id).get();
    	service.setArchived(false);
    	servicefactRepo.save(service);
    	return DisplayAll();
    }

}
