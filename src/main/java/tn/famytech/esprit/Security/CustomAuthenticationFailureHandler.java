package tn.famytech.esprit.Security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.sun.jdi.InternalException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler { 

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (exception instanceof InternalAuthenticationServiceException) {
			 Throwable internalException = exception.getCause();

	        
	        if (internalException instanceof LockedException) {
	            response.sendRedirect("/ERPPro/login?error=account_locked");
	        } else {
	            // Handle other internal exceptions
	            response.sendRedirect("/ERPPro/login?error=internal_error");
	        }
	    } else if (exception instanceof UsernameNotFoundException) {
	        response.sendRedirect("/ERPPro/login?error=user_not_found");
	    } else if (exception instanceof BadCredentialsException) {
	        response.sendRedirect("/ERPPro/login?error=bad_credentials");
	    } else {
	    	System.out.println("----->"+InternalException.class);
	    	System.out.println("----->>"+exception.getCause());
	        // Default behavior for other exceptions
	        response.sendRedirect("/ERPPro/login?error=generic_error");
	    }
	}

}
