package tn.famytech.esprit.Controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import tn.famytech.esprit.DTO.UserAndName;
import tn.famytech.esprit.Entites.Facture;
import tn.famytech.esprit.Entites.Personel;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Services.PersonelService;
import tn.famytech.esprit.Services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private PersonelService personelService;
	
	@GetMapping("/Users")
	public String displayUser(Model model) throws IOException {
		List<UserAndName> listres=userService.DisplayUser();
		for (int i=0; i<listres.size(); i++) {
			listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
		}
		
		 int pageSize = 5;
    	 Page<UserAndName> page = userService.findPaginated(1, pageSize,listres);
		
    	 model.addAttribute("currentPage", 1);
    	 model.addAttribute("totalPages", page.getTotalPages());
    	 model.addAttribute("totalItems", page.getTotalElements());
    	 model.addAttribute("users", page.getContent());
    	 model.addAttribute("Allusers", listres);

		model.addAttribute("activelink", "users");
		model.addAttribute("userp",new Personel());
		return "UsersList";		
	}
	 private String downloadImage( String name) throws IOException  {
		 
			return userService.downloadImage(name);
		}
	
	@PutMapping("/activeUser/{id}")
	@ResponseBody
	public List<UserAndName> ActiveUser(@PathVariable("id") long id,@RequestBody List<UserAndName> users) throws IOException {
		List<UserAndName> listres=userService.ActiveUserPser(id, users);
		for (int i=0; i<listres.size(); i++) {
			if(!listres.get(i).getUser().getImage().startsWith("data:image/png;base64")) {
				listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
			}
			
		}
		return listres;
	}
	
	@PutMapping("/desactiveUser/{id}")
	@ResponseBody
	public List<UserAndName> DesactiveUser(@PathVariable("id") long id,@RequestBody List<UserAndName> users) throws IOException {
		List<UserAndName> listres=userService.DesactiveUser(id, users);
		for (int i=0; i<listres.size(); i++) {
			System.out.println("hay role=="+listres.get(i).getUser().getRole());
			System.out.println("hay image=="+listres.get(i).getUser().getImage());
			if(!listres.get(i).getUser().getImage().startsWith("data:image/png;base64")) {
				listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
			}
		}
		return listres;
		
	}
	
	@GetMapping("/ActiveUsers")
	@ResponseBody
	public List<UserAndName> getActiveUser() throws IOException{
		List<UserAndName> listres=userService.diplayActiveUsers();
		for (int i=0; i<listres.size(); i++) {
			
				listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
			
		}
		return listres;
	}
	
	@GetMapping("/DesactiveUsers")
	@ResponseBody
	public List<UserAndName> getDesactiveUser() throws IOException{
		List<UserAndName> listres=userService.diplayDesactiveUsers();
		for (int i=0; i<listres.size(); i++) {
			
				listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
			
		}
		return listres;
	
	}
	
	@GetMapping("/UserById/{id}")
	@ResponseBody
	public User getUserById(@PathVariable("id") long id) {
		return userService.getUserById(id);
	}
	
	 @PostMapping("/UserpageList/{pageNo}")
	 @ResponseBody
     public List<UserAndName> findPaginatedList(@PathVariable(value="pageNo") int pageNo,@RequestBody List<UserAndName> users) {
    	 int pageSize = 5;
    	 Page<UserAndName> page = userService.findPaginated(pageNo, pageSize,users);
    	 
    	 return page.getContent(); 
     }
	 
	 @PostMapping("/Userpagetotal/{pageNo}")
	 @ResponseBody
     public int findPaginatedTotal(@PathVariable(value="pageNo") int pageNo,@RequestBody List<UserAndName> users) {
		 int pageSize = 5;
    	 Page<UserAndName> page = userService.findPaginated(pageNo, pageSize,users);
    	 return page.getTotalPages(); 
     }
	 
	 @PostMapping("/searchUsers")
	 @ResponseBody
	 public List<UserAndName> SearchUsers(@RequestParam("search") String search,@RequestBody List<UserAndName> users){
		 return userService.SearchPer(search, users);
	 }
	 
	 @PostMapping("/addUser")
	 @ResponseBody
	 public List<UserAndName> addUser(@ModelAttribute Personel user) throws IOException {
		 List<UserAndName> listres=userService.AddUser(user);
			for (int i=0; i<listres.size(); i++) {
				
					listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
				
			}
			return listres;
		 
	 }
	 
	 @PutMapping("/updateUser")
	 @ResponseBody
	 public List<UserAndName> updateUser(@ModelAttribute Personel user) throws IOException {
		 List<UserAndName> listres=userService.updateUserPer(user);
			for (int i=0; i<listres.size(); i++) {
				
					listres.get(i).getUser().setImage(downloadImage(listres.get(i).getUser().getImage()));
				
			}
			return listres;
		 
	 }
	 
	 
	
}
