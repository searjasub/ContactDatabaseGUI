package contactdatabasegui;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AddStageController {

    public TextField firstNameField;
    public TextField lastNameField;
    public TextField primaryEmailField;
    public TextField secondaryEmailField;
    public TextField primaryPhoneField;
    public TextField secondaryPhoneField;
    private Stage primaryStage;
    private Scene tableScene;
    private ObservableList<Contact> data;
    private DatabaseManager dm;
    private MainStageController controller;


    void setPrimaryScene(Stage primaryStage, Scene tmp, ObservableList<Contact> data, DatabaseManager dm, MainStageController mainStageController) {
        this.primaryStage = primaryStage;
        tableScene = tmp;
        this.data = data;
        this.dm = dm;
        controller = mainStageController;
    }

    public void onActionAdd() throws IOException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String primaryEmail = primaryEmailField.getText();
        String secondaryEmail = secondaryEmailField.getText();
        String primaryPhone = primaryPhoneField.getText();
        String secondaryPhone = secondaryPhoneField.getText();

        Contact contact = new Contact(firstName, lastName, primaryEmail, secondaryEmail, primaryPhone, secondaryPhone);
        contact.setId(dm.getNextId());
        dm.setNextId(dm.getNextId());
        dm.saveId();

        data.add(contact);
        controller.setPrimaryStage(primaryStage, tableScene);
        dm.update(contact, contact.getId());
        primaryStage.setScene(tableScene);
    }

    public void onActionCancel() {
        primaryStage.setScene(tableScene);
    }
}
