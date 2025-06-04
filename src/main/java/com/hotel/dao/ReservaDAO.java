package com.hotel.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.hotel.model.Reserva;

public class ReservaDAO implements DAO<Reserva> {
    private final List<Reserva> reservas = new ArrayList<>();
    private final String FILE_NAME = "reservas.dat";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ReservaDAO() {
        try {
            loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            reservas.clear();
        }
    }

    @Override
    public void save(Reserva reserva) throws IOException {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva não pode ser nula");
        }

        lock.writeLock().lock();
        try {
            reservas.removeIf(existing -> existing.getId().equals(reserva.getId()));
            reservas.add(reserva);
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Reserva> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID da reserva não pode ser nulo");
        }

        lock.readLock().lock();
        try {
            return reservas.stream()
                    .filter(r -> id.equals(r.getId()))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Reserva> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(reservas);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void update(Reserva reserva) throws IOException {
        save(reserva);
    }

    @Override
    public void deleteById(String id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID da reserva não pode ser nulo");
        }

        lock.writeLock().lock();
        try {
            reservas.removeIf(r -> id.equals(r.getId()));
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteAll() throws IOException {
        lock.writeLock().lock();
        try {
            reservas.clear();
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Reserva> findActiveReservations() {
        lock.readLock().lock();
        try {
            return reservas.stream()
                    .filter(Reserva::isActive)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Reserva> findReservationsByDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Data inicial deve ser anterior à data final");
        }

        lock.readLock().lock();
        try {
            return reservas.stream()
                    .filter(r -> !r.getCheckIn().isAfter(end) && !r.getCheckOut().isBefore(start))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Reserva> loadedReservas = (List<Reserva>) in.readObject();
            reservas.clear();
            reservas.addAll(loadedReservas);
        }
    }

    private void persist() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(reservas);
        }
    }

    private void saveAll(List<Reserva> reservas) throws IOException {
        lock.writeLock().lock();
        try {
            this.reservas.clear();
            this.reservas.addAll(reservas);
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }
} 