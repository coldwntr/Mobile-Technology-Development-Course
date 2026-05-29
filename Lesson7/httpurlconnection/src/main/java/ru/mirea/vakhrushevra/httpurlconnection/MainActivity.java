package ru.mirea.vakhrushevra.httpurlconnection;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView textIp;
    private TextView textCity;
    private TextView textRegion;
    private TextView textCountry;
    private TextView textWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLoad = findViewById(R.id.buttonLoad);

        textIp = findViewById(R.id.textIp);
        textCity = findViewById(R.id.textCity);
        textRegion = findViewById(R.id.textRegion);
        textCountry = findViewById(R.id.textCountry);
        textWeather = findViewById(R.id.textWeather);

        buttonLoad.setOnClickListener(v -> loadInfo());
    }

    private void loadInfo() {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            try {

                String ipInfo = downloadText("https://ipinfo.io/json");

                JSONObject json = new JSONObject(ipInfo);

                String ip = json.getString("ip");
                String city = json.getString("city");
                String region = json.getString("region");
                String country = json.getString("country");

                String loc = json.getString("loc");

                String[] coords = loc.split(",");

                String latitude = coords[0];
                String longitude = coords[1];

                String weatherUrl =
                        "https://api.open-meteo.com/v1/forecast?latitude="
                                + latitude
                                + "&longitude="
                                + longitude
                                + "&current_weather=true";

                String weatherResponse =
                        downloadText(weatherUrl);

                JSONObject weatherJson =
                        new JSONObject(weatherResponse);

                JSONObject currentWeather =
                        weatherJson.getJSONObject(
                                "current_weather");

                double temperature =
                        currentWeather.getDouble(
                                "temperature");

                runOnUiThread(() -> {

                    textIp.setText(
                            "IP: " + ip);

                    textCity.setText(
                            "Город: " + city);

                    textRegion.setText(
                            "Регион: " + region);

                    textCountry.setText(
                            "Страна: " + country);

                    textWeather.setText(
                            "Температура: "
                                    + temperature
                                    + " °C");
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        textWeather.setText(
                                "Ошибка: "
                                        + e.getMessage()));
            }

        });
    }

    private String downloadText(String urlString)
            throws Exception {

        URL url = new URL(urlString);

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));

        StringBuilder result =
                new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {

            result.append(line);
        }

        reader.close();

        connection.disconnect();

        return result.toString();
    }
}