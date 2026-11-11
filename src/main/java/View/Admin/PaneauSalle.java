package View.Admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import Controleur.SalleControler;
import Modele.Salle;
import View.*;

public class PaneauSalle extends AbstractPanel {

    private SalleControler controleur = new SalleControler();

    private DefaultTableModel model;
    private JTable table;

    // ===== EQUIPEMENTS PREDEFINIS =====
    private static final String[] EQUIPEMENTS = {
        "Lunette 4D",
        "Sound DOLBY ATMOS / IMAX",
        "Lunette 3D"
    };

    public PaneauSalle() {

        setLayout(new BorderLayout());
        setBackground(FenetrePrincipale.font);

        // ===== HEADER =====
        JLabel header = createHeader("GESTION DES SALLES");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(FenetrePrincipale.font);
        topPanel.setPreferredSize(new Dimension(100, 100));
        topPanel.add(header, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        // ===== TABLEAU =====
        model = new DefaultTableModel(new String[]{"ID", "Nom", "Capacité", "Équipement"}, 0) {
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

        // ===== HEADER TABLE =====
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

        JButton btnAdd    = createStyledButton("Ajouter");
        JButton btnDelete = createStyledButton("Supprimer");
        JButton btnEdit   = createStyledButton("Modifier");

        actions.add(btnAdd);
        actions.add(btnDelete);
        actions.add(btnEdit);

        add(actions, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> ajouter());
        btnDelete.addActionListener(e -> supprimer());
        btnEdit.addActionListener(e -> modifier());

        chargerSalles();
    }

    // ===== BOUTON STYLE =====
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(215, 83, 0));
        btn.setForeground(new Color(255, 211, 184));
        btn.setFont(util.GeneralFont.getfont(20));
        return btn;
    }

    // ===== CHARGEMENT =====
    private void chargerSalles() {
        model.setRowCount(0);
        List<Salle> salles = controleur.getAll();

        if (salles != null) {
            for (Salle s : salles) {
                model.addRow(new Object[]{
                        s.getIdSalle(),
                        s.getNom(),
                        s.getCapacite(),
                        s.getEquipement()
                });
            }
        }
    }

    // ===== PANEL EQUIPEMENTS (checkboxes) =====
    private JPanel creerPanelEquipements(List<String> selectionnes) {
        JPanel panel = new JPanel(new GridLayout(EQUIPEMENTS.length, 1));
        panel.setBackground(new Color(30, 30, 30));

        for (String eq : EQUIPEMENTS) {
            JCheckBox cb = new JCheckBox(eq);
            cb.setBackground(new Color(30, 30, 30));
            cb.setForeground(new Color(200, 200, 200));
            cb.setFont(util.GeneralFont.getfont(15));
            if (selectionnes != null && selectionnes.contains(eq)) {
                cb.setSelected(true);
            }
            panel.add(cb);
        }
        return panel;
    }

    // ===== RÉCUPÉRER LES EQUIPEMENTS COCHÉS =====
    private String[] getEquipementsCoches(JPanel panel) {
        List<String> coches = new ArrayList<>();
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JCheckBox) {
                JCheckBox cb = (JCheckBox) comp;
                if (cb.isSelected()) {
                    coches.add(cb.getText());
                }
            }
        }
        return coches.toArray(new String[0]);
    }

    // ===== AJOUT =====
    private void ajouter() {
        JTextField nom = new JTextField();
        JSpinner capacite = new JSpinner(new SpinnerNumberModel(50, 1, 200, 1));
        JPanel panelEquipements = creerPanelEquipements(null);

        Object[] message = {
                "Nom :", nom,
                "Capacité :", capacite,
                "Équipements :", panelEquipements
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Ajouter une salle", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String nomVal = nom.getText().trim();

            if (nomVal.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Le nom ne peut pas être vide.");
                return;
            }

            if (nomVal.length() > 100) {
                JOptionPane.showMessageDialog(null, "Le nom ne peut pas dépasser 100 caractères.");
                return;
            }

            if (controleur.existeDeja(nomVal, -1)) {
                JOptionPane.showMessageDialog(null, "Une salle avec ce nom existe déjà !");
                return;
            }

            int capaciteVal = (int) capacite.getValue();
            String equipementVal = String.join(", ", getEquipementsCoches(panelEquipements));

            Salle salle = new Salle(nomVal, capaciteVal, equipementVal);
            boolean succes = controleur.ajouter(salle);

            if (succes) {
                JOptionPane.showMessageDialog(null, "Salle ajoutée avec succès !");
                chargerSalles();
            } else {
                JOptionPane.showMessageDialog(null, "Échec de l'ajout.");
            }
        }
    }

    // ===== SUPPRESSION =====
    private void supprimer() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Sélectionne une salle.");
            return;
        }

        String nom = model.getValueAt(row, 1).toString();
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(null, "Supprimer la salle \"" + nom + "\" ?");

        if (confirm == JOptionPane.YES_OPTION) {
            boolean succes = controleur.supprimer(id);

            if (succes) {
                JOptionPane.showMessageDialog(null, "Salle supprimée avec succès !");
                chargerSalles();
            } else {
                JOptionPane.showMessageDialog(null, "Échec de la suppression.");
            }
        }
    }

    // ===== MODIFICATION =====
    private void modifier() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Sélectionne une salle.");
            return;
        }

        int id           = Integer.parseInt(model.getValueAt(row, 0).toString());
        String nomActuel = model.getValueAt(row, 1).toString();
        int capActuelle  = Integer.parseInt(model.getValueAt(row, 2).toString());
        String eqActuel  = model.getValueAt(row, 3).toString();

        JTextField nom = new JTextField(nomActuel);
        JSpinner capacite = new JSpinner(new SpinnerNumberModel(capActuelle, 1, 200, 1));

        List<String> eqList = new ArrayList<>();
        for (String eq : eqActuel.split(", ")) {
            eqList.add(eq.trim());
        }
        JPanel panelEquipements = creerPanelEquipements(eqList);

        Object[] message = {
                "Nom :", nom,
                "Capacité :", capacite,
                "Équipements :", panelEquipements
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Modifier la salle", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String nomVal = nom.getText().trim();

            if (nomVal.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Le nom ne peut pas être vide.");
                return;
            }

            if (nomVal.length() > 100) {
                JOptionPane.showMessageDialog(null, "Le nom ne peut pas dépasser 100 caractères.");
                return;
            }

            if (controleur.existeDeja(nomVal, id)) {
                JOptionPane.showMessageDialog(null, "Une salle avec ce nom existe déjà !");
                return;
            }

            int capaciteVal      = (int) capacite.getValue();
            String equipementVal = String.join(", ", getEquipementsCoches(panelEquipements));

            Salle salle = new Salle(id, nomVal, capaciteVal, equipementVal);
            boolean succes = controleur.modifier(salle);

            if (succes) {
                JOptionPane.showMessageDialog(null, "Salle modifiée avec succès !");
                chargerSalles();
            } else {
                JOptionPane.showMessageDialog(null, "Échec de la modification.");
            }
        }
    }

    @Override
    protected void construireMenu(JPanel menu) {
        // pas utilisé
    }
}