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

import com.hotel.model.Cliente;

public class ClienteDAO implements DAO<Cliente> {
    private final List<Cliente> clientes = new ArrayList<>();
    private final String FILE_NAME = "data/clientes.dat";
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ClienteDAO() {
        try {
            loadFromFile();
        } catch (IOException | ClassNotFoundException e) {
            clientes.clear();
        }
    }

    @Override
    public void save(Cliente cliente) throws IOException {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }

        lock.writeLock().lock();
        try {
            clientes.removeIf(existing -> existing.getId().equals(cliente.getId()));
            clientes.add(cliente);
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Cliente> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        lock.readLock().lock();
        try {
            return clientes.stream()
                    .filter(c -> id.equals(c.getId()))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Cliente> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(clientes);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void update(Cliente cliente) throws IOException {
        save(cliente);
    }

    @Override
    public void deleteById(String id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        lock.writeLock().lock();
        try {
            clientes.removeIf(c -> id.equals(c.getId()));
            persist();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Cliente> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }

        lock.readLock().lock();
        try {
            return clientes.stream()
                    .filter(c -> c.getNome().toLowerCase().contains(nome.toLowerCase()))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteAll() throws IOException {
        lock.writeLock().lock();
        try {
            clientes.clear();
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
            List<Cliente> loadedClientes = (List<Cliente>) in.readObject();
            clientes.clear();
            clientes.addAll(loadedClientes);
        }
    }

    private void persist() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(clientes);
        }
    }
}
