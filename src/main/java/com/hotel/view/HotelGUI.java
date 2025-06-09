package com.hotel.view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.hotel.dao.ClienteDAO;
import com.hotel.dao.PaymentDAO;
import com.hotel.dao.QuartoDAO;
import com.hotel.dao.ReservaDAO;
import com.hotel.dao.WorkerDAO;
import com.hotel.model.Cliente;
import com.hotel.model.Payment;
import com.hotel.model.Quarto;
import com.hotel.model.Reserva;
import com.hotel.model.Reserva.ReservaStatus;
import com.hotel.model.Worker;
import com.hotel.service.DataInitializer;
import com.hotel.service.QuartoService;
import com.hotel.service.ReservaService;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HotelGUI extends Application {
    // DAOs e serviços necessários para o funcionamento do sistema
    private final ClienteDAO clienteDAO;
    private final QuartoDAO quartoDAO;
    private final ReservaDAO reservaDAO;
    private final PaymentDAO paymentDAO;
    private final QuartoService quartoService;
    private final ReservaService reservaService;
    private final DataInitializer dataInitializer;

    // Tabelas para exibição dos dados
    private TableView<Cliente> clientTable;
    private TableView<Quarto> roomTable;
    private TableView<Reserva> reservationTable;

    // Labels para mostrar o resumo de disponibilidade dos quartos
    private Label availableRoomsLabel = new Label();
    private Label occupiedRoomsLabel = new Label();
    private Label occupancyRateLabel = new Label();

    // Priscila: Interface principal do sistema de gestão hoteleira
    public HotelGUI() {
        this.clienteDAO = new ClienteDAO();
        this.quartoDAO = new QuartoDAO();
        this.reservaDAO = new ReservaDAO();
        this.paymentDAO = new PaymentDAO();
        this.quartoService = new QuartoService(quartoDAO);
        this.reservaService = new ReservaService(reservaDAO, quartoDAO);
        this.dataInitializer = new DataInitializer(clienteDAO, quartoDAO, reservaDAO);
    }

    // Priscila: Inicializa a interface gráfica e faz o login do usuário
    @Override
    public void start(Stage primaryStage) {
        WorkerDAO workerDAO = new WorkerDAO();
        LoginDialog loginDialog = new LoginDialog(workerDAO);
        Optional<Worker> worker = loginDialog.showAndWait();
        while (worker.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Falha no Login");
            alert.setHeaderText("Autenticação Falhou");
            alert.setContentText("Nome de usuário ou senha inválidos. Por favor, tente novamente.");
            alert.showAndWait();
            worker = loginDialog.showAndWait();
        }
        try {
            dataInitializer.initializeData();
        } catch (IOException e) {
            showError("Erro ao inicializar dados", e.getMessage());
        }
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createClientsTab(),
            createRoomsTab(),
            createReservationsTab()
        );
        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Sistema de Gestão Hoteleira");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Priscila: Cria a aba de gerenciamento de clientes
    private Tab createClientsTab() {
        Tab tab = new Tab("Clientes");
        tab.setClosable(false);

        clientTable = new TableView<>();
        clientTable.getColumns().addAll(
            createColumn("ID", "id"),
            createColumn("Nome", "nome"),
            createColumn("Email", "email"),
            createColumn("Telefone", "telefone")
        );

        ButtonBar buttonBar = new ButtonBar();
        Button addButton = new Button("Adicionar Cliente");
        Button editButton = new Button("Editar Cliente");
        Button deleteButton = new Button("Eliminar Cliente");

        addButton.setOnAction(e -> showClientDialog(null));
        editButton.setOnAction(e -> {
            Cliente selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showClientDialog(selected);
            }
        });
        deleteButton.setOnAction(e -> {
            Cliente selected = clientTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteClient(selected);
            }
        });

        buttonBar.getButtons().addAll(addButton, editButton, deleteButton);

        VBox vbox = new VBox(10, clientTable, buttonBar);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);

        refreshClientTable();
        return tab;
    }

    // Priscila: Cria a aba de gerenciamento de quartos
    private Tab createRoomsTab() {
        Tab tab = new Tab("Quartos");
        tab.setClosable(false);

        roomTable = new TableView<>();

        // Create standard columns
        TableColumn<Quarto, String> numberColumn = createColumn("Número", "numero");
        TableColumn<Quarto, String> typeColumn = createColumn("Tipo", "tipo");
        TableColumn<Quarto, String> priceColumn = createColumn("Preço", "preco");

        // Create and configure the custom "Available" column
        TableColumn<Quarto, Boolean> availableColumn = new TableColumn<>("Disponível");
        availableColumn.setCellValueFactory(cellData -> {
            // Use SimpleBooleanProperty for boolean value
            return new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isDisponivel());
        });

        availableColumn.setCellFactory(column -> new TableCell<Quarto, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    boolean isAvailable = item; // item is already a Boolean
                    setText(isAvailable ? "Disponível" : "Ocupado");
                    // Apply color based on availability
                    if (isAvailable) {
                        setStyle("-fx-text-fill: green;");
                    } else {
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });

        // Add all columns to the table
        roomTable.getColumns().addAll(
            numberColumn,
            typeColumn,
            priceColumn,
            availableColumn // Add the custom column
        );

        // Labels for availability summary
        Label availableRoomsLabel = new Label();
        Label occupiedRoomsLabel = new Label();
        Label occupancyRateLabel = new Label();

        ButtonBar buttonBar = new ButtonBar();
        Button addButton = new Button("Adicionar Quarto");
        Button editButton = new Button("Editar Quarto");
        Button deleteButton = new Button("Eliminar Quarto");

        addButton.setOnAction(e -> showRoomDialog(null));
        editButton.setOnAction(e -> {
            Quarto selected = roomTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showRoomDialog(selected);
            }
        });
        deleteButton.setOnAction(e -> {
            Quarto selected = roomTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteRoom(selected);
            }
        });

        buttonBar.getButtons().addAll(addButton, editButton, deleteButton);

        // Add labels, table, and buttons to the VBox
        VBox vbox = new VBox(10,
            availableRoomsLabel,
            occupiedRoomsLabel,
            occupancyRateLabel,
            roomTable,
            buttonBar
        );
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);

        refreshRoomTable();
        return tab;
    }

    // Priscila: Cria a aba de gerenciamento de reservas
    private Tab createReservationsTab() {
        Tab tab = new Tab("Reservas");
        tab.setClosable(false);

        reservationTable = new TableView<>();
        reservationTable.getColumns().addAll(
            createColumn("ID", "id"),
            createColumn("Cliente", "cliente.nome"),
            createColumn("Quarto", "quarto.numero"),
            createColumn("Check-in", "checkIn"),
            createColumn("Check-out", "checkOut"),
            createColumn("Estado", "status")
        );

        // Priscila: Adiciona um listener para clicar duas vezes em uma linha para mostrar os detalhes da reserva
        reservationTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Reserva selectedReserva = reservationTable.getSelectionModel().getSelectedItem();
                if (selectedReserva != null) {
                    showReservationDetailsDialog(selectedReserva);
                }
            }
        });

        ButtonBar buttonBar = new ButtonBar();
        Button addButton = new Button("Nova Reserva");
        Button cancelButton = new Button("Cancelar Reserva");
        Button checkInButton = new Button("Check-in");
        Button checkOutButton = new Button("Check-out");
        Button editButton = new Button("Editar Reserva");

        addButton.setOnAction(e -> showReservationDialog(null));
        cancelButton.setOnAction(e -> {
            Reserva selected = reservationTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cancelReservation(selected);
            }
        });
        checkInButton.setOnAction(e -> {
            Reserva selected = reservationTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                checkInReservation(selected);
            }
        });
        checkOutButton.setOnAction(e -> {
            Reserva selected = reservationTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                checkOutReservation(selected);
            }
        });
        editButton.setOnAction(e -> {
            Reserva selected = reservationTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                editReservation(selected);
            }
        });

        buttonBar.getButtons().addAll(addButton, editButton, cancelButton, checkInButton, checkOutButton);

        VBox vbox = new VBox(10, reservationTable, buttonBar);
        vbox.setPadding(new Insets(10));
        tab.setContent(vbox);

        refreshReservationTable();
        return tab;
    }

    // Cria uma coluna para a tabela com o título e propriedade especificados
    private <T> TableColumn<T, String> createColumn(String title, String property) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> {
            try {
                // Handle nested properties
                String[] properties = property.split("\\.");
                Object value = data.getValue();
                for (String prop : properties) {
                    // Correctly get the method name for the property
                    String getterMethodName = "get" + prop.substring(0, 1).toUpperCase() + prop.substring(1);
                    value = value.getClass().getMethod(getterMethodName).invoke(value);
                }
                return new SimpleStringProperty(value != null ? value.toString() : "");
            } catch (Exception e) {
                // Print stack trace for debugging during development
                e.printStackTrace();
                return new SimpleStringProperty("");
            }
        });
        return column;
    }

    // Priscila: Mostra o diálogo para adicionar ou editar um cliente
    private void showClientDialog(Cliente cliente) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(cliente == null ? "Adicionar Cliente" : "Editar Cliente");

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();

        // Disable ID field for existing clients and new clients (will be generated)
        idField.setEditable(false);

        if (cliente != null) {
            idField.setText(cliente.getId());
            nameField.setText(cliente.getNome());
            emailField.setText(cliente.getEmail());
            phoneField.setText(cliente.getTelefone());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("ID:"), idField);
        grid.addRow(1, new Label("Nome:"), nameField);
        grid.addRow(2, new Label("Email:"), emailField);
        grid.addRow(3, new Label("Telefone:"), phoneField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String email = emailField.getText();
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showError("Erro de Email", "Formato de email inválido.");
                    return null;
                }

                String phone = phoneField.getText();
                if (!phone.matches("^\\+244 \\d{3} \\d{3} \\d{3}$")) {
                     showError("Erro de Telefone", "Formato de telefone inválido. Esperado: +244 xxx xxx xxx");
                     return null;
                }

                // Generate new ID only for new clients
                String clientId = (cliente == null) ? java.util.UUID.randomUUID().toString().substring(0, 8) : cliente.getId();

                return new Cliente(
                    clientId,
                    nameField.getText(),
                    email,
                    phone
                );
            }
            return null;
        });

        Optional<Cliente> result = dialog.showAndWait();
        result.ifPresent(c -> {
            try {
                clienteDAO.save(c);
                refreshClientTable();
            } catch (IOException e) {
                showError("Erro ao salvar cliente", e.getMessage());
            }
        });
    }

    // Priscila: Mostra o diálogo para adicionar ou editar um quarto
    private void showRoomDialog(Quarto quarto) {
        Dialog<Quarto> dialog = new Dialog<>();
        dialog.setTitle(quarto == null ? "Adicionar Quarto" : "Editar Quarto");

        TextField numberField = new TextField();
        TextField typeField = new TextField();
        TextField priceField = new TextField();
        CheckBox availableBox = new CheckBox("Disponível");
        Label amenitiesLabel = new Label("Comodidades:");
        Label descriptionLabel = new Label("Descrição:");
        TextField amenitiesField = new TextField(); // Display amenities as text
        TextField descriptionField = new TextField(); // Display description as text

        // Disable editing for display fields
        amenitiesField.setEditable(false);
        descriptionField.setEditable(false);

        if (quarto != null) {
            numberField.setText(quarto.getNumero());
            typeField.setText(quarto.getTipo());
            priceField.setText(String.valueOf(quarto.getPreco()));
            availableBox.setSelected(quarto.isDisponivel());
            // Display amenities and description
            amenitiesField.setText(String.join(", ", quarto.getAmenities()));
            descriptionField.setText(quarto.getDescription());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Número:"), numberField);
        grid.addRow(1, new Label("Tipo:"), typeField);
        grid.addRow(2, new Label("Preço:"), priceField);
        grid.addRow(3, new Label("Disponível:"), availableBox);
        grid.addRow(4, amenitiesLabel, amenitiesField);
        grid.addRow(5, descriptionLabel, descriptionField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Otniel: When editing, existing amenities and description will be preserved
                List<String> amenities = (quarto != null) ? quarto.getAmenities() : new ArrayList<>();
                String description = (quarto != null) ? quarto.getDescription() : "";

                return new Quarto(
                    numberField.getText(),
                    typeField.getText(),
                    Double.parseDouble(priceField.getText()),
                    availableBox.isSelected(),
                    amenities,
                    description
                );
            }
            return null;
        });

        Optional<Quarto> result = dialog.showAndWait();
        result.ifPresent(q -> {
            try {
                quartoService.addRoom(q); // addRoom handles both adding and updating
                refreshRoomTable();
            } catch (IOException e) {
                showError("Erro ao salvar quarto", e.getMessage());
            }
        });
    }

    // Priscila: Mostra o diálogo para criar uma nova reserva
    private void showReservationDialog(Reserva reserva) {
        Dialog<Reserva> dialog = new Dialog<>();
        dialog.setTitle("Nova Reserva");

        ComboBox<Cliente> clientCombo = new ComboBox<>();
        ComboBox<Quarto> roomCombo = new ComboBox<>();
        DatePicker checkInPicker = new DatePicker();
        DatePicker checkOutPicker = new DatePicker();

        clientCombo.setItems(FXCollections.observableArrayList(clienteDAO.findAll()));
        roomCombo.setItems(FXCollections.observableArrayList(quartoService.getAvailableRooms()));

        checkInPicker.setValue(LocalDate.now());
        checkOutPicker.setValue(LocalDate.now().plusDays(1));

        // Add listeners to date pickers to update room availability
        checkInPicker.valueProperty().addListener((obs, oldDate, newDate) -> updateAvailableRooms(roomCombo, newDate, checkOutPicker.getValue()));
        checkOutPicker.valueProperty().addListener((obs, oldDate, newDate) -> updateAvailableRooms(roomCombo, checkInPicker.getValue(), newDate));

        // Initial update of available rooms
        updateAvailableRooms(roomCombo, checkInPicker.getValue(), checkOutPicker.getValue());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Cliente:"), clientCombo);
        grid.addRow(1, new Label("Quarto:"), roomCombo);
        grid.addRow(2, new Label("Check-in:"), checkInPicker);
        grid.addRow(3, new Label("Check-out:"), checkOutPicker);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Otniel: Add a final availability check before closing the dialog
        // Otniel: This is crucial in case dates were changed after room selection
        dialog.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            LocalDate checkIn = checkInPicker.getValue();
            LocalDate checkOut = checkOutPicker.getValue();
            Quarto selectedRoom = roomCombo.getValue();

            if (checkIn == null || checkOut == null || selectedRoom == null) {
                showError("Erro de Seleção", "Por favor, selecione as datas de check-in, check-out e um quarto.");
                event.consume();
                return;
            }

            if (!reservaService.isRoomAvailable(selectedRoom.getNumero(), checkIn, checkOut)) {
                showError("Erro de Disponibilidade", "O quarto selecionado não está mais disponível para as datas escolhidas.");
                event.consume(); // Prevent dialog from closing
            }
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
                    showError("Erro de Data", "As datas de check-in e check-out devem ser selecionadas.");
                    return null;
                }
                if (checkInPicker.getValue().isAfter(checkOutPicker.getValue())) {
                    showError("Erro de Data", "A data de check-in deve ser anterior à data de check-out.");
                    return null;
                }

                Cliente selectedClient = clientCombo.getValue();
                Quarto selectedRoom = roomCombo.getValue();

                if (selectedClient == null) {
                    showError("Erro de Seleção", "Por favor, selecione um cliente.");
                    return null;
                }

                if (selectedRoom == null) {
                    showError("Erro de Seleção", "Por favor, selecione um quarto.");
                    return null;
                }

                // Check room availability before creating reservation
                if (!reservaService.isRoomAvailable(selectedRoom.getNumero(), checkInPicker.getValue(), checkOutPicker.getValue())) {
                     showError("Erro de Disponibilidade", "O quarto selecionado não está disponível para as datas escolhidas.");
                     return null;
                }

                try {
                    // Create the reservation object
                    Reserva newReserva = new Reserva(
                        java.util.UUID.randomUUID().toString().substring(0, 8), // Shortens the UUID
                        selectedClient,
                        selectedRoom,
                        checkInPicker.getValue(),
                        checkOutPicker.getValue(),
                        ReservaStatus.CONFIRMED // Set status to CONFIRMED
                    );
                    return newReserva; // Return the created reservation
                } catch (Exception e) {
                    showError("Erro ao criar reserva", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Reserva> result = dialog.showAndWait();
        result.ifPresent(r -> {
            // Calculate amount due
            double pricePerNight = r.getQuarto().getPreco();
            long nights = java.time.temporal.ChronoUnit.DAYS.between(r.getCheckIn(), r.getCheckOut());
            double amountDue = pricePerNight * nights;
            // Show payment dialog
            Dialog<Payment> paymentDialog = new Dialog<>();
            paymentDialog.setTitle("Pagamento");
            paymentDialog.setHeaderText("Insira os detalhes do pagamento");
            GridPane payGrid = new GridPane();
            payGrid.setHgap(10);
            payGrid.setVgap(10);
            payGrid.setPadding(new Insets(20, 150, 10, 10));
            TextField amountDueField = new TextField(String.format("%.2f", amountDue));
            amountDueField.setEditable(false);
            TextField amountReceivedField = new TextField();
            TextField changeField = new TextField();
            changeField.setEditable(false);
            ComboBox<String> methodCombo = new ComboBox<>(FXCollections.observableArrayList("Dinheiro", "Cartão", "Outro"));
            methodCombo.getSelectionModel().selectFirst();
            payGrid.add(new Label("Valor Devido:"), 0, 0);
            payGrid.add(amountDueField, 1, 0);
            payGrid.add(new Label("Valor Recebido:"), 0, 1);
            payGrid.add(amountReceivedField, 1, 1);
            payGrid.add(new Label("Troco:"), 0, 2);
            payGrid.add(changeField, 1, 2);
            payGrid.add(new Label("Método:"), 0, 3);
            payGrid.add(methodCombo, 1, 3);
            amountReceivedField.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    double received = Double.parseDouble(newVal);
                    if (received < 0) {
                        changeField.setText("Valor inválido");
                        return;
                    }
                    double change = received - amountDue;
                    changeField.setText(String.format("%.2f", change >= 0 ? change : 0));
                } catch (Exception ex) {
                    changeField.setText("");
                }
            });
            paymentDialog.getDialogPane().setContent(payGrid);
            paymentDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Add event filter to the OK button for validation
            Node okButton = paymentDialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                try {
                    double received = Double.parseDouble(amountReceivedField.getText());

                    if (received < 0) {
                        showError("Erro de Pagamento", "O valor recebido não pode ser negativo.");
                        event.consume(); // Prevent dialog from closing
                    } else if (received < amountDue) {
                        showError("Erro de Pagamento", "O valor recebido é menor que o valor devido.");
                        event.consume(); // Prevent dialog from closing
                    }
                } catch (NumberFormatException ex) {
                    showError("Erro de Pagamento", "Entrada de pagamento inválida.");
                    event.consume(); // Prevent dialog from closing
                }
            });

            paymentDialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        double received = Double.parseDouble(amountReceivedField.getText());
                        double change = received - amountDue;
                        return new Payment(r.getId(), amountDue, received, change, methodCombo.getValue());
                    } catch (NumberFormatException ex) {
                         // Otniel: This catch block might be redundant due to event filter, but keeping for safety
                        showError("Erro de Pagamento", "Entrada de pagamento inválida.");
                        return null;
                    }
                }
                return null;
            });

            Optional<Payment> paymentResult = paymentDialog.showAndWait();
            paymentResult.ifPresent(payment -> {
                // Otniel: Do NOT save the payment
                     return; 
            });
        });
    }

    // Atualiza a lista de quartos disponíveis baseado nas datas selecionadas
    private void updateAvailableRooms(ComboBox<Quarto> roomCombo, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn != null && checkOut != null && checkIn.isBefore(checkOut)) {
            List<Quarto> availableRooms = reservaService.getAvailableRooms(checkIn, checkOut);
            roomCombo.setItems(FXCollections.observableArrayList(availableRooms));
            // Otniel: Clear the selection if the previously selected room is no longer available
            if (roomCombo.getSelectionModel().getSelectedItem() != null && !availableRooms.contains(roomCombo.getSelectionModel().getSelectedItem())) {
                roomCombo.getSelectionModel().clearSelection();
            }
        } else {
            roomCombo.setItems(FXCollections.observableArrayList()); // Otniel: Show empty list if dates are invalid
        }
    }

    // Remove um cliente do sistema após confirmação
    private void deleteClient(Cliente cliente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminação");
        alert.setHeaderText("Eliminar Cliente");
        alert.setContentText("Tem certeza que deseja eliminar este cliente?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    clienteDAO.deleteById(cliente.getId());
                    refreshClientTable();
                } catch (IOException e) {
                    showError("Erro ao eliminar cliente", e.getMessage());
                }
            }
        });
    }

    // Remove um quarto do sistema após confirmação
    private void deleteRoom(Quarto quarto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminação");
        alert.setHeaderText("Eliminar Quarto");
        alert.setContentText("Tem certeza que deseja eliminar este quarto?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    quartoService.deleteRoom(quarto.getNumero());
                    refreshRoomTable();
                } catch (IOException e) {
                    showError("Erro ao eliminar quarto", e.getMessage());
                }
            }
        });
    }

    // Cancela uma reserva existente após confirmação
    private void cancelReservation(Reserva reserva) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelamento");
        alert.setHeaderText("Cancelar Reserva");
        alert.setContentText("Tem certeza que deseja cancelar esta reserva?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservaService.cancelReservation(reserva.getId());
                    showInfo("Cancelamento", "Reserva " + reserva.getId() + " cancelada com sucesso.");
                    refreshReservationTable();
                } catch (IOException e) {
                    showError("Erro ao cancelar reserva", e.getMessage());
                }
            }
        });
    }

    // Realiza o check-in de uma reserva
    private void checkInReservation(Reserva reserva) {
        try {
            reservaService.checkInReservation(reserva);
            showInfo("Check-in", "Reserva " + reserva.getId() + " fez check-in.");
            refreshReservationTable();
            refreshRoomTable(); // Room availability changed
        } catch (IOException e) {
            showError("Erro de Check-in", "Falha ao fazer check-in da reserva: " + e.getMessage());
        }
    }

    // Realiza o check-out de uma reserva
    private void checkOutReservation(Reserva reserva) {
        try {
            reservaService.checkOutReservation(reserva);
            showInfo("Check-out", "Reserva " + reserva.getId() + " fez check-out.");
            refreshReservationTable();
            refreshRoomTable(); // Room availability changed
        } catch (IOException e) {
            showError("Erro de Check-out", "Falha ao fazer check-out da reserva: " + e.getMessage());
        }
    }

    // Priscila: Edita os detalhes de uma reserva existente
    private void editReservation(Reserva reserva) {
        Dialog<Reserva> dialog = new Dialog<>();
        dialog.setTitle("Editar Reserva");
        dialog.setHeaderText("Editar Detalhes da Reserva");

        ComboBox<Cliente> clientCombo = new ComboBox<>();
        ComboBox<Quarto> roomCombo = new ComboBox<>();
        DatePicker checkInPicker = new DatePicker();
        DatePicker checkOutPicker = new DatePicker();

        clientCombo.setItems(FXCollections.observableArrayList(clienteDAO.findAll()));
        roomCombo.setItems(FXCollections.observableArrayList(quartoService.getAllRooms()));

        // Set current values
        clientCombo.setValue(reserva.getCliente());
        roomCombo.setValue(reserva.getQuarto());
        checkInPicker.setValue(reserva.getCheckIn());
        checkOutPicker.setValue(reserva.getCheckOut());

        // Add listeners to date pickers to update room availability
        checkInPicker.valueProperty().addListener((obs, oldDate, newDate) -> 
            updateAvailableRooms(roomCombo, newDate, checkOutPicker.getValue()));
        checkOutPicker.valueProperty().addListener((obs, oldDate, newDate) -> 
            updateAvailableRooms(roomCombo, checkInPicker.getValue(), newDate));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Cliente:"), clientCombo);
        grid.addRow(1, new Label("Quarto:"), roomCombo);
        grid.addRow(2, new Label("Check-in:"), checkInPicker);
        grid.addRow(3, new Label("Check-out:"), checkOutPicker);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
                    showError("Erro de Data", "As datas de check-in e check-out devem ser selecionadas.");
                    return null;
                }
                if (checkInPicker.getValue().isAfter(checkOutPicker.getValue())) {
                    showError("Erro de Data", "A data de check-in deve ser anterior à data de check-out.");
                    return null;
                }

                Cliente selectedClient = clientCombo.getValue();
                Quarto selectedRoom = roomCombo.getValue();

                if (selectedClient == null) {
                    showError("Erro de Seleção", "Por favor, selecione um cliente.");
                    return null;
                }

                if (selectedRoom == null) {
                    showError("Erro de Seleção", "Por favor, selecione um quarto.");
                    return null;
                }

                // Check room availability before updating reservation
                if (!reserva.getQuarto().equals(selectedRoom) && 
                    !reservaService.isRoomAvailable(selectedRoom.getNumero(), 
                    checkInPicker.getValue(), checkOutPicker.getValue())) {
                    showError("Erro de Disponibilidade", 
                        "O quarto selecionado não está disponível para as datas escolhidas.");
                    return null;
                }

                try {
                    // Create updated reservation object
                    Reserva updatedReserva = new Reserva(
                        reserva.getId(),
                        selectedClient,
                        selectedRoom,
                        checkInPicker.getValue(),
                        checkOutPicker.getValue(),
                        reserva.getStatus()
                    );
                    return updatedReserva;
                } catch (Exception e) {
                    showError("Erro ao atualizar reserva", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Reserva> result = dialog.showAndWait();
        result.ifPresent(updatedReserva -> {
            try {
                // Update the reservation
                reservaService.updateReservation(updatedReserva);
                showInfo("Sucesso", "Reserva atualizada com sucesso.");
                refreshReservationTable();
                refreshRoomTable();
            } catch (IOException e) {
                showError("Erro ao atualizar reserva", e.getMessage());
            }
        });
    }

    // Atualiza a tabela de clientes com os dados mais recentes
    private void refreshClientTable() {
        clientTable.setItems(FXCollections.observableArrayList(clienteDAO.findAll()));
    }

    // Priscila: Atualiza a tabela de quartos e mostra estatísticas
    private void refreshRoomTable() {
        List<Quarto> allRooms = quartoService.getAllRooms();
        List<Quarto> availableRooms = quartoService.getAvailableRooms();

        int totalRooms = allRooms.size();
        int availableCount = availableRooms.size();
        int occupiedCount = totalRooms - availableCount;

        availableRoomsLabel.setText("Quartos Disponíveis: " + availableCount);
        occupiedRoomsLabel.setText("Quartos Ocupados: " + occupiedCount);

        if (totalRooms > 0) {
            double occupancyRate = (double) occupiedCount / totalRooms * 100;
            occupancyRateLabel.setText(String.format("Taxa de Ocupação: %.2f%%", occupancyRate));
        } else {
            occupancyRateLabel.setText("Taxa de Ocupação: N/A");
        }

        roomTable.setItems(FXCollections.observableArrayList(allRooms));
    }

    // Atualiza a tabela de reservas ativas
    private void refreshReservationTable() {
        reservationTable.setItems(FXCollections.observableArrayList(reservaService.getActiveReservations()));
    }

    // Priscila: Mostra uma mensagem de erro para o usuário
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Priscila: Mostra uma mensagem informativa para o usuário
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Priscila: Mostra os detalhes completos de uma reserva
    private void showReservationDetailsDialog(Reserva reserva) {
        Dialog<Reserva> dialog = new Dialog<>();
        dialog.setTitle("Detalhes da Reserva");
        dialog.setHeaderText("Detalhes Completos da Reserva");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.addRow(0, new Label("ID da Reserva:"), new Label(reserva.getId()));
        grid.addRow(1, new Label("Cliente:"), new Label(reserva.getCliente().getNome()));
        grid.addRow(2, new Label("Quarto:"), new Label(reserva.getQuarto().getNumero() + " - " + reserva.getQuarto().getTipo()));
        grid.addRow(3, new Label("Check-in:"), new Label(reserva.getCheckIn().toString()));
        grid.addRow(4, new Label("Check-out:"), new Label(reserva.getCheckOut().toString()));
        grid.addRow(5, new Label("Estado:"), new Label(reserva.getStatus().toString()));

        // Retrieve and display payment details
        Optional<Payment> payment = paymentDAO.findByReservationId(reserva.getId());
        if (payment.isPresent()) {
            Payment p = payment.get();
            grid.addRow(6, new Label("--- Detalhes do Pagamento ---"));
            grid.addRow(7, new Label("Valor Devido:"), new Label(String.format("%.2f", p.getAmountDue())));
            grid.addRow(8, new Label("Valor Recebido:"), new Label(String.format("%.2f", p.getAmountReceived())));
            grid.addRow(9, new Label("Troco:"), new Label(String.format("%.2f", p.getChange())));
            grid.addRow(10, new Label("Método:"), new Label(p.getMethod()));
            grid.addRow(11, new Label("Data/Hora Pagamento:"), new Label(p.getDate().toString()));
        }

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 