package com.marketplace.dao;

import com.marketplace.models.Utilisateur;
import com.marketplace.utils.GestionnaireDonnees;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public static Utilisateur getUtilisateurParEmail(String email) throws SQLException {
        String query = "SELECT * FROM utilisateurs WHERE email = ?";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom_utilisateur"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role")
                );
            }
        }
        return null;
    }

    public static void creerUtilisateur(Utilisateur utilisateur) throws SQLException {
        String query = "INSERT INTO utilisateurs (nom_utilisateur, email, mot_de_passe, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, utilisateur.getNomUtilisateur());
            pstmt.setString(2, utilisateur.getEmail());
            pstmt.setString(3, utilisateur.getMotDePasse());
            pstmt.setString(4, utilisateur.getRole());
            pstmt.executeUpdate();
        }
    }

    public static void mettreAJourUtilisateur(Utilisateur utilisateur) throws SQLException {
        String query = "UPDATE utilisateurs SET nom_utilisateur = ?, email = ?, mot_de_passe = ?, role = ? WHERE id = ?";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, utilisateur.getNomUtilisateur());
            pstmt.setString(2, utilisateur.getEmail());
            pstmt.setString(3, utilisateur.getMotDePasse());
            pstmt.setString(4, utilisateur.getRole());
            pstmt.setInt(5, utilisateur.getId());
            pstmt.executeUpdate();
        }
    }

    public static void supprimerUtilisateur(int utilisateurId) throws SQLException {
        String query = "DELETE FROM utilisateurs WHERE id = ?";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, utilisateurId);
            pstmt.executeUpdate();
        }
    }

    public static List<Utilisateur> getTousLesVendeurs() throws SQLException {
        List<Utilisateur> vendeurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateurs WHERE role = 'vendeur'";
        try (Connection conn = GestionnaireDonnees.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                vendeurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom_utilisateur"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role")
                ));
            }
        }
        return vendeurs;
    }

    public static List<Utilisateur> rechercherVendeurs(String termeRecherche) throws SQLException {
        List<Utilisateur> vendeurs = new ArrayList<>();
        String query = "SELECT * FROM utilisateurs WHERE role = 'vendeur' AND (nom_utilisateur LIKE ? OR email LIKE ?)";
        try (Connection conn = GestionnaireDonnees.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + termeRecherche + "%");
            pstmt.setString(2, "%" + termeRecherche + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vendeurs.add(new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom_utilisateur"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("role")
                ));
            }
        }
        return vendeurs;
    }
}