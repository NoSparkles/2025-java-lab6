package importing.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class DataLoader {

    // Regex pattern for validating email addresses
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Regex pattern for validating domain names
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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

                            // Validate email format
                            if (!EMAIL_PATTERN.matcher(values[3]).matches()) {
                                errorHandler.accept("Invalid email in " + file + ": " + values[3]);
                                continue; // Skip this entry
                            }

                            // Validate domain format
                            if (!DOMAIN_PATTERN.matcher(values[6]).matches()) {
                                errorHandler.accept("Invalid domain in " + file + ": " + values[6]);
                                continue; // Skip this entry
                            }

                            // Validate birth date format
                            try {
                                LocalDate.parse(values[7]); // Ensure it's a valid date
                            } catch (DateTimeParseException e) {
                                errorHandler.accept("Invalid birth date format in " + file + ": " + values[7]);
                                continue; // Skip this entry
                            }

                            DataModel entry = new DataModel(
                                    Integer.parseInt(values[0]), values[1], values[2], values[3],
                                    values[4], values[5], values[6], values[7]);

                            Platform.runLater(() -> data.add(entry));
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