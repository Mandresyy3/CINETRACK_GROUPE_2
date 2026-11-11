package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * JButton avec coins arrondis + surbrillance au survol.
 * Utilisation identique à un JButton normal (texte, ActionListener...).
 */
public class Roundedbutton extends JButton {

    private boolean hovered = false;
    private boolean active = false;
    private final int radius = UITheme.RADIUS;

    public Roundedbutton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(UITheme.TEXT_PRIMARY);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    /** Marque ce bouton comme étant la page actuellement affichée. */
    public void setActive(boolean active) {
        this.active = active;
        setForeground(active ? UITheme.ACCENT_TEXT : UITheme.TEXT_PRIMARY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (active) {
            g2.setColor(UITheme.ACCENT);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        } else if (hovered) {
            g2.setColor(UITheme.BG_CARD_HOVER);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}