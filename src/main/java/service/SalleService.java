package service;

import java.util.List;

import Modele.Salle;
import dao.SalleDAO;
import exceptions.AccesDonneesException;
import exceptions.MetierException;

public class SalleService {

    private final SalleDAO salleDAO;

    public SalleService(SalleDAO salleDAO) {
        this.salleDAO = salleDAO;
    }

    public List<Salle> listerSalles() {
        return salleDAO.findAll();
    }

    public void ajouterSalle(Salle salle) {
        // Règle métier : deux salles ne portent pas le même nom
        if (salleDAO.existeDeja(salle.getNom(), -1)) {
            throw new MetierException("Une salle porte déjà ce nom.");
        }
        if (!salleDAO.ajouter(salle)) {
            throw new AccesDonneesException("Impossible d'enregistrer la salle.");
        }
    }

    public void modifierSalle(Salle salle) {
        if (salleDAO.existeDeja(salle.getNom(), salle.getIdSalle())) {
            throw new MetierException("Une autre salle porte déjà ce nom.");
        }
        if (!salleDAO.modifier(salle)) {
            throw new AccesDonneesException("Impossible de modifier la salle.");
        }
    }

    public void supprimerSalle(int id) {
        if (!salleDAO.supprimer(id)) {
            throw new AccesDonneesException("Impossible de supprimer la salle.");
        }
    }

    public boolean existeDeja(String nom, int idActuel) {
        return salleDAO.existeDeja(nom, idActuel);
    }
}