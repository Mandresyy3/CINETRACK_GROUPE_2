package View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import Modele.Film;
import util.GeneralFont;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.event.MouseEvent;

import Controleur.FilmControleur;

public class PaneauFilms extends AbstractPanel {

    private FilmControleur controleur = new FilmControleur();

    private final String LIST_FILM = "listes des films";
    private final String AJOUTER_FILM = "Ajout";
    private final String RECHER_FILM_TITRE = "recherche titre";
    private final String RECHER_FILM_GENRE = "recherche genre";

    private String[] Collones = { "Titre", "Synopsys", "Genre", "Duree", "Note" };

    private DefaultTableModel modelPrincipale = customModel();
    private DefaultTableModel modelRechercheTitre = customModel();
    private DefaultTableModel modelRechercheGenre = customModel();

    private CardLayout card;
    private JPanel contenuFilmPanel;

    private boolean rechercheParTitre = true;

    private DefaultTableModel customModel() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public PaneauFilms() {
        setName(FenetrePrincipale.PAGE_FILM);
        setLayout(new BorderLayout());

        // Menu de navigation
        JPanel menu = new JPanel();
        // 1 est une variable arbirtraire car il prendra toute la hauteur du panel
        menu.setPreferredSize(new Dimension(FenetrePrincipale.LARGEUR_MENU, 1));
        construireMenu(menu);
        add(menu, BorderLayout.WEST);

        // Panel pricipal
        card = new CardLayout();
        contenuFilmPanel = new JPanel(card);
        // Contenu du panel film
        ajouterContenu(LIST_FILM, "LIST DES FILMS", modelPrincipale);
        ajouterContenu(RECHER_FILM_TITRE, "RECHERCHER LES FILMS PAR TITRE", modelRechercheTitre);
        ajouterContenu(RECHER_FILM_GENRE, "RECHERCHER LES FILMS PAR GENRE", modelRechercheGenre);
        ajouterContenu(AJOUTER_FILM, "AJOUTER LES FILMS", null);

        card.show(contenuFilmPanel, LIST_FILM);
        add(contenuFilmPanel);
        contenuFilmPanel.setPreferredSize(getMaximumSize());
        chargerFilm(modelPrincipale, controleur.recupereFilm());
    }

