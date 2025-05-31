package view;

import dao.ClienteDAO;
import dao.QuartoDAO;
import dao.reservaDAO;
import model.Cliente;
import model.quarto;
import model.reserva;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class menu {
    private ClienteDAO clienteDAO = new ClienteDAO();
    private QuartoDAO quartoDAO = new QuartoDAO();
    private reservaDAO reservaDAO = new reservaDAO();
    private Scanner scanner = new Scanner(System.in);
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void start() {
        int option;
        do {
            System.out.println("\n==== Hotel Booking Menu ====");
            System.out.println("1. Gestão de Clientes");
            System.out.println("2. Gestão de Quartos");
            System.out.println("3. Gestão de Reservas");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> menuClientes();
                case 2 -> menuQuartos();
                case 3 -> menuReservas();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (option != 0);
    }

    private void menuClientes() {
        int option;
        do {
            System.out.println("\n==== Gestão de Clientes ====");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Listar Clientes");
            System.out.println("3. Buscar Cliente por ID");
            System.out.println("4. Atualizar Cliente");
            System.out.println("5. Remover Cliente");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> cadastrarCliente();
                case 2 -> listarClientes();
                case 3 -> buscarCliente();
                case 4 -> atualizarCliente();
                case 5 -> removerCliente();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida!");
            }
        } while (option != 0);
    }

    private void menuQuartos() {
        int option;
        do {
            System.out.println("\n==== Gestão de Quartos ====");
            System.out.println("1. Cadastrar Quarto");
            System.out.println("2. Listar Quartos");
            System.out.println("3. Buscar Quarto por Número");
            System.out.println("4. Atualizar Quarto");
            System.out.println("5. Remover Quarto");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> cadastrarQuarto();
                case 2 -> listarQuartos();
                case 3 -> buscarQuarto();
                case 4 -> atualizarQuarto();
                case 5 -> removerQuarto();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida!");
            }
        } while (option != 0);
    }

    private void menuReservas() {
        int option;
        do {
            System.out.println("\n==== Gestão de Reservas ====");
            System.out.println("1. Fazer Reserva");
            System.out.println("2. Listar Todas Reservas");
            System.out.println("3. Buscar Reserva por ID");
            System.out.println("4. Cancelar Reserva");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> fazerReserva();
                case 2 -> listarReservas();
                case 3 -> buscarReserva();
                case 4 -> cancelarReserva();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opção inválida!");
            }
        } while (option != 0);
    }


    private void cadastrarCliente() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        
        String id = String.valueOf(System.currentTimeMillis());
        Cliente cliente = new Cliente(nome, email, telefone, id);
        try {
            clienteDAO.save(cliente);
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private void listarClientes() {
        try {
            if (clienteDAO == null) {
                throw new IllegalStateException("ClienteDAO não inicializado");
            }
        
            List<Cliente> clientes = clienteDAO.findAll();
            if (clientes.isEmpty()) {
                System.out.println("Nenhum cliente cadastrado.");
                return;
            }
        
            System.out.println("\nLista de Clientes:");
            clientes.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }
    }


    private void buscarCliente() {
        System.out.print("ID do Cliente: ");
        String id = scanner.nextLine();
        
        try {
            Cliente cliente = clienteDAO.findByID(id);
            if (cliente != null) {
                System.out.println("\nCliente encontrado:");
                System.out.println(cliente);
            } else {
                System.out.println("Cliente não encontrado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
        }
    }
    
    private void atualizarCliente() {
        System.out.print("ID do Cliente: ");
        String id = scanner.nextLine();
        
        try {
            Cliente cliente = clienteDAO.findByID(id);
            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }
            
            System.out.println("Dados atuais: " + cliente);
            System.out.println("\nNovos dados (deixe em branco para manter o valor atual):");
            
            System.out.print("Nome: ");
            String nome = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Telefone: ");
            String telefone = scanner.nextLine();

            Cliente updatedCliente = new Cliente(
                nome.isEmpty() ? cliente.getNome() : nome,
                email.isEmpty() ? cliente.getEmail() : email,
                telefone.isEmpty() ? cliente.getTelefone() : telefone,
                id
            );
            
            clienteDAO.update(updatedCliente);
            System.out.println("Cliente atualizado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }
    
    private void removerCliente() {
        System.out.print("ID do Cliente: ");
        String id = scanner.nextLine();
        
        try {
            Cliente cliente = clienteDAO.findByID(id);
            if (cliente == null) {
                System.out.println("Cliente não encontrado.");
                return;
            }
            
            System.out.println("Confirmação: Deseja remover o cliente abaixo?");
            System.out.println(cliente);
            System.out.print("Digite 'SIM' para confirmar: ");
            String confirmacao = scanner.nextLine();
            
            if (confirmacao.equalsIgnoreCase("SIM")) {
                clienteDAO.deleteById(id);
                System.out.println("Cliente removido com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao remover cliente: " + e.getMessage());
        }
    }


    private void cadastrarQuarto() {
        System.out.print("Número do Quarto: ");
        String numero = scanner.nextLine();
        System.out.print("Tipo (Standard, Deluxe, Suite): ");
        String tipo = scanner.nextLine();
        System.out.print("Preço por diária: ");
        double preco = scanner.nextDouble();
        scanner.nextLine();
        
        quarto novoQuarto = new quarto(numero, tipo, preco);
        try {
            quartoDAO.save(novoQuarto);
            System.out.println("Quarto cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar quarto: " + e.getMessage());
        }
    }
    
    private void listarQuartos() {
        try {
            List<quarto> quartos = quartoDAO.findAll();
            if (quartos.isEmpty()) {
                System.out.println("Nenhum quarto cadastrado.");
                return;
            }
            
            System.out.println("\nLista de Quartos:");
            quartos.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Erro ao listar quartos: " + e.getMessage());
        }
    }
    
    private void buscarQuarto() {
        System.out.print("Número do Quarto: ");
        String numero = scanner.nextLine();
        
        try {
            quarto q = quartoDAO.findByID(numero);
            if (q != null) {
                System.out.println("\nQuarto encontrado:");
                System.out.println(q);
            } else {
                System.out.println("Quarto não encontrado.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar quarto: " + e.getMessage());
        }
    }
    
    private void atualizarQuarto() {
        System.out.print("Número do Quarto: ");
        String numero = scanner.nextLine();
        
        try {
            quarto q = quartoDAO.findByID(numero);
            if (q == null) {
                System.out.println("Quarto não encontrado.");
                return;
            }
            
            System.out.println("Dados atuais: " + q);
            System.out.println("\nNovos dados (deixe em branco para manter o valor atual):");
            
            System.out.print("Tipo (Standard, Deluxe, Suite): ");
            String tipo = scanner.nextLine();
            
            System.out.print("Preço por diária: ");
            String precoStr = scanner.nextLine();
            

            quarto updatedQuarto = new quarto(
                numero,
                tipo.isEmpty() ? q.getTipo() : tipo,
                precoStr.isEmpty() ? q.getPreco() : Double.parseDouble(precoStr)
            );
            
            quartoDAO.update(updatedQuarto);
            System.out.println("Quarto atualizado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao atualizar quarto: " + e.getMessage());
        }
    }
    
    private void removerQuarto() {
        System.out.print("Número do Quarto: ");
        String numero = scanner.nextLine();
        
        try {
            quarto q = quartoDAO.findByID(numero);
            if (q == null) {
                System.out.println("Quarto não encontrado.");
                return;
            }
            
            System.out.println("Confirmação: Deseja remover o quarto abaixo?");
            System.out.println(q);
            System.out.print("Digite 'SIM' para confirmar: ");
            String confirmacao = scanner.nextLine();
            
            if (confirmacao.equalsIgnoreCase("SIM")) {
                quartoDAO.deleteById(numero);
                System.out.println("Quarto removido com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao remover quarto: " + e.getMessage());
        }
    }


    private void fazerReserva() {
        try {

            List<Cliente> clientes = clienteDAO.findAll();
            if (clientes.isEmpty()) {
                System.out.println("Não há clientes cadastrados. Cadastre um cliente primeiro.");
                return;
            }
            

            List<quarto> quartos = quartoDAO.findAll();
            if (quartos.isEmpty()) {
                System.out.println("Não há quartos cadastrados. Cadastre um quarto primeiro.");
                return;
            }
            

            System.out.println("\nClientes disponíveis:");
            for (int i = 0; i < clientes.size(); i++) {
                System.out.println((i+1) + ". " + clientes.get(i));
            }
            
            System.out.print("Selecione o número do cliente: ");
            int clienteIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (clienteIndex < 0 || clienteIndex >= clientes.size()) {
                System.out.println("Seleção inválida!");
                return;
            }
            

            System.out.println("\nQuartos disponíveis:");
            for (int i = 0; i < quartos.size(); i++) {
                System.out.println((i+1) + ". " + quartos.get(i));
            }
            
            System.out.print("Selecione o número do quarto: ");
            int quartoIndex = scanner.nextInt() - 1;
            scanner.nextLine();
            
            if (quartoIndex < 0 || quartoIndex >= quartos.size()) {
                System.out.println("Seleção inválida!");
                return;
            }
            

            System.out.print("Data de check-in (dd/MM/yyyy): ");
            String checkInStr = scanner.nextLine();
            LocalDate checkIn;
            try {
                checkIn = LocalDate.parse(checkInStr, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido!");
                return;
            }
            
            System.out.print("Data de check-out (dd/MM/yyyy): ");
            String checkOutStr = scanner.nextLine();
            LocalDate checkOut;
            try {
                checkOut = LocalDate.parse(checkOutStr, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido!");
                return;
            }
            

            if (checkIn.isAfter(checkOut) || checkIn.equals(checkOut)) {
                System.out.println("Data de check-in deve ser anterior à data de check-out!");
                return;
            }
            
            if (checkIn.isBefore(LocalDate.now())) {
                System.out.println("Data de check-in deve ser a partir de hoje!");
                return;
            }
            

            String reservaId = String.valueOf(System.currentTimeMillis());
            reserva novaReserva = new reserva(
                reservaId, 
                clientes.get(clienteIndex).getId(),
                quartos.get(quartoIndex).getNumero(),
                checkIn,
                checkOut
            );
            
            reservaDAO.save(novaReserva);
            System.out.println("Reserva realizada com sucesso! ID: " + reservaId);
            
        } catch (Exception e) {
            System.err.println("Erro ao fazer reserva: " + e.getMessage());
        }
    }
    
    private void listarReservas() {
        try {
            List<reserva> reservas = reservaDAO.findAll();
            if (reservas.isEmpty()) {
                System.out.println("Nenhuma reserva encontrada.");
                return;
            }
            
            System.out.println("\nLista de Reservas:");
            for (reserva r : reservas) {
                Cliente cliente = clienteDAO.findByID(r.getClientId());
                quarto q = quartoDAO.findByID(r.getNumQuarto());
                
                System.out.println("Reserva ID: " + r.getId());
                System.out.println("Cliente: " + (cliente != null ? cliente.getNome() : "Cliente não encontrado"));
                System.out.println("Quarto: " + (q != null ? q.getNumero() + " (" + q.getTipo() + ")" : "Quarto não encontrado"));
                System.out.println("Check-in: " + r.getCheckIn().format(dateFormatter));
                System.out.println("Check-out: " + r.getCheckOut().format(dateFormatter));
                System.out.println("------------------------");
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar reservas: " + e.getMessage());
        }
    }
    
    private void buscarReserva() {
        System.out.print("ID da Reserva: ");
        String id = scanner.nextLine();
        
        try {
            reserva r = reservaDAO.findByID(id);
            if (r == null) {
                System.out.println("Reserva não encontrada.");
                return;
            }
            
            Cliente cliente = clienteDAO.findByID(r.getClientId());
            quarto q = quartoDAO.findByID(r.getNumQuarto());
            
            System.out.println("\nReserva encontrada:");
            System.out.println("Reserva ID: " + r.getId());
            System.out.println("Cliente: " + (cliente != null ? cliente.getNome() : "Cliente não encontrado"));
            System.out.println("Quarto: " + (q != null ? q.getNumero() + " (" + q.getTipo() + ")" : "Quarto não encontrado"));
            System.out.println("Check-in: " + r.getCheckIn().format(dateFormatter));
            System.out.println("Check-out: " + r.getCheckOut().format(dateFormatter));
        } catch (Exception e) {
            System.err.println("Erro ao buscar reserva: " + e.getMessage());
        }
    }
    
    private void cancelarReserva() {
        System.out.print("ID da Reserva: ");
        String id = scanner.nextLine();
        
        try {
            reserva r = reservaDAO.findByID(id);
            if (r == null) {
                System.out.println("Reserva não encontrada.");
                return;
            }
            
            Cliente cliente = clienteDAO.findByID(r.getClientId());
            quarto q = quartoDAO.findByID(r.getNumQuarto());
            
            System.out.println("\nConfirmação: Deseja cancelar a reserva abaixo?");
            System.out.println("Reserva ID: " + r.getId());
            System.out.println("Cliente: " + (cliente != null ? cliente.getNome() : "Cliente não encontrado"));
            System.out.println("Quarto: " + (q != null ? q.getNumero() + " (" + q.getTipo() + ")" : "Quarto não encontrado"));
            System.out.println("Check-in: " + r.getCheckIn().format(dateFormatter));
            System.out.println("Check-out: " + r.getCheckOut().format(dateFormatter));
            
            System.out.print("Digite 'SIM' para confirmar: ");
            String confirmacao = scanner.nextLine();
            
            if (confirmacao.equalsIgnoreCase("SIM")) {
                reservaDAO.deleteById(id);
                System.out.println("Reserva cancelada com sucesso!");
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao cancelar reserva: " + e.getMessage());
        }
    }
}