package com.marketplace.controllers;

import com.marketplace.dao.UtilisateurDAO;
import com.marketplace.models.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class InscriptionController {

    @FXML
    private TextField nomUtilisateurField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private RadioButton acheteurRadio;

    @FXML
    private RadioButton vendeurRadio;

    @FXML
    private void handleInscription(ActionEvent event) {
        String nomUtilisateur = nomUtilisateurField.getText();
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();
        String role = acheteurRadio.isSelected() ? "acheteur" : "vendeur";

        if (nomUtilisateur.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            Utilisateur nouvelUtilisateur = new Utilisateur(0, nomUtilisateur, email, motDePasse, role);
            UtilisateurDAO.creerUtilisateur(nouvelUtilisateur);
            afficherAlerte("Succès", "Inscription réussie. Vous pouvez maintenant vous connecter.");
            fermerFenetre();
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Une erreur s'est produite lors de l'inscription.");
            e.printStackTrace();
        }
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) nomUtilisateurField.getScene().getWindow();
        stage.close();
    }
}