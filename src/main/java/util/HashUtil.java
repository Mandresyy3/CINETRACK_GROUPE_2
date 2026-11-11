package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/* Package qui comporte des classe ou des methodes 
   qui sont utiliser dans les autres 
   classe pour ne pas trop les chargé */
   
//    classes de Hashage des mots de passe
public class HashUtil {
    /**
     * 
     * @param mdp c'est le mot de passe qui est haché
     * @return le hash du mot de passe
    */
    public static String HashMDP(String mdp) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(mdp.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                //Transforme le code en hexadecimal
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}