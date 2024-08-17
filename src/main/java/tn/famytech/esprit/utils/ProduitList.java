package tn.famytech.esprit.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import tn.famytech.esprit.DTO.ProduitLabelAndPu;

@Component
public class ProduitList {
	private   List<ProduitLabelAndPu> produits=new ArrayList<ProduitLabelAndPu>();
	
	public void SetList() {
		produits.add(new ProduitLabelAndPu("Audit", 1000, 250));
		produits.add(new ProduitLabelAndPu("Finance", 3000, 1000));
		
	}
	public List<ProduitLabelAndPu> getList(){
		SetList() ;
		return produits;
	}

}
