package main;

import java.util.List;

import Modele.AuditLogDAO;
import Modele.AuditLogModele;
import Modele.UserDAO;
import Modele.UserModele;
import View.FenetrePrincipale;

public class TestAudit {

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        AuditLogDAO auditDAO = new AuditLogDAO();

        // CAS 1 : un utilisateur connecté (admin, id 1) crée puis supprime un compte
        FenetrePrincipale.user_id = 1;
        String login1 = "test_audit_" + (System.currentTimeMillis() % 100000);

        System.out.println("=== CAS 1 : utilisateur connecté (id 1) ===");
        verifier("création du compte " + login1,
                userDAO.ajouter(new UserModele(login1, "motdepasse123", false)));

        AuditLogModele log = dernierLog(auditDAO);
        verifier("log INSERT sur la table utilisateur",
                log != null && "INSERT".equals(log.getAction()) && "utilisateur".equals(log.getTableName()));
        verifier("le log est lié à l'utilisateur 1",
                log != null && log.getUserId() == 1 && log.getLogin() != null);
        verifier("le hash du mot de passe n'apparaît PAS dans le log",
                log != null && log.getNewData() != null && !log.getNewData().contains("motdepasse"));

        verifier("suppression du compte " + login1, userDAO.suprimer(login1));

        log = dernierLog(auditDAO);
        verifier("log DELETE lié à l'utilisateur 1",
                log != null && "DELETE".equals(log.getAction()) && log.getUserId() == 1);
        verifier("le hash n'apparaît PAS dans l'ancien contenu du log",
                log != null && log.getOldData() != null && !log.getOldData().contains("motdepasse"));

        // CAS 2 : personne de connecté (id 0) : l'écriture doit réussir, user_id doit être NULL
        FenetrePrincipale.user_id = 0;
        String login2 = "test_audit_nc_" + (System.currentTimeMillis() % 100000);

        System.out.println("\n=== CAS 2 : personne de connecté (id 0) ===");
        verifier("création du compte " + login2 + " sans utilisateur connecté",
                userDAO.ajouter(new UserModele(login2, "motdepasse123", false)));

        log = dernierLog(auditDAO);
        verifier("log INSERT sans utilisateur (user_id NULL, affiché 0)",
                log != null && "INSERT".equals(log.getAction()) && log.getUserId() == 0 && log.getLogin() == null);

        userDAO.suprimer(login2); // nettoyage
        System.out.println("\nTest terminé : comptes de test supprimés.");
    }

    private static AuditLogModele dernierLog(AuditLogDAO dao) {
        List<AuditLogModele> logs = dao.getAll();
        return logs.isEmpty() ? null : logs.get(0);
    }

    private static void verifier(String nom, boolean ok) {
        System.out.println((ok ? "OK       : " : "PROBLÈME : ") + nom);
    }
}