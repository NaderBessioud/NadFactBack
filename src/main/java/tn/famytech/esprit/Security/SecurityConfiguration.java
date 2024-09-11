package tn.famytech.esprit.Security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;


import tn.famytech.esprit.Entites.UserType;
import tn.famytech.esprit.Services.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	
	private final JwtAuthenticationFilter authenticationFilter;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoderBean passwordencode;
	
	@Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	

	

	

	
	public Customizer<CorsConfigurer<HttpSecurity>> getcorss(){
		return new Customizer<CorsConfigurer<HttpSecurity>>() {
			@Override
			public void customize(CorsConfigurer<HttpSecurity> t) {
				t.configurationSource(getCorsConfiguration());
			}
		};
	}
	
	
	public CorsConfigurationSource getCorsConfiguration() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedOriginPattern("*");
		
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return source;
	}
	
	
	@Bean
	public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception{

		
		http.csrf().disable().cors(getcorss()).authorizeHttpRequests(request -> request.requestMatchers("/css/**").permitAll()
	            .requestMatchers("/js/**").permitAll()
		    .requestMatchers("/vendors/**").permitAll()
		    .requestMatchers("/assets/**").permitAll()
		    .requestMatchers("/images/**").permitAll()
	            .requestMatchers("/home/**").permitAll()
		    .requestMatchers("/nadfactmobile/**").permitAll()
		    .requestMatchers("/admin/**").hasAnyAuthority(UserType.Admin.name())
		    .requestMatchers("/user/**").hasAnyAuthority(UserType.Employee.name(),UserType.Manager.name(),UserType.Admin.name())
		    .requestMatchers("/client/**").hasAnyAuthority(UserType.Client.name())
		    .requestMatchers("/chat/**").hasAnyAuthority(UserType.Employee.name(),UserType.Manager.name(),UserType.Admin.name(),UserType.Client.name())

				
				
				
				
				.anyRequest().authenticated())
				
				.sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint());
			return http.build();
			}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService);
		authenticationProvider.setPasswordEncoder(passwordencode.passwordEncoder());
		return authenticationProvider;
	}
	
	 @Bean
	    public AuthenticationEntryPoint authenticationEntryPoint() {
	        return new CustomAuthenticationEntryPoint();
	    }
	 
	 @Bean
		public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
			return config.getAuthenticationManager();
		}
}
