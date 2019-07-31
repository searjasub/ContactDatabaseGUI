package contactdatabasegui;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLEngine implements Closeable, DatabaseManager {

    private Connection connection;

    public SQLEngine() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=ContactDatabaseGUI",
                    "ContactDatabaseGUI", "ContactDatabaseGUI");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\n\nError in constructor of SQL Engine");
        }
    }

    public List<Contact> searchByNameLike(String name) throws SQLException {
        List<Contact> results = new ArrayList<>();

        String selectSql =
                "SELECT person_id as id, NAME, \"age\" " +
                        "FROM \"person\" " +
                        "WHERE NAME like '%" + name + "%'";

        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(selectSql)) {
                while (resultSet.next()) {
                    Contact contact = new Contact();
                    contact.setFirstName(resultSet.getString("NAME"));
                    contact.setId(resultSet.getInt("id"));
                    results.add(contact);
                }
            }
        }
        return results;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to close connection", ex);
            }
            connection = null;
        }
    }

    @Override
    public void create(Contact contact) {
        String insertSql = "INSERT INTO \"Contacts\" (\"contact_id\", \"firstName\", \"lastName\", \"primaryEmail\", \"secondaryEmail\", \"primaryPhone\", \"secondaryPhone\") " +
                "VALUES (" + contact.getId() + ",\" " + contact.getFirstName() + "\", \"" + contact.getLastName() + "\", \"" + contact.getPrimaryEmail() + "\", \"" + contact.getSecondaryEmail() + "\", \"" + contact.getPrimaryPhone() + "\", \"" + contact.getSecondaryPhone() + "\");";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Creating does not work");
        }
    }

    @Override
    public Contact read(int id) throws IOException {
        return null;
    }

    @Override
    public void update(Contact contact, int id) throws SQLException {
        String updateSql =
                "UPDATE \"Contacts\" " +
                        "SET \"firstName\" =  ?" +
                        "SET \"lastName\" = ?" +
                        "SET \"primaryEmail\" = ?" +
                        "SET \"secondaryEmail\" = ?" +
                        "SET \"primaryPhone\" = ?" +
                        "SET \"secondaryPhone\" = ?" +
                        "WHERE contact_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getPrimaryEmail());
            statement.setString(4, contact.getSecondaryEmail());
            statement.setString(5, contact.getPrimaryPhone());
            statement.setString(6, contact.getSecondaryPhone());
            statement.setInt(7, id);
            statement.execute();
        }
    }

    @Override
    public void delete(int id)  {
        String deleteSql = "DELETE FROM \"Contacts\" WHERE \"contact_id\" = " + id + ";";
        try (Statement statement = connection.createStatement()) {
            statement.executeQuery(deleteSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Deleting is not working");
        }
    }

    @Override
    public int getNextId() throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(contact_id) as max_id FROM \"Contacts\";");

        if (resultSet != null){
            while (resultSet.next()){
                return resultSet.getInt("max_id");
            }
        }
        return -1;
    }

    @Override
    public void setNextId(int nextId) {

    }

    @Override
    public void saveId() {

    }
}
