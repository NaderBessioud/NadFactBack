package tn.famytech.esprit.Services;


import java.io.BufferedReader;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import com.itextpdf.text.pdf.security.PrivateKeySignature;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import tn.famytech.esprit.DTO.FactureDateAndList;
import tn.famytech.esprit.DTO.FactureListAndPDF;
import tn.famytech.esprit.Entites.Avoir;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.FacturePayementStatus;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Repositories.AvoirRepo;
import tn.famytech.esprit.Repositories.FactureRepo;
import tn.famytech.esprit.Repositories.PersonelRepo;
import java.util.Optional;
import java.util.stream.Stream;


@Service
public class AvoirService {
	
	private final String bqe="123";
	private final String iban="123";
	private final String identifiantf="123";
	private final String ribeuro="123";
	private final String ribtnd="123";
	private final String siegesocial="mourouj";
	private static final String[] units = { "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf" };
    private static final String[] dizaines = {"dix", "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf" };
    private static final String[] dizaines1 = { "", "", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingts", "quatre-vingt-dix" };
    private static final String[] quatreVingtDix = {"", "quatre-vingt-onze", "quatre-vingt-douze", "quatre-vingt-treize", "quatre-vingt-quatorze", "quatre-vingt-quinze", "quatre-vingt-seize", "quatre-vingt-dix-sept", "quatre-vingt-dix-huit", "quatre-vingt-dix-neuf"};
    private static final String[] soixanteDix = {"", "soixante et onze", "soixante-douze", "soixante-treize", "soixante-quatorze", "soixante-quinze", "soixante-seize", "soixante-dix-sept", "soixante-dix-huit", "soixante-dix-neuf"};
    private static final String[] milliers = { "", "mille", "million", "milliard" };
	
	@Autowired
	private AvoirRepo avoirRepo;
	@Autowired
	private FactureRepo factureRepo;
	
	@Autowired
	private PersonelRepo personelRepo;
	@Autowired
    private TemplateEngine templateEngine;
	
	@Autowired
	private  JavaMailSender mailSender;
	
	public Avoir findByNumberAndStatus(long number,AvoirStatus status) {
		return avoirRepo.findByNumberAndStatus(number,status);
	}
	
	public void addAvoir(Avoir avoir,long idf) {
		
		double sum=0;
		Facture facture=factureRepo.findById(idf).get();
		for (Avoir a : facture.getAvoirs()) {
			sum+=a.getMontant();
		}
		if(sum+avoir.getMontant()>facture.getTotalrestant() || avoir.getMontant() > facture.getTotalrestant()) {
			
		}
		else {
		avoir.setFact(facture);
		
		avoir.setDatecv(new Date());
		avoir.setStatus(AvoirStatus.Avoir);
		avoir= avoirRepo.save(avoir);
		facture.getAvoirs().add(avoir);
		facture.setTotalrestant(facture.getTotalrestant()-avoir.getMontant());
		factureRepo.save(facture);
		Facture profoma=factureRepo.findByNumberAndStatus(facture.getRef(),FactureStatus.Proforma_envoyee_validee);
		profoma.setTraited(false);
		factureRepo.save(profoma);
		
		}
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
	public String getValidDate() throws ParseException {
    	
		 LocalDateTime date = LocalDateTime.now();
		 Calendar calendar = Calendar.getInstance();
	        int currentYear = calendar.get(Calendar.YEAR);
		 if(factureRepo.getCanceledInvoice(currentYear).size() != 0) {
			 Instant instant = factureRepo.getCanceledInvoice(currentYear).get(0).getDateemission().toInstant();
			 date=LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            return sdf.format(finalDate);
	            
		}
	           
	        
	    }
	 

	
	public long AvoirNumber() {
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
        	
        	number =number1;
        	
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
	
	
	 public ByteArrayOutputStream generateAvoir(Avoir A) throws DocumentException, IOException {
	    	
	    	Date today = new Date();
	    	String numberString ="";
	        BigDecimal totalBigDecimal;
	   	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	   	Calendar calendar = Calendar.getInstance();
	    int currentYear = calendar.get(Calendar.YEAR);	
	    		A.setNumber(AvoirNumber());	
	   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	   	if(A.getDateemission() == null) {
		try {
			A.setDateemission(dateFormat.parse(getValidDate()));
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
	           
	           Paragraph paragraph = new Paragraph("Avoir"+" N "+A.getNumber()+"/"+currentYear, font);
	          
	           paragraph.setAlignment(Element.ALIGN_CENTER);

	           // Add the Paragraph to the document
	           document.add(paragraph);
	           document.add(Chunk.NEWLINE);
	           
	           Paragraph paragraphAv = new Paragraph("Avoir sur la facture N:"+A.getFact().getNumber()+"/"+currentYear+" "+dateFormat.format(A.getFact().getDateemission()));
		          
	           

	           // Add the Paragraph to the document
	           document.add(paragraphAv);
	           document.add(Chunk.NEWLINE);
	           
	           Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

	           // Create a Paragraph with centered text
	           Paragraph paragraph1 = new Paragraph("date d'emission  "+dateFormat.format(A.getDateemission()), fontd);
	          
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
	           PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré"));
	           ClienInfo.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo);
	           String exo="non";
	           PdfPCell ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo));
	           if(A.getFact().getClient().getExonere() && A.getFact().getClient().getDateexonere().after(new Date())) {
	        	   exo="oui";
	        	   ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré\ndate d'exonoration"));
	        	   ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo+"\n"+A.getFact().getClient().getDateexonere()));
	           }
	           
	           
	           ClienInfo1.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo1);
	           
