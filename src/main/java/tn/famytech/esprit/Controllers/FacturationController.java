package tn.famytech.esprit.Controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.hellosign.sdk.HelloSignException;
import com.itextpdf.text.DocumentException;

import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Services.ClientService;
import tn.famytech.esprit.Services.FactureService;


@Controller
@RequestMapping("/facturation")
public class FacturationController {
	
	@Autowired
	private FactureService factureService;
	
	@Autowired
	private ClientService clientService;
	
	
	@PostMapping("/downloadfacture")
	public ResponseEntity<ByteArrayResource> downloadFacture(byte[] facturePdfBytes) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException{
			
	        
	        try {
	            // Generate the facture PDF as a byte array
	            

	            // Set headers
	            HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_PDF);
	            headers.setContentDispositionFormData("factureTND.pdf", "factureTND.pdf");

	            // Return ResponseEntity with the PDF byte array
	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentLength(facturePdfBytes.length)
	                    .body(new ByteArrayResource(facturePdfBytes));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	}
	
	
	@PostMapping("/downloadfactureEuro")
	public ResponseEntity<ByteArrayResource> downloadFactureEuro(byte[] facturePdfBytes) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException{
		try {
            // Generate the facture Euro PDF as a byte array

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("factureEURO.pdf", "factureEURO.pdf");

            // Return ResponseEntity with the PDF byte array
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(facturePdfBytes.length)
                    .body(new ByteArrayResource(facturePdfBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
	}
	

	
	@GetMapping("previewFacture/{id}")
	public ResponseEntity<ByteArrayResource> previewFacture(@PathVariable("id") long id) throws FileNotFoundException, IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, DocumentException, WriterException{
		
		Facture f=factureService.getFactureById(id);
		if(f.getType()==TypeFacture.National) {
			return downloadFacture(factureService.generateFactureAfterSign(f).toByteArray());
		}
		else {
			return downloadFactureEuro(factureService.generateFactureEuroAfterSign(f).toByteArray());
		}
	}
	

	
	 
	 
     @GetMapping("/generateQR")
     public String getQRCode(Model model) throws com.google.zxing.WriterException{
         return factureService.getQRCode(model);
     }
     
     @GetMapping("/factures")
     public String DisplayFacture(Model model) {
    	/*model.addAttribute("factures", factureService.DisplayFactures());
    	 model.addAttribute("clients", clientService.DisplayClients());
    	 return "factures";*/
    	 
         

    	 int pageSize = 5;
    	 Page<Facture> page = factureService.findPaginated(1, pageSize,factureService.DisplayFacturesNotArchived());
    	 
    	
    	 List<Facture> factures = page.getContent();
    	 model.addAttribute("currentPage", 1);
    	 model.addAttribute("totalPages", page.getTotalPages());
    	 model.addAttribute("totalItems", page.getTotalElements());
    	 model.addAttribute("factures", factures);
    	 model.addAttribute("Allfactures", factureService.DisplayFacturesNotArchived());
    	 model.addAttribute("clients", clientService.DisplayClients());
    	 model.addAttribute("activelink", "factures");
    	
    	 return "factures"; 
    	 
     }
     
 
     
 
     
     @GetMapping("/addfacture")
     public String AddFacture(Model model) {
    	 model.addAttribute("Clients",clientService.DisplayClients());
    	 model.addAttribute("facture", new Facture());
    	 model.addAttribute("activelink", "factures");
    	
    	
    	 return "create_facture";
     }
     
    /* @PostMapping("factures")
     public String saveFacture(@ModelAttribute("facture") Facture facture) throws UnrecoverableKeyException, FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, DocumentException {
    	Facture f= factureService.AddFacture(facture);
    	 
    	 return "redirect:/home/factures";
     }*/
     
   
     
     @GetMapping("/FactureQRCode/{id}")
     @ResponseBody
     public byte[] FactureQRCode(@PathVariable("id") long id) throws WriterException, IOException, DocumentException {
    	return factureService.getQRCodeImage(id,250,250);
     }
     
     
 
     
     
	 


}
