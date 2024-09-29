package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tn.famytech.esprit.Entites.BCStatus;
import tn.famytech.esprit.Entites.BonCommande;
import tn.famytech.esprit.Entites.BonLivraison;
import tn.famytech.esprit.Entites.DepenseReelle;
import tn.famytech.esprit.Entites.Lignecommande;

import tn.famytech.esprit.Repositories.BLRepo;
import tn.famytech.esprit.Repositories.DepenseReelleRepo;

@Service
public class BLService {

	@Autowired
	private BLRepo blRepo;
	@Autowired
	private BonCommandeService bcservice;
	

	
	@Autowired
	private DepenseReelleRepo deprelrep;
	
	
	

	
	
	@Transactional
	public List<BonCommande> createBonLivraison(long id, BonLivraison bl,DepenseReelle dep) {
		BonCommande bc=bcservice.getById(id);
		bc.setStatus(BCStatus.Livre);
		bc.setBls(bl);
		bc=bcservice.saveBC(bc);
		bl.setBcliv(bc);
		dep.setMontant(bc.getMontant());
		dep.setBlss(bl);
		dep.setDate(bl.getDate());
		dep.setTypepay(bl.getTypepey());
		dep=deprelrep.save(dep);
		bl.setDepreal(dep);
		blRepo.save(bl);
		return bcservice.getNotelivredCommandes();
	
	}
	

	
	public List<BonLivraison> getAll(){
		return (List<BonLivraison>) blRepo.findAll();
	}

	public BonLivraison findbyid(long id) {
		return blRepo.findById(id).get();
	}
	
	
	
}
