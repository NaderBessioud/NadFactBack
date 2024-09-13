package tn.famytech.esprit.Services;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;


import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import java.security.cert.Certificate;
import java.sql.Blob;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hellosign.sdk.HelloSignClient;
import com.hellosign.sdk.HelloSignException;
import com.hellosign.sdk.resource.EmbeddedRequest;
import com.hellosign.sdk.resource.EmbeddedResponse;
import com.hellosign.sdk.resource.Event;
import com.hellosign.sdk.resource.SignatureRequest;
import com.hellosign.sdk.resource.TemplateSignatureRequest;
import com.hellosign.sdk.resource.support.CustomField;
import com.hellosign.sdk.resource.support.Signature;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.itextpdf.text.pdf.security.PrivateKeySignature;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.Transient;
import jakarta.transaction.Transactional;
import tn.famytech.esprit.DTO.FactureDateAndList;
import tn.famytech.esprit.DTO.FactureDateAndNumber;
import tn.famytech.esprit.DTO.FactureListAndPDF;
import tn.famytech.esprit.DTO.FacturesStat;
import tn.famytech.esprit.DTO.TotalAndTva;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Client;

import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FacturePayementStatus;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.Reglement;
import tn.famytech.esprit.Entites.Servicefact;
import tn.famytech.esprit.Entites.TypeClient;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.TypePayment;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Repositories.AvoirRepo;
import tn.famytech.esprit.Repositories.ClientRepo;

import tn.famytech.esprit.Repositories.FactureRepo;
import tn.famytech.esprit.Repositories.PersonelRepo;
import tn.famytech.esprit.Repositories.ReglementRepo;
import tn.famytech.esprit.Repositories.ServicefactRepo;
import tn.famytech.esprit.Repositories.UserRepository;
import java.io.OutputStream;




@Service
public class FactureService {
	
	 @Autowired
     private Environment env;
	@Autowired
	private FactureRepo factureRepo;
	@Autowired
	private AvoirRepo avoirRepo;
	
	
	
	@Autowired
	private ClientRepo clientRepo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ServicefactRepo produitrepo;
	
	@Autowired
	private PersonelRepo personelRepo;
	
	@Autowired
	private ReglementRepo reglementRepo;
	
	
	
	
	@Autowired
	private LigneCommandeService commandeService;
	
	
	
	
	
	@Autowired
	private  JavaMailSender mailSender;
	
	@Autowired
    private TemplateEngine templateEngine;
	
