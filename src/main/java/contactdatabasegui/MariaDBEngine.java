package contactdatabasegui;

import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MariaDBEngine implements DatabaseManager, Closeable {

    private Connection connection;
    private int nextId;
    private int size;

    public MariaDBEngine() {

        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost/contactdatabasegui",
                    "root", "Aa12345x");
            size = loadSize();
            nextId = getNextId();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("\n\nError in constructor of SQL Engine");
        }
    }

    public List<Contact> searchByNameLike(String name) throws SQLException {
        List<Contact> results = new ArrayList<>();

        String selectSql =
                "SELECT `person_id` as id, NAME, `age` " +
                        "FROM `person` " +
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

        String insertSql = "INSERT INTO `contactdatabasegui`.`contacts` (`contact_id`, `firstName`, `lastName`, `primaryEmail`, `secondaryEmail`, `primaryPhone`, `secondaryPhone`)" + "VALUES ('" + contact.getId() + "', '" + contact.getFirstName() + "', '" + contact.getLastName() + "', '" + contact.getPrimaryEmail() + "', '" + contact.getSecondaryEmail() + "', '" + contact.getPrimaryPhone() + "', '" + contact.getSecondaryPhone() + "')";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Creating does not work");
        }
    }


    @Override
    public void update(Contact contact, int id) throws SQLException {
        String updateSql =
                "UPDATE `contacts` " +
                        "SET `firstName` =  '" + contact.getFirstName() +
                        "', `lastName` = '" + contact.getLastName() +
                        "', `primaryEmail` = '" + contact.getPrimaryEmail() +
                        "', `secondaryEmail` = '" + contact.getSecondaryEmail() +
                        "', `primaryPhone` = '" + contact.getPrimaryPhone() +
                        "', `secondaryPhone` = '" + contact.getSecondaryPhone() +
                        "' WHERE `contact_id` = " + id + ";";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(updateSql);
        }
    }

    @Override
    public void delete(int id) {
        String deleteSql = "DELETE FROM `contacts` WHERE `contact_id` = " + id + ";";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteSql);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Deleting is not working");
        }
    }

    @Override
    public Contact read(int id) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM `contacts` WHERE `contact_id` = " + id + ";");

        Contact contact = null;
        if (resultSet != null) {
            while (resultSet.next()) {
                String name = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String primaryEmail = resultSet.getString("primaryEmail");
                String secondaryEmail = resultSet.getString("secondaryEmail");
                String primaryPhone = resultSet.getString("primaryPhone");
                String secondaryPhone = resultSet.getString("secondaryPhone");

                contact = new Contact(name, lastName, primaryEmail, secondaryEmail, primaryPhone, secondaryPhone);
            }
        }

        return contact;
    }

    @Override
    public int getNextId() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(`contact_id`) as `max_id` FROM `contacts`;");

        int result = 0;
        if (resultSet != null) {
            while (resultSet.next()) {
                result = resultSet.getInt("max_id");
            }
        }
        return result;
    }

    @Override
    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int loadSize() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) as `total_size` FROM `contacts`;");

        int result = 0;
        if (resultSet != null) {
            while (resultSet.next()) {
                result = resultSet.getInt("total_size");
            }
        }
        return result;
    }

    @Override
    public void saveId() {

    }
}
