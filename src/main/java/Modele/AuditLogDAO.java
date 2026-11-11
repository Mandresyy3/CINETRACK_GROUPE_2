package Modele;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Connexion.DataBaseConn;

public class AuditLogDAO extends DataBaseConn {

    public List<AuditLogModele> getAll() {

        String sql = "SELECT a.id, a.table_name, a.record_id, a.action, "
                   + "a.user_id, u.login, a.old_data, a.new_data, a.created_at "
                   + "FROM audit_log a "
                   + "LEFT JOIN utilisateur u ON u.id = a.user_id "
                   + "ORDER BY a.created_at DESC "
                   + "LIMIT 200";

        List<AuditLogModele> logs = new ArrayList<>();

        try (Connection c = super.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new AuditLogModele(
                        rs.getInt("id"),
                        rs.getString("table_name"),
                        rs.getInt("record_id"),
                        rs.getString("action"),
                        rs.getInt("user_id"),
                        rs.getString("login"),
                        rs.getString("old_data"),
                        rs.getString("new_data"),
                        rs.getTimestamp("created_at")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }
}