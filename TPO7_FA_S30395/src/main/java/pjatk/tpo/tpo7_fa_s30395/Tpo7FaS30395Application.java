package pjatk.tpo.tpo7_fa_s30395;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.swing.*;

@SpringBootApplication
public class Tpo7FaS30395Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartFrame::new);
    }
}