import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;

public class WeatherApp {
    public static void main(String[] args) {
        String[] cities = {"Kharar", "Delhi", "Mumbai", "Kolkata", "Chennai", "Bangalore", "Hyderabad", "Pune", "Jaipur", "Lucknow"};
        String apiKey = "be3f9b52fad2a0f9b21c100dcec534e6"; // Use your correct API key

        while (true) {
            System.out.println("====== Weather Report (" + java.time.LocalTime.now() + ") ======");
            for (String city : cities) {
                HttpURLConnection conn = null;
                BufferedReader in = null;
                try {
                    String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
                    String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=" + apiKey + "&units=metric";

                    URL url = new URL(urlString);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");

                    // Check for successful response
                    int responseCode = conn.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        System.out.println("City: " + city + " | Error: HTTP " + responseCode + " - " + conn.getResponseMessage());
                        continue; // Skip this city if error response
                    }

                    // Read and process the response
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    String responseString = response.toString();
                    if (responseString.isEmpty()) {
                        System.out.println("City: " + city + " | Error: Empty response");
                        continue;
                    }

                    JsonObject jsonObject = JsonParser.parseString(responseString).getAsJsonObject();
                    JsonObject main = jsonObject.getAsJsonObject("main");
                    double temp = main.get("temp").getAsDouble();
                    int humidity = main.get("humidity").getAsInt();
                    String weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();

                    System.out.println("City: " + city);
                    System.out.println("  Temperature: " + temp + "°C");
                    System.out.println("  Humidity: " + humidity + "%");
                    System.out.println("  Weather: " + weather);
                } catch (Exception e) {
                    System.out.println("City: " + city + " | Error: " + e.getMessage());
                } finally {
                    // Close resources safely
                    try {
                        if (in != null) in.close();
                        if (conn != null) conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000); // 1 second delay between cities
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("=========================================\n");

            try {
                Thread.sleep(10000); // Wait 10 seconds before next full update
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
