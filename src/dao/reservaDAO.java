package dao;

import model.reserva;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class reservaDAO implements dao<reserva, String> {
    private List<reserva> reservas = new ArrayList<>();
    private final String FILE_NAME = "reservas.dat";

    public reservaDAO() {
        try {
            loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            reservas = new ArrayList<>();
        }
    }

    @Override
    public void save(reserva r) throws IOException {
        if (r == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        
        synchronized (reservas) {
            reservas.removeIf(existing -> existing.getId().equals(r.getId()));
            reservas.add(r);
            persist();
        }
    }

    @Override
    public reserva findByID(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        synchronized (reservas) {
            return reservas.stream()
                    .filter(r -> id.equals(r.getId()))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public List<reserva> findAll() {
        synchronized (reservas) {
            return new ArrayList<>(reservas);
        }
    }

    @Override
    public void update(reserva obj) throws IOException {
        save(obj);
    }

    @Override
    public void deleteById(String id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        synchronized (reservas) {
            reservas.removeIf(r -> id.equals(r.getId()));
            persist();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            reservas = (List<reserva>) in.readObject();
        }
    }

    private void persist() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(reservas);
        }
    }
}