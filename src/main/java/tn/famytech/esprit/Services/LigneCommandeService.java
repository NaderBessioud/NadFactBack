package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Servicefact;
import tn.famytech.esprit.Repositories.FactureRepo;
import tn.famytech.esprit.Repositories.LigneCommandeRepo;
import tn.famytech.esprit.Repositories.ServicefactRepo;

@Service
public class LigneCommandeService {
	@Autowired
	private LigneCommandeRepo commandeRepo;
	
	@Autowired
	private ServicefactRepo produitRepo;
	
	@Autowired
	private FactureRepo factureRepo;
	
	@Transactional
	public Lignecommande AddLigneCommande(Lignecommande lc,Facture f) {
		Servicefact p=lc.getProduit();
		p = produitRepo.findByLibelle(lc.getProduit().getLibelle());
		
		lc.setProduit(p);
		List<Facture> facts = new ArrayList<Facture>();
		facts.add(f);
		lc.setFacturecom(facts);
		lc=commandeRepo.save(lc);
		if(p.getLignescommandes() == null) {
			p.setLignescommandes(new ArrayList<Lignecommande>());
		}
		p.getLignescommandes().add(lc);
		produitRepo.save(p);
		return lc;
	}
	
	@Transactional
	public Lignecommande updateLigneCommande(Lignecommande lc,long idf) {
		Facture fat=factureRepo.findById(idf).get();
		Lignecommande lc1=commandeRepo.findById(lc.getIdLC()).get();
		lc1.getFacturecom().remove(fat);
		fat.getCommandes().remove(lc1);
		Lignecommande  newC=commandeRepo.findExistingLigneCommande(lc.getCommentaire(), lc.getQte(), lc.getProduit().getPutnd(),lc.getProduit().getPueuro(), lc.getProduit().getLibelle());
		if(newC != null) {
			newC.getFacturecom().add(fat);
			commandeRepo.save(newC);
			
		}
		
		else {
			
			Lignecommande newCom=new Lignecommande();
			newCom.setCommentaire(lc.getCommentaire());
			newCom.setQte(lc.getQte());
			
			Servicefact p=lc.getProduit();
			Servicefact oldp =produitRepo.findById(lc.getProduit().getIdP()).orElse(null);
			if(oldp != null) {
				oldp.getLignescommandes().remove(lc1);
			}
				p=produitRepo.findByLibelle(lc.getProduit().getLibelle());
			
			
				newCom.setProduit(p);
			
				newCom=commandeRepo.save(newCom);
			
			p.getLignescommandes().add(newCom);
			produitRepo.save(p);
			
			
			
			
			factureRepo.save(fat);
			return newCom;
			
		}
		
		

		
		Servicefact p=lc.getProduit();
		Servicefact oldp =produitRepo.findById(lc.getProduit().getIdP()).orElse(null);
		if(oldp != null) {
			oldp.getLignescommandes().remove(lc1);
		}
	
			p=produitRepo.findByLibelle(lc.getProduit().getLibelle());
		
		
		lc1.setProduit(lc.getProduit());
		lc1.setCommentaire(lc.getCommentaire());
		lc1.setQte(lc.getQte());
		lc1=commandeRepo.save(lc1);
		
		p.getLignescommandes().add(lc1);
		produitRepo.save(p);
		return lc1;
		}
		
	
	
	@Transactional
	public void deleteLigneCommande(long id,long idf) {
		Lignecommande lc=commandeRepo.findById(id).get();
		Facture f=factureRepo.findById(idf).get();
		Servicefact p=lc.getProduit();
		lc.getFacturecom().remove(f);
		p.getLignescommandes().remove(lc);
		commandeRepo.save(lc);
		produitRepo.save(p);
	}
	
	@Transactional
	public Lignecommande affectLCTofacture(long id,Facture f) {
		Lignecommande lc=commandeRepo.findById(id).get();
		lc.getFacturecom().add(f);
		return lc;
	}
	
	public boolean checkIfExist(Lignecommande lc) {
		boolean result=false;
		
			result=commandeRepo.existsByQteAndCommentaire(lc.getQte(), lc.getCommentaire());
		
		return result;
	}

}
