package tn.famytech.esprit.Controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import tn.famytech.esprit.DTO.JwtAuthenticationResponse;
import tn.famytech.esprit.DTO.UserUpdatePassword;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Entites.UserType;
import tn.famytech.esprit.Services.AuthenticationService;
import tn.famytech.esprit.Services.FactureService;
import tn.famytech.esprit.Services.FileFTPService;
import tn.famytech.esprit.Services.GoogleTokenVerifier;
import tn.famytech.esprit.Services.LoggedInUserService;
import tn.famytech.esprit.Services.PersonelService;
import tn.famytech.esprit.Services.UserService;

@Controller
@RequestMapping("/home")
public class HomeController {
	private final GoogleTokenVerifier googleTokenVerifier = new GoogleTokenVerifier();
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileFTPService fileFTPService;
	
	@Autowired
	private PersonelService personelService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private LoggedInUserService inUserService;
	
    @GetMapping("home/{activelink}")
    public String home(@PathVariable("activelink") String activelink,Model model) {
   	model.addAttribute("activelink", activelink);
   	Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
    String email = loggedInUser.getName(); 
    User user = userService.getUserByEmail(email);
    if(user.getRole()==UserType.Client) {
    	Client c=(Client)user;
    	model.addAttribute("name",c.getLibelle());
        model.addAttribute("idu",c.getIdU());
    }
    else {
    	Personel p = (Personel)user;
    	 model.addAttribute("name",p.getFirstname());
    	    model.addAttribute("idu",p.getIdU());
    }
   
    
   	 
   	 return "home";
    }
    
    @PostMapping("/login")
    @ResponseBody
	public JwtAuthenticationResponse signin(@RequestParam("email") String email, @RequestParam("password") String password) {
		return authenticationService.signin(email, password);
	}
    
    @PostMapping("/loginWithGoogle")
    @ResponseBody
	public JwtAuthenticationResponse signinWithGoogle(@RequestParam("email") String email) {
		return authenticationService.signinWithGoogle(email);
	}
    
    @PostMapping("/logout/{email}")
    @ResponseBody
	public void logout(@PathVariable("email") String email) {
		 authenticationService.logout(email);
	}
 
    @GetMapping("home")
    public String home(Model model) {
    	model.addAttribute("user", new User());
    	return "home";
    }
    
    @GetMapping("login")
    public String login(Model model) {
    	model.addAttribute("user", new Personel());
    	model.addAttribute("usr", new User());
    	
    	return "login";
    }
    
	@PostMapping("register")
	public String addUser(@ModelAttribute("user") Personel user) {
		
		userService.AddUser(user);
		return "redirect:/login?success";
	}
	
	
	
	 @PostMapping("/uploadImage")
	 @ResponseBody
	 public String uploadImage(@RequestParam(value = "imageFile", required = true) MultipartFile  uploadFile) {
		 return userService.uploadImage(uploadFile);
	 }
	 
	 @GetMapping("/downloadImage")
		@ResponseBody
		public String downloadImage(@RequestParam("name") String name) throws IOException  {
		 
			return userService.downloadImage(name);
		}
	 
	 @PostMapping("/downloadImageMobile")
		@ResponseBody
		public String downloadImageMobile(@RequestParam("name") String name) throws IOException  {
		 
			return userService.downloadImage(name);
		}
	 
	 
	 @GetMapping("/downloadImageByid/{id}")
		@ResponseBody
		public String downloadImage(@PathVariable("id") long id) throws IOException  {
		 
			return userService.downloadImagebyid(id);
		}
	 @PostMapping("/uploadFile")
	 @ResponseBody
	 public String uploadFile(@RequestParam(value = "imageFile", required = true) MultipartFile  uploadFile) {
		 return fileFTPService.uploadfile(uploadFile);
	 }
	 
	
	 
	 
	 @GetMapping("/EmailCheck")
	 @ResponseBody
	 public boolean CheckEmail(@RequestParam("email") String email) {
		 return userService.CheckEmail(email);
	 }
	 
	 @GetMapping("/ResetPassword")
	 @ResponseBody
	 public boolean SendEmailResetPassword(@RequestParam("email") String email) {
		 return userService.SendEmailResetPassword(email);
	 }
	 
	 @GetMapping("/IsTokenValid")
	 @ResponseBody
	 public boolean IsTokenValid(@RequestParam("token") String token) {
		 return userService.IsTokenValid(token);
	 }
	 
	 @PutMapping("/UpdatePass")
	 @ResponseBody
	 public void UpdatePassword(@ModelAttribute UserUpdatePassword newpass) {
		 
		 userService.UpdatePassword(newpass);
	 }
	 
	 
	 @GetMapping("/NadFact")
	 public String DisplayHomePage() {
		 return "homepage";
	 } @PostMapping("facialauthentication")
	 
	 private String facialAuth(@RequestParam("email") String email) {
		 return personelService.AuthenticateUser(email);
	 }
	
	 @PostMapping("/google")
	    public String googleAuth(@RequestBody TokenRequest tokenRequest) {
	        try {
	            String email = googleTokenVerifier.verifyToken(tokenRequest.getToken());
	            if (email != null) {
	                // Process the email and other information as needed
	                return "Successfully authenticated as " + email;
	            } else {
	                return "Invalid ID token";
	            }
	        } catch (GeneralSecurityException | IOException e) {
	            e.printStackTrace();
	            return "Error during token verification";
	        }
	    }
	 
	 @GetMapping("/getUserType")
	 @ResponseBody
	 public String getUserType(@RequestParam String email) {
	 	return authenticationService.getUserType(email);
	 }
	 
	 @GetMapping("/downloadImagemobil")
	 
	    public ResponseEntity<String> downloadImagemobile(@RequestParam String name) {
	        FTPClient ftpClient = new FTPClient();
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        String base64Image = "";

	        try {
	            // Connect and login to the FTP server
	            ftpClient.connect("192.168.1.31", 21);
	            ftpClient.login("ftp-user", "ftpuser");
	            ftpClient.enterLocalPassiveMode();
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

	            // Retrieve the image from the FTP server
	            InputStream inputStream = ftpClient.retrieveFileStream(name);
	            byte[] buffer = new byte[1024];
	            int bytesRead;

	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                byteArrayOutputStream.write(buffer, 0, bytesRead);
	            }

	            boolean success = ftpClient.completePendingCommand();
	            if (success) {
	                // Encode the image to Base64
	                byte[] imageBytes = byteArrayOutputStream.toByteArray();
	                base64Image = Base64.getEncoder().encodeToString(imageBytes);

	                // Append the MIME type to the Base64 string
	                String mimeType = "image/png"; // Adjust the MIME type as needed
	                base64Image = "data:" + mimeType + ";base64," + base64Image;
	            }

	            inputStream.close();
	        } catch (IOException ex) {
	            ex.printStackTrace();
	            return ResponseEntity.status(500).body("Error downloading image");
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

	        return ResponseEntity.ok(base64Image);
	    }
	 
	 @GetMapping("/404")
	 public String Display404Page() {
		 return "404.html";
	 }
	 
	}



class TokenRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


	
	 
	


