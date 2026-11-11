package View;

import Controleur.SeanceControleur;
import Modele.Film;
import Modele.Salle;
import Modele.Seance;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import util.GeneralFont;

public class PaneauSeances extends JPanel {

    private SeanceControleur controleur = new SeanceControleur();

    private final String LIST_SEANCE = "liste des seances";
    private final String AJOUTER_SEANCE = "ajouter seance";
    private final String RECHER_SEANCE = "recherche date";
    private final String RECHER_SEANCE_TITRE = "recherche titre";

    private final String[] COLONNES = { "ID", "Film", "Salle", "Date", "Heure", "Places dispo" };

    private DefaultTableModel modelPrincipal = customModel();
    private DefaultTableModel modelRechercheDate = customModel();
    private DefaultTableModel modelRechercheTitre = customModel();

    private boolean rechercheParDate = true;

    private CardLayout card;
    private JPanel contenuSeancePanel;

    private DefaultTableModel customModel() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public PaneauSeances() {
        setName(FenetrePrincipale.PAGE_SEANCES);
        setLayout(new BorderLayout());

        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(FenetrePrincipale.LARGEUR_MENU, 1));
        construireMenu(menu);
        add(menu, BorderLayout.WEST);

        card = new CardLayout();
        contenuSeancePanel = new JPanel(card);

        ajouterContenu(LIST_SEANCE, "LISTE DES SÉANCES", modelPrincipal);
        ajouterContenu(RECHER_SEANCE, "RECHERCHER PAR DATE", modelRechercheDate);
        ajouterContenu(RECHER_SEANCE_TITRE, "RECHERCHER PAR TITRE", modelRechercheTitre);
        ajouterContenu(AJOUTER_SEANCE, "AJOUTER UNE SÉANCE", null);

        card.show(contenuSeancePanel, LIST_SEANCE);
        add(contenuSeancePanel);

