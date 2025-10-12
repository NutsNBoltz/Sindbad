import java.awt.*;
import javax.swing.*;



public class Main extends JPanel {
    public static void main(String[] args) {

        JFrame window = new JFrame("Sindbad Island");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GameMap mapPanel = new GameMap();
        window.add(mapPanel);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);


        };



}