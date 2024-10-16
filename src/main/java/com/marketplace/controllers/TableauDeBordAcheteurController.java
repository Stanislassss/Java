package com.marketplace.controllers;

import com.marketplace.dao.ProduitDAO;
import com.marketplace.dao.UtilisateurDAO;
import com.marketplace.models.Produit;
import com.marketplace.models.Utilisateur;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TableauDeBordAcheteurController {

    @FXML
    private ListView<Utilisateur> vendeurListView;

    @FXML
    private TextField champRecherche;

    @FXML
    private TextField nomUtilisateurField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private ComboBox<String> roleComboBox;

    private Utilisateur utilisateurActuel;

    public void initData(Utilisateur utilisateur) {
        this.utilisateurActuel = utilisateur;
        nomUtilisateurField.setText(utilisateur.getNomUtilisateur());
        emailField.setText(utilisateur.getEmail());
        roleComboBox.setItems(FXCollections.observableArrayList("acheteur", "vendeur"));
        roleComboBox.setValue(utilisateur.getRole());
        chargerVendeurs();
    }

    private void chargerVendeurs() {
        try {
            List<Utilisateur> vendeurs = UtilisateurDAO.getTousLesVendeurs();
            vendeurListView.setItems(FXCollections.observableArrayList(vendeurs));
            vendeurListView.setCellFactory(lv -> new ListCell<Utilisateur>() {
                @Override
                protected void updateItem(Utilisateur utilisateur, boolean empty) {
                    super.updateItem(utilisateur, empty);
                    setText(empty ? null : utilisateur.getNomUtilisateur());
                }
            });
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Échec du chargement des vendeurs.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRecherche() {
        String termeRecherche = champRecherche.getText();
        try {
            List<Utilisateur> resultatsRecherche = UtilisateurDAO.rechercherVendeurs(termeRecherche);
            vendeurListView.setItems(FXCollections.observableArrayList(resultatsRecherche));
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Échec de la recherche de vendeurs.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVoirProduits() {
        Utilisateur vendeurSelectionne = vendeurListView.getSelectionModel().getSelectedItem();
        if (vendeurSelectionne != null) {
            try {
                List<Produit> produits = ProduitDAO.getProduitsParVendeurId(vendeurSelectionne.getId());
                afficherDetailsProduits(produits, vendeurSelectionne.getNomUtilisateur());
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Échec du chargement des produits.");
                e.printStackTrace();
            } catch (IOException e) {
                afficherAlerte("Erreur", "Échec de l'affichage des détails des produits.");
                e.printStackTrace();
            }
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un vendeur.");
        }
    }

    private void afficherDetailsProduits(List<Produit> produits, String nomVendeur) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/marketplace/details_produit.fxml"));
        Parent root = loader.load();
        DetailsProduitController controller = loader.getController();
        controller.initData(produits, nomVendeur);
        Stage stage = new Stage();
        stage.setTitle("Produits de " + nomVendeur);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    @FXML
    private void handleMettreAJourProfil() {
        String nouveauNomUtilisateur = nomUtilisateurField.getText();
        String nouvelEmail = emailField.getText();
        String nouveauMotDePasse = motDePasseField.getText();
        String nouveauRole = roleComboBox.getValue();

        if (nouveauNomUtilisateur.isEmpty() || nouvelEmail.isEmpty() || nouveauMotDePasse.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            utilisateurActuel.setNomUtilisateur(nouveauNomUtilisateur);
            utilisateurActuel.setEmail(nouvelEmail);
            utilisateurActuel.setMotDePasse(nouveauMotDePasse);
            utilisateurActuel.setRole(nouveauRole);
            UtilisateurDAO.mettreAJourUtilisateur(utilisateurActuel);
            afficherAlerte("Succès", "Profil mis à jour avec succès. Veuillez vous reconnecter.");
            deconnexion();
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Échec de la mise à jour du profil.");
            e.printStackTrace();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Échec de la déconnexion après la mise à jour du profil.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimerCompte() {
        Alert alerteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alerteConfirmation.setTitle("Confirmer la suppression");
        alerteConfirmation.setHeaderText(null);
        alerteConfirmation.setContentText("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.");

        if (alerteConfirmation.showAndWait().get() == ButtonType.OK) {
            try {
                UtilisateurDAO.supprimerUtilisateur(utilisateurActuel.getId());
                afficherAlerte("Succès", "Votre compte a été supprimé.");
                deconnexion();
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Échec de la suppression du compte.");
                e.printStackTrace();
            } catch (IOException e) {
                afficherAlerte("Erreur", "Échec de la déconnexion après la suppression du compte.");
                e.printStackTrace();
            }
        }
    }

    private void deconnexion() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/marketplace/connexion.fxml"));
        Stage stage = (Stage) nomUtilisateurField.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}