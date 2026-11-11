package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Controleur.UserControleur;

public class Connexion extends JFrame {

    // Les donneés de l'utilisateur
    ArrayList<JTextField> userInput;
    UserControleur userControleur;

    // Runnable qui permet de changer de fenetre
    Runnable onSuccess;

    public Connexion(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        // le panel principal
        JPanel main = new JPanel(new BorderLayout());
        main.setPreferredSize(new Dimension(700, 500));
        main.setBackground(new Color(247, 168, 116));
        // Panel de connexion
        GridLayout grid1 = new GridLayout(5, 1);
        JPanel connexion = new JPanel(grid1);

        userInput = new ArrayList<>();
        userControleur = new UserControleur();

        ajouterComposent(connexion);

        // Centrer le panel
        main.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));
        main.add(connexion, BorderLayout.CENTER);
        add(main);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void ajouterComposent(JPanel p) {
        // Ajout des composants
        String[] LabelText = { "nom d'utilisateur :", "Mot de passe :" };
        for (int i = 0; i < LabelText.length; i++) {
            JLabel label = new JLabel(LabelText[i]);
            JTextField text = new JTextField();
            userInput.add(text);
            // Changement du font
            text.setFont(new Font("Arial", 1, 15));
            label.setFont(new Font("Arial", 1, 15));
            // Ajout de padding
            label.setBorder(BorderFactory.createEmptyBorder(1, 40, 1, 1));
            text.setBorder(BorderFactory.createEmptyBorder(1, 40, 1, 1));
            p.add(label);
            p.add(text);
        }
        JButton bouton = new JButton("Connexion");
        bouton.setBorder(BorderFactory.createEmptyBorder(80, 80, 80, 80));

        bouton.addActionListener(e -> {
            Boolean connecter = verifierConnexion();
            if (!connecter) {
                JOptionPane.showInternalMessageDialog(null, "Identifiant ou mot de passe invalide");
            } else {
                dispose(); // ferme la fenêtre

                if (onSuccess != null) {
                    onSuccess.run(); // exécute le code externe
                }
            }
        });
        bouton.setFocusable(false);
        p.add(bouton);
    }

    public boolean verifierConnexion() {
        HashMap<String, String> userData = new HashMap<>();
        String[] clee = { "login", "mp" };
        for (int i = 0; i < clee.length; i++) {
            userData.put(clee[i], userInput.get(i).getText());
        }
        return userControleur.verifierConnexion(userData);
    }

}
