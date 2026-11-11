package Modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Connexion.DataBaseConn;
import View.FenetrePrincipale;

public class SalleDAO extends DataBaseConn {

    public List<Salle> findAll() {
        List<Salle> liste = new ArrayList<>();
        String sql = "SELECT id, nom, capacite, equipment FROM salles";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Conversion du tableau PostgreSQL en String
                Array arr = rs.getArray("equipment");
                String equipmentVal = "";
                if (arr != null) {
                    String[] tab = (String[]) arr.getArray();
                    equipmentVal = String.join(", ", tab);
                }

                liste.add(new Salle(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getInt("capacite"),
                    equipmentVal
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }

    public boolean ajouter(Salle salle) {
        String sql = "INSERT INTO salles (nom, capacite, equipment) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            ps.setString(1, salle.getNom());
            ps.setInt(2, salle.getCapacite());

            // Conversion en tableau PostgreSQL
            Array equipmentArray = conn.createArrayOf("varchar", salle.getEquipement().split(", "));
            ps.setArray(3, equipmentArray);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifier(Salle salle) {
        String sql = "UPDATE salles SET nom = ?, capacite = ?, equipment = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            ps.setString(1, salle.getNom());
            ps.setInt(2, salle.getCapacite());

            // Conversion en tableau PostgreSQL
            Array equipmentArray = conn.createArrayOf("varchar", salle.getEquipement().split(", "));
            ps.setArray(3, equipmentArray);

            ps.setInt(4, salle.getIdSalle());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM salles WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeDeja(String nom, int idActuel) {
        String sql = "SELECT COUNT(*) FROM salles WHERE LOWER(nom) = LOWER(?) AND id != ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setInt(2, idActuel);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}