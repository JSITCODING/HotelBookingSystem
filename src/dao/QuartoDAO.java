package dao;

import model.quarto;
import java.util.*;
import java.io.*;

public class QuartoDAO implements dao<quarto, String> {
    private List<quarto> quartos = new ArrayList<>();
    private final String FILE_NAME = "quartos.dat";

    public QuartoDAO() {
        try {
            loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            quartos = new ArrayList<>();
        }
    }

    @Override
    public void save(quarto q) throws Exception {
        if (q == null) {
            throw new IllegalArgumentException("Quarto cannot be null");
        }
        quartos.removeIf(existing -> existing.getNumero().equals(q.getNumero()));
        quartos.add(q);
        persist();
    }

    @Override
    public quarto findByID(String numero) {
        if (numero == null) {
            throw new IllegalArgumentException("Numero cannot be null");
        }
        return quartos.stream()
                .filter(q -> q.getNumero().equals(numero))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<quarto> findAll() {
        return new ArrayList<>(quartos);
    }

    @Override
    public void update(quarto obj) throws IOException {
        if (obj == null) {
            throw new IllegalArgumentException("Quarto cannot be null");
        }
        deleteById(obj.getNumero());
        quartos.add(obj);
        persist();
    }

    @Override
    public void deleteById(String numero) throws IOException {
        quartos.removeIf(q -> q.getNumero().equals(numero));
        persist();
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            quartos = (List<quarto>) in.readObject();
        }
    }

    private void persist() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(quartos);
        }
    }
}