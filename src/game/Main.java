package game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame game = new JFrame();
        Controller controller = new Controller(new Model());
        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(450,500);
        game.setResizable(false);

        game.add(controller.getView());

        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}
