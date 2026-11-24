# CineTrack

Application de gestion de cinéma (films, salles, séances, utilisateurs) en **Java / JavaFX** avec une base **PostgreSQL**.
Projet fil rouge du BTS SIO SLAM, MCCI Business School.

Architecture : **FXML → contrôleur JavaFX → Service → DAO (interface) → DAO PostgreSQL**, avec une
**injection de dépendances** par constructeur (voir la section 4).

---

## 1. Prérequis

| Outil | Version | Remarque |
|---|---|---|
| JDK | **21** (Eclipse Temurin) | Le `pom.xml` impose Java 21, comme dans le cours Maven |
| PostgreSQL + pgAdmin | 14 ou plus | Retenir le mot de passe de l'utilisateur `postgres` |
| Git | récent | |
| IntelliJ IDEA | récent | Maven est déjà intégré à l'IDE |
| Scene Builder | facultatif | Pour dessiner les écrans FXML à la souris (voir la section 6) |

---

## 2. Installation (à faire une seule fois)

### 2.1 Récupérer le projet

```bash
git clone https://github.com/Mandresyy3/CINETRACK_GROUPE_2.git
cd CINETRACK_GROUPE_2
git checkout dev
```

### 2.2 Créer la base de données

1. Ouvrir **pgAdmin** et se connecter au serveur PostgreSQL.
2. Clic droit sur **Databases**, puis **Create**, puis **Database**. Nom : `cinetrack` (exactement).
3. Clic droit sur la base `cinetrack`, puis **Query Tool**.
4. Ouvrir le fichier `db/cinetrack.sql` (icône de dossier), puis appuyer sur **F5**.
5. Vérifier dans le panneau « Messages » qu'aucune erreur n'apparaît en rouge.

> **Attention :** `db/cinetrack.sql` supprime puis recrée toutes les tables. Le rejouer **efface les données** et remet les données de test.

### 2.3 Créer le fichier `.env`

À la racine du projet (à côté de `pom.xml`), créer un fichier nommé exactement `.env`
(sans `.txt`), en copiant `.env.example` et en mettant **ses propres valeurs** :

```
URL=jdbc:postgresql://localhost:5432/cinetrack
USER=postgres
PASSWORD=votre_mot_de_passe_postgres
```

Le fichier `.env` contient un mot de passe : il est ignoré par Git et ne doit **jamais** être envoyé sur GitHub ni partagé.
Chaque membre de l'équipe a son propre `.env`.

---

## 3. Lancer l'application

1. Ouvrir le dossier du projet dans IntelliJ (`File > Open`, choisir le dossier qui contient `pom.xml`).
2. Attendre que Maven télécharge les dépendances (barre de progression en bas).
3. Vérifier que le SDK du projet est le **JDK 21** (`File > Project Structure > Project`, Language level 21).
4. Clic droit sur `src/main/java/main/Launcher.java`, puis **Run 'Launcher.main()'**.
5. Vérifier que le **Working directory** (`Run > Edit Configurations`) est la racine du projet, là où se trouve le `.env`.

> On lance **`Launcher`** et non `MainApp` : `Launcher` n'hérite pas de `Application`, ce qui évite l'erreur
> « JavaFX runtime components are missing ». L'avertissement « Unsupported JavaFX configuration » dans la console est normal.
> `Main` est l'ancien point d'entrée Swing : il n'est plus utilisé pour lancer l'application.

### Connexion

Compte administrateur créé par le script SQL (**réservé au développement**) :

| Login | Mot de passe |
|---|---|
| `admin` | `admin123` |

### Écrans selon le rôle

| Onglet | Administrateur | Autre compte |
|---|---|---|
| Accueil (prochaine séance, film le mieux noté) | oui | oui |
| Films | oui | oui |
| Séances | oui | oui |
| Salles | oui | non |
| Utilisateurs | oui | non |
| Historique des modifications | oui | non |

---

## 4. Architecture et structure du projet

