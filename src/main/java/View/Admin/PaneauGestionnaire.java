package View.Admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import Controleur.UserControleur;
import Modele.UserModele;
import View.*;

public class PaneauGestionnaire extends AbstractPanel {

    private UserControleur controleur = new UserControleur();

    private DefaultTableModel model;
    private JTable table;

    public PaneauGestionnaire() {

        setLayout(new BorderLayout());
        setBackground(FenetrePrincipale.font);

        // ===== HEADER =====
        JLabel header = createHeader("GESTION DES GESTIONNAIRES");

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(FenetrePrincipale.font);
        topPanel.setPreferredSize(new Dimension(100, 100));
        topPanel.add(header, BorderLayout.NORTH);

        add(topPanel, BorderLayout.NORTH);

        // Classe anonyme pour modifier le comportement des cellules du tableau
        model = new DefaultTableModel(new String[] { "ID", "Login", "Admin" }, 0) {
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

        JButton btnAdd = createStyledButton("Ajouter");
        JButton btnDelete = createStyledButton("Supprimer");
        JButton btnEdit = createStyledButton("Modifier");

        actions.add(btnAdd);
        actions.add(btnDelete);
        actions.add(btnEdit);

        add(actions, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> ajouter());
        btnDelete.addActionListener(e -> supprimer());
        btnEdit.addActionListener(e -> modifier());

        chargerUsers();
    }

    // ===== BOUTON STYLE IDENTIQUE =====
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
    private void chargerUsers() {
        model.setRowCount(0);
        List<UserModele> users = controleur.getAll();

        if (users != null) {
            for (UserModele u : users) {
                model.addRow(new Object[] {
                        u.getId(),
                        u.getUserName(),
                        u.isAdmin()
                });
            }
        }
    }

    // ===== AJOUT =====
    private void ajouter() {
        JTextField login = new JTextField();
        JPasswordField mdp = new JPasswordField();

        Object[] message = {
                "Login:", login,
                "Mot de passe:", mdp
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Ajouter gestionnaire", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            HashMap<String, String> data = new HashMap<>();
            data.put("login", login.getText());
            data.put("mp", new String(mdp.getPassword()));

            try {
                controleur.ajouterGestionaire(data);
                chargerUsers();
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }
    }

    // ===== SUPPRESSION =====
    private void supprimer() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Sélectionne un utilisateur");
            return;
        }

        String login = model.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(null, "Supprimer " + login + " ?");

        if (confirm == JOptionPane.YES_OPTION) {
            new Modele.UserDAO().suprimer(login);
            chargerUsers();
        }
    }

    // ===== MODIFICATION =====
    private void modifier() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Sélectionne un utilisateur");
            return;
        }
        String loginActuel = model.getValueAt(row, 1).toString();
        String AdminVAlue = model.getValueAt(row, 2).toString();
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        JTextField nouveauLogin = new JTextField(loginActuel);

        JComboBox<Boolean> admin = new JComboBox<>();

        admin.addItem(Boolean.parseBoolean(AdminVAlue));
        admin.addItem(!Boolean.parseBoolean(AdminVAlue));

        Object[] message = {
                "Nouveau login:", nouveauLogin,
                "Admin:", admin
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Modifier utilisateur", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            HashMap<String,String> data = new HashMap<>();
            data.put("name", nouveauLogin.getText());
            String isAdmin = admin.getSelectedItem().toString();
            data.put("admin", isAdmin);

            boolean changement = controleur.update(id,data);
            if(changement){
            JOptionPane.showMessageDialog(null, "Informations modifiées avec succés");
            chargerUsers();
            }else{
            JOptionPane.showMessageDialog(null, "Un probleme est survenu");
            }
        }

    }

    @Override
    protected void construireMenu(JPanel menu) {
        // pas utilisé
    }
}