    private void ajouterContenu(String nom, String titre, DefaultTableModel model) {
        // Consulter tous les films

        JPanel listFilms = new JPanel(new BorderLayout());

        JLabel header = new JLabel(titre); // le titre de la page

        // Configuraion graphique de l'en-tête
        header.setHorizontalAlignment(0);
        header.setVerticalAlignment(0);
        header.setFont(GeneralFont.getfont(40));
        header.setForeground(Color.white);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(FenetrePrincipale.font);
        topPanel.setPreferredSize(new Dimension(contenuFilmPanel.getPreferredSize().width, 100));
        topPanel.add(header, BorderLayout.NORTH);

        listFilms.setBackground(FenetrePrincipale.font);
        listFilms.setPreferredSize(contenuFilmPanel.getPreferredSize());
        if (!nom.equals(AJOUTER_FILM)) {
            listFilms.add(createTablePanel(CreeTable(Collones, model), listFilms.getPreferredSize()));
        } else {
            ajouter(listFilms);
        }
        if (nom.equals(RECHER_FILM_TITRE) || nom.equals(RECHER_FILM_GENRE)) {
            JTextField barDeRecherche = new JTextField();
            barDeRecherche.setPreferredSize(new Dimension(200, 30));
            // Configuration de la bar de recherche
            barDeRecherche.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    recherche(rechercheParTitre, barDeRecherche);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    recherche(rechercheParTitre, barDeRecherche);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    // TODO Auto-generated method stub
                }

            });
            topPanel.add(barDeRecherche, BorderLayout.SOUTH);
            JMenuBar bar = new JMenuBar();
            bar.setBackground(FenetrePrincipale.font);
            String[] modeDeRecherche = { "Par titre", "Par genre" };

            bar.setBackground(FenetrePrincipale.font);
            // Ajouts des menu dans la bar
            for (String mode : modeDeRecherche) {
                JMenu menu = new JMenu(mode);
                menu.setOpaque(true);
                menu.setBackground(Color.BLACK);
                menu.setForeground(Color.WHITE);
                menu.getPopupMenu().setBackground(Color.black);

                JMenuItem item = new JMenuItem("Choisir");
                item.setOpaque(true);
                item.setBackground(FenetrePrincipale.font);
                item.setForeground(Color.WHITE);

                item.addActionListener(e -> {
                    if (mode.equals("Par titre")) {
                        card.show(contenuFilmPanel, RECHER_FILM_TITRE);
                        rechercheParTitre = true;                        
                    } else {
                        card.show(contenuFilmPanel, RECHER_FILM_GENRE);
                        chargerFilm(modelRechercheGenre, controleur.recupereFilm());
                        rechercheParTitre = false;
                    }
                });

                menu.add(item);
                bar.add(menu);
            }
            topPanel.add(bar);
            topPanel.setBackground(FenetrePrincipale.font);
        }
        listFilms.add(topPanel, BorderLayout.NORTH);
        contenuFilmPanel.add(listFilms, nom);
    }

    // rechrche pour la recherche de films
    private void recherche(boolean titre, JTextField field) {
        String text = field.getText();
        if (titre) {
            chargerFilm(modelRechercheTitre, controleur.chercherParTitre(text));
        } else {
            chargerFilm(modelRechercheGenre, controleur.chercherParGenre(text));
        }
    }

    // Configuration du panel d'ajout d'un film
    private void ajouter(JPanel p) {

        // Moyen d'envoie des données
        HashMap<String, Object> donneesFilms = new HashMap<>();

        p.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        JPanel ajoutPanel = new JPanel(new GridLayout(11, 1));

        Color ajoutFont = new Color(30, 30, 30);
        ajoutPanel.setBackground(ajoutFont);

        String[] titreDonneesFilms = { "Titre", "Synopsys", "Genre", "Durée (minutes)", "Note" };
        for (int i = 0; i < titreDonneesFilms.length; i++) {
            JLabel label = new JLabel(titreDonneesFilms[i]);
            label.setFont(GeneralFont.getfont(20));
            label.setForeground(new Color(200, 200, 200));
            ajoutPanel.add(label);

            // Configuration d'un slider pour les notes
            if (titreDonneesFilms[i].equals("Note")) {
                JSlider note = notesSlider();
                // Ajout au panel
                ajoutPanel.add(note);
                donneesFilms.put(titreDonneesFilms[i], note);
            } else {
                JTextField text = new JTextField();
                text.setName(titreDonneesFilms[i]);
                ajoutPanel.add(text);
                donneesFilms.put(titreDonneesFilms[i], text);
            }
        }
        JButton confirmer = new JButton("Ajouter");
        confirmer.addActionListener(e -> {
            envoyerLesDonnees(donneesFilms);
        });
        ajoutPanel.add(confirmer);
        p.add(ajoutPanel);
    }

    /*
     * La fonction qui recupere les données du formulaire et qui les envoie a la
     * base de donnée
     * 
     * @param donneesFilms les champs de texte et le slider
     */
