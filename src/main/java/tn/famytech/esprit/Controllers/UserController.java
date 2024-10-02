package tn.famytech.esprit.Controllers;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;


import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tn.famytech.esprit.Entites.BonLivraison;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.zxing.WriterException;
import com.hellosign.sdk.HelloSignException;
import com.itextpdf.text.DocumentException;

import net.sourceforge.tess4j.TesseractException;
import tn.famytech.esprit.DTO.AvoirAndFacturesList;
import tn.famytech.esprit.DTO.ClientAndReglement;
import tn.famytech.esprit.DTO.ClientWithImage;
import tn.famytech.esprit.DTO.DepRealWithAdresse;
import tn.famytech.esprit.DTO.FactureDateAndList;
import tn.famytech.esprit.DTO.FactureListAndPDF;
import tn.famytech.esprit.DTO.FactureMobile;
import tn.famytech.esprit.DTO.FacturesReglements;
import tn.famytech.esprit.DTO.FacturesReglementsRetenue;
import tn.famytech.esprit.DTO.FacturesStat;
import tn.famytech.esprit.DTO.PersonalDemandeDep;
import tn.famytech.esprit.DTO.ReglementPoint;
import tn.famytech.esprit.DTO.UserMobile;
import tn.famytech.esprit.DTO.UserOnLine;
import tn.famytech.esprit.DTO.UserOnLineWhitMSG;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.BonCommande;
import tn.famytech.esprit.Entites.BonLivraison;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.DemandeDepense;
import tn.famytech.esprit.Entites.DepenseReelle;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Fournisseur;
import tn.famytech.esprit.Entites.Message;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.Servicefact;
import tn.famytech.esprit.Entites.Reglement;
import tn.famytech.esprit.Entites.ReglementType;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Services.AuthenticationService;
import tn.famytech.esprit.Services.AvoirService;
import tn.famytech.esprit.Services.BLService;
import tn.famytech.esprit.Services.BonCommandeService;
import tn.famytech.esprit.Services.ClientService;
import tn.famytech.esprit.Services.DemandeDepenseService;
import tn.famytech.esprit.Services.DepenseReelleService;
import tn.famytech.esprit.Services.FactureService;
import tn.famytech.esprit.Services.FournisseurService;
import tn.famytech.esprit.Services.LoggedInUserService;
import tn.famytech.esprit.Services.MessageService;
import tn.famytech.esprit.Services.PersonelService;
import tn.famytech.esprit.Services.ServicefactService;
import tn.famytech.esprit.Services.ReglementService;
import tn.famytech.esprit.Services.UserService;
import tn.famytech.esprit.utils.ProduitList;

@Controller

