package com.hotel.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hotel.model.Worker;

public class WorkerDAO {
    private final List<Worker> workers = new ArrayList<>();

    public WorkerDAO() {
        
        workers.add(new Worker("Otniel", "15175", "Otniel"));
        workers.add(new Worker("Priscila", "15176", "Priscila"));
        workers.add(new Worker("Maria", "15177", "Maria"));
    }

    public Optional<Worker> authenticate(String username, String password) {
        return workers.stream()
                .filter(w -> w.getUsername().equals(username) && w.authenticate(password))
                .findFirst();
    }

    public void deleteAll() {
        workers.clear();
    }
} 
