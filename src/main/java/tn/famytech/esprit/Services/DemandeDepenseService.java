package tn.famytech.esprit.Services;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.ITessAPI.TessPageSegMode;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import tn.famytech.esprit.DTO.PersonalDemandeDep;
import tn.famytech.esprit.DTO.ProuduitQuantite;
import tn.famytech.esprit.Entites.BCStatus;
import tn.famytech.esprit.Entites.BonCommande;
import tn.famytech.esprit.Entites.DemandeDepense;
import tn.famytech.esprit.Entites.DemandeStatus;
import tn.famytech.esprit.Entites.DepenseReelle;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Fournisseur;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Repositories.BonCommandeRepo;
import tn.famytech.esprit.Repositories.DemandeDepenseRepo;
import tn.famytech.esprit.Repositories.FournisseurRepo;
import tn.famytech.esprit.Repositories.PersonelRepo;

@Service
public class DemandeDepenseService {
	
	@Autowired
	private DemandeDepenseRepo demandeDepenseRepo;
	
	@Autowired
	private PersonelRepo personelRepo;
	@Autowired
	private FournisseurRepo fournisseurRepo;
	@Autowired
	private BonCommandeRepo bcrepo;

	
	/*@Transactional
	public DemandeDepense CreateDemande(DemandeDepense demandeDepense,long idper) {
		Personel pers=personelRepo.findById(idper).get();
		demandeDepense.setDate(new Date());
		demandeDepense.setStatus(DemandeStatus.En_Attente);
		demandeDepense.setUserde(pers);
		pers.getDemandes().add(demandeDepense);
		personelRepo.save(pers);
		
		  for (int i = 0; i < demandeDepense.getProductsdep().size(); i++) {
		    	LigneCommandeDepense lc=demandeDepense.getProductsdep().get(i);
		    	
		    	LigneCommandeDepense lc1=comdepservice.AddLigneCommande(lc, demandeDepense);
		    	demandeDepense.getProductsdep().set(i, lc1);
		    }
		
		return demandeDepenseRepo.save(demandeDepense);
	}*/
	
	/*@Transactional
	public void updateDemandeDepense(DemandeDepense demande) {
		DemandeDepense demandedep=demandeDepenseRepo.findById(demande.getIdDD()).get();
		Personel per=personelRepo.findById(demande.getUserde().getIdU()).get();
		demandedep.setUserde(per);
		
		for(int i=0;i<demandedep.getProductsdep().size();i++) {
			if(demande.getProductsdep().contains(demandedep.getProductsdep().get(i))) {
				comdepservice.deleteLigneCommande(demandedep.getProductsdep().get(i).getIdLCD(), demandedep.getIdDD());
				demandedep.getProductsdep().remove(i);
			}
		}
		
		
		
		for(int i=0;i<demande.getProductsdep().size();i++) {
			
			if(demande.getProductsdep().get(i).getIdLCD() != 0) {
				LigneCommandeDepense lc=comdepservice.updateLigneCommande(demandedep.getProductsdep().get(i), demandedep.getIdDD());
				demandedep.getProductsdep().add(lc);
			}
			else {
				LigneCommandeDepense lc=comdepservice.AddLigneCommande(demande.getProductsdep().get(i), demandedep);
				demandedep.getProductsdep().add(lc);
				
			}
		}
	}*/
	
	public void ChangeDemandeStatus(DemandeStatus status, long id) {
		DemandeDepense depense =demandeDepenseRepo.findById(id).get();
		depense.setStatus(status);
		demandeDepenseRepo.save(depense);
		
	}
	
	public DemandeDepense getById(long id) {
		return demandeDepenseRepo.findById(id).get();	}
	
	public byte[] processImage(InputStream inputStream) throws IOException {
		BufferedImage originalImage = ImageIO.read(inputStream);

        // Redimensionner l'image
        int newWidth = originalImage.getWidth() * 3;
        int newHeight = originalImage.getHeight() * 3;
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        
        BufferedImage thresholdedImage = applyThreshold(resizedImage, 70);
        BufferedImage finalImage = specifyResolution(thresholdedImage, 300);
      

        // Convertir l'image en tableau de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);
        return baos.toByteArray();
		
	}
	
	public DemandeDepense getDemandeFromImage(InputStream inputStream) throws TesseractException, IOException, InterruptedException {
		byte[] imageBytes=processImage(inputStream);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
		 
	       /* ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
	        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream); 
	        
		File image = new File("C:\\Users\\ASUS\\Desktop\\Image1.jpg");
		Tesseract tesseract = new Tesseract();
		/*tesseract.setDatapath("src/main/resources/tessdata");*/
		
        
        Tesseract tesseract = new Tesseract();
        tesseract.setLanguage("fra"); // Définir la langue sur le français

        // Optionnel : définir le chemin des données si nécessaire

        tesseract.setTessVariable("user_defined_dpi", "300");
		String result = tesseract.doOCR(bufferedImage);
		//getDemandeDetails(result);
		 return getDemandeDetails(result);
		
	}
	
