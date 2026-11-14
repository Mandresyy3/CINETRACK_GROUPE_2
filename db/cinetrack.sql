-- ============================================================
-- CineTrack : script de création de la base de données
-- À exécuter dans une base vide (ex : "cinetrack") avec le
-- Query Tool de pgAdmin (ouvrir le fichier puis F5).
-- Peut être relancé : il supprime et recrée les tables.
-- ============================================================

-- Nettoyage (permet de relancer le script)
DROP TABLE IF EXISTS seances CASCADE;
DROP TABLE IF EXISTS salles CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS utilisateur CASCADE;
DROP FUNCTION IF EXISTS fn_audit_log() CASCADE;

-- ------------------------------------------------------------
-- Tables
-- ------------------------------------------------------------
CREATE TABLE utilisateur (
    id          SERIAL PRIMARY KEY,
    login       VARCHAR(50)  NOT NULL UNIQUE,
    motdepasse  VARCHAR(255) NOT NULL,
    admin       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE films (
    id        SERIAL PRIMARY KEY,
    titre     VARCHAR(150) NOT NULL,
    genre     VARCHAR(50),
    duree     INTEGER      NOT NULL CHECK (duree > 0),
    note      NUMERIC(3,1) CHECK (note >= 0 AND note <= 10),
    synopsis  TEXT
);

CREATE TABLE salles (
    id         SERIAL PRIMARY KEY,
    nom        VARCHAR(50) NOT NULL UNIQUE,
    capacite   INTEGER     NOT NULL CHECK (capacite > 0),
    equipment  VARCHAR(50)[]
);

CREATE TABLE seances (
    id           SERIAL PRIMARY KEY,
    idfilm       INTEGER NOT NULL REFERENCES films(id)  ON DELETE CASCADE,
    idsalle      INTEGER NOT NULL REFERENCES salles(id) ON DELETE CASCADE,
    dateseance   DATE    NOT NULL,
    heure        TIME    NOT NULL,
    placesdispo  INTEGER NOT NULL CHECK (placesdispo >= 0)
);

-- Index sur les clés étrangères (jointures plus rapides)
CREATE INDEX idx_seances_idfilm  ON seances(idfilm);
CREATE INDEX idx_seances_idsalle ON seances(idsalle);

CREATE TABLE audit_log (
    id          SERIAL PRIMARY KEY,
    table_name  VARCHAR(50) NOT NULL,
    record_id   INTEGER,
    action      VARCHAR(10) NOT NULL,
    -- SET NULL : on garde l'historique si l'utilisateur est supprimé
    user_id     INTEGER REFERENCES utilisateur(id) ON DELETE SET NULL,
    old_data    JSONB,
    new_data    JSONB,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- Données de départ (insérées AVANT les triggers pour ne pas
-- remplir audit_log avec les données de test)
-- ------------------------------------------------------------

-- Compte de test : login = admin / mot de passe = admin123
-- (le mot de passe est stocké haché en SHA-256, comme dans HashUtil)
INSERT INTO utilisateur (login, motdepasse, admin) VALUES
    ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', TRUE);

INSERT INTO films (titre, genre, duree, note, synopsis) VALUES
    ('Inception', 'Science-fiction', 148, 8.8,
     'Un voleur s''infiltre dans les rêves pour dérober des secrets.'),
    ('Le Fabuleux Destin d''Amélie Poulain', 'Comédie', 122, 8.3,
     'Une jeune femme parisienne change discrètement la vie de ceux qui l''entourent.');

INSERT INTO salles (nom, capacite, equipment) VALUES
    ('Salle 1', 120, ARRAY['3D', 'Dolby Atmos']),
    ('Salle 2',  80, ARRAY['2D']);

INSERT INTO seances (idfilm, idsalle, dateseance, heure, placesdispo) VALUES
    (1, 1, '2026-11-20', '20:00', 120),
    (2, 2, '2026-11-21', '18:30',  80);

-- ------------------------------------------------------------
-- Journal d'audit : fonction + triggers
-- ------------------------------------------------------------
CREATE FUNCTION fn_audit_log() RETURNS trigger
    LANGUAGE plpgsql
AS $$
DECLARE
    v_user_id INT;
BEGIN
    BEGIN
        v_user_id := current_setting('app.current_user_id', true)::INT;
    EXCEPTION WHEN OTHERS THEN
        v_user_id := NULL;
    END;

    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log(table_name, record_id, action, user_id, old_data, new_data)
        VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', v_user_id, NULL, row_to_json(NEW));
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log(table_name, record_id, action, user_id, old_data, new_data)
        VALUES (TG_TABLE_NAME, NEW.id, 'UPDATE', v_user_id, row_to_json(OLD), row_to_json(NEW));
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log(table_name, record_id, action, user_id, old_data, new_data)
        VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', v_user_id, row_to_json(OLD), NULL);
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

CREATE TRIGGER trg_audit_utilisateur
    AFTER INSERT OR UPDATE OR DELETE ON utilisateur
    FOR EACH ROW EXECUTE FUNCTION fn_audit_log();

CREATE TRIGGER trg_audit_films
    AFTER INSERT OR UPDATE OR DELETE ON films
    FOR EACH ROW EXECUTE FUNCTION fn_audit_log();

CREATE TRIGGER trg_audit_salles
    AFTER INSERT OR UPDATE OR DELETE ON salles
    FOR EACH ROW EXECUTE FUNCTION fn_audit_log();

CREATE TRIGGER trg_audit_seances
    AFTER INSERT OR UPDATE OR DELETE ON seances
    FOR EACH ROW EXECUTE FUNCTION fn_audit_log();
