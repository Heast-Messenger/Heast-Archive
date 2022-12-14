package heast.authserver.network;

import heast.core.network.UserAccount;
import heast.core.security.Keychain;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public final class Database {

    private static Connection connection;

    /**
     * Initializes the database.
     */
    public static void initialize() {
        System.out.println("Initializing database...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found");
        }

        try {
            String[] parts = Files.readString(
                Path.of("database-connection.txt")
            ).split(",");

            try {
                connection = DriverManager.getConnection(
                    "jdbc:mysql://" + parts[0] + ":" + parts[1] + "/" + "?user=" + parts[3] + "&password=" + parts[4]
                );
            } catch (SQLException e) {
                System.err.println("Failed to connect to database");
            }
            createDatabase();
        } catch (IOException e) {
            System.err.println("Could not read database-connection.txt");
        }
    }

    /**
     * Creates a new database, if there is none
     */
    private static void createDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE DATABASE IF NOT EXISTS messenger");
            statement.execute("USE messenger");
            statement.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INT(16) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY UNIQUE,"
                    + "name VARCHAR(32) NOT NULL,"
                    + "since DATETIME NOT NULL,"
                    + "email VARCHAR(255) NOT NULL,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "private_key BLOB NOT NULL,"
                    + "public_key MEDIUMTEXT NOT NULL,"
                    + "modulus MEDIUMTEXT NOT NULL"
                    + ");"
            );

        } catch (SQLException e) {
            System.err.println("Error generating database");
        }
    }

    /**
     * Adds a user to the database.
     * @param name The username to be registered.
     * @param email The email address of the user.
     * @param password The hashed password of the account.
     */
    public static boolean addEntry(String name, String email, String password, byte[] privateKey, String publicKey, String modulus) {
        try {
            BufferedInputStream byteStream= new BufferedInputStream(new ByteArrayInputStream(privateKey));

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO accounts (name, since, email, password, private_key, public_key, modulus) VALUES (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, name);
            stmt.setString(2, LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ));
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setBinaryStream(5, byteStream, byteStream.available());
            stmt.setString(6, publicKey);
            stmt.setString(7, modulus);
            stmt.execute();
            return true;
        } catch (SQLException | IOException e) {
            System.err.println("Failed to add entry to database for " + email);
            return false;
        }
    }

    public static boolean removeEntry(String email){
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM accounts WHERE email = ?");
            stmt.setString(1,email);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to delete entry from database for " + email);
            return false;
        }
    }

    /**
     * Checks if a user exists in the database.
     * @param email The email to be checked.
     * @param password The password to be checked.
     */
    public static boolean checkEntry(String email, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM accounts WHERE email = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet result = stmt.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.err.println("No entry found in database for " + email);
            return false;
        }
    }

    /**
     * Checks if a user exists in the database.
     * @param email The email to be checked.
     */
    public static boolean checkEntry(String email) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM accounts WHERE email = ?");
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.err.println("No entry found in database for " + email);
            return false;
        }
    }

    public static UserAccount getUser(String email) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM accounts WHERE email = ?");
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                final HashMap<String, BigInteger> keys = new HashMap<>();
                keys.put("private", null);
                keys.put("public", new BigInteger(result.getString("public_key")));
                keys.put("modulus", new BigInteger(result.getString("modulus")));
                Keychain keychain= new Keychain(keys);

                BufferedInputStream bs= new BufferedInputStream(result.getBinaryStream("private_key"));

                keychain.setSecret(bs.readAllBytes());

                return new UserAccount(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("email"),
                    result.getString("password"),
                    LocalDateTime.parse(result.getString("since"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    keychain
                );
            } else {
                return null;
            }
        } catch (SQLException | IOException e) {
            System.err.println("No entry found in database for " + email);
            return null;
        }
    }

    /**
     * Updates the password for a user.
     * @param email The email address of the user.
     * @param password The new password.
     */
    public static boolean updatePassword(String email, String password) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE accounts SET password = ? WHERE email = ?");
            stmt.setString(1, password);
            stmt.setString(2, email);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to update password for " + email);
            return false;
        }
    }
}
