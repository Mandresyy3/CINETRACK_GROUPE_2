package service;

import java.util.List;

import Modele.AuditLogModele;
import dao.AuditLogDAO;

public class AuditLogService {

    private final AuditLogDAO auditLogDAO;

    public AuditLogService(AuditLogDAO auditLogDAO) {
        this.auditLogDAO = auditLogDAO;
    }

    public List<AuditLogModele> listerLogs() {
        return auditLogDAO.getAll();
    }
}