/**
 *
 *  @author Fus Aleksandra S30395
 *
 */

package zad1;


import javax.swing.*;
import java.util.Locale;

public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();

    SwingUtilities.invokeLater(() -> new WebClientsGUI(Locale.getDefault().getDisplayCountry()));
  }
}
