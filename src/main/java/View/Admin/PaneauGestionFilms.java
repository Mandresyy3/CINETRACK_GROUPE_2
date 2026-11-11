package View.Admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Controleur.FilmControleur;
import Modele.Film;
import View.AbstractPanel;
import View.FenetrePrincipale;

public class PaneauGestionFilms extends AbstractPanel {

    private FilmControleur controleur = new FilmControleur();

    private DefaultTableModel model;
    private JTable table;

    // Liste parallèle qui stocke l'id de chaque film à l'index correspondant
    private List<Integer> idsFilms = new ArrayList<>();

    public PaneauGestionFilms() {
        setLayout(new BorderLayout());
        setBackground(FenetrePrincipale.font);

        // ===== HEADER =====
        JLabel header = createHeader("GESTION DES FILMS");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(FenetrePrincipale.font);
        topPanel.setPreferredSize(new Dimension(100, 100));
        topPanel.add(header, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        // ===== TABLEAU =====
        model = new DefaultTableModel(new String[]{"ID", "Titre", "Genre", "Durée", "Note"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 != 0
                            ? new Color(43, 43, 43)
                            : new Color(64, 64, 64));
                }
                return c;
            }
        };

        // ===== STYLE HEADER TABLEAU =====
        JTableHeader headerTable = table.getTableHeader();
        headerTable.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(43, 43, 43));
                c.setForeground(new Color(166, 166, 166));
                setHorizontalAlignment(CENTER);
                return c;
            }
        });

        table.setBackground(FenetrePrincipale.font);
        table.setForeground(new Color(166, 166, 166));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(FenetrePrincipale.font);
        scroll.setBackground(FenetrePrincipale.font);
        add(scroll, BorderLayout.CENTER);

        // ===== BOUTONS =====
        JPanel actions = new JPanel();
        actions.setBackground(FenetrePrincipale.font);

        JButton btnAjouter    = createStyledButton("Ajouter");
        JButton btnSupprimer  = createStyledButton("Supprimer");
        JButton btnActualiser = createStyledButton("Actualiser");

        actions.add(btnAjouter);
        actions.add(btnSupprimer);
        actions.add(btnActualiser);
        add(actions, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnAjouter.addActionListener(e -> ajouterFilm());
        btnSupprimer.addActionListener(e -> supprimerFilm());
        btnActualiser.addActionListener(e -> chargerFilms());

        chargerFilms();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(215, 83, 0));
        btn.setForeground(new Color(255, 211, 184));
        btn.setFont(util.GeneralFont.getfont(20));
        return btn;
    }

    // Charge tous les films depuis la base de données
    private void chargerFilms() {
        model.setRowCount(0);
        idsFilms.clear();

        List<Film> films = controleur.recupereFilm();

        for (Film film : films) {
            model.addRow(new Object[]{
                film.getIdFilm(),
                film.getTitre(),
                film.getGenre(),
                film.getDureeFormatee(),
                film.getNote()
            });
            idsFilms.add(film.getIdFilm());
        }
    }

    // Affiche un formulaire pour ajouter un film
    private void ajouterFilm() {
        JTextField titre    = new JTextField();
        JTextField genre    = new JTextField();
        JTextField duree    = new JTextField();
        JTextField note     = new JTextField();
        JTextField synopsis = new JTextField();

        Object[] message = {
            "Titre :",          titre,
            "Genre :",          genre,
            "Durée (minutes) :", duree,
            "Note (0-10) :",    note,
            "Synopsys :",       synopsis
        };

        int option = JOptionPane.showConfirmDialog(
            null, message, "Ajouter un film", JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            // Vérifier que les champs ne sont pas vides
            if (titre.getText().isBlank() || genre.getText().isBlank()
                    || duree.getText().isBlank() || note.getText().isBlank()
                    || synopsis.getText().isBlank()) {
                JOptionPane.showMessageDialog(null, "Tous les champs doivent être remplis.");
                return;
            }

            // Vérifier que la durée est un nombre entier
            if (!duree.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "La durée doit être en minutes (nombre entier).");
                return;
            }

            // Vérifier que la note est un nombre valide
            try {
                double n = Double.parseDouble(note.getText());
                if (n < 0 || n > 10) {
                    JOptionPane.showMessageDialog(null, "La note doit être entre 0 et 10.");
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "La note doit être un nombre.");
                return;
            }

            // Les clés doivent correspondre exactement à ce qu'attend FilmControleur
            HashMap<String, String> data = new HashMap<>();
            data.put("Titre",            titre.getText());
            data.put("Genre",            genre.getText());
            data.put("Durée (minutes)",  duree.getText());
            data.put("Note",             note.getText());
            data.put("Synopsys",         synopsis.getText());

            boolean succes = controleur.ajouter(data);
            if (succes) {
                JOptionPane.showMessageDialog(null, "Film ajouté avec succès.");
                chargerFilms();
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout.");
            }
        }
    }

    // Supprime le film sélectionné après confirmation
    private void supprimerFilm() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Sélectionne un film à supprimer.");
            return;
        }

        String titre = model.getValueAt(row, 1).toString();
        int idFilm   = idsFilms.get(row);

        int confirm = JOptionPane.showConfirmDialog(
            null,
            "Supprimer le film \"" + titre + "\" ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean succes = controleur.supprimerFilm(idFilm);
            if (succes) {
                JOptionPane.showMessageDialog(null, "Film supprimé avec succès.");
                chargerFilms();
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression.");
            }
        }
    }

    @Override
    protected void construireMenu(JPanel menu) {
        //  c'est PaneauAdmin qui gère le menu
    }
}