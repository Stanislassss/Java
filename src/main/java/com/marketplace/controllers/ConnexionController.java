package com.marketplace.controllers;

import com.marketplace.dao.UtilisateurDAO;
import com.marketplace.models.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ConnexionController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private void handleConnexion(ActionEvent event) {
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();

        try {
            Utilisateur utilisateur = UtilisateurDAO.getUtilisateurParEmail(email);
            if (utilisateur != null && utilisateur.getMotDePasse().equals(motDePasse)) {
                if (utilisateur.getRole().equals("acheteur")) {
                    chargerTableauDeBordAcheteur(utilisateur);
                } else if (utilisateur.getRole().equals("vendeur")) {
                    chargerTableauDeBordVendeur(utilisateur);
                }
            } else {
                afficherAlerte("Erreur", "Email ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Une erreur de base de données s'est produite.");
            e.printStackTrace();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Une erreur s'est produite lors du chargement de la page.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleInscription(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/marketplace/inscription.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Inscription");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Une erreur s'est produite lors du chargement de la page d'inscription.");
            e.printStackTrace();
        }
    }

    private void chargerTableauDeBordAcheteur(Utilisateur utilisateur) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marketplace/tableau_de_bord_acheteur.fxml"));
        Parent root = loader.load();
        TableauDeBordAcheteurController controller = loader.getController();
        controller.initData(utilisateur);
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
    }

    private void chargerTableauDeBordVendeur(Utilisateur utilisateur) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marketplace/tableau_de_bord_vendeur.fxml"));
        Parent root = loader.load();
        TableauDeBordVendeurController controller = loader.getController();
        controller.initData(utilisateur);
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}