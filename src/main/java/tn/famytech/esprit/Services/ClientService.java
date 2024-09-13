package tn.famytech.esprit.Services;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import tn.famytech.esprit.DTO.ClientWithImage;
import tn.famytech.esprit.DTO.FactureMobile;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Message;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.UserType;
import tn.famytech.esprit.Repositories.AvoirRepo;
import tn.famytech.esprit.Repositories.ClientRepo;
import tn.famytech.esprit.Repositories.FactureRepo;
import tn.famytech.esprit.Security.PasswordEncoderBean;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class ClientService {
	
	@Autowired
	private ClientRepo clientRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FactureRepo factureRepo;
	
	@Autowired
	private AvoirRepo avoirRepo;
	
	@Autowired
	private PasswordEncoderBean passwordencode;
	
	@Autowired
    private TemplateEngine templateEngine;
	
	@Autowired
	private  JavaMailSender mailSender;
	

	

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 12;
    private static final Random RANDOM = new SecureRandom();
    
	public Client getClientById(long id) {
		return clientRepo.findById(id).get();
	}
	
	  private static String generateRandomPassword() {
	        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

	        for (int i = 0; i < PASSWORD_LENGTH; i++) {
	            int index = RANDOM.nextInt(CHARACTERS.length());
	            password.append(CHARACTERS.charAt(index));
	        }

	        return password.toString();
	    }

	
	   private String loadEmailTemplate(String email,String mdp,String libelle) throws IOException {
	        Context context = new Context();
	        context.setVariable("libelle", libelle);
	        context.setVariable("email", email);
	        context.setVariable("pass", mdp);
	       
	        

	        
	        return templateEngine.process("email/newclient", context);
	    }

	public Client AddClient(Client c) {
		c.setArchived(false);
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        // Format the current date
	    String formattedDate = currentDate.format(formatter);
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				 c.setDatecreation(dateFormat.parse(formattedDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		c.setRole(UserType.Client);
		String pass=generateRandomPassword();
		c.setPassword(passwordencode.passwordEncoder().encode(pass));
		c.setActive(true);
		c.setDatecreation(new Date());
		
	      try {
              
              String emailContent = loadEmailTemplate(c.getEmail(),pass,c.getLibelle());
              MimeMessage mimeMessage = mailSender.createMimeMessage();
              MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
              helper.setSubject("NadFact");
              helper.setFrom("naderbessioud98@gmail.com");
              helper.setTo(c.getEmail());
              helper.setText(emailContent, true);

             

              // Send email
              mailSender.send(mimeMessage);
            
            
        	
             
          } catch (Exception e) {
          	
              e.printStackTrace();
              
          }
		
		return clientRepo.save(c);
	}

	
	public Client UpdateClient(Client c) {
		
		
		return clientRepo.save(c);
	}

	
	public void DeleteClient(Long id) {
		Client c= clientRepo.findById(id).orElse(null);
		clientRepo.delete(c);		
	}

	
	public List<Client> DisplayClients() {
	
		
		 return clientRepo.findByArchived(false);
	}
	
	public List<ClientWithImage> DisplayClientWithImage() throws IOException{
		List<ClientWithImage> result = new ArrayList<ClientWithImage>();
		for (Client client : DisplayClients()) {
			result.add(new ClientWithImage(client.getIdU(),client.getLibelle(),userService.downloadImage(client.getImage())));
		}
		return result;
	}
	
	  public Page<ClientWithImage> findPaginated(int pageNum, int pageSize,List<ClientWithImage> clients){
		  System.out.println("-------------->>>>>>"+pageNum);
	    	Pageable pageable = PageRequest.of(pageNum-1, pageSize);
	    	int start = (pageNum - 1) * pageSize;
	    	System.out.println("-------------->>>>>>"+start);
	        int end = Math.min((start + pageSize), clients.size());

	        List<ClientWithImage> sublist;
	        if (start <= end) {
	            sublist = clients.subList(start, end);
	        } else {
	            // If start index exceeds end index, return sublist from the beginning of the list
	            sublist = clients.subList(0, Math.min(pageSize, clients.size()));
	        }

	    	
	    	
	    	return new PageImpl<ClientWithImage>(sublist, pageable, clients.size());
	    }
	  
	  
	  public List<ClientWithImage> addClientAndUpdateList(Client c) throws IOException{
		  AddClient(c);
		  return DisplayClientWithImage();
	  }
	  
	  
	  public List<ClientWithImage> UpdateClientAndUpdateList(Client c,long id) throws IOException{
		  c.setIdU(id);
		  UpdateClient(c);
		  return DisplayClientWithImage();
	  }
	
	
	  public List<ClientWithImage> SearchClient(String serch) throws IOException{
		  List<ClientWithImage> result = new ArrayList<ClientWithImage>();
		  for (Client c : DisplayClients()) {
			if(c.getLibelle().contains(serch)  || c.getIdfiscal().contains(serch) || c.getAddresse().contains(serch)) {
				result.add(new ClientWithImage(c.getIdU(),c.getLibelle(),userService.downloadImage(c.getImage())));
			}
		
		}
			return result;
	  }
	  
	  public List<Client> DisplayNotArchivedCLient(){
		  return clientRepo.findByArchived(false);
	  }
	  
	  public List<Personel> getInteractPer(String email){
		  return clientRepo.findInteractPersonel(email);
	  }
	  
	  public List<Facture> findFactureByClient(long id){
		  Client cl=clientRepo.findById(id).get();
		  List<Facture> resultProforma=new ArrayList<Facture>();
		  List<Facture> result = new ArrayList<Facture>();
		  List<Facture> resultf=factureRepo.findByClientAndStatus(cl, FactureStatus.Proforma_envoyee);
		  resultf.addAll(factureRepo.findByClientAndStatus(cl, FactureStatus.Facture_envoye));
		  
		  
		  for (Facture facture1 : resultf) {
	  			if(facture1.getStatus()==FactureStatus.Facture || facture1.getStatus()==FactureStatus.Facture_valide || facture1.getStatus()==FactureStatus.Facture_envoye ) {
	  				result.add(facture1);
	  			}
	  			else{
	  				resultProforma.add(facture1);
	  			}
	  			
	  		}
	  		Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
	  		Collections.sort(resultProforma, Comparator.comparing(Facture::getNumber));
	  		for (int i = 0; i < resultProforma.size(); i++) {
	 		    
	  			 result.add(0,resultProforma.get(i) );
	  			}	
	  		
	  		
	  			return result;
	  }
	  public List<Facture> validateProformaByClient(long id,List<Facture> factures) {
		  Facture facture=factureRepo.findById(id).get();
		  facture.setStatus(FactureStatus.Proforma_envoyee_validee);
		  facture=factureRepo.save(facture);
		  
		  List<Facture> resultProforma=new ArrayList<Facture>();
		  List<Facture> result = new ArrayList<Facture>();
	    	
	    	
	    	for(int i=0;i<factures.size();i++) {
	    		if(factures.get(i).getIdF()==id) {
	    			factures.set(i, facture);
	    		}
	    	}
	    	
	    	 for (Facture facture1 : factures) {
	  			if(facture1.getStatus()==FactureStatus.Facture || facture1.getStatus()==FactureStatus.Facture_valide || facture1.getStatus()==FactureStatus.Facture_envoye ) {
	  				result.add(facture1);
	  			}
	  			else{
	  				resultProforma.add(facture1);
	  			}
	  			
	  		}
	  		Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
	  		Collections.sort(resultProforma, Comparator.comparing(Facture::getNumber));
	  		for (int i = 0; i < resultProforma.size(); i++) {
	 		    
	  			 result.add(0,resultProforma.get(i) );
	  			}	
	  		
	  		
	  			return result;
	  }
	  
	  public List<FactureMobile> getFactureByClient(String email){
		  List<FactureMobile> result=new ArrayList<FactureMobile>();
		  Client c=(Client) userService.getUserByEmail(email);
		  for (Facture f : factureRepo.findByClientAndArchived(c, false)) {
			if(f.getStatus()==FactureStatus.Facture_envoye ) {
				result.add(new FactureMobile(f.getIdF(), f.getNumber(),"Facture", f.getTotalttc(), f.getDateemission(), f.getStatus().toString(), f.getType().toString(), f.getPdfname()));
				for (Avoir av:avoirRepo.findByFact(f) ) {
					if(av.getStatus()==AvoirStatus.Avoir_envoye)
					result.add(new FactureMobile(av.getIdC(), av.getNumber(),"Avoir", av.getMontant(), av.getDateemission(), av.getStatus().toString(), f.getType().toString(), av.getPdfname()));

					
				}
			}
			
			else if(f.getStatus()==FactureStatus.Proforma_envoyee || f.getStatus()==FactureStatus.Proforma_envoyee_validee || f.getStatus()==FactureStatus.Proforma_envoyee_Refusee) {
				result.add(new FactureMobile(f.getIdF(), f.getNumber(),"Proforma", f.getTotalttc(), f.getDateemission(), f.getStatus().toString(), f.getType().toString(), f.getPdfname()));

			}
			
			
		}
		  
		  Collections.sort(result, Comparator.comparing(FactureMobile::getNumber).reversed());
		  System.out.println("---------------->"+result.size());
		  return result;
		  
		  
		  
		  
	  }
	  
	  
	  public void updateClientProfile(String email,String addr,long id,String image) {
		  Client c =clientRepo.findById(id).get();
		  c.setEmail(email);
		  c.setAddresse(addr);
		  c.setImage(image);
		  clientRepo.save(c);
	  }
	  
	  
	  public void updateCLientPass(long id,String pass) {
		  Client c =clientRepo.findById(id).get();
		  c.setPassword(passwordencode.passwordEncoder().encode(pass));
		  clientRepo.save(c);
	  }
	  
	  public boolean CheckOldPass(long id,String pass) {
		  Client c =clientRepo.findById(id).get();
	

		  return passwordencode.passwordEncoder().matches(pass, c.getPassword());
	  }
	  
	  public Client getClientByEmail(String email) {
		  return clientRepo.findByEmail(email);
	  }
	  
	  
	  
	
	  
	
	  
	
	  
	  
	 


}
