package tn.famytech.esprit.DTO;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.famytech.esprit.Entites.Facture;

@NoArgsConstructor
@Getter
@Setter
public class FactureListAndPDF {
private byte[] output;
private List<Facture> factures;
}
