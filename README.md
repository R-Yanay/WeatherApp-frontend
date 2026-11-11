# Weather App GUI

Eine JavaFX Desktop-App, die Wetterdaten von der [WeatherApp Spring Boot API](https://github.com/dein-name/WeatherApp) abruft und in einem Fenster anzeigt.

## Features

- **Aktuelles Wetter** mit Temperatur, Luftfeuchtigkeit, Wind und Beschreibung
- **5-Tage-Vorhersage** mit Min/Max Temperatur pro Tag
- **Helles Design** mit Beige/Hellblau/Weiss-Farbschema
- **HTTP-Client** Integration mit der Spring Boot Backend-API
- **JSON-Parsing** mit Gson

## Technologie

- **Java 17**
- **JavaFX 21**
- **Gson** fuer JSON-Parsing
- **Maven**

## Einrichtung

### 1. Backend starten

Diese App braucht die [WeatherApp Spring Boot API](https://github.com/dein-name/WeatherApp). Starte sie zuerst:

```bash
cd C:\Users\roiya\IdeaProjects\WeatherApp
mvn spring-boot:run
```

### 2. GUI starten

```bash
cd C:\Users\roiya\IdeaProjects\WeatherAppGUI
mvn javafx:run
```

## Nutzung

1. Gib einen Stadtname ein (z. B. "Zurich", "Berlin", "New York")
2. Klicke auf "Wetter anzeigen"
3. Das aktuelle Wetter und die 5-Tage-Vorhersage werden angezeigt

## Architektur

```
WeatherAppGUI/
├── Main.java                    # JavaFX Einstiegspunkt
├── WeatherAppGUI.java           # Fenster, Layout, HTTP-Client
├── WeatherResponse.java         # DTO fuer aktuelles Wetter
└── ForecastResponse.java        # DTO fuer 5-Tage-Vorhersage
```

## Lernen

Dieses Projekt zeigt wichtige JavaFX Konzepte:

| Konzept | Wo? |
|---------|-----|
| **JavaFX GUI** | VBox, HBox, Label, Button, TextField |
| **HTTP-Client** | `HttpClient` fuer API-Anfragen an das Backend |
| **JSON-Parsing** | Gson fuer JSON-zu-Objekt-Umwandlung |
| **Threads** | API-Aufrufe im Hintergrund, GUI bleibt reaktiv |
| **Platform.runLater()** | GUI-Updates vom Hintergrund-Thread in den JavaFX-Thread |
| **ScrollPane** | Scrollbare 5-Tage-Vorhersage |

## Lebenslauf

Dieses Projekt zeigt:

- **JavaFX** Desktop-App Entwicklung mit hellem Design
- **Backend-Frontend-Integration** (Spring Boot + JavaFX)
- **HTTP-Client** und **JSON-Parsing** mit Gson
- **Multithreading** fuer reaktive GUI
- **Full-Stack-Anwendung**: Backend (WeatherApp) + Frontend (WeatherAppGUI)

## Backend

Das Backend-Projekt [WeatherApp](https://github.com/dein-name/WeatherApp) ist eine Spring Boot REST API mit OpenWeatherMap-Integration.
