package heast.chatserver.network;

import java.sql.*;

public final class Database {

    private static Connection connection;

    public static void initialize() {
        System.out.println("Initializing database...");
        createTables();
    }


    /**
     * Creates a new database, if there is none
     */
    private static void createTables() {
        connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:chatserver/src/main/resources/chatServer.sqlite");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.execute("CREATE TABLE IF NOT EXISTS user_keys ("+
                    "id INT(16) NOT NULL PRIMARY KEY UNIQUE," +
                    "public_key MEDIUMTEXT NOT NULL," +
                    "modulus MEDIUMTEXT NOT NULL" +
                    ");"
            );
        }
        catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }

    /**
     * Adds a user to the database.
     * @param id The id of the user
     */
    public static boolean addKeys(int id, String publicKey, String modulus) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM user_keys");
            statement.execute();

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO user_keys (id, public_key, modulus) VALUES (?, ?, ?)");
            stmt.setInt(1,id);
            stmt.setString(2, publicKey);
            stmt.setString(3, modulus);
            stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to add entry to database for " + id);
            return false;
        }
    }

    public static boolean removeKeys(int id){
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM user_keys WHERE id = ?");
            stmt.setInt(1,id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to delete entry from database for " + id);
            return false;
        }
    }

    /**
     * Checks if a user exists in the database.
     * @param id The email to be checked.
     */
    public static boolean checkEntry(int id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user_keys WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            return result.next();
        } catch (SQLException e) {
            System.err.println("No entry found in database for " + id);
            return false;
        }
    }

}
