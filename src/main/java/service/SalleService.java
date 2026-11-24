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
        verifierRegles(salle);
        // Règle métier : deux salles ne portent pas le même nom
        if (salleDAO.existeDeja(salle.getNom(), -1)) {
            throw new MetierException("Une salle porte déjà ce nom.");
        }
        if (!salleDAO.ajouter(salle)) {
            throw new AccesDonneesException("Impossible d'enregistrer la salle.");
        }
    }

    public void modifierSalle(Salle salle) {
        verifierRegles(salle);
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
    // Règles métier communes à l'ajout et à la modification
    private void verifierRegles(Salle salle) {
        if (salle.getNom() == null || salle.getNom().isBlank()) {
            throw new MetierException("Le nom de la salle est obligatoire.");
        }
        if (salle.getCapacite() < 1 || salle.getCapacite() > 200) {
            throw new MetierException("La capacité doit être comprise entre 1 et 200 places.");
        }
    }
}