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

import com.hotel.model.Payment;

public class PaymentDAO {
    private final List<Payment> payments = new ArrayList<>();
    private final String FILE_NAME = "data/payments.dat";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public PaymentDAO() {
        try {
            loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            payments.clear();
        }
    }

    public void save(Payment payment) throws IOException {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }
        lock.writeLock().lock();
        try {
            payments.add(payment);
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Payment> findByReservationId(String reservationId) {
        lock.readLock().lock();
        try {
            return payments.stream()
                    .filter(p -> p.getReservationId().equals(reservationId))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Payment> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(payments);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deleteAll() throws IOException {
        lock.writeLock().lock();
        try {
            payments.clear();
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
            List<Payment> loadedPayments = (List<Payment>) in.readObject();
            payments.clear();
            payments.addAll(loadedPayments);
        }
    }

    private void persist() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(payments);
        }
    }
} 