@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private FactureService factureService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ReglementService reglementService;
	
	@Autowired
	private AvoirService avoirService;
	
	@Autowired
	private ServicefactService produitService;
	
	@Autowired
	private PersonelService personelService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private FournisseurService fournisseurService;
	
	@Autowired
	private BonCommandeService bcservice;
	
	@Autowired
	private BLService blService;
	
	@Autowired
	private DemandeDepenseService demandeDepenseService;
	
	@Autowired
	private DepenseReelleService depenseReelleService;
	
	
	
	
	
	@PostMapping("/downloadfacture")
	public ResponseEntity<ByteArrayResource> downloadFacture( Facture facture) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException{
			
	        
	        try {
	            // Generate the facture PDF as a byte array
	            byte[] facturePdfBytes = factureService.generateFacture(facture).toByteArray();

	            // Set headers
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_PDF);
	            headers.setContentDispositionFormData("factureInternationale.pdf", "factureInternationale.pdf");

	            // Return ResponseEntity with the PDF byte array
	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentLength(facturePdfBytes.length)
	                    .body(new ByteArrayResource(facturePdfBytes));
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	}
	
	
	@PostMapping("/downloadfactureEuro")
	public ResponseEntity<ByteArrayResource> downloadFactureEuro(@ModelAttribute Facture facture) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException{
		try {
            // Generate the facture Euro PDF as a byte array
            byte[] facturePdfBytes = factureService.generateFactureEuro(facture).toByteArray();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("factureExport.pdf", "factureExport.pdf");

            // Return ResponseEntity with the PDF byte array
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(facturePdfBytes.length)
                    .body(new ByteArrayResource(facturePdfBytes));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
	}
	
	@PostMapping("/downloadFacturepre")
	public ResponseEntity<ByteArrayResource> SignFacture(@RequestBody FactureListAndPDF flp){
		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        
			return ResponseEntity.ok()
			        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename='facture.pdf'")
			        .headers(headers)
			        .body(new ByteArrayResource(flp.getOutput()));
		
		
       
		
	}
	/*@PostMapping("/SignFacture/{id}")
	@ResponseBody
	public List<Facture> SignFacture(@PathVariable("id") long id,@RequestBody FactureDateAndList fdl){
		return factureService.SignFacture(id,fdl);
	}*/
	
	/*@DeleteMapping("/DeleteFacture/{id}")
	@ResponseBody
	public List<Facture> DeleteFacture(@PathVariable("id") long id,@RequestBody List<Facture> factures){
		return factureService.DeleteFacture(id, factures);
	}*/
	
	@PostMapping("/SignNewFacture")
	public ResponseEntity<ByteArrayResource> SignNewFacture(@ModelAttribute Facture facture){
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        
			return ResponseEntity.ok()
			        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename='facture.pdf'")
			        .headers(headers)
			        .body(new ByteArrayResource(factureService.SignNewFacture(facture).toByteArray()));
		
		
       
		
	}
	
	@GetMapping("previewFacture/{id}")
	public ResponseEntity<ByteArrayResource> previewFacture(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, SerialException, SQLException{
		
		Facture f=factureService.getFactureById(id);
		if(f.getStatus()==FactureStatus.Facture_valide || f.getStatus()==FactureStatus.Facture_envoye) {
			byte[] facturePdfBytes = factureService.downloadFile(f.getPdfname());
			 HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_PDF);
	            headers.setContentDispositionFormData("facture"+f.getType()+".pdf", "facture"+f.getType()+".pdf");

	            // Return ResponseEntity with the PDF byte array
	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentLength(facturePdfBytes.length)
	                    .body(new ByteArrayResource(facturePdfBytes));
		}
		else {
			
		
		if(f.getType()==TypeFacture.National) {
			return downloadFacture(f);
		}
		else {
			return downloadFactureEuro(f);
		}
		}
	}
	

	 
	 
     @GetMapping("/generateQR")
     public String getQRCode(Model model) throws com.google.zxing.WriterException{
         return factureService.getQRCode(model);
     }
     
     @GetMapping("/factures")
     public String DisplayFacture(Model model,@RequestParam("email") String email) {
    	/*model.addAttribute("factures", factureService.DisplayFactures());
    	 model.addAttribute("clients", clientService.DisplayClients());
    	 return "factures";*/
    	 
         

    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(1, pageSize,factureService.DisplayFacturesNotArchived(email));
    	 
    	
    	 List<Facture> factures = page.getContent();
    	 model.addAttribute("currentPage", 1);
    	
    	 model.addAttribute("totalPages", page.getTotalPages());
    	 model.addAttribute("totalItems", page.getTotalElements());
    	 model.addAttribute("factures", factures);
    	 model.addAttribute("Allfactures", factureService.DisplayFacturesNotArchived(email));
    	 model.addAttribute("clients", clientService.DisplayClients());
    	 model.addAttribute("activelink", "factures");
    	
    	 return "factures"; 
    	 
     }
     
 
     
 
     
     @GetMapping("/addfacture")
     public String AddFacture(Model model) {
    	 double tva=0.2;
    	 double timbre = 0.5;
    	 
    
    	 
    	 model.addAttribute("Clients",clientService.DisplayNotArchivedCLient());
    	 model.addAttribute("facture", new Facture());
    	 model.addAttribute("tva", tva);
    	 model.addAttribute("ctvtimbre", timbre);
    	 model.addAttribute("lcours", factureService.getLastCours());
    	 model.addAttribute("produits", produitService.DisplayNotArchiverService());
    	 model.addAttribute("type", "facture");
    	 model.addAttribute("activelink", "factures");
    	
    	
    	 return "create_facture";
     }
     
    
     
     @GetMapping("/updatefacture/{id}")
     public String updateFacture(Model model,@PathVariable("id") long id) {
    	 Facture f=factureService.getFactureById(id);
    	 ProduitList listproduit=new ProduitList();
    
    	 
    	 model.addAttribute("Clients",clientService.DisplayNotArchivedCLient());
    	 model.addAttribute("facture", f);
    	 model.addAttribute("lcours", factureService.getLastCours());
    	 model.addAttribute("produits", produitService.DisplayAll());
    	 
    	 model.addAttribute("activelink", "factures");
    	
    	
    	 return "update_facture";
     }
     
    /* @PostMapping("addAvoir")
     public ResponseEntity<ByteArrayResource> saveAvoir(@ModelAttribute("facture") Facture facture) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
    	byte[] facturePdfBytes= factureService.CreateAvoir(facture).toByteArray();
    		
    	 HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.APPLICATION_PDF);
         headers.setContentDispositionFormData("avoir.pdf", "avoir.pdf");

         // Return ResponseEntity with the PDF byte array
         return ResponseEntity.ok()
                 .headers(headers)
                 .contentLength(facturePdfBytes.length)
                 .body(new ByteArrayResource(facturePdfBytes));
    	 
    	
     }*/
     
     @PutMapping("CancelInvoice/{id}")
     @ResponseBody
     public List<Facture> CancelInvoice(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
    	return factureService.CancelInvoice(id,factures);
     }
     
     @PostMapping("factures")
     public String saveFacture(@ModelAttribute("facture") Facture facture) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
    	factureService.AddFacture(facture);
    	 
    	 return "redirect:/user/factures?email="+facture.getUser().getEmail();
     }
     
   
     
     @PostMapping("facturesupdate")
     public String updateFacture(@ModelAttribute("facture") Facture facture) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
    	
    	 Facture f= factureService.UpdateFacture(facture);
    	 
    	return "redirect:/user/factures?email="+facture.getUser().getEmail();
     }
     
   
     
     @GetMapping("/FactureQRCode/{id}")
     @ResponseBody
     public byte[] FactureQRCode(@PathVariable("id") long id) throws WriterException, IOException, DocumentException {
    	return factureService.getQRCodeImage(id,250,250);
     }
     
     @GetMapping("/getFactureById/{id}")
     @ResponseBody
	 public Facture getFactureById(@PathVariable("id") long id) {
		 return factureService.getFactureById(id);
	 }
	 

	  @GetMapping("/facturesfilter")
	    @ResponseBody
	     public List<Facture> DisplayFactureFiltred(@RequestParam(name = "startDate") String startDate,
	             @RequestParam(name = "endDate") String endDate,
	             @RequestParam(name = "factureType") String factureType,
	             @RequestParam(name = "clientId") long clientId,
	             @RequestParam(name = "archived") boolean archived,
	             @RequestParam("email") String email) throws ParseException {
	    	 
	    	 return factureService.filterFactuer(startDate, endDate, factureType, clientId,archived,email);
	    	 
	     }
	    
	    
	 @PostMapping("/facturesFilterSigned")
	 @ResponseBody
	 public List<Facture> DisplaySignedFacture(@RequestBody List<Facture> factures){
		 return factureService.filterSignedFactuers(factures);
	 }
	 
	 @PostMapping("/facturesFilterNotSigned")
	 @ResponseBody
	 public List<Facture> DisplayNotSignedFacture(@RequestBody List<Facture> factures){
		 return factureService.filterNotSignedFactuers(factures);
	 }
	 
	 @PostMapping("/facturesFilterArchived")
	 @ResponseBody
	 public List<Facture> filterArchivedFactuers(@RequestBody List<Facture> factures){
		 return factureService.filterArchivedFactuers(factures);
	 }
	 
	/* @PutMapping("/updateSignFacture/{id}")
	 @ResponseBody
	 public List<Facture> updateSignFacture(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		return factureService.SignFactureupdate(id,factures);
	 }*/
	 
	 @PutMapping("/updateArchivedFacture/{id}")
	 @ResponseBody
	 public  List<Facture> updateArchivedFacture (@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		 return factureService.ArchiveFactureupdate(id,factures);
	 }
	 
	 @PostMapping("/SearchFacture")
	 @ResponseBody
	 public List<Facture> searchFacture(@RequestParam("search") String search,@RequestBody List<Facture> factures){
		 return factureService.SearchFactures(search,factures);
	 }
	 
	 @PostMapping("/pageList/{pageNo}")
	 @ResponseBody
     public List<Facture> findPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<Facture> facutres) {
    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(pageNo, pageSize,facutres);
    	 
    	 return page.getContent(); 
     }
	 
	 @PostMapping("/serpagetotal/{pageNo}")
	 @ResponseBody
     public int findPaginatedTotalService(@PathVariable(value="pageNo") int pageNo) {
    	 int pageSize = 5;
    	 Page<Servicefact> page = produitService.findPaginated(pageNo, pageSize);
    	 return page.getTotalPages(); 
     }
	 
	 @PostMapping("/ServpageList/{pageNo}")
	 @ResponseBody
     public List<Servicefact> findPaginatedListServ(@PathVariable(value="pageNo") int pageNo) {
    	 int pageSize = 5;
    	 Page<Servicefact> page = produitService.findPaginated(pageNo, pageSize);
    	 
    	 return page.getContent(); 
     }
	 
	 @PostMapping("/pagetotal/{pageNo}")
	 @ResponseBody
     public int findPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<Facture> facutres) {
    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(pageNo, pageSize,facutres);
    	 return page.getTotalPages(); 
     }
	 
	 
	 @PostMapping("/DempageList/{pageNo}")
	 @ResponseBody
     public List<DemandeDepense> findDemPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<DemandeDepense> demandes) {
    	 int pageSize = 5;
    	 Page<DemandeDepense> page = demandeDepenseService.findPaginated(pageNo, pageSize,demandes);
    	 
    	 return page.getContent(); 
     }
	 
	 @PostMapping("/Dempagetotal/{pageNo}")
	 @ResponseBody
     public int findDemPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<DemandeDepense> demandes) {
    	 int pageSize = 5;
    	 Page<DemandeDepense> page = demandeDepenseService.findPaginated(pageNo, pageSize,demandes);
    	 return page.getTotalPages(); 
     }
	 
	 
	 @PostMapping("/SendInvoiceMail/{id}")
	 @ResponseBody
	 public List<Facture> SendInvoiceMail(@PathVariable("id") long id,@RequestBody List<Facture> facutres) {
		return factureService.SendInvoiceMail(id,facutres);
	 }
	 
	 @PostMapping("/SendProformaMail/{id}")
	 @ResponseBody
	 public List<Facture> SendProformaMail(@PathVariable("id") long id,@RequestBody List<Facture> facutres) {
		return factureService.SendProformaMail(id,facutres);
	 }
	 
	 @GetMapping("/LastInvoiceDate/{id}")
	 @ResponseBody
	 public Date getLastInvoiceDate(@PathVariable("id") long id) {
		 return factureService.getLastInvoiceDate(id);
	 }
	 
	 
	 @GetMapping("/getMaxInvoiceDate")
	 @ResponseBody
	 public ResponseEntity<Map<String, Object>>  getMaxInvoiceDate() {
		 Map<String, Object> response = new HashMap();
		 response.put("data", factureService.getMaxInvoiceDate());
		 return ResponseEntity.ok(response);
	 }
	 
	 @PostMapping("/FactureByClient/{idr}")
	 @ResponseBody
	 public List<Facture> getFactureByClient(@RequestParam("lib") String lib,@RequestParam("type") String type,@PathVariable("idr") long idr){
		 return factureService.DisplayFactureByClient(lib,type,idr);
	 }
	 
	 
	 @GetMapping("/FactureByClient/{lib}")
	 @ResponseBody
	 public List<Facture> getFactureByClient(@PathVariable("lib") String lib,@RequestParam("email") String email){
		 return factureService.DisplayInvoiceNotArchivedAndNotPayedByClient(lib,email);
	 }

	 @GetMapping("/FactureByReglement/{idr}")
	 @ResponseBody
	 public List<Facture> getFactureByReglement(@PathVariable("idr") long idr){
		 return factureService.DisplayFactureByReglement(idr);
	 }
	 
	 @GetMapping("/Allfactures")
	 @ResponseBody
	 public List<Facture> getFactureByClient(@RequestParam("email") String email){
		 return factureService.DisplayInvoiceNotArchivedAndNotPayed(email);
	 }
	 
	 @GetMapping("/clienttype/{id}")
	 @ResponseBody
	 public String getFactureTypeByClient(@PathVariable("id") long id) {
		 return factureService.getFactureTypeByClient(id);
	 }
	 
	 @PutMapping("/validateProforma/{id}")
	 @ResponseBody
	 public List<Facture> validateProforma(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		return factureService.ValidateProformaByManager(id, factures);
	 }
	 
	
	 
	 @PostMapping("/validateFacture/{id}/{idp}")
	 @ResponseBody
	 public List<Facture> validateFacture(@PathVariable("id") long id,@PathVariable("idp") long idp,@RequestBody FactureDateAndList flp) {
		return factureService.SignFacture(id,idp, flp);
	 }
	 
	 @PutMapping("/Facturer/{id}")
	 @ResponseBody
		public List<Facture> CreateFactureFromProformat(@PathVariable("id") long id,@RequestBody List<Facture> factures){
			
		
			return factureService.createFactureFromProforma(id, factures);
			
	       
			
		}
	 
	 @PostMapping("CheckIfExist")
	 @ResponseBody
	 public boolean checkifExist(@ModelAttribute("facture") Facture facture) {
		 return factureService.checkIfExist(facture);
	 }
	 
	 
	 
	
	 
	 //-----------------------Client Part-------------------------------------------------------------
	 
	 @GetMapping("/clients")
     public String DisplayClient(Model model) throws IOException {

    	 int pageSize = 6;
    	 Page<ClientWithImage> page = clientService.findPaginated(1, pageSize,clientService.DisplayClientWithImage());
    	 
    	
    	 List<ClientWithImage> clients = page.getContent();
    	 model.addAttribute("currentPage", 1);
    	 model.addAttribute("totalPages", page.getTotalPages());
    	 model.addAttribute("totalItems", page.getTotalElements());
    	 model.addAttribute("clients", clients);
    	 model.addAttribute("AllClient",clientService.DisplayClientWithImage() );
    	model.addAttribute("client",new Client());
    	 model.addAttribute("activelink", "clients");
    	
    	 return "client/clients"; 
    	 
     }
	 
	 
	  @PostMapping("/addClient")
	  @ResponseBody
	     public List<ClientWithImage> AddClient(@ModelAttribute Client client) throws IOException {
	    	
	    	 return clientService.addClientAndUpdateList(client);
	     }
	     
	  @PutMapping("/updateClient/{id}")
	  @ResponseBody
	     public List<ClientWithImage> UpdateClient(@PathVariable("id") long id,@ModelAttribute Client client) throws IOException {
	    	
	    	 return clientService.UpdateClientAndUpdateList(client,id);
	     }
	     @PostMapping("clients")
	     public String saveClient(@ModelAttribute("client") Client client) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
	    	Client C= clientService.AddClient(client);
	    	 
	    	 return "redirect:/home/clients";
	     }
	     
	     
	     @PostMapping("/ClientpageList/{pageNo}")
		 @ResponseBody
	     public List<ClientWithImage> findClientPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<ClientWithImage> clients) {
	    	 int pageSize = 6;
	    	 Page<ClientWithImage> page = clientService.findPaginated(pageNo, pageSize,clients);
	    	 
	    	 return page.getContent(); 
	     }
		 
		 @PostMapping("/Clientpagetotal/{pageNo}")
		 @ResponseBody
	     public int findClientPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<ClientWithImage> clients) {
			 int pageSize = 6;
			 Page<ClientWithImage> page = clientService.findPaginated(pageNo, pageSize,clients);
	    	 return page.getTotalPages(); 
	     }
		 
		 @GetMapping("/searchClient")
		 @ResponseBody
		 public List<ClientWithImage> SearchClient(@RequestParam("search") String search) throws IOException{
			 return clientService.SearchClient(search);
		 }
		 
		 @GetMapping("/ClientById/{id}")
		 @ResponseBody
		 public Client getClientById(@PathVariable("id") long id) {
			 return clientService.getClientById(id);
		 }
		 
		 
