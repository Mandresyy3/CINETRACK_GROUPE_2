package Modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import Connexion.DataBaseConn;
import View.FenetrePrincipale;

public class SeanceDAO extends DataBaseConn {

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------
    public List<Seance> findAll() {
        List<Seance> liste = new ArrayList<>();
        String sql = "SELECT * FROM seances s " +
                "JOIN films f ON f.id = s.idfilm " +
                "ORDER BY dateseance, heure";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                liste.add(new Seance(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getInt("idsalle"),
                        rs.getString("dateseance"),
                        rs.getString("heure"),
                        rs.getInt("placesdispo")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }

    // -------------------------------------------------------------------------
    // findByFilm
    // -------------------------------------------------------------------------
    public List<Seance> findByTitre(String titre) {
    List<Seance> liste = new ArrayList<>();

    String sql = "SELECT * FROM seances s " +
                 "JOIN films f ON f.id = s.idfilm " +
                 "WHERE f.titre LIKE ? " +
                 "ORDER BY dateseance, heure";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "%" + titre + "%"); // recherche flexible

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                liste.add(new Seance(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getInt("idsalle"),
                    rs.getString("dateseance"),
                    rs.getString("heure"),
                    rs.getInt("placesdispo")
                ));
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return liste;
}

    // -------------------------------------------------------------------------
    // findByDate
    // -------------------------------------------------------------------------
    public List<Seance> findByDate(String date) {
        List<Seance> liste = new ArrayList<>();
        String sql = "SELECT * FROM seances s " +
                "JOIN films f ON f.id = s.idfilm " +
                "WHERE TO_CHAR(dateseance, 'YYYY-MM-DD') ILIKE ? ORDER BY heure";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // ✅ Conversion String → java.sql.Date
            ps.setString(1, "%"+ date+ "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    liste.add(new Seance(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getInt("idsalle"),
                            rs.getString("dateseance"),
                            rs.getString("heure"),
                            rs.getInt("placesdispo")));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }

    // -------------------------------------------------------------------------
    // insert
    // -------------------------------------------------------------------------
    public boolean insert(Seance s) {
        String sql = "INSERT INTO seances (idfilm, idsalle, dateseance, heure, placesdispo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            ps.setInt(1, s.getIdFilm());
            ps.setInt(2, s.getIdSalle());
            // ✅ Conversion String → java.sql.Date
            ps.setDate(3, Date.valueOf(s.getDateSeance()));
            // ✅ Conversion String → java.sql.Time
            ps.setTime(4, Time.valueOf(s.getHeure() + ":00"));

            // ✅ Après
            String heure = s.getHeure().trim();
            if (heure.length() == 5) { // format HH:MM
                heure = heure + ":00"; // devient HH:MM:00
            }
            ps.setTime(4, Time.valueOf(heure));
            ps.setInt(5, s.getPlacesDispo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Seance s) {
    String sql = "UPDATE seances SET idfilm = ?, idsalle = ?, dateseance = ?, heure = ?, placesdispo = ? WHERE id = ?";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

        ps.setInt(1, s.getIdFilm());
        ps.setInt(2, s.getIdSalle());
        ps.setDate(3, Date.valueOf(s.getDateSeance()));

        String heure = s.getHeure().trim();
        if (heure.length() == 5) {
            heure = heure + ":00";
        }
        ps.setTime(4, Time.valueOf(heure));
        ps.setInt(5, s.getPlacesDispo());
        ps.setInt(6, s.getIdSeance()); // ← WHERE id = ?

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------
    public boolean delete(int id) {
        String sql = "DELETE FROM seances WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Renvoie la prochaine seance
    public Seance prochaineSeance() {
        // TODO Auto-generated method stub
        String sql = "SELECT * FROM seances s " +
                "JOIN films f ON f.id = s.idfilm " +
                "WHERE dateseance > NOW() " +
                "ORDER BY dateseance ASC LIMIT 1";
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Seance(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getInt("idsalle"),
                        rs.getString("dateseance"),
                        rs.getString("heure"),
                        rs.getInt("placesdispo"));
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return new Seance();
    }

    public Seance Meilleurnote() {
        // TODO Auto-generated method stub
        String sql = "SELECT * FROM seances s " +
                "JOIN films f ON f.id = s.idfilm " +
                " WHERE f.note = (SELECT MAX(note) FROM films)" +
                "ORDER BY dateseance ASC LIMIT 1";
        try (Connection c = getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Seance(
                        rs.getInt("note"),
                        rs.getString("titre"),
                        rs.getInt("idsalle"),
                        rs.getString("dateseance"),
                        rs.getString("heure"));
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return new Seance();
    }
}