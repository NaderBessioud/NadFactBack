package tn.famytech.esprit.Services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;


public class GoogleTokenVerifier {
	private static final String CLIENT_ID = "771121226622-hdjoefmfpbm1qhsjc52lqpk616qssdq5.apps.googleusercontent.com";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
    private  GoogleIdTokenVerifier verifier;
    
    public GoogleTokenVerifier() {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),JSON_FACTORY)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }
    
    public String verifyToken(String idTokenString) throws GeneralSecurityException, IOException {
    	  if (idTokenString == null || idTokenString.isEmpty()) {
              System.out.println("ID token string is null or empty");
              return null;
          }

          System.out.println("Received ID token string: " + idTokenString);
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            return email;  // or any other user info you want to extract
        } else {
            return null;  // or handle the invalid token case
        }
    }

}
