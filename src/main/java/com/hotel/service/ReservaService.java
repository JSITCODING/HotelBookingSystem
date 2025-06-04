package com.hotel.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.hotel.dao.QuartoDAO;
import com.hotel.dao.ReservaDAO;
import com.hotel.model.Quarto;
import com.hotel.model.Reserva;
import com.hotel.model.Reserva.ReservaStatus;

public class ReservaService {
    private final ReservaDAO reservaDAO;
    private final QuartoDAO quartoDAO;

    public ReservaService(ReservaDAO reservaDAO, QuartoDAO quartoDAO) {
        this.reservaDAO = reservaDAO;
        this.quartoDAO = quartoDAO;
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return isRoomAvailable(roomNumber, checkIn, checkOut, null);
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut, String excludeReservationId) {
        List<Reserva> existingReservations = reservaDAO.findReservationsByDateRange(checkIn, checkOut);
        
        return existingReservations.stream()
            .filter(r -> r.getQuarto().getNumero().equals(roomNumber))
            .filter(r -> excludeReservationId == null || !r.getId().equals(excludeReservationId))
            .noneMatch(r -> datesOverlap(r.getCheckIn(), r.getCheckOut(), checkIn, checkOut));
    }

    public List<Quarto> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Intervalo de datas inválido");
        }

        List<Quarto> allRooms = quartoDAO.findAll();
        List<Reserva> existingReservations = reservaDAO.findReservationsByDateRange(checkIn, checkOut);

        return allRooms.stream()
                .filter(quarto -> quarto.isDisponivel() &&
                        existingReservations.stream()
                                .noneMatch(r -> r.getQuarto().getNumero().equals(quarto.getNumero())))
                .toList();
    }

    public void makeReservation(Reserva reserva) throws IOException {
        if (!isRoomAvailable(reserva.getQuarto().getNumero(), reserva.getCheckIn(), reserva.getCheckOut())) {
            throw new IllegalStateException("Quarto não está disponível para as datas selecionadas");
        }

        quartoDAO.updateRoomStatus(reserva.getQuarto().getNumero(), false);
        reservaDAO.save(reserva);
    }

    public void cancelReservation(String reservationId) throws IOException {
        Optional<Reserva> reserva = reservaDAO.findById(reservationId);
        if (reserva.isPresent()) {
            quartoDAO.updateRoomStatus(reserva.get().getQuarto().getNumero(), true);
            reservaDAO.deleteById(reservationId);
        }
    }

    public void checkInReservation(Reserva reserva) throws IOException {
        reserva.setStatus(ReservaStatus.CHECKED_IN);
        reservaDAO.save(reserva);
        quartoDAO.updateRoomStatus(reserva.getQuarto().getNumero(), false);
    }

    public void checkOutReservation(Reserva reserva) throws IOException {
        reserva.setStatus(ReservaStatus.CHECKED_OUT);
        reservaDAO.save(reserva);
        quartoDAO.updateRoomStatus(reserva.getQuarto().getNumero(), true);
    }

    public List<Reserva> getActiveReservations() {
        return reservaDAO.findActiveReservations();
    }

    public List<Reserva> getReservationsByDateRange(LocalDate start, LocalDate end) {
        return reservaDAO.findReservationsByDateRange(start, end);
    }

    public void updateReservation(Reserva updatedReserva) throws IOException {
        if (!isRoomAvailable(updatedReserva.getQuarto().getNumero(), 
            updatedReserva.getCheckIn(), updatedReserva.getCheckOut(), 
            updatedReserva.getId())) {
            throw new IllegalStateException("Quarto não está disponível para as datas selecionadas");
        }
        reservaDAO.update(updatedReserva);
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
} 