const container = document.querySelector(".container"),
	pwShowHider = document.querySelectorAll(".showHidePW"),
	pwFields = document.querySelectorAll(".password"),
	signUp = document.querySelector(".signup-link"),
	loginv = document.querySelector(".login-link");
	
	pwShowHider.forEach(eyeIcon =>{
		eyeIcon.addEventListener("click",()=>{
			pwFields.forEach(pwField =>{
				if(pwField.type == "password"){
					pwField.type = "text";
					
					pwShowHider.forEach(icon =>{
						icon.classList.replace("uil-eye-slash", "uil-eye");
					})
				}
				else{
					pwField.type = "password";
					
					pwShowHider.forEach(icon =>{
						icon.classList.replace("uil-eye", "uil-eye-slash");
					})
				}
			})
		})
	})
	
	
	signUp.addEventListener("click",()=>{
		container.classList.add("active");
	})
	
	loginv.addEventListener("click",()=>{
		container.classList.remove("active");
	})
	
	
