package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.famytech.esprit.Entites.Fournisseur;
import tn.famytech.esprit.Repositories.FournisseurRepo;

@Service
public class FournisseurService {
	
	@Autowired
	private FournisseurRepo fournisseurRepo;
	
	public List<Fournisseur> getAllFournisseur(){
		return (List<Fournisseur>) fournisseurRepo.findAll();
	}
	
	public List<Fournisseur> getNotArchivedFournisseur(){
		
		return fournisseurRepo.getNotArchivedFournisseur();
	}
	
	public List<Fournisseur> addFournisseur(Fournisseur f) {
		System.out.println("hana houniiiii");
		f.setArchived(false);
		fournisseurRepo.save(f);
		return (List<Fournisseur>) fournisseurRepo.findAll();
	}
	
	public List<Fournisseur> archiveFournisseur(long id){
		Fournisseur f=fournisseurRepo.findById(id).get();
		f.setArchived(true);
		return (List<Fournisseur>) fournisseurRepo.findAll();
		
	}
	
	
	public List<Fournisseur> NotArchiveFournisseur(long id){
		Fournisseur f=fournisseurRepo.findById(id).get();
		f.setArchived(false);
		return (List<Fournisseur>) fournisseurRepo.findAll();
		
	}
	
	
	public List<Fournisseur> updateFournisseur(Fournisseur fournisseur){
		Fournisseur f=fournisseurRepo.findById(fournisseur.getIdF()).get();
		f.setAddresse(fournisseur.getAddresse());
		f.setRIB(fournisseur.getRIB());
		f.setTel(fournisseur.getTel());
		f.setLibelle(fournisseur.getLibelle());
		f.setArchived(fournisseur.isArchived());
		f.setImage(fournisseur.getImage());
		fournisseurRepo.save(f);
		return (List<Fournisseur>) fournisseurRepo.findAll();
		
	}
	
	public Fournisseur getFournisseurById(long id) {
		return fournisseurRepo.findById(id).get();
	}
	
	public List<Fournisseur> activerFour(long id){
		Fournisseur f= fournisseurRepo.findById(id).get();
		f.setArchived(false);
		f=fournisseurRepo.save(f);
		
		return getAllFournisseur();
	}
	
	public List<Fournisseur> archiverFour(long id){
		Fournisseur f= fournisseurRepo.findById(id).get();
		f.setArchived(true);
		f=fournisseurRepo.save(f);
		System.out.println(f.isArchived());
		return getAllFournisseur();
	}

	public List<Fournisseur> search(String search) {
		List<Fournisseur> result=new ArrayList<Fournisseur>();
		for (Fournisseur f : getAllFournisseur()) {
			if(f.getLibelle().contains(search) || f.getAddresse().contains(search) || f.getRIB().contains(search)
					|| f.getTel().contains(search)) {
				result.add(f);
			}
		}
		return result;
	}


}