	private final String bqe="123";
	private final String iban="123";
	private final String identifiantf="123";
	private final String ribeuro="123";
	private final String ribtnd="123";
	private final String siegesocial="mourouj";
	private final Logger logger = LoggerFactory.getLogger(FactureService.class);
	private static final String QR_CODE_IMAGE_PATH = "C:\\Users\\ASUS\\Desktop\\test\\QRCode.png";
	private static final String[] units = { "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf" };
    private static final String[] dizaines = {"dix", "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf" };
    private static final String[] dizaines1 = { "", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingts", "quatre-vingt-dix" };
    private static final String[] quatreVingtDix = {"", "quatre-vingt-onze", "quatre-vingt-douze", "quatre-vingt-treize", "quatre-vingt-quatorze", "quatre-vingt-quinze", "quatre-vingt-seize", "quatre-vingt-dix-sept", "quatre-vingt-dix-huit", "quatre-vingt-dix-neuf"};
    private static final String[] soixanteDix = {"", "soixante et onze", "soixante-douze", "soixante-treize", "soixante-quatorze", "soixante-quinze", "soixante-seize", "soixante-dix-sept", "soixante-dix-huit", "soixante-dix-neuf"};
    private static final String[] milliers = { "", "mille", "million", "milliard" };
	
	
	public Facture getFactureById(long id) {
		return factureRepo.findById(id).orElse(null);
	}
	public Facture getbyNumberAndStatus(long number,FactureStatus status) {
		return factureRepo.findByNumberAndStatus(number, status);
	}
		public Image downloadAndUseImage() {
	  
	   
	    String localFilePath = "/opt/images/famytech.png"; // Adjust the path as needed

	    FTPClient ftpClient = new FTPClient();

	    try {
	    	 ftpClient.connect("192.168.1.31", 21);
	         ftpClient.login("ftp-user", "ftpuser");
	        ftpClient.enterLocalPassiveMode();

	        try (OutputStream outputStream = new FileOutputStream(localFilePath)) {
	            boolean success = ftpClient.retrieveFile("famytech.png", outputStream);
	            if (success) {
	                System.out.println("Image downloaded successfully.");
	            } else {
	                System.out.println("Failed to download the image.");
	            }
	        }

	        ftpClient.logout();
	        ftpClient.disconnect();

	        // Use the downloaded image in your PDF
	        return Image.getInstance(localFilePath);

	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}
	
	public String getValidDate(String datest) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			 LocalDateTime date = LocalDateTime.parse(datest+"T14:30:00", formatter2);
			 Calendar calendar = Calendar.getInstance();
		        int currentYear = calendar.get(Calendar.YEAR);
			 if(factureRepo.getCanceledInvoice(currentYear).size() != 0) {
				 Instant instant = factureRepo.getCanceledInvoice(currentYear).get(0).getDateemission().toInstant();
				 date=LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
				 
 	            return sdf.format(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
			 }
			 else {
			 
			 List<String> dates = new ArrayList<>();
    	 
    	 
    	
    		  try {
    	            URL url = new URL("https://api.api-ninjas.com/v1/holidays?country=TN&year=2024&type=major_holiday");
    	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	            connection.setRequestProperty("accept", "application/json");
    	            connection.setRequestProperty("X-Api-Key", "TmpqTMeHkp6FB3COUmM8DA==YGxONn8wg28UlkEA");
    	            InputStream responseStream = connection.getInputStream();

    	            ObjectMapper mapper = new ObjectMapper();
    	            JsonNode root = mapper.readTree(responseStream);

    	            
    	            for (JsonNode holiday : root) {
    	                String name = holiday.get("name").asText();
    	                if (!name.equalsIgnoreCase("Muharram (Tentative Date)")) {
    	                    String eventdate = holiday.get("date").asText();
    	                    dates.add(eventdate);
    	                }
    	            }

    	         

    	            connection.disconnect();
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    	            String formattedDate = date.format(formatter);
    	           
    	            while(dates.contains(formattedDate) && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
    	           	 
    	           	 String formattedDate1 = date.format(formatter1);
    	           	 date = LocalDateTime.parse(formattedDate1);
    	           	 date = date.plusDays(1);
    	           	 formattedDate = date.format(formatter);
    	           	 System.out.println("haw data ---->"+formattedDate);
    	           	 
    	            }
    	            Date finalDate=new Date();
    	            try {
    	           	 
    	           	  finalDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    	   		} catch (Exception ex) {
    	   			// TODO Auto-generated catch block
    	   			ex.printStackTrace();
    	   		}
    	            
    	            return sdf.format(finalDate);
    	            
			}
    	           
    	        
    	    }
    	 


	@Transactional
	public boolean AddFacture(Facture f) {
		
		if(checkIfExist(f)) {
			return true;
		}
		else {
			
		
	 	 f=setProformaNumber(f);
	 	 f.setTraited(false);
	 	 f.setStatus(FactureStatus.Proforma);
	 	 f.setPayementstatus(FacturePayementStatus.Non_Paye);
	 	 f.setRetenueaff(false);
	 	 f.setRetenuepath("");
		
		
		Client c=clientRepo.findById(f.getClient().getIdU()).orElse(null);
		c.getFactures().add(f);
		clientRepo.save(c);
		f.setClient(c);
		if(c.getType() == TypeClient.International) {
			f.setTva(0);
			f.setCtvtimbre(0);
			f.setType(TypeFacture.Export);
		}
		
		else {
			f.setType(TypeFacture.National);
		}
		
		Date today = new Date();
		if(c.getExonere()==true && (c.getDateexonere().before(today) ||  c.getDateexonere().compareTo(today) ==0)){
			f.setTva(0);
			f.setCtvtimbre(0);
		}
		
	    
	    Personel user = personelRepo.findById(f.getUser().getIdU()).get();
	    user.getFacturesuser().add(f);
	    personelRepo.save(user);
	    f.setUser(user);
	    for (int i = 0; i < f.getCommandes().size(); i++) {
	    	Lignecommande lc=f.getCommandes().get(i);
	    	
	    	Lignecommande lc1=commandeService.AddLigneCommande(lc, f);
	    	f.getCommandes().set(i, lc1);
	    }
	   /* Iterator<Produit> iterator = f.getProduits().iterator();
	    while (iterator.hasNext()) {
	        Produit p = iterator.next();
	        iterator.remove(); 
	        Produit newProduit = produitService.AddProduit(p, f);
	        f.getProduits().add(newProduit);
	    }*/
		/*for (Produit p : f.getProduits()) {
			f.getProduits().remove(p);
			f.getProduits().add(produitService.AddProduit(p, f));
		}*/
		
		
		f.setTotalht(TotalHT(f));
		f.setTotalttc(CalculTotalTTC(f));
		f.setTotalrestant(f.getTotalttc());
		f.setRetenue(CalculRetunue(CalculTotalTTC(f)-f.getCtvtimbre()));
		f.setCtvht(CalculTotalHTConv(f));
		f.setCtvttc(CalculTotatlTTCConv(f));
		f.setCtvtva(f.getTva());
		f.setMontantlettres(TotalEnLettre(f.getTotalttc(),f.getType()));
		
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			f.setDateemission(dateFormat.parse(getValidDate(dateFormat.format(new Date()))));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 factureRepo.save(f);
		}
		return false;
		
	}

	@Transactional
	
	public Facture UpdateFacture(Facture f) {
		
		Facture facture = factureRepo.findById(f.getIdF()).get();
		Client c =clientRepo.findById(f.getClient().getIdU()).get();
		facture.setStatus(FactureStatus.Proforma);
		
		if(c.getType() == TypeClient.International) {
			f.setTva(0);
			f.setCtvtimbre(0);
			f.setType(TypeFacture.Export);
		}
		
		else {
			f.setType(TypeFacture.National);
		}
		if(facture.getType() == TypeFacture.Export) {
			facture.setDatecv(f.getDatecv());
			facture.setCours(f.getCours());
			facture.setTva(0);
			facture.setCtvtimbre(0);
		}
		else {
			facture.setTva(f.getTva());
			facture.setCtvtimbre(f.getCtvtimbre());
		}
		facture.setArchived(f.isArchived());
		for(int i=0;i<facture.getCommandes().size();i++) {
			if(f.getCommandes().contains(facture.getCommandes().get(i))) {
				commandeService.deleteLigneCommande(facture.getCommandes().get(i).getIdLC(), facture.getIdF());
				facture.getCommandes().remove(i);
			}
		}
		
		
		
		for(int i=0;i<f.getCommandes().size();i++) {
			
			if(f.getCommandes().get(i).getIdLC() != 0) {
				Lignecommande lc=commandeService.updateLigneCommande(f.getCommandes().get(i), facture.getIdF());
				facture.getCommandes().add(lc);
			}
			else {
				Lignecommande lc=commandeService.AddLigneCommande(f.getCommandes().get(i), facture);
				facture.getCommandes().add(lc);
				
			}
		}
		
		
	
		
		facture.setTotalht(TotalHT(f));
		facture.setTotalttc(CalculTotalTTC(f));
		facture.setTotalrestant(f.getTotalttc());
		facture.setRetenue(CalculRetunue(CalculTotalTTC(f)-f.getCtvtimbre()));
		facture.setCtvht(CalculTotalHTConv(f));
		facture.setCtvttc(CalculTotatlTTCConv(f));
		facture.setCtvtva(f.getTva());
		facture.setMontantlettres(TotalEnLettre(f.getTotalttc(),f.getType()));
		
		return factureRepo.save(facture);
	}
	
	public String TotalEnLettre(double total,TypeFacture type) {
		String currency="euros";
		String currency1="centimes";
		if(type == TypeFacture.National) {
			currency="dinars";
			currency1="millimes";
		}
		
		 BigDecimal totalBigDecimal = BigDecimal.valueOf(total);
	        totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	        String numberString = totalBigDecimal.toPlainString();
		
		String[] parts = numberString.split("\\.");
		String words = convertToWords((int) total) + " " + currency +" ";
		if(parts.length>1 && Integer.parseInt(parts[1]) != 0)
			words = words + convertToWordsLessThanThousand(Integer.parseInt(parts[1])) + currency1;
		
	
		return words;
	}
	public  String convertToWords(int number) {
        if (number == 0) {
            return "Zero";
        }

        String words = "";
        for (int i = 0; number > 0; i++) {
            if (number % 1000 != 0) {
                words = convertToWordsLessThanThousand(number % 1000) + milliers[i] + " " + words;
            }
            number /= 1000;
        }

        return words.trim();
    }
	
	private  String convertToWordsLessThanThousand(int number) {
	    	if (number == 0) {
	            return "";
	        } else if (number < 10) {
	            return units[number] + " ";
	        } else if (number < 20) {
	            return dizaines[number - 10] + " ";
	        } else if (number < 100) {
	        	if(number / 10 == 9) {
	        		return quatreVingtDix[number % 10] + " ";
	        	}
	        	else if(number / 10 == 7) {
	        		return soixanteDix[number % 10] + " ";
	        	}
	        	
	            return dizaines1[number / 10] + " " + units[number % 10] + " ";
	        } else {
	            return units[number / 100] + " cent " + convertToWordsLessThanThousand(number % 100);
	        }
	    }
	
	
	public List<Facture> SignFactureupdate(long id,String pdfname,List<Facture> factures) {
		List<Facture> result=new ArrayList<Facture>();
		List<Facture> resultProforma=new ArrayList<Facture>();
		
		Facture facture = factureRepo.findById(id).get();
		facture.setSigned(true);
		facture.setPdfname(pdfname);
		facture.setStatus(FactureStatus.Facture_valide);

		facture= factureRepo.save(facture);
		
		for(int i=0;i<factures.size();i++) {
			if(factures.get(i).getIdF()==facture.getIdF()) {
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
	
	public List<Facture> ArchiveFactureupdate(long id, List<Facture> factures) {
		System.out.println("-----"+factures);
		Facture facture = factureRepo.findById(id).get();
		facture.setArchived(true);
		factureRepo.save(facture);
		Iterator<Facture> iterator = factures.iterator();
		while (iterator.hasNext()) {
		    Facture f = iterator.next();
		    if (f.getIdF() == id) {
		        iterator.remove();
		    }
		}
		return factures;
	}

	
	public List<Facture> DeleteFacture(Long id,List<Facture> factures) {
		
		Facture f= factureRepo.findById(id).orElse(null);
		Iterator<Facture> iterator = factures.iterator();
		while (iterator.hasNext()) {
			Facture facture= iterator.next();
			if(facture.getIdF() == id) {
				iterator.remove();	
			}
		     
		}
		Iterator<Lignecommande> iterator1 = f.getCommandes().iterator();
		while (iterator.hasNext()) {
			Lignecommande lc = iterator1.next();
		    // Perform your logic here
		    iterator1.remove(); // Safely remove the current element
		    commandeService.deleteLigneCommande(lc.getIdLC(), f.getIdF());
		}
		
		factureRepo.delete(f);
		return factures;
	}

	
	public List<Facture> DisplayFactures() {
		
		return (List<Facture>) factureRepo.findAll();
	}
	
	public List<Facture> DisplayFacturesNotArchived() {
		List<Facture> factures= factureRepo.findFacturesByArchived(false);
		
		List<Facture> result =new ArrayList<Facture>();
		List<Facture> resultProformat =new ArrayList<Facture>();
		
		for (Facture facture : factures) {
			if(facture.getStatus()==FactureStatus.Facture || facture.getStatus()==FactureStatus.Facture_envoye || facture.getStatus()==FactureStatus.Facture_valide) {
				result.add(facture);
			}
			else {
				resultProformat.add(facture);
			}
			
		}
		Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
		
		Collections.sort(resultProformat, Comparator.comparing(Facture::getNumber));
		
	
		
		for (int i = 0; i < resultProformat.size(); i++) {
		    
		 result.add(0,resultProformat.get(i) );
		}
		
		
		return result;
	}
	
	public List<Facture> DisplayInvoiceNotArchivedAndNotPayed(){
		List<Facture> result=DisplayFacturesNotArchived();
		Iterator<Facture> iterator = result.iterator();
		while (iterator.hasNext()) {
            Facture facture = iterator.next();
            if(facture.getPayementstatus() == FacturePayementStatus.Paye  || facture.getStatus() != FactureStatus.Facture_envoye)  {
            	iterator.remove();
            }
          
        }
		Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
		return result;
	}
	
	
	public List<Facture> DisplayInvoiceNotArchivedAndNotPayedByClient(String lib){
		Client c=clientRepo.findByLibelle(lib);
		List<Facture> result=new ArrayList<Facture>();
		for (Facture facture : DisplayFacturesNotArchived()) {
			if(facture.getStatus()==FactureStatus.Facture_envoye && facture.getPayementstatus() != FacturePayementStatus.Paye && facture.getClient().getIdU()==c.getIdU()) {
				result.add(facture);
			}
		}
		Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
		return result;
	}
	
	public long setFactureNumber() {
		Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        long number=0;
        Optional<Long> firstElement1 = factureRepo.findTheGapNumber(currentYear).isEmpty() ? Optional.empty() : Optional.of(factureRepo.findTheGapNumber(currentYear).get(0));
        Optional<Long> firstElement2 = avoirRepo.findTheGapNumber(currentYear).isEmpty() ? Optional.empty() : Optional.of(avoirRepo.findTheGapNumber(currentYear).get(0));
      Long number1= Stream.of(firstElement1, firstElement2)
        .flatMap(Optional::stream)
        .max(Long::compare)
        .orElse(null);
        if(number1 != null ) {
        	number =number1+1;
        	
        }
        else {
        	if(avoirRepo.Avoirnumber(currentYear) != null) {
        		number=Math.max(factureRepo.countByDateemissionAndType(currentYear,false)+1, avoirRepo.Avoirnumber(currentYear)+1);
        	}
        	else {
        		number=factureRepo.countByDateemissionAndType(currentYear,false)+1;
        	}
        }
       
		return number;
	}
	
	public Facture setProformaNumber(Facture f) {
		Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if(factureRepo.countProformaByDateemissionAndType(currentYear,false) != null) {
        	 f.setNumber(factureRepo.countProformaByDateemissionAndType(currentYear,false)+1);
        	 return f;
        }
       f.setNumber(1);
		return f;
	}
	
	private double CalculRetunue(double ttc) {
		return ttc * 3 / 100;
	}
	
	public double TotalHT(Facture F) {
		System.out.println("hadha type mtaa facture "+F.getType());
		
		double result = 0;
		for (Lignecommande lc : F.getCommandes()) {
			if(F.getType()==TypeFacture.National) {
				result+= lc.getProduit().getPutnd()*lc.getQte();
				System.out.println("hadha qte mtaa facture "+lc.getQte());
				System.out.println("hadha prix mtaa facture "+lc.getProduit().getPutnd());
				System.out.println("hadha result pe mtaa facture "+result);
			}
			else {
				result+= lc.getProduit().getPueuro()*lc.getQte();
				
			}
			
		}
		
		
		return result;
	}
	
	public double CalculTotalTTC(Facture f) {


		double ttc=(TotalHT(f)*(1+(f.getTva()/100)))+f.getCtvtimbre();
		
		return ttc-CalculRetunue(ttc-f.getCtvtimbre());
	}
	
	public double CalculTotalHTConv(Facture f) {
		return TotalHT(f)*f.getCours();
	}
	
	public double CalculTotatlTTCConv(Facture f) {
		return (CalculTotalHTConv(f)*(1+(f.getCtvtva()/100)))+f.getCtvtimbre();
	}
	
	
    public ByteArrayOutputStream generateFacture(Facture f) throws DocumentException, IOException {
    	boolean remp=false;
    
    	if(f.getRef() !=0) {
    		remp=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).size()!=0;
    	}
    	Client c =clientRepo.findById(f.getClient().getIdU()).get();
    	if(c.getType()==TypeClient.International) {
    		f.setType(TypeFacture.Export);
    	}
    	else {
    		f.setType(TypeFacture.National);
    	}
    	Date today = new Date();
		if(c.getExonere()==true && (c.getDateexonere().after(today) ||  c.getDateexonere().compareTo(today) ==0)){
			f.setTva(0);
			f.setCtvtimbre(0);
		}
    	String numberString ="";
        BigDecimal totalBigDecimal;
   	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
   	Calendar calendar = Calendar.getInstance();
    int currentYear = calendar.get(Calendar.YEAR);	
	f.setCtvtva(f.getTva());	
	f.setClient(clientRepo.findById(f.getClient().getIdU()).get());   
   	if (f.getNumber() ==0)
   		f=setProformaNumber(f);
   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
   	if(f.getDateemission() == null) {
	try {
		f.setDateemission(dateFormat.parse(getValidDate(dateFormat.format(new Date()))));
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
}
   	}

           // Initialize Document and PdfWriter
           Document document = new Document();
           PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);

           // Open the document
           document.open();

           PdfContentByte contentByte = pdfWriter.getDirectContent();

           // Create a rectangle
           Rectangle rectangle = new Rectangle(80, 800, 500, 770);
           rectangle.setBorder(Rectangle.BOX);
           rectangle.setBorderWidth(1);

           // Add the rectangle to the document
           contentByte.rectangle(rectangle);

           // Move to the center of the rectangle for adding text
           contentByte.moveTo((rectangle.getLeft() + rectangle.getRight()) / 2, (rectangle.getBottom() + rectangle.getTop()) / 2);

           // Add content to the document
           Font font = FontFactory.getFont(FontFactory.COURIER, 16,Font.BOLD, BaseColor.BLACK);

           // Create a Paragraph with centered text
           String type="Proforma";
           if(f.getStatus()==FactureStatus.Facture || f.getStatus()==FactureStatus.Facture_envoye || f.getStatus()==FactureStatus.Facture_valide) {
        	   type="Facture";
           }
           Paragraph paragraph = new Paragraph(type+" N "+f.getNumber()+"/"+currentYear, font);
          
           paragraph.setAlignment(Element.ALIGN_CENTER);

           // Add the Paragraph to the document
           document.add(paragraph);
           document.add(Chunk.NEWLINE);
           if(remp ==true) {
        	   Facture fr=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).get(0);
        	   Paragraph paragraphAv = new Paragraph("Facture de remplacement de la facture N:"+fr.getNumber()+"/"+currentYear+" "+dateFormat.format(fr.getDateemission()));
		          
	           

	           // Add the Paragraph to the document
	           document.add(paragraphAv);
	           document.add(Chunk.NEWLINE);
           }
           
           Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

           // Create a Paragraph with centered text
           Paragraph paragraph1 = new Paragraph("date d'emission  "+dateFormat.format(f.getDateemission()), fontd);
          
           paragraph1.setAlignment(Element.ALIGN_RIGHT);

           // Add the Paragraph to the document
           document.add(paragraph1);
           
           Paragraph space = new Paragraph(); // Adjust the spacing as needed
           space.setSpacingAfter(10f);
           document.add(space);

           PdfPTable table = new PdfPTable(2); // 2 columns
           table.setWidthPercentage(100);

           // Add image to the first cell
           
           com.itextpdf.text.Image image = downloadAndUseImage(); 
           image.scaleAbsoluteWidth(230f);
           image.scaleAbsoluteHeight(100f);
         
           PdfPCell imageCell = new PdfPCell(image);
           imageCell.setBorderWidth(1f);
           table.addCell(imageCell);

           // Add text to the second cell
           PdfPCell textCell = new PdfPCell(new Phrase("Identification fiscale: "+identifiantf+"\n"
           		+ "RIB TND : "+ribtnd+"\nRIB EURO : "+ribeuro+"\nIBAN :"+iban+
           		"\nBQE :"+bqe+"\nagence :mourouj\nSiege social :"+siegesocial));
           textCell.setBorderWidth(1f);
           table.addCell(textCell);
           

           float[] columnWidths = {200f, 250f}; // Change these values as needed

           table.setWidths(columnWidths);
           table.setSpacingAfter(40f);
           

           document.add(table);
           // Close the document
           
          
           
           PdfPTable tableClient = new PdfPTable(2); // 2 columns
           tableClient.setWidthPercentage(100);
           PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal"));
           ClienInfo.setBorderWidth(1f);
           tableClient.addCell(ClienInfo);
           
           PdfPCell ClienInfo1 = new PdfPCell(new Phrase(f.getClient().getLibelle()+"\n"
           		+f.getClient().getAddresse()+"\n"+f.getClient().getIdfiscal()));
           ClienInfo1.setBorderWidth(1f);
           tableClient.addCell(ClienInfo1);
           
           PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
           ClienInfo2.setBorderWidth(1f);
           tableClient.addCell(ClienInfo2);
          
           PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(f.getRef())));
           ClienInfo3.setBorderWidth(1f);
           tableClient.addCell(ClienInfo3);
           tableClient.setSpacingAfter(40f);
           document.add(tableClient);

           Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
           PdfPTable tableDetails = new PdfPTable(4); // 2 columns
           tableDetails.setWidthPercentage(100);
           PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
          
           Details1.setBorderWidth(1f);
           tableDetails.addCell(Details1);
           
           PdfPCell Details2 = new PdfPCell(new Phrase("Nb jours",fontZ2));
           Details2.setHorizontalAlignment(Element.ALIGN_CENTER);
           Details2.setBorderWidth(1f);
           tableDetails.addCell(Details2);
           PdfPCell Details3 = new PdfPCell(new Phrase("PU DT",fontZ2));
           Details3.setHorizontalAlignment(Element.ALIGN_CENTER);
           Details3.setBorderWidth(1f);
           tableDetails.addCell(Details3);
           PdfPCell Total = new PdfPCell(new Phrase("Total DT",fontZ2));
           Total.setHorizontalAlignment(Element.ALIGN_CENTER);
           Total.setBorderWidth(1f);
           tableDetails.addCell(Total);
           
           for (Lignecommande lc : f.getCommandes()) {
			
        	 	  PdfPCell Details11;
        	 	  

        	 	 
                  if(lc.getCommentaire().length() != 0) {
                  	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()+"("+lc.getCommentaire()+")"));
                  }
                    
                  else {
                	
                  	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()));
                  }
           Details11.setBorderWidth(1f);
           tableDetails.addCell(Details11);
           PdfPCell Details21 = new PdfPCell(new Phrase(String.valueOf(lc.getQte())));
          
           Details21.setBorderWidth(1f);
           tableDetails.addCell(Details21);
          
           totalBigDecimal = BigDecimal.valueOf(lc.getProduit().getPutnd());
        
            
           
           totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	         numberString = totalBigDecimal.toPlainString();
           PdfPCell Details31 = new PdfPCell(new Phrase(numberString));
           
           Details31.setBorderWidth(1f);
           tableDetails.addCell(Details31);
           
           totalBigDecimal = BigDecimal.valueOf((lc.getProduit().getPutnd()*lc.getQte()));
         
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
           Total1.setBorderWidth(1f);
           tableDetails.addCell(Total1);
           }

           
           float[] columnWidthsD = {250f, 50f,50f,50f}; // Change these values as needed

           tableDetails.setWidths(columnWidthsD);
           tableDetails.setSpacingAfter(20f);
           document.add(tableDetails);
           
           PdfPTable tableTotal = new PdfPTable(2); // 2 columns
           tableTotal.setWidthPercentage(100);
           PdfPCell TotalhtL = new PdfPCell(new Phrase("Total HT"));
           TotalhtL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TotalhtL.setBorderWidth(1f);
           tableTotal.addCell(TotalhtL);
           totalBigDecimal = BigDecimal.valueOf(TotalHT(f));
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell Totalht = new PdfPCell(new Phrase(numberString));
           
           Totalht.setBorderWidth(1f);
           tableTotal.addCell(Totalht);
           totalBigDecimal = BigDecimal.valueOf(f.getTva());
	       totalBigDecimal = totalBigDecimal.setScale(0, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           
           PdfPCell TVAL = new PdfPCell(new Phrase("TVA("+numberString+"%)"));
           TVAL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TVAL.setBorderWidth(1f);
           tableTotal.addCell(TVAL);
           totalBigDecimal = BigDecimal.valueOf((TotalHT(f)*(f.getTva()/100)));
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           
           PdfPCell TVA = new PdfPCell(new Phrase(numberString));
           TVA.setBorderWidth(1f);
           tableTotal.addCell(TVA);
           
           PdfPCell TimbreL = new PdfPCell(new Phrase("Timbre"));
           TimbreL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TimbreL.setBorderWidth(1f);
           tableTotal.addCell(TimbreL);
           totalBigDecimal = BigDecimal.valueOf(f.getCtvtimbre());
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell Timbre = new PdfPCell(new Phrase(numberString));
           Timbre.setBorderWidth(1f);
           tableTotal.addCell(Timbre);
           
           PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
           TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TotalTTCL.setBorderWidth(1f);
           tableTotal.addCell(TotalTTCL);
           totalBigDecimal = BigDecimal.valueOf(CalculTotalTTC(f));
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell TotalTTC = new PdfPCell(new Phrase(numberString));
           TotalTTC.setBorderWidth(1f);
           tableTotal.addCell(TotalTTC);
           
           float[] columnWidthsT = {300f, 100f}; // Change these values as needed

           tableTotal.setWidths(columnWidthsT);
           tableTotal.setSpacingAfter(20f);
           document.add(tableTotal);
           
           
           PdfPTable tableMontantL = new PdfPTable(1); // 2 columns
           tableMontantL.setWidthPercentage(100);
           Font font1 = FontFactory.getFont(FontFactory.TIMES_BOLD, 12,Font.BOLD, BaseColor.BLACK);
           PdfPCell MontantL = new PdfPCell();
           Paragraph paragraph2 = new Paragraph();
           paragraph2.add(new Phrase("Arrêtée la présente facture à un total de\n", font1));

           paragraph2.add(new Phrase(TotalEnLettre(CalculTotalTTC(f),f.getType())));
           MontantL.addElement(paragraph2);
           tableMontantL.addCell(MontantL);
           
         
           float[] columnWidthsML = {300f};
           tableMontantL.setWidths(columnWidthsML);
           tableMontantL.setSpacingAfter(30f);
           document.add(tableMontantL);
           document.close();

           // Optionally, save the ByteArrayOutputStream to a file
          /* FileOutputStream fileOutputStream = new FileOutputStream("iTextHelloWorld.pdf");
               outputStream.writeTo(fileOutputStream);*/
           
			
			 return outputStream;
   }

    public ByteArrayOutputStream generateFactureEuro(Facture f) throws DocumentException, IOException {
    	f.setTva(0);
    	f.setCtvtimbre(0);
    	String numberString ="";
        BigDecimal totalBigDecimal;
      	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      	boolean remp=false;
        
    	if(f.getRef() !=0) {
    		remp=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).size()!=0;
    	}
      	
      	Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        
        Client c=clientRepo.findById(f.getClient().getIdU()).get();
    	if(c.getType()==TypeClient.International) {
    		f.setType(TypeFacture.Export);
    	}
    	else {
    		f.setType(TypeFacture.National);
    	}
        f.setClient(clientRepo.findById(f.getClient().getIdU()).get());
		
        if (f.getNumber() ==0)
    		f=setProformaNumber(f);
       	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       	
       	if(f.getDateemission() == null) {	    
		try {
			f.setDateemission(dateFormat.parse(getValidDate(dateFormat.format(new Date()))));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	}
              // Initialize Document and PdfWriter
              Document document = new Document();
              PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);

              // Open the document
              document.open();

              PdfContentByte contentByte = pdfWriter.getDirectContent();

              // Create a rectangle
              Rectangle rectangle = new Rectangle(80, 800, 500, 770);
              rectangle.setBorder(Rectangle.BOX);
              rectangle.setBorderWidth(1);

              // Add the rectangle to the document
              contentByte.rectangle(rectangle);

              // Move to the center of the rectangle for adding text
              contentByte.moveTo((rectangle.getLeft() + rectangle.getRight()) / 2, (rectangle.getBottom() + rectangle.getTop()) / 2);

              // Add content to the document
              Font font = FontFactory.getFont(FontFactory.COURIER, 16,Font.BOLD, BaseColor.BLACK);

              // Create a Paragraph with centered text
              String type="Proforma";
              if(f.getStatus()==FactureStatus.Facture || f.getStatus()==FactureStatus.Facture_envoye || f.getStatus()==FactureStatus.Facture_valide) {
           	   type="Facture";
              }
              Paragraph paragraph = new Paragraph(type+" N "+f.getNumber()+"/"+currentYear, font);
             
              paragraph.setAlignment(Element.ALIGN_CENTER);

              // Add the Paragraph to the document
              document.add(paragraph);
              document.add(Chunk.NEWLINE);
              if(remp ==true) {
           	   Facture fr=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).get(0);
           	   Paragraph paragraphAv = new Paragraph("Facture de remplacement de la facture N:"+fr.getNumber()+"/"+currentYear+" "+dateFormat.format(fr.getDateemission()));
   		          
   	           

   	           // Add the Paragraph to the document
   	           document.add(paragraphAv);
   	           document.add(Chunk.NEWLINE);
              }
              
              Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

              // Create a Paragraph with centered text
              Paragraph paragraph1 = new Paragraph("date d'emission "+dateFormat.format(f.getDateemission()) , fontd);
             
              paragraph1.setAlignment(Element.ALIGN_RIGHT);

              // Add the Paragraph to the document
              document.add(paragraph1);
              
              Paragraph space = new Paragraph(); // Adjust the spacing as needed
              space.setSpacingAfter(10f);
              document.add(space);

              PdfPTable table = new PdfPTable(2); // 2 columns
              table.setWidthPercentage(100);

              // Add image to the first cell
              
              com.itextpdf.text.Image image = downloadAndUseImage();
              image.scaleAbsoluteWidth(230f);
              image.scaleAbsoluteHeight(100f);
            
              PdfPCell imageCell = new PdfPCell(image);
              imageCell.setBorderWidth(1f);
              table.addCell(imageCell);

              // Add text to the second cell
              PdfPCell textCell = new PdfPCell(new Phrase("Identification fiscale: "+identifiantf+"\n"
                 		+ "RIB TND : "+ribtnd+"\nRIB EURO : "+ribeuro+"\nIBAN :"+iban+
                 		"\nBQE :"+bqe+"\nagence :mourouj\nSiege social :"+siegesocial));
              textCell.setBorderWidth(1f);
              table.addCell(textCell);
              

              float[] columnWidths = {200f, 250f}; // Change these values as needed

              table.setWidths(columnWidths);
              table.setSpacingAfter(40f);
              

              document.add(table);
              // Close the document
              
             
              
              PdfPTable tableClient = new PdfPTable(2); // 2 columns
              tableClient.setWidthPercentage(100);
              PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal"));
              ClienInfo.setBorderWidth(1f);
              tableClient.addCell(ClienInfo);
              
              PdfPCell ClienInfo1 = new PdfPCell(new Phrase(f.getClient().getLibelle()+"\n"+f.getClient().getAddresse()+"\n"+f.getClient().getIdfiscal()));
              ClienInfo1.setBorderWidth(1f);
              tableClient.addCell(ClienInfo1);
              
              PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
              ClienInfo2.setBorderWidth(1f);
              tableClient.addCell(ClienInfo2);
             
              PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(f.getRef())));
              ClienInfo3.setBorderWidth(1f);
              tableClient.addCell(ClienInfo3);
              tableClient.setSpacingAfter(40f);
              document.add(tableClient);
              Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
              PdfPTable tableDetails = new PdfPTable(4); // 2 columns
              tableDetails.setWidthPercentage(100);
              PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
              
              Details1.setBorderWidth(1f);
              tableDetails.addCell(Details1);
              
              PdfPCell Details2 = new PdfPCell(new Phrase("Nb jours",fontZ2));
              Details2.setHorizontalAlignment(Element.ALIGN_CENTER);
              Details2.setBorderWidth(1f);
              tableDetails.addCell(Details2);
              PdfPCell Details3 = new PdfPCell(new Phrase("PU €",fontZ2));
              Details3.setHorizontalAlignment(Element.ALIGN_CENTER);
              Details3.setBorderWidth(1f);
              tableDetails.addCell(Details3);
              PdfPCell Total = new PdfPCell(new Phrase("Total € ",fontZ2));
              Total.setHorizontalAlignment(Element.ALIGN_CENTER);
              Total.setBorderWidth(1f);
              tableDetails.addCell(Total);
              
              for (Lignecommande lc : f.getCommandes()) {
   			
             	  PdfPCell Details11;
             	 if(lc.getCommentaire().length()  != 0) {
                  	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()+"("+lc.getCommentaire()+")"));
                  }
                    
                  else {
                  	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()));
                  }
              Details11.setBorderWidth(1f);
              tableDetails.addCell(Details11);
              PdfPCell Details21 = new PdfPCell(new Phrase(String.valueOf(lc.getQte())));
             
              Details21.setBorderWidth(1f);
              tableDetails.addCell(Details21);
              
              totalBigDecimal = BigDecimal.valueOf(lc.getProduit().getPueuro());
           
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Details31 = new PdfPCell(new Phrase(numberString));
              Details31.setBorderWidth(1f);
              tableDetails.addCell(Details31);
              totalBigDecimal = BigDecimal.valueOf((lc.getProduit().getPueuro()*lc.getQte()));
          
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
              Total1.setBorderWidth(1f);
              tableDetails.addCell(Total1);
              }
              float[] columnWidthsD = {250f, 50f,50f,50f}; // Change these values as needed

              tableDetails.setWidths(columnWidthsD);
              tableDetails.setSpacingAfter(20f);
              document.add(tableDetails);
              
              PdfPTable tableTotal = new PdfPTable(2); // 2 columns
              tableTotal.setWidthPercentage(100);
              PdfPCell TotalhtL = new PdfPCell(new Phrase("Total HT"));
              TotalhtL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TotalhtL.setBorderWidth(1f);
              tableTotal.addCell(TotalhtL);
              totalBigDecimal = BigDecimal.valueOf(TotalHT(f));
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Totalht = new PdfPCell(new Phrase(numberString));
              Totalht.setBorderWidth(1f);
              tableTotal.addCell(Totalht);
     totalBigDecimal = BigDecimal.valueOf(f.getTva());
              
              totalBigDecimal = totalBigDecimal.setScale(0, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              
              PdfPCell TVAL = new PdfPCell(new Phrase("TVA("+numberString+"%)"));
              TVAL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TVAL.setBorderWidth(1f);
              tableTotal.addCell(TVAL);
totalBigDecimal = BigDecimal.valueOf((TotalHT(f)*(f.getTva()/100)));
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell TVA = new PdfPCell(new Phrase(numberString));
              TVA.setBorderWidth(1f);
              tableTotal.addCell(TVA);
              
              PdfPCell TimbreL = new PdfPCell(new Phrase("Timbre"));
              TimbreL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TimbreL.setBorderWidth(1f);
              tableTotal.addCell(TimbreL);
totalBigDecimal = BigDecimal.valueOf(f.getCtvtimbre());
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Timbre = new PdfPCell(new Phrase(numberString));
              Timbre.setBorderWidth(1f);
              tableTotal.addCell(Timbre);
              
              PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
              TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TotalTTCL.setBorderWidth(1f);
              tableTotal.addCell(TotalTTCL);
totalBigDecimal = BigDecimal.valueOf(CalculTotalTTC(f));
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              
              PdfPCell TotalTTC = new PdfPCell(new Phrase(numberString));
              TotalTTC.setBorderWidth(1f);
              tableTotal.addCell(TotalTTC);
              
              float[] columnWidthsT = {300f, 100f}; // Change these values as needed

              tableTotal.setWidths(columnWidthsT);
              tableTotal.setSpacingAfter(10f);
              document.add(tableTotal);
              
              
              PdfPTable tableMontantL = new PdfPTable(1); // 2 columns
              tableMontantL.setWidthPercentage(100);
              Font font1 = FontFactory.getFont(FontFactory.TIMES_BOLD, 12,Font.BOLD, BaseColor.BLACK);
              PdfPCell MontantL = new PdfPCell();
              Paragraph paragraph2 = new Paragraph();
              paragraph2.add(new Phrase("Arrêtée la présente facture à un total de\n", font1));
              paragraph2.add(new Phrase(TotalEnLettre(CalculTotalTTC(f),f.getType())));
              MontantL.addElement(paragraph2);
              tableMontantL.addCell(MontantL);
              
            
              float[] columnWidthsML = {300f};
              tableMontantL.setWidths(columnWidthsML);
              tableMontantL.setSpacingAfter(30f);
              document.add(tableMontantL);
              
              
              PdfPTable tableZone = new PdfPTable(1); // 2 columns
              tableZone.setWidthPercentage(100);
              Font fontZ = FontFactory.getFont(FontFactory.TIMES_BOLD, 12,Font.BOLD, BaseColor.BLACK);
              PdfPCell Zone = new PdfPCell(new Phrase("Zone reservée comptabilité interne:", fontZ));
              
              tableZone.addCell(Zone);
              float[] columnWidthsZ = {300f};
              tableZone.setWidths(columnWidthsZ);
              tableZone.setSpacingAfter(5f);
              document.add(tableZone);
              
              PdfPTable tableTotalEuro = new PdfPTable(2); // 2 columns
              tableTotalEuro.setWidthPercentage(100);
              Font fontZ1 = FontFactory.getFont(FontFactory.COURIER, 10,Font.BOLD, BaseColor.BLACK);
              
              PdfPCell TotalL = new PdfPCell(new Phrase("Date contre valeur\r\r\nCours moyen Euros (archives BCT en date de facturation)\r\r\n"
              		+ "CTV HT\r\r\n"
              		+ "CTV TVA "+f.getCtvtva()+"%\r\r\n"
              		+ "CTV Timbre\r\r\n"
              		+ "CTV TTC TND",fontZ1));
              TotalL.setBorderWidth(1f);
              tableTotalEuro.addCell(TotalL);
              totalBigDecimal = BigDecimal.valueOf(f.getCours());
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
   	         
   	      BigDecimal totalBigDecimal1 = BigDecimal.valueOf(CalculTotalHTConv(f));
          
          totalBigDecimal1 = totalBigDecimal1.setScale(3, RoundingMode.DOWN);

	         String numberString1 = totalBigDecimal1.toPlainString();
	         
	         BigDecimal totalBigDecimal2 = BigDecimal.valueOf((CalculTotalHTConv(f)*(f.getCtvtva()/100)));
	          
	          totalBigDecimal2 = totalBigDecimal2.setScale(3, RoundingMode.DOWN);

		         String numberString2 = totalBigDecimal2.toPlainString();
		         
		         BigDecimal totalBigDecimal3 = BigDecimal.valueOf(f.getCtvtimbre());
		          
		          totalBigDecimal3 = totalBigDecimal3.setScale(3, RoundingMode.DOWN);

			         String numberString3 = totalBigDecimal3.toPlainString();
			         
			         BigDecimal totalBigDecimal4 = BigDecimal.valueOf(CalculTotatlTTCConv(f));
			          
			          totalBigDecimal4 = totalBigDecimal4.setScale(3, RoundingMode.DOWN);

				         String numberString4 = totalBigDecimal4.toPlainString();
              PdfPCell TotalHT = new PdfPCell(new Phrase(dateFormat.format(f.getDatecv())+"\r\r\n"+numberString+" TND"+"\r\r\n\n"+numberString1+" TND"+"\r\r\n"+
            numberString2+" TND"+"\r\r\n"+numberString3+" TND"+"\r\r\n"+numberString4+" TND",fontZ1));
              TotalHT.setBorderWidth(1f);
              tableTotalEuro.addCell(TotalHT);
              float[] columnWidthsT1 = {150f, 150f};
              tableTotalEuro.setWidths(columnWidthsT1);
              document.add(tableTotalEuro);
              document.close();

              // Optionally, save the ByteArrayOutputStream to a file
             /* FileOutputStream fileOutputStream = new FileOutputStream("iTextHelloWorld.pdf");
                  outputStream.writeTo(fileOutputStream);*/
              
   			
   			 return outputStream;
      }

    

    
    

   /* 
    public List<Facture> getFacturesByClient(long id){
    	Client c =clientRepo.findById(id).get();
    	return factureRepo.findByClient(c);
    }
    
    public List<Facture> getFacturesByType(TypeFacture type){
    	return factureRepo.findByType(type);
    }
    
    public List<Facture> getFacturesByDateEmissionAndClient(String startdate,String enddate,long c){
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	try {
			return factureRepo.findFacturesByDateEmissionRangeAndClient(dateFormat.parse(startdate), dateFormat.parse(enddate), c);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    	
    }*/
    private TypeFacture mapStringtoTypeFacture(String type) {
    	 if (type.equals("Export")) {
             return TypeFacture.Export;
         } else  {
             return TypeFacture.National;
         }
    }
    
    public List<Facture> filterFactuer(String start,String end,String type,long id,boolean archived) throws ParseException{
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
    	if(start=="") {
    		if(!type.equals("null") && id != 0) {
    			System.out.println("Here----");
    			return factureRepo.findByClientAndTypeAndArchived(clientRepo.findById(id).get(), mapStringtoTypeFacture(type),archived);
    		}
    		else if(!type.equals("null")) {
    			return factureRepo.findByTypeAndArchived(mapStringtoTypeFacture(type),archived);
    		}
    		else if(id != 0) {
    			
    			return factureRepo.findByClientAndArchived(clientRepo.findById(id).get(),archived);
    		}
    		else {
    			return factureRepo.findFacturesByArchived(archived);
    		}
    	}
    	
    	else if(type.equals("null") ) {
    		if(id != 0) {
    			return factureRepo.findFacturesByDateEmissionRangeAndClientAndArchived(dateFormat.parse(start), dateFormat.parse(end), id,archived);
    		}
    		else {
    			return factureRepo.findFacturesByDateEmissionRangeAndArchived(dateFormat.parse(start), dateFormat.parse(end),archived);
    		}
    	}
    	else {
    		if(id != 0) {
    			return factureRepo.findFacturesByDateEmissionRangeAndTypeAndClientAndArchived(dateFormat.parse(start), dateFormat.parse(end), mapStringtoTypeFacture(type),id,archived);
    		}
    		else {
    			return factureRepo.findFacturesByDateEmissionRangeAndTypeAndArchived(dateFormat.parse(start), dateFormat.parse(end), mapStringtoTypeFacture(type),archived);
    		}
    		
    	}
    }
    
    
    
    public List<Facture> filterSignedFactuers(List<Facture> facutres){
    	List<Facture> result = new ArrayList<Facture>();
    	for (Facture facture : facutres) {
			if(facture.isSigned() )
				result.add(facture);
		}
    	return result;
    }
    
    public List<Facture> filterNotSignedFactuers(List<Facture> facutres){
    	List<Facture> result = new ArrayList<Facture>();
    	for (Facture facture : facutres) {
			if(!facture.isSigned() )
				result.add(facture);
		}
    	return result;
    }
    
    public List<Facture> filterArchivedFactuers(List<Facture> facutres){
    	List<Facture> result = new ArrayList<Facture>();
    	for (Facture facture : facutres) {
			if(facture.isArchived()) {
				
				result.add(facture);
			}
				
		}
    	return result;
    }
    
    public List<Facture> SearchFactures(String search,List<Facture> factures){
    	List<Facture> result = new ArrayList<Facture>();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    	for (Facture f : factures) {
			if(String.valueOf(f.getNumber()).contains(search) ||  String.valueOf(f.getTotalttc()).contains(search) || 
					f.getClient().getLibelle().contains(search) || dateFormat.format(f.getDateemission()).contains(search))
					result.add(f);
		}
    	
    	return result;
    }
    
  
  
    
    public Page<Facture> findPaginated(int pageNum, int pageSize,List<Facture> factures){
    	  
    	Pageable pageable = PageRequest.of(pageNum-1, pageSize);
    	int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), factures.size());

        List<Facture> sublist = factures.subList(start, end);

    	
    	
    	return new PageImpl<Facture>(sublist, pageable, factures.size());
    }
    
    
    
    public  void generateQRCodeImage(long id, int width, int height)
            throws WriterException, IOException, DocumentException {
    	String URL ="http://192.168.1.22:8080/ERPPro/facturation/previewFacture/"+id;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        
        BitMatrix bitMatrix = qrCodeWriter.encode(URL, BarcodeFormat.QR_CODE, width, height);
       
        Path path = FileSystems.getDefault().getPath(QR_CODE_IMAGE_PATH);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

    }
    