private void envoyerLesDonnees(HashMap<String, Object> donneesFilms) {

    HashMap<String, String> donnee = new HashMap<>();
    boolean valide = true;

    for (String key : donneesFilms.keySet()) {
        Object value = donneesFilms.get(key);

        if (value instanceof JTextField) {
            JTextField field = (JTextField) value;
            String texte = field.getText().trim();

            if (!texte.isEmpty()) {

                if ("Durée (minutes)".equals(field.getName()) && !texte.matches("\\d+")) {
                    JOptionPane.showMessageDialog(null, "La durée doit être en minutes (nombre)");
                    valide = false;
                    break;
                }
                field.setText("");
                donnee.put(key, texte);

            } else {
                JOptionPane.showMessageDialog(null, "Tous les champs doivent être remplis");
                valide = false;
                break;
            }

        } else if (value instanceof JSlider) {
            donnee.put(key, String.valueOf(((JSlider) value).getValue()));
        }
    }

    if (valide) {
        controleur.ajouter(donnee);
        chargerFilm(modelPrincipale, controleur.recupereFilm());
    }
}

    /**
     * 
     * @param collones Continent les noms de chaque collones
     * @param model    c'est le modele de la table (Ont fait toutes les operation
     *                 avec)
     * @return
     */
    // Renvoie des tableaux et les differencie avec leurs models
    private JTable CreeTable(String[] collones, DefaultTableModel model) {

        JTable table = new JTable(model) {
            @Override
            // Classe anonyme qui modifie les coleurs des lignes
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Set background for even rows to white and odd rows to light gray
                    c.setBackground(row % 2 != 0 ? new Color(43, 43, 43)
                            : new Color(64, 64, 64));
                }
                return c;
            }
        };

        JTableHeader header = table.getTableHeader();

        table.setBackground(FenetrePrincipale.font);

        // modifier le header du tablau (noms des collones)
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                c.setBackground(new Color(43, 43, 43)); // couleur du fond
                c.setForeground(new Color(166, 166, 166)); // couleur du texte
                setHorizontalAlignment(CENTER); // optionnel

                return c;
            }
        });

        table.setForeground(new Color(166, 166, 166));

        // AJout une action l'orsqu'on apuie sur une ligne
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();

                String titre = table.getValueAt(row, 0).toString();
                String synopsis = table.getValueAt(row, 1).toString();
                String duree = table.getValueAt(row, 3).toString();
                Double note = Double.parseDouble(table.getValueAt(row, 4).toString());

                MovieCard.displayMovieCard(titre, synopsis, duree, note);

                // Affiche une fenetre qui a les information des films (à finir)
            }
        });

        for (String films : collones) {
            model.addColumn(films);
        }
        return table;
    }

    // Revoie un panel personaliser et reutilisable pour facilement afficher des
    // tableau
    private JPanel createTablePanel(JTable table, Dimension dimension) {

        // Panel qui contiendra le tabeau
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(dimension);
        panel.setBackground(FenetrePrincipale.font);

        // Essentiel pour cree un JTable
        JScrollPane scroll = new JScrollPane(table);
        // fond du viewport 
        scroll.getViewport().setBackground(FenetrePrincipale.font);

        // optionnel (bordure + scrollpane)
        scroll.setBackground(FenetrePrincipale.font);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    @Override
    // affiche juste le menu
    protected final void construireMenu(JPanel menu) {
        // nom des differents boutons dans le menu
        String[] boutons = { "Conslter les films", "Rechercher", "Ajouter", "Retour", "Quiter" };

        JPanel panelBouton = new JPanel();

        panelBouton.setBackground(BG_MENU);
        panelBouton.setLayout(new GridLayout(boutons.length, 1, 1, 50));

        // Retreci l'espace entre les parois du panel et les boutons
        panelBouton.setBorder(BorderFactory.createEmptyBorder(30, 1, 30, 1));

        // Label contenant le logo
        JLabel label = new JLabel();
        label.setIcon(FenetrePrincipale.ChargerLogo());
        menu.add(label, BorderLayout.NORTH);
        /*
         * Ajout des boutons et
         * des actions attribuées à chacun
         */
        for (String nom : boutons) {
            // creation et changement des styles des boutons
            JButton bouton = super.createMenuButton(nom);
            // Ajout des actions
            bouton.addActionListener(e -> {
                switch (bouton.getText()) {
                    case "Conslter les films":
                        card.show(contenuFilmPanel, LIST_FILM);
                        chargerFilm(modelPrincipale, controleur.recupereFilm());
                        break;
                    case "Rechercher":
                        card.show(contenuFilmPanel, RECHER_FILM_TITRE);
                        chargerFilm(modelRechercheTitre, controleur.recupereFilm());
                        break;
                    case "Ajouter":
                        card.show(contenuFilmPanel, AJOUTER_FILM);
                        break;
                    case "Retour":
                        FenetrePrincipale.RetourALapagePricipale();
                        break;
                    case "Quiter":
                        System.exit(0);
                        break;
                }
            });
            panelBouton.add(bouton);
        }
        menu.setBackground(panelBouton.getBackground());
        menu.add(panelBouton, BorderLayout.CENTER);
    }

    // à changé
    private void chargerFilm(DefaultTableModel model, List<Film> films) {
        model.setRowCount(0); // reset du tableau
        // on ajouter toutes les donnees dans un tableau
        films.forEach((film) -> {
            String[] filmInfo = { film.getTitre(), film.getSynopsis(), film.getGenre(), film.getDureeFormatee(),
                    String.valueOf(film.getNote()) };
            model.addRow(filmInfo);
        });

    }

    private JSlider notesSlider() {
        JSlider note = new JSlider(0, 10, 5);

        // Activation de l'affichage (des numero sur le slider)
        note.setPaintTicks(true);
        note.setPaintLabels(true);
        note.setPaintTrack(true);

        // Espacement des graduations
        note.setMajorTickSpacing(1);

        // Snap sur les ticks (le curseur "colle" aux valeurs)
        note.setSnapToTicks(true);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int j = 0; j <= 10; j++) {
            JLabel numero = new JLabel(String.valueOf(j));
            labels.put(j, numero);
            numero.setForeground(Color.white);
        }
        // ajout des numero
        note.setLabelTable(labels);
        return note;
    }
}
