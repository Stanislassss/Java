package com.marketplace.controllers;

import com.marketplace.models.Produit;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class DetailsProduitController {

    @FXML
    private Label nomVendeurLabel;

    @FXML
    private ListView<Produit> produitListView;

    public void initData(List<Produit> produits, String nomVendeur) {
        nomVendeurLabel.setText("Produits de " + nomVendeur);
        produitListView.setItems(FXCollections.observableArrayList(produits));
        produitListView.setCellFactory(lv -> new javafx.scene.control.ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(produit.getNom() + " - " + produit.getCategorie() + " - " + produit.getPrix() + "FCFA");
                }
            }
        });
    }
}