```
CINETRACK_GROUPE_2/
├── db/
│   └── cinetrack.sql          Script de création de la base (tables, clés étrangères, audit)
├── src/main/java/
│   ├── main/                  Launcher et MainApp (démarrage JavaFX), Main (assemblage des objets), TestAudit
│   ├── controller/            Contrôleurs JavaFX (un par écran) : ils reçoivent leur Service par le constructeur
│   ├── service/               Services (règles métier) : ils reçoivent leur DAO par le constructeur
│   ├── dao/                   Interfaces DAO (FilmDAO, SalleDAO, SeanceDAO, UserDAO, AuditLogDAO)
│   ├── Modele/                Classes métier (Film, Salle, Seance, ...) et implémentations DAO (XxxDAOPostgres)
│   ├── Connexion/             DataBaseConn : connexion JDBC et utilisateur courant pour l'audit
│   ├── exceptions/            Exceptions métier (MetierException, AccesDonneesException, ...)
│   ├── util/                  Outils (HashUtil, EnvLoader)
│   ├── Controleur/, View/     Ancienne interface Swing (conservée, non utilisée au démarrage)
├── src/main/resources/
│   ├── views/                 Écrans FXML (un fichier par écran, plus main-view.fxml qui les réunit en onglets)
│   └── css/style.css          Feuille de style de toute l'application
├── pom.xml                    Dépendances Maven (driver PostgreSQL, JavaFX), Java 21
├── .env.example               Modèle de configuration (copier en .env)
└── .gitignore
```

### Rôle de chaque couche

- **FXML** : décrit l'apparence d'un écran (aucune logique). **CSS** : couleurs, polices, survol.
- **Contrôleur JavaFX** : lit la saisie, appelle le Service, affiche un message en cas d'erreur. Aucun SQL, aucune règle métier.
- **Service** : contient les règles métier (note entre 0 et 10, nom de salle unique, capacité de 1 à 200, ...).
  En cas de refus ou d'échec, il lève une exception (`MetierException`, `AccesDonneesException`) au lieu de renvoyer `false`.
- **DAO** : le seul endroit où il y a du SQL (JDBC, requêtes préparées).

### Convention DAO

- L'**interface** décrit ce que le DAO sait faire, sans code SQL : `dao/FilmDAO.java`.
- L'**implémentation** contient le SQL : `Modele/FilmDAOPostgres.java`, qui fait `implements FilmDAO`.

### Injection de dépendances

Une classe ne crée pas ses dépendances avec `new` : elle les **reçoit par son constructeur**
(un contrôleur reçoit son Service, un Service reçoit son DAO). L'assemblage est fait à **un seul endroit**, dans `Main` :
`creerControleur(Class<?> type)` fabrique chaque contrôleur avec ses Services et ses DAO.
`MainApp` la branche sur `FXMLLoader` avec `loader.setControllerFactory(Main::creerControleur)`.

### Ajouter un écran

1. Créer `src/main/resources/views/xxx-view.fxml` avec `fx:controller="controller.XxxController"`.
2. Créer `controller/XxxController.java` : le constructeur reçoit le Service ; les composants du FXML sont des champs `@FXML` de même nom que leur `fx:id`.
3. Ajouter, dans `Main`, une ligne `if (type == XxxController.class) return new XxxController(...)` dans `creerControleur`.
4. Ajouter un onglet avec `<fx:include source="xxx-view.fxml"/>` dans `main-view.fxml`.
5. Pour réserver l'onglet à l'administrateur : lui donner un `fx:id` et l'ajouter à la liste des onglets retirés dans `MainController`.

---

## 5. Base de données

| Table | Rôle |
|---|---|
| `utilisateur` | Comptes (`login`, `motdepasse` haché, `admin`) |
| `films` | Catalogue (titre, genre, durée, note, synopsis) |
| `salles` | Salles (nom, capacité, `equipment` : tableau PostgreSQL `varchar[]`) |
| `seances` | Séances, liées à un film et une salle par **clés étrangères** |
| `audit_log` | Historique des écritures (qui, quoi, avant/après) |

Règles de la base :

- une séance doit pointer vers un film **et** une salle existants (clés étrangères) ;
- supprimer un film ou une salle supprime aussi ses séances (`ON DELETE CASCADE`) ;
- la détection des conflits de séances (une salle réservée deux fois au même créneau) est prévue à l'étape 4.

### Audit

Des triggers PostgreSQL remplissent `audit_log` à chaque insertion, modification ou suppression.
Pour savoir **qui** agit, les DAO appellent `changeUserID(connexion, id)` **avant** l'écriture,
sur la **même connexion** : cela définit `app.current_user_id`, lu par le trigger.
L'identifiant vient de la connexion (écran de connexion). Si personne n'est connecté, `user_id` vaut `NULL`.
Le hash du mot de passe n'est jamais enregistré dans l'historique.

### Tester l'audit

`src/main/java/main/TestAudit.java` est un test **manuel** : il écrit dans la vraie base
(crée puis supprime deux comptes de test) et suppose que l'admin a l'id 1.
Le lancer avec un clic droit, **Run 'TestAudit.main()'**. Résultat attendu : toutes les lignes commencent par `OK`.

---

## 6. Modifier un écran avec Scene Builder (facultatif)

