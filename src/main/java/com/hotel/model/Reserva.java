package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

// Classe que representa uma reserva de quarto no hotel
public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Cliente cliente;
    private Quarto quarto;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private ReservaStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Otniel: Enum that defines the possible states of a reservation
    public enum ReservaStatus {
        PENDING("Pendente"),
        CONFIRMED("Confirmada"),
        CHECKED_IN("Check-in Realizado"),
        CHECKED_OUT("Check-out Realizado"),
        CANCELLED("Cancelada");

        private final String descricao;

        ReservaStatus(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    // Otniel: Constructor for creating a new reservation
    public Reserva(String id, Cliente cliente, Quarto quarto, LocalDate checkIn, LocalDate checkOut, ReservaStatus status) {
        this.id = id;
        this.cliente = cliente;
        this.quarto = quarto;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters e setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public ReservaStatus getStatus() {
        return status;
    }

    // Otniel: Updates the reservation status and records the update date
    public void setStatus(ReservaStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Otniel: Checks if the reservation is active (not cancelled and not finished)
    public boolean isActive() {
        return status != ReservaStatus.CANCELLED && 
               status != ReservaStatus.CHECKED_OUT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reserva)) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Reserva %s - Cliente: %s - Quarto: %s - %s a %s - Status: %s",
                id, cliente.getNome(), quarto.getNumero(), checkIn, checkOut, status.getDescricao());
    }
} 