//-------------------------------------------User Profile---------------------------
		 @GetMapping("/Profile/{email}")
		 public String UserProfile(@PathVariable("email") String email, Model model) {
			 model.addAttribute("user",personelService.getPersonelByEmail(email));
			 model.addAttribute("cl",new Client());
			 model.addAttribute("activelink","profile");
			 return "user/user_profile.html";
		 }
		 
		 @PutMapping("/updateProfile")
		 @ResponseBody
		 public User updateProfile(@ModelAttribute Personel user) {
			 return userService.updateProfile(user);
		 }
		 
		
		 
		 @GetMapping("/userById/{id}")
		 @ResponseBody
		 public User getUserById(@PathVariable("id") long id) {
			 return userService.getUserById(id);
		 }
		 
		 @GetMapping("/usernameById/{id}")
		 @ResponseBody
		 public String getUsernameById(@PathVariable("id") long id) {
			 return userService.getUsernameById(id);
		 }
		 
		 @PutMapping("/updatePassword")
		 @ResponseBody
		 public boolean updatePass(@ModelAttribute User user,@RequestParam("oldpass") String oldpass) {
			 return userService.updatePassword(oldpass, user);
		 }
		 
		/* @GetMapping("/CreateCertif")
		 @ResponseBody
		 public void createCertif(@RequestParam("name") String name,@RequestParam("lastname") String lastname) {
			 factureService.createUserCertif(name, lastname);
		 }*/
		 
		 @GetMapping("/loggedin")
		 @ResponseBody
		 public String showuser() {
			 return userService.getCurrentUserFirstName();
		 }
		 
		/* @GetMapping("/getvalideDate")
		 @ResponseBody
		 public String hashtext() throws IOException, ParseException  {
			 return factureService.getValidDate();
		 }*/
		 
		 
