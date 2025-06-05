-- LUT-30052 - [OAuth2 Client] Gestion des secrets par environnement

CREATE TABLE apimanager_client_secret (
    uuid            varchar(50),
    uuid_client     varchar(50),
    environnement   varchar(50),
    secret          varchar(2000),
    PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_client DROP COLUMN client_secret;
