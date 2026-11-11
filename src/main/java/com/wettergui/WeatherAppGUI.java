package com.wettergui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wettergui.dto.ForecastResponse;
import com.wettergui.dto.WeatherResponse;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Hauptfenster der Wetter-App GUI.
 * Ruft die Spring Boot API auf und zeigt das Wetter an.
 */
public class WeatherAppGUI {

    private static final String API_BASE_URL = "http://localhost:8080/api";
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private final Stage stage;

    private TextField cityField;
    private Label statusLabel;
    private VBox currentWeatherBox;
    private VBox forecastBox;

    public WeatherAppGUI(Stage stage) {
        this.stage = stage;
        stage.setTitle("Wetter-App");
        stage.setMinWidth(500);
        stage.setMinHeight(600);
    }

    public void show() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #e8f0fe;");

        // Titel
        Label titleLabel = new Label("🌤 Wetter-App");
        titleLabel.setFont(Font.font(null, FontWeight.BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #1a73e8;");
        titleLabel.setAlignment(Pos.CENTER);

        // Stadt-Eingabe
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        Label cityLabel = new Label("Stadt:");
        cityLabel.setFont(Font.font(null, 14));
        cityLabel.setStyle("-fx-text-fill: #3c4043;");
        cityField = new TextField("Zurich");
        cityField.setPrefWidth(180);
        cityField.setFont(Font.font(null, 14));
        cityField.setStyle("-fx-background-radius: 6; -fx-padding: 6;");
        Button searchButton = new Button("Wetter anzeigen");
        searchButton.setFont(Font.font(null, 13));
        searchButton.setStyle("-fx-background-color: #1a73e8; -fx-text-fill: white; -fx-padding: 6 14; -fx-background-radius: 6; -fx-cursor: hand;");
        searchButton.setOnAction(e -> fetchWeather());
        inputBox.getChildren().addAll(cityLabel, cityField, searchButton);

        // Status-Label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font(null, 12));
        statusLabel.setStyle("-fx-text-fill: #d93025;");
        statusLabel.setAlignment(Pos.CENTER);

        // Aktuelles Wetter
        currentWeatherBox = new VBox(8);
        currentWeatherBox.setPadding(new Insets(15));
        currentWeatherBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");

        // Vorhersage mit ScrollPane
        forecastBox = new VBox(6);
        forecastBox.setPadding(new Insets(10));

        ScrollPane forecastScroll = new ScrollPane(forecastBox);
        forecastScroll.setFitToWidth(true);
        forecastScroll.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        forecastScroll.setPadding(new Insets(5));
        VBox.setVgrow(forecastScroll, Priority.ALWAYS);

        Label forecastTitle = new Label("📅 5-Tage-Vorhersage");
        forecastTitle.setFont(Font.font(null, FontWeight.BOLD, 16));
        forecastTitle.setStyle("-fx-text-fill: #1a73e8;");
        forecastTitle.setAlignment(Pos.CENTER);
        forecastTitle.setPadding(new Insets(10, 0, 5, 0));

        root.getChildren().addAll(titleLabel, inputBox, statusLabel, currentWeatherBox, forecastTitle, forecastScroll);

        Scene scene = new Scene(root, 520, 550);
        stage.setScene(scene);
        stage.show();

        // Wetter beim Start laden
        fetchWeather();
    }

    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            statusLabel.setText("Bitte geben Sie eine Stadt ein.");
            return;
        }

        statusLabel.setText("Lade Wetterdaten...");
        currentWeatherBox.getChildren().clear();
        forecastBox.getChildren().clear();

