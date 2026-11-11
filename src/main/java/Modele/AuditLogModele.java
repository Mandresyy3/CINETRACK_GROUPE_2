package Modele;

import java.sql.Timestamp;

public class AuditLogModele {

    private int id;
    private String tableName;
    private int recordId;
    private String action;
    private int userId;
    private String login;      // récupéré par jointure
    private String oldData;
    private String newData;
    private Timestamp createdAt;

    public AuditLogModele(int id, String tableName, int recordId, String action,
                          int userId, String login, String oldData,
                          String newData, Timestamp createdAt) {
        this.id = id;
        this.tableName = tableName;
        this.recordId = recordId;
        this.action = action;
        this.userId = userId;
        this.login = login;
        this.oldData = oldData;
        this.newData = newData;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getTableName() { return tableName; }
    public int getRecordId() { return recordId; }
    public String getAction() { return action; }
    public int getUserId() { return userId; }
    public String getLogin() { return login; }
    public String getOldData() { return oldData; }
    public String getNewData() { return newData; }
    public Timestamp getCreatedAt() { return createdAt; }
}