package View;

import javax.swing.*;

import Controleur.SeanceControleur;
import Modele.Seance;
import View.Admin.PaneauAdmin;
import util.GeneralFont;

import java.awt.*;
import java.util.ArrayList;

public class FenetrePrincipale extends JFrame {

    static JPanel panelGenerale;
    static CardLayout card;
    // Tous les Panels
    Connexion connexion;
    PaneauFilms films;
    PaneauSeances seances;
    PaneauAdmin admin;

    // liste des boutons du menu, pour gerer l'etat "actif"
    private final ArrayList<Roundedbutton> boutonsMenu = new ArrayList<>();

    // Acces à l'adminitration :
    public static boolean isadmin;
    public static int user_id;

    public static final int LARGEUR_MENU = 320;
    public static final String PAGE_PRICIPALE = "principale";
    public static final String PAGE_FILM = "Films";
    public static final String PAGE_SEANCES = "Seances";
    public static final String PAGE_ADMIN = "admin panel";

    public static Color font = UITheme.BG_MAIN;

    public FenetrePrincipale() {
        // Look and feel par defaut du systeme (le style vient de nos composants
        // personnalises oundedbutton / RoundedPanel, pas de Nimbus)

        // Les panels
        card = new CardLayout();
        panelGenerale = new JPanel(card);
        panelGenerale.setBackground(UITheme.BG_MAIN);

        // pages (panels) films et seances
        films = new PaneauFilms();
        seances = new PaneauSeances();
        if (isadmin) {
            admin = new PaneauAdmin();
            panelGenerale.add(admin, admin.getName());
        }

        panelGenerale.add(films, films.getName());
        panelGenerale.add(seances, seances.getName());

        // Panel principale (contients les autres panels)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setName(PAGE_PRICIPALE);
        mainPanel.setBackground(UITheme.BG_MAIN);

        // menu a gauche
        JPanel menu = new JPanel(new BorderLayout());
        menu.setPreferredSize(new Dimension(LARGEUR_MENU, 1));
        menu.setBackground(UITheme.BG_SIDEBAR);
        ConstruireMenu(menu);
        mainPanel.add(menu, BorderLayout.WEST);

        // Contenu Princpale
        JPanel contenu = new JPanel(new BorderLayout());
        contenu.setBackground(UITheme.BG_MAIN);
        construireConenu(contenu);

        mainPanel.add(contenu);
        panelGenerale.add(mainPanel, mainPanel.getName());
        card.show(panelGenerale, PAGE_PRICIPALE);
        add(panelGenerale);
        setUndecorated(true); // Must be called before setVisible(true)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    private void construireConenu(JPanel contenu) {

        // Header
        JPanel titreConteneur = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titreConteneur.setBackground(UITheme.BG_MAIN);
        titreConteneur.setBorder(BorderFactory.createEmptyBorder(50, 60, 20, 60));
        JLabel titre = new JLabel("GESTIONNAIRE DE CINEMA");
        titre.setForeground(UITheme.TEXT_PRIMARY);
        titre.setFont(GeneralFont.getfont(38));
        titreConteneur.add(titre);

        // Contenu : deux cartes cote a cote
        JPanel panelInfo = new JPanel(new GridLayout(1, 2, 30, 0));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(30, 60, 60, 60));
        panelInfo.setBackground(UITheme.BG_MAIN);

        SeanceControleur sc = new SeanceControleur();

        // Carte : prochaine seance
        Seance prochaineSeance = sc.prochaineSeance();
        String contenuCarte1;
        if (prochaineSeance.getTitre() != null) {
            contenuCarte1 = "<html>" +
                    "<div style='font-size:1.4em;color:#E0A83A;font-weight:bold'>PROCHAINE SÉANCE</div><br>" +
                    "<div style='font-size:1.6em'>" + prochaineSeance.getTitre() + "</div><br>" +
                    "<div style='font-size:1.1em;color:#A0A0AC'>Salle " + prochaineSeance.getIdSalle() +
                    "&nbsp;&middot;&nbsp;" + prochaineSeance.getDateSeance() + " à " + prochaineSeance.getHeure()
                    + "h</div>" +
                    "</html>";
        } else {
            contenuCarte1 = "<html><div style='font-size:1.3em;color:#A0A0AC'>Aucune séance disponible</div></html>";
        }
        panelInfo.add(creerCarteInfo(contenuCarte1));

