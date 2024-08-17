package tn.famytech.esprit.Services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tn.famytech.esprit.Entites.DemandeDepense;
import tn.famytech.esprit.Entites.DepenseReelle;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Repositories.DepenseReelleRepo;

@Service
public class DepenseReelleService {
	
	@Autowired
	private DepenseReelleRepo deprrep;
	
	public DepenseReelle CreateDepenseReal(DepenseReelle dep) {
		return deprrep.save(dep);
	}
	
	public DepenseReelle getDepenseById(long id) {
		return deprrep.findById(id).get();
	}
	
	   
    public Page<DepenseReelle> findPaginated(int pageNum, int pageSize,List<DepenseReelle> depenses){
  	  
    	Pageable pageable = PageRequest.of(pageNum-1, pageSize);
    	int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), depenses.size());

        List<DepenseReelle> sublist = depenses.subList(start, end);

    	
    	
    	return new PageImpl<DepenseReelle>(sublist, pageable, depenses.size());
    }
    
    public List<DepenseReelle> getDepenseOrderByDate(){
    	 List<DepenseReelle> result= (List<DepenseReelle>) deprrep.findAll();
    	 Collections.sort(result, Comparator.comparing(DepenseReelle::getDate).reversed());
    	  return result;
    }
    
    public List<DepenseReelle> Search(String search){
    	List<DepenseReelle> result=new ArrayList<DepenseReelle>();
    	for (DepenseReelle dep  : getDepenseOrderByDate()) {
			if(String.valueOf(dep.getMontant()).contains(search) || dep.getBlss().getBcliv().getDdepense().getMotif().contains(search)
					|| String.valueOf(dep.getDate()).contains(search) || dep.getTypepay().toString().contains(search)) {
				result.add(dep);
			}
		}
    	Collections.sort(result, Comparator.comparing(DepenseReelle::getDate).reversed());
    	return result;
    }

}
