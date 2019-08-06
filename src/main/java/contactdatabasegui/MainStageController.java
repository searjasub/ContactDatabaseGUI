package contactdatabasegui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainStageController {

    public Pagination pagination;
    public Button addButton;
    public Button deleteButton;
    public HBox aboveTableView;
    public RadioButton exactMatch = new RadioButton();
    public RadioButton byCharacter = new RadioButton();
    public MenuBar menu;
    private DatabaseManager dm = new RandomAccessEngine("serialized/database.db");
    private TextField searchField;
    private RadioMenuItem sqlMenuOption;
    private RadioMenuItem mariaMenuOption;
    private RadioMenuItem localMenuOption;
    private ObservableList<Contact> data = createData();
    private FilteredList<Contact> filteredData;
    private Stage primaryStage;
    private Scene primaryScene;
    private TableView<Contact> tableView = createTable();

    public MainStageController() throws IOException, SQLException {
    }


    public void setPrimaryStage(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.primaryScene = scene;

        byCharacter.setSelected(true);

        searchField = new TextField();
        searchField.setFont(Font.font("SanSerif"));
        searchField.setPadding(new Insets(5, 10, 5, 10));
        searchField.setPrefSize(800, 35);
        searchField.setPromptText("Search");

        aboveTableView.setAlignment(Pos.BOTTOM_CENTER);
        aboveTableView.getChildren().add(2, searchField);

        filteredData = new FilteredList<>(data, c -> true);

        ToggleGroup toggleRadio = new ToggleGroup();
        toggleRadio.getToggles().add(byCharacter);
        toggleRadio.getToggles().add(exactMatch);

        searchField.setOnKeyReleased(e -> {
            searchField.textProperty().addListener((observableValue, oldValue, newValue) -> filteredData.setPredicate(contact -> {

                if (byCharacter.isSelected()) {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (contact.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (contact.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (contact.getPrimaryEmail().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else return contact.getPrimaryPhone().toLowerCase().contains(lowerCaseFilter);
                } else if (exactMatch.isSelected()) {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (contact.getFirstName().toLowerCase().equals(lowerCaseFilter)) {
                        return true;
                    } else if (contact.getLastName().toLowerCase().equals(lowerCaseFilter)) {
                        return true;
                    } else if (contact.getPrimaryEmail().toLowerCase().equals(lowerCaseFilter)) {
                        return true;
                    } else return contact.getPrimaryPhone().toLowerCase().equals(lowerCaseFilter);
                }
                return false;
            }));

            SortedList<Contact> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
            if (sortedData.size() > 100) {

                pagination.setPageCount((sortedData.size() / 100) + 1);
            } else {
                pagination.setPageCount(1);
            }
        });

        if (data.size() > 100) {
            pagination.setPageCount((data.size() / 100) + 1);
        } else {
            pagination.setPageCount(1);
        }

        pagination.setPageFactory(this::createPage);

        Menu menuOptions = new Menu("Storage");

        sqlMenuOption = new RadioMenuItem("SQL");
        mariaMenuOption = new RadioMenuItem("Maria DB");
        localMenuOption = new RadioMenuItem("Local Storage");

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(sqlMenuOption);
        toggleGroup.getToggles().add(mariaMenuOption);
        toggleGroup.getToggles().add(localMenuOption);

        menuOptions.getItems().add(sqlMenuOption);
        menuOptions.getItems().add(mariaMenuOption);
        menuOptions.getItems().add(localMenuOption);

        localMenuOption.setSelected(true);

        Menu about = new Menu("About");
        MenuItem info = new MenuItem("Info");
        info.setOnAction(event -> {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Made by Searjasub Lopez for CSC180",
                    ButtonType.OK);
            alert.setTitle("About");
            alert.show();
        });
        about.getItems().add(info);

        menu.getMenus().add(menuOptions);
        menu.getMenus().add(about);

        sqlMenuOption.setOnAction(event -> {
            sqlMenuOption.setSelected(true);
            dm = new SQLEngine();
            tableView.getItems().removeAll(filteredData);
            tableView.refresh();
            try {
                data = null;
                data = createSqlData();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOexception");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("SQL not working");
            }

            tableView.getItems().addAll(filteredData);
            primaryStage.setTitle("Contact Database - SQL Engine");

            refreshPaginationAndFilteredData();
        });

        mariaMenuOption.setOnAction(event -> {
            mariaMenuOption.setSelected(true);
            dm = new MariaDBEngine();
            tableView.getItems().removeAll(filteredData);
            tableView.refresh();
            data = null;
            try {
                data = createSqlData();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }

            tableView.getItems().addAll(filteredData);
            primaryStage.setTitle("Contact Database - MariaDB Engine");

            refreshPaginationAndFilteredData();
        });

        localMenuOption.setOnAction(event -> {
            localMenuOption.setSelected(true);
            dm = new RandomAccessEngine("serialized/database.db");
            tableView.getItems().removeAll(filteredData);
            tableView.refresh();
            data = null;
            try {
                data = createData();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }

            tableView.getItems().addAll(filteredData);
            primaryStage.setTitle("Contact Database - Random Access File Engine");

            refreshPaginationAndFilteredData();
        });
    }

    private void refreshPaginationAndFilteredData() {
        filteredData = new FilteredList<>(data, c -> true);

        if (data.size() > 100) {
            pagination.setPageCount((data.size() / 100) + 1);
        } else {
            pagination.setPageCount(1);
        }
        pagination.setPageFactory(this::createPage);
    }

    private ObservableList<Contact> createSqlData() throws IOException, SQLException {
        ObservableList<Contact> data = FXCollections.observableArrayList();

        for (int i = 1; i <= dm.loadSize() + 1; i++) {
            Contact contact = null;
            try {
                contact = dm.read(i);
                if (contact == null) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Couldn't lookup a Contact");
            }
            assert contact != null;
            if (!contact.getSecondaryEmail().equals("")) {
                contact.setId(i);
                data.add(contact);
            }
        }
        return data;
    }

    private ObservableList<Contact> createData() throws IOException, SQLException {
        ObservableList<Contact> data = FXCollections.observableArrayList();

        for (int i = 0; i < dm.getNextId(); i++) {
            Contact contact = null;
            try {
                contact = dm.read(i);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Couldn't lookup a Contact");
            }
            assert contact != null;
            if (!contact.getSecondaryEmail().equals("")) {
                contact.setId(i);
                data.add(contact);
            }
        }
        return data;
    }

    private TableView<Contact> createTable() {
        tableView = new TableView<>();
        tableView.setEditable(true);

        TableColumn<Contact, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setOnEditCommit(
                event -> {
                    event
                            .getTableView()
                            .getItems()
                            .get(event.getTablePosition().getRow())
                            .setFirstName(event.getNewValue());
                    try {
                        Contact contact = dm.read(tableView.getSelectionModel().getSelectedItem().getId());
                        contact.setFirstName(event.getNewValue());
                        dm.update(contact, tableView.getSelectionModel().getSelectedItem().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error on SQL Engine");
                    }
                }
        );

        TableColumn<Contact, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(
                event -> {
                    event
                            .getTableView()
                            .getItems()
                            .get(event.getTablePosition().getRow())
                            .setLastName(event.getNewValue());
                    try {
                        Contact contact = dm.read(tableView.getSelectionModel().getSelectedItem().getId());
                        contact.setLastName(event.getNewValue());
                        dm.update(contact, tableView.getSelectionModel().getSelectedItem().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error on SQL Engine");
                    }
                }
        );

        TableColumn<Contact, String> primaryEmailCol = new TableColumn<>("Primary Email");
        primaryEmailCol.setCellValueFactory(new PropertyValueFactory<>("primaryEmail"));
        primaryEmailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        primaryEmailCol.setOnEditCommit(
                event -> {
                    event
                            .getTableView()
                            .getItems()
                            .get(event.getTablePosition().getRow())
                            .setPrimaryEmail(event.getNewValue());
                    try {
                        Contact contact = dm.read(tableView.getSelectionModel().getSelectedItem().getId());
                        contact.setPrimaryEmail(event.getNewValue());
                        dm.update(contact, tableView.getSelectionModel().getSelectedItem().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error on SQL Engine");
                    }
                }
        );

        TableColumn<Contact, String> secondaryEmailCol = new TableColumn<>("Secondary Email");
        secondaryEmailCol.setCellValueFactory(new PropertyValueFactory<>("secondaryEmail"));
        secondaryEmailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        secondaryEmailCol.setOnEditCommit(
                event -> {
                    event
                            .getTableView()
                            .getItems()
                            .get(event.getTablePosition().getRow())
                            .setSecondaryEmail(event.getNewValue());
                    try {
                        Contact contact = dm.read(tableView.getSelectionModel().getSelectedItem().getId());
                        contact.setSecondaryEmail(event.getNewValue());
                        dm.update(contact, tableView.getSelectionModel().getSelectedItem().getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error on SQL Engine");
                    }
                }
        );

        TableColumn<Contact, String> primaryPhoneCol = new TableColumn<>("Primary Phone");
        primaryPhoneCol.setCellValueFactory(new PropertyValueFactory<>("primaryPhone"));
        primaryPhoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        primaryPhoneCol.setOnEditCommit(
                event -> {

                    String lowerCase = event.getNewValue().toLowerCase();
                    if (event.getNewValue().length() != 10 || lowerCase.contains("[^0-9]")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Please enter a valid 10 digit phone number", ButtonType.OK);
                        alert.setTitle("Wrong Input");
                        alert.setHeaderText("test");
                        alert.setResizable(false);
                        alert.show();
                    } else {
                        event
                                .getTableView()
                                .getItems()
                                .get(event.getTablePosition().getRow())
                                .setPrimaryPhone(event.getNewValue());
                        try {
                            Contact contact = dm.read(tableView.getSelectionModel().getSelectedItem().getId());
                            contact.setPrimaryPhone(event.getNewValue());
                            dm.update(contact, tableView.getSelectionModel().getSelectedItem().getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error on SQL Engine");
                        }
                    }
                }
        );

        TableColumn<Contact, String> secondaryPhoneCol = new TableColumn<>("Secondary Phone");
        secondaryPhoneCol.setCellValueFactory(new PropertyValueFactory<>("secondaryPhone"));
        secondaryPhoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        secondaryPhoneCol.setOnEditCommit(
                (TableColumn.CellEditEvent<Contact, String> event) -> {

                    String lowerCase = event.getNewValue().toLowerCase();
                    if (event.getNewValue().length() != 10 || lowerCase.contains("[^0-9]")) {
                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Please enter a valid 10 digit phone number", ButtonType.OK);
                        alert.setTitle("Wrong Input");
                        alert.setHeaderText("test");
                        alert.setResizable(false);
                        alert.show();
                    } else {
                        event
                                .getTableView()
                                .getItems()
                                .get(event.getTablePosition().getRow())
                                .setSecondaryPhone(event.getNewValue());
                        try {
                            Contact contact = dm.read(tableView.getSelectionModel().getSelectedItem().getId());
                            contact.setSecondaryPhone(event.getNewValue());
                            dm.update(contact, tableView.getSelectionModel().getSelectedItem().getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Error on SQL Engine");
                        }
                    }
                }
        );

        tableView.getColumns().setAll(firstNameCol, lastNameCol, primaryEmailCol, secondaryEmailCol, primaryPhoneCol, secondaryPhoneCol);

        return tableView;
    }

    public void onMenuItemExit() {
        primaryStage.close();
    }

    private Node createPage(Integer pageIndex) {
        int rowsPerPage = 100;
        int fromIndex = pageIndex * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, data.size());
        tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return tableView;
    }

    public void onActionDelete() throws IOException, SQLException {
        Contact selectedContact = tableView.getSelectionModel().getSelectedItem();
        System.out.println(selectedContact.getId());
        tableView.getItems().remove(selectedContact);
        dm.delete(selectedContact.getId());
        data = null;
        data = createSqlData();
        tableView.refresh();
    }

    public void onActionAdd() throws IOException {
        Scene tmp = this.primaryScene;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("../AddStage.fxml"));
        BorderPane root = loader.load();
        AddStageController a = loader.getController();
        Scene addScene = new Scene(root, 300, 250);

        a.setPrimaryScene(primaryStage, tmp, data, dm, this);
        primaryStage.setMaxHeight(200);
        primaryStage.setMaxWidth(300);
        primaryStage.setScene(addScene);

    }
}
