package service;

import java.util.List;

import Modele.Seance;
import dao.SeanceDAO;
import exceptions.AccesDonneesException;
import exceptions.MetierException;

public class SeanceService {

    private final SeanceDAO seanceDAO;

    public SeanceService(SeanceDAO seanceDAO) {
        this.seanceDAO = seanceDAO;
    }

    public List<Seance> listerSeances() {
        return seanceDAO.findAll();
    }

    public List<Seance> rechercherParDate(String date) {
        return seanceDAO.findByDate(date.trim());
    }

    public List<Seance> rechercherParTitre(String titre) {
        return seanceDAO.findByTitre(titre);
    }

    public void ajouterSeance(Seance seance) {
        verifierRegles(seance);
        if (!seanceDAO.insert(seance)) {
            throw new AccesDonneesException(
                    "Impossible d'enregistrer la séance (film ou salle inexistant, ou salle déjà réservée à ce créneau).");
        }
    }

    public void modifierSeance(Seance seance) {
        verifierRegles(seance);
        if (!seanceDAO.update(seance)) {
            throw new AccesDonneesException("Impossible de modifier la séance.");
        }
    }

    public void annulerSeance(int idSeance) {
        if (!seanceDAO.delete(idSeance)) {
            throw new AccesDonneesException("Impossible de supprimer la séance.");
        }
    }

    public Seance prochaineSeance() {
        return seanceDAO.prochaineSeance();
    }

    public Seance meilleureNote() {
        return seanceDAO.Meilleurnote();
    }

    // Règles métier communes à l'ajout et à la modification
    private void verifierRegles(Seance seance) {
        if (seance.getDateSeance() == null || seance.getDateSeance().isBlank()
                || seance.getHeure() == null || seance.getHeure().isBlank()) {
            throw new MetierException("Veuillez remplir tous les champs (date et heure obligatoires).");
        }
        if (seance.getPlacesDispo() < 0) {
            throw new MetierException("Le nombre de places ne peut pas être négatif.");
        }
    }
}