package View;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel {

    protected CardLayout card;
    protected JPanel contentPanel;

    protected final Color BG_MAIN = FenetrePrincipale.font;
    protected final Color BG_MENU = new Color(26, 26, 26);
    protected final Color BTN_COLOR = new Color(215, 83, 0);
    protected final Color BTN_TEXT = new Color(255, 211, 184);

    public AbstractPanel() {
        setLayout(new BorderLayout());

        // MENU
        JPanel menu = new JPanel(new BorderLayout());
        menu.setPreferredSize(new Dimension(FenetrePrincipale.LARGEUR_MENU, 1));
        add(menu, BorderLayout.WEST);

        construireMenu(menu);

        // CONTENU
        card = new CardLayout();
        contentPanel = new JPanel(card);
        add(contentPanel, BorderLayout.CENTER);
    }

    // 🔥 A implémenter dans les classes enfants
    protected abstract void construireMenu(JPanel menu);

    // 🔹 Créer un bouton stylé (réutilisable)
    protected JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setBackground(BTN_COLOR);
        btn.setForeground(BTN_TEXT);
        btn.setFont(util.GeneralFont.getfont(40));
        return btn;
    }

    // 🔹 Header standard
    protected JLabel createHeader(String titre) {
        JLabel header = new JLabel(titre);
        header.setHorizontalAlignment(0);
        header.setFont(util.GeneralFont.getfont(40));
        header.setForeground(Color.WHITE);
        return header;
    }

    // 🔹 Page simple (réutilisable)
    protected JPanel createPage(String titre) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_MAIN);

        panel.add(createHeader(titre), BorderLayout.NORTH);

        return panel;
    }

    // 🔹 Ajout d'une page dans le CardLayout
    protected void addPage(String name, JPanel panel) {
        contentPanel.add(panel, name);
    }

    // 🔹 Navigation
    protected void showPage(String name) {
        card.show(contentPanel, name);
    }
}