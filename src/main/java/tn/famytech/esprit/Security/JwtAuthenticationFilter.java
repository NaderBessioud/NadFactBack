package tn.famytech.esprit.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tn.famytech.esprit.Services.JWTService;
import tn.famytech.esprit.Services.UserService;
import org.apache.commons.lang3.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private final JWTService jwtService;
	
	@Autowired
	private final UserService userService;
	
	private RequestAttributeSecurityContextRepository repository = new RequestAttributeSecurityContextRepository();
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
				Cookie[] cookies = request.getCookies();
        String jwtToken = null;
		if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }
		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;
	
		if((StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ") )&& StringUtils.isEmpty(jwtToken)) {
			

			filterChain.doFilter(request, response);
			return;
		}
		if(authHeader != null) {
			jwt = authHeader.substring(7);
		}
		else {
			jwt=jwtToken;
		}
		
		userEmail = jwtService.extractUsername(jwt);
		
		
		if(StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null) {
		
			UserDetails userDetails = userService.loadUserByUsername(userEmail);
			
			if(jwtService.TokenIsValid(jwt, userDetails)) {
				SecurityContext contextHolder =SecurityContextHolder.createEmptyContext();
				
				UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(jwtService.extractUsername(jwt),null,userDetails.getAuthorities());
				
				UsernamePasswordAuthenticationToken token =new UsernamePasswordAuthenticationToken(userDetails, contextHolder);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				contextHolder.setAuthentication(authentication);
				SecurityContextHolder.setContext(contextHolder);
				repository.saveContext(contextHolder, request, response);


				
				
			}
			
		}
		
		filterChain.doFilter(request, response);
	}


}
