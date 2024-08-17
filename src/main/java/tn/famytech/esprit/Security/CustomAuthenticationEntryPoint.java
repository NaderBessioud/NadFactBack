package tn.famytech.esprit.Security;

import java.io.IOException;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		  if (authException.getCause() instanceof ExpiredJwtException) {
	            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token expired");
	        } else  if (authException.getCause() instanceof Unauthorized) {
	            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	        }
	        else   {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error");
	        }
		  
		
	}

}
