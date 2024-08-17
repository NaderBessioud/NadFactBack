package tn.famytech.esprit.DTO;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import tn.famytech.esprit.Entites.Client;
import tn.famytech.esprit.Entites.FacturePayementStatus;
import tn.famytech.esprit.Entites.FactureStatus;
import tn.famytech.esprit.Entites.Lignecommande;
import tn.famytech.esprit.Entites.Reglement;
import tn.famytech.esprit.Entites.TypeFacture;
import tn.famytech.esprit.Entites.User;

@Getter
@Setter
public class TotalAndTva {
private double total;
private double tva;
}
