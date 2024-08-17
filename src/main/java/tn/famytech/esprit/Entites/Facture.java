package tn.famytech.esprit.Entites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
public class Facture implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private long idF;
	private long number;
	@Temporal(TemporalType.DATE)
	private Date dateemission;
	@Temporal(TemporalType.DATE)
	 @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date datecv;
	private double retenue;
	private long ref;
	private double tva;
	private double cours;
	private double totalht;
	private double totalttc;
	private double ctvht;
	private double ctvttc;
	private double ctvtva;
	private double ctvtimbre;
	private String montantlettres;
	private TypeFacture type;
	private boolean signed;
	private boolean archived;
	private FactureStatus status;
	private String pdfname;
	private String pdfpath;
	private FacturePayementStatus payementstatus;
	private double totalrestant;
	private boolean retenueaff;
	private String retenuepath;
	private boolean traited;
	@ManyToOne
	private Client client;
	
	@JsonIgnore
	@ManyToOne
	private Personel user;
	
	@ManyToMany
	private List<Lignecommande> commandes;
	
	
	
	@JsonIgnore
	@ManyToMany(mappedBy = "regFactures")
	private List<Reglement> reglementsFact;
	
	@OneToMany(mappedBy = "fact")
	private List<Avoir> avoirs;
	
	public Facture() {
		this.commandes=new ArrayList<Lignecommande>();
	}

}