	           PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
	           ClienInfo2.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo2);
	          
	           PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(A.getFact().getRef())));
	           ClienInfo3.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo3);
	           tableClient.setSpacingAfter(40f);
	           document.add(tableClient);

	           Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
	           PdfPTable tableDetails = new PdfPTable(2); 
	           tableDetails.setWidthPercentage(100);
	           PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
	          
	           Details1.setBorderWidth(1f);
	           tableDetails.addCell(Details1);
	          
	           PdfPCell Total = new PdfPCell(new Phrase("Total DT",fontZ2));
	           Total.setHorizontalAlignment(Element.ALIGN_CENTER);
	           Total.setBorderWidth(1f);
	           tableDetails.addCell(Total);
       
	           PdfPCell Details11 = new PdfPCell(new Phrase("Avoir"));
	           Details11.setBorderWidth(1f);
	           tableDetails.addCell(Details11);
	          

	           totalBigDecimal = BigDecimal.valueOf((A.getMontant()));
		       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

		       numberString = totalBigDecimal.toPlainString();
	           PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
	           Total1.setBorderWidth(1f);
	           tableDetails.addCell(Total1);
	           

	           
	           float[] columnWidthsD = {300f,100f}; // Change these values as needed

	           tableDetails.setWidths(columnWidthsD);
	           tableDetails.setSpacingAfter(20f);
	           document.add(tableDetails);
	           
	           PdfPTable tableTotal = new PdfPTable(2); // 2 columns
	           tableTotal.setWidthPercentage(100);
	           
	           PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
	           TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
	           TotalTTCL.setBorderWidth(1f);
	           tableTotal.addCell(TotalTTCL);
	           totalBigDecimal = BigDecimal.valueOf(A.getMontant());
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

	           paragraph2.add(new Phrase(TotalEnLettre(A.getMontant(),A.getFact().getType())));
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
	 	
	 
	 public ByteArrayOutputStream generateAvoirEuro(Avoir A) throws DocumentException, IOException {
	    	
	    	
	    	String numberString ="";
	        BigDecimal totalBigDecimal;
	   	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	   	Calendar calendar = Calendar.getInstance();
	    int currentYear = calendar.get(Calendar.YEAR);	
	    A.setNumber(AvoirNumber());		
	   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	   	if(A.getDateemission() == null) {
		try {
			A.setDateemission(dateFormat.parse(getValidDate()));
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
	           Paragraph paragraph = new Paragraph("Avoir"+" N "+A.getNumber()+"/"+currentYear, font);
	          
	           paragraph.setAlignment(Element.ALIGN_CENTER);

	           // Add the Paragraph to the document
	           document.add(paragraph);
	           document.add(Chunk.NEWLINE);
	           
	           Paragraph paragraphAv = new Paragraph("Avoir sur la facture N:"+A.getFact().getNumber()+"/"+currentYear+" "+dateFormat.format(A.getFact().getDateemission()));
		          
	           

	           // Add the Paragraph to the document
	           document.add(paragraphAv);
	           document.add(Chunk.NEWLINE);
	           
	           Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

	           // Create a Paragraph with centered text
	           Paragraph paragraph1 = new Paragraph("date d'emission  "+dateFormat.format(A.getDateemission()), fontd);
	          
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
	           PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré"));
	           ClienInfo.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo);
	           String exo="non";
	           PdfPCell ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo));
	           if(A.getFact().getClient().getExonere() && A.getFact().getClient().getDateexonere().after(new Date())) {
	        	   exo="oui";
	        	   ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré\ndate d'exonoration"));
	        	   ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo+"\n"+A.getFact().getClient().getDateexonere()));
	           }
	           
	           
	           ClienInfo1.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo1);
	           
	           PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
	           ClienInfo2.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo2);
	          
	           PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(A.getFact().getRef())));
	           ClienInfo3.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo3);
	           tableClient.setSpacingAfter(40f);
	           document.add(tableClient);

	           Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
	           PdfPTable tableDetails = new PdfPTable(2); // 2 columns
	           tableDetails.setWidthPercentage(100);
	           PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
	          
	           Details1.setBorderWidth(1f);
	           tableDetails.addCell(Details1);
	          
	           PdfPCell Total = new PdfPCell(new Phrase("Total DT",fontZ2));
	           Total.setHorizontalAlignment(Element.ALIGN_CENTER);
	           Total.setBorderWidth(1f);
	           tableDetails.addCell(Total);
    
	           PdfPCell Details11 = new PdfPCell(new Phrase("Avoir"));
	           Details11.setBorderWidth(1f);
	           tableDetails.addCell(Details11);
	          

	           totalBigDecimal = BigDecimal.valueOf((A.getMontant()));
		       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

		       numberString = totalBigDecimal.toPlainString();
	           PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
	           Total1.setBorderWidth(1f);
	           tableDetails.addCell(Total1);
	           

	           
	           float[] columnWidthsD = {300f,100f}; // Change these values as needed

	           tableDetails.setWidths(columnWidthsD);
	           tableDetails.setSpacingAfter(20f);
	           document.add(tableDetails);
	           
	           PdfPTable tableTotal = new PdfPTable(2); // 2 columns
	           tableTotal.setWidthPercentage(100);
	           
	           PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
	           TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
	           TotalTTCL.setBorderWidth(1f);
	           tableTotal.addCell(TotalTTCL);
	           totalBigDecimal = BigDecimal.valueOf(A.getMontant());
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

	           paragraph2.add(new Phrase(TotalEnLettre(A.getMontant(),A.getFact().getType())));
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
	              		
	              		+ "CTV TTC TND",fontZ1));
	              TotalL.setBorderWidth(1f);
	              tableTotalEuro.addCell(TotalL);
	              totalBigDecimal = BigDecimal.valueOf(A.getCours());
	              
	              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	   	         numberString = totalBigDecimal.toPlainString();
	   	         

				         
				         BigDecimal totalBigDecimal4 = BigDecimal.valueOf(A.getMontant()*A.getCours());
				          
				          totalBigDecimal4 = totalBigDecimal4.setScale(3, RoundingMode.DOWN);

					         String numberString4 = totalBigDecimal4.toPlainString();
	              PdfPCell TotalHT = new PdfPCell(new Phrase(dateFormat.format(A.getDatecv())+"\r\r\n"+numberString+" TND"+"\r\r\n\n"+
	            "\r\r\n"+numberString4+" TND",fontZ1));
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
	 
	 public void updateAvoirPdfPath(long id,String path) {
		 Avoir avoir=avoirRepo.findById(id).get();
		 avoir.setPdfpath(path);
		 avoirRepo.save(avoir);
	 }
	 
	   public  byte[] getQRCodeImage(long id, int width, int height) throws WriterException, IOException {
	        QRCodeWriter qrCodeWriter = new QRCodeWriter();
	        
	        String URL ="http://192.168.1.31:8082/ERPPro/home";
	        String mapping ="facturation/previewFacture/"+id;
	        updateAvoirPdfPath(id, hashString(mapping));
	        BitMatrix bitMatrix = qrCodeWriter.encode(URL+hashString(mapping), BarcodeFormat.QR_CODE, width, height);

	        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
	     //   MatrixToImageConfig con = new MatrixToImageConfig( 0xFF000002 , 0xFFFFC041 ) ;
	        MatrixToImageConfig con = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);


	        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream,con);
	        byte[] pngData = pngOutputStream.toByteArray();
	        return pngData;
	    }
	 
	 
	 public ByteArrayOutputStream generateAvoirAfterSign(Avoir A) throws DocumentException, IOException, WriterException {
	    	
	    	Date today = new Date();
	    	String numberString ="";
	        BigDecimal totalBigDecimal;
	   	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	   	Calendar calendar = Calendar.getInstance();
	    int currentYear = calendar.get(Calendar.YEAR);	
	   	
	   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	   	if(A.getDateemission() == null) {
		try {
			A.setDateemission(dateFormat.parse(getValidDate()));
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
	           Paragraph paragraph = new Paragraph("Avoir"+" N "+A.getNumber()+"/"+currentYear, font);
	          
	           paragraph.setAlignment(Element.ALIGN_CENTER);

	           // Add the Paragraph to the document
	           document.add(paragraph);
	           document.add(Chunk.NEWLINE);
	           
	           Paragraph paragraphAv = new Paragraph("Avoir sur la facture N:"+A.getFact().getNumber()+"/"+currentYear+" "+dateFormat.format(A.getFact().getDateemission()));
		          
	           

	           // Add the Paragraph to the document
	           document.add(paragraphAv);
	           document.add(Chunk.NEWLINE);
	           
	           Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

	           // Create a Paragraph with centered text
	           Paragraph paragraph1 = new Paragraph("date d'emission  "+dateFormat.format(A.getDateemission()), fontd);
	          
	           paragraph1.setAlignment(Element.ALIGN_RIGHT);

	           // Add the Paragraph to the document
	           document.add(paragraph1);
	           
	           Paragraph space = new Paragraph(); // Adjust the spacing as needed
	           space.setSpacingAfter(10f);
	           document.add(space);

	           PdfPTable table = new PdfPTable(2); // 2 columns
	           table.setWidthPercentage(100);

	           // Add image to the first cell
	           
	           com.itextpdf.text.Image image =downloadAndUseImage();
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
	           PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré"));
	           ClienInfo.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo);
	           String exo="non";
	           PdfPCell ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo));
	           if(A.getFact().getClient().getExonere() && A.getFact().getClient().getDateexonere().after(new Date())) {
	        	   exo="oui";
	        	   ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré\ndate d'exonoration"));
	        	   ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo+"\n"+A.getFact().getClient().getDateexonere()));
	           }
	           
	           
	           ClienInfo1.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo1);
	           
	           PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
	           ClienInfo2.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo2);
	          
	           PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(A.getFact().getRef())));
	           ClienInfo3.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo3);
	           tableClient.setSpacingAfter(40f);
	           document.add(tableClient);

	           Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
	           PdfPTable tableDetails = new PdfPTable(2); // 2 columns
	           tableDetails.setWidthPercentage(100);
	           PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
	          
	           Details1.setBorderWidth(1f);
	           tableDetails.addCell(Details1);
	          
	           PdfPCell Total = new PdfPCell(new Phrase("Total DT",fontZ2));
	           Total.setHorizontalAlignment(Element.ALIGN_CENTER);
	           Total.setBorderWidth(1f);
	           tableDetails.addCell(Total);
    
	           PdfPCell Details11 = new PdfPCell(new Phrase("Avoir"));
	           Details11.setBorderWidth(1f);
	           tableDetails.addCell(Details11);
	          

	           totalBigDecimal = BigDecimal.valueOf((A.getMontant()));
		       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

		       numberString = totalBigDecimal.toPlainString();
	           PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
	           Total1.setBorderWidth(1f);
	           tableDetails.addCell(Total1);
	           

	           
	           float[] columnWidthsD = {300f,100f}; // Change these values as needed

	           tableDetails.setWidths(columnWidthsD);
	           tableDetails.setSpacingAfter(20f);
	           document.add(tableDetails);
	           
	           PdfPTable tableTotal = new PdfPTable(2); // 2 columns
	           tableTotal.setWidthPercentage(100);
	           
	           PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
	           TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
	           TotalTTCL.setBorderWidth(1f);
	           tableTotal.addCell(TotalTTCL);
	           totalBigDecimal = BigDecimal.valueOf(A.getMontant());
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

	           paragraph2.add(new Phrase(TotalEnLettre(A.getMontant(),A.getFact().getType())));
	           MontantL.addElement(paragraph2);
	           tableMontantL.addCell(MontantL);
	           
	         
	           float[] columnWidthsML = {300f};
	           tableMontantL.setWidths(columnWidthsML);
	           tableMontantL.setSpacingAfter(30f);
	           document.add(tableMontantL);
	           tableMontantL.setSpacingAfter(20f);
	           
	           Image qrCodeImage = Image.getInstance(getQRCodeImage(A.getIdC(),100,100));
	         
	           document.add(qrCodeImage);
	           document.close();

	           // Optionally, save the ByteArrayOutputStream to a file
	          /* FileOutputStream fileOutputStream = new FileOutputStream("iTextHelloWorld.pdf");
	               outputStream.writeTo(fileOutputStream);*/
	           
				
				 return outputStream;
	   }
	 
	 public ByteArrayOutputStream generateAvoirEuroAfterSign(Avoir A) throws DocumentException, IOException, WriterException {
	    	
	    	
	    	String numberString ="";
	        BigDecimal totalBigDecimal;
	   	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	   	Calendar calendar = Calendar.getInstance();
	    int currentYear = calendar.get(Calendar.YEAR);	
	    		
	   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	   	if(A.getDateemission() == null) {
		try {
			A.setDateemission(dateFormat.parse(getValidDate()));
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
	           Paragraph paragraph = new Paragraph("Avoir"+" N "+A.getNumber()+"/"+currentYear, font);
	          
	           paragraph.setAlignment(Element.ALIGN_CENTER);

	           // Add the Paragraph to the document
	           document.add(paragraph);
	           document.add(Chunk.NEWLINE);
	           
	           Paragraph paragraphAv = new Paragraph("Avoir sur la facture N:"+A.getFact().getNumber()+"/"+currentYear+" "+dateFormat.format(A.getFact().getDateemission()));
		          
	           

	           // Add the Paragraph to the document
	           document.add(paragraphAv);
	           document.add(Chunk.NEWLINE);
	           
	           Font fontd = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

	           // Create a Paragraph with centered text
	           Paragraph paragraph1 = new Paragraph("date d'emission  "+dateFormat.format(A.getDateemission()), fontd);
	          
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
	           PdfPCell ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré"));
	           ClienInfo.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo);
	           String exo="non";
	           PdfPCell ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo));
	           if(A.getFact().getClient().getExonere() && A.getFact().getClient().getDateexonere().after(new Date())) {
	        	   exo="oui";
	        	   ClienInfo = new PdfPCell(new Phrase("Client\nAdresse\nID.Fiscal\nExonoré\ndate d'exonoration"));
	        	   ClienInfo1 = new PdfPCell(new Phrase(A.getFact().getClient().getLibelle()+"\n"
	   	           		+A.getFact().getClient().getAddresse()+"\n"+A.getFact().getClient().getIdfiscal()+"\n"+exo+"\n"+A.getFact().getClient().getDateexonere()));
	           }
	           
	           
	           ClienInfo1.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo1);
	           
	           PdfPCell ClienInfo2 = new PdfPCell(new Phrase("Référence:"));
	           ClienInfo2.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo2);
	          
	           PdfPCell ClienInfo3 = new PdfPCell(new Phrase(String.valueOf(A.getFact().getRef())));
	           ClienInfo3.setBorderWidth(1f);
	           tableClient.addCell(ClienInfo3);
	           tableClient.setSpacingAfter(40f);
	           document.add(tableClient);

	           Font fontZ2 = FontFactory.getFont(FontFactory.TIMES_BOLD, 10,Font.BOLD, BaseColor.BLACK);
	           PdfPTable tableDetails = new PdfPTable(2); // 2 columns
	           tableDetails.setWidthPercentage(100);
	           PdfPCell Details1 = new PdfPCell(new Phrase("Libellé"));
	          
	           Details1.setBorderWidth(1f);
	           tableDetails.addCell(Details1);
	          
	           PdfPCell Total = new PdfPCell(new Phrase("Total DT",fontZ2));
	           Total.setHorizontalAlignment(Element.ALIGN_CENTER);
	           Total.setBorderWidth(1f);
	           tableDetails.addCell(Total);
 
	           PdfPCell Details11 = new PdfPCell(new Phrase("Avoir"));
	           Details11.setBorderWidth(1f);
	           tableDetails.addCell(Details11);
	          

	           totalBigDecimal = BigDecimal.valueOf((A.getMontant()));
		       totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

		       numberString = totalBigDecimal.toPlainString();
	           PdfPCell Total1 = new PdfPCell(new Phrase(numberString));
	           Total1.setBorderWidth(1f);
	           tableDetails.addCell(Total1);
	           

	           
	           float[] columnWidthsD = {300f,100f}; // Change these values as needed

	           tableDetails.setWidths(columnWidthsD);
	           tableDetails.setSpacingAfter(20f);
	           document.add(tableDetails);
	           
	           PdfPTable tableTotal = new PdfPTable(2); // 2 columns
	           tableTotal.setWidthPercentage(100);
	           
	           PdfPCell TotalTTCL = new PdfPCell(new Phrase("Total TTC"));
	           TotalTTCL.setHorizontalAlignment(Element.ALIGN_RIGHT);
	           TotalTTCL.setBorderWidth(1f);
	           tableTotal.addCell(TotalTTCL);
	           totalBigDecimal = BigDecimal.valueOf(A.getMontant());
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

	           paragraph2.add(new Phrase(TotalEnLettre(A.getMontant(),A.getFact().getType())));
	           MontantL.addElement(paragraph2);
	           tableMontantL.addCell(MontantL);
	           
	         
	           float[] columnWidthsML = {300f};
	           tableMontantL.setWidths(columnWidthsML);
	           tableMontantL.setSpacingAfter(30f);
	           document.add(tableMontantL);
	           Image qrCodeImage = Image.getInstance(getQRCodeImage(A.getIdC(),100,100));
	              
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
	              		
	              		+ "CTV TTC TND",fontZ1));
	              TotalL.setBorderWidth(1f);
	              tableTotalEuro.addCell(TotalL);
	              totalBigDecimal = BigDecimal.valueOf(A.getCours());
	              
	              totalBigDecimal = totalBigDecimal.setScale(3, RoundingMode.DOWN);

	   	         numberString = totalBigDecimal.toPlainString();
	   	         

				         
				         BigDecimal totalBigDecimal4 = BigDecimal.valueOf(A.getMontant()*A.getCours());
				          
				          totalBigDecimal4 = totalBigDecimal4.setScale(3, RoundingMode.DOWN);

					         String numberString4 = totalBigDecimal4.toPlainString();
	              PdfPCell TotalHT = new PdfPCell(new Phrase(dateFormat.format(A.getDatecv())+"\r\r\n"+numberString+" TND"+"\r\r\n\n"+
	            "\r\r\n"+numberString4+" TND",fontZ1));
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
	 
	 public ByteArrayOutputStream generetaAvoirFromFacture(Avoir avoir) throws DocumentException, IOException {
		 Facture facture=factureRepo.findById(avoir.getFact().getIdF()).get();
		 avoir.setFact(facture);
		 if(facture.getType() == TypeFacture.Export)
			 return generateAvoirEuro(avoir);
		else
			return generateAvoir(avoir);
		 }
	 
	 public List<Facture> validateAvoir(long id,List<Facture> factures) {
		 Avoir avoir=avoirRepo.findById(id).get();
		 avoir.setStatus(AvoirStatus.Avoir_valide);
		 avoirRepo.save(avoir);
		 for(int i=0;i<factures.size();i++) {
			 if(factures.get(i).getIdF()==avoir.getFact().getIdF()) {
				 for(int j=0;j<factures.get(i).getAvoirs().size();j++) {
					 if(factures.get(i).getAvoirs().get(j).getIdC()== avoir.getIdC()) {
						 factures.get(i).getAvoirs().set(j, avoir);
					 }
				 }
			 }
		 }
		 return factures;
	 }
	 
	  private String loadEmailTemplate(String templateName, String libelle) throws IOException {
	        Context context = new Context();
	        context.setVariable("libelle", libelle);
	        context.setVariable("type", "Avoir");
	        

	        
	        return templateEngine.process("email/" + templateName, context);
	    }
	  
	  
	  
	  
	  
	 public List<Facture> SendAvoirMail(long id,List<Facture> factures) {
	        Avoir avoir=avoirRepo.findById(id).get();
	        List<Facture> result = new ArrayList<Facture>();
	    	
	    	List<Facture> resultProforma=new ArrayList<Facture>();
	        byte[] pdfBytes;
	      
	            try {
	               
	                String emailContent = loadEmailTemplate("sendInvoice.html", avoir.getFact().getClient().getLibelle());
	                MimeMessage mimeMessage = mailSender.createMimeMessage();
	                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	                helper.setSubject("NadFact facture");
	                helper.setFrom("naderbessioud98@gmail.com");
	                helper.setTo(avoir.getFact().getClient().getEmail());
	                helper.setText(emailContent, true);

	                if(avoir.getFact().getType() == TypeFacture.National) {
	                	pdfBytes=generateAvoirAfterSign(avoir).toByteArray();
	                }
	                else {
	                	pdfBytes=generateAvoirEuroAfterSign(avoir).toByteArray();
	                }
	                ByteArrayResource pdfAttachment = new ByteArrayResource(pdfBytes);
	                helper.addAttachment("avoir.pdf", pdfAttachment);

	                // Send email
	                mailSender.send(mimeMessage);
	                avoir.setStatus(AvoirStatus.Avoir_envoye);
	                avoir= avoirRepo.save(avoir);
	                for(int i=0;i<factures.size();i++) {
	                	if(factures.get(i).getIdF() == avoir.getFact().getIdF()) {
	                		factures.set(i, avoir.getFact());
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
	    
	    public List<Facture> SignAvoirupdate(long id,String pdfname,List<Facture> factures) {
			List<Facture> result=new ArrayList<Facture>();
			List<Facture> resultProforma=new ArrayList<Facture>();
			
			Avoir avoir=avoirRepo.findById(id).get();
			
			avoir.setPdfname(pdfname);
			avoir.setStatus(AvoirStatus.Avoir_valide);
		
			avoirRepo.save(avoir);
			for(int i=0;i<factures.size();i++) {
				if(factures.get(i).getIdF()==avoir.getFact().getIdF()) {
					factures.set(i, avoir.getFact());
				}
			}
			
			
		
			for (Facture facture1 : factures) {
				Collections.sort(facture1.getAvoirs(), Comparator.comparing(Avoir::getNumber).reversed());
				if(facture1.getStatus()==FactureStatus.Facture || facture1.getStatus()==FactureStatus.Facture_valide || facture1.getStatus()==FactureStatus.Facture_envoye  ) {
					result.add(facture1);
				}
				else  {
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
	    
	    public List<Facture> SignAvoir(long id,long idp,List<Facture> factures) {
	    	
	  
	    	try {
	    		HttpHeaders headers = new HttpHeaders();
	            headers.setContentType(MediaType.APPLICATION_PDF);
	    		Personel per=personelRepo.findById(idp).get();
	    	
	    		String name=per.getFirstname();
	    		String lastname=per.getLastname();
	    		Avoir avoir=avoirRepo.findById(id).get();
	    		A.setNumber(AvoirNumber());
	    		
	    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    		
	    		if(avoir.getFact().getType()==TypeFacture.Export)
	    			outputStream=generateAvoirEuroAfterSign(avoir);
	    		else
	    			outputStream=generateAvoirAfterSign(avoir);
	    		
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
	            
	            
	            String pdfname= uploadfile(signedPdfStream,"Avoir"+avoir.getNumber());
	        	List<Facture> result=SignAvoirupdate(avoir.getIdC(),pdfname,factures);
	    		
	             
	            

	             return result;
	             } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    	
	    	
	    }
	    
	  public void DeleteAvoir(long id) {
		  
		  Avoir avoir=avoirRepo.findById(id).get();
		  Facture f=avoir.getFact();
		  f.getAvoirs().remove(avoir);
		  avoirRepo.delete(avoir);
		  factureRepo.save(f);
	  }
	  
	  public Avoir getAvoirById(long id) {
		  return avoirRepo.findById(id).get();
	  }
	  
	  public void updateAvoir(Avoir avoir) {
		  Avoir a= avoirRepo.findById(avoir.getIdC()).get();
		  a.setCours(avoir.getCours());
		  a.setDatecv(avoir.getDatecv());
		  a.setMontant(avoir.getMontant());
		  avoirRepo.save(a);
	  }
	  
	  @Transactional
	  public List<Facture> CancelAvoir(long id,List<Facture> factures) {
		  Avoir avoir=avoirRepo.findById(id).get();
		  List<Facture> result=new ArrayList<Facture>();
			List<Facture> resultProforma=new ArrayList<Facture>();
		  Facture facture=factureRepo.findById(avoir.getFact().getIdF()).get();
		  facture.getAvoirs().remove(avoir);
		  avoirRepo.delete(avoir);
		  factureRepo.save(facture);
		  for(int i=0;i<factures.size();i++) {
			  if(factures.get(i).getIdF()==avoir.getFact().getIdF()) {
				  
				  factures.set(i,facture);
			  }
		  }
		  

			for (Facture facture1 : factures) {
				Collections.sort(facture1.getAvoirs(), Comparator.comparing(Avoir::getNumber).reversed());
				if(facture1.getStatus()==FactureStatus.Facture || facture1.getStatus()==FactureStatus.Facture_valide || facture1.getStatus()==FactureStatus.Facture_envoye  ) {
					result.add(facture1);
				}
				else  {
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
	  
	  public List<Facture> CancelAvoirValider(long id,List<Facture> factures){
		  Avoir avoir=avoirRepo.findById(id).get();
		  
		  List<Facture> result=new ArrayList<Facture>();
		List<Facture> resultProforma=new ArrayList<Facture>();
		  avoir.setStatus(AvoirStatus.Avoir);
		  avoir=avoirRepo.save(avoir);
		  
		  if(avoir.getFact().getAvoirs().size() ==0) {
			  Facture profoma=factureRepo.findByNumber(avoir.getFact().getRef());
				profoma.setTraited(true);
				factureRepo.save(profoma);
		  }
		  
		  
		  for(int i=0;i<factures.size();i++) {
			  if(factures.get(i).getIdF()==avoir.getFact().getIdF()) {
				  factures.set(i, avoir.getFact());
			  }
		  }
		  

			for (Facture facture1 : factures) {
				Collections.sort(facture1.getAvoirs(), Comparator.comparing(Avoir::getNumber).reversed());
				if(facture1.getStatus()==FactureStatus.Facture || facture1.getStatus()==FactureStatus.Facture_valide || facture1.getStatus()==FactureStatus.Facture_envoye  ) {
					result.add(facture1);
				}
				else  {
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
	    
	    


	 


}