Clic droit sur un fichier FXML, puis **Open in SceneBuilder** (le chemin du **`SceneBuilder.exe`**, et non de l'installeur `.msi`,
se règle dans `File > Settings > Languages & Frameworks > JavaFX`). Trois règles pour ne rien casser :

- ne pas changer les **`fx:id`** (le contrôleur les retrouve par leur nom) ;
- ne pas changer les **actions** des boutons (`onAjouter`, `onModifier`, ...) ;
- ne pas vider le **contrôleur** du document (`fx:controller`).

La feuille de style est attachée dans `MainApp` (pas dans le FXML) : Scene Builder n'affiche pas le style, sauf si on ajoute
`css/style.css` dans `Preview > Scene Style Sheets`.

---

## 7. Travailler en équipe avec Git

Branches du projet : `main` (version finale), `dev` (version de travail commune), `feature` (développement).

Avant de commencer à travailler :

```bash
git checkout dev
git pull
```

Pour envoyer son travail :

```bash
git status                       # vérifier la liste des fichiers
git add chemin/du/fichier.java   # fichier par fichier, jamais "git add ." à l'aveugle
git commit -m "Message clair décrivant le changement"
git push
```

Puis, sur GitHub, ouvrir une **Pull Request** vers `dev` et faire relire avant de fusionner.
Ne jamais utiliser `git push --force`.

Fichiers qui ne doivent **jamais** être envoyés : `.env`, `.idea/`, `target/`.

---

## 8. Problèmes fréquents

| Message ou symptôme | Cause et solution |
|---|---|
| `Could not load .env file` | Le `.env` n'est pas à la racine, ou le Working directory est incorrect. Le placer à côté de `pom.xml` et corriger la configuration de lancement. |
| `The url cannot be null` | Conséquence du message précédent : le `.env` n'a pas été lu. |
| `password authentication failed` | Le `PASSWORD` du `.env` n'est pas celui de l'utilisateur PostgreSQL. |
| `Connection refused` | PostgreSQL n'est pas démarré, ou le port n'est pas 5432. |
| `database "cinetrack" does not exist` | La base n'a pas été créée (voir 2.2). |
| `relation "..." does not exist` | Le script `db/cinetrack.sql` n'a pas été exécuté sur cette base. |
| `JavaFX runtime components are missing` | Lancer `Launcher` et non `MainApp`. |
| `LoadException` au démarrage | Un `fx:id` du FXML ne correspond pas à un champ `@FXML`, une action n'existe pas dans le contrôleur, ou le contrôleur n'est pas dans `Main.creerControleur`. |
| `invalid source release: 21` ou `release version 21 not supported` | Le JDK utilisé n'est pas le 21 (voir `File > Project Structure`). |
| `UnsupportedClassVersionError ... class file version 69.0` | Des fichiers ont été compilés avec un autre JDK. Faire `Build > Rebuild Project`. |
| Modification d'un FXML ou du CSS sans effet | Fermer l'application, faire `Build > Rebuild Project`, relancer. |

---

## 9. Avancement

Étape 2 (échéance du 17/11/2026) :

- [x] Model
- [x] DAO Interface (`dao/`)
- [x] DAO Impl JDBC/PostgreSQL (`XxxDAOPostgres`)
- [x] Connexion à PostgreSQL (`.env` et `DataBaseConn`)
- [x] Service
- [x] Injection de dépendances (assemblage dans `Main`)
- [x] Contrôleurs JavaFX et FXML (connexion, accueil, films, salles, séances, utilisateurs, historique)
- [x] CSS

Étape 3 (échéance du 24/11/2026) : CRUD films et salles (fait), recherche multicritère (à compléter sur l'écran des films),
validation des données et gestion des exceptions (en partie faits).

---

## 10. Limites connues (à traiter)

- Les droits sont limités à deux rôles (administrateur ou non) : un compte non administrateur peut encore modifier les films et les séances. Les rôles administrateur et caissier avec droits distincts sont prévus à l'étape 6.
- Les mots de passe sont hachés en **SHA-256 sans sel** : à remplacer par bcrypt (étape 6).
- Le contrôleur de connexion retient l'utilisateur dans des champs statiques de l'ancienne classe Swing `View.FenetrePrincipale` (lus par `DataBaseConn` pour l'audit) : à remplacer par une classe de session dédiée.
- L'ancienne interface Swing (`View/`, `Controleur/`) est conservée mais n'est plus utilisée.
- L'application se connecte avec le compte `postgres` (tous les droits) : prévoir un compte applicatif aux droits minimaux.
- Aucun test automatisé JUnit pour l'instant.