public String hashString(String text) {
	
    
    try {
        // Create a MessageDigest instance for SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Update the digest with the input string bytes
        byte[] hashBytes = digest.digest(text.getBytes());

        // Convert the byte array to a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // Print the SHA-256 hash
        System.out.println("SHA-256 Hash: " + hexString.toString());
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
    return "";
}

	public void updateFacturePdfPath(long id,String pdfpath) {
		Facture f = factureRepo.findById(id).get();
		f.setPdfpath(pdfpath);
		factureRepo.save(f);
	}
    
    


    public  byte[] getQRCodeImage(long id, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        String URL ="http://192.168.1.22:8080/ERPPro/";
        String mapping ="facturation/previewFacture/"+id;
        updateFacturePdfPath(id, hashString(mapping));
        BitMatrix bitMatrix = qrCodeWriter.encode(URL+hashString(mapping), BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
     //   MatrixToImageConfig con = new MatrixToImageConfig( 0xFF000002 , 0xFFFFC041 ) ;
        MatrixToImageConfig con = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);


        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream,con);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }
    
   
  //qr code generator 
    public String getQRCode(Model model) throws com.google.zxing.WriterException{
        String medium="test";
        String content="famytech";

        byte[] image = new byte[0];
        try {

            // Generate and Return Qr Code in Byte Array
           // image = getQRCodeImage(medium,250,250);

            // Generate and Save Qr Code Image in static/image folder
          //  generateQRCodeImage(content,250,250,QR_CODE_IMAGE_PATH);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Convert Byte Array into Base64 Encode String
        String qrcode = Base64.getEncoder().encodeToString(image);

        model.addAttribute("medium",medium);
        model.addAttribute("github",content);
        model.addAttribute("qrcode",qrcode);

        return "qrcode";
    }
    
   
    
    
    
    
    public ByteArrayOutputStream SignNewFacture(Facture F) {
    	try {
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		
    		if(F.getType()==TypeFacture.Export)
    			outputStream=generateFactureEuro(F);
    		else
    			outputStream=generateFacture(F);
    		
    		 String keystorePath = "/opt/certificates/ca/keystore.p12";
             String keystorePassword = "ca_p12_password_springpro";
             KeyStore keystore = KeyStore.getInstance("PKCS12");
             keystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

             // Get the private key and certificate chain from the keystore
             String alias = (String) keystore.aliases().nextElement();
             PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
             Certificate[] chain = keystore.getCertificateChain(alias);

             // Create a reader to read the PDF template
             PdfReader reader = new PdfReader(outputStream.toByteArray());

             // Create a stamper to apply the signature
             ByteArrayOutputStream signedPdfStream = new ByteArrayOutputStream();
             PdfStamper stamper = PdfStamper.createSignature(reader, signedPdfStream, '\0');

             // Create appearance for signature
             float pageWidth = PageSize.A4.getWidth();
             float pageHeight = PageSize.A4.getHeight();

             // Rectangle dimensions (same as original code)
             float rectWidth = 144; // Width of the rectangle in points
             float rectHeight = 32; // Height of the rectangle in points

             // Calculate the coordinates of the lower left corner of the rectangle
             float rectX = pageWidth - rectWidth - 10; // 36 points margin from right edge
             float rectY = 36; // 36 points margin from bottom edge

             // Create the rectangle
             Rectangle rect = new Rectangle(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
             PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
             appearance.setReason("Facturation");
             appearance.setLocation("Tunisia");
             appearance.setVisibleSignature(rect, 1, "signature");
             ExternalDigest d;
             // Create the signature
             ExternalSignature es = new PrivateKeySignature(privateKey, "SHA-256", "BC");
             ExternalDigest digest = new BouncyCastleDigest();
             MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

             // Close the stamper and reader
             stamper.close();
             reader.close();   
             return signedPdfStream;
             } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public ByteArrayOutputStream generateFactureAfterSign(Facture f) throws DocumentException, IOException, WriterException {
    	
    	String numberString ="";
        BigDecimal totalBigDecimal;
   	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
   	boolean remp=false;
    
	if(f.getRef() !=0) {
		remp=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).size()!=0;
	} 
   	Calendar calendar = Calendar.getInstance();
    int currentYear = calendar.get(Calendar.YEAR);
 
	
	
	
	
	f.setCtvtva(f.getTva());
	
	f.setClient(clientRepo.findById(f.getClient().getIdU()).get());

   
   	if (f.getNumber() ==0)
   		f.setNumber(setFactureNumber());
   		
   	 
  
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


           // Initialize Document and PdfWriter
           Document document = new Document();
           PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);

           // Open the document
           document.open();

           PdfContentByte contentByte = pdfWriter.getDirectContent();

           // Create a rectangle
           Rectangle rectangle = new Rectangle(80, 800, 500, 770);
           rectangle.setBorder(Rectangle.BOX);
           rectangle.setBorderWidth(1);

           // Add the rectangle to the document
           contentByte.rectangle(rectangle);

           // Move to the center of the rectangle for adding text
           contentByte.moveTo((rectangle.getLeft() + rectangle.getRight()) / 2, (rectangle.getBottom() + rectangle.getTop()) / 2);

           // Add content to the document
           Font font = FontFactory.getFont(FontFactory.COURIER, 16,Font.BOLD, BaseColor.BLACK);

           // Create a Paragraph with centered text
           Paragraph paragraph = new Paragraph("Facture N "+f.getNumber()+"/"+currentYear, font);
          
           paragraph.setAlignment(Element.ALIGN_CENTER);

           // Add the Paragraph to the document
           document.add(paragraph);
           document.add(Chunk.NEWLINE);

if(remp ==true) {
        	   Facture fr=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).get(0);
        	   Paragraph paragraphAv = new Paragraph("Facture de remplacement de la facture N:"+fr.getNumber()+"/"+currentYear+" "+dateFormat.format(fr.getDateemission()));
		          
	           

	           // Add the Paragraph to the document
	           document.add(paragraphAv);
	           document.add(Chunk.NEWLINE);
           }
           
           Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

           // Create a Paragraph with centered text
           Paragraph paragraph1 = new Paragraph("date d'emission "+dateFormat.format(f.getDateemission()), fontd);
          
           paragraph1.setAlignment(Element.ALIGN_RIGHT);

           // Add the Paragraph to the document
           document.add(paragraph1);
           
           Paragraph space = new Paragraph(); // Adjust the spacing as needed
           space.setSpacingAfter(10f);
           document.add(space);

           PdfPTable table = new PdfPTable(2); // 2 columns
           table.setWidthPercentage(100);

           // Add image to the first cell
           
           com.itextpdf.text.Image image = downloadAndUseImage();
           image.scaleAbsoluteWidth(230f);
           image.scaleAbsoluteHeight(100f);
         
           PdfPCell imageCell = new PdfPCell(image);
           imageCell.setBorderWidth(1f);
           table.addCell(imageCell);

           // Add text to the second cell
           PdfPCell textCell = new PdfPCell(new Phrase("Identification fiscale: "+identifiantf+"\n"
           		+ "RIB TND : "+ribtnd+"\nRIB EURO : "+ribtnd+"\nIBAN :"+iban+
           		"\nBQE :"+bqe+"\nagence :mourouj\nSiege social :"+siegesocial));
           textCell.setBorderWidth(1f);
           table.addCell(textCell);
           

           float[] columnWidths = {200f, 250f}; // Change these values as needed

           table.setWidths(columnWidths);
           table.setSpacingAfter(40f);
           

           document.add(table);
           // Close the document
           
          
           
           PdfPTable tableClient = new PdfPTable(2); // 2 columns
           tableClient.setWidthPercentage(100);
           PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal"));
           ClienInfo.setBorderWidth(1f);
           tableClient.addCell(ClienInfo);
           
           PdfPCell ClienInfo1 = new PdfPCell(new Phrase(f.getClient().getLibelle()+"\n"
           		+f.getClient().getAddresse()+"\n"+f.getClient().getIdfiscal()));
           ClienInfo1.setBorderWidth(1f);
           tableClient.addCell(ClienInfo1);
            
           PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
           ClienInfo2.setBorderWidth(1f);
           tableClient.addCell(ClienInfo2);
           System.out.println("hadya red ===========>"+f.getRef());
           PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(f.getRef())));
           ClienInfo3.setBorderWidth(1f);
           tableClient.addCell(ClienInfo3);
           tableClient.setSpacingAfter(40f);
           document.add(tableClient);

           Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
           PdfPTable tableDetails = new PdfPTable(4); // 2 columns
           tableDetails.setWidthPercentage(100);
           PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
          
           Details1.setBorderWidth(1f);
           tableDetails.addCell(Details1);
           
           PdfPCell Details2 = new PdfPCell(new Phrase("Nb jours",fontZ2));
           Details2.setHorizontalAlignment(Element.ALIGN_CENTER);
           Details2.setBorderWidth(1f);
           tableDetails.addCell(Details2);
           PdfPCell Details3 = new PdfPCell(new Phrase("PU DT",fontZ2));
           Details3.setHorizontalAlignment(Element.ALIGN_CENTER);
           Details3.setBorderWidth(1f);
           tableDetails.addCell(Details3);
           PdfPCell Total = new PdfPCell(new Phrase("Total DT",fontZ2));
           Total.setHorizontalAlignment(Element.ALIGN_CENTER);
           Total.setBorderWidth(1f);
           tableDetails.addCell(Total);
           
           for (Lignecommande lc : f.getCommandes()) {
			
        	 	  PdfPCell Details11;
        	 	 if(lc.getCommentaire().length()  != 0) {
                  	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()+"("+lc.getCommentaire()+")"));
                  }
                    
                  else {
                  	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()));
                  }
           Details11.setBorderWidth(1f);
           tableDetails.addCell(Details11);
           PdfPCell Details21 = new PdfPCell(new Phrase(String.valueOf(lc.getQte())));
          
           Details21.setBorderWidth(1f);
           tableDetails.addCell(Details21);
           totalBigDecimal = BigDecimal.valueOf(lc.getProduit().getPueuro());
           if(f.getType()==TypeFacture.National) {
        	   totalBigDecimal = BigDecimal.valueOf(lc.getProduit().getPutnd());
           }
            
           
           totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	         numberString = totalBigDecimal.toPlainString();
           PdfPCell Details31 = new PdfPCell(new Phrase(numberString));
           
           Details31.setBorderWidth(1f);
           tableDetails.addCell(Details31);
           totalBigDecimal = BigDecimal.valueOf((lc.getProduit().getPueuro()*lc.getQte()));
           if(f.getType()==TypeFacture.National) {
        	   totalBigDecimal = BigDecimal.valueOf((lc.getProduit().getPutnd()*lc.getQte()));
           }
           
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
           Total1.setBorderWidth(1f);
           tableDetails.addCell(Total1);
           }

           
           float[] columnWidthsD = {250f, 50f,50f,50f}; // Change these values as needed

           tableDetails.setWidths(columnWidthsD);
           tableDetails.setSpacingAfter(20f);
           document.add(tableDetails);
           
           PdfPTable tableTotal = new PdfPTable(2); // 2 columns
           tableTotal.setWidthPercentage(100);
           PdfPCell TotalhtL = new PdfPCell(new Phrase("Total HT"));
           TotalhtL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TotalhtL.setBorderWidth(1f);
           tableTotal.addCell(TotalhtL);
           totalBigDecimal = BigDecimal.valueOf(TotalHT(f));
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell Totalht = new PdfPCell(new Phrase(numberString));
           
           Totalht.setBorderWidth(1f);
           tableTotal.addCell(Totalht);
           totalBigDecimal = BigDecimal.valueOf(f.getTva());
	       totalBigDecimal = totalBigDecimal.setScale(0, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           
           PdfPCell TVAL = new PdfPCell(new Phrase("TVA("+numberString+"%)"));
           TVAL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TVAL.setBorderWidth(1f);
           tableTotal.addCell(TVAL);
           totalBigDecimal = BigDecimal.valueOf((TotalHT(f)*(f.getTva()/100)));
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           
           PdfPCell TVA = new PdfPCell(new Phrase(numberString));
           TVA.setBorderWidth(1f);
           tableTotal.addCell(TVA);
           
           PdfPCell TimbreL = new PdfPCell(new Phrase("Timbre"));
           TimbreL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TimbreL.setBorderWidth(1f);
           tableTotal.addCell(TimbreL);
           totalBigDecimal = BigDecimal.valueOf(f.getCtvtimbre());
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell Timbre = new PdfPCell(new Phrase(numberString));
           Timbre.setBorderWidth(1f);
           tableTotal.addCell(Timbre);
           
           PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
           TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
           TotalTTCL.setBorderWidth(1f);
           tableTotal.addCell(TotalTTCL);
           totalBigDecimal = BigDecimal.valueOf(CalculTotalTTC(f));
	       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	       numberString = totalBigDecimal.toPlainString();
           PdfPCell TotalTTC = new PdfPCell(new Phrase(numberString));
           TotalTTC.setBorderWidth(1f);
           tableTotal.addCell(TotalTTC);
           
           float[] columnWidthsT = {300f, 100f}; // Change these values as needed

           tableTotal.setWidths(columnWidthsT);
           tableTotal.setSpacingAfter(20f);
           document.add(tableTotal);
           
           
           PdfPTable tableMontantL = new PdfPTable(1); // 2 columns
           tableMontantL.setWidthPercentage(100);
           Font font1 = FontFactory.getFont(FontFactory.TIMES_BOLD, 12,Font.BOLD, BaseColor.BLACK);
           PdfPCell MontantL = new PdfPCell();
           Paragraph paragraph2 = new Paragraph();
           paragraph2.add(new Phrase("Arrêtée la présente facture à un total de\n", font1));

           paragraph2.add(new Phrase(TotalEnLettre(CalculTotalTTC(f),f.getType())));
           MontantL.addElement(paragraph2);
           tableMontantL.addCell(MontantL);
           
         
           float[] columnWidthsML = {300f};
           tableMontantL.setWidths(columnWidthsML);
           tableMontantL.setSpacingAfter(30f);
           document.add(tableMontantL);
           tableMontantL.setSpacingAfter(20f);
           
           Image qrCodeImage = Image.getInstance(getQRCodeImage(f.getIdF(),100,100));
         
           document.add(qrCodeImage);
           document.close();

           // Optionally, save the ByteArrayOutputStream to a file
          /* FileOutputStream fileOutputStream = new FileOutputStream("iTextHelloWorld.pdf");
               outputStream.writeTo(fileOutputStream);*/
           
			
			 return outputStream;
   }

    public ByteArrayOutputStream generateFactureEuroAfterSign(Facture f) throws DocumentException, IOException, WriterException {
    	String numberString ="";
        BigDecimal totalBigDecimal;
      	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      	boolean remp=false;
        
    	if(f.getRef() !=0) {
    		remp=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).size()!=0;
    	} 
      	
      	Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (f.getNumber() ==0)

		f.setClient(clientRepo.findById(f.getClient().getIdU()).get());

	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
              // Initialize Document and PdfWriter
              Document document = new Document();
              PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);

              // Open the document
              document.open();

              PdfContentByte contentByte = pdfWriter.getDirectContent();

              // Create a rectangle
              Rectangle rectangle = new Rectangle(80, 800, 500, 770);
              rectangle.setBorder(Rectangle.BOX);
              rectangle.setBorderWidth(1);

              // Add the rectangle to the document
              contentByte.rectangle(rectangle);

              // Move to the center of the rectangle for adding text
              contentByte.moveTo((rectangle.getLeft() + rectangle.getRight()) / 2, (rectangle.getBottom() + rectangle.getTop()) / 2);

              // Add content to the document
              Font font = FontFactory.getFont(FontFactory.COURIER, 16,Font.BOLD, BaseColor.BLACK);

              // Create a Paragraph with centered text
              Paragraph paragraph = new Paragraph("Facture N "+f.getNumber()+"/"+currentYear, font);
             
              paragraph.setAlignment(Element.ALIGN_CENTER);

              // Add the Paragraph to the document
              document.add(paragraph);
              document.add(Chunk.NEWLINE);
              if(remp ==true) {
           	   Facture fr=factureRepo.factureRemplacement(f.getRef(),f.getIdF()).get(0);
           	   Paragraph paragraphAv = new Paragraph("Facture de remplacement de la facture N:"+fr.getNumber()+"/"+currentYear+" "+dateFormat.format(fr.getDateemission()));
   		          
   	           

   	           // Add the Paragraph to the document
   	           document.add(paragraphAv);
   	           document.add(Chunk.NEWLINE);
              }
              
              Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

              // Create a Paragraph with centered text
              Paragraph paragraph1 = new Paragraph("date d'emission "+dateFormat.format(f.getDateemission()) , fontd);
             
              paragraph1.setAlignment(Element.ALIGN_RIGHT);

              // Add the Paragraph to the document
              document.add(paragraph1);
              
              Paragraph space = new Paragraph(); // Adjust the spacing as needed
              space.setSpacingAfter(10f);
              document.add(space);

              PdfPTable table = new PdfPTable(2); // 2 columns
              table.setWidthPercentage(100);

              // Add image to the first cell
              
              com.itextpdf.text.Image image = downloadAndUseImage();
              image.scaleAbsoluteWidth(230f);
              image.scaleAbsoluteHeight(100f);
            
              PdfPCell imageCell = new PdfPCell(image);
              imageCell.setBorderWidth(1f);
              table.addCell(imageCell);

              // Add text to the second cell
              PdfPCell textCell = new PdfPCell(new Phrase("Identification fiscale: "+identifiantf+"\n"
                 		+ "RIB TND : "+ribtnd+"\nRIB EURO : "+ribtnd+"\nIBAN :"+iban+
                 		"\nBQE :"+bqe+"\nagence :mourouj\nSiege social :"+siegesocial));
              textCell.setBorderWidth(1f);
              table.addCell(textCell);
              

              float[] columnWidths = {200f, 250f}; // Change these values as needed

              table.setWidths(columnWidths);
              table.setSpacingAfter(40f);
              

              document.add(table);
              // Close the document
              
             
              
              PdfPTable tableClient = new PdfPTable(2); // 2 columns
              tableClient.setWidthPercentage(100);
              PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal"));
              ClienInfo.setBorderWidth(1f);
              tableClient.addCell(ClienInfo);
              
              PdfPCell ClienInfo1 = new PdfPCell(new Phrase(f.getClient().getLibelle()+"\n"
              		+f.getClient().getAddresse()+"\n"+f.getClient().getIdfiscal()));
              ClienInfo1.setBorderWidth(1f);
              tableClient.addCell(ClienInfo1);
              
              PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
              ClienInfo2.setBorderWidth(1f);
              tableClient.addCell(ClienInfo2);
              System.out.println("hadya red ===========>"+f.getRef());
              PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(f.getRef())));
              ClienInfo3.setBorderWidth(1f);
              tableClient.addCell(ClienInfo3);
              tableClient.setSpacingAfter(40f);
              document.add(tableClient);
              Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
              PdfPTable tableDetails = new PdfPTable(4); // 2 columns
              tableDetails.setWidthPercentage(100);
              PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
              
              Details1.setBorderWidth(1f);
              tableDetails.addCell(Details1);
              
              PdfPCell Details2 = new PdfPCell(new Phrase("Nb jours",fontZ2));
              Details2.setHorizontalAlignment(Element.ALIGN_CENTER);
              Details2.setBorderWidth(1f);
              tableDetails.addCell(Details2);
              PdfPCell Details3 = new PdfPCell(new Phrase("PU €",fontZ2));
              Details3.setHorizontalAlignment(Element.ALIGN_CENTER);
              Details3.setBorderWidth(1f);
              tableDetails.addCell(Details3);
              PdfPCell Total = new PdfPCell(new Phrase("Total € ",fontZ2));
              Total.setHorizontalAlignment(Element.ALIGN_CENTER);
              Total.setBorderWidth(1f);
              tableDetails.addCell(Total);
              
              for (Lignecommande lc : f.getCommandes()) {
            	  PdfPCell Details11;
            	  if(lc.getCommentaire().length()  != 0) {
            	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()+"("+lc.getCommentaire()+")"));
            }
              
            else {
            	 Details11 = new PdfPCell(new Phrase(lc.getProduit().getLibelle()));
            }
            	
              Details11.setBorderWidth(1f);
              tableDetails.addCell(Details11);
              PdfPCell Details21 = new PdfPCell(new Phrase(String.valueOf(lc.getQte())));
             
              Details21.setBorderWidth(1f);
              tableDetails.addCell(Details21);
              totalBigDecimal = BigDecimal.valueOf(lc.getProduit().getPueuro());
              if(f.getType()==TypeFacture.National) {
            	  totalBigDecimal = BigDecimal.valueOf(lc.getProduit().getPutnd());
              }
             
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Details31 = new PdfPCell(new Phrase(numberString));
              Details31.setBorderWidth(1f);
              tableDetails.addCell(Details31);
              totalBigDecimal = BigDecimal.valueOf((lc.getProduit().getPueuro()*lc.getQte()));
              if(f.getType()==TypeFacture.National) {
            	  totalBigDecimal = BigDecimal.valueOf((lc.getProduit().getPutnd()*lc.getQte()));
              }
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
              Total1.setBorderWidth(1f);
              tableDetails.addCell(Total1);
              }
              float[] columnWidthsD = {250f, 50f,50f,50f}; // Change these values as needed

              tableDetails.setWidths(columnWidthsD);
              tableDetails.setSpacingAfter(20f);
              document.add(tableDetails);
              
              PdfPTable tableTotal = new PdfPTable(2); // 2 columns
              tableTotal.setWidthPercentage(100);
              PdfPCell TotalhtL = new PdfPCell(new Phrase("Total HT"));
              TotalhtL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TotalhtL.setBorderWidth(1f);
              tableTotal.addCell(TotalhtL);
              totalBigDecimal = BigDecimal.valueOf(TotalHT(f));
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Totalht = new PdfPCell(new Phrase(numberString));
              Totalht.setBorderWidth(1f);
              tableTotal.addCell(Totalht);
     totalBigDecimal = BigDecimal.valueOf(f.getTva());
              
              totalBigDecimal = totalBigDecimal.setScale(0, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              
              PdfPCell TVAL = new PdfPCell(new Phrase("TVA("+numberString+"%)"));
              TVAL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TVAL.setBorderWidth(1f);
              tableTotal.addCell(TVAL);
totalBigDecimal = BigDecimal.valueOf((TotalHT(f)*(f.getTva()/100)));
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell TVA = new PdfPCell(new Phrase(numberString));
              TVA.setBorderWidth(1f);
              tableTotal.addCell(TVA);
              
              PdfPCell TimbreL = new PdfPCell(new Phrase("Timbre"));
              TimbreL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TimbreL.setBorderWidth(1f);
              tableTotal.addCell(TimbreL);
totalBigDecimal = BigDecimal.valueOf(f.getCtvtimbre());
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              PdfPCell Timbre = new PdfPCell(new Phrase(numberString));
              Timbre.setBorderWidth(1f);
              tableTotal.addCell(Timbre);
              
              PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
              TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
              TotalTTCL.setBorderWidth(1f);
              tableTotal.addCell(TotalTTCL);
totalBigDecimal = BigDecimal.valueOf(CalculTotalTTC(f));
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
              
              PdfPCell TotalTTC = new PdfPCell(new Phrase(numberString));
              TotalTTC.setBorderWidth(1f);
              tableTotal.addCell(TotalTTC);
              
              float[] columnWidthsT = {300f, 100f}; // Change these values as needed

              tableTotal.setWidths(columnWidthsT);
              tableTotal.setSpacingAfter(10f);
              document.add(tableTotal);
              
              
              PdfPTable tableMontantL = new PdfPTable(1); // 2 columns
              tableMontantL.setWidthPercentage(100);
              Font font1 = FontFactory.getFont(FontFactory.TIMES_BOLD, 12,Font.BOLD, BaseColor.BLACK);
              PdfPCell MontantL = new PdfPCell();
              Paragraph paragraph2 = new Paragraph();
              paragraph2.add(new Phrase("Arrêtée la présente facture à un total de\n", font1));
              paragraph2.add(new Phrase(TotalEnLettre(CalculTotalTTC(f),f.getType())));
              MontantL.addElement(paragraph2);
              tableMontantL.addCell(MontantL);
              
            
              float[] columnWidthsML = {300f};
              tableMontantL.setWidths(columnWidthsML);
              tableMontantL.setSpacingAfter(20f);
              document.add(tableMontantL);
              Image qrCodeImage = Image.getInstance(getQRCodeImage(f.getIdF(),100,100));
              
              document.add(qrCodeImage);
              qrCodeImage.setSpacingAfter(10f);
              
              PdfPTable tableZone = new PdfPTable(1); // 2 columns
              tableZone.setWidthPercentage(100);
              Font fontZ = FontFactory.getFont(FontFactory.TIMES_BOLD, 12,Font.BOLD, BaseColor.BLACK);
              PdfPCell Zone = new PdfPCell(new Phrase("Zone reservée comptabilité interne:", fontZ));
              
              tableZone.addCell(Zone);
              float[] columnWidthsZ = {300f};
              tableZone.setWidths(columnWidthsZ);
              tableZone.setSpacingAfter(5f);
              document.add(tableZone);
              
              
              
            
              
              PdfPTable tableTotalEuro = new PdfPTable(2); // 2 columns
              tableTotalEuro.setWidthPercentage(100);
              Font fontZ1 = FontFactory.getFont(FontFactory.COURIER, 10,Font.BOLD, BaseColor.BLACK);
              
              PdfPCell TotalL = new PdfPCell(new Phrase("Date contre valeur\r\r\nCours moyen Euros (archives BCT en date de facturation)\r\r\n"
              		+ "CTV HT\r\r\n"
              		+ "CTV TVA "+f.getCtvtva()+"%\r\r\n"
              		+ "CTV Timbre\r\r\n"
              		+ "CTV TTC TND",fontZ1));
              TotalL.setBorderWidth(1f);
              tableTotalEuro.addCell(TotalL);
              totalBigDecimal = BigDecimal.valueOf(f.getCours());
              
              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

   	         numberString = totalBigDecimal.toPlainString();
   	         
   	      BigDecimal totalBigDecimal1 = BigDecimal.valueOf(CalculTotalHTConv(f));
          
          totalBigDecimal1 = totalBigDecimal1.setScale(3, RoundingMode.DOWN);

	         String numberString1 = totalBigDecimal1.toPlainString();
	         
	         BigDecimal totalBigDecimal2 = BigDecimal.valueOf((CalculTotalHTConv(f)*(f.getCtvtva()/100)));
	          
	          totalBigDecimal2 = totalBigDecimal2.setScale(3, RoundingMode.DOWN);

		         String numberString2 = totalBigDecimal2.toPlainString();
		         
		         BigDecimal totalBigDecimal3 = BigDecimal.valueOf(f.getCtvtimbre());
		          
		          totalBigDecimal3 = totalBigDecimal3.setScale(3, RoundingMode.DOWN);

			         String numberString3 = totalBigDecimal3.toPlainString();
			         
			         BigDecimal totalBigDecimal4 = BigDecimal.valueOf(CalculTotatlTTCConv(f));
			          
			          totalBigDecimal4 = totalBigDecimal4.setScale(3, RoundingMode.DOWN);

				         String numberString4 = totalBigDecimal4.toPlainString();
              PdfPCell TotalHT = new PdfPCell(new Phrase(dateFormat.format(f.getDatecv())+"\r\r\n"+numberString+" TND"+"\r\r\n\n"+numberString1+" TND"+"\r\r\n"+
            numberString2+" TND"+"\r\r\n"+numberString3+" TND"+"\r\r\n"+numberString4+" TND",fontZ1));
              TotalHT.setBorderWidth(1f);
              tableTotalEuro.addCell(TotalHT);
              float[] columnWidthsT1 = {150f, 150f};
              tableTotalEuro.setWidths(columnWidthsT1);
              document.add(tableTotalEuro);
              document.close();

              // Optionally, save the ByteArrayOutputStream to a file
             /* FileOutputStream fileOutputStream = new FileOutputStream("iTextHelloWorld.pdf");
                  outputStream.writeTo(fileOutputStream);*/
              
   			
   			 return outputStream;
      }
    
    public String uploadfile(ByteArrayOutputStream uploadFile,String name) {
    	int currentYear = Year.now().getValue();
    	name=name+"_"+currentYear+".pdf";
    	
		   FTPClient ftpClient = new FTPClient();
		     try {
		    	
		    	 
		         ftpClient.connect("192.168.1.31", 21);
		         ftpClient.login("ftp-user", "ftpuser");
		         ftpClient.enterLocalPassiveMode();

		         ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		         // APPROACH #1: uploads first file using an InputStream
		         File firstLocalFile = new File("D:/Test/Projects.zip");

		         String firstRemoteFile = "Projects.zip";
		         InputStream inputStream = new ByteArrayInputStream(uploadFile.toByteArray());
		        
		         System.out.println("Start uploading first file");
		         
		         boolean done = ftpClient.storeFile(name, inputStream);
		         inputStream.close();
		         if (done) {
		             System.out.println("The first file is uploaded successfully.");
		         }

		         return name;

		     } catch (IOException ex) {
		         System.out.println("Error: " + ex.getMessage());
		         ex.printStackTrace();
		     } finally {
		         try {
		             if (ftpClient.isConnected()) {
		                 ftpClient.logout();
		                 ftpClient.disconnect();
		             }
		         } catch (IOException ex) {
		             ex.printStackTrace();
		         }
		     }
		     return null;

		}
    
    public boolean deleteFile(String fileName) {
        FTPClient ftpClient = new FTPClient();
        try {
        	 ftpClient.connect("192.168.1.31", 21);
	         ftpClient.login("ftp-user", "ftpuser");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            boolean deleted = ftpClient.deleteFile(fileName);

            if (deleted) {
                System.out.println("The file was deleted successfully.");
                return true;
            } else {
                System.out.println("Could not delete the file. It may not exist.");
            }

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
    
    @Transactional
    
    public List<Facture> createFactureFromProforma(long id,List<Facture> factures){
    	Facture newFacture=new Facture();
    	Facture proforma=factureRepo.findById(id).get();
    	List<Facture> result=new ArrayList<Facture>();
		List<Facture> resultProforma=new ArrayList<Facture>();
		
		
		
    	
    	newFacture.setStatus(FactureStatus.Facture);
		newFacture.setSigned(false);
		newFacture.setTraited(false);
		newFacture.setPayementstatus(FacturePayementStatus.Non_Paye);
		newFacture.setNumber(0);
		newFacture.setTotalrestant(proforma.getTotalttc());
		
		
         
         newFacture.setType(proforma.getType());
        newFacture.setDateemission(proforma.getDateemission());
 		if(newFacture.getType() == TypeFacture.Export) {
 			newFacture.setDatecv(proforma.getDatecv());
 			newFacture.setCours(proforma.getCours());
 			newFacture.setTva(0);
 			newFacture.setCtvtimbre(0);
 		}
 		else {
 			newFacture.setTva(proforma.getTva());
 			newFacture.setCtvtimbre(proforma.getCtvtimbre());
 		}
 		newFacture.setArchived(false);
 		newFacture.setCommandes(new ArrayList<Lignecommande>());
 		for(Lignecommande lc :proforma.getCommandes()) {
 			
		newFacture.getCommandes().add(commandeService.affectLCTofacture(lc.getIdLC(), newFacture));
		
		}
 		newFacture.setTotalht(proforma.getTotalht());
 		newFacture.setTotalttc(proforma.getTotalttc());
 		newFacture.setRetenue(CalculRetunue(proforma.getTotalttc()));
 		newFacture.setCtvht(proforma.getCtvht());
 		newFacture.setCtvttc(proforma.getCtvttc());
 		newFacture.setCtvtva(proforma.getCtvtva());
 		newFacture.setMontantlettres(proforma.getMontantlettres());
 		newFacture.setClient(proforma.getClient());
 		
 		newFacture.setUser(proforma.getUser());
 		newFacture.setRef(proforma.getNumber());
         newFacture= factureRepo.save(newFacture);
         proforma.setTraited(true);
         proforma=factureRepo.save(proforma);
         
     factures.add(newFacture);
		for(int i=0;i<factures.size();i++) {
			if(factures.get(i).getIdF()==id) {
				factures.remove(i);
				break;
			}
		}
    	
		for (Facture facture1 : factures) {
			if(facture1.getStatus()==FactureStatus.Facture || facture1.getStatus()==FactureStatus.Facture_envoye   ) {
				result.add(facture1);
			}
			else if((facture1.getStatus()==FactureStatus.Proforma  || facture1.getStatus()==FactureStatus.Proforma_envoyee  || facture1.getStatus()==FactureStatus.Proforma_envoyee_validee) && facture1.getIdF() !=  id) {
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
    
    
    
    public List<Facture> SignFacture(long id,long idp,FactureDateAndList fd1) {
    	
    
    	
    	
  
    	try {
    		HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Personel per=personelRepo.findById(idp).get();
    		String name=per.getFirstname();
    		String lastname=per.getLastname();
    		Facture F=factureRepo.findById(id).get();
    		F.setNumber(setFactureNumber());
    		
        	try {
                
           	 F.setDateemission(sdf.parse(getValidDate(fd1.getDate())));
               
                
                
            } catch (ParseException e) {
                e.printStackTrace();
            }

             // Check if the user is authenticated
           
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		
    		if(F.getType()==TypeFacture.Export)
    			outputStream=generateFactureEuroAfterSign(F);
    		else
    			outputStream=generateFactureAfterSign(F);
    		
String keystorePath = "/opt/certificates/"+name.toLowerCase()+"_"+lastname.toLowerCase()+"/ca/keystore.p12";
             String keystorePassword = "ca_p12_password_"+name.toLowerCase()+lastname.toLowerCase();
             KeyStore keystore = KeyStore.getInstance("PKCS12");
             keystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

             // Get the private key and certificate chain from the keystore
             String alias = (String) keystore.aliases().nextElement();
             PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
             Certificate[] chain = keystore.getCertificateChain(alias);

             // Create a reader to read the PDF template
             PdfReader reader = new PdfReader(outputStream.toByteArray());

             // Create a stamper to apply the signature
             ByteArrayOutputStream signedPdfStream = new ByteArrayOutputStream();
             PdfStamper stamper = PdfStamper.createSignature(reader, signedPdfStream, '\0');

             // Create appearance for signature
             float pageWidth = PageSize.A4.getWidth();
             float pageHeight = PageSize.A4.getHeight();

             // Rectangle dimensions (same as original code)
             float rectWidth = 144; // Width of the rectangle in points
             float rectHeight = 32; // Height of the rectangle in points

             // Calculate the coordinates of the lower left corner of the rectangle
             float rectX = pageWidth - rectWidth - 10; // 36 points margin from right edge
             float rectY = 36; // 36 points margin from bottom edge

             // Create the rectangle
             Rectangle rect = new Rectangle(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
             PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
             appearance.setReason("Facturation");
             appearance.setLocation("Tunisia");
             appearance.setVisibleSignature(rect, 1, "signature");
             ExternalDigest d;
             // Create the signature
             ExternalSignature es = new PrivateKeySignature(privateKey, "SHA-256", "BC");
             ExternalDigest digest = new BouncyCastleDigest();
             MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

             // Close the stamper and reader
             stamper.close();
             reader.close(); 
            
            
            String pdfname= uploadfile(signedPdfStream,"Facture"+F.getNumber());
        	List<Facture> result=SignFactureupdate(F.getIdF(),pdfname,fd1.getFactures());
    		

             return result;
             } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    	
    	
    }
    
    
    
    public byte[] downloadFile(String name) throws SerialException, SQLException, IOException {
	
    
    FTPClient ftpClient = new FTPClient();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    try {
    	ftpClient.connect("192.168.1.31", 21);
        ftpClient.login("ftp-user", "ftpuser");
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        InputStream inputStream = ftpClient.retrieveFileStream(name);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        return outputStream.toByteArray();
    } catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
        outputStream.close();
    }
    return null;
}
    
    public Facture getFactureByPath(String path) {
    	return factureRepo.findByPdfpath(path);
    }
    
    public Date getValideDateUsingGoogleCalender(String dateString ) throws IOException {
    	//LocalDate date = LocalDate.now(); 
    	StringBuilder response = new StringBuilder();
        LocalDateTime date = LocalDateTime.parse(dateString);
    	 
    	 if(date.getDayOfWeek() == DayOfWeek.SUNDAY) {
    		
    		 date = date.plusDays(1);
    		
    	 }
    	 try {
    	 String accessToken = "ya29.a0Ad52N39kT_j_8gPtPSJ7OgMadOBsauYBBMYvz9IbsBb4kiD7_uKcZ9YbFXGewPj3jwCGJKDDkLRgvCiJUfe438xmrgelKwocLHL4gI-sPXEC8vpqoMkzvtlo1pMMcefKjCE2tud_v29E9W9voZ_Q_NS8ErRa05Ia9Hz4aCgYKAQcSARESFQHGX2MiyZcAEJbLPMVrXI-W7tD8sw0171";
    	String url = "https://www.googleapis.com/calendar/v3/calendars/en.tn.official%23holiday%40group.v.calendar.google.com/events?timeMin=2024-01-01T00:00:00Z&timeMax=2024-12-30T23:59:59Z";
    	 URL obj = new URL(url);
         HttpURLConnection con = (HttpURLConnection) obj.openConnection();

         // Set the request method
         con.setRequestMethod("GET");

         // Set the Authorization header with Bearer token
         con.setRequestProperty("Authorization", "Bearer " + accessToken);

         // Get the response code
         int responseCode = con.getResponseCode();
         System.out.println("Response Code : " + responseCode);

         // Read the response body
         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String inputLine;
         

         while ((inputLine = in.readLine()) != null) {
             response.append(inputLine);
         }
         in.close();

        
         in.close();

         
    
    
} catch (IOException e) {
    e.printStackTrace();
}
    	 ObjectMapper mapper = new ObjectMapper();
         JsonNode holidays = mapper.readTree(response.toString()).get("items");

         // Initialize a list to store start dates
         List<String> startDates = new ArrayList<>();

         // Filter events and add start dates to the list
         for (JsonNode event : holidays) {
             String summary = event.get("summary").asText();
             if (!summary.equals("Muharram") && !summary.equals("Women’s Day")) {
                 startDates.add(event.get("start").get("date").asText());
             }
         }
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
         DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
         String formattedDate = date.format(formatter);
        
         while(startDates.contains(formattedDate)) {
        	 
        	 String formattedDate1 = date.format(formatter1);
        	 date = LocalDateTime.parse(formattedDate1);
        	 date = date.plusDays(1);
        	 formattedDate = date.format(formatter);
        	 
         }
         Date finalDate=new Date();
         try {
        	 
        	  finalDate = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         return finalDate;
  	 
    }
    
    
    
    	

    
    public double getLastCours() {
    	Facture facture=factureRepo.findFirstByDateemissionAndType(new Date(),TypeFacture.Export);
    	if(facture != null) {
    		return facture.getCours();
    	}
    	else {
    		return 0;
    	}
    	 
    }
   /* @Transactional
    public ByteArrayOutputStream CreateAvoir(Facture f) {
    	f=setFactureNumber(f);
    	f.setStatus(FactureStatus.Avoir);
    	
	
		Client c=clientRepo.findById(f.getClient().getIdC()).orElse(null);
		c.getFactures().add(f);
		clientRepo.save(c);
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
	    String email = loggedInUser.getName(); 
	    User user = userRepository.findByEmail(email);
	    user.getFacturesuser().add(f);
	    userRepository.save(user);
	    f.setUser(user);
	    
	    for (int i = 0; i < f.getCommandes().size(); i++) {
	    	Lignecommande lc=f.getCommandes().get(i);
	    	
	    	lc=commandeService.AddLigneCommande(lc, f);
	    	f.getCommandes().set(i, lc);
	    }
	    f.setTotalht(TotalHT(f));
		f.setTotalttc(CalculTotalTTC(f));
		f.setRetenue(CalculRetunue(CalculTotalTTC(f)-f.getCtvtimbre()));
		f.setCtvht(CalculTotalHTConv(f));
		f.setCtvttc(CalculTotatlTTCConv(f));
		f.setCtvtva(f.getTva());
		f.setMontantlettres(TotalEnLettre(f.getTotalttc(),f.getType()));
		f.setSigned(true);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			f.setDateemission(dateFormat.parse(getValidDate()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f=factureRepo.save(f);
		
		try {
    		HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
    		
    		FactureListAndPDF response=new FactureListAndPDF();
    	
    		String name="";
    		String lastname="";
    		
    		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

             // Check if the user is authenticated
             if (authentication != null && authentication.isAuthenticated()) {
                 // Get the principal (which should be your custom UserDetails implementation)
                 Object principal = authentication.getPrincipal();

                 // Assuming your UserDetails implementation is named CustomUserDetails
                 if (principal instanceof User) {
                 	User userDetails = (User) principal;

                     // Access the user's first name
                     name = userDetails.getFirstname();
                     lastname=userDetails.getLastname();
                 }
             }
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    		
    		if(f.getType()==TypeFacture.Export)
    			outputStream=generateFactureEuroAfterSign(f);
    		else
    			outputStream=generateFactureAfterSign(f);
    		
    		 String keystorePath = "C:\\Users\\ASUS\\Desktop\\PFE_Work\\"+name.toLowerCase()+"_"+lastname.toLowerCase()+"\\ca\\keystore.p12";
             String keystorePassword = "ca_p12_password_"+name.toLowerCase()+lastname.toLowerCase();
             KeyStore keystore = KeyStore.getInstance("PKCS12");
             keystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

             // Get the private key and certificate chain from the keystore
             String alias = (String) keystore.aliases().nextElement();
             PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
             Certificate[] chain = keystore.getCertificateChain(alias);

             // Create a reader to read the PDF template
             PdfReader reader = new PdfReader(outputStream.toByteArray());

             // Create a stamper to apply the signature
             ByteArrayOutputStream signedPdfStream = new ByteArrayOutputStream();
             PdfStamper stamper = PdfStamper.createSignature(reader, signedPdfStream, '\0');

             // Create appearance for signature
             float pageWidth = PageSize.A4.getWidth();
             float pageHeight = PageSize.A4.getHeight();

             // Rectangle dimensions (same as original code)
             float rectWidth = 144; // Width of the rectangle in points
             float rectHeight = 32; // Height of the rectangle in points

             // Calculate the coordinates of the lower left corner of the rectangle
             float rectX = pageWidth - rectWidth - 10; // 36 points margin from right edge
             float rectY = 36; // 36 points margin from bottom edge

             // Create the rectangle
             Rectangle rect = new Rectangle(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
             PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
             appearance.setReason("Facturation");
             appearance.setLocation("Tunisia");
             appearance.setVisibleSignature(rect, 1, "signature");
             ExternalDigest d;
             // Create the signature
             ExternalSignature es = new PrivateKeySignature(privateKey, "SHA-256", "BC");
             ExternalDigest digest = new BouncyCastleDigest();
             MakeSignature.signDetached(appearance, digest, es, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);

             // Close the stamper and reader
             stamper.close();
             reader.close(); 
            
            String pdfname= uploadfile(signedPdfStream,"Avoir"+f.getNumber());
    		f.setPdfname(pdfname);
    		factureRepo.save(f);
             return signedPdfStream;
             } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    	
    }
    */
    @Transactional
    public List<Facture> CancelInvoice(long id,List<Facture> factures) {
    	Facture facture=factureRepo.findById(id).get();
    	List<Facture> result = new ArrayList<Facture>();
    	
    	List<Facture> resultProforma=new ArrayList<Facture>();
    	deleteFile(facture.getPdfname());
    	facture.setNumber(0);
    	facture.setStatus(FactureStatus.Facture);
    	facture=factureRepo.save(facture);
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
    private String loadEmailTemplate(String templateName, String libelle,String status) throws IOException {
        Context context = new Context();
        context.setVariable("libelle", libelle);
        context.setVariable("type", status);
        

        
        return templateEngine.process("email/" + templateName, context);
    }
    
    
    private String loadEmailTemplateAction(String templateName, String libelle,String action,long num) throws IOException {
        Context context = new Context();
        context.setVariable("libelle", libelle);
        context.setVariable("action", action);
        context.setVariable("num", num);
        

        
        return templateEngine.process("email/" + templateName, context);
    }
    
    public List<Facture> SendProformaMail(long id,List<Facture> factures) {
        Facture facture = factureRepo.findById(id).get();
List<Facture> result = new ArrayList<Facture>();
    	
    	List<Facture> resultProforma=new ArrayList<Facture>();
        byte[] pdfBytes;
      
            try {
               
                String emailContent = loadEmailTemplate("sendInvoice.html", facture.getClient().getLibelle(),"Proforma");
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setSubject("NadFact facture");
                helper.setFrom("naderbessioud98@gmail.com");
                helper.setTo(facture.getClient().getEmail());
                helper.setText(emailContent, true);

                if(facture.getType() == TypeFacture.National) {
                	pdfBytes=generateFacture(facture).toByteArray();
                }
                else {
                	pdfBytes=generateFactureAfterSign(facture).toByteArray();
                }
                ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
                helper.addAttachment("invoice.pdf", pdfAttachment);

                // Send email
                mailSender.send(mimeMessage);
                facture.setStatus(FactureStatus.Proforma_envoyee);
                facture= factureRepo.save(facture);
                for(int i=0;i<factures.size();i++) {
                	if(factures.get(i).getIdF() == id) {
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
            } catch (Exception e) {
            	
                e.printStackTrace();
                return result;
            }
            
        
    }
    
    
    
    public void SendProformaActionMail(long id,String action) {
        Facture facture = factureRepo.findById(id).get();

    	
    
        byte[] pdfBytes;
      
            try {
               
                String emailContent = loadEmailTemplateAction("ProformaStatus.html", facture.getClient().getLibelle(),action,facture.getNumber());
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setSubject("NadFact facture");
                helper.setFrom("naderbessioud98@gmail.com");
                helper.setTo(facture.getUser().getEmail());
                helper.setText(emailContent, true);

                if(facture.getType() == TypeFacture.National) {
                	pdfBytes=generateFacture(facture).toByteArray();
                }
                else {
                	pdfBytes=generateFactureAfterSign(facture).toByteArray();
                }
                ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
                helper.addAttachment("invoice.pdf", pdfAttachment);

                // Send email
                mailSender.send(mimeMessage);
              

              
                
               
            } catch (Exception e) {
            	
                e.printStackTrace();
                
            }
            
        
    }
    
    public List<Facture> SendInvoiceMail(long id,List<Facture> factures) {
        Facture facture = factureRepo.findById(id).get();
        List<Facture> result = new ArrayList<Facture>();
    	
    	List<Facture> resultProforma=new ArrayList<Facture>();
      
            try {
               
                String emailContent = loadEmailTemplate("sendInvoice.html", facture.getClient().getLibelle(),"Facture");
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setSubject("NadFact facture");
                helper.setFrom("naderbessioud98@gmail.com");
                helper.setTo(facture.getClient().getEmail());
                helper.setText(emailContent, true);

                byte[] pdfBytes = downloadFile(facture.getPdfname());
                ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
                helper.addAttachment("invoice.pdf", pdfAttachment);

                // Send email
                mailSender.send(mimeMessage);
                facture.setStatus(FactureStatus.Facture_envoye);
                facture=factureRepo.save(facture);
                for(int i=0;i<factures.size();i++) {
                	if(factures.get(i).getIdF() == id) {
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
                
            } catch (Exception e) {
            	
                e.printStackTrace();
                return result;
            }
            
        
    }
    
    public Date getLastInvoiceDate(long id) {
    	
    	Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        long number =setFactureNumber();
    
    		if(factureRepo.findFactureByNumber(number) ==null) {
    			return new Date();
    		}
    		
    		return factureRepo.findFactureByNumber(number).getDateemission();
    	
    	
    	
    	
    }
    
    public Date getMaxInvoiceDate() {
    	Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        long number =setFactureNumber();
    	if(factureRepo.findFactureByNumber(number+1) != null) {
    		
    		return factureRepo.findFactureByNumber(number+1).getDateemission();
    	}
    	return null;
    }
    
    public List<Facture> DisplayFactureByClient(String lib,String type,long idr){
    	List<Facture> result=new ArrayList<Facture>();
    	Reglement r=reglementRepo.findById(idr).get();
    	Client c=clientRepo.findByLibelle(lib);
    	

    	if(type.equals("TND")) {
    		result = factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Non_Paye,TypeFacture.National);
        	

        	result.addAll(factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Paye_Partiel,TypeFacture.National));
    	}
    	else {
    		result = factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Non_Paye,TypeFacture.Export);
        	result.addAll(factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Paye_Partiel,TypeFacture.Export));

    	}
    	Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
    	
    	List<Facture> facts=r.getRegFactures();
    	Collections.sort(facts, Comparator.comparing(Facture::getNumber));
    	System.out.println("hawww size mtaa facturee mtaa client==="+facts.size());
    	for (Facture facture : facts) {
			if(facture.getClient().getIdU()==c.getIdU() && facture.getPayementstatus()==FacturePayementStatus.Paye) {
				result.add(0, facture);
			}
		}
    	return result;
    }
      public List<Facture> DisplayFactureByReglement(long idr){
    	List<Facture> result=new ArrayList<Facture>();
    	Reglement r=reglementRepo.findById(idr).get();
    	Client c = clientRepo.findById(r.getIdc()).get();
    	
    	

    	if(r.getType().toString().equals("TND")) {
    		result = factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Non_Paye,TypeFacture.National);
        	

        	result.addAll(factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Paye_Partiel,TypeFacture.National));
        	
    	}
    	else {
    		result = factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Non_Paye,TypeFacture.Export);
        	result.addAll(factureRepo.findByClientAndArchivedAndStatusAndPayementstatusAndType(c, false,FactureStatus.Facture_envoye,FacturePayementStatus.Paye_Partiel,TypeFacture.Export));

    	}
    	Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
    	
    	List<Facture> facts=r.getRegFactures();
    	Collections.sort(facts, Comparator.comparing(Facture::getNumber));
    	
    	for (Facture facture : facts) {
			if(facture.getClient().getIdU()==c.getIdU() && facture.getPayementstatus()==FacturePayementStatus.Paye) {
				result.add(0, facture);
			}
		}
    	return result;
    }
    public String getFactureTypeByClient(long id) {
    	Client c=clientRepo.findById(id).get();
    	if(c.getType() == TypeClient.International) {
    		return "Internationale";
    	}
    	else {
    		return "Export";
    	}
    }
    
    public double getTVASomme(int year,int month,TypeFacture type,FactureStatus status) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
    	 String start=year+"-"+formattedMonth+"-01";
	     String end=year+"-"+formattedMonth+"-28";
	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	     Date startdate=dateFormat.parse(start);
	     Date enddate=dateFormat.parse(end);
	     for (Facture f : factureRepo.FactureBydateAndTypeAndStatus(startdate, enddate,type,status)) {
		    	
		    		result+=f.getTva()*f.getTotalttc()/100;
		    	
			}
    	return result;
    }
    
    public double getTVASommeByClient(int year,int month,TypeFacture type,FactureStatus status,long id) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
    	 String start=year+"-"+formattedMonth+"-01";
	     String end=year+"-"+formattedMonth+"-28";
	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	     Date startdate=dateFormat.parse(start);
	     Date enddate=dateFormat.parse(end);
	     for (Facture f : factureRepo.FactureBydateAndTypeAndStatusByClient(startdate, enddate,type,status,id)) {
		    	
		    		result+=f.getTva()*f.getTotalttc()/100;
		    	
			}
    	return result;
    }
    
    public double getTimbreSomme(int year,int month,TypeFacture type,FactureStatus status) throws ParseException {
    	
    	 String formattedMonth = String.format("%02d", month);
    	 String start=year+"-"+formattedMonth+"-01";
	     String end=year+"-"+formattedMonth+"-28";
	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	     Date startdate=dateFormat.parse(start);
	     Date enddate=dateFormat.parse(end);
	     double result=factureRepo.TotalTimbreSumMonth(startdate, enddate, type, status) .stream()
         .mapToDouble(Double::doubleValue).sum();
	     
    	return result ;
    }
    
    public double getTimbreSommeByClient(int year,int month,TypeFacture type,FactureStatus status,long id) throws ParseException {
    	
   	 String formattedMonth = String.format("%02d", month);
   	 String start=year+"-"+formattedMonth+"-01";
	     String end=year+"-"+formattedMonth+"-28";
	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	     Date startdate=dateFormat.parse(start);
	     Date enddate=dateFormat.parse(end);
	     double result=factureRepo.TotalTimbreSumMonthByClient(startdate, enddate, type, status,id) .stream()
        .mapToDouble(Double::doubleValue).sum();
	     
   	return result ;
   }
    
    public double getTotalSomme(int year,int month,TypeFacture type,FactureStatus status) throws ParseException {
    	
   	 String formattedMonth = String.format("%02d", month);
   	 String start=year+"-"+formattedMonth+"-01";
	     String end=year+"-"+formattedMonth+"-28";
	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	     Date startdate=dateFormat.parse(start);
	   
	     Date enddate=dateFormat.parse(end);
	     
	     double result=factureRepo.TotalSumMonth(startdate, enddate, type, status) .stream()
        .mapToDouble(Double::doubleValue).sum();
	     
   	return result ;
   }
    
    public double getTotalSommeByClient(int year,int month,TypeFacture type,FactureStatus status,long id) throws ParseException {
    	
      	 String formattedMonth = String.format("%02d", month);
      	 String start=year+"-"+formattedMonth+"-01";
   	     String end=year+"-"+formattedMonth+"-28";
   	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
   	     Date startdate=dateFormat.parse(start);
   	     Date enddate=dateFormat.parse(end);
   	     double result=factureRepo.TotalSumMonthByClient(startdate, enddate, type, status,id) .stream()
           .mapToDouble(Double::doubleValue).sum();
   	     
      	return result ;
      }
    
    
    public double getTVASommeYear(int year,TypeFacture type,FactureStatus status) throws ParseException {
    	double result=0;
    	
	     for (Facture f : factureRepo.FactureByYearAndTypeAndStatus(year,type,status)) {
		    	
		    		result+=f.getTva()*f.getTotalttc()/100;
		    	
			}
    	return result;
    }
    
    public double getTVASommeYearByClient(int year,TypeFacture type,FactureStatus status,long id) throws ParseException {
    	double result=0;
    	
	     for (Facture f : factureRepo.FactureByYearAndTypeAndStatusByClient(year,type,status,id)) {
		    	
		    		result+=f.getTva()*f.getTotalttc()/100;
		    	
			}
    	return result;
    }
    
    public double getTimbreSommeYear(int year,TypeFacture type,FactureStatus status) throws ParseException {
    	
    	
	     double result=factureRepo.TotalTimbreSumYear(year, type, status) .stream()
         .mapToDouble(Double::doubleValue).sum();
	     
    	return result ;
    }
    
    public double getTimbreSommeYearByClient(int year,TypeFacture type,FactureStatus status,long id) throws ParseException {
    	
    	
	     double result=factureRepo.TotalTimbreSumYearByClient(year, type, status,id) .stream()
        .mapToDouble(Double::doubleValue).sum();
	     
   	return result ;
   }
    
    
    public double getTotalSommeYear(int year,TypeFacture type,FactureStatus status) throws ParseException {
    	
   	     double result=factureRepo.TotalSumYear(year, type, status) .stream()
           .mapToDouble(Double::doubleValue).sum();
   	     
      	return result ;
      }
    
    public double getTotalSommeYearByClient(int year,TypeFacture type,FactureStatus status,long id) throws ParseException {
    	
  	     double result=factureRepo.TotalSumYearByClient(year, type, status,id) .stream()
          .mapToDouble(Double::doubleValue).sum();
  	     
     	return result ;
     }
    
    
    
    public long FactureCurrentYear() {
    	Calendar c=Calendar.getInstance();
    	int year =c.get(Calendar.YEAR);
    	return factureRepo.countByYear(year);
    }
    
    public List<Long> FactureByMonthAndYear() {
    	List<Long> result=new ArrayList<Long>();
    	Calendar c=Calendar.getInstance();
    	int year =c.get(Calendar.YEAR);
	
         for (int i=1;i<=12;i++) {
        	 result.add(factureRepo.countByMonthAndYear(i, year));
         }
    
    	return result;
    }
    
    
    public long countByYearAndMonth(int month) {
    	Calendar c=Calendar.getInstance();
    	int year =c.get(Calendar.YEAR);
    	return factureRepo.countByMonthAndYear(month, year);
    }
    
    public List<Facture> ValidateProformaByManager(long id,List<Facture> factures) {
    	Facture facture=factureRepo.findById(id).get();
List<Facture> result = new ArrayList<Facture>();
    	
    	List<Facture> resultProforma=new ArrayList<Facture>();
    	facture.setStatus(FactureStatus.Proforma_validee);
    	facture=factureRepo.save(facture);
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
    
    public List<Facture> updateFactureStatus(long id,FactureStatus status,List<Facture> factures,String action) {
    	Facture facture=factureRepo.findById(id).get();
List<Facture> result = new ArrayList<Facture>();
    	
    	List<Facture> resultProforma=new ArrayList<Facture>();
    	facture.setStatus(status);
    	facture=factureRepo.save(facture);
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
  		
  		SendProformaActionMail(id, action);
  			return result;
    	
    }
    
    public void updateFactureStatusByClient(long id,FactureStatus status,String action) {
    	Facture facture=factureRepo.findById(id).get();

    	
    	
    	facture.setStatus(status);
    	facture=factureRepo.save(facture);
    
  		
  		SendProformaActionMail(id, action);
  		
    	
    }
    public boolean checkIfExist(Facture f) {
    	boolean result=true;
    	Client c=clientRepo.findById(f.getClient().getIdU()).get();
    	for (Lignecommande lc : f.getCommandes()) {
			if(commandeService.checkIfExist(lc) ==false) {
				result=false;
				break;
			}
			
		}
    	if(result==true) {
    		result=factureRepo.existsByDatecvAndTvaAndCoursAndClient(f.getDatecv(), f.getTva(), f.getCours(), c);
    	}
    	return result;
    	
    	
    }
    
    private double calculTvaSumAvoirtnd(int year,int month) throws ParseException {
    	double result=0;
    	String formattedMonth = String.format("%02d", month);
      	 String start=year+"-"+formattedMonth+"-01";
   	     String end=year+"-"+formattedMonth+"-28";
   	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
   	     Date startdate=dateFormat.parse(start);
   	     Date enddate=dateFormat.parse(end);
   	     for (Avoir avoir : avoirRepo.AvoirsByMonth(startdate, enddate, AvoirStatus.Avoir_envoye)) {
   	    	 if(avoir.getFact().getType()==TypeFacture.National) {
   	    		result+=avoir.getMontant()*avoir.getFact().getTva()/100;
   	    	 }
			
		}
   	     return result;
    	
    }
    
    private double calculTvaSumAvoireuro(int year,int month) throws ParseException {
    	double result=0;
    	String formattedMonth = String.format("%02d", month);
      	 String start=year+"-"+formattedMonth+"-01";
   	     String end=year+"-"+formattedMonth+"-28";
   	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
   	     Date startdate=dateFormat.parse(start);
   	     Date enddate=dateFormat.parse(end);
   	     for (Avoir avoir : avoirRepo.AvoirsByMonth(startdate, enddate, AvoirStatus.Avoir_envoye)) {
   	    	 if(avoir.getFact().getType()==TypeFacture.Export) {
   	    		result+=avoir.getMontant()*avoir.getFact().getTva()/100;
   	    	 }
			
		}
   	     return result;
    	
    }
    
    private Double calculTimbreAvoir(int year,int month,TypeFacture type) {
    	double result=0;
    	 for (Facture f : factureRepo.FactureByYearAndTypeAndStatus(year,type,FactureStatus.Facture_envoye)) {
		    	double totalav=0;
	    		for (Avoir avoir : f.getAvoirs()) {
					totalav+=avoir.getMontant();
				}
	    		if(f.getTotalttc() == totalav) {
	    			result+=f.getCtvtimbre();
	    		}
	    	
		}
    	 return result;
    	
    }
    
    private double CalcultotalfactureAvoir(int year,int month,TypeFacture type) throws ParseException {
    	double result=0;
    	
    	String formattedMonth = String.format("%02d", month);
      	 String start=year+"-"+formattedMonth+"-01";
   	     String end=year+"-"+formattedMonth+"-28";
   	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
   	     Date startdate=dateFormat.parse(start);
   	     Date enddate=dateFormat.parse(end);
   	     for (Avoir avoir : avoirRepo.AvoirsByMonth(startdate,enddate,AvoirStatus.Avoir_envoye)) {
			if(avoir.getFact().getType()==type) {
				result+=avoir.getMontant();
			}
		}
   	  return result;
   	    
    }
    
    private double calculTotalFacturectv(int year,int month,FactureStatus status) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	for (Facture facture : factureRepo.FactureBydateAndTypeAndStatus(startdate, enddate,TypeFacture.Export,status)) {
			result+=facture.getCtvttc();
		}
    	return result;
    }
   	  
    private double calculTotaltvactv(int year,int month,FactureStatus status) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	for (Facture facture : factureRepo.FactureBydateAndTypeAndStatus(startdate, enddate,TypeFacture.Export,status)) {
			result+=facture.getCtvtva();
		}
    	return result;
    }
    
    private double calculTotaltimbrectv(int year,int month,FactureStatus status) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	for (Facture facture : factureRepo.FactureBydateAndTypeAndStatus(startdate, enddate,TypeFacture.Export,status)) {
			result+=facture.getCtvtimbre();
		}
    	return result;
    }
    
    private double calculTotalFacturectvByClient(int year,int month,FactureStatus status,long id) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	for (Facture facture : factureRepo.FactureBydateAndTypeAndStatusByClient(startdate, enddate,TypeFacture.Export,status,id)) {
			result+=facture.getCtvttc();
		}
    	return result;
    }
   	  
    private double calculTotaltvactvByClient(int year,int month,FactureStatus status,long id) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	for (Facture facture : factureRepo.FactureBydateAndTypeAndStatusByClient(startdate, enddate,TypeFacture.Export,status,id)) {
			result+=facture.getCtvtva();
		}
    	return result;
    }
    
    private double calculTotaltimbrectvByClient(int year,int month,FactureStatus status,long id) throws ParseException {
    	double result=0;
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	for (Facture facture : factureRepo.FactureBydateAndTypeAndStatusByClient(startdate, enddate,TypeFacture.Export,status,id)) {
			result+=facture.getCtvtimbre();
		}
    	return result;
    }
    
    
    private double calculTotalFacturectvYear(int year,FactureStatus status) throws ParseException {
    	double result=0;
    
    	
    	for (Facture facture : factureRepo.FactureByYearAndTypeAndStatus(year,TypeFacture.Export,status)) {
			result+=facture.getCtvttc();
		}
    	return result;
    }
   	  
    private double calculTotaltvactvYear(int year,FactureStatus status) throws ParseException {
    	double result=0;
    
    	for (Facture facture : factureRepo.FactureByYearAndTypeAndStatus(year,TypeFacture.Export,status)) {
			result+=facture.getCtvtva();
		}
    	return result;
    }
    
    private double calculTotaltimbrectvYear(int year,FactureStatus status) throws ParseException {
    	double result=0;
    	
    	for (Facture facture : factureRepo.FactureByYearAndTypeAndStatus(year,TypeFacture.Export,status)) {
			result+=facture.getCtvtimbre();
		}
    	return result;
    }
    
    
    
    private double calculTotalFacturectvYearByClient(int year,FactureStatus status,long id) throws ParseException {
    	double result=0;
    
    	
    	for (Facture facture : factureRepo.FactureByYearAndTypeAndStatusByClient(year,TypeFacture.Export,status,id)) {
			result+=facture.getCtvttc();
		}
    	return result;
    }
   	  
    private double calculTotaltvactvYearByClient(int year,FactureStatus status,long id) throws ParseException {
    	double result=0;
    
    	for (Facture facture : factureRepo.FactureByYearAndTypeAndStatusByClient(year,TypeFacture.Export,status,id)) {
			result+=facture.getCtvtva();
		}
    	return result;
    }
    
    private double calculTotaltimbrectvYearByClient(int year,FactureStatus status,long id) throws ParseException {
    	double result=0;
    	
    	for (Facture facture : factureRepo.FactureByYearAndTypeAndStatusByClient(year,TypeFacture.Export,status,id)) {
			result+=facture.getCtvtimbre();
		}
    	return result;
    }
    
    private double Calcultfactureavoirctv(int year,int month) throws ParseException {
    	double result=0;
    	
    	String formattedMonth = String.format("%02d", month);
      	 String start=year+"-"+formattedMonth+"-01";
   	     String end=year+"-"+formattedMonth+"-28";
   	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
   	     Date startdate=dateFormat.parse(start);
   	     Date enddate=dateFormat.parse(end);
   	     for (Avoir avoir : avoirRepo.AvoirsByMonth(startdate,enddate,AvoirStatus.Avoir_envoye)) {
			if(avoir.getFact().getType()==TypeFacture.Export) {
				result+=avoir.getMontant()/avoir.getCours();
			}
		}
   	     return result;
    }
    
    private double calculTvaSumAvoireuroctv(int year,int month) throws ParseException {
    	double result=0;
    	String formattedMonth = String.format("%02d", month);
      	 String start=year+"-"+formattedMonth+"-01";
   	     String end=year+"-"+formattedMonth+"-28";
   	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
   	     Date startdate=dateFormat.parse(start);
   	     Date enddate=dateFormat.parse(end);
   	     for (Avoir avoir : avoirRepo.AvoirsByMonth(startdate, enddate, AvoirStatus.Avoir_envoye)) {
   	    	 if(avoir.getFact().getType()==TypeFacture.Export) {
   	    		result+=(avoir.getMontant()*avoir.getFact().getTva()/100)/avoir.getCours();
   	    	 }
			
		}
   	     return result;
    	
    }
    
    private long NomberAvoir(int year,int month) throws ParseException {
    	String formattedMonth = String.format("%02d", month);
     	 String start=year+"-"+formattedMonth+"-01";
  	     String end=year+"-"+formattedMonth+"-28";
  	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
  	     Date startdate=dateFormat.parse(start);
  	     Date enddate=dateFormat.parse(end);
  	      return avoirRepo.AvoirsByMonth(startdate, enddate, AvoirStatus.Avoir_envoye).size();
    }
   	
    private long NomberFacture(int year,int month) throws ParseException {
    	String formattedMonth = String.format("%02d", month);
    	 String start=year+"-"+formattedMonth+"-01";
 	     String end=year+"-"+formattedMonth+"-28";
 	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
 	     Date startdate=dateFormat.parse(start);
 	     Date enddate=dateFormat.parse(end);
 	     long result=factureRepo.FactureBydateAndTypeAndStatus(startdate, enddate, TypeFacture.Export, FactureStatus.Facture_envoye).size();
 	     result+=factureRepo.FactureBydateAndTypeAndStatus(startdate, enddate, TypeFacture.National, FactureStatus.Facture_envoye).size();
 	     
 	     return result;
    }
    
    
    private long NomberAvoirYear(int year) throws ParseException {
   
  	      return avoirRepo.AvoirsByYear(year, AvoirStatus.Avoir_envoye).size();
    }
   	
    private long NomberFactureYear(int year) throws ParseException {
    	
 	     long result=factureRepo.FactureByYearAndTypeAndStatus(year, TypeFacture.Export, FactureStatus.Facture_envoye).size();
 	     result+=factureRepo.FactureByYearAndTypeAndStatus(year, TypeFacture.National, FactureStatus.Facture_envoye).size();
 	     return result;
    }
    
   private List<FactureDateAndNumber> getAllFactures(Date startdate, Date enddate){
	   List<FactureDateAndNumber> result=factureRepo.FactureBydate(startdate, enddate);
	   result.addAll(avoirRepo.AvoirBydate(startdate, enddate));
	   Collections.sort(result, Comparator.comparing(FactureDateAndNumber::getNumber));
	   return result;
	   
   }
   
   private List<FactureDateAndNumber> getAllFacturesByYear(int year){
	   List<FactureDateAndNumber> result=factureRepo.FactureByYear(year);
	   result.addAll(avoirRepo.AvoirByYear(year));
	   Collections.sort(result, Comparator.comparing(FactureDateAndNumber::getNumber));
	   return result;
	   
   }
   	   
    	
   
    public FacturesStat calculFacturesStatMonth(int year,int month) throws ParseException {
    	FacturesStat result = new FacturesStat();
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	     result.setTotalfacturefacturetnd(getTotalSomme(year, month, TypeFacture.National, FactureStatus.Facture_envoye));
    	    result.setTotaltvafacturetnd(getTVASomme(year, month, TypeFacture.National, FactureStatus.Facture_envoye));
    	    result.setTotaltimbrefacturetnd(getTimbreSomme(year, month, TypeFacture.National, FactureStatus.Facture_envoye));
    	    result.setTotalfactureavoirtnd(CalcultotalfactureAvoir(year,month,TypeFacture.National));
    	    result.setTotaltvaavoirtnd(calculTvaSumAvoirtnd(year,month));
    	    result.setTotaltimbreavoirtnd(calculTimbreAvoir(year,month,TypeFacture.National));
    	    result.setTotaltnd(result.getTotalfacturefacturetnd()-result.getTotalfactureavoirtnd());
    	    result.setTotaltvatnd(result.getTotaltvafacturetnd()-result.getTotaltvaavoirtnd());
    	    result.setTotaltimbretnd(result.getTotaltimbrefacturetnd()-result.getTotaltimbreavoirtnd());
    	    
    	    result.setTotalfacturefactureeuro(getTotalSomme(year, month, TypeFacture.Export, FactureStatus.Facture_envoye));
    	    result.setTotaltvafactureeuro(getTVASomme(year, month, TypeFacture.Export, FactureStatus.Facture_envoye));
    	    result.setTotaltimbrefactureeuro(getTimbreSomme(year, month, TypeFacture.Export, FactureStatus.Facture_envoye));
    	    result.setTotalfactureavoireuro(CalcultotalfactureAvoir(year,month,TypeFacture.Export));
    	    result.setTotaltvaavoireuro(calculTvaSumAvoireuro(year,month));
    	    result.setTotaltimbreavoireuro(calculTimbreAvoir(year,month,TypeFacture.Export));
    	    result.setTotaleuro(result.getTotalfacturefactureeuro()-result.getTotalfactureavoireuro());
    	    result.setTotaltvaeuro(result.getTotaltvafactureeuro()-result.getTotaltvaavoireuro());
    	    result.setTotaltimbreeuro(result.getTotaltimbrefactureeuro()-result.getTotaltimbreavoireuro());
    	    
    	    result.setTotalfacturefactureeuroctv(calculTotalFacturectv(year,month,FactureStatus.Facture_envoye));
    	    result.setTotaltvafactureeuroctv(calculTotaltvactv(year,month,FactureStatus.Facture_envoye));
    	    result.setTotaltimbrefactureeuroctv(calculTotaltimbrectv(year,month,FactureStatus.Facture_envoye));
    	    result.setTotalfactureavoireuroctv(Calcultfactureavoirctv(year,month));
    	    result.setTotaltvaavoireuroctv(calculTvaSumAvoireuroctv(year,month));
    	    result.setTotaltimbreavoireuroctv(calculTimbreAvoir(year,month,TypeFacture.Export));
    	    result.setTotaleuroctv(result.getTotalfacturefactureeuroctv()-result.getTotalfactureavoireuroctv());
    	    result.setTotaltvaeuroctv(result.getTotaltvafactureeuroctv()-result.getTotaltvaavoireuroctv());
    	    result.setTotaltimbreeuroctv(result.getTotaltimbrefactureeuroctv()-result.getTotaltimbreavoireuroctv());
    	    
    	    result.setNombreavoirs(NomberAvoir(year, month));
    	    System.out.println("hay result 2======"+NomberFacture(year, month));
    	    result.setNombrefactures(NomberFacture(year, month));
    	    result.setFactures(getAllFactures(startdate, enddate));
    	    result.setNombrefacturestotal(result.getNombreavoirs()+result.getNombrefactures());
    	    
    	    result.setTotalfact(result.getTotaltnd()+result.getTotaleuroctv());
    	    result.setTotaltva(result.getTotaltvatnd()+result.getTotaltvaeuroctv());
    	    result.setTotaltim(result.getTotaltimbretnd()-result.getTotaltimbreeuroctv());
    	    
    	    
    	    return result;
    	
    }
    

    public FacturesStat calculProformasStatMonth(int year,int month) throws ParseException {
    	FacturesStat result = new FacturesStat();
    	 String formattedMonth = String.format("%02d", month);
       	 String start=year+"-"+formattedMonth+"-01";
    	     String end=year+"-"+formattedMonth+"-28";
    	     SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    	     Date startdate=dateFormat.parse(start);
    	     Date enddate=dateFormat.parse(end);
    	     result.setTotalfacturefacturetnd(getTotalSomme(year, month, TypeFacture.National, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltvafacturetnd(getTVASomme(year, month, TypeFacture.National, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltimbrefacturetnd(getTimbreSomme(year, month, TypeFacture.National, FactureStatus.Proforma_envoyee_validee));
    	    
    	    result.setTotaltnd(result.getTotalfacturefacturetnd());
    	    result.setTotaltvatnd(result.getTotaltvafacturetnd());
    	    result.setTotaltimbretnd(result.getTotaltimbrefacturetnd());
    	
    	    
    	    result.setTotalfacturefactureeuro(getTotalSomme(year, month, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltvafactureeuro(getTVASomme(year, month, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltimbrefactureeuro(getTimbreSomme(year, month, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee));
    	    
    	    result.setTotaleuro(result.getTotalfacturefactureeuro());
    	    result.setTotaltvaeuro(result.getTotaltvafactureeuro());
    	    result.setTotaltimbreeuro(result.getTotaltimbrefactureeuro());
    	    
    	
    	    
    	    result.setTotalfacturefactureeuroctv(calculTotalFacturectv(year,month,FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltvafactureeuroctv(calculTotaltvactv(year,month,FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltimbrefactureeuroctv(calculTotaltimbrectv(year,month,FactureStatus.Proforma_envoyee_validee));
    	    
    	    result.setTotaleuroctv(result.getTotalfacturefactureeuroctv());
    	    result.setTotaltvaeuroctv(result.getTotaltvafactureeuroctv());
    	    result.setTotaltimbreeuroctv(result.getTotaltimbrefactureeuroctv());
    	    
    	    result.setNombreavoirs(NomberAvoir(year, month));
    	    result.setNombrefactures(NomberFacture(year, month));
    	    result.setNombrefacturestotal(result.getNombreavoirs()+result.getNombrefactures());
    	    result.setFactures(getAllFactures(startdate, enddate));
    	    
    	    result.setTotalfact(result.getTotalfacturefacturetnd()+result.getTotalfacturefactureeuroctv());
    	    
    	    result.setTotaltva(result.getTotaltvafacturetnd()+result.getTotaltvafactureeuroctv());
    	    result.setTotaltim(result.getTotaltimbrefacturetnd()-result.getTotaltimbrefactureeuroctv());
    	    
    	    
    	    return result;
    	
    }
    
    
    public FacturesStat calculProformasStatYear(int year) throws ParseException {
    	FacturesStat result = new FacturesStat();
    	     result.setTotalfacturefacturetnd(getTotalSommeYear(year, TypeFacture.National, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltvafacturetnd(getTVASommeYear(year, TypeFacture.National, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltimbrefacturetnd(getTimbreSommeYear(year,  TypeFacture.National, FactureStatus.Proforma_envoyee_validee));
    	    
    	    result.setTotaltnd(result.getTotalfacturefacturetnd());
    	    result.setTotaltvatnd(result.getTotaltvafacturetnd());
    	    result.setTotaltimbretnd(result.getTotaltimbrefacturetnd());
    	    
    	    result.setTotalfacturefactureeuro(getTotalSommeYear(year, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltvafactureeuro(getTVASommeYear(year, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltimbrefactureeuro(getTimbreSommeYear(year, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee));
    	    
    	   
    	    
    	    result.setTotaleuro(result.getTotalfacturefactureeuro());
    	    result.setTotaltvaeuro(result.getTotaltvafactureeuro());
    	    result.setTotaltimbreeuro(result.getTotaltimbrefactureeuro());
    	
    	    
    	    result.setTotalfacturefactureeuroctv(calculTotalFacturectvYear(year,FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltvafactureeuroctv(calculTotaltvactvYear(year,FactureStatus.Proforma_envoyee_validee));
    	    result.setTotaltimbrefactureeuroctv(calculTotaltimbrectvYear(year,FactureStatus.Proforma_envoyee_validee));
    	   
    	    result.setTotaleuroctv(result.getTotalfacturefactureeuroctv());
    	    result.setTotaltvaeuroctv(result.getTotaltvafactureeuroctv());
    	    result.setTotaltimbreeuroctv(result.getTotaltimbrefactureeuroctv());
    	   
    	    
    	    result.setNombreavoirs(NomberAvoirYear(year));
    	    result.setNombrefactures(NomberFactureYear(year));
    	    result.setNombrefacturestotal(result.getNombreavoirs()+result.getNombrefactures());
    	    
    	    
    	    result.setTotalfact(result.getTotalfacturefacturetnd()+result.getTotalfacturefactureeuroctv());
    	    result.setTotaltva(result.getTotaltvafacturetnd()+result.getTotaltvafactureeuroctv());
    	    result.setTotaltim(result.getTotaltimbrefacturetnd()-result.getTotaltimbrefactureeuroctv());
    	    
    	    
    	    return result;
    	
    }
    
    
    
    
    
    public FacturesStat calculProformasStatMonthByClient(int year,int month,long id) throws ParseException {
    	FacturesStat result = new FacturesStat();
    	 
    	     result.setTotalfacturefacturetnd(getTotalSommeByClient(year, month, TypeFacture.National, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltvafacturetnd(getTVASommeByClient(year, month, TypeFacture.National, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltimbrefacturetnd(getTimbreSommeByClient(year, month, TypeFacture.National, FactureStatus.Proforma_envoyee_validee,id));
    	    
    	    result.setTotaltnd(result.getTotalfacturefacturetnd());
    	    result.setTotaltvatnd(result.getTotaltvafacturetnd());
    	    result.setTotaltimbretnd(result.getTotaltimbrefacturetnd());
    	    
    	    result.setTotalfacturefactureeuro(getTotalSommeByClient(year, month, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltvafactureeuro(getTVASommeByClient(year, month, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltimbrefactureeuro(getTimbreSommeByClient(year, month, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee,id));
    	    
    	    result.setTotaleuro(result.getTotalfacturefactureeuro());
    	    result.setTotaltvaeuro(result.getTotaltvafactureeuro());
    	    result.setTotaltimbreeuro(result.getTotaltimbrefactureeuro());
    	
    	    
    	    result.setTotalfacturefactureeuroctv(calculTotalFacturectvByClient(year,month,FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltvafactureeuroctv(calculTotaltvactvByClient(year,month,FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltimbrefactureeuroctv(calculTotaltimbrectvByClient(year,month,FactureStatus.Proforma_envoyee_validee,id));
    	    
    	    result.setTotaleuroctv(result.getTotalfacturefactureeuroctv());
    	    result.setTotaltvaeuroctv(result.getTotaltvafactureeuroctv());
    	    result.setTotaltimbreeuroctv(result.getTotaltimbrefactureeuroctv());
    	    
    	    result.setNombreavoirs(NomberAvoir(year, month));
    	    result.setNombrefactures(NomberFacture(year, month));
    	    result.setNombrefacturestotal(result.getNombreavoirs()+result.getNombrefactures());
    	    
    	    
    	    result.setTotalfact(result.getTotalfacturefacturetnd()+result.getTotalfacturefactureeuroctv());
    	    result.setTotaltva(result.getTotaltvafacturetnd()+result.getTotaltvafactureeuroctv());
    	    result.setTotaltim(result.getTotaltimbrefacturetnd()-result.getTotaltimbrefactureeuroctv());
    	    
    	    
    	    return result;
    	
    }
    
    
    
    public FacturesStat calculProformasStatYearByClient(int year,long id) throws ParseException {
    	FacturesStat result = new FacturesStat();
    	 
    	     result.setTotalfacturefacturetnd(getTotalSommeYearByClient(year, TypeFacture.National, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltvafacturetnd(getTVASommeYearByClient(year, TypeFacture.National, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltimbrefacturetnd(getTimbreSommeYearByClient(year,  TypeFacture.National, FactureStatus.Proforma_envoyee_validee,id));
    	    
    	    result.setTotaltnd(result.getTotalfacturefacturetnd());
    	    result.setTotaltvatnd(result.getTotaltvafacturetnd());
    	    result.setTotaltimbretnd(result.getTotaltimbrefacturetnd());
    	
    	    
    	    result.setTotalfacturefactureeuro(getTotalSommeYearByClient(year, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltvafactureeuro(getTVASommeYearByClient(year, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltimbrefactureeuro(getTimbreSommeYearByClient(year, TypeFacture.Export, FactureStatus.Proforma_envoyee_validee,id));
    	    
    	    result.setTotaleuro(result.getTotalfacturefactureeuro());
    	    result.setTotaltvaeuro(result.getTotaltvafactureeuro());
    	    result.setTotaltimbreeuro(result.getTotaltimbrefactureeuro());
    	
    	    System.out.println("hadhay result mtaa client=="+calculTotaltimbrectvYearByClient(year,FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotalfacturefactureeuroctv(calculTotalFacturectvYearByClient(year,FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltvafactureeuroctv(calculTotalFacturectvYearByClient(year,FactureStatus.Proforma_envoyee_validee,id));
    	    result.setTotaltimbrefactureeuroctv(calculTotaltimbrectvYearByClient(year,FactureStatus.Proforma_envoyee_validee,id));
    	    
    	    result.setTotaleuroctv(result.getTotalfacturefactureeuroctv());
    	    result.setTotaltvaeuroctv(result.getTotaltvafactureeuroctv());
    	    result.setTotaltimbreeuroctv(result.getTotaltimbrefactureeuroctv());
    	    
    	    result.setNombreavoirs(NomberAvoirYear(year));
    	    result.setNombrefactures(NomberFactureYear(year));
    	    result.setNombrefacturestotal(result.getNombreavoirs()+result.getNombrefactures());
    	    
    	    
    	    result.setTotalfact(result.getTotalfacturefacturetnd()+result.getTotalfacturefactureeuroctv());
    	    result.setTotaltva(result.getTotaltvafacturetnd()+result.getTotaltvafactureeuroctv());
    	    result.setTotaltim(result.getTotaltimbrefacturetnd()-result.getTotaltimbrefactureeuroctv());
    	    
    	    
    	    return result;
    	
    }
    
     public List<Facture> getFacturePayed(){
    	List<Facture> result=factureRepo.findByPayementstatus(FacturePayementStatus.Paye);
    	result.addAll(factureRepo.findByPayementstatus(FacturePayementStatus.Paye_Partiel));
    	for(int i=0;i<result.size();i++) {
    		if(result.get(i).getRetenuepath() == null || result.get(i).getRetenuepath() == "") {
    			result.remove(i);
    		}
    	}
    	Collections.sort(result, Comparator.comparing(Facture::getNumber).reversed());
    	return result;
    			}
    
   
   
    
   
}
    


    

