package com.hotel.model;

import java.io.Serializable;

// Classe que representa um funcionário do hotel
public class Worker implements Serializable {
    private String username;
    private String password;
    private String name;

    // Construtor para criar um novo funcionário
    public Worker(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // Getters para acessar os dados do funcionário
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    // Verifica se a senha fornecida corresponde à senha do funcionário
    public boolean authenticate(String password) {
        return this.password.equals(password);
    }
} 