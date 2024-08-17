package tn.famytech.esprit.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tn.famytech.esprit.utils.FileUtils;

@Configuration
public class ThymeleafConfig {
	
	  @Bean
	    public FileUtils fileUtils() {
	        return new FileUtils();
	    }

}
