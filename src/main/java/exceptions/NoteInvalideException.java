package exceptions;
 
/**
 * Exception lancée quand une note est hors de l'intervalle [0, 10].
 * Lancée dans Film.setNote() et FilmDAO.insert() / update().
 */
public class NoteInvalideException extends RuntimeException {
 
    private double valeurInvalide;
 
    public NoteInvalideException(double valeur) {
        super("Note invalide : " + valeur + ". La note doit être comprise entre 0 et 10.");
        this.valeurInvalide = valeur;
    }
 
    public double getValeurInvalide() {
        return valeurInvalide;
    }
}
 