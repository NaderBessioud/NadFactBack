package tn.famytech.esprit.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.AvoirStatus;
import tn.famytech.esprit.Entites.Facture;


@NoArgsConstructor
@Getter
@Setter
public class FactureDateAndNumber {
	private long number;

	private Date date;
	
	public FactureDateAndNumber(long number, Date date) {
        this.number = number;
        this.date = date;
    }

}
