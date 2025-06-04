package com.hotel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Classe que representa o hotel e suas informações básicas
public class Hotel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nome;
    private String endereco;
    private List<Quarto> quartos;
    
    // Construtor para criar um novo hotel
    public Hotel(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
        this.quartos = new ArrayList<>();
    }
    
    // Getters para acessar os dados do hotel
    public String getNome() {
        return nome;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    // Retorna uma cópia da lista de quartos para evitar modificações externas
    public List<Quarto> getQuartos() {
        return new ArrayList<>(quartos);
    }
    
    // Adiciona um novo quarto ao hotel
    public void addQuarto(Quarto q) {
        if (q != null) {
            quartos.add(q);
        }
    }
    
    // Remove um quarto do hotel
    public void removeQuarto(Quarto q) {
        quartos.remove(q);
    }
}