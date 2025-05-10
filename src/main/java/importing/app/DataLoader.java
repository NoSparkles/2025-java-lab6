package importing.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class DataLoader {
    public static void loadDataFromFiles(ObservableList<DataModel> data) {
        String[] files = {"MOCK_DATA1.csv", "MOCK_DATA2.csv", "MOCK_DATA3.csv"};
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (String file : files) {
            executor.execute(() -> {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    br.readLine(); // Skip header
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] values = line.split(",");
                        DataModel entry = new DataModel(
                                Integer.parseInt(values[0]), values[1], values[2], values[3],
                                values[4], values[5], values[6], values[7]);

                        // Update UI safely
                        Platform.runLater(() -> data.add(entry));

                        Thread.sleep(300); // Simulate delay
                    }
                    System.out.println(file + " įkėlimas baigtas!");
                } catch (Exception e) {
                    System.err.println("Klaida įkeliant failą " + file + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
    }
}