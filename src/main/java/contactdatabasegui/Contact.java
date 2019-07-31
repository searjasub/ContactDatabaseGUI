package contactdatabasegui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class Contact {

    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty primaryEmail;
    private StringProperty secondaryEmail;
    private StringProperty primaryPhone;
    private StringProperty secondaryPhone;
    private int id;

    Contact() {
    }

    public Contact(String firstName, String lastName, String primaryEmail, String secondaryEmail, String primaryPhone, String secondaryPhone) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPrimaryEmail(primaryEmail);
        this.setSecondaryEmail(secondaryEmail);
        this.setPrimaryPhone(primaryPhone);
        this.setSecondaryPhone(secondaryPhone);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstNameProperty().get();
    }

    public void setFirstName(String firstName) {
        firstNameProperty().set(firstName);
    }

    private StringProperty firstNameProperty() {
        if (firstName == null) {
            firstName = new SimpleStringProperty(this, "firstName");
        }
        return firstName;
    }

    public String getLastName() {
        return lastNameProperty().get();
    }

    public void setLastName(String lastName) {
        lastNameProperty().set(lastName);
    }

    private StringProperty lastNameProperty() {
        if (lastName == null) {
            lastName = new SimpleStringProperty(this, "lastName");
        }
        return lastName;
    }

    public String getPrimaryEmail() {
        return primaryEmailProperty().get();
    }

    void setPrimaryEmail(String primaryEmail) {
        primaryEmailProperty().set(primaryEmail);
    }

    private StringProperty primaryEmailProperty() {
        if (primaryEmail == null) {
            primaryEmail = new SimpleStringProperty(this, "primaryEmail");
        }
        return primaryEmail;
    }

    public String getSecondaryEmail() {
        return secondaryEmailProperty().get();
    }

    public void setSecondaryEmail(String secondaryEmail) {
        secondaryEmailProperty().set(secondaryEmail);
    }

    public StringProperty secondaryEmailProperty() {
        if (secondaryEmail == null) {
            secondaryEmail = new SimpleStringProperty(this, "secondaryEmail");
        }
        return secondaryEmail;
    }

    public String getPrimaryPhone() {
        return primaryPhoneProperty().get();
    }

    public void setPrimaryPhone(String primaryPhone) {
        primaryPhoneProperty().set(primaryPhone);
    }

    public StringProperty primaryPhoneProperty() {
        if (primaryPhone == null) {
            primaryPhone = new SimpleStringProperty(this, "primaryPhone");
        }
        return primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhoneProperty().get();
    }

    public void setSecondaryPhone(String secondaryPhone) {
        secondaryPhoneProperty().set(secondaryPhone);
    }

    public StringProperty secondaryPhoneProperty() {
        if (secondaryPhone == null) {
            secondaryPhone = new SimpleStringProperty(this, "secondaryPhone");
        }
        return secondaryPhone;
    }


    public byte[] serialize() {
        String raw = String.format(
                "%" + FieldSizes.NAME_LENGTH.getSize() + "s" +
                        "%" + FieldSizes.LAST_NAME_LENGTH.getSize() + "s" +
                        "%" + FieldSizes.PRIMARY_EMAIL.getSize() + "s" +
                        "%" + FieldSizes.SECONDARY_EMAIL.getSize() + "s" +
                        "%" + FieldSizes.PRIMARY_PHONE.getSize() + "s" +
                        "%" + FieldSizes.SECONDARY_PHONE.getSize() + "s",
                this.getFirstName(),
                this.getLastName(),
                this.getPrimaryEmail(),
                this.getSecondaryEmail(),
                this.getPrimaryPhone(),
                this.getSecondaryPhone());
        return raw.getBytes();
    }

    public void deserialize(byte[] data) {
        String rawData = new String(data);
        String rawName = rawData.substring(
                0,
                FieldSizes.NAME_LENGTH.getSize()).trim();
        this.setFirstName(rawName);

        String rawLastName = rawData.substring(
                FieldSizes.NAME_LENGTH.getSize(),
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize()).trim();
        this.setLastName(rawLastName);

        String rawPrimaryEmail = rawData.substring(
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize(),
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize() + FieldSizes.PRIMARY_EMAIL.getSize()).trim();
        this.setPrimaryEmail(rawPrimaryEmail);

        String rawSecondaryEmail = rawData.substring(
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize() + FieldSizes.PRIMARY_EMAIL.getSize(),
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize() + FieldSizes.PRIMARY_EMAIL.getSize() + FieldSizes.SECONDARY_EMAIL.getSize()).trim();
        this.setSecondaryEmail(rawSecondaryEmail);

        String rawPrimaryPhone = rawData.substring(
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize() + FieldSizes.PRIMARY_EMAIL.getSize() + FieldSizes.SECONDARY_EMAIL.getSize(),
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize() + FieldSizes.PRIMARY_EMAIL.getSize() + FieldSizes.SECONDARY_EMAIL.getSize() + FieldSizes.PRIMARY_PHONE.getSize()).trim();
        this.setPrimaryPhone(rawPrimaryPhone);

        String rawSecondaryPhone = rawData.substring(
                FieldSizes.NAME_LENGTH.getSize() + FieldSizes.LAST_NAME_LENGTH.getSize() + FieldSizes.PRIMARY_EMAIL.getSize() + FieldSizes.SECONDARY_EMAIL.getSize() + FieldSizes.PRIMARY_PHONE.getSize(),
                FieldSizes.getTotalLength()).trim();
        this.setSecondaryPhone(rawSecondaryPhone);


    }

    @Override
    public String toString() {
        return
                "Name: " + getFirstName() +
                        " " + getLastName() +
                        ", primaryEmail:" + getPrimaryEmail() +
                        ", secondaryEmail: " + getSecondaryEmail() +
                        ", primaryPhone: " + getPrimaryPhone() +
                        ", secondaryPhone: " + getSecondaryPhone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(getFirstName(), contact.getFirstName()) &&
                Objects.equals(getLastName(), contact.getLastName()) &&
                Objects.equals(getPrimaryEmail(), contact.getPrimaryEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getPrimaryEmail());
    }
}
