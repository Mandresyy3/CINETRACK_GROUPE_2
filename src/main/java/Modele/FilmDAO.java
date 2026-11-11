package Modele;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import View.FenetrePrincipale;
import Connexion.DataBaseConn;
import exceptions.FilmIntrouvableException;
import exceptions.NoteInvalideException;

public class FilmDAO extends DataBaseConn{

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------
    public List<Film> findAll() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id, titre, genre, duree, note, synopsis FROM films ORDER BY titre";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
                
            while (rs.next()) {
                films.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            System.err.println("Erreur base de données."+e.getLocalizedMessage());
        }

        return films;
    }

    // -------------------------------------------------------------------------
    // findByGenre
    // -------------------------------------------------------------------------
    public List<Film> rechercherParGenre(String genre) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id, titre, genre, duree, note, synopsis FROM films WHERE genre ILIKE ? ORDER BY titre";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            //  correction
            stmt.setString(1, "%" + genre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    films.add(mapResultSet(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur base de données.");
        }

        return films;
    }

    // -------------------------------------------------------------------------
    // rechercherParTitre
    // -------------------------------------------------------------------------
    public List<Film> rechercherParTitre(String motCle) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT id, titre, genre, duree, note, synopsis FROM films WHERE titre ILIKE ? ORDER BY titre";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%"+ motCle + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    films.add(mapResultSet(rs));
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur base de données.");
        }

        return films;
    }

    // -------------------------------------------------------------------------
    // insert
    // -------------------------------------------------------------------------
    public boolean ajouter(Film film) {
        if (film.getNote() < 0 || film.getNote() > 10) {
            throw new NoteInvalideException(film.getNote());
        }

        System.out.println(film.getDuree());

        String sql = "INSERT INTO films (titre, genre, duree, note, synopsis) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

                

            stmt.setString(1, film.getTitre());
            stmt.setString(2, film.getGenre());
            stmt.setInt(3, film.getDuree());
            stmt.setDouble(4, film.getNote());
            stmt.setString(5, film.getSynopsis());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Erreur base de données." + e.getLocalizedMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------
    public boolean update(Film film) {
        if (film.getNote() < 0 || film.getNote() > 10) {
            throw new NoteInvalideException(film.getNote());
        }

        String sql = "UPDATE films SET titre = ?, genre = ?, duree = ?, note = ?, synopsis = ? WHERE idFilm = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            stmt.setString(1, film.getTitre());
            stmt.setString(2, film.getGenre());
            stmt.setInt(3, film.getDuree());
            stmt.setDouble(4, film.getNote());
            stmt.setString(5, film.getSynopsis());
            stmt.setInt(6, film.getIdFilm());

            int lignes = stmt.executeUpdate();

            // ✅ vérification
            if (lignes == 0) {
                throw new FilmIntrouvableException(film.getIdFilm());
            }
            return true;

        } catch (Exception e) {
            System.err.println("Erreur base de données.");
            return false;

        }
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------
    public boolean delete(int id) {
        String sql = "DELETE FROM films WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            super.changeUserID(conn , FenetrePrincipale.user_id);   // <-- ajouté

            stmt.setInt(1, id);
            int lignes = stmt.executeUpdate();

            if (lignes == 0) {
                throw new FilmIntrouvableException(id);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Erreur base de données.");
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // mapResultSet
    // -------------------------------------------------------------------------
    private Film mapResultSet(ResultSet rs) throws SQLException {
        return new Film(
            rs.getInt("id"),
            rs.getString("titre"),
            rs.getString("genre"),
            rs.getInt("duree"),
            rs.getDouble("note"),
            rs.getString("synopsis")
        );
    }
}