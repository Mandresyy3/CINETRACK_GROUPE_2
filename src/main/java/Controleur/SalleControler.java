package Controleur;

import java.util.List;

import Modele.Salle;
import exceptions.MetierException;
import service.SalleService;

public class SalleControler {

    private final SalleService salleService;

    // Injection par constructeur : le contrôleur reçoit son Service
    public SalleControler(SalleService salleService) {
        this.salleService = salleService;
    }

    public List<Salle> getAll() {
        return salleService.listerSalles();
    }

    public boolean ajouter(Salle salle) {
        try {
            salleService.ajouterSalle(salle);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public boolean modifier(Salle salle) {
        try {
            salleService.modifierSalle(salle);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public boolean supprimer(int id) {
        try {
            salleService.supprimerSalle(id);
            return true;
        } catch (MetierException me) {
            System.err.println(me.getMessage());
            return false;
        }
    }

    public boolean existeDeja(String nom, int idActuel) {
        return salleService.existeDeja(nom, idActuel);
    }
}