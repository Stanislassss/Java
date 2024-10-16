package com.marketplace.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestionnaireDonnees {
    private static final String DB_URL = "jdbc:sqlite:donnees.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Créer la table Utilisateurs
            stmt.execute("CREATE TABLE IF NOT EXISTS utilisateurs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nom_utilisateur TEXT NOT NULL," +
                    "email TEXT NOT NULL," +
                    "mot_de_passe TEXT NOT NULL," +
                    "role TEXT NOT NULL)");

            // Créer la table Produits
            stmt.execute("CREATE TABLE IF NOT EXISTS produits (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nom TEXT NOT NULL," +
                    "categorie TEXT NOT NULL," +
                    "prix REAL NOT NULL," +
                    "vendeur_id INTEGER," +
                    "FOREIGN KEY (vendeur_id) REFERENCES utilisateurs(id))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}