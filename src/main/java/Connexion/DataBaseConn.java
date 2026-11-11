package Connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import util.EnvLoader;

public abstract class DataBaseConn {

    public Connection getConnection() throws Exception {

        EnvLoader.load(".env");
        final String URL = EnvLoader.get("URL");
        final String USER = EnvLoader.get("USER");
        final String PASSWORD = EnvLoader.get("PASSWORD");

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public boolean changeUserID(Connection c, int id) {
        String sql = "SELECT set_config('app.current_user_id', ?, false)";

        try (PreparedStatement pst = c.prepareStatement(sql)) {

            pst.setString(1, String.valueOf(id));
            pst.execute();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
