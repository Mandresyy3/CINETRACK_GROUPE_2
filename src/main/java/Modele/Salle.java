package Modele;

public class Salle {

    // Attributs
    private int idSalle;
    private String nom;
    private int capacite;
    private String equipement;
 
    // Constructeur vide
    public Salle() {
    }
 
    // Constructeur complet 
    public Salle(int idSalle, String nom, int capacite, String equipement) {
        this.idSalle    = idSalle;
        this.nom        = nom;
        this.capacite   = capacite;
        this.equipement = equipement;
    }
 
 // Constructeur sans id — pour INSERT
    public Salle(String nom, int capacite, String equipement) {
        this.nom        = nom;
        this.capacite   = capacite;
        this.equipement = equipement;
    }
   
 
    // Getters
    public int getIdSalle() {
        return idSalle;
    }
 
    public String getNom() {
        return nom;
    }
 
    public int getCapacite() {
        return capacite;
    }
 
    public String getEquipement() {
        return equipement;
    }
 
    // -------------------------
    // Setters
    // -------------------------
    public void setIdSalle(int idSalle) {
        this.idSalle = idSalle;
    }
 
    public void setNom(String nom) {
        this.nom = nom;
    }
 
    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }
 
    public void setEquipement(String equipement) {
        this.equipement = equipement;
    }
 
    // toString — affichage lisible dans les JComboBox et logs
    @Override
    public String toString() {
        return nom + " (capacité : " + capacite + " places) — " + equipement;
    }

}

