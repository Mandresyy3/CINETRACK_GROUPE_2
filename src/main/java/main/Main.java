package main;

import javax.swing.SwingUtilities;

import View.Connexion;
import View.FenetrePrincipale;

public class Main {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
        new Connexion(() -> new FenetrePrincipale());
        }

        });
    }
}
