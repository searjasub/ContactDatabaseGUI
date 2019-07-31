package lopez.sear.contactdatabasegui;

import contactdatabasegui.Contact;
import contactdatabasegui.RandomAccessEngine;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAccessEngineTest {

    private static final int TOTAL_CONTACTS = 410;
    private static RandomAccessEngine cm;

    @BeforeClass
    public static void init() throws IOException {
        cm = new RandomAccessEngine("serialized/database.db");
    }

    @Test
    public void testInsertContacts() throws IOException {
        long start = System.currentTimeMillis();
        File fileNames = new File("people/firstnames.txt");
        File fileLastNames = new File("people/lastnames.txt");

        List<String> nameList = new ArrayList<>();
        List<String> lastNameList = new ArrayList<>();

        BufferedReader nameReader = new BufferedReader(new FileReader(fileNames));
        BufferedReader lastNameReader = new BufferedReader(new FileReader(fileLastNames));

        String line;
        while ((line = nameReader.readLine()) != null) {
            nameList.add(line);
        }
        nameReader.close();
        while ((line = lastNameReader.readLine()) != null) {
            lastNameList.add(line);
        }
        lastNameReader.close();

        Random random = new Random();
        int nameListLength = nameList.size();
        int lasNameListLength = lastNameList.size();
        String[] providers = new String[]{"@outlook.com", "@gmail.com", "@neumont.edu", "@student.neumont.edu", "@hotmail.com", "@msn.com"};
        int providersLength = providers.length;

        for (int i = 0; i < TOTAL_CONTACTS; i++) {
            int rm = random.nextInt(nameListLength);
            int rl = random.nextInt(lasNameListLength);
            int rp = random.nextInt(providersLength);
            Contact contact = new Contact(
                    nameList.get(rm),
                    lastNameList.get(rl),
                    "" + nameList.get(rm) + "." + lastNameList.get(rl) + providers[rp],
                    "" + lastNameList.get(rl) + "." + nameList.get(rm) + providers[rp],
                    "801" + (random.nextInt(8999999) + 1000000),
                    "801" + (random.nextInt(8999999) + 1000000));
            cm.create(contact);
        }
        long end = System.currentTimeMillis();
        long totalTime = (end - start);
        System.out.println("It took " + totalTime + " milliseconds to create " + TOTAL_CONTACTS + " contacts and store them.");
    }

    @Test
    public void testLookUp() throws IOException {
        long start = System.currentTimeMillis();
        int lookFor = 720;
        for (int i = 0; i < lookFor; i++) {
            Random random = new Random();
            int id = random.nextInt(TOTAL_CONTACTS);
        }
            System.out.println(cm.read(lookFor).toString() + "\n");
        long end = System.currentTimeMillis();
        long totalTime = (end - start);
        System.out.println("It took " + totalTime + " milliseconds to look up and print " + lookFor + " contacts.");
    }

    @Test
    public void testDeleteContact() throws IOException {
        int id = 0;
        long start = System.currentTimeMillis();
        cm.delete(id);
        long end = System.currentTimeMillis();
        long totalTime = (end - start);
        System.out.println("It took " + totalTime + " milliseconds to delete the contact with id: " + id);
    }

    @Test
    public void testSize() throws IOException {
        System.out.println("The current size is: " + cm.loadSize());
    }

    @Test
    public void testNewSize() throws IOException {
        cm.saveSize();
    }
}



