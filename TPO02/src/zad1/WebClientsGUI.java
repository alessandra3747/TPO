package zad1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class WebClientsGUI extends JFrame {

    private static String country = "";
    private static Service service = new Service(country);

    private JPanel mainPanel;

    private JPanel WeatherPanel;
    private JTextField cityInput;
    private JButton showWeatherButton;
    private JList<String> weatherCategories;
    private JList<String> weatherValues;
    private JLabel weatherImageLabel;

    private JPanel RatesPanel;
    private JTextField currencyInput;
    private JLabel ratesPanelTitle;
    private JLabel rateValueLabel;
    private JLabel nbpPanelTitle;
    private JLabel nbpRateValueLabel;
    private JButton showRatesButton;

    private JPanel WebPanel;
    private JFXPanel fxPanel;

    public WebClientsGUI(String userCountry) {
        country = userCountry;
        service = new Service(country);

        setContentPane(mainPanel);
        setTitle("Web Clients");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);


        nbpPanelTitle.setText("Echange rates for PLN to " + service.getServiceCurrency());

        Double nbpRateValue = service.getNBPRate();

        nbpRateValueLabel.setText(String.valueOf(nbpRateValue));



        showWeatherButton.addActionListener(e -> {

            String city = cityInput.getText().trim();

            if (city.isEmpty()) {
                JOptionPane.showMessageDialog(WebClientsGUI.this, "Please enter a proper city name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setWeatherJLists(city);

            Platform.runLater(() -> setWebPanel(city));

        });

        showRatesButton.addActionListener(e -> {

            try {

                ratesPanelTitle.setText("Echange rates for " + service.getServiceCurrency() + " to " + currencyInput.getText().toUpperCase());

                Double rateValue = service.getRateFor(currencyInput.getText().trim());

                rateValueLabel.setText(String.valueOf(rateValue));

            } catch (NullPointerException exception) {
                JOptionPane.showMessageDialog(WebClientsGUI.this, "Please enter a proper currency.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        fxPanel = new JFXPanel();

        Platform.runLater(() -> setWebPanel(null));

        WebPanel.add(fxPanel);

    }


    private void setWeatherJLists(String city) {
        try {
            String weatherJson = WebClientsGUI.service.getWeather(city);

            Map<String, String> weatherData = getWeatherData(weatherJson);

            DefaultListModel<String> categoriesModel = new DefaultListModel<>();
            DefaultListModel<String> valuesModel = new DefaultListModel<>();

            for (Map.Entry<String, String> entry : weatherData.entrySet()) {
                categoriesModel.addElement(entry.getKey());
                valuesModel.addElement(entry.getValue());
            }

            weatherCategories.setModel(categoriesModel);
            weatherValues.setModel(valuesModel);

            updateWeatherPicture(valuesModel.get(0));

        } catch (Exception exception) {
            JOptionPane.showMessageDialog(WebClientsGUI.this, "Please enter a proper city name.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private Map<String, String> getWeatherData(String json) {

        Map<String, String> data = new LinkedHashMap<>();
        Gson gson = new Gson();

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonObject main = jsonObject.getAsJsonObject("main");
        JsonArray weatherArray = jsonObject.getAsJsonArray("weather");

        JsonObject weather = weatherArray.get(0).getAsJsonObject();

        data.put("Main", weather.get("main").getAsString());
        data.put("Temperature", main.get("temp").getAsString());
        data.put("Feels Like", main.get("feels_like").getAsString());
        data.put("Temp Min", main.get("temp_min").getAsString());
        data.put("Temp Max", main.get("temp_max").getAsString());
        data.put("Pressure", main.get("pressure").getAsString());
        data.put("Humidity", main.get("humidity").getAsString());

        return data;
    }


    private void updateWeatherPicture(String currentWeather) {
        BufferedImage weatherImage;
        try{
            switch (currentWeather) {
                case "Clear":
                    weatherImage = ImageIO.read(new File("Assets/Clear.jpg"));;
                    break;
                case "Rain":
                    weatherImage = ImageIO.read(new File("Assets/Rain.jpg"));
                    break;
                case "Clouds":
                    weatherImage = ImageIO.read(new File("Assets/Clouds.jpg"));
                    break;
                case "Squall":
                    weatherImage = ImageIO.read(new File("Assets/Squall.jpg"));
                    break;
                case "Snow":
                    weatherImage = ImageIO.read(new File("Assets/Snow.jpg"));
                    break;
                case "Thunderstorm":
                    weatherImage = ImageIO.read(new File("Assets/Thunderstorm.jpg"));
                    break;
                default:
                    weatherImage = ImageIO.read(new File("Assets/nodata.jpg"));
            }

            weatherImageLabel.setIcon(new ImageIcon(weatherImage));


        } catch (IOException exception) {
            System.out.println("Error while generating weather image." + exception.getMessage());
        }
    }


    private void setWebPanel(String city) {

        WebView webView = new WebView();

        if(city == null)
            webView.getEngine().load("https://pl.wikipedia.org/wiki");
        else
            webView.getEngine().load("https://pl.wikipedia.org/wiki/" + city);

        Scene scene = new Scene(webView);

        fxPanel.setScene(scene);

    }
}
