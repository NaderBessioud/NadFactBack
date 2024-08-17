package tn.famytech.esprit.DTO;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Facture;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FacturesStat {
	private double totalfacturefacturetnd;
	private double totaltvafacturetnd;
	private double totaltimbrefacturetnd;
	private double totalfactureavoirtnd;
	private double totaltvaavoirtnd;
	private double totaltimbreavoirtnd;
	private double totaltnd;
	private double totaltvatnd;
	private double totaltimbretnd;
	
	private double totalfacturefactureeuro;
	private double totaltvafactureeuro;
	private double totaltimbrefactureeuro;
	
	private double totalfactureavoireuro;
	private double totaltvaavoireuro;
	private double totaltimbreavoireuro;
	private double totaleuro;
	private double totaltvaeuro;
	private double totaltimbreeuro;

	private double totalfacturefactureeuroctv;
	private double totaltvafactureeuroctv;
	private double totaltimbrefactureeuroctv;
	private double totalfactureavoireuroctv;
	private double totaltvaavoireuroctv;
	private double totaltimbreavoireuroctv;
	private double totaleuroctv;
	private double totaltvaeuroctv;
	private double totaltimbreeuroctv;
	
	private double totalfb;
	private double totalfbeuro;
	private double totalfbeuroctv;
	private double totalfball;
	
	private double regmontant;

	private List<FactureDateAndNumber> factures;
	private long nombrefacturestotal;
	private long nombreavoirs;
	private long nombrefactures;
	
	private double totalfact;
	private double totaltva;
	private double totaltim;
}
