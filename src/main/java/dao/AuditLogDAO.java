package dao;

import java.util.List;

import Modele.AuditLogModele;

public interface AuditLogDAO {

    List<AuditLogModele> getAll();
}