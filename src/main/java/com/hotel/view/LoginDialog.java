package com.hotel.view;

import com.hotel.dao.WorkerDAO;
import com.hotel.model.Worker;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

// Priscila: Diálogo de login para autenticação de usuários
public class LoginDialog extends Dialog<Worker> {
    private final WorkerDAO workerDAO;

    public LoginDialog(WorkerDAO workerDAO) {
        this.workerDAO = workerDAO;
        setTitle("Login");
        setHeaderText("Por favor, insira suas credenciais");

        ButtonType loginButtonType = new ButtonType("Entrar", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        PasswordField password = new PasswordField();

        grid.add(new Label("Nome de Usuário:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(password, 1, 1);

        getDialogPane().setContent(grid);

        // Converte o resultado do diálogo para um objeto Worker após autenticação
        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return workerDAO.authenticate(username.getText(), password.getText()).orElse(null);
            }
            return null;
        });

        // Priscila: Habilita/desabilita o botão de login baseado no nome de usuário
        Node loginButton = getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Priscila: Valida o campo de usuário quando o texto muda
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
    }
} 