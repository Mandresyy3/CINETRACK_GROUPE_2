# CineTrack

Application de gestion de cinéma (films, salles, séances, utilisateurs) en **Java** avec une base **PostgreSQL**.
Projet fil rouge du BTS SIO SLAM, MCCI Business School.

L'interface actuelle est en **Swing** (la migration vers JavaFX/FXML est prévue à l'étape 2 du projet).
Architecture : MVC en couches (Contrôleur, Service, DAO), avec des **interfaces** pour les DAO et une
**injection de dépendances** par constructeur (voir la section 4).

---

## 1. Prérequis

| Outil | Version | Remarque |
|---|---|---|
| JDK | **21** (Eclipse Temurin) | Le `pom.xml` impose Java 21, comme dans le cours Maven |
| PostgreSQL + pgAdmin | 14 ou plus | Retenir le mot de passe de l'utilisateur `postgres` |
| Git | récent | |
| IntelliJ IDEA | récent | Maven est déjà intégré à l'IDE |

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

### Avec IntelliJ (recommandé)

1. Ouvrir le dossier du projet (`File > Open`, choisir le dossier qui contient `pom.xml`).
2. Attendre que Maven télécharge les dépendances (barre de progression en bas).
3. Vérifier que le SDK du projet est le **JDK 21** (`File > Project Structure > Project`, Language level 21).
4. Clic droit sur `src/main/java/main/Main.java`, puis **Run 'Main.main()'**.
5. Vérifier que le **Working directory** (`Run > Edit Configurations`) est la racine du projet, là où se trouve le `.env`.

### En ligne de commande

```bash
mvn compile exec:java -Dexec.mainClass="main.Main"
```

La commande doit être lancée depuis la racine du projet, pour que le `.env` soit trouvé.

### Connexion

Compte administrateur créé par le script SQL (**réservé au développement**) :

| Login | Mot de passe |
|---|---|
| `admin` | `admin123` |

---

## 4. Architecture et structure du projet

```
CINETRACK_GROUPE_2/
├── db/
│   └── cinetrack.sql          Script de création de la base (tables, clés étrangères, audit)
├── src/main/java/
│   ├── main/                  Point d'entrée (Main, qui assemble aussi les objets) et test manuel (TestAudit)
│   ├── Connexion/             DataBaseConn : connexion JDBC et utilisateur courant pour l'audit
│   ├── Modele/                Classes métier (Film, Salle, Seance, ...) et implémentations DAO (XxxDAOPostgres)
│   ├── dao/                   Interfaces DAO (FilmDAO, SalleDAO, SeanceDAO, UserDAO, AuditLogDAO)
│   ├── service/               Services (règles métier) : ils reçoivent leur DAO par le constructeur
│   ├── Controleur/            Contrôleurs : ils reçoivent leur Service par le constructeur
│   ├── View/                  Interface Swing (View/Admin : écrans administrateur)
│   ├── exceptions/            Exceptions métier (MetierException, AccesDonneesException, ...)
│   └── util/                  Outils (HashUtil, EnvLoader)
├── pom.xml                    Dépendances Maven (driver PostgreSQL, JavaFX), Java 21
├── .env.example               Modèle de configuration (copier en .env)
└── .gitignore
```

Chaîne d'appel actuelle :
**View → Controleur → Service → DAO (interface dans `dao/`) → DAOPostgres (dans `Modele/`) → PostgreSQL**.

### Rôle de chaque couche

- **Contrôleur** : lit la saisie, appelle le Service, traduit une exception en message pour l'écran. Aucun SQL, aucune règle métier.
- **Service** : contient les règles métier (par exemple : note entre 0 et 10, deux salles ne portent pas le même nom).
  En cas de refus ou d'échec, il lève une exception (`MetierException`, `AccesDonneesException`) au lieu de renvoyer `false`.
- **DAO** : le seul endroit où il y a du SQL (JDBC, requêtes préparées).

