package contactdatabasegui;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBEngine implements DatabaseManager, Closeable {

    private Connection connection;

    public MariaDBEngine() {
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost;databaseName=ContactDatabaseGUI",
                    "ContactDatabaseGUI", "ContactDatabaseGUI");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\n\nError in constructor of MariaDB Engine");
        }
    }

    @Override
    public void create(Contact contact) throws IOException {

    }

    @Override
    public Contact read(int id) throws IOException {
        return null;
    }

    @Override
    public void update(Contact contact, int id) throws IOException {

    }

    @Override
    public void delete(int id) throws IOException {

    }

    @Override
    public int getNextId() throws IOException {
        return 0;
    }

    @Override
    public void setNextId(int nextId) {

    }

    @Override
    public void saveId() throws IOException {

    }

    @Override
    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to close connection", ex);
            }
            connection = null;
        }
    }
}
