package contactdatabasegui;

import java.io.*;

public class RandomAccessEngine implements DatabaseManager {

    private RandomAccessFile file;
    private RandomAccessFile database;
    private int nextId;
    private int size;

    public RandomAccessEngine(String filePath) {
        try {
            file = new RandomAccessFile(filePath, "rw");
            database = new RandomAccessFile("serialized/database.db", "rw");
            nextId = getNextId();
            size = loadSize();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in constructor of random access engine");
        }
    }

    @Override
    public void saveId() throws IOException {
        File file = new File("nextid.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("" + (nextId + 1));
        writer.close();
    }

    public int loadSize() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("size.txt")));
        String text;
        int size = 0;
        while ((text = reader.readLine()) != null) {
            size = Integer.parseInt(text);
        }
        reader.close();
        return size;
    }

    public void saveSize() throws IOException {
        File file = new File("size.txt");
        FileWriter writer = new FileWriter(file);
        long totalSize = database.length();
        long newSize = totalSize / FieldSizes.getTotalLength();
        writer.write("" + (newSize));
        writer.close();
    }

    @Override
    public int getNextId() throws IOException {
        File file = new File("nextid.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String text;
        int nextId = 0;
        while ((text = reader.readLine()) != null) {
            nextId = Integer.parseInt(text);
        }
        reader.close();
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    private long calculateOffset(int id, int length) {
        return (id * length);
    }


    @Override
    public void create(Contact entity) throws IOException {
        byte[] serializedEntity = entity.serialize();
        int newId = nextId;
        long offset = calculateOffset(newId, serializedEntity.length);
        file.seek((offset));
        file.write(serializedEntity);
        nextId++;
        size++;
        file.seek(serializedEntity.length);

        saveId();
        saveSize();
    }

    @Override
    public Contact read(int id) throws IOException {
        long offset = calculateOffset(id, FieldSizes.getTotalLength());
        database.seek(offset);
        byte[] buffer = new byte[FieldSizes.getTotalLength()];
        database.read(buffer);
        Contact entity = new Contact();
        entity.deserialize(buffer);
        database.read();
        return entity;
    }

    @Override
    public void update(Contact entity, int id) throws IOException {
        byte[] serializedPerson = entity.serialize();
        long offset = calculateOffset(id, serializedPerson.length);
        file.seek((offset));
        file.write(serializedPerson);
        file.seek(serializedPerson.length);
    }

    @Override
    public void delete(int id) throws IOException {
        long offset = calculateOffset(id, FieldSizes.getTotalLength());
        database.seek(offset);
        byte[] buffer = new byte[FieldSizes.getTotalLength()];
        database.write(buffer);
        size--;
        File file = new File("size.txt");
        FileWriter writer = new FileWriter(file);
        writer.write("" + (size));
        writer.close();
    }
}
