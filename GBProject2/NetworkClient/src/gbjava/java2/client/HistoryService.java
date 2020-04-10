package gbjava.java2.client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {

    private final int HISTORY_READ_BUFFER = 10;

    private File file;

    public List<String> load(int count) {
        List<String> messages = new ArrayList<>(10);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String message = null;
            do {
                // read up to HISTORY_READ_BUFFER=10
                for (int i = 0; i < HISTORY_READ_BUFFER; i++) {
                    message = reader.readLine();
                    if (message != null) {
                        messages.add(message);
                    } else {
                        break;
                    }
                }

                // clear up to count
                int a = messages.size() - count;
                for(int j=0; j< a;j++) {
                    messages.remove(0);
                }

            } while (message != null);

        } catch (Exception e) {
            System.err.println("History loading failed");
        }
        //System.out.printf("History: %s:%s%n", messages.size(), messages);
        return messages;
    }

    public void write(List<String> messages) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            for (String message : messages) {
                writer.write(String.format("%s%n", message));
            }
        } catch (IOException e) {
            System.err.println("History writing failed");
        }
    }

    public void initHistory(String login) {
        String fileName = String.format("history_%s.txt",login);
        file = new File(fileName);
        try {
            if(file.createNewFile()) {
                System.out.printf("History file %s created%n", fileName);
            } else {
                System.out.printf("History file %s exists%n", fileName);
            }
        } catch (IOException e) {
            System.err.println("History loading failed");
        }

    }
}
