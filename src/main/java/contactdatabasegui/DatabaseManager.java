package contactdatabasegui;

import java.io.IOException;
import java.sql.SQLException;

public interface DatabaseManager {

    void create(Contact contact) throws IOException;

    Contact read(int id) throws IOException;

    void update(Contact contact, int id) throws IOException, SQLException;

    void delete(int id) throws IOException;

    int getNextId() throws IOException, SQLException;

    void setNextId(int nextId);

    void saveId() throws IOException;

}
