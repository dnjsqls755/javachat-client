package view.frame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherFetcher {
    public static String getWeatherCondition(String city, String apiKey) {
        try {
        	
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city +
                               "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JSONObject obj = new JSONObject(json.toString());
            return obj.getJSONArray("weather").getJSONObject(0).getString("main");
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}