# CineTrack
En **Java** avec une base **PostgreSQL**.

L'interface actuelle est en **Swing**(on modifie en javaFX plus tard). L'architecture suit le modèle MVC (packages `Modele`, `View`, `Controleur`).

---

## 1. Prérequis

| Outil | Version | Remarque |
|---|---|---|
| JDK | **25** (ex. Eclipse Adoptium) | Le `pom.xml` impose Java 25 |
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
3. Vérifier que le SDK du projet est le JDK 25 (`File > Project Structure`).
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

## 4. Structure du projet

```
CINETRACK_GROUPE_2/
├── db/
│   └── cinetrack.sql          Script de création de la base (tables, clés étrangères, audit)
├── src/main/java/
│   ├── main/                  Point d'entrée (Main) et tests manuels
│   ├── Connexion/             DataBaseConn : connexion JDBC et utilisateur courant pour l'audit
│   ├── Modele/                Classes métier (Film, Salle, Seance, ...) et DAO (accès SQL)
│   ├── Controleur/            Logique entre la vue et les DAO
│   ├── View/                  Interface Swing (View/Admin : écrans administrateur)
│   ├── exceptions/            Exceptions métier
│   └── util/                  Outils (HashUtil, EnvLoader)
├── pom.xml                    Dépendances Maven (driver PostgreSQL)
├── .env.example               Modèle de configuration (copier en .env)
└── .gitignore
```

Chaîne d'appel : **View → Controleur → DAO (Modele) → PostgreSQL**.

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
- une salle ne peut pas avoir deux séances au même jour et à la même heure ;
- supprimer un film ou une salle supprime aussi ses séances (`ON DELETE CASCADE`).

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
| `invalid source release: 25` | Le JDK utilisé n'est pas le 25 (voir `File > Project Structure`). |
| Caractères accentués illisibles | Définir l'encodage en UTF-8 (`pom.xml` le fait déjà pour Maven). |

---

## 8. Limites connues (à traiter)

- Les mots de passe sont hachés en **SHA-256 sans sel** : à remplacer par Argon2id ou bcrypt.
- Le contrôle d'accès administrateur est surtout fait dans l'interface (menus masqués) : il doit aussi être vérifié côté code, avec refus par défaut.
- L'application se connecte avec le compte `postgres` (tous les droits) : prévoir un compte applicatif aux droits minimaux.
- Aucun test automatisé JUnit pour l'instant.
