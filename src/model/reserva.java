package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class reserva implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String clientId;
    private String numQuarto;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public reserva(String id,
                       String clientId,
                       String roomNumber,
                       LocalDate checkIn,
                       LocalDate checkOut)
    {
        this.id = id;
        this.clientId = clientId;
        this.numQuarto = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }


    public String getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getNumQuarto() {
        return numQuarto;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    @Override
    public String toString() {
        return "Reservation{id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", numQuarto='" + numQuarto + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof reserva)) return false;
        reserva that = (reserva) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
