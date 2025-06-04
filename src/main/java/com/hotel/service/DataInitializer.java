package com.hotel.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.hotel.dao.ClienteDAO;
import com.hotel.dao.QuartoDAO;
import com.hotel.dao.ReservaDAO;
import com.hotel.model.Cliente;
import com.hotel.model.Quarto;
import com.hotel.model.Reserva;
import com.hotel.model.Reserva.ReservaStatus;

public class DataInitializer {
    private final ClienteDAO clienteDAO;
    private final QuartoDAO quartoDAO;
    private final ReservaDAO reservaDAO;
    private final Random random = new Random();

    private static final String CLIENT_FILE = "data/clientes.dat";
    private static final String ROOM_FILE = "data/quartos.dat";
    private static final String RESERVATION_FILE = "data/reservas.dat";

    public DataInitializer(ClienteDAO clienteDAO, QuartoDAO quartoDAO, ReservaDAO reservaDAO) {
        this.clienteDAO = clienteDAO;
        this.quartoDAO = quartoDAO;
        this.reservaDAO = reservaDAO;
    }

    public void initializeData() throws IOException {
        File clientFile = new File(CLIENT_FILE);
        File roomFile = new File(ROOM_FILE);
        File reservationFile = new File(RESERVATION_FILE);

        // Check if data files exist. If any exist, assume data is already initialized.
        if (clientFile.exists() || roomFile.exists() || reservationFile.exists()) {
            System.out.println("Data files already exist. Skipping initialization.");
            return;
        }

        // If no data files exist, perform initial population
        System.out.println("Data files not found. Initializing data...");
        clienteDAO.deleteAll();
        quartoDAO.deleteAll();
        reservaDAO.deleteAll();

        initializeQuartos();
        initializeClientes();
        initializeReservas();
        System.out.println("Data initialization complete.");
    }

    private void initializeClientes() throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        String[] angolanNames = {
            "Maria", "António", "Manuel", "Isabel", "Pedro", "Ana", "José", "João", "Francisco", "Jose",
            "Carlos", "Bruno", "Fernando", "Daniel", "Lil", "Paula", "Carla", "Daniela", "Jéssica",
            "Bruna", "Isabel", "Alexandra", "Adrien", "Helder", "JOAO", "Denis", "Jordan da Glória",
            "aplha camara", "thierno", "Guelo", "ousman", "edgar", "Kim", "arioste", "Tobiana",
            "Pel", "Enano", "lamine", "bagamba", "Wenny", "Horácio", "RAUL", "Ernesto", "Loth",
            "Benarld", "Abdourahime", "Sam", "faical", "maxime", "Chemah Dennis", "JOHN",
            "Adriano", "Semai", "FISTON JOHN", "Miguel", "Daniel", "JOHN MWALILWA", "soba ngenda",
            "geraldo", "Nelson"
        };

        for (int i = 0; i < 50; i++) {
            String id = UUID.randomUUID().toString().substring(0, 8);
            String nome = angolanNames[random.nextInt(angolanNames.length)];
            String email = nome.toLowerCase().replace(" ", ".") + i + "@example.com";
            String telefone = generateAngolanPhoneNumber();
            clientes.add(new Cliente(id, nome, email, telefone));
        }
        for (Cliente cliente : clientes) {
            clienteDAO.save(cliente);
        }
    }

    private String generateAngolanPhoneNumber() {
        int prefix = random.nextInt(3);
        int middle = random.nextInt(900) + 100;
        int suffix = random.nextInt(1000);
        return String.format("+244 9%d %d %03d", prefix + 1, middle, suffix);
    }

    private void initializeQuartos() throws IOException {
        List<Quarto> quartos = new ArrayList<>();
        String[] types = {"Double(2P)", "Casal(2P)", "Deluxe(4P)", "Executive", "Single(1P)"};
        double[] prices = {190000.0, 200000.0, 225000.0, 300000.0, 175000.0};
        int[] counts = {70, 70, 70, 70, 70};

        List<String> doubleAmenities = List.of("Wi-Fi Grátis", "TV", "Ar Condicionado");
        String doubleDescription = "Quarto confortável com cama de casal.";

        List<String> casalAmenities = List.of("Wi-Fi Grátis", "TV", "Mini-frigorífico");
        String casalDescription = "Quarto acolhedor para casais.";

        List<String> deluxeAmenities = List.of("Wi-Fi Grátis", "TV", "Varanda", "Jacuzzi");
        String deluxeDescription = "Quarto deluxe espaçoso com comodidades extras.";

        List<String> executiveAmenities = List.of("Wi-Fi Grátis", "TV", "Escritório", "Cafeteira", "Vista para a Cidade");
        String executiveDescription = "Quarto premium para viajantes de negócios.";

        List<String> singleAmenities = List.of("Wi-Fi Grátis", "TV");
        String singleDescription = "Quarto compacto para viajantes individuais.";

        int roomNumber = 1;
        for (int i = 0; i < types.length; i++) {
            List<String> currentAmenities;
            String currentDescription;
            switch (types[i]) {
                case "Double(2P)":
                    currentAmenities = doubleAmenities;
                    currentDescription = doubleDescription;
                    break;
                case "Casal(2P)":
                    currentAmenities = casalAmenities;
                    currentDescription = casalDescription;
                    break;
                case "Deluxe(4P)":
                    currentAmenities = deluxeAmenities;
                    currentDescription = deluxeDescription;
                    break;
                case "Executive":
                    currentAmenities = executiveAmenities;
                    currentDescription = executiveDescription;
                    break;
                case "Single(1P)":
                    currentAmenities = singleAmenities;
                    currentDescription = singleDescription;
                    break;
                default:
                    currentAmenities = new ArrayList<>();
                    currentDescription = "";
            }
            for (int j = 0; j < counts[i]; j++) {
                quartos.add(new Quarto(String.valueOf(roomNumber++), types[i], prices[i], true, currentAmenities, currentDescription));
            }
        }

        for (Quarto quarto : quartos) {
            quartoDAO.save(quarto);
        }
    }

    private void initializeReservas() throws IOException {
        List<Cliente> allClients = clienteDAO.findAll();
        List<Quarto> allRooms = quartoDAO.findAll();
        List<Reserva> reservas = new ArrayList<>();

        if (allClients.isEmpty() || allRooms.isEmpty()) {
            System.err.println("Não é possível inicializar reservas: Clientes ou Quartos estão vazios.");
            return;
        }

        for (int i = 0; i < 50; i++) {
            Cliente cliente = allClients.get(random.nextInt(allClients.size()));
            Quarto quarto = allRooms.get(random.nextInt(allRooms.size()));

            LocalDate checkIn = LocalDate.now().plusDays(random.nextInt(30));
            LocalDate checkOut = checkIn.plusDays(random.nextInt(7) + 1);

            reservas.add(new Reserva(
                UUID.randomUUID().toString().substring(0, 8),
                cliente,
                quarto,
                checkIn,
                checkOut,
                ReservaStatus.CONFIRMED
            ));
        }

        for (Reserva reserva : reservas) {
             boolean roomAvailable = true;
             for(Reserva existingReserva : reservaDAO.findAll()){
                 if(existingReserva.getQuarto().equals(reserva.getQuarto()) && datesOverlap(existingReserva.getCheckIn(), existingReserva.getCheckOut(), reserva.getCheckIn(), reserva.getCheckOut())){
                     roomAvailable = false;
                     break;
                 }
             }
             if(roomAvailable){
                 reservaDAO.save(reserva);
                 quartoDAO.updateRoomStatus(reserva.getQuarto().getNumero(), false);
             }
        }
    }
    
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
} 