        // Aktuelles Wetter in neuem Thread laden
        new Thread(() -> fetchCurrentWeather(city)).start();
    }

    private void fetchCurrentWeather(String city) {
        try {
            String url = API_BASE_URL + "/weather?city=" + encodeCity(city);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                WeatherResponse weather = gson.fromJson(response.body(), WeatherResponse.class);
                Platform.runLater(() -> displayCurrentWeather(weather));

                // Vorhersage laden
                new Thread(() -> fetchForecast(city)).start();
            } else {
                Platform.runLater(() -> statusLabel.setText("Stadt '" + city + "' nicht gefunden."));
            }
        } catch (IOException | InterruptedException e) {
            Platform.runLater(() -> statusLabel.setText("Fehler: Konnte Wetterdaten nicht laden. Ist der Server gestartet?"));
        }
    }

    private void fetchForecast(String city) {
        try {
            String url = API_BASE_URL + "/forecast?city=" + encodeCity(city);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
                List<ForecastResponse> forecast = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    ForecastResponse fr = gson.fromJson(jsonArray.get(i), ForecastResponse.class);
                    forecast.add(fr);
                }
                Platform.runLater(() -> displayForecast(forecast));
            } else {
                Platform.runLater(() -> {
                    Label noForecast = new Label("Vorhersage nicht verfuegbar.");
                    noForecast.setStyle("-fx-text-fill: #999;");
                    forecastBox.getChildren().add(noForecast);
                });
            }
        } catch (IOException | InterruptedException e) {
            // Vorhersage-Fehler stillschweigend ignorieren
        }
    }

    private void displayCurrentWeather(WeatherResponse weather) {
        statusLabel.setText("");

        // Stadt und Temperatur
        Label cityLabel = new Label(weather.getCity());
        cityLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
        cityLabel.setStyle("-fx-text-fill: #3c4043;");
        cityLabel.setAlignment(Pos.CENTER);

        Label tempLabel = new Label(String.format("%.1f°C", weather.getTemperature()));
        tempLabel.setFont(Font.font(null, FontWeight.BOLD, 40));
        tempLabel.setStyle("-fx-text-fill: #1a73e8;");
        tempLabel.setAlignment(Pos.CENTER);

        Label descLabel = new Label(capitalize(weather.getDescription()));
        descLabel.setFont(Font.font(null, 14));
        descLabel.setStyle("-fx-text-fill: #5f6368;");
        descLabel.setAlignment(Pos.CENTER);

        // Details
        HBox detailsBox = new HBox(20);
        detailsBox.setAlignment(Pos.CENTER);
        detailsBox.setPadding(new Insets(10, 0, 0, 0));

        Text feelsLike = createDetailText("Gefuehl: " + String.format("%.1f°C", weather.getFeelsLike()));
        Text humidity = createDetailText("Luftfeuchtigkeit: " + weather.getHumidity() + "%");
        Text wind = createDetailText("Wind: " + String.format("%.1f m/s", weather.getWindSpeed()));

        detailsBox.getChildren().addAll(feelsLike, humidity, wind);

        currentWeatherBox.getChildren().addAll(cityLabel, tempLabel, descLabel, detailsBox);
    }

    private Text createDetailText(String text) {
        Text t = new Text(text);
        t.setFont(Font.font(null, 12));
        t.setFill(Color.web("#5f6368"));
        return t;
    }

    private void displayForecast(List<ForecastResponse> forecast) {
        for (ForecastResponse day : forecast) {
            HBox dayBox = new HBox(15);
            dayBox.setAlignment(Pos.CENTER_LEFT);
            dayBox.setPadding(new Insets(6, 8, 6, 8));
            dayBox.setStyle("-fx-background-color: #e8f0fe; -fx-background-radius: 6;");

            Label dayLabel = new Label(day.getDate());
            dayLabel.setMinWidth(80);
            dayLabel.setFont(Font.font(null, FontWeight.BOLD, 13));
            dayLabel.setStyle("-fx-text-fill: #3c4043;");

            Label tempLabel = new Label(String.format("%.1f / %.1f °C", day.getTempMin(), day.getTempMax()));
            tempLabel.setMinWidth(90);
            tempLabel.setFont(Font.font(null, 13));
            tempLabel.setStyle("-fx-text-fill: #1a73e8;");

            Label descLabel = new Label(capitalize(day.getDescription()));
            descLabel.setFont(Font.font(null, 13));
            descLabel.setStyle("-fx-text-fill: #5f6368;");

            dayBox.getChildren().addAll(dayLabel, tempLabel, descLabel);
            forecastBox.getChildren().add(dayBox);
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private String encodeCity(String city) {
        try {
            return java.net.URLEncoder.encode(city, "UTF-8");
        } catch (IOException e) {
            return city;
        }
    }
}
