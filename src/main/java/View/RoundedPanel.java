package View;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel avec coins arrondis, utilisé comme "carte" (style dashboard moderne).
 */
public class RoundedPanel extends JPanel {

    private final int radius;
    private final Color bg;

    public RoundedPanel(LayoutManager layout, Color bg) {
        this(layout, bg, UITheme.RADIUS);
    }

    public RoundedPanel(LayoutManager layout, Color bg, int radius) {
        super(layout);
        this.bg = bg;
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}