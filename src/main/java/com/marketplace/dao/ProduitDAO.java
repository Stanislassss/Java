package com.marketplace.dao;

import com.marketplace.models.Produit;
import com.marketplace.utils.GestionnaireDonnees;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    public static void creerProduit(Produit produit) throws SQLException {
        String query = "INSERT INTO produits (nom, categorie, prix, vendeur_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getCategorie());
            pstmt.setDouble(3, produit.getPrix());
            pstmt.setInt(4, produit.getVendeurId());
            pstmt.executeUpdate();
        }
    }

    public static void mettreAJourProduit(Produit produit) throws SQLException {
        String query = "UPDATE produits SET nom = ?, categorie = ?, prix = ? WHERE id = ?";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getCategorie());
            pstmt.setDouble(3, produit.getPrix());
            pstmt.setInt(4, produit.getId());
            pstmt.executeUpdate();
        }
    }

    public static void supprimerProduit(int produitId) throws SQLException {
        String query = "DELETE FROM produits WHERE id = ?";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, produitId);
            pstmt.executeUpdate();
        }
    }

    public static List<Produit> getProduitsParVendeurId(int vendeurId) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produits WHERE vendeur_id = ?";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, vendeurId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("categorie"),
                        rs.getDouble("prix"),
                        rs.getInt("vendeur_id")
                ));
            }
        }
        return produits;
    }

    public static List<Produit> rechercherProduits(String termeRecherche, String categorie, Double prixMin, Double prixMax) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM produits WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (termeRecherche != null && !termeRecherche.isEmpty()) {
            queryBuilder.append(" AND nom LIKE ?");
            params.add("%" + termeRecherche + "%");
        }

        if (categorie != null && !categorie.isEmpty()) {
            queryBuilder.append(" AND categorie = ?");
            
            params.add(categorie);
        }

        if (prixMin != null) {
            queryBuilder.append(" AND prix >= ?");
            params.add(prixMin);
        }

        if (prixMax != null) {
            queryBuilder.append(" AND prix <= ?");
            params.add(prixMax);
        }

        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("categorie"),
                        rs.getDouble("prix"),
                        rs.getInt("vendeur_id")
                ));
            }
        }
        return produits;
    }
}