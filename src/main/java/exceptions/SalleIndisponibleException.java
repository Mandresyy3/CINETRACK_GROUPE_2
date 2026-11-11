package exceptions;

/**
 * Exception lancée quand une salle est déjà réservée à une date/heure donnée.
 * Lancée dans SeanceDAO.insert() si la contrainte UNIQUE est violée (SQLState 23505).
 */
public class SalleIndisponibleException extends RuntimeException {

    private int idSalle;
    private String dateHeure;

    public SalleIndisponibleException(int idSalle, String dateHeure) {
        super("La salle " + idSalle + " est déjà réservée à ce créneau : " + dateHeure);
        this.idSalle   = idSalle;
        this.dateHeure = dateHeure;
    }

    public int getIdSalle() {
        return idSalle;
    }

    public String getDateHeure() {
        return dateHeure;
    }
}