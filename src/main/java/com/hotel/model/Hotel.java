package com.hotel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String endereco;
    private List<Quarto> quartos;
    
    public Hotel(String nome, String endereco) {
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
    
    public List<Quarto> getQuartos() {
        return new ArrayList<>(quartos);
    }
    
    public void addQuarto(Quarto q) {
        if (q != null) {
            quartos.add(q);
        }
    }
    
    public void removeQuarto(Quarto q) {
        quartos.remove(q);
    }
}