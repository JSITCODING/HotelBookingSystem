package main;

import view.menu;

public class Main {
    public static void main(String[] args) {
        try {
            menu menuPrincipal = new menu();
            menuPrincipal.start();
        } catch (Exception e) {
            System.err.println("Erro fatal na aplicação: " + e.getMessage());
            e.printStackTrace();
        } finally {

            System.out.println("Programa finalizado.");
        }
    }
}