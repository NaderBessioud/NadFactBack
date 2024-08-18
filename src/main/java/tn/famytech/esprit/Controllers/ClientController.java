package tn.famytech.esprit.Controllers;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.text.DocumentException;

import tn.famytech.esprit.DTO.FactureMobile;
import tn.famytech.esprit.DTO.UserMobile;
import tn.famytech.esprit.DTO.UserOnLineWhitMSG;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Message;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Services.AuthenticationService;
import tn.famytech.esprit.Services.AvoirService;
import tn.famytech.esprit.Services.ClientService;
import tn.famytech.esprit.Services.FactureService;
import tn.famytech.esprit.Services.LoggedInUserService;
import tn.famytech.esprit.Services.MessageService;
import tn.famytech.esprit.Services.PersonelService;
import tn.famytech.esprit.Services.UserService;

@Controller

@RequestMapping("/client")
public class ClientController {
	
	@Autowired
	private FactureService factureService;
	
	@Autowired
	private AvoirService avoirService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private PersonelService personelService;
	
	 @GetMapping("/getFactureById/{id}")
     @ResponseBody
	 public Facture getFactureById(@PathVariable("id") long id) {
		 return factureService.getFactureById(id);
	 }
	 
	 @PostMapping("/pageList/{pageNo}")
	 @ResponseBody
     public List<Facture> findPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<Facture> facutres) {
    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(pageNo, pageSize,facutres);
    	 
    	 return page.getContent(); 
     }
	 
	 @PostMapping("/pagetotal/{pageNo}")
	 @ResponseBody
     public int findPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<Facture> facutres) {
    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(pageNo, pageSize,facutres);
    	 return page.getTotalPages(); 
     }
	 
	 private String downloadImage( String name) throws IOException  {
		 
			return userService.downloadImage(name);
		}
	 
	 
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
	 
	
	 @PutMapping("/ValidateProformaByClient/{id}")
	 @ResponseBody
	 public List<Facture> validateProformaByClient(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		return factureService.updateFactureStatus(id, FactureStatus.Proforma_envoyee_validee, factures,"validé");
	 }
	 
	 @PutMapping("/denyProformaByClient/{id}")
	 @ResponseBody
	 public List<Facture> denyProforma(@PathVariable("id") long id,@RequestBody List<Facture> factures) {
		return factureService.updateFactureStatus(id, FactureStatus.Proforma_envoyee_Refusee, factures,"refusé");
	 }
	 
	 @PostMapping("/SearchFacture")
	 @ResponseBody
	 public List<Facture> searchFacture(@RequestParam("search") String search,@RequestBody List<Facture> factures){
		 return factureService.SearchFactures(search,factures);
	 }
	 
	 @PutMapping("/updateprofile")
	 @ResponseBody
	 public void updateClientProfile(@RequestParam("email") String email,@RequestParam("addr") String addr,@RequestParam("id") long id,@RequestParam("image") String image){
		 clientService.updateClientProfile(email, addr, id,image);
	 }
	 
	 
	 @PostMapping("/checkoldpass")
	 @ResponseBody
	 public boolean checkoldpass(@RequestParam("id") long id,@RequestParam("pass") String pass){
		return clientService.CheckOldPass(id,pass);
	 }
	 
	 
	 @PutMapping("/updatePass")
	 @ResponseBody
	 public void updateClientProfile(@RequestParam("id") long id,@RequestParam("pass") String pass){
		 clientService.updateCLientPass(id,pass);
	 }
	 
	 @GetMapping("/clientById/{id}")
	 @ResponseBody
	 public ResponseEntity<Client> getClientById(@PathVariable("id") long id){
		 Client client = clientService.getClientById(id);
	        if (client != null) {
	            return ResponseEntity.ok(client);
	        } else {
	            return ResponseEntity.notFound().build();
	        }
	 }
	 
	 @GetMapping("/FactureByClient/{clientemail}")
	 @ResponseBody
	 public List<FactureMobile> getFactureByClient(@PathVariable("clientemail") String email){
		 return clientService.getFactureByClient(email);
	 }
	 
	 private List<UserOnLineWhitMSG> getInteractOnLine(long id){
		 List<UserOnLineWhitMSG> result =new ArrayList<UserOnLineWhitMSG>();
		 User userDetails=userService.getUserById(id);
			List<Personel> interact = clientService.getInteractPer(userDetails.getEmail());
     		
     		for (Personel personel : interact) {
     			Message msg=messageService.getLasMessage(id, personel.getIdU());
     			if(authenticationService.getLoggedIndUser().stream().anyMatch(user -> user.getEmail().equals(personel.getEmail()))) {
     				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(), true,personel.getImage(),msg.getContent(),msg.getDay(),msg.isSeen()));
     			}
     			else {
     				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(), false,personel.getImage(),msg.getContent(),msg.getDay(),msg.isSeen()));

     			}
			}
     		return result;
	 }
	 
	
	 
	
	 
	 private List<UserOnLineWhitMSG> getLoggedInUser(String email) throws IOException{
		 		List<UserOnLineWhitMSG> result = new ArrayList<>();
             	User userDetails=userService.getUserByEmail(email);
             	
             		
             		List<Personel> interact = clientService.getInteractPer(userDetails.getEmail());
             		
             		for (Personel personel : interact) {
             			Message msg=messageService.getLasMessage(userDetails.getIdU(), personel.getIdU());
             			if(authenticationService.getLoggedIndUser().stream().anyMatch(user -> user.getEmail().equals(personel.getEmail()))) {
             				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(),true, downloadImage(personel.getImage()),msg.getContent(),msg.getDay(),msg.isSeen()));
             			}
             			else {
             				result.add(new UserOnLineWhitMSG(personel.getIdU(),personel.getFirstname(),personel.getFirstname()+" "+personel.getLastname(), personel.getEmail(), false,downloadImage(personel.getImage()),msg.getContent(),msg.getDay(),msg.isSeen()));

             			}
					}
             		return result;
             		   
	 }
	 
	 @PostMapping("readmsg")
	 @ResponseBody
	 public List<UserOnLineWhitMSG> readMsg(@RequestBody List<Long> msgs,@RequestParam("email") String email) throws IOException {
		 messageService.ReadMsg(msgs);
		 return getLoggedInUser(email);
	 }
	 
	 
	/* @PostMapping("readmsgbyid/{id}")
	 @ResponseBody
	 public void readMsg(@PathVariable("id") long id) throws IOException {
		 messageService.ReadSingleMSG(id);
		 
	 }*/
	 
	 
	 @GetMapping("/MessagesMobile/{ids}/{idr}")
		@ResponseBody
		public List<Message> getMessagesMobile(@PathVariable("ids") long ids,@PathVariable("idr") long idr){
		 
		
			return messageService.getMessages(ids, idr);
		}
	 
	 @GetMapping("/UsersMobile/{id}")
	 @ResponseBody
	 public List<UserMobile> getUserMobile(@PathVariable("id") long id){
		 List<UserMobile> result=new ArrayList<UserMobile>();
		 for (UserOnLineWhitMSG usr : getInteractOnLine(id)) {
			 Message msg=messageService.getLasMessage(id, usr.getIdU());
			result.add(new UserMobile(usr.getIdU(),usr.getFullname(), usr.getEmail(), usr.isOnline(),usr.getImage(),messageService.getMessages(id,usr.getIdU()).size(),msg.getContent(),msg.getDay(),msg.isSeen()));
		}
		 System.out.println("----------------->>>>>>>"+result.size());
		return result;
	 }
	 
		@GetMapping("/FactureClient/{id}")
		public String DisplayFactureClient(Model model,@PathVariable("id") long ids) {
			 int pageSize = 5;
	    	 
			
			 
			 Page<Facture> page = factureService.findPaginated(1, pageSize,clientService.findFactureByClient(ids));
	    	 List<Facture> factures = page.getContent();
			 
	    	 model.addAttribute("currentPage", 1);
	    	
	    	 model.addAttribute("totalPages", page.getTotalPages());
	    	 model.addAttribute("totalItems", page.getTotalElements());
	    	 model.addAttribute("factures", factures);
	    	 model.addAttribute("Allfactures", clientService.findFactureByClient(ids));
	    	 
	    	 model.addAttribute("activelink", "facturescl");
	    	
	    	 return "facturescl"; 
			 
			
		}
		
		 @PutMapping("/updateProfilecl")
		 @ResponseBody
		 public User updateProfile(@ModelAttribute Client user) {
			 return userService.updateProfilecl(user);
		 }
		 
		  @PutMapping("/validateProforma/{id}")
			 @ResponseBody
			 public void validateProforma(@PathVariable("id") long id) {
				 factureService.updateFactureStatusByClient(id, FactureStatus.Proforma_envoyee_validee,"validé");
			 }
			 
			 @PutMapping("/denyProforma/{id}")
			 @ResponseBody
			 public void denyProforma(@PathVariable("id") long id) {
				 factureService.updateFactureStatusByClient(id, FactureStatus.Proforma_envoyee_Refusee,"refusé");
				
			 }
			 
			 
			 @GetMapping("/Profile/{email}")
			 public String UserProfile(@PathVariable("email") String email, Model model) {
				 model.addAttribute("user",new Personel());
				 model.addAttribute("cl",clientService.getClientByEmail(email));
				 model.addAttribute("activelink","profile");
				 return "user/user_profile.html";
			 }
			 
			 
			  
		
		 
		 
		 
		
	 
	 
	
	 
	 
	

}
