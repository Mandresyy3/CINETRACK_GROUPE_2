package View;

import javax.swing.*;
import java.awt.*;

/**
 * Classe pour afficher une carte de film.
 */
public class MovieCard {

    /**
     * Affiche une fenêtre présentant les détails d'un film.
     *
     * @param titre   Le titre du film
     * @param synopsys Le synopsis du film
     * @param duree   La durée du film en format dh dmin
     * @param note    La note du film (0.0 à 10.0)
     */
    public static void displayMovieCard(String titre, String synopsys, String duree, double note) {
        
        // Création de la fenêtre principale
        JFrame frame = new JFrame("Fiche du Film : " + titre);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // centre la fenêtre

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(44, 44, 44));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // Panel titre + note
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(mainPanel.getBackground());

        // Label titre
        JLabel titleLabel = new JLabel(titre);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(220, 220, 220));

        // Label note
        JLabel noteLabel = new JLabel(String.format(" %.1f / 10", note));
        noteLabel.setFont(new Font("Arial", Font.BOLD, 18));
        noteLabel.setForeground(new Color(220, 220, 220));

        // titre à gauche et note à droite
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(noteLabel, BorderLayout.EAST);

        // Panel détails (synopsis + durée)
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(mainPanel.getBackground());

        // Durée
        JLabel dureeLabel = new JLabel(String.format("Durée : "+ duree ));
        dureeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dureeLabel.setForeground(new Color(220, 220, 220));

        // Synopsis avec retour à la ligne automatique
        JTextArea synopsisArea = new JTextArea(synopsys);

        // Configuration graphique 
        synopsisArea.setLineWrap(true);
        synopsisArea.setWrapStyleWord(true);
        synopsisArea.setEditable(false);
        synopsisArea.setFont(new Font("Arial", Font.PLAIN, 14));
        synopsisArea.setForeground(new Color(220, 220, 220));
        synopsisArea.setBackground(mainPanel.getBackground());

        detailsPanel.add(dureeLabel, BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(synopsisArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);


        // Ajout des panels au panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);


        // Ajout panel principal à la frame
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

}