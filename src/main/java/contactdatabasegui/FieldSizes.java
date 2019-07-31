package contactdatabasegui;

public enum FieldSizes {

    NAME_LENGTH(255),
    LAST_NAME_LENGTH(255),
    PRIMARY_EMAIL(255),
    SECONDARY_EMAIL(255),
    PRIMARY_PHONE(12),
    SECONDARY_PHONE(12);

    private int size;

    FieldSizes(int size) {
        this.setSize(size);
    }

    public static int getTotalLength() {
        return NAME_LENGTH.getSize() + LAST_NAME_LENGTH.getSize() + PRIMARY_EMAIL.getSize() + SECONDARY_EMAIL.getSize() + PRIMARY_PHONE.getSize() + SECONDARY_PHONE.getSize();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
