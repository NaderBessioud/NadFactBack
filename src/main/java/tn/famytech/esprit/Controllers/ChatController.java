package tn.famytech.esprit.Controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.mail.internet.ParseException;
import tn.famytech.esprit.DTO.UserOnLineWhitMSG;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.Message;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Services.AuthenticationService;
import tn.famytech.esprit.Services.ClientService;
import tn.famytech.esprit.Services.MessageService;
import tn.famytech.esprit.Services.PersonelService;
import tn.famytech.esprit.Services.UserService;

@Controller

@RequestMapping("/chat")
public class ChatController {
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PersonelService personelService;
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	 private String downloadImage( String name) throws IOException  {
		 
			return userService.downloadImage(name);
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
             			if(msg != null) {
             				if(!msg.isSeen() && msg.getSender().getIdU() != userDetails.getIdU()) {
                 				seen=false;
                 			}
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
	
	 @GetMapping("/chat")
	 public String renderChat(Model model,@RequestParam("email") String email) throws IOException {
		
		 String img="";
		 

			  

                // Assuming your UserDetails implementation is named CustomUserDetails
                
               	 User userDetails = userService.getUserByEmail(email);
               	 img=downloadImage(userDetails.getImage());
                 	long ids=userDetails.getIdU();
                
		    	
			 
		 
		 
		 List<UserOnLineWhitMSG> users=getLoggedInUser(email);
		
		 model.addAttribute("userOnLine",users);
		
		 model.addAttribute("ids",ids);
		 
		 model.addAttribute("senderimage",img);
		 
		 if(users.size() != 0) {
			 model.addAttribute("receiverimage",users.get(0).getImage());
			 
			 model.addAttribute("online",users.get(0).isOnline());
			 model.addAttribute("username",users.get(0).getFullname());
			 model.addAttribute("nb",getMessages(users.get(0).getIdU(),ids).size());
			
		 }
		 else {
			 model.addAttribute("receiverimage","");
			 
			 
			 model.addAttribute("username","");
			 model.addAttribute("nb",1);
		 }
			
		
		 return "client/chat"; 
	 }

	 @PostMapping("/serachchat")
	 @ResponseBody
	 public List<UserOnLineWhitMSG> searchchat(@RequestParam("search") String search,@RequestParam("email") String email) throws IOException{
		 List<UserOnLineWhitMSG> result = new ArrayList<UserOnLineWhitMSG>();
		 for (UserOnLineWhitMSG user :  getLoggedInUser(email)) {
			if(user.getFullname().toUpperCase().contains(search.toUpperCase())) {
				
				result.add(user);
			}
		}
		 return result;
	 }
	 
	 @PostMapping("/Messages")
	 
		@ResponseBody
		public List<Message> getMessages(@RequestParam("idr") long idr,@RequestParam("ids") long ids){
		     	
			return messageService.getMessages(ids, idr);
		}
		
		@PostMapping("/addMessage")
		  @SendTo("/topic/messages")
		@ResponseBody
		public Message addMessage(@RequestParam("content") String message,@RequestParam("idr") long idr,@RequestParam("ids") long ids) throws ParseException {
			 
			
			return messageService.addMessage(message, ids, idr);
		}
		
		 @GetMapping("IsUserOnLine/{id}")
		 @ResponseBody
		 public boolean IsUserOnline(@PathVariable("id") long id) {
			 for (User usr : authenticationService.getLoggedIndUser()) {
				if(usr.getIdU()==id) {
					return true;
				}
			}
			 return false;
		 }
		 
		 @PostMapping("readmsgsbyid")
		 @ResponseBody
		 public boolean readMsg(@RequestBody List<Long> ids) throws IOException {
			return messageService.ReadSingleMSG(ids);
			 
		 }
		 
		 
		 @GetMapping("/usernameById/{id}")
		 @ResponseBody
		 public String getUsernameById(@PathVariable("id") long id) {
			 return userService.getUsernameById(id);
		 }
	
	

}
