package exceptions;
 
/**
 * Exception lancée quand un film est introuvable en base de données.
 * Lancée dans FilmDAO.findById() si aucun film ne correspond à l'id.
 */
public class FilmIntrouvableException extends RuntimeException {
 
    private int idFilm;
 
    public FilmIntrouvableException(int id) {
        super("Aucun film trouvé avec l'id : " + id);
        this.idFilm = id;
    }
 
    public int getIdFilm() {
        return idFilm;
    }
}
 