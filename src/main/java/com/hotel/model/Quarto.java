package com.hotel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Classe que representa um quarto do hotel
public class Quarto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numero;
    private String tipo;
    private double preco;
    private boolean disponivel;
    private List<String> amenities;
    private String description;

    // Enum that defines the available room types
    public enum TipoQuarto {
        STANDARD("Standard"),
        DELUXE("Deluxe"),
        SUITE("Suite");

        private final String descricao;

        TipoQuarto(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Otniel: Constructor for creating a room with all information
    public Quarto(String numero, String tipo, double preco, boolean disponivel, List<String> amenities, String description) {
        this.numero = numero;
        this.tipo = tipo;
        this.preco = preco;
        this.disponivel = disponivel;
        this.amenities = amenities;
        this.description = description;
    }

    // Otniel: Simplified constructor for creating a basic room
    public Quarto(String numero, String tipo, double preco, boolean disponivel) {
        this(numero, tipo, preco, disponivel, new ArrayList<>(), "");
    }

    // Getters e setters
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPreco() {
        return preco;
    }

    // Atualiza o preço do quarto, garantindo que não seja negativo
    public void setPreco(double preco) {
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        this.preco = preco;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quarto)) return false;
        Quarto quarto = (Quarto) o;
        return Objects.equals(numero, quarto.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return String.format("Quarto %s - %s (R$ %.2f/diária) - %s\nComodidades: %s\nDescrição: %s",
                numero, tipo, preco, disponivel ? "Disponível" : "Ocupado", amenities, description);
    }
}