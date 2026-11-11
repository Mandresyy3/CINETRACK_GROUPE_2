package View.Admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import Controleur.AuditLogControleur;
import Modele.AuditLogModele;
import View.AbstractPanel;
import View.FenetrePrincipale;

public class PaneauAuditLog extends AbstractPanel {

    private AuditLogControleur controleur = new AuditLogControleur();

    private DefaultTableModel model;
    private JTable table;

    // Liste parallèle : les données JSON complètes, non affichées dans le tableau
    private List<AuditLogModele> logs;

    public PaneauAuditLog() {
        setLayout(new BorderLayout());
        setBackground(FenetrePrincipale.font);

        // ===== HEADER =====
        JLabel header = createHeader("JOURNAL D'AUDIT");
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(FenetrePrincipale.font);
        topPanel.setPreferredSize(new Dimension(100, 100));
        topPanel.add(header, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        // ===== TABLEAU =====
        model = new DefaultTableModel(
                new String[]{"Date", "Utilisateur", "Table", "Action", "ID enreg."}, 0) {
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
        table.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(FenetrePrincipale.font);
        scroll.setBackground(FenetrePrincipale.font);
        add(scroll, BorderLayout.CENTER);

        // ===== BOUTONS =====
        JPanel actions = new JPanel();
        actions.setBackground(FenetrePrincipale.font);

        JButton btnDetail     = createStyledButton("Voir le détail");
        JButton btnActualiser = createStyledButton("Actualiser");

        actions.add(btnDetail);
        actions.add(btnActualiser);
        add(actions, BorderLayout.SOUTH);

        btnDetail.addActionListener(e -> afficherDetail());
        btnActualiser.addActionListener(e -> chargerLogs());

        chargerLogs();
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

    // Charge les entrées du journal depuis la base
    private void chargerLogs() {
        model.setRowCount(0);

        logs = controleur.recupereLogs();

        for (AuditLogModele log : logs) {
            model.addRow(new Object[]{
                log.getCreatedAt(),
                log.getLogin() == null ? "(inconnu)" : log.getLogin(),
                log.getTableName(),
                log.getAction(),
                log.getRecordId()
            });
        }
    }

    // Affiche old_data / new_data de la ligne sélectionnée
    private void afficherDetail() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Sélectionne une ligne du journal.");
            return;
        }

        // Indispensable si l'utilisateur a trié le tableau
        int index = table.convertRowIndexToModel(row);
        AuditLogModele log = logs.get(index);

        JTextArea zone = new JTextArea(
                "AVANT :\n" + (log.getOldData() == null ? "(aucune)" : log.getOldData())
                + "\n\nAPRES :\n" + (log.getNewData() == null ? "(aucune)" : log.getNewData()));
        zone.setEditable(false);
        zone.setLineWrap(true);
        zone.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(zone);
        scroll.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(null, scroll,
                "Détail — " + log.getAction() + " sur " + log.getTableName(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected void construireMenu(JPanel menu) {
        // c'est PaneauAdmin qui gère le menu
    }
}