        // Carte : film le mieux note
        Seance meilleurNote = sc.Meilleurnote();
        String contenuCarte2;
        if (meilleurNote.getTitre() != null) {
            contenuCarte2 = "<html>" +
                    "<div style='font-size:1.4em;color:#E0A83A;font-weight:bold'>MIEUX NOTÉ</div><br>" +
                    "<div style='font-size:1.6em'>" + meilleurNote.getTitre() + "</div><br>" +
                    "<div style='font-size:1.1em;color:#A0A0AC'>Note " + meilleurNote.getNote() + "/10&nbsp;&middot;&nbsp;Salle "
                    + meilleurNote.getIdSalle() + "</div>" +
                    "<div style='font-size:1.1em;color:#A0A0AC'>" + meilleurNote.getDateSeance() + " à "
                    + meilleurNote.getHeure() + "h</div>" +
                    "</html>";
        } else {
            contenuCarte2 = "<html><div style='font-size:1.3em;color:#A0A0AC'>Aucune séance disponible</div></html>";
        }
        panelInfo.add(creerCarteInfo(contenuCarte2));

        contenu.add(titreConteneur, BorderLayout.NORTH);
        contenu.add(panelInfo);
    }

    /** Cree une carte arrondie contenant un label HTML. */
    private RoundedPanel creerCarteInfo(String htmlContenu) {
        RoundedPanel carte = new RoundedPanel(new BorderLayout(), UITheme.BG_CARD);
        carte.setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));
        carte.setPreferredSize(new Dimension(1, 220));

        JLabel label = new JLabel(htmlContenu);
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setFont(GeneralFont.getfont(16));
        carte.add(label, BorderLayout.NORTH);
        return carte;
    }

    // ajoute les elements du menu (bouton et images)
    public void ConstruireMenu(JPanel menu) {
        JPanel panelBouton = new JPanel();
        panelBouton.setOpaque(false);
        panelBouton.setLayout(new BoxLayout(panelBouton, BoxLayout.Y_AXIS));
        panelBouton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ArrayList<String> boutons = new ArrayList<>();
        if (isadmin) {
            String[] boutonsText = { "Seances", "Films", "Admin menu", "Quiter" };
            for (String boutonLabel : boutonsText) {
                boutons.add(boutonLabel);
            }
        } else {
            String[] boutonsText = { "Seances", "Films", "Quiter" };
            for (String boutonLabel : boutonsText) {
                boutons.add(boutonLabel);
            }
        }

        JLabel label = new JLabel();
        label.setIcon(ChargerLogo());
        menu.add(label, BorderLayout.NORTH);

        boutonsMenu.clear();
        for (int i = 0; i < boutons.size(); i++) {
            Roundedbutton bouton = new Roundedbutton(boutons.get(i));
            bouton.setAlignmentX(Component.LEFT_ALIGNMENT);
            bouton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            bouton.setFont(GeneralFont.getfont(22));
            bouton.addActionListener(e -> {
                for (Roundedbutton b : boutonsMenu) {
                    b.setActive(false);
                }
                switch (bouton.getText()) {
                    case "Seances":
                        bouton.setActive(true);
                        card.show(panelGenerale, PAGE_SEANCES);
                        break;
                    case "Films":
                        bouton.setActive(true);
                        card.show(panelGenerale, PAGE_FILM);
                        break;
                    case "Admin menu":
                        bouton.setActive(true);
                        card.show(panelGenerale, PAGE_ADMIN);
                        break;
                    case "Quiter":
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            });
            boutonsMenu.add(bouton);
            panelBouton.add(bouton);
            if (i < boutons.size() - 1) {
                panelBouton.add(Box.createRigidArea(new Dimension(1, 12)));
            }
        }
        menu.add(panelBouton);
    }

    public static ImageIcon ChargerLogo() {
        // logo
        ImageIcon logo = new ImageIcon("image\\logo.png");
        // Redimensionner l'image (largeur, hauteur)
        Image image = logo.getImage();
        Image newImg = image.getScaledInstance(LARGEUR_MENU - 60, 190, Image.SCALE_SMOOTH);

        return new ImageIcon(newImg);
    }

    public static void RetourALapagePricipale() {
        card.show(panelGenerale, PAGE_PRICIPALE);
    }

}