### Convention DAO

- L'**interface** décrit ce que le DAO sait faire, sans code SQL : `dao/FilmDAO.java`.
- L'**implémentation** contient le SQL : `Modele/FilmDAOPostgres.java`, qui fait `implements FilmDAO`.
- Le code qui utilise un DAO déclare le **type de l'interface** : `FilmDAO dao = new FilmDAOPostgres();`.

### Injection de dépendances

Une classe ne crée pas ses dépendances avec `new` : elle les **reçoit par son constructeur**
(un contrôleur reçoit son Service, un Service reçoit son DAO).
L'assemblage est fait à **un seul endroit**, dans `Main` : `filmControleur()`, `salleControleur()`,
`seanceControleur()` et `userControleur()` fabriquent la chaîne DAO, Service, Contrôleur.
Les vues demandent leur contrôleur à `Main` (par exemple `Main.filmControleur()`).

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
Si personne n'est connecté, `user_id` vaut `NULL`. Le hash du mot de passe n'est jamais enregistré dans l'historique.

### Tester l'audit

`src/main/java/main/TestAudit.java` est un test **manuel** : il écrit dans la vraie base
(crée puis supprime deux comptes de test) et suppose que l'admin a l'id 1.
Le lancer avec un clic droit, **Run 'TestAudit.main()'**. Résultat attendu : toutes les lignes commencent par `OK`.

---

## 6. Travailler en équipe avec Git

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

## 7. Problèmes fréquents

| Message ou symptôme | Cause et solution |
|---|---|
| `Could not load .env file` | Le `.env` n'est pas à la racine, ou le Working directory est incorrect. Le placer à côté de `pom.xml` et corriger la configuration de lancement. |
| `The url cannot be null` | Conséquence du message précédent : le `.env` n'a pas été lu. |
| `password authentication failed` | Le `PASSWORD` du `.env` n'est pas celui de l'utilisateur PostgreSQL. |
| `Connection refused` | PostgreSQL n'est pas démarré, ou le port n'est pas 5432. |
| `database "cinetrack" does not exist` | La base n'a pas été créée (voir 2.2). |
| `relation "..." does not exist` | Le script `db/cinetrack.sql` n'a pas été exécuté sur cette base. |
| `invalid source release: 21` ou `release version 21 not supported` | Le JDK utilisé n'est pas le 21 (voir `File > Project Structure`). |
| `UnsupportedClassVersionError ... class file version 69.0` | Des fichiers ont été compilés avec un autre JDK. Faire `Build > Rebuild Project`. |
| Caractères accentués illisibles | Définir l'encodage en UTF-8 (`pom.xml` le fait déjà pour Maven). |

---

## 8. Avancement de l'étape 2 (échéance du 17/11/2026)

- [x] Model
- [x] DAO Interface (`dao/`)
- [x] DAO Impl JDBC/PostgreSQL (`XxxDAOPostgres`)
- [x] Connexion à PostgreSQL (`.env` et `DataBaseConn`)
- [x] JDK 21 et dépendances JavaFX dans le `pom.xml`
- [x] Service
- [x] Injection de dépendances (assemblage dans le `Main`)
- [ ] FXML, CSS et contrôleurs JavaFX

---

## 9. Limites connues (à traiter)

- Seul `AuditLogControleur` appelle encore directement son DAO (pas de service d'audit).
- Les vues Swing demandent leur contrôleur à `Main` : assemblage provisoire, à remplacer par celui de JavaFX.
- Les mots de passe sont hachés en **SHA-256 sans sel** : à remplacer par bcrypt (étape 6).
- Le contrôle d'accès administrateur est surtout fait dans l'interface (menus masqués) : il doit aussi être vérifié côté code, avec refus par défaut.
- L'application se connecte avec le compte `postgres` (tous les droits) : prévoir un compte applicatif aux droits minimaux.
- Aucun test automatisé JUnit pour l'instant.