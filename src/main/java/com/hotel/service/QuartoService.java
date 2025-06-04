package com.hotel.service;

import com.hotel.dao.QuartoDAO;
import com.hotel.model.Quarto;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class QuartoService {
    private final QuartoDAO quartoDAO;

    public QuartoService(QuartoDAO quartoDAO) {
        this.quartoDAO = quartoDAO;
    }

    public void addRoom(Quarto quarto) throws IOException {
        if (quarto == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        quartoDAO.save(quarto);
    }

    public void updateRoom(Quarto quarto) throws IOException {
        if (quarto == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        quartoDAO.update(quarto);
    }

    public void deleteRoom(String roomNumber) throws IOException {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        quartoDAO.deleteById(roomNumber);
    }

    public Optional<Quarto> getRoom(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        return quartoDAO.findById(roomNumber);
    }

    public List<Quarto> getAllRooms() {
        return quartoDAO.findAll();
    }

    public List<Quarto> getAvailableRooms() {
        return quartoDAO.findAvailableRooms();
    }

    public void updateRoomStatus(String roomNumber, boolean disponivel) throws IOException {
        Optional<Quarto> quartoOpt = quartoDAO.findById(roomNumber);
        if (quartoOpt.isPresent()) {
            Quarto quarto = quartoOpt.get();
            quarto.setDisponivel(disponivel);
            quartoDAO.update(quarto);
        }
    }
} 