    private BufferedImage applyThreshold(BufferedImage image, int percentage) {
        int threshold = (int) (percentage / 100.0 * 255);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int color = (image.getRGB(x, y) & 0xFF) > threshold ? 0xFFFFFF : 0x000000;
                result.setRGB(x, y, color);
            }
        }
        return result;
    }
    

    private BufferedImage specifyResolution(BufferedImage image, int dpi) {
        // Créer une nouvelle image avec les mêmes propriétés mais avec la résolution spécifiée
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g2d.dispose();

        // Spécifier la résolution DPI de l'image
        newImage = setDPI(newImage, dpi);

        return newImage;
    }
    
    private BufferedImage setDPI(BufferedImage image, int dpi) {
        // Créer une nouvelle image avec les mêmes dimensions mais avec les métadonnées de résolution DPI
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Créer les métadonnées d'image avec la résolution DPI spécifiée
        Image tempImage = newImage.getScaledInstance(newImage.getWidth(), newImage.getHeight(), Image.SCALE_SMOOTH);
        newImage = new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d1 = newImage.createGraphics();
        g2d1.drawImage(tempImage, 0, 0, null);
        g2d1.dispose();
        return newImage;
    }

    private DemandeDepense getDemandeDetails(String text) {
    	 String[] lines = text.split("\\n");
    	 List<ProuduitQuantite> res=new ArrayList<ProuduitQuantite>();
    	 String fourname=getFournisseurLibelle(lines[0]);
    	 double montant=0;
    	 DemandeDepense demande=new DemandeDepense();
    	 for (int i = 0; i < lines.length; i++) {
    		 
    		   if(lines[i].contains("Total TTC")) {
              	 
              	 montant=getMontant(lines[i]);
    	 }}

         // Variable to track if we are capturing lines
         boolean captureLines = false;

         // Iterate through the lines
        for (int i = 0; i < lines.length; i++) {
        	
             // Check if the line contains "Description"
             if (lines[i].contains("Description")) {
                 captureLines = true; // Start capturing lines after this line
                 continue; // Skip processing this line
             }
             

             // Check if we should capture this line
             if (captureLines) {
                 // Check if the line contains "Total HT"
                 if (lines[i].contains("Total HT")) {
                     break; // Stop capturing lines once we find "Total HT"
                 }
                 
                 // Process or store the line as needed
                 
                 res.add(getProductElement(lines[i]));
             }
             
            
         }
         demande.setBudget(montant);
         String motif="";
         for (ProuduitQuantite prouduitQuantite : res) {
        	 System.out.println("------------------------>>"+prouduitQuantite.getLibelle().toLowerCase());
			if(prouduitQuantite.getLibelle().toLowerCase().equals("main-d'œuvre ".toLowerCase())) {
				if(motif=="") {
					motif+="Achat de "+prouduitQuantite.getQte()+" heures de main d'oeuvre ";
				}
				else {
					motif+="et "+prouduitQuantite.getQte()+" heures de main d'oeuvre ";
				}
			}
			else {
				if(motif=="") {
					motif+="Achat de "+prouduitQuantite.getQte()+" piéces de "+prouduitQuantite.getLibelle();
				}
				else {
					motif+="et "+prouduitQuantite.getQte()+" piéces de "+prouduitQuantite.getLibelle();
				}
			}
		}
         demande.setMotif(motif);
         Fournisseur four=fournisseurRepo.findByLibelle(fourname);
         demande.setFournisseur(four);
         return demande;
         
     }
    
    private ProuduitQuantite getProductElement(String line) {
    	ProuduitQuantite result=new ProuduitQuantite();
    	

        // Iterate through the line to find the index of the first number
        int firstNumberIndex = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (Character.isDigit(c)) {
                firstNumberIndex = i;
                break;
            }
        }

        // If a number was found, extract the number
        String number = "";
        if (firstNumberIndex != -1) {
            StringBuilder numberBuilder = new StringBuilder();
            for (int i = firstNumberIndex; i < line.length(); i++) {
                char c = line.charAt(i);
                if (Character.isDigit(c)) {
                    numberBuilder.append(c);
                } else {
                    break; // Stop if a non-digit character is encountered
                }
            }
            number = numberBuilder.toString();
        }
        int numberconv=Integer.parseInt(number);

        // Output the results
  
        result.setLibelle(line.substring(0, firstNumberIndex));
        result.setQte(numberconv);
        
      
        return result;
    
       }
    
    private String getFournisseurLibelle(String text) {
    	int index = text.indexOf("Devis");
    	String result="";
    	if(index != -1) {
    		 int endIndex = index - 1;
    		  if (endIndex >= 0) {
                  // Extract the substring before the space
                   result = text.substring(0, endIndex);
                  
              } else {
                  System.out.println("No text found before 'Devis'.");
              }
          } else {
              System.out.println("The word 'Devis' was not found in the string.");
           
    	}
    	return result;
    }
    
    private double getMontant(String text) {
    	double result=0;
    	String word="Total TTC";
    	int index = text.indexOf(word);
    	 String resulttextbefore="";
         String resulttextafter="";
         String resulttext="";
        if (index != -1) {
            // Calculate the starting index for the substring
            int startIndex = index + word.length() + 1;

            // Ensure that the startIndex is within the string bounds
            if (startIndex < text.length()) {
                // Extract the substring from the startIndex to the end of the string
                 resulttext = text.substring(startIndex);
                
                if(resulttext.indexOf(",") != -1) {
                 resulttextbefore=resulttext.substring(0,resulttext.indexOf(","));
                 resulttextafter=resulttext.substring(resulttext.indexOf(",")+1);
                }
            } else {
                System.out.println("No text found after 'Total TTC'.");
            }
        } else {
            System.out.println("The word 'Total TTC' was not found in the string.");
        }
        
        String regex = "\\d+";

        // Compile the regular expression
        Pattern pattern = Pattern.compile(regex);
        if(resulttext.indexOf(",") != -1) {
        Matcher matcher = pattern.matcher(resulttextbefore);
        Matcher matcher1 = pattern.matcher(resulttextafter);

        // List to hold all digit substrings
        List<String> digitStrings = new ArrayList<>();
        List<String> digitStrings1 = new ArrayList<>();
        while (matcher.find()) {
            digitStrings.add(matcher.group());
        }
        while (matcher1.find()) {
            digitStrings1.add(matcher1.group());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : digitStrings) {
            stringBuilder.append(str);
        }
        
        StringBuilder stringBuilder1 = new StringBuilder();
        for (String str : digitStrings1) {
        	stringBuilder1.append(str);
        }
        String resultstring = stringBuilder.toString();
        resultstring +="."+ stringBuilder1.toString();
        result=Double.parseDouble(resultstring);
      
        }
        else {
        	Matcher matcher = pattern.matcher(resulttext);
           

            // List to hold all digit substrings
            List<String> digitStrings = new ArrayList<>();
            
            while (matcher.find()) {
                digitStrings.add(matcher.group());
            }
         
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : digitStrings) {
                stringBuilder.append(str);
            }
            
          
            String resultstring = stringBuilder.toString();
            
            result=Double.parseDouble(resultstring);
            // Print all the digit substrings
            
        }
        return result;

    }
    
    @Transactional
    public DemandeDepense CreateDemande(PersonalDemandeDep pdem) {
    	
    	DemandeDepense dem=pdem.getDemande();
    	Personel per=personelRepo.findById(pdem.getId()).get();
    	Fournisseur four=fournisseurRepo.findById(pdem.getIdf()).get();
    	
    	dem.setFournisseur(four);
    	dem.setDate(new Date());
    	dem.setStatus(DemandeStatus.En_Attente);
    	dem.setUserde(per);
    	dem=demandeDepenseRepo.save(dem);
    	four.getDemandefour().add(dem);
    	fournisseurRepo.save(four);
    	per.getDemandes().add(dem);
    	personelRepo.save(per);
    	
    	
    	return dem;
    }
    
    public Page<DemandeDepense> findPaginated(int pageNum, int pageSize,List<DemandeDepense> demandes){
  	  
    	Pageable pageable = PageRequest.of(pageNum-1, pageSize);
    	int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), demandes.size());

        List<DemandeDepense> sublist = demandes.subList(start, end);

    	
    	
    	return new PageImpl<DemandeDepense>(sublist, pageable, demandes.size());
    }
    
    public List<DemandeDepense> getEnattenteDemande(){
    	return demandeDepenseRepo.getDemandeEnAttente();
    }
    
    public List<DemandeDepense> search(String search){
    	List<DemandeDepense> result= new ArrayList<DemandeDepense>();
    	for (DemandeDepense dem : getEnattenteDemande()) {
			if(dem.getMotif().contains(search) || String.valueOf(dem.getBudget()).contains(search)
					|| String.valueOf(dem.getDate()).contains(search) || dem.getCat().toString().contains(search)) {
				result.add(dem);
			}
		}
    	Collections.sort(result, Comparator.comparing(DemandeDepense::getDate).reversed());
    	return result;
    	
    			
    }
    
    public List<DemandeDepense> acceptDemande(long id){
    	DemandeDepense dem=demandeDepenseRepo.findById(id).get();
    	BonCommande bc=new BonCommande();
    	bc.setDate(new Date());
    	bc.setDescription(dem.getMotif());
    	bc.setMontant(dem.getBudget());
    	bc.setStatus(BCStatus.En_Attente);
    	bc.setDdepense(dem);
    	bc=bcrepo.save(bc);
    	dem.setStatus(DemandeStatus.Acceptee);
    	dem.setBc(bc);
    	demandeDepenseRepo.save(dem);
    	
    	
    	return getEnattenteDemande();
    }
    
    public List<DemandeDepense> refuseDemande(long id){
    	DemandeDepense dem=demandeDepenseRepo.findById(id).get();
    	dem.setStatus(DemandeStatus.Refusee);
    	demandeDepenseRepo.save(dem);
    	return getEnattenteDemande();
    }
    
    public DemandeDepense saveDem(DemandeDepense demm) {
    	return demandeDepenseRepo.save(demm);
    }
    }
    