        chargerSeances(modelPrincipal, controleur.chargerSeances());
    }

    private void ajouterContenu(String nom, String titre, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FenetrePrincipale.font);

        JLabel header = new JLabel(titre);
        header.setHorizontalAlignment(0);
        header.setVerticalAlignment(0);
        header.setFont(GeneralFont.getfont(40));
        header.setForeground(Color.white);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(FenetrePrincipale.font);
        topPanel.setPreferredSize(new Dimension(contenuSeancePanel.getPreferredSize().width, 100));
        topPanel.add(header, BorderLayout.NORTH);

        if (nom.equals(RECHER_SEANCE) || nom.equals(RECHER_SEANCE_TITRE)) {

            JTextField barDeRecherche = new JTextField();
            barDeRecherche.setPreferredSize(new Dimension(200, 30));

            if (nom.equals(RECHER_SEANCE)) {
                barDeRecherche.setToolTipText("Format : YYYY-MM-DD");
            } else {
                barDeRecherche.setToolTipText("Entrez le titre du film");
            }

            barDeRecherche.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    recherche(rechercheParDate, barDeRecherche);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    recherche(rechercheParDate, barDeRecherche);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            topPanel.add(barDeRecherche, BorderLayout.SOUTH);

            JMenuBar bar = new JMenuBar();
            bar.setBackground(FenetrePrincipale.font);

            String[] modesDeRecherche = { "Par date", "Par titre" };

            for (String mode : modesDeRecherche) {
                JMenu menuMode = new JMenu(mode);
                menuMode.setOpaque(true);
                menuMode.setBackground(Color.BLACK);
                menuMode.setForeground(Color.WHITE);
                menuMode.getPopupMenu().setBackground(Color.black);

                JMenuItem item = new JMenuItem("Choisir");
                item.setOpaque(true);
                item.setBackground(FenetrePrincipale.font);
                item.setForeground(Color.WHITE);

                item.addActionListener(e -> {
                    if (mode.equals("Par date")) {
                        card.show(contenuSeancePanel, RECHER_SEANCE);
                        rechercheParDate = true;
                    } else {
                        card.show(contenuSeancePanel, RECHER_SEANCE_TITRE);
                        chargerSeances(modelRechercheTitre, controleur.chargerSeances());
                        rechercheParDate = false;
                    }
                });

                menuMode.add(item);
                bar.add(menuMode);
            }

            topPanel.add(bar);
            topPanel.setBackground(FenetrePrincipale.font);
        }

        panel.add(topPanel, BorderLayout.NORTH);

        if (!nom.equals(AJOUTER_SEANCE)) {
            panel.add(createTablePanel(creerTable(COLONNES, model),
                    new Dimension(contenuSeancePanel.getPreferredSize().width,
                            contenuSeancePanel.getPreferredSize().height - 100)),
                    BorderLayout.CENTER);
        } else {
            ajouterFormulaire(panel);
        }

        contenuSeancePanel.add(panel, nom);
    }

    private void recherche(boolean parDate, JTextField field) {
        String text = field.getText();
        if (parDate) {
            chargerSeances(modelRechercheDate, controleur.chargerSeancesParDate(text));
        } else {
            chargerSeances(modelRechercheTitre, controleur.chargerSeancesParFilm(text));
        }
    }

    private void ajouterFormulaire(JPanel p) {
        p.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JPanel formPanel = new JPanel(new GridLayout(14, 1, 5, 10));
        Color bgColor = new Color(30, 30, 30);
        formPanel.setBackground(bgColor);

        JLabel lblFilm = new JLabel("Film");
        lblFilm.setForeground(new Color(200, 200, 200));
        lblFilm.setFont(GeneralFont.getfont(20));

        JComboBox<String> comboFilm = new JComboBox<>();
        comboFilm.setBackground(bgColor);
        comboFilm.setForeground(Color.white);

        List<Film> films = controleur.chargerFilms();
        for (Film f : films) {
            comboFilm.addItem(f.getIdFilm() + " — " + f.getTitre());
        }

        JLabel lblSalle = new JLabel("Salle");
        lblSalle.setForeground(new Color(200, 200, 200));
        lblSalle.setFont(GeneralFont.getfont(20));

        JComboBox<String> comboSalle = new JComboBox<>();
        comboSalle.setBackground(bgColor);
        comboSalle.setForeground(Color.white);

        List<Salle> salles = controleur.chargerSalles();
        for (Salle s : salles) {
            comboSalle.addItem(s.getIdSalle() + " — " + s.getNom());
        }

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD)");
        lblDate.setForeground(new Color(200, 200, 200));
        lblDate.setFont(GeneralFont.getfont(20));
        JTextField champDateAjout = new JTextField();

        JLabel lblHeure = new JLabel("Heure (HH:MM)");
        lblHeure.setForeground(new Color(200, 200, 200));
        lblHeure.setFont(GeneralFont.getfont(20));
        JTextField champHeure = new JTextField();

        JLabel lblPlaces = new JLabel("Places disponibles");
        lblPlaces.setForeground(new Color(200, 200, 200));
        lblPlaces.setFont(GeneralFont.getfont(20));
        JTextField champPlaces = new JTextField();

        formPanel.add(lblFilm);
        formPanel.add(comboFilm);
        formPanel.add(lblSalle);
        formPanel.add(comboSalle);
        formPanel.add(lblDate);
        formPanel.add(champDateAjout);
        formPanel.add(lblHeure);
        formPanel.add(champHeure);
        formPanel.add(lblPlaces);
        formPanel.add(champPlaces);

        JButton btnAjouter = new JButton("Ajouter la séance");
        btnAjouter.addActionListener(e -> {
            int indexFilm = comboFilm.getSelectedIndex();
            if (indexFilm < 0 || films.isEmpty()) {
                afficherErreur("Veuillez sélectionner un film.");
                return;
            }

            int indexSalle = comboSalle.getSelectedIndex();
            if (indexSalle < 0 || salles.isEmpty()) {
                afficherErreur("Veuillez sélectionner une salle.");
                return;
            }

            int idFilm = films.get(indexFilm).getIdFilm();
            int idSalle = salles.get(indexSalle).getIdSalle();

            String resultat = controleur.ajouterSeance(
                    idFilm,
                    idSalle,
                    champDateAjout.getText(),
                    champHeure.getText(),
                    champPlaces.getText());

            if (resultat.equals("OK")) {
                afficherSucces("Séance ajoutée avec succès !");
                card.show(contenuSeancePanel, LIST_SEANCE);
                chargerSeances(modelPrincipal, controleur.chargerSeances());
            } else {
                afficherErreur(resultat);
            }
        });

        formPanel.add(new JLabel());
        formPanel.add(new JLabel());
        formPanel.add(btnAjouter);

        p.add(formPanel);
    }

    // ✅ Formulaire de modification pré-rempli
    private void ouvrirFormulaireModification(int idSeance, String titreActuel, int idSalleActuel,
            String dateActuelle, String heureActuelle, int placesActuelles) {

        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier la séance");
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(12, 1, 5, 8));
        Color bgColor = new Color(30, 30, 30);
        formPanel.setBackground(bgColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // --- ComboBox Film pré-rempli ---
        JLabel lblFilm = new JLabel("Film");
        lblFilm.setForeground(new Color(200, 200, 200));
        JComboBox<String> comboFilm = new JComboBox<>();
        comboFilm.setBackground(bgColor);
        comboFilm.setForeground(Color.white);

        List<Film> films = controleur.chargerFilms();
        int indexFilmActuel = 0;
        for (int i = 0; i < films.size(); i++) {
            comboFilm.addItem(films.get(i).getIdFilm() + " — " + films.get(i).getTitre());
            if (films.get(i).getTitre() != null && films.get(i).getTitre().equals(titreActuel)) {
                indexFilmActuel = i;
            }
        }
        comboFilm.setSelectedIndex(indexFilmActuel);

        // --- ComboBox Salle pré-remplie ---
        JLabel lblSalle = new JLabel("Salle");
        lblSalle.setForeground(new Color(200, 200, 200));
        JComboBox<String> comboSalle = new JComboBox<>();
        comboSalle.setBackground(bgColor);
        comboSalle.setForeground(Color.white);

        List<Salle> salles = controleur.chargerSalles();
        int indexSalleActuelle = 0;
        for (int i = 0; i < salles.size(); i++) {
            comboSalle.addItem(salles.get(i).getIdSalle() + " — " + salles.get(i).getNom());
            if (salles.get(i).getIdSalle() == idSalleActuel) {
                indexSalleActuelle = i;
            }
        }
        comboSalle.setSelectedIndex(indexSalleActuelle);

        // --- Champs pré-remplis ---
        JLabel lblDate = new JLabel("Date (YYYY-MM-DD)");
        lblDate.setForeground(new Color(200, 200, 200));
        JTextField champDate = new JTextField(dateActuelle);

        JLabel lblHeure = new JLabel("Heure (HH:MM)");
        lblHeure.setForeground(new Color(200, 200, 200));
        JTextField champHeure = new JTextField(heureActuelle);

        JLabel lblPlaces = new JLabel("Places disponibles");
        lblPlaces.setForeground(new Color(200, 200, 200));
        JTextField champPlaces = new JTextField(String.valueOf(placesActuelles));

        formPanel.add(lblFilm);
        formPanel.add(comboFilm);
        formPanel.add(lblSalle);
        formPanel.add(comboSalle);
        formPanel.add(lblDate);
        formPanel.add(champDate);
        formPanel.add(lblHeure);
        formPanel.add(champHeure);
        formPanel.add(lblPlaces);
        formPanel.add(champPlaces);

        // --- Bouton Confirmer ---
        JButton btnConfirmer = new JButton("Confirmer la modification");
        btnConfirmer.addActionListener(e -> {
            int indexFilm = comboFilm.getSelectedIndex();
            int indexSalle = comboSalle.getSelectedIndex();

            int idFilm = films.get(indexFilm).getIdFilm();
            int idSalle = salles.get(indexSalle).getIdSalle();

            String resultat = controleur.modifierSeance(
                    idSeance,
                    idFilm,
                    idSalle,
                    champDate.getText(),
                    champHeure.getText(),
                    champPlaces.getText());

            if (resultat.equals("OK")) {
                afficherSucces("Séance modifiée avec succès !");
                dialog.dispose();
                chargerSeances(modelPrincipal, controleur.chargerSeances());
            } else {
                afficherErreur(resultat);
            }
        });

        formPanel.add(new JLabel());
        formPanel.add(btnConfirmer);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JTable creerTable(String[] colonnes, DefaultTableModel model) {
        JTable table = new JTable(model) {
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

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
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

        // ✅ Clic sur une ligne → popup Modifier / Supprimer / Fermer
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int idSeance = (int) table.getValueAt(row, 0);
                    String film = table.getValueAt(row, 1).toString();
                    int idSalle = (int) table.getValueAt(row, 2);
                    String date = table.getValueAt(row, 3).toString();
                    String heure = table.getValueAt(row, 4).toString();
                    int places = Integer.parseInt(table.getValueAt(row, 5).toString());

                    String[] options = { "Modifier", "Supprimer", "Fermer" };
                    int choix = JOptionPane.showOptionDialog(
                            null,
                            "Film : " + film + "\n" +
                                    "Date : " + date + "\n" +
                                    "Heure : " + heure + "\n" +
                                    "Places : " + places,
                            "Séance sélectionnée",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            null,
                            options,
                            options[2]);

                    if (choix == 0) {
                        // ✅ Modifier
                        ouvrirFormulaireModification(idSeance, film, idSalle, date, heure, places);
                    } else if (choix == 1) {
                        // ✅ Supprimer
                        int confirmation = JOptionPane.showConfirmDialog(
                                null,
                                "Voulez-vous supprimer la séance de \"" + film + "\" ?",
                                "Confirmation",
                                JOptionPane.YES_NO_OPTION);
                        if (confirmation == JOptionPane.YES_OPTION) {
                            boolean succes = controleur.annulerSeance(idSeance);
                            if (succes) {
                                afficherSucces("Séance supprimée !");
                                chargerSeances(model, controleur.chargerSeances());
                            } else {
                                afficherErreur("Erreur lors de la suppression.");
                            }
                        }
                    }
                }
            }
        });

        for (String col : colonnes) {
            model.addColumn(col);
        }
        return table;
    }

    private JPanel createTablePanel(JTable table, Dimension dimension) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(dimension);
        panel.setBackground(FenetrePrincipale.font);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(FenetrePrincipale.font);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void construireMenu(JPanel menu) {
        String[] boutons = { "Voir les séances", "Rechercher", "Ajouter", "Retour", "Quitter" };

        JPanel panelBouton = new JPanel();
        panelBouton.setBackground(new Color(26, 26, 26));
        panelBouton.setLayout(new GridLayout(boutons.length, 1, 1, 50));
        panelBouton.setBorder(BorderFactory.createEmptyBorder(30, 1, 30, 1));

        JLabel label = new JLabel();
        label.setIcon(FenetrePrincipale.ChargerLogo());
        menu.add(label, BorderLayout.NORTH);

        for (int i = 0; i < boutons.length; i++) {
            JButton bouton = new JButton(boutons[i]);
            bouton.setFocusable(false);
            bouton.setBorderPainted(false);
            bouton.setBackground(new Color(215, 83, 0));
            bouton.setForeground(new Color(255, 211, 184));
            bouton.setFont(GeneralFont.getfont(40));

            bouton.addActionListener(e -> {
                switch (bouton.getText()) {
                    case "Voir les séances":
                        card.show(contenuSeancePanel, LIST_SEANCE);
                        chargerSeances(modelPrincipal, controleur.chargerSeances());
                        break;
                    case "Rechercher":
                        card.show(contenuSeancePanel, RECHER_SEANCE);
                        chargerSeances(modelRechercheDate, controleur.chargerSeances());
                        chargerSeances(modelRechercheTitre, controleur.chargerSeances());
                        rechercheParDate = true;
                        break;
                    case "Ajouter":
                        card.show(contenuSeancePanel, AJOUTER_SEANCE);
                        break;
                    case "Retour":
                        FenetrePrincipale.RetourALapagePricipale();
                        break;
                    case "Quitter":
                        System.exit(0);
                        break;
                }
            });
            panelBouton.add(bouton);
        }

        menu.setBackground(panelBouton.getBackground());
        menu.add(panelBouton, BorderLayout.CENTER);
    }

    private void chargerSeances(DefaultTableModel model, List<Seance> seances) {
        model.setRowCount(0);
        for (Seance s : seances) {
            Object[] row = {
                    s.getIdSeance(),    
                    s.getTitre(),       
                    s.getIdSalle(),     
                    s.getDateSeance(),  
                    s.getHeure(),       
                    s.getPlacesDispo()   
            };
            model.addRow(row);
        }
    }

    public void afficherSucces(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur",
                JOptionPane.ERROR_MESSAGE);
    }
}