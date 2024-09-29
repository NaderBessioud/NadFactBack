package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tn.famytech.esprit.DTO.DepRealWithAdresse;
import tn.famytech.esprit.Entites.BCStatus;
import tn.famytech.esprit.Entites.BonCommande;
import tn.famytech.esprit.Entites.BonLivraison;
import tn.famytech.esprit.Entites.DemandeDepense;
import tn.famytech.esprit.Entites.DemandeStatus;
import tn.famytech.esprit.Entites.DepenseReelle;
import tn.famytech.esprit.Repositories.BLRepo;
import tn.famytech.esprit.Repositories.BonCommandeRepo;
import tn.famytech.esprit.Repositories.DepenseReelleRepo;


@Service
public class BonCommandeService {

	@Autowired
	private BonCommandeRepo commandeRepo;
	
	@Autowired
	private DemandeDepenseService depenseService;
	
	@Autowired
	private BLRepo blrepo;
	
	@Autowired
	private DepenseReelleRepo depenseReelleRepo;
	
	public BonCommande CreateBonCommande(BonCommande bonCommande,long id) {
		DemandeDepense depense=depenseService.getById(id);
		BonCommande commande=new BonCommande();
		commande.setDate(new Date());
		commande.setMontant(depense.getBudget());
		commande.setDescription(bonCommande.getDescription());
		
		commande.setStatus(BCStatus.En_Attente);
		depenseService.ChangeDemandeStatus(DemandeStatus.Acceptee, id);
		commande= commandeRepo.save(commande);
		depense.setBc(commande);
		return commande;
		}
	
	
	public List<BonCommande> getAll(){
		return (List<BonCommande>) commandeRepo.findAll();
	}
	
	public void ChangeStatus(long id,double montant) {
		BonCommande bonCommande=commandeRepo.findById(id).get();
		if(montant == bonCommande.getMontant()) {
			bonCommande.setStatus(BCStatus.Livre);
		}
		else {
			bonCommande.setStatus(BCStatus.Livre_Pertiel);
		}
		
		commandeRepo.save(bonCommande);
	}
	
	public BonCommande getById(long id) {
		return commandeRepo.findById(id).get();
	}
	
	public BonCommande saveBC(BonCommande bc) {
		return commandeRepo.save(bc);
	}
	
	public List<BonCommande> getNotelivredCommandes(){
		List<BonCommande> result=new ArrayList<BonCommande>();
		for (BonCommande bonCommande : commandeRepo.findAll()) {
			if(bonCommande.getStatus()==BCStatus.En_Attente) {
				result.add(bonCommande);
			}
			
		}
		return result;
	}
	
	public List<BonCommande> CancelBC(long id){
		BonCommande bc=commandeRepo.findById(id).get();
		DemandeDepense dem=depenseService.getById(bc.getDdepense().getIdDD());
		dem.setStatus(DemandeStatus.En_Attente);
		dem.setBc(null);
		depenseService.saveDem(dem);
		commandeRepo.deleteById(id);
		return getNotelivredCommandes();
	}
	
	@Transactional
	public List<BonCommande> DeliverBC(long id,DepRealWithAdresse depaddr){
		BonCommande bc=commandeRepo.findById(id).get();
		DepenseReelle dep=depaddr.getDep();
		BonLivraison bl=new BonLivraison();
		bl.setDate(dep.getDate());
		bl.setAddresse(depaddr.getAdresse());
		bl.setBldoc(depaddr.getBldoc());
		bl.setBcliv(bc);
		
		bl=blrepo.save(bl);
		dep.setBlss(bl);
		dep.setMontant(bc.getMontant());
		dep=depenseReelleRepo.save(dep);
		bl.setDepreal(dep);
		bl=blrepo.save(bl);
		bc.setStatus(BCStatus.Livre);
		bc.setBls(bl);
		commandeRepo.save(bc);
		return getNotelivredCommandes();
	}
	
	
	public BonCommande getBCById(long id) {
		return commandeRepo.findById(id).get();
	}
	
	
}