/*---------------------------Reglement part------------------------------------*/
		 @GetMapping("/reglements")
	     public String DisplayReglements(Model model,@RequestParam("email") String email) throws IOException {

	    
	    	 model.addAttribute("Clients", clientService.DisplayClients());
	    	 model.addAttribute("reglements", reglementService.DisplayReglemensNotAffected());
	    	 model.addAttribute("factures", factureService.DisplayInvoiceNotArchivedAndNotPayed(email));
	    	 model.addAttribute("Reglement", new Reglement());
	    	 model.addAttribute("activelink", "reglement");
	    	
	    	 return "Reglement/reglement"; 
	    	 
	     }
		 
		 @PostMapping("/addReglement")
		 @ResponseBody
		 public List<ClientAndReglement> addReglement(@RequestBody ClientAndReglement reglement) {
			 return reglementService.addReglement(reglement);
		 }
		 
		 @GetMapping("/GetReglementByClient/{lib}")
		 @ResponseBody
		 public List<ClientAndReglement> getReglementByClient(@PathVariable("lib") String lib){
			 return reglementService.DisplayClientReglement(lib);
		 }
	
 		@GetMapping("/ReglementByFacture/{idf}")
		 @ResponseBody
		 public List<ClientAndReglement> getReglementByFacture(@PathVariable("idf") long idf){
			 return reglementService.DisplayReglementByFacture(idf);
		 }
		 	
		 @GetMapping("/DisplayReglement")
		 @ResponseBody
		 public List<ClientAndReglement> getReglements(){
			 return reglementService.DisplayReglemensNotAffected();
		 }
		 
		 @PostMapping("/AffectReglementFacture/{idR}/{idF}")
		 @ResponseBody
		 public FacturesReglements affectReglementFacture(@PathVariable("idR") long idR,@PathVariable("idF") long idF,@RequestBody FacturesReglements facturesReglements) {
			return reglementService.AffectRegelementToFacture(idR, idF,facturesReglements);
		 }
		 
		 @PostMapping("/AffectReglementFactureWithRetenue/{idR}/{idF}")
		 @ResponseBody
		 public FacturesReglements affectReglementFactureWithRetenue(@PathVariable("idR") long idR,@PathVariable("idF") long idF,@RequestBody FacturesReglementsRetenue retenuepath) {
			 return reglementService.AffectRegelementToFactureWithRetenue(idR, idF,retenuepath);
		 }
		 
		 @GetMapping("RegById/{id}")
		 @ResponseBody
		 public Reglement getRegById(@PathVariable("id") long id) {
			 return reglementService.getReglementById(id);
		 }
		 
		 @PostMapping("/updateReglement")
		 @ResponseBody
		 public List<ClientAndReglement> updateReglement(@RequestBody ClientAndReglement reglement) {
			 return reglementService.updateReglement(reglement);
		 }
		 
		 @PostMapping("/deleteRegl/{id}")
		 @ResponseBody
		 public List<ClientAndReglement> DeleteReglement(@RequestParam("lib") String lib,@PathVariable("id") long id) {
			 return reglementService.DeleteReglement(lib,id);
		 }
		 
		 
		 
