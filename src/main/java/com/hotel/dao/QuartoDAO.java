package com.hotel.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.hotel.model.Quarto;

public class QuartoDAO implements DAO<Quarto> {
    private final List<Quarto> quartos = new ArrayList<>();
    private final String FILE_NAME = "quartos.dat";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public QuartoDAO() {
        try {
            loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            quartos.clear();
        }
    }

    @Override
    public void save(Quarto entity) throws IOException {
        if (entity == null) {
            throw new IllegalArgumentException("Quarto não pode ser nulo");
        }

        lock.writeLock().lock();
        try {
            quartos.removeIf(existing -> existing.getNumero().equals(entity.getNumero()));
            quartos.add(entity);
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update(Quarto entity) throws IOException {
        save(entity);
    }

    @Override
    public void deleteById(String id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("Número do quarto não pode ser nulo");
        }

        lock.writeLock().lock();
        try {
            quartos.removeIf(q -> q.getNumero().equals(id));
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Quarto> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Número do quarto não pode ser nulo");
        }

        lock.readLock().lock();
        try {
            return quartos.stream()
                    .filter(q -> q.getNumero().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Quarto> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(quartos);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Quarto> findAvailableRooms() {
        lock.readLock().lock();
        try {
            return quartos.stream()
                    .filter(Quarto::isDisponivel)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateRoomStatus(String roomNumber, boolean disponivel) throws IOException {
        if (roomNumber == null) {
            throw new IllegalArgumentException("Número do quarto não pode ser nulo");
        }

        lock.writeLock().lock();
        try {
            Optional<Quarto> quarto = findById(roomNumber);
            if (quarto.isPresent()) {
                quarto.get().setDisponivel(disponivel);
                persist();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void deleteAll() throws IOException {
        lock.writeLock().lock();
        try {
            quartos.clear();
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Quarto> loadedQuartos = (List<Quarto>) in.readObject();
            quartos.clear();
            quartos.addAll(loadedQuartos);
        }
    }

    private void persist() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(quartos);
        }
    }
}