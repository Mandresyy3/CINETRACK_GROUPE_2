package View.Admin;

import javax.swing.*;

import View.AbstractPanel;
import View.FenetrePrincipale;

import java.awt.*;

public class PaneauAdmin extends AbstractPanel {

    private final String GESTIONNAIRE = "gestionnaire";
    private final String SALLE = "salle";
    private final String FILM = "film";
    private final String AUDIT = "audit";

    public PaneauAdmin() {
        super();

        setName(FenetrePrincipale.PAGE_ADMIN);
        addPage(GESTIONNAIRE, new PaneauGestionnaire());
        addPage(SALLE, new PaneauSalle());
        addPage(FILM,new PaneauGestionFilms());
        addPage(AUDIT, new PaneauAuditLog());
        showPage(AUDIT); 
        showPage(GESTIONNAIRE);
    }

    @Override
    protected void construireMenu(JPanel menu) {

        JPanel panelBouton = new JPanel(new GridLayout(6, 1, 1, 50));
        panelBouton.setBackground(BG_MENU);
        panelBouton.setBorder(BorderFactory.createEmptyBorder(30, 1, 30, 1));

        String[] boutons = {
                "Gestionnaire",
                "Gestion Salle",
                "Gestion Films",
                "Journal d'audit",
                "Retour",
                "Quiter"
        };

        for (String nom : boutons) {

            JButton btn = createMenuButton(nom);

            btn.addActionListener(e -> {
                switch (nom) {
                    case "Gestionnaire":
                        showPage(GESTIONNAIRE);
                        break;
                    case "Gestion Salle":
                        showPage(SALLE);
                        break;
                    case "Gestion Films":
                        showPage(FILM);
                        break;
                    case "Journal d'audit":
                    	showPage(AUDIT);
                        break;
                    case "Retour":
                        FenetrePrincipale.RetourALapagePricipale();
                        break;
                    case "Quiter":
                        System.exit(0);
                        break;
                }
            });

            panelBouton.add(btn);
        }

        JLabel logo = new JLabel();
        logo.setIcon(FenetrePrincipale.ChargerLogo());

        menu.setBackground(BG_MENU);
        menu.add(logo, BorderLayout.NORTH);
        menu.add(panelBouton, BorderLayout.CENTER);
    }
}
