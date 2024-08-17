package tn.famytech.esprit.Services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tn.famytech.esprit.DTO.UserAndName;
import tn.famytech.esprit.DTO.UserUpdatePassword;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.PasswordResetToken;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Entites.UserType;
import tn.famytech.esprit.Repositories.ClientRepo;
import tn.famytech.esprit.Repositories.PasswordResetTokenRepository;
import tn.famytech.esprit.Repositories.PersonelRepo;
import tn.famytech.esprit.Repositories.UserRepository;
import tn.famytech.esprit.Security.PasswordEncoderBean;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PasswordResetTokenRepository resetTokenRepository;
	
	@Autowired
	private PasswordEncoderBean passwordencode;
	
	@Autowired
	private  JavaMailSender mailSender;
	
	@Autowired
    private TemplateEngine templateEngine;
	
	@Autowired
	private PersonelRepo personelRepo;
	
	@Autowired
	private ClientRepo clientRepo;
	
	
	
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByEmail(username);

		if(user == null) {
			
			throw new UsernameNotFoundException("Invalid username or password");
		}
			
		
		return user;
		
	}
	
	
	public List<UserAndName> AddUser(Personel user) {
		
		
		user.setPassword(passwordencode.passwordEncoder().encode(user.getPassword()));
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	        // Format the current date
	    String formattedDate = currentDate.format(formatter);
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			user.setDatecreation(dateFormat.parse(formattedDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		personelRepo.save(user);
		return DisplayUser();
	}
	
	public User getUserByEmail(String email) {
		return repository.findByEmail(email);
	}
	
	public List<UserAndName> DisplayUser(){
		List<UserAndName> result = new ArrayList<UserAndName>();
		for (User usr : (List<User>) repository.findAll()) {
			if(usr.getRole()!=UserType.Client) {
				Personel per=personelRepo.findByEmail(usr.getEmail());
				result.add(new UserAndName(usr,per.getFirstname()+" "+per.getLastname()));
			}
			else {
				Client cl=clientRepo.findById(usr.getIdU()).get();
				result.add(new UserAndName(usr,cl.getLibelle()));
			}
			
		}
		return result;
	}

	
    public  boolean createFolder(String parentFolderPath) {
        File parentFolder = new File(parentFolderPath);
        File childFolder = new File(parentFolder, "ca");

        // Check if the parent folder already exists
        if (!parentFolder.exists()) {
            // Create the parent folder
            boolean created = parentFolder.mkdirs();

            if (created) {
                System.out.println("Parent folder created successfully.");
            } else {
                System.out.println("Failed to create parent folder.");
                return false; // Exit method if failed to create parent folder
            }
        } else {
            System.out.println("Parent folder already exists.");
           
        }

        // Check if the child folder already exists
        if (!childFolder.exists()) {
            // Create the child folder inside the parent folder
            boolean created = childFolder.mkdir();

            if (created) {
                System.out.println("Child folder created successfully.");
                
                // Set execute permission for everyone
                parentFolder.setExecutable(true, false);
                childFolder.setExecutable(true, false);
                return true;
            } else {
                System.out.println("Failed to create child folder.");
            }
        } else {
            System.out.println("Child folder already exists.");
        }
        return false;
    }
 
    public static void executeCommandInDirectory(String command, String directory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.directory(new File(directory));

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = errorReader.readLine()) != null) {
                System.err.println(line); // Print error output to stderr
            }

            int exitCode = process.waitFor();
            System.out.println("Command executed with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void createUserCertif(String name,String lastname) {
    	System.out.println("Creating Certif");
    	String pass=name.toLowerCase()+lastname.toLowerCase();
    	String folderPath="C:\\Users\\ASUS\\Desktop\\PFE_Work\\"+name+"_"+lastname;
    	String opensslCommand = "openssl req -x509 -newkey rsa:4096 -keyout ca/key.pkcs1.pem -out ca/certificate.crt.pem -days 1825 -subj \"/C=TN/ST=Tunisia/L=Tunisia/O=Famytech/OU=SpringPro/CN=CA for "+name+"_"+lastname+"\" -passout pass:ca_key_password_"+pass;
    	String opensslPkcs12Command = "openssl pkcs12 -export -out ca/keystore.p12 -inkey ca/key.pkcs1.pem -in ca/certificate.crt.pem -passin pass:ca_key_password_"+pass+" -passout pass:ca_p12_password_"+pass;
    	String keytoolCommand = "keytool -importkeystore -srckeystore ca/keystore.p12 -srcstorepass ca_p12_password_"+pass+" -srcstoretype pkcs12 -alias 1 -destalias ca_alias -destkeystore keystore.jks -deststorepass keystore_password_"+pass;
    	   if(createFolder(folderPath)) {
    		   
    	   
    	    executeCommandInDirectory(opensslCommand, folderPath);
    	    executeCommandInDirectory(opensslPkcs12Command, folderPath);
    	    executeCommandInDirectory(keytoolCommand, folderPath);
    	   }
    	
    	
    }
    
	
	public List<UserAndName> updateUserPer(Personel user){
		
		Personel u=personelRepo.findById(user.getIdU()).get();
		UserType type=u.getRole();
		u.setEmail(user.getEmail());
		u.setLastname(user.getLastname());
		u.setFirstname(user.getFirstname());
		if(user.getPassword().length() != 0) {
			
			
		u.setPassword(passwordencode.passwordEncoder().encode(user.getPassword()));
		}
		u.setImage(user.getImage());
		
		u.setRole(user.getRole());
		repository.save(u);
		if(user.getRole() != UserType.Employee && type == UserType.Employee) {
			createUserCertif(user.getFirstname(),user.getLastname());
		}
		
		
		return DisplayUser();
		
	}

	
	public List<UserAndName> ActiveUserPser(Long id, List<UserAndName> users){
		
		User user=repository.findById(id).get();
		user.setActive(true);
		repository.save(user);
		if(user.getRole() != UserType.Client) {
			Personel per=personelRepo.findByEmail(user.getEmail());
			if(per.getRole() == UserType.Manager || per.getRole() == UserType.Admin )
				createUserCertif(per.getFirstname(),per.getLastname());
		}
		for(int i=0;i<users.size();i++) {
			if(id==users.get(i).getUser().getIdU()) {
				if(user.getRole() != UserType.Client) {
					Personel per=personelRepo.findByEmail(user.getEmail());
					users.set(i, new UserAndName(user,per.getFirstname()+" "+per.getLastname()));
			}
				else {
					Client cl=clientRepo.findById(id).get();
					users.set(i, new UserAndName(user,cl.getLibelle()));
				}
		}
		}
		
		
		return users;
	}
	
	
	public List<UserAndName> DesactiveUser(Long id, List<UserAndName> users){
		
		User user=repository.findById(id).get();
		user.setActive(false);
		repository.save(user);
		
		for(int i=0;i<users.size();i++) {
			if(id==users.get(i).getUser().getIdU()) {
				if(user.getRole() != UserType.Client) {
					Personel per=personelRepo.findByEmail(user.getEmail());
					users.set(i, new UserAndName(user,per.getFirstname()+" "+per.getLastname()));
			}
				else {
					Client cl=clientRepo.findById(id).get();
					users.set(i, new UserAndName(user,cl.getLibelle()));
				}
		}
		}
		return users;
	}
	
	public List<UserAndName> diplayActiveUsers(){
		List<UserAndName> result=new ArrayList<UserAndName>();
		for (User usr : repository.findByActive(true)) {
			
			if(usr.getRole() != UserType.Client) {
				Personel per=personelRepo.findByEmail(usr.getEmail());
				result.add(new UserAndName(usr,per.getFirstname()+" "+per.getLastname()));
			}
			else {
				Client cl=clientRepo.findById(usr.getIdU()).get();
				result.add(new UserAndName(usr,cl.getLibelle()));
			}
		} ;
		return result;
	}
	
	public List<UserAndName> diplayDesactiveUsers(){
		List<UserAndName> result=new ArrayList<UserAndName>();
		for (User usr : repository.findByActive(false)) {
			if(usr.getRole() != UserType.Client) {
				Personel per=personelRepo.findByEmail(usr.getEmail());
				result.add(new UserAndName(usr,per.getFirstname()+" "+per.getLastname()));
			}
			else {
				Client cl=clientRepo.findById(usr.getIdU()).get();
				result.add(new UserAndName(usr,cl.getLibelle()));
			}
		} ;
		return result;
	}
	
	public User getUserById(long id) {
		return repository.findById(id).orElse(null);
	}
	
	public String getUsernameById(long id) {
		User user= repository.findById(id).orElse(null);
		if(user instanceof Client) {
			return ((Client)user).getLibelle();
		}
		else {
			return ((Personel)user).getFirstname()+" "+((Personel)user).getLastname();
		}
	}
	
    public Page<UserAndName> findPaginated(int pageNum, int pageSize,List<UserAndName> users){
    	Pageable pageable = PageRequest.of(pageNum-1, pageSize);
    	int start = (pageNum - 1) * pageSize;
        int end = Math.min((start + pageSize), users.size());

        List<UserAndName> sublist = users.subList(start, end);

    	
    	
    	return new PageImpl<UserAndName>(sublist, pageable, users.size());
    }
    
    public List<UserAndName> SearchPer(String search,List<UserAndName> users){
    	List<UserAndName> result = new ArrayList<UserAndName>();
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    	for (UserAndName user : users) {
    		if (user.getUser().getRole() != UserType.Client) {
    			Personel per=personelRepo.findByEmail(user.getUser().getEmail());
        		String fullname = per.getFirstname()+" "+per.getLastname();
    			if(user.getUser().getEmail().toUpperCase().contains(search.toUpperCase()) || user.getUser().getRole().toString().toUpperCase().contains(search.toUpperCase()) || 
    			fullname.toUpperCase().contains(search.toUpperCase()) || dateFormat.format(user.getUser().getDatecreation()).contains(search)){
    					result.add(new UserAndName(user.getUser(),per.getFirstname()+" "+per.getLastname()));
    				}
    		}
    		else {
    			Client cl=clientRepo.findById(user.getUser().getIdU()).get();
        		String fullname = cl.getLibelle();
    			if(user.getUser().getEmail().toUpperCase().contains(search.toUpperCase()) || user.getUser().getRole().toString().toUpperCase().contains(search.toUpperCase()) || 
    			fullname.toUpperCase().contains(search.toUpperCase()) || dateFormat.format(user.getUser().getDatecreation()).contains(search)){
    					result.add(new UserAndName(user.getUser(),cl.getLibelle()));
    				}
    		}
    		
		}
    	return result;
    }
    
    public boolean CheckEmail(String email) {
    	if(repository.findByEmail(email) != null)
    		return true;
    	else
    		return false;
    }
    
    public String uploadImage(MultipartFile uploadFile) {
    	
    	 FTPClient ftpClient = new FTPClient();
		  
		     try {
		         ftpClient.connect("192.168.1.18", 21);
		         ftpClient.login("ftp-user", "ftpuser");
		         ftpClient.enterLocalPassiveMode();
		         ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		         InputStream inputStream = uploadFile.getInputStream();
		         String extension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
		         System.out.println("Start uploading first file");		         
		         boolean done = ftpClient.storeFile(uploadFile.getOriginalFilename(), inputStream);
		         inputStream.close();
		         if (done) {
		             System.out.println("The first file is uploaded successfully.");
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
		     return uploadFile.getOriginalFilename();

		}
    
    
    public String downloadImage( String name) throws IOException {
		 FTPClient ftpClient = new FTPClient();
		 String encodidImage="";
		 byte[] data;
		  try {
     		
      	  ftpClient.connect("192.168.1.18", 21);
		  ftpClient.login("ftp-user", "ftpuser");
          ftpClient.enterLocalPassiveMode();
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
          String remoteFile2 = name;
          InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
          ByteArrayOutputStream buffer = new ByteArrayOutputStream();
          data = IOUtils.toByteArray(inputStream);
          boolean success = ftpClient.completePendingCommand();
          if (success) {
              System.out.println("File #2 has been downloaded successfully.");
          }
         // outputStream2.close();
          inputStream.close();
         
          
        
           encodidImage=Base64.getEncoder().encodeToString(data);
          encodidImage="data:image/png;base64,"+encodidImage;
      } catch (IOException ex) {
          System.out.println("Error: " + ex.getMessage());
          ex.printStackTrace();
         
      }
		  
		  finally {
          try {
              if (ftpClient.isConnected()) {
                  ftpClient.logout();
                  ftpClient.disconnect();
                  
              }
          } catch (IOException ex) {
              ex.printStackTrace();
          }
          
          return encodidImage; 
         
      }
	 }
    
    public String downloadImagebyid(long id) throws IOException {
    	User usr=repository.findById(id).get();
    	String name=usr.getImage();
		 FTPClient ftpClient = new FTPClient();
		 String encodidImage="";
		 byte[] data;
		  try {
     		
      	  ftpClient.connect("192.168.1.18", 21);
		  ftpClient.login("ftp-user", "ftpuser");
          ftpClient.enterLocalPassiveMode();
          ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
          String remoteFile2 = name;
          InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
          ByteArrayOutputStream buffer = new ByteArrayOutputStream();
          data = IOUtils.toByteArray(inputStream);
          boolean success = ftpClient.completePendingCommand();
          if (success) {
              System.out.println("File #2 has been downloaded successfully.");
          }
         // outputStream2.close();
          inputStream.close();
         
          
        
           encodidImage=Base64.getEncoder().encodeToString(data);
          encodidImage="data:image/png;base64,"+encodidImage;
      } catch (IOException ex) {
          System.out.println("Error: " + ex.getMessage());
          ex.printStackTrace();
         
      }
		  
		  finally {
          try {
              if (ftpClient.isConnected()) {
                  ftpClient.logout();
                  ftpClient.disconnect();
                  
              }
          } catch (IOException ex) {
              ex.printStackTrace();
          }
          
          return encodidImage; 
         
      }
	 }
    
    private String generateForgotPasswordToken(User user) {
    	UUID uuid =UUID.randomUUID();
    	LocalDateTime currentDateTime=LocalDateTime.now();
    	LocalDateTime expireDate=currentDateTime.plusMinutes(30);
    	PasswordResetToken resetToken=new PasswordResetToken();
    	resetToken.setUser(user);
    	resetToken.setExpiredate(expireDate);
    	resetToken.setToken(uuid.toString());
    	resetTokenRepository.save(resetToken);
    	return resetToken.getToken();
    }
    
    
    public boolean SendEmailResetPassword(String email) {
    	User user=repository.findByEmail(email);
    	if(user == null || !user.isActive() || user.getRole()==UserType.Client)
    		return false;
    	else {
    		
    		try {
    			Personel per=(Personel) user;
        		String token=generateForgotPasswordToken(user);
        		String userFullname = per.getFirstname()+" "+per.getLastname();
        		String emailContent = loadEmailTemplate("resetpassword.html", token,userFullname);
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setSubject("ERPPro Password Reset");
                helper.setFrom("naderbessioud98@gmail.com");
                helper.setTo(user.getEmail());
                helper.setText(emailContent, true);
                mailSender.send(mimeMessage);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
    		return true;
    	}
    	
    	
    	
    	
    	
    	
    

    }
    
    private boolean IsTokenExpired(PasswordResetToken token) {
    	LocalDateTime dateTime=LocalDateTime.now();
    	return token.getExpiredate().isAfter(dateTime);
    }
    
    public boolean IsTokenValid(String token) {
    	PasswordResetToken passwordResetToken = resetTokenRepository.findByToken(token);
    	if(passwordResetToken != null && IsTokenExpired(passwordResetToken))
    		return true;
    	else
    		return false;
    }
    
    public void UpdatePassword(UserUpdatePassword newuser) {
       
        User user=repository.findByEmail(newuser.getEmail());
        user.setPassword(passwordencode.passwordEncoder().encode(newuser.getPassword()));
        repository.save(user);
    }
    
    private String loadEmailTemplate(String templateName, String token, String name) throws IOException {
        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("name", name);
        context.setVariable("url", "aa");

        
        return templateEngine.process("email/" + templateName, context);
    }
    
    
    public User updateProfile(Personel u) {
    	Personel user = personelRepo.findById(u.getIdU()).get();
    	u.setPassword(user.getPassword());
    	u.setRole(user.getRole());
    	u.setActive(true);
    	return repository.save(u);
    }
    
    public User updateProfilecl(Client u) {
    	
    	Client user = clientRepo.findById(u.getIdU()).get();
    	u.setPassword(user.getPassword());
    	u.setRole(UserType.Client);
    	u.setActive(true);
    	return repository.save(u);
    }
    
    public  boolean updatePassword(String oldpass,User u) {
    	System.out.println(oldpass);
    	User user = repository.findById(u.getIdU()).get();
    
    	
    	if(passwordencode.passwordEncoder().matches(oldpass,user.getPassword())) {
    		System.out.println("------->>>>>>>>>>>>"+u.getPassword());
    		user.setPassword(passwordencode.passwordEncoder().encode(u.getPassword()));
    		repository.save(user);
    		return true;
    	}
    
    	return false;
    }
    
    public String getCurrentUserFirstName() {
        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal (which should be your custom UserDetails implementation)
            Object principal = authentication.getPrincipal();

            // Assuming your UserDetails implementation is named CustomUserDetails
            if (principal instanceof User) {
            	User userDetails = (User) principal;
            	String firstName ="";
                // Access the user's first name
            	if(userDetails.getRole()==UserType.Client) {
            		Client c=(Client) userDetails;
            		firstName=c.getLibelle();
            	}
            	else {
            		Personel per=(Personel) userDetails;
            		firstName=per.getFirstname();
            	}
               
                return "Current logged-in user's first name: " + firstName;
            }
        }

        return "User not authenticated";
    }

    public String getUserRoleById(long id) {
    	return repository.findById(id).get().getRole().toString();
    }
  
    }
    

   
	
	


