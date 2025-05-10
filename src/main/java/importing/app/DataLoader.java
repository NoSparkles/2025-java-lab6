package importing.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.Random;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;

public class DataLoader implements Runnable {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String fileName;
    private final ObservableList<DataModel> data;
    private final ObservableList<Integer> rowCounts;
    private final Consumer<String> errorHandler;
    private final int fileIndex;
    private static int activeThreads = 0; // Track running threads
    private static final Object lock = new Object(); // Synchronization lock
    private final Runnable onComplete;

    public DataLoader(String fileName, ObservableList<DataModel> data, ObservableList<Integer> rowCounts,
                      Consumer<String> errorHandler, Runnable onComplete, int fileIndex) {
        this.fileName = fileName;
        this.data = data;
        this.rowCounts = rowCounts;
        this.errorHandler = errorHandler;
        this.onComplete = onComplete;
        this.fileIndex = fileIndex;
        
        synchronized (DataLoader.lock) {
            ++DataLoader.activeThreads; // Increase active thread count when a new thread starts
        }
    }

    @Override
    public void run() {
        Random random = new Random();

        try (BufferedReader br = new BufferedReader(new FileReader(this.fileName))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = line.split(",");

                    // Validate email format
                    if (!EMAIL_PATTERN.matcher(values[3]).matches()) {
                        errorHandler.accept("Invalid email in " + this.fileName + ": " + values[3]);
                        continue;
                    }

                    // Validate domain format
                    if (!DOMAIN_PATTERN.matcher(values[6]).matches()) {
                        errorHandler.accept("Invalid domain in " + this.fileName + ": " + values[6]);
                        continue;
                    }

                    // Validate birth date format
                    try {
                        LocalDate.parse(values[7]);
                    } catch (Exception e) {
                        errorHandler.accept("Invalid birth date in " + this.fileName + ": " + values[7]);
                        continue;
                    }

                    DataModel entry = new DataModel(
                            Integer.parseInt(values[0]), values[1], values[2], values[3],
                            values[4], values[5], values[6], values[7]);

                    Thread.sleep(random.nextInt(20)); // Simulate processing time

                    Platform.runLater(() -> {
                        data.add(entry);
                        rowCounts.set(this.fileIndex, rowCounts.get(this.fileIndex) + 1);
                    });

                } catch (Exception lineError) {
                    errorHandler.accept("Error processing line in " + this.fileName + ": " + line + "\nDetails: " + lineError.getMessage());
                }
            }
        } catch (Exception fileError) {
            errorHandler.accept("Error opening file " + this.fileName + ": " + fileError.getMessage());
        }

        // Decrease active thread count when a thread finishes
        synchronized (DataLoader.lock) {
            --DataLoader.activeThreads;
            if (DataLoader.activeThreads == 0) {
                Platform.runLater(this.onComplete); // Only call onComplete when all threads finish
            }
        }
    }

    public static void loadDataFromFiles(ObservableList<DataModel> data, ObservableList<Integer> rowCounts,
                                         Consumer<String> errorHandler, Runnable onComplete) {
        String[] files = {"MOCK_DATA1.csv", "MOCK_DATA2.csv", "MOCK_DATA3.csv"};
        for (int i = 0; i < files.length; i++) {
            new Thread(new DataLoader(files[i], data, rowCounts, errorHandler, onComplete, i)).start();
        }
    }
}