/*------------------------------Dashbord---------*/
		 @GetMapping("/dashbord")
		 public String DisplayDashbord(Model model) throws ParseException {
			 Date currentdate=new Date();
			 Calendar calendar = Calendar.getInstance();
		     calendar.setTime(currentdate);
		     int currentmonth = calendar.get(Calendar.MONTH) + 1;
		    
		     int year = calendar.get(Calendar.YEAR);
		    
		     //String formattedTva = String.format("%.3f", factureService.getTVASomme(year, currentmonth));
		    // String formattedTimbre = String.format("%.3f", factureService.getTimbreSomme(year, currentmonth));
		     
		     
		     model.addAttribute("Clients",clientService.DisplayNotArchivedCLient());
		    
		    model.addAttribute("stat",factureService.calculFacturesStatMonth(year, currentmonth));
		    model.addAttribute("factures",factureService.calculFacturesStatMonth(year, currentmonth).getFactures());
		    
		  
		    
		    
		     model.addAttribute("nbf",factureService.FactureCurrentYear());
			 return "Dashbord/dashbord";
		 }
		 
		 @PostMapping("/FactureTvaSumTND")
		 @ResponseBody
		 public double FactureTvaSumTND(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASomme(y, m,TypeFacture.National,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformTvaSumTND")
		 @ResponseBody
		 public double ProformTvaSumTND(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASomme(y, m,TypeFacture.National,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/FactureTvaSumEuro")
		 @ResponseBody
		 public double FactureTvaSumEuro(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASomme(y, m,TypeFacture.Export,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformTvaSumEuro")
		 @ResponseBody
		 public double ProformTvaSumEuro(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASomme(y, m,TypeFacture.Export,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/FactureTimbreSumTnd")
		 @ResponseBody
		 public double FactureTimbreSumTnd(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSomme(y, m,TypeFacture.National,FactureStatus.Facture_envoye);
		 }
		 
		 
		 @PostMapping("/ProformaTimbreSumTnd")
		 @ResponseBody
		 public double ProformaTimbreSumTnd(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSomme(y, m,TypeFacture.National,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/FactureTimbreSumEuro")
		 @ResponseBody
		 public double FactureTimbreSumEuro(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSomme(y, m,TypeFacture.Export,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformaTimbreSumEuro")
		 @ResponseBody
		 public double ProformaTimbreSumEuro(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSomme(y, m,TypeFacture.Export,FactureStatus.Proforma_envoyee);
		 }
		 
		 
		 @PostMapping("/FactureTotalSumTND")
		 @ResponseBody
		 public double FactureTotalSumTND(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSomme(y, m,TypeFacture.National,FactureStatus.Facture_envoye);
		 }
		 
		 
		 @PostMapping("/FactureTotalSumEuro")
		 @ResponseBody
		 public double FactureTotalSumEuro(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSomme(y, m,TypeFacture.Export,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformaTotalSumEuro")
		 @ResponseBody
		 public double ProformaTotalSumEuro(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSomme(y, m,TypeFacture.Export,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/ProformaTotalSumTND")
		 @ResponseBody
		 public double ProformaTotalSumTND(@RequestParam("month") int m,@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSomme(y, m,TypeFacture.National,FactureStatus.Proforma_envoyee);
		 }
		
		 
		 
		 
		 
		 
		 
		 @PostMapping("/FactureTvaSumTNDYear")
		 @ResponseBody
		 public double FactureTvaSumTNDYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASommeYear(y,TypeFacture.National,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformTvaSumTNDYear")
		 @ResponseBody
		 public double ProformTvaSumTNDYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASommeYear(y,TypeFacture.National,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/FactureTvaSumEuroYear")
		 @ResponseBody
		 public double FactureTvaSumEuroYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASommeYear(y,TypeFacture.Export,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformTvaSumEuroYear")
		 @ResponseBody
		 public double ProformTvaSumEuroYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTVASommeYear(y,TypeFacture.Export,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/FactureTimbreSumTndYear")
		 @ResponseBody
		 public double FactureTimbreSumTndYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSommeYear(y,TypeFacture.National,FactureStatus.Facture_envoye);
		 }
		 
		 
		 @PostMapping("/ProformaTimbreSumTndYear")
		 @ResponseBody
		 public double ProformaTimbreSumTndYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSommeYear(y,TypeFacture.National,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/FactureTimbreSumEuroYear")
		 @ResponseBody
		 public double FactureTimbreSumEuroYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSommeYear(y,TypeFacture.Export,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformaTimbreSumEuroYear")
		 @ResponseBody
		 public double ProformaTimbreSumEuroYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTimbreSommeYear(y,TypeFacture.Export,FactureStatus.Proforma_envoyee);
		 }
		 
		 
		 @PostMapping("/FactureTotalSumTNDYear")
		 @ResponseBody
		 public double FactureTotalSumTNDYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSommeYear(y,TypeFacture.National,FactureStatus.Facture_envoye);
		 }
		 
		 
		 @PostMapping("/FactureTotalSumEuroYear")
		 @ResponseBody
		 public double FactureTotalSumEuroYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSommeYear(y,TypeFacture.Export,FactureStatus.Facture_envoye);
		 }
		 
		 @PostMapping("/ProformaTotalSumEuroYear")
		 @ResponseBody
		 public double ProformaTotalSumEuroYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSommeYear(y,TypeFacture.Export,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/ProformaTotalSumTNDYear")
		 @ResponseBody
		 public double ProformaTotalSumTNDYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.getTotalSommeYear(y,TypeFacture.National,FactureStatus.Proforma_envoyee);
		 }
		 
		 @PostMapping("/MensuelRealsortie")
		 @ResponseBody
		 public FacturesStat MensuelReal(@RequestParam("year") int y,@RequestParam("month") int m) throws ParseException {
			 return factureService.calculFacturesStatMonth(y, m);
		 }
		 
		 @PostMapping("/MensuelPotentielsortie")
		 @ResponseBody
		 public FacturesStat PotentielMonth(@RequestParam("year") int y,@RequestParam("month") int m) throws ParseException {
			 return factureService.calculProformasStatMonth(y, m);
		 }
		 
		 @PostMapping("/YearPotentielsortie")
		 @ResponseBody
		 public FacturesStat PotentielYear(@RequestParam("year") int y) throws ParseException {
			 return factureService.calculProformasStatYear(y);
		 }
		 
		 @PostMapping("/MensuelPotentielsortieByClient/{id}")
		 @ResponseBody
		 public FacturesStat PotentielMonthByClient(@RequestParam("year") int y,@RequestParam("month") int m,@PathVariable("id") long id) throws ParseException {
			 return factureService.calculProformasStatMonthByClient(y, m,id);
		 }
		 
		 @PostMapping("/YearPotentielsortieByClient/{id}")
		 @ResponseBody
		 public FacturesStat PotentielYearByClient(@RequestParam("year") int y,@PathVariable("id") long id) throws ParseException {
			 return factureService.calculProformasStatYearByClient(y,id);
		 }
		 
		
		 
		 
		 @PostMapping("/MensuelRegsortie")
		 @ResponseBody
		 public FacturesStat RealMonth(@RequestParam("year") int y,@RequestParam("month") int m) throws ParseException, JsonMappingException, JsonProcessingException {
			 return reglementService.calculReglementsStatMonth(y, m);
		 }
		 
		 @PostMapping("/YearRegsortie")
		 @ResponseBody
		 public FacturesStat RealYear(@RequestParam("year") int y) throws ParseException, JsonMappingException, JsonProcessingException {
			 return reglementService.calculReglementsStatYear(y);
		 }
		 
		 @PostMapping("/MensuelRealsortieByClient/{id}")
		 @ResponseBody
		 public FacturesStat RealMonthByClient(@RequestParam("year") int y,@RequestParam("month") int m,@PathVariable("id") long id) throws ParseException, JsonMappingException, JsonProcessingException {
			 return reglementService.calculReglementsStatMonthByClient(y, m,id);
		 }
		 
		 @PostMapping("/YearRealsortieByClient/{id}")
		 @ResponseBody
		 public FacturesStat RealYearByClient(@RequestParam("year") int y,@PathVariable("id") long id) throws ParseException, JsonMappingException, JsonProcessingException {
			 return reglementService.calculReglementsStatYearByClient(y,id);
		 }
		 
		 @GetMapping("/ChangeRate")
		 @ResponseBody
		 public double ChangeRate() throws ParseException, JsonMappingException, JsonProcessingException {
			 return reglementService.GetChangeRate();
		 }
		 
		 
		
		 @GetMapping("/FactureByYear")
		 @ResponseBody
		 public long getFactureByYear() {
			 return factureService.FactureCurrentYear();
		 }
		 
		 @PostMapping("/FactureByYear")
		 @ResponseBody
		 public long getFactureByYear(@RequestParam("month") int m) {
			 return factureService.countByYearAndMonth(m);
		 }
		 
		 
		 @GetMapping("/FactureByYearMonth")
		 @ResponseBody
		 public List<Long> getFactureByYearMonth() {
			 return factureService.FactureByMonthAndYear();
		 }
		 
	
/*-----------------------------------Avoirs Parts------------------------------*/
		
		   @GetMapping("/addAvoir/{id}")
		     public String AddFacture(Model model,@PathVariable("id") long id) {
		    	 Facture f=factureService.getFactureById(id);  	 
		    	 model.addAttribute("Client",f.getClient().getLibelle());
		    	 model.addAttribute("factureid", id);
		    	 model.addAttribute("type", f.getType());
		    	 model.addAttribute("avoir", new Avoir());
			model.addAttribute("mindate", f.getDateemission());
		    	 
		    	
		    	 return "Avoir/create_avoir";
		     }
		   
		   @PostMapping("avoir/{id}")
		   @ResponseBody
		     public ResponseEntity<Map<String, String>> saveAvoir(@PathVariable("id") long idf,@ModelAttribute("avoir") Avoir avoir,@RequestParam("email") String email) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
			   Map<String, String> response = new HashMap<>();
			   avoirService.addAvoir(avoir,idf);
			    response.put("redirect", "/ERPPro/user/factures?email="+email);
		    	 
			   return ResponseEntity.ok(response);
		     }
		   
		   @GetMapping("/updateAvoir/{id}")
		     public String updateAvoir(Model model,@PathVariable("id") long id) {
			  
			   Avoir a=avoirService.getAvoirById(id);
		    	 model.addAttribute("Client",a.getFact().getClient().getLibelle());
		    	 
		    	 model.addAttribute("type", a.getFact().getType());
		    	 model.addAttribute("avoiru",a );
		    	 model.addAttribute("avoir",new Avoir() );
		    	 
		    	
		    	 return "Avoir/create_avoir";
		     }
		   
		   @PostMapping("updateavoir")
		   @ResponseBody
		     public ResponseEntity<Map<String, String>> UpdateAvoir(@ModelAttribute("avoir") Avoir avoir,@RequestParam("email") String email) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
			   Map<String, String> response = new HashMap<>();
			   avoirService.updateAvoir(avoir);
			    response.put("redirect", "/ERPPro/user/factures?email="+email);
			   return ResponseEntity.ok(response);
		     }
		   
		 @GetMapping("GenerateAvoir")
		 @ResponseBody
		 public ResponseEntity<ByteArrayResource> generateAvoir(@RequestBody Avoir avoir) throws DocumentException{
			  try {
		            
		            byte[] avoirPdfBytes = avoirService.generetaAvoirFromFacture(avoir).toByteArray();

		            // Set headers
		            HttpHeaders headers = new HttpHeaders();
		            headers.setContentType(MediaType.APPLICATION_PDF);
		            headers.setContentDispositionFormData("factureInternationale.pdf", "factureInternationale.pdf");

		            // Return ResponseEntity with the PDF byte array
		            return ResponseEntity.ok()
		                    .headers(headers)
		                    .contentLength(avoirPdfBytes.length)
		                    .body(new ByteArrayResource(avoirPdfBytes));
		        } catch (IOException e) {
		            e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		        }
		 }
		 
		 
		 
		 @PutMapping("ValidateAvoir/{id}/{idp}")
		 @ResponseBody
		 public List<Facture> validateAvoir(@PathVariable("id") long id,@PathVariable("idp") long idp,@RequestBody List<Facture> factures){
			 return avoirService.SignAvoir(id,idp,factures);
		 }
		 
		 @PostMapping("/SendAvoirMail/{id}")
		 @ResponseBody
		 public List<Facture> SendAvoirMail(@PathVariable("id") long id,@RequestBody List<Facture> facutres) {
			return avoirService.SendAvoirMail(id,facutres);
		 }
		 
		 public ResponseEntity<ByteArrayResource> downloadAvoir( Avoir avoir) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException{
				
		        
		        try {
		            // Generate the facture PDF as a byte array
		            byte[] facturePdfBytes = avoirService.generateAvoir(avoir).toByteArray();

		            // Set headers
		            HttpHeaders headers = new HttpHeaders();
		            headers.setContentType(MediaType.APPLICATION_PDF);
		            headers.setContentDispositionFormData("avoirInternationale.pdf", "avoirInternationale.pdf");

		            // Return ResponseEntity with the PDF byte array
		            return ResponseEntity.ok()
		                    .headers(headers)
		                    .contentLength(facturePdfBytes.length)
		                    .body(new ByteArrayResource(facturePdfBytes));
		        } catch (IOException e) {
		            e.printStackTrace();
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		        }
		}
		
		
		
		public ResponseEntity<ByteArrayResource> downloadAvoirEuro( Avoir avoir) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException{
			try {
	            // Generate the facture Euro PDF as a byte array
	            byte[] facturePdfBytes = avoirService.generateAvoirEuro(avoir).toByteArray();

	            // Set headers
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_PDF);
	            headers.setContentDispositionFormData("avoirExport.pdf", "avoirExport.pdf");

	            // Return ResponseEntity with the PDF byte array
	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentLength(facturePdfBytes.length)
	                    .body(new ByteArrayResource(facturePdfBytes));
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
		}

		 
		 @GetMapping("previewAvoir/{id}")
			public ResponseEntity<ByteArrayResource> previewAvoir(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, SerialException, SQLException{
				
					Avoir avoir=avoirService.getAvoirById(id);
				if(avoir.getStatus()==AvoirStatus.Avoir_valide || avoir.getStatus()==AvoirStatus.Avoir_envoye) {
					byte[] facturePdfBytes = factureService.downloadFile(avoir.getPdfname());
					 HttpHeaders headers = new HttpHeaders();
			            headers.setContentType(MediaType.APPLICATION_PDF);
			            headers.setContentDispositionFormData("facture"+avoir.getFact().getType()+".pdf", "facture"+avoir.getFact().getType()+".pdf");

			            // Return ResponseEntity with the PDF byte array
			            return ResponseEntity.ok()
			                    .headers(headers)
			                    .contentLength(facturePdfBytes.length)
			                    .body(new ByteArrayResource(facturePdfBytes));
				}
				else {
				
				
					
				
				if(avoir.getFact().getType()==TypeFacture.National) {
					return downloadAvoir(avoir);
				}
				else {
					return downloadAvoirEuro(avoir);
				
				}
				}
			}
		 
		 @DeleteMapping("CancelAvoir/{id}")
		 @ResponseBody
		 public List<Facture> CancelAvoir(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
			  return avoirService.CancelAvoir(id, factures);
		 }
		 
		 @PutMapping("CancelAvoirValide/{id}")
		 @ResponseBody
		 public List<Facture> CancelAvoirValide(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
			 return avoirService.CancelAvoirValider(id, factures);
		 }
		 
		 
		
/*----------------------Produits Part----------------------*/
		 
		 @GetMapping("/Services")
	     public String DisplayProduit(Model model) {
	     
	    
	    	
	

	    	 int pageSize = 5;
	    	 Page<Servicefact> page = produitService.findPaginated(1, pageSize);
	    	 
	    	
	    	 List<Servicefact> services = page.getContent();
	    	 model.addAttribute("currentPage", 1);
	    	
	    	 model.addAttribute("totalPages", page.getTotalPages());
	    	 model.addAttribute("totalItems", page.getTotalElements());
	    	 model.addAttribute("produits", services);
	    	 model.addAttribute("AllProduit", produitService.DisplayAll());
	    	 model.addAttribute("prod",new Servicefact());
	    	 model.addAttribute("activelink", "services");
	    	 
	    	
	    	 return "produit";
	     }
		 
		 @GetMapping("ProduitById/{id}")
		 @ResponseBody
		 public Servicefact getProduitById(@PathVariable("id") long id) {
			 return produitService.getProduitById(id);
		 }
		 
		 @PostMapping("addProd")
		 @ResponseBody
		 public List<Servicefact> addProduit(@ModelAttribute("prod") Servicefact produit){
			 return produitService.addProduct(produit);
		 }
		 
		 @GetMapping("SearchProduit/{search}")
		 @ResponseBody
		 public List<Servicefact> SearchProduits(@PathVariable("search") String search){
			 return produitService.SearchProduit(search);
		 }
		 
		 
		 
		 private List<UserOnLineWhitMSG> getLoggedInUser(String email) throws IOException{
			 
			 List<UserOnLineWhitMSG> result = new ArrayList<>();

               
                 // Assuming your UserDetails implementation is named CustomUserDetails
               
                	 User userDetails = userService.getUserByEmail(email);
                 	
                 	if(userDetails instanceof Client) {
                 		
                 		List<Personel> interact = clientService.getInteractPer(email);
                 		for (Personel personel : interact) {
                 			Message msg=messageService.getLasMessage(userDetails.getIdU(), personel.getIdU());
                 			boolean seen =true;
                 			if(!msg.isSeen() && msg.getSender().getIdU() != userDetails.getIdU() && msg != null) {
                 				seen=false;
                 			}
                 			if(authenticationService.getLoggedIndUser().stream().anyMatch(user -> user.getEmail().equals(personel.getEmail()))) {
                 				if(msg != null) {
                     				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(),true, downloadImage(personel.getImage()),msg.getContent(),msg.getDay(),seen));

                 				}
                 				else {
                     				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(),true, downloadImage(personel.getImage()),"","",seen));

                 				}
                 			}
                 			else {
                 				if(msg != null) {
                     				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(), false,downloadImage(personel.getImage()),msg.getContent(),msg.getDay(),seen));

                 				}
                 				else {
                     				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(), false,downloadImage(personel.getImage()),"","",seen));

                 				}

                 			}
						}
                 		return result;
                 		
                 	}
                 	else {
                 		List<User> users=personelService.findContact(email);
                 		
                 		for (User u : users) {
                 			Client cl=(Client)userService.getUserByEmail(u.getEmail());
                 			Message msg=messageService.getLasMessage(userDetails.getIdU(), cl.getIdU());
                 			if(authenticationService.getLoggedIndUser().stream().anyMatch(user -> user.getEmail().equals(u.getEmail()))) {
                 				
                 				result.add(new UserOnLineWhitMSG(cl.getIdU(),cl.getLibelle(),cl.getLibelle(), cl.getEmail(), true,downloadImage(cl.getImage()),msg.getContent(),msg.getDay(),msg.isSeen()));
                 			}
                 			else {
                 				result.add(new UserOnLineWhitMSG(cl.getIdU(),cl.getLibelle(),cl.getLibelle(), cl.getEmail(), false,downloadImage(cl.getImage()),msg.getContent(),msg.getDay(),msg.isSeen()));

                 			}
						}
                 		return result;
                 	}
                     // Access the user's first name
                     
                 

		 
             
		 }
		 
		 private String downloadImage( String name) throws IOException  {
			 
				return userService.downloadImage(name);
			}
		 

		 
		
			
			
			
			

			
			
			@PostMapping("/addFournisseur")
			@ResponseBody
			public List<Fournisseur> addFournisseur(@ModelAttribute Fournisseur f){
				return fournisseurService.addFournisseur(f);
			}
			
			@PostMapping("/updateFournisseur")
			@ResponseBody
			public List<Fournisseur> updateFournisseur(@ModelAttribute Fournisseur f){
				System.out.println("hhaddhi l image=="+f.getImage());
				return fournisseurService.updateFournisseur(f);
			}
			
			@PutMapping("/archiveFournisseur/{id}")
			@ResponseBody
			public List<Fournisseur> archiveFournisseur(@PathVariable("id") long id){
				return fournisseurService.archiveFournisseur(id);
			}
			
			@PutMapping("/NotarchiveFournisseur/{id}")
			@ResponseBody
			public List<Fournisseur> NotarchiveFournisseur(@PathVariable("id") long id){
				return fournisseurService.NotArchiveFournisseur(id);
			}
			
			@GetMapping("/fournisseur")
			public String DisplayFournisseur(Model model) throws IOException {
				List<Fournisseur> result = fournisseurService.getAllFournisseur();
				for(int i=0;i<result.size();i++) {
					result.get(i).setImage(downloadImage(result.get(i).getImage()));
				}
				model.addAttribute("fournisseurs",result);
				model.addAttribute("newfour", new Fournisseur());
				return "Fournisseur/fournisseur";
			}
			
			@GetMapping("/FourById/{id}")
			@ResponseBody
			public Fournisseur getFournisseurById(@PathVariable("id") long id) {
				return fournisseurService.getFournisseurById(id);
			}
			
			
			@GetMapping("/boncommande")
			public String displayBonCommande(Model model) {
				model.addAttribute("newbl", new BonLivraison());
				model.addAttribute("bcs",bcservice.getNotelivredCommandes());
				return "bc";
			}
			
			@DeleteMapping("/CancelBC/{id}")
			@ResponseBody
			public List<BonCommande> CancelBC(@PathVariable long id) {
				return  bcservice.CancelBC(id);
			}
			
			@PostMapping("/deliverBC/{id}")
			@ResponseBody
			public List<BonCommande> deleverBC(@PathVariable long id,@RequestParam("bl") BonLivraison bl,@RequestParam("depreal") DepenseReelle depreal) {
				return  blService.createBonLivraison(id,bl,depreal);
			}
			
			@PostMapping("/DemandeFromDevis")
			@ResponseBody
			public DemandeDepense getStringFromImage(@RequestParam("imageDevis") MultipartFile image) throws TesseractException, IOException, InterruptedException  {
				
				return demandeDepenseService.getDemandeFromImage(image.getInputStream());
			
			}
			
			
			@GetMapping("DemandeDep")
			public String DisplayDemandeForm(Model model) {
				model.addAttribute("dem", new DemandeDepense());
				model.addAttribute("fours",fournisseurService.getNotArchivedFournisseur());
				return "Depense/demande";
			}
			
			@PostMapping("CreateDemande")
			@ResponseBody
			public DemandeDepense CreateDemand(@RequestBody PersonalDemandeDep pdem) {
				System.out.println("------------>>>>>"+pdem.getDemande().getBudget());
				return demandeDepenseService.CreateDemande(pdem);
			}
			
			@GetMapping("/Demandesstatus")
			public String DisplayDemandesStatus(Model model) {

		    	 int pageSize = 5;
		    	 Page<DemandeDepense> page = demandeDepenseService.findPaginated(1, pageSize,demandeDepenseService.getEnattenteDemande());
		    	 
		    	
		    	 List<DemandeDepense> demandes = page.getContent();
		    	 model.addAttribute("currentPage", 1);
		    	 
		    	 model.addAttribute("totalPages", page.getTotalPages());
		    	 model.addAttribute("totalItems", page.getTotalElements());
		    	 model.addAttribute("demandes", demandes);
		    	 model.addAttribute("Alldemandes", demandeDepenseService.getEnattenteDemande());
		    	return "Depense/demandestatus";
		    	 
			}
			
			
			
			@GetMapping("DepFacturePDF/{id}")
			public ResponseEntity<ByteArrayResource> previewDepenseFacture(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, SerialException, SQLException{
				System.out.println(id);
				DepenseReelle d=depenseReelleService.getDepenseById(id);
				byte[] depensePdfBytes = factureService.downloadFile(d.getFacture());
				 HttpHeaders headers = new HttpHeaders();
		            headers.setContentType(MediaType.APPLICATION_PDF);
		            headers.setContentDispositionFormData("facture.pdf", "facture.pdf");

		            // Return ResponseEntity with the PDF byte array
		            return ResponseEntity.ok()
		                    .headers(headers)
		                    .contentLength(depensePdfBytes.length)
		                    .body(new ByteArrayResource(depensePdfBytes));
				}
			
			@GetMapping("/DepFactureJPG/{id}")
			public ResponseEntity<ByteArrayResource> previewDepenseImage(@PathVariable("id") long id) throws IOException, SQLException {
			    // Remplacez cette ligne par la logique pour obtenir le nom de l'image à partir de l'id
			    DepenseReelle d = depenseReelleService.getDepenseById(id); 
			    byte[] imageBytes = factureService.downloadFile(d.getFacture());

			    HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.IMAGE_JPEG); // Changez le type MIME en fonction du type d'image
			    headers.setContentDispositionFormData("image.jpg", "image.jpg"); // Changez l'extension en fonction du type d'image

			    return ResponseEntity.ok()
			            .headers(headers)
			            .contentLength(imageBytes.length)
			            .body(new ByteArrayResource(imageBytes));
			}
			
			
			@GetMapping("DepPayPDF/{id}")
			public ResponseEntity<ByteArrayResource> previewDepensePayment(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, SerialException, SQLException{
				
				DepenseReelle d=depenseReelleService.getDepenseById(id);
				byte[] depensePdfBytes = factureService.downloadFile(d.getPapierpay());
				 HttpHeaders headers = new HttpHeaders();
		            headers.setContentType(MediaType.APPLICATION_PDF);
		            headers.setContentDispositionFormData("facture.pdf", "facture.pdf");

		            // Return ResponseEntity with the PDF byte array
		            return ResponseEntity.ok()
		                    .headers(headers)
		                    .contentLength(depensePdfBytes.length)
		                    .body(new ByteArrayResource(depensePdfBytes));
				}
			
			@GetMapping("DepPayJPG/{id}")
			public ResponseEntity<ByteArrayResource> previewDepensePaymentJPG(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, SerialException, SQLException{
				
				DepenseReelle d=depenseReelleService.getDepenseById(id);
				byte[] imageBytes = factureService.downloadFile(d.getPapierpay());
				HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.IMAGE_JPEG); // 
			    headers.setContentDispositionFormData("image.jpg", "image.jpg");

			    return ResponseEntity.ok()
			            .headers(headers)
			            .contentLength(imageBytes.length)
			            .body(new ByteArrayResource(imageBytes));
				}
			
			
			 @PostMapping("/DeppageList/{pageNo}")
			 @ResponseBody
		     public List<DepenseReelle> findDepPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<DepenseReelle> depenses) {
		    	 int pageSize = 5;
		    	 Page<DepenseReelle> page = depenseReelleService.findPaginated(pageNo, pageSize,depenses);
		    	 
		    	 return page.getContent(); 
		     }
			 
			 @PostMapping("/Deppagetotal/{pageNo}")
			 @ResponseBody
		     public int findDepPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<DepenseReelle> depenses) {
		    	 int pageSize = 5;
		    	 Page<DepenseReelle> page = depenseReelleService.findPaginated(pageNo, pageSize,depenses);
		    	 return page.getTotalPages(); 
		     }
			
			 
			 @GetMapping("/Depenses")
				public String DisplayDepenses(Model model) {
			    	 int pageSize = 5;
			    	 Page<DepenseReelle> page = depenseReelleService.findPaginated(1, pageSize,depenseReelleService.getDepenseOrderByDate());
			    	 
			    	
			    	 List<DepenseReelle> demandes = page.getContent();
			    	 model.addAttribute("currentPage", 1);
			    	 
			    	 model.addAttribute("totalPages", page.getTotalPages());
			    	 model.addAttribute("totalItems", page.getTotalElements());
			    	 model.addAttribute("depenses", demandes);
			    	 model.addAttribute("Alldepenses", depenseReelleService.getDepenseOrderByDate());
			    	return "Depense/depensereal";
			    	 
				}
			 
			 @GetMapping("DepenseSearch/{search}")
			 @ResponseBody
			 public List<DepenseReelle> DepenseSearch(@PathVariable("search") String search){
				 return depenseReelleService.Search(search);
			 }
			 
			 
			 @GetMapping("DemandeSearch/{search}")
			 @ResponseBody
			 public List<DemandeDepense> DemandeSearch(@PathVariable("search") String search){
				 return demandeDepenseService.search(search);
			 }
			 
			 @GetMapping("/bonLivraison")
				public String displayBonLivraison(Model model) {
				
					model.addAttribute("bcs",blService.getAll());
					return "bl";
				}
			 
			
			
			 
			 
			
			 
			 
			 @PutMapping("/ActiverFour/{id}")
			 @ResponseBody
			 public List<Fournisseur> activerFourn(@PathVariable("id") long id){
				 return fournisseurService.activerFour(id);
			 }
			 
			 
			 @PutMapping("/DesactiverFour/{id}")
			 @ResponseBody
			 public List<Fournisseur> ArchiverFour(@PathVariable("id") long id){
				 return fournisseurService.archiverFour(id);
			 }
			 
			 
			 @PostMapping("/SearchFour")
			 @ResponseBody
			 public List<Fournisseur> SearchFour(@RequestParam("search") String search){
				 return fournisseurService.search(search);
			 }
			 
			 @GetMapping("/AcceptDem/{id}")
			 @ResponseBody
			 public List<DemandeDepense> acceptDem(@PathVariable("id") long id){
				 return demandeDepenseService.acceptDemande(id);
			 }
			 
			 @GetMapping("/RefuseDem/{id}")
			 @ResponseBody
			 public List<DemandeDepense> refuseDem(@PathVariable("id") long id){
				 System.out.println("------->"+id);
				 return demandeDepenseService.refuseDemande(id);
			 }
			 
			 @PostMapping("/delevreBC/{id}")
			 @ResponseBody
			 public List<BonCommande> deleverBC(@PathVariable("id") long id,@RequestBody DepRealWithAdresse bl){
				 
				 return bcservice.DeliverBC(id,bl);
			 }
			 
			 @GetMapping("/getReglementPoints")
			 @ResponseBody
			 public List<ReglementPoint> getReglementPoint() throws JsonMappingException, JsonProcessingException, ParseException{
				 return reglementService.getReglementsPoints();
			 }
			 
			 @GetMapping("getUserRoleById/{id}")
			 @ResponseBody
			 public String getUserRoleById(@PathVariable("id") long id) {
				 return userService.getUserRoleById(id);
			 }
			 
			 
			 @PutMapping("/ArchiveService/{id}")
			 @ResponseBody
			 public List<Servicefact> archiveService(@PathVariable("id") long id){
				 return produitService.ArchiverService(id);
			 }
			 
			 
			 @PutMapping("/DesrchiveService/{id}")
			 @ResponseBody
			 public List<Servicefact> DesrchiveService(@PathVariable("id") long id){
				 return produitService.DesarchiverService(id);
			 }
			
			
			@GetMapping("/factureret")
				public String displayRetenues(Model model) throws IOException {
					List<Facture> factures=factureService.getFacturePayed();
					
					
					 int pageSize = 5;
			    	 Page<Facture> page = factureService.findPaginated(1, pageSize,factures);
					
			    	 model.addAttribute("currentPage", 1);
			    	 model.addAttribute("totalPages", page.getTotalPages());
			    	 model.addAttribute("totalItems", page.getTotalElements());
			    	 model.addAttribute("factures", page.getContent());
			    	 model.addAttribute("Allfactures", factures);
					 model.addAttribute("activelink", "retenues");
					
					return "Reglement/Factureret";		
				}



	@GetMapping("/CheckEmailAndLibelle")
	@ResponseBody
	public boolean CheckEmailAndLibelle(@RequestParam("email") String email,@RequestParam("lib") String lib) {
					return clientService.CheckUserInfo(email, lib);
				}
			
			
			@GetMapping("BLdocPDF/{id}")
				public ResponseEntity<ByteArrayResource> previewretBLpdf(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, SerialException, SQLException{
					
					BonLivraison bl=blService.findbyid(id);
					byte[] depensePdfBytes = factureService.downloadFile(bl.getBldoc());
					 HttpHeaders headers = new HttpHeaders();
			            headers.setContentType(MediaType.APPLICATION_PDF);
			            headers.setContentDispositionFormData("retenue.pdf", "retenue.pdf");

			            // Return ResponseEntity with the PDF byte array
			            return ResponseEntity.ok()
			                    .headers(headers)
			                    .contentLength(depensePdfBytes.length)
			                    .body(new ByteArrayResource(depensePdfBytes));
					}
				
				@GetMapping("/BLJPEG/{id}")
				public ResponseEntity<ByteArrayResource> previewBLImage(@PathVariable("id") long id) throws IOException, SQLException {
				
					BonLivraison bl=blService.findbyid(id);
				    byte[] imageBytes = factureService.downloadFile(bl.getBldoc());

				    HttpHeaders headers = new HttpHeaders();
				    headers.setContentType(MediaType.IMAGE_JPEG); // Changez le type MIME en fonction du type d'image
				    headers.setContentDispositionFormData("image.jpg", "image.jpg"); // Changez l'extension en fonction du type d'image

				    return ResponseEntity.ok()
				            .headers(headers)
				            .contentLength(imageBytes.length)
				            .body(new ByteArrayResource(imageBytes));
				}
			
			
		 


}
