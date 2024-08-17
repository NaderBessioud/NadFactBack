package tn.famytech.esprit.Controllers;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hellosign.sdk.HelloSignException;
import lombok.experimental.PackagePrivate;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Services.ClientService;
import tn.famytech.esprit.Services.FactureService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/facturationsign")
public class FactureSignController {
	@Autowired
	private FactureService factureService;
	
	@Autowired
	private ClientService clientService;

	 
	 @GetMapping("/getFactureById/{id}")
	 public Facture getFactureById(@PathVariable("id") long id) {
		 return factureService.getFactureById(id);
	 }
	 
	/* @GetMapping("/getFactures")
	 public List<Facture> displayFacturesByDateAndClient(@RequestParam("start") String startdate,@RequestParam("end") String enddate,@RequestParam("id") long c){
		 return factureService.getFacturesByDateEmissionAndClient(startdate, enddate, c);
	 }
	 
	 @GetMapping("/getFacturesByClient")
	 public List<Facture> displayFacturesByDateAndClient(@RequestParam("id") long c){
		 return factureService.getFacturesByClient(c);
	 }*/
	 
	    @GetMapping("/facturesfilter")
	     public List<Facture> DisplayFactureFiltred(@RequestParam(name = "startDate") String startDate,
	             @RequestParam(name = "endDate") String endDate,
	             @RequestParam(name = "factureType") String factureType,
	             @RequestParam(name = "clientId") long clientId,
	             @RequestParam(name = "archived") boolean archived) throws ParseException {
	    	 
	    	 return factureService.filterFactuer(startDate, endDate, factureType, clientId,archived);
	    	 
	     }
	    
	    
	 @PostMapping("/facturesFilterSigned")
	 public List<Facture> DisplaySignedFacture(@RequestBody List<Facture> factures){
		 return factureService.filterSignedFactuers(factures);
	 }
	 
	 @PostMapping("/facturesFilterNotSigned")
	 public List<Facture> DisplayNotSignedFacture(@RequestBody List<Facture> factures){
		 return factureService.filterNotSignedFactuers(factures);
	 }
	 
	 @PostMapping("/facturesFilterArchived")
	 public List<Facture> filterArchivedFactuers(@RequestBody List<Facture> factures){
		 return factureService.filterArchivedFactuers(factures);
	 }
	 
	/* @PutMapping("/updateSignFacture/{id}")
	 public List<Facture> updateSignFacture(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		return factureService.SignFactureupdate(id,factures);
	 }*/
	 
	 @PutMapping("/updateArchivedFacture/{id}")
	 public  List<Facture> updateArchivedFacture (@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		 return factureService.ArchiveFactureupdate(id,factures);
	 }
	 
	 @PostMapping("/SearchFacture")
	 public List<Facture> searchFacture(@RequestParam("search") String search,@RequestBody List<Facture> factures){
		 return factureService.SearchFactures(search,factures);
	 }
	 
	 @PostMapping("/pageList/{pageNo}")
     public List<Facture> findPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<Facture> facutres) {
    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(pageNo, pageSize,facutres);
    	 
    	 return page.getContent(); 
     }
	 
	 @PostMapping("/pagetotal/{pageNo}")
     public int findPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<Facture> facutres) {
    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(pageNo, pageSize,facutres);
    	 return page.getTotalPages(); 
     }
	 
	 /*@GetMapping("/numberToWords")
	 public String NumberToWords(@RequestParam("n") long id) {
		 return factureService.TotalEnLettre(id);
	 }*/
	 
	 
     
	     
	 


}
