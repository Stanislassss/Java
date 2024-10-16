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

public class TableauDeBordVendeurController {

    @FXML
    private ListView<Produit> produitListView;

    @FXML
    private TextField nomProduitField;

    @FXML
    private TextField categorieProduitField;

    @FXML
    private TextField prixProduitField;

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
        chargerProduits();
    }

    private void chargerProduits() {
        try {
            List<Produit> produits = ProduitDAO.getProduitsParVendeurId(utilisateurActuel.getId());
            produitListView.setItems(FXCollections.observableArrayList(produits));
            produitListView.setCellFactory(lv -> new ListCell<Produit>() {
                @Override
                protected void updateItem(Produit produit, boolean empty) {
                    super.updateItem(produit, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(produit.getNom() + " - " + produit.getPrix() + "FCFA");
                    }
                }
            });
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Échec du chargement des produits.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterProduit() {
        String nom = nomProduitField.getText();
        String categorie = categorieProduitField.getText();
        String prixStr = prixProduitField.getText();

        if (nom.isEmpty() || categorie.isEmpty() || prixStr.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs du produit.");
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            Produit nouveauProduit = new Produit(0, nom, categorie, prix, utilisateurActuel.getId());
            ProduitDAO.creerProduit(nouveauProduit);
            chargerProduits();
            effacerChampsProduit();
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Format de prix invalide.");
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Échec de l'ajout du produit.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMettreAJourProduit() {
        Produit produitSelectionne = produitListView.getSelectionModel().getSelectedItem();
        if (produitSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un produit à mettre à jour.");
            return;
        }

        String nom = nomProduitField.getText();
        String categorie = categorieProduitField.getText();
        String prixStr = prixProduitField.getText();

        if (nom.isEmpty() || categorie.isEmpty() || prixStr.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs du produit.");
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            produitSelectionne.setNom(nom);
            produitSelectionne.setCategorie(categorie);
            produitSelectionne.setPrix(prix);
            ProduitDAO.mettreAJourProduit(produitSelectionne);
            chargerProduits();
            effacerChampsProduit();
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Format de prix invalide.");
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Échec de la mise à jour du produit.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSupprimerProduit() {
        Produit produitSelectionne = produitListView.getSelectionModel().getSelectedItem();
        if (produitSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        Alert alerteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        alerteConfirmation.setTitle("Confirmer la suppression");
        alerteConfirmation.setHeaderText(null);
        alerteConfirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        if (alerteConfirmation.showAndWait().get() == ButtonType.OK) {
            try {
                ProduitDAO.supprimerProduit(produitSelectionne.getId());
                chargerProduits();
                effacerChampsProduit();
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Échec de la suppression du produit.");
                e.printStackTrace();
            }
        }
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

    private void effacerChampsProduit() {
        nomProduitField.clear();
        categorieProduitField.clear();
        prixProduitField.clear();
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}