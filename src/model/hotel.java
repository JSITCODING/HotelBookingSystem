package model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class hotel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String endereco;
    private List<quarto> quartos;
    
    public hotel(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
        this.quartos = new ArrayList<>();
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public List<quarto> getQuartos() {
        return new ArrayList<>(quartos);
    }
    
    public void addQuarto(quarto q) {
        if (q != null) {
            quartos.add(q);
        }
    }
    
    public void removeQuarto(quarto q) {
        quartos.remove(q);
    }
}