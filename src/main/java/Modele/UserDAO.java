package Modele;

import java.util.ArrayList;
import java.util.List;

import Connexion.DataBaseConn;
import View.FenetrePrincipale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.HashUtil;

public class UserDAO extends DataBaseConn {
    // objet pour hasher les mots de passe

    public UserDAO() {
    }

    public List<UserModele> getAll() {
        String sql = "SELECT * FROM utilisateur";

        List<UserModele> users = new ArrayList<>();

        try (Connection c = getConnection(); 
                PreparedStatement stmt = c.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(new UserModele(
                        rs.getString("login"),
                        "",
                        rs.getBoolean("admin"),
                        rs.getInt("id")));
            }

            return users;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public boolean ajouter(UserModele user) {

        String sql = "INSERT INTO utilisateur (login,motDePasse,admin) VALUES (?,?,?)";

        try (Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement(sql)) {
            super.changeUserID(c , FenetrePrincipale.user_id);   // <-- ajouté

            // on hash le mot de passe
            String hashedPSW = HashUtil.HashMDP(user.getMp());

            stmt.setString(1, user.getUserName());
            stmt.setString(2, hashedPSW);
            stmt.setBoolean(3, user.isAdmin());
            return stmt.execute();
        } catch (SQLException sqle) {
            System.out.println("Message " + sqle.getMessage());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean verfier(UserModele user) {
        String sql = "SELECT motDePasse FROM utilisateur WHERE login = ?";

        try (Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement(sql);) {

            stmt.setString(1, user.getUserName());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String mp = HashUtil.HashMDP(user.getMp());

                if (rs.getString("motDePasse").equals(mp)) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            System.err.println("Message : " + e.getMessage());
        }
        return false;

    }

    public boolean suprimer(String userName) {
        String sql = "DELETE FROM utilisateur WHERE login = ?";

        try (Connection c = getConnection();
                PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            super.changeUserID(c , FenetrePrincipale.user_id);   // <-- ajouté

            return pstmt.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    public boolean isAdmin(UserModele user) {
        String sql = "SELECT admin FROM utilisateur WHERE login = ?";

        try (Connection c = getConnection();
                PreparedStatement pstmt = c.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("admin");
            }

        } catch (Exception e) {
            System.out.println("non + " + e.getLocalizedMessage());
        }
        return false;
    }

    public boolean update(UserModele user) {

        String sql = "UPDATE utilisateur SET login = ?, admin = ? WHERE id = ?";

        try (Connection c = getConnection();
                PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, user.getUserName());
            stmt.setBoolean(2, user.isAdmin());
            stmt.setInt(3, user.getId());
            super.changeUserID(c , FenetrePrincipale.user_id);   // <-- ajouté

            // executeUpdate() renvoie le nombre de lignes impactées
            int rowsAffected = stmt.executeUpdate();

            // On retourne true si au moins une ligne a été mise à jour
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace(); // Plus utile pour le débogage que getLocalizedMessage()
            return false;
        }
    }

    public int getUserID(UserModele gestionnaire) {
        String sql = "SELECT id FROM utilisateur WHERE login = ?";

        try (Connection c = getConnection();
                PreparedStatement pstmt = c.prepareStatement(sql)) {

            pstmt.setString(1, gestionnaire.getUserName());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (Exception e) {
            System.out.println("non + " + e.getLocalizedMessage());
        }
        return 0;
    }

}
