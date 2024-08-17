package tn.famytech.esprit.Entites;



import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetToken {
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idprt;
	private String token;
	private LocalDateTime expiredate;
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private User user;

}
