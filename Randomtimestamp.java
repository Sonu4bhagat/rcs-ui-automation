import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Randomtimestamp {

    public static Map<String, String> generateRandomTimeStamp() {
        Map<String, String> data = new HashMap<>();

        // Logic to generate a random timestamp
        LocalTime time = LocalTime.of(
                (int) (Math.random() * 24),
                (int) (Math.random() * 60),
                (int) (Math.random() * 60));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = time.format(formatter);
        String randomKey = "Timestamp_" + System.currentTimeMillis();

        data.put(randomKey, timestamp);
        return data;
    }

    public static void main(String[] args) {
        Map<String, String> randomData = generateRandomTimeStamp();
        randomData.forEach((key, value) -> System.out.println(key + " = " + value));
    }
}