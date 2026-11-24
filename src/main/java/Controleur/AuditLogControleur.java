 package Controleur;

import java.util.List;

import Modele.AuditLogDAOPostgres;
import Modele.AuditLogModele;
import dao.AuditLogDAO;

public class AuditLogControleur {

    private AuditLogDAO dao = new AuditLogDAOPostgres();

    public List<AuditLogModele> recupereLogs() {
        return dao.getAll();
    }
}