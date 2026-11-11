package Controleur;

import java.util.List;
import Modele.Salle;
import Modele.SalleDAO;

public class SalleControler {

    private SalleDAO salleDAO = new SalleDAO();

    public List<Salle> getAll() {
        return salleDAO.findAll();
    }

    public boolean ajouter(Salle salle) {
        return salleDAO.ajouter(salle);
    }

    public boolean modifier(Salle salle) {
        return salleDAO.modifier(salle);
    }

    public boolean supprimer(int id) {
        return salleDAO.supprimer(id);
    }

    public boolean existeDeja(String nom, int idActuel) {
        return salleDAO.existeDeja(nom, idActuel);
    }
}