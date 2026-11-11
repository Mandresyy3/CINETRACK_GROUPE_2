 package Controleur;

import java.util.List;

import Modele.AuditLogDAO;
import Modele.AuditLogModele;

public class AuditLogControleur {

    private AuditLogDAO dao = new AuditLogDAO();

    public List<AuditLogModele> recupereLogs() {
        return dao.getAll();
    }
}