package importing.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class DataLoader {
    public static void loadDataFromFiles(ObservableList<DataModel> data, Consumer<String> errorHandler, Runnable onComplete) {
        String[] files = {"MOCK_DATA1.csv", "MOCK_DATA2.csv", "MOCK_DATA3.csv"};
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (String file : files) {
            executor.execute(() -> {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    br.readLine(); // Skip header
                    String line;
                    while ((line = br.readLine()) != null) {
                        try {
                            String[] values = line.split(",");
                            DataModel entry = new DataModel(
                                    Integer.parseInt(values[0]), values[1], values[2], values[3],
                                    values[4], values[5], values[6], values[7]);

                            Platform.runLater(() -> data.add(entry));
                            //Thread.sleep(10);
                        } catch (Exception lineError) {
                            errorHandler.accept("Error processing line in " + file + ": " + line + "\nDetails: " + lineError.getMessage());
                        }
                    }
                } catch (Exception fileError) {
                    errorHandler.accept("Error opening file " + file + ": " + fileError.getMessage());
                }
            });
        }

        executor.submit(() -> { // Call completion callback when all files are processed
            executor.shutdown();
            Platform.runLater(onComplete);
        });

        executor.shutdown();
    }
}